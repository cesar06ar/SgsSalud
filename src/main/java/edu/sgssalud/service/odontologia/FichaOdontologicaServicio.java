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


import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.odontologia.FichaOdontologica_;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.util.PersistenceUtil;
import java.io.Serializable;
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
public class FichaOdontologicaServicio extends PersistenceUtil<FichaOdontologica> implements Serializable{
    private static final long serialVersionUID = 234L;
    @Inject
    private BussinesEntityService bussinesEntityService;

    public FichaOdontologicaServicio() {
        super(FichaOdontologica.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
        bussinesEntityService.setEntityManager(em);
    }

    public List<FichaOdontologica> getFichasOdontologicas(final int limit, final int offset) {
        return findAll(FichaOdontologica.class);
    }

    public List<FichaOdontologica> getFichasOdontologicas() {
        List list = this.findAll(FichaOdontologica.class);
        return list;
    }

    public FichaOdontologica getFichaOdontologicaPorId(final Long id) {
        return (FichaOdontologica) findById(FichaOdontologica.class, id);
    }

    public FichaOdontologica getFichaOdontologicaPorFichaMedica(FichaMedica fichaMedica) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<FichaOdontologica> query = builder.createQuery(FichaOdontologica.class);
        Root<FichaOdontologica> entity = query.from(FichaOdontologica.class);
        query.where(builder.equal(entity.get(FichaOdontologica_.fichaMedica), fichaMedica));      
        return getSingleResult(query);
    }

}
