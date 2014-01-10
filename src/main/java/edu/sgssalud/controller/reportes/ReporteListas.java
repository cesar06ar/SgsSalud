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
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.farmacia.MedicamentoService;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.paciente.PacienteServicio;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

/**
 *
 * @author cesar
 */
@RequestScoped
@Named(value = "reporteListas")
public class ReporteListas {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ReporteListas.class);

    private static final String REPORTE_USUARIOS = "Reporte";  //nombre del reporte .jasper   
    private static final String REPORTE_PACIENTES = "pacientes";
    private static final String REPORTE_FICHASMEDICAS = "listaFichasMed";
    private static final String REPORTE_CONSULTASMEDICAS = "listaConsultasMedicas";
    private static final String REPORTE_MEDICAMENTOS = "listaMedicamentos";

    @Inject
    @Web
    private EntityManager em;

    @Inject
    private ProfileService profileService;
    @Inject
    private PacienteServicio pacienteServicio;
    @Inject
    private FichaMedicaServicio fichaMedicaServicio;
    @Inject
    private ConsultaMedicaServicio consultaMedicaServicio;    
    
    @Inject
    private MedicamentoService medicamentosServicio;

    @Inject
    JasperReportAction JasperReportAction;

    /**
     * Default constructor.
     */
    public ReporteListas() {
    }

    @PostConstruct
    public void init() {
        profileService.setEntityManager(em);
        pacienteServicio.setEntityManager(em);
        fichaMedicaServicio.setEntityManager(em);
        consultaMedicaServicio.setEntityManager(em);
        medicamentosServicio.setEntityManager(em);
    }

    private String getRealPath(String path) {
        ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        return context.getRealPath(path);
    }

    /**
     * 
     public void render() {
     if (log.isDebugEnabled()) {
     log.debug("export as pdf");
     }
     final String mimeType = "application/pdf";
     final String attachFileName = "usuarios.pdf";
     final String reportTemplate = "/reportes/Reporte.jasper";

     if (log.isDebugEnabled()) {
     log.debug("mimeType@" + mimeType);
     log.debug("attachFileName@" + attachFileName);
     }

     ExternalContext externalContext = facesContext.getExternalContext();

     externalContext.setResponseContentType(mimeType);
     externalContext.addResponseHeader("Content-Disposition",
     "attachment;filename=" + attachFileName + "");

     InputStream sourceTemplate = resourceProvider
     .loadResourceStream(reportTemplate);
     String pathTemplate = getRealPath(reportTemplate);

     Map<String, Object> _values = new HashMap<String, Object>();
     _values.put("contacts", profileListService);
     _values.put("usd", "$");

     String stringReport = new VelocityTemplate(sourceTemplate,
     velocityContext).merge(_values);

     if (log.isDebugEnabled()) {
     log.debug("report source file content@" + stringReport);
     }
     // source
     ReportDefinition report;
     try {
     report = compiler.compile(new ByteArrayInputStream(stringReport
     .getBytes("UTF-8")));
     //JasperPrint reporte = JasperFillManager.fillReport(sourceTemplate, _values, new JREmptyDataSource());
     Report reportInstance = report.fill(new JREmptyDataSource(), null);

     pdfRenderer.render(reportInstance,
     externalContext.getResponseOutputStream());
     //JasperExportManager.exportReportToPdf(reporte);
     } catch (ReportException e) {
     e.printStackTrace();
     } catch (UnsupportedEncodingException e) {
     e.printStackTrace();
     } catch (IOException e) {
     e.printStackTrace();
     }

     facesContext.responseComplete();

     }
    
     public void render1() {
     if (log.isDebugEnabled()) {
     log.debug("export as pdf without apache velocity");
     }
     final String mimeType = "application/pdf";
     final String attachFileName = "usuarios.pdf";
     final String reportTemplate = "/reportes/Reporte.jrxml";

     if (log.isDebugEnabled()) {
     log.debug("mimeType@" + mimeType);
     log.debug("attachFileName@" + attachFileName);
     }

     ExternalContext externalContext = facesContext.getExternalContext();

     externalContext.setResponseContentType(mimeType);
     externalContext.addResponseHeader("Content-Disposition",
     "attachment;filename=" + attachFileName + "");

     InputStream sourceTemplate = resourceProvider
     .loadResourceStream(reportTemplate);

     // source
     ReportDefinition report;
     try {
     report = compiler.compile(sourceTemplate);
     Report reportInstance = report.fill(new JRBeanCollectionDataSource(
     profileListService.getResultList()), null);

     pdfRenderer.render(reportInstance,
     externalContext.getResponseOutputStream());
     } catch (ReportException e) {
     e.printStackTrace();
     } catch (UnsupportedEncodingException e) {
     e.printStackTrace();
     } catch (IOException e) {
     e.printStackTrace();
     }

     facesContext.responseComplete();
     }
    
     */
    public void renderProfile() {

        final String attachFileName = "usuarios.pdf";

        //parametros 
//        Map<String, Object> _values = new HashMap<String, Object>();
//        _values.put("contacts", profileListService);
//        _values.put("usd", "$");
        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_USUARIOS, profileService.findAll(), null, attachFileName);

        if (log.isDebugEnabled()) {
            log.debug("export as pdf");
        }
    }

    public void renderPacientes() {

        final String attachFileName = "pacientes.pdf";
        List<Paciente> pacientes = pacienteServicio.getPacientes();
        //parametros 
        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("numeroPacientes", pacientes.size());
        //_values.put("numeroPacientes", pacientes.size());
        _values.put("usd", "$");

        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_PACIENTES, pacientes, _values, attachFileName);

        if (log.isDebugEnabled()) {
            log.debug("export as pdf");
        }
    }

    public void renderFichasMedicas() {

        final String attachFileName = "fichasMedicas.pdf";
        List<FichaMedica> fichasMed = fichaMedicaServicio.getFichasMedicas();
        //parametros 
        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("nFichas", fichasMed.size());        
        _values.put("usd", "$");

        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_FICHASMEDICAS, fichasMed, _values, attachFileName);

//        if (log.isDebugEnabled()) {
//            log.debug("export as pdf");
//        }

    }

    public void renderConsultasMedicas() {

        final String attachFileName = "consultasMedicas.pdf";
        List<ConsultaMedica> consultMed = consultaMedicaServicio.getConsulasMedicas();
        //parametros 
        Map<String, Object> _values = new HashMap<String, Object>();

        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_CONSULTASMEDICAS, consultMed, _values, attachFileName);

        if (log.isDebugEnabled()) {
            log.debug("export as pdf");
        }

    }
    
    public void renderMedicamentos() {

        final String attachFileName = "lista_de_Medicamentos.pdf";
        List<Medicamento> medicamentos = medicamentosServicio.buscarTodos();
        //parametros 
        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("nMedicamentos", medicamentos.size());        
        _values.put("usd", "$");

        //Exportar a pdf 
        JasperReportAction.exportToPdf(REPORTE_MEDICAMENTOS, medicamentos, _values, attachFileName);

//        if (log.isDebugEnabled()) {
//            log.debug("export as pdf");
//        }

    }

}
