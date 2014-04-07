/*
 * Copyright 2014 cesar.
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
import edu.sgssalud.model.servicios.Turno;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.medicina.TurnoService;
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
import org.jboss.seam.transaction.Transactional;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class TurnoHome extends BussinesEntityHome<Turno> implements Serializable {

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private TurnoService turnoS;

    @Inject
    private Identity identity;
    @Inject
    private PacienteServicio pacienteS;   
    private Long pacienteId;    
    private Paciente paciente;

    private List<Turno> listaTurnos = new ArrayList<Turno>();

    public Long getTurnoId() {
        return (Long) getId();
    }

    public void setTurnoId(Long turnoId) {
        setId(turnoId);
    }

    @TransactionAttribute   //
    public Turno load() {
        if (isIdDefined()) {
            wire();
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
        /*el bussinesEntityService.setEntityManager(em) solo va si la Entidad en este caso (ConsultaMedia)
         *hereda de la Entidad BussinesEntity...  caso contrario no se lo agrega
         */
        //bussinesEntityService.setEntityManager(em);
        turnoS.setEntityManager(em);
    }

    @Override
    protected Turno createInstance() {
        //prellenado estable para cualquier clase 

        Date now = Calendar.getInstance().getTime();
        Turno turno = new Turno();
        return turno;
    }

    @Override
    public Class<Turno> getEntityClass() {
        return Turno.class;
    }

    @TransactionAttribute
    public String guardar() {
        Date now = Calendar.getInstance().getTime();
        //getInstance().setLastUpdate(now);
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
//        log.info("sgssalud --> ingreso a eliminar: " + getInstance().getId());
        try {
            if (getInstance() == null) {
                throw new NullPointerException("Servicio is null");
            }
            if (getInstance().isPersistent()) {
                delete(getInstance());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  " + getInstance().getId(), ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "¡No existe una entidad para ser borrada!", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", e.toString()));
        }
        return "/pages/serviciosMedicos/lista.xhtml?faces-redirect=true";
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
        if(pacienteId != null){
            this.setPaciente(pacienteS.getPacientePorId(pacienteId));
        } 
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }  
    
    public List<Turno> getListaTurnos() {
        //return listaTurnos;
        return turnoS.getTurnos();
    }

    public void setListaTurnos(List<Turno> listaTurnos) {
        this.listaTurnos = listaTurnos;
    }
}
