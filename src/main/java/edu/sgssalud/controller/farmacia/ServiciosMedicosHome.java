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
package edu.sgssalud.controller.farmacia;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.servicios.Servicio;
import edu.sgssalud.service.ServiciosMedicosService;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
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
import org.jboss.seam.transaction.Transactional;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class ServiciosMedicosHome extends BussinesEntityHome<Servicio> implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ServiciosMedicosHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private ServiciosMedicosService servicioMedicoS;
    
    private String rutaImg;

    public Long getServicioId() {
        return (Long) getId();
    }

    public void setServicioId(Long ServicioId) {
        setId(ServicioId);
    }

    public List<String> getRutaImagenes() {
        List<String> rutas = new ArrayList<String>();
        String ruta = "/resources/odontograma/";
        File dir = new File("/home/tania/NetBeansProjects/SgsSalud/src/main/webapp/resources/odontograma/");
        String[] imagenes = dir.list();
        if (imagenes != null) {
            for (String s : imagenes) {
                rutas.add(ruta.concat(s));
            }
        }
        return rutas;
    }

    public String getRutaImg() {
        return rutaImg;
    }

    public void setRutaImg(String rutaImg) {        
        this.rutaImg = rutaImg;
        log.info("ingreso a fijar "+rutaImg);
    }    

    @TransactionAttribute   //
    public Servicio load() {
        if (isIdDefined()) {
            wire();
        }
        log.info("sgssalud --> cargar instance " + getInstance());
        return getInstance();
    }

    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        bussinesEntityService.setEntityManager(em);
        servicioMedicoS.setEntityManager(em);
    }

    @Override
    protected Servicio createInstance() {
        //prellenado estable para cualquier clase         
        Date now = Calendar.getInstance().getTime();
        Servicio servicioMedico = new Servicio();
        servicioMedico.setCategoria("ODONTOLOGIA");
        return servicioMedico;
    }

    @Override
    public Class<Servicio> getEntityClass() {
        return Servicio.class;
    }

    @TransactionAttribute
    public String guardar() {
        log.info("ingreso a guardar");
        Date now = Calendar.getInstance().getTime();
        try {
            if (getInstance().isPersistent()) {
                log.info("actualiza 1");
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo el Servicio Médico: " + getInstance().getNombre() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                log.info("actualiza 2");
            } else {
                log.info("crear 1");
                create(getInstance());
                save(getInstance());
                delete(now);
                log.info("crear 2");
                FacesMessage msg = new FacesMessage("Se creo nuevo Servicio Médico: " + getInstance().getNombre() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getNombre());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return "/pages/admin/serviciosMedicos/lista.xhtml?faces-redirect=true";
    }

    public String nombreImg(String ruta) {
        String resultado = "";
        String[] c = ruta.split("/");
        for (String s : c) {
            if (s.contains(".png") || s.contains(".jpg") || s.contains(".gif")) {
                resultado = s;
            }
        }
        return resultado;
    }
}
