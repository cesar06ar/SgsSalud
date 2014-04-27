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
package edu.sgssalud.controller.labClinico;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.controller.odontologia.TratamientoDataModel;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.model.labClinico.Parametros;
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import edu.sgssalud.model.labClinico.ResultadoExamenLabClinico;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.labClinico.ExamenLabService;
import edu.sgssalud.service.labClinico.ResultadoExamenLCService;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.HistoriaClinicaServicio;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.Lists;
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
import org.jboss.seam.transaction.Transactional;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class PedidoExamenLabHome extends BussinesEntityHome<PedidoExamenLaboratorio> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private Identity identity;
    @Inject
    private PacienteServicio pacienteServic;
    @Inject
    private ProfileService profileServicio;
    @Inject
    private FichaMedicaServicio fichaMedicaServicio;
    @Inject
    private HistoriaClinicaServicio historiaClinService;
    @Inject
    private FichaOdontologicaServicio fichaOdonServicio;
    @Inject
    private ExamenLabService examenLabService;
    @Inject
    private ResultadoExamenLCService resultadoService;

    private Long fichaMedicaId;
    private Long consultaMedId;
    private Long consultaOdontId;
    private Long examenLabId;
    private Long pacienteId;
    private boolean result;

    private Paciente paciente;
    private HistoriaClinica hc;
    private FichaOdontologica fo;
    private FichaMedica fichaMedica;
    private ExamenLabClinico examenLabClinico;
    private ExamenDataModel examenDataModel = new ExamenDataModel();
    private List<ResultadoExamenLabClinico> listaResultadosExamenLab = new ArrayList<ResultadoExamenLabClinico>();
    private List<ExamenLabClinico> listaExamenLab = new ArrayList<ExamenLabClinico>();
    private List<ExamenLabClinico> listaPedidoExamenLabC = new ArrayList<ExamenLabClinico>();

    //private DualListModel<ExamenLabClinico> pickListExamenesLab = new DualListModel<ExamenLabClinico>();

    /*Métodos get y set para obtener el Id de la clase*/
    public Long getPedidoExamenId() {
        return (Long) getId();
    }

    public void setPedidoExamenId(Long pedidoExamenId) {
        //this.recetaId = recetaId;
        setId(pedidoExamenId);
        if (getInstance().isPersistent()) {
            this.listaResultadosExamenLab = resultadoService.getResultadosExamenPorPedidoExamen(getInstance());
        }
    }

    @TransactionAttribute
    public PedidoExamenLaboratorio load() {
        if (isIdDefined()) {
            wire();
        }
        //log.info("sgssalud --> cargar instance " + getInstance());
        return getInstance();
    }

    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        //bussinesEntityService.setEntityManager(em);
        pacienteServic.setEntityManager(em);
        fichaMedicaServicio.setEntityManager(em);
        historiaClinService.setEntityManager(em);
        fichaOdonServicio.setEntityManager(em);
        profileServicio.setEntityManager(em);
        examenLabService.setEntityManager(em);
        resultadoService.setEntityManager(em);
        if (pacienteId == null) {
            paciente = new Paciente();
            fichaMedica = new FichaMedica();
        }
        if (consultaMedId == null) {
            hc = new HistoriaClinica();
        }
        if (consultaOdontId == null) {
            fo = new FichaOdontologica();
        }
        listaExamenLab = examenLabService.getExamenesLab();
