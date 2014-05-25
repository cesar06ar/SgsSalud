/*
 * Copyright 2013 cesar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sgssalud.controller.medicina;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.controller.paciente.PacienteHome;
import edu.sgssalud.model.BussinesEntityAttribute;
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.security.authorization.SecurityRules;
import edu.sgssalud.service.SettingService;
import edu.sgssalud.service.farmacia.RecetaServicio;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.HistoriaClinicaServicio;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.odontologia.ConsultaOdontologicaServicio;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.StringValidations;
import edu.sgssalud.util.UI;
import edu.sgssalud.util.UtilRoot;
import edu.sgssalud.web.service.WebServiceSGAClientConnection;
import java.io.Serializable;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.security.Identity;
import org.jboss.seam.transaction.Transactional;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class FichaMedicaHome extends BussinesEntityHome<FichaMedica> implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(FichaMedicaHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private Identity identity;
    @Inject
    private FichaMedicaServicio fichaMedicaService;
    @Inject
    private PacienteServicio pacienteS;
    @Inject
    private ProfileService profileS;
    @Inject
    private HistoriaClinicaServicio historiaClinService;
    @Inject
    private ConsultaMedicaServicio consultaMedService;
    @Inject
    private FichaOdontologicaServicio fichaOdonServicio;
    @Inject
    private ConsultaOdontologicaServicio consultaOdontService;
    @Inject
    private RecetaServicio recetaServicio;
    @Inject
    SettingService settingService;
    private WebServiceSGAClientConnection coneccionSGA = new WebServiceSGAClientConnection();
    //private Setting setting;
    private List<Setting> settingList;
    private Long pacienteId;
    private Long consultaMedicaId;
    private String parametroBusqueda;
    private Paciente paciente;
    private HistoriaClinica historiaClinica;
    private FichaOdontologica fichaOdontologica;
    private ConsultaMedica consultaMedica;
    private ConsultaOdontologica consultaOdont;
//    private List<ConsultaMedica> listaConsultasM = new ArrayList<ConsultaMedica>();
//    private List<ConsultaOdontologica> listaConsultasO = new ArrayList<ConsultaOdontologica>();
    private List<Paciente> listaPacietes = new ArrayList<Paciente>();
    private List<SignosVitales> listaSignosVitales = new ArrayList<SignosVitales>();
    private boolean panelFechaDia = false;
    private boolean panelFechaMes = false;
    private boolean panelIntervaloPersonalizado = false;
    private Date fecha;

    /*<== Métodos get y set para obtener el Id de la clase*/
    public Long getFichaMedicaId() {
        return (Long) getId();
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        setId(fichaMedicaId);
        FichaMedica f = fichaMedicaService.getFichaMedicaPorId(fichaMedicaId);
        if (f.isPersistent()) {
            this.setInstance(f);
            this.setPaciente(getInstance().getPaciente());
            this.setHistoriaClinica(historiaClinService.buscarPorFichaMedica(getInstance()));
            this.setFichaOdontologica(fichaOdonServicio.getFichaOdontologicaPorFichaMedica(getInstance()));
            if (getHistoriaClinica() != null && getFichaOdontologica() != null) {
                this.cargarSignosVitales(getHistoriaClinica().getConsultas(), getFichaOdontologica().getConsultas());
            } else if (getHistoriaClinica() != null && getFichaOdontologica() == null) {
                this.cargarSignosVitales(getHistoriaClinica().getConsultas(), new ArrayList<ConsultaOdontologica>());
            } else if (getHistoriaClinica() == null && getFichaOdontologica() != null) {
                this.cargarSignosVitales(new ArrayList<ConsultaMedica>(), getFichaOdontologica().getConsultas());
            }
        }
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
        this.setPaciente(pacienteS.getPacientePorId(pacienteId));
        FichaMedica f = fichaMedicaService.getFichaMedicaPorPaciente(paciente);
        if (f == null) {
            createInstance();
            getInstance().setNumeroFicha(getGenerarNumeroFicha());
        } else {
            setInstance(f);
            this.setHistoriaClinica(historiaClinService.buscarPorFichaMedica(getInstance()));
            this.setFichaOdontologica(fichaOdonServicio.getFichaOdontologicaPorFichaMedica(getInstance()));
            if (getHistoriaClinica() != null && getFichaOdontologica() != null) {
                this.cargarSignosVitales(getHistoriaClinica().getConsultas(), getFichaOdontologica().getConsultas());
            } else if (getHistoriaClinica() != null && getFichaOdontologica() == null) {
                this.cargarSignosVitales(getHistoriaClinica().getConsultas(), new ArrayList<ConsultaOdontologica>());
            } else if (getHistoriaClinica() == null && getFichaOdontologica() != null) {
                this.cargarSignosVitales(new ArrayList<ConsultaMedica>(), getFichaOdontologica().getConsultas());
            }
        }
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    public Long getConsultaMedicaId() {
        return consultaMedicaId;
    }

    public void setConsultaMedicaId(Long consultaMedicaId) {
        this.consultaMedicaId = consultaMedicaId;
    }

    public ConsultaMedica getConsultaMedica() {
        return consultaMedica;
    }

    public void setConsultaMedica(ConsultaMedica consultaMedica) {
        this.consultaMedica = consultaMedica;
    }

    public String getParametroBusqueda() {
        return parametroBusqueda;
    }

    public void setParametroBusqueda(String parametroBusqueda) {
        this.parametroBusqueda = parametroBusqueda;
        //this.setListaPacietes(pacienteS.BuscarPacientePorParametro(parametroBusqueda));
    }

    public FichaOdontologica getFichaOdontologica() {
        return fichaOdontologica;
    }

    public void setFichaOdontologica(FichaOdontologica fichaOdontologica) {
        this.fichaOdontologica = fichaOdontologica;
    }

    public ConsultaOdontologica getConsultaOdont() {
        return consultaOdont;
    }

    public void setConsultaOdont(ConsultaOdontologica consultaOdont) {
        this.consultaOdont = consultaOdont;
    }

    public List<Paciente> getListaPacietes() {
        return listaPacietes;
    }

    public void setListaPacietes(List<Paciente> listaPacietes) {
        this.listaPacietes = listaPacietes;
    }

    public List<SignosVitales> getListaSignosVitales() {
        return listaSignosVitales;
    }

    public void setListaSignosVitales(List<SignosVitales> listaSignosVitales) {
        this.listaSignosVitales = listaSignosVitales;
    }

    public boolean isPanelFechaDia() {
        return panelFechaDia;
    }

    public void setPanelFechaDia(boolean panelFechaDia) {
        this.panelFechaDia = panelFechaDia;
    }

    public boolean isPanelFechaMes() {
        return panelFechaMes;
    }

    public void setPanelFechaMes(boolean panelFechaMes) {
        this.panelFechaMes = panelFechaMes;
    }

    public boolean isPanelIntervaloPersonalizado() {
        return panelIntervaloPersonalizado;
    }

    public void setPanelIntervaloPersonalizado(boolean panelIntervaloPersonalizado) {
        this.panelIntervaloPersonalizado = panelIntervaloPersonalizado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /*<==....*/
    @TransactionAttribute   //
    public FichaMedica load() {
        if (isIdDefined()) {
            wire();
        }
        log.info("sgssalud --> cargar instance " + getInstance());
        return getInstance();
    }

    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        bussinesEntityService.setEntityManager(em);
        fichaMedicaService.setEntityManager(em);
        pacienteS.setEntityManager(em);
        consultaMedService.setEntityManager(em);
        historiaClinService.setEntityManager(em);
        fichaOdonServicio.setEntityManager(em);
        recetaServicio.setEntityManager(em);
        consultaOdontService.setEntityManager(em);
        profileS.setEntityManager(em);
        if (pacienteId == null) {
            paciente = new Paciente();
        }
        if (consultaMedicaId != null) {
            consultaMedica = new ConsultaMedica();
        }
        if (getInstance().isPersistent()) {
            historiaClinica = historiaClinService.buscarPorFichaMedica(getInstance());
        }
        //getInstance().setFechaApertura(new Date());        
        this.getInstance().setNumeroFicha(this.getGenerarNumeroFicha());  //asignacion automatica de numero de ficha
        settingService.setEntityManager(em);
        settingList = settingService.getSettingByName("id_oferta");
    }

    @Override
    protected FichaMedica createInstance() {
        //prellenado estable para cualquier clase 
        BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(FichaMedica.class.getName());
        Date now = Calendar.getInstance().getTime();
        FichaMedica fichaMedic = new FichaMedica();
        fichaMedic.setCreatedOn(now);
        //fichaMedic.setLastUpdate(now);
        fichaMedic.setActivationTime(now);
        //fichaMedic.setFechaApertura(now);
        if (identity.isLoggedIn()) {
            fichaMedic.setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));    //cambiar atributo a 
        }
        fichaMedic.setType(_type);                          //Obtengo datos dotos de manera     
        fichaMedic.buildAttributes(bussinesEntityService);  //

        historiaClinica = new HistoriaClinica();
        historiaClinica.setCreatedOn(now);
        historiaClinica.setActivationTime(now);
        historiaClinica.setLastUpdate(now);
        historiaClinica.setConsultas(new ArrayList<ConsultaMedica>());
        fichaOdontologica = new FichaOdontologica();
        fichaOdontologica.setCreatedOn(now);
        fichaOdontologica.setActivationTime(now);
        fichaOdontologica.setLastUpdate(now);
        fichaOdontologica.setConsultas(new ArrayList<ConsultaOdontologica>());
        return fichaMedic;
    }

    @Override
    public Class<FichaMedica> getEntityClass() {
        return FichaMedica.class;
    }

    public void mostrarPanelFecha(int n) {
        fecha = new Date();
        if (n == 1) {
            setPanelFechaDia(true);
            setPanelFechaMes(false);
            setPanelIntervaloPersonalizado(false);
        } else if (n == 2) {
            setPanelFechaDia(false);
            setPanelFechaMes(true);
            setPanelIntervaloPersonalizado(false);
        } else {
            setPanelFechaDia(false);
            setPanelFechaMes(false);
            setPanelIntervaloPersonalizado(true);
        }
    }

    @TransactionAttribute
    public String guardar() {
        //log.info("Ingreso a guardar");
        System.out.println("Ingreso a guardar_____" + getInstance().getId());
        String salida = null;
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        historiaClinica.setLastUpdate(now);
        fichaOdontologica.setLastUpdate(now);
        try {
            System.out.println("Ingreso a guardar 2_______");
            if (paciente.isPersistent()) {
                if (getInstance().isPersistent()) {
                    getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
                    System.out.println("Ingreso a guardar 2_______");
                    save(getInstance());
                    FacesMessage msg = new FacesMessage("Se actualizo Ficha Medica: " + getInstance().getNumeroFicha() + " con éxito");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                } else {
                    this.getInstance().setPaciente(paciente);
                    System.out.println("Ingreso a guardar 2_______");
                    create(getInstance());
                    historiaClinica.setFichaMedica(getInstance());
                    fichaOdontologica.setFichaMedica(getInstance());
                    save(getInstance());
                    save(historiaClinica);
                    save(fichaOdontologica);
                    FacesMessage msg = new FacesMessage("Se creo nueva Ficha Medica: " + getInstance().getNumeroFicha() + " con éxito");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                }
                salida = "/pages/depSalud/fichaMedica.xhtml?faces-redirect=true&fichaMedicaId=" + getInstance().getId();
            } else {
                FacesMessage msg = new FacesMessage("Primero debe cargar un paciente");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }

        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getNumeroFicha());
            FacesContext.getCurrentInstance().addMessage(" ", msg);
        }

        return salida;
    }

    @Transactional
    public String borrarConsultaMedica() {
        try {
            if (getInstance() == null) {
                throw new NullPointerException("Consulta Medica es null");
            }
            if (getInstance().isPersistent()) {
                if (recetaServicio.buscarRecetaPorConsultaMedica(consultaMedica).isEmpty()) {  //verificar si tiene recetas y examenes de laboratorio recetaServicio.buscarRecetaPorConsultaMedica(consultaMedica).isEmpty()
                    boolean delete = consultaMedService.borrarConsultaMedica(consultaMedica);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente la consulta:  " + consultaMedica.getId(), "" + delete));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Contiene Recetas", ""));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar la Consulta", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERRORE", e.toString()));
        }
        return null;
    }

    public String buscarPorParametro() {
        //buscar primero por numero de ficha
        listaPacietes = new ArrayList<Paciente>();
        String salida = "/pages/depSalud/fichaMedica.xhtml?faces-redirect=true";
        if (!parametroBusqueda.isEmpty()) {
            FichaMedica fichaMedList = fichaMedicaService.getFichaMedicaPorNumeroFicha(Long.parseLong(parametroBusqueda));
            if (fichaMedList != null) {
                this.setInstance(fichaMedList);
                this.setPaciente(getInstance().getPaciente());
                salida += "&fichaMedicaId=" + getInstance().getId();
            } else {
                Paciente p = pacienteS.BuscarPacientePorParametro1(parametroBusqueda);
                if (p.isPersistent()) {
                    this.setPaciente(p);
                    this.setInstance(fichaMedicaService.getFichaMedicaPorPaciente(p));
                    this.getInstance().setNumeroFicha(getGenerarNumeroFicha());
                    salida += "&pacienteId=" + paciente.getId();
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "No encontro resultados", "");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                }
                //salida = null;
                //this.setListaPacietes(pacienteS.BuscarPacientePorParametroCorto(salida));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El parametro de busqueda es incorrecto", " "));
        }
        this.setParametroBusqueda("");
        return salida;
    }

    /*<== método que retorna la lista de tipos de datos enumerados ...*/
    public List<FichaMedica.GrupoSangineo> getListaGruposSangineos() {
        wire();
        List<FichaMedica.GrupoSangineo> list = Arrays.asList(getInstance().getGrupoSangineo().values());
        return list;
    }
    /*....==>*/

    public Long getGenerarNumeroFicha() {
        List<FichaMedica> listaF = fichaMedicaService.getFichasMedicas();
        Long num = new Long(001);
        if (!listaF.isEmpty()) {
            for (FichaMedica fm : listaF) {
                if (fm.getNumeroFicha() >= num) {
                    num = (fm.getNumeroFicha() + 1);
                }
            }
        }
        return num;
    }

    public List<String> getListaGeneros() {
        List<String> generos = new ArrayList<String>();
        generos.add("femenino");
        generos.add("masculino");
        return generos;
    }

    /*<== métodos para selección de consultas medicas en la tabla de primefaces...
     public void onRowSelect(SelectEvent event) {
     FacesMessage msg = new FacesMessage(UI.getMessages("Consulta Medica") + " " + UI.getMessages("common.selected"), "" + ((ConsultaMedica) event.getObject()).getId());
     FacesContext.getCurrentInstance().addMessage("", msg);
     }

     public void onRowUnselect(UnselectEvent event) {
     FacesMessage msg = new FacesMessage(UI.getMessages("Consulta Medica") + " " + UI.getMessages("common.unselected"), "" + ((ConsultaMedica) event.getObject()).getId());
     FacesContext.getCurrentInstance().addMessage("", msg);
     this.setConsultaMedica(null);
     }

     public void onRowSelect1(SelectEvent event) {
     FacesMessage msg = new FacesMessage(UI.getMessages("Consulta Odontologica") + " " + UI.getMessages("common.selected"), "" + ((ConsultaOdontologica) event.getObject()).getId());
     FacesContext.getCurrentInstance().addMessage("", msg);
     FacesContext f = FacesContext.getCurrentInstance();
     f.getExternalContext().getFlash().setKeepMessages(true);
     //RequestContext.getCurrentInstance().execute("msgDialog.show()");
     }

     public void onRowUnselect1(UnselectEvent event) {
     FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, UI.getMessages("Consulta Odontologica") + " " + UI.getMessages("common.unselected"), "" + ((ConsultaOdontologica) event.getObject()).getId());
     FacesContext.getCurrentInstance().addMessage("", msg);
     this.setConsultaMedica(null);
     }*/
    public void cargarSignosVitales(List<ConsultaMedica> lcm, List<ConsultaOdontologica> lco) {
        listaSignosVitales = new ArrayList<SignosVitales>();
        List<SignosVitales> lsgTemp1 = new ArrayList<SignosVitales>();
        List<SignosVitales> lsgTemp = new ArrayList<SignosVitales>();
        if (!lcm.isEmpty()) {
            for (ConsultaMedica cm : lcm) {
                if (!lsgTemp.contains(cm.getSignosVitales())) {
                    lsgTemp.add(cm.getSignosVitales());
                }
            }
        }
        if (!lco.isEmpty()) {
            for (ConsultaOdontologica co : lco) {
                if (!lsgTemp1.contains(co.getSignosVitales())) {
                    lsgTemp1.add(co.getSignosVitales());
                }
            }
        }
        if (!lsgTemp.isEmpty() & !lsgTemp1.isEmpty()) {
            listaSignosVitales = new ArrayList<SignosVitales>();
            List<SignosVitales> listasg = new ArrayList<SignosVitales>();
            //lsgTemp.addAll(lsgTemp1);    
            listasg = UtilRoot.getFilter(lsgTemp, lsgTemp1);
            listaSignosVitales.addAll(listasg);
        } else if (!lsgTemp.isEmpty() & lsgTemp1.isEmpty()) {
            listaSignosVitales.addAll(lsgTemp);
        } else if (lsgTemp.isEmpty() & !lsgTemp1.isEmpty()) {
            listaSignosVitales.addAll(lsgTemp1);
        }
    }
    /*....==>*/

    public String nuevaConsulta() {
        SecurityRules sr = new SecurityRules();
        String salida = null;
        if (getInstance().isPersistent()) {
            if ("Universitario".equals(getInstance().getPaciente().getTipoEstudiante())) {
                boolean matriculado = coneccionSGA.getEstudianteMatriculado_WS_SGA(getInstance().getPaciente().getCedula());
                if (matriculado) {
                    if (sr.isEnfermero(identity)) {
                        salida = salidaVista(1);
                    } else if (sr.isMedico(identity)) {
                        salida = salidaVista(2);
                    } else if (sr.isEnfermero(identity)) {
                        salida = salidaVista(3);
                    }
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "El paciente no puede acceder a los servicios ya que actualmente no esta matriculado", " ");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } else {
                if (sr.isEnfermero(identity)) {
                    salida = salidaVista(1);
                } else if (sr.isMedico(identity)) {
                    salida = salidaVista(2);
                } else if (sr.isEnfermero(identity)) {
                    salida = salidaVista(3);
                }
            }

        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione una Ficha Médica", " ");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return salida;
    }

    public String salidaVista(int s) {
        if (s == 1) {
            return "/pages/depSalud/enfermeria/signosVitales.xhtml?faces-redirect=true"
                    + "&fichaMedicaId=" + getInstance().getId()
                    + "&backView=fichaMedica";
        } else if (s == 2) {
            return "/pages/depSalud/medicina/consultaMedica.xhtml?faces-redirect=true"
                    + "&fichaMedicaId=" + getInstance().getId()
                    + "&backView=/fichaMedica";
        } else if (s == 3) {
            return "/pages/depSalud/odontologia/consultaOdontologica.xhtml?faces-redirect=true"
                    + "&fichaMedicaId=" + getInstance().getId()
                    + "&backView=/fichaMedica";
        } else {
            return null;
        }
    }

    public boolean estudientaMatriculado() {
        if (getInstance().isPersistent()) {
            if ("Universitario".equals(getInstance().getPaciente().getTipoEstudiante())) {
                return  coneccionSGA.getEstudianteMatriculado_WS_SGA(getInstance().getPaciente().getCedula());                
            }else{
                return true;
            }
        }
        return false;
    }
    
    public String titleNuevaConsulta(){
        if(estudientaMatriculado()){
            return "Agregar Nueva Consulta";
        }else{
            return "El paciente no puede acceder a los servicios ya que actualmente no esta matriculado";
        }        
    }
}
