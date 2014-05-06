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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.util.Dates;
import edu.sgssalud.util.FechasUtil;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;

/**
 *
 * @author cesar
 */
public class WebServiceSGAClientConnection {

    public Paciente validarPaciente(String cedula) {
        System.out.println("Ingreso a coneccion________");
        Paciente p = new Paciente();
        if (!cedula.isEmpty()) {
            String URL = "http://ws.unl.edu.ec/sgaws/wspersonal/soap/api.wsdl";
            try {
                String resultado = executeWebServiceOperation(URL, "sgaws_datos_estudiante", new Object[]{cedula});
                List<String> listaDatos = convertirJsonArrayAString(resultado);
                if (!listaDatos.isEmpty() && cedula.equals(listaDatos.get(0))) {
                    p.setCedula(listaDatos.get(0));
                    p.setNombreUsuario(listaDatos.get(0));
                    p.setClave(listaDatos.get(0));
                    p.setNombres(listaDatos.get(1));
                    p.setApellidos(listaDatos.get(2));
                    p.setFechaNacimiento(Dates.getFormatoFecha1(listaDatos.get(3)));
                    p.setTelefono(listaDatos.get(4));
                    p.setCelular(listaDatos.get(5));
                    String s = listaDatos.get(8) + "--" + listaDatos.get(6);
                    p.setDireccion(s);
                    p.setNacionalidad(listaDatos.get(7));
                    p.setEmail(listaDatos.get(9));
                    p.setGenero(listaDatos.get(10));
                    
                    System.out.println("FECHA NACI "+listaDatos.get(3));
                    return p;
                } else {
                    System.out.println("CEDULA VACIA___O NO CONSTA EN EL WEB SERVICE");
                }
            } catch (Exception ex) {
                Logger.getLogger(WebServiceSGAClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("CEDULA VACIA___");
        }
        return null;
    }

    public boolean autenticarUsuariosWSSGA(String user, String password) {
        if (!user.isEmpty() && !password.isEmpty()) {
            String URL = "http://ws.unl.edu.ec/sgaws/wsvalidacion/soap/api.wsdl";
            try {
                String resultado = executeWebServiceOperation(URL, "sgaws_validar_estudiante", new Object[]{user, password});
                System.out.println("RESULTADO____"+resultado);
                if (!resultado.isEmpty() && resultado.equals("true")) {
                    return true;
                } else {
                    //System.out.println("CEDULA VACIA___O NO CONSTA EN EL WEB SERVICE");
                }
            } catch (Exception ex) {
                Logger.getLogger(WebServiceSGAClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //System.out.println("CEDULA VACIA___");
        }
        return false;
    }

    public String executeWebServiceOperation(String wsdlURL,
            String operation, Object[] parameters) throws Exception {

        URL url = new URL(wsdlURL);
        System.out.println("Invoco servicio web ");
        ClassLoader currentClassLoader = Thread.currentThread()
                .getContextClassLoader();
        HttpURLConnection connection = null;
        try {
            String user = "sgssalud";
            String pass = "catv3856";

            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(7000);
            connection.connect();
            //int rc = connection.getResponseCode();
            //if (rc == HttpURLConnection.HTTP_OK) {
            System.out.println("Se conecto -----");
            //Autenticacion al web service
            Bus bus = CXFBusFactory.getThreadDefaultBus();
            MyHTTPConduitConfigurer conf = new MyHTTPConduitConfigurer(user, pass);
            bus.setExtension(conf, HTTPConduitConfigurer.class);
            JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory
                    .newInstance(bus);

            Client client = factory.createClient(url);
            Object[] res;
            //System.out.println("Invocando WebService---> " + operation);
            if (parameters.length == 0) {
                res = client.invoke(operation);
            } else {
                //System.out.println("Con parámetros---> " + Arrays.toString(parameters));
                res = client.invoke(operation, parameters);
            }
            Object o = res[0];
            connection.disconnect();
            return o.toString();
//            } else {
//                System.out.println("No se pudo conectar-----");
//            }
            //Autenticacion al web service

        } catch (Exception e) {
            //e.printStackTrace();
            throw new Exception("Problema al ejecutar servicio Web " + e);
        } finally {
            System.out.println("Termino Consulta--->");
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
        //return null;
    }

    public List<String> convertirJsonArrayAString(String json) {
        Gson converter = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> list = converter.fromJson(json.toString(), type);
        return list;
    }

    public static void main(String[] args) {
//        new WebServiceSGAClientConnection().cargarServicio("1722404041");
/*
         JsonParser parser = new JsonParser();
         JsonElement elem = parser.parse(r);
         JsonElement elem = parser.parse("[\"1722404041\", \"c\u00e9sar antonio\", \"abad ramos\", \"1989-06-24\", \"2688501\", \"086768023\", \"Cdla. Julio Ordo\u00f1ez\", \"Ecuador\", \"loja\", \"cesar06ar@gmail.com\", \"masculino\"]");
         JsonArray elemArr = elem.getAsJsonArray();
         String c = "";//elemArr.getAsString();
         List<String> ls = new ArrayList<String>();
         ls.addAll(elemArr.);
         for (int i = 0; i < elemArr.size(); i++) {
         ls.add(elemArr.get(i).getAsString());
         System.out.println(elemArr.get(i).getAsString());
         }
         System.out.println("JSON ARRAY" + elem.toString());
         System.out.println("LISTA JAVA " + ls.toString());
         */
//        
//        Date fIngreso = new Date(); //Date(2013, 02, 2);
//        Date felaboracion = new Date();//Date(2014, 02, 5);
//        //boolean c = FechasUtil.getFechasIguales(fIngreso, felaboracion);
//        boolean c = fIngreso.before(felaboracion);
//        //int c = FechasUtil.getFechaLimite(fIngreso, felaboracion);
//        System.out.println("dias: " + c);              
    }
}
