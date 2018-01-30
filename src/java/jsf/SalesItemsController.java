package jsf;

import jpa.entities.SalesItems;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import jpa.session.SalesItemsFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import jpa.entities.Sales;

@Named("salesItemsController")
@SessionScoped
public class SalesItemsController implements Serializable {

    private SalesItems current;
    private DataModel items = null;
    @EJB
    private jpa.session.SalesItemsFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<SalesItems> salesitems = new ArrayList<>();
    private List<SalesItems> salesanditems = new ArrayList<>();
    private SalesItems sale;

    public SalesItemsController() {
    }

    public SalesItems getSelected() {
        if (current == null) {
            current = new SalesItems();
            selectedItemIndex = -1;
        }
        return current;
    }

    private SalesItemsFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (SalesItems) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new SalesItems();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("SalesItemsCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (SalesItems) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("SalesItemsUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (SalesItems) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("SalesItemsDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public SalesItems getSalesItems(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    /**
     * @return the salesitems
     */
    public List<SalesItems> getSalesitems() {
        return salesitems;
    }

    /**
     * @param salesitems the salesitems to set
     */
    public void setSalesitems(List<SalesItems> salesitems) {
        this.salesitems = salesitems;
    }

    /**
     * @return the salesanditems
     */
    public List<SalesItems> getSalesanditems() {
        salesanditems = ejbFacade.listItemsAndSale();
        return salesanditems;
    }

    /**
     * @param salesanditems the salesanditems to set
     */
    public void setSalesanditems(List<SalesItems> salesanditems) {
        this.salesanditems = salesanditems;
    }

    public void findBySale() {

    }

    public int quantidade(Sales s) {
        int qtde = 0;
        for (SalesItems item : s.getSalesItemsList()) {
            qtde += item.getAmount();
        }
        return qtde;
    }

    public double subtotal(Sales s) {
        double subtotal = 0;
        for (SalesItems item : s.getSalesItemsList()) {
            subtotal += (item.getAmount() * item.getTravelPackagesId().getValue());
        }
        return subtotal;
    }

    public double total(Sales s) {
        return subtotal(s) - s.getDiscount();
    }

    /**
     * @return the sale
     */
    public SalesItems getSale() {
        return sale;
    }

    /**
     * @param sale the sale to set
     */
    public void setSale(SalesItems sale) {
        this.sale = sale;
    }

    public void printCust(){
        System.out.println(sale.getSalesId().getCustomersId().getName());
    }
    
    @FacesConverter(forClass = SalesItems.class)
    public static class SalesItemsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SalesItemsController controller = (SalesItemsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "salesItemsController");
            return controller.getSalesItems(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof SalesItems) {
                SalesItems o = (SalesItems) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + SalesItems.class.getName());
            }
        }

    }

}
