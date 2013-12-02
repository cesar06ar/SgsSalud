/*
 * Copyright 2012 jlgranda.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sgssalud.util;

import com.google.common.io.Files;
import edu.sgssalud.model.medicina.HistoriaClinica;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jlgranda
 */
public class Lists {

    /**
     * Convierte una lista de objetos en una cadena separada por comas, con los
     * elementos de la lista. Los objetos de la lista deben implementar el
     * método toString
     *
     * @param list la lista sobre la cual se construye la cadena de elementos
     * seperadas por comas
     * @return una cadena separada por comas, con los elementos de la lista.
     */
    public static String listToString(List list) {

        if (list.isEmpty()) {
            return "-";
        }

        StringBuilder buffer = new StringBuilder();
        int index = 0;
        for (Iterator _iterator = list.iterator(); _iterator.hasNext(); buffer.append(_iterator.next().toString())) {
            if (index > 0) {
                buffer.append(", ");
            }
            index++;
        }
        return buffer.toString();
    }

    /**
     * Convierte una lista de objetos en una cadena separada por comas, con los
     * elementos de la lista. Los objetos de la lista deben implementar el
     * método toString
     *
     * @param list la lista sobre la cual se construye la cadena de elementos
     * seperadas por comas
     * @return una cadena separada por comas, con los elementos de la lista.
     */
    public static String listToString(String[] list) {

        if (list == null) {
            return "-";
        }

        StringBuilder buffer = new StringBuilder();

        int length = list.length;
        for (int i = 0; i < length; i++) {
            buffer.append(list[i]);
            if (i > 0) {
                buffer.append(", ");
            }
        }
        return buffer.toString();
    }

    /**
     * Convierte una lista de objetos en una cadena separada por separador, con
     * los elementos de la lista. Los objetos de la lista deben implementar el
     * método toString
     *
     * @param list la lista sobre la cual se construye la cadena de elementos
     * seperadas por comas
     * @return una cadena separada por separador, con los elementos de la lista.
     */
    public static String listToString(List list, char separador) {

        if (list.isEmpty()) {
            return "-";
        }

        StringBuilder buffer = new StringBuilder();
        int index = 0;
        for (Iterator _iterator = list.iterator(); _iterator.hasNext(); buffer.append(_iterator.next().toString())) {
            if (index > 0) {
                buffer.append(separador);
                buffer.append(' ');
            }
            index++;
        }
        return buffer.toString();
    }

    /**
     * Convierte una lista de objetos en una cadena separada por separador, con
     * los elementos de la lista. Los objetos de la lista deben implementar el
     * método toString
     *
     * @param list la lista sobre la cual se construye la cadena de elementos
     * seperadas por comas
     * @return una cadena separada por separador, con los elementos de la lista.
     */
    public static String listToString(List list, String separador) {

        if (list.isEmpty()) {
            return "-";
        }

        StringBuilder buffer = new StringBuilder();
        int index = 0;
        for (Iterator _iterator = list.iterator(); _iterator.hasNext(); buffer.append(_iterator.next().toString())) {
            if (index > 0) {
                buffer.append(separador);
            }
            index++;
        }
        return buffer.toString();
    }

    public static String toUpperCase(String str) {
        if (str == null) {
            return str;
        }
        return str.toUpperCase();
    }
    private static final int MINIMUM_SUPPORTED_LENGTH = 4;

