package sample;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RegexValidator;
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
    @FXML private RegexValidator majorFilterValidation;
    @FXML private JFXButton clearFiltersButton;
    @FXML private RegexValidator firstNameValidation;
    @FXML private RegexValidator lastNameValidation;
    @FXML private RegexValidator majorValidation;
    @FXML private NumberValidator ageValidation;
    @FXML private NumberValidator gpaValidation;


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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bottomBar.setVisible(false);
        filterContainer.setVisible(false);
        clearFiltersButton.setVisible(false);
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
        majorFilterTextField.getValidators().add(majorFilterValidation);
        firstNameTextField.getValidators().add(firstNameValidation);
        lastNameTextField.getValidators().add(lastNameValidation);
        ageTextField.getValidators().add(ageValidation);
        gpaTextField.getValidators().add(gpaValidation);
        majorTextField.getValidators().add(majorValidation);

        // Linking Database items to the ListView
        ObservableList<Student> dbStudentList = FXCollections.observableArrayList();
        studentListView.setItems(dbStudentList);

        // Method to populate fields with selected student
        studentListView.getSelectionModel().selectedItemProperty().addListener((
                ObservableValue<? extends Student> ov, Student old_val, Student new_val)
                -> {
            clearFields();
            firstNameTextField.setLabelFloat(false);
            deleteStudentButton.setDisable(false);
            filterButton.setDisable(false);
            if (!dbStudentList.isEmpty()) {
                Student selectedItem = studentListView.getSelectionModel().getSelectedItem();
                firstNameTextField.setText(((Student) selectedItem).getFName());
                lastNameTextField.setText(((Student) selectedItem).getLName());
                ageTextField.setText(String.valueOf(((Student) selectedItem).getAge()));
                majorTextField.setText(((Student) selectedItem).getMajor());
                gpaTextField.setText(String.valueOf(((Student) selectedItem).getGpa()));
            } else {
                deleteStudentButton.setDisable(true);
                filterButton.setDisable(true);
            }
            firstNameTextField.setLabelFloat(true);
        });

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
            createTableButton.setDisable(true);
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
            filterButton.setDisable(true);
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

                    stmt.close();
                    conn.close();
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
                    loadDataButton.requestFocus();
                    createTableButton.setDisable(true);
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
                createTableButton.setDisable(false);
                deleteTableButton.setDisable(true);
                loadDataButton.setDisable(true);
                firstNameTextField.setDisable(true);
                lastNameTextField.setDisable(true);
                ageTextField.setDisable(true);
                gpaTextField.setDisable(true);
                majorTextField.setDisable(true);
                filterContainer.setVisible(false);
                clearFiltersButton.setVisible(false);
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
                stmt.close();
                conn.close();
                System.out.println("DATA LOADED");
                studentListView.getSelectionModel().selectFirst();
                studentListView.requestFocus();
            }
            catch (Exception ex) {
                System.out.println("DATA NOT LOADED");
                System.out.println(ex.getMessage());
            }
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
                conn.close();
                stmt.close();

                System.out.println("STUDENT ADDED");
                topHalf.setDisable(false);
                sideBar.setDisable(false);
                toolbar.setDisable(false);
                bottomBar.setVisible(false);
                loadDataButton.requestFocus();
            } catch (Exception ex) {
                System.out.println("FAILED TO ADD");
                System.out.println(ex.getMessage());
            }
            clearFields();
            bottomBar.setVisible(false);
        });

        clearButton.setOnAction(actionEvent -> {
            clearFields();
            firstNameTextField.resetValidation();
            lastNameTextField.resetValidation();
            ageTextField.resetValidation();
            gpaTextField.resetValidation();
            majorTextField.resetValidation();
            confirmButton.setDisable(false);
        });

        cancelButton.setOnAction(actionEvent -> {
            topHalf.setDisable(false);
            sideBar.setDisable(false);
            toolbar.setDisable(false);
            bottomBar.setVisible(false);
            clearFields();
            firstNameTextField.resetValidation();
            lastNameTextField.resetValidation();
            ageTextField.resetValidation();
            gpaTextField.resetValidation();
            majorTextField.resetValidation();
            confirmButton.setDisable(false);
            // Label floats for jfx text fields are bugged so these next few lines correct that
            if (!dbStudentList.isEmpty()) {
                firstNameTextField.requestFocus();
                firstNameTextField.setText(studentListView.getSelectionModel().getSelectedItem().getFName());
                lastNameTextField.requestFocus();
                lastNameTextField.setText(studentListView.getSelectionModel().getSelectedItem().getLName());
                ageTextField.requestFocus();
                ageTextField.setText(String.valueOf(studentListView.getSelectionModel().getSelectedItem().getAge()));
                gpaTextField.requestFocus();
                gpaTextField.setText(String.valueOf(studentListView.getSelectionModel().getSelectedItem().getGpa()));
                majorTextField.requestFocus();
                majorTextField.setText(studentListView.getSelectionModel().getSelectedItem().getMajor());
                studentListView.requestFocus();
                studentListView.getSelectionModel().selectFirst();
            }
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
                    conn.close();
                    stmt.close();
                    System.out.println("STUDENT UPDATED");
                    loadDataButton.requestFocus();
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

                conn.close();
                stmt.close();

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
                loadDataButton.requestFocus();
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

        // Filter Stuff
        filterButton.setOnAction(actionEvent -> {
            if (toggleCount.get() % 2 == 0) {
                filterContainer.setVisible(true);
                clearFiltersButton.setVisible(true);
            } else {
                filterContainer.setVisible(false);
                clearFiltersButton.setVisible(false);
                clearFilters();
            }
            toggleCount.set(toggleCount.get() + 1);
        });

        clearFiltersButton.setOnAction(actionEvent -> {
            clearFilters();
        });

        ageFilterSelection.setOnAction(actionEvent -> {
            if (ageFilterSelection.getSelectionModel().getSelectedIndex() == 2) {
                minAgeTextField.setVisible(true);
                maxAgeTextField.setPromptText("Max");
            } else {
                minAgeTextField.setVisible(false);
                maxAgeTextField.setPromptText("");
            }
        });

        gpaFilterSelection.setOnAction(actionEvent -> {
            if (gpaFilterSelection.getSelectionModel().getSelectedIndex() == 2) {
                minGpaTextField.setVisible(true);
                maxGpaTextField.setPromptText("Max");
            } else {
                minGpaTextField.setVisible(false);
                maxGpaTextField.setPromptText("");
            }
        });

        minAgeTextField.setOnKeyReleased(e -> {
            loadDataButton.setDisable(false);
            minAgeTextField.resetValidation();
            if (!minAgeTextField.getText().equals("")) {
                minAgeTextField.validate();
                if (minAgeTextField.getActiveValidator() != null && minAgeTextField.getActiveValidator().getHasErrors()) {
                    loadDataButton.setDisable(true);
                }
            }
        });

        maxAgeTextField.setOnKeyReleased(e -> {
            loadDataButton.setDisable(false);
            maxAgeTextField.resetValidation();
            if (!maxAgeTextField.getText().equals("")) {
                maxAgeTextField.validate();
                if (maxAgeTextField.getActiveValidator() != null && maxAgeTextField.getActiveValidator().getHasErrors()) {
                    loadDataButton.setDisable(true);
                }
            }
        });

        minGpaTextField.setOnKeyReleased(e -> {
            loadDataButton.setDisable(false);
            minGpaTextField.resetValidation();
            if (!minGpaTextField.getText().equals("")) {
                minGpaTextField.validate();
                if (minGpaTextField.getActiveValidator() != null && minGpaTextField.getActiveValidator().getHasErrors()) {
                    loadDataButton.setDisable(true);
                }
            }
        });

        maxGpaTextField.setOnKeyReleased(e -> {
            loadDataButton.setDisable(false);
            maxGpaTextField.resetValidation();
            if (!maxGpaTextField.getText().equals("")) {
                maxGpaTextField.validate();
                if (maxGpaTextField.getActiveValidator() != null && maxGpaTextField.getActiveValidator().getHasErrors()) {
                    loadDataButton.setDisable(true);
                }
            }
        });

        majorFilterTextField.setOnKeyReleased(e -> {
            loadDataButton.setDisable(false);
            majorFilterTextField.resetValidation();
            if (!majorFilterTextField.getText().equals("")) {
                majorFilterTextField.validate();
                if (majorFilterTextField.getActiveValidator() != null && majorFilterTextField.getActiveValidator().getHasErrors()) {
                    loadDataButton.setDisable(true);
                }
            }
        });

        firstNameTextField.setOnKeyReleased(e -> {
            confirmButton.setDisable(false);
            updateStudentButton.setDisable(false);
            firstNameTextField.resetValidation();
            if (!firstNameTextField.getText().equals("")) {
                firstNameTextField.validate();
                if (firstNameTextField.getActiveValidator() != null && firstNameTextField.getActiveValidator().getHasErrors()) {
                    confirmButton.setDisable(true);
                    updateStudentButton.setDisable(true);
                }
            }
        });

        lastNameTextField.setOnKeyReleased(e -> {
            confirmButton.setDisable(false);
            updateStudentButton.setDisable(false);
            lastNameTextField.resetValidation();
            if (!lastNameTextField.getText().equals("")) {
                lastNameTextField.validate();
                if (lastNameTextField.getActiveValidator() != null && lastNameTextField.getActiveValidator().getHasErrors()) {
                    confirmButton.setDisable(true);
                    updateStudentButton.setDisable(true);
                }
            }
        });

        majorTextField.setOnKeyReleased(e -> {
            confirmButton.setDisable(false);
            updateStudentButton.setDisable(false);
            majorTextField.resetValidation();
            if (!majorTextField.getText().equals("")) {
                majorTextField.validate();
                if (majorTextField.getActiveValidator() != null && majorTextField.getActiveValidator().getHasErrors()) {
                    confirmButton.setDisable(true);
                    updateStudentButton.setDisable(true);
                }
            }
        });

        ageTextField.setOnKeyReleased(e -> {
            confirmButton.setDisable(false);
            updateStudentButton.setDisable(false);
            ageTextField.resetValidation();
            if (!ageTextField.getText().equals("")) {
                ageTextField.validate();
                if (ageTextField.getActiveValidator() != null && ageTextField.getActiveValidator().getHasErrors()) {
                    confirmButton.setDisable(true);
                    updateStudentButton.setDisable(true);
                }
            }
        });

        gpaTextField.setOnKeyReleased(e -> {
            confirmButton.setDisable(false);
            updateStudentButton.setDisable(false);
            gpaTextField.resetValidation();
            if (!gpaTextField.getText().equals("")) {
                gpaTextField.validate();
                if (gpaTextField.getActiveValidator() != null && gpaTextField.getActiveValidator().getHasErrors()) {
                    confirmButton.setDisable(true);
                    updateStudentButton.setDisable(true);
                }
            }
        });
    }
}