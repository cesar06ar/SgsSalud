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
package edu.sgssalud.web.service;

import com.sun.xml.ws.client.BindingProviderProperties;
import ec.edu.unl.ws.sgaws.wspersonal.soap.SGAWebServicesPersonal;
import ec.edu.unl.ws.sgaws.wspersonal.soap.SGAWebServicesPersonalPortType;
import java.io.Serializable;
import java.util.Map;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class TestWebService implements Serializable {

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(TestWebService.class);
    private String cedula;
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    @TransactionAttribute
    public void cargar() {
        
        SGAWebServicesPersonal wscpersonal = new SGAWebServicesPersonal();
        SGAWebServicesPersonalPortType port = wscpersonal.getSGAWebServicesPersonalPortType();
        
        if (!cedula.isEmpty()) {            
            TestWebService.autenticacion_Servidor((BindingProvider) port);            
            log.info("resultado:--->  "+port.sgawsDatosEstudiante(cedula));
            //setResult(port.sgawsDatosEstudiante(cedula));
            log.info("Ingreso a cargar datos");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "¡Ingrese una CedulaValida!", ""));
        }
        log.info("cargado con éxito");
    }

    public static void autenticacion_Servidor(BindingProvider provide) {
        try {

            Map<String, Object> requestContext = provide.getRequestContext();
            //requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT, 17000);
            //requestContext.put("org.jboss.webservice.client.timeout", 1 * 60 * 1000);
            requestContext.put(BindingProvider.USERNAME_PROPERTY, "sgssalud");
            requestContext.put(BindingProvider.PASSWORD_PROPERTY, "catv3856");
        } catch (Exception e) {
            // TODO: handle exception
            System.out.print(e);
        }
    }
}
