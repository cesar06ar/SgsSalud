/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sgssalud.service.generic;

import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

/**
 *
 * @author wilman
 */
@Local
public interface CrudService {
    
    //public String LOCAL_NAME = "/lotcarmv/CrudService/local";    
    public String LOCAL_NAME = "/CrudService";    

    public <T> T create(T t);
    public <T> T find(Class type, Object id);
    public <T> T update(T t);
    public void delete(Class type, Object id);
    public <T> T getReference(Class type, Object id);
     
    public List findWithNamedQuery(String queryName);
    <T> T findSingleResultWithNamedQuery(String namedQueryName, Map parameters) 
            throws NonUniqueResultException, NoResultException;
    public List findWithNamedQuery(String queryName,int resultLimit);
    public List findWithNamedQuery(String namedQueryName, Map parameters);
    public List findWithNamedQuery(String namedQueryName, Map parameters,int resultLimit);
    public List findByNativeQuery(String sql, Class type);
       
    public int count(java.lang.Class type);
    public List findAll(Class type);
    public List findRange(Class type, int[] range);
    public EntityManager getEntityManager();
    public <T> T findwithJoinFetch(Class type, Object id, String... relationshipName)
            throws NonUniqueResultException, NoResultException;

}
