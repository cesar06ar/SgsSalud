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
package edu.sgssalud.stats;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import edu.sgssalud.model.BussinesEntity;
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.model.Property;
import edu.sgssalud.model.Structure;

import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.service.BussinesEntityService;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.MeterGaugeChartModel;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
@Named
@RequestScoped
public class Statistics {

   //private ProfileService ps;
    
    private BussinesEntityService bes;

    public Statistics() {
    }

    @Inject
    public Statistics(EntityManager em, BussinesEntityService bes) {
        
        this.bes = bes;
        //ps.setEntityManager(em);
        this.bes.setEntityManager(em);
        
    }

    public long getUserCount() {
        return bes.countBussinesEntity(Profile.class);
    }
    
    public float calculeAttributesCompletedPercent(Long bussinesEntityId, String bussinesEntityTypeName) {
        Long required = bes.getCountRequiredProperties(bussinesEntityId, bussinesEntityTypeName);
        Long completed = bes.getCountCompletedRequiredProperties(bussinesEntityId, bussinesEntityTypeName);
        if (required == 0L) {
            return 1;
        }
        return (float) (completed / required);
    }

    public float calculeMembersCompletedPercent(Long required, Long completed) {
        if (required == 0L) {
            return 1;
        }
        if (completed > required) {
            return 1;
        }
        return (float) (completed / required);
    }
    private MeterGaugeChartModel meterGaugeModel;

    private void createMeterGaugeModel(Number value) {

        List<Number> intervals = new ArrayList<Number>() {
            {
                add(0);
                add(25);
                add(50);
                add(75);
                add(100);
            }
        };

        meterGaugeModel = new MeterGaugeChartModel(value, intervals);
    }

    public MeterGaugeChartModel getMeterGaugeModel() {
        return meterGaugeModel;
    }

    public void setMeterGaugeModel(MeterGaugeChartModel meterGaugeModel) {
        this.meterGaugeModel = meterGaugeModel;
    }

    public MeterGaugeChartModel buildMeterGaugeModel(Number value) {
        createMeterGaugeModel(value);
        return getMeterGaugeModel();
    }
    /**
     * ********************************************************************
     * Bar and Line chart
     *********************************************************************
     */
    private CartesianChartModel categoryModel;
    private CartesianChartModel linearModel;

    public CartesianChartModel getCategoryModel() {
        return categoryModel;
    }

    public CartesianChartModel getLinearModel() {
        return linearModel;
    }

    private void createCategoryModel() {
        categoryModel = new CartesianChartModel();

        ChartSeries entities = new ChartSeries();
        entities.setLabel("Entidades");
        entities.set("Perfiles", bes.countBussinesEntity(Profile.class));
        
        //entities.set("Tipos de entidades de negocio", bes.countBussinesEntity(BussinesEntityType.class));
        //entities.set("Estructuras de datos", bes.countBussinesEntity(Structure.class));
        //entities.set("Propiedades", bes.countBussinesEntity(Property.class));
        /*entities.set("2006", 44);
        entities.set("2007", 150);
        entities.set("2008", 25);*/

        /*ChartSeries girls = new ChartSeries();
        girls.setLabel("Girls");

        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 110);
        girls.set("2007", 135);
        girls.set("2008", 120);*/

        categoryModel.addSeries(entities);
        //categoryModel.addSeries(girls);
    }
    
    public CartesianChartModel buildCartesianChartModel(){
        createCategoryModel();
        return getCategoryModel();
    }

   /* private void createLinearModel() {
        linearModel = new CartesianChartModel();

        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Series 1");

        series1.set(1, 2);
        series1.set(2, 1);
        series1.set(3, 3);
        series1.set(4, 6);
        series1.set(5, 8);

        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel("Series 2");
        series2.setMarkerStyle("diamond");

        series2.set(1, 6);
        series2.set(2, 3);
        series2.set(3, 2);
        series2.set(4, 7);
        series2.set(5, 9);

        linearModel.addSeries(series1);
        linearModel.addSeries(series2);
    }*/
}
