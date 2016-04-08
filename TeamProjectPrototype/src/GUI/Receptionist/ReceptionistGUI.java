/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Receptionist;

import Controllers.DatabaseConnection;
import Controllers.ReceptionistController;
import Entities.Customer;
import Entities.Job;
import Entities.StockDelivery;
import Entities.StockItem;
import Entities.Vehicle;
import GUI.Franchisee.CustomerTableModel;
import GUI.Franchisee.VehicleTableModel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.Timer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

/**
 * need to add verifcation checks for adding job/stock
 * @author Wolf
 */
public class ReceptionistGUI extends javax.swing.JFrame {
    private ArrayList<Job> jobData;
    private ArrayList<Vehicle> vehData;
    private ArrayList<StockItem> stockData;
    private ArrayList<StockDelivery> deliveryData;
    private ArrayList<Customer> cusData;
    private ArrayList<Vehicle> cusVehData;
    private ReceptionistController controller;
    private int selectedRow, selectedCol, jobID, cusID;
    private String regNo;
    private String stockCode;
    private double costItem;
    private Timer timer;

    /**
     * Creates new form ReceptionistGUI
     */
    public ReceptionistGUI(DatabaseConnection dbc) {
        controller = new ReceptionistController(dbc);
        jobData = new ArrayList<>();
        vehData = new ArrayList<>();
        stockData = new ArrayList<>();
        deliveryData = new ArrayList<>();
        cusVehData = new ArrayList<>();
        selectedRow = -1;
        selectedCol = -1;
        initTimer();
        initData();
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);

    }
    
    private void initTimer(){
        //SELECT * FROM `sparepartstock` WHERE `Stock_Level` < `Threshold_Level`
        timer = new Timer(10000, new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int amount = controller.lowStockAmount();
                if(amount > 0){
                    int response = JOptionPane.showConfirmDialog(null, "There are "+amount+" items that are below the threshold."
                            + "\nDo you wish to view these items? \nNote: Clicking Yes will stop the reminders till next login", "Items below thershold!", 
                         JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); 
                    if(response == JOptionPane.YES_OPTION){
                        stockData = controller.getLowStockData();
                        stm.refresh(stockData);
                        panelSwitcher("viewStock");
                        timer.stop();
                    }
                }
            }
        });  
        timer.start();
    }
    
