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

package edu.sgssalud.service.farmacia;

import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.model.farmacia.Receta_Medicamento;
import edu.sgssalud.model.farmacia.Receta_Medicamento_;
import edu.sgssalud.model.medicina.ConsultaMedica_;
import edu.sgssalud.model.medicina.EnfermedadCIE10;
import edu.sgssalud.util.PersistenceUtil;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author cesar
 */
public class RecetaMedicamentoService extends PersistenceUtil<Receta_Medicamento>{
    
    public RecetaMedicamentoService(){
        super(Receta_Medicamento.class);
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    public List<Receta_Medicamento> todasReceta_Medicamento() {
        List list = this.findAll(Receta_Medicamento.class);
        return list;
    }

    public Receta_Medicamento getReceta_MedicamentoPorId(final Long id) {
        return (Receta_Medicamento) findById(Receta_Medicamento.class, id);
    }
    
    public List<Receta_Medicamento> obtenerPorReceta(final Receta receta) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Receta_Medicamento> query = builder.createQuery(Receta_Medicamento.class);
        Root<Receta_Medicamento> entity = query.from(Receta_Medicamento.class);
        query.where(builder.equal(entity.get(Receta_Medicamento_.receta), receta));
        return getResultList(query);
    }
    
     public List<Receta_Medicamento> obtenerPorMedicamento(final Medicamento medicamento) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Receta_Medicamento> query = builder.createQuery(Receta_Medicamento.class);
        Root<Receta_Medicamento> entity = query.from(Receta_Medicamento.class);
        query.where(builder.equal(entity.get(Receta_Medicamento_.medicamento), medicamento));
        return getResultList(query);
    }
     
    public List<Receta_Medicamento> obtenerPorMedicamentoYFechas(final Date fechaI, final Date fechaF, final Medicamento medicamento) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Receta_Medicamento> query = builder.createQuery(Receta_Medicamento.class);
        Root<Receta_Medicamento> entity = query.from(Receta_Medicamento.class);
        query.where(builder.equal(entity.get(Receta_Medicamento_.medicamento), medicamento), builder.between(entity.get(Receta_Medicamento_.fecha), fechaI, fechaF));
        return getResultList(query);
    } 
}
