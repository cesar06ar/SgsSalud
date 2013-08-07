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
package edu.sgssalud.security;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.model.Group;
import edu.sgssalud.model.Property;
import edu.sgssalud.model.Structure;
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.model.security.IdentityObjectCredentialType;
import edu.sgssalud.model.security.IdentityObjectType;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.util.Dates;
import org.jboss.seam.security.management.picketlink.IdentitySessionProducer;
import org.jboss.seam.transaction.TransactionPropagation;
import org.jboss.seam.transaction.Transactional;
import org.jboss.solder.servlet.WebApplication;
import org.jboss.solder.servlet.event.Initialized;
import org.picketlink.idm.api.Attribute;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.api.User;
import org.picketlink.idm.common.exception.IdentityException;

/**
 * Validates that the database contains the minimum required entities to
 * function from SocialPM
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @adapter <a href="mailto:jlgranda81@gmail.com">José Luis Granda</a>
 * @adapter <a href="mailto:cesar06ar@gmail.com">César Abad Ramos</a>
 */
@Transactional(TransactionPropagation.REQUIRED)
public class InitializeDatabase {
    
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    @Inject
    private IdentitySessionFactory identitySessionFactory;
    @Inject
    protected BussinesEntityService bussinesEntityService;
    
    @Transactional
    public void validate(@Observes @Initialized final WebApplication webapp) throws IdentityException {
        bussinesEntityService.setEntityManager(entityManager);
        validateDB();
        validateStructure();
        validateIdentityObjectTypes();
        validateSecurity();
        
    }
    
    private void validateDB() {
        Setting singleResult = null;
        try {
            TypedQuery<Setting> query = entityManager.createQuery("from Setting s where s.name='schemaVersion'",
                    Setting.class);
            singleResult = query.getSingleResult();
        } catch (NoResultException e) {
            Date now = Calendar.getInstance().getTime();
            singleResult = new Setting("schemaVersion", "0");
            singleResult.setCreatedOn(now);
            singleResult.setLastUpdate(now);
            entityManager.persist(singleResult);
            entityManager.flush();
        }
        
        System.out.println("Current database schema version is [" + singleResult.getValue() + "]");
        
    }
    
    private void validateIdentityObjectTypes() {
        if (entityManager.createQuery("select t from IdentityObjectType t where t.name = :name")
                .setParameter("name", "USER")
                .getResultList().size() == 0) {
            
            IdentityObjectType user = new IdentityObjectType();
            user.setName("USER");
            entityManager.persist(user);
        }
        
        if (entityManager.createQuery("select t from IdentityObjectType t where t.name = :name")
                .setParameter("name", "GROUP")
                .getResultList().size() == 0) {
            
            IdentityObjectType group = new IdentityObjectType();
            group.setName("GROUP");
            entityManager.persist(group);
        }
    }
    
    private void validateSecurity() throws IdentityException {
        // Validate credential types
        if (entityManager.createQuery("select t from IdentityObjectCredentialType t where t.name = :name")
                .setParameter("name", "PASSWORD")
                .getResultList().size() == 0) {
            
            IdentityObjectCredentialType PASSWORD = new IdentityObjectCredentialType();
            PASSWORD.setName("PASSWORD");
            entityManager.persist(PASSWORD);
        }
        
        Map<String, Object> sessionOptions = new HashMap<String, Object>();
        sessionOptions.put(IdentitySessionProducer.SESSION_OPTION_ENTITY_MANAGER, entityManager);
        
        
        IdentitySession session = identitySessionFactory.createIdentitySession("default", sessionOptions);
        
        /*
         * Create our test user (me!)
         */
        Date now = Calendar.getInstance().getTime();
        BussinesEntityType bussinesEntityType = null;
        TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                BussinesEntityType.class);
        query.setParameter("name", Profile.class.getName());
        Profile p = null;
        Profile admin = null;
        List<User> members = new ArrayList<User>();
        org.picketlink.idm.api.Group g = session.getPersistenceManager().findGroup("Admin", "GROUP");
        if (g == null) {
            g = session.getPersistenceManager().createGroup("Admin", "GROUP");
        }
        
