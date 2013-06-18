/**
 * 
 */
package edu.sgssalud.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@TransactionAttribute
public abstract class PersistenceUtil<T> implements Serializable {

    private static final long serialVersionUID = -276417828563635020L;
    protected EntityManager em;
    protected final Class<T> entityClass;

    public PersistenceUtil(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public abstract void setEntityManager(EntityManager em);

    protected <T> long count(final Class<T> type) {
        CriteriaBuilder qb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(type)));
        return getEntityManager().createQuery(cq).getSingleResult();
    }

    protected <T> void create(final T entity) {
        getEntityManager().persist(entity);
    }

    protected <T> void delete(final T entity) throws NoResultException {
        getEntityManager().remove(entity);
    }

    protected <T> T deleteById(final Class<T> type, final Long id) throws NoResultException {
        T object = findById(type, id);
        delete(object);
        return object;
    }

    @SuppressWarnings("unchecked")
    protected <T> T findById(final Class<T> type, final Long id) throws NoResultException {
        Class<?> clazz = getObjectClass(type);
        T result = (T) getEntityManager().find(clazz, id);
        if (result == null) {
            throw new NoResultException("No object of type: " + type + " with ID: " + id);
        }
        return result;
    }

    protected <T> void save(final T entity) {
        if (getEntityManager() == null) {
            throw new IllegalStateException("Must initialize EntityManager before using Services!");
        }

        getEntityManager().merge(entity);
    }

    protected <T> void refresh(final T entity) {
        getEntityManager().refresh(entity);
    }

    protected Class<?> getObjectClass(final Object type) throws IllegalArgumentException {
        Class<?> clazz = null;
        if (type == null) {
            throw new IllegalArgumentException("Null has no type. You must pass an Object");
        } else if (type instanceof Class<?>) {
            clazz = (Class<?>) type;
        } else {
            clazz = type.getClass();
        }
        return clazz;
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> findByNamedQuery(final String namedQueryName) {
        return getEntityManager().createNamedQuery(namedQueryName).getResultList();
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> findByNamedQuery(final String namedQueryName, final Object... params) {
        Query query = getEntityManager().createNamedQuery(namedQueryName);
        int i = 1;
        for (Object p : params) {
            query.setParameter(i++, p);
        }
        return query.getResultList();
    }

    protected <T> List<T> findAll(final Class<T> type) {
        CriteriaQuery<T> query = getEntityManager().getCriteriaBuilder().createQuery(type);
        query.from(type);
        return getEntityManager().createQuery(query).getResultList();
    }

    @SuppressWarnings("unchecked")
    protected <T> T findUniqueByNamedQuery(final String namedQueryName) throws NoResultException {
        return (T) getEntityManager().createNamedQuery(namedQueryName).getSingleResult();
    }

    @SuppressWarnings("unchecked")
    protected <T> T findUniqueByNamedQuery(final String namedQueryName, final Object... params)
            throws NoResultException {
        Query query = getEntityManager().createNamedQuery(namedQueryName);
        int i = 1;
        for (Object p : params) {
            query.setParameter(i++, p);
        }

        return (T) query.getSingleResult();
    }

    protected <T> T getSingleResult(final CriteriaQuery<T> query) {
        return this.<T>getTypedSingleResult(query);
    }

    protected <T> T getTypedSingleResult(final CriteriaQuery<T> query) {
        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    protected <T> List<T> getResultList(final CriteriaQuery<T> query) {
        return getEntityManager().createQuery(query).getResultList();
    }

    protected <T> List<T> getResultList(final CriteriaQuery<T> query,
            int maxresults, int firstresult) {
        return getEntityManager().createQuery(query).setMaxResults(maxresults)
                .setFirstResult(firstresult).getResultList();
    }

    protected CriteriaBuilder getCriteriaBuilder() {
        return getEntityManager().getCriteriaBuilder();
    }

    protected TypedQuery<?> createQuery(CriteriaQuery<?> criteriaQuery) {
        return getEntityManager().createQuery(criteriaQuery);
    }

    public T find(final long id) {
        return getEntityManager().find(entityClass, id);
    }

    public QueryData<T> find(int start, int end, String sortField,
            QuerySortOrder order, Map<String, Object> filters) {

        QueryData<T> queryData = new QueryData<T>();

        CriteriaBuilder cb = getCriteriaBuilder();
        CriteriaQuery<T> c = cb.createQuery(entityClass);
        Root<T> account = c.from(entityClass);
        c.select(account);

        CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
        Root<T> accountCount = countQ.from(entityClass);
        countQ.select(cb.count(accountCount));

        List<Predicate> criteria = new ArrayList<Predicate>();
        for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
            String filterProperty = it.next();
            Object filterValue = filters.get(filterProperty);
            ParameterExpression<?> pexp = cb.parameter(filterValue != null ? filterValue.getClass() : Object.class,
                    filterProperty);
            Predicate predicate = cb.equal(account.get(filterProperty), pexp);
            criteria.add(predicate);
        }

        if (criteria.size() == 1) {
            c.where(criteria.get(0));
            countQ.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
            countQ.where(cb.and(criteria.toArray(new Predicate[0])));
        }

        if (sortField != null) {
            Path<String> path = account.get(sortField);
            if (order == QuerySortOrder.ASC) {
                c.orderBy(cb.asc(path));
            } else {
                c.orderBy(cb.desc(path));
            }
        }

        TypedQuery<T> q = (TypedQuery<T>) createQuery(c);
        // q.setHint("org.hibernate.cacheable", true);
        TypedQuery<Long> countquery = (TypedQuery<Long>) createQuery(countQ);
        // countquery.setHint("org.hibernate.cacheable", true);

        for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
            String filterProperty = it.next();
            Object filterValue = filters.get(filterProperty);
            q.setParameter(filterProperty, filterValue);
            countquery.setParameter(filterProperty, filterValue);
        }

        q.setMaxResults(end - start);
        q.setFirstResult(start);

        queryData.setResult(q.getResultList());
        Long totalResultCount = countquery.getSingleResult();
        queryData.setTotalResultCount(totalResultCount);

        return queryData;
    }

    public <T> T find(final Class<T> clazz, final Long id) {
        return findById(clazz, id);
    }
}