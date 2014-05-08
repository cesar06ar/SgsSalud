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

import edu.sgssalud.cdi.Current;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.labClinico.ExamenLabClinico;
import edu.sgssalud.model.labClinico.Parametros;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.service.labClinico.ExamenLabService;
import edu.sgssalud.service.labClinico.ResultadoExamenLCService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class ExamenHome extends BussinesEntityHome<ExamenLabClinico> implements Serializable {

    @Inject
    @Web
    private EntityManager em;
    private Parametros parametro;
    @Inject
    private Event<Parametros> evento;
    @Inject
    private ExamenLabService examenLabService;
    @Inject
    private ResultadoExamenLCService resultadoService;
    private List<Parametros> parametros = new ArrayList<Parametros>();
    private List<String> categorias = new ArrayList<String>();

    public Long getExamenId() {
        return (Long) getId();
    }

    public void setExamenId(Long examenId) {
        setId(examenId);
        if (getInstance().isPersistent()) {
            parametros = examenLabService.getParametrosPorExamen(getInstance());
            this.listarCategorias();
        }
    }

    public Parametros getParametro() {
        return parametro;
    }

    public void setParametro(Parametros parametro) {
        this.parametro = parametro;
    }

    public List<Parametros> getParametros() {
        return parametros;
    }

    public void setParametros(List<Parametros> parametros) {
        this.parametros = parametros;
    }

    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }
    
    public void listarCategorias(){
        if(getInstance().getCategorias() != null){
            String c = getInstance().getCategorias();
            categorias = Arrays.asList(c.split(","));
        }
    }    

    @Produces
    @Current
    @Named("examen")
    @TransactionAttribute   //
    public ExamenLabClinico load() {
        if (isIdDefined()) {
            wire();
        }
        //log.info("sgssalud --> cargar instance " + getInstance());
        return getInstance();
    }

    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        examenLabService.setEntityManager(em);
        parametro = new Parametros();
        resultadoService.setEntityManager(em);
//        if(getInstance().isPersistent()){
//            this.listarCategorias();
//        }
    }

    @Override
    protected ExamenLabClinico createInstance() {
        //prellenado estable para cualquier clase 
        //BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(ConsultaMedica.class.getName());
        Date now = Calendar.getInstance().getTime();
        ExamenLabClinico examenLab = new ExamenLabClinico();
        examenLab.setCreatedOn(now);
        examenLab.setLastUpdate(now);
        examenLab.setActivationTime(now);
        examenLab.setParametros(new ArrayList<Parametros>());
        //fichaMedic.setResponsable(null);    //cambiar atributo a                 
        return examenLab;
    }

    @Override
    public Class<ExamenLabClinico> getEntityClass() {
        return ExamenLabClinico.class;
    }

    @TransactionAttribute
    public String guardar() {
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        String salida = null;
        try {
            if (getInstance().isPersistent()) {
                getInstance().setParametros(parametros);
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo el nombre del Examen: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                salida = "/pages/labClinico/listaExamenes.xhtml?faces-redirect=true";
            } else {
                //getInstance().setParametros(parametros);
                create(getInstance());
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se creo nueva Examen: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                salida = "/pages/labClinico/examenes.xhtml?faces-redirect=true"
                        + "&examenLabId=" + getInstance().getId();
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return salida;
    }

    @Transactional
    public String borrarParametro() {
        //log.info("sgssalud --> ingreso a eliminar: " + getInstance().getId());
        try {
            boolean ban = resultadoService.getResultadoParametros(parametro).isEmpty();
            if (parametro != null && ban) {
                delete(parametro);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  ", "El parametro seleccionado"));
                parametro = new Parametros();
            }else{
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "El parametro contiene datos asociados ", "no se puede borrar"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", e.toString()));
        }
        return "/pages/labClinico/examenes.xhtml?faces-redirect=true"
                        + "&examenLabId=" + getInstance().getId();
    }

    public List<ExamenLabClinico.Tipo> listaTiposExamen() {
        wire();
        List<ExamenLabClinico.Tipo> list = Arrays.asList(getInstance().getTipo().values());
        return list;
    }

    @TransactionAttribute
    public void agregarParametro() {
        System.out.println("agregar parametro");
        try {
            //getInstance().agregarParametro(parametro);
            parametro.setExamenLabClinico(getInstance());
            //save(getInstance());
            create(parametro);
            save(parametro);
            parametros = examenLabService.getParametrosPorExamen(getInstance());
            parametro = new Parametros();
            evento.fire(parametro);
            System.out.println("fin agregar parametro");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
