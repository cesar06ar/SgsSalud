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
package edu.sgssalud.service.labClinico;

import edu.sgssalud.model.PersistentObject_;
import edu.sgssalud.model.farmacia.Receta_Medicamento;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.model.labClinico.ExamenLabClinico_;
import edu.sgssalud.model.labClinico.Parametros;
import edu.sgssalud.model.labClinico.Parametros_;
import edu.sgssalud.model.odontologia.Diente;
import edu.sgssalud.model.odontologia.Diente_;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.util.PersistenceUtil;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author cesar
 */
public class ExamenLabService extends PersistenceUtil<ExamenLabClinico> {

    public ExamenLabService() {
        super(ExamenLabClinico.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public List<ExamenLabClinico> getExamenesLab() {
        List list = this.findAll(ExamenLabClinico.class);
        return list;
    }

    public ExamenLabClinico getExamenPorId(final Long id) {
        return (ExamenLabClinico) findById(ExamenLabClinico.class, id);
    }

    public List<Parametros> getParametrosPorExamen(final ExamenLabClinico exam) {
        TypedQuery<Parametros> query = em.createNamedQuery("Parametros.buscarTodosOrderanos", Parametros.class);              
        query.setParameter("idExamen", exam.getId());
        return query.getResultList();
    }
    
    public List<Parametros> getParametrosPorExamen1(final ExamenLabClinico exam) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Parametros> query = builder.createQuery(Parametros.class);
        Root<Parametros> entity = query.from(Parametros.class);
        query.where(builder.equal(entity.get(Parametros_.examenLabClinico), exam));        
        return getResultList(query);
    }

    public ExamenLabClinico getExamenPorCodigo(final String codigo) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ExamenLabClinico> query = builder.createQuery(ExamenLabClinico.class);
        Root<ExamenLabClinico> entity = query.from(ExamenLabClinico.class);
        query.where(builder.equal(entity.get(ExamenLabClinico_.code), codigo));
        return getSingleResult(query);
    }
    
    public boolean isCodigoDisponible(String codigo) {
        try {
            ExamenLabClinico examen = getExamenPorCodigo(codigo);
            if (examen != null) {
                return false;
            } else {
                return true;
            }
        } catch (NoResultException e) {
            return false;
        }
    }
    
    public void borrarEntidad(Object entidad){
        delete(entidad);
    }

}
