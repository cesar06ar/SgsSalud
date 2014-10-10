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
package edu.sgssalud.service.farmacia;

import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.model.farmacia.Receta_;
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.util.Dates;
import edu.sgssalud.util.PersistenceUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
public class RecetaServicio extends PersistenceUtil<Receta> implements Serializable {

    private static final long serialVersionUID = 11L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(BussinesEntityService.class);

    public RecetaServicio() {
        super(Receta.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public List<Receta> getRecetas() {
        List list = this.findAll(Receta.class);
        return list;
    }

    public List<Receta> getRecetas(final int limit, final int offset) {
        return findAll(Receta.class);
    }

    public Receta buscarRecetaPorId(final Long id) {
        return (Receta) findById(Receta.class, id);
    }

    public List<Receta> obtenerRecetas(final int limite, final int offset) {
        return findAll(Receta.class);
    }

    public Receta buscarRecetaPorPaciente(final Paciente paciente) throws NoResultException {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Receta> query = builder.createQuery(Receta.class);
        Root<Receta> bussinesEntityType = query.from(Receta.class);
        query.where(builder.equal(bussinesEntityType.get(Receta_.paciente), paciente));
        return getSingleResult(query);
    }

    public Receta buscarRecetaPorFecha(final Date fecha) throws NoResultException {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Receta> query = builder.createQuery(Receta.class);
        Root<Receta> bussinesEntityType = query.from(Receta.class);
        query.where(builder.equal(bussinesEntityType.get(Receta_.fechaEmision), fecha));
        return getSingleResult(query);
    }

    public List<Receta> buscarRecetaPorFechas(final Date fechaInf, final Date fechaSup) throws NoResultException {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Receta> query = builder.createQuery(Receta.class);
        Root<Receta> objeto = query.from(Receta.class);
        query.where(builder.between(objeto.get(Receta_.fechaEntrega), fechaInf, fechaSup),
                builder.or(builder.between(objeto.get(Receta_.fechaEmision), fechaInf, fechaSup)));        
        return getResultList(query);
    }
    public List<Receta> buscarRecetaPorFechasEmision(final Date fechaInf, final Date fechaSup) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Receta> query = builder.createQuery(Receta.class);
        Root<Receta> objeto = query.from(Receta.class);
        query.where(builder.between(objeto.get(Receta_.fechaEmision), fechaInf, fechaSup));        
        return getResultList(query);
    }
    public List<Receta> buscarRecetaPorConsultaMedica(final ConsultaMedica consulMedica) throws NoResultException {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Receta> query = builder.createQuery(Receta.class);
        Root<Receta> bussinesEntityType = query.from(Receta.class);
        query.where(builder.equal(bussinesEntityType.get(Receta_.consultaMedica), consulMedica));
        return getResultList(query);

    }

    public List<Receta> buscarTodos() {
        try {
            return findAll(Receta.class);
        } catch (Exception e) {
            log.info("error no encontro nada " + e.getMessage());
            return null;
        }
    }

    public List<Receta> BuscarRecetasPorParametro(String parametro) {
        TypedQuery<Receta> query = null;
        if (Dates.getFormatoFecha(parametro) != null) {
            query = em.createNamedQuery("Receta.buscarPorFecha", Receta.class);
            query.setParameter("clave", Dates.getFormatoFecha(parametro));
        } else {
            query = em.createNamedQuery("Receta.buscarPorCriterio", Receta.class);
            query.setParameter("clave", parametro);
        }
//        else if{
//            query = em.createNamedQuery("Receta.buscarPorConsulta", Receta.class);
//            query.setParameter("clave",parametro);
//            
//        }
        return query.getResultList();
    }

    public void guardarReceta(Receta r) {
        if (r.isPersistent()) {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(r);
            getEntityManager().getTransaction().commit();
        }
        System.out.println("Actualizado correctamente:________________");
    }

    public Long getGenerarNumeroFicha() {
        List<Receta> listaF = getRecetas();
        Long num = (long) 001;
        if (!listaF.isEmpty()) {
            for (Receta rm : listaF) {
                if (rm.getNumero() >= num) {
                    num = (rm.getNumero() + 1);
                }
            }
        }
        return num;
    }

}
