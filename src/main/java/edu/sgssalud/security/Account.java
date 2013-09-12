/**
 *
 */
package edu.sgssalud.security;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
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
import edu.sgssalud.service.paciente.PacienteServicio;

import org.jboss.seam.security.Identity;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @adapter <a href="mailto:jlgranda81@gmail.com">José Luis Granda</a>
 *
 */
@Named("account")
@RequestScoped
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

    @PostConstruct
    public void init() {
        ps.setEntityManager(em);
        pacienteS.setEntityManager(em);
    }
    Paciente loggedIn1 = new Paciente();

    @Produces
    @LoggedIn
    @RequestScoped
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
    Profile loggedIn = new Profile();

    @Produces
    @LoggedIn
    @RequestScoped
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

        if (identity.isLoggedIn() && !loggedIn.isPersistent()) {
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
}
