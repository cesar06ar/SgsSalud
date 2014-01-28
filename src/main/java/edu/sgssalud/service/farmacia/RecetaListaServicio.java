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
package edu.sgssalud.service.farmacia;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.util.Lists;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
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
 * @author tania
 */
@Named(value = "recetaListaServicio")
@ViewScoped
public class RecetaListaServicio extends LazyDataModel<Receta> {

    private static final long serialVersionUID = 13L;
    private static final int MAX_RESULTS = 13;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(RecetaListaServicio.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private RecetaServicio recetaServicio;    
    @Inject
    private Identity identity;   
    @Inject
    private ProfileService profileServicio;
    private List<Receta> resultList;
    private int primerResult = 0;
    private Receta[] recetasSeleccionados;
    private Receta recetaSeleccionada;
    private String parametroBusqueda;
    private List<String> listaIndicaciones = new ArrayList<String>();
    private List<String> listaMedicaciones = new ArrayList<String>();

    public RecetaListaServicio() {
        setPageSize(MAX_RESULTS);
        resultList = new ArrayList<Receta>();
    }

    public List<Receta> getResultList() {
        if (resultList.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
            resultList = recetaServicio.obtenerRecetas(this.getPageSize(), primerResult);
        }
        return resultList;
    }

    public void setResultList(List<Receta> resultList) {
        this.resultList = resultList;
    }

    public int getPrimerResult() {
        return primerResult;
    }

    public void setPrimerResult(int primerResult) {
        this.primerResult = primerResult;
        this.resultList = null;
    }

    public Receta[] getRecetasSeleccionados() {
        return recetasSeleccionados;
    }

    public void setRecetasSeleccionados(Receta[] recetasSeleccionados) {
        this.recetasSeleccionados = recetasSeleccionados;
    }

    public Receta getRecetaSeleccionada() {
        return recetaSeleccionada;
    }

    public void setRecetaSeleccionada(Receta recetaSeleccionada) {
        this.recetaSeleccionada = recetaSeleccionada;
    }

    public int obtenerSiguienteResultado() {
        return primerResult + this.getPageSize();
    }

    public int obtenerRecetaAnterior() {
        return this.getPageSize() >= primerResult ? 0 : primerResult - this.getPageSize();
    }

    public String getParametroBusqueda() {
        return parametroBusqueda;
    }

    public void setParametroBusqueda(String parametroBusqueda) {
        this.parametroBusqueda = parametroBusqueda;
//        this.setResultList(recetaServicio.BuscarRecetasPorParametro(parametroBusqueda));                       
    }   
    
//   @Override
//    public List<Receta> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
//        int end = first + pageSize;
//
//        QuerySortOrder order = QuerySortOrder.ASC;
//        if (sortOrder == SortOrder.DESCENDING) {
//            order = QuerySortOrder.DESC;
//        }
//        Map<String, Object> _filters = new HashMap<String, Object>();
//        /*_filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
//         _filters.putAll(filters);*/
//
//        QueryData<Receta> qData = recetaServicio.find(first, end, sortField, order, _filters);
//        this.setRowCount(qData.getTotalResultCount().intValue());
//        this.setResultList(qData.getResult());        
//        
//        return qData.getResult();        
//    }

    public List<String> getListaIndicaciones() {
        return listaIndicaciones;
    }

    public void setListaIndicaciones(List<String> listaIndicaciones) {
        this.listaIndicaciones = listaIndicaciones;
    }

    public List<String> getListaMedicaciones() {
        return listaMedicaciones;
    }

    public void setListaMedicaciones(List<String> listaMedicaciones) {
        this.listaMedicaciones = listaMedicaciones;
    }

   @Override
    public List<Receta> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        int end = first + pageSize;

        QuerySortOrder order = QuerySortOrder.ASC;
        if (sortOrder == SortOrder.DESCENDING) {
            order = QuerySortOrder.DESC;
        }
        Map<String, Object> _filters = new HashMap<String, Object>();
        /*_filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
         _filters.putAll(filters);*/

        QueryData<Receta> qData = recetaServicio.find(first, end, sortField, order, _filters);
        this.setRowCount(qData.getTotalResultCount().intValue());
        this.setResultList(qData.getResult());                
        return resultList;        
    }
    
    @PostConstruct
    public void init() {
        recetaServicio.setEntityManager(em);
        profileServicio.setEntityManager(em);
        if (resultList.isEmpty()) {
            resultList = recetaServicio.obtenerRecetas(this.getPageSize(), primerResult);
        }
    }

    @Override
    public Receta getRowData(String id) {
        return recetaServicio.buscarRecetaPorId(Long.parseLong(id));
    }

    @Override
    public Object getRowKey(Receta entity) {
        return entity.getPaciente().getNombres();
    }

    public void onRowSelect(SelectEvent event) {
        this.listaMedicaciones = Lists.stringToList(((Receta) event.getObject()).getMedicaciones());
        this.listaIndicaciones = Lists.stringToList(((Receta) event.getObject()).getIndicaciones());
        FacesMessage msg = new FacesMessage(UI.getMessages("Receta") + " " + UI.getMessages("common.selected"), ((Receta) event.getObject()).getPaciente().getNombres());
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        this.listaMedicaciones = null;
        this.listaIndicaciones = null;
        FacesMessage msg = new FacesMessage(UI.getMessages("Medicamento") + " " + UI.getMessages("common.unselected"), ((Receta) event.getObject()).getPaciente().getNombres());
        FacesContext.getCurrentInstance().addMessage("", msg);
        this.setRecetaSeleccionada(null);
    }

    public void buscarPorParametro() {
        this.setResultList(recetaServicio.BuscarRecetasPorParametro(parametroBusqueda));
    }
    
    public void entregarReceta(){
        
        System.out.println("Actualizado correctamente :________________ ");
        Date now = Calendar.getInstance().getTime();
        this.recetaSeleccionada.setEstado("Entregada");
        this.recetaSeleccionada.setFechaEntrega(now);
        this.recetaSeleccionada.setResponsableEntrega(profileServicio.getProfileByIdentityKey(identity.getUser().getKey()));        
        
        if (recetaSeleccionada.isPersistent()) {
        //    em.getTransaction().begin();
            em.merge(recetaSeleccionada);
          //  em.getTransaction().commit();
        }        
        FacesMessage msg = new FacesMessage(UI.getMessages("El Medicamento") + " ha sido entregado" , "");
        FacesContext.getCurrentInstance().addMessage("", msg);
    }
}
