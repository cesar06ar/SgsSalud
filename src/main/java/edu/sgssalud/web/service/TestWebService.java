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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
//import org.apache.commons.httpclient.util.HttpURLConnection;
import org.primefaces.json.JSONArray;

import org.primefaces.json.JSONException;
/*import sun.misc.BASE64Encoder;*/

/**
 *
 * @author cesar
 */
@Named
@RequestScoped
public class TestWebService {

    //private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(TestWebService.class);
    private String cedula;
    private String clave;    

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

    @PostConstruct
    public void init() {
        this.setCedula("1722404041");
    }



    


   

    public List<String> convertirJsonArrayAString(String jsonArraylist) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonArraylist);
        List<String> ls = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            ls.add(jsonArray.getString(i));
        }
        return ls;
    }
    /*
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
     }*/
    //    public String validar() {
//
//        String ruta = "http://ws.unl.edu.ec/sgaws/wspersonal/sgaws_datos_estudiante?cedula=" + cedula;
//        //String ruta = "http://ws.unl.edu.ec/sgaws/documento_wsdl?";
//        //String ruta = "http://sgssalud:catv3856ws.unl.edu.ec/sgaws/wsvalidacion/sgaws_validar_estudiante?cedula=" + cedula + "&clave=" + clave;
//        //String ruta = "http://localhost:8080/demoee6/rest/members/1";
//
//        URL url;
//
//        System.out.println("Ingreso conectar ");
//        HttpURLConnection connection = null;
//        try {
//            //String userpass = "sgssalud" + ":" + "catv3856";
//            //String encod = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
//
//            //encod1 = new org.drools.core.util.codec.Base64().encodeToString(userpass.getBytes());
//            url = new URL(ruta);
//            connection = (HttpURLConnection) url.openConnection();
//            //System.out.println("OPEN CONECION____");
//            //connection.setDoOutput(true);
//            //connection.setInstanceFollowRedirects(false);
//            connection.setRequestMethod("POST");
//            //connection.setRequestProperty("Content-Type", "application/xml");
//            connection.setRequestProperty("Content-Type", "text/plain");
//            //connection.setRequestProperty("Content-Type", "text/json");
//            //connection.setRequestProperty("Content-Type", "application/json");
//            connection.setRequestProperty("charset", "UTF-8");
//            //conection.setRequestProperty("Authorization", "Basic " + encod);
//            //connection.setAllowUserInteraction(false);
//
//            Authenticator.setDefault(new Authenticator() {
//                @Override
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication("sgssalud", "catv3856".toCharArray());
//                }
//            });
//            connection.connect();
//
//            //org.apache.commons.httpclient.util.HttpURLConnection c = (org.apache.commons.httpclient.util.HttpURLConnection) url.openConnection();           
//            //InputStream in = conection.getInputStream();            
//            int rc = connection.getResponseCode();
//            if (rc == HttpURLConnection.HTTP_OK) {
//                System.out.println("se conecto  ");
//                //List<NameValuePair> params = new ArrayList<NameValuePair>();
//                //params.add(new BasicNameValuePair("firstParam", paramValue1));
//                /*DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
//                 wr.writeBytes(urlParam);
//                 wr.flush();
//                 wr.close();*/
//                //
//                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String str = bufferReader.readLine();
//                StringBuilder stringBuffer = new StringBuilder();
//                System.out.println("se conecto  222  " + str);
//                /*while ((str = bufferReader.readLine()) != null) {
//                 stringBuffer.append(str);
//                 stringBuffer.append("\n");
//                 }*/
//
//                /*JsonReader lector = new JsonReader(new InputStreamReader(
//                 connection.getInputStream()));
//                 JsonParser parseador = new JsonParser();
//                 JsonElement raiz = parseador.parse(lector);
//                 JsonArray lista = raiz.getAsJsonArray();
//                 System.out.println("RESULTADO  " + lista.getAsString());*/
//                //System.out.println("RESULTADO 2 " + s.toString());
//                //connection.disconnect();
//            }
//            System.out.println("TERMINO SIN RESPUESTA  ");
//            /*JsonParser parser = new JsonParser();
//             JsonElement elemS = parser.parse(stringBuffer.toString());            
//             JsonArray elemArrS = elemS.getAsJsonArray();            
//             System.out.println("RESULTADO Gson  " + elemArrS.toString());*/
//            connection.disconnect();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            System.out.println("Invalid URL");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            System.out.println("ERRROR...... ");
//            Logger.getLogger(TestWebService.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            System.out.println("ERRROR generales...... ");
//            Logger.getLogger(TestWebService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }

}
