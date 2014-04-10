/*
 * Copyright 2013 cesar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 Z */
package edu.sgssalud.controller.odontologia;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.Diente;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.odontologia.Odontograma;
import edu.sgssalud.model.odontologia.Tratamiento;
import edu.sgssalud.model.servicios.Servicio;
import edu.sgssalud.service.ServiciosMedicosService;
import edu.sgssalud.service.generic.CrudService;
import edu.sgssalud.service.generic.QueryParameter;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.odontologia.ConsultaOdontologicaServicio;
import edu.sgssalud.util.UI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;
import org.primefaces.model.SelectableDataModel;

/**
 * @author cesar
 */
//@Named("tratamientoData")
//@ViewScoped
public class TratamientoDataModel extends ListDataModel<Tratamiento> implements SelectableDataModel<Tratamiento>, Serializable {

//    @Inject
//    @Web
//    private EntityManager em;

    public TratamientoDataModel() {
    }

    public TratamientoDataModel(List<Tratamiento> list) {
        super(list);
    }

    @Override
    public Object getRowKey(Tratamiento t) {
        return t.getId();
    }

    @Override
    public Tratamiento getRowData(String rowKey) {
        List<Tratamiento> lista = (List<Tratamiento>) getWrappedData();

        for (Tratamiento tratam : lista) {
            if (tratam.getId().equals(rowKey)) {
                return tratam;
            }
        }
        return null;
    }

}
