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

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.sun.xml.ws.client.BindingProviderProperties;
import ec.edu.unl.ws.sgaws.wspersonal.soap.SGAWebServicesPersonal;
import ec.edu.unl.ws.sgaws.wspersonal.soap.SGAWebServicesPersonalPortType;
import ec.edu.unl.ws.sgaws.wsvalidacion.soap.SGAWebServicesValidacion;
import ec.edu.unl.ws.sgaws.wsvalidacion.soap.SGAWebServicesValidacionPortType;
import edu.sgssalud.Sgssalud;


import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import org.jboss.net.axis.ServiceFactory;
import org.primefaces.json.JSONArray;

import org.primefaces.json.JSONException;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class TestWebService implements Serializable{

    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(TestWebService.class);
    private String cedula ;
    private String clave;
    private String result;

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    
    @PostConstruct
    public void init() {
        this.setCedula("1722404041");        
    }
    
    public String validar() {
        log.info("Ingreso a cargar 1 ");
        SGAWebServicesValidacion wsvalidacion = new SGAWebServicesValidacion();
        SGAWebServicesValidacionPortType port = wsvalidacion.getSGAWebServicesValidacionPortType();

        if (!cedula.isEmpty() && !clave.isEmpty()) {
            TestWebService.autenticacion_Servidor((BindingProvider) port);
            String result = port.sgawsValidarEstudiante(cedula, clave);
            log.info("la autenticacion es: " + result);
        }
        return null;
    }

    @TransactionAttribute
    public String cargar() {
        log.info("Ingreso a cargar 1 ");
        URL url = null;
        try {
            url = new URL("http://ws.unl.edu.ec/sgaws/wspersonal/soap/api.wsdl");
        } catch (MalformedURLException ex) {
            Logger.getLogger(TestWebService.class.getName()).log(Level.SEVERE, null, ex);
        }
        SGAWebServicesPersonal wscpersonal = new SGAWebServicesPersonal(url);
        SGAWebServicesPersonalPortType port = wscpersonal.getSGAWebServicesPersonalPortType();
        log.info("Ingreso a cargar 2 ");
        if (!cedula.isEmpty()) {
            TestWebService.autenticacion_Servidor((BindingProvider) port);
            Service svc = Service.create(wscpersonal.getServiceName());


            //JAXBContext jbc;
            try {
//                jbc = JAXBContext.newInstance("org.apache.axis2.jaxws.sample.mtom");
//                Dispatch<Object> dispatch = svc.createDispatch(wscpersonal.getServiceName(), jbc, Service.Mode.PAYLOAD);


                //MtomStreamWriter proxy = svc.getPort();
//                BindingProvider bp = (BindingProvider) port;
//                SOAPBinding binding = (SOAPBinding) bp.getBinding();
//                binding.setMTOMEnabled(true);
                //URL url = svc.getWSDLDocumentLocation();
                //svc.addPort(wscpersonal.getServiceName(),SOAPBinding.SOAP11HTTP_MTOM_BINDING, url.toString());

                log.info("Ingreso a cargar datos ");
                JsonParser parser = new JsonParser();
                log.info("cedula valor 1 " + cedula);
                String r = port.sgawsDatosEstudiante(cedula);

                log.info("cedula valor " + cedula);
                JsonElement elem = parser.parse(r);
                JsonArray elemArr = elem.getAsJsonArray();
                log.info("Resultado " + elemArr.toString());
                this.setResult(elemArr.toString());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("resultado: " + elemArr.toString(), ""));

            } catch (Exception ex) {
                Logger.getLogger(TestWebService.class.getName()).log(Level.SEVERE, null, ex);
                log.info("Ingreso a cargar error ");
            }
            log.info("Ingreso a cargar datos");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "¡Ingrese una CedulaValida!", ""));
        }
        log.info("cargado con éxito");
        return null;
    }

    public static void autenticacion_Servidor(BindingProvider provide) {
        try {

            Map<String, Object> requestContext = provide.getRequestContext();
            requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT, 17000);
            requestContext.put("org.jboss.webservice.client.timeout", 1 * 60 * 1000);
            requestContext.put(BindingProvider.USERNAME_PROPERTY, "sgssalud");
            requestContext.put(BindingProvider.PASSWORD_PROPERTY, "catv3856");
        } catch (Exception e) {
            // TODO: handle exception
            System.out.print(e);
        }
    }

    public List<String> convertirJsonArrayAString(String jsonArraylist) throws JSONException {

        JSONArray jsonArray = new JSONArray(jsonArraylist);
        List<String> ls = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            ls.add(jsonArray.getString(i));
        }
        return ls;
    }
}
