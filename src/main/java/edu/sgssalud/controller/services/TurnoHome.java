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
package edu.sgssalud.controller.services;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.servicios.Turno;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.medicina.TurnoService;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.FechasUtil;
import edu.sgssalud.util.Strings;
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
import org.jboss.seam.security.Identity;
import org.jboss.seam.transaction.Transactional;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class TurnoHome extends BussinesEntityHome<Turno> implements Serializable {

    @Inject
    @Web
    private EntityManager em;
    @Inject
    private TurnoService turnoS;

    //@Inject
    //private Identity identity;
    @Inject
    private PacienteServicio pacienteS;
    private Long pacienteId;
    private String horaD;
    private Date fecha;
    private Date fechaBusc;
    private Paciente paciente;
    private List<Turno> listaTurnos = new ArrayList<Turno>();
    private List<String> listaHoras = new ArrayList<String>();

    public Long getTurnoId() {
        return (Long) getId();
    }

    public void setTurnoId(Long turnoId) {
        setId(turnoId);
    }

    @TransactionAttribute   //
    public Turno load() {
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
        pacienteS.setEntityManager(em);
        turnoS.setEntityManager(em);
        listaHoras = this.agregarHoras();
        listaTurnos = turnoS.getTurnos();
    }
    
    public void cargarTurnosPaciente(){
        listaTurnos = turnoS.getTurnosPorFecha(paciente);
    }
    
    @Override
    protected Turno createInstance() {
        //prellenado estable para cualquier clase 
        Date now = Calendar.getInstance().getTime();
        Turno turno = new Turno();
        turno.setFechaEmision(now);
        return turno;
    }

    @Override
    public Class<Turno> getEntityClass() {
        return Turno.class;
    }

    @TransactionAttribute
    public String guardar() {
        System.out.println("INGRESO GUARDAR_____-");
        String salida = "";
        Date now = Calendar.getInstance().getTime();
        int hora;
        int min;
        try {
            //System.out.println("INGRESO GUARDAR_____2-");
            if (getInstance().isPersistent()) {
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se actualizo Ficha Medica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
            } else {
                //System.out.println("INGRESO GUARDAR_____3-");
                hora = Strings.hora_minuto(horaD, 0);
                min = Strings.hora_minuto(horaD, 1);
                getInstance().setFechaCita(fecha);
                getInstance().setHora(FechasUtil.fijarHoraMinutoConFecha(getInstance().getFechaCita(), hora, min));
                getInstance().setEstado("Pendiente");
                getInstance().setPaciente(paciente);
                create(getInstance());
                save(getInstance());
                FacesMessage msg = new FacesMessage("Se creo nueva Ficha Medica: " + getInstance().getId() + " con éxito");
                FacesContext.getCurrentInstance().addMessage("", msg);
                //System.out.println("INGRESO GUARDAR_____EXITO");
                salida = "/pages/"+getBackView()+"?faces-redirect=true"
                        +"&pacienteId="+getPacienteId();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
            //System.out.println("INGRESO GUARDAR_____error");
        }
        return salida;
    }

    @Transactional
    public void borrarEntidad() {
//        log.info("sgssalud --> ingreso a eliminar: " + getInstance().getId());
        try {
            if (getInstance() == null) {
                throw new NullPointerException("Servicio is null");
            }
            if (getInstance().isPersistent()) {
                delete(getInstance());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  " + getInstance().getId(), ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "¡No existe una entidad para ser borrada!", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", e.toString()));
        }
        //return "/pages/serviciosMedicos/lista.xhtml?faces-redirect=true";
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
        if (pacienteId != null) {
            this.setPaciente(pacienteS.getPacientePorId(pacienteId));
        }
    }

    public String getHoraD() {
        return horaD;
    }

    public void setHoraD(String horaD) {
        this.horaD = horaD;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
        if (fecha != null) {
            this.horasDisponibles();
        }
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<Turno> getListaTurnos() {
        return listaTurnos;
        //return turnoS.getTurnos();
    }

    public void setListaTurnos(List<Turno> listaTurnos) {
        this.listaTurnos = listaTurnos;
    }

    public List<String> getListaHoras() {
        return listaHoras;
    }

    public void setListaHoras(List<String> listaHoras) {
        this.listaHoras = listaHoras;
    }

    public List<String> agregarHoras() {
        //listaHoras = new ArrayList<String>();
        List<String> horas = new ArrayList<String>();
        Date f; //= Calendar.getInstance().getTime();
        int hora = 20;
        int min = 00;
        for (int i = 0; i < 9; i++) {
            if (min == 0) {
                f = FechasUtil.fijarHoraMinuto(hora, min);
                horas.add(FechasUtil.getHoraActual(f));
                min = 30;
            } else {
                f = FechasUtil.fijarHoraMinuto(hora, min);
                horas.add(FechasUtil.getHoraActual(f));
                min = 00;
                hora++;
            }
        }
        hora = 3;
        min = 00;
        for (int i = 0; i < 7; i++) {
            if (min == 0) {
                f = FechasUtil.fijarHoraMinuto(hora, min);
                horas.add(FechasUtil.getHoraActual(f));
                min = 30;
            } else {
                f = FechasUtil.fijarHoraMinuto(hora, min);
                horas.add(FechasUtil.getHoraActual(f));
                min = 00;
                hora++;
            }
        }
        return horas;
    }

    public void horasDisponibles() {
        //System.out.println("LISTA DE TURNOS POR FECHA___" + listaTurnos.toString());
        listaTurnos = turnoS.getTurnosPorFecha(fecha);
        String hora = null;
        List<String> listaH = new ArrayList<String>();
        if (!listaTurnos.isEmpty()) {
            for (Turno t : listaTurnos) {
                System.out.println("TURNO______" + t.toString());
                hora = FechasUtil.getHoraActual(t.getHora());
                System.out.println("HORA ___" + hora);
                for (String h : listaHoras) {
                    if (h.equals(hora)) {
                        //listaHoras.remove(hora);
                        listaH.add(h);
                        System.out.println("ELIMINO HORA");
                    }
                }
            }
            listaHoras.removeAll(listaH);
        }
    }

    public void buscarPorFecha() {
        listaTurnos = turnoS.getTurnosPorFecha(fechaBusc);
    }

    public Date getFechaBusc() {
        return fechaBusc;
    }

    public void setFechaBusc(Date fechaBusc) {
        this.fechaBusc = fechaBusc;
        if (fechaBusc != null) {
            listaTurnos = turnoS.getTurnosPorFecha(fechaBusc);
        }
    }

}
