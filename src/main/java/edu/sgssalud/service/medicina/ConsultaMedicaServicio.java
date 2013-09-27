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
package edu.sgssalud.service.medicina;

import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.ConsultaMedica_;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.util.PersistenceUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author cesar
 */
public class ConsultaMedicaServicio extends PersistenceUtil<ConsultaMedica> implements Serializable {

    private static final long serialVersionUID = 234L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ConsultaMedicaServicio.class);
    @Inject
    private BussinesEntityService bussinesEntityService;

    public ConsultaMedicaServicio() {
        super(ConsultaMedica.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
        bussinesEntityService.setEntityManager(em);
    }

    public List<ConsultaMedica> getConsultasMedicas(final int limit, final int offset) {
        return findAll(ConsultaMedica.class);
    }

    public List<ConsultaMedica> getConsulasMedicas() {
        List list = this.findAll(ConsultaMedica.class);
        return list;
    }

    public ConsultaMedica getConsultaMedicaPorId(final Long id) {
        return (ConsultaMedica) findById(ConsultaMedica.class, id);
    }

    public ConsultaMedica getConsultaMedicaPorFechaConsulta(final Date fechaConsulta) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ConsultaMedica> query = builder.createQuery(ConsultaMedica.class);
        Root<ConsultaMedica> entity = query.from(ConsultaMedica.class);
        query.where(builder.equal(entity.get(ConsultaMedica_.fechaConsulta), fechaConsulta));
        return getSingleResult(query);
    }

    public List<ConsultaMedica> getConsultaMedicaPorHistoriaClinica(final HistoriaClinica historiaClinica) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ConsultaMedica> query = builder.createQuery(ConsultaMedica.class);
        Root<ConsultaMedica> entity = query.from(ConsultaMedica.class);
        query.where(builder.equal(entity.get(ConsultaMedica_.historiaClinica), historiaClinica));
        return getResultList(query);
    }

    public ConsultaMedica getPorSignosVitales(final SignosVitales signosVitales) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ConsultaMedica> query = builder.createQuery(ConsultaMedica.class);
        Root<ConsultaMedica> entity = query.from(ConsultaMedica.class);
        query.where(builder.equal(entity.get(ConsultaMedica_.signosVitales), signosVitales));
        return getSingleResult(query);
    }
}
