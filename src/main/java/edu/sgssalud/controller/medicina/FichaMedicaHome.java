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
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.security.authorization.SecurityRules;
import edu.sgssalud.service.farmacia.RecetaServicio;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.HistoriaClinicaServicio;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.odontologia.consultaOdontologicaServicio;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.StringValidations;
import edu.sgssalud.util.UI;
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
    private consultaOdontologicaServicio consultaOdontService;
    @Inject
    private RecetaServicio recetaServicio;
    private Long pacienteId;
    private Long consultaMedicaId;
    private String parametroBusqueda;
    private Paciente paciente;
    private HistoriaClinica historiaClinica;
    private FichaOdontologica fichaOdontologica;
    private ConsultaMedica consultaMedica;
    private ConsultaOdontologica consultaOdont;
    private List<ConsultaMedica> listaConsultasM = new ArrayList<ConsultaMedica>();
    private List<ConsultaOdontologica> listaConsultasO = new ArrayList<ConsultaOdontologica>();

    /*<== Métodos get y set para obtener el Id de la clase*/
    public Long getFichaMedicaId() {
        return (Long) getId();
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        setId(fichaMedicaId);
        if (getInstance().isPersistent()) {
            this.setPaciente(getInstance().getPaciente());
            this.setHistoriaClinica(historiaClinService.buscarPorFichaMedica(getInstance()));
            this.setFichaOdontologica(fichaOdonServicio.getFichaOdontologicaPorFichaMedica(getInstance()));
            this.setListaConsultasM(consultaMedService.getConsultaMedicaPorHistoriaClinica(historiaClinica));
            this.setListaConsultasO(consultaOdontService.buscarPorFichaOdontologica(fichaOdontologica));
        }
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
        this.setPaciente(pacienteS.getPacientePorId(pacienteId));
        this.setInstance(fichaMedicaService.getFichaMedicaPorPaciente(paciente));
        if (!getInstance().isPersistent()) {
            getInstance().setNumeroFicha(getGenerarNumeroFicha());
        } else {
            this.setHistoriaClinica(historiaClinService.buscarPorFichaMedica(getInstance()));
            this.setFichaOdontologica(fichaOdonServicio.getFichaOdontologicaPorFichaMedica(getInstance()));
            this.setListaConsultasM(consultaMedService.getConsultaMedicaPorHistoriaClinica(historiaClinica));
            this.setListaConsultasO(consultaOdontService.buscarPorFichaOdontologica(fichaOdontologica));
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

    public List<ConsultaOdontologica> getListaConsultasO() {
        return listaConsultasO;
    }

    public void setListaConsultasO(List<ConsultaOdontologica> listaConsultasO) {
        Collections.sort(listaConsultasO);
        this.listaConsultasO = listaConsultasO;
    }

    public List<ConsultaMedica> getListaConsultasM() {
        Collections.sort(listaConsultasM);
        return listaConsultasM;
    }

    public void setListaConsultasM(List<ConsultaMedica> listaConsultasM) {
        this.listaConsultasM = listaConsultasM;
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
            listaConsultasM = consultaMedService.getConsultaMedicaPorHistoriaClinica(historiaClinica);
        } else {
            listaConsultasM = new ArrayList<ConsultaMedica>();
        }
        //getInstance().setFechaApertura(new Date());        
        this.getInstance().setNumeroFicha(this.getGenerarNumeroFicha());  //asignacion automatica de numero de ficha
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
        if(identity.isLoggedIn()){
            fichaMedic.setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));    //cambiar atributo a 
        }        
        fichaMedic.setType(_type);
        fichaMedic.buildAttributes(bussinesEntityService);  //

        historiaClinica = new HistoriaClinica();
        historiaClinica.setCreatedOn(now);
        historiaClinica.setActivationTime(now);
        historiaClinica.setLastUpdate(now);
        fichaOdontologica = new FichaOdontologica();
        fichaOdontologica.setCreatedOn(now);
        fichaOdontologica.setActivationTime(now);
        fichaOdontologica.setLastUpdate(now);
        return fichaMedic;
    }

    @Override
    public Class<FichaMedica> getEntityClass() {
        return FichaMedica.class;
    }

    @TransactionAttribute
    public String guardar() {
        log.info("Ingreso a guardar");
        String salida = null;
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        historiaClinica.setLastUpdate(now);
        fichaOdontologica.setLastUpdate(now);
        try {
            if (paciente.isPersistent()) {
                if (getInstance().isPersistent()) {
                    getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
                    save(getInstance());
                    FacesMessage msg = new FacesMessage("Se actualizo Ficha Medica: " + getInstance().getNumeroFicha() + " con éxito");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                } else {
                    this.getInstance().setPaciente(paciente);
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
                    this.setListaConsultasM(consultaMedService.getConsultaMedicaPorHistoriaClinica(historiaClinica));
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
        SecurityRules s = new SecurityRules();
        String salida = "/pages/depSalud/fichaMedica.xhtml?faces-redirect=true";
//        if(s.isOdontologo(identity)){
//            salida = "/pages/odontologia/fichaMedica.xhtml?faces-redirect=true";
//        }else{
//            salida = "/pages/medicina/fichaMedica.xhtml?faces-redirect=true";
//        }       
        
        if (!parametroBusqueda.isEmpty() && StringValidations.isDecimal(parametroBusqueda)) {
            FichaMedica fichaMedList = fichaMedicaService.getFichaMedicaPorNumeroFicha(Long.parseLong(parametroBusqueda));
            if (fichaMedList != null) {
                this.setInstance(fichaMedList);
                this.setPaciente(getInstance().getPaciente());
                salida += "&fichaMedicaId=" + getInstance().getId();
            } else {
                Paciente p = pacienteS.buscarPorCedula(parametroBusqueda);
                if (p != null) {
                    this.setPaciente(p);
                    this.setInstance(fichaMedicaService.getFichaMedicaPorPaciente(p));
                    salida += "&pacienteId=" + paciente.getId();
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "No encontro resultados", "");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El parametro de busqueda es incorrecto", " "));
        }
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

    public List<Paciente.Genero> getListaGeneros() {
        wire();
        List<Paciente.Genero> list = Arrays.asList(paciente.getGenero().values());
        return list;
    }

    /*<== métodos para selección de consultas medicas en la tabla de primefaces...*/
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
    }
    /*....==>*/
}
