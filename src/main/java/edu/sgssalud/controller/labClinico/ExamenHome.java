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
package edu.sgssalud.controller.labClinico;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.model.paciente.Paciente;
import java.io.Serializable;
import java.util.Arrays;
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

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class ExamenHome extends BussinesEntityHome<ExamenLabClinico> implements Serializable{

    @Inject
    @Web
    private EntityManager em;

    public Long getExamenId() {
        return (Long) getId();
    }

    public void setExamenId(Long examenId) {
        setId(examenId);
    }

    @TransactionAttribute   //
    public ExamenLabClinico load() {
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
    }

    @Override
    protected ExamenLabClinico createInstance() {
        //prellenado estable para cualquier clase 
        //BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(ConsultaMedica.class.getName());
        Date now = Calendar.getInstance().getTime();
        ExamenLabClinico examenLab = new ExamenLabClinico();
        examenLab.setCreatedOn(now);
        examenLab.setLastUpdate(now);
        examenLab.setActivationTime(now);

        //fichaMedic.setResponsable(null);    //cambiar atributo a                 
        return examenLab;
    }

    @Override
    public Class<ExamenLabClinico> getEntityClass() {
        return ExamenLabClinico.class;
    }

    @TransactionAttribute
    public String guardar() {
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        try {
            if (getInstance().isPersistent()) {
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo el nombre del Examen: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                create(getInstance());
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se creo nueva Examen: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return "/pages/labClinico/listaExamenes.xhtml?faces-redirect=true";
    }

    @Transactional
    public String borrarEntidad() {
        //log.info("sgssalud --> ingreso a eliminar: " + getInstance().getId());
        try {
            if (getInstance() == null) {
                throw new NullPointerException("Servicio is null");
            }
            if (getInstance().isPersistent()) {
                delete(getInstance());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  " + getInstance().getName(), ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "¡No existe una entidad para ser borrada!", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", e.toString()));
        }
        return "/pages/serviciosMedicos/lista.xhtml?faces-redirect=true";
    }
    
    public List<ExamenLabClinico.Tipo> listaTiposExamen(){
        wire();
        List<ExamenLabClinico.Tipo> list = Arrays.asList(getInstance().getTipo().values());
        return list;
    }
}
