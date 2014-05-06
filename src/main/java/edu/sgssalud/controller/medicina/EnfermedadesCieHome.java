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
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.model.labClinico.ResultadoExamenLabClinico;
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.EnfermedadCIE10;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.EnfermedadesCie10Servicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.HistoriaClinicaServicio;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.odontologia.ConsultaOdontologicaServicio;
import edu.sgssalud.util.UI;
import java.io.Serializable;
import java.util.AbstractList;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class EnfermedadesCieHome extends BussinesEntityHome<EnfermedadCIE10> implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(EnfermedadesCieHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private EnfermedadesCie10Servicio enfcie10service;

    private EnfermedadCIE10 enfermedad;
    private List<EnfermedadCIE10> listaEnfermedades = new ArrayList<EnfermedadCIE10>();

    public List<EnfermedadCIE10> getListaEnfermedades() {
        listaEnfermedades = enfcie10service.getEnfermedadesCIE10();
        Collections.sort(listaEnfermedades);
        return listaEnfermedades;
    }

    public void setListaEnfermedades(List<EnfermedadCIE10> listaEnfermedades) {
        this.listaEnfermedades = listaEnfermedades;
    }

    public EnfermedadCIE10 getEnfermedad() {
        return enfermedad;
    }

    public void setEnfermedad(EnfermedadCIE10 enfermedad) {
        this.enfermedad = enfermedad;
    }

    @TransactionAttribute   //
    public EnfermedadCIE10 load() {
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
        enfcie10service.setEntityManager(em);
        this.setInstance(new EnfermedadCIE10());
    }

    @Override
    protected EnfermedadCIE10 createInstance() {
        //prellenado estable para cualquier clase                 
        EnfermedadCIE10 enf = new EnfermedadCIE10();
        return enf;
    }

    @Override
    public Class<EnfermedadCIE10> getEntityClass() {
        return EnfermedadCIE10.class;
    }

    @TransactionAttribute
    public String guardar() {
        String salida = null;
        Date now = Calendar.getInstance().getTime();
        try {
            if (getInstance().isPersistent()) {
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo los Signos Vitales: " + getInstance().getNombre() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                salida = "";
                init();
            } else {
                if (!this.existe(getInstance().getCodigo())) {
                    create(getInstance());
                    save(getInstance());
                    System.out.println("GUARDO 3_______________");
                    FacesMessage msg = new FacesMessage("Se agrego enfermedad Cie 10 :" + getInstance().getNombre() + " con exito");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                    System.out.println("GUARDO 4_______________");
                    listaEnfermedades = enfcie10service.getEnfermedadesCIE10();
                    this.init();
                    salida = "/pages/depSalud/medicina/enfermedadescie10?faces-redirect=true";
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "otra enfermedad tiene el mismo codigo ", null);
                    FacesContext.getCurrentInstance().addMessage("", msg);
                }
            }

        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return salida;
    }

    @Transactional
    public String borrar() {
        try {
            //boolean datos = enfcie10service.consultarDatos(getInstance().getId());
            if (enfermedad.isPersistent() && enfcie10service.buscarPorEnfermedad(enfermedad)) {
                delete(enfermedad);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  " + enfermedad.getNombre(), ""));
                RequestContext.getCurrentInstance().execute("deletedDlg.hide()"); //cerrar el popup si se grabo correctamente
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se puede borrar esta enfermedad " + enfermedad.getNombre(), ""));
                RequestContext.getCurrentInstance().execute("deletedDlg.hide()"); //cerrar el popup si se grabo correctamente                    
            }
        } catch (Exception e) {
            //e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ESTA ENFERMEDAD TIENE DATOS RELACIONADOS ", null));

        }
        return null;
    }

    public void onRowSelect(SelectEvent event) {
        this.setInstance((EnfermedadCIE10) event.getObject());
        FacesMessage msg = new FacesMessage(UI.getMessages("Enfermedad CIE 10") + " " + UI.getMessages("common.selected"), "" + ((EnfermedadCIE10) event.getObject()).getId());
        FacesContext.getCurrentInstance().addMessage("", msg);
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage(UI.getMessages("Enfermedad CIE 10") + " " + UI.getMessages("common.unselected"), "" + ((EnfermedadCIE10) event.getObject()).getId());
        FacesContext.getCurrentInstance().addMessage("", msg);
        this.setInstance(null);
    }

    public boolean existe(String codigo) {
        for (EnfermedadCIE10 enf : listaEnfermedades) {
            if (enf.getCodigo().equals(codigo)) {
                return true;
            }
        }
        return false;
    }
}
