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
package edu.sgssalud.security.authentication;

import java.io.IOException;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import edu.sgssalud.util.UI;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Authenticator.AuthenticationStatus;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.events.DeferredAuthenticationEvent;
import org.jboss.seam.security.events.LoggedInEvent;
import org.jboss.seam.security.events.LoginFailedEvent;
import org.jboss.seam.security.external.openid.OpenIdAuthenticator;
import org.jboss.seam.security.management.IdmAuthenticator;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;
import org.picketlink.idm.api.User;


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Named
@RequestScoped
public class Authentication
{
   Logger logger = Logger.getLogger(Authentication.class);

   @Inject
   private HttpSession session;

   @Inject
   private FacesContext context;

   @Inject
   private Identity identity;

   @Inject
   private Messages messages;

   @Inject
   private OpenIdAuthenticator openAuth;

  public void loginSuccess(@Observes final LoggedInEvent event, final NavigationHandler navigation,
            final FacesContext context,
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException
   {
      User user = event.getUser();
      logger.info("User logged in [{}, {}]", user.getId(), user.getKey());

      String viewId = context.getViewRoot().getViewId();
      logger.info("viewId [{}]", viewId);
      if (!"/pages/signup.xhtml".equals(viewId) && !"/pages/login.xhtml".equals(viewId) && !"/pages/reset.xhtml".equals(viewId) )
      {
         // TODO need a better way to navigate: this doesn't work with AJAX requests
          HttpInboundServletRewrite rewrite = new HttpInboundRewriteImpl(request, response);

          response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
          response.setHeader("Location", rewrite.getContextPath() + rewrite.getURL());
          response.flushBuffer();

          return;
      }
      else {
         String result = "/pages/home";
         navigation.handleNavigation(context, null, result + "?faces-redirect=true");
      }
   }

   /*
    * This is called outside of the JSF lifecycle.
    */
   public void openLoginSuccess(@Observes final DeferredAuthenticationEvent event, final NavigationHandler navigation)
   {
      if (event.isSuccess())
      {
         logger.info("User logged in with OpenID");
      }
      else
      {
         logger.info("User failed to login via OpenID, potentially due to cancellation");
      }
   }

   public void loginFailed(@Observes final LoginFailedEvent event, final NavigationHandler navigation)
            throws InterruptedException
   {
      if (!(OpenIdAuthenticator.class.equals(identity.getAuthenticatorClass())
      && AuthenticationStatus.DEFERRED.equals(openAuth.getStatus())))
      {
         Exception exception = event.getLoginException();
         if (exception != null)
         {
            logger.error(
                     "Login failed due to exception" + identity.getAuthenticatorName() + ", "
                              + identity.getAuthenticatorClass()
                              + ", " + identity); // TODO , exception );
            //messages.warn("Whoops! Something went wrong with your login. Care to try again? We'll try to figure out what went wrong.");
            messages.warn(UI.getMessages("common.login.fail"));
            
         }
         else
         {
            //messages.warn("Whoops! We don't recognize that username or password. Care to try again?");
             messages.warn(UI.getMessages("common.login.bad.usernamepassword"));
         }
         Thread.sleep(500);

         navigation.handleNavigation(context, null, "/pages/login?faces-redirect=true");
      }
   }

   public void login() throws InterruptedException
   {
      identity.setAuthenticatorClass(IdmAuthenticator.class);
      try {
         identity.login();
      }
      catch (Exception e) {
         identity.login();
      }
   }

   public String logout()
   {
      identity.setAuthenticatorClass(IdmAuthenticator.class);
      identity.logout();

      //session.invalidate();

      return "/pages/home?faces-redirect=true";
   }
}
