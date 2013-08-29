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
import edu.sgssalud.util.FechasUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;

/**
 *
 * @author cesar
 */
public class Test {

    public static void main(String[] args) {
        /*try {

         System.out.println("-- SERIALIZANDO OBJETO a String JSON como: ");

         // Instanciar GSON para dejar la salida "bonita" y con soporte de Anotaciones
         Gson gson = new GsonBuilder().setPrettyPrinting()
         .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

         // Almacenar cadena="ABCDEFG"
         ClaseObject c = new ClaseObject();
         c.classname = "java.lang.String";
         c.type = "STRING";
         c.valor = "ABCDEFG";
         c.var = "cadena";

         // Almacenar numero="0123456789"
         ClaseObject c2 = new ClaseObject();
         c2.classname = "java.lang.String";
         c2.type = "STRING";
         c2.valor = "c\u00e9sar";
         c2.var = "nombre";

         // La clase contenedora
         ClaseObject complejo = new ClaseObject();
         complejo.classname = "java.util.ArrayList";

         ArrayList array = new ArrayList(2);
         array.add(c);
         array.add(c2);

         complejo.coleccion = array;
         //String complex = gson.toJson(complejo);            
            
         String[] complex1 = new String[]{"1722404041", "c\u00e9sar antonio", "abad ramos", "1989-06-24", "2688501", "086768023", "Cdla. Julio Ordo\u00f1ez", "Ecuador", "loja", "cesar06ar@gmail.com", "masculino"};
         String complex = gson.toJson(complex1);            
         // Lo serializamos a JSON String
         System.out.println("Objeto codificado como :" + complex);
            
         // y ahora el proceso inverso, lo decodificamos
         System.out.println("-- DESERIALIZANDO String JSON a OBJETO:");

         ClaseObject leido = new ClaseObject();
         leido = gson.fromJson(complex, ClaseObject.class);

         System.out.println("Classname es:" + leido.classname);

         int num_hijos = leido.coleccion.size();
         System.out.printf("Tiene %s elementos en la coleccion", num_hijos);
         System.out.println();

         //            int indice = 0;
         //            Iterator ite = leido.coleccion.iterator();
         //            while (ite.hasNext()) {
         //                ClaseObject hijo = (ClaseObject) ite.next();
         //                System.out.println("-&amp;amp;gt; hijo " + indice + " valor: " + hijo.valor);
         //                indice++;
         //            }
            
            
            
         } catch (Exception e) {
         e.printStackTrace();
         }*/

//        JSONArray jsonArray;
//        try {
//            jsonArray = new JSONArray("[\"1722404041\", \"c\u00e9sar antonio\", \"abad ramos\", \"1989-06-24\", \"2688501\", \"086768023\", \"Cdla. Julio Ordo\u00f1ez\", \"Ecuador\", \"loja\", \"cesar06ar@gmail.com\", \"masculino\"]");
//            List<String> ls = new ArrayList<String>();
//            for (int i = 0; i < jsonArray.length(); i++) {
//                ls.add(jsonArray.getString(i));
//            }
//            
//        } catch (JSONException ex) {
//            System.out.println(" Error al cargar datos ");
//        }

//        File f = new File("/home/cesar/Escritorio/datosEstudiante.txt");
//        BufferedReader entrada;
//        try {
//            entrada = new BufferedReader(new FileReader(f));
//            String linea = "";
//            JsonReader r = null;
//            while (entrada.ready()) {
//                linea = entrada.readLine();
//                if (!linea.isEmpty()) {
//                    System.out.println("Texto Ingresado " + linea);
//                    JsonParser parser = new JsonParser();
//                    JsonElement elem = parser.parse(linea);
//                    JsonArray elemArr = elem.getAsJsonArray();
//                    System.out.println("JSON Array " + elemArr.toString());
//                    List<String> ls = new ArrayList<String>();
//                    for (int i = 0; i < elemArr.size(); i++) {
//                        ls.add(elemArr.get(i).getAsString());
//                    }
//                    System.out.println("Lista " + ls.toString());
//                }
//            }

//            //r = new JsonReader();
//            JsonParser parser = new JsonParser();
//            //JsonElement elem = parser.parse(r);
//            JsonElement elem = parser.parse("[\"1722404041\", \"c\u00e9sar antonio\", \"abad ramos\", \"1989-06-24\", \"2688501\", \"086768023\", \"Cdla. Julio Ordo\u00f1ez\", \"Ecuador\", \"loja\", \"cesar06ar@gmail.com\", \"masculino\"]");
//            JsonArray elemArr = elem.getAsJsonArray();
//            String c = "";//elemArr.getAsString();
//            List<String> ls = new ArrayList<String>();
//            //ls.addAll(elemArr.);
//            for (int i = 0; i < elemArr.size(); i++) {
//                ls.add(elemArr.get(i).getAsString());
//                System.out.println(elemArr.get(i).getAsString());
//            }
//            System.out.println("JSON ARRAY" + elem.toString());
//            System.out.println("LISTA JAVA " + ls.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
        Date fIngreso = new Date(); //Date(2013, 02, 2);
        Date felaboracion = new Date();//Date(2014, 02, 5);
        //boolean c = FechasUtil.getFechasIguales(fIngreso, felaboracion);
        boolean c = fIngreso.before(felaboracion);
        //int c = FechasUtil.getFechaLimite(fIngreso, felaboracion);
        System.out.println("dias: " + c);
    }
}
