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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import edu.sgssalud.model.BussinesEntity;
import edu.sgssalud.model.BussinesEntityAttribute;
import edu.sgssalud.model.Group;
import edu.sgssalud.model.Property;
import edu.sgssalud.model.Structure;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.service.BussinesEntityService;
import org.jboss.seam.transaction.Transactional;

/**
 *
 * @author jlgranda
 */
@TransactionAttribute
public abstract class BussinesEntityHome<E> extends Home<EntityManager, E> implements Serializable {

    private static final long serialVersionUID = -1L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(BussinesEntityHome.class);
    @Inject
    protected BussinesEntityService bussinesEntityService;
    protected BussinesEntity bussinesEntity;
    private Property property;
    private String backView;
    private String previous;
    private boolean editionEnabled = true;

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getBackView() {
        return backView;
    }

    public void setBackView(String backView) {
        this.backView = backView;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    } 
    
    
    public boolean isEditionEnabled() {
        return editionEnabled;
    }

    public void setEditionEnabled(boolean editionEnabled) {
        this.editionEnabled = editionEnabled;
    }

    @Override
    public void create() {
        super.create();
        if (getEntityManager() == null) {
            throw new IllegalStateException("entityManager is null");
        }
    }

    public boolean isManaged() {
        return getInstance() != null
                && getEntityManager().contains(getInstance());
    }

    @Transactional
    public String update() {
        super.save(getInstance());
        getEntityManager().flush();
        updatedMessage();
        return "updated";
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String persist() {
        super.create(getInstance());
        createdMessage();
        return "persisted";
    }

    @Transactional
    public String remove() {
        super.delete(getInstance());
        getEntityManager().flush();
        deletedMessage();
        return "removed";
    }

    @Transactional
    @Override
    public E find() {
        if (getEntityManager().isOpen()) {
            E result = loadInstance();
            if (result == null) {
                result = handleNotFound();
            }
            return result;
        } else {
            return null;
        }
    }

    protected E loadInstance() {
        return getEntityManager().find(getEntityClass(), getId());
    }

    @Override
    protected String getPersistenceContextName() {
        return "entityManager";
    }

    @Override
    protected String getEntityName() {
        //return PersistenceProvider.instance().getName(getInstance(), getEntityManager());
        return "empty";
    }

    public BussinesEntity getBussinesEntity() {
        return bussinesEntity;
    }

    public void setBussinesEntity(BussinesEntity bussinesEntity) {
        this.bussinesEntity = bussinesEntity;
        log.info("eqaula --> BussinessEntity set to " + bussinesEntity);
    }

    //protected abstract void buildAttributes(E entity);
    @Deprecated
    protected BussinesEntityAttribute buildAtribute(Property p) {
        BussinesEntityAttribute attribute = new BussinesEntityAttribute();
        attribute.setName(p.getName());
        attribute.setType(p.getType());
        attribute.setValue((Serializable) p.getValue());
        attribute.setProperty(p); //Relaciona los objetos de forma directa
        return attribute;
    }

    /**
     * Return a List of BussinesEntityAttribute for structure names
     *
     * @param names
     * @return a List of BussinesEntityAttribute for structure names
     */
    public List<BussinesEntityAttribute> findAttributes(final String... names) {

        List<BussinesEntityAttribute> _buffer = new ArrayList<BussinesEntityAttribute>();
        for (String name : names) {
            if (attrs.containsKey(name)) {
                _buffer.addAll(attrs.get(name));
                log.info("eqaula --> attributes from cache map");
            } else {
                _buffer.addAll(((BussinesEntity) getInstance()).getBussinessEntityAttributes(name));
                putAttributesIntoMap(name, _buffer);
                log.info("eqaula --> add into chace " + name + ", " + _buffer);
            }
        }
        return _buffer;
    }
    protected Map<String, List<BussinesEntityAttribute>> attrs = new HashMap<String, List<BussinesEntityAttribute>>();

    public void putAttributesIntoMap(String key, List<BussinesEntityAttribute> _attrs) {
        attrs.put(key, _attrs);
    }

    /**
     * Return list of BussinesEntityAttribute for BussinesEntityType names lists
     *
     * @param names list of names for BussinessEntityType
     * @return list of BussinesEntityAttribute for BussinesEntityType names
     * lists
     */
    public List<BussinesEntityAttribute> findBussinesEntityAttribute(final String names) {
        log.info("eqaula --> findBussinesEntityAttribute  " + names + " into " + getInstance());
        if (getInstance() == null) {
            return new ArrayList<BussinesEntityAttribute>();
        }

        List<BussinesEntityAttribute> temp = findAttributes(names.split(","));

        log.info("eqaula --> attributes (" + temp.size() + ")");
        return temp;
    }
    
}