    /**
     * Truncate text on a whitespace boundary (near a specified length). The
     * length of the resultant string will be in the range:<br>
     * <code>   (requested-length * .25) ~ (requested-length * 1.5) </code>
     *
     * @param text Text to truncate
     * @param length Target length
     * @return Truncated text
     */
    public static String truncateAtWhitespace(String text, int length) {
        int desired, lowerBound, upperBound;
        /*
         * Make sure we have a reasonable length to work with
         */
        if (length < MINIMUM_SUPPORTED_LENGTH) {
            throw new IllegalArgumentException("Requested length too short (must be "
                    + MINIMUM_SUPPORTED_LENGTH + " or greated)");
        }
        /*
         * No need to truncate - the original string "fits"
         */
        if (text.length() <= length) {
            return text;
        }
        /*
         * Try to find whitespace befor the requested maximum
         */
        lowerBound = length / 4;
        upperBound = length + (length / 2);

        for (int i = length - 1; i > lowerBound; i--) {
            if (Character.isWhitespace(text.charAt(i))) {
                return text.substring(0, i);
            }
        }
        /*
         * No whitespace - look beyond the desired maximum
         */
        for (int i = (length); i < upperBound; i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return text.substring(0, i);
            }
        }
        /*
         * No whitespace, just truncate the text at the requested length
         */
        return text.substring(0, length);
    }

    public static List<String> stringToList(String s) {

        if (s == null) {
            return new ArrayList<String>();
        }

        if (s.isEmpty()) {
            return new ArrayList<String>();
        }
        return Arrays.asList(s.split(","));
    }
    
    public static String findDefaultValue(String values) {
        return findDefaultValue(values, "*");
    }
    
    public static String findDefaultValue(String values, String mnemotecnico) {
        List<String> options = Lists.stringToList(values);
        String defaultValue = null;
        for (String s : options) {
            if (s.contains(mnemotecnico)) {
                defaultValue = s.substring(0, s.length() - 1);
            }
        }
        return defaultValue;
    }
    
    final static String FILE_NAME = "C:\\Temp\\input.txt";
    final static String OUTPUT_FILE_NAME = "C:\\Temp\\output.txt";
    final static Charset ENCODING = StandardCharsets.UTF_8;

    public static void main(String args[]) {
        String aFileName = "/home/jlgranda/iaenvirtual/selenium/CrearCurso";
        int MAX = 114;
        Date now = Calendar.getInstance().getTime();
        Date caduca = new Date("22/01/2014");
//        caduca.setDate(22);
//        caduca.setMonth(01);
//        caduca.setYear(2014);
        if(FechasUtil.getFechaLimite(now, caduca) < 90){
            System.out.println("ALERTA CADUCIDAD");
        }else{
            System.out.println("No caduca ");
        }
//        String content = null;
//        File file = null;
//        for (int i = 1; i <= MAX; i++) {
//            content = buildContent(i);
//            aFileName = aFileName + i;
//            file = new File(aFileName);
//            Files.write(content, file, ENCODING);
//            System.out.println(aFileName + " created!");
//            aFileName = "/home/jlgranda/iaenvirtual/selenium/CrearCurso";
//        }
       
        //System.out.println(h.agregarEnfermedad(c));

    }

    private static String buildContent(int i) {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        builder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        builder.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n");
        builder.append("<head profile=\"http://selenium-ide.openqa.org/profiles/test-case\">\n");
        builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
        builder.append("<link rel=\"selenium.base\" href=\"http://www.iaenvirtual.org\" />\n");
        builder.append("<title>CrearCurso</title>\n");
        builder.append("</head>\n");
        builder.append("<body>\n");
        builder.append("<table cellpadding=\"1\" cellspacing=\"1\" border=\"1\">\n");
        builder.append("<thead>\n");
        builder.append("<tr><td rowspan=\"1\" colspan=\"3\">CrearCursoPNBV</td></tr>\n");
        builder.append("</thead><tbody>\n");
        builder.append("<tr>\n");
        builder.append("	<td>open</td>\n");
        builder.append("	<td>/course/edit.php?category=2&amp;returnto=topcat</td>\n");
        builder.append("	<td></td>\n");
        builder.append("</tr>\n");
        builder.append("<tr>\n");
        builder.append("	<td>type</td>\n");
        builder.append("	<td>id=id_fullname</td>\n");
        builder.append("	<td>Plan Nacional del Buen Vivir [" + i + "]</td>\n");
        builder.append("</tr>\n");
        builder.append("<tr>\n");
        builder.append("	<td>type</td>\n");
        builder.append("	<td>id=id_shortname</td>\n");
        builder.append("	<td>PNBV-" + i + "</td>\n");
        builder.append("</tr>\n");
        builder.append("<tr>\n");
        builder.append("	<td>type</td>\n");
        builder.append("	<td>id=id_idnumber</td>\n");
        builder.append("	<td>PNBV-" + i + "</td>\n");
        builder.append("</tr>\n");
        builder.append("<tr>\n");
        builder.append("	<td>type</td>\n");
        builder.append("	<td>id=id_summary_editor</td>");
        builder.append("	<td><strong>Curso Virtual Plan Nacional para el Buen Vivir</strong><br>El objeto de estudio de este curso son los preceptos constitucionales, el marco legal en que se desenvuelve el nuevo Régimen de desarrollo planificado, participativo y descentralizado así como las metodologías y herramientas para el Plan Nacional para el Buen Vivir PNBV.</td>");
        builder.append("</tr>\n");
        builder.append("<tr>\n");
        builder.append("	<td>select</td>\n");
        builder.append("	<td>id=id_format</td>\n");
        builder.append("	<td>label=Formato de temas</td>\n");
        builder.append("</tr>\n");
        builder.append("<tr>\n");
        builder.append("	<td>clickAndWait</td>\n");
        builder.append("	<td>id=id_submitbutton</td>\n");
        builder.append("	<td></td>\n");
        builder.append("</tr>\n");
        builder.append("</tbody></table>\n");
        builder.append("</body>\n");
        builder.append("</html>\n");
        return builder.toString();
    }
}
