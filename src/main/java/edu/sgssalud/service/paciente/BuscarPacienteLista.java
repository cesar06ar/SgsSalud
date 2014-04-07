/*
 * Copyright 2014 cesar.
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
package edu.sgssalud.service.paciente;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.paciente.Paciente;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 *
 * @author cesar
 */
@Named
@RequestScoped
public class BuscarPacienteLista implements Serializable {

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private PacienteServicio pacienteServicio;
    private List<Paciente> resultList;

    private Object entidad;

    @PostConstruct
    public void init() {
        pacienteServicio.setEntityManager(em);
    }

    public List<Paciente> getResultList() {
        return resultList;
    }

    public void setResultList(List<Paciente> resultList) {
        this.resultList = resultList;
    }

    public Object getEntidad() {
        return entidad;
    }

    public void setEntidad(Object entidad) {
        this.entidad = entidad;
    }

}