//    private void updateRowHeights(){
//        for (int row = 0; row < jobTable.getRowCount(); row++){
//            int rowHeight = jobTable.getRowHeight();
//            Component comp = jobTable.prepareRenderer(jobTable.getCellRenderer(row, 2), row, 2);
//            rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
//            jobTable.setRowHeight(row, rowHeight);
//        }
//    }
    
    public void initData(){
        jobData = controller.getJobData();
        vehData = controller.getVehData();
        stockData = controller.getStockData();
        cusData = controller.getCusData();
    }
    
    private void updateData(){
        initData(); //re-run this method to get the most updated version
        jtm.refresh(jobData); //sends the updated data to tablemodel and updates it to jtable
        vtm.refresh(vehData);
        stm.refresh(stockData);
    }
    
    public void initVehData(){
        vehData = controller.getCusVehData(cusID);
        vtm.refresh(vehData);
    }
    
    private void jobRightClickMenu(ActionEvent event){
        JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
        int selected;
        try{
            selected = (int)jobTable.getValueAt(selectedRow, 0);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,
                "Please select a valid row.",
                "Invalid selection",
            JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if(menu == workReqMenu){
            new JobInformationList(jobData.get(selectedRow).getWorkReq());
        }else if(menu == alterJobMenu){
            jobID = selected;
            JobInformationList list = new JobInformationList(jobData.get(selectedRow).getWorkReq());
            list.setVisible(false);
            ArrayList<String> li = new ArrayList<>();
            li = list.getData();
            for(int i=0; i < li.size(); i++){
                workReqTextArea1.append(li.get(i)+"\n");
            }
            String status = (String) jobTable.getValueAt(selectedRow, 2);
            String bustype = (String) jobTable.getValueAt(selectedRow, 3);
            String dur = (String) jobTable.getValueAt(selectedRow, 4);
            String[] spl = dur.split(":");
            switch (status) {
                case "IDLE":
                    jobStatusCombo.setSelectedIndex(0);
                    break;
                case "IN PROGRESS":
                    jobStatusCombo.setSelectedIndex(1);
                    break;
                default:
                    jobStatusCombo.setSelectedIndex(2);
                    break;
            }
            
            switch (bustype) {
                case "Repair":
                    busTypeCombo1.setSelectedIndex(0);
                    break;
                case "Service":
                    busTypeCombo1.setSelectedIndex(1);
                    break;
                default:
                    busTypeCombo1.setSelectedIndex(2);
                    break;
            }
            
            hourSpinner1.setValue(Integer.parseInt(spl[0]));
            minSpinner1.setValue(Integer.parseInt(spl[1]));
            secSpinner1.setValue(Integer.parseInt(spl[2]));
            panelSwitcher("alterJob");
        }else if(menu == generateInvoiceMenu){
            if(((String)jobTable.getValueAt(selectedRow, 2)).equals("COMPLETED")){
                controller.generateInvoice(selected,(String)jobTable.getValueAt(selectedRow, 1));
            }else{
                JOptionPane.showMessageDialog(null,
                    "Job status must be completed before invoice generation",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        
        }
    }
    
    private void vehRightClickMenu(ActionEvent event){
       JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
       String selected;
       try{
            selected = (String)vehTable.getValueAt(selectedRow, 0);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,
                "Please select a valid row.",
                "Invalid selection",
            JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }
       if(menu == createJobMenu){
            regNo = selected;
            newJobTitle.setText("Creating a new Job for "+selected);
            panelSwitcher("newJobInfo");
        }
        updateData();
    }
    
    private void stockRightClickMenu(ActionEvent event){
       JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
       String selected;
       try{
            selected = (String)stockTable.getValueAt(selectedRow, 0);
        }catch(Exception e){
            System.out.println(selectedRow);
            JOptionPane.showMessageDialog(null,
                "Please select a valid row.",
                "Invalid selection",
            JOptionPane.ERROR_MESSAGE);
            return;
        }
       
       if(menu == viewDeliveryMenu){
            stockCode = selected;
            deliveryData = controller.getDeliveryData(stockCode);
            sdtm.refresh(deliveryData);
            panelSwitcher("viewDelivery");
        }else if(menu == newDeliveryMenu){
            stockCode = selected;
            costItem = (double)stockTable.getValueAt(selectedRow, 6);
            panelSwitcher("newOrder");
        }else if(menu == alterStockItem){
            stockCode = selected;
            codeField1.setText((String)stockTable.getValueAt(selectedRow, 0));
            nameField1.setText((String)stockTable.getValueAt(selectedRow, 1));
            manuField1.setText((String)stockTable.getValueAt(selectedRow, 2));
            vehTypeField1.setText((String)stockTable.getValueAt(selectedRow, 3));
            yearField1.setText((String)stockTable.getValueAt(selectedRow, 4));
            priceField1.setText(String.valueOf(stockTable.getValueAt(selectedRow, 5)));
            costItemField1.setText(String.valueOf(stockTable.getValueAt(selectedRow, 6)));
            stockField1.setText(String.valueOf(stockTable.getValueAt(selectedRow, 7)));
            thresoldField1.setText(String.valueOf(stockTable.getValueAt(selectedRow, 9)));
            initalStockField.setText(String.valueOf(stockTable.getValueAt(selectedRow, 10)));
            panelSwitcher("alterStock");
        }
        updateData();  
    }
    
    private void deliveryRightClickAction(ActionEvent event){
       JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
       int selected;
       try{
            selected = (int)deliveryTable.getValueAt(selectedRow, 0);
        }catch(Exception e){
            System.out.println(selectedRow);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Please select a valid row.",
                "Invalid selection",
            JOptionPane.ERROR_MESSAGE);
            return;
        }
       
       if(menu == recieveDeliveryMenu){
  
           if((Boolean)deliveryTable.getValueAt(selectedRow, 3)){
                JOptionPane.showMessageDialog(null,
                    "This item has already been recieved.",
                    "Cannot recieve item again.",
                    JOptionPane.ERROR_MESSAGE);
           }else{
               controller.recieveDelivery(selected,(int)deliveryTable.getValueAt(selectedRow, 2),(String)deliveryTable.getValueAt(selectedRow, 1));
           }
       }else if(menu == deleteDeliveryMenu){
           controller.deleteDelivery(selected);
       }
       updateData();
       panelSwitcher("viewStock");
    }
    
    private void cusRightClickMenuAction(ActionEvent event){
        JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
        int selected = (int)cusTable.getValueAt(selectedRow, 0);
        if(selectedCol == -1 || selectedRow == -1){
            JOptionPane.showMessageDialog(null,
                "Please select a valid row.",
                "Invalid selection",
            JOptionPane.ERROR_MESSAGE);
        }else if(menu == addVehMenu){
            panelSwitcher("AddVeh");
            cusID = selected;
            //vehTitleLabel.setText("Attach a Vehicle to "+cusTable.getValueAt(selectedRow,1));
        }else if(menu == viewVehMenu){
            cusID = selected;
            initVehData();
            panelSwitcher("newJob");
        }  
    }
    
    public void panelSwitcher(String type){
        mainPanel.removeAll();
        switch(type){
            case "ViewJobDetails":
                mainPanel.add(viewJobPanel);
                break;
            case "newJob":
                mainPanel.add(newJobPanel);
                break;
            case "newJobInfo":
                mainPanel.add(newJobInfoPanel);
                break;
            case "viewStock":
                mainPanel.add(viewStockPanel);
                break;
            case "addStockItem":
                mainPanel.add(newStockPanel);
            case "viewDelivery":
                mainPanel.add(viewDeliveryPanel);
            case "newOrder":
                mainPanel.add(orderInfoPanel);
            case "alterStock":
                mainPanel.add(alterStockPanel);
            case "alterJob":
                mainPanel.add(alterJobPanel);
            case "viewCus":
                mainPanel.add(customerPanel);
        }
        mainPanel.repaint();
        mainPanel.revalidate();
    }
    
    private void searchTable(String filter){
        jobTable.setRowSorter(jobSorter);
        vehTable.setRowSorter(vehSorter);
        stockTable.setRowSorter(stockSorter);
        deliveryTable.setRowSorter(deliverySorter);
        //(?i) is used to ignore case in the search term
        jobSorter.setRowFilter(RowFilter.regexFilter("(?i)"+filter));
        vehSorter.setRowFilter(RowFilter.regexFilter("(?i)"+filter));
        stockSorter.setRowFilter(RowFilter.regexFilter("(?i)"+filter));
        deliverySorter.setRowFilter(RowFilter.regexFilter("(?i)"+filter));
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jobRightClickMenu = new javax.swing.JPopupMenu();
        workReqMenu = new javax.swing.JMenuItem();
        alterJobMenu = new javax.swing.JMenuItem();
        generateInvoiceMenu = new javax.swing.JMenuItem();
        vehRightClickMenu = new javax.swing.JPopupMenu();
        createJobMenu = new javax.swing.JMenuItem();
        stockRightClickMenu = new javax.swing.JPopupMenu();
        deliverySettingMenu = new javax.swing.JMenu();
        viewDeliveryMenu = new javax.swing.JMenuItem();
        newDeliveryMenu = new javax.swing.JMenuItem();
        alterStockItem = new javax.swing.JMenuItem();
        deliveryRightClickMenu = new javax.swing.JPopupMenu();
        recieveDeliveryMenu = new javax.swing.JMenuItem();
        deleteDeliveryMenu = new javax.swing.JMenuItem();
        cusRightClickMenu = new javax.swing.JPopupMenu();
        addVehMenu = new javax.swing.JMenuItem();
        viewVehMenu = new javax.swing.JMenuItem();
        sideMenuPanel = new javax.swing.JPanel();
        viewVehicleBtn = new javax.swing.JButton();
        viewJobBtn = new javax.swing.JButton();
        userConLabel = new javax.swing.JLabel();
        updateTableBtn = new javax.swing.JButton();
        searchLabel = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        viewStockBtn = new javax.swing.JButton();
        addStockBtn = new javax.swing.JButton();
        viewCustomersBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        viewJobPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jobTable = new javax.swing.JTable();
        newJobPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        vehTable = new javax.swing.JTable();
        newJobInfoPanel = new javax.swing.JPanel();
        newJobTitle = new javax.swing.JLabel();
        workReqLabel = new javax.swing.JLabel();
        busTypeLabel = new javax.swing.JLabel();
        durationLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        workReqTextArea = new javax.swing.JTextArea();
        busTypeCombo = new javax.swing.JComboBox();
        createJobBtn = new javax.swing.JButton();
        workReqLabel1 = new javax.swing.JLabel();
        workReqLabel2 = new javax.swing.JLabel();
        hourSpinner = new javax.swing.JSpinner();
        minSpinner = new javax.swing.JSpinner();
        secSpinner = new javax.swing.JSpinner();
        hourLabel = new javax.swing.JLabel();
        minLabel = new javax.swing.JLabel();
        secLabel = new javax.swing.JLabel();
        viewStockPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        stockTable = new javax.swing.JTable();
        newStockPanel = new javax.swing.JPanel();
        codeLabel = new javax.swing.JLabel();
        stockNameLabel = new javax.swing.JLabel();
        manuLabel = new javax.swing.JLabel();
        vehTypeLabel = new javax.swing.JLabel();
        yearLabel = new javax.swing.JLabel();
        stockLvlLabel = new javax.swing.JLabel();
        thresholdLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        codeField = new javax.swing.JTextField();
        nameField = new javax.swing.JTextField();
        manuField = new javax.swing.JTextField();
        vehTypeField = new javax.swing.JTextField();
        yearField = new javax.swing.JTextField();
        stockField = new javax.swing.JTextField();
        thresoldField = new javax.swing.JTextField();
        addNewItemBtn = new javax.swing.JButton();
        priceLabel = new javax.swing.JLabel();
        priceField = new javax.swing.JTextField();
        costItemLabel = new javax.swing.JLabel();
        costItemField = new javax.swing.JTextField();
        viewDeliveryPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        deliveryTable = new javax.swing.JTable();
        orderInfoPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        quantityField = new javax.swing.JTextField();
        createOrder = new javax.swing.JButton();
        alterStockPanel = new javax.swing.JPanel();
        codeLabel1 = new javax.swing.JLabel();
        priceField1 = new javax.swing.JTextField();
        manuLabel1 = new javax.swing.JLabel();
        yearLabel1 = new javax.swing.JLabel();
        thresoldField1 = new javax.swing.JTextField();
        alterItemBtn = new javax.swing.JButton();
        stockNameLabel1 = new javax.swing.JLabel();
        manuField1 = new javax.swing.JTextField();
        vehTypeField1 = new javax.swing.JTextField();
        costItemLabel1 = new javax.swing.JLabel();
        thresholdLabel1 = new javax.swing.JLabel();
        vehTypeLabel1 = new javax.swing.JLabel();
        priceLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        stockField1 = new javax.swing.JTextField();
        nameField1 = new javax.swing.JTextField();
        stockLvlLabel1 = new javax.swing.JLabel();
        codeField1 = new javax.swing.JTextField();
        costItemField1 = new javax.swing.JTextField();
        yearField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        initalStockField = new javax.swing.JTextField();
        alterJobPanel = new javax.swing.JPanel();
        secLabel1 = new javax.swing.JLabel();
        minSpinner1 = new javax.swing.JSpinner();
        secSpinner1 = new javax.swing.JSpinner();
        workReqLabel5 = new javax.swing.JLabel();
        busTypeLabel1 = new javax.swing.JLabel();
        minLabel1 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        workReqTextArea1 = new javax.swing.JTextArea();
        newJobTitle1 = new javax.swing.JLabel();
        alterJobBtn = new javax.swing.JButton();
        workReqLabel4 = new javax.swing.JLabel();
        busTypeCombo1 = new javax.swing.JComboBox();
        durationLabel1 = new javax.swing.JLabel();
        workReqLabel3 = new javax.swing.JLabel();
        hourSpinner1 = new javax.swing.JSpinner();
        hourLabel1 = new javax.swing.JLabel();
        jobStatusLabel = new javax.swing.JLabel();
        jobStatusCombo = new javax.swing.JComboBox();
        customerPanel = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        cusTable = new javax.swing.JTable();

        workReqMenu.setText("View Work Required");
        workReqMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workReqMenuActionPerformed(evt);
            }
        });
        jobRightClickMenu.add(workReqMenu);

        alterJobMenu.setText("Alter job information");
        alterJobMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterJobMenuActionPerformed(evt);
            }
        });
        jobRightClickMenu.add(alterJobMenu);

        generateInvoiceMenu.setText("Generate an Invoice");
        generateInvoiceMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateInvoiceMenuActionPerformed(evt);
            }
        });
        jobRightClickMenu.add(generateInvoiceMenu);

        jobTable.setComponentPopupMenu(jobRightClickMenu);

        createJobMenu.setText("Create a job for this Vehicle");
        createJobMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createJobMenuActionPerformed(evt);
            }
        });
        vehRightClickMenu.add(createJobMenu);

        vehTable.setComponentPopupMenu(vehRightClickMenu);

        deliverySettingMenu.setText("Delivery Settings");

        viewDeliveryMenu.setText("View Delivery Info for this item");
        viewDeliveryMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewDeliveryMenuActionPerformed(evt);
            }
        });
        deliverySettingMenu.add(viewDeliveryMenu);
        stockTable.setComponentPopupMenu(stockRightClickMenu);

        newDeliveryMenu.setText("Order Stock for this item");
        newDeliveryMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newDeliveryMenuActionPerformed(evt);
            }
        });
        deliverySettingMenu.add(newDeliveryMenu);

        stockRightClickMenu.add(deliverySettingMenu);

        alterStockItem.setText("Alter Stock Item");
        alterStockItem.setToolTipText("");
        alterStockItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterStockItemActionPerformed(evt);
            }
        });
        stockRightClickMenu.add(alterStockItem);

        recieveDeliveryMenu.setText("Recieve Delivery");
        recieveDeliveryMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recieveDeliveryMenuActionPerformed(evt);
            }
        });
        deliveryRightClickMenu.add(recieveDeliveryMenu);

        deleteDeliveryMenu.setText("Delete Delivery");
        deleteDeliveryMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDeliveryMenuActionPerformed(evt);
            }
        });
        deliveryRightClickMenu.add(deleteDeliveryMenu);

        deliveryTable.setComponentPopupMenu(deliveryRightClickMenu);

        addVehMenu.setText("Add Vehicle to customer");
        addVehMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addVehMenuActionPerformed(evt);
            }
        });
        cusRightClickMenu.add(addVehMenu);

        viewVehMenu.setText("View Assosicated Vehicles");
        viewVehMenu.setToolTipText("");
        viewVehMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewVehMenuActionPerformed(evt);
            }
        });
        cusRightClickMenu.add(viewVehMenu);

        cusTable.setComponentPopupMenu(cusRightClickMenu);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        sideMenuPanel.setBackground(new java.awt.Color(255, 255, 255));
        sideMenuPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        viewVehicleBtn.setText("View all Vehicles");
        viewVehicleBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewVehicleBtnActionPerformed(evt);
            }
        });

        viewJobBtn.setText("View Job Details");
        viewJobBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewJobBtnActionPerformed(evt);
            }
        });

        userConLabel.setText("User Control");

        updateTableBtn.setText("Update Table Data");
        updateTableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateTableBtnActionPerformed(evt);
            }
        });

        searchLabel.setText("Quick Search");

        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });

        jLabel1.setText("Stock Control");

        viewStockBtn.setText("View Current Stock");
        viewStockBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewStockBtnActionPerformed(evt);
            }
        });

        addStockBtn.setText("Add a new Stock Item");
        addStockBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStockBtnActionPerformed(evt);
            }
        });

        viewCustomersBtn.setText("View all Customers");
        viewCustomersBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewCustomersBtnActionPerformed(evt);
            }
        });

        jButton1.setText("Search Job by Name");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sideMenuPanelLayout = new javax.swing.GroupLayout(sideMenuPanel);
        sideMenuPanel.setLayout(sideMenuPanelLayout);
        sideMenuPanelLayout.setHorizontalGroup(
            sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideMenuPanelLayout.createSequentialGroup()
                .addGroup(sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sideMenuPanelLayout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(userConLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(sideMenuPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(viewJobBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(updateTableBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                            .addComponent(viewVehicleBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(sideMenuPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(sideMenuPanelLayout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(searchLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(searchField)))
                    .addGroup(sideMenuPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(viewCustomersBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sideMenuPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(51, 51, 51))
            .addGroup(sideMenuPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(viewStockBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addStockBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        sideMenuPanelLayout.setVerticalGroup(
            sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideMenuPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(userConLabel)
                .addGap(18, 18, 18)
                .addComponent(viewJobBtn)
                .addGap(10, 10, 10)
                .addComponent(updateTableBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(viewVehicleBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(viewCustomersBtn)
                .addGap(18, 18, 18)
                .addComponent(searchLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(16, 16, 16)
                .addComponent(viewStockBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addStockBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.setLayout(new java.awt.CardLayout());

        jobTable.setModel(jtm = new JobTableModel(jobData));
        jobSorter = new TableRowSorter<JobTableModel>(jtm);
        jobTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jobTableMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(jobTable);

        javax.swing.GroupLayout viewJobPanelLayout = new javax.swing.GroupLayout(viewJobPanel);
        viewJobPanel.setLayout(viewJobPanelLayout);
        viewJobPanelLayout.setHorizontalGroup(
            viewJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        viewJobPanelLayout.setVerticalGroup(
            viewJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );

        mainPanel.add(viewJobPanel, "card2");

        vehTable.setModel(vtm = new VehicleTableModel(vehData));
        vehSorter = new TableRowSorter<VehicleTableModel>(vtm);
        vehTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                vehTableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(vehTable);

        javax.swing.GroupLayout newJobPanelLayout = new javax.swing.GroupLayout(newJobPanel);
        newJobPanel.setLayout(newJobPanelLayout);
        newJobPanelLayout.setHorizontalGroup(
            newJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        newJobPanelLayout.setVerticalGroup(
            newJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );

        mainPanel.add(newJobPanel, "card3");

        newJobTitle.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        newJobTitle.setText("Creating a new Job for a [Vehicle Registeration]:");

        workReqLabel.setText("Work required:");

        busTypeLabel.setText("Business type:");

        durationLabel.setText("Duration:");

        workReqTextArea.setColumns(20);
        workReqTextArea.setRows(5);
        jScrollPane2.setViewportView(workReqTextArea);

        busTypeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Repair", "Service", "MoT" }));

        createJobBtn.setText("Create Job");
        createJobBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createJobBtnActionPerformed(evt);
            }
        });

        workReqLabel1.setText("(Add each task");

        workReqLabel2.setText("on a new line)");

        hourLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hourLabel.setText("Hours");

        minLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minLabel.setText("Minutes");

        secLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        secLabel.setText("Seconds");

        javax.swing.GroupLayout newJobInfoPanelLayout = new javax.swing.GroupLayout(newJobInfoPanel);
        newJobInfoPanel.setLayout(newJobInfoPanelLayout);
        newJobInfoPanelLayout.setHorizontalGroup(
            newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newJobInfoPanelLayout.createSequentialGroup()
                .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newJobInfoPanelLayout.createSequentialGroup()
                        .addGap(175, 175, 175)
                        .addComponent(newJobTitle))
                    .addGroup(newJobInfoPanelLayout.createSequentialGroup()
                        .addGap(195, 195, 195)
                        .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(newJobInfoPanelLayout.createSequentialGroup()
                                .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(workReqLabel)
                                    .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(workReqLabel2)
                                        .addComponent(workReqLabel1)))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(newJobInfoPanelLayout.createSequentialGroup()
                                .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(busTypeLabel)
                                    .addComponent(durationLabel))
                                .addGap(18, 18, 18)
                                .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(busTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(newJobInfoPanelLayout.createSequentialGroup()
                                        .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(hourLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(hourSpinner, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                                        .addGap(40, 40, 40)
                                        .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(minSpinner)
                                            .addComponent(minLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                                        .addGap(40, 40, 40)
                                        .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(secSpinner)
                                            .addComponent(secLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))))))))
                .addContainerGap(181, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newJobInfoPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(createJobBtn)
                .addGap(282, 282, 282))
        );
        newJobInfoPanelLayout.setVerticalGroup(
            newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newJobInfoPanelLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(newJobTitle)
                .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newJobInfoPanelLayout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(workReqLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(workReqLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(workReqLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newJobInfoPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)))
                .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(busTypeLabel)
                    .addComponent(busTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hourLabel)
                    .addComponent(minLabel)
                    .addComponent(secLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(newJobInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(durationLabel))
                .addGap(24, 24, 24)
                .addComponent(createJobBtn)
                .addGap(33, 33, 33))
        );

        mainPanel.add(newJobInfoPanel, "card4");

        stockTable.setModel(stm = new StockTableModel(stockData));
        stockSorter = new TableRowSorter<StockTableModel>(stm);
        stockTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                stockTableMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(stockTable);

        javax.swing.GroupLayout viewStockPanelLayout = new javax.swing.GroupLayout(viewStockPanel);
        viewStockPanel.setLayout(viewStockPanelLayout);
        viewStockPanelLayout.setHorizontalGroup(
            viewStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        viewStockPanelLayout.setVerticalGroup(
            viewStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );

        mainPanel.add(viewStockPanel, "card5");

        codeLabel.setText("Part Code");

        stockNameLabel.setText("Part name");

        manuLabel.setText("Maufacturer");

        vehTypeLabel.setText("Vehicle Type");

        yearLabel.setText("Year");

        stockLvlLabel.setText("Stock level");

        thresholdLabel.setText("Low Threshold");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel9.setText("Add a new Stock Item");

        addNewItemBtn.setText("Add new Item");
        addNewItemBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewItemBtnActionPerformed(evt);
            }
        });

        priceLabel.setText("Price");

        costItemLabel.setText("Cost per item");

        javax.swing.GroupLayout newStockPanelLayout = new javax.swing.GroupLayout(newStockPanel);
        newStockPanel.setLayout(newStockPanelLayout);
        newStockPanelLayout.setHorizontalGroup(
            newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newStockPanelLayout.createSequentialGroup()
                .addContainerGap(266, Short.MAX_VALUE)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addNewItemBtn)
                    .addComponent(jLabel9))
                .addGap(257, 257, 257))
            .addGroup(newStockPanelLayout.createSequentialGroup()
                .addGap(317, 317, 317)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(codeLabel)
                        .addComponent(stockNameLabel))
                    .addComponent(manuLabel)
                    .addComponent(vehTypeLabel)
                    .addComponent(yearLabel)
                    .addComponent(stockLvlLabel)
                    .addComponent(thresholdLabel)
                    .addComponent(priceLabel)
                    .addComponent(costItemLabel))
                .addGap(70, 70, 70)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(codeField)
                    .addComponent(nameField)
                    .addComponent(manuField)
                    .addComponent(vehTypeField)
                    .addComponent(yearField)
                    .addComponent(stockField)
                    .addComponent(thresoldField)
                    .addComponent(priceField)
                    .addComponent(costItemField, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        newStockPanelLayout.setVerticalGroup(
            newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newStockPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(codeLabel)
                    .addComponent(codeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stockNameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(manuLabel)
                    .addComponent(manuField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vehTypeLabel)
                    .addComponent(vehTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yearField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yearLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(priceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(priceLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(costItemLabel)
                    .addComponent(costItemField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stockField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stockLvlLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(thresholdLabel)
                    .addComponent(thresoldField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(addNewItemBtn)
                .addContainerGap(58, Short.MAX_VALUE))
        );

        mainPanel.add(newStockPanel, "card6");

        deliveryTable.setModel(sdtm = new StockDeliveryTableModel(deliveryData));
        deliverySorter = new TableRowSorter<StockDeliveryTableModel>(sdtm);
        deliveryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                deliveryTableMouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(deliveryTable);

        javax.swing.GroupLayout viewDeliveryPanelLayout = new javax.swing.GroupLayout(viewDeliveryPanel);
        viewDeliveryPanel.setLayout(viewDeliveryPanelLayout);
        viewDeliveryPanelLayout.setHorizontalGroup(
            viewDeliveryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        viewDeliveryPanelLayout.setVerticalGroup(
            viewDeliveryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );

        mainPanel.add(viewDeliveryPanel, "card7");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Enter Order information");

        jLabel3.setText("Quantity:");

        createOrder.setText("Create Order");
        createOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createOrderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout orderInfoPanelLayout = new javax.swing.GroupLayout(orderInfoPanel);
        orderInfoPanel.setLayout(orderInfoPanelLayout);
        orderInfoPanelLayout.setHorizontalGroup(
            orderInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderInfoPanelLayout.createSequentialGroup()
                .addGap(303, 303, 303)
                .addGroup(orderInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(createOrder)
                    .addGroup(orderInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2)
                        .addGroup(orderInfoPanelLayout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(56, 56, 56)
                            .addComponent(quantityField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(319, Short.MAX_VALUE))
        );
        orderInfoPanelLayout.setVerticalGroup(
            orderInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderInfoPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                .addGroup(orderInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(quantityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(64, 64, 64)
                .addComponent(createOrder)
                .addGap(178, 178, 178))
        );

        mainPanel.add(orderInfoPanel, "card8");

        codeLabel1.setText("Part Code");

        manuLabel1.setText("Maufacturer");

        yearLabel1.setText("Year");

        alterItemBtn.setText("Alter Stock Item");
        alterItemBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterItemBtnActionPerformed(evt);
            }
        });

        stockNameLabel1.setText("Part name");

        costItemLabel1.setText("Cost per item");

        thresholdLabel1.setText("Low Threshold");

        vehTypeLabel1.setText("Vehicle Type");

        priceLabel1.setText("Price");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel10.setText("Alter Stock Item");

        stockLvlLabel1.setText("Stock level");

        jLabel5.setText("Inital Stock:");

        javax.swing.GroupLayout alterStockPanelLayout = new javax.swing.GroupLayout(alterStockPanel);
        alterStockPanel.setLayout(alterStockPanelLayout);
        alterStockPanelLayout.setHorizontalGroup(
            alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alterStockPanelLayout.createSequentialGroup()
                .addGap(304, 304, 304)
                .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addComponent(manuLabel1)
                        .addGap(80, 80, 80)
                        .addComponent(manuField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addComponent(vehTypeLabel1)
                        .addGap(79, 79, 79)
                        .addComponent(vehTypeField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addComponent(yearLabel1)
                        .addGap(117, 117, 117)
                        .addComponent(yearField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addComponent(priceLabel1)
                        .addGap(116, 116, 116)
                        .addComponent(priceField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addComponent(costItemLabel1)
                        .addGap(75, 75, 75)
                        .addComponent(costItemField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addComponent(stockLvlLabel1)
                        .addGap(88, 88, 88)
                        .addComponent(stockField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10)
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(codeLabel1)
                        .addGap(90, 90, 90)
                        .addComponent(codeField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addComponent(stockNameLabel1)
                        .addGap(90, 90, 90)
                        .addComponent(nameField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(thresholdLabel1)
                            .addComponent(jLabel5))
                        .addGap(70, 70, 70)
                        .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(thresoldField1)
                            .addComponent(initalStockField, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))))
                .addContainerGap(314, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alterStockPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(alterItemBtn)
                .addGap(275, 275, 275))
        );
        alterStockPanelLayout.setVerticalGroup(
            alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alterStockPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(codeLabel1))
                    .addComponent(codeField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(stockNameLabel1))
                    .addComponent(nameField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(manuLabel1))
                    .addComponent(manuField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(vehTypeLabel1))
                    .addComponent(vehTypeField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yearLabel1)
                    .addComponent(yearField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(priceLabel1)
                    .addComponent(priceField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterStockPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(costItemLabel1))
                    .addComponent(costItemField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stockLvlLabel1)
                    .addComponent(stockField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(thresholdLabel1)
                    .addComponent(thresoldField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(alterStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(initalStockField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addComponent(alterItemBtn)
                .addGap(0, 24, Short.MAX_VALUE))
        );

        mainPanel.add(alterStockPanel, "card9");

        alterJobPanel.setPreferredSize(new java.awt.Dimension(875, 448));

        secLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        secLabel1.setText("Seconds");

        workReqLabel5.setText("on a new line)");

        busTypeLabel1.setText("Business type:");

        minLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minLabel1.setText("Minutes");

        workReqTextArea1.setColumns(20);
        workReqTextArea1.setRows(5);
        jScrollPane6.setViewportView(workReqTextArea1);

        newJobTitle1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        newJobTitle1.setText("Alter Job information:");

        alterJobBtn.setText("Alter Job ");
        alterJobBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterJobBtnActionPerformed(evt);
            }
        });

        workReqLabel4.setText("(Add each task");

        busTypeCombo1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Repair", "Service", "MoT" }));

        durationLabel1.setText("Duration:");

        workReqLabel3.setText("Work required:");

        hourLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hourLabel1.setText("Hours");

        jobStatusLabel.setText("Job Status:");

        jobStatusCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "IDLE", "IN PROGRESS", "COMPLETED" }));

        javax.swing.GroupLayout alterJobPanelLayout = new javax.swing.GroupLayout(alterJobPanel);
        alterJobPanel.setLayout(alterJobPanelLayout);
        alterJobPanelLayout.setHorizontalGroup(
            alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alterJobPanelLayout.createSequentialGroup()
                .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterJobPanelLayout.createSequentialGroup()
                        .addGap(511, 511, 511)
                        .addComponent(alterJobBtn))
                    .addGroup(alterJobPanelLayout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(alterJobPanelLayout.createSequentialGroup()
                                .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(busTypeLabel1)
                                    .addComponent(jobStatusLabel))
                                .addGap(18, 18, 18)
                                .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(busTypeCombo1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jobStatusCombo, 0, 100, Short.MAX_VALUE)))
                            .addGroup(alterJobPanelLayout.createSequentialGroup()
                                .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(workReqLabel3)
                                    .addComponent(workReqLabel4)
                                    .addGroup(alterJobPanelLayout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addComponent(workReqLabel5)))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(alterJobPanelLayout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addComponent(durationLabel1)
                        .addGap(43, 43, 43)
                        .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(alterJobPanelLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(newJobTitle1))
                            .addGroup(alterJobPanelLayout.createSequentialGroup()
                                .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(alterJobPanelLayout.createSequentialGroup()
                                        .addComponent(hourSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(40, 40, 40)
                                        .addComponent(minSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(alterJobPanelLayout.createSequentialGroup()
                                        .addComponent(hourLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(40, 40, 40)
                                        .addComponent(minLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(40, 40, 40)
                                .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(secLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(secSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(285, Short.MAX_VALUE))
        );
        alterJobPanelLayout.setVerticalGroup(
            alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alterJobPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(newJobTitle1)
                .addGap(34, 34, 34)
                .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterJobPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(workReqLabel3)
                        .addGap(6, 6, 6)
                        .addComponent(workReqLabel4)
                        .addGap(6, 6, 6)
                        .addComponent(workReqLabel5))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterJobPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(busTypeLabel1))
                    .addComponent(busTypeCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jobStatusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jobStatusLabel))
                .addGap(19, 19, 19)
                .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minLabel1)
                    .addComponent(hourLabel1)
                    .addComponent(secLabel1))
                .addGap(6, 6, 6)
                .addGroup(alterJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hourSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(durationLabel1))
                .addGap(24, 24, 24)
                .addComponent(alterJobBtn))
        );

        mainPanel.add(alterJobPanel, "card10");

        cusTable.setModel(ctm = new CustomerTableModel(cusData));
        cusTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cusTableMouseReleased(evt);
            }
        });
        jScrollPane7.setViewportView(cusTable);

        javax.swing.GroupLayout customerPanelLayout = new javax.swing.GroupLayout(customerPanel);
        customerPanel.setLayout(customerPanelLayout);
        customerPanelLayout.setHorizontalGroup(
            customerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        customerPanelLayout.setVerticalGroup(
            customerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );

        mainPanel.add(customerPanel, "card11");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sideMenuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sideMenuPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void viewVehicleBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewVehicleBtnActionPerformed
        updateData();
        panelSwitcher("newJob");
    }//GEN-LAST:event_viewVehicleBtnActionPerformed

    private void viewJobBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewJobBtnActionPerformed
       panelSwitcher("ViewJobDetails");
    }//GEN-LAST:event_viewJobBtnActionPerformed

    private void updateTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateTableBtnActionPerformed
       updateData();
    }//GEN-LAST:event_updateTableBtnActionPerformed

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
        searchTable(searchField.getText());
    }//GEN-LAST:event_searchFieldKeyReleased

    private void jobTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jobTableMouseReleased
        selectedRow = jobTable.getSelectedRow();
        selectedCol = jobTable.getSelectedColumn();
    }//GEN-LAST:event_jobTableMouseReleased

    private void workReqMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workReqMenuActionPerformed
        jobRightClickMenu(evt);
    }//GEN-LAST:event_workReqMenuActionPerformed

    private void createJobMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createJobMenuActionPerformed
        vehRightClickMenu(evt);
    }//GEN-LAST:event_createJobMenuActionPerformed

    private void vehTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vehTableMouseReleased
        selectedRow = vehTable.getSelectedRow();
        selectedCol = vehTable.getSelectedColumn();
    }//GEN-LAST:event_vehTableMouseReleased

    private void createJobBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createJobBtnActionPerformed
        controller.createJob(regNo, workReqTextArea.getText(),busTypeCombo.getSelectedItem().toString(),
                (int)hourSpinner.getValue(),(int)minSpinner.getValue(),(int)secSpinner.getValue());
        panelSwitcher("ViewJobDetails");
        updateData();
        workReqTextArea.setText("");
        busTypeCombo.setSelectedIndex(0);
        hourSpinner.setValue(0);
        minSpinner.setValue(0);
        secSpinner.setValue(0);
    }//GEN-LAST:event_createJobBtnActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void viewStockBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewStockBtnActionPerformed
        updateData();
        panelSwitcher("viewStock");
    }//GEN-LAST:event_viewStockBtnActionPerformed

    private void addNewItemBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewItemBtnActionPerformed
        Pattern numRegex = Pattern.compile("^\\+?([0-9])+$");
        Pattern priceRegex = Pattern.compile("^[0-9]*(\\.[0-9]*)?$");
        Matcher stockMatcher = numRegex.matcher(stockField.getText());
        Matcher tMatcher = numRegex.matcher(thresoldField.getText());
        Matcher priceMatcher = priceRegex.matcher(priceField.getText());
        Matcher costMatcher = priceRegex.matcher(costItemField.getText());

        String errorMsg = "";
        boolean errorFlag = false;
        if(codeField.getText().length() < 1 || nameField.getText().length() < 1 
                || manuField.getText().length() < 1 || yearField.getText().length() < 1 
                || priceField.getText().length() < 1 || stockField.getText().length() < 1
                || thresholdLabel.getText().length() < 1){
            errorMsg += "Fill in all fields!\n";
            errorFlag = true;
        }
        if(!stockMatcher.find() || !tMatcher.find()){
            errorMsg += "Stock level and threshold must contain numerical values only!\n";
            errorFlag = true;
        }
        if(!priceMatcher.find() || !costMatcher.find()){
            errorMsg += "Price must be valid - Example: 123.45 OR 123\n";
            errorFlag = true;
        }
        if(errorFlag){
            JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE); 
        }else{
            controller.createStockItem(codeField.getText(), nameField.getText(), manuField.getText(), vehTypeField.getText(), yearField.getText(),
                    Double.parseDouble(priceField.getText()), Integer.parseInt(stockField.getText()), Integer.parseInt(thresoldField.getText()), Double.parseDouble(costItemField.getText()) );
            updateData();
            codeField.setText("");
            nameField.setText("");
            manuField.setText("");
            vehTypeField.setText("");
            yearField.setText("");
            priceField.setText("");
            stockField.setText("");
            thresoldField.setText("");
            costItemField.setText("");
            panelSwitcher("viewStock");
        }
        
    }//GEN-LAST:event_addNewItemBtnActionPerformed

    private void addStockBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStockBtnActionPerformed
        panelSwitcher("addStockItem");
    }//GEN-LAST:event_addStockBtnActionPerformed

    private void viewDeliveryMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewDeliveryMenuActionPerformed
        stockRightClickMenu(evt);
    }//GEN-LAST:event_viewDeliveryMenuActionPerformed

    private void stockTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stockTableMouseReleased
        selectedRow = stockTable.getSelectedRow();
        selectedCol = stockTable.getSelectedColumn();
    }//GEN-LAST:event_stockTableMouseReleased

    private void newDeliveryMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newDeliveryMenuActionPerformed
        stockRightClickMenu(evt);
    }//GEN-LAST:event_newDeliveryMenuActionPerformed

    private void alterItemBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterItemBtnActionPerformed
        Pattern numRegex = Pattern.compile("^\\+?([0-9])+$");
        Pattern priceRegex = Pattern.compile("^[0-9]*(\\.[0-9]*)?$");
        Matcher stockMatcher = numRegex.matcher(stockField1.getText());
        Matcher tMatcher = numRegex.matcher(thresoldField1.getText());
        Matcher priceMatcher = priceRegex.matcher(priceField1.getText());
        Matcher costMatcher = priceRegex.matcher(costItemField1.getText());
        Matcher istockMatcher = numRegex.matcher(initalStockField.getText());

        String errorMsg = "";
        boolean errorFlag = false;
        if(codeField1.getText().length() < 1 || nameField1.getText().length() < 1 
                || manuField1.getText().length() < 1 || yearField1.getText().length() < 1 
                || priceField1.getText().length() < 1 || stockField1.getText().length() < 1
                || thresholdLabel1.getText().length() < 1 || initalStockField.getText().length() < 1){
            errorMsg += "Fill in all fields!\n";
            errorFlag = true;
        }
        if(!stockMatcher.find() || !tMatcher.find() || !istockMatcher.find()){
            errorMsg += "Stock level and threshold must contain numerical values only!\n";
            errorFlag = true;
        }
        if(!priceMatcher.find() || !costMatcher.find()){
            errorMsg += "Price must be valid - Example: 123.45 OR 123\n";
            errorFlag = true;
        }
        if(errorFlag){
            JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE); 
        }else{
        controller.alterStockItem(codeField1.getText(), nameField1.getText(), manuField1.getText(), vehTypeField1.getText(), yearField1.getText(),
                    Double.parseDouble(priceField1.getText()), Integer.parseInt(stockField1.getText()), Integer.parseInt(thresoldField1.getText()), 
                    Double.parseDouble(costItemField1.getText()), Integer.parseInt(initalStockField.getText()), stockCode);
                panelSwitcher("viewStock");
        }
        updateData();
    }//GEN-LAST:event_alterItemBtnActionPerformed

    private void alterStockItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterStockItemActionPerformed
        stockRightClickMenu(evt);
    }//GEN-LAST:event_alterStockItemActionPerformed

    private void alterJobBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterJobBtnActionPerformed

        controller.alterJob(jobID, workReqTextArea1.getText(), jobStatusCombo.getSelectedItem().toString(), busTypeCombo.getSelectedItem().toString(),
                (int)hourSpinner1.getValue(), (int)minSpinner1.getValue(), (int)secSpinner1.getValue());
        panelSwitcher("viewJobPanel");
    }//GEN-LAST:event_alterJobBtnActionPerformed

    private void alterJobMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterJobMenuActionPerformed
        jobRightClickMenu(evt);
    }//GEN-LAST:event_alterJobMenuActionPerformed

    private void createOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createOrderActionPerformed
        controller.orderItem(stockCode, Integer.parseInt(quantityField.getText()), costItem);
    }//GEN-LAST:event_createOrderActionPerformed

    private void recieveDeliveryMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recieveDeliveryMenuActionPerformed
        deliveryRightClickAction(evt);
    }//GEN-LAST:event_recieveDeliveryMenuActionPerformed

    private void deliveryTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deliveryTableMouseReleased
        selectedRow = deliveryTable.getSelectedRow();
        selectedCol = deliveryTable.getSelectedColumn();
    }//GEN-LAST:event_deliveryTableMouseReleased

    private void deleteDeliveryMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDeliveryMenuActionPerformed
        deliveryRightClickAction(evt);
    }//GEN-LAST:event_deleteDeliveryMenuActionPerformed

    private void viewCustomersBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewCustomersBtnActionPerformed
        panelSwitcher("viewCus");
    }//GEN-LAST:event_viewCustomersBtnActionPerformed

    private void cusTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cusTableMouseReleased
        selectedRow = cusTable.getSelectedRow();
        selectedCol = cusTable.getSelectedColumn();
    }//GEN-LAST:event_cusTableMouseReleased

    private void addVehMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addVehMenuActionPerformed
        cusRightClickMenuAction(evt);
    }//GEN-LAST:event_addVehMenuActionPerformed

    private void viewVehMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewVehMenuActionPerformed
        cusRightClickMenuAction(evt);
    }//GEN-LAST:event_viewVehMenuActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jobData = controller.searchJobByName(searchField.getText());
        searchField.setText("");
        searchTable("");
        jtm.refresh(jobData);
        panelSwitcher("ViewJobDetails");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void generateInvoiceMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateInvoiceMenuActionPerformed
        jobRightClickMenu(evt);
    }//GEN-LAST:event_generateInvoiceMenuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addNewItemBtn;
    private javax.swing.JButton addStockBtn;
    private javax.swing.JMenuItem addVehMenu;
    private javax.swing.JButton alterItemBtn;
    private javax.swing.JButton alterJobBtn;
    private javax.swing.JMenuItem alterJobMenu;
    private javax.swing.JPanel alterJobPanel;
    private javax.swing.JMenuItem alterStockItem;
    private javax.swing.JPanel alterStockPanel;
    private javax.swing.JComboBox busTypeCombo;
    private javax.swing.JComboBox busTypeCombo1;
    private javax.swing.JLabel busTypeLabel;
    private javax.swing.JLabel busTypeLabel1;
    private javax.swing.JTextField codeField;
    private javax.swing.JTextField codeField1;
    private javax.swing.JLabel codeLabel;
    private javax.swing.JLabel codeLabel1;
    private javax.swing.JTextField costItemField;
    private javax.swing.JTextField costItemField1;
    private javax.swing.JLabel costItemLabel;
    private javax.swing.JLabel costItemLabel1;
    private javax.swing.JButton createJobBtn;
    private javax.swing.JMenuItem createJobMenu;
    private javax.swing.JButton createOrder;
    private javax.swing.JPopupMenu cusRightClickMenu;
    private javax.swing.JTable cusTable;
    private CustomerTableModel ctm;
    private TableRowSorter<CustomerTableModel> sorter;
    private javax.swing.JPanel customerPanel;
    private javax.swing.JMenuItem deleteDeliveryMenu;
    private javax.swing.JPopupMenu deliveryRightClickMenu;
    private javax.swing.JMenu deliverySettingMenu;
    private javax.swing.JTable deliveryTable;
    private StockDeliveryTableModel sdtm;
    private TableRowSorter deliverySorter;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JLabel durationLabel1;
    private javax.swing.JMenuItem generateInvoiceMenu;
    private javax.swing.JLabel hourLabel;
    private javax.swing.JLabel hourLabel1;
    private javax.swing.JSpinner hourSpinner;
    private javax.swing.JSpinner hourSpinner1;
    private javax.swing.JTextField initalStockField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JPopupMenu jobRightClickMenu;
    private javax.swing.JComboBox jobStatusCombo;
    private javax.swing.JLabel jobStatusLabel;
    private javax.swing.JTable jobTable;
    private JobTableModel jtm;
    private TableRowSorter<JobTableModel> jobSorter;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField manuField;
    private javax.swing.JTextField manuField1;
    private javax.swing.JLabel manuLabel;
    private javax.swing.JLabel manuLabel1;
    private javax.swing.JLabel minLabel;
    private javax.swing.JLabel minLabel1;
    private javax.swing.JSpinner minSpinner;
    private javax.swing.JSpinner minSpinner1;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField nameField1;
    private javax.swing.JMenuItem newDeliveryMenu;
    private javax.swing.JPanel newJobInfoPanel;
    private javax.swing.JPanel newJobPanel;
    private javax.swing.JLabel newJobTitle;
    private javax.swing.JLabel newJobTitle1;
    private javax.swing.JPanel newStockPanel;
    private javax.swing.JPanel orderInfoPanel;
    private javax.swing.JTextField priceField;
    private javax.swing.JTextField priceField1;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JLabel priceLabel1;
    private javax.swing.JTextField quantityField;
    private javax.swing.JMenuItem recieveDeliveryMenu;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JLabel secLabel;
    private javax.swing.JLabel secLabel1;
    private javax.swing.JSpinner secSpinner;
    private javax.swing.JSpinner secSpinner1;
    private javax.swing.JPanel sideMenuPanel;
    private javax.swing.JTextField stockField;
    private javax.swing.JTextField stockField1;
    private javax.swing.JLabel stockLvlLabel;
    private javax.swing.JLabel stockLvlLabel1;
    private javax.swing.JLabel stockNameLabel;
    private javax.swing.JLabel stockNameLabel1;
    private javax.swing.JPopupMenu stockRightClickMenu;
    private javax.swing.JTable stockTable;
    private StockTableModel stm;
    private TableRowSorter stockSorter;
    private javax.swing.JLabel thresholdLabel;
    private javax.swing.JLabel thresholdLabel1;
    private javax.swing.JTextField thresoldField;
    private javax.swing.JTextField thresoldField1;
    private javax.swing.JButton updateTableBtn;
    private javax.swing.JLabel userConLabel;
    private javax.swing.JPopupMenu vehRightClickMenu;
    private javax.swing.JTable vehTable;
    private VehicleTableModel vtm;
    private TableRowSorter vehSorter;
    private javax.swing.JTextField vehTypeField;
    private javax.swing.JTextField vehTypeField1;
    private javax.swing.JLabel vehTypeLabel;
    private javax.swing.JLabel vehTypeLabel1;
    private javax.swing.JButton viewCustomersBtn;
    private javax.swing.JMenuItem viewDeliveryMenu;
    private javax.swing.JPanel viewDeliveryPanel;
    private javax.swing.JButton viewJobBtn;
    private javax.swing.JPanel viewJobPanel;
    private javax.swing.JButton viewStockBtn;
    private javax.swing.JPanel viewStockPanel;
    private javax.swing.JMenuItem viewVehMenu;
    private javax.swing.JButton viewVehicleBtn;
    private javax.swing.JLabel workReqLabel;
    private javax.swing.JLabel workReqLabel1;
    private javax.swing.JLabel workReqLabel2;
    private javax.swing.JLabel workReqLabel3;
    private javax.swing.JLabel workReqLabel4;
    private javax.swing.JLabel workReqLabel5;
    private javax.swing.JMenuItem workReqMenu;
    private javax.swing.JTextArea workReqTextArea;
    private javax.swing.JTextArea workReqTextArea1;
    private javax.swing.JTextField yearField;
    private javax.swing.JTextField yearField1;
    private javax.swing.JLabel yearLabel;
    private javax.swing.JLabel yearLabel1;
    // End of variables declaration//GEN-END:variables
}
