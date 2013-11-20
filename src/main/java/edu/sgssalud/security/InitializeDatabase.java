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
import edu.sgssalud.model.medicina.*;
import edu.sgssalud.model.odontologia.*;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.model.security.IdentityObjectCredentialType;
import edu.sgssalud.model.security.IdentityObjectRelationshipType;
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

        if (entityManager.createQuery("select t from IdentityObjectRelationshipType t where t.name = :name")
                .setParameter("name", "JBOSS_IDENTITY_MEMBERSHIP")
                .getResultList().size() == 0) {
            IdentityObjectRelationshipType relation = new IdentityObjectRelationshipType();
            relation.setName("JBOSS_IDENTITY_MEMBERSHIP");
            entityManager.persist(relation);
        }

        if (entityManager.createQuery("select t from IdentityObjectCredentialType t where t.name = :name")
                .setParameter("name", "PASSWORD")
                .getResultList().size() == 0) {

            IdentityObjectCredentialType PASSWORD = new IdentityObjectCredentialType();
            PASSWORD.setName("PASSWORD");
            entityManager.persist(PASSWORD);
        }
    }

    private void validateSecurity() throws IdentityException {
        // Validate credential types
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
        org.picketlink.idm.api.Group g = session.getPersistenceManager().findGroup("ADMIN", "GROUP");
        if (g == null) {
            g = session.getPersistenceManager().createGroup("ADMIN", "GROUP");
            g = session.getPersistenceManager().createGroup("MEDICOS", "GROUP");
            g = session.getPersistenceManager().createGroup("ODONTOLOGOS", "GROUP");
            g = session.getPersistenceManager().createGroup("ENFERMEROS", "GROUP");
            g = session.getPersistenceManager().createGroup("SECRETARIA", "GROUP");
            g = session.getPersistenceManager().createGroup("FARMACEUTICOS", "GROUP");
            g = session.getPersistenceManager().createGroup("LABORATORISTAS", "GROUP");
        }

        bussinesEntityType = query.getSingleResult();
        if (session.getPersistenceManager().findUser("admin") == null) {
            User u = session.getPersistenceManager().createUser("admin");
            session.getAttributesManager().updatePassword(u, "adminadmin");
            session.getAttributesManager().addAttribute(u, "email", "cesar06ar@hotmail.com");
            members.add(u);
            //TODO revisar error al implementar la relacion entre un grupo y usuario.... 
            //session.getRelationshipManager().associateUser(g, u);             

            p = new Profile();
            p.setEmail("sgssalud@unl.edu");
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
        validarEstructuraDelPaciente();
        validarEstructuraDatosPersonalesDelPaciente();
        validarEstructuraDatosAcademicosColegioDelPaciente();
        validarEstructuraDatosAcademicosEscuelaDelPaciente();

        //validar estructuras de fichaMedica
        validarEstructuraFichaMedica();
        validarAlergiasFichaMedica();
        validarHabitosFichaMedica();
        validarAntecedentesPersonalesFichaMedica();
        validarAntecedentesFamiliaresFichaMedica();
        //validar estructuras de Historia Clinica 
        validarEstructuraHistoriaClinica();
        //validar estructuras de consulta medica
        validarEstructuraConsultaMedica();
        validarRevisionOrganosYSistemasConsultaMedica();
        validarExamenFisicoConsultaMedica();
        //validar estructuras de ficha Odontologica                 
        validarEstructuraFichaOdontologica();
        //validar estructuras de consulta odontologica
        validarEstructuraConsultaOdontologica();
        validarExamenFisicoConsultaOdont();
        validarExamenDentarioConsultaOdont();
        validarEvaluacionPeriodontalConsultaOdont();

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
            structure.setName(Profile.class.getName());
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de entidad de negocios
            List<Property> attributes = new ArrayList<Property>();

            attributes.add(buildStructureTypeProperty("PersonalData", "Datos personales", "Información personal relevante", "/pages/profile/data/personal", 1L));
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

    private void validarEstructuraDatosPersonalesDelPerfilDeUsuario() {
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
            structure.setName(name);
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
            structure.setName(name);
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
            structure.setName(Paciente.class.getName());
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de Tipos de entidades de negocio
            List<Property> attributes = new ArrayList<Property>();
            attributes.add(buildStructureTypeProperty("datosPersonalesPaciente", "Datos Personales", "Información personal relevante", "/pages/paciente/paciente", 1L));
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

    private void validarEstructuraDatosPersonalesDelPaciente() {
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

            attributes.add(buildProperty("Personal", "estadoCivil", "java.lang.String[]", "Soltero*,Casado,Unión libre,Divorciado,Viudo", false, "Estado civil", "Indique su estado civil", false, 1L));
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

    private void validarEstructuraDatosAcademicosColegioDelPaciente() {
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

    private void validarEstructuraDatosAcademicosEscuelaDelPaciente() {
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

    /*Estructuras del SGSSALUD*/
    public void validarEstructuraFichaMedica() {
        BussinesEntityType bussinesEntityType = null;

        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", FichaMedica.class.getName());
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(FichaMedica.class.getName());

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName(FichaMedica.class.getName());
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de Tipos de entidades de negocio
            List<Property> attributes = new ArrayList<Property>();
            attributes.add(buildStructureTypeProperty("alergiasFichaMedica", "ALERGIAS", "Información Ficha Medica", "/pages/medicina/fichaMedica", 1L));
            attributes.add(buildStructureTypeProperty("habitosFichaMedica", "HÁBITOS", "Información Ficha Medica", "/pages/medicina/fichaMedica", 2L));
            attributes.add(buildStructureTypeProperty("antecedentesPersonalesFichaMedica", "ANTECEDENTES PERSONALES", "Información Ficha Medica", "/pages/medicina/fichaMedica", 3L));
            attributes.add(buildStructureTypeProperty("antecedentesFamiliaresFichaMedica", "ANTECEDENTES FAMILIARES", "Información Ficha Medica", "/pages/paciente/paciente", 4L));
            //Agregar atributos
            structure.setProperties(attributes);

            bussinesEntityType.addStructure(structure);

            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }

    public void validarAlergiasFichaMedica() {
        BussinesEntityType bussinesEntityType = null;
        String name = "alergiasFichaMedica";
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
            attributes.add(buildProperty("fichaGeneralA", "penicilina", Boolean.class.getName(), "", false, "PENICILINA", "", false, 1L));
            attributes.add(buildProperty("fichaGeneralA", "antibiotico", Boolean.class.getName(), "", false, "ANTIBIOTICOS", "", false, 2L));
            attributes.add(buildProperty("fichaGeneralA", "anestesia", Boolean.class.getName(), "", false, "ANESTESIA", "", false, 3L));

            //Agregar atributos
            structure.setProperties(attributes);
            bussinesEntityType.addStructure(structure);
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }

    public void validarHabitosFichaMedica() {
        BussinesEntityType bussinesEntityType = null;
        String name = "habitosFichaMedica";
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
            attributes.add(buildProperty("fichaGeneralH", "ingestaAlcohol", "java.lang.String[]", "Frecuentemente,Esporádicamente,Nunca*", false, "Ingesta de Alcohol", "", false, 1L));
            attributes.add(buildProperty("fichaGeneralH", "Tabaco", "java.lang.String[]", "Frecuentemente,Esporádicamente,Nunca*", false, "Consumo de Tabaco", "", false, 2L));
            attributes.add(buildProperty("fichaGeneralH", "actividadFisica", "java.lang.String[]", "Frecuente,Esporádica*,Nunca", false, "Actividad Física", "", false, 3L));
            attributes.add(buildProperty("fichaGeneralH", "estupefacientes", "java.lang.String[]", "Frecuentemente,Esporádicamente,Nunca*", false, "Consumo de Estupefacientes", "", false, 4L));

            //Agregar atributos
            structure.setProperties(attributes);
            bussinesEntityType.addStructure(structure);
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }

    public void validarAntecedentesPersonalesFichaMedica() {
        BussinesEntityType bussinesEntityType = null;
        String name = "antecedentesPersonalesFichaMedica";
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
            attributes.add(buildProperty("fichaGeneralAP", "crecimientoNormal", Boolean.class.getName(), "", false, "1. Crecimiento normal", "", false, 1L));
            attributes.add(buildProperty("fichaGeneralAp", "vacunasCompletas", Boolean.class.getName(), "", false, "2. Vacunas Completas", "", false, 2L));
            attributes.add(buildProperty("fichaGeneralAP", "enfCronicas", Boolean.class.getName(), "", false, "3. Enf. Cronicas", "Enfermedades Cronicas", false, 3L));
            attributes.add(buildProperty("fichaGeneralAP", "enfContagiosas", Boolean.class.getName(), "", false, "4. Enf. Contagiosas", "Enfermedades Contagiosas", false, 4L));
            attributes.add(buildProperty("fichaGeneralAP", "otros", Boolean.class.getName(), "", false, "5. Otros", "", false, 11L));

            //Agregar atributos
            structure.setProperties(attributes);
            bussinesEntityType.addStructure(structure);
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }

    public void validarAntecedentesFamiliaresFichaMedica() {
        BussinesEntityType bussinesEntityType = null;
        String name = "antecedentesFamiliaresFichaMedica";
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
            attributes.add(buildProperty("fichaGeneralAF", "diabetes", Boolean.class.getName(), "", false, "2. Diabetes", "", false, 1L));
            attributes.add(buildProperty("fichaGeneralAF", "hemorragais", Boolean.class.getName(), "", false, "3. Hemorragias", "", false, 2L));
            attributes.add(buildProperty("fichaGeneralAF", "hipertension", Boolean.class.getName(), "", false, "4. Hipertensión", "", false, 3L));
            attributes.add(buildProperty("fichaGeneralAF", "tuberculosis", Boolean.class.getName(), "", false, "5. Tuberculosis", "", false, 4L));
            attributes.add(buildProperty("fichaGeneralAF", "asma", Boolean.class.getName(), "", false, "6. Asma", "", false, 5L));
            attributes.add(buildProperty("fichaGeneralAF", "enfCardiaca", Boolean.class.getName(), "", false, "7. Enf. Cardiaca", "", false, 6L));
            attributes.add(buildProperty("fichaGeneralAF", "cancer", Boolean.class.getName(), "", false, "8. Cancer", "", false, 7L));

            //Agregar atributos
            structure.setProperties(attributes);
            bussinesEntityType.addStructure(structure);
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }

    public void validarEstructuraHistoriaClinica() {
        BussinesEntityType bussinesEntityType = null;

        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", HistoriaClinica.class.getName());
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(HistoriaClinica.class.getName());

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName(HistoriaClinica.class.getName());
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de Tipos de entidades de negocio
            List<Property> attributes = new ArrayList<Property>();
            //attributes.add(buildStructureTypeProperty("antecedentesPersonalesFichaMedica", "Antecedentes Personales", "Información Ficha Medica", "/pages/medicina/fichaMedica", 1L));

            //Agregar atributos
            structure.setProperties(attributes);

            bussinesEntityType.addStructure(structure);

            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }

    public void validarEstructuraFichaOdontologica() {
        BussinesEntityType bussinesEntityType = null;

        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", FichaOdontologica.class.getName());
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(FichaOdontologica.class.getName());

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName(FichaOdontologica.class.getName());
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de Tipos de entidades de negocio           

            bussinesEntityType.addStructure(structure);

            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }

    public void validarEstructuraConsultaMedica() {
        BussinesEntityType bussinesEntityType = null;

        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", ConsultaMedica.class.getName());
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(ConsultaMedica.class.getName());

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName(ConsultaMedica.class.getName());
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de Tipos de entidades de negocio
            List<Property> attributes = new ArrayList<Property>();
            attributes.add(buildStructureTypeProperty("revisionOrganosYSistemasCM", "REVISIÓN DE ÓRGANOS Y SISTEMAS", "Revisión Actual de Órganos y Sistemas", "/pages/medicina/fichaMedica", 1L));
            attributes.add(buildStructureTypeProperty("examenFisicoCM", "EXAMEN FÍSICO", "Examen Físico", "/pages/medicina/fichaMedica", 1L));

            //Agregar atributos
            structure.setProperties(attributes);

            bussinesEntityType.addStructure(structure);

            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
    
    public void validarRevisionOrganosYSistemasConsultaMedica() {
        BussinesEntityType bussinesEntityType = null;
        String name = "revisionOrganosYSistemasCM";
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
            attributes.add(buildProperty("consultaMedicaROS", "orgSentidos", Boolean.class.getName(), "", false, "1. Órg. de Sentidos", "Órganos de los Sentidos <br/>(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 1L));
            attributes.add(buildProperty("consultaMedicaROS", "respiratorios", Boolean.class.getName(), "", false, "2. Respiratorio", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 2L));
            attributes.add(buildProperty("consultaMedicaROS", "cardioVascular", Boolean.class.getName(), "", false, "3. Cardio Vascular", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 3L));
            attributes.add(buildProperty("consultaMedicaROS", "digestivo", Boolean.class.getName(), "", false, "4. Digestivo", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 4L));
            attributes.add(buildProperty("consultaMedicaROS", "genital", Boolean.class.getName(), "", false, "5. Genital", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 5L));
            attributes.add(buildProperty("consultaMedicaROS", "urinario", Boolean.class.getName(), "", false, "6. Urinario", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 6L));
            attributes.add(buildProperty("consultaMedicaROS", "musculoEsq", Boolean.class.getName(), "", false, "7. Mús. Esquelético", "Músculo Esquelético <br/> (Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 7L));
            attributes.add(buildProperty("consultaMedicaROS", "endocrino", Boolean.class.getName(), "", false, "8. Endocrino", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 8L));
            attributes.add(buildProperty("consultaMedicaROS", "hemoLinfatico", Boolean.class.getName(), "", false, "9. Hemo Linfático", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 9L));
            attributes.add(buildProperty("consultaMedicaROS", "nervioso", Boolean.class.getName(), "", false, "10. Nervioso", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 10L));            
            //Agregar atributos
            structure.setProperties(attributes);
            bussinesEntityType.addStructure(structure);
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
    
     public void validarExamenFisicoConsultaMedica() {
        BussinesEntityType bussinesEntityType = null;
        String name = "examenFisicoCM";
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
            attributes.add(buildProperty("consultaMedicaEF", "cabeza", Boolean.class.getName(), "", false, "1. Cabeza", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 1L));
            attributes.add(buildProperty("consultaMedicaEF", "cuello", Boolean.class.getName(), "", false, "2. Cuello", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 2L));
            attributes.add(buildProperty("consultaMedicaEF", "torax", Boolean.class.getName(), "", false, "3. Torax", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 3L));
            attributes.add(buildProperty("consultaMedicaEF", "abdomen", Boolean.class.getName(), "", false, "4. Abdomen", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 4L));
            attributes.add(buildProperty("consultaMedicaEF", "pelvis", Boolean.class.getName(), "", false, "5. Pelvis", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 5L));
            attributes.add(buildProperty("consultaMedicaEF", "extremidades", Boolean.class.getName(), "", false, "6. Extremidades", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 6L));            
            
            //Agregar atributos
            structure.setProperties(attributes);
            bussinesEntityType.addStructure(structure);
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }  
         
    public void validarEstructuraConsultaOdontologica() {
        BussinesEntityType bussinesEntityType = null;

        try {
            TypedQuery<BussinesEntityType> query = entityManager.createQuery("from BussinesEntityType b where b.name=:name",
                    BussinesEntityType.class);
            query.setParameter("name", ConsultaOdontologica.class.getName());
            bussinesEntityType = query.getSingleResult();
        } catch (NoResultException e) {
            bussinesEntityType = new BussinesEntityType();
            bussinesEntityType.setName(ConsultaOdontologica.class.getName());

            //Agrupaciones de propiedades
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            Structure structure = null;
            structure = new Structure();
            structure.setName(ConsultaOdontologica.class.getName());
            structure.setCreatedOn(now);
            structure.setLastUpdate(now);

            //Lista de atributos de Tipos de entidades de negocio
            List<Property> attributes = new ArrayList<Property>();
            attributes.add(buildStructureTypeProperty("examenFisicoConsultaOdont", "EXAMEN FÍSICO", "Información Ficha Odontologica", "/pages/medicina/fichaMedica", 1L));
            attributes.add(buildStructureTypeProperty("examenDentarioConsultaOdont", "EXAMEN DENTARIO", "Información Ficha Odontologica", "/pages/medicina/fichaMedica", 2L));
            attributes.add(buildStructureTypeProperty("evaluacionPeriodontalConsultaOdont", "EVALUACIÓN PERIODONTAL", "Información Ficha Odontologica", "/pages/medicina/fichaMedica", 3L));

            //Agregar atributos
            structure.setProperties(attributes);

            bussinesEntityType.addStructure(structure);

            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
    
    public void validarExamenFisicoConsultaOdont() {
        BussinesEntityType bussinesEntityType = null;
        String name = "examenFisicoConsultaOdont";
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
            attributes.add(buildProperty("consultaOdontEF", "piel", Boolean.class.getName(), "", false, "Piel", "", false, 1L));
            attributes.add(buildProperty("consultaOdontEF", "tejidoMuscular", Boolean.class.getName(), "", false, "Tejido muscular", "", false, 2L));
            attributes.add(buildProperty("consultaOdontEF", "puntosDolorosos", Boolean.class.getName(), "", false, "Puntos dolorosos", "", false, 3L));
            attributes.add(buildProperty("consultaOdontEF", "pisoBoca", Boolean.class.getName(), "", false, "Piso de la boca", "", false, 4L));
            attributes.add(buildProperty("consultaOdontEF", "galndulaSalival", Boolean.class.getName(), "", false, "Glándulas salivales", "", false, 5L));
            attributes.add(buildProperty("consultaOdontEF", "oclusion", Boolean.class.getName(), "", false, "Oclusión", "", false, 6L)); 
            attributes.add(buildProperty("consultaOdontEF", "labios", Boolean.class.getName(), "", false, "Labios", "", false, 7L)); 
            attributes.add(buildProperty("consultaOdontEF", "atm", Boolean.class.getName(), "", false, "A.T.M", "", false, 8L)); 
            attributes.add(buildProperty("consultaOdontEF", "lengua", Boolean.class.getName(), "", false, "Lengua", "", false, 9L)); 
            attributes.add(buildProperty("consultaOdontEF", "carrillos", Boolean.class.getName(), "", false, "Carrillos", "", false, 10L)); 
            attributes.add(buildProperty("consultaOdontEF", "maxSuperior", Boolean.class.getName(), "", false, "Max. Superior", "", false, 11L)); 
            attributes.add(buildProperty("consultaOdontEF", "posicionMaxilar", Boolean.class.getName(), "", false, "Mal posición Maxilar", "", false, 12L)); 
            attributes.add(buildProperty("consultaOdontEF", "gangliosLinfaticos", Boolean.class.getName(), "", false, "Ganglios Linfáticos", "", false, 13L)); 
            attributes.add(buildProperty("consultaOdontEF", "organosSentidos", Boolean.class.getName(), "", false, "Órganos de los Sentidos", "", false, 14L)); 
            attributes.add(buildProperty("consultaOdontEF", "paladar", Boolean.class.getName(), "", false, "Paladar", "", false, 15L)); 
            attributes.add(buildProperty("consultaOdontEF", "encia", Boolean.class.getName(), "", false, "Encía", "", false, 16L)); 
            attributes.add(buildProperty("consultaOdontEF", "maxInferior", Boolean.class.getName(), "", false, "Max. inferior", "", false, 17L)); 
            
            //Agregar atributos
            structure.setProperties(attributes);
            bussinesEntityType.addStructure(structure);
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
    
    public void validarExamenDentarioConsultaOdont() {
        BussinesEntityType bussinesEntityType = null;
        String name = "examenDentarioConsultaOdont";
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
            attributes.add(buildProperty("consultaOdontED", "malFormacionesDent", Boolean.class.getName(), "", false, "Mal formación dentarias", "", false, 1L));
            attributes.add(buildProperty("consultaOdontED", "desgastes", Boolean.class.getName(), "", false, "Desgastes", "", false, 2L));
            attributes.add(buildProperty("consultaOdontED", "pigmentaciones", Boolean.class.getName(), "", false, "Pigmentaciones", "", false, 3L));
            attributes.add(buildProperty("consultaOdontED", "malPosicionDent", Boolean.class.getName(), "", false, "Mal posición dentarias", "", false, 4L));
            attributes.add(buildProperty("consultaOdontED", "patologiaPopular", Boolean.class.getName(), "", false, "Patología popular", "", false, 5L));            
            
            //Agregar atributos
            structure.setProperties(attributes);
            bussinesEntityType.addStructure(structure);
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
    
    public void validarEvaluacionPeriodontalConsultaOdont() {
        BussinesEntityType bussinesEntityType = null;
        String name = "evaluacionPeriodontalConsultaOdont";
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
            attributes.add(buildProperty("consultaOdontED", "placaBacteriana", Boolean.class.getName(), "", false, "Placa bacteriana", "", false, 1L));
            attributes.add(buildProperty("consultaOdontED", "materiaAlba", Boolean.class.getName(), "", false, "Materia alba", "", false, 2L));
            attributes.add(buildProperty("consultaOdontED", "calculo", Boolean.class.getName(), "", false, "Cálculo", "", false, 3L));           
            
            //Agregar atributos
            structure.setProperties(attributes);
            bussinesEntityType.addStructure(structure);
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }
    /*FIN Estructuras del SGSSALUD*/
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
}
