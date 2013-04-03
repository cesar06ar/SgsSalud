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
package edu.sgssalud.reports;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
//import net.sf.jasperreports.engine.JRDataSource;
//import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.config.SettingHome;
import edu.sgssalud.service.ProfileListService;
import org.jboss.seam.reports.Report;
import org.jboss.seam.reports.ReportCompiler;
import org.jboss.seam.reports.ReportDefinition;
import org.jboss.seam.reports.ReportRenderer;
import org.jboss.seam.reports.jasper.annotations.Jasper;
import org.jboss.seam.reports.output.PDF;
import org.jboss.solder.resourceLoader.Resource;

/**
 *
 * @author cesar
 */
@Named(value = "profileReport")
@RequestScoped
public class ProfileReport {
    
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ProfileReport.class);
    @Inject
    @Web
    private EntityManager em;

    @Inject
    @Resource("profilesReport.jrxml")
    InputStream sourceReport;
    @Inject
    @Jasper
    ReportCompiler compiler;
    @Jasper
    JRDataSource jasperDataSource;
    @Inject
    @Jasper
    @PDF
    ReportRenderer pdfRenderer;
    @Inject
    ProfileListService profileListService;
    
    @PostConstruct
    public void init() {
       log.info("eqaula ----> started report profile!");
    }

    public ByteArrayOutputStream generateProfileReport() throws Exception {

        ReportDefinition report = compiler.compile(sourceReport);

        Map<String, Object> params = new HashMap<String, Object> ();
        params.put("ReportTitle", "Profile Report");

        //TODO cargar el reporte desde una consulta SQL
        Report reportInstance = report.fill(
                new JRBeanCollectionDataSource(profileListService.getResultList()), params);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        pdfRenderer.render(reportInstance, os);

        return os;

    }

    public void createPdf() throws Exception {

        log.info("eqaula ----> print to pdf");
        byte[] pdfData = generateProfileReport().toByteArray();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response =
                (HttpServletResponse) externalContext.getResponse();

        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition",
                "attachment; filename=\"profileReport.pdf\"");

        OutputStream output = response.getOutputStream();
        output.write(pdfData);
        output.close();

        facesContext.responseComplete();
    }
}
