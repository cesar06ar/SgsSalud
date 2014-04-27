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
import edu.sgssalud.model.farmacia.Receta_Medicamento;
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.farmacia.MedicamentoService;
import edu.sgssalud.service.farmacia.RecetaMedicamentoService;
import edu.sgssalud.service.farmacia.RecetaServicio;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.odontologia.ConsultaOdontologicaServicio;
import edu.sgssalud.util.Lists;
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
    private ConsultaOdontologicaServicio consultaOdontServicio;
    @Inject
    private RecetaMedicamentoService recetaMedicamentoServicio;

    private Long consultaMedicaId;
    private Long consultaOdontId;
    private Long recetaId;
    private Long pacienteId;
    private int unidadesMedicacion;
    private String nombreMedic;
    private String indicacion;
    private Paciente paciente;
    private Medicamento medicamento;
    private FichaMedica fichaMedica;
    private ConsultaMedica consultaMed;
    private ConsultaOdontologica consultaOdont;
    private Medicamento medicamentoSeleccionado;
    private List<Medicamento> listaMedicamentosStock = new ArrayList<Medicamento>();
    private List<Medicamento> listaMedicamentosFiltrados = new ArrayList<Medicamento>();
    private List<Receta_Medicamento> listaRecetaMed = new ArrayList<Receta_Medicamento>();
    private List<String> listaIndicaciones = new ArrayList<String>();
    //private List<Receta> listaRecetas = new ArrayList<Receta>();
    private List<String> listaMedicaciones = new ArrayList<String>();

    public RecetaHome() {
    }

    /*Métodos get y set para obtener el Id de la clase*/
    public Long getRecetaId() {
        return recetaId;
    }

    public void setRecetaId(Long recetaId) {
        this.recetaId = recetaId;
        setId(recetaId);
        if (recetaId != null) {
            this.setInstance(recetasServicio.buscarRecetaPorId(recetaId));
        }
        //System.out.println("INIT RECETA:_____1" + getInstance().toString());
        if (getInstance().isPersistent()) {
            this.listaRecetaMed = getInstance().getListaRecetaMedicamento();
            this.paciente = getInstance().getPaciente();
            this.listaIndicaciones = Lists.stringToList(getInstance().getIndicaciones());
            this.listaMedicaciones = Lists.stringToList(getInstance().getMedicaciones());
        }
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
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

    public Long getConsultaMedicaId() {
        return consultaMedicaId;
    }

    public void setConsultaMedicaId(Long consultaMedicaId) {
        this.consultaMedicaId = consultaMedicaId;
        if (consultaMedicaId != null) {
            this.setConsultaMed(consultaMedicaServicio.getConsultaMedicaPorId(consultaMedicaId));
            this.setFichaMedica(consultaMed.getHistoriaClinica().getFichaMedica());
            this.setPaciente(getFichaMedica().getPaciente());
        }
    }

    public Long getConsultaOdontId() {
        return consultaOdontId;
    }

    public void setConsultaOdontId(Long consultaOdontId) {
        this.consultaOdontId = consultaOdontId;
        if (consultaOdontId != null) {
            this.setConsultaOdont(consultaOdontServicio.getPorId(consultaOdontId));
            this.setFichaMedica(consultaOdont.getFichaOdontologica().getFichaMedica());
            this.setPaciente(getFichaMedica().getPaciente());
        }
    }

    public ConsultaMedica getConsultaMed() {
        return consultaMed;
    }

    public void setConsultaMed(ConsultaMedica consultaMed) {
        this.consultaMed = consultaMed;
    }

    public ConsultaOdontologica getConsultaOdont() {
        return consultaOdont;
    }

    public void setConsultaOdont(ConsultaOdontologica consultaOdont) {
        this.consultaOdont = consultaOdont;
    }

    public Medicamento getMedicamentoSeleccionado() {
        return medicamentoSeleccionado;
    }

    public void setMedicamentoSeleccionado(Medicamento medicamentoSeleccionado) {
        this.medicamentoSeleccionado = medicamentoSeleccionado;
    }

    public List<Medicamento> getListaMedicamentosStock() {
        return medicamentosServicio.buscarPorStockDisponible(); //controlar los medicamentos en stock
    }

    public void setListaMedicamentosStock(List<Medicamento> listaMedicamentosStock) {
        this.listaMedicamentosStock = listaMedicamentosStock;
    }

    public List<Receta_Medicamento> getListaRecetaMed() {
        return listaRecetaMed;
    }

    public void setListaRecetaMed(List<Receta_Medicamento> listaRecetaMed) {
        this.listaRecetaMed = listaRecetaMed;
    }

    public List<String> getListaIndicaciones() {
        return listaIndicaciones;
    }

    public void setListaIndicaciones(List<String> listaIndicaciones) {
        this.listaIndicaciones = listaIndicaciones;
    }

    public int getUnidadesMedicacion() {
        return unidadesMedicacion;
    }

    public void setUnidadesMedicacion(int unidadesMedicacion) {
        this.unidadesMedicacion = unidadesMedicacion;
    }

    public String getNombreMedic() {
        return nombreMedic;
    }

    public void setNombreMedic(String nombreMedic) {
        this.nombreMedic = nombreMedic;
    }

    public String getIndicacion() {
        return indicacion;
    }

    public void setIndicacion(String indicacion) {
        this.indicacion = indicacion;
    }

    public List<String> getListaMedicaciones() {
        return listaMedicaciones;
    }

    public void setListaMedicaciones(List<String> listaMedicaciones) {
        this.listaMedicaciones = listaMedicaciones;
    }

    public List<Medicamento> getListaMedicamentosFiltrados() {
        return listaMedicamentosFiltrados;
    }

    public void setListaMedicamentosFiltrados(List<Medicamento> listaMedicamentosFiltrados) {
        this.listaMedicamentosFiltrados = listaMedicamentosFiltrados;
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        bussinesEntityService.setEntityManager(em);
        fichaMedicaServicio.setEntityManager(em);
        consultaMedicaServicio.setEntityManager(em);
        consultaOdontServicio.setEntityManager(em);
        profileServicio.setEntityManager(em);
        recetasServicio.setEntityManager(em);
        medicamentosServicio.setEntityManager(em);
        recetaMedicamentoServicio.setEntityManager(em);

        if (pacienteId == null) {
            paciente = new Paciente();
        }

        if (consultaMedicaId == null) {
            consultaMed = new ConsultaMedica();
        }

        if (consultaOdontId == null) {
            consultaOdont = new ConsultaOdontologica();
        }

        this.nombreMedic = null;
        this.indicacion = null;
        this.numReceta();
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
    @Override
    protected Receta createInstance() {
        //prellenado estable para cualquier clase 
        Date now = Calendar.getInstance().getTime();
        Receta receta = new Receta();
        //receta.setConsultaMedica(null);
        //receta.setConsultaOdontologica(null);
        receta.setFechaEmision(now);
        receta.setResponsableEmision(profileServicio.getProfileByIdentityKey(identity.getUser().getKey()));
        return receta;

    }

    @Override
    public Class<Receta> getEntityClass() {
        return Receta.class;
    }

    @TransactionAttribute
    public String guardarReceta() {
        Date now = Calendar.getInstance().getTime();
        System.out.println("Ingreso a Guardar _____");
        String salida = "/pages/depSalud" + getBackView() + "?faces-redirect=true";
        try {
            if (getInstance().isPersistent()) {

                for (Medicamento m : listaMedicamentosStock) {
                    for (Receta_Medicamento rm : getInstance().getListaRecetaMedicamento()) {
                        if (m.equals(rm.getMedicamento())) {
                            int c = m.getUnidades();
                            m.setUnidades(c - rm.getCantidad());
                            save(m);
                        }
                    }
                }
                save(getInstance());
            } else {                
                if (consultaMed.isPersistent()) {
                    getInstance().setConsultaMedica(consultaMed);

                } else if (consultaOdont.isPersistent()) {
                    getInstance().setConsultaOdontologica(consultaOdont);
                }
                System.out.println("Guardar _____");
                if ( paciente.isPersistent()) {

                    getInstance().setFechaEmision(now);
                    getInstance().setEstado("Emitida");

                    getInstance().setPaciente(paciente);
                    getInstance().setIndicaciones(Lists.listToString(listaIndicaciones));
                    getInstance().setMedicaciones(Lists.listToString(listaMedicaciones));

                    for (Receta_Medicamento recetaMed : listaRecetaMed) {
                        int cantidad = recetaMed.getMedicamento().getUnidades() - recetaMed.getCantidad();
                        Medicamento medicament = recetaMed.getMedicamento();
                        medicament.setUnidades(cantidad);
                        recetaMed.setSaldo(cantidad);
                        recetaMed.setFecha(now);
                        save(medicament);
                    }
                    getInstance().setListaRecetaMedicamento(listaRecetaMed);
                    create(getInstance());
                    //getInstance().setResponsableEmision(profileServicio.getProfileByIdentityKey(identity.getUser().getKey()));
                    save(getInstance());
                    System.out.println("Guardo RECETA CON EXITO_____" + getInstance().toString());
                    FacesMessage msg = new FacesMessage("Se envió la receta: " + getInstance().getId() + " con éxito");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                    salida += "&consultaMedicaId= " + consultaMedicaId
                            + "&fichaMedicaId=" + getFichaMedica().getId()
                            + "&consultaOdontId=" + consultaOdontId
                            + "&backView=" + this.getPrevious();
                            
                } else {
                    FacesMessage msg = new FacesMessage("Debe cargar una consulta Primero");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage("Error al guardar la receta");
            FacesContext.getCurrentInstance().addMessage("", msg);
        }

        return salida;
    }

    public void cargarMedicamento() {
        Medicamento med = getMedicamentoSeleccionado();
        String name = med.getNombreComercial() + " (" + med.getUnidades() + " " + med.getPresentacion() + ") ";
        this.setNombreMedic(name);
        System.out.println("cargar Medicamento____:" + nombreMedic);
    }

    public void cargarMedicamentoAReceta() {

        //this.actualizarStockMedicamento(unidadesMedicacion);
        System.out.println("VALORES DE ENTRADA " + getNombreMedic() + getIndicacion() + unidadesMedicacion);
        if (!getNombreMedic().isEmpty() && !getIndicacion().isEmpty()) {
            if (unidadesMedicacion > 0 && unidadesMedicacion <= this.getMedicamentoSeleccionado().getUnidades()) {

                Receta_Medicamento recetaMed = new Receta_Medicamento();
                recetaMed.setCantidad(unidadesMedicacion);
                if (medicamentoSeleccionado.isPersistent()) {  //controla si esa receta tiene o no medicamentos de farmacia                
                    //System.out.println("MEdicameto _ " + medicamentoSeleccionado.toString());
                    //if (this.noContieneMedicamento(medicamentoSeleccionado)) {                    
                    if (!this.contieneMedicamento(nombreMedic)) {
                        recetaMed.setMedicamento(medicamentoSeleccionado);
                        String med = nombreMedic.toUpperCase() + " # " + this.getUnidadesMedicacion() + "\n";
                        this.getListaRecetaMed().add(recetaMed);

                        this.getListaMedicaciones().add(med);
                        this.getListaIndicaciones().add(indicacion + "\n");
                        System.out.println(med);
                        System.out.println(indicacion);
                    } else {
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "El medicamento ya ha sido agregado", "");
                        FacesContext.getCurrentInstance().addMessage("", msg);
                    }
                }
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "La cantidad debe ser mayor a 0", "<br/> o menor a la cantidad disponible");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
            //TODO____renderizar para verificar si tiene o no medicamentos en la farmacia            
            this.reiniciar();
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe llenar todos los datos", "");
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
    }

    public void reiniciar() {
        this.setMedicamento(null);
        this.setNombreMedic(null);
        this.setUnidadesMedicacion(0);
        this.setIndicacion(null);
    }

    public void onRowSelect(SelectEvent event) {
//        FacesMessage msg = new FacesMessage("Car Selected", ((Car) event.getObject()).getModel());    
//        FacesContext.getCurrentInstance().addMessage(null, msg);    " /*" + med.getNombreGenerico() +
        this.setNombreMedic(null);
        Medicamento med = getMedicamentoSeleccionado();
        String name = "(" + med.getNombreComercial() + ", " + med.getNombreGenerico() + " ) " + med.getPresentacion();
        this.setNombreMedic(name);
        this.setIndicacion(med.getNombreComercial().toUpperCase() + ": ");
        System.out.println("cargar Medicamento____:" + nombreMedic);
    }

    public void onRowUnselect(UnselectEvent event) {
//        FacesMessage msg = new FacesMessage("Car Unselected", ((Car) event.getObject()).getModel());  
//        FacesContext.getCurrentInstance().addMessage(null, msg);  
        this.setNombreMedic(null);
        this.setIndicacion(null);
    }

    public boolean contieneMedicamento(String nombreMed) {
        System.out.println("INGRESO a verificar MEDICAMENTO:_________" + nombreMed);
        if (!this.getListaMedicaciones().isEmpty()) {
            System.out.println("Lista de medicaciones:_________" + this.getListaMedicaciones());
            for (String s : this.getListaMedicaciones()) {
                if (s.contains(nombreMed)) {
                    //System.out.println("Medicamento verificado:_________");
                    return true;
                }
            }
        }
        return false;
    }

    public void numReceta() {
        long valor = recetasServicio.getGenerarNumeroFicha();
        if (valor < 99) {
            getInstance().setNumvalue("0000" + valor);
        } else if (valor < 999) {
            getInstance().setNumvalue("000" + valor);
        } else if (valor < 9999) {
            getInstance().setNumvalue("00" + valor);
        } else if (valor < 99999) {
            getInstance().setNumvalue("0" + valor);
        } else if (valor < 999999) {
            getInstance().setNumvalue("" + valor);
        }
        getInstance().setNumero(valor);
    }
}
