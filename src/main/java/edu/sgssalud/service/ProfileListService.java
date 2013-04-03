/*
 * Copyright 2012 cesar.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
import org.jboss.solder.logging.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class ProfileListService extends LazyDataModel<Profile> {

    private static Logger log = Logger.getLogger(ProfileListService.class);
    private static final int MAX_RESULTS = 5;
    @Inject
    @Web
    private EntityManager entityManager;
    @Inject
    private ProfileService profileService;
    private List<Profile> resultList;
    private int firstResult = 0;
    private String typeName;
    private Profile[] selectedProfiles;
    private Profile selectedProfile;
    
    public ProfileListService() {
        setPageSize(MAX_RESULTS);
        resultList = new ArrayList<Profile>();
    }

    @PostConstruct
    public void init() {
        profileService.setEntityManager(entityManager);
    }

    @Override
    public List<Profile> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {

        int end = first + pageSize;

        QuerySortOrder order = QuerySortOrder.ASC;
        if (sortOrder == SortOrder.DESCENDING) {
            order = QuerySortOrder.DESC;
        }
        Map<String, Object> _filters = new HashMap<String, Object>();
        /*_filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
         _filters.putAll(filters);*/

        QueryData<Profile> qData = profileService.find(first, end, sortField, order, _filters);
        this.setRowCount(qData.getTotalResultCount().intValue());
        return qData.getResult();
    }
    
    @Override
    public Profile getRowData(String rowKey) {

        return profileService.findByName(rowKey);
    }

    @Override
    public Object getRowKey(Profile entity) {
        return entity.getName();
    }

    public List<Profile> getResultList() {
        if (resultList.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
            resultList = profileService.getProfiles(this.getPageSize(), firstResult);
        }
        return resultList;
    }

    public void setResultList(List<Profile> resultList) {
        this.resultList = resultList;
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
        this.firstResult = firstResult;
        this.resultList = null;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }     

    public Profile[] getSelectedProfiles() {
        return selectedProfiles;
    }

    public void setSelectedProfiles(Profile[] selectedProfiles) {
        this.selectedProfiles = selectedProfiles;
    }

    public Profile getSelectedProfile() {
        return selectedProfile;
    }

    public void setSelectedProfile(Profile selectedProfile) {
        this.selectedProfile = selectedProfile;
    }
    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("profile") + " " + UI.getMessages("common.selected"), ((Profile) event.getObject()).getName());

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("profile") + " " + UI.getMessages("common.unselected"), ((Profile) event.getObject()).getName());

        FacesContext.getCurrentInstance().addMessage(null, msg);
        this.setSelectedProfile(null);
    }
}
