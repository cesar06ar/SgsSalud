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
package edu.sgssalud.controller.farmacia;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.farmacia.MedicamentoService;
import edu.sgssalud.service.farmacia.RecetaServicio;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.odontologia.consultaOdontologicaServicio;
import edu.sgssalud.util.UI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.security.Identity;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author tania
 */
@Named("recetaHome")
@ViewScoped
public class RecetaHome extends BussinesEntityHome<Receta> implements Serializable {

    private static final long serialVersionUID = 10L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(RecetaHome.class);
    /*Atributos importantes para acceso a la BD ==>*/
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private RecetaServicio recetasServicio;
    @Inject
    private MedicamentoService medicamentosServicio;
    @Inject
    private Identity identity;
    @Inject
    private ConsultaMedicaServicio consultaMedicaServicio;
    @Inject
    private ProfileService profileServicio;
    @Inject
    private FichaMedicaServicio fichaMedicaServicio;
    @Inject
    private consultaOdontologicaServicio consultaOdontServicio;
    private Long pacienteId;
    private Long fichaMedicaId;
    private Long consultaMedicaId;
    private Long consultaOdontId;
    private int unidadesMedicacion;
    private int dosis;
    private String unidadDosis;
    private int tiempoToma;
    private String unidadTiempoToma;
    private int duracionTratamiento;
    private String unidadDuracionTratamiento;
    private String parametroBusqueda;
    private Paciente paciente;
    private Medicamento medicamento;
    private ConsultaMedica consultaMedica;
    private FichaMedica fichaMedica;
    private Medicamento medicamentoSeleccionado;
    private Receta recetaSeleccionada;
    private List<Medicamento> listaMedicamentos = new ArrayList<Medicamento>();;
    private List<Receta> listaRecetas = new ArrayList<Receta>();;
    private List<String> unidadesDosis = new ArrayList<String>();;
    private String presentacion;

    public RecetaHome() {
         
    }

    /*Métodos get y set para obtener el Id de la clase*/
    public Long getRecetaId() {
        return (Long) getId();

    }

