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
package edu.sgssalud.faces.converter;


import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.medicina.EnfermedadCIE10;
import edu.sgssalud.service.medicina.EnfermedadesCie10Servicio;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 *
 * @author cesar
 */
@RequestScoped
@FacesConverter("enfermedadescie10Converter")
public class Enfermedadescie10Converter implements Converter, Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private EnfermedadesCie10Servicio enfServicio;

    @PostConstruct
    public void setup() {
        //System.out.println("UserConverter started up");
    }

    private Long getKey(String value) {        
        int start = value.indexOf("id=");
        int end = value.indexOf(",") == -1 ? value.indexOf("]") : value.indexOf(",");
        return Long.valueOf(value.substring(start + 3, end).trim());
    }

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && !value.isEmpty() && enfServicio != null) {
            try {
                enfServicio.setEntityManager(em);
                return enfServicio.getEnfermedadCIE10PorId(getKey(value));
            } catch (NoResultException e) {
                return new EnfermedadCIE10();
            }
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object value) {
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }  
    }
}
