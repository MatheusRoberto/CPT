package jsf;


import jpa.entities.Sales;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import jpa.session.SalesFacade;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import jpa.entities.Plots;
import jpa.entities.SalesItems;
import jsf.util.ConexaoReport;
import org.primefaces.event.CellEditEvent;

@Named("salesController")
@SessionScoped
public class SalesController implements Serializable {

    private Sales current;
    private SalesItems item = new SalesItems();
    private ArrayList<SalesItems> pedido = new ArrayList<>();
    private DataModel items = null;
    @EJB
    private jpa.session.SalesFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private int numberPlots = 1;
    private Date firstDate;
    private ArrayList<Date> datas = new ArrayList<>();
    private double valuePlots;
    private final ArrayList<Plots> plotsList = new ArrayList<>();

    public SalesController() {
    }

    public Sales getSelected() {
        if (current == null) {
            current = new Sales();
            selectedItemIndex = -1;
            item = new SalesItems();
        }
        return current;
    }

    private SalesFacade getFacade() {
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
        current = (Sales) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Sales();
        pedido = new ArrayList<>();
        item = new SalesItems();
        selectedItemIndex = -1;
        return "index";
    }

    public String create() {
        try {
            current.setDate(new Date());
//            getFacade().create(current);
//            for (SalesItems salesItems : pedido) {
//                salesItems.setSalesId(current);
//            }
            if (datas.size() > 1) {
                geraParcela();
                current.setPlotsList(plotsList);
            }
            current.setSalesItemsList(pedido);
            getFacade().create(current);
            if (datas.size() > 1) {
                imprimeCarne();
            }
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("SalesCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Sales) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("SalesUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Sales) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("SalesDeleted"));
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

    public Sales getSales(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Sales.class)
    public static class SalesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SalesController controller = (SalesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "salesController");
            return controller.getSales(getKey(value));
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
            if (object instanceof Sales) {
                Sales o = (Sales) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Sales.class.getName());
            }
        }

    }

    /**
     * @return the item
     */
    public SalesItems getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(SalesItems item) {
        this.item = item;
    }

    /**
     * @return the pedido
     */
    public ArrayList<SalesItems> getPedido() {
        return pedido;
    }

    /**
     * @param pedido the pedido to set
     */
    public void setPedido(ArrayList<SalesItems> pedido) {
        this.pedido = pedido;
    }

    public void addItem() {
        SalesItems e = new SalesItems();
        boolean found = false;
        if (pedido.isEmpty()) {
            e.setAmount(item.getAmount());
            e.setSalesId(current);
            e.setTravelPackagesId(item.getTravelPackagesId());
            pedido.add(e);
            // System.out.println("Empty");
        } else {
            for (SalesItems salesItems : pedido) {
                if (Objects.equals(salesItems.getTravelPackagesId().getId(), item.getTravelPackagesId().getId())) {
                    salesItems.setAmount(salesItems.getAmount() + item.getAmount());
                    // System.out.println("Found");
                    found = true;
                }
            }
            if (!found) {
                e.setAmount(item.getAmount());
                e.setSalesId(current);
                e.setTravelPackagesId(item.getTravelPackagesId());
                pedido.add(e);
                // System.out.println("Not Found");
            }
        }

    }

    public void altQtdeItem(SalesItems e, int qtde) {
        if (pedido.contains(e)) {
            e.setAmount(qtde);
        }
    }

    public void subItem(SalesItems e) {
        ArrayList<SalesItems> atual = new ArrayList<>();
        for (SalesItems salesItems : pedido) {
            if (!Objects.equals(e.getTravelPackagesId().getId(), salesItems.getTravelPackagesId().getId())) {
                atual.add(salesItems);
            }
        }
        pedido = atual;
    }

    public double getTotalItem(SalesItems e) {
        return e.getAmount() * e.getTravelPackagesId().getValue();
    }

    public double subTotal(double v, int q) {
        return v * q;
    }

    public int getQuantidade() {
        int qtde = 0;
        qtde = pedido.stream().map((salesItems) -> salesItems.getAmount()).reduce(qtde, Integer::sum);
        return qtde;
    }

    public double getTotal() {
        double total = 0;
        total = pedido.stream().map((salesItems) -> salesItems.getTravelPackagesId().getValue() * salesItems.getAmount()).reduce(total, (accumulator, _item) -> accumulator + _item);
        return total - current.getDiscount();
    }

    public double getSubTotalVenda() {
        double total = 0;
        total = pedido.stream().map((salesItems) -> salesItems.getTravelPackagesId().getValue() * salesItems.getAmount()).reduce(total, (accumulator, _item) -> accumulator + _item);
        return total;
    }

    public void onCellEdit(CellEditEvent event) {
        int oldSI = (int) event.getOldValue();
        int newSI = (int) event.getNewValue();

        if (newSI != oldSI) {
            pedido.get(event.getRowIndex()).setAmount(newSI);
        }

//        System.out.println(event.getRowIndex() + " Old: "+pedido.get(event.getRowIndex()).getAmount()
//        + " OldCellEdit: "+oldSI+" newCellEdit: "+newSI);
    }

    /**
     * @return the numberPlots
     */
    public int getNumberPlots() {
        return numberPlots;
    }

    /**
     * @param numberPlots the numberPlots to set
     */
    public void setNumberPlots(int numberPlots) {
        this.numberPlots = numberPlots;
    }

    /**
     * @return the firstDate
     */
    public Date getFirstDate() {
        return firstDate;
    }

    /**
     * @param firstDate the firstDate to set
     */
    public void setFirstDate(Date firstDate) {
        this.firstDate = firstDate;
    }

    /**
     * @return the datas
     */
    public ArrayList<Date> getDatas() {
        return datas;
    }

    /**
     * @param datas the datas to set
     */
    public void setDatas(ArrayList<Date> datas) {
        this.datas = datas;
    }

    public void geraData() {
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date diaAtual;
        datas.clear();
        if (firstDate == null) {
            diaAtual = new Date();
        } else {
            diaAtual = firstDate;
        }
        for (int e = 0; e < numberPlots; e++) {
            gc.setTime(diaAtual);
            gc.add(GregorianCalendar.MONTH, e);
            Date date = gc.getTime();
            datas.add(date);
        }
    }

    /**
     * @return the valuePlots
     */
    public double getValuePlots() {
        valuePlots = (getTotal() / numberPlots);
        return valuePlots;
    }

    public void geraParcela() {
        plotsList.clear();
        Plots p;
        for (Date data : datas) {
            p = new Plots();
            p.setDueDate(data);
            p.setValue(valuePlots);
            p.setSalesId(current);
            plotsList.add(p);
        }
    }

    public void imprimeCarne() {
        ConexaoReport conexao = new ConexaoReport();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("id", current.getId());
        parametros.put("SUBREPORT_DIR", "/reports/");
        conexao.getRelatorio("/reports/VariasViagemCapa_A4_Landscape.jasper", parametros);

    }

    public boolean existPlots() {
        return datas.size() > 1;
    }
}
