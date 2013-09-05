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

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import edu.sgssalud.cdi.Current;
import edu.sgssalud.cdi.LoggedIn;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.UI;

/**
 *
 * @author cesar
 */
@RequestScoped
@FacesValidator("dniDisponiblePaciente")
public class validadorDniDisponiblePaciente implements Validator {

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private PacienteServicio ps;
    @Inject
    @Current
    private Paciente paciente;

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object value)
            throws ValidatorException {
        ps.setEntityManager(em);
        String currentDni = "";
        //System.out.println("Perfil de usuario es: "+paciente.toString());
        if (paciente.isPersistent()) {
            currentDni = ps.find(paciente.getId()).getCedula();
            //System.out.println("el dni es: " + currentDni);
        }
        if (!currentDni.equals(value)) {
            if (!currentDni.equals(value)) {
                if (value instanceof String) {

                    ps.setEntityManager(em);
                    //System.out.println("dni ingresada diferente al valor en la base de datos: ");
                    if (!ps.isDniDisponible((String) value)) {
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, UI.getMessages("validator.dni"), null));
                    }
                }
            }
        }

    }
}
