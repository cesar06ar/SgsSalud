/**
 */
package edu.sgssalud.faces.validator;

import edu.sgssalud.cdi.Current;
import edu.sgssalud.model.farmacia.Medicamento;
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

@FacesValidator("cantidadRecetaValidador")
@RequestScoped
public class CantidadRecetaValidador implements Validator {

    //public static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(CantidadRecetaValidador.class);
//    @Inject
//    @Current
//    private Medicamento medicamentoBean;
//    @Inject
    
    @Inject
    private InputElement<Integer> unidadesMedicacion;
    
    @Inject
    private InputElement<Integer> cantidadDisponible;

    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value)
            throws ValidatorException {

        Integer unidades = cantidadDisponible.getValue();//(Integer) component.getAttributes().get("cantidadMedicamentos");
        Integer cantidad = unidadesMedicacion.getValue();
        System.out.println("Cantidad de medicamento _________________________________");

        if (cantidad != null) {
            if (cantidad == 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "La cantidad debe ser mayor a 0", "");
                throw new ValidatorException(msg);
            } else if (unidades != null) {
                if (cantidad >= unidades) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "La cantidad debe ser menor a la cantidad disponible", "");
                    throw new ValidatorException(msg);
                }
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ingrese una cantidad", "");
            throw new ValidatorException(msg);
        }
    }
}
