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
package edu.sgssalud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author cesar
 */
@Named
@RequestScoped
public class galleryController implements Serializable {

    private List<String> images;
    private Integer entero;
    private double real;
    private String cadena;
    private Date fecha;
    private Long enteroL;
    private boolean boleano;
    
    @PostConstruct
    public void init() {
        images = new ArrayList<String>();

        for (int i = 0; i <= 1; i++) {
            images.add("img" + i + ".jpg");
        }
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }  

    public Integer getEntero() {
        return entero;
    }

    public void setEntero(Integer entero) {
        this.entero = entero;
    }

    public double getReal() {
        return real;
    }

    public void setReal(double real) {
        this.real = real;
    }

    public String getCadena() {
        return cadena;
    }

    public void setCadena(String cadena) {
        this.cadena = cadena;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getEnteroL() {
        return enteroL;
    }

    public void setEnteroL(Long enteroL) {
        this.enteroL = enteroL;
    } 

    public boolean isBoleano() {
        return boleano;
    }

    public void setBoleano(boolean boleano) {
        this.boleano = boleano;
    }
    
    public void update(boolean ban){
        setBoleano(ban);
        setEntero(1);
        setCadena("valor");
    }
}
