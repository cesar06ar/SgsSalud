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

import edu.sgssalud.model.medicina.EnfermedadCIE10;
import edu.sgssalud.model.medicina.EnfermedadCIE10_;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.util.PersistenceUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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
 * @author cesar
 */
public class EnfermedadesCie10Servicio extends PersistenceUtil<EnfermedadCIE10> implements Serializable {

    public EnfermedadesCie10Servicio(){
        super(EnfermedadCIE10.class);
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    public List<EnfermedadCIE10> getEnfermedadesCIE10() {
        List list = this.findAll(EnfermedadCIE10.class);
        return list;
    }

    public EnfermedadCIE10 getEnfermedadCIE10PorId(final Long id) {
        return (EnfermedadCIE10) findById(EnfermedadCIE10.class, id);
    }
    
    public EnfermedadCIE10 getEnfermedadCIE10PorCodigo(final String codigo) throws NoResultException {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<EnfermedadCIE10> query = builder.createQuery(EnfermedadCIE10.class);
        Root<EnfermedadCIE10> entity = query.from(EnfermedadCIE10.class);
        query.where(builder.equal(entity.get(EnfermedadCIE10_.code), codigo));
        return getSingleResult(query);
    }
    
    public boolean isCodigoDisponible(String codigo) {
        try {
            getEnfermedadCIE10PorCodigo(codigo);
            return false;
        } catch (NoResultException e) {
            return true;
        }
    }
    
    public List<EnfermedadCIE10> buscarPorHistoriaClinica(HistoriaClinica hc){
        String consulta = "from EnfermedadesCIE10 e, enfCIE10_HC enf_hc where enf_hc.historiaclin_id = :id";        
        TypedQuery<EnfermedadCIE10> query = em.createNamedQuery(consulta, EnfermedadCIE10.class);
        query.setParameter(":id", hc.getId());
        return query.getResultList();
    }
    
    public List<EnfermedadCIE10> getEnfermedadesSinHistoriaClinica(List<EnfermedadCIE10> enfPosee){
        List<EnfermedadCIE10> todas = getEnfermedadesCIE10();
        List<EnfermedadCIE10> auxList = new ArrayList<EnfermedadCIE10>();
        for (EnfermedadCIE10 enf : todas) {
            if(enfPosee.contains(enf)){
                auxList.add(enf);
            }
        }
        todas.removeAll(auxList);
        return todas;
    }
    
}
