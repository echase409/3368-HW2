package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Controller implements Initializable {

    @FXML private JFXButton createTableButton;
    @FXML private JFXButton loadDataButton;
    @FXML private JFXListView<Student> studentListView;

    final String hostname = "student-db.csyipcd3jqnc.us-east-1.rds.amazonaws.com";
    final String dbname = "student_db";
    final String port = "3306";
    final String username = "admin";
    final String password = "dbpassword";
    final String AWS_URL = "jdbc:mysql://" + hostname + ":" + port + "/" + dbname + "?user=" + username + "&password=" + password;

    // Generates a random GPA between 1.00 and 4.00
    public float generateRandomGpa() {
        return (float) ((float)(Math.random() * ((4.00 - 0.00) + 1)) + 0.00);
    }

    // Generates a random age between 18 and 50
    public int generateRandomAge() {
        return (int)(Math.random() * ((50 - 18) + 1)) + 18;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AtomicReference<String> errorLog = new AtomicReference<>("");
/*        try{
            Connection conn = DriverManager.getConnection(AWS_URL);

            Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE Student");
            stmt.close();
            conn.close();
            System.out.println("TABLE DROPPED");
        }
        catch (Exception ex)
        {
            var msg = ex.getMessage();
            System.out.println("TABLE NOT DROPPED");
            System.out.println(msg);
        }*/
        // Linking Database items to the ListView
        ObservableList<Student> dbStudentList = FXCollections.observableArrayList();
        studentListView.setItems(dbStudentList);

        // Performing Initial Data Load
        try {

            Connection conn = DriverManager.getConnection(AWS_URL);

            Statement stmt = conn.createStatement();

            String sqlStatement = "SELECT FirstName, LastName, Id, Age, Major, GPA FROM Student";
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
            errorLog.set(errorLog + ex.getMessage() + "\n");
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

                    stmt.close();
                    conn.close();
                }
                catch (Exception ex)
                {
                    System.out.println("TABLE ALREADY EXISTS, NOT CREATED");
                    errorLog.set(errorLog + ex.getMessage() + "\n");
                }
            }
            catch (Exception ex) {
                errorLog.set(errorLog + ex.getMessage() + "\n");
            }
        });

        // Fill the list with data from the database
        loadDataButton.setOnAction(actionEvent -> {
            try {

                Connection conn = DriverManager.getConnection(AWS_URL);

                Statement stmt = conn.createStatement();

                if (dbStudentList.size() > 0) {
                    int dbSize = dbStudentList.size();
                    for (int i = 0; i < dbSize; i++) {
                        dbStudentList.remove(0);
                    }
                }

                String sqlStatement = "SELECT FirstName, LastName, Id, Age, Major, GPA FROM Student";
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
            }
            catch (Exception ex) {
                System.out.println("DATA NOT LOADED");
                errorLog.set(errorLog + ex.getMessage() + "\n");
            }
        });
    }
}