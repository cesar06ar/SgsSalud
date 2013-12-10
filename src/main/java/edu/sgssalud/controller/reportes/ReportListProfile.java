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

import edu.sgssalud.cdi.Web;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.ProfileListService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.jboss.seam.mail.templating.velocity.CDIVelocityContext;
import org.jboss.seam.mail.templating.velocity.VelocityTemplate;
import org.jboss.seam.reports.Report;
import org.jboss.seam.reports.ReportCompiler;
import org.jboss.seam.reports.ReportDefinition;
import org.jboss.seam.reports.ReportRenderer;
import org.jboss.seam.reports.exceptions.ReportException;
import org.jboss.seam.reports.jasper.annotations.Jasper;
import org.jboss.seam.reports.output.PDF;
import org.jboss.solder.resourceLoader.ResourceProvider;

/**
 *
 * @author cesar
 */
@RequestScoped
@Named(value = "reportListProfile")
public class ReportListProfile {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ReportListProfile.class);

    @Inject
    private ResourceProvider resourceProvider;

    @Inject
    FacesContext facesContext;

    @Inject
    transient CDIVelocityContext velocityContext;

    @Inject
    @Jasper
    private transient ReportCompiler compiler;
    // @Inject
    // @XLS
    // @Jasper
    // private transient ReportRenderer xslRenderer;
    @Inject
    @PDF
    @Jasper
    private transient ReportRenderer pdfRenderer;

    @Inject
    private ProfileListService profileListService;

    @Inject
    @Web
    private EntityManager em;

    /**
     * Default constructor.
     */
    public ReportListProfile() {
    }

    @PostConstruct
    public void init(){
        profileListService.setEntityManager(em);
    }
    
    public void render() {
        if (log.isDebugEnabled()) {
            log.debug("export as pdf");
        }
        final String mimeType = "application/pdf";
        final String attachFileName = "usuarios.pdf";
        final String reportTemplate = "/reportes/contancts1.jrxml";

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
            Report reportInstance = report.fill(new JREmptyDataSource(), null);

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
}
