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
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.HistoriaClinicaServicio;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.odontologia.ConsultaOdontologicaServicio;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class SignosVitalesHome extends BussinesEntityHome<SignosVitales> implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(SignosVitalesHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private FichaMedicaServicio fms;
    @Inject
    private HistoriaClinicaServicio hcs;
    @Inject
    private FichaOdontologicaServicio fichaOdonServicio;
    @Inject
    private ConsultaMedicaServicio consultaMedSer;
    @Inject
    private ConsultaOdontologicaServicio consultaOdontSer;
    private Long fichaMedicaId;
    private HistoriaClinica historiaClinica;
    private FichaOdontologica fichaOdontologica;
    private ConsultaMedica consultaMed;
    private ConsultaOdontologica consultaOdont;
    private boolean servicioMedico;
    private boolean servicioDental;

    public Long getSignosVitalesId() {
        return (Long) getId();
    }

    public void setSignosVitalesId(Long signosVitales) {
        setId(signosVitales);
        setConsultaMed(consultaMedSer.getPorSignosVitales(getInstance()));         
        setConsultaOdont(consultaOdontSer.getPorSignosVitales(getInstance()));
        servicioMedico = consultaMed != null;
        servicioDental = consultaOdont != null; 
        
    }

    public Long getFichaMedicaId() {
        return fichaMedicaId;
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        this.fichaMedicaId = fichaMedicaId;
        if (fichaMedicaId != null) {
            this.setHistoriaClinica(hcs.buscarPorFichaMedica(fms.getFichaMedicaPorId(fichaMedicaId)));
            this.setFichaOdontologica(fichaOdonServicio.getFichaOdontologicaPorFichaMedica(fms.getFichaMedicaPorId(fichaMedicaId)));
        }
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    public FichaOdontologica getFichaOdontologica() {
        return fichaOdontologica;
    }

    public void setFichaOdontologica(FichaOdontologica fichaOdontologica) {
        this.fichaOdontologica = fichaOdontologica;
    }

    public boolean isServicioMedico() {
        return servicioMedico;
    }

    public void setServicioMedico(boolean servicioMedico) {
        this.servicioMedico = servicioMedico;
    }

    public boolean isServicioDental() {
        return servicioDental;
    }

    public void setServicioDental(boolean servicioDental) {
        this.servicioDental = servicioDental;
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

    @TransactionAttribute   //
    public SignosVitales load() {
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
        /*el bussinesEntityService.setEntityManager(em) solo va si la Entidad en este caso (ConsultaMedia)
         *hereda de la Entidad BussinesEntity...  caso contrario no se lo agrega
         */
        bussinesEntityService.setEntityManager(em);
        fms.setEntityManager(em);
        hcs.setEntityManager(em);
        fichaOdonServicio.setEntityManager(em);
        consultaMedSer.setEntityManager(em);
        consultaOdontSer.setEntityManager(em);
    }

    @Override
    protected SignosVitales createInstance() {
        //prellenado estable para cualquier clase         
        Date now = Calendar.getInstance().getTime();
        SignosVitales signosVitales = new SignosVitales();
        signosVitales.setFechaActual(now);
        BussinesEntityType _typeCM = bussinesEntityService.findBussinesEntityTypeByName(ConsultaMedica.class.getName());
        consultaMed = new ConsultaMedica();
        consultaMed.setCreatedOn(now);
        consultaMed.setLastUpdate(now);
        consultaMed.setActivationTime(now);
        consultaMed.setFechaConsulta(now);
        consultaMed.setHoraConsulta(now);
        consultaMed.setSignosVitales(new SignosVitales());
        consultaMed.setType(_typeCM);

        consultaMed.buildAttributes(bussinesEntityService);

        BussinesEntityType _typeCO = bussinesEntityService.findBussinesEntityTypeByName(ConsultaOdontologica.class.getName());
        consultaOdont = new ConsultaOdontologica();
        consultaOdont.setCreatedOn(now);
        consultaOdont.setLastUpdate(now);
        consultaOdont.setActivationTime(now);
        consultaOdont.setFechaConsulta(now);
        consultaOdont.setHoraConsulta(now);
        consultaOdont.setSignosVitales(new SignosVitales());
        consultaOdont.setType(_typeCO);
        consultaOdont.buildAttributes(bussinesEntityService);

        //fichaMedic.setResponsable(null);    //cambiar atributo a         
        return signosVitales;
    }

    @Override
    public Class<SignosVitales> getEntityClass() {
        return SignosVitales.class;
    }

    @TransactionAttribute
    public String guardar() {
        String salida = null;
        Date now = Calendar.getInstance().getTime();
        try {
            if (getInstance().isPersistent()) {
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo los Signos Vitales: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                salida = "/pages/depSalud/fichaMedica.xhtml?faces-redirect=true&fichaMedicaId=" + getFichaMedicaId();
            } else {
                if (servicioMedico || servicioDental) {
                    create(getInstance());
                    //log.info("crear ");
                    if (servicioMedico) {
                        consultaMed.setHistoriaClinica(historiaClinica);
                        consultaMed.setSignosVitales(getInstance());
                        consultaMed.setCode("PENDIENTE");
                        save(consultaMed);
                        salida = "/pages/depSalud/fichaMedica.xhtml?faces-redirect=true&fichaMedicaId=" + getFichaMedicaId();
                    }
                    if (servicioDental) {
                        consultaOdont.setFichaOdontologica(fichaOdontologica);
                        consultaOdont.setSignosVitales(getInstance());
                        consultaOdont.setCode("PENDIENTE");
                        save(consultaOdont);
                        salida = "/pages/depSalud/fichaMedica.xhtml?faces-redirect=true&fichaMedicaId=" + getFichaMedicaId();
                    }
                } else {
                    FacesMessage msg = new FacesMessage("Debe selecionar al menos un servicio");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                }
            }

        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return salida;
    }
}
