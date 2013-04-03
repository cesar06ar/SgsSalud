/*
 * Copyright 2013 jlgranda.
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
package edu.sgssalud.faces.converter;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.profile.Profile;
import org.omnifaces.converter.SelectItemsConverter;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.model.SimpleGroup;

/**
 *
 * @author jlgranda
 */

@RequestScoped
@FacesConverter("org.eqaula.glue.faces.converter.SimpleGroupConverter")
public class SimpleGroupConverter extends SelectItemsConverter {

    
    @Inject
    @Web
    private EntityManager em;
    
    @Inject
    private IdentitySession security;

    @PostConstruct
    public void setup() {

    }

    @PreDestroy
    public void shutdown() {

    }

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && !value.isEmpty() && security != null) {

            try {
                try {
                    return security.getPersistenceManager().findGroup(value, "GROUP");
                } catch (IdentityException ex) {
                    Logger.getLogger(SimpleGroupConverter.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (NoResultException e) {
                return new Profile();
            }

        }

        return null;
    }


    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String key = (value instanceof SimpleGroup) ? ((SimpleGroup) value).getName() : null;
        return (key != null) ? key : null;
    }

}
