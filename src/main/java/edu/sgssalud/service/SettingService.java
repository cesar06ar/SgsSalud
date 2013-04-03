/*
 * Copyright 2013 lucho.
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

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.model.config.Setting_;

import edu.sgssalud.util.PersistenceUtil;

/**
 *
 * @author lucho
 */
public class SettingService extends PersistenceUtil<Setting> {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(BussinesEntityService.class);
    private static final long serialVersionUID = -2654253198159918622L;

    public SettingService() {
        super(Setting.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public Setting getSettingById(final Long id) {
        return (Setting) findById(Setting.class, id);
    }

    public List<Setting> getSettings(final int limit, final int offset) {
        return findAll(Setting.class);
    }

    public Setting findByName(final String name) {
        log.info("find Profile with name " + name);
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Setting> query = builder.createQuery(Setting.class);
        Root<Setting> bussinesEntityType = query.from(Setting.class);
        query.where(builder.equal(bussinesEntityType.get(Setting_.name), name));
        return getSingleResult(query);
    }

    
     public Setting getSettingByName(final String name) throws NoResultException {
        TypedQuery<Setting> query = em.createQuery("SELECT p FROM Setting p WHERE p.name = :name", Setting.class);
        query.setParameter("name", name);
        Setting result = query.getSingleResult();
        return result;
    }
    
    public boolean isNameAvailable(String name) {
        try {
           Setting b = getSettingByName(name);
            if(b != null){                
                return false;
            }else{                
                return true;
            }
        } catch (NoResultException e) {
            return true;
        }
    }
}
