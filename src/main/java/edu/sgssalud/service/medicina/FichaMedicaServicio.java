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
package edu.sgssalud.service.medicina;

import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.util.PersistenceUtil;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author cesar
 */
public class FichaMedicaServicio extends PersistenceUtil<FichaMedica> implements Serializable{

    private static final long serialVersionUID = 234L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(FichaMedicaServicio.class);
    
    @Inject
    private BussinesEntityService bussinesEntityService;
    
    public FichaMedicaServicio(){
        super(FichaMedica.class);
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
        bussinesEntityService.setEntityManager(em);
    }
    
    public long getFichaMedicaCount() {
        return count(FichaMedica.class);
    }
    
    public List<FichaMedica> getFichasMedicas(final int limit, final int offset) {
        return findAll(FichaMedica.class);
    }    

    public List<FichaMedica> getFichasMedicas() {
        List list = this.findAll(FichaMedica.class);
        return list;
    }
    public FichaMedica getFichaMedicaPorId(final Long id) {
        return (FichaMedica) findById(FichaMedica.class, id);
    }
}
