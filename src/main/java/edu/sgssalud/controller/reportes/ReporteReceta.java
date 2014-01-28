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
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.service.generic.CrudService;
import edu.sgssalud.service.generic.QueryParameter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author cesar
 */
@RequestScoped
@Named(value = "reporteReceta")
public class ReporteReceta implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ReporteReceta.class);

    private static final String REPORTE_RECETA = "recetaMedica";  //nombre del reporte .jasper   

    @EJB
    CrudService crudService;

    @Inject
    JasperReportAction JasperReportAction;

    public void renderReceta() {
        System.out.println("INGRESO a RECETA REPORTE________");

        FacesContext context = FacesContext.getCurrentInstance();
        final String attachFileName = "receta.pdf";
        //List<Paciente> pacientes = pacienteServicio.getPacientes();
        //parametros 

        String medicaciones = null;
        String indicaciones = null;
        String nombres = null;
        String apellidos = null;
        String recetaId = null;
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {

        } else {
            medicaciones = context.getExternalContext().getRequestParameterMap().get("medicaciones");
            indicaciones = context.getExternalContext().getRequestParameterMap().get("indicaciones");
            nombres = context.getExternalContext().getRequestParameterMap().get("nombres");
            apellidos = context.getExternalContext().getRequestParameterMap().get("apellidos");
            recetaId = context.getExternalContext().getRequestParameterMap().get("recetaId");
        }

        System.out.println("Valores " + medicaciones + ", " + indicaciones + ", " + nombres + ", " + apellidos + ", " + recetaId);

        Map<String, Object> _values = new HashMap<String, Object>();
        _values.put("nombres", nombres);
        _values.put("apellidos", apellidos);        
        _values.put("recetaId", Long.parseLong(recetaId));
        _values.put("usd", "$");

        //Exportar a pdf 
        List<Receta> recetas = crudService.findWithNamedQuery("Receta.buscarPorId", QueryParameter.with("recetaId", Long.parseLong(recetaId)).parameters());
        System.out.println("PASA AL JASPER_ REPORT" + recetas.toString());
        JasperReportAction.exportToPdf(REPORTE_RECETA, _values, attachFileName);

        if (log.isDebugEnabled()) {
            log.debug("export as pdf");
        }
    }
}
