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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

/**
 *
 * @author cesar
 */
@WebServiceClient(name="ClienteWebService", targetNamespace="http://echo",
        wsdlLocation = "http://ws.unl.edu.ec/")
public class ClienteWebService extends Service{
    
    private final static URL ECHOSERVICE_WSDL_LOCATION;
    
    static {
        URL url = null;
        try {
            url = new URL("http://ws.unl.edu.ec/");
        } catch (MalformedURLException e ) {
            e.printStackTrace();
        }
        ECHOSERVICE_WSDL_LOCATION = url;
    }
    
    public ClienteWebService(URL wsdlLocation, QName serviceName){
        super(wsdlLocation, serviceName);        
    }
    
    public ClienteWebService(){
        super(ECHOSERVICE_WSDL_LOCATION, new QName("http://echo/", "ClienteWebService"));        
    }
    /*
    @WebEndpoint(name = "EchoPort")
    public Echo getEchoPort()
    {
         return (Echo)super.getPort(new QName("http://echo/", "EchoPort"), Echo.class);
    }*/
}
