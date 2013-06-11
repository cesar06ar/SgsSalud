/*
 * Copyright 2013 jlgranda.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * hUnless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sgssalud;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.service.SettingService;

/**
 *
 * @author jlgranda
 */
@Named 
@ViewScoped
public class Sgssalud implements Serializable {
    private static final long serialVersionUID = 7673935874688660972L;
    
    @Inject
    @Web
    private EntityManager em;
    
    @Inject
    SettingService settingService;
    
    public String getProperty(String name, final String defaultValue){
        Setting s = settingService.findByName(name);
        return s== null ? defaultValue : s.getValue();
    }
    
    @PostConstruct
    public void init() {
        settingService.setEntityManager(em);
    }
}
