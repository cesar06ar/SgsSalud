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
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.model.labClinico.Parametros;
import edu.sgssalud.model.medicina.*;
import edu.sgssalud.model.odontologia.*;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.model.security.IdentityObjectAttribute;
import edu.sgssalud.model.security.IdentityObjectCredentialType;
import edu.sgssalud.model.security.IdentityObjectRelationshipType;
import edu.sgssalud.model.security.IdentityObjectType;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.util.Dates;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import javax.faces.context.FacesContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;
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
        //List<User> members = new ArrayList<User>();
        org.picketlink.idm.api.Group g = session.getPersistenceManager().findGroup("ADMIN", "GROUP");
        //session.getAttributesManager().
        if (g == null) {
            g = session.getPersistenceManager().createGroup("MEDICOS", "GROUP");
            g = session.getPersistenceManager().createGroup("ODONTOLOGOS", "GROUP");
            g = session.getPersistenceManager().createGroup("ENFERMEROS", "GROUP");
            g = session.getPersistenceManager().createGroup("SECRETARIA", "GROUP");
            g = session.getPersistenceManager().createGroup("FARMACEUTICOS", "GROUP");
            g = session.getPersistenceManager().createGroup("LABORATORISTAS", "GROUP");
            g = session.getPersistenceManager().createGroup("PACIENTE", "GROUP");
            g = session.getPersistenceManager().createGroup("ADMIN", "GROUP");
        }

        bussinesEntityType = query.getSingleResult();
        if (session.getPersistenceManager().findUser("admin") == null) {
            User u = session.getPersistenceManager().createUser("admin");
            session.getAttributesManager().updatePassword(u, "adminadmin");
            session.getAttributesManager().addAttribute(u, "email", "sgssalud@unl.edu");
            session.getAttributesManager().addAttribute(u, "estado", "ACTIVO");
            //members.add(u);
            //TODO revisar error al implementar la relacion entre un grupo y usuario.... 
            session.getRelationshipManager().associateUser(g, u);

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
        validarDatosEnfermedadesCIE10();
        validarDatosExamenLabCLinico();
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
            attributes.add(buildProperty("Dirección permanente", "country", String.class.getName(), "Ecuador", false, "País", "País de residencia", false, 4L));
            attributes.add(buildProperty("Dirección permanente", "city", String.class.getName(), "Loja", false, "Ciudad", "Ciudad de residencia", false, 5L));
            attributes.add(buildProperty("Dirección permanente", "address", String.class.getName(), null, false, "Dirección", "Calles y número de casa", false, 6L));
            attributes.add(buildProperty("Dirección permanente", "phone", String.class.getName(), null, false, "Teléfono", "Telefóno de contacto", false, 7L));
            attributes.add(buildProperty("Contacto en emergencia", "emergencyContact", String.class.getName(), null, false, "Contacta en caso de emergencia", "Sí se presenta alguna emergencia, a quién debemos llamar?", false, 8L));
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

            attributes.add(buildProperty("academicoColegio", "nivelEducativo", "java.lang.String[]", "Bachillerato*, E.G.B (Basica Superior)", false, "Nivel Educativo",
                    "Indique el Nivel Educativo "
                    + "<br/>Educacion General Basica (E.G.B) "
                    + "<br/>E.G.B - Basica Superior: corresponde a 8º., 9º. y 10º. para estudiantes de 12 a 14 años de edad"
                    + "<br/>Bachillerato", false, 1L));
            attributes.add(buildProperty("academicoColegio", "especialidad", String.class.getName(), "", false, "Especialidad", "Indique que especialidad sigue, si es estudiante de bachillerato", false, 1L));
            attributes.add(buildProperty("academicoColiegio", "curso", String.class.getName(), "", false, "Curso", "Indique el nombre en que está", false, 3L));
            attributes.add(buildProperty("academicoColegio", "paralelo", String.class.getName(), "", false, "Paralelo", "Indique en que paralelo está", false, 3L));
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

            attributes.add(buildProperty("academicoColegio", "nivelEducativo", "java.lang.String[]", "E.G.B (Preparatoria), E.G.B (Basica Elemental), E.G.B (Basica Media)*", false, "Nivel Educativo",
                    "Indique el Nivel Educativo "
                    + "<br/>Educacion General Basica (E.G.B) "
                    + "<br/>E.G.B - Preparatoria: corresponde a 1er grado, para estudiantes de 5 años de edad."
                    + "<br/>E.G.B - Basica Elemental: corresponde a 2º., 3º. grados, y 4º. para estudiantes de 6 a 8 años de edad"
                    + "<br/>E.G.B - Basica Media: corresponde a 5º., 6º. y 7º. grado, para estudiantes de 9 a 11 años de edad", false, 1L));
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
            attributes.add(buildStructureTypeProperty("alergiasFichaMedica", "ALERGIAS", "Información Ficha Medica", "/pages/depSalud/fichaMedica", 1L));
            attributes.add(buildStructureTypeProperty("habitosFichaMedica", "HÁBITOS", "Información Ficha Medica", "/pages/depSalud/fichaMedica", 2L));
            attributes.add(buildStructureTypeProperty("antecedentesPersonalesFichaMedica", "ANTECEDENTES PERSONALES", "Información Ficha Medica", "/pages/depSalud/fichaMedica", 3L));
            attributes.add(buildStructureTypeProperty("antecedentesFamiliaresFichaMedica", "ANTECEDENTES FAMILIARES", "Información Ficha Medica", "/pages/depSalud/fichaMedica", 4L));
            attributes.add(buildStructureTypeProperty("otrosFichaMedica", "OTROS", "Otra información de la Ficha médica", "/pages/depSalud/fichaMedica", 5L));
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

    public void validarOtrosFichaMedica() {
        BussinesEntityType bussinesEntityType = null;
        String name = "otrosFichaMedica";
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
            attributes.add(buildProperty("fichaGeneralAF", "otros", "java.lang.MultiLineString", "...", false, "Otros", "Agregar información adicional de ficha médica", false, 1L));
            

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
            attributes.add(buildProperty("consultaOdontEF", "piel", Boolean.class.getName(), "", false, "1. Piel", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 1L));
            attributes.add(buildProperty("consultaOdontEF", "tejidoMuscular", Boolean.class.getName(), "", false, "2. Tejido muscular", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 2L));
            attributes.add(buildProperty("consultaOdontEF", "puntosDolorosos", Boolean.class.getName(), "", false, "3. Puntos dolorosos", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 3L));
            attributes.add(buildProperty("consultaOdontEF", "pisoBoca", Boolean.class.getName(), "", false, "4. Piso de la boca", "", false, 4L));
            attributes.add(buildProperty("consultaOdontEF", "galndulaSalival", Boolean.class.getName(), "", false, "5. Glándulas salivales", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 5L));
            attributes.add(buildProperty("consultaOdontEF", "oclusion", Boolean.class.getName(), "", false, "6. Oclusión", "(Sin marcar [] = Sin Patologia <br/> marcado [x] = Con Patologia)", false, 6L));
            attributes.add(buildProperty("consultaOdontEF", "labios", Boolean.class.getName(), "", false, "7. Labios", "", false, 7L));
            attributes.add(buildProperty("consultaOdontEF", "atm", Boolean.class.getName(), "", false, "8. A.T.M", "", false, 8L));
            attributes.add(buildProperty("consultaOdontEF", "lengua", Boolean.class.getName(), "", false, "9. Lengua", "", false, 9L));
            attributes.add(buildProperty("consultaOdontEF", "carrillos", Boolean.class.getName(), "", false, "10. Carrillos", "", false, 10L));
            attributes.add(buildProperty("consultaOdontEF", "maxSuperior", Boolean.class.getName(), "", false, "11. Max. Superior", "Maxilar Superior", false, 11L));
            attributes.add(buildProperty("consultaOdontEF", "posicionMaxilar", Boolean.class.getName(), "", false, "12. Mal posición Maxilar", "", false, 12L));
            attributes.add(buildProperty("consultaOdontEF", "gangliosLinfaticos", Boolean.class.getName(), "", false, "13. Ganglios Linfáticos", "", false, 13L));
            attributes.add(buildProperty("consultaOdontEF", "organosSentidos", Boolean.class.getName(), "", false, "14. Órganos de los Sentidos", "", false, 14L));
            attributes.add(buildProperty("consultaOdontEF", "paladar", Boolean.class.getName(), "", false, "15. Paladar", "", false, 15L));
            attributes.add(buildProperty("consultaOdontEF", "encia", Boolean.class.getName(), "", false, "16. Encía", "", false, 16L));
            attributes.add(buildProperty("consultaOdontEF", "maxInferior", Boolean.class.getName(), "", false, "17. Max. inferior", "Maxilar Inferior", false, 17L));

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
            attributes.add(buildProperty("consultaOdontED", "malFormacionesDent", Boolean.class.getName(), "", false, "1. Mal formación dentarias", "", false, 1L));
            attributes.add(buildProperty("consultaOdontED", "desgastes", Boolean.class.getName(), "", false, "2. Desgastes", "", false, 2L));
            attributes.add(buildProperty("consultaOdontED", "pigmentaciones", Boolean.class.getName(), "", false, "3. Pigmentaciones", "", false, 3L));
            attributes.add(buildProperty("consultaOdontED", "malPosicionDent", Boolean.class.getName(), "", false, "4. Mal posición dentarias", "", false, 4L));
            attributes.add(buildProperty("consultaOdontED", "patologiaPopular", Boolean.class.getName(), "", false, "5. Patología popular", "", false, 5L));

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
            attributes.add(buildProperty("consultaOdontED", "placaBacteriana", Boolean.class.getName(), "", false, "1. Placa bacteriana", "", false, 1L));
            attributes.add(buildProperty("consultaOdontED", "materiaAlba", Boolean.class.getName(), "", false, "2. Materia alba", "", false, 2L));
            attributes.add(buildProperty("consultaOdontED", "calculo", Boolean.class.getName(), "", false, "3. Cálculo", "", false, 3L));

            //Agregar atributos
            structure.setProperties(attributes);
            bussinesEntityType.addStructure(structure);
            entityManager.persist(bussinesEntityType);
            entityManager.flush();
        }
    }

    public void validarDatosEnfermedadesCIE10() {
        TypedQuery<EnfermedadCIE10> query = entityManager.createQuery("from EnfermedadCIE10 b",
                EnfermedadCIE10.class);
        if (query.getResultList().isEmpty()) {
            try {
                /*
                 ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

                 //String CSV = "/home/cesar/NetBeansProjects/SgsSalud/src/main/webapp/resources/otros/XLS_Cie10.csv";
                 String rutaCsv = context.getRealPath("/resources/otros/XLS_Cie10.csv");
                 FileInputStream fstream = new FileInputStream(rutaCsv);
                 DataInputStream entrada = new DataInputStream(fstream);
                 BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
                 String strLinea;
                 EnfermedadCIE10 enf;
                 List<String> ls1;//= new ArrayList<String>();
                 //int id = 0;
                 while ((strLinea = buffer.readLine()) != null) {
                 String[] v = strLinea.split(";");
                 ls1 = Arrays.asList(v);
                 enf = new EnfermedadCIE10();
                 enf.setId(Long.parseLong(ls1.get(0)));
                 enf.setCodigo(ls1.get(1));
                 enf.setNombre(ls1.get(2));
                 entityManager.persist(enf);
                 //entityManager.flush();
                 //id++;
                 }
                 // Cerramos el archivo
                 entrada.close();
                 */
                //String cargarDatos = " COPY EnfermedadCIE10 (id, codigo, nombre) FROM '" + CSV + "' USING DELIMITERS ';' csv ";
                //int cargar = entityManager.createNativeQuery(cargarDatos).executeUpdate();
                //Query q = entityManager.createNativeQuery(cargarDatos);
                //q.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(" --> error cargar enfermedades cie10: " + e.getMessage());

            }
        }

    }

    public void validarDatosExamenLabCLinico() {
        TypedQuery<ExamenLabClinico> query = entityManager.createQuery("from ExamenLabClinico e",
                ExamenLabClinico.class);
        if (query.getResultList().isEmpty()) {
            Date now = Calendar.getInstance().getTime();
            Calendar ago = Calendar.getInstance();
            //ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
            ExamenLabClinico exam = null;
            List<ExamenLabClinico> enfs = new ArrayList<ExamenLabClinico>();
            List<Parametros> listaP = new ArrayList<Parametros>();
            Parametros p;
            exam = new ExamenLabClinico("Biometría", "001", "PRINCIPAL,FORMULA LEUCOCITARIA", 0.0, ExamenLabClinico.Tipo.HEMATOLÓGICOS, now);
            p = new Parametros("PRINCIPAL", "HEMATOCRITO:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("PRINCIPAL", "HEMOGLOBINA:", "g %", "0", "0");
            listaP.add(p);
            p = new Parametros("PRINCIPAL", "LEUCOCITOS:", "", "0", "0");
            listaP.add(p);
            p = new Parametros("PRINCIPAL", "PLAQUETAS:", "", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "NEUTROFILOS SEG:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "EOSINOFILOS:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "LINFOSITOS:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "MONOCITOS:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "BASOFILOS:", "%", "0", "0");
            listaP.add(p);
            exam.setParametros(listaP);
            enfs.add(exam);
            listaP = new ArrayList<Parametros>();
            exam = new ExamenLabClinico("Sangre", "002", "PRINCIPAL,FORMULA LEUCOCITARIA", 0.0, ExamenLabClinico.Tipo.HEMATOLÓGICOS, now);
            p = new Parametros("PRINCIPAL", "HEMATOCRITO:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("PRINCIPAL", "HEMOGLOBINA:", "g %", "0", "0");
            listaP.add(p);
            p = new Parametros("PRINCIPAL", "LEUCOCITOS:", "", "0", "0");
            listaP.add(p);
            p = new Parametros("PRINCIPAL", "PLAQUETAS:", "", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "NEUTROFILOS SEG:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "EOSINOFILOS:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "LINFOSITOS:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "MONOCITOS:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "BASOFILOS:", "%", "0", "0");
            listaP.add(p);
            exam.setParametros(listaP);
            enfs.add(exam);
            listaP = new ArrayList<Parametros>();
            exam = new ExamenLabClinico("Glucosa Basal", "010", null, 0.0, ExamenLabClinico.Tipo.BIOQUIMÍCO_Y_ENZIMÁTICOS, now);
            p = new Parametros(null, "resultado", "", "0", "0");
            exam.agregarParametro(p);
            enfs.add(exam);
            exam = new ExamenLabClinico("Colesterol", "011", null, 0.0, ExamenLabClinico.Tipo.BIOQUIMÍCO_Y_ENZIMÁTICOS, now);
            p = new Parametros(null, "resultado", "", "0", "0");
            exam.agregarParametro(p);
            enfs.add(exam);
            exam = new ExamenLabClinico("Sodio", "020", null, 0.0, ExamenLabClinico.Tipo.ELECTROLITOS, now);
            p = new Parametros(null, "resultado", "", "0", "0");
            exam.agregarParametro(p);
            enfs.add(exam);
            exam = new ExamenLabClinico("PCR", "030", null, 0.0, ExamenLabClinico.Tipo.MARCADORES_TUMORALES, now);
            p = new Parametros(null, "resultado", "", "0", "0");
            exam.agregarParametro(p);
            enfs.add(exam);
            exam = new ExamenLabClinico("T 3", "040", null, 0.0, ExamenLabClinico.Tipo.HORMONAS, now);
            p = new Parametros(null, "resultado", "", "0", "0");
            exam.agregarParametro(p);
            enfs.add(exam);
            exam = new ExamenLabClinico("T 4", "041", null, 0.0, ExamenLabClinico.Tipo.HORMONAS, now);
            p = new Parametros(null, "resultado", "", "0", "0");
            exam.agregarParametro(p);
            enfs.add(exam);
            exam = new ExamenLabClinico("ASTO", "050", "PRINCIPAL,FORMULA LEUCOCITARIA", 0.0, ExamenLabClinico.Tipo.INMUNOLÓGICOS, now);
            p = new Parametros(null, "resultado", "", "0", "0");
            exam.agregarParametro(p);
            enfs.add(exam);
            exam = new ExamenLabClinico("VDRL", "051", "PRINCIPAL,FORMULA LEUCOCITARIA", 0.0, ExamenLabClinico.Tipo.INMUNOLÓGICOS, now);
            p = new Parametros("PRINCIPAL", "HEMATOCRITO:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("PRINCIPAL", "HEMOGLOBINA:", "g %", "0", "0");
            listaP.add(p);
            p = new Parametros("PRINCIPAL", "LEUCOCITOS:", "Xmm3", "0", "0");
            listaP.add(p);
            p = new Parametros("PRINCIPAL", "UREA:", "mg%", "0", "0");
            listaP.add(p);
            p = new Parametros("PRINCIPAL", "ÁCIDO ÚRICO:", "mg%", "0", "0");
            listaP.add(p);
            p = new Parametros("PRINCIPAL", "CREATININA:", "mg%", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "NEUTROFILOS SEG:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "LINFOSITOS:", "%", "0", "0");
            listaP.add(p);
            p = new Parametros("FORMULA LEUCOCITARIA", "EOSINOFILOS:", "%", "0", "0");
            listaP.add(p);
            exam.setParametros(listaP);
            enfs.add(exam);
            listaP = new ArrayList<Parametros>();
            exam = new ExamenLabClinico("Citológico", "056", null, 0.0, ExamenLabClinico.Tipo.LÍQUIDOS_BIOLÓGICOS, now);
            p = new Parametros(null, "resultado", "", "0", "0");
            exam.agregarParametro(p);
            enfs.add(exam);
            exam = new ExamenLabClinico("Orina", "060", "FÍSICO,ELEMENTAL,MICROSCÓPICO", 0.0, ExamenLabClinico.Tipo.ORINA, now);
            p = new Parametros("FÍSICO", "Color:", "", "", "");
            listaP.add(p);
            p = new Parametros("FÍSICO", "Olor:", "", "", "");
            listaP.add(p);
            p = new Parametros("FÍSICO", "Aspecto:", "", "", "");
            listaP.add(p);
            p = new Parametros("FÍSICO", "Densidad: ", "", "", "");
            listaP.add(p);
            p = new Parametros("FÍSICO", "Ph:", "", "", "");
            listaP.add(p);
            p = new Parametros("ELEMENTAL", "Nitritos:", "+/-", "", "");
            listaP.add(p);
            p = new Parametros("ELEMENTAL", "Proteínas:", "+/-", "", "");
            listaP.add(p);
            p = new Parametros("ELEMENTAL", "Glucosa:", "+/-", "", "");
            listaP.add(p);
            p = new Parametros("ELEMENTAL", "C. Cetónicos:", "+/-", "", "");
            listaP.add(p);
            p = new Parametros("ELEMENTAL", "Bilirrubina:", "+/-", "", "");
            listaP.add(p);
            p = new Parametros("ELEMENTAL", " Sangre:", "+/-", "", "");
            listaP.add(p);
            p = new Parametros("MICROSCÓPICO", "Leucocitos:", "XC", "", "");
            listaP.add(p);
            p = new Parametros("MICROSCÓPICO", "BACTERIAS:", "+/-", "", "");
            listaP.add(p);
            exam.setParametros(listaP);
            enfs.add(exam);
            listaP = new ArrayList<Parametros>();
            exam = new ExamenLabClinico("Gota Fresca", "061", null, 0.0, ExamenLabClinico.Tipo.ORINA, now);
            p = new Parametros(null, "resultado", "", "0", "0");
            exam.agregarParametro(p);
            enfs.add(exam);
            exam = new ExamenLabClinico("UROANÁLISIS", "062", "FÍSICO,ELEMENTAL,MICROSCÓPICO", 0.0, ExamenLabClinico.Tipo.ORINA, now);
            p = new Parametros(null, "resultado", "", "0", "0");
            exam.agregarParametro(p);
            enfs.add(exam);
            exam = new ExamenLabClinico("COPROPARASITARIO", "070", null, 0.0, ExamenLabClinico.Tipo.HECES, now);
            p = new Parametros(null, "COLOR:", "", "", "");
            exam.agregarParametro(p);
            p = new Parametros(null, "CONSISTECIA:", "", "", "");
            exam.agregarParametro(p);
            p = new Parametros(null, "TROFOZOOITOS DE EMBADOMONAS INTESTINALIS:", "+/-", "", "");
            exam.agregarParametro(p);
            enfs.add(exam);
            exam = new ExamenLabClinico("SECRECIÒN VAGINAL", "070", "EN FRESCO,GRAM", 0.0, ExamenLabClinico.Tipo.SECRECIONES, now);
            p = new Parametros("EN FRESCO", "CELULAS EPIELIALES:", "+/-", "", "");
            listaP.add(p);
            p = new Parametros("EN FRESCO", "ESPORAS DE HONGO:", "", "", "");
            listaP.add(p);
            p = new Parametros("EN FRESCO", "LEUCOCITOS:", "", "", "");
            listaP.add(p);
            p = new Parametros("EN FRESCO", "FLORA BACTERIANA:", "+/-", "", "");
            listaP.add(p);
            p = new Parametros("GRAM", "COCOBACILOS:", "+/-", "", "");
            listaP.add(p);
            exam.setParametros(listaP);
            enfs.add(exam);
            exam = new ExamenLabClinico("ESPERMATOGRAMA", "070", null, 0.0, ExamenLabClinico.Tipo.OTROS, now);
            p = new Parametros(null, "resultado", "", "0", "0");
            exam.agregarParametro(p);
            enfs.add(exam);

            for (ExamenLabClinico e : enfs) {
                entityManager.persist(e);
                //entityManager.flush();
            }
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
