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

import edu.sgssalud.model.odontologia.Tratamiento;
import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
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