        bussinesEntityType = query.getSingleResult();
        if (session.getPersistenceManager().findUser("admin") == null) {
            User u = session.getPersistenceManager().createUser("admin");
            session.getAttributesManager().updatePassword(u, "4dm1nglue3");
            session.getAttributesManager().addAttribute(u, "email", "cesar06ar@hotmail.com");
            members.add(u);
            //TODO revisar error al implementar la relacion entre un grupo y usuario.... 
            //session.getRelationshipManager().associateUser(g, u);             

            p = new Profile();
            p.setEmail("cesar06ar@hotmail.com");
            p.setUsername("admin");
            p.setPassword("adminadmin");
            p.getIdentityKeys().add(u.getKey());
            p.setUsernameConfirmed(true);
            p.setShowBootcamp(true);
            
            p.setName("Administrador");
            p.setFirstname("SgsSalud");
            p.setSurname("Software Clinico");
            p.setCreatedOn(now);
            p.setLastUpdate(now);
            p.setActivationTime(now);
            p.setExpirationTime(Dates.addDays(now, 364));
            p.setResponsable(null); //Establecer al usuario actual
            p.setType(bussinesEntityType); //Relacionar con un tipo de entidad de negocio y su estructura
            p.buildAttributes(bussinesEntityService); //Crear la estructura de datos glue
            entityManager.persist(p);
            entityManager.flush();
            admin = p;
            
        }     
    }
    
    private void validateStructure() {
        validarEstructuraParaPerfilDeUsuario();
        validarEstructuraDatosPersonalesDelPerfilDeUsuario();       
        validarEstructuraEducacionDelPerfilDeUsuarios();        
        //validarEstructuraTrayectoriaLaboralDelPerfilDeUsuarios();  
        validarEstructuraDelPaciente();  
        validarEstructuraDatosPersonalesDelPaciente();
        validarEstructuraDatosAcademicosColegioDelPaciente();
        validarEstructuraDatosAcademicosEscuelaDelPaciente();
    }
    
    private void validarEstructuraParaPerfilDeUsuario() {
        BussinesEntityType bussinesEntityType = null;
        
        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", Profile.class.getName());
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(Profile.class.getName());

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName("Data for " + Profile.class.getName());
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de entidad de negocios
            List<Property> attributes = new ArrayList<Property>();
            
            attributes.add(buildStructureTypeProperty("PersonalData", "Datos personales", "Información personal relevante", "/pages/profile/data/personal", 1L));
            attributes.add(buildGroupTypeProperty("Spouse", "Esposa/o", false, null, 1L, 1L, "Datos de su conyugue", 2L));
            attributes.add(buildGroupTypeProperty("Childrens", "Hijos", false, null, 1L, 0L, "Datos de sus hijos", 3L));
            attributes.add(buildGroupTypeProperty("Education", "Educación", false, null, 1L, 0L, "Detalle sus logros académicos", 4L));            
            //attributes.add(buildGroupTypeProperty("TrayectoriaLaboral", "Trayectoria Laboral", false, null, 1L, 0L, "Detalle de la trayectoria laboral desde el año 2000 en adelante", 5L));
            //Agregar atributos
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
        
        System.out.println("Structure for Profile [" + bussinesEntityType + "]");
    }
    
    private void validarEstructuraDatosPersonalesDelPerfilDeUsuario(){
        BussinesEntityType bussinesEntityType = null;
        String name = "PersonalData";
        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", name);
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(name);

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName("Data for " + name);
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de entidad de negocios
            List<Property> attributes = new ArrayList<Property>();

          
            attributes.add(buildProperty("Personal", "maritalstatus", "java.lang.String[]", "Casado*,Soltero,Divorciado,Unión libre", false, "Estado civil", "Indique su estado civil", false, 1L));
            attributes.add(buildProperty("Personal", "birthday", Date.class.getName(), ago.getTime(), false, "Fecha de nacimiento", "Nunca olvidaremos su cumpleaños", false, 2L));
            attributes.add(buildProperty("Personal", "gender", "java.lang.String[]", "Másculino,Femenino", false, "Género", "", false, 3L));
            attributes.add(buildProperty("Personal", "birthplace", String.class.getName(), "Loja", false, "Lugar de nacimiento", "Dónde nacio?", false, 4L));
            attributes.add(buildProperty("Dirección permanente", "country", String.class.getName(), "Ecuador", false, "País", "País de residencia", false, 5L));
            attributes.add(buildProperty("Dirección permanente", "city", String.class.getName(), "Loja", false, "Ciudad", "Ciudad de residencia", false, 6L));
            attributes.add(buildProperty("Dirección permanente", "parish", String.class.getName(), null, false, "Parroquia", "Parroquia de residencia", false, 7L));
            attributes.add(buildProperty("Dirección permanente", "neighborhood", String.class.getName(), null, false, "Barrio", "Barrio de residencia", false, 8L));
            attributes.add(buildProperty("Dirección permanente", "address", String.class.getName(), null, false, "Dirección", "Calles y número de casa", false, 9L));
            attributes.add(buildProperty("Dirección permanente", "phone", String.class.getName(), null, false, "Teléfono", "Telefóno de contacto", false, 10L));
            attributes.add(buildProperty("Contacto en emergencia", "emergencyContact", String.class.getName(), null, false, "Contacta en caso de emergencia", "Sí se presenta alguna emergencia, a quién debemos llamar?", false, 11L));
            //attributes.add(buildProperty("Personal", "hobies", "java.lang.MultiLineString", null, false, "Hobies", "Las cosas que disfruta en su tiempo libre (separe con comas)", false, 12L));

            //Agregar atributos
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
        
    }
    
    private void validarEstructuraEducacionDelPerfilDeUsuarios() {
        BussinesEntityType bussinesEntityType = null;
        String name = "Education";
        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", name);
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(name);

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName("Data for " + name);
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de entidad de negocios
            List<Property> attributes = new ArrayList<Property>();
            
            attributes.add(buildProperty("title", String.class.getName(), "", true, "Titulo", "¿Qué titulación obtuviste?", true, 1L));
            attributes.add(buildProperty("country", String.class.getName(), "", true, "País", "¿En que país obtuvo este título?", true, 2L));
            attributes.add(buildProperty("institution", String.class.getName(), "", true, "Institución", "¿En que centro de estudios?", true, 3L));
            attributes.add(buildProperty("graduationDate", Date.class.getName(), now, false, "Fecha de Graduación", "¿Cuándo finalizó sus estudios?", 4L));
            attributes.add(buildProperty("atPresent", "java.lang.String[]", "Sí,No*", true, "Al presente", "¿Esta cursando actualmente esta titulación?", 5L));
            attributes.add(buildProperty("level", "java.lang.String[]", "Secundario,Terciario,Universitario,Postgrado,Master,Doctorado,Otro", true, "Nivel de estudio", "Nivel de los estudios cursados", true, 6L));

            //Agregar atributos
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
        
    private void validarEstructuraDelPaciente() {
        BussinesEntityType bussinesEntityType = null;
        
        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", Paciente.class.getName());
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(Paciente.class.getName());

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName("Data for " + Paciente.class.getName());
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de Tipos de entidades de negocio
            List<Property> attributes = new ArrayList<Property>();            
            attributes.add(buildStructureTypeProperty("datosPersonales", "Datos Personales", "Información personal relevante", "/pages/paciente/paciente", 1L));
            //attributes.add(buildStructureTypeProperty("datosAcademicosUniversitario", "Datos Academicos Estudiante Universitario", "Información Academica del Estudiante", "/pages/paciente/paciente", 2L));
            attributes.add(buildStructureTypeProperty("datosAcademicosEstudianteColegio", "Datos Academicos Estudiante de Colegio", "Información Academica del Estudiante", "/pages/paciente/paciente", 2L));
            attributes.add(buildStructureTypeProperty("datosAcademicosEstudianteEscuela", "Datos Academicos Estudiante de Escuela", "Información Academica del Estudiante", "/pages/paciente/paciente", 3L));
            //Agregar atributos
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
        
        System.out.println("Estructura de Paciente [" + bussinesEntityType + "]");
    }
    
    private void validarEstructuraDatosPersonalesDelPaciente(){
       BussinesEntityType bussinesEntityType = null;
        String name = "datosPersonalesPaciente";
        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", name);
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(name);

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName(name);
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de entidad de negocios
            List<Property> attributes = new ArrayList<Property>();
           
            attributes.add(buildProperty("Personal", "estadoCivil", "java.lang.String[]", "Soltero*,Casado,Unión libre,Divorciado,Viudo", false, "Estado civil", "Indique el estado civil", false, 1L));
            attributes.add(buildProperty("Personal", "sectorProcedencia", "java.lang.String[]", "Urbano*,Rural", false, "Sector de Procedencia", "Indique el sector del cual procede", false, 2L));
            attributes.add(buildProperty("Personal", "etnia", "java.lang.String[]", "Blanco*,Mestizo,Indígena,Afro-Ecuatoriano,Montubio,Negro", false, "Etnia", "Seleccione su etnia a la cual pertenece", false, 3L));
            attributes.add(buildProperty("Personal", "discapacidad", String.class.getName(), "", false, "Tipo de Discapacidad", "Indique el tipo de discapacidad en caso de padecerla", false, 4L));
            attributes.add(buildProperty("Personal", "lugarTrabajo", String.class.getName(), "", false, "Lugar de Trabajo", "Indique el lugar de trabajo si tiene alguno", false, 5L));
                        
            //Agregar atributos
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
        
    }
    
    private void validarEstructuraDatosAcademicosColegioDelPaciente(){
        BussinesEntityType bussinesEntityType = null;
        String name = "datosAcademicosEstudianteColegio";
        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", name);
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(name);

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName(name);
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de entidad de negocios
            List<Property> attributes = new ArrayList<Property>();

            attributes.add(buildProperty("academicoColegio", "especialidad", String.class.getName(), "", false, "Especialidad", "Indique que especialidad sigue", false, 1L));
            attributes.add(buildProperty("academicoColiegio", "curso", String.class.getName(), "", false, "Curso", "Indique el nombre en que está", false, 3L));
            attributes.add(buildProperty("academicoColegio", "paralelo", java.lang.Long.class.getName(), "", false, "Paralelo", "Indique en que paralelo está", false, 3L));
            //Agregar atributos
            structure.setProperties(attributes);

            bussinesEntityType.addStructure(structure);

            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
        
    }
    
    private void validarEstructuraDatosAcademicosEscuelaDelPaciente(){
        BussinesEntityType bussinesEntityType = null;
        String name = "datosAcademicosEstudianteEscuela";
        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", name);
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(name);

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName(name);
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de entidad de negocios
            List<Property> attributes = new ArrayList<Property>();

            attributes.add(buildProperty("academicoEscuela", "anioBasica", String.class.getName(), "", false, "Año de Básica", "Indique en que año de básica está", false, 1L));
            attributes.add(buildProperty("academicoEscuela", "paralelo", String.class.getName(), "", false, "Paralelo", "Indique en que paralelo está", false, 2L));
            
            //Agregar atributos
            structure.setProperties(attributes);

            bussinesEntityType.addStructure(structure);

            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
    
    private Property buildGroupTypeProperty(String name, String label, boolean showDefaultBussinesEntityProperties, String generatorName, Long minimumMembers, Long maximumMembers, String helpinline, Long sequence) {
        Property property = new Property();
        property.setName(name);
        property.setType(Group.class.getName());
        property.setValue(null);
        property.setRequired(true);
        property.setLabel(label);
        property.setHelpInline(helpinline);
        property.setShowDefaultBussinesEntityProperties(showDefaultBussinesEntityProperties);
        property.setGeneratorName(generatorName);
        property.setMinimumMembers(minimumMembers);
        property.setMaximumMembers(maximumMembers);
        property.setSequence(sequence);
        return property;
    }
    
    private Property buildStructureTypeProperty(String name, String label, String helpinline, String customForm, Long sequence) {
        Property property = new Property();
        property.setName(name);
        property.setType(Structure.class.getName());
        property.setValue(null);
        property.setRequired(true);
        property.setLabel(label);
        property.setHelpInline(helpinline);
        property.setCustomForm(customForm);
        property.setShowDefaultBussinesEntityProperties(false);
        property.setGeneratorName(null);
        property.setMaximumMembers(null);
        property.setSequence(sequence);
        return property;
    }
    
    private Property buildProperty(String name, String type, Serializable value, boolean required, String label, String helpinline, Long sequence) {
        Property property = new Property();
        property.setName(name);
        property.setType(type);
        property.setValue(value);
        property.setRequired(required);
        property.setLabel(label);
        property.setHelpInline(helpinline);
        property.setShowInColumns(false);
        property.setSequence(sequence);
        return property;
    }
    
    private Property buildProperty(String name, String type, Serializable value, boolean required, String label, String helpinline, boolean showInColumns, Long sequence) {
        Property property = new Property();
        property.setName(name);
        property.setType(type);
        property.setValue(value);
        property.setRequired(required);
        property.setLabel(label);
        property.setHelpInline(helpinline);
        property.setRender(null);
        property.setCustomForm(null);
        property.setShowInColumns(showInColumns);
        property.setSequence(sequence);
        return property;
    }
    
    private Property buildProperty(String name, String type, Serializable value, boolean required, String label, String helpinline, boolean showInColumns, String validator, Long sequence) {
        Property property = new Property();
        property.setName(name);
        property.setType(type);
        property.setValue(value);
        property.setRequired(required);
        property.setLabel(label);
        property.setHelpInline(helpinline);
        property.setRender(null);
        property.setCustomForm(null);
        property.setShowInColumns(showInColumns);
        property.setValidator(validator);
        property.setSequence(sequence);
        return property;
    }
    
    private Property buildProperty(String groupName, String name, String type, Serializable value, boolean required, String label, String helpinline, boolean showInColumns, Long sequence) {
        Property property = new Property();
        property.setGeneratorName(null);
        property.setGroupName(groupName);
        property.setName(name);
        property.setType(type);
        property.setValue(value);
        property.setRequired(required);
        property.setLabel(label);
        property.setHelpInline(helpinline);
        property.setRender(null);
        property.setCustomForm(null);
        property.setShowInColumns(showInColumns);
        property.setSequence(sequence);
        return property;
    }
    
    private Property buildPropertyAsSurvey(String name, String type, Serializable value, boolean required, String label, String helpinline, Long sequence) {
        Property property = new Property();
        property.setName(name);
        property.setType(type);
        property.setValue(value);
        property.setRequired(required);
        property.setLabel(label);
        property.setHelpInline(helpinline);
        property.setRender(null);
        property.setCustomForm(null);
        property.setShowInColumns(false);
        property.setSequence(sequence);
        property.setSurvey(true);
        return property;
    }
    private static final String[][] DATA = {
        {"0", "PLAN DE CUENTAS SEGUN NIIF COMPLETAS Y NIIF PARA PYMES", "SCHEMA", "Plan de cuentas según NIIF Completas y NIIF para PYMES"},
        {"1", "ACTIVO", "GENDER", "Conforman el estado de situación financiera, de flujo de efectivo y de evolución patrimonio"},
        {"10101", "EFECTIVO Y EQUIVALENTES AL EFECTIVO", "ACCOUNT", "Efectivo, comprende la caja y los depósitos bancarios a la vista.<br/>Equivalente al efectivo, son inversiones a corto plazo de gran liquidez (hasta 90 días), que son fácilmente convertibles en valores en efectivo, con riesgo insignificante de cambios en su valor. <br/>NIC 7p6, 7, 8, 9, 48, 49 NIIF para PYMES p7.2"},
        {"10102", "ACTIVOS FINANCIEROS", "ACCOUNT", "NIC 32 p11 - NIC 39 - NIIF 7 - NIIF 9 <br/>Secciones 11 y 12 NIIF para las PYMES"},
        {"1010201", "ACTIVOS FINANCIEROS A VALOR RAZONABLE CON CAMBIOS EN RESULTADOS", "ACCOUNT", "Son los activos financieros adquiridos para negociar activamente, con el objetivo de generar ganancia. <br/>Medición Inicial y Posterior:<br/>A valor razonable<br/>La variación se reconoce resultado del ejercicio"},
        {"2", "PASIVO", "GENDER", "Conforman el estado de situación financiera, de flujo de efectivo y de evolución patrimonio"},
        {"3", "PATRIMONIO", "GENDER", "Conforman el estado de situación financiera, de flujo de efectivo y de evolución patrimonio"},
        {"4", "CUENTAS DE RESULTADO ACREEDORAS", "GENDER", "Cuentas de gestión de partidas de resultados acreedoras y deudoras, indispensable para la elaboración del balance de perdidas y ganancias"},
        {"5", "CUENTAS DE RESULTADO DEUDORAS", "GENDER", "Cuentas de gestión de partidas de resultados acreedoras y deudoras, indispensable para la elaboración del balance de perdidas y ganancias"},
        {"6", "CUENTAS CONTINGENTES", "GENDER", "Agrupan las obligaciones eventuales"},
        {"7", "CUENTAS DE ORDEN", "GENDER", "Cuentas de orden y de control, indispensables para la buena administración"}
    };
}
