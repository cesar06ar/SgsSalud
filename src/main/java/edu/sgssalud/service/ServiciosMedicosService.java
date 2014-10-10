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
package edu.sgssalud.service;

//import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.service.medicina.*;
import edu.sgssalud.model.servicios.Servicio;
import edu.sgssalud.model.servicios.Servicio_;
import edu.sgssalud.util.PersistenceUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author cesar
 */
public class ServiciosMedicosService extends PersistenceUtil<Servicio> implements Serializable {

    private static final long serialVersionUID = 234L;
    //private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ConsultaMedicaServicio.class);

    public ServiciosMedicosService() {
        super(Servicio.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public List<Servicio> todosServicios(final int limit, final int offset) {
        return findAll(Servicio.class);
    }

    public List<Servicio> todosServicios() {
        return findAll(Servicio.class);
    }

    public Servicio getSignosVitalesPorId(final Long id) {
        return (Servicio) findById(Servicio.class, id);
    }

    public List<Servicio> todosServicios(String categoria) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Servicio> query = builder.createQuery(Servicio.class);
        Root<Servicio> entity = query.from(Servicio.class);
        query.where(builder.equal(entity.get(Servicio_.categoria), categoria));
        return getResultList(query);
    }

    public Servicio buscarPorNombre(final String nombre) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Servicio> query = builder.createQuery(Servicio.class);
        Root<Servicio> entity = query.from(Servicio.class);
        query.where(builder.equal(entity.get(Servicio_.name), nombre));
        return getSingleResult(query);
    }

    public List<Servicio> getServicioPorFechas(final Date fechaI, final Date fechaF) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Servicio> query = builder.createQuery(Servicio.class);
        Root<Servicio> entity = query.from(Servicio.class);
        query.where(builder.between(entity.get(Servicio_.createdOn), fechaI, fechaF));
        return getResultList(query);
    }

    public List<Servicio> getServicioPorFechas(final Date fechaI, final Date fechaF, String genero) {
        TypedQuery<Servicio> query = em.createNamedQuery("ServiciosEnfermeria.buscarPorRagnoFechasYsexo", Servicio.class);
        query.setParameter("fInicio", fechaI);
        query.setParameter("fFin", fechaF);
        query.setParameter("genero", genero);
        return query.getResultList();
    }

    public List<Servicio> getServicioPorFechas(final Date fechaI, final Date fechaF, String genero, String servicio) {
        TypedQuery<Servicio> query = em.createNamedQuery("ServiciosEnfermeria.buscarPorRagnoFechasYsexo", Servicio.class);
        query.setParameter("fInicio", fechaI);
        query.setParameter("fFin", fechaF);
        query.setParameter("servicio", servicio);
        query.setParameter("genero", genero);
        return query.getResultList();
    }
    
    public List<ConsultaMedica> getConsultaPor(final Date fechaI, final Date fechaF, String genero, Long responsableId) {
        TypedQuery<ConsultaMedica> query = em.createNamedQuery("ConsultaMedica.buscarSignosVitalesPorRagnoFechasYsexo", ConsultaMedica.class);
        query.setParameter("fInicio", fechaI);
        query.setParameter("fFin", fechaF);
        query.setParameter("responsableId", responsableId);
        query.setParameter("genero", genero);
        return query.getResultList();
    }

    public boolean borrarEntidad(Long servicioId) {
        try {
            String borrarServicio = "DELETE FROM servicio s WHERE s.id = " + servicioId;
            int deleted1 = em.createNativeQuery(borrarServicio).executeUpdate();
            return (deleted1 == 1);
        } catch (Exception e) {
            return false;
        }
    }

//    public List<Tratamiento> buscarPorServicio(Servicio serv) {
//        CriteriaBuilder builder = getCriteriaBuilder();
//        CriteriaQuery<Tratamiento> query = builder.createQuery(Tratamiento.class);
//        Root<Tratamiento> entity = query.from(Tratamiento.class);
//        query.where(builder.equal(entity.get(Tratamiento_.servicioDisponible), serv));
//        return getResultList(query);
//    }
}
