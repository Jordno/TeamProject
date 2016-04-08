/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Franchisee;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 *  This class is used to format the date selected by JDatePicker component
 *  rather than 15-Mar-2016, it will display 2016-03-15. A format used by MySQL.
 * @author Wolf
 */
public class DateSQLFormatter extends AbstractFormatter {

    private String datePattern = "yyyy-MM-dd";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }
        return null;
    }
}

// OLD CODE MAY NOT NEED
//        SqlDateModel model = new SqlDateModel();
//        // A list of properties needed by JDatePicker to display correctly
//        Properties p = new Properties();
//        p.put("text.today", "Today");
//        p.put("text.month", "Month");
//        p.put("text.year", "Year");
//        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
//        // Uses a custom format class as it must follow standard date for sql formatting
//        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateSQLFormatter());
//        datePicker.setShowYearButtons(true);
//        datePickerBtn.add(datePicker); //add to button easier to modify and change around
//        //Date selectedDates = (Date) datePicker.getModel().fr;
//        //java.sql.Date ss = new java.sql.Date(selectedDate.getTime());
//        datePicker.addActionListener(new java.awt.event.ActionListener(){
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                        System.out.println(datePicker.getJFormattedTextField().getText()); //prints it out
//                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); //use SQL standard format
//                        String selectedDate = datePicker.getJFormattedTextField().getText(); //retrieve date from JDatePIcker
//                        System.out.println(datePicker.getModel().getValue());
//                try {
//                    //As Sql date now requires a 'long' time , you must first convert the standard util date object
//                    //and get the time equivliant before it can be translated into an Sql date object
//                    java.util.Date da = (java.util.Date) df.parse(selectedDate);
//                    //java.sql.Date sqlStartDate = new java.sql.Date(da.getTime());
//                    java.sql.Date sqlStartDate = (java.sql.Date)datePicker.getModel().getValue();
//                    System.out.println(sqlStartDate);
//                } catch (ParseException ex) {
//                    Logger.getLogger(FranchiseeGUI.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });