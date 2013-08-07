/**
 * *
 * You should have received a copy of the GNU General Public License along with
 * Glue. If not, see <http://www.gnu.org/licenses/>.
 * 
* For individuals or entities who wish to use Glue privately, or internally,
 * the following terms do not apply:
 * 
* For OEMs, ISVs, and VARs who wish to distribute Glue with their products, or
 * host their product online, Eqaula provides flexible OEM commercial licenses.
 * 
* Optionally, Customers may choose a Commercial License. For additional
 * details, contact an Eqaula representative (sales@eqaula.org)
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

@FacesValidator("valueTextPropertyValidator")
@RequestScoped
public class ValueTextPropertyValidator implements Validator {

    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value)
            throws ValidatorException {
        
        String field = value.toString();
        if (field.contains("*")) {
            field = field.replace("*", "");
        }
        if (!StringValidations.isPunctuatedTextUTF8(field)) {
            FacesMessage msg = new FacesMessage(UI.getMessages("validation.badUTF8Input"), UI.getMessages("validation.badUTF8Input.detail"));
            throw new ValidatorException(msg);
        }
    }
}
