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
package edu.sgssalud.service.medicina;

import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.servicios.Turno;
import edu.sgssalud.model.servicios.Turno_;
import edu.sgssalud.util.PersistenceUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author cesar
 */
public class TurnoService extends PersistenceUtil<Turno> implements Serializable {

    private static final long serialVersionUID = 234L;

    public TurnoService() {
        super(Turno.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public List<Turno> getTurnos(final int limit, final int offset) {
        return findAll(entityClass);
    }

    public List<Turno> getTurnos() {
        return findAll(Turno.class);
    }

    public Turno getSTurnoPorId(final Long id) {
        return (Turno) findById(Turno.class, id);
    }

    public List<Turno> getTurnosPor(final Date fecha) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Turno> query = builder.createQuery(Turno.class);
        Root<Turno> entity = query.from(Turno.class);
        query.where(builder.equal(entity.get(Turno_.fechaCita), fecha));
        return getResultList(query);
    }

    public List<Turno> getTurnosPor(final String estado) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Turno> query = builder.createQuery(Turno.class);
        Root<Turno> entity = query.from(Turno.class);
        query.where(builder.equal(entity.get(Turno_.estado), estado));
        return getResultList(query);
    }

    public List<Turno> getTurnosPor(final Date fecha, final String estado) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Turno> query = builder.createQuery(Turno.class);
        Root<Turno> entity = query.from(Turno.class);
        query.where(builder.equal(entity.get(Turno_.fechaCita), fecha), builder.or(builder.equal(entity.get(Turno_.estado), estado)));
        return getResultList(query);
    }

    public List<Turno> getTurnosPorFecha(final Paciente paciente) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Turno> query = builder.createQuery(Turno.class);
        Root<Turno> entity = query.from(Turno.class);
        query.where(builder.equal(entity.get(Turno_.paciente), paciente));
        return getResultList(query);
    }
}
