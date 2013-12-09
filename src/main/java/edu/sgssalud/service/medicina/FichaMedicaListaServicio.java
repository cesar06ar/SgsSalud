/*
 * Copyright 2013 tania.
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
package edu.sgssalud.service.medicina;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
import java.util.ArrayList;
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
import org.jboss.solder.logging.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author tania
 */
@Named("fichaMedicaListaServicio")
@ViewScoped
public class FichaMedicaListaServicio extends LazyDataModel<FichaMedica>{
    
    private static final long serialVersionUID = 5L;
    private static final int MAX_RESULTS = 5;
    private static Logger log = Logger.getLogger(FichaMedicaListaServicio.class);

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private FichaMedicaServicio fms;    
    private List<FichaMedica> resultList;
    private int primerResult = 0;
    private FichaMedica[] fichaMedicSeleccionadas;
    private FichaMedica fichaMedicSeleccionada;
    private String parametroBusqueda;
    
    /*Método para inicializar tabla*/
    public FichaMedicaListaServicio() {
        setPageSize(MAX_RESULTS);
        resultList = new ArrayList<FichaMedica>();
    }
    
    @PostConstruct
    public void init() {
        fms.setEntityManager(em);
        if (resultList.isEmpty() ) {
           resultList = fms.getFichasMedicas(getPageSize(), primerResult);
        }
    }
    @Override
    public List<FichaMedica> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        int end = first + pageSize;

        QuerySortOrder order = QuerySortOrder.ASC;
        if (sortOrder == SortOrder.DESCENDING) {
            order = QuerySortOrder.DESC;
        }
        Map<String, Object> _filters = new HashMap<String, Object>();
        /*_filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
         _filters.putAll(filters);*/

        QueryData<FichaMedica> qData = fms.find(first, end, sortField, order, _filters);
        this.setRowCount(qData.getTotalResultCount().intValue());
        this.setResultList(qData.getResult());        
        
        return qData.getResult();        
    }
    /*Método sobreescrito para cargar los datos desde la base de datos hacia la tabla*/
    
    
    /*Métodos que me permiten seleccionar un objeto de la tabla*/
    
    @Override
    public Object getRowKey(FichaMedica entity) {
        return entity.getPaciente().getNombres();
    }
//
//    @Override
//    public FichaMedica getRowData(Long id) {
//        return fms.getFichaMedicaPorId(id);
//    }

    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("FichaMedica") + " " + UI.getMessages("common.selected"), ((FichaMedica) event.getObject()).getPaciente().getNombres());
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("FichaMedica") + " " + UI.getMessages("common.unselected"), ((FichaMedica) event.getObject()).getPaciente().getNombres());
        FacesContext.getCurrentInstance().addMessage("", msg);
        this.setFichaMedicSeleccionado(null);
    }
    /*.............*/

    /*Métodos GET y SET de los Atributos*/
    public int getNextFirstResult() {
        return primerResult + this.getPageSize();
    }

    public int getPreviousFirstResult() {
        return this.getPageSize() >= primerResult ? 0 : primerResult - this.getPageSize();
    }

    public int getFirstResult() {
        return primerResult;
    }

    public void setFirstResult(int firstResult) {
        this.primerResult = firstResult;
        this.resultList = null;
    }

    public List<FichaMedica> getResultList() {
       if (resultList.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
            resultList = fms.getFichasMedicas(this.getPageSize(), primerResult);
        }
        
        return resultList;
    }

    public void setResultList(List<FichaMedica> resultList) {
        this.resultList = resultList;
    }

    
    
    
//    public String getParametroBusqueda() {
//        return parametroBusqueda;
//    }
//
//    public void setParametroBusqueda(String parametroBusqueda) {
//        this.parametroBusqueda = parametroBusqueda;
//        setResultList(cms.getConsultaMedicaPorId(parametroBusqueda));
//    }    
    
    /*..
     * Busca de la base de datos pacientes segun el parametro ingresado
     */
//    public void buscarPorParametroAutoComplete() {
//        //this.setResulList(medicamentoService.BuscarMedicamentosPorParametro(parametroBusqueda));
//    }
//
//    public void buscarPorParametro() {
//        this.setResultList(cms.BuscarPacientePorTodosParametros(parametroBusqueda));
//    }

    public FichaMedica getFichaMedicSeleccionado() {
        return fichaMedicSeleccionada;
    }

    public void setFichaMedicSeleccionado(FichaMedica fichaMedicSeleccionado) {
        this.fichaMedicSeleccionada = fichaMedicSeleccionado;
    }
    
    public void buscarFichaMedicaPorParametro() {
        this.setResultList(fms.BuscarFichaMedicaPorParametro(parametroBusqueda));
    }
}
