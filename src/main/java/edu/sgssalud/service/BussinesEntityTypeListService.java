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
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author cesar
 */
@RequestScoped
@Named
public class BussinesEntityTypeListService extends LazyDataModel<BussinesEntityType> {

    private static final long serialVersionUID = 4819808125494695200L;
    private static final int MAX_RESULTS = 5;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(BussinesEntityTypeListService.class);
    @Inject
    @Web
    private EntityManager entityManager;
    @Inject
    private BussinesEntityTypeService bussinesEntityTypeService;
    private List<BussinesEntityType> resultList;
    private int firstResult = 0;
    private BussinesEntityType[] selectedBussinesEntitiesType;
    private BussinesEntityType selectedBussinesEntityType; //Filtro de cuenta schema

    public BussinesEntityTypeListService() {
        setPageSize(MAX_RESULTS);
        resultList = new ArrayList<BussinesEntityType>();
    }

    public List<BussinesEntityType> getResultList() {
        if (resultList.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
            resultList = bussinesEntityTypeService.find(this.getPageSize(), firstResult);
        }

        return resultList;
    }

    public void setResultList(List<BussinesEntityType> resultList) {
        this.resultList = resultList;
    }

    public String find() {
        /*
         try {
         init();
         resultList = bussinesEntityTypeService.findAll();
         String summary = "Encontrados! ";             
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));
         System.out.println("lista "+resultList.get(0));
         } catch (Exception e) {
            
         }
         */
        this.getResultList();
        return "/pages/admin/bussinesentitytype/list";
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
        log.info("set first result + firstResult");
        this.firstResult = firstResult;
        this.resultList = null;
    }

    public boolean isPreviousExists() {
        return firstResult > 0;
    }

    @PostConstruct
    public void init() {
        log.info("Setup entityManager into bussinesEntityTypeService...");
        bussinesEntityTypeService.setEntityManager(entityManager);
    }

    @Override
    public BussinesEntityType getRowData(String rowKey) {

        return bussinesEntityTypeService.findByName(rowKey);
    }

    @Override
    public Object getRowKey(BussinesEntityType entity) {
        return entity.getName();
    }

    

    public BussinesEntityType[] getSelectedBussinesEntitiesType() {
        return selectedBussinesEntitiesType;
    }

    public void setSelectedBussinesEntitiesType(BussinesEntityType[] selectedBussinesEntitiesType) {
        this.selectedBussinesEntitiesType = selectedBussinesEntitiesType;
    }

    public BussinesEntityType getSelectedBussinesEntityType() {
        return selectedBussinesEntityType;
    }

    public void setSelectedBussinesEntityType(BussinesEntityType selectedBussinesEntityType) {
        this.selectedBussinesEntityType = selectedBussinesEntityType;
    }

    @Override
    public List<BussinesEntityType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {

        log.info("load results for ..." + first + ", " + pageSize);
        
        
        int end = first + pageSize;

        QuerySortOrder order = QuerySortOrder.ASC;
        if (sortOrder == SortOrder.DESCENDING) {
            order = QuerySortOrder.DESC;
        }
        Map<String, Object> _filters = new HashMap<String, Object>();
        /*_filters.put(BussinesEntityType_.name, getSelectedBussinesEntityType()); //Filtro por defecto
         _filters.putAll(filters);*/

        QueryData<BussinesEntityType> qData = bussinesEntityTypeService.find(first, end, sortField, order, _filters);
        this.setRowCount(qData.getTotalResultCount().intValue());
        return qData.getResult();
    }
    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("module.BussinesEntityType") + " " + UI.getMessages("common.selected"), ((BussinesEntityType) event.getObject()).getName());

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("module.BussinesEntityType") + " " + UI.getMessages("common.unselected"), ((BussinesEntityType) event.getObject()).getName());

        FacesContext.getCurrentInstance().addMessage(null, msg);
        this.setSelectedBussinesEntityType(null);
    }
}
