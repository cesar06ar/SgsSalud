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
package edu.sgssalud.controller.labClinico;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import edu.sgssalud.model.labClinico.ResultadoExamenLabClinico;
import edu.sgssalud.model.labClinico.ResultadoParametro;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.labClinico.PedidoExamenService;
import edu.sgssalud.service.labClinico.ResultadoExamenLCService;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
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
public class ResultadosExamenHome extends LazyDataModel<ResultadoExamenLabClinico> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int MAX_RESULTS = 10;

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private PacienteServicio pacienteService;
    @Inject
    private PedidoExamenService pedidoExamenService;
    @Inject
    private ResultadoExamenLCService resultadosExamenService;
    private Long pedidoExamenId;
    private PedidoExamenLaboratorio pedidoExamen;
    private ResultadoExamenLabClinico resultadoSelect;
    private List<ResultadoExamenLabClinico> listaResultadosExamen = new ArrayList<ResultadoExamenLabClinico>();
    private List<ResultadoParametro> listaResultadoParam = new ArrayList<ResultadoParametro>();
    private int primerResult = 0;
    private boolean estadoResult = false;
    private Date fecha; //= new Date();

    public Long getPedidoExamenId() {
        return pedidoExamenId;
    }

    public void setPedidoExamenId(Long pedidoExamenId) {
        //setPedidoExamenId(pedidoExamenId);
        this.pedidoExamenId = pedidoExamenId;
        this.setPedidoExamen(pedidoExamenService.getPedidoPorId(pedidoExamenId));
        this.listaResultadosExamen = resultadosExamenService.getResultadosExamenPorPedidoExamen(pedidoExamen);
        //this.setPaciente(pedidoExamen.getPaciente());
    }

    public List<ResultadoExamenLabClinico> getListaResultadosExamen() {
        return listaResultadosExamen;
    }

    public void setListaResultadosExamen(List<ResultadoExamenLabClinico> listaResultadosExamen) {
        this.listaResultadosExamen = listaResultadosExamen;
    }

    public PedidoExamenLaboratorio getPedidoExamen() {
        return pedidoExamen;
    }

    public void setPedidoExamen(PedidoExamenLaboratorio pedidoExamen) {
        this.pedidoExamen = pedidoExamen;
    }

    public ResultadoExamenLabClinico getResultadoSelect() {
        return resultadoSelect;
    }

    public void setResultadoSelect(ResultadoExamenLabClinico resultadoSelect) {
        this.resultadoSelect = resultadoSelect;
    }

    public int getPrimerResult() {
        return primerResult;
    }

    public void setPrimerResult(int primerResult) {
        this.primerResult = primerResult;
        this.listaResultadosExamen = null;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public ResultadosExamenHome() {
        setPageSize(MAX_RESULTS);
        //resultList = new ArrayList<Medicamento>();        
    }

    public List<ResultadoParametro> getListaResultadoParam() {
        return listaResultadoParam;
    }

    public void setListaResultadoParam(List<ResultadoParametro> listaResultadoParam) {
        this.listaResultadoParam = listaResultadoParam;
    }
    

    @PostConstruct
    public void init() {
        //setEntityManager(em);
        pacienteService.setEntityManager(em);
        pedidoExamenService.setEntityManager(em);
        resultadosExamenService.setEntityManager(em);
    }

    @TransactionAttribute
    public String guardar() {
        Date now = Calendar.getInstance().getTime();
        try {
            if (pedidoExamen.isPersistent() && estadoResult) {
                this.pedidoExamen.setEstado("Realizado");
                em.merge(pedidoExamen);
            } else {
                FacesMessage msg = new FacesMessage("Agrege resultados antes de guardar ", null);
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + resultadoSelect.getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }

        return "/pages/labClinico/listaPedidosExam.xhtml?faces-redirect=true";
    }

    @TransactionAttribute
    public void guardarResultado() {
        //Date now = Calendar.getInstance().getTime();
        try {
            if (resultadoSelect.isPersistent()) {
                resultadoSelect.setResultadosParametros(listaResultadoParam);
                em.merge(resultadoSelect);
                FacesMessage msg = new FacesMessage("Se guardo resultado de Examen: " + resultadoSelect.getId() + " con éxito", null);
                FacesContext.getCurrentInstance().addMessage("", msg);
                this.setResultadoSelect(null);
                System.out.println("Guardo con exito_________");
                this.estadoResult = true;
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + resultadoSelect.getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }

        //return "/pages/labClinico/listaExamenes.xhtml?faces-redirect=true";
    }

    @Transactional
    public String borrarEntidad() {
        //log.info("sgssalud --> ingreso a eliminar: " + getInstance().getId());
//        try {
//            if (getInstance() == null) {
//                throw new NullPointerException("Servicio is null");
//            }
//            if (getInstance().isPersistent()) {
//                delete(getInstance());
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  " + getInstance().getName(), ""));
//            } else {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "¡No existe una entidad para ser borrada!", ""));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", e.toString()));
//        }
        return "/pages/serviciosMedicos/lista.xhtml?faces-redirect=true";
    }

    @Override
    public List<ResultadoExamenLabClinico> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        int end = first + pageSize;

        QuerySortOrder order = QuerySortOrder.ASC;
        if (sortOrder == SortOrder.DESCENDING) {
            order = QuerySortOrder.DESC;
        }
        Map<String, Object> _filters = new HashMap<String, Object>();
        /*_filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
         _filters.putAll(filters);*/

        QueryData<ResultadoExamenLabClinico> qData = resultadosExamenService.find(first, end, sortField, order, _filters);
        this.setRowCount(qData.getTotalResultCount().intValue());
        this.setListaResultadosExamen(qData.getResult());
        return listaResultadosExamen;
    }

    @Override
    public ResultadoExamenLabClinico getRowData(String id) {
        return resultadosExamenService.getResultadosExamenLCPorId(Long.parseLong(id));
    }

    @Override
    public Object getRowKey(ResultadoExamenLabClinico entity) {
        return entity.getId();
    }

    public void onRowSelect(SelectEvent event) {
        this.resultadoSelect.setFechaRealizacion(Calendar.getInstance().getTime());
        listaResultadoParam = resultadosExamenService.getResultadoParametros(resultadoSelect);
        FacesMessage msg = new FacesMessage(UI.getMessages("Resultado Examen") + " " + UI.getMessages("common.selected"), "" + ((ResultadoExamenLabClinico) event.getObject()).getId());
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("Resultado Examen") + " " + UI.getMessages("common.unselected"), "" + ((ResultadoExamenLabClinico) event.getObject()).getId());
        FacesContext.getCurrentInstance().addMessage("", msg);
        this.setResultadoSelect(null);
    }
    
    public Long getCosto(){
       long total = 0;
        for (ResultadoExamenLabClinico res : listaResultadosExamen) {
            total += res.getExamenLab().getCosto();
        }
        return total; 
    }
    /*
     public void onEdit(RowEditEvent event) {
     FacesMessage msg = new FacesMessage("Resultado Editado", ((ResultadoExamenLabClinico) event.getObject()).getExamenLab().getName());
     FacesContext.getCurrentInstance().addMessage(null, msg);
     }
    
     public void onCancel(RowEditEvent event) {
     FacesMessage msg = new FacesMessage("Resultado Cancelado", ((ResultadoExamenLabClinico) event.getObject()).getExamenLab().getName());
     FacesContext.getCurrentInstance().addMessage(null, msg);
     }
    
     public void onCellEdit(CellEditEvent event) {
     ResultadoExamenLabClinico resultado = (ResultadoExamenLabClinico) event.getNewValue();
     System.out.println("REsultado____- " + resultado.getId() + "   " + resultado.getValorResultado());
     try {
     em.merge(resultado);
     } catch (Exception ex) {
     System.out.println("Error");
     }
        
     FacesMessage msg = new FacesMessage("Resultado Modificado", ("" + resultado.getId()));
     FacesContext.getCurrentInstance().addMessage(null, msg);
     }*/

}
