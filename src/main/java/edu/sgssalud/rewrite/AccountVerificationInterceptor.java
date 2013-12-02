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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import edu.sgssalud.security.Account;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Invoke;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.el.El;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @adapter <a href="mailto:jlgranda81@gmail.com">José Luis Granda</a>
 */
public class AccountVerificationInterceptor extends HttpConfigurationProvider {

    @Inject
    private Account account;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Configuration getConfiguration(final ServletContext context) {
        ConfigurationBuilder config = ConfigurationBuilder.begin();
//      account.setEntityManager(em);
//      Profile current = account.getLoggedIn();
//      if (current.isPersistent() && !current.isUsernameConfirmed())
//      {
//         return config.defineRule()
//                  .when(DispatchType.isRequest()
//                           .and(Direction.isInbound())
//                           .and(GlueResources.excluded()))
//                  .perform(Forward.to("/account/confirm"))
//
//                  .addRule(Join.path("/account/confirm").to("/pages/accountConfirm.xhtml"));
//      }
        return config;
//                .addRule(Join.path("/").to("/pages/loggedOffHome.xhtml"))
//                .defineRule()
//                .when(Direction.isInbound().and(Path.matches("/logout")))
////                .perform(Invoke.binding(El.retrievalMethod("#{authentication.logout}"))
////                        .and(Redirect.temporary(context.getContextPath() + "/")));
//                .perform(new HttpOperation() {
//                    @Override
//                    public void performHttp(HttpServletRewrite event, EvaluationContext context) {
//                        event.getRequest().getSession().invalidate();
//                    }
//                }.and(Redirect.temporary(context.getContextPath() + "/")));
    }

    @Override
    public int priority() {
        return 0;
        //return Integer.MIN_VALUE;
    }
}
