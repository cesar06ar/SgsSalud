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

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.logging.Logger;

public class CacheControlPhaseListener implements PhaseListener
{
   private static final long serialVersionUID = 3470377662512577653L;
   private static final Logger log = Logger.getLogger(CacheControlPhaseListener.class);

   public CacheControlPhaseListener()
   {
      log.info("CacheControlPhaseListener is ACTIVE");
   }

   @Override
   public PhaseId getPhaseId()
   {
      return PhaseId.RENDER_RESPONSE;
   }

   @Override
   public void afterPhase(final PhaseEvent event)
   {}

   @Override
   public void beforePhase(final PhaseEvent event)
   {
      HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
               .getResponse();
      response.addHeader("Pragma", "no-cache");
      response.addHeader("Cache-Control", "no-cache");
      response.addHeader("Cache-Control", "must-revalidate");
      response.addHeader("Expires", "Mon, 1 Aug 1999 10:00:00 GMT");
   }
}