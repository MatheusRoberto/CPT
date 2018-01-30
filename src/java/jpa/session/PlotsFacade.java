/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.session;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jpa.entities.Plots;
import jpa.entities.Sales;

/**
 *
 * @author matheus
 */
@Stateless
public class PlotsFacade extends AbstractFacade<Plots> {

    @PersistenceContext(unitName = "CPTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlotsFacade() {
        super(Plots.class);
    }
    
    public List<Plots> findPlotsBySaleID(Sales sale){
       return em.createNamedQuery("Plots.findBySales_Id").setParameter("saleID", sale).getResultList();
    }
    
    public List<Plots> listPlotsPayDay(){
        return em.createNamedQuery("Plots.listPayDay").setParameter("dueDate", new Date()).getResultList();
    }
}
