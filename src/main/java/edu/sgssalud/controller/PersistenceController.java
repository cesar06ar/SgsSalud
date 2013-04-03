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
import javax.persistence.EntityManager;

/**
 *
 * @author jlgranda
 */
public abstract class PersistenceController<T> extends Controller implements Serializable {

    private static final long serialVersionUID = -7170396044131690408L;
    EntityManager entityManager;
    private transient T persistenceContext;

    public T getPersistenceContext() {
        if (persistenceContext == null) {
            persistenceContext = (T) getComponentInstance(getPersistenceContextName());
        }
        return persistenceContext;
    }

    public void setPersistenceContext(T persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    protected abstract String getPersistenceContextName();

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
