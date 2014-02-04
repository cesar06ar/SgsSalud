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

import edu.sgssalud.cdi.Current;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.controller.paciente.PacienteHome;
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.farmacia.MedicamentoService;
import edu.sgssalud.service.farmacia.RecetaServicio;
import edu.sgssalud.util.Dates;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.picketlink.idm.common.exception.IdentityException;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author tania
 */
@Named("medicamentoHome")
@ViewScoped
public class MedicamentoHome extends BussinesEntityHome<Medicamento> implements Serializable {

    private static final long serialVersionUID = 7L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(MedicamentoHome.class);
    /*Atributos importantes para acceso a la BD ==>*/
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private MedicamentoService ms;
    @Inject
    private RecetaServicio rs;
    
    private List<Receta> listaRecetas;
    private Receta recetaSeleccionada;
    private List<Medicamento> listaMedicamentos;
    private Medicamento medicamentoSeleccionado;
    
    
     public MedicamentoHome() {
        listaRecetas = new ArrayList<Receta>();
        listaMedicamentos = new ArrayList<Medicamento>();
     
     }
     
    /*Métodos get y set para obtener el Id de la clase*/
    public Long getMedicamentoId() {
        return (Long) getId();
    }

    public void setMedicamentoId(Long medicamentoId) {
        setId(medicamentoId);
    }
    
    public Long getRecetaId() {
        return (Long) getId();
    }

    public void setRecetaId(Long recetaId) {
        setId(recetaId);
    }
    
    public List<Medicamento> getlistarTodos() {
        return ms.buscarTodos();
    }

    public List<Medicamento> getListaMedicamentos() {
        return listaMedicamentos;

    }

    public void setListaMedicamentos(List<Medicamento> listaMedicamentos) {
        this.listaMedicamentos = listaMedicamentos;
    }

//    public List<Receta> getRecetas() {
//        List<Receta> lr = RecetaServicio.buscarTodos();
//        Collections.sort(lr, new Comparator() {
//            @Override
//            public int compare(Object rc1, Object rc2) {
//                Receta r1 = (Receta) rc1;
//                Receta r2 = (Receta) rc2;
//                return r1.getNombre().compareToIgnoreCase(r2.getNombre());
//                //return e1.getCategoria()
//            }
//        });
//        return lr;
//    }

    /*Método para  cargar una instancia de medicamento==>*/
//    @Produces
//    //@Named("med")
//    @Current    
    @TransactionAttribute   //    
    public Medicamento load() {
        if (isIdDefined()) {
            wire();
        }
        log.info("sgssalud --> cargar instance " + getInstance());
        //getInstance().setFechaIngreso(new Date());
        return getInstance();
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
       
    }

    /*Metodo que retorna una instancia de la clase (Medicamento) cuando ya esta creada==>*/
    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    /*Metodo importante para actualizar EntityManager y tener acceso a la DB==>*/
    @PostConstruct
    public void init() {
        setEntityManager(em);
        bussinesEntityService.setEntityManager(em);
        rs.setEntityManager(em);
        ms.setEntityManager(em);        
    }

    @Override
    protected Medicamento createInstance() {
        //prellenado estable para cualquier clase 
        Date now = Calendar.getInstance().getTime();
        Medicamento medicament = new Medicamento();
        medicament.setCreatedOn(now);
        medicament.setLastUpdate(now);
        medicament.setActivationTime(now);
        //med.setExpirationTime(Dates.addDays(now, 364));        
        medicament.setResponsable(null);    //cambiar atributo a 
        medicament.setFechaIngreso(now);  //Fecha actual de ingreso 
        medicament.buildAttributes(bussinesEntityService);  //
        return medicament;
    }

    @Override
    public Class<Medicamento> getEntityClass() {
        return Medicamento.class;
    }

    @TransactionAttribute
    public String guardarMedicamento() {
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        if (getInstance().isPersistent()) {
            save(getInstance());
        } else {
            create(getInstance());
            getInstance().setCantidadIngreso(getInstance().getUnidades());
            save(getInstance());
            FacesMessage msg = new FacesMessage("Se creo nuevo medicamento: " + getInstance().getNombreComercial() + " con éxito");
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return "/pages/farmacia/medicamento/lista.xhtml?faces-redirect=true";
    }

    public void validarFC() {
    }
    
      public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(" Medicamento Seleccionado", ((Medicamento) event.getObject()).getNombreComercial());
        setMedicamentoSeleccionado((Medicamento) event.getObject());
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Medicamento No seleccionado", ((Medicamento) event.getObject()).getNombreComercial());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        this.setMedicamentoSeleccionado(null);
    }

    public Object getRowKey(Medicamento med) {
        //log.info("ingreso a getRowData");
        return med.getNombreComercial();
    }

    public Medicamento getRowData(String rowKey) {
        //log.info("ingreso a getRowData");
        return ms.buscarPorNombreMedicamento(rowKey);
    }
    
}


