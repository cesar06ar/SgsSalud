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
import edu.sgssalud.service.MedicamentoService;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 *
 * @author tania
 */
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
    
    @TransactionAttribute   //
    public Medicamento load() {
        if (isIdDefined()) {
            wire();
        }
        log.info("sgssalud --> cargar instance " + getInstance());
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
    }
}
