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
package edu.sgssalud.controller.reportes;

import com.smartics.common.action.report.JasperReportAction;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import edu.sgssalud.model.labClinico.ResultadoExamenLabClinico;
import edu.sgssalud.model.labClinico.ResultadoParametro;
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.model.servicios.Servicio;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.ServiciosMedicosService;
import edu.sgssalud.service.farmacia.MedicamentoService;
import edu.sgssalud.service.farmacia.RecetaServicio;
import edu.sgssalud.service.labClinico.ExamenLabService;
import edu.sgssalud.service.labClinico.ResultadoExamenLCService;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.odontologia.ConsultaOdontologicaServicio;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.FechasUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import org.jboss.seam.security.Identity;

/**
 *
 * @author cesar
 */
@RequestScoped
@Named(value = "reporteListas")
public class ReporteListas {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ReporteListas.class);

    private static final String REPORTE_USUARIOS = "Usuarios";  //nombre del reporte .jasper   
    private static final String REPORTE_PACIENTES = "pacientes";
    private static final String REPORTE_FICHASMEDICAS = "listaFichasMedicas";
    private static final String REPORTE_CONSULTASMEDICAS = "listaConsultasMed";
    private static final String REPORTE_CONSULTAS_ODONT = "listaConsultasOdont";
    private static final String REPORTE_RECETAS = "listaRecetas";
    private static final String REPORTE_PEDIDO = "pedidoExamen";
    private static final String REPORTE_EXAMENES = "listaExamenes";
    private static final String REPORTE_MEDICAMENTOS = "listaMedicamentos";
    private static final String REPORTE_RESULTADO_EXAMEN = "resultadoExamen";
    private static final String REPORTE_SERVICIOS_MEDICOS = "listaServiciosEnfermeria";
    @Inject
    @Web
    private EntityManager em;
    @Inject
    JasperReportAction JasperReportAction;
    @Inject
    private ProfileService profileService;
    @Inject
    private PacienteServicio pacienteServicio;
    @Inject
    private FichaMedicaServicio fichaMedicaServicio;
    @Inject
    private ConsultaMedicaServicio consultaMedicaServicio;
    @Inject
    private ConsultaOdontologicaServicio consultaOdontologicaServicio;
    @Inject
    private RecetaServicio recetaServicio;
    @Inject
    private MedicamentoService medService;
    @Inject
    private ExamenLabService examenesService;
    @Inject
    private ResultadoExamenLCService resultadoEService;
    @Inject
    private Identity identity;
    @Inject
    private ServiciosMedicosService servicioMedS;

    private Date fechaInf;
    private Date fechaSup;
    private boolean estado;
    private String parametroBusqued;
    private Profile pLoggeado;

    private PedidoExamenLaboratorio pedido;
    private ResultadoExamenLabClinico resultadoExamen = new ResultadoExamenLabClinico();

    /**
     * Default constructor.
     */
    public ReporteListas() {
    }

    @PostConstruct
    public void init() {
        profileService.setEntityManager(em);
        pacienteServicio.setEntityManager(em);
        medService.setEntityManager(em);
        recetaServicio.setEntityManager(em);
        examenesService.setEntityManager(em);
        fichaMedicaServicio.setEntityManager(em);
        consultaMedicaServicio.setEntityManager(em);
        consultaOdontologicaServicio.setEntityManager(em);
        resultadoEService.setEntityManager(em);
        pLoggeado = profileService.getProfileByIdentityKey(identity.getUser().getKey());
        servicioMedS.setEntityManager(em);
        //resultadoExamen = new ResultadoExamenLabClinico();

    }

    private String getRealPath(String path) {
        ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        return context.getRealPath(path);
    }

    /**
     * public void render() { if (log.isDebugEnabled()) { log.debug("export as
     * pdf"); } final String mimeType = "application/pdf"; final String
     * attachFileName = "usuarios.pdf"; final String reportTemplate =
     * "/reportes/Reporte.jasper";
     *
     * if (log.isDebugEnabled()) { log.debug("mimeType@" + mimeType);
     * log.debug("attachFileName@" + attachFileName); }
     *
     * ExternalContext externalContext = facesContext.getExternalContext();
     *
     * externalContext.setResponseContentType(mimeType);
     * externalContext.addResponseHeader("Content-Disposition",
     * "attachment;filename=" + attachFileName + "");
     *
     * InputStream sourceTemplate = resourceProvider
     * .loadResourceStream(reportTemplate); String pathTemplate =
     * getRealPath(reportTemplate);
     *
     * Map<String, Object> _values = new HashMap<String, Object>();
     * _values.put("contacts", profileListService); _values.put("usd", "$");
     *
     * String stringReport = new VelocityTemplate(sourceTemplate,
     * velocityContext).merge(_values);
     *
     * if (log.isDebugEnabled()) { log.debug("report source file content@" +
     * stringReport); } // source ReportDefinition report; try { report =
     * compiler.compile(new ByteArrayInputStream(stringReport
     * .getBytes("UTF-8"))); //JasperPrint reporte =
     * JasperFillManager.fillReport(sourceTemplate, _values, new
     * JREmptyDataSource()); Report reportInstance = report.fill(new
     * JREmptyDataSource(), null);
     *
     * pdfRenderer.render(reportInstance,
     * externalContext.getResponseOutputStream());
     * //JasperExportManager.exportReportToPdf(reporte); } catch
     * (ReportException e) { e.printStackTrace(); } catch
     * (UnsupportedEncodingException e) { e.printStackTrace(); } catch
     * (IOException e) { e.printStackTrace(); }
     *
     * facesContext.responseComplete();
     *
     * }
     *
     * public void render1() { if (log.isDebugEnabled()) { log.debug("export as
     * pdf without apache velocity"); } final String mimeType =
     * "application/pdf"; final String attachFileName = "usuarios.pdf"; final
     * String reportTemplate = "/reportes/Reporte.jrxml";
     *
     * if (log.isDebugEnabled()) { log.debug("mimeType@" + mimeType);
     * log.debug("attachFileName@" + attachFileName); }
     *
     * ExternalContext externalContext = facesContext.getExternalContext();
     *
     * externalContext.setResponseContentType(mimeType);
     * externalContext.addResponseHeader("Content-Disposition",
     * "attachment;filename=" + attachFileName + "");
     *
     * InputStream sourceTemplate = resourceProvider
     * .loadResourceStream(reportTemplate);
     *
     * // source ReportDefinition report; try { report =
     * compiler.compile(sourceTemplate); Report reportInstance = report.fill(new
     * JRBeanCollectionDataSource( profileListService.getResultList()), null);
     *
     * pdfRenderer.render(reportInstance,
     * externalContext.getResponseOutputStream()); } catch (ReportException e) {
     * e.printStackTrace(); } catch (UnsupportedEncodingException e) {
     * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
     *
     * facesContext.responseComplete(); }
     *
     *
     */
    public void renderProfile() {

        final String attachFileName = "usuarios.pdf";
        List<Profile> p = profileService.findAllA(estado);

        String logo = getRealPath("/reportes/unl.png");

        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("numeroP", p.size());
        _values.put("logo", logo);
        _values.put("usuarioResponsable", pLoggeado.getFullName());
        _values.put("usd", "$");
        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_USUARIOS, p, _values, attachFileName);

        if (log.isDebugEnabled()) {
            log.debug("export as pdf");
        }
    }

    public void renderPacientes() {

        final String attachFileName = "pacientes.pdf";
        List<Paciente> pacientes = pacienteServicio.getPacientes();
//        if (parametroBusqued != null) {
//            pacientes = pacienteServicio.getPacientesPorParametros(parametroBusqued);
//        }
        //parametros 
        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("numeroPacientes", pacientes.size());

        String logo = getRealPath("/reportes/unl.png");
        _values.put("usuarioResponsable", pLoggeado.getFullName());
        _values.put("logo", logo);
        _values.put("usd", "$");

        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_PACIENTES, pacientes, _values, attachFileName);

        if (log.isDebugEnabled()) {
            log.debug("export as pdf");
        }
    }

    public void renderFichasMedicas() {

        final String attachFileName = "fichasMedicas.pdf";
        List<FichaMedica> listafichaMed = fichaMedicaServicio.getFichasMedicas();
        Map<String, Object> _values = new HashMap<String, Object>();
        if (fechaInf != null && fechaSup != null) {
            listafichaMed = fichaMedicaServicio.getFichaMedicaPorFechas(fechaInf, fechaSup);
//            _values.put("fechaDesde", fechaInf);
//            _values.put("fechaHasta", fechaSup);
        } else {
            _values.put("fechaDesde", "");
            _values.put("fechaHasta", "");

        }

        String logo = getRealPath("/reportes/unl.png");

        //parametros 
        _values.put("numero", listafichaMed.size());
        _values.put("usuarioResponsable", pLoggeado.getFullName());
        _values.put("logo", logo);
        _values.put("fechaDesde", fechaInf);
        _values.put("fechaHasta", fechaSup);
        _values.put("usd", "$");

        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_FICHASMEDICAS, listafichaMed, _values, attachFileName);
        if (log.isDebugEnabled()) {
            log.debug("export as pdf");
        }
    }

    public void renderConsultasMedicas() {
        final String attachFileName = "consultasMedicas.pdf";
        List<ConsultaMedica> consulMed = consultaMedicaServicio.getConsulasMedicas();
        if (fechaInf != null && fechaSup != null) {
            System.out.println("INGRESO A BUSCAR POR FECHAS ");
            consulMed = consultaMedicaServicio.buscarPorRangoFechas(fechaInf, fechaSup);
        }
        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("numero", consulMed.size());
        String logo = getRealPath("/reportes/unl.png");
        _values.put("usuarioResponsable", pLoggeado.getFullName());
        _values.put("logo", logo);
        _values.put("fechaDesde", fechaInf);
        _values.put("fechaHasta", fechaSup);
        //_values.put("usd", "$");
        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_CONSULTASMEDICAS, consulMed, _values, attachFileName);
    }

    public void renderConsultasOdont() {
        final String attachFileName = "consultasOdontológicas.pdf";
        List<ConsultaOdontologica> consulOdont = consultaOdontologicaServicio.TodasConsulasOdontologica();
        if (fechaInf != null && fechaSup != null) {
            consulOdont = consultaOdontologicaServicio.buscarPorFechas(fechaInf, fechaSup);
        }
        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("numero", consulOdont.size());
        String logo = getRealPath("/reportes/unl.png");
        _values.put("logo", logo);
        _values.put("fechaDesde", fechaInf);
        _values.put("fechaHasta", fechaSup);
        _values.put("usuarioResponsable", pLoggeado.getFullName());
        //_values.put("usd", "$");
        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_CONSULTAS_ODONT, consulOdont, _values, attachFileName);
    }

    public void renderMedicamentos() {

        try {
            final String attachFileName = "medicamentos.pdf";
            List<Medicamento> medicamentos = medService.buscarTodos();
            //parametros 
            Map<String, Object> _values = new HashMap<String, Object>();
            _values.put("nMedicamentos", medicamentos.size());
            String logo = getRealPath("/reportes/unl.png");
            _values.put("usuarioResponsable", pLoggeado.getFullName());
            _values.put("logo", logo);
            _values.put("usd", "$");
            //Exportar a pdf 
            JasperReportAction.exportToPdf(REPORTE_MEDICAMENTOS, medicamentos, _values, attachFileName);
        } catch (Exception e) {
//            System.out.println("Error:____________________________________");
//            e.printStackTrace();
        }

    }

    public void renderExamenes() {
        String logo = getRealPath("/reportes/unl.png");
        final String attachFileName = "examenesLaboratorio.pdf";
        List<ExamenLabClinico> examenes = examenesService.getExamenesLab();
        //parametros 
        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("numero", examenes.size());
        _values.put("logo", logo);
        _values.put("usuarioResponsable", pLoggeado.getFullName());
        _values.put("usd", "$");

        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_EXAMENES, examenes, _values, attachFileName);
    }

    public void renderRecetas() {
        final String attachFileName = "recetas.pdf";
        List<Receta> receta = recetaServicio.getRecetas();
        if (fechaInf != null && fechaSup != null) {
            receta = recetaServicio.buscarRecetaPorFechas(fechaInf, fechaSup);
        }

        //parametros 
        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("numero", receta.size());
        String logo = getRealPath("/reportes/unl.png");
        _values.put("logo", logo);
        _values.put("fechaDesde", fechaInf);
        _values.put("fechaHasta", fechaSup);
        _values.put("usuarioResponsable", pLoggeado.getFullName());
        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_RECETAS, receta, _values, attachFileName);
        fechaInf = null;
        fechaSup = null;
    }

    public void renderPedido() {
        final String attachFileName = "pedidoExamen.pdf";

        List<ExamenLabClinico> examenes = new ArrayList<ExamenLabClinico>();
        List<ResultadoExamenLabClinico> listR = resultadoEService.getResultadosExamenPorPedidoExamen(pedido);
        if (!listR.isEmpty()) {
            for (ResultadoExamenLabClinico r : listR) {
                examenes.add(r.getExamenLab());
            }
        }
        String logo = getRealPath("/reportes/unl.png");
        //parametros 
        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("nombres", pedido.getPaciente().getNombres() + " " + pedido.getPaciente().getApellidos());
        _values.put("fechaE", pedido.getFechaPedido());
        _values.put("logo", logo);
        _values.put("medico", pedido.getResponsableEmision().getFullName());
        //_values.put("usd", "$");
        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_PEDIDO, examenes, _values, attachFileName);
    }

    public void renderResultadoExamen() {
        final String attachFileName = "resultdoExamen.pdf";
        String logo = getRealPath("/reportes/unl.png");
        if (resultadoExamen.isPersistent()) {
            //List<String> categorias = Arrays.asList(resultadoExamen.getExamenLab().getCategorias().split(","));

            Map<String, Object> _values = new HashMap<String, Object>();
            String nombres = resultadoExamen.getPedidoExamenLab().getPaciente().getCedula() + " " + resultadoExamen.getPedidoExamenLab().getPaciente().getNombres() + " " + resultadoExamen.getPedidoExamenLab().getPaciente().getApellidos();

            System.out.println("RESULTADO " + resultadoExamen.toString() + " " + nombres);
            _values.put("nombres", nombres);
            _values.put("nombreExamen", resultadoExamen.getExamenLab().getName());
            _values.put("logo", logo);
            _values.put("medico", ("Dr. " + resultadoExamen.getResponsableEntrega().getFullName()));
            _values.put("usd", "$");
            /*if (categorias.isEmpty()) {
             _values.put("cat1", "");
             _values.put("cat2", "");
             _values.put("cat3", "");
             } else if (categorias.size() == 1) {
             _values.put("cat1", categorias.get(0));            
             } else if (categorias.size() == 2) {
             _values.put("cat1", categorias.get(0));
             _values.put("cat2", categorias.get(1));            
             } else if (categorias.size() == 3) {
             _values.put("cat1", categorias.get(0));
             _values.put("cat2", categorias.get(1));
             _values.put("cat3", categorias.get(2));
             }*/
            List<ResultadoParametro> listaP = resultadoEService.getResultadoParametros(resultadoExamen);
            System.out.println("RESULTADO PARAMETROS" + listaP.toString());
            //FALTA ORGANIZAR EL REPORT
            //_values.put("usd", "$");
            //Exportar a pdf 
            JasperReportAction.exportToPdf(REPORTE_RESULTADO_EXAMEN, listaP, _values, attachFileName);
        }

    }

    public void renderServiciosMedicos() {

        final String attachFileName = "ServiciosMedicos.pdf";
        List<Servicio> listaServiciosEnf = servicioMedS.todosServicios();
        Map<String, Object> _values = new HashMap<String, Object>();
        if (fechaInf != null && fechaSup != null) {
            listaServiciosEnf = servicioMedS.getServicioPorFechas(fechaInf, fechaSup);
        }

        String logo = getRealPath("/reportes/unl.png");
        //parametros 
        _values.put("numero", listaServiciosEnf.size());
        _values.put("usuarioResponsable", pLoggeado.getFullName());
        _values.put("logo", logo);
        _values.put("fechaDesde", fechaInf);
        _values.put("fechaHasta", fechaSup);
        _values.put("usd", "$");

        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_SERVICIOS_MEDICOS, listaServiciosEnf, _values, attachFileName);
        if (log.isDebugEnabled()) {
            log.debug("export as pdf");
        }
    }

    //FALTA 
    // Examenes entregados
    // 
    public Date getFechaInf() {
        return fechaInf;
    }

    public void setFechaInf(Date fechaInf) {
        this.fechaInf = fechaInf;
    }

    public Date getFechaSup() {
        return fechaSup;
    }

    public void setFechaSup(Date fechaSup) {
        this.fechaSup = fechaSup;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public PedidoExamenLaboratorio getPedido() {
        return pedido;
    }

    public void setPedido(PedidoExamenLaboratorio pedido) {
        this.pedido = pedido;
    }

    public ResultadoExamenLabClinico getResultadoExamen() {
        return resultadoExamen;
    }

    public void setResultadoExamen(ResultadoExamenLabClinico resultadoExamen) {
        this.resultadoExamen = resultadoExamen;
    }

    public String getParametroBusqued() {
        return parametroBusqued;
    }

    public void setParametroBusqued(String parametroBusqued) {
        this.parametroBusqued = parametroBusqued;
    }

}
