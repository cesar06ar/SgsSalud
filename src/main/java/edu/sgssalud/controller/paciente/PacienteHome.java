/*
 * Copyright 2013 cesar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sgssalud.controller.paciente;

import edu.sgssalud.cdi.Current;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.paciente.Paciente;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.international.status.Messages;
import edu.sgssalud.service.PacienteServicio;
import edu.sgssalud.util.Dates;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.inject.Produces;
import org.jboss.seam.security.Authenticator;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.IdmAuthenticator;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.PersistenceManager;
import org.picketlink.idm.api.User;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.PasswordCredential;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class PacienteHome extends BussinesEntityHome<Paciente> implements Serializable {

    private static final long serialVersionUID = 7632987414391869389L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(PacienteHome.class);
    /*Atributos importantes para acceso a la BD ==>*/
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private PacienteServicio pcs;
    /*<==....*/
    /*Atributos para autenticación y manejo de usuarios con seam 3 */
    private Messages msg;
    @Inject
    private Identity identity;
    @Inject
    private Credentials credentials;
    @Inject
    private IdentitySession security;    
    @Inject
    private IdmAuthenticator idmAuth;
    /*<==....*/
    /*Atributos propios para interaccion con la vista*/
    private String password;
    private String passwordConfirm;
    private String nombreEstructura;
    /*<==....*/

    /*Métodos get y set para obtener el Id de la clase*/
    public Long getPacienteId() {
        return (Long) getId();
    }

    public void setPacienteId(Long pacienteId) {
        setId(pacienteId);
    }
    /*<==....*/

    /*Métodos get y set del Bean PacienteHome*/
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getNombreEstructura() {
        return nombreEstructura;
    }

    public void setNombreEstructura(String nombreEstructura) {
        this.nombreEstructura = nombreEstructura;
    }
    /*<==....*/

    /*Método para  cargar una instancia de paciente==>*/
    @Produces
    @Named("paciente")
    @Current
    @TransactionAttribute   //
    public Paciente load() {
        if (isIdDefined()) {
            wire();
        }
        /*else if(this.instance == null){
            
         if (identity.isLoggedIn() && !(identity.inGroup(SecurityRules.ADMIN, "GROUP") || "admin".contains(SecurityRules.getUsername(identity)))) {                
         setInstance(ps.getProfileByUsername(identity.getUser().getKey()));
         } else if (identity.isLoggedIn() && (identity.inGroup(SecurityRules.ADMIN, "GROUP") || "admin".contains(SecurityRules.getUsername(identity)))){
         setInstance(createInstance());
         }
         }*/
        log.info("sgssalud --> cargar instance " + getInstance());
        return getInstance();
    }
    /*<==....*/

    /*Metodo que retorna una instancia de la clase (Paciente) cuando ya esta creada==>*/
    @TransactionAttribute
    public void wire() {
        getInstance();
    }
    /*<==....*/

    /*Metodo importante para actualizar EntityManager y tener acceso a la DB==>*/
    @PostConstruct
    public void init() {
        setEntityManager(em);
        bussinesEntityService.setEntityManager(em);
    }
    /*<==....*/

    /*Método que me permite crear una intancia (un nuevo Paciente)==>*/
    @Override
    protected Paciente createInstance() {
        //prellenado estable para cualquier clase 
        Date now = Calendar.getInstance().getTime();
        Paciente paciente = new Paciente();
        paciente.setCreatedOn(now);
        paciente.setLastUpdate(now);
        paciente.setActivationTime(now);
        paciente.setExpirationTime(Dates.addDays(now, 364));
        paciente.setResponsable(null);    //cambiar atributo a 
        paciente.buildAttributes(bussinesEntityService);  //
        return paciente;
    }
    /*<==....*/
    /*==>*/

    @Override
    public Class<Paciente> getEntityClass() {
        return Paciente.class;
    }
    /*<==....*/

    /*Método que me permite guardar o actualizar un paciente este método
     * interactua con la vista en el boton guardar...==>*/
    @TransactionAttribute
    public String guardarPaciente() {
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        if (getInstance().isPersistent()) {
            save(getInstance());
        } else {
            try {
                register();
            } catch (IdentityException ex) {
                Logger.getLogger(PacienteHome.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    /*<==....*/
    
     /*Sección de métodos agrupados para crear o actulizar una cuenta de usuario para el paciente==>*/    
    @TransactionAttribute
    public String register() throws IdentityException {
        createUser();
        credentials.setUsername(getInstance().getNombreUsuario());
        credentials.setCredential(new PasswordCredential(getPassword()));     
        identity.setAuthenticatorClass(IdmAuthenticator.class);

        /*
         * Try twice to work around some state bug in Seam Security
         * TODO file issue in seam security
         */
        String result = identity.login();
        if (Identity.RESPONSE_LOGIN_EXCEPTION.equals(result)) {
            result = identity.login();
        }
        return result;
    }
    
    @TransactionAttribute
    private void createUser() throws IdentityException {
        // TODO validate username, email address, and user existence
        PersistenceManager identityManager = security.getPersistenceManager();
        User user = identityManager.createUser(getInstance().getNombreUsuario());

        AttributesManager attributesManager = security.getAttributesManager();
        attributesManager.updatePassword(user, getPassword());
        attributesManager.addAttribute(user, "email", getInstance().getEmail());  //me permite agregar un atributo de cualquier tipo a un usuario 
        em.flush();

        // TODO figure out a good pattern for this...
        //setInstance(createInstance());
        //getInstance().setEmail(email);
        getInstance().setPassword(getPassword());
        getInstance().getIdentityKeys().add(user.getKey());
        getInstance().setConfirmed(true);
        getInstance().setShowBootcamp(true);
        create(getInstance()); //
        setPacienteId(getInstance().getId());
        wire();
        getInstance().setName(getInstance().getNombreUsuario()); //Para referencia
        getInstance().setType(bussinesEntityService.findBussinesEntityTypeByName(
                Paciente.class.getName()));
        getInstance()
                .buildAttributes(bussinesEntityService);
        save(getInstance()); //Actualizar estructura de datos

    }
    /*==>*/
    /*<==....*/
}
