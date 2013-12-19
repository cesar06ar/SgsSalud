/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartics.common.report;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

/**
 *
 * @author wduck
 */
@Stateless(name = "jasperReportService")
public class JasperReportService {
    
    private static final Logger LOG = 
            Logger.getLogger(JasperReportService.class.getName());

    public void exportToPdfStream(String pathTemplate, Collection<?> data, 
            Map<String, Object> parameters, OutputStream outputStream) 
            throws JRException  {
        LOG.log(Level.INFO, "---> ReportUrl: {0}", pathTemplate);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                data);        
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(
                pathTemplate, parameters, dataSource);
        JasperExportManager.exportReportToPdfStream(reportJasperPrint, outputStream);
        
    }
    
    public void exportToPdfStream(String pathTemplate, Map<String, Object> parameters, 
            OutputStream outputStream) throws JRException  {
        //LOG.log(Level.INFO,"----- Generando Reporte con {0} ", pathTemplate);
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(
                pathTemplate, parameters, new JREmptyDataSource());
        //LOG.info("----- Exportando el Reporte...");
        JasperExportManager.exportReportToPdfStream(reportJasperPrint, outputStream);
        //LOG.info("----- Saliendo Exportando el Reporte...");
    }
    
    public void exportToPdfStream(String pathTemplate, Connection connection,
            Map<String, Object> parameters, OutputStream outputStream) 
            throws JRException  {
        LOG.log(Level.INFO,"----- Generando Reporte con {0} ", pathTemplate);
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(
                pathTemplate, parameters, connection);
        LOG.info("----- Exportando el Reporte...");
        JasperExportManager.exportReportToPdfStream(reportJasperPrint, outputStream);
        
        
        
    }
    
    public void exportToPdfStream(InputStream inputSource, Collection<?> data, 
            Map<String, Object> parameters, OutputStream outputStream) 
            throws JRException  {
        
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);        
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(inputSource, 
                parameters, dataSource);
        JasperExportManager.exportReportToPdfStream(reportJasperPrint, outputStream);
        
    }
    
    public void exportToPdfStream(InputStream inputSource, Map<String, Object> parameters, 
            OutputStream outputStream) throws JRException  {
        
        //LOG.info("----- Generando Reporte...");
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(inputSource, 
                parameters, new JREmptyDataSource());
        //LOG.info("----- Exportando el Reporte...");
        JasperExportManager.exportReportToPdfStream(reportJasperPrint, outputStream);
        
    }
    
    public void exportToPdfStream(InputStream inputSource, Connection connection,
            Map<String, Object> parameters, OutputStream outputStream) 
            throws JRException  {
        
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(inputSource, 
                parameters, connection);
        JasperExportManager.exportReportToPdfStream(reportJasperPrint, outputStream);
        
    }
    
    public void exportToSpreadSheetStream(String pathTemplate, Collection<?> data, 
            Map<String, Object> parameters, OutputStream outputStream) 
            throws JRException  {
        LOG.log(Level.INFO, "---> ReportUrl: {0}", pathTemplate);       
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                data);        
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(
                pathTemplate, parameters, dataSource);
        exportToSpreadSheetStream(reportJasperPrint, outputStream);
        
    }
    
    public void exportToSpreadSheetStream(String pathTemplate, 
            Map<String, Object> parameters, OutputStream outputStream) 
            throws JRException  {
        //LOG.log(Level.INFO, "---> ReportUrl: {0}", pathTemplate);
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(
                pathTemplate, parameters, new JREmptyDataSource());
        exportToSpreadSheetStream(reportJasperPrint, outputStream);
    }
    
    public void exportToSpreadSheetStream(String pathTemplate, Connection connection,
            Map<String, Object> parameters, OutputStream outputStream) 
            throws JRException  {
        //LOG.log(Level.INFO, "---> ReportUrl: {0}", pathTemplate);
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(
                pathTemplate, parameters, connection);
        exportToSpreadSheetStream(reportJasperPrint, outputStream);        
    }
    
    public void exportToSpreadSheetStream(InputStream inputSource, Collection<?> data, 
            Map<String, Object> parameters, OutputStream outputStream) 
            throws JRException  {
        
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);        
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(inputSource, 
                parameters, dataSource);
        exportToSpreadSheetStream(reportJasperPrint, outputStream);
        
    }
    
    public void exportToSpreadSheetStream(InputStream inputSource, 
            Map<String, Object> parameters, OutputStream outputStream) 
            throws JRException  {
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(inputSource, 
                parameters, new JREmptyDataSource());
        exportToSpreadSheetStream(reportJasperPrint, outputStream);        
    }
    
    public void exportToSpreadSheetStream(InputStream inputSource, Connection connection,
            Map<String, Object> parameters, OutputStream outputStream) 
            throws JRException  {
        
        JasperPrint reportJasperPrint = JasperFillManager.fillReport(inputSource, 
                parameters, connection);
        exportToSpreadSheetStream(reportJasperPrint, outputStream);
    }
    
    public void exportToSpreadSheetStream(JasperPrint reportJasperPrint,
            OutputStream outputStream) throws JRException  {
        
        JRXlsExporter exporter = new JRXlsExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT,
                reportJasperPrint);
        exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
                Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
                Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED,
                Boolean.TRUE);
        exporter.setParameter(
                JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                Boolean.TRUE);
        exporter.setParameter(
                JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,
                Boolean.TRUE);
        exporter.setParameter(
                JRXlsExporterParameter.IS_DETECT_CELL_TYPE,
                Boolean.TRUE);
        exporter.setParameter(
                JRXlsExporterParameter.IGNORE_PAGE_MARGINS,
                Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outputStream);
        exporter.exportReport();

    }
    
    public void exportToRtfStream(JasperPrint reportJasperPrint, OutputStream outputStream) throws JRException {

        //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JRRtfExporter exporter = new JRRtfExporter();
        
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportJasperPrint);
	exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        //exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);

        exporter.exportReport();
        
        /*
        byte[] bytes = byteArrayOutputStream.toByteArray();

        response.setHeader("Content-Disposition", "attachment; filename=\"" + outputFileName + "\"");
        response.setContentType("application/rtf");

        // w:
        response.setContentLength(bytes.length);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes, 0, bytes.length);
        outputStream.flush();
        FacesContext.getCurrentInstance().getApplication()
                .getStateManager().saveView(FacesContext.getCurrentInstance());
        FacesContext.getCurrentInstance().responseComplete();
	//fin w:
        */
    }
    
    /**
     * TODO: por hacer los demas metodos para convertir a RTF igual que los 
     * metodos anteriores
     * 
     */
     
    /**
     * Agregar el datasource como parametro
     * @param datasourceName
     * @param data
     * @param parameters
     */
    public void addDataSourceAsParameter(String datasourceName,
            Collection<?> data, Map<String, Object> parameters) {
        JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(
                data);
        parameters.put(datasourceName, datasource);
    }

}


