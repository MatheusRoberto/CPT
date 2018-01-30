/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author matheus
 */
public class ConexaoReport {

    private final HttpServletResponse response;
    private final FacesContext context;
    private ByteArrayOutputStream arrayOutputStream;
    private InputStream inputStream;
    private Connection conexao;

    public ConexaoReport() {
        this.context = FacesContext.getCurrentInstance();
        this.response = (HttpServletResponse) context.getExternalContext().getResponse();
    }

    public void getRelatorio(String arquivo, Map<String, Object> parametros) {
        inputStream = this.getClass().getResourceAsStream(arquivo);
        arrayOutputStream = new ByteArrayOutputStream();


        try {
            JasperReport report = (JasperReport) JRLoader.loadObject(inputStream);
            JasperPrint print = JasperFillManager.fillReport(report, parametros, getConexao());
            JasperExportManager.exportReportToPdfStream(print, arrayOutputStream);

            response.reset();
            response.setContentType("application/pdf");
            response.setContentLength(arrayOutputStream.size());
            response.setHeader("Content-disposition", "inline: filename=carne.pdf");
            response.getOutputStream().write(arrayOutputStream.toByteArray());
            response.getOutputStream().flush();
            response.getOutputStream().close();

            context.responseComplete();
            fechaConexao();

        } catch (JRException | IOException ex) {
            Logger.getLogger(ConexaoReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConexao() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexao = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaee_parcela", "root", "madsr1411");
            System.out.println("Conexão com banco");
            return conexao;
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(ConexaoReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conexao;
    }

    public void fechaConexao() {
        try {
            this.conexao.close();
            System.out.println("Conexao Fechada");
        } catch (SQLException ex) {
            Logger.getLogger(ConexaoReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
