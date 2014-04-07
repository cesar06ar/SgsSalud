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
package edu.sgssalud.service.labClinico;

import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.farmacia.Receta_Medicamento;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import edu.sgssalud.util.Dates;
import edu.sgssalud.util.PersistenceUtil;
import edu.sgssalud.util.StringValidations;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author cesar
 */
public class PedidoExamenService extends PersistenceUtil<PedidoExamenLaboratorio> {

    public PedidoExamenService() {
        super(PedidoExamenLaboratorio.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public List<PedidoExamenLaboratorio> getPedidosExamenesLab() {
        List list = this.findAll(PedidoExamenLaboratorio.class);
        return list;
    }

    public PedidoExamenLaboratorio getPedidoPorId(final Long id) {
        return (PedidoExamenLaboratorio) findById(PedidoExamenLaboratorio.class, id);
    }

    public List<PedidoExamenLaboratorio> getPedidosExamenesLab(Date parametro) {
        TypedQuery<PedidoExamenLaboratorio> query = null;
        List<PedidoExamenLaboratorio> lista = new ArrayList<PedidoExamenLaboratorio>();
        //System.out.println("INGRESO A BUSCAR POR FECHA " + parametro);
        if (parametro != null) {
            query = em.createNamedQuery("PedidoExamenLab.buscarPorFecha", PedidoExamenLaboratorio.class);
            query.setParameter("fecha", parametro);
            lista = query.getResultList();
        }
//        if(lista.isEmpty()){
//            lista = this.getPedidosExamenesLab();
//        }
        //System.out.println("RESULTADO  POR FECHA " + query.getResultList());
        return lista;
    }
}
