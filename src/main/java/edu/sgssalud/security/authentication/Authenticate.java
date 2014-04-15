/**
 *
 */
package edu.sgssalud.security.authentication;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.profile.ProfileService;
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
import javax.persistence.EntityManager;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Authenticator.AuthenticationStatus;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.events.DeferredAuthenticationEvent;
import org.jboss.seam.security.events.LoggedInEvent;
import org.jboss.seam.security.events.LoginFailedEvent;
import org.jboss.seam.security.external.openid.OpenIdAuthenticator;
import org.jboss.seam.security.management.IdmAuthenticator;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;
import org.picketlink.idm.api.Credential;
import org.picketlink.idm.api.User;
import edu.sgssalud.service.paciente.PacienteServicio;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jboss.seam.security.Authenticator;
import org.jboss.seam.security.BaseAuthenticator;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.picketlink.idm.impl.api.model.SimpleUser;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Named
@RequestScoped
public class Authenticate extends BaseAuthenticator implements Authenticator {

    //private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(Authenticate.class);
    Logger logger = Logger.getLogger(Authenticate.class);
    @Inject
    private HttpSession session;
    @Inject
    private FacesContext context;
    @Inject
    private Credentials credencials;

    @Inject
    private Identity identity;
    @Inject
    private IdentitySession security;
    @Inject
    private Messages messages;
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private ProfileService profileService;
    @Inject
    private PacienteServicio pacienteServic;
    @Inject
    private OpenIdAuthenticator openAuth;

    @PostConstruct
    public void init() {
        pacienteServic.setEntityManager(em);
        profileService.setEntityManager(em);
    }

    @Override
    public void authenticate() {
        identity.setAuthenticatorClass(IdmAuthenticator.class);
        String userName = credencials.getUsername();
        String pass = ((PasswordCredential) credencials.getCredential()).getValue();
        System.out.println("USER NAME_ " + userName + "   PASS  " + pass);
        try {
            User u = security.getPersistenceManager().findUser(userName);
            //AttributesManager attributesManager = security.getAttributesManager();                        
            if (u.getId() != null) {
                boolean ispass = security.getAttributesManager().validatePassword(u, pass);
                if (ispass) {
                    this.setStatus(AuthenticationStatus.SUCCESS);
                    setUser(u);
                    System.out.println("INGRESO correcto ____");
                    identity.login();
                } else {
                    this.setStatus(AuthenticationStatus.FAILURE);
                    System.out.println("FALLO al ingresar");
                    identity.login();
                }
            }

        } catch (IdentityException ex) {
            System.out.println("ERROR_");
            //java.util.logging.Logger.getLogger(Authenticate.class.getName()).log(Level.SEVERE, null, ex);
            this.setStatus(AuthenticationStatus.FAILURE);            
            identity.login();
        }
    }
//evento que realiza seam cuando la autenticacion es exitosa 

    public void loginSuccess(@Observes final LoggedInEvent event,
            final NavigationHandler navigation,
            final FacesContext context1,
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {

        String viewId = context1.getViewRoot().getViewId();

        if (!"/pages/login.xhtml".equals(viewId)) {

            HttpInboundServletRewrite rewrite = new HttpInboundRewriteImpl(request, response);
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", rewrite.getContextPath() + rewrite.getURL());
            response.flushBuffer();
        } else {
            String result = "/pages/home.xhtml";
            navigation.handleNavigation(context, null, result + "?faces-redirect=true");
            //context.getExternalContext().redirect("/Sgssalud/pages/home.xhtml?faces-redirect=true");
        }
    }

//evento cuando falla la autenticacion
    public void loginFailed(@Observes final LoginFailedEvent event, final NavigationHandler navigation)
            throws InterruptedException {

        Exception exception = event.getLoginException();
        if (exception != null) {
            logger.error("Login failed due to exception" + identity.getAuthenticatorName() + ", "
                    + identity.getAuthenticatorClass() + ", " + identity);
            // TODO , exception );
            //messages.warn("Whoops! Something went wrong with your login. Care to try again? We'll try to figure out what went wrong.");
            messages.warn(UI.getMessages("common.login.fail"));

        } else {
            //messages.warn("Whoops! We don't recognize that username or password. Care to try again?");
            messages.warn(UI.getMessages("common.login.bad.usernamepassword"));                        
        }
        Thread.sleep(200);
        navigation.handleNavigation(context, null, "/pages/login.xhtml?faces-redirect=true");
    }

}
