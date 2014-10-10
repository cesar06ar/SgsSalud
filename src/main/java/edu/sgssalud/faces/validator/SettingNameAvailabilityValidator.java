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
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.service.SettingService;
import edu.sgssalud.util.UI;

/**
 *
 * @author lucho
 */
@RequestScoped
@FacesValidator("wordAvailabilityValidator")
public class SettingNameAvailabilityValidator implements Validator {

    @Inject
    private EntityManager em;
    @Inject 
    private SettingService settingService;
    @Inject
    @Current
    private Setting setting;

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object value)
            throws ValidatorException {        
        settingService.setEntityManager(em);
        String currentName = "";
        if (setting.isPersistent()) {  //controla objeto en edicion            
            currentName = settingService.find(setting.getId()).getName();
        }

        if (!currentName.equals(value)) {
            if (value instanceof String ) {
                if (!settingService.isNameAvailable((String) value)) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, UI.getMessages("El nombre indicado para esta propiedad ya está en us"), null));
                }
            }
        } 
    }
}