//        pickListExamenesLab = new DualListModel<ExamenLabClinico>(this.getListaExamenLab(), this.getListaPedidoExamenLabC());
        examenDataModel = new ExamenDataModel(listaExamenLab);
    }

    @Override
    protected PedidoExamenLaboratorio createInstance() {
        //prellenado estable para cualquier clase 
        //BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(ConsultaMedica.class.getName());
        Date now = Calendar.getInstance().getTime();
        PedidoExamenLaboratorio pedidoExamenLaboratorio = new PedidoExamenLaboratorio();
        pedidoExamenLaboratorio.setFechaPedido(now);
        pedidoExamenLaboratorio.setResponsableEmision(profileServicio.getProfileByIdentityKey(identity.getUser().getKey()));

        //fichaMedic.setResponsable(null);    //cambiar atributo a 
        return pedidoExamenLaboratorio;
    }

    @Override
    public Class<PedidoExamenLaboratorio> getEntityClass() {
        return PedidoExamenLaboratorio.class;
    }

    @TransactionAttribute
    public String guardar() {
        Date now = Calendar.getInstance().getTime();
        System.out.println("INGRESo a Guardar _________");
        try {
            if (getInstance().isPersistent()) {
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo Pedido : " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                if (consultaMedId != null) {
                    getInstance().setHistoriaClinica(hc);
                } else if (consultaOdontId != null) {
                    getInstance().setFichaOdontologica(fo);
                }
                //this.listaPedidoExamenLabC = pickListExamenesLab.getTarget();
                //System.out.println("Lista De Examens para Pedido   PICK:_______\n" + listaPedidoExamenLabC);
                if (!this.listaPedidoExamenLabC.isEmpty()) {

                    getInstance().setPaciente(paciente);
                    getInstance().setEstado("Nuevo");
                    getInstance().setResponsableEmision(profileServicio.getProfileByIdentityKey(identity.getUser().getKey()));

                    create(getInstance());
                    save(getInstance());
                    update();
                    System.out.println("Guardo Con exito 0_________");
                    ResultadoExamenLabClinico resultadoExa;
                    List<Parametros> pl = null;
                    for (ExamenLabClinico ex : this.listaPedidoExamenLabC) {
                        resultadoExa = new ResultadoExamenLabClinico();
                        resultadoExa.setExamenLab(ex);
                        resultadoExa.setPedidoExamenLab(getInstance());
                        pl = examenLabService.getParametrosPorExamen(ex);
                        System.out.println("PARAM________"+pl.toString());
                        resultadoExa.agregarValoresResultados(pl);
                        save(resultadoExa);
                        update();
                    }
                    System.out.println("Guardo Con exito_________--");
                }

                FacesMessage msg = new FacesMessage("Se agrego nuevo Pedido de Examenes: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return "/pages" + this.getBackView() + "?faces-redirect=true"
                + "&fichaMedicaId= " + this.fichaMedicaId
                + "&consultaMedicaId= " + this.consultaMedId
                + "&consultaOdontId= " + this.consultaOdontId
                + "&backView=" + this.getPrevious();
    }

    @Transactional
    public String borrarEntidad() {
        //log.info("sgssalud --> ingreso a eliminar: " + getInstance().getId());
        try {
            if (getInstance() == null) {
                throw new NullPointerException("Servicio is null");
            }
            if (getInstance().isPersistent()) {
                delete(getInstance());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  " + getInstance().getId(), ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "¡No existe una entidad para ser borrada!", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", e.toString()));
        }
        return "/pages/serviciosMedicos/lista.xhtml?faces-redirect=true";
    }

    public void agregarExamen(ExamenLabClinico examen) {
        if (examen.isSelect()) {
            if (!listaPedidoExamenLabC.contains(examen)) {
                listaPedidoExamenLabC.add(examen);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se agrego:  " + examen.getName(), " con éxito"));
            }
        } else if (listaPedidoExamenLabC.contains(examen)) {
            listaExamenLab.remove(examen);
        }
    }

    public Long getFichaMedicaId() {
        return fichaMedicaId;
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        this.fichaMedicaId = fichaMedicaId;
        this.setFichaMedica(fichaMedicaServicio.getFichaMedicaPorId(fichaMedicaId));
        if (fichaMedica.isPersistent()) {
            this.setPaciente(fichaMedica.getPaciente());
            this.setHc(historiaClinService.buscarPorFichaMedica(fichaMedica));
            this.setFo(fichaOdonServicio.getFichaOdontologicaPorFichaMedica(fichaMedica));
        }
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
        this.setPaciente(pacienteServic.getPacientePorId(pacienteId));
        FichaMedica f = fichaMedicaServicio.getFichaMedicaPorPaciente(paciente);
        if (f != null) {
            this.setFichaMedica(f);
            this.setHc(historiaClinService.buscarPorFichaMedica(fichaMedica));
            this.setFo(fichaOdonServicio.getFichaOdontologicaPorFichaMedica(fichaMedica));
        }
    }

    public Long getConsultaMedId() {
        return consultaMedId;
    }

    public void setConsultaMedId(Long consultaMedId) {
        this.consultaMedId = consultaMedId;
    }

    public Long getConsultaOdontId() {
        return consultaOdontId;
    }

    public void setConsultaOdontId(Long consultaOdontId) {
        this.consultaOdontId = consultaOdontId;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public HistoriaClinica getHc() {
        return hc;
    }

    public void setHc(HistoriaClinica hc) {
        this.hc = hc;
    }

    public FichaOdontologica getFo() {
        return fo;
    }

    public void setFo(FichaOdontologica fo) {
        this.fo = fo;
    }

    public Long getExamenLabId() {
        return examenLabId;
    }

    public void setExamenLabId(Long examenLabId) {
        this.examenLabId = examenLabId;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public FichaMedica getFichaMedica() {
        return fichaMedica;
    }

    public void setFichaMedica(FichaMedica fichaMedica) {
        this.fichaMedica = fichaMedica;
    }

    public ExamenLabClinico getExamenLabClinico() {
        return examenLabClinico;
    }

    public void setExamenLabClinico(ExamenLabClinico examenLabClinico) {
        this.examenLabClinico = examenLabClinico;
    }

    public List<ExamenLabClinico> getListaExamenLab() {
        //listaExamenLab = examenLabService.getExamenesLab();
        return listaExamenLab;
    }

    public void setListaExamenLab(List<ExamenLabClinico> listaExamenLab) {
        this.listaExamenLab = listaExamenLab;
    }

    public List<ResultadoExamenLabClinico> getListaResultadosExamenLab() {
        return listaResultadosExamenLab;
    }

    public void setListaResultadosExamenLab(List<ResultadoExamenLabClinico> listaResultadosExamenLab) {
        this.listaResultadosExamenLab = listaResultadosExamenLab;
    }

    public List<ExamenLabClinico> getListaPedidoExamenLabC() {
        return listaPedidoExamenLabC;
    }

    public void setListaPedidoExamenLabC(List<ExamenLabClinico> listaPedidoExamenLabC) {
        this.listaPedidoExamenLabC = listaPedidoExamenLabC;
    }

    public ExamenDataModel getExamenDataModel() {
        return examenDataModel;
    }

    public void setExamenDataModel(ExamenDataModel examenDataModel) {
        this.examenDataModel = examenDataModel;
    }

    /**
     * public void onTransfer(TransferEvent event) { StringBuilder builder = new
     * StringBuilder(); ExamenLabClinico exa; FacesMessage msg = new
     * FacesMessage(); msg.setSeverity(FacesMessage.SEVERITY_INFO); for (Object
     * item : event.getItems()) { exa = (ExamenLabClinico) item;
     * builder.append(exa.getName()).append(" \n"); if
     * (!this.pickListExamenesLab.getTarget().contains(exa)) {
     * msg.setSummary("Examenes Agregados"); } else { msg.setSummary("Examenes
     * Excluido"); } msg.setDetail(builder.toString()); }
     *
     * FacesContext.getCurrentInstance().addMessage(null, msg); }
     */
    public boolean contieneObj(List<ExamenLabClinico> listaExam, ExamenLabClinico examenLab) {
        for (ExamenLabClinico exam : listaExam) {
            if (exam.getId().equals(examenLab.getId())) {
                System.out.println("True____________");
                return true;
            }
        }

        return false;
    }

}
