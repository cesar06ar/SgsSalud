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

package edu.sgssalud.faces;

import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;

import org.jboss.seam.faces.event.qualifier.Before;
import org.jboss.seam.faces.event.qualifier.RenderResponse;

/**
 * Sets style class <b>errorHighlight</b> on components that have validation errors. Sets the request scope
 * <b>#{focus}</b> variable to the first component with validation failure.
 */
@RequestScoped
public class InvalidFieldHighlightListener
{
   public void setFocus(@Observes @Before @RenderResponse PhaseEvent event)
   {
      final FacesContext facesContext = event.getFacesContext();

      facesContext.getViewRoot().visitTree(VisitContext.createVisitContext(facesContext), new VisitCallback() {
         boolean focused = false;

         @Override
         public VisitResult visit(VisitContext context, UIComponent target)
         {
            if (target instanceof UIInput)
            {
               if (!((UIInput) target).isValid())
               {
                  if (focused == false)
                  {
                     focused = true;
                     facesContext.getExternalContext().getRequestMap()
                              .put("focus", target.getId().replaceAll(":", "\\\\\\\\:"));
                  }

                  Map<String, Object> attributes = target.getAttributes();
                  String styleClass = (String) attributes.get("styleClass");
                  if ((styleClass == null) || "".equals(styleClass))
                  {
                     attributes.put("styleClass", "error");
                  }
                  else if (!styleClass.contains("error"))
                  {
                     attributes.put("styleClass", styleClass + " error");
                  }
               }

               else
               {
                  Map<String, Object> attributes = target.getAttributes();
                  String styleClass = (String) attributes.get("styleClass");
                  if (styleClass != null && styleClass.contains("error"))
                  {
                     styleClass = styleClass.replaceFirst("\\berror\\b", "");
                     attributes.put("styleClass", styleClass);
                  }
               }
            }
            return VisitResult.ACCEPT;
         }
      });

   }
}