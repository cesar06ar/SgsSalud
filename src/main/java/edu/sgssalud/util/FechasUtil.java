/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sgssalud.util;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author cesar
 */
public class FechasUtil {

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//        // TODO code aplication logic here     
//
//        Date fnat = new Date();
//
//        System.out.println("fecha en String " + FechasUtil.getFecha(fnat));
//        System.out.println(" es:  " + FechasUtil.calcularEdad(fnat));
//    }

    public static Integer calcularEdad(Date fecha) {
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        String fechaS = formateador.format(fecha);
        Date fechaNac = null;
        try {
            /*Se puede cambiar la mascara por el formato de la fecha que se
             //quiera recibir, por ejemplo año mes día "yyyy-MM-dd"
             en este caso es día mes año*/
            fechaNac = new SimpleDateFormat("dd-MM-yyyy").parse(fechaS);
        } catch (Exception ex) {
            System.out.println("Error:" + ex);
        }
        Calendar fechaNacimiento = Calendar.getInstance();
        //Se crea un objeto con la fecha actual
        fechaNacimiento.setTime(fechaNac);
        //Se restan la fecha actual y la fecha de nacimiento
        
        Calendar fechaActual = Calendar.getInstance();
        //Se asigna la fecha recibida a la fecha de nacimiento.

        int anio = fechaActual.get(Calendar.YEAR) - fechaNacimiento.get(Calendar.YEAR);
        int mes = fechaActual.get(Calendar.MONTH) - fechaNacimiento.get(Calendar.MONTH);
        int dia = fechaActual.get(Calendar.DATE) - fechaNacimiento.get(Calendar.DATE);
        //Se ajusta el año dependiendo el mes y el día
        
        if (mes < 0 || (mes == 0 && dia < 0)) {
            anio--;
        }
        //Regresa la edad en base a la fecha de nacimiento

        return anio;
    }

    public static String getFecha(Date fecha) {
        Date ahora = fecha;
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        return formateador.format(ahora);
    }
}
