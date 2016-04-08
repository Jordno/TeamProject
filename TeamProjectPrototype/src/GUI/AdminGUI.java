/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Controllers.AdminController;
import Controllers.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Entities.Staff;
import Entities.Task;
import com.sun.org.glassfish.gmbal.DescriptorFields;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Wolf
 */
public class AdminGUI extends javax.swing.JFrame {
    private ArrayList<Staff> staffData; //needed to populate the jtable
    private ArrayList<Task> taskListData;
    private AdminController controller;
    private int selectedRow, selectedCol, listID; //used to get selected cell
    
    /**
     * Creates new form AdminGUI
     * @param conn
     */
    public AdminGUI(DatabaseConnection conn) {
        controller = new AdminController(conn);
        staffData = new ArrayList<>(); 
        taskListData = new ArrayList<>();
        selectedCol = -1;
        selectedRow = -1;
        initalizeData(); //retrieves the data collected in the controll class, needed for StaffTableModel.
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
        //for(Staff c : data) System.out.println(c.getDate());
    }
    
    private void initalizeData(){
        staffData = controller.getStaffData();
        taskListData = controller.getTaskListData();
    }
    
    
    public void panelSwitcher(String type){
        mainPanel.removeAll();
        switch(type){
            case "viewAcc":
                mainPanel.add(viewAccPanel);
                break;
            case "viewTask":
                mainPanel.add(viewTaskList);
                break;
            case "viewTypes":
                mainPanel.add(viewJobType);
                break;
            case "alterTask":
                mainPanel.add(alterTaskPanel);
                break;
        }
        mainPanel.repaint();
        mainPanel.revalidate();
    }
    
    /**
     * When a rightclick menu option is selected, this information is passed to  this method
     * which will call the nesscary method in admin controller
     * @param event 
     */
    public void rightClickMenuAction(ActionEvent event) { //right click to add and delete row and create seat
        JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
        String selected = (String)staffJTable.getValueAt(selectedRow, 1);
        if(selectedCol == -1 || selectedRow == -1){
            JOptionPane.showMessageDialog(null,
                "Please select a valid row.",
                "Invalid selection",
            JOptionPane.ERROR_MESSAGE);
        }else if(menu == changeNameMenu){ 
            controller.changeUsername(selected); //cast to string because it's originally an object.
        }else if (menu == changePassMenu){
            controller.changePassword(selected);
        }else if(menu == changeTypeMenu){
            controller.changeType(selected);
        }else if(menu == deleteAccMenu){
            controller.deleteAccount(selected);
        }
        updateData(); //update information once done.
    }
    
    public void taskRightClickMenuAction(ActionEvent event){
        JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
        int selected = (int)taskListTable.getValueAt(selectedRow, 0);
        if(selectedCol == -1 || selectedRow == -1){
            JOptionPane.showMessageDialog(null,
                "Please select a valid row.",
                "Invalid selection",
            JOptionPane.ERROR_MESSAGE);
        }else if(menu == alterTaskDetail){ 
            alterTaskField.setText((String)taskListTable.getValueAt(selectedRow, 1));
            String time = (String)taskListTable.getValueAt(selectedRow, 2);
            String[] spl = time.split(":");
            System.out.println(spl[0]+spl[2]);
            alterHourSpinner.setValue(Integer.parseInt(spl[0]));
            alterMinSpinner.setValue(Integer.parseInt(spl[1]));
            alterSecSpinner.setValue(Integer.parseInt(spl[2]));
            listID = selected;
            panelSwitcher("alterTask");
        }else if (menu == deleteTask){
            //controller.changePassword(selected);
        }
        updateData(); //update information once done.
    }
    
    /**
     * Gets the new information from table and refreshs the table model to display
     * the newest version.
     */
    private void updateData(){
        initalizeData(); //re-run this method to get the most updated version
        stm.refresh(staffData); //sends the updated data to tablemodel and updates it to jtable
        tltm.refresh(taskListData);
    }
    
