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

package edu.sgssalud.service.odontologia;

import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.Diente;
import edu.sgssalud.model.odontologia.Tratamiento;
import edu.sgssalud.model.odontologia.Tratamiento_;
import edu.sgssalud.model.servicios.Servicio;
import edu.sgssalud.util.PersistenceUtil;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author cesar
 */
public class TratamientoServicio extends PersistenceUtil<Tratamiento> implements Serializable{

    private static final long serialVersionUID = 1L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(TratamientoServicio.class);
    
    
    public TratamientoServicio() {
        super(Tratamiento.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
     public List<Tratamiento> getListaTratamientos(final int limit, final int offset) {
        return findAll(Tratamiento.class);
    }

    public List<Tratamiento> getListaTratamientos() {
        List list = this.findAll(Tratamiento.class);
        return list;
    }

    public Tratamiento getTratamientoPorId(final Long id) {
        return (Tratamiento) findById(Tratamiento.class, id);
    }

    public List<Tratamiento> buscarPorDiente(Diente diente) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Tratamiento> query = builder.createQuery(Tratamiento.class);
        Root<Tratamiento> entity = query.from(Tratamiento.class);
        query.where(builder.equal(entity.get(Tratamiento_.diente), diente));      
        return getResultList(query);
    }
    
    public List<Tratamiento> buscarPorConsultaOdontologica(ConsultaOdontologica consultaOdont) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Tratamiento> query = builder.createQuery(Tratamiento.class);
        Root<Tratamiento> entity = query.from(Tratamiento.class);
        query.where(builder.equal(entity.get(Tratamiento_.consultaOdontologica), consultaOdont));      
        return getResultList(query);
    }    
    
    public List<Tratamiento> buscarPorServicioOdontologico(Servicio servicioOdontDisponible) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Tratamiento> query = builder.createQuery(Tratamiento.class);
        Root<Tratamiento> entity = query.from(Tratamiento.class);
        query.where(builder.equal(entity.get(Tratamiento_.servicioDisponible), servicioOdontDisponible));      
        return getResultList(query);
    }

    
}
