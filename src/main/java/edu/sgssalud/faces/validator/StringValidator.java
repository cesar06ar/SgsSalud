/**
 */
package edu.sgssalud.faces.validator;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import edu.sgssalud.util.StringValidations;
import edu.sgssalud.util.UI;

@FacesValidator("stringValidator")
@RequestScoped
public class StringValidator implements Validator {
    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value)
            throws ValidatorException {
        String field = value.toString();        
        if (!StringValidations.isPunctuatedTextUTF8(field) ) {
            FacesMessage msg = new FacesMessage(UI.getMessages("validation.badUTF8Input"), UI.getMessages("validation.badUTF8Input.detail"));
            throw new ValidatorException(msg);
        }else if(StringValidations.containsDecimal(field)){
            FacesMessage msg = new FacesMessage(UI.getMessages("validation.containsDigit"));
            throw new ValidatorException(msg);
        }
    }
}
