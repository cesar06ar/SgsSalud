/*
 * Copyright 2013 cesar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eqaula.glue.controller.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.eqaula.glue.cdi.Web;
import org.eqaula.glue.model.accounting.Account;
import org.eqaula.glue.model.accounting.Account_;
import org.eqaula.glue.util.QueryData;
import org.eqaula.glue.util.QuerySortOrder;
import org.eqaula.glue.util.UI;
import org.jboss.seam.security.management.picketlink.IdentitySessionProducer;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.api.UnsupportedCriterium;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.model.SimpleGroup;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author cesar
 */
@Named
@RequestScoped
public class SecurityGroupListService extends LazyDataModel<Group> {

    private static final long serialVersionUID = 4819808125494695197L;
    private static final int MAX_RESULTS = 5;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(SecurityGroupListService.class);
    @Inject
    @Web
    private EntityManager entityManager;
    @Inject
    private IdentitySessionFactory identitySessionFactory;
    @Inject
    private SecurityGroupService securityGroupService;
    private int firstResult = 0;
    private Group[] selectedGroups;
    private Group selectedGroup;
    private String groupName;

    public SecurityGroupListService() {
        setPageSize(MAX_RESULTS);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getNextFirstResult() {
        return firstResult + this.getPageSize();
    }

    public int getPreviousFirstResult() {
        return this.getPageSize() >= firstResult ? 0 : firstResult - this.getPageSize();
    }

    public int getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(int firstResult) {
        log.info("set first result" + firstResult);
        this.firstResult = firstResult;
    }

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(Group selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public Group[] getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(Group[] selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    // public void assignGroups(List<Group> g){
    //     if (g.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
    //      g = securityGroupService.getGroups();
    //     }
    //}
    @PostConstruct
    public void init() {
        securityGroupService.setEntityManager(entityManager);
        Map<String, Object> sessionOptions = new HashMap<String, Object>();
        sessionOptions.put(IdentitySessionProducer.SESSION_OPTION_ENTITY_MANAGER, entityManager);
        try {
            securityGroupService.setSecurity(identitySessionFactory.createIdentitySession("default", sessionOptions));
        } catch (IdentityException ex) {
            Logger.getLogger(SecurityGroupListService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("Group") + " " + UI.getMessages("common.selected"), ((Group) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("Group") + " " + UI.getMessages("common.unselected"), ((Group) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        this.setSelectedGroup(null);
    }

    @Override
    public Group getRowData(String rowKey) {
        try {
            return securityGroupService.findByName(rowKey);
        } catch (IdentityException ex) {
            FacesMessage msg = new FacesMessage(UI.getMessages("common.error"), ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            Logger.getLogger(SecurityGroupListService.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        return null;
    }

    @Override
    public Object getRowKey(Group entity) {
        return entity.getName();
    }

    @Override
    public List<Group> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        log.info("Loading groups from security");
        List<Group> result = new ArrayList<Group>();
        try {
            int end = first + pageSize;
            QuerySortOrder order = QuerySortOrder.ASC;
            if (sortOrder == SortOrder.DESCENDING) {
                order = QuerySortOrder.DESC;
            }
            Map<String, Object> _filters = new HashMap<String, Object>();
            result = securityGroupService.find(first, end, sortField, order, _filters);
            this.setRowCount(result.size());
        } catch (UnsupportedCriterium ex) {
            Logger.getLogger(SecurityGroupListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IdentityException ex) {
            Logger.getLogger(SecurityGroupListService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
