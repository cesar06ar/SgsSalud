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
package edu.sgssalud.controller.odontologia;

import edu.sgssalud.controller.BussinesEntityHome;
import edu.sgssalud.controller.Home;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.odontologia.Diente;
import edu.sgssalud.model.odontologia.Tratamiento;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

/**
 *
 * @author cesar
 */
@Dependent
public class TratamientoAction extends BussinesEntityHome<Tratamiento> implements Serializable {

    private Long dienteId;
    private Diente diente;
    private Long ConsultaOdontId;
    private ConsultaOdontologica consultaOdont;

    //private EntityManager em;
    
//    @PostConstruct
//    private void init(){
//        
//    }
    
    public void guardarTratamiento() {
        System.out.println("TratamientoAction "+" Guardar: ______________");
        Date now = Calendar.getInstance().getTime();
        try {
            getInstance().setFechaRealizacion(now);
            getInstance().setConsultaOdontologica(consultaOdont);            
            getInstance().setDiente(diente);
            System.out.println("TratamientoAction "+" Guardar 2: ______________"+getInstance().toString()+" cuandrantes: ______________");
            System.out.println("TratamientoAction "+" Diente: ______________"+getDiente().getId());
            System.out.println("TratamientoAction "+" consultaOdont: ______________"+getConsultaOdont().getId());
            //error al agregar el diente ....  verificar 
            create(getInstance());  
            //getEm().getTransaction().begin();t
            //getEm().persist(getInstance());
            //getEm().getTransaction().commit();
            //getEm().flush();
            //getEm().close();
            //save(getInstance());            
            System.out.println("TratamientoAction "+" Guardo Con exito: ______________");
            FacesMessage msg = new FacesMessage("Se agrego tratamiento: " + getInstance().getNombre() + " con éxito");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            setInstance(new Tratamiento());
        } catch (Exception e) {
            System.out.println("error :__________"+e.getMessage());            
            FacesMessage msg = new FacesMessage("Error al guardar: " + getInstance().getId());
            FacesContext.getCurrentInstance().addMessage("", msg);
            e.printStackTrace();
        }
    }

    public Long getDienteId() {
        return dienteId;
    }

    public void setDienteId(Long dienteId) {
        this.dienteId = dienteId;
    }

    public Diente getDiente() {
        return diente;
    }

    public void setDiente(Diente diente) {
        this.diente = diente;
    }

    public Long getConsultaOdontId() {
        return ConsultaOdontId;
    }

    public void setConsultaOdontId(Long ConsultaOdontId) {
        this.ConsultaOdontId = ConsultaOdontId;
    }

    public ConsultaOdontologica getConsultaOdont() {
        return consultaOdont;
    }

    public void setConsultaOdont(ConsultaOdontologica consultaOdont) {
        this.consultaOdont = consultaOdont;
    }   
}


