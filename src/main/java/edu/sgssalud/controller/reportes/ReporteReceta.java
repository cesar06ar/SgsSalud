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
package edu.sgssalud.controller.reportes;

import com.smartics.common.action.report.JasperReportAction;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.service.farmacia.RecetaServicio;
import java.util.ArrayList;
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
@Named(value = "reporteReceta")
public class ReporteReceta {

    //private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ReporteReceta.class);
    private static final String REPORTE_RECETA = "recetaMedica";  //nombre del reporte .jasper   
    
    @Inject
    @Web
    private EntityManager em;
    @Inject
    JasperReportAction JasperReportAction;
    @Inject
    private RecetaServicio recetaServicio;

    private Receta receta;

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }

    @PostConstruct
    public void init() {
        recetaServicio.setEntityManager(em);
    }

    public void renderReceta() {
        
        ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String logo = context.getRealPath("/reportes/unl.png");
        final String attachFileName = "receta.pdf";                      
        if (receta.isPersistent()) {
            Map<String, Object> _values = new HashMap<String, Object>();
            _values.put("nombres", receta.getPaciente().getNombres()+" "+receta.getPaciente().getApellidos());           
            _values.put("numReceta", receta.getNumvalue());
            _values.put("fechaE", receta.getFechaEntrega());                  
            _values.put("logo", logo);
            _values.put("medico", receta.getResponsableEmision().getFullName());
            _values.put("usd", "$");

            //Exportar a pdf 
            List<Receta> recetas = new ArrayList<Receta>();
            recetas.add(receta);
            //System.out.println("PASA AL JASPER_ REPORT" + recetas.toString());
            JasperReportAction.exportToPdf(REPORTE_RECETA, recetas, _values, attachFileName);
        } 
    }
}
