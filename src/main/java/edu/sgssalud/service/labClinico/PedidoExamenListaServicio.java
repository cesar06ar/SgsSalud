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
import edu.sgssalud.controller.security.SecurityGroupService;
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import edu.sgssalud.model.labClinico.ResultadoExamenLabClinico;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
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
public class PedidoExamenListaServicio extends LazyDataModel<PedidoExamenLaboratorio> {

    private static final long serialVersionUID = 1L;
    private static final int MAX_RESULTS = 10;

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private PedidoExamenService pedidosExamenLabService;
    @Inject
    private ResultadoExamenLCService resultadosExamenesService;
    
//    @Inject
//    private ProfileService profileServicio;
    private List<PedidoExamenLaboratorio> resultList = new ArrayList<PedidoExamenLaboratorio>();
    //private List<ExamenLabClinico> resultListFilter = new ArrayList<ExamenLabClinico>();
    private int primerResult = 0;
    private String parametroBusqueda;
    private Date fecha; //= new Date();
    private PedidoExamenLaboratorio[] PedidosExamenesSeleccionados;
    private PedidoExamenLaboratorio PedidoExamenSeleccionado;

    public PedidoExamenListaServicio() {
        setPageSize(MAX_RESULTS);
        //resultList = new ArrayList<Medicamento>();        
    }

    public List<PedidoExamenLaboratorio> getResultList() {
        return resultList;
    }

    public void setResultList(List<PedidoExamenLaboratorio> resultList) {
        this.resultList = resultList;
    }

    public int getPrimerResult() {
        return primerResult;
    }

    public void setPrimerResult(int primerResult) {
        this.primerResult = primerResult;
        this.resultList = null;
    }

    public PedidoExamenLaboratorio[] getPedidosExamenesSeleccionados() {
        return PedidosExamenesSeleccionados;
    }

    public void setPedidosExamenesSeleccionados(PedidoExamenLaboratorio[] PedidosExamenesSeleccionados) {
        this.PedidosExamenesSeleccionados = PedidosExamenesSeleccionados;
    }

    public PedidoExamenLaboratorio getPedidoExamenSeleccionado() {
        return PedidoExamenSeleccionado;
    }

    public void setPedidoExamenSeleccionado(PedidoExamenLaboratorio PedidoExamenSeleccionado) {
        this.PedidoExamenSeleccionado = PedidoExamenSeleccionado;
    }

    public PedidoExamenService getPedidosExamenLabService() {
        return pedidosExamenLabService;
    }

    public void setPedidosExamenLabService(PedidoExamenService pedidosExamenLabService) {
        this.pedidosExamenLabService = pedidosExamenLabService;
    }

    public String getParametroBusqueda() {
        return parametroBusqueda;
    }

    public void setParametroBusqueda(String parametroBusqueda) {
        this.parametroBusqueda = parametroBusqueda;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public List<PedidoExamenLaboratorio> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        int end = first + pageSize;

        QuerySortOrder order = QuerySortOrder.ASC;
        if (sortOrder == SortOrder.DESCENDING) {
            order = QuerySortOrder.DESC;
        }
        Map<String, Object> _filters = new HashMap<String, Object>();
        /*_filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
         _filters.putAll(filters);*/

        QueryData<PedidoExamenLaboratorio> qData = pedidosExamenLabService.find(first, end, sortField, order, _filters);
        this.setRowCount(qData.getTotalResultCount().intValue());
        this.setResultList(qData.getResult());
        return resultList;
    }

    @PostConstruct
    public void init() {
        pedidosExamenLabService.setEntityManager(em);
        resultadosExamenesService.setEntityManager(em);
        //profileServicio.setEntityManager(em);
        if (resultList.isEmpty()) {
            resultList = pedidosExamenLabService.getPedidosExamenesLab();
        }
    }

    @Override
    public PedidoExamenLaboratorio getRowData(String id) {
        return pedidosExamenLabService.getPedidoPorId(Long.parseLong(id));
    }

    @Override
    public Object getRowKey(PedidoExamenLaboratorio entity) {
        return entity.getId();
    }

    public void onRowSelect(SelectEvent event) {

        FacesMessage msg = new FacesMessage(UI.getMessages("Pedido Examen") + " " + UI.getMessages("common.selected"), "" + ((PedidoExamenLaboratorio) event.getObject()).getId());
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("Pedido Examen") + " " + UI.getMessages("common.unselected"), "" + ((PedidoExamenLaboratorio) event.getObject()).getId());
        FacesContext.getCurrentInstance().addMessage("", msg);
        this.setPedidoExamenSeleccionado(null);
    }

    public void buscarPorParametro() {
        this.setResultList(pedidosExamenLabService.getPedidosExamenesLab(fecha));
    }

    public void agregarMuestra() {
        System.out.println("Actualizado correctamente :________________ ");
        Date now = Calendar.getInstance().getTime();
        if (PedidoExamenSeleccionado.isPersistent()) {
            PedidoExamenSeleccionado.setEstado("Pendiente");
            em.merge(PedidoExamenSeleccionado);
            System.out.println("Actualizado correcta  :________________ ");
        }
//        System.out.println("Actualizado correcta 2 :________________ ");
//        if (resultList.isEmpty()) {
        resultList = pedidosExamenLabService.getPedidosExamenesLab();
//        }
        System.out.println("Actualizado correcta 3 :________________ ");
        FacesMessage msg = new FacesMessage("Los Códigos de muestra han sido agregados" + " ", "");
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    @TransactionAttribute
    public void borrar() {
        System.out.println("Ingreso a borrar______________-");
        try {
            if (PedidoExamenSeleccionado.isPersistent()) {
                PedidoExamenLaboratorio p = PedidoExamenSeleccionado;
                ResultadoExamenLabClinico aux;
                List<ResultadoExamenLabClinico> listaResultadosExamenLab = resultadosExamenesService.getResultadosExamenPorPedidoExamen(p);
                for (ResultadoExamenLabClinico result : listaResultadosExamenLab) {
                    aux = result;
                    em.remove(aux);
                    em.flush();
                }
                em.flush();
                em.remove(em.merge(p));
                PedidoExamenSeleccionado = null;
                FacesMessage msg = new FacesMessage("Se elimino correctamente. ", "");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERRORE", e.toString()));
            e.printStackTrace();
        }
        resultList = pedidosExamenLabService.getPedidosExamenesLab();
    }

}
