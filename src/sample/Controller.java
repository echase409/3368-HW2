package sample;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller implements Initializable {
    @FXML private JFXButton createTableButton;
    @FXML private JFXButton loadDataButton;
    @FXML private JFXButton deleteTableButton;
    @FXML private ToolBar toolbar;
    @FXML private JFXListView<Student> studentListView;
    @FXML private JFXTextField firstNameTextField;
    @FXML private JFXTextField lastNameTextField;
    @FXML private JFXTextField ageTextField;
    @FXML private JFXTextField majorTextField;
    @FXML private JFXTextField gpaTextField;
    @FXML private JFXButton addStudentButton;
    @FXML private JFXButton updateStudentButton;
    @FXML private JFXButton deleteStudentButton;
    @FXML private JFXButton confirmButton;
    @FXML private JFXButton clearButton;
    @FXML private JFXButton cancelButton;
    @FXML private JFXButton yesButton;
    @FXML private JFXButton noButton;
    @FXML private AnchorPane topHalf;
    @FXML private VBox sideBar;
    @FXML private HBox bottomBar;
    @FXML private HBox bottomBar2;
    @FXML private JFXButton filterButton;
    @FXML private VBox filterContainer;
    @FXML private JFXTextField minAgeTextField;
    @FXML private JFXTextField maxAgeTextField;
    @FXML private JFXTextField minGpaTextField;
    @FXML private JFXTextField maxGpaTextField;
    @FXML private JFXTextField majorFilterTextField;
    @FXML private JFXComboBox<String> ageFilterSelection;
    @FXML private JFXComboBox<String> gpaFilterSelection;
    @FXML private JFXComboBox<String> majorFilterSelection;
    @FXML private NumberValidator minAgeFilterValidation;
    @FXML private NumberValidator maxAgeFilterValidation;
    @FXML private NumberValidator minGpaFilterValidation;
    @FXML private NumberValidator maxGpaFilterValidation;

    // DB Stuff
    final String hostname = "student-db.csyipcd3jqnc.us-east-1.rds.amazonaws.com";
    final String dbname = "student_db";
    final String port = "3306";
    final String username = "admin";
    final String password = "dbpassword";
    final String AWS_URL = "jdbc:mysql://" + hostname + ":" + port + "/" + dbname + "?user=" + username + "&password=" + password;

    // Generates a random GPA between 1.00 and 4.00
    public float generateRandomGpa() {
        return (float) ((float)(Math.random() * ((3.00 - 1.50) + 1)) + 1.50);
    }

    // Generates a random age between 18 and 50
    public int generateRandomAge() {
        return (int)(Math.random() * ((24 - 18) + 1)) + 18;
    }

    public void clearFields() {
        firstNameTextField.clear();
        lastNameTextField.clear();
        ageTextField.clear();
        gpaTextField.clear();
        majorTextField.clear();
    }

    public void clearFilters() {
        ageFilterSelection.getSelectionModel().selectFirst();
        gpaFilterSelection.getSelectionModel().selectFirst();
        majorFilterSelection.getSelectionModel().selectFirst();
        minAgeTextField.clear();
        maxAgeTextField.clear();
        minGpaTextField.clear();
        maxGpaTextField.clear();
        majorFilterTextField.clear();
        minAgeTextField.setVisible(false);
        minGpaTextField.setVisible(false);
    }

    public void disable() {
        sideBar.setDisable(true);
        bottomBar.setVisible(false);
        deleteTableButton.setDisable(true);
        loadDataButton.setDisable(true);
        filterButton.setDisable(true);
        filterContainer.setVisible(false);
        firstNameTextField.setDisable(true);
        lastNameTextField.setDisable(true);
        ageTextField.setDisable(true);
        gpaTextField.setDisable(true);
        majorTextField.setDisable(true);
    }

    public void enable() {
        addStudentButton.setDisable(false);
        updateStudentButton.setDisable(false);
        deleteStudentButton.setDisable(false);
        deleteTableButton.setDisable(false);
        loadDataButton.setDisable(false);
        filterButton.setDisable(false);
        firstNameTextField.setDisable(false);
        lastNameTextField.setDisable(false);
        ageTextField.setDisable(false);
        gpaTextField.setDisable(false);
        majorTextField.setDisable(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bottomBar.setVisible(false);
        filterContainer.setVisible(false);
        bottomBar2.setVisible(false);
        AtomicInteger toggleCount = new AtomicInteger(0);

        ageFilterSelection.setItems(FXCollections.observableArrayList("-", "Equals", "Between", "Greater than", "Less than"));
        gpaFilterSelection.setItems(FXCollections.observableArrayList("-", "Equals", "Between", "Greater than", "Less than"));
        majorFilterSelection.setItems(FXCollections.observableArrayList("-", "Equals", "Contains", "Starts with", "Ends with"));
        ageFilterSelection.getSelectionModel().selectFirst();
        gpaFilterSelection.getSelectionModel().selectFirst();
        majorFilterSelection.getSelectionModel().selectFirst();
        minAgeTextField.setVisible(false);
        minGpaTextField.setVisible(false);
        minAgeTextField.getValidators().add(minAgeFilterValidation);
        maxAgeTextField.getValidators().add(maxAgeFilterValidation);
        minGpaTextField.getValidators().add(minGpaFilterValidation);
        maxGpaTextField.getValidators().add(maxGpaFilterValidation);

        // Linking Database items to the ListView
        ObservableList<Student> dbStudentList = FXCollections.observableArrayList();
        studentListView.setItems(dbStudentList);

        // Performing Initial Data Load
        try {
            Connection conn = DriverManager.getConnection(AWS_URL);

            Statement stmt = conn.createStatement();

            String sqlStatement = "SELECT * FROM Student";
            ResultSet result = stmt.executeQuery(sqlStatement);

            while (result.next()) {
                Student student = new Student();
                student.setFName(result.getString("FirstName"));
                student.setLName(result.getString("LastName"));
                student.setId(UUID.fromString(result.getString("Id")));
                student.setAge(Integer.parseInt(result.getString("Age")));
                student.setMajor(result.getString("Major"));
                student.setGpa(Float.parseFloat(result.getString("GPA")));
                dbStudentList.add(student);
            }

            stmt.close();
            conn.close();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            topHalf.setDisable(true);
            sideBar.setDisable(true);
            deleteTableButton.setDisable(true);
            loadDataButton.setDisable(true);
            firstNameTextField.setDisable(true);
            lastNameTextField.setDisable(true);
            ageTextField.setDisable(true);
            gpaTextField.setDisable(true);
            majorTextField.setDisable(true);
        }

        // Create new Student table if it doesn't already exist
        createTableButton.setOnAction(actionEvent -> {
            try {

                Connection conn = DriverManager.getConnection(AWS_URL);

                Statement stmt = conn.createStatement();


                try {
                    stmt.execute("CREATE TABLE Student (" +
                            "FirstName CHAR(25), " +
                            "LastName CHAR(25), " +
                            "Id VARCHAR(36), " +
                            "Age INT(3), " +
                            "Major CHAR(50), " +
                            "GPA FLOAT(4,2) )");

                    System.out.println("TABLE CREATED");

                    char[] studentNameArray = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
                    String[] studentMajorArray = {"Computer Information Systems", "Computer Science", "Construction Management",
                            "Digital Media", "Supply Chain", "Management Information Systems",
                            "Economics", "Accounting", "Physics", "Marketing"};
                    String sql = "";
                    String idString = "";


                    for (int i = 0; i < 10; i++) {
                        idString = UUID.randomUUID().toString();
                        sql = "INSERT INTO Student VALUES" +
                                "('Student', '" + studentNameArray[i] +"', " +
                                "'" + idString +"', '"+ generateRandomAge() +"', " +
                                "'"+ studentMajorArray[i] +"', '"+ generateRandomGpa() +"')";

                        stmt.executeUpdate(sql);
                    }

                    System.out.println("TABLE FILLED");
                    topHalf.setDisable(false);
                    sideBar.setDisable(false);
                    deleteTableButton.setDisable(false);
                    loadDataButton.setDisable(false);
                    firstNameTextField.setDisable(false);
                    lastNameTextField.setDisable(false);
                    ageTextField.setDisable(false);
                    gpaTextField.setDisable(false);
                    majorTextField.setDisable(false);
                    stmt.close();
                    conn.close();
                }
                catch (Exception ex)
                {
                    System.out.println("TABLE ALREADY EXISTS, NOT CREATED");
                }
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });

        // Delete Student Table if it exists
        deleteTableButton.setOnAction(actionEvent -> {
            try{
                Connection conn = DriverManager.getConnection(AWS_URL);

                Statement stmt = conn.createStatement();

                stmt.execute("DROP TABLE Student");
                stmt.close();
                conn.close();
                System.out.println("TABLE DROPPED");

                if (dbStudentList.size() > 0) {
                    int dbSize = dbStudentList.size();
                    for (int i = 0; i < dbSize; i++) {
                        dbStudentList.remove(0);
                    }
                }
                clearFields();
                topHalf.setDisable(true);
                sideBar.setDisable(true);
                deleteTableButton.setDisable(true);
                loadDataButton.setDisable(true);
                firstNameTextField.setDisable(true);
                lastNameTextField.setDisable(true);
                ageTextField.setDisable(true);
                gpaTextField.setDisable(true);
                majorTextField.setDisable(true);
                filterContainer.setVisible(false);
                toggleCount.set(0);
            }
            catch (Exception ex)
            {
                System.out.println("TABLE NOT DROPPED");
                System.out.println(ex.getMessage());
            }
        });

        // Fill the list with data from the database
        loadDataButton.setOnAction(actionEvent -> {
            clearFields();
            try {

                Connection conn = DriverManager.getConnection(AWS_URL);

                Statement stmt = conn.createStatement();

                if (dbStudentList.size() > 0) {
                    int dbSize = dbStudentList.size();
                    for (int i = 0; i < dbSize; i++) {
                        dbStudentList.remove(0);
                    }
                }

                String sqlStatement = "SELECT * FROM Student";

                if ( (toggleCount.get() % 2 != 0) && (!minAgeTextField.getText().isEmpty() || !maxAgeTextField.getText().isEmpty() || !minGpaTextField.getText().isEmpty()
                        || !maxGpaTextField.getText().isEmpty() || !majorFilterTextField.getText().isEmpty()) ) { // Filters exist
                    sqlStatement = sqlStatement + " WHERE 1=1";
                    // Grabbing Age filter criteria (if any)
                    int filterChoice = ageFilterSelection.getSelectionModel().getSelectedIndex();
                    switch (filterChoice) {
                        case 1: sqlStatement = sqlStatement + " AND Age=" + maxAgeTextField.getText();
                                break;
                        case 2: sqlStatement = sqlStatement + " AND Age BETWEEN " + minAgeTextField.getText() + " AND " + maxAgeTextField.getText();
                                break;
                        case 3: sqlStatement = sqlStatement + " AND Age > " + maxAgeTextField.getText();
                                break;
                        case 4: sqlStatement = sqlStatement + " AND Age < " + maxAgeTextField.getText();
                                break;
                        default: break;
                    }

                    // Grabbing GPA filter criteria (if any)
                    filterChoice = gpaFilterSelection.getSelectionModel().getSelectedIndex();
                    switch (filterChoice) {
                        case 1: sqlStatement = sqlStatement + " AND GPA=" + maxGpaTextField.getText();
                            break;
                        case 2: sqlStatement = sqlStatement + " AND GPA BETWEEN " + minGpaTextField.getText() + " AND " + maxGpaTextField.getText();
                            break;
                        case 3: sqlStatement = sqlStatement + " AND GPA > " + maxGpaTextField.getText();
                            break;
                        case 4: sqlStatement = sqlStatement + " AND GPA < " + maxGpaTextField.getText();
                            break;
                        default: break;
                    }

                    // Grabbing Major filter criteria (if any)
                    filterChoice = majorFilterSelection.getSelectionModel().getSelectedIndex();
                    switch (filterChoice) {
                        case 1: sqlStatement = sqlStatement + " AND Major='" + majorFilterTextField.getText() + "'";
                            break;
                        case 2: sqlStatement = sqlStatement + " AND Major LIKE '%" + majorFilterTextField.getText() + "%'";
                            break;
                        case 3: sqlStatement = sqlStatement + " AND Major LIKE '" + majorFilterTextField.getText() + "%'";
                            break;
                        case 4: sqlStatement = sqlStatement + " AND GPA Major LIKE '%" + majorFilterTextField.getText() + "'";
                            break;
                        default: break;
                    }
                }

                ResultSet result = stmt.executeQuery(sqlStatement);

                while (result.next()) {
                    Student student = new Student();
                    student.setFName(result.getString("FirstName"));
                    student.setLName(result.getString("LastName"));
                    student.setId(UUID.fromString(result.getString("Id")));
                    student.setAge(Integer.parseInt(result.getString("Age")));
                    student.setMajor(result.getString("Major"));
                    student.setGpa(Float.parseFloat(result.getString("GPA")));
                    dbStudentList.add(student);
                }

                System.out.println("DATA LOADED");

                stmt.close();
                conn.close();
                studentListView.getSelectionModel().selectFirst();
                studentListView.requestFocus();
            }
            catch (Exception ex) {
                System.out.println("DATA NOT LOADED");
                System.out.println(ex.getMessage());
            }
        });

        // Method to populate fields with selected student
        studentListView.getSelectionModel().selectedItemProperty().addListener((
                ObservableValue<? extends Student> ov, Student old_val, Student new_val)
                -> {
            clearFields();
            if (!dbStudentList.isEmpty()) {
                Student selectedItem = studentListView.getSelectionModel().getSelectedItem();
                firstNameTextField.setText(((Student) selectedItem).getFName());
                lastNameTextField.setText(((Student) selectedItem).getLName());
                ageTextField.setText(String.valueOf(((Student) selectedItem).getAge()));
                majorTextField.setText(((Student) selectedItem).getMajor());
                gpaTextField.setText(String.valueOf(((Student) selectedItem).getGpa()));
            } else {
                deleteStudentButton.setDisable(true);
            }
        });

        // Toggle Filter Button
        filterButton.setOnAction(actionEvent -> {
            if (toggleCount.get() % 2 == 0) {
                filterContainer.setVisible(true);
            } else {
                filterContainer.setVisible(false);
                clearFilters();
            }
            toggleCount.set(toggleCount.get() + 1);
        });

        ageFilterSelection.setOnAction(actionEvent -> {
            if (ageFilterSelection.getSelectionModel().getSelectedIndex() == 2) {
                minAgeTextField.setVisible(true);
                maxAgeTextField.setPromptText("Max");
                /*maxAgeFilterValidation.setDisable(false);*/
            } else {
                minAgeTextField.setVisible(false);
                maxAgeTextField.setPromptText("");
                /*maxAgeFilterValidation.setDisable(true);*/
            }
        });

        gpaFilterSelection.setOnAction(actionEvent -> {
            if (gpaFilterSelection.getSelectionModel().getSelectedIndex() == 2) {
                minGpaTextField.setVisible(true);
                maxGpaTextField.setPromptText("Max");
                maxGpaFilterValidation.setDisable(false);
            } else {
                minGpaTextField.setVisible(false);
                maxGpaTextField.setPromptText("");
                maxGpaFilterValidation.setDisable(true);
            }
        });

        minAgeTextField.setOnKeyReleased(e -> {
            if (!minAgeTextField.getText().equals(""))
                minAgeTextField.validate();
        });

        maxAgeTextField.setOnKeyReleased(e -> {
            maxAgeFilterValidation.setDisable(true);
            if (!maxAgeTextField.getText().equals(""))
                /*maxAgeFilterValidation.setDisable(false);*/
                maxAgeTextField.validate();
        });

        addStudentButton.setOnAction(actionEvent -> {
            clearFields();
            topHalf.setDisable(true);
            sideBar.setDisable(true);
            toolbar.setDisable(true);
            bottomBar.setVisible(true);
            firstNameTextField.requestFocus();
        });

        confirmButton.setOnAction(actionEvent -> {
            try {
                Connection conn = DriverManager.getConnection(AWS_URL);
                Statement stmt = conn.createStatement();

                String idString = UUID.randomUUID().toString();
                String sql = "INSERT INTO Student VALUES" +
                        "('" + firstNameTextField.getText() + "', '" + lastNameTextField.getText() +"', " +
                        "'" + idString +"', '" + ageTextField.getText() + "', " +
                        "'" + majorTextField.getText() + "', '" + gpaTextField.getText() + "')";
                stmt.executeUpdate(sql);

                System.out.println("STUDENT ADDED");
                topHalf.setDisable(false);
                sideBar.setDisable(false);
                toolbar.setDisable(false);
                bottomBar.setVisible(false);
            } catch (Exception ex) {
                System.out.println("FAILED TO ADD");
                System.out.println(ex.getMessage());
            }
            clearFields();
            bottomBar.setVisible(false);
        });

        clearButton.setOnAction(actionEvent -> {
            clearFields();
        });

        cancelButton.setOnAction(actionEvent -> {
            topHalf.setDisable(false);
            sideBar.setDisable(false);
            toolbar.setDisable(false);
            bottomBar.setVisible(false);
            clearFields();
        });

        updateStudentButton.setOnAction(actionEvent -> {
            if (studentListView.getSelectionModel().getSelectedItem() != null) {
                try {
                    Connection conn = DriverManager.getConnection(AWS_URL);
                    Statement stmt = conn.createStatement();

                    String sql = "UPDATE Student\n" +
                            "SET FirstName='" + firstNameTextField.getText() +
                            "', LastName='" + lastNameTextField.getText() +
                            "', Age=" + ageTextField.getText() +
                            ", Major='" + majorTextField.getText() +
                            "', GPA=" + gpaTextField.getText() + "\n" +
                            "WHERE Id='" + studentListView.getSelectionModel().getSelectedItem().getId().toString() + "';";
                    stmt.executeUpdate(sql);
                    System.out.println("STUDENT UPDATED");
                } catch (Exception ex) {
                    System.out.println("FAILED TO UPDATE");
                    System.out.println(ex.getMessage());
                }
            }
        });

        deleteStudentButton.setOnAction(actionEvent -> {
            if (studentListView.getSelectionModel().getSelectedItem() != null) {
                bottomBar2.setVisible(true);
                topHalf.setDisable(true);
                toolbar.setDisable(true);
                sideBar.setDisable(true);
                firstNameTextField.setDisable(true);
                lastNameTextField.setDisable(true);
                ageTextField.setDisable(true);
                gpaTextField.setDisable(true);
                majorTextField.setDisable(true);
            }
        });

        yesButton.setOnAction(actionEvent -> {
            try {
                Connection conn = DriverManager.getConnection(AWS_URL);
                Statement stmt = conn.createStatement();

                String sql = "DELETE FROM Student WHERE Id='" + studentListView.getSelectionModel().getSelectedItem().getId().toString() + "';";
                stmt.executeUpdate(sql);

                System.out.println("STUDENT DELETED");

                bottomBar2.setVisible(false);
                topHalf.setDisable(false);
                toolbar.setDisable(false);
                sideBar.setDisable(false);
                firstNameTextField.setDisable(false);
                lastNameTextField.setDisable(false);
                ageTextField.setDisable(false);
                gpaTextField.setDisable(false);
                majorTextField.setDisable(false);
            } catch (Exception ex) {
                System.out.println("FAILED TO DELETE");
                System.out.println(ex.getMessage());
            }
        });

        noButton.setOnAction(actionEvent -> {
            bottomBar2.setVisible(false);
            topHalf.setDisable(false);
            toolbar.setDisable(false);
            sideBar.setDisable(false);
            firstNameTextField.setDisable(false);
            lastNameTextField.setDisable(false);
            ageTextField.setDisable(false);
            gpaTextField.setDisable(false);
            majorTextField.setDisable(false);
        });
    }
}