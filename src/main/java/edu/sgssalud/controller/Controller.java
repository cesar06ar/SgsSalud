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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sgssalud.controller;

import java.io.Serializable;
import javax.faces.context.FacesContext;
import edu.sgssalud.util.Interpolator;
import org.jboss.solder.logging.Logger;

/**
 *
 * @author jlgranda
 */
public abstract class Controller implements Serializable {

    private static Logger log = Logger.getLogger(Controller.class);

    protected FacesMessages getFacesMessages() {
        return FacesMessages.instance();
    }

    protected void addFacesMessage(String messageTemplate, Object... params) {
        getFacesMessages().add(messageTemplate, params);
    }

    protected void addFacesMessageFromResourceBundle(String key, Object... params) {
        getFacesMessages().addFromResourceBundle(key, params);
    }

    protected Logger getLog() {
        return log;
    }

    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    protected String interpolate(String string, Object... params) {
        return Interpolator.interpolate(string, params);
    }

    protected void debug(Object object, Object... params) {
        log.debugf(object.toString(), params);
    }

    protected void debug(Object object, Throwable t, Object... params) {
        log.debugf(t, object.toString(), params);
    }

    protected void error(Object object, Object... params) {
        log.errorf(object.toString(), params);
    }

    protected void error(Object object, Throwable t, Object... params) {
        log.errorf(t, object.toString(), params);
    }

    protected void fatal(Object object, Object... params) {
        log.fatalf(object.toString(), params);
    }

    protected void fatal(Object object, Throwable t, Object... params) {
        log.fatalf(t, object.toString(), params);
    }

    protected void info(Object object, Object... params) {
        log.infof(object.toString(), params);
    }

    protected void info(Object object, Throwable t, Object... params) {
        log.infof(t, object.toString(), params);
    }

    protected void trace(Object object, Object... params) {
        log.tracef(object.toString(), params);
    }

    protected void trace(Object object, Throwable t, Object... params) {
        log.tracef(t, object.toString(), params);
    }

    protected void warn(Object object, Object... params) {
        log.warnf(object.toString(), params);
    }

    protected void warn(Object object, Throwable t, Object... params) {
        log.warnf(t, object.toString(), params);
    }

    protected Object getComponentInstance(String name) {
        //return Component.getInstance(name);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected Object getComponentInstance(Class clazz) {
        //return Component.getInstance(clazz);
        throw new UnsupportedOperationException("Not yet implemented");
    }
    /*
     protected void invalidateSession()
     {
     Session.instance().invalidate();
     }

     protected boolean isTransactionMarkedRollback()
     {
     try
     {
     return Transaction.instance().isMarkedRollback();
     }
     catch (Exception e)
     {
     return false;
     }
     }
     */
    private static final long serialVersionUID = 3100628880857984156L;
}
