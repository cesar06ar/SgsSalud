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
import edu.sgssalud.model.Structure;
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.HistoriaClinicaServicio;
import edu.sgssalud.service.paciente.PacienteServicio;
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
import org.jboss.seam.security.Identity;
import org.jboss.seam.transaction.Transactional;
import org.primefaces.context.RequestContext;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class ConsultaMedicaHome extends BussinesEntityHome<ConsultaMedica> implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ConsultaMedicaHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private ConsultaMedicaServicio cms;
    @Inject
    private FichaMedicaServicio fms;
    @Inject
    private HistoriaClinicaServicio hcs;
    @Inject
    private Identity identity;
    @Inject
    private ProfileService profileS;
    private HistoriaClinica hc;
    private SignosVitales signosVitales;
    private Long fichaMedicaId;

    public Long getConsultaMedicaId() {
        return (Long) getId();
    }

    public void setConsultaMedicaId(Long consultaMedicaId) {
        setId(consultaMedicaId);        
    }

    public Long getFichaMedicaId() {
        return fichaMedicaId;
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        this.fichaMedicaId = fichaMedicaId;
        this.setHc(hcs.buscarPorFichaMedica(fms.getFichaMedicaPorId(fichaMedicaId)));
    }

    public HistoriaClinica getHc() {
        return hc;
    }

    public void setHc(HistoriaClinica hc) {
        this.hc = hc;
    }

    public SignosVitales getSignosVitales() {
        return signosVitales;
    }

    public void setSignosVitales(SignosVitales signosVitales) {
        this.signosVitales = signosVitales;
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        /*el bussinesEntityService.setEntityManager(em) solo va si la Entidad en este caso (ConsultaMedia)
         *hereda de la Entidad BussinesEntity...  caso contrario no se lo agrega
         */
        bussinesEntityService.setEntityManager(em);
        cms.setEntityManager(em);
        hcs.setEntityManager(em);
        fms.setEntityManager(em);
        profileS.setEntityManager(em);
        if (getInstance().isPersistent()) {
            if(getInstance().getResponsable() == null){
                getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
            }
        }
    }

    @TransactionAttribute
    public ConsultaMedica load() {
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

    @Override
    protected ConsultaMedica createInstance() {
        //prellenado estable para cualquier clase 
        BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(ConsultaMedica.class.getName());
        Date now = Calendar.getInstance().getTime();
        ConsultaMedica consultaMedica = new ConsultaMedica();
        consultaMedica.setCreatedOn(now);
        consultaMedica.setLastUpdate(now);
        consultaMedica.setActivationTime(now);        
        consultaMedica.setSignosVitales(new SignosVitales());
        consultaMedica.setType(_type);
        consultaMedica.buildAttributes(bussinesEntityService);  //
        return consultaMedica;
    }

    @Override
    public Class<ConsultaMedica> getEntityClass() {
        return ConsultaMedica.class;
    }

    @TransactionAttribute
    public String guardar() {
        log.info("Ingreso a guardar");
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        try {
            if (getInstance().isPersistent()) {
                getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
                getInstance().getSignosVitales().setFechaActual(now);
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo Consulta Médica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                getInstance().setHistoriaClinica(hc);
                getInstance().getSignosVitales().setFechaActual(now);
                create(getInstance());
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se creo nueva Consulta Médica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        //return "/pages/medicina/fichaMedica.xhtml?faces-redirect=true&fichaMedicaId="+getFichaMedicaId();
        return null;
    }

    @Transactional
    public String borrar() {
        try {
            if (getInstance() == null) {
                throw new NullPointerException("property is null");
            }
            if (getInstance().isPersistent()) {

                delete(getInstance());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  " + getInstance().getName(), ""));
                RequestContext.getCurrentInstance().execute("deletedDlg.hide()"); //cerrar el popup si se grabo correctamente


            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "" + " " + getInstance().getName(), ""));
                RequestContext.getCurrentInstance().execute("deletedDlg.hide()"); //cerrar el popup si se grabo correctamente                    
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERRORE", e.toString()));
        }
        return null;
    }

    public void otroMetodo() {
    }
}
