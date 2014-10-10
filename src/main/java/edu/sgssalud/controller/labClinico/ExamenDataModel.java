/*
 * Copyright 2014 cesar.
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
package edu.sgssalud.controller.labClinico;

import edu.sgssalud.model.labClinico.ExamenLabClinico;
import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author cesar
 */
public class ExamenDataModel extends ListDataModel<ExamenLabClinico> implements SelectableDataModel<ExamenLabClinico>, Serializable {

    public ExamenDataModel() {
    }

    public ExamenDataModel(List<ExamenLabClinico> list) {
        super(list);
    }

    @Override
    public Object getRowKey(ExamenLabClinico t) {
        return t.getId();
    }

    @Override
    public ExamenLabClinico getRowData(String string) {
        List<ExamenLabClinico> lista = (List<ExamenLabClinico>) getWrappedData();
        for (ExamenLabClinico tratam : lista) {
            if (tratam.getId().equals(string)) {
                return tratam;
            }
        }
        return null;
    }

}
