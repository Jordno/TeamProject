/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Mechanic;

import Controllers.DatabaseConnection;
import Controllers.MechanicController;
import Entities.Job;
import Entities.StockItem;
import Entities.Vehicle;
import GUI.Franchisee.VehicleTableModel;
import GUI.Receptionist.JobInformationList;
import GUI.Receptionist.JobTableModel;
import GUI.Receptionist.StockTableModel;
import java.awt.event.ActionEvent;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Wolf
 */
public class MechanicGUI extends javax.swing.JFrame {

    private ArrayList<Job> jobData;
    private ArrayList<JobTask> taskData;
    private ArrayList<StockItem> stockData;
    private ArrayList<Vehicle> vehData;
    private HashMap<String,Time> taskList;
    private int selectedRow, selectedCol;
    private int staffID, jobID, taskID; //used to find which person has taken the job
    private MechanicController controller;
    
    /**
     * Creates new form MechanicGUI
     */

    public MechanicGUI(DatabaseConnection dbc, int staffID){
        controller = new MechanicController(dbc, staffID);
        this.staffID = staffID;
        jobData = new ArrayList<>();
        taskData = new ArrayList<>();
        stockData = new ArrayList<>();
        vehData = new ArrayList<>();
        taskList = new HashMap<>();
        selectedRow = -1;
        selectedCol = -1;
        initData();
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
        
    }
    
    public void initData(){
        jobData = controller.getPendingJobData();
        stockData = controller.getStockData();
        taskList = controller.getTaskList();
        //ComboBoxModel model = new DefaultComboBoxModel(keys);
        //comboBox.setModel(model);
    }
    
    public void updateData(){
        initData();
        jtm.refresh(jobData);
        stm.refresh(stockData);
    }
    
    public void initSelectedData(){
        jobData = controller.getSelectedPendingJobData();
        jtm.refresh(jobData);
    }
    
    public void initTaskData(int jobID){
        taskData = controller.getSelectedTaskData(jobID);
        jttm.refresh(taskData);
    }
    
    public void initVehData(String regNo){
        vehData = controller.getCusVehData(regNo);
        vtm.refresh(vehData);
    }
    
    public void panelSwitcher(String type){
        mainPanel.removeAll();
        switch(type){
            case "viewJob":
                mainPanel.add(allJobPanel);
                break;
            case "viewSelected":
                mainPanel.add(selectedJobPanel);
                break;
            case "viewTask":
                mainPanel.add(viewTaskPanel);
                break;
            case "addTask":
                mainPanel.add(newTaskPanel);
                break;
            case "alterTask":
                mainPanel.add(alterTaskPanel);
                break;
            case "selectStock":
                mainPanel.add(viewStockPanel);
                break;
            case "viewVeh":
                mainPanel.add(viewVehPanel);
                break;
        }
        mainPanel.repaint();
        mainPanel.revalidate();
    }
    
