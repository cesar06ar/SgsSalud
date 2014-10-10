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
package edu.sgssalud.service.paciente;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.util.UI;
import edu.sgssalud.web.service.WebServiceSGAClientConnection;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author cesar: Esta clase hereda de LazyDataModel de Primefaces, para hacer
 * una carga ligera de datos...
 */
@Named
//@ManagedBean(name = "pacienteListaServicio")
@ViewScoped
public class PacienteListaServicio implements Serializable { //extends LazyDataModel<Paciente> {

    private static final int MAX_RESULTS = 5;
    /*Inyeción de Dependencias importantes para cargar los datos*/
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private PacienteServicio pacienteServicio;
    @Inject
    private FichaMedicaServicio fichaMedServicio;
    private List<Paciente> resultList = new ArrayList<Paciente>();
    private int firstResult = 0;
    //private Paciente[] pacientesSeleccionados;
    private Paciente pacienteSelecionado;
    private String parametroBusqueda;
    private WebServiceSGAClientConnection coneccionSGA = new WebServiceSGAClientConnection();

    public PacienteListaServicio() {
        //setPageSize(MAX_RESULTS);
        resultList = new ArrayList<Paciente>();
    }

    @PostConstruct
    public void init() {
        pacienteServicio.setEntityManager(em);
        fichaMedServicio.setEntityManager(em);
//        settingService.setEntityManager(em);
//        settingList = settingService.getSettingByName("id_oferta");
//        if (resultList.isEmpty()) {
//            //resultList = pacienteServicio.getPacientes(getPageSize(), firstResult);
//        }
    }

    /*Método sobreescrito para cargar los datos desde la base de datos hacia la tabla*/
    /*@Override
     public List<Paciente> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
     int end = first + pageSize;

     QuerySortOrder order = QuerySortOrder.ASC;
     if (sortOrder == SortOrder.DESCENDING) {
     order = QuerySortOrder.DESC;
     }
     Map<String, Object> _filters = new HashMap<String, Object>();
     _filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
     _filters.putAll(filters);

     QueryData<Paciente> qData = pacienteServicio.find(first, end, sortField, order, _filters);
     this.setRowCount(qData.getTotalResultCount().intValue());
     this.setResultList(qData.getResult());
     Collections.sort(resultList);
     return qData.getResult();
     }*/

    /*Métodos que me permiten seleccionar un objeto de la tabla*/
//    @Override
    public Paciente getRowData(String rowKey) {
        return pacienteServicio.buscarPorNombres(rowKey);
    }

    //  @Override
    public Object getRowKey(Paciente entity) {
        return entity.getName();
    }

    public void onRowSelect(SelectEvent event) {
        Paciente p = (Paciente) event.getObject();
        FacesMessage msg = new FacesMessage(UI.getMessages("Paciente") + " " + UI.getMessages("common.selected"), p.getNombres() + " "+ p.getApellidos());
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        Paciente p = (Paciente) event.getObject();
        FacesMessage msg = new FacesMessage(UI.getMessages("Paciente") + " " + UI.getMessages("common.unselected"), p.getNombres() + " "+ p.getApellidos());
        FacesContext.getCurrentInstance().addMessage("", msg);
        this.setPacienteSelecionado(null);
    }
    /*.............*/

    /*Métodos GET y SET de los Atributos*/
//    public int getNextFirstResult() {
//        return firstResult + this.getPageSize();
//    }
//
//    public int getPreviousFirstResult() {
//        return this.getPageSize() >= firstResult ? 0 : firstResult - this.getPageSize();
//    }
    public int getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        this.resultList = null;
    }

    public List<Paciente> getResultList() {
//        if (resultList.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
//            resultList = pacienteServicio.getPacientes(firstResult, firstResult);
//        }
//        Collections.sort(resultList);
        return resultList;
    }

    public void setResultList(List<Paciente> resultList) {
        this.resultList = resultList;
    }

    public Paciente getPacienteSelecionado() {
        return pacienteSelecionado;
    }

    public void setPacienteSelecionado(Paciente pacienteSelecionado) {
        this.pacienteSelecionado = pacienteSelecionado;
    }

    public String getParametroBusqueda() {
        return parametroBusqueda;
    }

    public void setParametroBusqueda(String parametroBusqueda) {
        this.parametroBusqueda = parametroBusqueda;
        if (parametroBusqueda.length() >= 3) {
            String criterio = parametroBusqueda.replace(" ", "%");
            setResultList(pacienteServicio.buscarPacientePorParametro(criterio));
        }
    }


    /*..
     * Busca de la base de datos pacientes segun el parametro ingresado
     */
    public void buscarPorParametro() {
        this.setResultList(pacienteServicio.buscarPacientePorTodosParametros(parametroBusqueda));
    }

//    public boolean tieneFicha(){
//        if(pacienteSelecionado.isPersistent()){
//            return (fichaMedServicio.getFichaMedicaPorPaciente(pacienteSelecionado) != null) ;
//        }        
//        return false;        
//    }
    public String renderizarVistaMatriculado(String ruta, String backView) {
        //SecurityRules sr = new SecurityRules();
        if (pacienteSelecionado.getId() != null) {
            if ("Universitario".equals(pacienteSelecionado.getTipoEstudiante())) {
                boolean matriculado = coneccionSGA.getEstudianteMatriculado_WS_SGA(pacienteSelecionado.getCedula());
                if (matriculado) {
                    ruta += "?faces-redirect=true"
                            + "&pacienteId=" + pacienteSelecionado.getId()
                            + "&backView=" + backView;
                    return ruta;
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "El paciente no puede acceder a los servicios ya que actualmente no esta matriculado", " ");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    RequestContext.getCurrentInstance().update(":form:tablaPacientes :form:messages");
                }
            } else {
                ruta += "?faces-redirect=true"
                        + "&pacienteId=" + pacienteSelecionado.getId()
                        + "&backView=" + backView;
                return ruta;
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione un Paciente", " ");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        this.setPacienteSelecionado(null);
        return null;
    }
}
