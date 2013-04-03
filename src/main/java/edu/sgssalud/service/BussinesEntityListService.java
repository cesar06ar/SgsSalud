package edu.sgssalud.service;


/*
 * Copyright 2012 jlgranda.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.BussinesEntity;
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.model.BussinesEntity_;

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
 * @author jlgranda
 */
@RequestScoped
@Named
public class BussinesEntityListService extends LazyDataModel<BussinesEntity> {

    /**
     *
     */
    private static final long serialVersionUID = 4819808125494695197L;
    private static final int MAX_RESULTS = 5;
    private static Logger log = Logger.getLogger(BussinesEntityListService.class);
    @Inject
    @Web
    private EntityManager entityManager;
    @Inject
    private BussinesEntityService bussinesEntityService;
    private List<BussinesEntity> resultList;
    private int firstResult = 0;
    private BussinesEntityType type;
    private String typeName;
    private BussinesEntity[] selectedBussinesEntities;
    private BussinesEntity selectedBussinesEntity; //Filtro de cuenta schema

    public BussinesEntityListService() {
        setPageSize(MAX_RESULTS);
        resultList = new ArrayList<BussinesEntity>();
    }

    @PostConstruct
    public void init() {
        bussinesEntityService.setEntityManager(entityManager);
    }

    public List<BussinesEntity> getResultList() {
        log.info("load BussinesEntitys");

        if (resultList.isEmpty()/* && getSelectedBussinesEntity() != null*/) {
            resultList = bussinesEntityService.find(this.getPageSize(), firstResult/*, getSelectedBussinesEntity()*/);
        }
        return resultList;
    }

    public int getNextFirstResult() {
        return firstResult + this.getPageSize();
    }

    public int getPreviousFirstResult() {
        return this.getPageSize() >= firstResult ? 0 : firstResult - this.getPageSize();
    }

    public Integer getFirstResult() {
        return firstResult;
    }

    public BussinesEntityType getType() {
        if (type == null){
            setType(bussinesEntityService.findBussinesEntityTypeByName(getTypeName()));
        }
        return type;
    }

    public void setType(BussinesEntityType type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setFirstResult(Integer firstResult) {
        log.info("set first result + firstResult");
        this.firstResult = firstResult;
        this.resultList = null;
    }

    public boolean isPreviousExists() {
        return firstResult > 0;
    }

    public boolean isNextExists() {
        return bussinesEntityService.count() > this.getPageSize() + firstResult;
    }

    @Override
    public BussinesEntity getRowData(String rowKey) {

        return bussinesEntityService.findByName(rowKey);
    }

    @Override
    public Object getRowKey(BussinesEntity entity) {
        return entity.getName();
    }

    @Override
    public List<BussinesEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {

        int end = first + pageSize;

        QuerySortOrder order = QuerySortOrder.ASC;
        if (sortOrder == SortOrder.DESCENDING) {
            order = QuerySortOrder.DESC;
        }
        Map<String, Object> _filters = new HashMap<String, Object>();
        _filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
        _filters.putAll(filters);

        QueryData<BussinesEntity> qData = bussinesEntityService.find(first, end, sortField, order, _filters);
        this.setRowCount(qData.getTotalResultCount().intValue());
        return qData.getResult();
    }

    public BussinesEntity[] getSelectedBussinesEntities() {
        return selectedBussinesEntities;
    }

    public void setSelectedBussinesEntities(BussinesEntity[] selectedBussinesEntities) {
        this.selectedBussinesEntities = selectedBussinesEntities;
    }

    public BussinesEntity getSelectedBussinesEntity() {
        return selectedBussinesEntity;
    }

    public void setSelectedBussinesEntity(BussinesEntity selectedBussinesEntity) {
        this.selectedBussinesEntity = selectedBussinesEntity;
    }
    
    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("BussinesEntity") + " " + UI.getMessages("common.selected"), ((BussinesEntity) event.getObject()).getName());

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("BussinesEntity") + " " + UI.getMessages("common.unselected"), ((BussinesEntity) event.getObject()).getName());

        FacesContext.getCurrentInstance().addMessage(null, msg);
        this.setSelectedBussinesEntity(null);
    }
}
