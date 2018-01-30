package jsf;

import jpa.entities.Plots;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import jpa.session.PlotsFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import jpa.entities.Sales;
import jsf.util.ConexaoReport;

@Named("plotsController")
@SessionScoped
public class PlotsController implements Serializable {

    private Plots current;
    private DataModel items = null;
    @EJB
    private jpa.session.PlotsFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<Plots> plotsPay = new ArrayList<>();
    private int nmbPlots;
    private List<Plots> plotsPayDay = new ArrayList<>();

    public PlotsController() {
    }

    public Plots getSelected() {
        if (current == null) {
            current = new Plots();
            selectedItemIndex = -1;
        }
        return current;
    }

    private PlotsFacade getFacade() {
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
        current = (Plots) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Plots();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("PlotsCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Plots) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("PlotsUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Plots) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("PlotsDeleted"));
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

    public Plots getPlots(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    /**
     * @return the plotsPay
     */
    public List<Plots> getPlotsPay() {
        return plotsPay;
    }

    /**
     * @param plotsPay the plotsPay to set
     */
    public void setPlotsPay(ArrayList<Plots> plotsPay) {
        this.plotsPay = plotsPay;
    }

    public void readPlots(ValueChangeEvent event) {
        Sales s = (Sales) event.getNewValue();
        System.out.println(ejbFacade.findPlotsBySaleID(s));
        plotsPay = ejbFacade.findPlotsBySaleID(s);
    }

    /**
     * @return the valuePay
     */
    public int getNmbPlots() {
        return nmbPlots;
    }

    /**
     * @param nmbplots
     */
    public void setNmbPlots(int nmbplots) {
        this.nmbPlots = nmbplots;
    }

    public void payVarious() {
        Date d = new Date();
        for (Plots plots : plotsPay) {
            if (plots.getPayday() == null && nmbPlots > 0) {
                plots.setPayday(d);
                nmbPlots--;
                ejbFacade.edit(plots);
            }
        }
    }

    public void imprimeCarne() {
        ConexaoReport conexao = new ConexaoReport();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("id", current.getSalesId().getId());
        parametros.put("SUBREPORT_DIR", "/reports/");
        conexao.getRelatorio("/reports/VariasViagemCapa_A4_Landscape.jasper", parametros);

    }

    public boolean paga(Plots p) {
        return p.getPayday() == null;
    }

    public void payPlot(Plots p) {
        if (p.getPayday() == null) {
            p.setPayday(new Date());
            getFacade().edit(p);
        }
    }

    public double totalPay() {
        double total = 0;
        for (Plots p : plotsPay) {
            if (p.getPayday() != null) {
                total += p.getValue();
            }
        }
        return total;
    }

    public double faultPay() {
        double fault = 0;
        for (Plots p : plotsPay) {
            if (p.getPayday() == null) {
                fault += p.getValue();
            }
        }
        return fault;
    }

    public int qtdePlots() {
        int f = 0;
        for (Plots plots : plotsPay) {
            if (plots.getPayday() == null) {
                f++;
            }
        }
        return f;
    }

    /**
     * @return the plotsPayDay
     */
    public List<Plots> getPlotsPayDay() {
        listPlotsPayDay();
        return plotsPayDay;
    }

    /**
     * @param plotsPayDay the plotsPayDay to set
     */
    public void setPlotsPayDay(List<Plots> plotsPayDay) {
        this.plotsPayDay = plotsPayDay;
    }
    
    public void listPlotsPayDay(){
        plotsPayDay = ejbFacade.listPlotsPayDay();
    }

    @FacesConverter(forClass = Plots.class)
    public static class PlotsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PlotsController controller = (PlotsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "plotsController");
            return controller.getPlots(getKey(value));
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
            if (object instanceof Plots) {
                Plots o = (Plots) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Plots.class.getName());
            }
        }

    }

}
