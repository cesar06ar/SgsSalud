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


import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.model.medicina.SignosVitales_;
import edu.sgssalud.model.odontologia.*;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.util.PersistenceUtil;
import java.io.Serializable;
import java.util.Collections;
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
public class ConsultaOdontologicaServicio extends PersistenceUtil<ConsultaOdontologica> implements Serializable{
     private static final long serialVersionUID = 234L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ConsultaOdontologicaServicio.class);
    @Inject
    private BussinesEntityService bussinesEntityService;

    public ConsultaOdontologicaServicio() {
        super(ConsultaOdontologica.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
        bussinesEntityService.setEntityManager(em);
    }

    public List<ConsultaOdontologica> getConsultasOdontologica(final int limit, final int offset) {
        return findAll(ConsultaOdontologica.class);
    }

    public List<ConsultaOdontologica> TodasConsulasOdontologica() {
        List list = this.findAll(ConsultaOdontologica.class);
        return list;
    }

    public ConsultaOdontologica getPorId(final Long id) {
        return (ConsultaOdontologica) findById(ConsultaOdontologica.class, id);
    }

    public ConsultaOdontologica buscarPorFechaConsulta(final Date fechaConsulta) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ConsultaOdontologica> query = builder.createQuery(ConsultaOdontologica.class);
        Root<ConsultaOdontologica> entity = query.from(ConsultaOdontologica.class);
        query.where(builder.equal(entity.get(ConsultaOdontologica_.fechaConsulta), fechaConsulta));
        return getSingleResult(query);
    }

    public List<ConsultaOdontologica> buscarPorFichaOdontologica(FichaOdontologica fichaOdont) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ConsultaOdontologica> query = builder.createQuery(ConsultaOdontologica.class);
        Root<ConsultaOdontologica> entity = query.from(ConsultaOdontologica.class);
        query.where(builder.equal(entity.get(ConsultaOdontologica_.fichaOdontologica), fichaOdont));
        List<ConsultaOdontologica> temp = getResultList(query);
        Collections.sort(temp);
        return temp;
    }

    public ConsultaOdontologica getPorSignosVitales(final SignosVitales signosVitales) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ConsultaOdontologica> query = builder.createQuery(ConsultaOdontologica.class);
        Root<ConsultaOdontologica> entity = query.from(ConsultaOdontologica.class);
        query.where(builder.equal(entity.get(ConsultaOdontologica_.signosVitales), signosVitales));
        return getSingleResult(query);
    }
    
    public SignosVitales getSignosVitalesPorIdConsultaOdontologica(final ConsultaOdontologica consultaMed){
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<SignosVitales> query = builder.createQuery(SignosVitales.class);
        Root<SignosVitales> entity = query.from(SignosVitales.class);
        query.where(builder.equal(entity.get(SignosVitales_.id), consultaMed.getSignosVitales().getId()));
        return getSingleResult(query);
    }

}
