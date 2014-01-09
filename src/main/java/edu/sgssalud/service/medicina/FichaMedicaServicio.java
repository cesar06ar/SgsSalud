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
import edu.sgssalud.model.medicina.FichaMedica_;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.Dates;
import edu.sgssalud.util.PersistenceUtil;
import edu.sgssalud.util.StringValidations;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author cesar
 */
public class FichaMedicaServicio extends PersistenceUtil<FichaMedica> implements Serializable {

    private static final long serialVersionUID = 234L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(FichaMedicaServicio.class);
    @Inject
    private BussinesEntityService bussinesEntityService;
    @Inject
    private PacienteServicio ps;

    public FichaMedicaServicio() {
        super(FichaMedica.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
        bussinesEntityService.setEntityManager(em);
        ps.setEntityManager(em);
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

    public FichaMedica getFichaMedicaPorNumeroFicha(final Long numeroFicha) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<FichaMedica> query = builder.createQuery(FichaMedica.class);
        Root<FichaMedica> entity = query.from(FichaMedica.class);
        query.where(builder.equal(entity.get(FichaMedica_.numeroFicha), numeroFicha));
        return getSingleResult(query);
    }

    public FichaMedica getFichaMedicaPorPaciente(final Paciente p) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<FichaMedica> query = builder.createQuery(FichaMedica.class);
        Root<FichaMedica> entity = query.from(FichaMedica.class);
        query.where(builder.equal(entity.get(FichaMedica_.paciente), p));
        return getSingleResult(query);
    }

    public List<FichaMedica> BuscarFichaMedicaPorParametro(String parametro) {
        StringValidations sv = new StringValidations();
        List<FichaMedica> consulFichas = new ArrayList<FichaMedica>();
        TypedQuery<FichaMedica> query = null;
        if (StringValidations.isDecimal(parametro)) {
            if (sv.validadorCedula(parametro) == true) {
                consulFichas.add(getFichaMedicaPorPaciente(ps.buscarPorCedula(parametro)));
            } else {
                query = em.createNamedQuery("FichaMedica.buscarPorNumero", FichaMedica.class);
                query.setParameter("clave", Integer.parseInt(parametro));
                consulFichas = query.getResultList();
            }
        } else if (Dates.getFormatoFecha(parametro) != null) {
            query = em.createNamedQuery("FichaMedica.buscarPorFecha", FichaMedica.class);
            query.setParameter("clave", Dates.getFormatoFecha(parametro));
            consulFichas = query.getResultList();
        } 
        return consulFichas;
        
    }
}
