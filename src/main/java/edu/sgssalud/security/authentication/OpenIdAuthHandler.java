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
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.profile.ProfileService;
import org.jboss.seam.security.Authenticator.AuthenticationStatus;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.events.DeferredAuthenticationEvent;
import org.jboss.seam.security.external.api.ResponseHolder;
import org.jboss.seam.security.external.openid.OpenIdAuthenticator;
import org.jboss.seam.security.external.openid.api.OpenIdPrincipal;
import org.jboss.seam.security.external.spi.OpenIdRelyingPartySpi;
import org.jboss.seam.security.management.picketlink.IdentitySessionProducer;
import org.jboss.seam.transaction.Transactional;


@TransactionAttribute
public class OpenIdAuthHandler implements OpenIdRelyingPartySpi
{
   @Inject
   private EntityManager em;

   @Inject
   private Identity identity;

   @Inject
   private ProfileService profileService;

   @Inject
   private ServletContext servletContext;

   @Inject
   private OpenIdAuthenticator openIdAuthenticator;

   @Inject
   private Event<DeferredAuthenticationEvent> deferredAuthentication;

   @Override
   @Transactional
   public void loginSucceeded(final OpenIdPrincipal principal, final ResponseHolder responseHolder)
   {
      try {
         openIdAuthenticator.success(principal);
         deferredAuthentication.fire(new DeferredAuthenticationEvent(true));

         /*
          * If this is a new registration, we need to create a profile for this OpenId.
          */
         if (attachProfile(principal, responseHolder))
         {
            responseHolder.getResponse().sendRedirect(servletContext.getContextPath() + "/");
            return;
         }
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
      deferredAuthentication.fire(new DeferredAuthenticationEvent(false));
   }

   @Override
   public void loginFailed(final String message, final ResponseHolder responseHolder)
   {
      try {
         deferredAuthentication.fire(new DeferredAuthenticationEvent(false));
//         openIdAuthenticator.setStatus(AuthenticationStatus.FAILURE);
         responseHolder.getResponse().sendRedirect(servletContext.getContextPath() + "/signup");
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   @PostConstruct
   public void init()
   {
      profileService.setEntityManager(em);
   }

   private boolean attachProfile(final OpenIdPrincipal principal, final ResponseHolder responseHolder)
   {
      try
      {
         Map<String, Object> sessionOptions = new HashMap<String, Object>();
         sessionOptions.put(IdentitySessionProducer.SESSION_OPTION_ENTITY_MANAGER, em);

         String key = identity.getUser().getKey();
         String email = principal.getAttribute("email");

         try {
            Profile profileByEmail = profileService.getProfileByEmail(email);
            if (!profileService.hasProfileByIdentityKey(key))
            {
               identity.logout();

               responseHolder.getResponse().sendRedirect(
                        servletContext.getContextPath()
                                 + "/signup?error=The email address used by your OpenID account is already " +
                                 "registered with a username on this website. " +
                                 "Please log in with that account and visit the " +
                                 "Accounts page to merge your profiles.");
            }
            else if (profileByEmail.equals(profileService.getProfileByIdentityKey(key)))
            {
               return true;
            }
            else
            {
               identity.logout();

               responseHolder.getResponse().sendRedirect(
                        servletContext.getContextPath()
                                 + "/signup?error=The email address used by your OpenID account is already " +
                                 "registered with a username on this website. " +
                                 "Please log in with that account and visit the " +
                                 "Accounts page to merge your profiles.");
            }
         }
         catch (NoResultException e) {
            if (!profileService.hasProfileByIdentityKey(key))
            {
               Profile p = new Profile();
               p.getIdentityKeys().add(key);
               p.setEmail(email);
               p.setName(principal.getAttribute("firstName") + " " + principal.getAttribute("lastName"));
               p.setUsername(profileService.getRandomUsername(p.getName()));
               profileService.create(p);
               return true;
            }
         }
      }
      catch (Exception e) {
         try {
            identity.logout();
         }
         catch (Exception e2) {}

         throw new RuntimeException(e);
      }

      return false;
   }
}