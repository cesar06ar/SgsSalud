/**
 *
 */
package edu.sgssalud.security.authentication;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.security.IdentityObjectAttribute;
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
import org.picketlink.idm.api.User;
import edu.sgssalud.service.paciente.PacienteServicio;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.servlet.ServletContext;
import org.jboss.seam.security.Authenticator;
import org.jboss.seam.security.external.api.ResponseHolder;
import org.jboss.seam.security.external.openid.api.OpenIdPrincipal;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.PersistenceManager;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.PasswordCredential;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Named
@RequestScoped
public class Authentication {

    Logger logger = Logger.getLogger(Authentication.class);
//    @Inject
//    private HttpSession session;
    @Inject
    private ServletContext servletContext;

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
    private IdmAuthenticator idmAuth;
    @Inject
    private OpenIdAuthenticator openAuth;
    @Inject
    Event<DeferredAuthenticationEvent> deferredAuthentication;

//    private WebServiceSGAClientConnection conexionSGA;
    @PostConstruct
    public void init() {
        pacienteServic.setEntityManager(em);
        profileService.setEntityManager(em);
        //conexionSGA = new WebServiceSGAClientConnection();
    }

    public void loginSuccess(@Observes final LoggedInEvent event, final NavigationHandler navigation,
            final FacesContext context,
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        User user = event.getUser();
        logger.info("User logged in [{}, {}]", user.getId(), user.getKey());

        String viewId = context.getViewRoot().getViewId();
        if (!"/pages/signup.xhtml".equals(viewId)) {
            // TODO need a better way to navigate: this doesn't work with AJAX requests
            HttpInboundServletRewrite rewrite = new HttpInboundRewriteImpl(request, response);

            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", rewrite.getContextPath() + rewrite.getURL());
            response.flushBuffer();

            return;
        } else {

            String result = "/pages/home.xhtml";
            navigation.handleNavigation(context, null, result + "?faces-redirect=true");
        }
    }

    /*
     * This is called outside of the JSF lifecycle.
     */
    public void openLoginSuccess(@Observes final DeferredAuthenticationEvent event, final NavigationHandler navigation) {
        if (event.isSuccess()) {
            logger.info("User logged in with OpenID");
        } else {
            logger.info("User failed to login via OpenID, potentially due to cancellation");
        }
    }

    public void loginSucceeded(OpenIdPrincipal principal, ResponseHolder responseHolder) {

        try {
            openAuth.success(principal);
            deferredAuthentication.fire(new DeferredAuthenticationEvent(true));
            responseHolder.getResponse().sendRedirect(servletContext.getContextPath() + "/pages/home.xhtml");
        } catch (IOException e) {

            throw new RuntimeException(e);

        }

    }

    public void loginFailed(@Observes final LoginFailedEvent event, final NavigationHandler navigation)
            throws InterruptedException {
        if (!(OpenIdAuthenticator.class.equals(identity.getAuthenticatorClass())
                && AuthenticationStatus.DEFERRED.equals(openAuth.getStatus()))) {
            Exception exception = event.getLoginException();
            if (exception != null) {
                logger.error(
                        "Login failed due to exception" + identity.getAuthenticatorName() + ", "
                        + identity.getAuthenticatorClass()
                        + ", " + identity);
                //messages.warn("Whoops! Something went wrong with your login. Care to try again? We'll try to figure out what went wrong.");
                //messages.warn(UI.getMessages("common.login.fail"));

            } else {
                //messages.warn("Whoops! We don't recognize that username or password. Care to try again?");
                messages.warn(UI.getMessages("common.login.bad.usernamepassword"));
            }
            Thread.sleep(50);
            navigation.handleNavigation(context, null, "/pages/login?faces-redirect=true");
        }
    }

    public void login() throws InterruptedException {
        identity.setAuthenticatorClass(IdmAuthenticator.class);
        try {
            identity.login();
        } catch (Exception e) {
            //identity.tryLogin();
            identity.login();
        }
    }

    public void login1() throws InterruptedException, IdentityException {
        identity.setAuthenticatorClass(IdmAuthenticator.class);
        PersistenceManager identityManager = security.getPersistenceManager();
        User user = identityManager.findUser(credencials.getUsername());
        if (user != null) {
            IdentityObjectAttribute ida = profileService.getAttributos(user.getKey(), "estado").get(0);
            //Profile userP = profileService.getProfileByUsername(credencials.getUsername());
            //Paciente p = pacienteServic.getPacientePorNombreUsuario(credencials.getUsername());
            if (ida != null && "ACTIVO".equals(ida.getValue())) {
                //try {
                this.login();
            } else {
                messages.warn("!El usuario esta inactivo o no existe¡");  //los datos no son correctos                    
            }

        } else {
            messages.warn(UI.getMessages("common.login.bad.usernamepassword"));
        }
        //buscar usuario si esta habilitado para realizar ingresar al sistema y realizar las acciones solicitadas
    }

    public String logout() {
        identity.setAuthenticatorClass(IdmAuthenticator.class);
        identity.logout();
        //session.invalidate();
        return "/pages/loggedOffHome.xhtml?faces-redirect=true";
    }
   
}
