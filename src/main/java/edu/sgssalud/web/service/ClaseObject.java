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

import com.google.gson.annotations.SerializedName;
import java.util.Collection;

/**
 *
 * @author cesar
 */
public class ClaseObject {

// El tipo de la clase, p.ej "Java.lang.String"
// Ejemplo del uso de Anotaciones GSON
    @SerializedName("tipo_de_la_clase")
    public String classname;
// el valor que se almacena si es un tipo simplre como String o integer, p.ej "100"    
    public String valor;
// la variable que se usa si es un tipo simple, p.ej "saldo"    
    public String var;
// el tipo de la variable: simple como (String) o complejo (ArrayList, etc)    
    public String type;
// Si es una clase con un atributo que es otra clase, aqui almacenamos estos    
    @SerializedName("hijos")
    public Collection coleccion;
// identificador para tipos CHOICE
    public String idchoice;
// identificador para tipos SEQUENCE
    public String idseq;
// el tipo de clase que almacena si es un tipo complejo
    public String coleccionType;
}
