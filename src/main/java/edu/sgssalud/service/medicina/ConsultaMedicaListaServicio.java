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
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.service.farmacia.MedicamentoListaServicio;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
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
@Named("consultaMedicaListaServicio")
@ConversationScoped
public class ConsultaMedicaListaServicio implements Serializable { //extends LazyDataModel<ConsultaMedica>

    private static final long serialVersionUID = 5L;
    private static final int MAX_RESULTS = 5;
    private static Logger log = Logger.getLogger(ConsultaMedicaListaServicio.class);

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private ConsultaMedicaServicio cms;
    private List<ConsultaMedica> resultList;
    private int primerResult = 0;
    private ConsultaMedica[] consulMedicSeleccionadas;
    private ConsultaMedica consulMedicSeleccionada;
    private String parametroBusqueda;
    private Date inicio;// = new Date();
    private Date fin;// = new Date();
    /*Método para inicializar tabla*/

    public ConsultaMedicaListaServicio() {
        //setPageSize(MAX_RESULTS);
        resultList = new ArrayList<ConsultaMedica>();
    }

    @PostConstruct
    public void init() {
        cms.setEntityManager(em);
        if (resultList.isEmpty()) {
            resultList = cms.getConsulasMedicas();
        }
        inicio = new Date();
        fin = new Date();
    }

    /*Método sobreescrito para cargar los datos desde la base de datos hacia la tabla*/
    /*@Override
     public List<ConsultaMedica> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
     int end = first + pageSize;

     QuerySortOrder order = QuerySortOrder.ASC;
     if (sortOrder == SortOrder.DESCENDING) {
     order = QuerySortOrder.DESC;
     }
     Map<String, Object> _filters = new HashMap<String, Object>();
     _filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
     _filters.putAll(filters);

     QueryData<ConsultaMedica> qData = cms.find(first, end, sortField, order, _filters);
     this.setRowCount(qData.getTotalResultCount().intValue());
     this.setResultList(qData.getResult());        
     Collections.sort(resultList);
     return qData.getResult();        
     }*/
    /*Métodos que me permiten seleccionar un objeto de la tabla*/
//    @Override
//    public ConsultaMedica getRowData(Long t) {
//        return cms.getConsultaMedicaPorId(t);
//    }
    public void onRowSelect(SelectEvent event) {
        this.setConsulMedicSeleccionada((ConsultaMedica) event.getObject());
        FacesMessage msg = new FacesMessage(UI.getMessages("ConsultaMedica") + " " + UI.getMessages("common.selected"), "" + ((ConsultaMedica) event.getObject()).getId());
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("ConsultaMedica") + " " + UI.getMessages("common.unselected"), "" + ((ConsultaMedica) event.getObject()).getId());
        FacesContext.getCurrentInstance().addMessage("", msg);
        this.setConsulMedicSeleccionada(null);
    }
    /*.............*/

    /*Métodos GET y SET de los Atributos*/
    public int getFirstResult() {
        return primerResult;
    }

    public void setFirstResult(int firstResult) {
        this.primerResult = firstResult;
        this.resultList = null;
    }

    public List<ConsultaMedica> getResultList() {
        Collections.sort(resultList);
        return resultList;
    }

    public void setResultList(List<ConsultaMedica> resultList) {
        this.resultList = resultList;
    }

    public ConsultaMedica getConsulMedicSeleccionada() {
        return consulMedicSeleccionada;
    }

    public void setConsulMedicSeleccionada(ConsultaMedica consulMedicSeleccionada) {
        this.consulMedicSeleccionada = consulMedicSeleccionada;
    }

    public String getParametroBusqueda() {
        return parametroBusqueda;
    }

    public void setParametroBusqueda(String parametroBusqueda) {
        this.parametroBusqueda = parametroBusqueda;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    /*..
     * Busca de la base de datos pacientes segun el parametro ingresado
     */
//    public void buscarPorParametroAutoComplete() {
//        //this.setResulList(medicamentoService.BuscarMedicamentosPorParametro(parametroBusqueda));
//    }
//
    public void buscarPorParametro() {
        System.out.println("FECHAS " + inicio + fin);
        this.setResultList(cms.buscarPorRangoFechas(inicio, fin));
        this.setInicio(null);
        this.setFin(null);
    }

    public void actualizar() {
        this.setResultList(cms.getConsulasMedicas());
        this.setInicio(null);
        this.setFin(null);
    }

}