    private void searchTable(String filter){
        //TableRowSorter<StaffTableModel> sorter = new TableRowSorter<>(stm);
        staffJTable.setRowSorter(sorter);
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
        changeNameMenu = new javax.swing.JMenuItem();
        changePassMenu = new javax.swing.JMenuItem();
        changeTypeMenu = new javax.swing.JMenuItem();
        deleteAccMenu = new javax.swing.JMenuItem();
        taskRightClickMenu = new javax.swing.JPopupMenu();
        alterTaskDetail = new javax.swing.JMenuItem();
        deleteTask = new javax.swing.JMenuItem();
        updateButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        searchLabel = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        viewAccPanel = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        staffJTable = new javax.swing.JTable();
        newAccPane = new javax.swing.JLayeredPane();
        addNewAcc = new javax.swing.JButton();
        newUsernameField = new javax.swing.JTextField();
        newPassField = new javax.swing.JPasswordField();
        newAccType = new javax.swing.JComboBox();
        newNameLabel = new javax.swing.JLabel();
        newPassLabel = new javax.swing.JLabel();
        newAccTypeLabel = new javax.swing.JLabel();
        newSurnameField = new javax.swing.JTextField();
        newNameField = new javax.swing.JTextField();
        surnameLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        labourField = new javax.swing.JTextField();
        labourLabel = new javax.swing.JLabel();
        viewTaskList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taskListTable = new javax.swing.JTable();
        newTaskPane = new javax.swing.JLayeredPane();
        addNewTaskBtn = new javax.swing.JButton();
        taskField = new javax.swing.JTextField();
        taskLabel = new javax.swing.JLabel();
        hourSpinner = new javax.swing.JSpinner();
        minSpinner = new javax.swing.JSpinner();
        secSpinner = new javax.swing.JSpinner();
        durLabel = new javax.swing.JLabel();
        hourLabel = new javax.swing.JLabel();
        minLabel = new javax.swing.JLabel();
        secLabel = new javax.swing.JLabel();
        viewJobType = new javax.swing.JPanel();
        alterTaskPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        alterTaskField = new javax.swing.JTextField();
        taskLabel1 = new javax.swing.JLabel();
        alterHourSpinner = new javax.swing.JSpinner();
        alterMinSpinner = new javax.swing.JSpinner();
        alterSecSpinner = new javax.swing.JSpinner();
        durLabel1 = new javax.swing.JLabel();
        hourLabel1 = new javax.swing.JLabel();
        minLabel1 = new javax.swing.JLabel();
        secLabel1 = new javax.swing.JLabel();
        alterTaskBtn = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        backupMenuItem = new javax.swing.JMenuItem();
        restoreMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        taskListMenu = new javax.swing.JMenuItem();
        jobTypeMenu = new javax.swing.JMenuItem();
        viewAccMenu = new javax.swing.JMenuItem();

        changeNameMenu.setText("Change account username");
        changeNameMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeNameMenuActionPerformed(evt);
            }
        });
        rightClickMenu.add(changeNameMenu);

        changePassMenu.setText("Change account password");
        changePassMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePassMenuActionPerformed(evt);
            }
        });
        rightClickMenu.add(changePassMenu);

        changeTypeMenu.setText("Change account type");
        changeTypeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeTypeMenuActionPerformed(evt);
            }
        });
        rightClickMenu.add(changeTypeMenu);

        deleteAccMenu.setText("Delete account details");
        deleteAccMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAccMenuActionPerformed(evt);
            }
        });
        rightClickMenu.add(deleteAccMenu);

        staffJTable.setComponentPopupMenu(rightClickMenu);

        alterTaskDetail.setText("Alter Task details");
        alterTaskDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterTaskDetailActionPerformed(evt);
            }
        });
        taskRightClickMenu.add(alterTaskDetail);

        deleteTask.setText("Delete Task details");
        deleteTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTaskActionPerformed(evt);
            }
        });
        taskRightClickMenu.add(deleteTask);

        taskListTable.setComponentPopupMenu(taskRightClickMenu);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Admin Control Panel");

        updateButton.setText("Update");
        updateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                updateButtonMouseReleased(evt);
            }
        });

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });

        searchLabel.setText("Quick Search:");

        mainPanel.setLayout(new java.awt.CardLayout());

        staffJTable.setModel(stm = new StaffTableModel(staffData));
        sorter = new TableRowSorter<>(stm);
        staffJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                staffJTableMouseReleased(evt);
            }
        });
        jScrollPane.setViewportView(staffJTable);

        newAccPane.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Add new account"));

        addNewAcc.setText("Add");
        addNewAcc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                addNewAccMouseReleased(evt);
            }
        });

        newAccType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ADMIN", "FRANCHISEE", "FOREPERSON", "MECHANIC", "RECEPTIONIST" }));
        newAccType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAccTypeActionPerformed(evt);
            }
        });

        newNameLabel.setText("Username: ");

        newPassLabel.setText("Password: ");

        newAccTypeLabel.setText("Account Type:");

        surnameLabel.setText("Surname: ");

        nameLabel.setText("Name: ");

        labourLabel.setText("Labour Cost:");

        javax.swing.GroupLayout newAccPaneLayout = new javax.swing.GroupLayout(newAccPane);
        newAccPane.setLayout(newAccPaneLayout);
        newAccPaneLayout.setHorizontalGroup(
            newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, newAccPaneLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newNameLabel)
                    .addComponent(surnameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(newSurnameField)
                    .addComponent(newUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newPassLabel)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(newNameField)
                    .addComponent(newPassField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newAccTypeLabel)
                    .addComponent(labourLabel))
                .addGap(18, 18, 18)
                .addGroup(newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addNewAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(labourField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(newAccType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        newAccPaneLayout.setVerticalGroup(
            newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newAccPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(newSurnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(surnameLabel))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(newNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(nameLabel)
                        .addComponent(labourField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labourLabel)))
                .addGap(18, 18, 18)
                .addGroup(newAccPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newPassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newPassLabel)
                    .addComponent(newUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newNameLabel)
                    .addComponent(newAccTypeLabel)
                    .addComponent(newAccType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(addNewAcc)
                .addContainerGap())
        );
        newAccPane.setLayer(addNewAcc, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newAccPane.setLayer(newUsernameField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newAccPane.setLayer(newPassField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newAccPane.setLayer(newAccType, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newAccPane.setLayer(newNameLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newAccPane.setLayer(newPassLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newAccPane.setLayer(newAccTypeLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newAccPane.setLayer(newSurnameField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newAccPane.setLayer(newNameField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newAccPane.setLayer(surnameLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newAccPane.setLayer(nameLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newAccPane.setLayer(labourField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        labourField.setVisible(false);
        labourLabel.setVisible(false);
        newAccPane.setLayer(labourLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout viewAccPanelLayout = new javax.swing.GroupLayout(viewAccPanel);
        viewAccPanel.setLayout(viewAccPanelLayout);
        viewAccPanelLayout.setHorizontalGroup(
            viewAccPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, viewAccPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newAccPane)
                .addContainerGap())
            .addComponent(jScrollPane)
        );
        viewAccPanelLayout.setVerticalGroup(
            viewAccPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewAccPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newAccPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(viewAccPanel, "card2");

        taskListTable.setModel(tltm = new TaskListTableModel(taskListData));
        taskListTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                taskListTableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(taskListTable);

        newTaskPane.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Add new Task"));

        addNewTaskBtn.setText("Add");
        addNewTaskBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                addNewTaskBtnMouseReleased(evt);
            }
        });

        taskLabel.setText("Task Description:");

        durLabel.setText("Task Duration:");

        hourLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hourLabel.setText("Hours");

        minLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minLabel.setText("Minutes");

        secLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        secLabel.setText("Seconds");

        javax.swing.GroupLayout newTaskPaneLayout = new javax.swing.GroupLayout(newTaskPane);
        newTaskPane.setLayout(newTaskPaneLayout);
        newTaskPaneLayout.setHorizontalGroup(
            newTaskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newTaskPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newTaskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(taskLabel)
                    .addComponent(durLabel))
                .addGap(28, 28, 28)
                .addGroup(newTaskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(taskField, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(newTaskPaneLayout.createSequentialGroup()
                        .addGroup(newTaskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(newTaskPaneLayout.createSequentialGroup()
                                .addComponent(hourLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(minLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(newTaskPaneLayout.createSequentialGroup()
                                .addComponent(hourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(75, 75, 75)
                                .addComponent(minSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(newTaskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(secSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(secLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(89, 89, 89)
                .addComponent(addNewTaskBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        newTaskPaneLayout.setVerticalGroup(
            newTaskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newTaskPaneLayout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(newTaskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(taskLabel)
                    .addComponent(taskField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(newTaskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newTaskPaneLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(newTaskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addNewTaskBtn)
                            .addComponent(hourLabel)
                            .addComponent(minLabel)
                            .addComponent(secLabel)))
                    .addGroup(newTaskPaneLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(newTaskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(minSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(hourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(secSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(durLabel))))
                .addContainerGap())
        );
        newTaskPane.setLayer(addNewTaskBtn, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newTaskPane.setLayer(taskField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newTaskPane.setLayer(taskLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newTaskPane.setLayer(hourSpinner, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newTaskPane.setLayer(minSpinner, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newTaskPane.setLayer(secSpinner, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newTaskPane.setLayer(durLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newTaskPane.setLayer(hourLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newTaskPane.setLayer(minLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        newTaskPane.setLayer(secLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout viewTaskListLayout = new javax.swing.GroupLayout(viewTaskList);
        viewTaskList.setLayout(viewTaskListLayout);
        viewTaskListLayout.setHorizontalGroup(
            viewTaskListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, viewTaskListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newTaskPane)
                .addContainerGap())
        );
        viewTaskListLayout.setVerticalGroup(
            viewTaskListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewTaskListLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newTaskPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(viewTaskList, "card3");

        javax.swing.GroupLayout viewJobTypeLayout = new javax.swing.GroupLayout(viewJobType);
        viewJobType.setLayout(viewJobTypeLayout);
        viewJobTypeLayout.setHorizontalGroup(
            viewJobTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 660, Short.MAX_VALUE)
        );
        viewJobTypeLayout.setVerticalGroup(
            viewJobTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 581, Short.MAX_VALUE)
        );

        mainPanel.add(viewJobType, "card4");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Altering Task Details");

        taskLabel1.setText("Task Description:");

        durLabel1.setText("Task Duration:");

        hourLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hourLabel1.setText("Hours");

        minLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minLabel1.setText("Minutes");

        secLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        secLabel1.setText("Seconds");

        alterTaskBtn.setText("Alter Task");
        alterTaskBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterTaskBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout alterTaskPanelLayout = new javax.swing.GroupLayout(alterTaskPanel);
        alterTaskPanel.setLayout(alterTaskPanelLayout);
        alterTaskPanelLayout.setHorizontalGroup(
            alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alterTaskPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(220, 220, 220))
            .addGroup(alterTaskPanelLayout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(alterTaskBtn)
                    .addGroup(alterTaskPanelLayout.createSequentialGroup()
                        .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(taskLabel1)
                            .addComponent(durLabel1))
                        .addGap(28, 28, 28)
                        .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(alterTaskField, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(alterTaskPanelLayout.createSequentialGroup()
                                .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(alterTaskPanelLayout.createSequentialGroup()
                                        .addComponent(hourLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(minLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(alterTaskPanelLayout.createSequentialGroup()
                                        .addComponent(alterHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(75, 75, 75)
                                        .addComponent(alterMinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(alterSecSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(secLabel1, javax.swing.GroupLayout.Alignment.TRAILING))))))
                .addContainerGap(171, Short.MAX_VALUE))
        );
        alterTaskPanelLayout.setVerticalGroup(
            alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alterTaskPanelLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1)
                .addGap(58, 58, 58)
                .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(taskLabel1)
                    .addComponent(alterTaskField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterTaskPanelLayout.createSequentialGroup()
                        .addGap(126, 126, 126)
                        .addComponent(alterTaskBtn))
                    .addGroup(alterTaskPanelLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(alterMinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(alterHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(alterSecSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(durLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(hourLabel1)
                            .addComponent(minLabel1)
                            .addComponent(secLabel1))))
                .addContainerGap(289, Short.MAX_VALUE))
        );

        mainPanel.add(alterTaskPanel, "card5");

        fileMenu.setText("File");

        backupMenuItem.setText("Backup Database");
        backupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backupMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(backupMenuItem);

        restoreMenuItem.setText("Restore Database");
        restoreMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(restoreMenuItem);

        jMenuBar1.add(fileMenu);

        viewMenu.setText("View");

        taskListMenu.setText("View Task List Table");
        taskListMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                taskListMenuActionPerformed(evt);
            }
        });
        viewMenu.add(taskListMenu);

        jobTypeMenu.setText("View Job Type Table");
        jobTypeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jobTypeMenuActionPerformed(evt);
            }
        });
        viewMenu.add(jobTypeMenu);

        viewAccMenu.setText("View Accounts Table");
        viewAccMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAccMenuActionPerformed(evt);
            }
        });
        viewMenu.add(viewAccMenu);

        jMenuBar1.add(viewMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(searchLabel)
                .addGap(18, 18, 18)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(145, 145, 145)
                .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateButton)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchLabel))
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateButtonMouseReleased
        updateData();
    }//GEN-LAST:event_updateButtonMouseReleased

    private void changeNameMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeNameMenuActionPerformed
        rightClickMenuAction(evt);
    }//GEN-LAST:event_changeNameMenuActionPerformed

    private void staffJTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staffJTableMouseReleased
        selectedRow = staffJTable.getSelectedRow();
        selectedCol = staffJTable.getSelectedColumn();
    }//GEN-LAST:event_staffJTableMouseReleased

    private void changePassMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePassMenuActionPerformed
        rightClickMenuAction(evt);
    }//GEN-LAST:event_changePassMenuActionPerformed

    private void changeTypeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeTypeMenuActionPerformed
        rightClickMenuAction(evt);
    }//GEN-LAST:event_changeTypeMenuActionPerformed

    private void addNewAccMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addNewAccMouseReleased
        if(newUsernameField.getText().length() < 3 || newPassField.getText().length() < 3 || newSurnameField.getText().length() < 3 || newNameField.getText().length() < 3){
            JOptionPane.showMessageDialog(null,
                    "All fields must be at least 3 characters long.",
                    "User not added!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            String type = (String)newAccType.getSelectedItem();
            if(type.equals("FOREPERSON") || type.equals("MECHANIC") ){
                controller.addNewUser(newUsernameField.getText(), newPassField.getText(), 
                    newAccType.getSelectedItem().toString(), 
                    newSurnameField.getText(), newNameField.getText(), Double.parseDouble(labourField.getText()));
            }else{
                controller.addNewUser(newUsernameField.getText(), newPassField.getText(), 
                    newAccType.getSelectedItem().toString(), 
                    newSurnameField.getText(), newNameField.getText());
            }
            updateData();
            newUsernameField.setText("");
            newPassField.setText("");
            newSurnameField.setText("");
            newNameField.setText("");
            labourField.setText("");
        }
    }//GEN-LAST:event_addNewAccMouseReleased

    private void deleteAccMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAccMenuActionPerformed
        rightClickMenuAction(evt);
    }//GEN-LAST:event_deleteAccMenuActionPerformed

    private void backupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backupMenuItemActionPerformed
        //Creates a JFileChooser to allow the use to select the MySQL directory
        //this is then used to excute backup using mysqldump.exe
        
//        JFileChooser chooser = new JFileChooser();
//        String mySQLDir = null;
//        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        chooser.setDialogTitle("Select MySQL folder");
//        int returnVal = chooser.showOpenDialog(this);
//        if(returnVal == JFileChooser.APPROVE_OPTION) {
//             mySQLDir = chooser.getSelectedFile().getAbsolutePath();
//        }
//        
//        try {
//            String folderPath =  System.getProperty("user.dir")+"\\backup";
//            //create directory if it don't exist.
//            new File(folderPath).mkdir();
//            //Creates a cmd command to be used later on, uses mysqldump to backup
//            String executeCmd = mySQLDir+"\\bin\\mysqldump -u root --password= --databases gartisdb -r " +folderPath+ "\\garitsDBBackup.sql";
//            //executes the command, and waits for the outcome.
//            Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
//            int processComplete = runtimeProcess.waitFor();
//
//            //Tell the user if the outcome was succesful or not
//            if (processComplete == 0) {
//                JOptionPane.showMessageDialog(null,
//                    "Back up was succesful!",
//                    "Backup completed",
//                    JOptionPane.ERROR);
//            } else {
//                JOptionPane.showMessageDialog(null,
//                    "Back up has failed, please try again",
//                    "Backup has failed",
//                    JOptionPane.ERROR);
//            }
//
//        } catch (Exception ex) {
//                JOptionPane.showMessageDialog(null,
//                    "An error has occured while backing up! "+ex.getMessage(),
//                    "Backup has failed",
//                    JOptionPane.ERROR);
//           // String executeCmd = "C:\\xampp\\mysql\\bin\\mysqldump -u" + dbUser + " -p" + dbPass + " --database " + dbName + " -r " + "C:\\xampp\\mysql";
//             //StringBuilder executeCmd = new StringBuilder(test+"\\bin\\mysqldump -u root --password= --databases gartisDB"+folderPath+"\\garitsDBBackup.sql");
//             //String executeCmd = test+"\\bin\\mysqldump -u root --password= --databases gartisDB"+ folderPath+"\\garitsDBBackup.sql";
//            //restore command
//            //mysql -u root -p gartisdb < C:\xampp\mysql\test\backup.sql
//        }
        controller.backupDatabase();
    }//GEN-LAST:event_backupMenuItemActionPerformed

    private void restoreMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreMenuItemActionPerformed
        controller.restoreDatabase();
    }//GEN-LAST:event_restoreMenuItemActionPerformed

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
        searchTable(searchField.getText());
    }//GEN-LAST:event_searchFieldKeyReleased

    private void newAccTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newAccTypeActionPerformed
        String type = (String)newAccType.getSelectedItem();
        if(type.equals("FOREPERSON") || type.equals("MECHANIC") ){
            labourField.setVisible(true);
            labourLabel.setVisible(true);
        }else{
            labourField.setVisible(false);
            labourLabel.setVisible(false);
        }
    }//GEN-LAST:event_newAccTypeActionPerformed

    private void taskListMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_taskListMenuActionPerformed
        panelSwitcher("viewTask");
    }//GEN-LAST:event_taskListMenuActionPerformed

    private void jobTypeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jobTypeMenuActionPerformed
        panelSwitcher("viewJobType");
    }//GEN-LAST:event_jobTypeMenuActionPerformed

    private void addNewTaskBtnMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addNewTaskBtnMouseReleased
        controller.addTask(taskField.getText(),(int)hourSpinner.getValue(),
                (int)minSpinner.getValue(),(int)secSpinner.getValue());
        taskField.setText("");
        hourSpinner.setValue(0);
        minSpinner.setValue(0);
        secSpinner.setValue(0);
        updateData();
    }//GEN-LAST:event_addNewTaskBtnMouseReleased

    private void viewAccMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAccMenuActionPerformed
        panelSwitcher("viewAcc");
    }//GEN-LAST:event_viewAccMenuActionPerformed

    private void taskListTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_taskListTableMouseReleased
        selectedRow = taskListTable.getSelectedRow();
        selectedCol = taskListTable.getSelectedColumn();
    }//GEN-LAST:event_taskListTableMouseReleased

    private void alterTaskDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterTaskDetailActionPerformed
        taskRightClickMenuAction(evt);
    }//GEN-LAST:event_alterTaskDetailActionPerformed

    private void deleteTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTaskActionPerformed
        taskRightClickMenuAction(evt);
    }//GEN-LAST:event_deleteTaskActionPerformed

    private void alterTaskBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterTaskBtnActionPerformed
        controller.alterTask(listID,alterTaskField.getText(),(int)alterHourSpinner.getValue(),
                (int)alterHourSpinner.getValue(),(int)alterHourSpinner.getValue());
        updateData();
        panelSwitcher("viewTask");
    }//GEN-LAST:event_alterTaskBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addNewAcc;
    private javax.swing.JButton addNewTaskBtn;
    private javax.swing.JSpinner alterHourSpinner;
    private javax.swing.JSpinner alterMinSpinner;
    private javax.swing.JSpinner alterSecSpinner;
    private javax.swing.JButton alterTaskBtn;
    private javax.swing.JMenuItem alterTaskDetail;
    private javax.swing.JTextField alterTaskField;
    private javax.swing.JPanel alterTaskPanel;
    private javax.swing.JMenuItem backupMenuItem;
    private javax.swing.JMenuItem changeNameMenu;
    private javax.swing.JMenuItem changePassMenu;
    private javax.swing.JMenuItem changeTypeMenu;
    private javax.swing.JMenuItem deleteAccMenu;
    private javax.swing.JMenuItem deleteTask;
    private javax.swing.JLabel durLabel;
    private javax.swing.JLabel durLabel1;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel hourLabel;
    private javax.swing.JLabel hourLabel1;
    private javax.swing.JSpinner hourSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem jobTypeMenu;
    private javax.swing.JTextField labourField;
    private javax.swing.JLabel labourLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel minLabel;
    private javax.swing.JLabel minLabel1;
    private javax.swing.JSpinner minSpinner;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLayeredPane newAccPane;
    private javax.swing.JComboBox newAccType;
    private javax.swing.JLabel newAccTypeLabel;
    private javax.swing.JTextField newNameField;
    private javax.swing.JLabel newNameLabel;
    private javax.swing.JPasswordField newPassField;
    private javax.swing.JLabel newPassLabel;
    private javax.swing.JTextField newSurnameField;
    private javax.swing.JLayeredPane newTaskPane;
    private javax.swing.JTextField newUsernameField;
    private javax.swing.JMenuItem restoreMenuItem;
    private javax.swing.JPopupMenu rightClickMenu;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JLabel secLabel;
    private javax.swing.JLabel secLabel1;
    private javax.swing.JSpinner secSpinner;
    private javax.swing.JTable staffJTable;
    private StaffTableModel stm;
    private TableRowSorter<StaffTableModel> sorter;
    private javax.swing.JLabel surnameLabel;
    private javax.swing.JTextField taskField;
    private javax.swing.JLabel taskLabel;
    private javax.swing.JLabel taskLabel1;
    private javax.swing.JMenuItem taskListMenu;
    private javax.swing.JTable taskListTable;
    private TaskListTableModel tltm;
    private javax.swing.JPopupMenu taskRightClickMenu;
    private javax.swing.JButton updateButton;
    private javax.swing.JMenuItem viewAccMenu;
    private javax.swing.JPanel viewAccPanel;
    private javax.swing.JPanel viewJobType;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JPanel viewTaskList;
    // End of variables declaration//GEN-END:variables
}
