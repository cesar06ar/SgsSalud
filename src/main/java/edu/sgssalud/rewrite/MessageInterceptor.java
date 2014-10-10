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
package edu.sgssalud.rewrite;

import java.util.Arrays;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.jboss.seam.international.status.Messages;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.util.URLBuilder;
import org.ocpsoft.rewrite.servlet.config.QueryString;
import org.ocpsoft.rewrite.servlet.util.QueryStringBuilder;




/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MessageInterceptor extends HttpConfigurationProvider
{
   @Inject
   private Messages messages;

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder.begin()

               .defineRule()
               .when(
                        QueryString.parameterExists("info")
                                 .or(QueryString.parameterExists("warning"))
                                 .or(QueryString.parameterExists("error"))
               )
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
                  {
                     for (String type : Arrays.asList("info", "warning", "error")) {
                        String error = event.getRequest().getParameter(type);
                        if (error != null)
                        {
                           if ("info".equals(type))
                              messages.info(error);
                           if ("warn".equals(type))
                              messages.warn(error);
                           if ("error".equals(type))
                              messages.error(error);
                        }
                     }

                      URLBuilder url = URLBuilder.createNew();
                      QueryStringBuilder query = url.appendPathSegments(event.getRequestPath())
                      .getQueryStringBuilder();
                     
                      query.removeParameter("info");
                      query.removeParameter("warn");
                      query.removeParameter("error");
                     
                      String target = url.toURL();
                      Forward.to(target).perform(event, context);
                  }
               });

   }

   @Override
   public int priority()
   {
      return 0;
   }
}
