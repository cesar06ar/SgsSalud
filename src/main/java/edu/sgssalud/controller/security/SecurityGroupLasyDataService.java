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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import edu.sgssalud.cdi.Web;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.IdentitySearchCriteriaImpl;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author cesar
 */
public class SecurityGroupLasyDataService extends ListDataModel<Group> implements SelectableDataModel<Group> {

    private static final long serialVersionUID = 7632987414391869389L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(SecurityGroupHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private SecurityGroupService securityGroupService;
    @Inject
    private IdentitySession security;
    private List<Group> resultList;

    public SecurityGroupLasyDataService() {
        resultList = new ArrayList<Group>();
    }

    public SecurityGroupLasyDataService(EntityManager em, List<Group> list) {
        super(list);
        this.em = em;
        this.resultList = list;
    }

    public List<Group> getResultList() {        
        return resultList;
    }

    public void setResultList(List<Group> resultList) {
        this.resultList = resultList;
    }

//    public List<Group> assignGroups(List<Group> g, EntityManager em) {
//        if (g.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
//            this.em = em;            
//            g = securityGroupService.getGroups();
//            log.info("eqaula --> assignGroups " + g);
//        }
//        return g;
//    }

    @Override
    public Object getRowKey(Group entity) {
        return entity.getName();
    }

    @Override
    public Group getRowData(String rowKey) {

        for (Group group : getResultList()) {
            if (group.getName().equals(rowKey)) {
                return group;
            }
        }
        return null;
    }

    @PostConstruct
    public void init() {
        log.info("Setup entityManager into GroupService...");
        securityGroupService.setEntityManager(em);
    }
}
