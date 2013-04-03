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
package edu.sgssalud.util;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import edu.sgssalud.cdi.Web;

/**
 *
 * @author jlgranda
 */
@Named
@RequestScoped
public class AdminTools implements Serializable {
    private static final long serialVersionUID = -1981713367535247946L;
    
    @Inject
    @Web
    private EntityManager em;
    
    private String query;
    
    private String result;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    
    
    public void executeQuery(){
        Query query = em.createQuery(getQuery());
        result = query.getResultList().isEmpty() ? "" : query.getResultList().toString();
    }
    
}
