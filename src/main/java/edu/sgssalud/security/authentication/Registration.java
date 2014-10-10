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

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import edu.sgssalud.cdi.LoggedIn;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.profile.ProfileService;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Authenticator.AuthenticationStatus;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.events.UserCreatedEvent;
import org.jboss.seam.security.external.openid.OpenIdAuthenticator;
import org.jboss.seam.security.management.IdmAuthenticator;
import org.ocpsoft.logging.Logger;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.PersistenceManager;
import org.picketlink.idm.api.User;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.PasswordCredential;


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Named
@RequestScoped
public class Registration
{
   private static Logger log = Logger.getLogger(Registration.class);

   @Inject
   private EntityManager em;

   private Messages msg;
   private Identity identity;
   private Credentials credentials;
   private IdentitySession security;

   private Profile profile;
   private ProfileService ps;

   @Inject
   private OpenIdAuthenticator oidAuth;

   @Inject
   private IdmAuthenticator idmAuth;

   public Registration()
   {}

   @Inject
   public Registration(@LoggedIn final Profile profile, final ProfileService profileService,
            final IdentitySession security,
            final Credentials credentials,
            final Identity identity, final Messages msg)
   {
      this.msg = msg;
      this.identity = identity;
      this.credentials = credentials;
      this.security = security;
      this.profile = profile;
      this.ps = profileService;
   }

   @PostConstruct
   public void init()
   {
      ps.setEntityManager(em);
   }

   private String username;
   private String password;
   private String passwordConfirm;
   private String email;

   @TransactionAttribute
   public String register() throws IdentityException
   {
      createUser();

      credentials.setUsername(username);
      credentials.setCredential(new PasswordCredential(password));

      oidAuth.setStatus(AuthenticationStatus.FAILURE);
      identity.setAuthenticatorClass(IdmAuthenticator.class);

      /*
       * Try twice to work around some state bug in Seam Security

       */
      String result = identity.login();
      if (Identity.RESPONSE_LOGIN_EXCEPTION.equals(result)) {
         result = identity.login();
      }

      return result;
   }

   @TransactionAttribute
   private void createUser() throws IdentityException
   {

      PersistenceManager identityManager = security.getPersistenceManager();
      User user = identityManager.createUser(username);

      AttributesManager attributesManager = security.getAttributesManager();
      attributesManager.updatePassword(user, password);
      attributesManager.addAttribute(user, "email", email);

      em.flush();


      Profile p = new Profile();
      p.setEmail(email);
      p.setUsername(username);
      p.getIdentityKeys().add(user.getKey());
      p.setUsernameConfirmed(true);
      ps.create(p);
   }

   @TransactionAttribute
   public void openRegister() throws IdentityException
   {
      idmAuth.setStatus(AuthenticationStatus.FAILURE);
      identity.setAuthenticatorClass(OpenIdAuthenticator.class);

      /*
       * Try twice to work around some state bug in Seam Security

       */
      String result = identity.login();
      if (Identity.RESPONSE_LOGIN_EXCEPTION.equals(result)) {
         result = identity.login();
      }
   }

   @TransactionAttribute
   public String confirmUsername()
   {
      Profile p = ps.getProfileById(profile.getId());
      p.setUsername(username);
      p.setUsernameConfirmed(true);
      ps.save(p);
      msg.info("Congrats! Your username is, and forever will be, \"" + p.getUsername() + "\".");
      return "/pages/home?faces-redirect=true";
   }

   /*
    * This is called outside of the JSF lifecycle.
    */
   public void registerSuccess(@Observes final UserCreatedEvent event)
   {
      Object user = event.getUser();
      log.info("User registered [{}]", user);
   }

   public String getUsername()
   {
      return username;
   }

   public void setUsername(final String username)
   {
      this.username = username;
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(final String password)
   {
      this.password = password;
   }

   public String getEmail()
   {
      return email;
   }

   public void setEmail(final String email)
   {
      this.email = email;
   }

   public String getPasswordConfirm()
   {
      return passwordConfirm;
   }

   public void setPasswordConfirm(final String passwordConfirm)
   {
      this.passwordConfirm = passwordConfirm;
   }
}
