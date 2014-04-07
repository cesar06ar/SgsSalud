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

import edu.sgssalud.model.farmacia.Receta_Medicamento;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.util.PersistenceUtil;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author cesar
 */
public class ExamenLabService extends PersistenceUtil<ExamenLabClinico>{
     
    public ExamenLabService(){
        super(ExamenLabClinico.class);
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    public List<ExamenLabClinico> getExamenesLab() {
        List list = this.findAll(ExamenLabClinico.class);
        return list;
    }

    public ExamenLabClinico getExamenPorId(final Long id) {
        return (ExamenLabClinico) findById(ExamenLabClinico.class, id);
    }
}