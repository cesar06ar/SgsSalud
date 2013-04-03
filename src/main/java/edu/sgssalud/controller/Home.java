/*
 * Copyright 2012 jlgranda.
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
package edu.sgssalud.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.TransactionAttribute;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import edu.sgssalud.controller.Expressions.ValueExpression;
import edu.sgssalud.util.QueryData;
import edu.sgssalud.util.QuerySortOrder;

/**
 * Seam 2 Home Class Clone
 *
 * @author jlgranda
 *
 */
public abstract class Home<T, E> extends MutableController<T> implements Serializable {

    private Object id;
    protected E instance;
    private Class<E> entityClass;
    protected Expressions.ValueExpression newInstance;
    private String deletedMessage = "Successfully deleted";
    private String createdMessage = "Successfully created";
    private String updatedMessage = "Successfully updated";

    protected void updatedMessage() {
        debug("updated entity #0 #1", getEntityClass().getName(), getId());
        //getFacesMessages().addFromResourceBundleOrDefault( SEVERITY_INFO, getUpdatedMessageKey(), getUpdatedMessage() );
    }

    protected void deletedMessage() {
        debug("deleted entity #0 #1", getEntityClass().getName(), getId());
        //getFacesMessages().addFromResourceBundleOrDefault( SEVERITY_INFO, getDeletedMessageKey(), getDeletedMessage() );
    }

    protected void createdMessage() {
        debug("created entity #0 #1", getEntityClass().getName(), getId());
        //getFacesMessages().addFromResourceBundleOrDefault( SEVERITY_INFO, getCreatedMessageKey(), getCreatedMessage() );
    }

    public void create() {
        if (getEntityClass() == null) {
            throw new IllegalStateException("entityClass is null");
        }
    }

    @TransactionAttribute
    public E getInstance() {
        if (instance == null) {
            initInstance();
        }
        return instance;
    }

    public void clearInstance() {
        setInstance(null);
        setId(null);
    }

    protected void initInstance() {
        if (isIdDefined()) {
            if (true /*!isTransactionMarkedRollback()*/) {
                //we cache the instance so that it does not "disappear"
                //after remove() is called on the instance
                //is this really a Good Idea??
                setInstance(find());
            }
        } else {
            setInstance(createInstance());
        }
    }

    protected E find() {
        return null;
    }

