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
package edu.sgssalud.controller.services;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.servicios.Servicio;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.ServiciosMedicosService;
import edu.sgssalud.service.paciente.PacienteServicio;
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
import org.jboss.seam.security.Identity;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class ServiciosMedicosHome extends BussinesEntityHome<Servicio> implements Serializable {


    @Inject
    @Web
    private EntityManager em;
    @Inject
    private ServiciosMedicosService servicioMedicoS;
    @Inject
    private Identity identity;
    @Inject
    private PacienteServicio pacienteS;
    @Inject
    private ProfileService profileS;

    private Long pacienteId;
    private String obs;
    private Paciente paciente;
    

    private List<String> nombres = new ArrayList<String>();

    public Long getServicioId() {
        return (Long) getId();
    }

    public void setServicioId(Long ServicioId) {
        setId(ServicioId);
        if (getInstance().isPersistent()) {
            this.setObs(getInstance().getDescription());
        }
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }    
//    public List<String> getRutaImagenes() {
//        List<String> rutas = new ArrayList<String>();
//        String ruta = "/resources/odontograma/";
//        File dir = new File("/home/tania/NetBeansProjects/SgsSalud/src/main/webapp/resources/odontograma/");
//        String[] imagenes = dir.list();
//        if (imagenes != null) {
//            for (String s : imagenes) {
//                rutas.add(ruta.concat(s));
//            }
//        }
//        return rutas;
//    }
    public List<String> getNombres() {
        return nombres;
    }

    public void setNombres(List<String> nombres) {
        this.nombres = nombres;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
//        System.out.println("INGRESO a fijar variable "+pacienteId);
        if(pacienteId != null){
            this.setPaciente(pacienteS.getPacientePorId(pacienteId));
        }        
    }

    @TransactionAttribute   //
    public Servicio load() {
        if (isIdDefined()) {
            wire();
        }
//        log.info("sgssalud --> cargar instance " + getInstance());
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
        servicioMedicoS.setEntityManager(em);
        pacienteS.setEntityManager(em);
        profileS.setEntityManager(em);
//        if (pacienteId == null) {
//            System.out.println("Hola mundo");
//            paciente = new Paciente();
//        }
//        nombres = new ArrayList<String>();
//        nombres.add("INYECCIÓN");
//        nombres.add("CURACIÓN");
    }

    @Override
    protected Servicio createInstance() {
        //prellenado estable para cualquier clase         
        Date now = Calendar.getInstance().getTime();
        Servicio servicioMedico = new Servicio();
        servicioMedico.setCategoria("ENFERMERIA");   //representa la categoria
        servicioMedico.setCreatedOn(now);
        servicioMedico.setLastUpdate(now);
//        if(pacienteId != null){
//            servicioMedico.setPaciente(pacienteS.getPacientePorId(this.pacienteId));            
//        }
        if (identity.isLoggedIn()) {
            servicioMedico.setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));    //cambiar atributo a 
        }
        return servicioMedico;
    }

    @Override
    public Class<Servicio> getEntityClass() {
        return Servicio.class;
    }

    @TransactionAttribute
    public String guardar() {
        System.out.println("ingreso a guardar");
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        getInstance().setDescription(this.getObs());
        try {
//            System.out.println("Paciente " + this.pacienteId);
            if (this.paciente != null) {
                if (getInstance().isPersistent()) {
                    
                    getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));    //cambiar atributo a 
                    save(getInstance());
                    FacesMessage msg = new FacesMessage("Se actualizo el Servicio Médico: " + getInstance().getName() + " con éxito");
                    FacesContext.getCurrentInstance().addMessage("", msg);
//                    System.out.println("actualiza 2");
                } else {
                    System.out.println("crear 2");
                    if (getInstance().getPaciente() == null) {
                        this.getInstance().setPaciente(paciente);
                    }
                    create(getInstance());
                    save(getInstance());
                    //delete(now);
                    
                    FacesMessage msg = new FacesMessage("Se creo nuevo Servicio Médico: " + getInstance().getName() + " con éxito");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                }
            } else {
                FacesMessage msg = new FacesMessage("debe cargar un paciente ");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage("Error al guardar: ");
            FacesContext.getCurrentInstance().addMessage("", msg);

        }
        return "/pages/depSalud/enfermeria/serviciosMedicos/lista.xhtml?faces-redirect=true";
    }

//    public String nombreImg(String ruta) {
//        String resultado = "";
//        String[] c = ruta.split("/");
//        for (String s : c) {
//            if (s.contains(".png") || s.contains(".jpg") || s.contains(".gif")) {
//                resultado = s;
//            }
//        }
//        return resultado;
//    }
}
