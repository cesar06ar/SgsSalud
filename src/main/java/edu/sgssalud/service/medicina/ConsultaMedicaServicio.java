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

import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.ConsultaMedica_;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.model.medicina.SignosVitales_;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.ConsultaOdontologica_;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.util.Dates;
import edu.sgssalud.util.FechasUtil;
import edu.sgssalud.util.PersistenceUtil;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author cesar
 */
public class ConsultaMedicaServicio extends PersistenceUtil<ConsultaMedica> implements Serializable {

    private static final long serialVersionUID = 234L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ConsultaMedicaServicio.class);
    @Inject
    private BussinesEntityService bussinesEntityService;

    public ConsultaMedicaServicio() {
        super(ConsultaMedica.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
        bussinesEntityService.setEntityManager(em);
    }

    public List<ConsultaMedica> getConsultasMedicas(final int limit, final int offset) {
        return findAll(ConsultaMedica.class);
    }

    public List<ConsultaMedica> getConsulasMedicas() {
        List list = this.findAll(ConsultaMedica.class);
        return list;
    }

    public ConsultaMedica getConsultaMedicaPorId(final Long id) {
        return (ConsultaMedica) findById(ConsultaMedica.class, id);
    }

    public ConsultaMedica getConsultaMedicaPorFechaConsulta(final Date fechaConsulta) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ConsultaMedica> query = builder.createQuery(ConsultaMedica.class);
        Root<ConsultaMedica> entity = query.from(ConsultaMedica.class);
        query.where(builder.equal(entity.get(ConsultaMedica_.fechaConsulta), fechaConsulta));
        return getSingleResult(query);
    }

    public List<ConsultaMedica> getConsultaMedicaPorHistoriaClinica(HistoriaClinica historiaClinica) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ConsultaMedica> query = builder.createQuery(ConsultaMedica.class);
        Root<ConsultaMedica> entity = query.from(ConsultaMedica.class);
        query.where(builder.equal(entity.get(ConsultaMedica_.historiaClinica), historiaClinica));
        List<ConsultaMedica> temp = getResultList(query);
        Collections.sort(temp);
        return temp;
    }

    public ConsultaMedica getPorSignosVitales(final SignosVitales signosVitales) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ConsultaMedica> query = builder.createQuery(ConsultaMedica.class);
        Root<ConsultaMedica> entity = query.from(ConsultaMedica.class);
        query.where(builder.equal(entity.get(ConsultaMedica_.signosVitales), signosVitales));
        return getSingleResult(query);
    }

    public SignosVitales getSignosVitalesPorIdConsultaMedica(final ConsultaMedica consultaMed) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<SignosVitales> query = builder.createQuery(SignosVitales.class);
        Root<SignosVitales> entity = query.from(SignosVitales.class);
        query.where(builder.equal(entity.get(SignosVitales_.id), consultaMed.getSignosVitales().getId()));
        return getSingleResult(query);
    }

    public List<ConsultaMedica> buscarPorFechas(Date inicio, Date fin) {
        List<ConsultaMedica> lista = new ArrayList<ConsultaMedica>();
        String f1 = FechasUtil.getFecha1(inicio);
        String f2 = FechasUtil.getFecha1(fin);
        System.out.println("INGRESO A BUSCAR______" + f1 + "____por fechas  " + f2);
        TypedQuery<ConsultaMedica> query = null;
        if (inicio != null && fin != null) {
            System.out.println("INGRESO A BUSCAR__________por fechas");
            query = em.createNamedQuery("ConsultaMedica.buscarPorFechas", ConsultaMedica.class);
            query.setParameter("inicio", f1);
            query.setParameter("fin", f2);
            lista = query.getResultList();
            System.out.println("RESULTADO_________" + lista.toString());
            return lista;
        } else {

            return this.getConsulasMedicas();
        }
    }

    public List<ConsultaMedica> buscarPorRangoFechas(Date inicio, Date fin) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ConsultaMedica> query = builder.createQuery(ConsultaMedica.class);
        Root<ConsultaMedica> entity = query.from(ConsultaMedica.class);
        query.where(builder.between(entity.get(ConsultaMedica_.fechaConsulta), inicio, fin));        
        List<ConsultaMedica> temp = getResultList(query);
        Collections.sort(temp);
        //System.out.println("lista de consultas_____-- " + temp.toString());
        return temp;
    }

    public boolean borrarConsultaMedica(ConsultaMedica cm) {
        try {
            log.info(" --> Borrar Consulta : " + cm.getId());
            String borrarAttributos = "DELETE FROM bussinesEntityAttribute a WHERE a.bussinesentity_id = " + cm.getId();
            String borrarConsultaMedica = "DELETE FROM consultamedica cm where cm.id = :id";
            String borrarBussinesEntity = "DELETE FROM bussinesEntity b where b.id = :id";
            String borrarSignosVitales = "DELETE FROM signosvitales sv where sv.id = :id";
            long idsv = cm.getSignosVitales().getId();
            int deleted1 = em.createNativeQuery(borrarAttributos).executeUpdate();
            Query cons1 = em.createNativeQuery(borrarConsultaMedica);
            int deleted2 = cons1.setParameter("id", cm.getId()).executeUpdate();
            Query cons2 = em.createNativeQuery(borrarBussinesEntity);
            int deleted3 = cons2.setParameter("id", cm.getId()).executeUpdate();
            Query cons = em.createNativeQuery(borrarSignosVitales);
            int delete4 = cons.setParameter("id", idsv).executeUpdate();
            return (deleted1 == 1 && deleted2 == 1 && deleted3 == 1 && delete4 == 1);
        } catch (Exception e) {
            log.info(" --> error al borrar : " + e.getMessage());
            return false;
        }
    }
}
