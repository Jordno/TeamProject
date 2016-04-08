/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Franchisee;

import Controllers.DatabaseConnection;
import Controllers.FranchiseeController;
import Entities.Customer;
import Entities.Staff;
import Entities.Vehicle;
import GUI.StaffTableModel;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.UIManager;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.jdatepicker.impl.*;

/**
 *
 * @author Wolf
 */
public class FranchiseeGUI extends javax.swing.JFrame {
    private ArrayList<Customer> cusData;
    private ArrayList<Vehicle> vehData;
    private ArrayList<Staff> staffData;
    private ArrayList<String> busTypeData;
    private FranchiseeController controller;
    private int selectedRow, selectedCol, cusID;
    private String vehID;
    private Timer timer;

    /**
     * Creates new form FranchiseeGUI
     */
    public FranchiseeGUI(DatabaseConnection dbc) {
        controller = new FranchiseeController(dbc);
        cusData = new ArrayList<>();
        vehData = new ArrayList<>();
        staffData = new ArrayList<>();
        busTypeData = new ArrayList<>();
        selectedRow = -1;
        selectedCol = -1;
        initTimer();
        initCusData();
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initTimer(){
        timer = new Timer(150000, new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int amount = controller.checkDueMot();
                if(amount > 0){
                    int response = JOptionPane.showConfirmDialog(null, "There are "+amount+" MoT due."
                            + "\n Do you wish to stop reminders?", "MoTs due", 
                         JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); 
                    if(response == JOptionPane.YES_OPTION) 
                        timer.stop();
                }

            }
        });  
        timer.start();
    }
    private void initCusData(){
        cusData = controller.getCusData();
        busTypeData = controller.getBusData();
    }
    
    private void initVehData(){
        vehData = controller.getVehData(cusID);
    }
    
    private void updateData(){
        initCusData(); //re-run this method to get the most updated version
        ctm.refresh(cusData); //sends the updated data to tablemodel and updates it to jtable
    }
    
    private void updateVehData(){
        initVehData();
        vtm.refresh(vehData);
    }
    
    public void rightClickMenuAction(ActionEvent event) { //right click to add and delete row and create seat
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
            vehTitleLabel.setText("Attach a Vehicle to "+cusTable.getValueAt(selectedRow,1));
        }else if(menu == viewVehMenu){
            cusID = selected;
            updateVehData();
            panelSwitcher("ViewVeh");
        }else if(menu == alterCusMenu){
            cusID = selected;
            fNameField1.setText((String)cusTable.getValueAt(selectedRow, 1));
            teleField1.setText((String)cusTable.getValueAt(selectedRow, 2)); 
            mobileField1.setText((String)cusTable.getValueAt(selectedRow, 3));
            emailField1.setText((String)cusTable.getValueAt(selectedRow, 4));
            addressField1.setText((String)cusTable.getValueAt(selectedRow, 5));
            streetField1.setText((String)cusTable.getValueAt(selectedRow, 6));
            localityField1.setText((String)cusTable.getValueAt(selectedRow, 7));
            cityField1.setText((String)cusTable.getValueAt(selectedRow, 8));
            pCodeField1.setText((String)cusTable.getValueAt(selectedRow, 9));
            noteField1.setText((String)cusTable.getValueAt(selectedRow, 10));
            panelSwitcher("alterCus");
        }else if(menu == deleteCusMenu){
            cusID = selected;
            controller.deleteCustomer(cusID);
        }
        updateData(); //update information once done.
    }
    
    public void vehRightClickMenuAction(ActionEvent event){
        JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
        vehID  = (String) vehTable.getValueAt(selectedRow, 0);
        if(menu == alterVehMenu){
            regField1.setText((String)vehTable.getValueAt(selectedRow, 0));
            engField1.setText(String.valueOf(vehTable.getValueAt(selectedRow, 1)));
            chassisField1.setText(String.valueOf(vehTable.getValueAt(selectedRow, 2)));
            colourField1.setText((String)vehTable.getValueAt(selectedRow, 3)); 
            makeField1.setText((String)vehTable.getValueAt(selectedRow, 4));
            modelField1.setText((String)vehTable.getValueAt(selectedRow, 5));
            yearField1.setText(String.valueOf(vehTable.getValueAt(selectedRow, 6)));
            motDate = (Date)vehTable.getValueAt(selectedRow, 7);
            panelSwitcher("alterVeh");
        }else if(menu == deleteVehMenu){
            controller.deleteVehicle(vehID);
            updateVehData();
        }
    }
    
    public void staffRightClickAction(ActionEvent event){
        JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
        int selected  = (int) staffJTable.getValueAt(selectedRow, 0);
        if(menu == selectStaffMenu){
            controller.generateAvgPriceReport(selected,(String)staffJTable.getValueAt(selectedRow, 3) ,
                    (String)staffJTable.getValueAt(selectedRow, 4));
        }
    
    
    }
    
    public void panelSwitcher(String type){
        mainPanel.removeAll();
        //Remove the search
        searchLabel.setVisible(false);
        searchField.setVisible(false);
        switch(type){
            case "ViewCusDetails":
                mainPanel.add(viewCusPanel);
                searchLabel.setVisible(true);
                searchField.setVisible(true);
                break;
            case "AddCus":
                mainPanel.add(addCusPanel);
                break;
            case "AddVeh":
                mainPanel.add(addVehiclePanel);
                break;
            case "ViewVeh":
                mainPanel.add(viewVehPanel);
                break;
            case "alterCus":
                mainPanel.add(alterCusPanel);
                break;
            case "alterVeh":
                mainPanel.add(alterVehDetails);
                break;
            case "viewMech":
                mainPanel.add(viewMechPanel);
                break;
            case "vehBooked":
                mainPanel.add(vehicleBookPanel);
        }
        mainPanel.repaint();
        mainPanel.revalidate();
    }
    
    private void searchTable(String filter){
        cusTable.setRowSorter(sorter);
        //(?i) is used to ignore case in the search term
        sorter.setRowFilter(RowFilter.regexFilter("(?i)"+filter));
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rightClickMenu = new javax.swing.JPopupMenu();
        alterCusMenu = new javax.swing.JMenuItem();
        deleteCusMenu = new javax.swing.JMenuItem();
        addVehMenu = new javax.swing.JMenuItem();
        viewVehMenu = new javax.swing.JMenuItem();
        vehRightClickMenu = new javax.swing.JPopupMenu();
        alterVehMenu = new javax.swing.JMenuItem();
        deleteVehMenu = new javax.swing.JMenuItem();
        staffRightClickMenu = new javax.swing.JPopupMenu();
        selectStaffMenu = new javax.swing.JMenuItem();
        sideMenuPanel = new javax.swing.JPanel();
        newCusBtn = new javax.swing.JButton();
        viewCusBtn = new javax.swing.JButton();
        userConLabel = new javax.swing.JLabel();
        updateTableBtn = new javax.swing.JButton();
        searchLabel = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        mainPanel = new javax.swing.JPanel();
        viewCusPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        cusTable = new javax.swing.JTable();
        addCusPanel = new javax.swing.JPanel();
        fNameLabel = new javax.swing.JLabel();
        fNameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        teleLabel = new javax.swing.JLabel();
        emLabel = new javax.swing.JLabel();
        addressLabel = new javax.swing.JLabel();
        cityLabel = new javax.swing.JLabel();
        pCodeLabel = new javax.swing.JLabel();
        addressField = new javax.swing.JTextField();
        cityField = new javax.swing.JTextField();
        pCodeField = new javax.swing.JTextField();
        emailField = new javax.swing.JTextField();
        teleField = new javax.swing.JTextField();
        createCusBtn = new javax.swing.JButton();
        mobileLabel = new javax.swing.JLabel();
        streetLabel = new javax.swing.JLabel();
        localityLabel = new javax.swing.JLabel();
        noteLabel = new javax.swing.JLabel();
        streetField = new javax.swing.JTextField();
        localityField = new javax.swing.JTextField();
        mobileField = new javax.swing.JTextField();
        noteField = new javax.swing.JTextField();
        addVehiclePanel = new javax.swing.JPanel();
        vehTitleLabel = new javax.swing.JLabel();
        regLabel = new javax.swing.JLabel();
        engLabel = new javax.swing.JLabel();
        colourLabel = new javax.swing.JLabel();
        makeLabel = new javax.swing.JLabel();
        modelLabel = new javax.swing.JLabel();
        regField = new javax.swing.JTextField();
        engField = new javax.swing.JTextField();
        colourField = new javax.swing.JTextField();
        makeField = new javax.swing.JTextField();
        modelField = new javax.swing.JTextField();
        chassisLabel = new javax.swing.JLabel();
        motLabel = new javax.swing.JLabel();
        chassisField = new javax.swing.JTextField();
        datePickerBtn = new javax.swing.JButton();
        attachVehBtn = new javax.swing.JButton();
        yearLabel = new javax.swing.JLabel();
        yearField = new javax.swing.JTextField();
        viewVehPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        vehTable = new javax.swing.JTable();
        alterCusPanel = new javax.swing.JPanel();
        fNameLabel1 = new javax.swing.JLabel();
        teleField1 = new javax.swing.JTextField();
        emailField1 = new javax.swing.JTextField();
        addressField1 = new javax.swing.JTextField();
        localityField1 = new javax.swing.JTextField();
        alterCusBtn = new javax.swing.JButton();
        streetField1 = new javax.swing.JTextField();
        addressLabel1 = new javax.swing.JLabel();
        cityLabel1 = new javax.swing.JLabel();
        fNameField1 = new javax.swing.JTextField();
        teleLabel1 = new javax.swing.JLabel();
        cityField1 = new javax.swing.JTextField();
        mobileField1 = new javax.swing.JTextField();
        noteField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        localityLabel1 = new javax.swing.JLabel();
        pCodeLabel1 = new javax.swing.JLabel();
        streetLabel1 = new javax.swing.JLabel();
        mobileLabel1 = new javax.swing.JLabel();
        emLabel1 = new javax.swing.JLabel();
        pCodeField1 = new javax.swing.JTextField();
        noteLabel1 = new javax.swing.JLabel();
        alterVehDetails = new javax.swing.JPanel();
        vehTitleLabel1 = new javax.swing.JLabel();
        chassisField1 = new javax.swing.JTextField();
        engLabel1 = new javax.swing.JLabel();
        colourField1 = new javax.swing.JTextField();
        chassisLabel1 = new javax.swing.JLabel();
        alterVehBtn = new javax.swing.JButton();
        datePickerBtn1 = new javax.swing.JButton();
        modelField1 = new javax.swing.JTextField();
        colourLabel1 = new javax.swing.JLabel();
        modelLabel1 = new javax.swing.JLabel();
        engField1 = new javax.swing.JTextField();
        makeLabel1 = new javax.swing.JLabel();
        motLabel1 = new javax.swing.JLabel();
        makeField1 = new javax.swing.JTextField();
        regField1 = new javax.swing.JTextField();
        yearField1 = new javax.swing.JTextField();
        regLabel1 = new javax.swing.JLabel();
        yearLabel1 = new javax.swing.JLabel();
        viewMechPanel = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        staffJTable = new javax.swing.JTable();
        vehicleBookPanel = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        sparePartReportMenu = new javax.swing.JMenuItem();
        avgPriceReportMenu = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        alterCusMenu.setText("Alter Customer Details");
        alterCusMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterCusMenuActionPerformed(evt);
            }
        });
        rightClickMenu.add(alterCusMenu);

        deleteCusMenu.setText("Delete customer");
        deleteCusMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCusMenuActionPerformed(evt);
            }
        });
        rightClickMenu.add(deleteCusMenu);

        addVehMenu.setText("Add Vehicle to customer");
        addVehMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addVehMenuActionPerformed(evt);
            }
        });
        rightClickMenu.add(addVehMenu);

        viewVehMenu.setText("View Assosicated Vehicles");
        viewVehMenu.setToolTipText("");
        viewVehMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewVehMenuActionPerformed(evt);
            }
        });
        rightClickMenu.add(viewVehMenu);

        cusTable.setComponentPopupMenu(rightClickMenu);

        alterVehMenu.setText("Alter Vehicle Details");
        alterVehMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterVehMenuActionPerformed(evt);
            }
        });
        vehRightClickMenu.add(alterVehMenu);

        deleteVehMenu.setText("Delete selected Vehicle");
        deleteVehMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteVehMenuActionPerformed(evt);
            }
        });
        vehRightClickMenu.add(deleteVehMenu);

        vehTable.setComponentPopupMenu(vehRightClickMenu);

        selectStaffMenu.setText("Generate Report on this Staff");
        selectStaffMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectStaffMenuActionPerformed(evt);
            }
        });
        staffRightClickMenu.add(selectStaffMenu);

        staffJTable.setComponentPopupMenu(staffRightClickMenu);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GARTIS - Franchisee");

        sideMenuPanel.setBackground(new java.awt.Color(255, 255, 255));
        sideMenuPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        newCusBtn.setText("Add new customer");
        newCusBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCusBtnActionPerformed(evt);
            }
        });

        viewCusBtn.setText("View Customer Details");
        viewCusBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewCusBtnActionPerformed(evt);
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

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout sideMenuPanelLayout = new javax.swing.GroupLayout(sideMenuPanel);
        sideMenuPanel.setLayout(sideMenuPanelLayout);
        sideMenuPanelLayout.setHorizontalGroup(
            sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideMenuPanelLayout.createSequentialGroup()
                .addGroup(sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sideMenuPanelLayout.createSequentialGroup()
                        .addGroup(sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(sideMenuPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(viewCusBtn))
                            .addGroup(sideMenuPanelLayout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addComponent(userConLabel))
                            .addGroup(sideMenuPanelLayout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addComponent(searchLabel)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(sideMenuPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(searchField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(updateTableBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newCusBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        sideMenuPanelLayout.setVerticalGroup(
            sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideMenuPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(userConLabel)
                .addGap(18, 18, 18)
                .addComponent(viewCusBtn)
                .addGap(10, 10, 10)
                .addComponent(updateTableBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(newCusBtn)
                .addGap(18, 18, 18)
                .addComponent(searchLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.setLayout(new java.awt.CardLayout());

        cusTable.setModel(ctm = new CustomerTableModel(cusData));
        sorter = new TableRowSorter<CustomerTableModel>(ctm);
        cusTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cusTableMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(cusTable);

        javax.swing.GroupLayout viewCusPanelLayout = new javax.swing.GroupLayout(viewCusPanel);
        viewCusPanel.setLayout(viewCusPanelLayout);
        viewCusPanelLayout.setHorizontalGroup(
            viewCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        viewCusPanelLayout.setVerticalGroup(
            viewCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );

        mainPanel.add(viewCusPanel, "card2");

        fNameLabel.setText("Name:");

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 36)); // NOI18N
        jLabel2.setText("Create a new customer account");

        teleLabel.setText("Telephone:");

        emLabel.setText("Email:");

        addressLabel.setText("Address:");

        cityLabel.setText("City:");

        pCodeLabel.setText("Post code:");

        createCusBtn.setText("Create");
        createCusBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createCusBtnActionPerformed(evt);
            }
        });

        mobileLabel.setText("Mobile:");

        streetLabel.setText("Street:");

        localityLabel.setText("Locality:");

        noteLabel.setText("Other Notes:");

        javax.swing.GroupLayout addCusPanelLayout = new javax.swing.GroupLayout(addCusPanel);
        addCusPanel.setLayout(addCusPanelLayout);
        addCusPanelLayout.setHorizontalGroup(
            addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addCusPanelLayout.createSequentialGroup()
                .addGap(148, 148, 148)
                .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addCusPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(241, Short.MAX_VALUE))
                    .addGroup(addCusPanelLayout.createSequentialGroup()
                        .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, addCusPanelLayout.createSequentialGroup()
                                .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(addressLabel)
                                    .addComponent(cityLabel)
                                    .addComponent(fNameLabel))
                                .addGap(41, 41, 41)
                                .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(cityField, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                    .addComponent(addressField)
                                    .addComponent(fNameField, javax.swing.GroupLayout.Alignment.LEADING)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, addCusPanelLayout.createSequentialGroup()
                                .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(teleLabel)
                                    .addComponent(pCodeLabel)
                                    .addComponent(emLabel))
                                .addGap(30, 30, 30)
                                .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(emailField)
                                    .addComponent(pCodeField)
                                    .addComponent(teleField, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))))
                        .addGap(30, 30, 30)
                        .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(localityLabel)
                                .addComponent(streetLabel, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(mobileLabel)
                            .addComponent(noteLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(streetField, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(localityField)
                            .addComponent(mobileField)
                            .addComponent(noteField))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addCusPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(createCusBtn)
                .addGap(229, 229, 229))
        );
        addCusPanelLayout.setVerticalGroup(
            addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addCusPanelLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel2)
                .addGap(33, 33, 33)
                .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fNameLabel)
                    .addComponent(fNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addressLabel)
                    .addComponent(addressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(streetLabel)
                    .addComponent(streetField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cityLabel)
                    .addComponent(cityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(localityLabel)
                    .addComponent(localityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pCodeLabel)
                    .addComponent(pCodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(teleLabel)
                    .addComponent(teleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mobileLabel)
                    .addComponent(mobileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(addCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emLabel)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(noteLabel)
                    .addComponent(noteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addComponent(createCusBtn)
                .addContainerGap())
        );

        mainPanel.add(addCusPanel, "card2");

        addVehiclePanel.setPreferredSize(new java.awt.Dimension(875, 454));

        vehTitleLabel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        vehTitleLabel.setText("Attach Vehicle to Customer");

        regLabel.setText("Registation Number:");

        engLabel.setText("Engine Serial Number:");

        colourLabel.setText("Colour:");

        makeLabel.setText("Make:");

        modelLabel.setText("Model:");

        makeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeFieldActionPerformed(evt);
            }
        });

        chassisLabel.setText("Chassis Number:");

        motLabel.setText("Last MoT Check:");

        datePickerBtn.setText(".");

        attachVehBtn.setText("Attach Vehicle");
        attachVehBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attachVehBtnActionPerformed(evt);
            }
        });

        yearLabel.setText("Year:");

        javax.swing.GroupLayout addVehiclePanelLayout = new javax.swing.GroupLayout(addVehiclePanel);
        addVehiclePanel.setLayout(addVehiclePanelLayout);
        addVehiclePanelLayout.setHorizontalGroup(
            addVehiclePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addVehiclePanelLayout.createSequentialGroup()
                .addGap(190, 190, 190)
                .addComponent(vehTitleLabel))
            .addGroup(addVehiclePanelLayout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addComponent(regLabel)
                .addGap(22, 22, 22)
                .addComponent(regField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(addVehiclePanelLayout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addComponent(engLabel)
                .addGap(15, 15, 15)
                .addComponent(engField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(addVehiclePanelLayout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addComponent(colourLabel)
                .addGap(85, 85, 85)
                .addComponent(colourField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(addVehiclePanelLayout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addComponent(makeLabel)
                .addGap(91, 91, 91)
                .addComponent(makeField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(addVehiclePanelLayout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addComponent(modelLabel)
                .addGap(88, 88, 88)
                .addComponent(modelField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(addVehiclePanelLayout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addComponent(chassisLabel)
                .addGap(40, 40, 40)
                .addComponent(chassisField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(addVehiclePanelLayout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addComponent(yearLabel)
                .addGap(94, 94, 94)
                .addComponent(yearField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(addVehiclePanelLayout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addComponent(motLabel)
                .addGap(41, 41, 41)
                .addComponent(datePickerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(addVehiclePanelLayout.createSequentialGroup()
                .addGap(511, 511, 511)
                .addComponent(attachVehBtn))
        );
        addVehiclePanelLayout.setVerticalGroup(
            addVehiclePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addVehiclePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(vehTitleLabel)
                .addGap(36, 36, 36)
                .addGroup(addVehiclePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regLabel)
                    .addComponent(regField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(addVehiclePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(engLabel)
                    .addComponent(engField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(addVehiclePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(colourLabel)
                    .addComponent(colourField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(addVehiclePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(makeLabel)
                    .addComponent(makeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(addVehiclePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addVehiclePanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(modelLabel))
                    .addComponent(modelField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(addVehiclePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chassisLabel)
                    .addComponent(chassisField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(addVehiclePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yearLabel)
                    .addComponent(yearField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(addVehiclePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(motLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(datePickerBtn))
                .addGap(24, 24, 24)
                .addComponent(attachVehBtn))
        );

        datePickerBtn.setEnabled(false);

        mainPanel.add(addVehiclePanel, "card4");
        SqlDateModel model = new SqlDateModel(); //SQL date model used by JDatePicker
        // A list of properties needed by JDatePicker to display correctly
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        // Uses a custom format class as it must follow standard date for sql formatting
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new GUI.Franchisee.DateSQLFormatter());
        datePicker.setShowYearButtons(true);
        datePickerBtn.add(datePicker); //add to button easier to modify and change around
        datePicker.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                motDate = (Date) datePicker.getModel().getValue();
                System.out.println(motDate);
            }
        });

        vehTable.setModel(vtm = new VehicleTableModel(vehData));
        vehTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                vehTableMouseReleased(evt);
            }
        });
        vehTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                vehTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(vehTable);

        javax.swing.GroupLayout viewVehPanelLayout = new javax.swing.GroupLayout(viewVehPanel);
        viewVehPanel.setLayout(viewVehPanelLayout);
        viewVehPanelLayout.setHorizontalGroup(
            viewVehPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        viewVehPanelLayout.setVerticalGroup(
            viewVehPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );

        mainPanel.add(viewVehPanel, "card5");

        fNameLabel1.setText("Name:");

        alterCusBtn.setText("Alter");
        alterCusBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterCusBtnActionPerformed(evt);
            }
        });

        addressLabel1.setText("Address:");

        cityLabel1.setText("City:");

        teleLabel1.setText("Telephone:");

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 36)); // NOI18N
        jLabel3.setText("Alter customer account");

        localityLabel1.setText("Locality:");

        pCodeLabel1.setText("Post code:");

        streetLabel1.setText("Street:");

        mobileLabel1.setText("Mobile:");

        emLabel1.setText("Email:");

        noteLabel1.setText("Other Notes:");

        javax.swing.GroupLayout alterCusPanelLayout = new javax.swing.GroupLayout(alterCusPanel);
        alterCusPanel.setLayout(alterCusPanelLayout);
        alterCusPanelLayout.setHorizontalGroup(
            alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alterCusPanelLayout.createSequentialGroup()
                .addGap(186, 186, 186)
                .addGroup(alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addComponent(fNameLabel1)
                        .addGap(53, 53, 53)
                        .addComponent(fNameField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addComponent(addressLabel1)
                        .addGap(41, 41, 41)
                        .addComponent(addressField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(streetLabel1)
                        .addGap(48, 48, 48)
                        .addComponent(streetField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addComponent(cityLabel1)
                        .addGap(61, 61, 61)
                        .addComponent(cityField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(localityLabel1)
                        .addGap(42, 42, 42)
                        .addComponent(localityField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addComponent(pCodeLabel1)
                        .addGap(33, 33, 33)
                        .addComponent(pCodeField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addComponent(teleLabel1)
                        .addGap(30, 30, 30)
                        .addComponent(teleField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(mobileLabel1)
                        .addGap(48, 48, 48)
                        .addComponent(mobileField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addComponent(emLabel1)
                        .addGap(56, 56, 56)
                        .addComponent(emailField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(noteLabel1)
                        .addGap(19, 19, 19)
                        .addComponent(noteField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addGap(433, 433, 433)
                        .addComponent(alterCusBtn)))
                .addContainerGap(193, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alterCusPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(239, 239, 239))
        );
        alterCusPanelLayout.setVerticalGroup(
            alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alterCusPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel3)
                .addGap(35, 35, 35)
                .addGroup(alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(fNameLabel1))
                    .addComponent(fNameField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addressField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(streetField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addressLabel1)
                            .addComponent(streetLabel1))))
                .addGap(18, 18, 18)
                .addGroup(alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cityField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(localityField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cityLabel1)
                            .addComponent(localityLabel1))))
                .addGap(18, 18, 18)
                .addGroup(alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(pCodeLabel1))
                    .addComponent(pCodeField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(teleField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mobileField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(teleLabel1)
                            .addComponent(mobileLabel1))))
                .addGap(18, 18, 18)
                .addGroup(alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(emailField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(noteField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(alterCusPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(alterCusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emLabel1)
                            .addComponent(noteLabel1))))
                .addGap(56, 56, 56)
                .addComponent(alterCusBtn)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        mainPanel.add(alterCusPanel, "card6");

        alterVehDetails.setPreferredSize(new java.awt.Dimension(875, 454));

        vehTitleLabel1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        vehTitleLabel1.setText("Alter Vehicle Details");

        engLabel1.setText("Engine Serial Number:");

        chassisLabel1.setText("Chassis Number:");

        alterVehBtn.setText("Alter Details");
        alterVehBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterVehBtnActionPerformed(evt);
            }
        });

        datePickerBtn1.setText(".");

        colourLabel1.setText("Colour:");

        modelLabel1.setText("Model:");

        makeLabel1.setText("Make:");

        motLabel1.setText("Last MoT Check:");

        makeField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeField1ActionPerformed(evt);
            }
        });

        regLabel1.setText("Registation Number:");

        yearLabel1.setText("Year:");

        javax.swing.GroupLayout alterVehDetailsLayout = new javax.swing.GroupLayout(alterVehDetails);
        alterVehDetails.setLayout(alterVehDetailsLayout);
        alterVehDetailsLayout.setHorizontalGroup(
            alterVehDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alterVehDetailsLayout.createSequentialGroup()
                .addGap(263, 263, 263)
                .addComponent(vehTitleLabel1))
            .addGroup(alterVehDetailsLayout.createSequentialGroup()
                .addGap(272, 272, 272)
                .addComponent(regLabel1)
                .addGap(22, 22, 22)
                .addComponent(regField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(alterVehDetailsLayout.createSequentialGroup()
                .addGap(272, 272, 272)
                .addComponent(engLabel1)
                .addGap(15, 15, 15)
                .addComponent(engField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(alterVehDetailsLayout.createSequentialGroup()
                .addGap(272, 272, 272)
                .addComponent(colourLabel1)
                .addGap(85, 85, 85)
                .addComponent(colourField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(alterVehDetailsLayout.createSequentialGroup()
                .addGap(272, 272, 272)
                .addComponent(makeLabel1)
                .addGap(91, 91, 91)
                .addComponent(makeField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(alterVehDetailsLayout.createSequentialGroup()
                .addGap(272, 272, 272)
                .addComponent(modelLabel1)
                .addGap(88, 88, 88)
                .addComponent(modelField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(alterVehDetailsLayout.createSequentialGroup()
                .addGap(272, 272, 272)
                .addComponent(chassisLabel1)
                .addGap(40, 40, 40)
                .addComponent(chassisField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(alterVehDetailsLayout.createSequentialGroup()
                .addGap(272, 272, 272)
                .addComponent(yearLabel1)
                .addGap(94, 94, 94)
                .addComponent(yearField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(alterVehDetailsLayout.createSequentialGroup()
                .addGap(272, 272, 272)
                .addComponent(motLabel1)
                .addGap(41, 41, 41)
                .addComponent(datePickerBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(alterVehDetailsLayout.createSequentialGroup()
                .addGap(543, 543, 543)
                .addComponent(alterVehBtn))
        );
        alterVehDetailsLayout.setVerticalGroup(
            alterVehDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alterVehDetailsLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(vehTitleLabel1)
                .addGap(37, 37, 37)
                .addGroup(alterVehDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regLabel1)
                    .addComponent(regField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(alterVehDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(engLabel1)
                    .addComponent(engField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(alterVehDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(colourLabel1)
                    .addComponent(colourField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(alterVehDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterVehDetailsLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(makeLabel1))
                    .addComponent(makeField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(alterVehDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterVehDetailsLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(modelLabel1))
                    .addComponent(modelField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(alterVehDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterVehDetailsLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(chassisLabel1))
                    .addComponent(chassisField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(alterVehDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yearLabel1)
                    .addComponent(yearField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(alterVehDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(motLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(alterVehDetailsLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(datePickerBtn1)))
                .addGap(24, 24, 24)
                .addComponent(alterVehBtn))
        );

        datePickerBtn1.setEnabled(false);

        mainPanel.add(alterVehDetails, "card7");
        SqlDateModel model1 = new SqlDateModel(); //SQL date model used by JDatePicker
        // A list of properties needed by JDatePicker to display correctly
        Properties p1 = new Properties();
        p1.put("text.today", "Today");
        p1.put("text.month", "Month");
        p1.put("text.year", "Year");
        JDatePanelImpl datePanel1 = new JDatePanelImpl(model1, p1);
        // Uses a custom format class as it must follow standard date for sql formatting
        JDatePickerImpl datePicker1 = new JDatePickerImpl(datePanel1,new GUI.Franchisee.DateSQLFormatter());
        datePicker1.setShowYearButtons(true);
        datePickerBtn1.add(datePicker1); //add to button easier to modify and change around
        datePicker1.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterDate = (Date) datePicker1.getModel().getValue();
            }
        });

        staffJTable.setModel(stm = new StaffTableModel(staffData));
        staffJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                staffJTableMouseReleased(evt);
            }
        });
        jScrollPane.setViewportView(staffJTable);

        javax.swing.GroupLayout viewMechPanelLayout = new javax.swing.GroupLayout(viewMechPanel);
        viewMechPanel.setLayout(viewMechPanelLayout);
        viewMechPanelLayout.setHorizontalGroup(
            viewMechPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        viewMechPanelLayout.setVerticalGroup(
            viewMechPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );

        mainPanel.add(viewMechPanel, "card8");

        Object[] keys = busTypeData.toArray();
        jComboBox1.setModel(new DefaultComboBoxModel(keys));

        javax.swing.GroupLayout vehicleBookPanelLayout = new javax.swing.GroupLayout(vehicleBookPanel);
        vehicleBookPanel.setLayout(vehicleBookPanelLayout);
        vehicleBookPanelLayout.setHorizontalGroup(
            vehicleBookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vehicleBookPanelLayout.createSequentialGroup()
                .addGap(375, 375, 375)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(472, Short.MAX_VALUE))
        );
        vehicleBookPanelLayout.setVerticalGroup(
            vehicleBookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vehicleBookPanelLayout.createSequentialGroup()
                .addGap(118, 118, 118)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(316, Short.MAX_VALUE))
        );

        mainPanel.add(vehicleBookPanel, "card9");

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Reports");

        sparePartReportMenu.setText("Generate Spareparts");
        sparePartReportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sparePartReportMenuActionPerformed(evt);
            }
        });
        jMenu2.add(sparePartReportMenu);

        avgPriceReportMenu.setText("Generate Average Job price per Mechanic ");
        avgPriceReportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                avgPriceReportMenuActionPerformed(evt);
            }
        });
        jMenu2.add(avgPriceReportMenu);

        jMenuItem1.setText("jMenuItem1");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

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

    private void newCusBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCusBtnActionPerformed
        panelSwitcher("AddCus");
    }//GEN-LAST:event_newCusBtnActionPerformed

    private void createCusBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createCusBtnActionPerformed
        Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Pattern pCodeRegex = Pattern.compile("^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$");
        Pattern wordRegex = Pattern.compile("^[a-zA-Z]+$");
        Pattern teleRegex = Pattern.compile("^\\+?([0-9 ])+$");
        Matcher emailMatcher = emailRegex.matcher(emailField.getText());
        Matcher pCodeMatcher = pCodeRegex.matcher(pCodeField.getText());
        Matcher wordMatcher = wordRegex.matcher(cityField.getText());
        Matcher teleMatcher = teleRegex.matcher(teleField.getText());
        String errorMsg = "";
        boolean errorFlag = false;
        if(fNameField.getText().length() < 3){
            errorMsg += "Name field must be at least 3 characters long!\n";
            errorFlag = true;
        }
        if(addressField.getText().length() < 5){
            errorMsg += "Address field must be at least 5 characters long!\n";
            errorFlag = true;
        }
        if(!wordMatcher.find() || cityField.getText().length() < 3){
            errorMsg += "City must be at least 3 characters long and contain alphabetic characters only!\n";
            errorFlag = true;
        }
        if(!pCodeMatcher.find()){
            errorMsg += "Post code must be valid! Example: EC1V 0HB\n";
            errorFlag = true;
        }
        if(emailField.getText().length() > 0){
            if(!emailMatcher.find()){
                errorMsg += "Email must be valid! Example: John@example.com\n"; 
                errorFlag = true;
            }
        }
        if(!teleMatcher.find() || teleField.getText().length() < 6){
            errorMsg += "Telephone number must contain at minimum 6 numerical values! \n"; 
            errorFlag = true;
        }
        if(errorFlag){
            JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE); 
        }else{
            controller.createCustomer(fNameField.getText(), teleField.getText(), 
                mobileField.getText(), emailField.getText(), addressField.getText(),
                streetField.getText(), localityField.getText(), cityField.getText(),
                pCodeField.getText(), noteField.getText(), "Account Holder");
            
            //reset fields
            fNameField.setText("");
            teleField.setText(""); 
            mobileField.setText("");
            emailField.setText("");
            addressField.setText("");
            streetField.setText("");
            localityField.setText("");
            cityField.setText("");
            pCodeField.setText("");
            noteField.setText("");
        }
        updateData();
    }//GEN-LAST:event_createCusBtnActionPerformed

    private void viewCusBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewCusBtnActionPerformed
        panelSwitcher("ViewCusDetails");
    }//GEN-LAST:event_viewCusBtnActionPerformed

    private void updateTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateTableBtnActionPerformed
        updateData();
    }//GEN-LAST:event_updateTableBtnActionPerformed

    private void cusTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cusTableMouseReleased
        selectedRow = cusTable.getSelectedRow();
        selectedCol = cusTable.getSelectedColumn();
    }//GEN-LAST:event_cusTableMouseReleased

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
        searchTable(searchField.getText());
    }//GEN-LAST:event_searchFieldKeyReleased

    private void makeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeFieldActionPerformed

    }//GEN-LAST:event_makeFieldActionPerformed

    private void addVehMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addVehMenuActionPerformed
        rightClickMenuAction(evt);
    }//GEN-LAST:event_addVehMenuActionPerformed

    private void attachVehBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attachVehBtnActionPerformed
        String errorMsg = "";
        boolean errorFlag = false;
        if(regField.getText().length() < 1 || engField.getText().length() < 1 
                || makeField.getText().length() < 1 || modelField.getText().length() < 1
                || colourField.getText().length() < 1 || chassisField.getText().length() < 1){
            errorMsg += "You cannot have empty fields!\n";
            errorFlag = true;
        }
        if(motDate == null){
            errorMsg += "Please select a valid MoT date!\n";
            errorFlag = true;
        }
        if(errorFlag){
            JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE); 
        }else{
            controller.addVehicle(regField.getText(), cusID, 
                Integer.parseInt(engField.getText()), colourField.getText(), makeField.getText(),
                modelField.getText(), Integer.parseInt(chassisField.getText()), Integer.parseInt(yearField.getText()), motDate);
            
            //reset fields
            regField.setText("");
            engField.setText("");
            colourField.setText(""); 
            makeField.setText("");
            modelField.setText("");
            chassisField.setText("");
            yearField.setText("");
            updateVehData();
        }
        panelSwitcher("ViewCusDetails");
    }//GEN-LAST:event_attachVehBtnActionPerformed

    private void viewVehMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewVehMenuActionPerformed
        rightClickMenuAction(evt);
    }//GEN-LAST:event_viewVehMenuActionPerformed

    private void alterCusBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterCusBtnActionPerformed
        Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Pattern pCodeRegex = Pattern.compile("^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$");
        Pattern wordRegex = Pattern.compile("^[a-zA-Z]+$");
        Pattern teleRegex = Pattern.compile("^\\+?([0-9 ])+$");
        Matcher emailMatcher = emailRegex.matcher(emailField1.getText());
        Matcher pCodeMatcher = pCodeRegex.matcher(pCodeField1.getText());
        Matcher wordMatcher = wordRegex.matcher(cityField1.getText());
        Matcher teleMatcher = teleRegex.matcher(teleField1.getText());
        String errorMsg = "";
        boolean errorFlag = false;
        if(fNameField1.getText().length() < 3){
            errorMsg += "Name field must be at least 3 characters long!\n";
            errorFlag = true;
        }
        if(addressField1.getText().length() < 5){
            errorMsg += "Address field must be at least 5 characters long!\n";
            errorFlag = true;
        }
        if(!wordMatcher.find() || cityField1.getText().length() < 3){
            errorMsg += "City must be at least 3 characters long and contain alphabetic characters only!\n";
            errorFlag = true;
        }
        if(!pCodeMatcher.find()){
            errorMsg += "Post code must be valid! Example: EC1V 0HB\n";
            errorFlag = true;
        }
        if(emailField1.getText().length() > 0){
            if(!emailMatcher.find()){
                errorMsg += "Email must be valid! Example: John@example.com\n"; 
                errorFlag = true;
            }
        }
        if(!teleMatcher.find() || teleField1.getText().length() < 6){
            errorMsg += "Telephone number must contain at minimum 6 numerical values! \n"; 
            errorFlag = true;
        }
        if(errorFlag){
            JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE); 
        }else{
            controller.alterCustomer(fNameField1.getText(), teleField1.getText(), 
                mobileField1.getText(), emailField1.getText(), addressField1.getText(),
                streetField1.getText(), localityField1.getText(), cityField1.getText(),
                pCodeField1.getText(), noteField1.getText(), "Account Holder", cusID);
                
        }
        updateData();
        panelSwitcher("ViewCusDetails");
    }//GEN-LAST:event_alterCusBtnActionPerformed

    private void alterCusMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterCusMenuActionPerformed
        rightClickMenuAction(evt);
    }//GEN-LAST:event_alterCusMenuActionPerformed

    private void alterVehBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterVehBtnActionPerformed
        String errorMsg = "";
        boolean errorFlag = false;
        if(regField1.getText().length() < 1 || engField1.getText().length() < 1 
                || makeField1.getText().length() < 1 || modelField1.getText().length() < 1
                || colourField1.getText().length() < 1 || chassisField1.getText().length() < 1){
            errorMsg += "You cannot have empty fields!\n";
            errorFlag = true;
        }
        if(alterDate == null){
            errorMsg += "Please select a valid MoT date!\n";
            errorFlag = true;
        }
        if(errorFlag){
            JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE); 
        }else{
            controller.alterVehicle(regField1.getText(), vehID, 
                Integer.parseInt(engField1.getText()), colourField1.getText(), makeField1.getText(),
                modelField1.getText(), Integer.parseInt(chassisField1.getText()), Integer.parseInt(yearField1.getText()), alterDate);
            //reset fields
            regField.setText("");
            engField.setText("");
            colourField.setText(""); 
            makeField.setText("");
            modelField.setText("");
            chassisField.setText("");
            yearField.setText("");
            updateVehData();
        }
        panelSwitcher("ViewVeh");
    }//GEN-LAST:event_alterVehBtnActionPerformed

    private void makeField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_makeField1ActionPerformed

    private void alterVehMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterVehMenuActionPerformed
        vehRightClickMenuAction(evt);
    }//GEN-LAST:event_alterVehMenuActionPerformed

    private void deleteVehMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteVehMenuActionPerformed
        vehRightClickMenuAction(evt);
    }//GEN-LAST:event_deleteVehMenuActionPerformed

    private void vehTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_vehTableKeyReleased

    }//GEN-LAST:event_vehTableKeyReleased

    private void vehTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vehTableMouseReleased
        selectedRow = vehTable.getSelectedRow();
        selectedCol = vehTable.getSelectedColumn();
    }//GEN-LAST:event_vehTableMouseReleased

    private void deleteCusMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCusMenuActionPerformed
        rightClickMenuAction(evt);
    }//GEN-LAST:event_deleteCusMenuActionPerformed

    private void sparePartReportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sparePartReportMenuActionPerformed
        controller.generateSpartPartReport();
    }//GEN-LAST:event_sparePartReportMenuActionPerformed

    private void staffJTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staffJTableMouseReleased
        selectedRow = staffJTable.getSelectedRow();
        selectedCol = staffJTable.getSelectedColumn();
    }//GEN-LAST:event_staffJTableMouseReleased

    private void avgPriceReportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_avgPriceReportMenuActionPerformed
        staffData = controller.getStaffData();
        stm.refresh(staffData);
        panelSwitcher("viewMech");
    }//GEN-LAST:event_avgPriceReportMenuActionPerformed

    private void selectStaffMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectStaffMenuActionPerformed
        staffRightClickAction(evt);
    }//GEN-LAST:event_selectStaffMenuActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        panelSwitcher("vehBooked");
    }//GEN-LAST:event_jMenuItem1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel addCusPanel;
    private javax.swing.JMenuItem addVehMenu;
    private javax.swing.JPanel addVehiclePanel;
    private Date motDate;
    private javax.swing.JTextField addressField;
    private javax.swing.JTextField addressField1;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JLabel addressLabel1;
    private javax.swing.JButton alterCusBtn;
    private javax.swing.JMenuItem alterCusMenu;
    private javax.swing.JPanel alterCusPanel;
    private javax.swing.JButton alterVehBtn;
    private javax.swing.JPanel alterVehDetails;
    private Date alterDate;
    private javax.swing.JMenuItem alterVehMenu;
    private javax.swing.JButton attachVehBtn;
    private javax.swing.JMenuItem avgPriceReportMenu;
    private javax.swing.JTextField chassisField;
    private javax.swing.JTextField chassisField1;
    private javax.swing.JLabel chassisLabel;
    private javax.swing.JLabel chassisLabel1;
    private javax.swing.JTextField cityField;
    private javax.swing.JTextField cityField1;
    private javax.swing.JLabel cityLabel;
    private javax.swing.JLabel cityLabel1;
    private javax.swing.JTextField colourField;
    private javax.swing.JTextField colourField1;
    private javax.swing.JLabel colourLabel;
    private javax.swing.JLabel colourLabel1;
    private javax.swing.JButton createCusBtn;
    private javax.swing.JTable cusTable;
    private CustomerTableModel ctm;
    private TableRowSorter<CustomerTableModel> sorter;
    private javax.swing.JButton datePickerBtn;
    private javax.swing.JButton datePickerBtn1;
    private javax.swing.JMenuItem deleteCusMenu;
    private javax.swing.JMenuItem deleteVehMenu;
    private javax.swing.JLabel emLabel;
    private javax.swing.JLabel emLabel1;
    private javax.swing.JTextField emailField;
    private javax.swing.JTextField emailField1;
    private javax.swing.JTextField engField;
    private javax.swing.JTextField engField1;
    private javax.swing.JLabel engLabel;
    private javax.swing.JLabel engLabel1;
    private javax.swing.JTextField fNameField;
    private javax.swing.JTextField fNameField1;
    private javax.swing.JLabel fNameLabel;
    private javax.swing.JLabel fNameLabel1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField localityField;
    private javax.swing.JTextField localityField1;
    private javax.swing.JLabel localityLabel;
    private javax.swing.JLabel localityLabel1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField makeField;
    private javax.swing.JTextField makeField1;
    private javax.swing.JLabel makeLabel;
    private javax.swing.JLabel makeLabel1;
    private javax.swing.JTextField mobileField;
    private javax.swing.JTextField mobileField1;
    private javax.swing.JLabel mobileLabel;
    private javax.swing.JLabel mobileLabel1;
    private javax.swing.JTextField modelField;
    private javax.swing.JTextField modelField1;
    private javax.swing.JLabel modelLabel;
    private javax.swing.JLabel modelLabel1;
    private javax.swing.JLabel motLabel;
    private javax.swing.JLabel motLabel1;
    private javax.swing.JButton newCusBtn;
    private javax.swing.JTextField noteField;
    private javax.swing.JTextField noteField1;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JLabel noteLabel1;
    private javax.swing.JTextField pCodeField;
    private javax.swing.JTextField pCodeField1;
    private javax.swing.JLabel pCodeLabel;
    private javax.swing.JLabel pCodeLabel1;
    private javax.swing.JTextField regField;
    private javax.swing.JTextField regField1;
    private javax.swing.JLabel regLabel;
    private javax.swing.JLabel regLabel1;
    private javax.swing.JPopupMenu rightClickMenu;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JMenuItem selectStaffMenu;
    private javax.swing.JPanel sideMenuPanel;
    private javax.swing.JMenuItem sparePartReportMenu;
    private javax.swing.JTable staffJTable;
    private StaffTableModel stm;
    private TableRowSorter<StaffTableModel> staffSorter;
    private javax.swing.JPopupMenu staffRightClickMenu;
    private javax.swing.JTextField streetField;
    private javax.swing.JTextField streetField1;
    private javax.swing.JLabel streetLabel;
    private javax.swing.JLabel streetLabel1;
    private javax.swing.JTextField teleField;
    private javax.swing.JTextField teleField1;
    private javax.swing.JLabel teleLabel;
    private javax.swing.JLabel teleLabel1;
    private javax.swing.JButton updateTableBtn;
    private javax.swing.JLabel userConLabel;
    private javax.swing.JPopupMenu vehRightClickMenu;
    private javax.swing.JTable vehTable;
    private VehicleTableModel vtm;
    private javax.swing.JLabel vehTitleLabel;
    private javax.swing.JLabel vehTitleLabel1;
    private javax.swing.JPanel vehicleBookPanel;
    private javax.swing.JButton viewCusBtn;
    private javax.swing.JPanel viewCusPanel;
    private javax.swing.JPanel viewMechPanel;
    private javax.swing.JMenuItem viewVehMenu;
    private javax.swing.JPanel viewVehPanel;
    private javax.swing.JTextField yearField;
    private javax.swing.JTextField yearField1;
    private javax.swing.JLabel yearLabel;
    private javax.swing.JLabel yearLabel1;
    // End of variables declaration//GEN-END:variables
}
