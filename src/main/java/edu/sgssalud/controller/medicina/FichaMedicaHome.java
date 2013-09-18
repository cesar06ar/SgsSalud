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
package edu.sgssalud.controller.medicina;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.Dates;
import java.io.Serializable;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class FichaMedicaHome extends BussinesEntityHome<FichaMedica> implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(FichaMedicaHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private FichaMedicaServicio fms;
    @Inject
    private PacienteServicio ps;
    private Long pacienteId;
    private Paciente paciente;
    private String parametroBusqueda;

    /*<== Métodos get y set para obtener el Id de la clase*/
    public Long getFichaMedicaId() {
        return (Long) getId();
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        setId(fichaMedicaId);
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
        this.setPaciente(ps.getPacientePorId(pacienteId));
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getParametroBusqueda() {
        return parametroBusqueda;
    }

    public void setParametroBusqueda(String parametroBusqueda) {
        this.parametroBusqueda = parametroBusqueda;
    }
    /*<==....*/

    @TransactionAttribute   //
    public FichaMedica load() {
        if (isIdDefined()) {
            wire();
        }
        log.info("sgssalud --> cargar instance " + getInstance());
        return getInstance();
    }

    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        fms.setEntityManager(em);
        ps.setEntityManager(em);
        if (pacienteId != null) {
            paciente = new Paciente();
        }

        //bussinesEntityService.setEntityManager(em);        
    }

    @Override
    protected FichaMedica createInstance() {
        //prellenado estable para cualquier clase 
        //BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(FichaMedica.class.getName());
        Date now = Calendar.getInstance().getTime();
        FichaMedica fichaMedic = new FichaMedica();
        fichaMedic.setCreatedOn(now);
        fichaMedic.setLastUpdate(now);
        fichaMedic.setActivationTime(now);
        //fichaMedic.setExpirationTime(Dates.addDays(now, 364));
        //fichaMedic.setResponsable(null);    //cambiar atributo a 
        //fichaMedic.setType(_type);
        //fichaMedic.buildAttributes(bussinesEntityService);  //
        return fichaMedic;
    }

    @Override
    public Class<FichaMedica> getEntityClass() {
        return FichaMedica.class;
    }

    @TransactionAttribute
    public String guardar() {
        return null;
    }

    public void buscarPorParametro() {
        getInstance().findBussinesEntityAttribute("");
    }
}