    public void setRecetaId(Long recetaId) {
        setId(recetaId);
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public ConsultaMedica getConsultaMedica() {
        return consultaMedica;
    }

    public void setConsultaMedica(ConsultaMedica consultaMedica) {
        this.consultaMedica = consultaMedica;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public FichaMedica getFichaMedica() {
        return fichaMedica;
    }

    public void setFichaMedica(FichaMedica fichaMedica) {
        this.fichaMedica = fichaMedica;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Long getFichaMedicaId() {
        return fichaMedicaId;
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        this.fichaMedicaId = fichaMedicaId;
        if (fichaMedicaId != null) {
            this.setFichaMedica(fichaMedicaServicio.getFichaMedicaPorId(fichaMedicaId));
            this.setPaciente(fichaMedica.getPaciente());
        }
    }

    public Long getConsultaMedicaId() {
        return consultaMedicaId;
    }

    public void setConsultaMedicaId(Long consultaMedicaId) {
        this.consultaMedicaId = consultaMedicaId;
        if (consultaMedicaId != null) {
            this.setConsultaMedica(consultaMedicaServicio.getConsultaMedicaPorId(consultaMedicaId));
        }
    }

    public Long getConsultaOdontId() {
        return consultaOdontId;
    }

    public void setConsultaOdontId(Long consultaOdontId) {
        this.consultaOdontId = consultaOdontId;
        if (consultaOdontId != null) {
            getInstance().setConsultaOdontologica(consultaOdontServicio.getPorId(consultaOdontId));
        }
    }

    public String getParametroBusqueda() {
        return parametroBusqueda;
    }

    public void setParametroBusqueda(String parametroBusqueda) {
        this.parametroBusqueda = parametroBusqueda;
    }

    public List<Receta> getListaRecetas() {
        return listaRecetas;
    }

    public void setListaRecetas(List<Receta> listaRecetas) {
        this.listaRecetas = listaRecetas;
    }

    public Receta getRecetaSeleccionada() {
        return recetaSeleccionada;
    }

    public void setRecetaSeleccionada(Receta recetaSeleccionada) {
        this.recetaSeleccionada = recetaSeleccionada;
    }

    public Medicamento getMedicamentoSeleccionado() {
        return medicamentoSeleccionado;
    }

    public void setMedicamentoSeleccionado(Medicamento medicamentoSeleccionado) {
        this.medicamentoSeleccionado = medicamentoSeleccionado;
        this.setPresentacion(medicamentoSeleccionado.getPresentacion());        
        this.cargarUnidadesDosis();
                
    }

    public List<Medicamento> getListaMedicamentos() {
        return medicamentosServicio.buscarTodos(); //controlar los medicamentos en stock
    }

    public void setListaMedicamentos(List<Medicamento> listaMedicamentos) {
        this.listaMedicamentos = listaMedicamentos;
    }

    public int getUnidadesMedicacion() {
        return unidadesMedicacion;
    }

    public void setUnidadesMedicacion(int unidadesMedicacion) {
        this.unidadesMedicacion = unidadesMedicacion;
    }

    public int getDosis() {
        return dosis;
    }

    public void setDosis(int dosis) {
        this.dosis = dosis;
    }

    public String getUnidadDosis() {
        return unidadDosis;
    }

    public void setUnidadDosis(String unidadDosis) {
        this.unidadDosis = unidadDosis;
    }

    public int getTiempoToma() {
        return tiempoToma;
    }

    public void setTiempoToma(int tiempoToma) {
        this.tiempoToma = tiempoToma;
    }

    public String getUnidadTiempoToma() {
        return unidadTiempoToma;
    }

    public void setUnidadTiempoToma(String unidadTiempoToma) {
        this.unidadTiempoToma = unidadTiempoToma;
    }

    public int getDuracionTratamiento() {
        return duracionTratamiento;
    }

    public void setDuracionTratamiento(int duracionTratamiento) {
        this.duracionTratamiento = duracionTratamiento;
    }

    public String getUnidadDuracionTratamiento() {
        return unidadDuracionTratamiento;
    }

    public void setUnidadDuracionTratamiento(String unidadDuracionTratamiento) {
        this.unidadDuracionTratamiento = unidadDuracionTratamiento;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    @TransactionAttribute   //    
    public Receta load() {
        if (isIdDefined()) {
            wire();
        }
        log.info("sgssalud --> cargar instance " + getInstance());
        return getInstance();
    }

    /*Metodo que retorna una instancia de la clase (Receta) cuando ya esta creada==>*/
    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    /*Metodo importante para actualizar EntityManager y tener acceso a la DB==>*/
    @PostConstruct
    public void init() {
        setEntityManager(em);
        bussinesEntityService.setEntityManager(em);
        fichaMedicaServicio.setEntityManager(em);
        recetasServicio.setEntityManager(em);
        medicamentosServicio.setEntityManager(em);
        consultaMedicaServicio.setEntityManager(em);
        profileServicio.setEntityManager(em);
        cargarUnidadesDosis();
        if (pacienteId == null) {
            paciente = new Paciente();
        }
        if (consultaMedicaId != null) {
            consultaMedica = new ConsultaMedica();
        }
        
    }

    @Override
    protected Receta createInstance() {
        //prellenado estable para cualquier clase 
        Date now = Calendar.getInstance().getTime();
        Receta receta = new Receta();
        receta.setFecha(now);
        receta.setResponsableEmision(profileServicio.getProfileByIdentityKey(identity.getUser().getKey()));
        receta.setResponsableEntrega(profileServicio.getProfileByIdentityKey(identity.getUser().getKey()));
        return receta;

    }

    @Override
    public Class<Receta> getEntityClass() {
        return Receta.class;
    }

    @TransactionAttribute
    public String guardarReceta() {
        Date now = Calendar.getInstance().getTime();
//        getInstance().setLastUpdate(now);
        if (getInstance().isPersistent()) {
            save(getInstance());
        } else {
            create(getInstance());
            save(getInstance());
            FacesMessage msg = new FacesMessage("Se envió la receta: " + getInstance().getId() + " con éxito");
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return "/pages/farmacia/receta/lista.xhtml?faces-redirect=true";
    }

    public void cargarIndicacion() {
    }

    public void cargarUnidadesDosis() {             
        unidadesDosis = new ArrayList<String>();
        if ("Ampollas".equals(presentacion)) {
            unidadesDosis.add("Ampolla/s");            
        } else if ("Pastillas".equals(presentacion)) {
            unidadesDosis.add("Pastilla/s");            
        } else if ("Sobres".equals(presentacion)) {
            unidadesDosis.add("Sobre/s");            
        } else if ("Jarabe".equals(presentacion)) {
            unidadesDosis.add("Cucharada");            
            unidadesDosis.add("Cucharadita");
            unidadesDosis.add("ml");
            unidadesDosis.add("cm3");            
        } else if ("Crema".equals(presentacion)) {
            unidadesDosis.add("Aplicacion");            
        } else if ("Pomada".equals(presentacion)) {
            unidadesDosis.add("Aplicacion");     
        }
        
    }

    public List<String> getUnidadesDosis() {
        return unidadesDosis;
    }

    public void setUnidadesDosis(List<String> unidadesDosis) {
        this.unidadesDosis = unidadesDosis;
    }    
    
}
