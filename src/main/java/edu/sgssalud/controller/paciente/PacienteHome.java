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
import edu.sgssalud.model.*;
import edu.sgssalud.model.profile.Profile;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.international.status.Messages;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.Dates;
import edu.sgssalud.util.Strings;
import edu.sgssalud.util.UI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.jasypt.util.password.BasicPasswordEncryptor;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/*...==>*/
/*...==>*/
/**
 *
 * @author cesar
 */
@Named
@ViewScoped
//@ConversationScoped
public class PacienteHome extends BussinesEntityHome<Paciente> implements Serializable {

    private static final long serialVersionUID = 7632987414391869389L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(PacienteHome.class);
    /*<== Atributos importantes para acceso a la BD */
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private PacienteServicio pcs;
    /*....==>*/
    /*<== Atributos para autenticación y manejo de usuarios con seam 3 */
    private Messages msg;
    @Inject
    private Identity identity;
    @Inject
    private Credentials credentials;
    @Inject
    private IdentitySession security;
    @Inject
    private IdmAuthenticator idmAuth;
    /*....==>*/
    /*<== Atributos propios para interaccion con la vista*/
    private String clave;
    private String confirmarClave;
    private String nombreEstructura;
    private boolean rendPanelEstUni;
    private boolean rendPanelEstCol;
    private boolean rendPanelEstEsc;
    private String tipoEstudiante;

    private UploadedFile file;
    /*....==>*/

    /*<== Métodos get y set para obtener el Id de la clase*/
    public Long getPacienteId() {
        return (Long) getId();
    }

    public void setPacienteId(Long pacienteId) {
        setId(pacienteId);
    }

