/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.session;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jpa.entities.TravelPackages;

/**
 *
 * @author matheus
 */
@Stateless
public class TravelPackagesFacade extends AbstractFacade<TravelPackages> {

    @PersistenceContext(unitName = "CPTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TravelPackagesFacade() {
        super(TravelPackages.class);
    }

    public List<TravelPackages> listTravelNext() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 10);
        System.out.println(c.getTime());
        return em.createNamedQuery("TravelPackages.ListTravelNext")
                .setParameter("init", new Date())
                .setParameter("end", c.getTime()).getResultList();
    }

}
