// Alayah Benjamin & Simiah Woods
// COMP 360 Programming Language : Project 2
// This project is displaying a General University Management System. This project will allow user to add student's name, ID, address and their degree track
//Demonstrates single inheritance: BSStudent, MSStudent, and PhDStudent
//Creating this project creating a GUI

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.*;
import java.util.List;


public class UniversityManagementSystem extends JFrame {
    // Creating the visuals within the GUI
    // This GUI will give visuals of registration, grading, and student logic and remain unchanged
    private static final Color NAVY = new Color(24, 43, 72);
    private static final Color BLUE = new Color(35, 85, 150);
    private static final Color BLUE_HOVER = new Color(50, 110, 180);
    private static final Color TEAL = new Color(0, 150, 160);
    private static final Color PAGE = new Color(240, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(30, 45, 65);
    private static final Color MUTED = new Color(75, 85, 100);
    private static final Color BORDER = new Color(195, 205, 220);
    private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);

    // Stores students by their unique ID so when it comes to duplicates they can eb detected quickly
    private final Map<String, Student> students = new LinkedHashMap<>();
    // The course catalog made is something we made already (predefined), but user can add more Course entries to expand it
    //The courses  will have their class and class number, course name, and credits they offer
    private final List<Course> catalog = Arrays.asList(
            new Course("COMP 163", "Introduction to Programming", 3),
            new Course("COMP 167", "Computer Program Desgin", 3),
            new Course("COMP 280", "Data Structures", 3),
            new Course("COMP 285", "Design and Analysis of Algorithms", 3),
            new Course("COMP 267", "Data Base Design", 3),
            new Course("COMP 360", "Programming Languages", 3),
            new Course("COMP 365", "Artificial Intelligence & Machine Learning", 3),
            new Course("COMP 350", "Operating Systems", 3)
    );

    // Creating a drop-down on the menus to allow sharing the student data across the registration, records, and grading tabs
    private final JComboBox<Student> registeringStudents = new JComboBox<>();
    private final JComboBox<Student> StudentRecords = new JComboBox<>();
    private final JComboBox<Student> StudentGrades = new JComboBox<>();
    private final JComboBox<Course> courseChoice = new JComboBox<>();
    private final JComboBox<String> TypeofAddition= new JComboBox<>(new String[] {
            "Course", "MS Project", "PhD Dissertation"
    });
    private final JTextField TitleofAddition = new JTextField(22);
    private final JLabel TitleLabelofAddition = new JLabel("Project/Dissertation Title:");
    private final JTextArea RegistrationResult = outputArea();
    private final JTextArea studentDetails = outputArea();
    private final JComboBox<Course> Coursegrading = new JComboBox<>();
    private final JComboBox<String> GradingChoices = new JComboBox<>(new String[] {"A", "B", "C", "D", "F"});

