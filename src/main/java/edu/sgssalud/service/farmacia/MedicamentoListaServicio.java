package edu.sgssalud.service.farmacia;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.SettingListService;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
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
import org.jboss.solder.logging.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author tania
 */

@Named(value="medicamentoListaServicio")
@ViewScoped
public class MedicamentoListaServicio extends LazyDataModel<Medicamento> {

    private static final long serialVersionUID = 5L;
    private static final int MAX_RESULTS = 5;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(SettingListService.class);
    
    @Inject
    @Web
    private EntityManager em;    
    @Inject
    private MedicamentoService medicamentoService;
    private List<Medicamento> resultList;
    private int primerResult =0;
    private Medicamento[] medicamentosSeleccionados;
    private Medicamento medicamentoSeleccionado;

    public MedicamentoListaServicio() {
        setPageSize(MAX_RESULTS);
        resultList = new ArrayList<Medicamento>();
    }

    public List<Medicamento> getResultList() {
        if (resultList.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
            resultList = medicamentoService.obtenerMedicamentos(this.getPageSize(), primerResult);
        }
        return resultList;
    }

    public void setResulList(List<Medicamento> resulList) {
        this.resultList = resulList;
    }

    public int getPrimerResul() {
        return primerResult;
    }

    public void setPrimerResul(int primerResul) {
        this.primerResult = primerResul;
        this.resultList = null;
    }
    
    public int obtenerSiguienteResultado(){
        return primerResult + this.getPageSize();
    }
    
    public int obtenerMedicamentoAnterior(){
        return this.getPageSize() >= primerResult ? 0 : primerResult - this.getPageSize();
    }

    public Medicamento[] getMedicamentosSeleccionados() {
        return medicamentosSeleccionados;
    }

    public void setMedicamentosSeleccionados(Medicamento[] medicamentosSeleccionados) {
        this.medicamentosSeleccionados = medicamentosSeleccionados;
    }

    public Medicamento getMedicamentoSeleccionado() {
        return medicamentoSeleccionado;
    }

    public void setMedicamentoSeleccionado(Medicamento medicamentoSeleccionado) {
        this.medicamentoSeleccionado = medicamentoSeleccionado;
    }    
    
    @Override
    public List<Medicamento> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> map) {
        int end = first + pageSize;

        QuerySortOrder order = QuerySortOrder.ASC;
        if (sortOrder == SortOrder.DESCENDING) {
            order = QuerySortOrder.DESC;
        }
        Map<String, Object> _filters = new HashMap<String, Object>();
        /*_filters.put(BussinesEntity_.type.getName(), getType()); //Filtro por defecto
         _filters.putAll(filters);*/

        QueryData<Medicamento> qData = medicamentoService.find(first, end, sortField, order, _filters);
        this.setRowCount(qData.getTotalResultCount().intValue());
        return qData.getResult();
    }
    
    @PostConstruct
    public void init() {
        medicamentoService.setEntityManager(em);
    }
    
    @Override
    public Medicamento getRowData(String nombre) {
        return medicamentoService.buscarPorNombreMedicamento(nombre);
    }

    @Override
    public Object getRowKey(Medicamento entity) {
        return entity.getName();
    }
    
    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("medicamento") + " " + UI.getMessages("common.selected"), ((Setting) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("medicamento") + " " + UI.getMessages("common.unselected"), ((Setting) event.getObject()).getName());

        FacesContext.getCurrentInstance().addMessage(null, msg);
        this.setMedicamentoSeleccionado(null);
    }
}

