/*
 * Copyright 2014 cesar.
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
package edu.sgssalud.service.labClinico;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.farmacia.RecetaListaServicio;
import edu.sgssalud.service.farmacia.RecetaServicio;
import edu.sgssalud.util.Lists;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.security.Identity;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author cesar
 */
@Named()
@ViewScoped
public class ExamenLabListaServicio extends LazyDataModel<ExamenLabClinico> {

    private static final long serialVersionUID = 1L;
    private static final int MAX_RESULTS = 10;

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private ExamenLabService examenLabService;
//    @Inject
//    private Identity identity;
//    @Inject
//    private ProfileService profileServicio;
    private List<ExamenLabClinico> resultList = new ArrayList<ExamenLabClinico>();
    private List<ExamenLabClinico> resultListFilter = new ArrayList<ExamenLabClinico>();
    private int primerResult = 0;
    private ExamenLabClinico[] ExamenesSeleccionados;
    private ExamenLabClinico ExamenSeleccionado;

    //private String parametroBusqueda;
    public List<ExamenLabClinico> getResultList() {
        if (resultList.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
            resultList = examenLabService.getExamenesLab();
            //resultListFilter = resultList;
        }
        return resultList;
    }

    public void setResultList(List<ExamenLabClinico> resultList) {
        this.resultList = resultList;
    }

    public List<ExamenLabClinico> getResultListFilter() {
        Collections.sort(resultList);
        return resultListFilter;
    }

    public void setResultListFilter(List<ExamenLabClinico> resultListFilter) {
        this.resultListFilter = resultListFilter;
    }

    public int getPrimerResult() {
        return primerResult;
    }

    public void setPrimerResult(int primerResult) {
        this.primerResult = primerResult;
        this.resultList = null;
    }

    public ExamenLabClinico getExamenSeleccionado() {
        return ExamenSeleccionado;
    }

    public void setExamenSeleccionado(ExamenLabClinico ExamenSeleccionado) {
        this.ExamenSeleccionado = ExamenSeleccionado;
    }

    public ExamenLabClinico[] getExamenesSeleccionados() {
        return ExamenesSeleccionados;
    }

    public void setExamenesSeleccionados(ExamenLabClinico[] ExamenesSeleccionados) {
        this.ExamenesSeleccionados = ExamenesSeleccionados;
    }

    @Override
    public List<ExamenLabClinico> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        int end = first + pageSize;

        QuerySortOrder order = QuerySortOrder.ASC;
        if (sortOrder == SortOrder.DESCENDING) {
            order = QuerySortOrder.DESC;
        }
        Map<String, Object> _filters = new HashMap<String, Object>();
        /*_filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
         _filters.putAll(filters);*/

        QueryData<ExamenLabClinico> qData = examenLabService.find(first, end, sortField, order, _filters);
        this.setRowCount(qData.getTotalResultCount().intValue());
        this.setResultList(qData.getResult());
        return resultList;
    }

    @PostConstruct
    public void init() {
        examenLabService.setEntityManager(em);
        //profileServicio.setEntityManager(em);
        if (resultList.isEmpty()) {
            resultList = examenLabService.getExamenesLab();
        }
    }

    @Override
    public ExamenLabClinico getRowData(String id) {
        return examenLabService.getExamenPorId(Long.parseLong(id));
    }

    @Override
    public Object getRowKey(ExamenLabClinico entity) {
        return entity.getName();
    }

    public void onRowSelect(SelectEvent event) {
        //this.listaMedicaciones = Lists.stringToList(((Receta) event.getObject()).getMedicaciones());
        //this.listaIndicaciones = Lists.stringToList(((Receta) event.getObject()).getIndicaciones());
        FacesMessage msg = new FacesMessage(UI.getMessages("Examen") + " " + UI.getMessages("common.selected"), ((ExamenLabClinico) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    public void onRowUnselect(UnselectEvent event) {
//        this.listaMedicaciones = null;
//        this.listaIndicaciones = null;
        FacesMessage msg = new FacesMessage(UI.getMessages("Examen") + " " + UI.getMessages("common.unselected"), ((ExamenLabClinico) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage("", msg);
        this.setExamenSeleccionado(null);
    }
}
