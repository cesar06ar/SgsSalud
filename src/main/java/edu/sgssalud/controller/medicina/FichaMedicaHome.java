/*
 * Copyright 2013 cesar.
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
package edu.sgssalud.controller.medicina;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.controller.paciente.PacienteHome;
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.HistoriaClinicaServicio;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.StringValidations;
import edu.sgssalud.util.UI;
import java.io.Serializable;
import java.util.*;
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

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class FichaMedicaHome extends BussinesEntityHome<FichaMedica> implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(FichaMedicaHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private FichaMedicaServicio fms;
    @Inject
    private PacienteServicio ps;
    @Inject
    private ConsultaMedicaServicio cms;
    @Inject
    private HistoriaClinicaServicio hcs;
    private Long pacienteId;
    private Paciente paciente;
    private String parametroBusqueda;
    private HistoriaClinica historiaClinica;
    private Long consultaMedicaId;
    private ConsultaMedica consultaMedica;
    private List<ConsultaMedica> listaConsultasM = new ArrayList<ConsultaMedica>();

    /*<== Métodos get y set para obtener el Id de la clase*/
    public Long getFichaMedicaId() {
        return (Long) getId();
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        setId(fichaMedicaId);
        this.setPaciente(getInstance().getPaciente());
        this.setHistoriaClinica(hcs.buscarPorFichaMedica(getInstance()));
//        this.setListaConsultasM(cms.getConsultaMedicaPorHistoriaClinica(historiaClinica));
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
        this.setPaciente(ps.getPacientePorId(pacienteId));
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    public Long getConsultaMedicaId() {
        return consultaMedicaId;
    }

    public void setConsultaMedicaId(Long consultaMedicaId) {
        this.consultaMedicaId = consultaMedicaId;
        this.setConsultaMedica(cms.getConsultaMedicaPorId(consultaMedicaId));
    }

    public ConsultaMedica getConsultaMedica() {
        return consultaMedica;
    }

    public void setConsultaMedica(ConsultaMedica consultaMedica) {
        this.consultaMedica = consultaMedica;
    }

    public String getParametroBusqueda() {
        return parametroBusqueda;
    }

    public void setParametroBusqueda(String parametroBusqueda) {
        this.parametroBusqueda = parametroBusqueda;
    }

    public List<ConsultaMedica> getListaConsultasM() {
        return listaConsultasM;
    }

    public void setListaConsultasM(List<ConsultaMedica> listaConsultasM) {
        this.listaConsultasM = listaConsultasM;
    }

    /*<==....*/
    @TransactionAttribute   //
    public FichaMedica load() {
        if (isIdDefined()) {
            wire();
        }
        log.info("sgssalud --> cargar instance " + getInstance());
        return getInstance();
    }

    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        bussinesEntityService.setEntityManager(em);

        fms.setEntityManager(em);
        ps.setEntityManager(em);
        cms.setEntityManager(em);
        hcs.setEntityManager(em);

        if (pacienteId == null) {
            paciente = new Paciente();
        }
        getInstance().setFechaApertura(new Date());
        this.getInstance().setNumeroFicha(this.getGenerarNumeroFicha());
    }

    @Override
    protected FichaMedica createInstance() {
        //prellenado estable para cualquier clase 
        BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(FichaMedica.class.getName());
        Date now = Calendar.getInstance().getTime();
        FichaMedica fichaMedic = new FichaMedica();
        fichaMedic.setCreatedOn(now);
        //fichaMedic.setLastUpdate(now);
        fichaMedic.setActivationTime(now);
        fichaMedic.setFechaApertura(now);
        //fichaMedic.setResponsable(null);    //cambiar atributo a 
        fichaMedic.setType(_type);
        fichaMedic.buildAttributes(bussinesEntityService);  //

        historiaClinica = new HistoriaClinica();
        historiaClinica.setCreatedOn(now);
        historiaClinica.setActivationTime(now);
        historiaClinica.setLastUpdate(now);
        return fichaMedic;
    }

    @Override
    public Class<FichaMedica> getEntityClass() {
        return FichaMedica.class;
    }

    @TransactionAttribute
    public String guardar() {
        log.info("Ingreso a guardar");
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        historiaClinica.setLastUpdate(now);
        log.info("Ingreso a guardar 1");
        try {
            if (getInstance().isPersistent()) {
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo Ficha Medica: " + getInstance().getNumeroFicha() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                this.getInstance().setPaciente(paciente);
                log.info("Ingreso a guardar 2");
                create(getInstance());
                historiaClinica.setFichaMedica(getInstance());
                save(getInstance());
                save(historiaClinica);
                
                FacesMessage msg = new FacesMessage("Se creo nueva Ficha Medica: " + getInstance().getNumeroFicha() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getNumeroFicha());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }

        return "/pages/medicina/fichaMedica.xhtml?faces-redirect=true&fichaMedicaId=" + getInstance().getId() + "&pacienteId=" + getInstance().getPaciente().getId();
    }

    public void buscarPorParametro() {
        //buscar primero por numero de ficha
        FichaMedica fichaMedList = fms.getFichaMedicaPorNumeroFicha(parametroBusqueda);
        if (StringValidations.isDecimal(parametroBusqueda)) {
            Paciente p = ps.buscarPorCedula(parametroBusqueda);
            if (fichaMedList != null) {
                this.setInstance(fichaMedList);
                this.setPaciente(getInstance().getPaciente());
            } else if (p != null) {
                this.setPaciente(p);
                this.setInstance(fms.getFichaMedicaPorPaciente(p));
            } else {
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, "No encontro resultados  ", ""));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, "El parametro de busqueda es incorrecto:  ", ""));
        }
    }

    /*<== método que retorna la lista de tipos de datos enumerados ...*/
    public List<FichaMedica.GrupoSangineo> getListaGruposSangineos() {
        wire();
        List<FichaMedica.GrupoSangineo> list = Arrays.asList(getInstance().getGrupoSangineo().values());
        return list;
    }
    /*....==>*/

    public Long getGenerarNumeroFicha() {
        List<FichaMedica> listaF = fms.getFichasMedicas();
        Long num = new Long(001);
        if (!listaF.isEmpty()) {
            for (FichaMedica fm : listaF) {
                if (fm.getNumeroFicha() >= num) {
                    num = (fm.getNumeroFicha() + 1);
                }
            }
        }
        return num;
    }

    public List<Paciente.Genero> getListaGeneros() {
        wire();
        List<Paciente.Genero> list = Arrays.asList(paciente.getGenero().values());
        return list;
    }

    /*<== métodos para selección de consultas medicas en la tabla de primefaces...*/
    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("Consulta Medica") + " " + UI.getMessages("common.selected"), "" + ((ConsultaMedica) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("Consulta Medica") + " " + UI.getMessages("common.unselected"), ((ConsultaMedica) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage("", msg);
        this.setConsultaMedica(null);
    }
    /*....==>*/
}
