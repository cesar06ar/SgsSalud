/**
* This file is part of Glue: Adhesive BRMS
*
* Copyright (c)2012 José Luis Granda <jlgranda@eqaula.org> (Eqaula Tecnologías Cia Ltda)
* Copyright (c)2012 Eqaula Tecnologías Cia Ltda (http://eqaula.org)
*
* If you are developing and distributing open source applications under
* the GNU General Public License (GPL), then you are free to re-distribute Glue
* under the terms of the GPL, as follows:
*
* GLue is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Glue is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Glue. If not, see <http://www.gnu.org/licenses/>.
*
* For individuals or entities who wish to use Glue privately, or
* internally, the following terms do not apply:
*
* For OEMs, ISVs, and VARs who wish to distribute Glue with their
* products, or host their product online, Eqaula provides flexible
* OEM commercial licenses.
*
* Optionally, Customers may choose a Commercial License. For additional
* details, contact an Eqaula representative (sales@eqaula.org)
*/
package edu.sgssalud.security.validator;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import edu.sgssalud.cdi.LoggedIn;

import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.profile.ProfileService;

@RequestScoped
@FacesValidator("signupUsernameAvailabilityValidator")
public class SignupUsernameAvailabilityValidator implements Validator
{
   @Inject
   private EntityManager em;

   @Inject
   private ProfileService ps;

   @Inject
   @LoggedIn
   private Profile profile;

   @Override
   public void validate(final FacesContext context, final UIComponent comp, final Object value)
            throws ValidatorException
   {
      if (value instanceof String && !value.equals(profile.getUsername())) {
         ps.setEntityManager(em);
         if (!ps.isUsernameAvailable((String) value))
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre de usuario no esta disponible", null));
      }
   }
}