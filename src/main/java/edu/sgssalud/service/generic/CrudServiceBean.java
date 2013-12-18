/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sgssalud.service.generic;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

/**
 *
 * @author wilman
 */
@Stateless(name="CrudService")
//@TransactionAttribute(TransactionAttributeType.MANDATORY)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CrudServiceBean implements CrudService {

    @PersistenceContext
    EntityManager em;
    
    /**
     *
     * @param <T>
     * @param t
     * @return
     */
    @Override
    public <T> T create(T t) {
        this.em.persist(t);
        this.em.flush();
        this.em.refresh(t);
        return t;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T find(Class type, Object id) {
       return (T) this.em.find(type, id);
    }

    /**
     *
     * @param type
     * @param id
     */
    @Override
    public void delete(Class type, Object id) {
       Object ref = this.em.getReference(type, id);
       this.em.remove(ref);
    }

    @Override
    public <T> T update(T t) {
        return (T)this.em.merge(t);
    }
    
    @Override
    public <T> T getReference(Class type, Object id){
        return (T) this.em.getReference(type, id);
    }

    @Override
    public List findWithNamedQuery(String namedQueryName){
        return this.em.createNamedQuery(namedQueryName).getResultList();
    }
    
    @Override
    public <T> T findSingleResultWithNamedQuery(String namedQueryName, 
         Map parameters) throws NonUniqueResultException, 
            NoResultException{
        Set<Entry> rawParameters = parameters.entrySet();
        Query query;
        query = this.em.createNamedQuery(namedQueryName);
        for (Entry entry : rawParameters) {
            query.setParameter(entry.getKey().toString(), entry.getValue());
        }
        return (T)query.getSingleResult();
    }
    
    
    @Override
    public List findWithNamedQuery(String namedQueryName, Map parameters){
        return findWithNamedQuery(namedQueryName, parameters, 0);
    }

    @Override
    public List findWithNamedQuery(String queryName, int resultLimit) {
        return this.em.createNamedQuery(queryName).
                setMaxResults(resultLimit).
                getResultList();    
    }

    /**
     *
     * @param sql
     * @param type
     * @return
     */
    @Override
    public List findByNativeQuery(String sql, Class type) {
        return this.em.createNativeQuery(sql, type).getResultList();
    }
   
    @Override
    public List findWithNamedQuery(String namedQueryName, Map parameters, 
        int resultLimit){
        Set<Entry> rawParameters = parameters.entrySet();
        Query query;
        query = this.em.createNamedQuery(namedQueryName);
        if(resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        for (Entry entry : rawParameters) {
            query.setParameter(entry.getKey().toString(), entry.getValue());
        }
        return query.getResultList();
    }
    
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    @Override
    public List findAll(Class type) {
        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(type));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List findRange(Class type, int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(type));
        javax.persistence.Query q = em.createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    @Override
    public int count(Class type) {
        javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root rt = cq.from(type);
        cq.select(em.getCriteriaBuilder().count(rt));
        javax.persistence.Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    @Override
    public <T> T findwithJoinFetch(Class type, Object id, String... relationshipName) 
            throws NonUniqueResultException, NoResultException {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
               
        Root<T> from = criteriaQuery.from(type);
        for(String rn: relationshipName){
            from.fetch(rn, JoinType.LEFT);
        }
        
        
        criteriaQuery.where(criteriaBuilder.equal(from.get("id"), id));
        
        TypedQuery<T> query = em.createQuery(criteriaQuery);
        return (T)query.getSingleResult();
    }

}
