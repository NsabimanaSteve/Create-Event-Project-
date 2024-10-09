import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.*;
import java.util.List;
import java.util.UUID;

// /**
//  * CalendarManagerGUI is a graphical user interface for managing calendar events.
//  * It provides functionalities to add, search, sort, view history, and generate summaries
//  * of calendar events.
//  */
public class GUI extends JFrame {
    private Calendar calendarManager;  
    private JTable eventTable;                
    private DefaultTableModel tableModel;     
    private JTextField titleField, locationField, descriptionField;  
    private JSpinner dateSpinner, startTimeSpinner, endTimeSpinner;   
    private JComboBox<String> priorityCombo;  
    private JTextArea historyTextArea;
    private JTextArea summaryTextArea;

    /**
     * Constructs a CalendarManagerGUI object and initializes the UI components.
     * Sets up the layout, styling, and event handling for the calendar manager application.
     */
    public GUI() {
        Font customFont1 = new Font("Segoe UI", Font.BOLD, 14);
        Font customFont2 = new Font("Segoe UI", Font.PLAIN, 14);

        UIManager.put("Button.font", customFont1);
        UIManager.put("Label.font", customFont2);
        UIManager.put("TextField.font", customFont2);
        UIManager.put("TextArea.font", customFont2);
        UIManager.put("ComboBox.font", customFont2);
        UIManager.put("Table.font", customFont2);

        UIManager.put("Button.background", new Color(204, 229, 255)); 
        UIManager.put("Button.foreground", Color.BLACK); 
        UIManager.put("Button.border", BorderFactory.createLineBorder(Color.BLACK));

        calendarManager = new Calendar();  
        setTitle("Calendar Manager");  
        setSize(800, 600);  
        getContentPane().setBackground(new Color(0, 255, 255)); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        setLocationRelativeTo(null);  

        // Create tabbed pane for different views
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create the panels for each tab
        JPanel Dashboard = createDashboard();
        JPanel addEventPanel = createAddEventPanel();
        JPanel searchPanel = createSearchPanel();
        JPanel sortPanel = createSortPanel();
        JPanel historyPanel = createHistoryPanel();
        JPanel summaryPanel = createSummaryPanel();

        // Set the background color for each panel
        Dashboard.setBackground(new Color(240, 240, 204));
        addEventPanel.setBackground(new Color(255, 228, 225)); 
        searchPanel.setBackground(new Color(240, 240, 204)); 
        sortPanel.setBackground(new Color(204, 255, 204)); 
        historyPanel.setBackground(new Color(255, 228, 225)); 
        summaryPanel.setBackground(new Color(240, 240, 204)); 

        // Add the panels to the tabbed pane
        tabbedPane.addTab(null, Dashboard);
        tabbedPane.setTabComponentAt(0, TabComponent("DashBoard"));

        tabbedPane.addTab(null, addEventPanel);
        tabbedPane.setTabComponentAt(1, TabComponent("Add Event"));

        tabbedPane.addTab(null, searchPanel);
        tabbedPane.setTabComponentAt(2, TabComponent("Search"));

        tabbedPane.addTab(null, sortPanel);
        tabbedPane.setTabComponentAt(3, TabComponent("Sort"));

        tabbedPane.addTab(null, historyPanel);
        tabbedPane.setTabComponentAt(4, TabComponent("History"));

        tabbedPane.addTab(null, summaryPanel);
        tabbedPane.setTabComponentAt(5, TabComponent("Generate Summary"));

        add(tabbedPane);   
    }

   /**
 * Creates a tab component with a given title for the tabbed pane.
 *
 * @param title the title to display on the tab component
 * @return a JPanel component with the specified title
 */ 
    private Component TabComponent(String title) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
        JLabel label = new JLabel(title);
        label.setForeground(Color.BLACK); 
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label);
        return panel;
    }


