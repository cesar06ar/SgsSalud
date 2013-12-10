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

import com.lowagie.text.pdf.ArabicLigaturizer;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.model.Structure;
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.EnfermedadCIE10;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.EnfermedadesCie10Servicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.HistoriaClinicaServicio;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.Lists;
import edu.sgssalud.util.UI;
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
import org.jboss.seam.security.Identity;
import org.jboss.seam.transaction.Transactional;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

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
    @Inject
    private EnfermedadesCie10Servicio cie10servicio;

    private HistoriaClinica hc;
    private SignosVitales signosVitales;
    private Long fichaMedicaId;
    private Paciente paciente;

    private List<EnfermedadCIE10> listaEnfCie10 = new ArrayList<EnfermedadCIE10>();
    private List<EnfermedadCIE10> listaEnfPosee = new ArrayList<EnfermedadCIE10>();

    private DualListModel<EnfermedadCIE10> pickListEnfermedades = new DualListModel<EnfermedadCIE10>();

    private CartesianChartModel linearModel = new CartesianChartModel();

    public Long getConsultaMedicaId() {
        return (Long) getId();
    }

    public void setConsultaMedicaId(Long consultaMedicaId) {
        setId(consultaMedicaId);
        setPaciente(getInstance().getHistoriaClinica().getFichaMedica().getPaciente());
        crearModeloLineal();
    }

    public Long getFichaMedicaId() {
        return fichaMedicaId;
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        this.fichaMedicaId = fichaMedicaId;
        FichaMedica fm = fms.getFichaMedicaPorId(fichaMedicaId);
        this.setHc(hcs.buscarPorFichaMedica(fm));
        this.setPaciente(fm.getPaciente());
        //crearModeloLineal();
        if (hc.isPersistent()) {
            if (!hc.getEnfermedadesCIE10().isEmpty()) {
                this.setListaEnfPosee(hc.getEnfermedadesCIE10());
                this.setListaEnfCie10(cie10servicio.getEnfermedadesSinHistoriaClinica(listaEnfPosee));
                //this.setListaEnfCie10(cie10servicio.getEnfermedadesCIE10());
            } else {
                this.setListaEnfCie10(cie10servicio.getEnfermedadesCIE10());
            }
            pickListEnfermedades = new DualListModel<EnfermedadCIE10>(listaEnfCie10, listaEnfPosee);
        }
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

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<EnfermedadCIE10> getListaEnfCie10() {
        Collections.sort(listaEnfCie10);
        return listaEnfCie10;
    }

    public void setListaEnfCie10(List<EnfermedadCIE10> listaEnfCie10) {
        this.listaEnfCie10 = listaEnfCie10;
    }

    public List<EnfermedadCIE10> getListaEnfPosee() {
        Collections.sort(listaEnfPosee);
        return listaEnfPosee;
    }

    public void setListaEnfPosee(List<EnfermedadCIE10> listaEnfPosee) {
        this.listaEnfPosee = listaEnfPosee;
    }

    public DualListModel<EnfermedadCIE10> getPickListEnfermedades() {
        return pickListEnfermedades;
    }

    public void setPickListEnfermedades(DualListModel<EnfermedadCIE10> pickListEnfermedades) {
        this.pickListEnfermedades = pickListEnfermedades;
    }

    public CartesianChartModel getLinearModel() {
        return linearModel;
    }

    public void setLinearModel(CartesianChartModel linearModel) {
        this.linearModel = linearModel;
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
        cie10servicio.setEntityManager(em);
        if (getInstance().isPersistent()) {
            if (getInstance().getResponsable() == null) {
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
                if (getInstance().getResponsable() == null) {
                    getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
                }
                getInstance().getSignosVitales().setFechaActual(now);
                /*El codigo hace referencia al estado de la consulta puede ser
                 *PENDIENTE o REALIZADA*/
                getInstance().setCode("REALIZADA");
                save(getInstance());
                hc.setLastUpdate(now);
                save(hc);
                FacesMessage msg = new FacesMessage("Se actualizo Consulta Médica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                getInstance().setHistoriaClinica(hc);
                getInstance().getSignosVitales().setFechaActual(now);
                create(getInstance());
                hc.setEnfermedadesCIE10(pickListEnfermedades.getTarget());
                save(hc);
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se creo nueva Consulta Médica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId() + " " + e.getMessage());
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

    public void cargarEnfermedadesActuales() {
        //String enfEstandar = UI.getMessages("EnfermedadesEstandarEcuador");
        listaEnfCie10 = new ArrayList<EnfermedadCIE10>();
        this.setListaEnfCie10(cie10servicio.getEnfermedadesCIE10());
    }

    public void onTransfer(TransferEvent event) {
        StringBuilder builder = new StringBuilder();
        for (Object item : event.getItems()) {
            builder.append(((EnfermedadCIE10) item).getName()).append("<br />");
            hc.agregarEnfermedad((EnfermedadCIE10) item);
        }

        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        msg.setSummary("Enfermerdad Agregada");
        msg.setDetail(builder.toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String getInidicadirIMC() {
        if (paciente.getEdad() < 5) {
            int peso = (paciente.getEdad() * 2) + 8;
            return "Peso del Niño = " + peso;
        } else if (paciente.getEdad() == 5 && paciente.getEdad() <= 10) {
            int peso = (paciente.getEdad() * 3) + 3;
            return "Peso del Niño = " + peso;
        } else if (paciente.getEdad() >= 15) {
            double tallam = (getInstance().getSignosVitales().getTalla() / 100) * (getInstance().getSignosVitales().getTalla() / 100);
            double imc = getInstance().getSignosVitales().getPeso() / tallam;
            String indicador = String.valueOf(imc);
            indicador = indicador.substring(0, 6);
            if (imc < 18.5) {
                return "Bajo de Peso. IMC ( " + indicador + " )";
            } else if (imc >= 18.5 && imc <= 24.99) {
                return "Normal . IMC ( " + indicador + " )";
            } else if (imc >= 25 && imc <= 29.99) {
                return "Sobre Peso . IMC ( " + indicador + " )";
            } else if (imc > 30) {
                return "Obesidad. IMC ( " + indicador + " )";
            }
        }
        return "";
    }

    public void crearModeloLineal() {
        linearModel = new CartesianChartModel();
        ChartSeries serieImc = new ChartSeries();
        serieImc.setLabel("Indice Masa Corporal = " + getInidicadirIMC());
        serieImc.set("BAJO PESO", 0);
        serieImc.set("NORMAL", 18.5);
        serieImc.set("SOBRE PESO", 25);
        serieImc.set("OBESIDAD", 30);
        linearModel.addSeries(serieImc);
    }
}
