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

import java.io.Serializable;
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
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author lucho
 */
@Named(value = "settingListService")
@ViewScoped
public class SettingListService extends LazyDataModel<Setting> implements Serializable {

    private static final long serialVersionUID = 4819808125494695197L;
    private static final int MAX_RESULTS = 5;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(SettingListService.class);
    @Inject
    @Web
    private EntityManager entityManager;
    @Inject
    private SettingService settingService;
    private List<Setting> resultList;
    private int firstResult = 0;
    private Setting[] selectedSettings;
    private Setting selectedSetting;

    public SettingListService() {
        setPageSize(MAX_RESULTS);
        resultList = new ArrayList<Setting>();
    }

    public List<Setting> getResultList() {
        log.info("load BussinesEntityType");
        if (resultList.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
            resultList = settingService.getSettings(this.getPageSize(), firstResult);
            log.info("eqaula --> resultlist " + resultList);
        }
        return resultList;
    }

    public void setResultList(List<Setting> resultList) {
        this.resultList = resultList;
    }

    public void setFirstResult(int firstResult) {
        log.info("set first result + firstResult");
        this.firstResult = firstResult;
        this.resultList = null;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public int getNextFirstResult() {
        return firstResult + this.getPageSize();
    }

    public int getPreviousFirstResult() {
        return this.getPageSize() >= firstResult ? 0 : firstResult - this.getPageSize();
    }

    public Setting getSelectedSetting() {
        return selectedSetting;
    }

    public void setSelectedSetting(Setting selectedSetting) {
        this.selectedSetting = selectedSetting;
    }

     @Override
    public List<Setting> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        int end = first + pageSize;

        QuerySortOrder order = QuerySortOrder.ASC;
        if (sortOrder == SortOrder.DESCENDING) {
            order = QuerySortOrder.DESC;
        }
        Map<String, Object> _filters = new HashMap<String, Object>();
        /*_filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
         _filters.putAll(filters);*/

        QueryData<Setting> qData = settingService.find(first, end, sortField, order, _filters);
        this.setRowCount(qData.getTotalResultCount().intValue());
        return qData.getResult();
    }

    @PostConstruct
    public void init() {
        log.info("Setup entityManager into WareHouseService...");
        settingService.setEntityManager(entityManager);
    }

    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("module.setting") + " " + UI.getMessages("common.selected"), ((Setting) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("module.setting") + " " + UI.getMessages("common.unselected"), ((Setting) event.getObject()).getName());

        FacesContext.getCurrentInstance().addMessage(null, msg);
        this.setSelectedSetting(null);
    }

    @Override
    public Setting getRowData(String rowKey) {

        return settingService.findByName(rowKey);
    }

    @Override
    public Object getRowKey(Setting entity) {
        return entity.getName();
    }
}
