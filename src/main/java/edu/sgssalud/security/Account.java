/**
 *
 */
package edu.sgssalud.security;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import edu.sgssalud.cdi.LoggedIn;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.security.authorization.SecurityRules;
import edu.sgssalud.service.paciente.PacienteServicio;
import javax.enterprise.context.SessionScoped;

import org.jboss.seam.security.Identity;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @adapter <a href="mailto:jlgranda81@gmail.com">José Luis Granda</a>
 *
 */
@Named("account")
//@RequestScoped
@SessionScoped
public class Account implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(Account.class);
    private static final long serialVersionUID = 8474539305281711165L;
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private Identity identity;
    @Inject
    private ProfileService ps;
    @Inject
    private PacienteServicio pacienteS;

    private SecurityRules securityRules;

    @PostConstruct
    public void init() {
        ps.setEntityManager(em);
        pacienteS.setEntityManager(em);
    }
    Paciente loggedIn1 = new Paciente();
    Profile loggedIn = new Profile();

    @Produces
    @LoggedIn
    @SessionScoped
    @Named("userPaciente")
    public Paciente getLoggedPaciente() {
        if (identity.isLoggedIn() && !loggedIn1.isPersistent()) {
            try {
                loggedIn1 = pacienteS.getPacientePorIdentityKey(identity.getUser().getKey());
            } catch (NoResultException e) {
                throw e;
            }
        } else if (!identity.isLoggedIn()) {
        }
        return loggedIn1;
    }

    @Produces
    @LoggedIn
    //@RequestScoped
    @SessionScoped
    @Named("userProfile")
    public Profile getLoggedIn() {
        if (identity.isLoggedIn() && !loggedIn.isPersistent()) {
            try {
                loggedIn = ps.getProfileByIdentityKey(identity.getUser().getKey());
            } catch (NoResultException e) {
                throw e;
            }
        } else if (!identity.isLoggedIn()) {
        }
        return loggedIn;
    }

    public Long getLoggedId() {
        Long id = new Long(0);
        if (identity.isLoggedIn()) {
            try {
                loggedIn = ps.getProfileByIdentityKey(identity.getUser().getKey());
                if (loggedIn != null) {
                    id = loggedIn.getId();
                    return id;
                } else {
                    loggedIn1 = pacienteS.getPacientePorIdentityKey(identity.getUser().getKey());
                    if (loggedIn1 != null) {
                        id = loggedIn1.getId();
                        return id;
                    }
                }
            } catch (NoResultException e) {
                throw e;
            }
        } else if (!identity.isLoggedIn()) {
        }
        return id;
    }

    @TransactionAttribute
    public void saveAjax() {
        Profile current = getLoggedIn();
        ps.save(current);
    }

    @TransactionAttribute
    public void displayBootcampAjax() {
        Profile current = getLoggedIn();
        current.setShowBootcamp(true);
        ps.save(current);
    }

    @TransactionAttribute
    public void dismissBootcampAjax() {
        Profile current = getLoggedIn();
        current.setShowBootcamp(false);
        ps.save(current);
    }

    public void setEntityManager(final EntityManager em) {
        this.em = em;
        ps.setEntityManager(em);
    }

    public boolean isUserPaciente() {

        if (identity.isLoggedIn()) {
            try {
                boolean valor = pacienteS.getPacientePorIdentityKey(identity.getUser().getKey()) != null;
                return valor;
            } catch (NoResultException e) {
                throw e;
            }
        } else if (!identity.isLoggedIn()) {
        }
        return false;
    }

    public boolean isUserProfile() {

        if (identity.isLoggedIn()) {
            try {
                boolean valor = ps.getProfileByIdentityKey(identity.getUser().getKey()) != null;
                return valor;
            } catch (NoResultException e) {
                throw e;
            }
        } else if (!identity.isLoggedIn()) {
        }
        return false;
    }

    public boolean tienePermiso(final String grupoUser) {
        SecurityRules s = new SecurityRules();
        if (SecurityRules.ADMIN.equals(grupoUser)) {
            return s.isAdmin(identity);
        } else if (SecurityRules.MEDICOS.equals(grupoUser)) {
            return s.isMedico(identity);
        } else if (SecurityRules.ENFERMEROS.equals(grupoUser)) {
            return s.isEnfermero(identity);
        } else if (SecurityRules.ODONTOLOGOS.equals(grupoUser)) {
            return s.isOdontologo(identity);
        } else if (SecurityRules.SECRETARIA.equals(grupoUser)) {
            return s.isSecretaria(identity);
        } else if (SecurityRules.FARMACEUTICOS.equals(grupoUser)) {
            return s.isFarmaceutica(identity);
        } else if (SecurityRules.LABORATORISTAS.equals(grupoUser)) {
            return s.isLaboratorista(identity);
        }
        return false;
    }

    public boolean isRenderView() {
        SecurityRules s = new SecurityRules();
        if (s.isMedico(identity) || s.isOdontologo(identity) || s.isEnfermero(identity)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isRenderView1() {
        SecurityRules s = new SecurityRules();
        if (s.isMedico(identity) || s.isOdontologo(identity)) {
            return true;
        } else {
            return false;
        }
    }

    public String loadPages() {
        SecurityRules s = new SecurityRules();
        if (identity.isLoggedIn()) {
            if (s.isMedico(identity) || s.isOdontologo(identity) || s.isEnfermero(identity)) {
                return null;
            } else {
                return "/pages/denied.xhtml";
            }
        } else {
            return "/pages/login.xhtml";
        }
    }

    public String loadPagesRol() {
        SecurityRules s = new SecurityRules();
        if (identity.isLoggedIn()) {
            if (s.isMedico(identity) || s.isOdontologo(identity)) {
                return null;
            } else {
                return "/pages/denied.xhtml";
            }
        } else {
            return "/pages/login.xhtml";
        }
    }

    public String loadPagesLoggedIn() {
        //SecurityRules s = new SecurityRules();
        if (identity.isLoggedIn()) {
            return null;
        } else {
            return "/pages/login.xhtml";
        }
    }

    public SecurityRules getSecurityRules() {
        return securityRules;
    }

    public void setSecurityRules(SecurityRules securityRules) {
        this.securityRules = securityRules;
    }

}
