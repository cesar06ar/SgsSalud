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
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.model.farmacia.Receta_Medicamento;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.model.labClinico.Parametros;
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import edu.sgssalud.model.labClinico.ResultadoExamenLabClinico;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.Diente;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.odontologia.Odontograma;
import edu.sgssalud.model.odontologia.Tratamiento;
import edu.sgssalud.model.servicios.Turno;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.SettingService;
import edu.sgssalud.service.farmacia.RecetaMedicamentoService;
import edu.sgssalud.service.farmacia.RecetaServicio;
import edu.sgssalud.service.labClinico.ExamenLabService;
import edu.sgssalud.service.labClinico.PedidoExamenService;
import edu.sgssalud.service.labClinico.ResultadoExamenLCService;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.TurnoService;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.odontologia.ConsultaOdontologicaServicio;
import edu.sgssalud.service.odontologia.TratamientoServicio;
import edu.sgssalud.util.FechasUtil;
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
import org.primefaces.context.RequestContext;
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
    @Inject
    SettingService settingService;
    private Setting setting;

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
//    private List<Diente> listaDientesC1 = new ArrayList<Diente>();
//    private List<Diente> listaDientesC2 = new ArrayList<Diente>();
//    private List<Diente> listaDientesC3 = new ArrayList<Diente>();
//    private List<Diente> listaDientesC4 = new ArrayList<Diente>();
    private List<Tratamiento> tratamientos = new ArrayList<Tratamiento>();

    @Inject
    private ExamenLabService examenLabService;
    @Inject
    private PedidoExamenService pedidoServicio;
    private List<ExamenLabClinico> listaExamenLab = new ArrayList<ExamenLabClinico>();
    private PedidoExamenLaboratorio pedido;

    private Long turnoId;
    private Turno turno = new Turno();
    @Inject
    private TurnoService turnoS;

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

