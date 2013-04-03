/**
 * This file is part of Glue: Adhesive BRMS
 * 
* Copyright (c)2012 José Luis Granda <jlgranda@eqaula.org> (Eqaula Tecnologías
 * Cia Ltda) Copyright (c)2012 Eqaula Tecnologías Cia Ltda (http://eqaula.org)
 * 
* If you are developing and distributing open source applications under the GNU
 * General Public License (GPL), then you are free to re-distribute Glue under
 * the terms of the GPL, as follows:
 * 
* GLue is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
* Glue is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
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
package edu.sgssalud.rewrite;

import java.net.MalformedURLException;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.bind.ParameterizedPattern;
import org.ocpsoft.rewrite.bind.RegexCapture;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpCondition;
import org.ocpsoft.rewrite.servlet.config.IPath.PathParameter;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.util.ParameterStore;


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Resource extends HttpCondition {
    // TODO move to rewrite proper

     // TODO move to rewrite proper
   private static final Logger log = Logger.getLogger(Resource.class);

   private ParameterizedPattern resource = new ParameterizedPattern("/pages/{page}.xhtml");

   private Resource(final String resource)
   {
      this.resource = new ParameterizedPattern(resource);

   
      for (RegexCapture parameter : this.resource.getParameters().values()) {
         where(parameter.getName()).bindsTo(Evaluation.property(parameter.getName()));
      }
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      String file = resource.build(event, context);
      try {
         return event.getRequest().getServletContext().getResource(file) != null;
      }
      catch (MalformedURLException e) {
         log.debug("Invalid file format [{}]", file);
      }
      return false;
   }

   public static Resource exists(final String resource)
   {
      return new Resource(resource);
   }

   public PathParameter where(String param)
   {
      return parameters.where(param, new PathParameter(null, this.resource.getParameter(param)));
}

   public PathParameter where(String param, Binding binding)
   {
      return where(param).bindsTo(binding);
   }
    private final ParameterStore<PathParameter> parameters = new ParameterStore<PathParameter>();
}