    /*<==....*/
    /*<== Métodos get y set del Bean PacienteHome*/
    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }    

    public String getConfirmarClave() {
        return confirmarClave;
    }

    public void setConfirmarClave(String confirmarClave) {
        this.confirmarClave = confirmarClave;
    }    

    public String getNombreEstructura() {
        return nombreEstructura;
    }

    public void setNombreEstructura(String nombreEstructura) {
        this.nombreEstructura = nombreEstructura;
    }

    public boolean isRendPanelEstCol() {
        return rendPanelEstCol;
    }

    public void setRendPanelEstCol(boolean rendPanelEstCol) {
        this.rendPanelEstCol = rendPanelEstCol;
    }

    public boolean isRendPanelEstEsc() {
        return rendPanelEstEsc;
    }

    public void setRendPanelEstEsc(boolean rendPanelEstEsc) {
        this.rendPanelEstEsc = rendPanelEstEsc;
    }

    public boolean isRendPanelEstUni() {
        return rendPanelEstUni;
    }

    public void setRendPanelEstUni(boolean rendPanelEstUni) {
        this.rendPanelEstUni = rendPanelEstUni;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /*<==....*/
    /*<== Método para  cargar una instancia de paciente */
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
    /*....==>*/

    /*<== Metodo que retorna una instancia de la clase (Paciente) cuando ya esta creada*/
    @TransactionAttribute
    public void wire() {
        getInstance();
        if (getInstance().isPersistent()) {
            String t = getInstance().getTipoEstudiante();
            log.info("ingreso a fijar tipo: " + t);
            this.setTipoEstudiante(t);
        } else {
            log.info("ingreso a fijar tipo Nuevo");
            this.rendPanelEstUni = false;
            this.rendPanelEstCol = false;
            this.rendPanelEstEsc = false;
        }
    }
    /*....==>*/

    /*<== Metodo importante para actualizar EntityManager y tener acceso a la DB*/
    @PostConstruct
    public void init() {
        setEntityManager(em);
        pcs.setEntityManager(em);
        bussinesEntityService.setEntityManager(em);
    }
    /*....==>*/

    /*<== Método que me permite crear una intancia (un nuevo Paciente)==>*/
    @Override
    protected Paciente createInstance() {
        //prellenado estable para cualquier clase 
        BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(Paciente.class.getName());
        Date now = Calendar.getInstance().getTime();
        Paciente paciente = new Paciente();
        paciente.setCreatedOn(now);
        paciente.setLastUpdate(now);
        paciente.setActivationTime(now);
        paciente.setExpirationTime(Dates.addDays(now, 364));
        paciente.setResponsable(null);    //cambiar atributo a 
        paciente.setType(_type);
        paciente.buildAttributes(bussinesEntityService);  //
        return paciente;
    }
    /*....==>*/

    @Override
    public Class<Paciente> getEntityClass() {
        return Paciente.class;
    }
    /*....==>*/

    /*<== Método que me permite guardar o actualizar un paciente este método
     * interactua con la vista en el boton guardar...==>*/
    @TransactionAttribute
    public String guardarPaciente() {
        Date now = Calendar.getInstance().getTime();
        String salida = null;
        getInstance().setLastUpdate(now);
        if (getInstance().isPersistent()) {
            save(getInstance());
            salida = "/pages/" + getBackView() + "?faces-redirect=true";
        } else {
            try {
                register();
                FacesMessage msg = new FacesMessage("Se creo nuevo paciente: " + getInstance().getNombres() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                salida = "/pages/" + getBackView() + "?faces-redirect=true";
            } catch (IdentityException ex) {
                //Logger.getLogger(PacienteHome.class.getName()).log(Level.SEVERE, null, ex);
                FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getNombres() + " ¡Puede que el correo ingresado ya pertenesca a otro usuario!");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        }

        return salida;
    }
    /*....==>*/

    /*<== Sección de métodos agrupados para crear o actulizar una cuenta de usuario para el paciente*/
    @TransactionAttribute
    public String register() throws IdentityException {
        createUser();
        credentials.setUsername(getInstance().getNombreUsuario());
        credentials.setCredential(new PasswordCredential(getInstance().getClave()));
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
        getInstance().setNombreUsuario(getInstance().getCedula());
        getInstance().setClave(getInstance().getCedula());
        PersistenceManager identityManager = security.getPersistenceManager();
        User user = identityManager.createUser(getInstance().getCedula());

        AttributesManager attributesManager = security.getAttributesManager();
        attributesManager.updatePassword(user, getInstance().getCedula());
        attributesManager.addAttribute(user, "email", getInstance().getEmail());  //me permite agregar un atributo de cualquier tipo a un usuario 
        em.flush();

        // TODO figure out a good pattern for this...
        getInstance().getIdentityKeys().add(user.getKey());
        getInstance().setShowBootcamp(true);
        create(getInstance());
        setPacienteId(getInstance().getId());

        //crear seguridad de paciente
        try {
            org.picketlink.idm.api.Group group = security.getPersistenceManager().findGroup("PACIENTE", "GROUP");
            security.getRelationshipManager().associateUser(group, user);
        } catch (Exception e) {
            System.out.println("Error");
        }
        wire();
//        getInstance().setType(bussinesEntityService.findBussinesEntityTypeByName(
//                Paciente.class.getName()));
//        getInstance()
//                .buildAttributes(bussinesEntityService);

        save(getInstance()); //Actualizar estructura de datos  
    }
    
    @TransactionAttribute
    public String cambiarClave() throws IdentityException, InterruptedException {
        PersistenceManager identityManager = security.getPersistenceManager();
        User user = identityManager.findUser(getInstance().getNombreUsuario());
        AttributesManager attributesManager = security.getAttributesManager();
        attributesManager.updatePassword(user, clave);
        getInstance().setClave(clave);
        save(getInstance());

        em.flush();
        credentials.setUsername(getInstance().getNombreUsuario());
        credentials.setCredential(new PasswordCredential(clave));
        //oidAuth.setStatus(Authenticator.AuthenticationStatus.FAILURE);
        identity.setAuthenticatorClass(IdmAuthenticator.class);
//        String result = identity.login();
//        if (Identity.RESPONSE_LOGIN_EXCEPTION.equals(result)) {
//            result = identity.login();
//        }
        return "/pages/home.xhtml?faces-redirect=true";
    }

    /*<== método que retorna la lista de tipos de datos enumerados ...*/
    public List<Paciente.Genero> getListaGeneros() {
        wire();
        List<Paciente.Genero> list = Arrays.asList(getInstance().getGenero().values());
        return list;
    }
    /*....==>*/

    public List<String> tiposEstudiante() {
        List<String> list = new ArrayList<String>();
        list.add("Universitario");
        list.add("Colegio");
        list.add("Escuela");
        return list;
    }

    public List<String> listaAreas() {
        List<String> list = new ArrayList<String>();
        list.add("EDUCATIVA");
        list.add("JURÍDICA");
        list.add("AGROPECUARIA");
        list.add("ENERGÍA");
        list.add("SALUD");
        return list;
    }

    public String getTipoEstudiante() {
        return tipoEstudiante;
    }

    public void setTipoEstudiante(String tipoEstudiante) {
        log.info("setTipoEstudiante");
        this.tipoEstudiante = tipoEstudiante;
        getInstance().setTipoEstudiante(this.tipoEstudiante);

        //log.info("fijar tipo estudiante : " + getInstance().getTipoEstudiante());
        if (!getInstance().getTipoEstudiante().isEmpty()) {
            if (getInstance().getTipoEstudiante().equals("Universitario")) {
                this.setRendPanelEstCol(false);
                this.setRendPanelEstEsc(false);
                this.setRendPanelEstUni(true);
                //log.info("TES---  Uniersitario " + rendPanelEstUni);
            } else if ("Colegio".equals(getInstance().getTipoEstudiante())) {
                this.setRendPanelEstUni(false);
                this.setRendPanelEstEsc(false);
                this.setRendPanelEstCol(true);
            } else if ("Escuela".equals(getInstance().getTipoEstudiante())) {
                this.setRendPanelEstUni(false);
                this.setRendPanelEstCol(false);
                this.setRendPanelEstEsc(true);
            }
        } else {
            this.setRendPanelEstUni(false);
            this.setRendPanelEstCol(false);
            this.setRendPanelEstEsc(false);
        }

    }
    /*<==....*/

    public void upload() {
        FacesMessage msg = new FacesMessage("Ok", "Fichero " + file.getFileName() + " subido correctamente.");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    private Photos buildPhoto(UploadedFile file) {
        if (getInstance().getFoto() != null) {
            System.out.println("Ingreso a Actualizar Imagen:.........======================");
            Photos foto = getInstance().getFoto();
            foto.setPhoto(file.getContents());
            foto.setName(file.getFileName());
            foto.setContentType(file.getContentType());
            foto.setSize(file.getSize());
            return foto;
        } else {
            System.out.println("Ingreso a subir Imagen:.......................===========");
            Photos foto = new Photos();
            foto.setPhoto(file.getContents());
            foto.setName(file.getFileName());
            foto.setContentType(file.getContentType());
            foto.setSize(file.getSize());
            return foto;
        }

    }

    public void subirImagen(FileUploadEvent event) {
        //if (file != null) {
        System.out.println("Ingreso a subir Imagen_________________________________________________");
        try {
            getInstance().setFoto(buildPhoto(event.getFile()));
            FacesMessage msg = new FacesMessage("Ok", "Fichero " + event.getFile().getFileName() + " subido correctamente.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error", "Al subir fichero");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        //}
    }
}
