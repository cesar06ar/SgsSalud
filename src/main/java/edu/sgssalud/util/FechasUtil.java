/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sgssalud.util;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
        int anio = 0;
        int edad = 0;
        /*
         int anio = fechaActual.get(Calendar.YEAR) - fechaNacimiento.get(Calendar.YEAR);
         int mes = fechaActual.get(Calendar.MONTH) - fechaNacimiento.get(Calendar.MONTH);
         int dia = fechaActual.get(Calendar.DATE) - fechaNacimiento.get(Calendar.DATE);
         //Se ajusta el año dependiendo el mes y el día

         if (mes < 0 || (mes == 0 && dia < 0)) {
         anio--;
         }*/
        if (fechaActual.get(Calendar.MONTH) <= fechaNacimiento.get(Calendar.MONTH)) {
            if (fechaActual.get(Calendar.MONTH) == fechaNacimiento.get(Calendar.MONTH)) {
                if (fechaActual.get(Calendar.DATE) > fechaNacimiento.get(Calendar.DATE)) {
                    anio = -1; //Aun no celebra su cumpleaÃ±os
                }
            } else {
                anio = -1; //Aun no celebra su cumpleaÃ±os
            }
        }
        edad = (fechaActual.get(Calendar.YEAR) - fechaNacimiento.get(Calendar.YEAR)) + anio;
        //Regresa la edad en base a la fecha de nacimiento

        return edad;
    }

    public static String getFecha(Date fecha) {
        Date ahora = fecha;
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        return formateador.format(ahora);
    }

    /**
     * Formatea las fechas con formato yyyy-MM-dd
     *
     * @param fecha
     * @return
     */
    public static String getFecha1(Date fecha) {
        Date ahora = fecha;
        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
        return formateador.format(ahora);

    }

    public static Date fijarHoraMinuto(int horas, int minutos) {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR, horas);
        time.set(Calendar.MINUTE, minutos);
        return time.getTime(); // Devuelve el objeto Date con las nuevas horas añadidas	
    }

    public static Date fijarHoraMinutoConFecha(Date fecha, int horas, int minutos) {
        Calendar time = Calendar.getInstance();
        time.setTime(fecha);
        if (horas >= 3 && horas <= 6) {
            time.set(Calendar.HOUR, horas + 12);
        } else {
            time.set(Calendar.HOUR, horas);
        }
        time.set(Calendar.MINUTE, minutos);
        return time.getTime(); // Devuelve el objeto Date con las nuevas horas añadidas	
    }

    public static Date sumarRestarHorasFecha(Date fecha, int horas) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.HOUR, horas);  // numero de horas a añadir, o restar en caso de horas<0
        return calendar.getTime(); // Devuelve el objeto Date con las nuevas horas añadidas	
    }

    public static Date sumarRestaMinutosFecha(Date fecha, int minutos) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.MINUTE, minutos);  // numero de horas a añadir, o restar en caso de horas<0
        return calendar.getTime(); // Devuelve el objeto Date con las nuevas horas añadidas	
    }

    public static String getHoraActual(Date hora) {
        //Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("hh:mm:a");
        return formateador.format(hora);
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

    public static boolean editable(Date fecha, Date hora, int horas) {
        Calendar now = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        Calendar horaCon = Calendar.getInstance();
        List<String> horasLis = Arrays.asList(FechasUtil.getHoraActual(hora).split(":"));
        int horaC = Integer.parseInt(horasLis.get(0));
        int minute = Integer.parseInt(horasLis.get(1));
        cal.set(Calendar.HOUR, horaC);
        cal.set(Calendar.MINUTE, minute);
        Date fechaCon = FechasUtil.sumarRestarHorasFecha(fecha, horas);
        cal.setTime(fechaCon);

        System.out.println("HORA CONSULTA " + horaC + " : " + minute);
        int horaActual = now.get(Calendar.HOUR);
        int horaConsulta = cal.get(Calendar.HOUR);
        System.out.println("HORA Actual _____  " + horaActual + " hora fecha _________ " + horaConsulta);
        System.out.println("HORA Actual 1 _____  " +now.toString() + " hora fecha 1_________ " + cal.toString());
//        if (horaActual > horaConsulta) {
//            return true;
//        }
        if(now.after(cal)){
            System.out.println("YA NO SE PUEDE EDITAR");
            return true;
        }        
        return false;
    }

    public static int getFechaLimite(Date fechaActual, Date fechaPosterior) {  //retorna la diferencia en dias
        long dias = -1;
        if (fechaActual != null && fechaPosterior != null) {
            final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día 
            Calendar timeMenor = Calendar.getInstance();
            timeMenor.setTime(fechaActual);

            int anio = timeMenor.get(Calendar.YEAR);
            int mes = timeMenor.get(Calendar.MONTH);
            int dia = timeMenor.get(Calendar.DATE);

            Calendar actual = new GregorianCalendar(anio, mes, dia);
            java.sql.Date fecha = new java.sql.Date(actual.getTimeInMillis());
            dias = (fechaPosterior.getTime() - fecha.getTime()) / MILLSECS_PER_DAY;
        }
        return (int) dias;
    }

    public static boolean getFormatoFecha(String fecha) {
        //metodo para validar si la fecha es correcta

        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat formatoFecha1 = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat formatoFecha2 = new SimpleDateFormat("dd/MM/yyyy");
            Date date = formatoFecha.parse(fecha);
            date = formatoFecha1.parse(fecha);
            date = formatoFecha2.parse(fecha);
        } catch (Exception e) {
            return false;
        }
        return true;

    }
}
