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

import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.medicina.HistoriaClinica_;
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
public class HistoriaClinicaServicio extends PersistenceUtil<HistoriaClinica> implements Serializable{
     private static final long serialVersionUID = 234L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(HistoriaClinicaServicio.class);
    @Inject
    private BussinesEntityService bussinesEntityService;

    
    public HistoriaClinicaServicio(){
        super(HistoriaClinica.class);
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
        bussinesEntityService.setEntityManager(em);
    }
    
     public List<HistoriaClinica> getHistoriasClinicas(final int limit, final int offset) {
        return findAll(HistoriaClinica.class);
    }

    public List<HistoriaClinica> getHistoriasClinicas() {
        List list = this.findAll(HistoriaClinica.class);
        return list;
    }

    public HistoriaClinica buscarPorId(final Long id) {
        return (HistoriaClinica) findById(HistoriaClinica.class, id);
    }
    
     public HistoriaClinica buscarPorFichaMedica(final FichaMedica fichaMedica){
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<HistoriaClinica> query = builder.createQuery(HistoriaClinica.class);
        Root<HistoriaClinica> entity = query.from(HistoriaClinica.class);
        query.where(builder.equal(entity.get(HistoriaClinica_.fichaMedica), fichaMedica));
        return getSingleResult(query);
    }
}
