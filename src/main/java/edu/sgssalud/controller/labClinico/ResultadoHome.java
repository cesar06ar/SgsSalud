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
package edu.sgssalud.controller.labClinico;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import edu.sgssalud.model.labClinico.ResultadoExamenLabClinico;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.labClinico.ResultadoExamenLCService;
import edu.sgssalud.service.paciente.PacienteServicio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.security.Identity;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class ResultadoHome extends BussinesEntityHome<ResultadoExamenLabClinico> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private PacienteServicio pacienteService;
    @Inject
    private ResultadoExamenLCService resultadoService;
    @Inject
    private ProfileService profileServicio;
    @Inject
    private Identity identity;
    private Long pedidoExamenId;
    private PedidoExamenLaboratorio pedidoExam;
    private Paciente paciente;
    private List<String> categorias = new ArrayList<String>();

    /*Métodos get y set para obtener el Id de la clase*/
    public Long getResultadoId() {
        return (Long) getId();
    }

    public void setResultadoId(Long resultadoId) {
        //this.recetaId = recetaId;
        setId(resultadoId);
        ResultadoExamenLabClinico r = resultadoService.find(resultadoId);
        if (r != null) {
            setInstance(r);
            pedidoExam = getInstance().getPedidoExamenLab();
            Date ahora = Calendar.getInstance().getTime();
            getInstance().setFechaRealizacion(ahora);
            this.listarCategorias();
        }
    }

    public Long getPedidoExamenId() {
        return pedidoExamenId;
    }

    public void setPedidoExamenId(Long pedidoExamenId) {
        this.pedidoExamenId = pedidoExamenId;
    }

    public PedidoExamenLaboratorio getPedidoExam() {
        return pedidoExam;
    }

    public void setPedidoExam(PedidoExamenLaboratorio pedidoExam) {
        this.pedidoExam = pedidoExam;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    public void listarCategorias() {
        if (getInstance().getExamenLab().getCategorias() != null) {
            String c = getInstance().getExamenLab().getCategorias();
            categorias = Arrays.asList(c.split(","));
        }
    }

    @TransactionAttribute
    public ResultadoExamenLabClinico load() {
        if (isIdDefined()) {
            wire();
        }
        return getInstance();
    }

    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        //bussinesEntityService.setEntityManager(em);        
        resultadoService.setEntityManager(em);
        pacienteService.setEntityManager(em);
        profileServicio.setEntityManager(em);
        if (getInstance().isPersistent()) {
            this.listarCategorias();
        }

    }

    @Override
    protected ResultadoExamenLabClinico createInstance() {
        //prellenado estable para cualquier clase 
        //BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(ConsultaMedica.class.getName());        
        ResultadoExamenLabClinico resultado = new ResultadoExamenLabClinico();
        return resultado;
    }

    @Override
    public Class<ResultadoExamenLabClinico> getEntityClass() {
        return ResultadoExamenLabClinico.class;
    }

    @TransactionAttribute
    public String guardar() {
        //Date now = Calendar.getInstance().getTime();
        System.out.println("INGRESo a Guardar _________");
        try {
            if (getInstance().isPersistent()) {
                System.out.println("Guardo Con exito_________1");
                PedidoExamenLaboratorio p = getInstance().getPedidoExamenLab();
                p.setEstado("Realizado");
                save(p);
                update();
                getInstance().setResponsableEntrega(profileServicio.getProfileByIdentityKey(identity.getUser().getKey()));
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo Pedido : " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                System.out.println("Guardo Con exito_________--");
                create(getInstance());
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se agrego nuevo Pedido de Examenes: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return "/pages/labClinico/resultadosExamen.xhtml?faces-redirect=true"
                + "&pedidoExamenId=" + getInstance().getPedidoExamenLab().getId();

    }

}
