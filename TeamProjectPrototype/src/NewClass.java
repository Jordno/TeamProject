
import Controllers.DatabaseConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Wolf
 */
public class NewClass {
    NewClass(){
        createReport();
    }
    public static void main(String args[]){
        new NewClass();
    
    }
    public void createReport(){
        DatabaseConnection dbc = new DatabaseConnection();
        Connection conn = dbc.getConnection();
        
        try{
            System.out.println(System.getProperty("user.dir")+"\\reporttemplates\\Invoice.jrxml");
            JasperReport jasperMasterReport = JasperCompileManager.compileReport("C:\\Users\\Wolf\\Documents\\IReport\\Invoice.jrxml");
            //JasperReport jasperSubReport = JasperCompileManager.compileReport("C:\\Users\\Wolf\\Documents\\IReport\\Invoice_customersDetail.jrxml");
            
            
//            String cDetail = "C:\\Users\\Wolf\\Documents\\IReport\\Invoice_customersDetail.jrxml";
//            JasperDesign jd = JRXmlLoader.load(cDetail);
            Map paraMap = new HashMap();
            paraMap.put("jobID",2);
            //paraMap.put("SUBREPORT_DIR",jasperSubReport);
            //JRDesignQuery query = new JRDesignQuery();
            //query.setText("SELECT * FROM `customers` WHERE customerID = 2");
            //jd.setQuery(query);
            //JasperReport jr = JasperCompileManager.compileReport(jasperMas);
            JasperPrint jp = JasperFillManager.fillReport(jasperMasterReport, paraMap, conn);
            JasperExportManager.exportReportToPdfFile(jp,
                  "C:\\Users\\Wolf\\Documents\\NetBeansProjects\\TeamProjectPrototype\\sample_report.pdf");
            

//            jd = JRXmlLoader.load("C:\\Users\\Wolf\\Documents\\IReport\\Invoice.jrxml");
//            jr = JasperCompileManager.compileReport(jd);
//            jp = JasperFillManager.fillReport(jr, null, conn);
            
            /**
             * JasperReport jasperMasterReport = JasperCompileManager.compileReport(masterReportSource);
JasperReport jasperSubReport = JasperCompileManager.compileReport(subReportSource);
             * Integer rnFromUser = ...;
Map parametersMap = new HashMap();  
parametersMap.put("rn", rnFromUser);
JasperPrint jasperPrint = JasperFillManager.fillReport(
jasperReport, parametersMap, jdbcConnection);
             */
            JasperViewer.viewReport(jp);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    /**
     * SELECT `Name`,`Address`,`Street`,`Locality`,`City`,`PostCode` FROM `jobs` "
                    + "INNER JOIN vehicles ON vehicles.RegNo=jobs.RegNo "
                    + "INNER JOIN customers ON vehicles.CustomerID = customers.CustomerID"
                    + "WHERE customerID = 2
     */
}
