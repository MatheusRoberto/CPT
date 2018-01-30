/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author matheus
 */
@Entity
@Table(name = "travel_packages")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TravelPackages.findAll", query = "SELECT t FROM TravelPackages t")
    , @NamedQuery(name = "TravelPackages.findById", query = "SELECT t FROM TravelPackages t WHERE t.id = :id")
    , @NamedQuery(name = "TravelPackages.findByTravel", query = "SELECT t FROM TravelPackages t WHERE t.travel = :travel")
    , @NamedQuery(name = "TravelPackages.findByDeparture", query = "SELECT t FROM TravelPackages t WHERE t.departure = :departure")
    , @NamedQuery(name = "TravelPackages.findByArrival", query = "SELECT t FROM TravelPackages t WHERE t.arrival = :arrival")
    , @NamedQuery(name = "TravelPackages.findByValue", query = "SELECT t FROM TravelPackages t WHERE t.value = :value")
    ,@NamedQuery(name = "TravelPackages.ListTravelNext", query = "SELECT t FROM TravelPackages t WHERE t.departure BETWEEN :init AND :end")})
public class TravelPackages implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = true)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "travel")
    private String travel;
    @Column(name = "departure")
    @Temporal(TemporalType.DATE)
    private Date departure;
    @Column(name = "arrival")
    @Temporal(TemporalType.DATE)
    private Date arrival;
    @Basic(optional = false)
    @NotNull
    @Column(name = "value")
    private double value;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "travelPackagesId")
    private List<SalesItems> salesItemsList;

    public TravelPackages() {
    }

    public TravelPackages(Integer id) {
        this.id = id;
    }

    public TravelPackages(Integer id, String travel, double value) {
        this.id = id;
        this.travel = travel;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTravel() {
        return travel;
    }

    public void setTravel(String travel) {
        this.travel = travel;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    public Date getArrival() {
        return arrival;
    }

    public void setArrival(Date arrival) {
        this.arrival = arrival;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @XmlTransient
    public List<SalesItems> getSalesItemsList() {
        return salesItemsList;
    }

    public void setSalesItemsList(List<SalesItems> salesItemsList) {
        this.salesItemsList = salesItemsList;
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
        if (!(object instanceof TravelPackages)) {
            return false;
        }
        TravelPackages other = (TravelPackages) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return travel;//"jpa.entities.TravelPackages[ id=" + id + " ]";
    }

}
