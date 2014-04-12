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
package edu.sgssalud.controller.medicina;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.odontologia.Tratamiento;
import edu.sgssalud.service.medicina.ConsultaMedicaServicio;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.HistoriaClinicaServicio;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.odontologia.TratamientoServicio;
import edu.sgssalud.service.paciente.PacienteServicio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.security.Identity;

/**
 *
 * @author cesar
 */
@Named
@RequestScoped
public class consultasPacienteHome implements Serializable {

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private FichaMedicaServicio fichaMedS;
    @Inject
    private HistoriaClinicaServicio hcs;
    @Inject
    private FichaOdontologicaServicio fos;
    @Inject
    private TratamientoServicio tratamientoServicio;
    @Inject
    private PacienteServicio pacienteS;

    private Long pacienteId;
    private FichaMedica fichaMedica;
    private HistoriaClinica hc;
    private FichaOdontologica fo;
    private List<Tratamiento> tratamientos = new ArrayList<Tratamiento>();

    @PostConstruct
    public void init() {
        pacienteS.setEntityManager(em);
        fichaMedS.setEntityManager(em);
        hcs.setEntityManager(em);
        fos.setEntityManager(em);
        tratamientoServicio.setEntityManager(em);        
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
        if (pacienteId != null) {
            this.setFichaMedica(fichaMedS.getFichaMedicaPorPaciente(pacienteS.getPacientePorId(pacienteId)));
            this.setHc(hcs.buscarPorFichaMedica(fichaMedica));
            this.setFo(fos.getFichaOdontologicaPorFichaMedica(fichaMedica));
        }
    }

    public FichaMedica getFichaMedica() {
        return fichaMedica;
    }

    public void setFichaMedica(FichaMedica fichaMedica) {
        this.fichaMedica = fichaMedica;
    }

    public HistoriaClinica getHc() {
        return hc;
    }

    public void setHc(HistoriaClinica hc) {
        this.hc = hc;
    }

    public FichaOdontologica getFo() {
        return fo;
    }

    public void setFo(FichaOdontologica fo) {
        this.fo = fo;
    }

    public List<Tratamiento> getTratamientos() {
        return tratamientos;
    }

    public void setTratamientos(List<Tratamiento> tratamientos) {
        this.tratamientos = tratamientos;
    }   
    
    public List<Tratamiento> buscarPorConsulta(ConsultaOdontologica co){
        return tratamientoServicio.buscarPorConsultaOdontologica(co);
    }
}