//    public List<Diente> getListaDientesC1() {
//        //this.IniciarDientes();
//        Collections.sort(listaDientesC1);
//        return listaDientesC1;
//    }
//
//    public void setListaDientesC1(List<Diente> listaDientesC1) {
//        this.listaDientesC1 = listaDientesC1;
//    }
//
//    public List<Diente> getListaDientesC2() {
//        return listaDientesC2;
//    }
//
//    public void setListaDientesC2(List<Diente> listaDientesC2) {
//        this.listaDientesC2 = listaDientesC2;
//    }
//
//    public List<Diente> getListaDientesC3() {
//        return listaDientesC3;
//    }
//
//    public void setListaDientesC3(List<Diente> listaDientesC3) {
//        this.listaDientesC3 = listaDientesC3;
//    }
//    public List<Diente> getListaDientesC4() {
//        Collections.sort(listaDientesC4);
//        return listaDientesC4;
//    }
//
//    public void setListaDientesC4(List<Diente> listaDientesC4) {
//        this.listaDientesC4 = listaDientesC4;
//    }
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

    public PedidoExamenLaboratorio getPedido() {
        return pedido;
    }

    public void setPedido(PedidoExamenLaboratorio pedido) {
        this.pedido = pedido;
    }

    public List<ExamenLabClinico> getListaExamenLab() {
        return listaExamenLab;
    }

    public void setListaExamenLab(List<ExamenLabClinico> listaExamenLab) {
        this.listaExamenLab = listaExamenLab;
    }

    public Long getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(Long turnoId) {
        this.turnoId = turnoId;
        if (turnoId != null) {
            turno = turnoS.find(turnoId);
        }
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
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

        settingService.setEntityManager(em);
        setting = settingService.findByName("consultaActiva");
//        if (getInstance().isPersistent()) {
////            this.IniciarDientes();
//            Odontograma o = new Odontograma();
//            //          o.setDientes(listaDientes);
////            log.info("Init Dientes  " + fichaOdontolog.toString());
//            //fichaOdontolog.setOdontograma(o);
//            //fichaOdontolog.setOdontogramaInicial(o);            
//        }
        //getInstance().setTiempoConsulta(FechasUtil.sumarRestaMinutosFecha(getInstance().getHoraConsulta(), 30));
        //log.info("Odont Inicial " + fichaOdontolog.getOdontogramaInicial());
        //log.info("Odont  " + fichaOdontolog.getOdontograma());

        /*if (!fichaOdontolog.getOdontograma().isPersistent()) {
         fichaOdontolog.setOdontograma(o);
         }
         if (!fichaOdontolog.getOdontogramaInicial().isPersistent()) {
         fichaOdontolog.setOdontogramaInicial(o);
         }*/
        examenLabService.setEntityManager(em);
        pedidoServicio.setEntityManager(em);
        listaExamenLab = examenLabService.getExamenesLab();
        pedido = new PedidoExamenLaboratorio();
        turnoS.setEntityManager(em);
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
        getInstance().setTiempoConsulta(FechasUtil.sumarRestaMinutosFecha(now, 5));
        try {
            if (getInstance().isPersistent()) {
                //System.out.println("Guardar__________1" + getInstance().getId());
                getInstance().setCode("REALIZADA");
                getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
                getInstance().getSignosVitales().setFechaActual(now);
                //System.out.println("Guardar__________ 2");
                save(getInstance());
                if (turno.isPersistent()) {
                    turno.setEstado("Realizada");
                    save(turno);
                }
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
                if (turno.isPersistent()) {
                    turno.setEstado("Realizada");
                    save(turno);
                }
                FacesMessage msg = new FacesMessage("Se creo nueva Consulta Odontológica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);

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
        //return FechasUtil.editable(getInstance().getFechaConsulta(), getInstance().getHoraConsulta(), 24);
        if ("REALIZADA".equals(getInstance().getCode()) && "false".equals(setting.getValue())) {
            //  System.out.println("DESHABILITADO");
            return true;
        }
        return false;
    }

    @TransactionAttribute
    public String agregarPedido() {
        Date now = Calendar.getInstance().getTime();
        System.out.println("INGRESo a Guardar _________");
        String salida = null;
        boolean ning = false;
        for (ExamenLabClinico ex : listaExamenLab) {
            if (ex.isSelect()) {
                ning = true;
                break;
            }
        }
        try {
            if (!ning) {
                //save(getInstance());
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe seleccionar minimo un examen", null);
                FacesContext.getCurrentInstance().addMessage("", msg);
                //return null;
            } else {
                //this.listaPedidoExamenLabC = pickListExamenesLab.getTarget();                               
                pedido.setFichaOdontologica(fichaOdontolog);
                pedido.setPaciente(fichaOdontolog.getFichaMedica().getPaciente());
                pedido.setEstado("Nuevo");
                pedido.setResponsableEmision(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
                pedido.setFechaPedido(now);
                create(pedido);
                save(pedido);
                update();
                //System.out.println("Guardo Con exito 0_________");
                ResultadoExamenLabClinico resultadoExa;
                List<Parametros> pl = null;//                    
                for (ExamenLabClinico ex : this.listaExamenLab) {
                    if (ex.isSelect()) {
                        resultadoExa = new ResultadoExamenLabClinico();
                        resultadoExa.setExamenLab(ex);
                        resultadoExa.setPedidoExamenLab(pedido);
                        pl = examenLabService.getParametrosPorExamen(ex);
                        resultadoExa.agregarValoresResultados(pl);
                        save(resultadoExa);
                        //update();
                    }
                }

                FacesMessage msg = new FacesMessage("Se agrego nuevo Pedido de Examenes: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                pedido = new PedidoExamenLaboratorio();
                //hc = hcs.buscarPorFichaMedica(fms.getFichaMedicaPorId(fichaMedicaId));

                RequestContext.getCurrentInstance().update(":form:tabOpc:tablaPedidos :form:growl");
                RequestContext.getCurrentInstance().execute("pedidoDlg.hide();");
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

}
