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
package edu.sgssalud.controller.odontologia;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.model.medicina.SignosVitales;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.Diente;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.odontologia.Odontograma;
import edu.sgssalud.model.odontologia.Tratamiento;
import edu.sgssalud.model.servicios.Servicio;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.ServiciosMedicosService;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.odontologia.FichaOdontologicaServicio;
import edu.sgssalud.service.odontologia.consultaOdontologicaServicio;
import java.io.Serializable;
import java.util.*;
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
@Named("consultaOdontHome")
@ViewScoped
public class ConsultaOdontologicaHome extends BussinesEntityHome<ConsultaOdontologica> implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(ConsultaOdontologicaHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private consultaOdontologicaServicio consultaOdontServicio;
    @Inject
    private FichaMedicaServicio fichaMedicaServicio;
    @Inject
    private FichaOdontologicaServicio fichaOdontServicio;
    @Inject
    private Identity identity;
    @Inject
    private ProfileService profileS;
    @Inject
    private ServiciosMedicosService serviciosMedicosS;
    private Diente diente;
    private FichaOdontologica fichaOdontolog;
    private Servicio servicio;
    private SignosVitales signosVitales;
    private Tratamiento tratamiento;
    private Long fichaMedicaId;
    private List<Servicio> listaServicios = new ArrayList<Servicio>();
    private List<Diente> listaDientes = new ArrayList<Diente>();
    private List<Diente> listaDientesC1 = new ArrayList<Diente>();
    private List<Diente> listaDientesC2 = new ArrayList<Diente>();
    private List<Diente> listaDientesC3 = new ArrayList<Diente>();
    private List<Diente> listaDientesC4 = new ArrayList<Diente>();

    public Long getConsultaOdontologicaId() {
        return (Long) getId();
    }

    public void setConsultaOdontologicaId(Long consultaOdontId) {
        setId(consultaOdontId);
    }

    public FichaOdontologica getFichaOdontolog() {
        return fichaOdontolog;
    }

    public void setFichaOdontolog(FichaOdontologica fichaOdontolog) {
        this.fichaOdontolog = fichaOdontolog;
    }

    public Long getFichaMedicaId() {
        return fichaMedicaId;
    }

    public void setFichaMedicaId(Long fichaMedicaId) {
        this.fichaMedicaId = fichaMedicaId;
        if (fichaMedicaId != null) {
            this.setFichaOdontolog(fichaOdontServicio.getFichaOdontologicaPorFichaMedica(
                    fichaMedicaServicio.getFichaMedicaPorId(fichaMedicaId)));
        }
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public SignosVitales getSignosVitales() {
        return signosVitales;
    }

    public void setSignosVitales(SignosVitales signosVitales) {
        this.signosVitales = signosVitales;
    }

    public Diente getDiente() {
        return diente;
    }

    public void setDiente(Diente diente) {
        log.info("valor diente: " + diente.toString());
        this.diente = diente;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public List<Servicio> getListaServicios() {
        return serviciosMedicosS.todosServicios("ODONTOLOGIA");
    }

    public void setListaServicios(List<Servicio> listaServicios) {
        this.listaServicios = listaServicios;
    }

    public List<Diente> getListaDientes() {
        this.IniciarDientes();
        return listaDientes;
    }

    public void setListaDientes(List<Diente> listaDientes) {
        this.listaDientes = listaDientes;
    }

    public List<Diente> getListaDientesC1() {
        this.IniciarDientes();
        Collections.sort(listaDientesC1);
        return listaDientesC1;
    }

    public void setListaDientesC1(List<Diente> listaDientesC1) {
        this.listaDientesC1 = listaDientesC1;
    }

    public List<Diente> getListaDientesC2() {
        return listaDientesC2;
    }

    public void setListaDientesC2(List<Diente> listaDientesC2) {
        this.listaDientesC2 = listaDientesC2;
    }

    public List<Diente> getListaDientesC3() {
        return listaDientesC3;
    }

    public void setListaDientesC3(List<Diente> listaDientesC3) {
        this.listaDientesC3 = listaDientesC3;
    }

    public List<Diente> getListaDientesC4() {
        Collections.sort(listaDientesC4);
        return listaDientesC4;
    }

    public void setListaDientesC4(List<Diente> listaDientesC4) {
        this.listaDientesC4 = listaDientesC4;
    }

    @TransactionAttribute
    public ConsultaOdontologica load() {
        if (isIdDefined()) {
            wire();
        }
        log.info("sgssalud --> cargar instance " + getInstance());
         if (getInstance().isPersistent()) {
            if (getInstance().getResponsable() == null) {
                getInstance().setResponsable(profileS.getProfileByIdentityKey(identity.getUser().getKey()));
            }
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
        bussinesEntityService.setEntityManager(em);
        consultaOdontServicio.setEntityManager(em);
        fichaMedicaServicio.setEntityManager(em);
        fichaOdontServicio.setEntityManager(em);
        profileS.setEntityManager(em);
        serviciosMedicosS.setEntityManager(em);
        if (getInstance().isPersistent()) {            
            this.IniciarDientes();
            Odontograma o = new Odontograma();
            o.setDientes(listaDientes);
            log.info("Init Dientes  " + fichaOdontolog.toString());
            fichaOdontolog.setOdontograma(o);
            fichaOdontolog.setOdontogramaInicial(o);
            if (!tratamiento.isPersistent()) {
                this.setTratamiento(new Tratamiento());
            }
        }
        //log.info("Odont Inicial " + fichaOdontolog.getOdontogramaInicial());
        //log.info("Odont  " + fichaOdontolog.getOdontograma());

        /*if (!fichaOdontolog.getOdontograma().isPersistent()) {
         fichaOdontolog.setOdontograma(o);
         }
         if (!fichaOdontolog.getOdontogramaInicial().isPersistent()) {
         fichaOdontolog.setOdontogramaInicial(o);
         }*/
    }

    @Override
    protected ConsultaOdontologica createInstance() {
        //prellenado estable para cualquier clase 
        BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(ConsultaOdontologica.class.getName());
        Date now = Calendar.getInstance().getTime();
        ConsultaOdontologica consultaOdont = new ConsultaOdontologica();
        consultaOdont.setCreatedOn(now);
        consultaOdont.setLastUpdate(now);
        consultaOdont.setActivationTime(now);
        consultaOdont.setSignosVitales(new SignosVitales());
        consultaOdont.setType(_type);
        consultaOdont.buildAttributes(bussinesEntityService);

//        fOdontologica.setCreatedOn(now);
//        fOdontologica.setActivationTime(now);
//        fOdontologica.setLastUpdate(now);
        return consultaOdont;
    }

    @Override
    public Class<ConsultaOdontologica> getEntityClass() {
        return ConsultaOdontologica.class;
    }

    @TransactionAttribute
    public String guardar() {
        String salida = null;
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        try {
            if (getInstance().isPersistent()) {
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo Ficha Medica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                create(getInstance());
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se creo nueva Ficha Medica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
        }
        return salida;
    }

    @TransactionAttribute
    public void guardarTratamiento() {
        log.info("valor diente: " + diente);

    }

    public void IniciarDientes() {
        listaDientesC1 = new ArrayList<Diente>();
        listaDientesC2 = new ArrayList<Diente>();
        listaDientesC3 = new ArrayList<Diente>();
        listaDientesC4 = new ArrayList<Diente>();
        String ruta = "/resources/odontograma/diente.png";
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 8; j++) {
                String p = "" + i + j;
                if (i == 1) {
                    if (j == 1 || j == 2) {
                        listaDientesC1.add(new Diente("Incisivo", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 3) {
                        listaDientesC1.add(new Diente("Canino", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 4 || j == 5) {
                        listaDientesC1.add(new Diente("Premolar", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 6 || j == 7 || j == 8) {
                        listaDientesC1.add(new Diente("Molar", Integer.parseInt(p), i, ruta));
                    }
                } else if (i == 2) {
                    if (j == 1 || j == 2) {
                        listaDientesC2.add(new Diente("Incisivo", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 3) {
                        listaDientesC2.add(new Diente("Canino", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 4 || j == 5) {
                        listaDientesC2.add(new Diente("Premolar", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 6 || j == 7 || j == 8) {
                        listaDientesC2.add(new Diente("Molar", Integer.parseInt(p), i, ruta));
                    }
                } else if (i == 3) {
                    if (j == 1 || j == 2) {
                        listaDientesC3.add(new Diente("Incisivo", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 3) {
                        listaDientesC3.add(new Diente("Canino", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 4 || j == 5) {
                        listaDientesC3.add(new Diente("Premolar", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 6 || j == 7 || j == 8) {
                        listaDientesC3.add(new Diente("Molar", Integer.parseInt(p), i, ruta));
                    }
                } else if (i == 4) {
                    if (j == 1 || j == 2) {
                        listaDientesC4.add(new Diente("Incisivo", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 3) {
                        listaDientesC4.add(new Diente("Canino", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 4 || j == 5) {
                        listaDientesC4.add(new Diente("Premolar", Integer.parseInt(p), i, ruta));
                    }
                    if (j == 6 || j == 7 || j == 8) {
                        listaDientesC4.add(new Diente("Molar", Integer.parseInt(p), i, ruta));
                    }
                }
            }
        }
    }
}