    // Building the main window when they run the program, there will be tabs and connects to the selections
    public UniversityManagementSystem(){
        super("General University Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 740);
        getContentPane().setBackground(PAGE);
        setMinimumSize(new Dimension(790,570));
        setLocationRelativeTo(null);

        for (Course course : catalog) courseChoice.addItem(course);

        // Each tab located in the GUI System given their own method to keep the GUI code organized
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Add Student", studentTab());
        tabs.addTab("Registration", registrationTab());
        tabs.addTab("Student Records", recordsTab());
        tabs.addTab("Available Courses", catalogTab());
        tabs.addTab("Enter Course Grade", gradesTab());
        tabs.setFont(new Font("AlasassyCaps", Font.BOLD, 13));
        tabs.setBackground(WHITE);
        tabs.setForeground(NAVY);
        tabs.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(tabs);
        updateAdditionControls();
        TypeofAddition.addActionListener(e -> updateAdditionControls());
        registeringStudents.addActionListener(e -> updateRegistrationResult());
        StudentRecords.addActionListener(e-> updateStudentDetails());
        StudentGrades.addActionListener(e-> refreshGradeCourses());
        applyTheme(getContentPane());
    }
    // Creating the student entry information for the Add Student tab/button
    private JPanel studentTab(){
        JPanel panel = paddedPanel();
        panel.setLayout(new BorderLayout(12,12));
        panel.add(heading("Add a New Student"), BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = constraints();
        JTextField fName = new JTextField(22);
        JTextField lName = new JTextField(22);
        JTextField studentID = new JTextField(22);
        JTextField studentPhoneNumber = new JTextField(22);
        JTextField studentAddress = new JTextField(22);
        JComboBox<String> addition = new JComboBox<>(new String[] {"Undergraduate (BS)", "Master's (MS)", "Doctoral (PhD)"});

        //The order that the information would be displayed for the user to add student
        addField(form, c, 0, "First Name:", fName);
        addField(form, c, 1, "Last Name:", lName);
        addField(form, c, 2, "Student ID:", studentID);
        addField(form, c, 3, "Phone Number:", studentPhoneNumber);
        addField(form, c, 4, "Address:", studentAddress);
        addField(form, c, 5, "Student Type:", addition);
        JButton addButton = new JButton("Add Student");
        c.gridx = 1; c.gridy = 6; c.anchor = GridBagConstraints.WEST;
        form.add(addButton, c);
        addButton.addActionListener(e ->{
            String firstName = fName.getText().trim();
            String lastName = lName.getText().trim();
            String studentId = studentID.getText().trim();
            //Validation in order to prevent incomplete records
            if (firstName.isEmpty() || lastName.isEmpty() || studentId.isEmpty()){
                error("First Name, Last Name, and Student ID are required.");
                return;
            }
            //This method is used to prevent multiple students from having the same ID, everyone should have their own different one
            if (students.containsKey(studentId)){
                error("Student ID already exists. Please use a unique ID");
                return;
            }
            // Using polymorphism, making the selected degree determine the students subclass
            String selected = (String) addition.getSelectedItem();
            Student student;
            if (selected.startsWith("Undergraduate")){
                student = new BSStudent(firstName, lastName, studentId,studentPhoneNumber.getText().trim(), studentAddress.getText().trim());
            } else if (selected.startsWith("Master")){
                student = new MSStudent(firstName, lastName, studentId,studentPhoneNumber.getText().trim(), studentAddress.getText().trim());
            } else {
                student = new MSStudent.PhDStudent(firstName, lastName, studentId,studentPhoneNumber.getText().trim(), studentAddress.getText().trim());
            }
            // Saving the new student information and updating all the student selector tabs
            students.put(studentId, student);
            registeringStudents.addItem(student);
            StudentRecords.addItem(student);
            StudentGrades.addItem(student);
            fName.setText(""); lName.setText(""); studentID.setText("");
            studentPhoneNumber.setText(""); studentAddress.setText("");
            JOptionPane.showMessageDialog(this, "Student information was added successfully:\n" + student.basicSummary());
        });
        panel.add(form, BorderLayout.CENTER);
        panel.add(new JLabel("Required: First Name, Last Name, and Unique Student ID."), BorderLayout.SOUTH);
        return panel;
    }
    // Registration Tab: showing all the registration choices for courses, MS projects, and dissertations
    private JPanel registrationTab(){
        JPanel panel = paddedPanel();
        panel.setLayout(new BorderLayout(12,12));
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = constraints();
        addField(top, c, 0, "Student:", registeringStudents);
        addField(top, c, 1, "Registration type:", TypeofAddition);
        addField(top, c, 2, "Available course:", courseChoice);
        addField(top, c, 3, TitleLabelofAddition, TitleofAddition);
        JButton register = new JButton("Register");
        c.gridx = 1; c.gridy = 4; c.anchor = GridBagConstraints.WEST;
        top.add(register, c);
        register.addActionListener(e->registerSelection());
        JPanel header = new JPanel(new BorderLayout());
        header.add(heading("Course, Project, and Dissertation Registration"), BorderLayout.NORTH);
        header.add(top, BorderLayout.CENTER);
        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(RegistrationResult), BorderLayout.CENTER);
        updateRegistrationResult();
        return panel;
    }
    // Handles the Register button; the Student subclasses enforce eligibility
    private void registerSelection(){
        Student student = (Student) registeringStudents.getSelectedItem();
        if (student == null) { error("Add a student before registering for courses."); return; }
        String choice = (String) TypeofAddition.getSelectedItem();
        try {
           if ("Course".equals(choice)) {
               Course course = (Course) courseChoice.getSelectedItem();
               if (course == null) { error("No course is available."); return; }
               student.registerCourse(course);
           } else {
               String title = TitleofAddition.getText().trim();
               if (title.isEmpty()) { error("Enter a project or dissertation title."); return; }
               if ("MS Project".equals(choice)) student.registerMSProject(title);
               else student.registerDissertation(title);
               TitleofAddition.setText("");
               }
           updateRegistrationResult();
           updateStudentDetails();
           refreshGradeCourses();
           JOptionPane.showMessageDialog(this,
                   "Registration successful! \n\n" + student.registrationSummary(),
                            "Registered Student", JOptionPane.INFORMATION_MESSAGE);
           } catch (IllegalArgumentException | IllegalStateException ex){
            error(ex.getMessage());
        }
    }

    // Enables the course selector or project-title field as appropriate
    private void updateAdditionControls(){
        boolean course = "Course".equals(TypeofAddition.getSelectedItem());
        courseChoice.setEnabled(course);
        TitleofAddition.setEnabled(!course);
        TitleLabelofAddition.setEnabled(!course);
    }

    // Allows users to select a students name and view their information and their full academic record
    private JPanel recordsTab(){
        JPanel panel = paddedPanel();
        panel.setLayout(new BorderLayout(12,12));
        JPanel heading = new JPanel(new BorderLayout(10,10));
        heading.add(heading("View Student Information"), BorderLayout.NORTH);
        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selector.add(new JLabel("Select student:"));
        selector.add(StudentRecords);
        JButton view = new JButton("View Record");
        view.addActionListener(e-> updateStudentDetails());
        selector.add(view);
        heading.add(selector, BorderLayout.SOUTH);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(new JScrollPane(studentDetails), BorderLayout.CENTER);
        updateStudentDetails();
        return panel;
    }

    // Displays the available catalog in a read only table
    private JPanel catalogTab(){
        JPanel panel = paddedPanel();
        panel.setLayout(new BorderLayout(12,12));
        panel.add(heading("All Available Courses"), BorderLayout.NORTH);
        DefaultTableModel model = new DefaultTableModel(new String[] {"Course Code", "Course Title", "Credit Hours"}, 0){
            @Override public boolean isCellEditable(int row, int column) {return false;}
        };
        for (Course course : catalog) model.addRow(new Object[]{course.code, course.title, course.credits});
        JTable table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(BODY_FONT);
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setSelectionBackground(new Color(215,231,251));
        table.setSelectionForeground(NAVY);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        tableHeader.setBackground(NAVY);
        tableHeader.setForeground(WHITE);
        tableHeader.setPreferredSize(new Dimension(0, 42));
        table.setFillsViewportHeight(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
    // Records letter grades for the courses a student has already been registered for
    private JPanel gradesTab(){
        JPanel panel = paddedPanel();
        panel.setLayout(new BorderLayout(12,12));
        panel.add(heading("Record a Course Grade and Calculate GPA"), BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = constraints();
        addField(form, c, 0, "Student:", StudentGrades);
        addField(form, c, 1, "Registered course:", Coursegrading);
        addField(form, c, 2, "Letter grade:", GradingChoices);
        JButton save = new JButton("Save Grade");
        c.gridx = 1; c.gridy = 3; c.anchor = GridBagConstraints.WEST;
        form.add(save, c);
        save.addActionListener(e -> {
            Student student = (Student) StudentGrades.getSelectedItem();
            Course course = (Course) Coursegrading.getSelectedItem();
            if (student == null || course == null){
                error("Select a student with a registered course first.");
                return;
            }
            student.setGrade(course.code, (String) GradingChoices.getSelectedItem());
            updateStudentDetails();
            updateRegistrationResult();
            JOptionPane.showMessageDialog(this, "Grade saved for " + course.code + ".\nCurrent GPA: " + student.gpaText());

        });
        panel.add(form, BorderLayout.CENTER);
        panel.add(new JLabel("GPA uses graded course credit hours. Ungraded courses do not affect GPA."), BorderLayout.SOUTH);
        return panel;
    }
    //Method made to only have courses registered by the chosen student will be shown in the grade selector
    private void refreshGradeCourses(){
        Student student = (Student) StudentGrades.getSelectedItem();
        Coursegrading.removeAllItems();
        if (student != null) for (Course course : student.courses.values()) Coursegrading.addItem(course);
    }

    //Updates the text area after a registration or a student selection
    private void updateRegistrationResult(){
        Student student = (Student) registeringStudents.getSelectedItem();
        RegistrationResult.setText(student == null ? "Add a student to get started" : student.registrationSummary());
        RegistrationResult.setCaretPosition(0);
    }

    //Refreshes the displayed student records, including their GPA
    private void updateStudentDetails(){
        Student student = (Student) StudentRecords.getSelectedItem();
        studentDetails.setText(student == null ? "No students have been added yet." : student.fullRecord());
        studentDetails.setCaretPosition(0);
    }
    // Shared layout helpers keep spacing and appearance consistent
    private static JPanel paddedPanel(){
        JPanel panel = new JPanel();
        panel.setBackground(PAGE);
        panel.setBorder(new EmptyBorder(24,28,24,28));
        return panel;
    }
    private static JLabel heading(String text){
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setForeground(NAVY);
        label.setBorder(new EmptyBorder(0,0,12,0));
        return label;
    }

    private static JTextArea outputArea(){
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN,14));
        area.setBackground(WHITE);
        area.setForeground(TEXT);
        area.setSelectionColor(new Color(194,225,245));
        area.setBorder(new EmptyBorder(12,12,12,12));
        return area;
    }

    //GridBagConstraints position form labels and input fields within two columns
    private static GridBagConstraints constraints(){
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7,7,7,7);
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private static void addField(JPanel panel, GridBagConstraints c, int row, String label, JComponent input){
        addField(panel, c, row, new JLabel(label),input);
    }
    private static void addField(JPanel panel, GridBagConstraints c, int row, JLabel label, JComponent input){
        c.gridy = row; c.gridx = 0; c.weightx = 0;
        panel.add(label,c );
        c.gridx = 1; c.weightx =1;
        panel.add(input, c);
    }

    //Recursive used to pass every existing tab and control gets the same look
    //Walks through nested Swing components to style buttons, inputs, and labels
    private static void applyTheme(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            panel.setBackground(PAGE);
        }
        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            if (label.getFont().getSize() < 18) {
                label.setFont(new Font("SansSerif", Font.BOLD, 13));
                label.setForeground(MUTED);
            }
        }
        if (component instanceof JButton) {
            JButton button = (JButton) component;
            button.setFont(new Font("SansSerif", Font.BOLD, 14));
            button.setBackground(BLUE);
            button.setForeground(Color.BLACK);
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BLUE, 1, true),
                    new EmptyBorder(10, 20, 10, 20)));
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (button.isEnabled()) button.setBackground(BLUE_HOVER);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setBackground(BLUE);
                }
            });
        }
            if (component instanceof JTextField) {
                JTextField field = (JTextField) component;
                field.setFont(BODY_FONT);
                field.setForeground(TEXT);
                field.setBackground(WHITE);
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER, 1, true), new EmptyBorder(8, 10, 8, 10)));
            }
            if (component instanceof JComboBox<?>) {
                JComboBox<?> combo = (JComboBox<?>) component;
                combo.setFont(BODY_FONT);
                combo.setBackground(WHITE);
                combo.setForeground(TEXT);
                combo.setBorder(new LineBorder(BORDER, 1, true));
            }
            if (component instanceof JScrollPane){
                JScrollPane scroll  = (JScrollPane) component;
                scroll.setBorder(new LineBorder(BORDER,1,true));
                scroll.getViewport().setBackground(WHITE);
            }
            if (component instanceof Container) {
                for (Component child : ((Container) component).getComponents()) {
                    applyTheme(child);
                }
            }
        }
        //Displays validation and registration errors in a popup dialog
        private void error (String message){
            JOptionPane.showMessageDialog(this, message, "Invalid Request", JOptionPane.ERROR_MESSAGE);
        }

        public static void main (String[]args){
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }
                UIManager.put("TabbedPane.selected", new Color(222, 236, 253));
                UIManager.put("TabbedPane.contentAreaColor", PAGE);
                UIManager.put("TabbedPane.focus", TEAL);
                UIManager.put("ComboBox.selectionBackground", BLUE);
                UIManager.put("ComboBox.selectionForeground", WHITE);
                new UniversityManagementSystem().setVisible(true);
            });
        }
    }
    //Creating classes , This class is the Course: show their unique code, title, and the amount of credits
    class Course {
        final String code;
        final String title;
        final int credits;

        Course(String code, String title, int credits) {
            this.code = code;
            this.title = title;
            this.credits = credits;
        }
        @Override public String toString(){ return code + " - " + title; }

    }
    // Parent class: shared data and operation are inherited by the different student degree paths
    abstract class Student {
        final String firstName;
        final String lastName;
        final String id;
        final String phone;
        final String address;
        //Maps used to track each student's registered courses and assigned grades
        final Map<String, Course> courses = new LinkedHashMap<>();
        final Map<String, String> grades = new LinkedHashMap<>();
        String msProject;
        String dissertation;

        Student(String firstName, String lastName, String id, String phone, String address){
            this.firstName = firstName;
            this.lastName = lastName;
            this.id = id;
            this.phone = phone;
            this.address = address;
        }

        //Subclasses provide their own degree description
        abstract String studentType();
        // Preventing the same student from registering for a course more than once
        void registerCourse(Course course){
            if (courses.containsKey(course.code)){
               throw new IllegalStateException("This student is already registered for " + course.code + ".");
        }
            courses.put(course.code, course);
    }
    //Default restrictions are overridden by eligible graduate subclasses
        void registerMSProject(String title){
            throw new IllegalStateException("Only MS and PhD students can register for an MS Project.");
        }
        void registerDissertation(String title){
            throw new IllegalStateException("Only PhD students can register for an Dissertation.");
        }

        //Grades are only allowed for registered courses and valid letter values
        void setGrade(String courseCode, String grade){
            if (!courses.containsKey(courseCode)){
                throw new IllegalArgumentException("Student is not registered for " + courseCode + ".");
            }
            if (!Arrays.asList("A", "B", "C", "D", "F").contains(grade)) {
                throw new IllegalArgumentException("Invalid letter grade.");
            }
            grades.put(courseCode, grade);
        }
        //Computes a credit weighted GPA, A=4, B=3, C=2, D=1, F=0
        // Courses without a grade are excluded from the calculation
        String gpaText() {
            int gradedCredits = 0;
            int totalPoints = 0;
            for (Map.Entry<String, String> entry : grades.entrySet()) {
                Course course = courses.get(entry.getKey());
                if (course == null) continue;
                int points;
                switch (entry.getValue()) {
                    case "A":
                        points = 4;
                        break;
                    case "B":
                        points = 3;
                        break;
                    case "C":
                        points = 2;
                        break;
                    case "D":
                        points = 1;
                        break;
                    default:
                        points = 0;
                }
                gradedCredits += course.credits;
                totalPoints += points * course.credits;
            }
            return gradedCredits == 0 ? "N/A (no graded courses found)" :
                    String.format(Locale.US,"%.2f", (double) totalPoints / gradedCredits);
        }
        String basicSummary(){
            return "Name: " + firstName + " " + lastName + "\n" + "Stduent ID: " + id + "\n" + "Type: " + studentType();
        }

        String registrationSummary() {
            StringBuilder stringbuild = new StringBuilder();
            stringbuild.append("First Name: ").append(firstName).append("\n")
                    .append("Last Name: ").append(lastName).append("\n")
                    .append("Student ID: ").append(id).append("\n\n")
                    .append("Student Type: ").append(studentType()).append("\n\n")
                    .append("REGISTERED COURSES\n")
                    .append("------------------------------\n");
            if (courses.isEmpty()) stringbuild.append("No courses are registered for.\n");
            else for (Course course : courses.values()) {
                stringbuild.append(course.code).append(" - ").append(course.title)
                        .append("(Grade: ").append(grades.getOrDefault(course.code, "Not assigned"))
                        .append(")\n");
            }
            stringbuild.append("\nMS Project: ").append(msProject == null ? "None" : msProject).append("\n")
                    .append("PhD Dissertation: ").append(dissertation == null ? "None" : dissertation).append("\n");
            return stringbuild.toString();
        }
        // Combines contact details, GPA, and registration itno one report
        String fullRecord(){
            return basicSummary() + "\n" + "Phone: " + (phone.isEmpty() ? "Not provided" : phone) + "\n" +
                    "Address: " + (address.isEmpty() ? "Not provided" : address) + "\n" +
                    "GPA: " + gpaText() + "\n\n" + registrationSummary().substring(registrationSummary().indexOf("REGISTERED COURSES"));
        }
        @Override public String toString() {return lastName + ", " + firstName + "(" + id + ")"; }
    }
    //Undergraduate: course registration only
    class BSStudent extends Student{
        BSStudent(String f, String l, String id, String p, String a) {super(f, l, id, p, a); }
        @Override String studentType() { return "Undergraduate (BS)"; }
    }

    //Registration for only Masters Students
    class MSStudent extends Student{
        MSStudent(String f, String l, String id, String p, String a) {super(f, l, id, p, a); }
        @Override String studentType() { return "Master's (MS)"; }
        @Override void registerMSProject(String title){
            if (msProject != null) throw new IllegalArgumentException("An MS Project is already registered");
            msProject = title;
        }
        //Registration for only PhD Students
    static class PhDStudent extends Student {
            PhDStudent(String f, String l, String id, String p, String a) {
                super(f, l, id, p, a);
            }

            @Override
            String studentType() {
                return "Doctoral (PhD)";
            }

            @Override
            void registerMSProject(String title) {
                if (msProject != null) throw new IllegalArgumentException("An MS Project is already registered");
                msProject = title;
            }

            @Override void registerDissertation(String title){
                if (dissertation != null) throw new IllegalArgumentException("A dissertation is already registered");
                dissertation = title;
            }
        }
    }