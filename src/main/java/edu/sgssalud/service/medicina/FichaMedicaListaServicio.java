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

import edu.sgssalud.Sgssalud;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.security.authorization.SecurityRules;
import edu.sgssalud.service.SettingService;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;
import edu.sgssalud.util.UI;
import edu.sgssalud.web.service.WebServiceSGAClientConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
import org.jboss.solder.logging.Logger;
import org.primefaces.context.RequestContext;
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
public class FichaMedicaListaServicio extends LazyDataModel<FichaMedica> {

    private static final long serialVersionUID = 5L;
    private static final int MAX_RESULTS = 5;
    private static Logger log = Logger.getLogger(FichaMedicaListaServicio.class);

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private FichaMedicaServicio fms;
    @Inject
    private Identity identity;
    private List<FichaMedica> resultList;
    private int primerResult = 0;
    private FichaMedica[] fichaMedicSeleccionadas;
    private FichaMedica fichaMedicSeleccionada;
    private String parametroBusqueda;
    private Date fechaI;
    private Date fechaF;
    private WebServiceSGAClientConnection coneccionSGA = new WebServiceSGAClientConnection();

    /*Método para inicializar tabla*/
    public FichaMedicaListaServicio() {
        setPageSize(MAX_RESULTS);
        resultList = new ArrayList<FichaMedica>();

//        fechaI = now;
//        fechaF = now;
    }

    @PostConstruct
    public void init() {
        fms.setEntityManager(em);
        if (resultList.isEmpty()) {
            Date now = Calendar.getInstance().getTime();
            resultList = fms.getFichaMedicaPorFechas(now, now);
            Collections.sort(resultList);
            //resultList = fms.getFichasMedicas();
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
        return entity.getId();
    }

    @Override
    public FichaMedica getRowData(String rowKey) {
        return fms.getFichaMedicaPorId(Long.parseLong(rowKey));
    }

    public void onRowSelect(SelectEvent event) {
        fichaMedicSeleccionada = (FichaMedica) event.getObject();
        FacesMessage msg = new FacesMessage(UI.getMessages("FichaMedica") + " " + UI.getMessages("common.selected"), ((FichaMedica) event.getObject()).getNumeroFicha().toString());
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("FichaMedica") + " " + UI.getMessages("common.unselected"), ((FichaMedica) event.getObject()).getNumeroFicha().toString());
        FacesContext.getCurrentInstance().addMessage("", msg);
        this.setFichaMedicSeleccionada(null);
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
//       if (resultList.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
//            resultList = fms.getFichasMedicas(this.getPageSize(), primerResult);
//        }        
        return resultList;
    }

    public void setResultList(List<FichaMedica> resultList) {
        this.resultList = resultList;
    }

    public FichaMedica getFichaMedicSeleccionada() {
        return fichaMedicSeleccionada;
    }

    public void setFichaMedicSeleccionada(FichaMedica fichaMedicSeleccionada) {
        this.fichaMedicSeleccionada = fichaMedicSeleccionada;
    }

    public String getParametroBusqueda() {
        return parametroBusqueda;
    }

    public void setParametroBusqueda(String parametroBusqueda) {
        this.parametroBusqueda = parametroBusqueda;
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

    public FichaMedica[] getFichaMedicSeleccionadas() {
        return fichaMedicSeleccionadas;
    }

    public void setFichaMedicSeleccionadas(FichaMedica[] fichaMedicSeleccionadas) {
        this.fichaMedicSeleccionadas = fichaMedicSeleccionadas;
    }

    public void buscarFichaMedicaPorFechas() {
        if (fechaI != null && fechaF != null) {
            this.setResultList(fms.getFichaMedicaPorFechas(fechaI, fechaF));
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe ingresar las fechas", " ");
            FacesContext.getCurrentInstance().addMessage("", msg);
        }

    }

    public void buscarFichaMedicaInit() {

        //resultList = fms.getFichasMedicas();
    }

    public void buscarTodos() {
        this.setResultList(fms.getFichasMedicas());
        fechaI = null;
        fechaF = null;
    }

    public String nuevaConsulta() {
        SecurityRules sr = new SecurityRules();
        String salida = null;
        if (fichaMedicSeleccionada.isPersistent()) {
            if ("Universitario".equals(fichaMedicSeleccionada.getPaciente().getTipoEstudiante())) {
                boolean matriculado = coneccionSGA.getEstudianteMatriculado_WS_SGA(fichaMedicSeleccionada.getPaciente().getCedula());
                if (matriculado) {
                    if (sr.isEnfermero(identity)) {
                        salida = salidaVista(1);
                    } else if (sr.isMedico(identity)) {
                        salida = salidaVista(2);
                    } else if (sr.isOdontologo(identity)) {
                        salida = salidaVista(3);
                    }
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "El paciente no puede acceder a los servicios ya que actualmente no esta matriculado", " ");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    RequestContext.getCurrentInstance().update(":form:tablaFichasMedicas :form:print_f");
                    resultList = fms.getFichasMedicas();
                    this.setFichaMedicSeleccionada(null);
                }

            } else {
                if (sr.isEnfermero(identity)) {
                    salida = salidaVista(1);
                } else if (sr.isMedico(identity)) {
                    salida = salidaVista(2);
                } else if (sr.isOdontologo(identity)) {
                    salida = salidaVista(3);
                }
            }

        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione una Ficha Médica", " ");
            FacesContext.getCurrentInstance().addMessage(null, msg);

        }
        return salida;
    }

    public String salidaVista(int s) {
        if (s == 1) {
            return "/pages/depSalud/enfermeria/signosVitales.xhtml?faces-redirect=true"
                    + "&fichaMedicaId=" + fichaMedicSeleccionada.getId()
                    + "&backView=fichaMedica";
        } else if (s == 2) {
            return "/pages/depSalud/medicina/consultaMedica.xhtml?faces-redirect=true"
                    + "&fichaMedicaId=" + fichaMedicSeleccionada.getId()
                    + "&backView=/fichaMedica";
        } else if (s == 3) {
            return "/pages/depSalud/odontologia/consultaOdontologica.xhtml?faces-redirect=true"
                    + "&fichaMedicaId=" + fichaMedicSeleccionada.getId()
                    + "&backView=/fichaMedica";
        } else {
            return null;
        }
    }
}
