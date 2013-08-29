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

import edu.sgssalud.cdi.Current;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.controller.paciente.PacienteHome;
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.farmacia.MedicamentoService;
import edu.sgssalud.util.Dates;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.inject.Produces;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.picketlink.idm.common.exception.IdentityException;

/**
 *
 * @author tania
 */
@Named("medicamentoHome")
@ViewScoped
public class MedicamentoHome extends BussinesEntityHome<Medicamento> implements Serializable{
    
    private static final long serialVersionUID = 7L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(MedicamentoHome.class);
    /*Atributos importantes para acceso a la BD ==>*/
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private MedicamentoService ms;
    
    /*Métodos get y set para obtener el Id de la clase*/
    public Long getMedicamentoId() {
        return (Long) getId();
    }

    public void setMedicamentoId(Long medicamentoId) {
        setId(medicamentoId);
    }
    
    /*Método para  cargar una instancia de medicamento==>*/
    
//    @Produces
//    //@Named("med")
//    @Current    
    @TransactionAttribute   //    
    public Medicamento load() {
        if (isIdDefined()) {
            wire();
        }
        log.info("sgssalud --> cargar instance " + getInstance());
        //getInstance().setFechaIngreso(new Date());
        return getInstance();
    }
    
   /*Metodo que retorna una instancia de la clase (Medicamento) cuando ya esta creada==>*/
    @TransactionAttribute
    public void wire() {
        getInstance();
    }
    
     /*Metodo importante para actualizar EntityManager y tener acceso a la DB==>*/
    @PostConstruct
    public void init() {
        setEntityManager(em);
        bussinesEntityService.setEntityManager(em);
        //getInstance().setFechaIngreso(new Date());
    }
    
    @Override
    protected Medicamento createInstance() {
        //prellenado estable para cualquier clase 
        Date now = Calendar.getInstance().getTime();
        Medicamento medicament = new Medicamento();
        medicament.setCreatedOn(now);
        medicament.setLastUpdate(now);
        medicament.setActivationTime(now);
        //med.setExpirationTime(Dates.addDays(now, 364));        
        medicament.setResponsable(null);    //cambiar atributo a 
        medicament.setFechaIngreso(now);
        medicament.buildAttributes(bussinesEntityService);  //
        return medicament;
    }
    
    @Override
    public Class<Medicamento> getEntityClass() {
        return Medicamento.class;
    }
    
//    @TransactionAttribute
//    public String guardarMedicamento() {
//        Date now = Calendar.getInstance().getTime();
//        getInstance().setLastUpdate(now);
//        if (getInstance().isPersistent()) {
//            save(getInstance());
//        } else {
//            try {
//                register();
//            } catch (IdentityException ex) {
//                Logger.getLogger(PacienteHome.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return null;
//    }
   public void validarFC(){
       
   }    
    
}
