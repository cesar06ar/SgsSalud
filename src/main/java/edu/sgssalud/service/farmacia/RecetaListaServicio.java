/*
 * Copyright 2013 tania.
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
package edu.sgssalud.service.farmacia;

import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.farmacia.Medicamento;
import edu.sgssalud.model.farmacia.Receta;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author tania
 */

@Named(value = "recetaListaServicio")
@ViewScoped
public class RecetaListaServicio extends LazyDataModel<Medicamento>{
    
    private static final long serialVersionUID = 13L;
    private static final int MAX_RESULTS = 13;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(RecetaListaServicio.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private RecetaServicio recetaServicio;
    private List<Receta> resultList;
    private int primerResult = 0;
    private Receta[] recetasSeleccionados;
    private Receta recetaSeleccionado;
    private String parametroBusqueda;

    public RecetaListaServicio() {
        setPageSize(MAX_RESULTS);
        resultList = new ArrayList<Receta>();
    }

    public List<Receta> getResultList() {
        if (resultList.isEmpty() /*&& getSelectedBussinesEntityType() != null*/) {
            resultList = recetaServicio.obtenerRecetas(this.getPageSize(), primerResult);
        }
        return resultList;
    }

    public void setResultList (List<Receta> resultList){
        this.resultList = resultList;        
            
    }
    
    public int getPrimerResult() {
        return primerResult;
    }

    public void setPrimerResult(int primerResult) {
        this.primerResult = primerResult;
    }
    
       

    public Receta[] getRecetasSeleccionados() {
        return recetasSeleccionados;
    }

    public void setRecetasSeleccionados(Receta[] recetasSeleccionados) {
        this.recetasSeleccionados = recetasSeleccionados;
    }

    public Receta getRecetaSeleccionado() {
        return recetaSeleccionado;
    }

    public void setRecetaSeleccionado(Receta recetaSeleccionado) {
        this.recetaSeleccionado = recetaSeleccionado;
    }

    
    public int obtenerSiguienteResultado() {
        return primerResult + this.getPageSize();
    }

    public int obtenerMedicamentoAnterior() {
        return this.getPageSize() >= primerResult ? 0 : primerResult - this.getPageSize();
    }
    
    
}
