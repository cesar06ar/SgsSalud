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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.model.servicios.Servicio;
import edu.sgssalud.util.FechasUtil;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
import java.util.Date;
import org.jboss.seam.transaction.Transactional;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author lucho
 */
@Named(value = "servicioMedListS")
@ViewScoped
public class ServicioMededicoListService extends LazyDataModel<Servicio> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int MAX_RESULTS = 5;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ServicioMededicoListService.class);
    @Inject
    @Web
    private EntityManager entityManager;
    @Inject
    private ServiciosMedicosService servicioMedS;
    private List<Servicio> resultList;
    private int firstResult = 0;
    private Setting[] selectedServicios;
    private Servicio selectedServicio;

    private Date fechaI;
    private Date fechaF;

    public ServicioMededicoListService() {
        setPageSize(MAX_RESULTS);
        resultList = new ArrayList<Servicio>();
    }

    public List<Servicio> getResultList() {
//        log.info("load BussinesEntityType");
//        if (resultList.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
//            resultList = servicioMedS.todosServicios(this.getPageSize(), firstResult);
//            log.info("eqaula --> resultlist " + resultList);
//        }
        return resultList;
    }

    public void setResultList(List<Servicio> resultList) {
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

    public Setting[] getSelectedServicios() {
        return selectedServicios;
    }

    public void setSelectedServicios(Setting[] selectedServicios) {
        this.selectedServicios = selectedServicios;
    }

    public Servicio getSelectedServicio() {
        return selectedServicio;
    }

    public void setSelectedServicio(Servicio selectedServicio) {
        this.selectedServicio = selectedServicio;
    }

    public int getNextFirstResult() {
        return firstResult + this.getPageSize();
    }

    public int getPreviousFirstResult() {
        return this.getPageSize() >= firstResult ? 0 : firstResult - this.getPageSize();
    }

    @Override
    public List<Servicio> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        int end = first + pageSize;

        QuerySortOrder order = QuerySortOrder.ASC;
        if (sortOrder == SortOrder.DESCENDING) {
            order = QuerySortOrder.DESC;
        }
        Map<String, Object> _filters = new HashMap<String, Object>();
        /*_filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
         _filters.putAll(filters);*/

        QueryData<Servicio> qData = servicioMedS.find(first, end, sortField, order, _filters);
        this.setRowCount(qData.getTotalResultCount().intValue());
        return qData.getResult();
    }

    @PostConstruct
    public void init() {
        log.info("Setup entityManager into WareHouseService...");
        servicioMedS.setEntityManager(entityManager);
        if (resultList.isEmpty()) {
            Date now = FechasUtil.sumarRestarHorasFecha(new Date(), 12);
            Date now1 = FechasUtil.sumarRestarHorasFecha(new Date(), -12);
            resultList = servicioMedS.getServicioPorFechas(now1, now);
        }
    }

    @Transactional
    public String borrarEntidad() {
        log.info("sgssalud --> ingreso a eliminar: " + getSelectedServicio().getId());
        try {
            if (selectedServicio == null) {
                throw new NullPointerException("Servicio is null");
            }
            if (selectedServicio.isPersistent()) {
                servicioMedS.borrarEntidad(selectedServicio.getId());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  " + selectedServicio.getName(), ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "¡No existe una entidad para ser borrada!", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", e.toString()));
        }
        return "/pages/depSalud/enfermeria/serviciosMedicos/lista.xhtml?faces-redirect=true";
    }

    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage("Servicio Médico" + " " + UI.getMessages("common.selected"), ((Servicio) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Servicio Médico" + " " + UI.getMessages("common.unselected"), ((Servicio) event.getObject()).getName());

        FacesContext.getCurrentInstance().addMessage("", msg);
        this.setSelectedServicio(null);
    }

    @Override
    public Servicio getRowData(String rowKey) {
        return servicioMedS.buscarPorNombre(rowKey);
        //return new Servicio();
    }

    @Override
    public Object getRowKey(Servicio entity) {
        return entity.getName();
    }

    public void buscarPorFechas() {
        if (fechaI != null && fechaF != null) {
            this.setResultList(servicioMedS.getServicioPorFechas(fechaI, fechaF));
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe ingresar las fechas", " ");
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
    }

    public void buscarTodos() {
        this.setResultList(servicioMedS.todosServicios());
    }

    public Date getFechaI() {
        return fechaI;
    }

    public void setFechaI(Date fechaI) {
        this.fechaI = fechaI;
    }

    public Date getFechaF() {
        return fechaF;
    }

    public void setFechaF(Date fechaF) {
        this.fechaF = fechaF;
    }
}
