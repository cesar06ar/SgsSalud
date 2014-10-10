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
import edu.sgssalud.model.medicina.FichaMedica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.servicios.Turno;
import edu.sgssalud.security.authorization.SecurityRules;
import edu.sgssalud.service.medicina.FichaMedicaServicio;
import edu.sgssalud.service.medicina.TurnoService;
import edu.sgssalud.service.paciente.PacienteServicio;
import edu.sgssalud.util.FechasUtil;
import edu.sgssalud.util.Strings;
import edu.sgssalud.web.service.WebServiceSGAClientConnection;
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

    @Inject
    private Identity identity;
    @Inject
    private PacienteServicio pacienteS;
    @Inject
    private FichaMedicaServicio fms;
    private Long pacienteId;
    private String horaD;
    private Date fecha;
    private Date fechaBusc;// = new Date();fechaBuscFin
    private Date fechaBuscFin;
    private Paciente paciente;
    private List<Turno> listaTurnos = new ArrayList<Turno>();
    private List<String> listaHoras = new ArrayList<String>();
    private String criterioBusqueda;
    private Turno turnoSelect;
    private String estado;

    private WebServiceSGAClientConnection coneccionSGA = new WebServiceSGAClientConnection();
//    @Inject
//    SettingService settingService;
//    private List<Setting> settingList;

    public Long getTurnoId() {
        return (Long) getId();
    }

    public void setTurnoId(Long turnoId) {
        setId(turnoId);
        if (getInstance().isPersistent()) {
            actualizarFecha_Hora();
        }
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
        listaTurnos = turnoS.getTurnosPor(Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
        fms.setEntityManager(em);
        turnoSelect = new Turno();

//        settingService.setEntityManager(em);
//        settingList = settingService.getSettingByName("id_oferta");
    }

    public void cargarTurnosPaciente() {
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
        int hora;
        int min;
        try {
            if (fecha != null && horaD != null && getInstance().getServicio() != null) {
                if (getInstance().isPersistent()) {
                    System.out.println("INGRESO GUARDAR existente_____-" + getInstance().toString());
                    if (!fecha.equals(getInstance().getFechaCita())) {
                        hora = Strings.hora_minuto(horaD, 0);
                        min = Strings.hora_minuto(horaD, 1);
                        getInstance().setFechaCita(fecha);
                        getInstance().setHora(FechasUtil.fijarHoraMinutoConFecha(getInstance().getFechaCita(), hora, min));
                        getInstance().setEstado("Postergada");
                    }
                    save(getInstance());
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Se actualizo Cita Médica: " + getInstance().getId() + " con éxito", " ");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                    setInstance(null);
                    salida = "/pages/" + getBackView() + "?faces-redirect=true";
                } else {
                    //System.out.println("INGRESO GUARDAR_____3-");
                    hora = Strings.hora_minuto(horaD, 0);
                    min = Strings.hora_minuto(horaD, 1);
                    getInstance().setFechaCita(fecha);
                    getInstance().setHora(FechasUtil.fijarHoraMinutoConFecha(getInstance().getFechaCita(), hora, min));
                    getInstance().setEstado("Por Realizar");
                    getInstance().setPaciente(paciente);
                    create(getInstance());
                    save(getInstance());
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Se creo nueva Cita Medica: " + getInstance().getId() + " con éxito", "");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                    //System.out.println("INGRESO GUARDAR_____EXITO");
                    salida = "/pages/" + getBackView() + "?faces-redirect=true"
                            + "&pacienteId=" + getPacienteId();
                }
            }
            //System.out.println("INGRESO GUARDAR_____2-");

        } catch (Exception e) {
            //e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al guardar ", "LLene todos los datos");
            FacesContext.getCurrentInstance().addMessage("", msg);
            //System.out.println("INGRESO GUARDAR_____error");
        }
        return salida;
    }

    @TransactionAttribute
    public String postergar() {
        System.out.println("INGRESO postergar_____-");
        String salida = null;        
        int hora;
        int min;
        try {
            if (fecha != null && horaD != null && turnoSelect.getServicio() != null) {
                if (turnoSelect.isPersistent()) {
                    System.out.println("INGRESO GUARDAR existente_____-" + turnoSelect.toString());
                    if (!fecha.equals(turnoSelect.getFechaCita())) {
                        hora = Strings.hora_minuto(horaD, 0);
                        min = Strings.hora_minuto(horaD, 1);
                        turnoSelect.setFechaCita(fecha);
                        turnoSelect.setHora(FechasUtil.fijarHoraMinutoConFecha(turnoSelect.getFechaCita(), hora, min));
                        turnoSelect.setEstado("Postergada");
                    }
                    save(getInstance());
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Se actualizo Cita Médica: " + turnoSelect.getId() + " con éxito", " ");
                    FacesContext.getCurrentInstance().addMessage("", msg);
                    setInstance(null);
                    turnoSelect = null;
                }
            }

        } catch (Exception e) {
            //e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al guardar ", "LLene todos los datos");
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
            this.listaHoras = this.agregarHoras();
//            if (turnoSelect != null) {
//                this.horasDisponiblesPostergar();
//            } else {
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
        int hora = 8;
        int min = 00;
        String horaS = "";
        for (int i = 0; i < 9; i++) {
            if (min == 00) {
//                f = FechasUtil.fijarHoraMinuto(hora, min);
//                horas.add(FechasUtil.getHoraActual(f));
                if (hora >= 10) {
                    horaS = "" + hora + ":00" + ":AM";
                } else {
                    horaS = "0" + hora + ":00" + ":AM";
                }
                horas.add(horaS);
                min = 30;
            } else {
//                f = FechasUtil.fijarHoraMinuto(hora, min);
//                horas.add(FechasUtil.getHoraActual(f));
                //
                if (hora >= 10) {
                    horaS = "" + hora + ":30" + ":AM";
                } else {
                    horaS = "0" + hora + ":30" + ":AM";
                }
                horas.add(horaS);
                min = 00;
                hora++;
            }
        }
        hora = 3;
        min = 00;
        for (int i = 0; i < 7; i++) {
            if (min == 0) {
//                f = FechasUtil.fijarHoraMinuto(hora, min);
//                horas.add(FechasUtil.getHoraActual(f));
                horaS = "0" + hora + ":00" + ":PM";
                horas.add(horaS);
                min = 30;
            } else {
//                f = FechasUtil.fijarHoraMinuto(hora, min);
//                horas.add(FechasUtil.getHoraActual(f));
                horaS = "0" + hora + ":30" + ":PM";
                horas.add(horaS);
                min = 00;
                hora++;
            }
        }
        return horas;
    }

    public void horasDisponibles() {
        //System.out.println("LISTA DE TURNOS POR FECHA___" + listaTurnos.toString());
        listaTurnos = turnoS.getTurnosPor(fecha);
        String hora = null;
        List<String> listaH = new ArrayList<String>();
        if (!listaTurnos.isEmpty()) {
            for (Turno t : listaTurnos) {
                System.out.println("TURNO______" + t.toString());
                hora = FechasUtil.getHoraActual(t.getHora());
                System.out.println("HORA ___" + hora);
                if (getInstance().getServicio().equals(t.getServicio())) {
                    for (String h : listaHoras) {
                        if (h.equals(hora)) {
                            //listaHoras.remove(hora);
                            listaH.add(h);
                            System.out.println("ELIMINO HORA");
                        }
                    }
                }

            }
            listaHoras.removeAll(listaH);
        }
    }

   
    public void actualizarFecha_Hora() {

        String hor = FechasUtil.getHoraActual(getInstance().getHora());
        setHoraD(hor);
        setFecha(getInstance().getFechaCita());

    }

    public void buscarPor() {
        if (fechaBusc != null && fechaBuscFin != null && !estado.isEmpty()) {
            System.out.println("Ingreso aqui");
            listaTurnos = turnoS.getTurnosPor(fechaBusc, fechaBuscFin, estado);
        } else if (fechaBusc != null && fechaBuscFin != null) {
            System.out.println("Ingreso aqui 2");
            listaTurnos = turnoS.getTurnosPor(fechaBusc, fechaBuscFin);
        } else if (fechaBusc != null && fechaBuscFin == null) {
            System.out.println("Ingreso aqui 2");
            listaTurnos = turnoS.getTurnosPor(fechaBusc);
        } else if (fechaBusc == null && fechaBuscFin != null) {
            System.out.println("Ingreso aqui 2");
            listaTurnos = turnoS.getTurnosPor(fechaBuscFin);
        } else if (estado != null) {
            System.out.println("Ingreso aqui 3");
            listaTurnos = turnoS.getTurnosPor(estado);
        }
    }

    public void buscarTodos() {
        listaTurnos = turnoS.getTurnos();
    }

    public String cancelarCitas() {
        List<Turno> turnosL = turnoS.getTurnosPor(new Date());
        System.out.println("Turnos : "+turnosL.toString());
        if (!turnosL.isEmpty()) {            
            for (Turno t : turnosL) {                
                if ("Por Realizar".equals(t.getEstado())) {
                    System.out.println("Turno no realizado"+t.getId());
                    t.setEstado("No Realizada");
                    em.merge(t);
                }
            }
            listaTurnos = turnoS.getTurnosPor(new Date());
            //return "/pages/depSalud/agenda.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "No hay ninguna cita para este día  ", ""));
        }
        return null;
    }

    public Date getFechaBusc() {
        return fechaBusc;
    }

    public void setFechaBusc(Date fechaBusc) {
        this.fechaBusc = fechaBusc;
        if (fechaBusc != null) {
            listaTurnos = turnoS.getTurnosPor(fechaBusc);
        }
    }

    public String getCriterioBusqueda() {
        return criterioBusqueda;
    }

    public void setCriterioBusqueda(String criterioBusqueda) {
        this.criterioBusqueda = criterioBusqueda;
    }

    public Turno getTurnoSelect() {
        return turnoSelect;
    }

    public void setTurnoSelect(Turno turnoSelect) {
        this.turnoSelect = turnoSelect;
        actualizarFecha_Hora();
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaBuscFin() {
        return fechaBuscFin;
    }

    public void setFechaBuscFin(Date fechaBuscFin) {
        this.fechaBuscFin = fechaBuscFin;
    }

    public String buscarPaciente() {
        if (criterioBusqueda != null) {
            Paciente p = pacienteS.buscarPorCedula(criterioBusqueda);
            if (p != null) {
                if (renderizarVistaMatriculado(p)) {
                    return "/pages/depSalud/turno.xhtml?faces-redirect=true"
                            + "&pacienteId=" + p.getId()
                            + "&backView=depSalud/agenda";
                }
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "No se encontro ningun paciente con el número de cedula ingresado", " ");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        return null;
    }

    public boolean renderizarVistaMatriculado(Paciente p) {
        if (p.isPersistent()) {
            if ("Universitario".equals(p.getTipoEstudiante())) {
                boolean matriculado = coneccionSGA.getEstudianteMatriculado_WS_SGA(p.getCedula());

                if (matriculado) {
                    return true;
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "El paciente no puede acceder a los servicios ya que actualmente no esta matriculado", " ");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }

        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione un Paciente", " ");
            FacesContext.getCurrentInstance().addMessage(null, msg);

        }
        return false;
    }

    public String agregarConsulta() {
        SecurityRules sr = new SecurityRules();
        String s = null;
        if (getInstance().isPersistent()) {
            FichaMedica fm = fms.getFichaMedicaPorPaciente(getInstance().getPaciente());
            if (fm != null) {
                if (sr.isEnfermero(identity)) {
                    s = salidaVista(1, fm);
                } else if (sr.isMedico(identity)) {
                    s = salidaVista(2, fm);
                } else if (sr.isOdontologo(identity)) {
                    s = salidaVista(3, fm);
                }
            } else {
                s = "/pages/depSalud/fichaMedica.xhtml?faces-redirect=true"
                        + "&pacienteId=" + getInstance().getPaciente().getId()
                        + "&backView=/listaFichaMedica";
            }
        }
        return s;
    }

    public String salidaVista(int s, FichaMedica fm) {
        if (s == 1) {
            return "/pages/depSalud/enfermeria/signosVitales.xhtml?faces-redirect=true"
                    + "&fichaMedicaId=" + fm.getId()
                    + "&turnoId=" + getInstance().getId()
                    + "&backView=fichaMedica";

        } else if (s == 2) {
            return "/pages/depSalud/medicina/consultaMedica.xhtml?faces-redirect=true"
                    + "&fichaMedicaId=" + fm.getId()
                    + "&turnoId=" + getInstance().getId()
                    + "&backView=/listaFichaMedica";
        } else if (s == 3) {
            return "/pages/depSalud/odontologia/consultaOdontologica.xhtml?faces-redirect=true"
                    + "&fichaMedicaId=" + fm.getId()
                    + "&turnoId=" + getInstance().getId()
                    + "&backView=/listaFichaMedica";
        } else {
            return "/pages/depSalud/fichaMedica.xhtml?faces-redirect=true"
                    + "&turnoId=" + getInstance().getId()
                    + "&backView=/listaFichaMedica";
        }
    }
}
