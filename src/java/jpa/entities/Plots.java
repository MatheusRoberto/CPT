/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author matheus
 */
@Entity
@Table(name = "plots")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Plots.findAll", query = "SELECT p FROM Plots p")
    , @NamedQuery(name = "Plots.findById", query = "SELECT p FROM Plots p WHERE p.id = :id")
    , @NamedQuery(name = "Plots.findByValue", query = "SELECT p FROM Plots p WHERE p.value = :value")
    , @NamedQuery(name = "Plots.findByDueDate", query = "SELECT p FROM Plots p WHERE p.dueDate = :dueDate")
    , @NamedQuery(name = "Plots.findByPayday", query = "SELECT p FROM Plots p WHERE p.payday = :payday")
    , @NamedQuery(name = "Plots.findBySales_Id", query = "SELECT p FROM Plots p WHERE p.salesId = :saleID ORDER BY p.dueDate ASC")
    , @NamedQuery(name = "Plots.listPayDay", query = "SELECT p FROM Plots p INNER JOIN Sales s ON p.salesId = s INNER JOIN Customers c ON s.customersId = c WHERE p.dueDate = :dueDate")})
public class Plots implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "value")
    private double value;
    @Column(name = "due_date")
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    @Column(name = "payday")
    @Temporal(TemporalType.DATE)
    private Date payday;
    @JoinColumn(name = "sales_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Sales salesId;

    public Plots() {
    }

    public Plots(Integer id) {
        this.id = id;
    }

    public Plots(Integer id, double value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getPayday() {
        return payday;
    }

    public void setPayday(Date payday) {
        this.payday = payday;
    }

    public Sales getSalesId() {
        return salesId;
    }

    public void setSalesId(Sales salesId) {
        this.salesId = salesId;
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
        if (!(object instanceof Plots)) {
            return false;
        }
        Plots other = (Plots) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.Plots[ id=" + id + " ]";
    }

}