/**
 * Creates the dashboard panel with a table to display events and buttons for actions.
 *
 * @return a JPanel containing the dashboard layout with event table and buttons
 */
    private JPanel createDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); 

        // Define table columns
        String[] columns = {"Title", "Description","Start Time", "End Time", "Location", "Priority"};
        tableModel = new DefaultTableModel(columns, 0);  
        eventTable = new JTable(tableModel); 
        JScrollPane scrollPane = new JScrollPane(eventTable);  
        panel.add(scrollPane, BorderLayout.CENTER);  

   
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 255, 255));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(100, 149, 237)); 
        refreshButton.setForeground(Color.BLACK);
        JButton removeButton = new JButton("Remove Event");
        removeButton.setBackground(new Color(100, 149, 237)); 
        removeButton.setForeground(Color.BLACK);
        JButton updateButton = new JButton("Update Event");
        updateButton.setBackground(new Color(100, 149, 237)); 
        updateButton.setForeground(Color.BLACK);

        buttonPanel.add(refreshButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);
        panel.add(buttonPanel, BorderLayout.SOUTH); 

        // Add action listeners for buttons
        refreshButton.addActionListener(e -> refreshEventTable());
        removeButton.addActionListener(e -> removeSelectedEvent());
        updateButton.addActionListener(e -> updateSelectedEvent());

        return panel;
        
    }


   /**
 * Creates the panel for adding new events, including form fields and a button to submit the event.
 *
 * @return a JPanel with the form fields for adding events
 */
    private JPanel createAddEventPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));  
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);  


        // Initialize form fields and spinners
        titleField = new JTextField(20);
        locationField = new JTextField(20);
        descriptionField = new JTextField(20);
        dateSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        priorityCombo = new JComboBox<>(new String[]{"High", "Low"});

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(dateEditor);
        
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);
        
        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);

      
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        panel.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        panel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        panel.add(dateSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        panel.add(startTimeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1;
        panel.add(endTimeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1;
        panel.add(priorityCombo, gbc);

        JButton addButton = new JButton("Add Event");
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> addEvent());  

        return panel;
    }


   /**
 * Creates the search panel for searching events by various attributes.
 *
 * @return a JPanel with search options and a result area
 */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

       
        JPanel searchOptionsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        String[] searchOptions = {"Title", "Location", "Priority", "Description", "Date"};
        JComboBox<String> searchAttributeCombo = new JComboBox<>(searchOptions);
        JTextField searchValueField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setForeground(Color.BLACK);
        searchButton.setFont(searchButton.getFont().deriveFont(Font.BOLD));

        searchOptionsPanel.add(new JLabel("Search by:"));
        searchOptionsPanel.add(searchAttributeCombo);
        searchOptionsPanel.add(new JLabel("Search value:"));
        searchOptionsPanel.add(searchValueField);
        searchOptionsPanel.add(new JLabel(""));
        searchOptionsPanel.add(searchButton);

        panel.add(searchOptionsPanel, BorderLayout.NORTH);  
    
        JTextArea resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        
        searchButton.addActionListener(e -> {
            String attribute = (String) searchAttributeCombo.getSelectedItem();
            String value = searchValueField.getText();
            List<Event> events = calendarManager.viewEvents(attribute, value);
            resultArea.setText("");  
            for (Event event : events) {
                resultArea.append(event.toString() + "\n\n");  
            }
        });
       

        return panel;
    }


    /**
 * Creates the panel for sorting events based on different criteria.
 *
 * @return a JPanel with sorting options and a table to display sorted events
 */
    private JPanel createSortPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
       
        String[] sortOptions = {"Title", "Date", "Priority"};
        JComboBox<String> sortCriteriaCombo = new JComboBox<>(sortOptions);
        JButton sortButton = new JButton("Sort");
        sortButton.setForeground(Color.BLACK);
        sortButton.setFont(sortButton.getFont().deriveFont(Font.BOLD));
        
        
    
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.add(new JLabel("Sort by:"));
        controlsPanel.add(sortCriteriaCombo);
        controlsPanel.add(sortButton);
        
        panel.add(controlsPanel, BorderLayout.NORTH);
        
       
        String[] columns = {"Title", "Start Time", "End Time", "Location", "Priority"};
        DefaultTableModel sortTableModel = new DefaultTableModel(columns, 0);
        JTable sortEventTable = new JTable(sortTableModel);
        JScrollPane scrollPane = new JScrollPane(sortEventTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
       
        sortButton.addActionListener(e -> {
            String criteria = (String) sortCriteriaCombo.getSelectedItem();
            List<Event> sortedEvents = calendarManager.sortEvents(criteria);
            
            sortTableModel.setRowCount(0); 
            for (Event event : sortedEvents) {
                Object[] row = {
                    event.getTitle(),
                    event.getStartTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
                    event.getEndTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
                    event.getLocation(),
                    event.getPriority()
                };
                sortTableModel.addRow(row);
            }
        });

        return panel;
    }

    
    /**
 * Creates the panel for generating a summary of events within a specified date range.
 *
 * @return a JPanel with input fields for date range and a text area to display the summary
 */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout());

        JTextField startDateField = new JTextField(10);
        JTextField endDateField = new JTextField(10);
        JButton generateButton = new JButton("Generate Summary");
        generateButton.setForeground(Color.BLACK);
        generateButton.setFont(generateButton.getFont().deriveFont(Font.BOLD));

        inputPanel.add(new JLabel("Start Date (MM/dd/yyyy):"));
        inputPanel.add(startDateField);
        inputPanel.add(new JLabel("End Date (MM/dd/yyyy):"));
        inputPanel.add(endDateField);
        inputPanel.add(generateButton);

        summaryTextArea = new JTextArea(20, 40);
        summaryTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(summaryTextArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        generateButton.addActionListener(e -> generateSummary(startDateField.getText(), endDateField.getText()));

        return panel;
    }


      
    /**
 * Creates the panel for for storing past events moved from the dashboard.
 *
 * @return a JPanel with input fields for date range and a text area to display the history
 */
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        historyTextArea = new JTextArea(20, 40);
        historyTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh History");
        refreshButton.addActionListener(e -> refreshHistory());
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

 

