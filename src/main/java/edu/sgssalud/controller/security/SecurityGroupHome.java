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
package edu.sgssalud.controller.security;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import edu.sgssalud.cdi.Web;
import org.jboss.seam.security.Identity;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.model.SimpleGroup;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class SecurityGroupHome implements Serializable {

    private static final long serialVersionUID = 7632987414391869389L;
    private static final org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(SecurityGroupHome.class);
    @Inject
    @Web
    private EntityManager entityManager;
    @Inject
    private Identity identity;
    @Inject
    private IdentitySession security;
    private Group instance;
    @Inject
    private SecurityGroupService securityGroupService;
    private String groupKey;
    private String groupName;

    @PostConstruct
    public void init() {
        securityGroupService.setSecurity(security);
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getGroupName() {
        if (isManaged()) {
            setGroupName(getInstance().getName());
        }
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setInstance(Group instance) {
        this.instance = instance;
    }

    public Group getInstance() {
        if (instance == null) {
            initInstance();
        }
        return instance;
    }

    @TransactionAttribute
    public String saveGroup() {
        log.info("Save instance for " + getInstance().getKey() + " with name " + getGroupName());
        if (isManaged()) {
            //implementar actualización de nombre de grupo, evaluar si es necesario
        } else {
            try {
                security.getPersistenceManager().createGroup(getGroupName(), "GROUP");
            } catch (IdentityException ex) {
                Logger.getLogger(SecurityGroupHome.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "/pages/admin/security/list?faces-redirect=true";
    }

    protected Group createInstance() {
        Group u = new SimpleGroup("New Group", "GROUP");                
        return u;
    }

    @TransactionAttribute
    public void load() {
        if (isIdDefined()) {
            wire();
        }
        log.info("eqaula --> Loaded instance " + getInstance());
    }
  
    @TransactionAttribute
    public void wire() {
        getInstance();
    }
  
    public boolean isPersistent() {
        return getInstance().getKey() != null;
    }

    public boolean isIdDefined() {
        return getGroupKey() != null && !"".equals(getGroupKey());
    }

    public Group find() throws IdentityException {
        if (securityGroupService.getSecurity() != null) {
            Group result = securityGroupService.findByKey(getGroupKey());
            if (result == null) {
                result = handleNotFound();
            }
            return result;
        } else {
            return null;
        }
    }

    public void clearInstance() {
        setInstance(null);
        setGroupKey(null);
    }

    protected void initInstance() {
        if (isIdDefined()) {
            if (true /*!isTransactionMarkedRollback()*/) {
                try {
                    //we cache the instance so that it does not "disappear"
                    //after remove() is called on the instance
                    //is this really a Good Idea??
                    setInstance(find());
                } catch (IdentityException ex) {
                    Logger.getLogger(SecurityGroupHome.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            setInstance(createInstance());
        }
    }

    private Group handleNotFound() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean isManaged() {
        return getInstance() != null
                && getGroupKey() != null;
    }
}
