/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author matheus
 */
@Entity
@Table(name = "sales_items")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SalesItems.findAll", query = "SELECT s FROM SalesItems s")
    , @NamedQuery(name = "SalesItems.findById", query = "SELECT s FROM SalesItems s WHERE s.id = :id")
    , @NamedQuery(name = "SalesItems.findByAmount", query = "SELECT s FROM SalesItems s WHERE s.amount = :amount")})
public class SalesItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "amount")
    private int amount;
    @JoinColumn(name = "sales_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Sales salesId;
    @JoinColumn(name = "travel_packages_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TravelPackages travelPackagesId;

    public SalesItems() {
    }

    public SalesItems(Integer id) {
        this.id = id;
    }

    public SalesItems(Integer id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Sales getSalesId() {
        return salesId;
    }

    public void setSalesId(Sales salesId) {
        this.salesId = salesId;
    }

    public TravelPackages getTravelPackagesId() {
        return travelPackagesId;
    }

    public void setTravelPackagesId(TravelPackages travelPackagesId) {
        this.travelPackagesId = travelPackagesId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SalesItems)) {
            return false;
        }
        SalesItems other = (SalesItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.SalesItems[ id=" + id + " ]";
    }
    
}
