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
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.Diente;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.odontologia.Odontograma;
import edu.sgssalud.model.odontologia.Tratamiento;
import edu.sgssalud.model.servicios.Servicio;
import edu.sgssalud.service.ServiciosMedicosService;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.odontologia.ConsultaOdontologicaServicio;
import edu.sgssalud.util.UI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class OdontogramaHome extends BussinesEntityHome<Odontograma> implements Serializable {
    
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(OdontogramaHome.class);
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
    private Long fichaMedicaId;
    private Long consultaOdontId;
    private int tipo;  //se refiere al otontograma 0 es el inicial 1 es el evolutivo
    private String nombreOdont;
    private String nombreDiente;
    private String nombreTratamiento;
    private String ruta ;
    private boolean panel1 = false;
    private boolean panel2 = false;
    private boolean panel3 = false;
    private Diente diente11;
    private Diente diente12;
    private Diente diente13;
    private Diente diente14;
    private Diente diente15;
    private Diente diente16;
    private Diente diente17;
    private Diente diente18;
    private Diente diente21;
    private Diente diente22;
    private Diente diente23;
    private Diente diente24;
    private Diente diente25;
    private Diente diente26;
    private Diente diente27;
    private Diente diente28;
    private Diente diente31;
    private Diente diente32;
    private Diente diente33;
    private Diente diente34;
    private Diente diente35;
    private Diente diente36;
    private Diente diente37;
    private Diente diente38;
    private Diente diente41;
    private Diente diente42;
    private Diente diente43;
    private Diente diente44;
    private Diente diente45;
    private Diente diente46;
    private Diente diente47;
    private Diente diente48;
    private Diente selectDient;
    private FichaOdontologica fichaOdont;
    private ConsultaOdontologica consultaOdont;
    private Servicio servicio;
    private Tratamiento tratamiento;
    private List<Servicio> listaServicios = new ArrayList<Servicio>();
    private List<Tratamiento> listaTratamient = new ArrayList<Tratamiento>();
    
    public Long getOdontogramaId() {
        return (Long) getId();
    }
    
    public void setOdontogramaId(Long odontogramaId) {
        setId(odontogramaId);
    }
    
    @TransactionAttribute
    public Odontograma load() {
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
        fichaMedServ.setEntityManager(em);
        fichaOdontServ.setEntityManager(em);
        consultaOdontServ.setEntityManager(em);
        bussinesEntityService.setEntityManager(em);
        serviciosMedicosS.setEntityManager(em);
        selectDient = new Diente();
        tratamiento = new Tratamiento();
        
        if (!getInstance().isPersistent()) {
            getInstance().setDientes(crearDientes());
        }
        
        this.actualizarDientes(getInstance().getDientes());
        
    }
    
    @Override
    protected Odontograma createInstance() {
        //prellenado estable para cualquier clase 

        Date now = Calendar.getInstance().getTime();
        Odontograma odontograma = new Odontograma();

        //fichaMedic.setResponsable(null);    //cambiar atributo a         
        return odontograma;
    }
    
    @Override
    public Class<Odontograma> getEntityClass() {
        return Odontograma.class;
    }
    
    @TransactionAttribute
    public String guardar() {
        Date now = Calendar.getInstance().getTime();
        try {
            if (getInstance().isPersistent()) {
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo Ficha Medica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                create(getInstance());
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se creo nueva Ficha Medica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return null;
    }
    
    @Transactional
    public String borrarEntidad() {
        log.info("sgssalud --> ingreso a eliminar: " + getInstance().getId());
        return null;
    }
    
    @TransactionAttribute
    public void guardarTratamiento() {
        log.info("valor diente: " + selectDient);
        Date now = Calendar.getInstance().getTime();        
        
//        try {
//          save(selectDient);                        
            //tratamiento.setServicioDisponible(servicio);
            tratamiento.setFechaRelizacion(now);
            tratamiento.setDiente(selectDient);
            tratamiento.setConsultaOdontologica(consultaOdont);
            tratamiento.setNombre(nombreTratamiento);
            selectDient.agregarTratamiento(tratamiento);
            if(getInstance().isPersistent()){
                getInstance().agregarDiente(selectDient);
                create(selectDient);
                create(tratamiento);
            }else{                
                persistirOdontograma();
                getInstance().agregarDiente(selectDient);
                save(getInstance());
                create(selectDient);
                create(tratamiento);                
            }            
            save(selectDient);            
            save(tratamiento);            
            getListaTratamient().add(tratamiento);
            
            this.actualizarDiente(selectDient);
            log.info("guardo con exito ");
//        } catch (Exception e) {
//            log.info("Error guardar");
//        }
        
        tratamiento = new Tratamiento();
        selectDient = new Diente();
    }
    
    public Long getFichaMedicaId() {
        return fichaMedicaId;
    }
    
    public void setFichaMedicaId(Long fichaMedicaId) {
        this.fichaMedicaId = fichaMedicaId;
        if (fichaMedicaId != null) {
            FichaMedica f = fichaMedServ.getFichaMedicaPorId(fichaMedicaId);
            this.setFichaOdont(fichaOdontServ.getFichaOdontologicaPorFichaMedica(f));
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
        this.setNombreDiente(this.selectDient.getNombre() + this.selectDient.getPosicion());
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
    
    public List<Servicio> getListaServicios() {
        return serviciosMedicosS.todosServicios("ODONTOLOGIA");
    }
    
    public void setListaServicios(List<Servicio> listaServicios) {
        this.listaServicios = listaServicios;
    }
    
    public List<Tratamiento> getListaTratamient() {
        return listaTratamient;
    }
    
    public void setListaTratamient(List<Tratamiento> listaTratamient) {
        this.listaTratamient = listaTratamient;
    }
    
    public String getNombreOdont() {
        //return (selectDient.getNombre() + " " + selectDient.getPosicion());
        return nombreOdont;
    }
    
    public void setNombreOdont(String nombreOdont) {
        this.nombreOdont = nombreOdont;
    }
    
    public String getNombreDiente() {
        return nombreDiente;
    }
    
    public void setNombreDiente(String nombreDiente) {
        this.nombreDiente = nombreDiente;
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
        
    public void actualizarDientes(List<Diente> dientes) {
        for (Diente d : dientes) {            
            actualizarDiente(d);
        }
    }      
        
    public void actualizarDiente(Diente d) {
        //log.info("actualizar diente: ");
        if (d.getPosicion() == 11) {
            setDiente11(d);
        } else if (d.getPosicion() == 12) {
            setDiente12(d);
        } else if (d.getPosicion() == 13) {
            setDiente13(d);
        } else if (d.getPosicion() == 14) {
            setDiente14(d);
        } else if (d.getPosicion() == 15) {
            setDiente15(d);
        } else if (d.getPosicion() == 16) {
            setDiente16(d);
        } else if (d.getPosicion() == 17) {
            setDiente17(d);
        } else if (d.getPosicion() == 18) {
            setDiente18(d);
        } else if (d.getPosicion() == 21) {
            setDiente21(d);
        } else if (d.getPosicion() == 22) {
            setDiente22(d);
        } else if (d.getPosicion() == 23) {
            setDiente23(d);
        } else if (d.getPosicion() == 24) {
            setDiente24(d);
        } else if (d.getPosicion() == 25) {
            setDiente25(d);
        } else if (d.getPosicion() == 26) {
            setDiente26(d);
        } else if (d.getPosicion() == 27) {
            setDiente27(d);
        } else if (d.getPosicion() == 28) {
            setDiente28(d);
        } else if (d.getPosicion() == 31) {
            setDiente31(d);
        } else if (d.getPosicion() == 32) {
            setDiente32(d);
        } else if (d.getPosicion() == 33) {
            setDiente33(d);
        } else if (d.getPosicion() == 34) {
            setDiente34(d);
        } else if (d.getPosicion() == 35) {
            setDiente35(d);
        } else if (d.getPosicion() == 36) {
            setDiente36(d);
        } else if (d.getPosicion() == 37) {
            setDiente37(d);
        } else if (d.getPosicion() == 38) {
            setDiente38(d);
        } else if (d.getPosicion() == 41) {
            setDiente41(d);
        } else if (d.getPosicion() == 42) {
            setDiente42(d);
        } else if (d.getPosicion() == 43) {
            setDiente43(d);
        } else if (d.getPosicion() == 44) {
            setDiente44(d);
        } else if (d.getPosicion() == 45) {
            setDiente45(d);
        } else if (d.getPosicion() == 46) {
            setDiente46(d);
        } else if (d.getPosicion() == 47) {
            setDiente47(d);
        } else if (d.getPosicion() == 48) {
            setDiente48(d);
        }
    }
    
    public void persistirOdontograma() {
        if (!getInstance().isPersistent()) {            
            create(getInstance());
            //getInstance().setDientes(crearDientes());
            //actualizarDientes(getInstance().getDientes());
        }
    }
    
    public List<Diente> crearDientes() {
        List<Diente> listaDientes = new ArrayList<Diente>();
        
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 8; j++) {
                String p = "" + i + j;
                if (j == 1 || j == 2) {
                    listaDientes.add(new Diente("Incisivo", Integer.parseInt(p), i, null));
                }
                if (j == 3) {
                    listaDientes.add(new Diente("Canino", Integer.parseInt(p), i, null));
                }
                if (j == 4 || j == 5) {
                    listaDientes.add(new Diente("Premolar", Integer.parseInt(p), i, null));
                }
                if (j == 6 || j == 7 || j == 8) {
                    listaDientes.add(new Diente("Molar", Integer.parseInt(p), i, null));
                }
            }
        }
        return listaDientes;
    }
    
    public String getNombreTratamiento() {
        return nombreTratamiento;
    }

    public void setNombreTratamiento(String nombreTratamiento) {
        this.nombreTratamiento = nombreTratamiento;
    }  
        
    //metodo para cargar dientes de un odontograma creado
    //metodos de seleccion de primefaces     
//    public void onRowSelect(SelectEvent event) {
//        FacesMessage msg = new FacesMessage(UI.getMessages("Tratamiento") + " " + UI.getMessages("common.selected"), ((Tratamiento) event.getObject()).getNombre());
//        FacesContext.getCurrentInstance().addMessage("", msg);
//    }
//
//    public void onRowUnselect(UnselectEvent event) {
//        FacesMessage msg = new FacesMessage(UI.getMessages("Tratamiento") + " " + UI.getMessages("common.unselected"), ((Tratamiento) event.getObject()).getNombre());
//        FacesContext.getCurrentInstance().addMessage("", msg);
//        this.setTratamiento(null);
//    }    
}
