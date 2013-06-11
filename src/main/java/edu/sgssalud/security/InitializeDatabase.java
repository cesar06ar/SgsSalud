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
            session.getAttributesManager().addAttribute(u, "email", "glue@eqaula.org");
            members.add(u);
            //TODO revisar error al implementar la relacion entre un grupo y usuario.... 
            //session.getRelationshipManager().associateUser(g, u);             

            p = new Profile();
            p.setEmail("glue@eqaula.org");
            p.setUsername("admin");
            p.setPassword("4dm1nglue3");
            p.getIdentityKeys().add(u.getKey());
            p.setUsernameConfirmed(true);
            p.setShowBootcamp(true);
            
            p.setName("Administrador");
            p.setFirstname("Glue");
            p.setSurname("Adhesive Software");
            p.setCreatedOn(now);
            p.setLastUpdate(now);
            p.setActivationTime(now);
            p.setExpirationTime(Dates.addDays(now, 364));
            p.setAuthor(null); //Establecer al usuario actual
            p.setType(bussinesEntityType); //Relacionar con un tipo de entidad de negocio y su estructura
            p.buildAttributes(bussinesEntityService); //Crear la estructura de datos glue
            entityManager.persist(p);
            entityManager.flush();
            admin = p;
            
        }     
    }
    
    private void validateStructure() {
        validateStructureForProfile();
        validateStructureForPersonalData();
        validateStructureForSpouse();
        validateStructureForChildrens();
        validateStructureForStrategic();
        validateStructureForEducation();
        validateStructureForRole();
        validateStructureForTasks();
        validateStructureForCapacitacion();
        validateStructureForTrayectoriaLaboral();                
    }
    
    private void validateStructureForProfile() {
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
            attributes.add(buildStructureTypeProperty("Role", "Detalle del cargo que desempeña", "Detalle del cargo que desempeña", null, 5L));
            attributes.add(buildStructureTypeProperty("Strategic", "Datos estratégicos", "Custionario para recolectar información estratégica", null, 6L));
            attributes.add(buildGroupTypeProperty("Tasks", "Tareas a su cargo", false, null, 1L, 0L, "Detalle las tareas que corresponden a su cargo dentro de la institución", 7L));
            attributes.add(buildGroupTypeProperty("Capacitacion", "Eventos de Capacitación", false, null, 0L, 0L, "Detalle de capacitaciones con  respaldo fisico notarizado desde el año 2000 en adelante", 8L));
            attributes.add(buildGroupTypeProperty("TrayectoriaLaboral", "Trayectoria Laboral", false, null, 1L, 0L, "Detalle de la trayectoria laboral desde el año 2000 en adelante", 9L));


            //Agregar atributos
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
        
        System.out.println("Structure for Profile [" + bussinesEntityType + "]");
    }
       
   
    
    private void validateStructureForPersonalData() {
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

            /*
             * Personal
             ESTADO CIVIL	FECHA DE NACIMIENTO (en números) día/mes/año	GENERO	NACIONALIDAD	MODALIDAD: CONTRATO O NOMBRAMIENTO	LIBRETA  MILITAR           (sin guión)	TIPO DE SANGRE	
             * Discapacidad
             * Discapacidad     SI   /   NO	N° CONADIS (sin guión)       / SIN  TIENE CARNET	TIPO FÍSICA/MENTAL	% PORCENTAJE	
             * Dirección permanente
             * CALLE PRINCIPAL	No.	CALLE SECUNDARIA	REFERENCIA (SECTOR)	PROVINCIA	PARROQUIA	CANTON	TELÉFONO DOMICILIO SIN GUIÓN 	TELÉFONO CELULAR  SIN GUIÓN 	TELÉFONO DE TRABAJO  SIN GUIÓN     	CORREO ELECTRÓNICO  (INSTITUCIONAL/ LABORAL)	CORREO ELECTRÓNICO (PERSONAL)	
             * Contacto en emergencia
             * APELLIDOS	NOMBRES	TELÉFONO LOCAL      SIN GUIÓN 	TELEFONO CELULAR  SIN GUIÓN 	
             * DEclaración de bienes
             * No. DE NOTARIA DE DECLARACIÓN DE BIENES	LUGAR DE NOTARIA DE DECLARACION DE BIENES      (CIUDAD - CANTÓN)	FECHA DE DECLARACION DE BIENES            (en números) día/mes/año
             * AUTOIDENTIFICACION ETNICA	
             * AFROECUATORIANO/AFRODESCENDIENTE - BLANCO - INDIGENA - MESTIZO - MONTUBIO - MULATO - NEGRO - OTRA	
             * EN CASO DE SELECCIONAR INDIGENA:   ACHUAR - ANDOA - AWA - CHACHI - COFAN - EPERA - KICHWA - SECOYA - SHIWIAR - SHUAR - SIONA - TSACHILA - WAORANI - ZAPARA
             */
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
            attributes.add(buildProperty("Personal", "hobies", "java.lang.MultiLineString", null, false, "Hobies", "Las cosas que disfruta en su tiempo libre (separe con comas)", false, 12L));

            //Agregar atributos
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
        
    }
    
    private void validateStructureForSpouse() {
        BussinesEntityType bussinesEntityType = null;
        String name = "Spouse";
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

            //attributes.add(buildAttribute("birthday", Date.class.getName(), ago.getTime(), true, "Fecha de nacimiento", "Nunca olvidaremos su cumpleaños"));
            attributes.add(buildProperty("tipoRelacion", "java.lang.String[]", "Casado,Unión libre", true, "Tipo de Relacion", "Indique que relacion tiene", false, 1L));
            attributes.add(buildProperty("apellidos", String.class.getName(), null, true, "Apellidos", "Escriba sus Apellidos", true, 2L));
            attributes.add(buildProperty("nombres", String.class.getName(), null, true, "Nombres", "Escriba sus nombre", true, 3L));
            attributes.add(buildProperty("cedula", String.class.getName(), null, true, "Cédula / Pasaporte", "Digite su número de cédula o pasaporte sin guiones", true, "ciValidator", 4L));
            attributes.add(buildProperty("empresaTrabajo", String.class.getName(), null, false, "Empresa en la que Trabaja", "Indique la empresa que trabaja", false, 5L));
            attributes.add(buildProperty("funcionCargo", String.class.getName(), null, false, "Actividad / Función o Cargo", "Que cargo o funcion tiene", false, 6L));

            //Agregar atributos
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
        
    }
    
    private void validateStructureForChildrens() {
        BussinesEntityType bussinesEntityType = null;
        String name = "Childrens";
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
            
            attributes.add(buildProperty("apellidos", String.class.getName(), null, true, "Apellidos", "Escriba sus Apellidos", true, 1L));
            attributes.add(buildProperty("nombres", String.class.getName(), null, true, "Nombres", "Escriba sus nombre", true, 2L));
            attributes.add(buildProperty("cedula", String.class.getName(), null, true, "Cédula / Pasaporte", "Diguite su número de cédula o pasaporte sin guiones", true, "ciValidator", 3L));
            attributes.add(buildProperty("birthday", Date.class.getName(), ago.getTime(), false, "Fecha de nacimiento", "Nunca olvidaremos su cumpleaños", 4L));
            attributes.add(buildProperty("tipoInstruccion", "java.lang.String[]", "Pre-Basica,Basica,Bachillerato,Egresado,Superior,4 Nivel,Ninguno", false, "Tipo de Instrucción", "Indique el tipo de instrucción", 5L));

            //Agregar atributos
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
        
    }
    
    private void validateStructureForEducation() {
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
    
    private void validateStructureForRole() {
        BussinesEntityType bussinesEntityType = null;
        String name = "Role";
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
            
            attributes.add(buildProperty("role", String.class.getName(), "", true, "Cargo actual", "El cargo actual que desempeña", 1L));
            attributes.add(buildProperty("remuneracion", Double.class.getName(), 0.0, true, "Remuneración unificada", "Monto de remuneración unificada del cargo", 2L));
            attributes.add(buildProperty("jefe", String.class.getName(), "", true, "Jefe inmediato", "El nombre de su jefe inmediato", 3L));
            attributes.add(buildProperty("tipoContratacion", String.class.getName(), "", true, "Tipo de contrato", "La modalidad de contratación", 4L));
            attributes.add(buildProperty("tituloProfesional1", String.class.getName(), "", true, "Título profesional", "Indique el título profesional que posee", 5L));
            attributes.add(buildProperty("tituloProfesional2", String.class.getName(), "", false, "Otro título profesional", "Indique el título profesional que posee", 6L));
            attributes.add(buildProperty("tituloCuartoNivel1", String.class.getName(), "", false, "Título de cuarto nivel", "Indique el título de cuarto nivel que posee", 7L));
            attributes.add(buildProperty("tituloCuartoNivel2", String.class.getName(), "", false, "Otro título de cuarto nivel", "Indique el título de cuarto nivel que posee", 8L));
            attributes.add(buildProperty("aniosServicio", Long.class.getName(), 0, true, "Años de servicio", "Indique el tiempo en años que va laborando", 9L));
            attributes.add(buildProperty("historiaLaboral", String.class.getName(), "", true, "Historia laboral", "Indique el código de historia laboral", 10L));
            attributes.add(buildProperty("logrosPersonales", String.class.getName(), "", false, "Logros personales relevantes", "Indique los logros personales relavantes en relación a la institución", 11L));
            attributes.add(buildProperty("comentario", "java.lang.MultiLineString", "", false, "Comentario", "Haganos conocer sus ideas y/o comentarios acerca de este proceso", 12L));

            //Agregar atributos
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
    
    private void validateStructureForStrategic() {
        BussinesEntityType bussinesEntityType = null;
        String name = "Strategic";
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
            
            attributes.add(buildPropertyAsSurvey("mision", "java.lang.MultiLineString", null, true, "Cuál cree usted que debería ser la misión de la empresa pública?", "Analise la pregunta y conteste en el espacio correspondiente", 1L));
            attributes.add(buildPropertyAsSurvey("vision", "java.lang.MultiLineString", null, true, "Cuál cree usted que debería ser la visión de la empresa pública?", "Analise la pregunta y conteste en el espacio correspondiente", 2L));
            attributes.add(buildPropertyAsSurvey("objetivos", "java.lang.MultiLineString", null, true, "Cuál cree usted que debería ser los objetivos estratégicos de la empresa pública?", "Analise la pregunta y conteste en el espacio correspondiente", 3L));
            attributes.add(buildPropertyAsSurvey("fortalezas", "java.lang.MultiLineString", null, true, "Identifique fortalezas para la nueva empresa?", "Analise la pregunta y conteste en el espacio correspondiente. Utilice comas para separar varias.", 4L));
            attributes.add(buildPropertyAsSurvey("oportunidades", "java.lang.MultiLineString", null, true, "Identifique oportunidades para la nueva empresa?", "Analise la pregunta y conteste en el espacio correspondiente. Utilice comas para separar varias.", 5L));
            attributes.add(buildPropertyAsSurvey("debilidades", "java.lang.MultiLineString", null, true, "Identifique debilidades para la nueva empresa?", "Analise la pregunta y conteste en el espacio correspondiente. Utilice comas para separar varias.", 6L));
            attributes.add(buildPropertyAsSurvey("amenazas", "java.lang.MultiLineString", null, true, "Identifique amanezas para la nueva empresa?", "Analise la pregunta y conteste en el espacio correspondiente. Utilice comas para separar varias.", 7L));
            attributes.add(buildPropertyAsSurvey("empresavsunidad", "java.lang.MultiLineString", null, true, "Identifique oportunidades para la nueva empresa?", "Analise la pregunta y conteste en el espacio correspondiente. Utilice comas para separar varias.", 8L));
            attributes.add(buildPropertyAsSurvey("ventajas", "java.lang.MultiLineString", null, true, "Menciones ventajas de la empresa en relación a una unidad?", "Analise la pregunta y conteste en el espacio correspondiente. Utilice comas para separar varias.", 9L));
            attributes.add(buildPropertyAsSurvey("desventajas", "java.lang.MultiLineString", null, true, "Menciones desventajas de la empresa en relación a una unidad?", "Analise la pregunta y conteste en el espacio correspondiente. Utilice comas para separar varias.", 10L));
            attributes.add(buildPropertyAsSurvey("calidad", "java.lang.MultiLineString", null, true, "Ha evidenciado control de calidad en el ejercicio de su gestión?", "Analise la pregunta y conteste en el espacio correspondiente", 11L));
            attributes.add(buildPropertyAsSurvey("aporte", "java.lang.MultiLineString", null, true, "Cómo aportaría ud para la construcción de la nueva empresa pública?", "Analise la pregunta y conteste en el espacio correspondiente", 12L));
            attributes.add(buildPropertyAsSurvey("compromiso", "java.lang.MultiLineString", null, true, "Cuál sería su comprimiso personal para con la nueva empresa pública?", "Analise la pregunta y conteste en el espacio correspondiente", 13L));
            attributes.add(buildPropertyAsSurvey("valoracionservicio", "java.lang.MultiLineString", null, true, "Cómo cree ud que los usuarios valoran el servicio de la UMAPAL?", "Analise la pregunta y conteste en el espacio correspondiente", 14L));
            
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
    
    private void validateStructureForTasks() {
        BussinesEntityType bussinesEntityType = null;
        String name = "Tasks";
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
            List<Property> properties = new ArrayList<Property>();
            
            properties.add(buildProperty("taskName", String.class.getName(), null, true, "Tarea", "Resuma la tarea que realiza, use un verbo de acción. Ej.: Elaborar, Controlar, Notificar, etc", true, 1L));
            properties.add(buildProperty("taskSummary", String.class.getName(), null, true, "Descripción", "Describa en detalle la tarea que realiza, sea generoso.", true, 2L));
            properties.add(buildProperty("taskTime", Float.class.getName(), null, true, "Tiempo", "Tiempo promedio que le toma la tarea", true, 3L));
            properties.add(buildProperty("taskReportTo", String.class.getName(), null, true, "Responsable", "A quién reporta el cumplimiento de la tarea", true, 4L));
            //TODO agregar prpiedades para tareas

            //Agregar atributos
            structure.setProperties(properties);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
    
    private void validateStructureForCapacitacion() {
        BussinesEntityType bussinesEntityType = null;
        String name = "Capacitacion";
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

            //attributes.add(buildAttribute("birthday", Date.class.getName(), ago.getTime(), true, "Fecha de nacimiento", "Nunca olvidaremos su cumpleaños"));
            attributes.add(buildProperty("nameEvent", String.class.getName(), null, true, "Nombre del Evento", "Indique el evento que se realizo", true, 1L));
            attributes.add(buildProperty("typeEvent", "java.lang.String[]", "Curso,Seminario,Taller,Conferencia,Diplomado", true, "Tipo de Evento", "Indique que tipo de evento se realizó", true, 2L));
            attributes.add(buildProperty("auspiciante", String.class.getName(), null, false, "Auspiciante", "Indique el Auspiciante ya sea Empresa o Institución", 3L));
            attributes.add(buildProperty("duracionHoras", Long.class.getName(), null, true, "Duración en Horas", "Indique cuantas horas duro el Evento", true, 4L));
            attributes.add(buildProperty("country", String.class.getName(), null, false, "País", "País donde se realizo el evento", true, 5L));
            attributes.add(buildProperty("dateBegin", Date.class.getName(), ago.getTime(), false, "Fecha de Inicio", "Fecha que inicio el evento", 6L));
            attributes.add(buildProperty("dateEnd", Date.class.getName(), ago.getTime(), false, "Fecha de Finalización", "Fecha de Culminación", 7L));

            //Agregar atributos
            structure.setProperties(attributes);
            
            bussinesEntityType.addStructure(structure);
            
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
    
    private void validateStructureForTrayectoriaLaboral() {
        BussinesEntityType bussinesEntityType = null;
        String name = "TrayectoriaLaboral";
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
            attributes.add(buildProperty("tipoInstitucion", "java.lang.String[]", "Pública, Privada, Mixta, Otra", true, "Tipo de Institución", "Indique que tipo de institución", true, 1L));
            attributes.add(buildProperty("nombreInstitucion", String.class.getName(), null, true, "Nombre de la Institución", "Indique el nombre de la institución u organización", true, 2L));
            attributes.add(buildProperty("unidadAdministrativa", "java.lang.String[]", "Departamento,Área,Dirección", true, "Unidad Administrativa", "Indique que tipo de unidad administrativa", 3L));
            attributes.add(buildProperty("denominacionPuesto", String.class.getName(), null, false, "Denominación del Puesto", "Indique la denominación del puesto", true, 4L));
            attributes.add(buildProperty("fechaInicio", Date.class.getName(), ago.getTime(), false, "Fecha de Ingreso", "¿Cuándo ingreso?", 5L));
            attributes.add(buildProperty("fechaFin", Date.class.getName(), ago.getTime(), false, "Fecha de Salida", "¿Cuándo salió?", 6L));
            attributes.add(buildProperty("responsabilidades", "java.lang.MultiLineString", null, false, "Principales Responsabilidades", "Indique que responsabilidades tenia", 7L));
            attributes.add(buildProperty("motivoSalida", "java.lang.MultiLineString", null, false, "Motivo de Salida", "¿Por qué motivo salio?", 8L));

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
