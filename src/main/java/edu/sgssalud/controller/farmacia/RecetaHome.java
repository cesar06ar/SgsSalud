/*
 * Copyright 2013 tania.
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
package edu.sgssalud.controller.farmacia;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.farmacia.Receta;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 *
 * @author tania
 */

@Named("recetaHome")
@ViewScoped
public class RecetaHome extends BussinesEntityHome<Receta> implements Serializable{
    
    private static final long serialVersionUID = 10L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(RecetaHome.class);
    
    /*Atributos importantes para acceso a la BD ==>*/
    @Inject
    @Web
    private EntityManager em;
    @Inject
    //private RecetaServicio rs;

    /*Métodos get y set para obtener el Id de la clase*/
    public Long getRecetaId() {
        return (Long) getRecetaId();
    }

    public void setRecetaId(Long recetaId) {
        setRecetaId(recetaId);
    }

    /*Método para  cargar una instancia receta==>*/
//    @Produces
//    //@Named("med")
//    @Current    
    @TransactionAttribute   //    
    public Receta load() {
        if (isIdDefined()) {
            wire();
        }
        log.info("sgssalud --> cargar instance " + getInstance());
        return getInstance();
    }

    /*Metodo que retorna una instancia de la clase (Receta) cuando ya esta creada==>*/
    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    /*Metodo importante para actualizar EntityManager y tener acceso a la DB==>*/
    @PostConstruct
    public void init() {
        setEntityManager(em);
        bussinesEntityService.setEntityManager(em);
    }

    @Override
    protected Receta createInstance() {
        //prellenado estable para cualquier clase 
        Date now = Calendar.getInstance().getTime();
        Receta receta = new Receta();
//        receta.setCreatedOn(now);
//        receta.setLastUpdate(now);
//        receta.setActivationTime(now);  
//        receta.setResponsable(null);    //cambiar atributo a 
//        receta.setFecha(now);  //Fecha actual de ingreso 
//        receta.buildAttributes(bussinesEntityService);  //
        return receta;
       
    }

    @Override
    public Class<Receta> getEntityClass() {
        return Receta.class;
    }

    @TransactionAttribute
    public String guardarReceta() {
        Date now = Calendar.getInstance().getTime();
//        getInstance().setLastUpdate(now);
        if (getInstance().isPersistent()) {
            save(getInstance());
        } else {
            create(getInstance());
            save(getInstance());
            FacesMessage msg = new FacesMessage("Se envió la receta: " + getInstance().getId() + " con éxito");
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return "/pages/farmacia/receta/lista.xhtml?faces-redirect=true";
    }

    public void validarFC() {
    }
    
    
}
