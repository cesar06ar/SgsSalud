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
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.model.farmacia.Receta_Medicamento;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.model.labClinico.Parametros;
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import edu.sgssalud.model.labClinico.ResultadoExamenLabClinico;
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.EnfermedadCIE10;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.medicina.Hc_Cie10;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.servicios.Turno;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.SettingService;
import edu.sgssalud.service.farmacia.RecetaMedicamentoService;
import edu.sgssalud.service.farmacia.RecetaServicio;
import edu.sgssalud.service.labClinico.ExamenLabService;
import edu.sgssalud.service.labClinico.PedidoExamenListaServicio;
import edu.sgssalud.service.labClinico.PedidoExamenService;
import edu.sgssalud.service.labClinico.ResultadoExamenLCService;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.EnfermedadesCie10Servicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.HistoriaClinicaServicio;
import edu.sgssalud.service.medicina.TurnoService;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.FechasUtil;
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
import org.hibernate.Session;
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
    @Inject
    private RecetaMedicamentoService recetaMedicamentoServicio;
    @Inject
    private RecetaServicio recetasServicio;
    @Inject
    private ResultadoExamenLCService resultadosExamenesService;
    @Inject
    SettingService settingService;
    private Setting setting;

    private HistoriaClinica hc;
    private SignosVitales signosVitales;
    private Long fichaMedicaId;
    private Paciente paciente;
    private PedidoExamenLaboratorio pedidoExamen;
    private Receta receta;
    private Hc_Cie10 enfermedadCie;
    private List<EnfermedadCIE10> listaEnfCie10 = new ArrayList<EnfermedadCIE10>();
    private List<Hc_Cie10> listaEnfPosee = new ArrayList<Hc_Cie10>();

    private List<PedidoExamenLaboratorio> listaPedidos = new ArrayList<PedidoExamenLaboratorio>();
    private CartesianChartModel linearModel = new CartesianChartModel();

    //attributos para agregar pedido de examen 
    @Inject
    private ExamenLabService examenLabService;
    @Inject
    private PedidoExamenService pedidoServicio;
    private List<ExamenLabClinico> listaExamenLab = new ArrayList<ExamenLabClinico>();
    private PedidoExamenLaboratorio pedido;

    private Long turnoId;
    private Turno turno;
    @Inject
    private TurnoService turnoS;

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
        if (hc != null) {
            listaEnfPosee = hc.getLista_enfcie10();
            listaEnfCie10 = cie10servicio.getEnfermedadesCIE10();
            listaPedidos = hc.getPedidosExamenLab();
            /* if (!hc.getEnfermedadesCIE10().isEmpty()) {
             this.setListaEnfPosee(hc.getEnfermedadesCIE10());
             this.setListaEnfCie10(cie10servicio.getEnfermedadesSinHistoriaClinica(listaEnfPosee));
             //this.setListaEnfCie10(cie10servicio.getEnfermedadesCIE10());
             } else {
             this.setListaEnfCie10(cie10servicio.getEnfermedadesCIE10());
             }
             pickListEnfermedades = new DualListModel<EnfermedadCIE10>(listaEnfCie10, listaEnfPosee);
             */
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

    public List<Hc_Cie10> getListaEnfPosee() {
        return listaEnfPosee;
    }

    public void setListaEnfPosee(List<Hc_Cie10> listaEnfPosee) {
        this.listaEnfPosee = listaEnfPosee;
    }

    public Hc_Cie10 getEnfermedadCie() {
        return enfermedadCie;
    }

    public void setEnfermedadCie(Hc_Cie10 enfermedadCie) {
        this.enfermedadCie = enfermedadCie;
    }

    public CartesianChartModel getLinearModel() {
        return linearModel;
    }

    public void setLinearModel(CartesianChartModel linearModel) {
        this.linearModel = linearModel;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }

    public PedidoExamenLaboratorio getPedidoExamen() {
        return pedidoExamen;
    }

    public void setPedidoExamen(PedidoExamenLaboratorio pedidoExamen) {
        this.pedidoExamen = pedidoExamen;
    }

    public List<PedidoExamenLaboratorio> getListaPedidos() {
        return listaPedidos;
    }

    public void setListaPedidos(List<PedidoExamenLaboratorio> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    public PedidoExamenLaboratorio getPedido() {
        return pedido;
    }

    public void setPedido(PedidoExamenLaboratorio pedido) {
        this.pedido = pedido;
    }

    public List<ExamenLabClinico> getListaExamenLab() {
        return listaExamenLab;
    }

    public void setListaExamenLab(List<ExamenLabClinico> listaExamenLab) {
        this.listaExamenLab = listaExamenLab;
    }

    public Long getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(Long turnoId) {
        this.turnoId = turnoId;
        if (turnoId != null) {
            turno = turnoS.find(turnoId);
        }
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
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
        recetaMedicamentoServicio.setEntityManager(em);
        recetasServicio.setEntityManager(em);
        receta = new Receta();
        resultadosExamenesService.setEntityManager(em);
        settingService.setEntityManager(em);
        setting = settingService.findByName("consultaActiva");
        if (getInstance().isPersistent()) {
            if (getInstance().getResponsable() == null && identity.isLoggedIn()) {
                getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
            }
        }
        getInstance().setTiempoConsulta(FechasUtil.sumarRestaMinutosFecha(getInstance().getHoraConsulta(), 30));

        examenLabService.setEntityManager(em);
        pedidoServicio.setEntityManager(em);
        listaExamenLab = examenLabService.getExamenesLab();
        pedido = new PedidoExamenLaboratorio();
        turnoS.setEntityManager(em);
    }

    @TransactionAttribute
    public ConsultaMedica load() {
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
        //log.info("Ingreso a guardar");
        String salida = null;
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        getInstance().setTiempoConsulta(FechasUtil.sumarRestaMinutosFecha(now, 5));
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
                System.out.println("enfermedades____________" + listaEnfPosee.toString());
                hc.setLastUpdate(now);
                hc.setLista_enfcie10(listaEnfPosee);
                update();
                save(hc);
                if (turno.isPersistent()) {
                    turno.setEstado("Realizada");
                    save(turno);
                }
                FacesMessage msg = new FacesMessage("Se actualizo Consulta Médica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                getInstance().setHistoriaClinica(hc);
                getInstance().getSignosVitales().setFechaActual(now);
                getInstance().setCode("REALIZADA");
                getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
                create(getInstance());
                //hc.setEnfermedadesCIE10(pickListEnfermedades.getTarget());
                save(hc);
                save(getInstance());
                if (turno.isPersistent()) {
                    turno.setEstado("Realizada");
                    save(turno);
                }
                FacesMessage msg = new FacesMessage("Se creo nueva Consulta Médica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                salida = "/pages/depSalud/medicina/consultaMedica.xhtml?faces-redirect=true"
                        + "&fichaMedicaId=" + getFichaMedicaId()
                        + "&consultaMedicaId=" + getInstance().getId()
                        + "&backView=" + this.getBackView();
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId() + " " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        //return "/pages/medicina/fichaMedica.xhtml?faces-redirect=true&fichaMedicaId="+getFichaMedicaId();
        return salida;
    }

    public void agregarEnfermedad(EnfermedadCIE10 enfcie10) {
        System.out.println("INGRESO METODO___" + enfcie10.toString());
        try {
            boolean b = false;
            if (!listaEnfPosee.isEmpty()) {
                System.out.println("INGRESO METODO___1");
                for (Hc_Cie10 lhc : hc.getLista_enfcie10()) {
                    if (lhc.getEnf_cieE10().getId().equals(enfcie10.getId())) {
                        b = true;
                    }
                }
            }
            System.out.println("INGRESO METODO___2");
            if (!b && hc.isPersistent()) {
                Hc_Cie10 enf = new Hc_Cie10();
                enf.setHistoriaClinica(hc);
                enf.setEnf_cieE10(enfcie10);
                hc.agregarEnfermedad(enf);
                save(hc);
                setListaEnfPosee(hc.getLista_enfcie10());
                System.out.println("AGREGO ENF  " + listaEnfPosee.size());
                FacesMessage msg = new FacesMessage("Se agrego enfermedad del cie 10: " + enf.getEnf_cieE10().getCodigo() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "¡Esta enfermedad ya ha sido agregada!", "");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        enfcie10 = null;
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

    @TransactionAttribute
    public String borrarEnfermedad() {
        System.out.println("Borro enfermedad___0");
        if (enfermedadCie != null) {

            try {
                System.out.println("Borro enfermedad___");
                delete(enfermedadCie);
                //hc.borrarEnfermedad(enfermedadCie);
                setListaEnfPosee(hc.getLista_enfcie10());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  " + enfermedadCie.getEnf_cieE10().getCodigo(), ""));
                RequestContext.getCurrentInstance().execute("deletedDlg2.hide()"); //cerrar el popup si se grabo correctamente
                return "/pages/depSalud/medicina/consultaMedica.xhtml?faces-redirect=true"
                        + "&fichaMedicaId=" + getFichaMedicaId()
                        + "&consultaMedicaId=" + getInstance().getId()
                        + "&backView=" + this.getBackView();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL BORRAR", ""));
                e.printStackTrace();
            }
        }
        return null;
    }

    @TransactionAttribute
    public String borrarReceta() {
        System.out.println("Borrar receta_______: " + receta.toString());
        String salida = "";
        try {
            if (getInstance().isPersistent()) {
                Receta_Medicamento aux;
                List<Receta_Medicamento> listaRM = recetaMedicamentoServicio.obtenerPorReceta(receta);
                for (Receta_Medicamento recetaMed1 : listaRM) {
                    Medicamento medicament = recetaMed1.getMedicamento();
                    int cantidad = medicament.getUnidades() + recetaMed1.getCantidad();
                    medicament.setUnidades(cantidad);
                    //medicament.set
                    aux = recetaMed1;
                    em.merge(medicament);
                    em.remove(aux);
                }
                delete(receta);
                //wire();
                //this.getInstance().setRecetas(recetasServicio.buscarRecetaPorConsultaMedica(getInstance()));
                System.out.println("ELIMINO RECETA");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se elimino receta", "" + getInstance().getId()));
                salida = "/pages/depSalud/medicina/consultaMedica.xhtml?faces-redirect=true"
                        + "&fichaMedicaId=" + getFichaMedicaId()
                        + "&consultaMedicaId=" + getInstance().getId()
                        + "&backView=" + this.getBackView();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERRORE", e.toString()));
            e.printStackTrace();
        }
        return salida;
    }

    @TransactionAttribute
    public String borrarPedidoExamen() {
        System.out.println("Borrar Pedido Examen_______: " + pedidoExamen.toString());
        String salida = "";
        try {
            if (getPedidoExamen().isPersistent()) {
                List<ResultadoExamenLabClinico> listaResultadosExamenLab = new ArrayList<ResultadoExamenLabClinico>();
                listaResultadosExamenLab = resultadosExamenesService.getResultadosExamenPorPedidoExamen(pedidoExamen);
                ResultadoExamenLabClinico aux;
                for (ResultadoExamenLabClinico result : listaResultadosExamenLab) {
                    aux = result;
                    em.remove(aux);
                }

                delete(pedidoExamen);
                //System.out.println("ELIMINO Pedido");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se elimino Pedido", "" + pedidoExamen.getId()));
                salida = "/pages/depSalud/medicina/consultaMedica.xhtml?faces-redirect=true"
                        + "&fichaMedicaId=" + getFichaMedicaId()
                        + "&consultaMedicaId=" + getInstance().getId()
                        + "&backView=" + this.getBackView();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERRORE", e.toString()));
            e.printStackTrace();
        }
        return salida;
    }

    public void cargarEnfermedadesActuales() {
        //String enfEstandar = UI.getMessages("EnfermedadesEstandarEcuador");
        listaEnfCie10 = new ArrayList<EnfermedadCIE10>();
        this.setListaEnfCie10(cie10servicio.getEnfermedadesCIE10());
    }

    /**
     *
     * @return Retorna el valor del indice de masa corporal
     */
    public String getInidicadorIMC() {
        if (getInstance().isPersistent()) {
            if (paciente.getEdad() < 5) {
                int peso = ((paciente.getEdad() * 2) + 8);
                return "Peso del Niño = " + peso;
            } else if (paciente.getEdad() == 5 && paciente.getEdad() <= 10) {
                int peso = (paciente.getEdad() * 3) + 3;
                return "Peso del Niño = " + peso;
            } else if (paciente.getEdad() >= 15) {
                if (getInstance().getSignosVitales().getTalla() != 0.0 && getInstance().getSignosVitales().getPeso() != 0.0) {
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
            }
            return "";
        } else {
            return "";
        }
    }

    /**
     * Metodo para crear un grafico estadistico de primefaces
     */
    public void crearModeloLineal() {
        linearModel = new CartesianChartModel();
        ChartSeries serieImc = new ChartSeries();
        serieImc.setLabel("Indice Masa Corporal = " + getInidicadorIMC());
        serieImc.set("BAJO PESO", 0);
        serieImc.set("NORMAL", 18.5);
        serieImc.set("SOBRE PESO", 25);
        serieImc.set("OBESIDAD", 30);
        linearModel.addSeries(serieImc);
    }

    /**
     *
     * @return verdadero si se puede editar la consulta medica
     */
    public boolean isEditable() {
        //return FechasUtil.editable(getInstance().getFechaConsulta(), getInstance().getHoraConsulta(), 24);
        //System.out.println("Config "+setting.getValue());

        if ("REALIZADA".equals(getInstance().getCode()) && "false".equals(setting.getValue())) {
            //  System.out.println("DESHABILITADO");
            return true;
        }
        return false;
    }

    @TransactionAttribute
    public String agregarPedido() {
        Date now = Calendar.getInstance().getTime();
        System.out.println("INGRESo a Guardar _________");
        String salida = null;
        boolean ning = false;
        for (ExamenLabClinico ex : listaExamenLab) {
            if (ex.isSelect()) {
                ning = true;
                break;
            }
        }
        try {
            if (!ning) {
                //save(getInstance());
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe seleccionar minimo un examen", null);
                FacesContext.getCurrentInstance().addMessage("", msg);
                //return null;
            } else {
                //this.listaPedidoExamenLabC = pickListExamenesLab.getTarget();                               
                pedido.setHistoriaClinica(hc);
                pedido.setPaciente(paciente);
                pedido.setEstado("Nuevo");
                pedido.setResponsableEmision(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
                pedido.setFechaPedido(now);
                create(pedido);
                save(pedido);
                update();
                //System.out.println("Guardo Con exito 0_________");
                ResultadoExamenLabClinico resultadoExa;
                List<Parametros> pl = null;//                    
                for (ExamenLabClinico ex : this.listaExamenLab) {
                    if (ex.isSelect()) {
                        resultadoExa = new ResultadoExamenLabClinico();
                        resultadoExa.setExamenLab(ex);
                        resultadoExa.setPedidoExamenLab(pedido);
                        pl = examenLabService.getParametrosPorExamen(ex);
                        resultadoExa.agregarValoresResultados(pl);
                        save(resultadoExa);
                        update();
                    }
                }

                FacesMessage msg = new FacesMessage("Se agrego nuevo Pedido de Examenes: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                pedido = new PedidoExamenLaboratorio();
                //hc = hcs.buscarPorFichaMedica(fms.getFichaMedicaPorId(fichaMedicaId));
                listaPedidos = pedidoServicio.getPedidos(hc);
                RequestContext.getCurrentInstance().update(":form:tabOpc:tablaPedidos :form:growl");
                RequestContext.getCurrentInstance().execute("pedidoDlg.hide();");
                salida = "/pages/depSalud/medicina/consultaMedica.xhtml?faces-redirect=true"
                        + "&fichaMedicaId=" + getFichaMedicaId()
                        + "&consultaMedicaId=" + getInstance().getId()
                        + "&backView=" + this.getBackView();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }

        return salida;
    }

}
