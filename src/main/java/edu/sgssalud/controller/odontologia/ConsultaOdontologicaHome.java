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
package edu.sgssalud.controller.odontologia;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.model.farmacia.Receta_Medicamento;
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import edu.sgssalud.model.labClinico.ResultadoExamenLabClinico;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.Diente;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.odontologia.Odontograma;
import edu.sgssalud.model.odontologia.Tratamiento;
import edu.sgssalud.model.servicios.Servicio;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.ServiciosMedicosService;
import edu.sgssalud.service.farmacia.RecetaMedicamentoService;
import edu.sgssalud.service.farmacia.RecetaServicio;
import edu.sgssalud.service.labClinico.ResultadoExamenLCService;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.odontologia.ConsultaOdontologicaServicio;
import edu.sgssalud.service.odontologia.TratamientoServicio;
import edu.sgssalud.util.FechasUtil;
import java.io.InputStream;
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
import org.primefaces.model.UploadedFile;

/**
 *
 * @author cesar
 */
@Named("consultaOdontHome")
@ViewScoped
public class ConsultaOdontologicaHome extends BussinesEntityHome<ConsultaOdontologica> implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ConsultaOdontologicaHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private ConsultaOdontologicaServicio consultaOdontServicio;
    @Inject
    private FichaMedicaServicio fichaMedicaServicio;
    @Inject
    private FichaOdontologicaServicio fichaOdontServicio;
    @Inject
    private Identity identity;
    @Inject
    private ProfileService profileS;
    @Inject
    private TratamientoServicio tratamientoServicio;
    @Inject
    private RecetaMedicamentoService recetaMedicamentoServicio;
    @Inject
    private RecetaServicio recetasServicio;
    @Inject
    private ResultadoExamenLCService resultadosExamenesService;
    private Diente diente;
    private FichaOdontologica fichaOdontolog;
    //private Servicio servicio;
    private SignosVitales signosVitales;
    private Tratamiento tratamiento;
    private Long fichaMedicaId;
    private PedidoExamenLaboratorio pedidoExamen;
    private Receta receta;

    private UploadedFile file;
    //private List<Servicio> listaServicios = new ArrayList<Servicio>();
    private List<Diente> listaDientes = new ArrayList<Diente>();
    private List<Diente> listaDientesC1 = new ArrayList<Diente>();
    private List<Diente> listaDientesC2 = new ArrayList<Diente>();
    private List<Diente> listaDientesC3 = new ArrayList<Diente>();
    private List<Diente> listaDientesC4 = new ArrayList<Diente>();
    private List<Tratamiento> tratamientos = new ArrayList<Tratamiento>();

    public Long getConsultaOdontologicaId() {
        return (Long) getId();
    }

    public void setConsultaOdontologicaId(Long consultaOdontId) {
        setId(consultaOdontId);
        tratamientos = tratamientoServicio.buscarPorConsultaOdontologica(consultaOdontServicio.getPorId(getConsultaOdontologicaId()));
    }

    public FichaOdontologica getFichaOdontolog() {
        return fichaOdontolog;
    }

    public void setFichaOdontolog(FichaOdontologica fichaOdontolog) {
        this.fichaOdontolog = fichaOdontolog;
    }

    public Long getFichaMedicaId() {
        return fichaMedicaId;
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        this.fichaMedicaId = fichaMedicaId;
        if (fichaMedicaId != null) {
            this.setFichaOdontolog(fichaOdontServicio.getFichaOdontologicaPorFichaMedica(
                    fichaMedicaServicio.getFichaMedicaPorId(fichaMedicaId)));
        }
    }

    public SignosVitales getSignosVitales() {
        return signosVitales;
    }

    public void setSignosVitales(SignosVitales signosVitales) {
        this.signosVitales = signosVitales;
    }

    public Diente getDiente() {
        return diente;
    }

    public void setDiente(Diente diente) {
        log.info("valor diente: " + diente.toString());
        this.diente = diente;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<Diente> getListaDientes() {
        return listaDientes;
    }

    public void setListaDientes(List<Diente> listaDientes) {
        this.listaDientes = listaDientes;
    }

    public List<Diente> getListaDientesC1() {
        //this.IniciarDientes();
        Collections.sort(listaDientesC1);
        return listaDientesC1;
    }

    public void setListaDientesC1(List<Diente> listaDientesC1) {
        this.listaDientesC1 = listaDientesC1;
    }

    public List<Diente> getListaDientesC2() {
        return listaDientesC2;
    }

    public void setListaDientesC2(List<Diente> listaDientesC2) {
        this.listaDientesC2 = listaDientesC2;
    }

    public List<Diente> getListaDientesC3() {
        return listaDientesC3;
    }

    public void setListaDientesC3(List<Diente> listaDientesC3) {
        this.listaDientesC3 = listaDientesC3;
    }

    public List<Diente> getListaDientesC4() {
        Collections.sort(listaDientesC4);
        return listaDientesC4;
    }

    public void setListaDientesC4(List<Diente> listaDientesC4) {
        this.listaDientesC4 = listaDientesC4;
    }

    public List<Tratamiento> getTratamientos() {
        return tratamientos;
    }

    public void setTratamientos(List<Tratamiento> tratamientos) {
        this.tratamientos = tratamientos;
    }

    public PedidoExamenLaboratorio getPedidoExamen() {
        return pedidoExamen;
    }

    public void setPedidoExamen(PedidoExamenLaboratorio pedidoExamen) {
        this.pedidoExamen = pedidoExamen;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }

    @TransactionAttribute
    public ConsultaOdontologica load() {
        if (isIdDefined()) {
            wire();
        }
        //log.info("sgssalud --> cargar instance " + getInstance());
        if (getInstance().isPersistent()) {
            if (getInstance().getResponsable() == null) {
                getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
            }
        }
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
        consultaOdontServicio.setEntityManager(em);
        fichaMedicaServicio.setEntityManager(em);
        fichaOdontServicio.setEntityManager(em);
        profileS.setEntityManager(em);
        tratamientoServicio.setEntityManager(em);
        recetaMedicamentoServicio.setEntityManager(em);
        recetasServicio.setEntityManager(em);
        receta = new Receta();
        resultadosExamenesService.setEntityManager(em);

        if (getInstance().isPersistent()) {
//            this.IniciarDientes();
            Odontograma o = new Odontograma();
            //          o.setDientes(listaDientes);
//            log.info("Init Dientes  " + fichaOdontolog.toString());
            //fichaOdontolog.setOdontograma(o);
            //fichaOdontolog.setOdontogramaInicial(o);            
        }
        getInstance().setTiempoConsulta(FechasUtil.sumarRestaMinutosFecha(getInstance().getHoraConsulta(), 30));
        //log.info("Odont Inicial " + fichaOdontolog.getOdontogramaInicial());
        //log.info("Odont  " + fichaOdontolog.getOdontograma());

        /*if (!fichaOdontolog.getOdontograma().isPersistent()) {
         fichaOdontolog.setOdontograma(o);
         }
         if (!fichaOdontolog.getOdontogramaInicial().isPersistent()) {
         fichaOdontolog.setOdontogramaInicial(o);
         }*/
    }

    @Override
    protected ConsultaOdontologica createInstance() {
        //prellenado estable para cualquier clase 
        BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(ConsultaOdontologica.class.getName());
        Date now = Calendar.getInstance().getTime();
        ConsultaOdontologica consultaOdont = new ConsultaOdontologica();
        consultaOdont.setCreatedOn(now);
        consultaOdont.setLastUpdate(now);
        consultaOdont.setActivationTime(now);
        consultaOdont.setSignosVitales(new SignosVitales());
        consultaOdont.setType(_type);
        consultaOdont.buildAttributes(bussinesEntityService);

//        fOdontologica.setCreatedOn(now);
//        fOdontologica.setActivationTime(now);
//        fOdontologica.setLastUpdate(now);
        return consultaOdont;
    }

    @Override
    public Class<ConsultaOdontologica> getEntityClass() {
        return ConsultaOdontologica.class;
    }

    @TransactionAttribute
    public String guardar() {
        String salida = "";
        System.out.println("Guardar__________");
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        try {
            if (getInstance().isPersistent()) {
                //System.out.println("Guardar__________1" + getInstance().getId());
                getInstance().setCode("REALIZADA");
                getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
                getInstance().getSignosVitales().setFechaActual(now);
                //System.out.println("Guardar__________ 2");
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo Consulta Odontológica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                System.out.println("Guardar__________3");
            } else {
                getInstance().setCode("REALIZADA");
                getInstance().setFichaOdontologica(fichaOdontolog);
                getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
                getInstance().getSignosVitales().setFechaActual(now);
                create(getInstance().getSignosVitales());
                create(getInstance());
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se creo nueva Consulta Odontológica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                System.out.println("SE CREO CORRECTAMENTE______");
                salida = "/pages/depSalud/odontologia/consultaOdontologica.xhtml?faces-redirect=true"
                        + "&fichaMedicaId=" + getFichaMedicaId()
                        + "&consultaOdontId=" + getInstance().getId()
                        + "&backView=" + getBackView();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return salida;
    }
    /*
     @TransactionAttribute
     public void guardarTratamiento() {
     log.info("valor diente: " + diente);
     diente.setRutaIcon(servicio.getRutaImg());
     tratamiento.setFechaRelizacion(new Date());
     tratamientos.add(tratamiento);
     this.actualizarDiente(diente);
     }*/
    /*
     @TransactionAttribute
     public String agregarOdontograma(int odont) {
     String salida = null;
     Odontograma odontog = new Odontograma();
     //odontog.setDientes(this.agregarDientes());
     try {
     if (odont == 1) {  //inicial
     create(odontog);
     fichaOdontolog.setOdontogramaInicial(odontog);
     save(odontog);
     save(fichaOdontolog);
     salida = "/pages/depSalud/odontologia/odont.xhtml?faces-redirect=true"
     + "&fichaMedicaId=" + getFichaMedicaId()
     + "&consultaOdontId=" + getInstance().getId()
     + "&odontogramaId=" + odontog.getId()
     + "&backView=consultaOdontologica"
     + "&tipo=1";
     } else {  //Evolutivo
     create(odontog);
     fichaOdontolog.setOdontograma(odontog);
     save(odontog);
     save(fichaOdontolog);
     salida = "/pages/depSalud/odontologia/odont.xhtml?faces-redirect=true"
     + "&fichaMedicaId=" + getFichaMedicaId()
     + "&consultaOdontId=" + getInstance().getId()
     + "&odontogramaId=" + odontog.getId()
     + "&backView=consultaOdontologica"
     + "&tipo=0";
     }
     } catch (Exception e) {
     FacesMessage msg = new FacesMessage("Error", "al crear odontograma");
     FacesContext.getCurrentInstance().addMessage(null, msg);
     }
     return salida;
     }*/

    public void upload() {

        try {
            byte[] buffer = new byte[(int) file.getSize()];
            this.getInstance().setRadiografiaDental(buffer);
            FacesMessage msg = new FacesMessage("Ok", "Fichero " + file.getFileName() + " subido correctamente.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            log.info("Consulta Odont: Error al cargar la imagen");
        }
    }
    /*
     public void IniciarDientes() {
     listaDientesC1 = new ArrayList<Diente>();
     listaDientesC2 = new ArrayList<Diente>();
     listaDientesC3 = new ArrayList<Diente>();
     listaDientesC4 = new ArrayList<Diente>();
     String ruta = "/resources/odontograma/diente.png";
     for (int i = 1; i <= 4; i++) {
     for (int j = 1; j <= 8; j++) {
     String p = "" + i + j;
     if (i == 1) {
     if (j == 1 || j == 2) {
     listaDientesC1.add(new Diente("Incisivo", Integer.parseInt(p), i));
     }
     if (j == 3) {
     listaDientesC1.add(new Diente("Canino", Integer.parseInt(p), i));
     }
     if (j == 4 || j == 5) {
     listaDientesC1.add(new Diente("Premolar", Integer.parseInt(p), i));
     }
     if (j == 6 || j == 7 || j == 8) {
     listaDientesC1.add(new Diente("Molar", Integer.parseInt(p), i));
     }
     } else if (i == 2) {
     if (j == 1 || j == 2) {
     listaDientesC2.add(new Diente("Incisivo", Integer.parseInt(p), i));
     }
     if (j == 3) {
     listaDientesC2.add(new Diente("Canino", Integer.parseInt(p), i));
     }
     if (j == 4 || j == 5) {
     listaDientesC2.add(new Diente("Premolar", Integer.parseInt(p), i));
     }
     if (j == 6 || j == 7 || j == 8) {
     listaDientesC2.add(new Diente("Molar", Integer.parseInt(p), i));
     }
     } else if (i == 3) {
     if (j == 1 || j == 2) {
     listaDientesC3.add(new Diente("Incisivo", Integer.parseInt(p), i));
     }
     if (j == 3) {
     listaDientesC3.add(new Diente("Canino", Integer.parseInt(p), i));
     }
     if (j == 4 || j == 5) {
     listaDientesC3.add(new Diente("Premolar", Integer.parseInt(p), i));
     }
     if (j == 6 || j == 7 || j == 8) {
     listaDientesC3.add(new Diente("Molar", Integer.parseInt(p), i));
     }
     } else if (i == 4) {
     if (j == 1 || j == 2) {
     listaDientesC4.add(new Diente("Incisivo", Integer.parseInt(p), i));
     }
     if (j == 3) {
     listaDientesC4.add(new Diente("Canino", Integer.parseInt(p), i));
     }
     if (j == 4 || j == 5) {
     listaDientesC4.add(new Diente("Premolar", Integer.parseInt(p), i));
     }
     if (j == 6 || j == 7 || j == 8) {
     listaDientesC4.add(new Diente("Molar", Integer.parseInt(p), i));
     }
     }
     }
     }
     }

     public void actualizarDiente(Diente dient) {
     if (dient.getCuadrante() == 1) {
     for (Diente d : listaDientesC1) {
     if (d.equals(dient)) {
     d = dient;
     }
     }
     } else if (dient.getCuadrante() == 2) {
     for (Diente d : listaDientesC3) {
     if (d.equals(dient)) {
     d = dient;
     }
     }
     } else if (dient.getCuadrante() == 3) {
     for (Diente d : listaDientesC2) {
     if (d.equals(dient)) {
     d = dient;
     }
     }
     } else if (dient.getCuadrante() == 4) {
     for (Diente d : listaDientesC1) {
     if (d.equals(dient)) {
     d = dient;
     }
     }
     }
     }

     public List<Diente> agregarDientes() {
     List<Diente> dientes = new ArrayList<Diente>();
     for (int i = 1; i <= 4; i++) {
     for (int j = 1; j <= 8; j++) {
     String p = "" + i + j;
     if (j == 1 || j == 2) {
     dientes.add(new Diente("Incisivo", Integer.parseInt(p), i));
     }
     if (j == 3) {
     dientes.add(new Diente("Canino", Integer.parseInt(p), i));
     }
     if (j == 4 || j == 5) {
     dientes.add(new Diente("Premolar", Integer.parseInt(p), i));
     }
     if (j == 6 || j == 7 || j == 8) {
     dientes.add(new Diente("Molar", Integer.parseInt(p), i));
     }
     }
     }
     return dientes;
     }*/

    @TransactionAttribute
    public String borrarReceta() {
        System.out.println("Borrar receta_______: " + receta.toString());
        String salida = "";
        try {
            if (getInstance().isPersistent()) {
                Receta_Medicamento aux;
                List<Receta_Medicamento> listaRM = recetaMedicamentoServicio.obtenerPorReceta(receta);
                for (Receta_Medicamento recetaMed1 : listaRM) {
                    Medicamento medicament = recetaMed1.getMedicamento();
                    int cantidad = medicament.getUnidades() + recetaMed1.getCantidad();
                    medicament.setUnidades(cantidad);
                    aux = recetaMed1;
                    em.merge(medicament);
                    em.remove(aux);
                }
                delete(receta);
                //wire();
                //this.getInstance().setRecetas(recetasServicio.buscarRecetaPorConsultaMedica(getInstance()));
                System.out.println("ELIMINO RECETA");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se elimino receta", "" + getInstance().getId()));
                salida = "/pages/depSalud/odontologia/consultaOdontologica.xhtml?faces-redirect=true"
                        + "&fichaMedicaId=" + getFichaMedicaId()
                        + "&consultaOdontId=" + getInstance().getId()
                        + "&backView=" + getBackView();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERRORE", e.toString()));
            e.printStackTrace();
        }
        return salida;
    }

    @TransactionAttribute
    public String borrarPedidoExamen() {
        System.out.println("Borrar Pedido Examen_______: " + pedidoExamen.toString());
        String salida = "";
        try {
            if (getPedidoExamen().isPersistent()) {
                List<ResultadoExamenLabClinico> listaResultadosExamenLab = new ArrayList<ResultadoExamenLabClinico>();
                listaResultadosExamenLab = resultadosExamenesService.getResultadosExamenPorPedidoExamen(pedidoExamen);
                ResultadoExamenLabClinico aux;
                for (ResultadoExamenLabClinico result : listaResultadosExamenLab) {
                    aux = result;
                    em.remove(aux);
                }

                delete(pedidoExamen);
                //System.out.println("ELIMINO Pedido");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se elimino Pedido", "" + pedidoExamen.getId()));
                salida = "/pages/depSalud/odontologia/consultaOdontologica.xhtml?faces-redirect=true"
                        + "&fichaMedicaId=" + getFichaMedicaId()
                        + "&consultaOdontId=" + getInstance().getId()
                        + "&backView=" + getBackView();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERRORE", e.toString()));
            e.printStackTrace();
        }
        return salida;
    }

    public boolean isEditable() {
        return FechasUtil.editable(getInstance().getHoraConsulta(), 24);
    }

}
