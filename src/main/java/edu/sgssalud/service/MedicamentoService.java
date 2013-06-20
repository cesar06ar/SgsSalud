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
package edu.sgssalud.service;

import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.paciente.Paciente_;
import edu.sgssalud.util.PersistenceUtil;
import edu.sgssalud.util.Strings;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author tania
 */
public class MedicamentoService extends PersistenceUtil<Medicamento> implements Serializable{
    
    private static final long serialVersionUID = 6569835981443699931L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(BussinesEntityService.class);

    public MedicamentoService() {
        super(Medicamento.class);
    }

   
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;        
    }
    
    //metodo count
    public long count() {
        return count(Medicamento.class);
    }

    

    public Medicamento buscarMedicamentosProId(final Long id) {
        return (Medicamento) findById(Medicamento.class, id);
    }
    /*public Medicamento buscarPorNombreMedicamento(final String name) {
        log.info("buscar medicamento por nombre " + name);
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Medicamento> query = builder.createQuery(Medicamento.class);
        Root<Medicamento> bussinesEntityType = query.from(Medicamento.class);
        query.where(builder.equal(bussinesEntityType.get(Medicamento_.name), name));
        return getSingleResult(query);    
    }*/
            
    /*@TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void crear(final Medicamento medicamento) {
        medicamento.setShowBootcamp(true);
        super.create(medicamento);
    }*/

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(final Medicamento medicamento) {
        super.save(medicamento);
        em.flush();
    }
    
    /*Metodo se utilizá en los Bean de las tablas de pacientes*/
    public long getMedicamentoCount() {
        return count(Medicamento.class);
    }
    
    /*Metodo se utilizá en los Bean de las tablas de pacientes*/
    public List<Medicamento> buscarMedicamentos(final int limit, final int offset) {
        return findAll(Medicamento.class);
    }
    /*Metodo se utilizá en los Bean de las tablas de pacientes*/
    public List<Medicamento> getMedicamentos() {
        List list = this.findAll(Medicamento.class);
        return list;
    }
    
    
    
     
   
    
    
}
