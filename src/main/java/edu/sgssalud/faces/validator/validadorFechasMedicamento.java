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
package edu.sgssalud.faces.validator;

import edu.sgssalud.util.FechasUtil;
import edu.sgssalud.util.UI;
import java.util.Date;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import org.jboss.seam.faces.validation.InputElement;

/**
 *
 * @author cesar
 */
@RequestScoped
@FacesValidator("validadorFechasMedicamento")
public class validadorFechasMedicamento implements Validator {
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(validadorFechasMedicamento.class);
//    @Inject
//    private InputElement<Date> fechaIngreso;
    @Inject
    private InputElement<Date> fechaElaboracion;
    @Inject
    private InputElement<Date> fechaCaducidad;
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        log.info("Ingreso a validar fechas");
        
        //Date fIngreso = fechaIngreso.getValue();
        Date fElaboracion = fechaElaboracion.getValue();
        Date fCaducidad = fechaCaducidad.getValue();        
        log.info(" fechaElab:"+fElaboracion+"  fechaCad: "+fCaducidad);
        if (fElaboracion != null && fCaducidad != null) {
            if (!fElaboracion.before(fCaducidad)) {  //metodo que compara si una fecha es anterior a la otra
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, UI.getMessages("La Fecha de Caducidad debe ser mayor a la Fecha de Elaboración"), ""));
            }
//            if (!fElaboracion.before(fIngreso)) {  //metodo que compara si una fecha es anterior a la otra
//                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, UI.getMessages("La Fecha de Elaboracion debe ser menor a la Fecha de Ingreso"), ""));
//            }
            if (FechasUtil.getFechaLimite(new Date(), fCaducidad) < 85) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, UI.getMessages("El Medicamento tiene fecha de caducidad muy corta"), ""));
            }
        }

    }
}
