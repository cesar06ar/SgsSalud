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
package edu.sgssalud.web.filter;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ResponseHeaderFilter implements Filter {

    FilterConfig fc;

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;

        // Set the characterencoding for the request and response streams.  
        //req.setCharacterEncoding("UTF-8");  
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
 
        response.setContentType("text/xml; charset=UTF-8"); 
        
        req.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        for (Enumeration<?> e = this.fc.getInitParameterNames(); e.hasMoreElements();) {
            String headerName = (String) e.nextElement();
            response.addHeader(headerName, this.fc.getInitParameter(headerName));
        }

        chain.doFilter(req, response);
    }

    @Override
    public void init(final FilterConfig filterConfig) {
        this.fc = filterConfig;
    }

    @Override
    public void destroy() {
        this.fc = null;
    }
}