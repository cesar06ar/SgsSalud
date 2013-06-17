/*
 * Copyright 2013 tania.
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
package edu.sgssalud.service;

import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.util.PersistenceUtil;
import java.io.Serializable;
import javax.persistence.EntityManager;

/**
 *
 * @author tania
 */
public class MedicamentoService extends PersistenceUtil<Medicamento> implements Serializable{
    
    private static final long serialVersionUID = 6569835981443699931L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(BussinesEntityService.class);

    public MedicamentoService() {
        super(Medicamento.class);
    }

   
    @Override
    public void setEntityManager(EntityManager em) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
