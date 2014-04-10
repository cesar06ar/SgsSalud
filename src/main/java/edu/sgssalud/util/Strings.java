/**
 * This file is part of Glue: Adhesive BRMS
 * 
* Copyright (c)2012 José Luis Granda <jlgranda@eqaula.org> (Eqaula Tecnologías
 * Cia Ltda) Copyright (c)2012 Eqaula Tecnologías Cia Ltda (http://eqaula.org)
 * 
* If you are developing and distributing open source applications under the GNU
 * General Public License (GPL), then you are free to re-distribute Glue under
 * the terms of the GPL, as follows:
 * 
* GLue is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
* Glue is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
* You should have received a copy of the GNU General Public License along with
 * Glue. If not, see <http://www.gnu.org/licenses/>.
 * 
* For individuals or entities who wish to use Glue privately, or internally,
 * the following terms do not apply:
 * 
* For OEMs, ISVs, and VARs who wish to distribute Glue with their products, or
 * host their product online, Eqaula provides flexible OEM commercial licenses.
 * 
* Optionally, Customers may choose a Commercial License. For additional
 * details, contact an Eqaula representative (sales@eqaula.org)
 */
package edu.sgssalud.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public class Strings {

    /**
     * Replace all instances of camel case naming standard with the underscore
     * equivalent. E.g. "someString" becomes "some_string". This method does not
     * perform any validation and will not throw any exceptions.
     *
     * @param input unformatted string
     * @return modified result
     */
    public static String camelToUnderscore(final String input) {
        String result = input;
        if (input instanceof String) {
            result = input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        }
        return result;
    }

    /**
     * Join all strings, using the delim character as the delimeter. E.g.: a
     * delim of "-", and strings of "foo", "bar" would produce "foo-bar"
     *
     * @param buf
     * @param strings
     * @return
     */
    public static String join(final String delim, final String... strings) {
        String result = "";
        for (String string : strings) {
            result += delim + string;
        }

        if (delim != null) {
            result = result.substring(delim.length());
        }
        return result;
    }

    public static String canonicalize(final String name) {
        String result = null;
        if (name != null) {
            result = name.toLowerCase().replace(' ', '-').replaceAll("[^a-z0-9-]*", "").replaceAll("-+", "-");
        }
        return result;
    }

    public static int hora_minuto(String hora, final int pos){
        List<String> hor = Arrays.asList(hora.split(":"));
        int h = Integer.parseInt(hor.get(pos));        
        return h;
    }
    
    public static String guardarImagenEnFicheroTemporal(byte[] imagen, String nombreArchivo) {
        String rutaImg = null;
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = servletContext.getRealPath("") + File.separatorChar
                + "resources" + File.separatorChar + "img" + File.separatorChar
                + "temp" + File.separatorChar + nombreArchivo;
        File f = null;
        InputStream in = null;

        try {
            f = new File(path);
            in = new ByteArrayInputStream(imagen);
            FileOutputStream out = new FileOutputStream(f.getAbsoluteFile());

            int c = 0;
            while ((c = in.read()) >= 0) {
                out.write(c);
            }
            out.flush();
            out.close();
            rutaImg = "/resources/img/temp/" + nombreArchivo;

        } catch (Exception e) {
            System.out.println("Error al convertir la imagen");
        }

        return rutaImg;
    }
}
