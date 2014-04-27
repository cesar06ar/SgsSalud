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

import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.model.labClinico.Parametros;
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import edu.sgssalud.model.labClinico.ResultadoExamenLabClinico;
import edu.sgssalud.model.labClinico.ResultadoExamenLabClinico_;
import edu.sgssalud.model.labClinico.ResultadoParametro;
import edu.sgssalud.model.labClinico.ResultadoParametro_;
import edu.sgssalud.util.PersistenceUtil;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author cesar
 */
public class ResultadoExamenLCService extends PersistenceUtil<ResultadoExamenLabClinico>{
     
    public ResultadoExamenLCService(){
        super(ResultadoExamenLabClinico.class);
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    public List<ResultadoExamenLabClinico> getResultadosExamenLC() {
        List list = this.findAll(ResultadoExamenLabClinico.class);
        return list;
    }

    public ResultadoExamenLabClinico getResultadosExamenLCPorId(final Long id) {
        return (ResultadoExamenLabClinico) findById(ResultadoExamenLabClinico.class, id);
    }
    
    public List<ResultadoExamenLabClinico> getResultadosExamenPorPedidoExamen(PedidoExamenLaboratorio pedidoE) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ResultadoExamenLabClinico> query = builder.createQuery(ResultadoExamenLabClinico.class);
        Root<ResultadoExamenLabClinico> entity = query.from(ResultadoExamenLabClinico.class);
        query.where(builder.equal(entity.get(ResultadoExamenLabClinico_.pedidoExamenLab), pedidoE));
        return getResultList(query);        
    }
    
    public List<ResultadoExamenLabClinico> getResultadosELCPorPedidoExamen(ExamenLabClinico examenL) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ResultadoExamenLabClinico> query = builder.createQuery(ResultadoExamenLabClinico.class);
        Root<ResultadoExamenLabClinico> entity = query.from(ResultadoExamenLabClinico.class);
        query.where(builder.equal(entity.get(ResultadoExamenLabClinico_.ExamenLab), examenL));
        return getResultList(query);        
    }    
    
    public List<ResultadoParametro> getResultadoParametros(ResultadoExamenLabClinico relc) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ResultadoParametro> query = builder.createQuery(ResultadoParametro.class);
        Root<ResultadoParametro> entity = query.from(ResultadoParametro.class);
        query.where(builder.equal(entity.get(ResultadoParametro_.resultadoExamenLabClinico), relc));
        return getResultList(query);        
    }    
    
    public List<ResultadoParametro> getResultadoParametros(Parametros p) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ResultadoParametro> query = builder.createQuery(ResultadoParametro.class);
        Root<ResultadoParametro> entity = query.from(ResultadoParametro.class);
        query.where(builder.equal(entity.get(ResultadoParametro_.parametro), p));
        return getResultList(query);        
    }    
    
}