    protected E handleNotFound() {
        //throw new EntityNotFoundException( getId(), getEntityClass() );
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected E createInstance() {
        if (newInstance != null) {
            return (E) newInstance.getValue();
        } else if (getEntityClass() != null) {
            try {
                return getEntityClass().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    public Class<E> getEntityClass() {
        if (entityClass == null) {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                entityClass = (Class<E>) paramType.getActualTypeArguments()[0];
            } else {
                throw new IllegalArgumentException("Could not guess entity class by reflection");
            }
        }
        return entityClass;
    }

    public void setEntityClass(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        if (setDirty(this.id, id)) {
            setInstance(null);
        }
        this.id = id;
    }

    protected void assignId(Object id) {
        setDirty(this.id, id);
        this.id = id;
    }

    public boolean isIdDefined() {
        return getId() != null && !"".equals(getId());
    }

    public void setInstance(E instance) {
        setDirty(this.instance, instance);
        this.instance = instance;
    }

    public Expressions.ValueExpression getNewInstance() {
        return newInstance;
    }

    public void setNewInstance(ValueExpression newInstance) {
        this.newInstance = newInstance;
    }

    public String getCreatedMessage() {
        return createdMessage;
    }

    public void setCreatedMessage(String createdMessage) {
        this.createdMessage = createdMessage;
    }

    public String getDeletedMessage() {
        return deletedMessage;
    }

    public void setDeletedMessage(String deletedMessage) {
        this.deletedMessage = deletedMessage;
    }

    public String getUpdatedMessage() {
        return updatedMessage;
    }

    public void setUpdatedMessage(String updatedMessage) {
        this.updatedMessage = updatedMessage;
    }

    protected String getMessageKeyPrefix() {
        String className = getEntityClass().getName();
        return className.substring(className.lastIndexOf('.') + 1) + '_';
    }

    protected String getCreatedMessageKey() {
        return getMessageKeyPrefix() + "created";
    }

    protected String getUpdatedMessageKey() {
        return getMessageKeyPrefix() + "updated";
    }

    protected String getDeletedMessageKey() {
        return getMessageKeyPrefix() + "deleted";
    }

    protected String getSimpleEntityName() {
        String name = getEntityName();
        return name.lastIndexOf(".") > 0 && name.lastIndexOf(".") < name.length() ? name.substring(name.lastIndexOf(".") + 1, name.length()) : name;
    }

    protected abstract String getEntityName();
    private static final long serialVersionUID = 7520839069908084915L;
    
    /******************************************************
     * Adapted from PersistenceUtil
     ******************************************************/
    protected <E> long count(final Class<E> type) {
        CriteriaBuilder qb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(type)));
        return getEntityManager().createQuery(cq).getSingleResult();
    }

    protected <E> void create(final E entity) {
        getEntityManager().persist(entity);
    }
    
    protected <E> void save(final E entity) {
        if (getEntityManager() == null) {
            throw new IllegalStateException("Must initialize EntityManager before using Services!");
        }
        getEntityManager().merge(entity);
    }

    protected <E> void refresh(final E entity) {
        getEntityManager().refresh(entity);
    }

    protected <E> void delete(final E entity) throws NoResultException {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    protected <E> E deleteById(final Class<E> type, final Long id) throws NoResultException {
        E object = findById(type, id);
        delete(object);
        return object;
    }

    @SuppressWarnings("unchecked")
    protected <E> E findById(final Class<E> type, final Long id) throws NoResultException {
        Class<?> clazz = getObjectClass(type);
        E result = (E) getEntityManager().find(clazz, id);
        if (result == null) {
            throw new NoResultException("No object of type: " + type + " with ID: " + id);
        }
        return result;
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
    protected <E> List<E> findByNamedQuery(final String namedQueryName) {
        return getEntityManager().createNamedQuery(namedQueryName).getResultList();
    }

    @SuppressWarnings("unchecked")
    protected <E> List<E> findByNamedQuery(final String namedQueryName, final Object... params) {
        Query query = getEntityManager().createNamedQuery(namedQueryName);
        int i = 1;
        for (Object p : params) {
            query.setParameter(i++, p);
        }
        return query.getResultList();
    }

    protected <E> List<E> findAll(final Class<E> type) {
        CriteriaQuery<E> query = getEntityManager().getCriteriaBuilder().createQuery(type);
        query.from(type);
        return getEntityManager().createQuery(query).getResultList();
    }

    @SuppressWarnings("unchecked")
    protected <E> E findUniqueByNamedQuery(final String namedQueryName) throws NoResultException {
        return (E) getEntityManager().createNamedQuery(namedQueryName).getSingleResult();
    }

    @SuppressWarnings("unchecked")
    protected <E> E findUniqueByNamedQuery(final String namedQueryName, final Object... params)
            throws NoResultException {
        Query query = getEntityManager().createNamedQuery(namedQueryName);
        int i = 1;
        for (Object p : params) {
            query.setParameter(i++, p);
        }

        return (E) query.getSingleResult();
    }

    protected <E> E getSingleResult(final CriteriaQuery<E> query) {
        return this.<E>getTypedSingleResult(query);
    }

    protected <E> E getTypedSingleResult(final CriteriaQuery<E> query) {
        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    protected <E> List<E> getResultList(final CriteriaQuery<E> query) {
        return getEntityManager().createQuery(query).getResultList();
    }

    protected <E> List<E> getResultList(final CriteriaQuery<E> query,
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


    public E find(final long id) {
        return getEntityManager().find(entityClass, id);
    }
    
    public QueryData<E> find(int start, int end, String sortField,
            QuerySortOrder order, Map<String, Object> filters) {

        QueryData<E> queryData = new QueryData<E>();

        CriteriaBuilder cb = getCriteriaBuilder();
        CriteriaQuery<E> c = cb.createQuery(entityClass);
        Root<E> account = c.from(entityClass);
        c.select(account);

        CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
        Root<E> accountCount = countQ.from(entityClass);
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

        TypedQuery<E> q = (TypedQuery<E>) createQuery(c);
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
}
