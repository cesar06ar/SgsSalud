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

package edu.sgssalud.controller.farmacia;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.farmacia.Receta_Medicamento;
import edu.sgssalud.service.farmacia.MedicamentoService;
import edu.sgssalud.service.farmacia.RecetaMedicamentoService;
import edu.sgssalud.service.farmacia.RecetaServicio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import org.jboss.seam.transaction.Transactional;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class CardexHome implements Serializable {
    
    @Inject
    @Web
    private EntityManager em;    
    @Inject
    private RecetaMedicamentoService recetaMedService;     
    @Inject
    private RecetaServicio recetaServicio;    
    @Inject
    private MedicamentoService medicamentoService;
    
    private Long medicamentoId;
    private int saldo;
    private Date fechaI;
    private Date fechaF;
    private String backView;
    private Medicamento medicamento;
    private List<Receta_Medicamento> listaRecetasMedicamentos = new ArrayList<Receta_Medicamento>();
//    @TransactionAttribute   //
//    public Receta_Medicamento load() {
//        if (isIdDefined()) {
//            wire();
//        }
//        //log.info("sgssalud --> cargar instance " + getInstance());
//        return getInstance();
//    }
//
//    @TransactionAttribute
//    public void wire() {
//        getInstance();
//    }
    
    @PostConstruct
    public void init() {           
        recetaMedService.setEntityManager(em);
        recetaServicio.setEntityManager(em);
        medicamentoService.setEntityManager(em);        
        listaRecetasMedicamentos = recetaMedService.obtenerPorMedicamento(medicamento);
    }    

    @TransactionAttribute
    public String guardar() {
        
        return null;
    } 

    public Long getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(Long medicamentoId) {
        this.medicamentoId = medicamentoId;
        if(medicamentoId != null){
            medicamento = medicamentoService.buscarMedicamentosPorId(medicamentoId);
            listaRecetasMedicamentos = recetaMedService.obtenerPorMedicamento(medicamento);
        }
    }  

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }   

    public String getBackView() {
        return backView;
    }

    public void setBackView(String backView) {
        this.backView = backView;
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
    
    public List<Receta_Medicamento> getListaRecetasMedicamentos() {
        //return listaRecetasMedicamentos;
        Collections.sort(listaRecetasMedicamentos);
        return listaRecetasMedicamentos;
    }

    public void setListaRecetasMedicamentos(List<Receta_Medicamento> listaRecetasMedicamentos) {
        this.listaRecetasMedicamentos = listaRecetasMedicamentos;
    }  

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }            
    
    public void buscarPorFechas(){
        listaRecetasMedicamentos = recetaMedService.obtenerPorMedicamentoYFechas(fechaI, fechaF, medicamento);
    }
    
    public void buscarTodos(){
        listaRecetasMedicamentos = recetaMedService.obtenerPorMedicamento(medicamento);
    }
}