    private void jobRightClickMenu(ActionEvent event){
        updateData();
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
        }else if(menu == assignJob){
            controller.assignJob(selected);
        }
        updateData();
    }
    
    private void selectedRightClickMenu(ActionEvent event){
        initSelectedData();
        JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
        int selected;
        try{
            selected = (int)jobTable.getValueAt(selectedRow, 0);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,
                "Please select a valid row.",
                "Invalid selection",
            JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        } 
        if(menu == selectedWorkReqMenu){
            for (Job p : jobData) {
                if (p.getJobID() == (selected)) { // Or use an accessor function for `nomeParagem` if appropriate
                    new JobInformationList(p.getWorkReq());
                    break;
                }
            }

            
        }else if(menu == assignJob){
            controller.assignJob(selected);
        }else if(menu == viewTaskMenu){
            initTaskData(selected);
            panelSwitcher("viewTask");
        }else if(menu == addTaskMenu){
            jobID = selected;
            panelSwitcher("addTask");
        }else if(menu == alterJobStatus){
            String[] choices = { "IDLE", "IN PROGRESS", "COMPLETED"};
            String input = (String) JOptionPane.showInputDialog(null, "Select new job status",
                "Select new job status", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
            if(input != null){
                controller.alterJobStatus(selected, input);
            }
        }else if(menu == selectPartMenu){
            jobID = selected;
            panelSwitcher("selectStock");
        }else if(menu == viewVehMenu){
            jobID = selected;
            initVehData((String)jobTable.getValueAt(selectedRow, 1));
            panelSwitcher("viewVeh");
        }
        initSelectedData();
    }
    
    private void taskRightClickMenu(ActionEvent event){
        JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
        int selected;
        try{
            selected = (int)taskTable.getValueAt(selectedRow, 0);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,
                "Please select a valid row.",
                "Invalid selection",
            JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }
        
        if(menu == deleteTaskMenu){
            controller.deleteTask(selected);
            initTaskData((int)taskTable.getValueAt(selectedRow, 1));
            panelSwitcher("viewTask");
        }else if(menu == alterTaskMenu){
            taskID = selected;
            jobID = (int)taskTable.getValueAt(selectedRow, 1);
            taskField1.setText((String)taskTable.getValueAt(selectedRow, 2));
            String dur = (String) taskTable.getValueAt(selectedRow, 3);
            String[] spl = dur.split(":");
            hourSpinner1.setValue(Integer.parseInt(spl[0]));
            minSpinner1.setValue(Integer.parseInt(spl[1]));
            secSpinner1.setValue(Integer.parseInt(spl[2]));
            panelSwitcher("alterTask");
        }
    }
    
    private void stockRightClickMenu(ActionEvent event){
        JMenuItem menu = (JMenuItem) event.getSource(); //gets what the user selects
        String selected;
        try{
            selected = (String)stockTable.getValueAt(selectedRow, 0);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,
                "Please select a valid row.",
                "Invalid selection",
            JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }
        
        if(menu == selectQuanMenu){
            boolean errorFlag = false;

            String input = (String)JOptionPane.showInputDialog(null, "Select amount",
                "Select amount", JOptionPane.QUESTION_MESSAGE);
            int amountUsed = Integer.parseInt(input);
            int newLevel = (int)stockTable.getValueAt(selectedRow, 7) - Integer.parseInt(input);
            if(newLevel < 0){
                JOptionPane.showMessageDialog(null,
                    "You cannot use more than what is currently in-stock!",
                    "Invalid quantity",
                    JOptionPane.ERROR_MESSAGE);
                errorFlag = true;
            }
            if(input != null && !errorFlag){
                controller.useStockPart(selected, newLevel,(double)stockTable.getValueAt(selectedRow, 6), jobID, amountUsed);
            }
        }
        updateData();
    }
    
    private void searchTable(String filter){
        jobTable.setRowSorter(jobSorter);
        selectedJobTable.setRowSorter(jobSorter);
        taskTable.setRowSorter(taskSorter);
        stockTable.setRowSorter(stockSorter);
        //vehTable.setRowSorter(vehSorter);
        //(?i) is used to ignore case in the search term
        jobSorter.setRowFilter(RowFilter.regexFilter("(?i)"+filter));
        taskSorter.setRowFilter(RowFilter.regexFilter("(?i)"+filter));
        //vehSorter.setRowFilter(RowFilter.regexFilter("(?i)"+filter));
        stockSorter.setRowFilter(RowFilter.regexFilter("(?i)"+filter));
        
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
        assignJob = new javax.swing.JMenuItem();
        selectedRightClickMenu = new javax.swing.JPopupMenu();
        selectedWorkReqMenu = new javax.swing.JMenuItem();
        viewTaskMenu = new javax.swing.JMenuItem();
        addTaskMenu = new javax.swing.JMenuItem();
        selectPartMenu = new javax.swing.JMenuItem();
        alterJobStatus = new javax.swing.JMenuItem();
        viewVehMenu = new javax.swing.JMenuItem();
        taskRightClickMenu = new javax.swing.JPopupMenu();
        deleteTaskMenu = new javax.swing.JMenuItem();
        alterTaskMenu = new javax.swing.JMenuItem();
        stockRightClickMenu = new javax.swing.JPopupMenu();
        selectQuanMenu = new javax.swing.JMenuItem();
        sideMenuPanel = new javax.swing.JPanel();
        viewSelectedBtn = new javax.swing.JButton();
        viewPendingBtn = new javax.swing.JButton();
        userConLabel = new javax.swing.JLabel();
        updateTableBtn = new javax.swing.JButton();
        searchLabel = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        mainPanel = new javax.swing.JPanel();
        allJobPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jobTable = new javax.swing.JTable();
        selectedJobPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        selectedJobTable = new javax.swing.JTable();
        viewTaskPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taskTable = new javax.swing.JTable();
        newTaskPanel = new javax.swing.JPanel();
        taskTitleLabel = new javax.swing.JLabel();
        descLabel = new javax.swing.JLabel();
        taskField = new javax.swing.JTextField();
        hourSpinner = new javax.swing.JSpinner();
        minSpinner = new javax.swing.JSpinner();
        secSpinner = new javax.swing.JSpinner();
        preTaskCombo = new javax.swing.JComboBox();
        durationLabel = new javax.swing.JLabel();
        hourLabel = new javax.swing.JLabel();
        minLabel = new javax.swing.JLabel();
        secLabel = new javax.swing.JLabel();
        newTaskBtn = new javax.swing.JButton();
        alterTaskPanel = new javax.swing.JPanel();
        taskTitleLabel1 = new javax.swing.JLabel();
        descLabel1 = new javax.swing.JLabel();
        taskField1 = new javax.swing.JTextField();
        hourSpinner1 = new javax.swing.JSpinner();
        minSpinner1 = new javax.swing.JSpinner();
        secSpinner1 = new javax.swing.JSpinner();
        preTaskCombo1 = new javax.swing.JComboBox();
        durationLabel1 = new javax.swing.JLabel();
        hourLabel1 = new javax.swing.JLabel();
        minLabel1 = new javax.swing.JLabel();
        secLabel1 = new javax.swing.JLabel();
        alterTaskBtn = new javax.swing.JButton();
        viewStockPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        stockTable = new javax.swing.JTable();
        viewVehPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        vehTable = new javax.swing.JTable();

        workReqMenu.setText("View Work Required");
        workReqMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workReqMenuActionPerformed(evt);
            }
        });
        jobRightClickMenu.add(workReqMenu);

        assignJob.setText("Assign job to self");
        assignJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assignJobActionPerformed(evt);
            }
        });
        jobRightClickMenu.add(assignJob);

        jobTable.setComponentPopupMenu(jobRightClickMenu);

        selectedWorkReqMenu.setText("View work required");
        selectedWorkReqMenu.setToolTipText("");
        selectedWorkReqMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectedWorkReqMenuActionPerformed(evt);
            }
        });
        selectedRightClickMenu.add(selectedWorkReqMenu);

        viewTaskMenu.setText("View associated tasks");
        viewTaskMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewTaskMenuActionPerformed(evt);
            }
        });
        selectedRightClickMenu.add(viewTaskMenu);

        addTaskMenu.setText("Add a task to selected job");
        addTaskMenu.setToolTipText("");
        addTaskMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTaskMenuActionPerformed(evt);
            }
        });
        selectedRightClickMenu.add(addTaskMenu);

        selectPartMenu.setText("Select a Stock Part to use");
        selectPartMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPartMenuActionPerformed(evt);
            }
        });
        selectedRightClickMenu.add(selectPartMenu);

        alterJobStatus.setText("Modify Job status");
        alterJobStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterJobStatusActionPerformed(evt);
            }
        });
        selectedRightClickMenu.add(alterJobStatus);

        viewVehMenu.setText("View Vehicle information");
        viewVehMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewVehMenuActionPerformed(evt);
            }
        });
        selectedRightClickMenu.add(viewVehMenu);

        selectedJobTable.setComponentPopupMenu(selectedRightClickMenu);

        deleteTaskMenu.setText("Delete selected task");
        deleteTaskMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTaskMenuActionPerformed(evt);
            }
        });
        taskRightClickMenu.add(deleteTaskMenu);

        alterTaskMenu.setText("Alter task information");
        alterTaskMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterTaskMenuActionPerformed(evt);
            }
        });
        taskRightClickMenu.add(alterTaskMenu);

        taskTable.setComponentPopupMenu(taskRightClickMenu);

        selectQuanMenu.setText("Select amount of parts");
        selectQuanMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectQuanMenuActionPerformed(evt);
            }
        });
        stockRightClickMenu.add(selectQuanMenu);

        stockTable.setComponentPopupMenu(stockRightClickMenu);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        sideMenuPanel.setBackground(new java.awt.Color(255, 255, 255));
        sideMenuPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        viewSelectedBtn.setText("View Selected Jobs");
        viewSelectedBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewSelectedBtnActionPerformed(evt);
            }
        });

        viewPendingBtn.setText("View Pending Jobs");
        viewPendingBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewPendingBtnActionPerformed(evt);
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
                                .addGap(52, 52, 52)
                                .addComponent(userConLabel))
                            .addGroup(sideMenuPanelLayout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addComponent(searchLabel)))
                        .addGap(0, 37, Short.MAX_VALUE))
                    .addGroup(sideMenuPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(searchField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(updateTableBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewSelectedBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewPendingBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        sideMenuPanelLayout.setVerticalGroup(
            sideMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideMenuPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(userConLabel)
                .addGap(18, 18, 18)
                .addComponent(viewPendingBtn)
                .addGap(10, 10, 10)
                .addComponent(updateTableBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(viewSelectedBtn)
                .addGap(18, 18, 18)
                .addComponent(searchLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        javax.swing.GroupLayout allJobPanelLayout = new javax.swing.GroupLayout(allJobPanel);
        allJobPanel.setLayout(allJobPanelLayout);
        allJobPanelLayout.setHorizontalGroup(
            allJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        allJobPanelLayout.setVerticalGroup(
            allJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
        );

        mainPanel.add(allJobPanel, "card2");

        selectedJobTable.setModel(jtm = new JobTableModel(jobData));
        selectedJobTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                selectedJobTableMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(selectedJobTable);

        javax.swing.GroupLayout selectedJobPanelLayout = new javax.swing.GroupLayout(selectedJobPanel);
        selectedJobPanel.setLayout(selectedJobPanelLayout);
        selectedJobPanelLayout.setHorizontalGroup(
            selectedJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        selectedJobPanelLayout.setVerticalGroup(
            selectedJobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
        );

        mainPanel.add(selectedJobPanel, "card2");

        taskTable.setModel(jttm = new JobTaskTableModel(taskData));
        taskSorter = new TableRowSorter<JobTaskTableModel>(jttm);
        taskTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                taskTableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(taskTable);

        javax.swing.GroupLayout viewTaskPanelLayout = new javax.swing.GroupLayout(viewTaskPanel);
        viewTaskPanel.setLayout(viewTaskPanelLayout);
        viewTaskPanelLayout.setHorizontalGroup(
            viewTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        viewTaskPanelLayout.setVerticalGroup(
            viewTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
        );

        mainPanel.add(viewTaskPanel, "card4");

        taskTitleLabel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        taskTitleLabel.setText("Adding a new task");

        descLabel.setText("Task Description:");

        Object[] keys = taskList.keySet().toArray();
        preTaskCombo.setModel(new DefaultComboBoxModel(keys));
        preTaskCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preTaskComboActionPerformed(evt);
            }
        });

        durationLabel.setText("Task Duration:");

        hourLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hourLabel.setText("Hours");

        minLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minLabel.setText("Minutes");

        secLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        secLabel.setText("Seconds");

        newTaskBtn.setText("Add new Task");
        newTaskBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTaskBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout newTaskPanelLayout = new javax.swing.GroupLayout(newTaskPanel);
        newTaskPanel.setLayout(newTaskPanelLayout);
        newTaskPanelLayout.setHorizontalGroup(
            newTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newTaskPanelLayout.createSequentialGroup()
                .addGroup(newTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newTaskPanelLayout.createSequentialGroup()
                        .addGap(284, 284, 284)
                        .addComponent(taskTitleLabel))
                    .addGroup(newTaskPanelLayout.createSequentialGroup()
                        .addGap(351, 351, 351)
                        .addComponent(preTaskCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(newTaskPanelLayout.createSequentialGroup()
                        .addGap(293, 293, 293)
                        .addComponent(descLabel)
                        .addGap(42, 42, 42)
                        .addComponent(taskField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(newTaskPanelLayout.createSequentialGroup()
                        .addGap(421, 421, 421)
                        .addComponent(hourLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(minLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(secLabel))
                    .addGroup(newTaskPanelLayout.createSequentialGroup()
                        .addGap(293, 293, 293)
                        .addComponent(durationLabel)
                        .addGap(58, 58, 58)
                        .addComponent(hourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(minSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(secSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newTaskPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(newTaskBtn)
                .addGap(226, 226, 226))
        );
        newTaskPanelLayout.setVerticalGroup(
            newTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newTaskPanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(taskTitleLabel)
                .addGap(34, 34, 34)
                .addComponent(preTaskCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addGroup(newTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(taskField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(descLabel))
                .addGap(84, 84, 84)
                .addGroup(newTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hourLabel)
                    .addComponent(minLabel)
                    .addComponent(secLabel))
                .addGap(6, 6, 6)
                .addGroup(newTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newTaskPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(durationLabel))
                    .addComponent(hourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59)
                .addComponent(newTaskBtn)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        mainPanel.add(newTaskPanel, "card5");

        taskTitleLabel1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        taskTitleLabel1.setText("Altering a task");

        descLabel1.setText("Task Description:");

        preTaskCombo1.setModel(new DefaultComboBoxModel(keys));
        preTaskCombo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preTaskCombo1ActionPerformed(evt);
            }
        });

        durationLabel1.setText("Task Duration:");

        hourLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hourLabel1.setText("Hours");

        minLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minLabel1.setText("Minutes");

        secLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        secLabel1.setText("Seconds");

        alterTaskBtn.setText("Alter exisiting Task");
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
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(alterTaskBtn)
                .addGap(226, 226, 226))
            .addGroup(alterTaskPanelLayout.createSequentialGroup()
                .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterTaskPanelLayout.createSequentialGroup()
                        .addGap(351, 351, 351)
                        .addComponent(preTaskCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterTaskPanelLayout.createSequentialGroup()
                        .addGap(293, 293, 293)
                        .addComponent(descLabel1)
                        .addGap(42, 42, 42)
                        .addComponent(taskField1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterTaskPanelLayout.createSequentialGroup()
                        .addGap(421, 421, 421)
                        .addComponent(hourLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(minLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(secLabel1))
                    .addGroup(alterTaskPanelLayout.createSequentialGroup()
                        .addGap(293, 293, 293)
                        .addComponent(durationLabel1)
                        .addGap(58, 58, 58)
                        .addComponent(hourSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(minSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(secSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alterTaskPanelLayout.createSequentialGroup()
                        .addGap(285, 285, 285)
                        .addComponent(taskTitleLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        alterTaskPanelLayout.setVerticalGroup(
            alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alterTaskPanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(taskTitleLabel1)
                .addGap(30, 30, 30)
                .addComponent(preTaskCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(taskField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(descLabel1))
                .addGap(84, 84, 84)
                .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hourLabel1)
                    .addComponent(minLabel1)
                    .addComponent(secLabel1))
                .addGap(6, 6, 6)
                .addGroup(alterTaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alterTaskPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(durationLabel1))
                    .addComponent(hourSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59)
                .addComponent(alterTaskBtn)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        mainPanel.add(alterTaskPanel, "card5");

        stockTable.setModel(stm = new StockTableModel(stockData));
        stockSorter = new TableRowSorter<StockTableModel>(stm);
        stockTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                stockTableMouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(stockTable);

        javax.swing.GroupLayout viewStockPanelLayout = new javax.swing.GroupLayout(viewStockPanel);
        viewStockPanel.setLayout(viewStockPanelLayout);
        viewStockPanelLayout.setHorizontalGroup(
            viewStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        viewStockPanelLayout.setVerticalGroup(
            viewStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
        );

        mainPanel.add(viewStockPanel, "card5");

        vehTable.setModel(vtm = new VehicleTableModel(vehData));
        vehTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                vehTableMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(vehTable);

        javax.swing.GroupLayout viewVehPanelLayout = new javax.swing.GroupLayout(viewVehPanel);
        viewVehPanel.setLayout(viewVehPanelLayout);
        viewVehPanelLayout.setHorizontalGroup(
            viewVehPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );
        viewVehPanelLayout.setVerticalGroup(
            viewVehPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );

        mainPanel.add(viewVehPanel, "card3");

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

    private void viewSelectedBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewSelectedBtnActionPerformed
        initSelectedData();
        panelSwitcher("viewSelected");
    }//GEN-LAST:event_viewSelectedBtnActionPerformed

    private void viewPendingBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewPendingBtnActionPerformed
        updateData();
        panelSwitcher("viewJob");
    }//GEN-LAST:event_viewPendingBtnActionPerformed

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

    private void assignJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assignJobActionPerformed
        jobRightClickMenu(evt);
    }//GEN-LAST:event_assignJobActionPerformed

    private void selectedJobTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selectedJobTableMouseReleased
        selectedRow = selectedJobTable.getSelectedRow();
        selectedCol = selectedJobTable.getSelectedColumn();
    }//GEN-LAST:event_selectedJobTableMouseReleased

    private void selectedWorkReqMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectedWorkReqMenuActionPerformed
        selectedRightClickMenu(evt);
    }//GEN-LAST:event_selectedWorkReqMenuActionPerformed

    private void addTaskMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTaskMenuActionPerformed
        selectedRightClickMenu(evt);
    }//GEN-LAST:event_addTaskMenuActionPerformed

    private void viewTaskMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewTaskMenuActionPerformed
        selectedRightClickMenu(evt);
    }//GEN-LAST:event_viewTaskMenuActionPerformed

    private void preTaskComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preTaskComboActionPerformed
        //Predefined Tasks, Oil Check, Engine Check, Tyre Replacement, Fender Replacement, Windscreen Replacement
        String selectedTask = (String)preTaskCombo.getSelectedItem();
        taskField.setText(selectedTask);
        String duration = taskList.get(selectedTask).toString();
        String[] spl = duration.split(":");
        hourSpinner.setValue(Integer.parseInt(spl[0]));
        minSpinner.setValue(Integer.parseInt(spl[1]));
        secSpinner.setValue(Integer.parseInt(spl[2]));
//        if(preTaskCombo.getSelectedIndex() == 1){
//            taskField.setText("Oil Check");
//            hourSpinner.setValue(0);
//            minSpinner.setValue(30);
//            secSpinner.setValue(0);
//        }else if(preTaskCombo.getSelectedIndex() == 2){
//            taskField.setText("Engine Check");
//            hourSpinner.setValue(1);
//            minSpinner.setValue(15);
//            secSpinner.setValue(0);
//        }else if(preTaskCombo.getSelectedIndex() == 3){
//            taskField.setText("Tyre Replacement");
//            hourSpinner.setValue(0);
//            minSpinner.setValue(45);
//            secSpinner.setValue(0);
//        }else if(preTaskCombo.getSelectedIndex() == 4){
//            taskField.setText("Fender Replacement");
//            hourSpinner.setValue(1);
//            minSpinner.setValue(30);
//            secSpinner.setValue(0);
//        }else if(preTaskCombo.getSelectedIndex() == 5){
//            taskField.setText("Windscreen Replacement");
//            hourSpinner.setValue(00);
//            minSpinner.setValue(45);
//            secSpinner.setValue(0);
//        }
    }//GEN-LAST:event_preTaskComboActionPerformed

    private void newTaskBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTaskBtnActionPerformed
        controller.addNewTask(jobID, taskField.getText(),(int)hourSpinner.getValue(),(int)minSpinner.getValue(),(int)secSpinner.getValue());
        initTaskData(jobID);
        panelSwitcher("viewTask");
    }//GEN-LAST:event_newTaskBtnActionPerformed

    private void preTaskCombo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preTaskCombo1ActionPerformed
        //Predefined Tasks, Oil Check, Engine Check, Tyre Replacement, Fender Replacement, Windscreen Replacement
        String selectedTask = (String)preTaskCombo1.getSelectedItem();
        taskField1.setText(selectedTask);
        String duration = taskList.get(selectedTask).toString();
        String[] spl = duration.split(":");
        hourSpinner1.setValue(Integer.parseInt(spl[0]));
        minSpinner1.setValue(Integer.parseInt(spl[1]));
        secSpinner1.setValue(Integer.parseInt(spl[2]));
    }//GEN-LAST:event_preTaskCombo1ActionPerformed

    private void alterTaskBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterTaskBtnActionPerformed
        controller.alterTask(taskID,taskField1.getText(), (int)hourSpinner1.getValue(), 
                (int)minSpinner1.getValue(), (int)secSpinner1.getValue());
        initTaskData(jobID);
        panelSwitcher("viewTask");
    }//GEN-LAST:event_alterTaskBtnActionPerformed

    private void deleteTaskMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTaskMenuActionPerformed
        taskRightClickMenu(evt);
    }//GEN-LAST:event_deleteTaskMenuActionPerformed

    private void alterTaskMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterTaskMenuActionPerformed
        taskRightClickMenu(evt);
    }//GEN-LAST:event_alterTaskMenuActionPerformed

    private void taskTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_taskTableMouseReleased
        selectedRow = taskTable.getSelectedRow();
        selectedCol = taskTable.getSelectedColumn();
    }//GEN-LAST:event_taskTableMouseReleased

    private void alterJobStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterJobStatusActionPerformed
        selectedRightClickMenu(evt);
    }//GEN-LAST:event_alterJobStatusActionPerformed

    private void stockTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stockTableMouseReleased
        selectedRow = stockTable.getSelectedRow();
        selectedCol = stockTable.getSelectedColumn();
    }//GEN-LAST:event_stockTableMouseReleased

    private void selectPartMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPartMenuActionPerformed
        selectedRightClickMenu(evt);
    }//GEN-LAST:event_selectPartMenuActionPerformed

    private void selectQuanMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectQuanMenuActionPerformed
        stockRightClickMenu(evt);
    }//GEN-LAST:event_selectQuanMenuActionPerformed

    private void vehTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vehTableMouseReleased
        selectedRow = vehTable.getSelectedRow();
        selectedCol = vehTable.getSelectedColumn();
    }//GEN-LAST:event_vehTableMouseReleased

    private void viewVehMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewVehMenuActionPerformed
        selectedRightClickMenu(evt);
    }//GEN-LAST:event_viewVehMenuActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addTaskMenu;
    private javax.swing.JPanel allJobPanel;
    private javax.swing.JMenuItem alterJobStatus;
    private javax.swing.JButton alterTaskBtn;
    private javax.swing.JMenuItem alterTaskMenu;
    private javax.swing.JPanel alterTaskPanel;
    private javax.swing.JMenuItem assignJob;
    private javax.swing.JMenuItem deleteTaskMenu;
    private javax.swing.JLabel descLabel;
    private javax.swing.JLabel descLabel1;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JLabel durationLabel1;
    private javax.swing.JLabel hourLabel;
    private javax.swing.JLabel hourLabel1;
    private javax.swing.JSpinner hourSpinner;
    private javax.swing.JSpinner hourSpinner1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPopupMenu jobRightClickMenu;
    private javax.swing.JTable jobTable;
    private JobTableModel jtm;
    private TableRowSorter<JobTableModel> jobSorter;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel minLabel;
    private javax.swing.JLabel minLabel1;
    private javax.swing.JSpinner minSpinner;
    private javax.swing.JSpinner minSpinner1;
    private javax.swing.JButton newTaskBtn;
    private javax.swing.JPanel newTaskPanel;
    private javax.swing.JComboBox preTaskCombo;
    private javax.swing.JComboBox preTaskCombo1;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JLabel secLabel;
    private javax.swing.JLabel secLabel1;
    private javax.swing.JSpinner secSpinner;
    private javax.swing.JSpinner secSpinner1;
    private javax.swing.JMenuItem selectPartMenu;
    private javax.swing.JMenuItem selectQuanMenu;
    private javax.swing.JPanel selectedJobPanel;
    private javax.swing.JTable selectedJobTable;
    private javax.swing.JPopupMenu selectedRightClickMenu;
    private javax.swing.JMenuItem selectedWorkReqMenu;
    private javax.swing.JPanel sideMenuPanel;
    private javax.swing.JPopupMenu stockRightClickMenu;
    private javax.swing.JTable stockTable;
    private StockTableModel stm;
    private TableRowSorter stockSorter;
    private javax.swing.JTextField taskField;
    private javax.swing.JTextField taskField1;
    private javax.swing.JPopupMenu taskRightClickMenu;
    private javax.swing.JTable taskTable;
    private JobTaskTableModel jttm;
    private TableRowSorter taskSorter;
    private javax.swing.JLabel taskTitleLabel;
    private javax.swing.JLabel taskTitleLabel1;
    private javax.swing.JButton updateTableBtn;
    private javax.swing.JLabel userConLabel;
    private javax.swing.JTable vehTable;
    private VehicleTableModel vtm;
    private TableRowSorter vehSorter;
    private javax.swing.JButton viewPendingBtn;
    private javax.swing.JButton viewSelectedBtn;
    private javax.swing.JPanel viewStockPanel;
    private javax.swing.JMenuItem viewTaskMenu;
    private javax.swing.JPanel viewTaskPanel;
    private javax.swing.JMenuItem viewVehMenu;
    private javax.swing.JPanel viewVehPanel;
    private javax.swing.JMenuItem workReqMenu;
    // End of variables declaration//GEN-END:variables
}
