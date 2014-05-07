/**
 */
package edu.sgssalud.faces.validator;

import edu.sgssalud.util.Dates;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import edu.sgssalud.util.UI;
import java.util.Calendar;
import java.util.Date;
import javax.inject.Inject;
import org.jboss.seam.faces.validation.InputElement;

@FacesValidator("dateValidatorTurno")
@RequestScoped
public class DateValidatorTurno implements Validator {
    
    @Inject
    private InputElement<Date> fechaTurno;
    
    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value)
            throws ValidatorException {
        String field = value.toString();
        Date now = new Date();
        Date fecha = fechaTurno.getValue();
        System.out.println("FECHAS  " + fecha + " _______" + now);
        if (fecha.before(now)) {
            FacesMessage msg = new FacesMessage(UI.getMessages("validation.date"), "La fecha debe ser posterior a la fecha actual");
            throw new ValidatorException(msg);
        }
    }
}
