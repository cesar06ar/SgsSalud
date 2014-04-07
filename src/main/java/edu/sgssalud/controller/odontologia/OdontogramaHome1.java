/*
 * Copyright 2013 cesar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
Z */
package edu.sgssalud.controller.odontologia;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.Diente;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.odontologia.Odontograma;
import edu.sgssalud.model.odontologia.Tratamiento;
import edu.sgssalud.model.servicios.Servicio;
import edu.sgssalud.service.ServiciosMedicosService;
import edu.sgssalud.service.generic.CrudService;
import edu.sgssalud.service.generic.QueryParameter;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.odontologia.ConsultaOdontologicaServicio;
import edu.sgssalud.util.UI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;

/**
 * @author cesar
 */
@Named("odontogramaHome1")
@ViewScoped
public class OdontogramaHome1 extends BussinesEntityHome<Odontograma> implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(OdontogramaHome1.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private FichaMedicaServicio fichaMedServ;
    @Inject
    private FichaOdontologicaServicio fichaOdontServ;
    @Inject
    private ConsultaOdontologicaServicio consultaOdontServ;
    @Inject
    private ServiciosMedicosService serviciosMedicosS;
    @Inject
    private TratamientoAction tratamientoAction;
    @EJB
    CrudService crudService;

    private Long fichaMedicaId;
    private Long consultaOdontId;
    private int tipo;  //se refiere al otontograma 0 es el inicial 1 es el evolutivo
    private String nombreOdont;
    private String procedimiento;
    private String nombreTratamiento;
    private String ruta;
    private boolean panel1, panel2, panel3 = false;
    private boolean c1, c2, c3, c4, c5 = false;
    private Diente diente11, diente12, diente13, diente14, diente15, diente16, diente17, diente18 = new Diente();
    private Diente diente21, diente22, diente23, diente24, diente25, diente26, diente27, diente28 = new Diente();
    private Diente diente31, diente32, diente33, diente34, diente35, diente36, diente37, diente38 = new Diente();
    private Diente diente41, diente42, diente43, diente44, diente45, diente46, diente47, diente48 = new Diente();
    private Diente selectDient;
    private FichaOdontologica fichaOdont;
    private ConsultaOdontologica consultaOdont;
    private Servicio servicio;
    private Tratamiento tratamiento;
    private List<Diente> listaDientes = new ArrayList<Diente>();
//    private List<Servicio> listaServicios = new ArrayList<Servicio>();
    private List<Tratamiento> listaTratamient = new ArrayList<Tratamiento>();

    public Long getOdontogramaId() {
        return (Long) getId();
    }

    public void setOdontogramaId(Long odontogramaId) {        
        setId(odontogramaId);
        if(odontogramaId != null){
            //this.setInstance((Odontograma)crudService.findSingleResultWithNamedQuery("Odontograma.buscarPorId", QueryParameter.with("odontogramaId", odontogramaId).parameters()));
            //this.actualizarDientes(crudService.findWithNamedQuery("Diente.buscarPorOdontograma", QueryParameter.with("odontogramaId", odontogramaId).parameters()));
            
        }     
    }

    @TransactionAttribute
    public Odontograma load() {
        if (isIdDefined()) {
            wire();
            //this.actualizarDientes(getInstance().getDientes());
        }
        //log.info("sgssalud --> cargar instance " + getInstance());
        return getInstance();
    }

    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        fichaMedServ.setEntityManager(em);
        fichaOdontServ.setEntityManager(em);
        consultaOdontServ.setEntityManager(em);
        //bussinesEntityService.setEntityManager(em);
        //serviciosMedicosS.setEntityManager(em);
        selectDient = new Diente();
        tratamiento = new Tratamiento();
        crudService.getEntityManager();
                
        //tratamientoAction.setEntityManager(em);
        if (!getInstance().isPersistent()) {
            getInstance().setDientes(crearDientes());
            //getInstance().getDientes().size();
        } else {
            this.actualizarDientes(getInstance().getDientes());
        }
        this.nombreTratamiento = UI.getMessages("tratamiento.carie");
        this.panel2 = true;
        this.ruta = "/resources/odontograma/caries.png";

    }

    @Override
    protected Odontograma createInstance() {
        //prellenado estable para cualquier clase 

        Date now = Calendar.getInstance().getTime();
        Odontograma odontograma = new Odontograma();

        //fichaMedic.setResponsable(null);    //cambiar atributo a         
        tratamiento = new Tratamiento();
        return odontograma;

    }

    @Override
    public Class<Odontograma> getEntityClass() {
        return Odontograma.class;
    }

    @TransactionAttribute
    public String guardar() {
        String salida = null;
        Date now = Calendar.getInstance().getTime();
        try {
            if (getInstance().isPersistent()) {
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo Odontograma: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                create(getInstance());
                if (tipo == 0) {
                    getInstance().setObservacion("Odontograma Inicial");
                    fichaOdont.setOdontogramaInicial(getInstance());
                } else {
                    getInstance().setObservacion("Odontograma Evolutivo");
                    fichaOdont.setOdontograma(getInstance());
                }
                save(getInstance());
                save(fichaOdont);
                FacesMessage msg = new FacesMessage("Se creo nuevo Odontograma: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                salida = "/pages/depSalud/odontologia/odont.xhtml?faces-redirect=true"
                        + "&fichaMedicaId=" + getFichaMedicaId()
                        + "&consultaOdontId=" + getConsultaOdontId()
                        + "&odontogramaId=" + getInstance().getId()
                        + "&backView=consultaOdontologica"
                        + "&tipo=1";
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return salida;
    }

    @Transactional
    public String borrarEntidad() {
        log.info("sgssalud --> ingreso a eliminar: " + getInstance().getId());
        return null;
    }

    @TransactionAttribute
    public String guardarTratamiento() {
        System.out.println("valor diente: ____________---" + nombreTratamiento);
        Date now = Calendar.getInstance().getTime();
        boolean ban = false;
        //String mensaje = null;
        String salida = null;
        if (panel1) {   //tratamientos comunes            
            this.cargarDientesSeleccionados();
            ban = !listaDientes.isEmpty();
        } else if (panel2) {  //cuando los tratamientos se aplican a las partes del diente            
            this.cargarDientesSeleccionados();
            //this.agregarCuadrante(tratamiento);
            ban = !listaDientes.isEmpty();
        } else if (panel3) {  //para guardar protesis total 
            ban = listaDientes.isEmpty();
        }
        //TODO.. falta agregar responsable       
        //System.out.println("Dientes Seleccionados :__________________" + getListaDientes().size() + " " + listaDientes.toString());
        //System.out.println("Tratamientos:__________________" + tratamiento.toString());        
        //System.out.println("Persistir dientes 0:__________________");

        if (ban) {
            for (Diente diente : listaDientes) {
                System.out.println("Persistir dientes 1:__________________");
                tratamiento = new Tratamiento();
                //            if (ban) {  //TODO___  controlar si el diente ya tiene un tratamiento especifico 
                tratamiento.setFechaRealizacion(now);
                tratamiento.setNombre(getNombreTratamiento());
                tratamiento.setProcedimiento(procedimiento);
                this.agregarCuadrante(tratamiento);
                tratamiento.setConsultaOdontologica(consultaOdont);
                tratamiento.setDiente(extraerDiente(diente.getPosicion()));

                System.out.println("Persistir dientes 2:__________________");
                create(tratamiento);
                //diente.setSelect(false);
                FacesMessage msg = new FacesMessage("Se agrego tratamiento: " + tratamiento.getNombre() + " con éxito");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                salida = "/pages/depSalud/odontologia/odont.xhtml?faces-redirect=true"
                        + "&fichaMedicaId=" + getFichaMedicaId()
                        + "&consultaOdontId=" + getConsultaOdontId()
                        + "&odontogramaId=" + getInstance().getId()
                        + "&backView=consultaOdontologica"
                        + "&tipo=1";
//            } else {
//                FacesMessage msg = new FacesMessage("El diente deleccionado ya tiene el tratamiento: " + tratamiento.getNombre());
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//                System.out.println("Ingreso aqui");
//            }
                //this.actualizarDiente(diente);  //no esta actualizando revisar            
            }
        } else {
            FacesMessage msg = new FacesMessage("Debe seleccionar un diente ");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        //save(consultaOdont);
        System.out.println("_______: guardo con exito ");
        tratamiento = new Tratamiento();
        this.setProcedimiento(null);
        this.setListaTratamient(consultaOdontServ.getTratamientosPorConsulta(consultaOdontId, getOdontogramaId()));
        //this.actualizarDientes(getInstance().getDientes());
        //return null;
        return salida;
    }

    @TransactionAttribute
    public void borrarTratamiento() {
        try {
            if (tratamiento.isPersistent()) {
                delete(tratamiento);
                FacesMessage msg = new FacesMessage("Se elimino tratamiento: " + tratamiento.getNombre() + " con éxito");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage("el tratamiento no es persistente");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL GUARDAR", null));
        }

    }

    public Long getFichaMedicaId() {
        return fichaMedicaId;
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        this.fichaMedicaId = fichaMedicaId;
        if (fichaMedicaId != null) {
            FichaMedica f = fichaMedServ.getFichaMedicaPorId(fichaMedicaId);
            this.setFichaOdont(fichaOdontServ.getFichaOdontologicaPorFichaMedica(f));
            if (getInstance().isPersistent()) {
                this.actualizarDientes(getInstance().getDientes());
            }
        }
    }

    public Long getConsultaOdontId() {
        return consultaOdontId;
    }

    public void setConsultaOdontId(Long consultaOdontId) {
        this.consultaOdontId = consultaOdontId;
        if (consultaOdontId != null) {
            setConsultaOdont(consultaOdontServ.getPorId(consultaOdontId));
        }
    }

    public FichaOdontologica getFichaOdont() {
        return fichaOdont;
    }

    public void setFichaOdont(FichaOdontologica fichaOdont) {
        this.fichaOdont = fichaOdont;
    }

    public ConsultaOdontologica getConsultaOdont() {
        return consultaOdont;
    }

    public void setConsultaOdont(ConsultaOdontologica consultaOdont) {
        this.consultaOdont = consultaOdont;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
        if (tipo == 0) {
            this.setNombreOdont("Inicial");
        } else {
            this.setNombreOdont("Evolutivo");
        }
    }

    public Diente getSelectDient() {
        return selectDient;
    }

    public void setSelectDient(Diente selectDient) {
        //log.info("setSelecDiente");
        this.selectDient = selectDient;
        //this.setNombreDiente(this.selectDient.getNombre() + this.selectDient.getPosicion());
    }

    public Diente getDiente11() {
        return diente11;
    }

    public void setDiente11(Diente diente11) {
        this.diente11 = diente11;
    }

    public Diente getDiente12() {
        return diente12;
    }

    public void setDiente12(Diente diente12) {
        this.diente12 = diente12;
    }

    public Diente getDiente13() {
        return diente13;
    }

    public void setDiente13(Diente diente13) {
        this.diente13 = diente13;
    }

    public Diente getDiente14() {
        return diente14;
    }

    public void setDiente14(Diente diente14) {
        this.diente14 = diente14;
    }

    public Diente getDiente15() {
        return diente15;
    }

    public void setDiente15(Diente diente15) {
        this.diente15 = diente15;
    }

    public Diente getDiente16() {
        return diente16;
    }

    public void setDiente16(Diente diente16) {
        this.diente16 = diente16;
    }

    public Diente getDiente17() {
        return diente17;
    }

    public void setDiente17(Diente diente17) {
        this.diente17 = diente17;
    }

    public Diente getDiente18() {
        return diente18;
    }

    public void setDiente18(Diente diente18) {
        this.diente18 = diente18;
    }

    public Diente getDiente21() {
        return diente21;
    }

    public void setDiente21(Diente diente21) {
        this.diente21 = diente21;
    }

    public Diente getDiente22() {
        return diente22;
    }

    public void setDiente22(Diente diente22) {
        this.diente22 = diente22;
    }

    public Diente getDiente23() {
        return diente23;
    }

    public void setDiente23(Diente diente23) {
        this.diente23 = diente23;
    }

    public Diente getDiente24() {
        return diente24;
    }

    public void setDiente24(Diente diente24) {
        this.diente24 = diente24;
    }

    public Diente getDiente25() {
        return diente25;
    }

    public void setDiente25(Diente diente25) {
        this.diente25 = diente25;
    }

    public Diente getDiente26() {
        return diente26;
    }

    public void setDiente26(Diente diente26) {
        this.diente26 = diente26;
    }

    public Diente getDiente27() {
        return diente27;
    }

    public void setDiente27(Diente diente27) {
        this.diente27 = diente27;
    }

    public Diente getDiente28() {
        return diente28;
    }

    public void setDiente28(Diente diente28) {
        this.diente28 = diente28;
    }

    public Diente getDiente31() {
        return diente31;
    }

    public void setDiente31(Diente diente31) {
        this.diente31 = diente31;
    }

    public Diente getDiente32() {
        return diente32;
    }

    public void setDiente32(Diente diente32) {
        this.diente32 = diente32;
    }

    public Diente getDiente33() {
        return diente33;
    }

    public void setDiente33(Diente diente33) {
        this.diente33 = diente33;
    }

    public Diente getDiente34() {
        return diente34;
    }

    public void setDiente34(Diente diente34) {
        this.diente34 = diente34;
    }

    public Diente getDiente35() {
        return diente35;
    }

    public void setDiente35(Diente diente35) {
        this.diente35 = diente35;
    }

    public Diente getDiente36() {
        return diente36;
    }

    public void setDiente36(Diente diente36) {
        this.diente36 = diente36;
    }

    public Diente getDiente37() {
        return diente37;
    }

    public void setDiente37(Diente diente37) {
        this.diente37 = diente37;
    }

    public Diente getDiente38() {
        return diente38;
    }

    public void setDiente38(Diente diente38) {
        this.diente38 = diente38;
    }

    public Diente getDiente41() {
        return diente41;
    }

    public void setDiente41(Diente diente41) {
        this.diente41 = diente41;
    }

    public Diente getDiente42() {
        return diente42;
    }

    public void setDiente42(Diente diente42) {
        this.diente42 = diente42;
    }

    public Diente getDiente43() {
        return diente43;
    }

    public void setDiente43(Diente diente43) {
        this.diente43 = diente43;
    }

    public Diente getDiente44() {
        return diente44;
    }

    public void setDiente44(Diente diente44) {
        this.diente44 = diente44;
    }

    public Diente getDiente45() {
        return diente45;
    }

    public void setDiente45(Diente diente45) {
        this.diente45 = diente45;
    }

    public Diente getDiente46() {
        return diente46;
    }

    public void setDiente46(Diente diente46) {
        this.diente46 = diente46;
    }

    public Diente getDiente47() {
        return diente47;
    }

    public void setDiente47(Diente diente47) {
        this.diente47 = diente47;
    }

    public Diente getDiente48() {
        return diente48;
    }

    public void setDiente48(Diente diente48) {
        this.diente48 = diente48;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
        //this.SelectDient.setRutaIcon(servicio.getRutaImg());
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public List<Tratamiento> getListaTratamient() {
        //consultaOdontServ.buscarTratamientos;
        return consultaOdontServ.getTratamientosPorConsulta(consultaOdontId, getOdontogramaId());
        //return listaTratamient;
    }

    public void setListaTratamient(List<Tratamiento> listaTratamient) {
        this.listaTratamient = listaTratamient;
    }

    public List<Diente> getListaDientes() {
        return listaDientes;
    }

    public void setListaDientes(List<Diente> listaDientes) {
        this.listaDientes = listaDientes;
    }

    public String getNombreOdont() {
        //return (selectDient.getNombre() + " " + selectDient.getPosicion());
        return nombreOdont;
    }

    public void setNombreOdont(String nombreOdont) {
        this.nombreOdont = nombreOdont;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public boolean isPanel1() {
        return panel1;
    }

    public void setPanel1(boolean panel1) {
        this.panel1 = panel1;
    }

    public boolean isPanel2() {
        return panel2;
    }

    public void setPanel2(boolean panel2) {
        this.panel2 = panel2;
    }

    public boolean isPanel3() {
        return panel3;
    }

    public void setPanel3(boolean panel3) {
        this.panel3 = panel3;
    }

    public boolean isC1() {
        return c1;
    }

    public void setC1(boolean c1) {
        this.c1 = c1;
    }

    public boolean isC2() {
        return c2;
    }

    public void setC2(boolean c2) {
        this.c2 = c2;
    }

    public boolean isC3() {
        return c3;
    }

    public void setC3(boolean c3) {
        this.c3 = c3;
    }

    public boolean isC4() {
        return c4;
    }

    public void setC4(boolean c4) {
        this.c4 = c4;
    }

    public boolean isC5() {
        return c5;
    }

    public void setC5(boolean c5) {
        this.c5 = c5;
    }

    public void actualizarDientes(List<Diente> dientes) {
        for (Diente d : dientes) {
            actualizarDiente(d);
        }
    }

    public void actualizar() {
        selectDient = new Diente();
    }

    public void actualizarDiente(Diente d) {
        System.out.println("actualizar diente: ________"+d.getId());
        //if (d.isPersistent()) {
            if (d.getPosicion() == 11) {
                this.diente11 = d;
                this.getDiente11().setTratamientos(d.getTratamientos());
            } else if (d.getPosicion() == 12) {
                this.diente12 = d;
            } else if (d.getPosicion() == 13) {
                this.diente13 = d;
            } else if (d.getPosicion() == 14) {
                this.diente14 = d;
            } else if (d.getPosicion() == 15) {
                this.diente15 = d;
            } else if (d.getPosicion() == 16) {
                this.diente16 = d;
            } else if (d.getPosicion() == 17) {
                this.diente17 = d;
            } else if (d.getPosicion() == 18) {
                this.diente18 = d;
            } else if (d.getPosicion() == 21) {
                this.diente21 = d;
            } else if (d.getPosicion() == 22) {
                this.diente22 = d;
            } else if (d.getPosicion() == 23) {
                this.diente23 = d;
            } else if (d.getPosicion() == 24) {
                this.diente24 = d;
            } else if (d.getPosicion() == 25) {
                this.diente25 = d;
            } else if (d.getPosicion() == 26) {
                this.diente26 = d;
            } else if (d.getPosicion() == 27) {
                this.diente27 = d;
            } else if (d.getPosicion() == 28) {
                this.diente28 = d;
            } else if (d.getPosicion() == 31) {
                this.diente31 = d;
            } else if (d.getPosicion() == 32) {
                this.diente32 = d;
            } else if (d.getPosicion() == 33) {
                this.diente33 = d;
            } else if (d.getPosicion() == 34) {
                this.diente34 = d;
            } else if (d.getPosicion() == 35) {
                this.diente35 = d;
            } else if (d.getPosicion() == 36) {
                this.diente36 = d;
            } else if (d.getPosicion() == 37) {
                this.diente37 = d;
            } else if (d.getPosicion() == 38) {
                this.diente38 = d;
            } else if (d.getPosicion() == 41) {
                this.diente41 = d;
            } else if (d.getPosicion() == 42) {
                this.diente42 = d;
            } else if (d.getPosicion() == 43) {
                this.diente43 = d;
            } else if (d.getPosicion() == 44) {
                this.diente44 = d;
            } else if (d.getPosicion() == 45) {
                this.diente45 = d;
            } else if (d.getPosicion() == 46) {
                this.diente46 = d;
            } else if (d.getPosicion() == 47) {
                this.diente47 = d;
            } else if (d.getPosicion() == 48) {
                this.diente48 = d;
            }
        //}
    }

//    public void persistirOdontograma() {
//        if (!getInstance().isPersistent()) {
//            create(getInstance());
//            //getInstance().setDientes(crearDientes());
//            //actualizarDientes(getInstance().getDientes());
//        }
//    }
    public List<Diente> crearDientes() {
        List<Diente> listaNuevosDientes = new ArrayList<Diente>();

        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 8; j++) {
                String p = "" + i + j;
                if (j == 1 || j == 2) {
                    listaNuevosDientes.add(new Diente("Incisivo", Integer.parseInt(p), i));
                }
                if (j == 3) {
                    listaNuevosDientes.add(new Diente("Canino", Integer.parseInt(p), i));
                }
                if (j == 4 || j == 5) {
                    listaNuevosDientes.add(new Diente("Premolar", Integer.parseInt(p), i));
                }
                if (j == 6 || j == 7 || j == 8) {
                    listaNuevosDientes.add(new Diente("Molar", Integer.parseInt(p), i));
                }
            }
        }
        return listaNuevosDientes;
    }

    public List<Tratamiento> getMostrarTratamientos(Diente d) {
        return d.getTratamientos();
    }

    public String getNombreTratamiento() {
        return nombreTratamiento;
    }

    public void setNombreTratamiento(String nombreTratamiento) {
        this.nombreTratamiento = nombreTratamiento;
    }

    public void mostrarPanel(int n) {
        tratamiento = new Tratamiento();
        if (n == 1) {
            setPanel1(true);
            setPanel2(false);
            setPanel3(false);
        } else if (n == 2) {
            setPanel1(false);
            setPanel2(true);
            setPanel3(false);
        } else {
            setPanel1(false);
            setPanel2(false);
            setPanel3(true);
        }
    }

    public void agregarCuadrante(Tratamiento t) {
        System.out.println(" agregar cuadrantes tratamiento: _________________ " + c1 + " " + c2 + " " + c3 + " " + c4 + " " + c5);
        if (c1) {
//            t.getCuadrante().add("c1");
        }
        if (c2) {
  //          t.getCuadrante().add("c2");
        }
        if (c3) {
    //        t.getCuadrante().add("c3");
        }
        if (c4) {
      //      t.getCuadrante().add("c4");
        }
        if (c5) {
        //    t.getCuadrante().add("c5");
        }

        c1 = c2 = c3 = c4 = c5 = false;
    }

    public void cargarDientesSeleccionados() {
        listaDientes = new ArrayList<Diente>();
        System.out.println("Dientes seleccionados ___________________________");
        if (diente11.isSelect()) {
            listaDientes.add(diente11);
        }
        if (diente12.isSelect()) {
            listaDientes.add(diente12);
        }
        if (diente13.isSelect()) {
            listaDientes.add(diente13);
        }
        if (diente14.isSelect()) {
            listaDientes.add(diente14);
        }
        if (diente15.isSelect()) {
            listaDientes.add(diente15);
        }
        if (diente16.isSelect()) {
            listaDientes.add(diente16);
        }
        if (diente17.isSelect()) {
            listaDientes.add(diente17);
        }
        if (diente18.isSelect()) {
            listaDientes.add(diente18);
        }
        if (diente21.isSelect()) {
            listaDientes.add(diente21);
        }
        if (diente22.isSelect()) {
            listaDientes.add(diente22);
        }
        if (diente23.isSelect()) {
            listaDientes.add(diente23);
        }
        if (diente24.isSelect()) {
            listaDientes.add(diente24);
        }
        if (diente25.isSelect()) {
            listaDientes.add(diente25);
        }
        if (diente26.isSelect()) {
            listaDientes.add(diente26);
        }
        if (diente27.isSelect()) {
            listaDientes.add(diente27);
        }
        if (diente28.isSelect()) {
            listaDientes.add(diente28);
        }
        if (diente31.isSelect()) {
            listaDientes.add(diente31);
        }
        if (diente32.isSelect()) {
            listaDientes.add(diente32);
        }
        if (diente33.isSelect()) {
            listaDientes.add(diente33);
        }
        if (diente34.isSelect()) {
            listaDientes.add(diente34);
        }
        if (diente35.isSelect()) {
            listaDientes.add(diente35);
        }
        if (diente36.isSelect()) {
            listaDientes.add(diente36);
        }
        if (diente37.isSelect()) {
            listaDientes.add(diente37);
        }
        if (diente38.isSelect()) {
            listaDientes.add(diente38);
        }
        if (diente41.isSelect()) {
            listaDientes.add(diente41);
        }
        if (diente42.isSelect()) {
            listaDientes.add(diente42);
        }
        if (diente43.isSelect()) {
            listaDientes.add(diente43);
        }
        if (diente44.isSelect()) {
            listaDientes.add(diente44);
        }
        if (diente45.isSelect()) {
            listaDientes.add(diente45);
        }
        if (diente46.isSelect()) {
            listaDientes.add(diente46);
        }
        if (diente47.isSelect()) {
            listaDientes.add(diente47);
        }
        if (diente48.isSelect()) {
            listaDientes.add(diente48);
        }
    }

    public Diente extraerDiente(int posicion) {
        System.out.println("extraer diente: __________");
        Diente daux = null;
        try {
            List<Diente> dientes = getInstance().getDientes();
            for (Diente dient : dientes) {  //cuausa problemas
                if (dient.getPosicion() == posicion) {
                    daux = dient;
                }
            }
        } catch (Exception e) {
            System.out.println("Error extraer diente");
            e.printStackTrace();
        }
        return daux;
    }

    public boolean nombreTratamientoDisponible(final Long dienteId, final String nombreTratamiento) {
        System.out.println("Nombre tratamiento ");
        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("dienteId", dienteId);
        _values.put("nombreTratamiento", nombreTratamiento);
        Tratamiento t = (Tratamiento) crudService.findWithNamedQuery("Tratamiento.buscarPorDienteYNombre", _values, 1);

        System.out.println("Nombre tratamiento " + t);
        return t.isPersistent();
    }
}
