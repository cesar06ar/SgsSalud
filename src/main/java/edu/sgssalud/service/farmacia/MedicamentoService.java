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
package edu.sgssalud.service.farmacia;

import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.farmacia.Medicamento_;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.paciente.Paciente_;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.util.PersistenceUtil;
import edu.sgssalud.util.Strings;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import edu.sgssalud.util.*;

/**
 *
 * @author tania
 */
public class MedicamentoService extends PersistenceUtil<Medicamento> implements Serializable {

    private static final long serialVersionUID = 4L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(BussinesEntityService.class);

    public MedicamentoService() {
        super(Medicamento.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public Medicamento buscarMedicamentosProId(final Long id) {
        return (Medicamento) findById(Medicamento.class, id);
    }

    public List<Medicamento> obtenerMedicamentos(final int limite, final int offset) {
        return findAll(Medicamento.class);
    }

    public Medicamento buscarPorNombreMedicamento(final String nombreComercial) {
        log.info("buscar medicamento por nombre " + nombreComercial);
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Medicamento> query = builder.createQuery(Medicamento.class);
        Root<Medicamento> bussinesEntityType = query.from(Medicamento.class);
        query.where(builder.equal(bussinesEntityType.get(Medicamento_.nombreComercial), nombreComercial));
        return getSingleResult(query);
    }

    public Medicamento buscarPorNombreMed(final String nombre) throws NoResultException {
        TypedQuery<Medicamento> query = em.createQuery("SELECT m FROM Medicamento m WHERE m.nombreComercial = :nombre", Medicamento.class);
        query.setParameter("nombreComercial", nombre);
        Medicamento result = query.getSingleResult();
        return result;
    }

    public List<Medicamento> BuscarMedicamentosPorParametro(String parametro) {
        TypedQuery<Medicamento> query = null;
        if (StringValidations.isDecimal(parametro)) {
            query = em.createNamedQuery("Medicamento.buscarPorNumero", Medicamento.class);
            query.setParameter("clave", Integer.parseInt(parametro));
        } else {
            query = em.createNamedQuery("Medicamento.buscarPorParametro", Medicamento.class);
            query.setParameter("clave", parametro);
        }
        return query.getResultList();
    }

    public List<Medicamento> BuscarMedicamentosPorParametro1(String parametro) {
        TypedQuery<Medicamento> query = null;
        if (StringValidations.isDecimal(parametro)) {
            query = em.createNamedQuery("Medicamento.buscarPorNumero", Medicamento.class);
            query.setParameter("clave", Integer.parseInt(parametro));
        } else if (Dates.getFormatoFecha(parametro) != null) {
            query = em.createNamedQuery("Medicamento.buscarPorFecha", Medicamento.class);
            query.setParameter("clave", Dates.getFormatoFecha(parametro));
        } else {
            query = em.createNamedQuery("Medicamento.buscarPorParametro", Medicamento.class);
            query.setParameter("clave", parametro);
        }
        return query.getResultList();
    }

    public boolean esNombreDisponible(String nombre) {
        try {
            Medicamento med = buscarPorNombreMed(nombre);
            if (med != null) {
                return false;
            } else {
                return true;
            }
        } catch (NoResultException e) {
            return true;
        }

    }
}