/**
 * Adds a new event to the calendar based on user input from the form fields.
 *
 * @throws IllegalArgumentException if the provided date and time are invalid
 * @throws Exception if any other unexpected error occurs during event addition
 */
    
 private void addEvent() {
    try {
        String title = titleField.getText();
        String location = locationField.getText();
        String description = descriptionField.getText();
        
        
        java.util.Date dateValue = (java.util.Date) dateSpinner.getValue();
        LocalDate date = dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        
        java.util.Date startTimeValue = (java.util.Date) startTimeSpinner.getValue();
        java.util.Date endTimeValue = (java.util.Date) endTimeSpinner.getValue();
        
        LocalTime startTime = startTimeValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime endTime = endTimeValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        
        
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
        
        String priority = (String) priorityCombo.getSelectedItem();

        Event newEvent = new Event(title, startDateTime, endDateTime, location, UUID.randomUUID().toString(), description, priority);

        boolean added = calendarManager.addEvent(newEvent);

        if (added) {
            JOptionPane.showMessageDialog(this, "Event added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearAddEventFields(); 
            refreshEventTable();
        } else {
            JOptionPane.showMessageDialog(this, 
                "The selected date and time (" + startDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")) + 
                ") is already used by another event. Please choose a different time.",
                "Time Slot Occupied", 
                JOptionPane.WARNING_MESSAGE);
        }

    }catch (IllegalArgumentException ex) {
    JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Time", JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error adding event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}


/**
* Removes the selected event from the event table.
 @throws DateTimeParseException if the start time of the event cannot be parsed
**/
private void removeSelectedEvent() {
       
    int selectedRow = eventTable.getSelectedRow();
    System.out.println("Selected Row Index: " + selectedRow);
    if (selectedRow >= 0) {
        String startTimeStr = (String) tableModel.getValueAt(selectedRow, 2); 
        
        try {
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
            calendarManager.removeEvent(startTime.toString());
            refreshEventTable();
            JOptionPane.showMessageDialog(this, "Event removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Error parsing event date: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select an event to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
    }
}

/**
* Updates the selected event from the event table.
 @throws DateTimeParseException if the start time of the event cannot be parsed
**/
private void updateSelectedEvent() {
    int selectedRow = eventTable.getSelectedRow();
    if (selectedRow >= 0) {
        String startTimeStr = (String) tableModel.getValueAt(selectedRow, 1); 
        
        try {
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
            Event event = calendarManager.searchEventByDatetime(startTime);
            
            if (event != null) {
            JDialog updateDialog = new JDialog(this, "Update Event", true);
            updateDialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Initialize fields with existing event data
            JTextField updateTitleField = new JTextField(event.getTitle(), 20);
            JTextField updateLocationField = new JTextField(event.getLocation(), 20);
            JTextField updateDescriptionField = new JTextField(event.getDescription(), 20);
            JSpinner updateDateSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner updateStartTimeSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner updateEndTimeSpinner = new JSpinner(new SpinnerDateModel());
            JComboBox<String> updatePriorityCombo = new JComboBox<>(new String[]{"High", "Medium", "Low"});

            // Set date and time format for spinners
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(updateDateSpinner, "MM/dd/yyyy");
            updateDateSpinner.setEditor(dateEditor);
            updateDateSpinner.setValue(java.sql.Date.valueOf(event.getStartTime().toLocalDate()));

            JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(updateStartTimeSpinner, "HH:mm");
            updateStartTimeSpinner.setEditor(startTimeEditor);
            updateStartTimeSpinner.setValue(java.sql.Time.valueOf(event.getStartTime().toLocalTime()));

            JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(updateEndTimeSpinner, "HH:mm");
            updateEndTimeSpinner.setEditor(endTimeEditor);
            updateEndTimeSpinner.setValue(java.sql.Time.valueOf(event.getEndTime().toLocalTime()));

            updatePriorityCombo.setSelectedItem(event.getPriority());

            // Add components to dialog
            gbc.gridx = 0; gbc.gridy = 0;
            updateDialog.add(new JLabel("Title:"), gbc);
            gbc.gridx = 1;
            updateDialog.add(updateTitleField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            updateDialog.add(new JLabel("Location:"), gbc);
            gbc.gridx = 1;
            updateDialog.add(updateLocationField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            updateDialog.add(new JLabel("Description:"), gbc);
            gbc.gridx = 1;
            updateDialog.add(updateDescriptionField, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            updateDialog.add(new JLabel("Date:"), gbc);
            gbc.gridx = 1;
            updateDialog.add(updateDateSpinner, gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            updateDialog.add(new JLabel("Start Time:"), gbc);
            gbc.gridx = 1;
            updateDialog.add(updateStartTimeSpinner, gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            updateDialog.add(new JLabel("End Time:"), gbc);
            gbc.gridx = 1;
            updateDialog.add(updateEndTimeSpinner, gbc);

            gbc.gridx = 0; gbc.gridy = 6;
            updateDialog.add(new JLabel("Priority:"), gbc);
            gbc.gridx = 1;
            updateDialog.add(updatePriorityCombo, gbc);

            JButton updateButton = new JButton("Update");
            gbc.gridx = 0; gbc.gridy = 7;
            gbc.gridwidth = 2;
            updateDialog.add(updateButton, gbc);

            updateButton.addActionListener(e -> {
                try {
                    String newTitle = updateTitleField.getText();
                    String newLocation = updateLocationField.getText();
                    String newDescription = updateDescriptionField.getText();
                    LocalDate newDate = ((java.util.Date) updateDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalTime newStartTime = ((java.util.Date) updateStartTimeSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                    LocalTime newEndTime = ((java.util.Date) updateEndTimeSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                    LocalDateTime newStartDateTime = LocalDateTime.of(newDate, newStartTime);
                    LocalDateTime newEndDateTime = LocalDateTime.of(newDate, newEndTime);
                    String newPriority = (String) updatePriorityCombo.getSelectedItem();

                    Event updatedEvent = new Event(newTitle, newStartDateTime, newEndDateTime, newLocation, event.getId(), newDescription, newPriority);
                    calendarManager.removeEvent(startTime.toString());
                    calendarManager.addEvent(updatedEvent);

                    updateDialog.dispose();
                    refreshEventTable();
                    JOptionPane.showMessageDialog(this, "Event updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error updating event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            updateDialog.pack();
            updateDialog.setLocationRelativeTo(this);  
            updateDialog.setVisible(true);
        }else {
            JOptionPane.showMessageDialog(this, "Event not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (DateTimeParseException ex) {
        JOptionPane.showMessageDialog(this, "Error parsing event date: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
} else {
    JOptionPane.showMessageDialog(this, "Please select an event to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
}
}

 // Refresh the event table
 private void refreshEventTable() {
    SwingUtilities.invokeLater(() -> {
        calendarManager.refreshEvents();
        tableModel.setRowCount(0);
        List<Event> events = calendarManager.sortEvents("date");
        
        for (Event event : events) {
            Object[] row = {
                event.getTitle(),
                event.getDescription(),
                event.getStartTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
                event.getEndTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
                event.getLocation(),
                event.getPriority()
            };
            tableModel.addRow(row);
        }
    });
}

/**
 * Clears the fields in the "Add Event" form.
 * <p>
 * This method resets all input fields in the event creation 
 * form, including the title, location, description, date, start time, end time, and priority. 
 * It ensures that the form is empty and ready for new input.
 * </p>
 */
private void clearAddEventFields() {
    titleField.setText("");
    locationField.setText("");
    descriptionField.setText("");
    dateSpinner.setValue(new java.util.Date());
    startTimeSpinner.setValue(new java.util.Date());
    endTimeSpinner.setValue(new java.util.Date());
    priorityCombo.setSelectedIndex(0);
}

/**
 * Refreshes the history view with past events.
 * <p>
 * This method retrieves historical events from the {@link Calendar}, 
 * clears the current text area, and appends each event to the history text area. 
 * It also refreshes the event table to ensure consistency between the event history and the current event list.
 * </p>
 */
private void refreshHistory() {
    calendarManager.refreshEvents();
    historyTextArea.setText("");
    for (Event event : calendarManager.getHistoryEvents()) {
        historyTextArea.append(event.toString() + "\n\n");
    }
    refreshEventTable(); 
}

/**
 * Generates and displays a summary of events within the specified date range.
 *
 * @param startDateStr the start date of the summary period, in the format "MM/dd/yyyy"
 * @param endDateStr the end date of the summary period, in the format "MM/dd/yyyy"
 * @throws DateTimeParseException if the date strings are not in the expected format
 */
    private void generateSummary(String startDateStr, String endDateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            String summary = calendarManager.generateSummary(startDate, endDate);
            summaryTextArea.setText(summary);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM/dd/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new GUI().setVisible(true);
        });
    }
   
}