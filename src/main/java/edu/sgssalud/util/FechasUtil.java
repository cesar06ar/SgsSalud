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
        SimpleDateFormat formateador = new SimpleDateFormat("dd-mm-yyyy");
        String fechaS = formateador.format(fecha);
        Date fechaNac = null;
        try {
            /*Se puede cambiar la mascara por el formato de la fecha que se
             //quiera recibir, por ejemplo año mes día "yyyy-MM-dd"
             en este caso es día mes año*/
            fechaNac = new SimpleDateFormat("dd-mm-yyyy").parse(fechaS);
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

    public static boolean getFechasIguales(Date fechaMenor, Date fechaMayor) {
        if (fechaMenor != null && fechaMayor != null) {
            Calendar timeMayor = Calendar.getInstance();
            Calendar timeMenor = Calendar.getInstance();
            timeMenor.setTime(fechaMenor);
            timeMayor.setTime(fechaMayor);
            int valor = timeMayor.get(Calendar.YEAR) - timeMenor.get(Calendar.YEAR);
            boolean y = (valor > 0);
            boolean m = (timeMayor.get(Calendar.MONTH) > timeMenor.get(Calendar.MONTH));
            boolean d = (timeMayor.get(Calendar.DATE) > timeMenor.get(Calendar.DATE));
            if (y) {
                return true;
            } else if (m) {
                return true;
            } else if (d) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static int getFechaLimite(Date fechaActual, Date fechaPosterior) {
        if (fechaActual != null && fechaPosterior != null) {
            Calendar timeMayor = Calendar.getInstance();
            Calendar timeMenor = Calendar.getInstance();
            timeMenor.setTime(fechaActual);
            timeMayor.setTime(fechaPosterior);

            int anio = timeMayor.get(Calendar.YEAR) - timeMenor.get(Calendar.YEAR);
            int mes = timeMayor.get(Calendar.MONTH) - timeMenor.get(Calendar.MONTH);
            int dia = timeMayor.get(Calendar.DATE) - timeMenor.get(Calendar.DATE);
            int dias = 0;
            //System.out.println("año :" + anio + " mes:" + mes + " dias:" + dia);
            if (anio > 0) {
                dias += 365;
                if (mes > 0) {
                    dias += (mes * 30);
                    dias += dia;
                }else{
                    dias += dia;
                }
            } else if (mes > 0) {
                dias += (mes * 30);
                dias += dia;
            } else if (dia > 0) {
                dias += dia;
            }

            return dias;
        }
        return 0;
    }
}
