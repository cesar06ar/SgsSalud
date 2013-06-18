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
package edu.sgssalud.service;

import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.paciente.Paciente_;
import edu.sgssalud.util.PersistenceUtil;
import edu.sgssalud.util.Strings;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author cesar
 */
public class PacienteServicio extends PersistenceUtil<Paciente> implements Serializable{
    
    private static final long serialVersionUID = 234L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(PacienteServicio.class);
    
    @Inject
    private BussinesEntityService bussinesEntityService;
    
    public PacienteServicio(){
        super(Paciente.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
        bussinesEntityService.setEntityManager(em);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void crear(final Paciente paciente) {
        paciente.setShowBootcamp(true);
        super.create(paciente);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(final Paciente user) {
        super.save(user);
        em.flush();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String getNombreUsuarioAleatorio(String seed) {
        String username = Strings.canonicalize(seed);
        while (!isNombreUsuarioDisponible(username)) {
            username += new Random(System.currentTimeMillis()).nextInt() % 100;
        }
        return username;
    }
    
    
    /*Metodo se utilizá en los Bean de las tablas de pacientes*/
    public long getPacienteCount() {
        return count(Paciente.class);
    }
    
    /*Metodo se utilizá en los Bean de las tablas de pacientes*/
    public List<Paciente> getPacientes(final int limit, final int offset) {
        return findAll(Paciente.class);
    }
    /*Metodo se utilizá en los Bean de las tablas de pacientes*/
    public List<Paciente> getPacientes() {
        List list = this.findAll(Paciente.class);
        return list;
    }
    /*Metodo para verificar si el nombreUsuario del Paciente esta disponible*/
    public boolean isNombreUsuarioDisponible(String nombreUsuario) {
        try {
            getPacientePorNombreUsuario(nombreUsuario);
            return false;
        } catch (NoResultException e) {
            return true;
        }
    }
    
    /*Metodo para buscar paciente por nombre de usuario*/
    public Paciente getPacientePorNombreUsuario(final String nombreUsuario) throws NoResultException {
        TypedQuery<Paciente> query = em.createQuery("SELECT p FROM Paciente p WHERE p.nombreUsuario = :nombreUsuario", Paciente.class);        
        query.setParameter("nombreUsuario", nombreUsuario);
        Paciente result = query.getSingleResult();
        return result;
    }
    
    public Paciente getPacientePorEmail(final String email) throws NoResultException {
        TypedQuery<Paciente> query = em.createQuery("SELECT p FROM Paciente p WHERE p.email = :email", Paciente.class);
        query.setParameter("email", email);
        Paciente result = query.getSingleResult();
        return result;
    }

    public boolean hasPacienteByIdentityKey(final String key) throws NoResultException {
        try {
            getPacientePorIdentityKey(key);
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public Paciente getPacientePorIdentityKey(final String key) throws NoResultException {
        TypedQuery<Paciente> query = em.createQuery(
                "SELECT p FROM Paciente p JOIN p.identityKeys k WHERE k = :identityKey", Paciente.class);
        query.setParameter("identityKey", key);
        Paciente result = query.getResultList().isEmpty() ? null : query.getResultList().get(0);
        return result;
    }
   
    public Paciente getPacientePorId(final Long id) {
        return (Paciente) findById(Paciente.class, id);
    }
    /*Método para validar cedula o pasaporte*/
   public boolean isDniDisponible(String cedula) {
        TypedQuery<Paciente> query = em.createQuery("SELECT p FROM Paciente p WHERE p.cedula = :cedula", Paciente.class);
        query.setParameter("cedula", cedula);
        Paciente result = query.getSingleResult();
        return result!=null;        
    }

    public boolean isDireccionEmailDisponoble(String email) {
        try {
            getPacientePorEmail(email);
            return false;
        } catch (NoResultException e) {
            return true;
        }
    }
    
    public Paciente buscarPorNombres(final String nombres) {
   
        log.info("Buscar paciente por nombres " + nombres);

        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Paciente> query = builder.createQuery(Paciente.class);
        Root<Paciente> bussinesEntityType = query.from(Paciente.class);
        query.where(builder.equal(bussinesEntityType.get(Paciente_.nombres), nombres));
        return getSingleResult(query);
        //return  null;
    }  
    
    
}

