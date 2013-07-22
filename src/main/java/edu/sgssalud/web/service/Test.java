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
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author cesar
 */
public class Test {

    public static void main(String[] args) {
        try {

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
        }

    }
}
