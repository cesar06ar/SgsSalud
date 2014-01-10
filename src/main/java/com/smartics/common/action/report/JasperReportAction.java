/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartics.common.action.report;

//import com.smartics.common.jsf.JsfUtil;
import com.smartics.common.report.JasperReportService;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author wduck
 */
//@Named(value = "jasperReportAction")
@Dependent
public class JasperReportAction implements Serializable{

    @EJB
    JasperReportService jasperReportService;
    
    protected String REPORT_DIR;
    protected String SUB_REPORT_DIR;
    protected String EXTENSION_TEMPLATE;
    private static final Logger LOGGER = Logger.getLogger(JasperReportAction.class.getName());
    
    
    
    /**
     * Creates a new instance of JasperReportAction
     */
    public JasperReportAction() {
        REPORT_DIR = "reportes/";
        SUB_REPORT_DIR = "reportes/";
        EXTENSION_TEMPLATE = ".jasper";
    }
    
    private String getRealPath(String path){
        ServletContext context =  (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        return context.getRealPath(path);
    }
    
    private String buildRealPathFromReportFileName(String sourceReportFile){
        return getRealPath("/" + REPORT_DIR + sourceReportFile + EXTENSION_TEMPLATE);
    }
    
    public void exportToPdf(String sourceReportFile, Map<String, Object> parameters, 
            String outputFileName) {
        
        String pathTemplate = buildRealPathFromReportFileName(sourceReportFile);
        //LOGGER.log(Level.INFO, "----> pathTemplate {0}", pathTemplate);
        
        FacesContext context = FacesContext.getCurrentInstance();
//        InputStream reportStream =
//                    context.getExternalContext().getResourceAsStream(
//                            pathTemplate);
//        
        try {
            
            HttpServletResponse response =
                    (HttpServletResponse)context.getExternalContext().getResponse();
            
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename="+ outputFileName);
            //response.addHeader("Content-Disposition", "attachment; filename="+ outputFileName);
            
            ServletOutputStream servletOutputStream = response.getOutputStream();
            
            jasperReportService.exportToPdfStream(pathTemplate, parameters, 
                    servletOutputStream);
            
            servletOutputStream.flush();
            servletOutputStream.close();
                        
        } catch (JRException ex) {
            LOGGER.log(Level.SEVERE, "-- ERROR EN JASPER REPORT ACTION {0}", ex);
            //JsfUtil.addErrorMessage(ex, "Error al generar el reporte: " + outputFileName);  //agregar al messages
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "-- ERROR EN JASPER REPORT ACTION {0}", e);
        }
        finally{
            LOGGER.log(Level.INFO, "----> Saliendo de la generacion del reporte: {0}", 
                new Object[]{pathTemplate});
            context.responseComplete();            
        }
    }
    
    
    public void exportToPdf(String sourceReportFile, Collection<?> data, 
            Map<String, Object> parameters, String outputFileName) {
        
        String pathTemplate = buildRealPathFromReportFileName(sourceReportFile);
        LOGGER.log(Level.INFO, "----> pathTemplate {0}", pathTemplate);
        
        FacesContext context = FacesContext.getCurrentInstance();
//        InputStream reportStream =
//                    context.getExternalContext().getResourceAsStream(
//                            pathTemplate);
//        
        try {
            
            HttpServletResponse response =
                    (HttpServletResponse)context.getExternalContext().getResponse();
            
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename="+ outputFileName);
            //response.addHeader("Content-Disposition", "attachment; filename="+ outputFileName);
            
            ServletOutputStream servletOutputStream = response.getOutputStream();
            
            jasperReportService.exportToPdfStream(pathTemplate, data, parameters, 
                    servletOutputStream);
            
            servletOutputStream.flush();
            servletOutputStream.close();
                        
        } catch (JRException ex) {
            LOGGER.log(Level.SEVERE, "-- ERROR EN JASPER REPORT ACTION {0}", ex);
            //JsfUtil.addErrorMessage(ex, "Error al generar el reporte: " + outputFileName);
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "-- ERROR EN JASPER REPORT ACTION {0}", e);
        }
        finally{
            LOGGER.log(Level.INFO, "----> Saliendo de la generacion del reporte: {0}", 
                new Object[]{pathTemplate});
            context.responseComplete();            
        }
    }
}

