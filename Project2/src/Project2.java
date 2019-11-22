import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Project2 {
  private static final String DB_DRIVER = "org.postgresql.Driver";
  private static final String DB_CONNECTION_URL = "jdbc:postgresql://127.0.0.1/postgres";
  private static final String DB_USER = "postgres";
  private static final String DB_PASSWORD = "yp2207";

  public static void main(String args[]) throws ClassNotFoundException, SQLException {
    Class.forName(DB_DRIVER);
    Properties props = new Properties();

    /* Setting Connection Info */
    props.setProperty("user", DB_USER);
    props.setProperty("password", DB_PASSWORD);
    props.setProperty("characterEncoding", "UTF-8");
    props.setProperty("serverTimezone", "Asia/Seoul");

    /* Connect! */
    Connection conn = DriverManager.getConnection(DB_CONNECTION_URL, props);

    /* Create SQL statement object */
    Statement st = conn.createStatement();

    /* Create Table SQL */
    String createTableSQL = "CREATE TABLE student_table " +
                            "(ID int, " +
                            "name varchar(20) not null, " +
                            "address varchar(50) not null," +
                            "department_ID int," +
                            "primary key (ID))";
    st.executeUpdate(createTableSQL);

    /* Insert Row directly using raw statement */
    String insertSQLDirectly = "INSERT INTO student_table values (1, 'Brandt', 'addr1', 1)";
    st.executeUpdate(insertSQLDirectly);

    /* Insert Row using PreparedStatement */
    String insertSQLPrepared = "INSERT INTO student_table (ID, name, address, department_ID) values (?, ?, ?, ?)";

    PreparedStatement preparedStmt = conn.prepareStatement(insertSQLPrepared);
    preparedStmt.setInt(1, 2);
    preparedStmt.setString(2, "Chavez");
    preparedStmt.setString(3, "addr2");
    preparedStmt.setInt(4, 2);
    preparedStmt.execute();

    /* Show current rows in student_table */
    ResultSet rs = st.executeQuery("SELECT ID, name, address, department_ID FROM student_table");
    System.out.println("============ RESULT ============");
    while (rs.next()) {
        System.out.print("ID : " + rs.getString(1) + ", ");
        System.out.print("Name : " + rs.getString(2) + ", ");
        System.out.print("Address : " + rs.getString(3) + ", ");
        System.out.print("Department_ID : " + rs.getString(4));
        System.out.println();
    }

    /* Update Row */
    String updateSQL = "UPDATE student_table SET address = ? where ID = ?";
    preparedStmt = conn.prepareStatement(updateSQL);
    preparedStmt.setString(1, "addr3");
    preparedStmt.setInt(2, 2);
    preparedStmt.executeUpdate();

    /* Show current rows in student_table */
    rs = st.executeQuery("SELECT ID, name, address, department_ID FROM student_table");
    System.out.println("============ RESULT ============");
    while (rs.next()) {
        System.out.print("ID : " + rs.getString(1) + ", ");
        System.out.print("Name : " + rs.getString(2) + ", ");
        System.out.print("Address : " + rs.getString(3) + ", ");
        System.out.print("Department_ID : " + rs.getString(4));
        System.out.println();
    }

    /* Delete Row */
    String deleteSQL = "DELETE FROM student_table where ID = 2";
    st.executeUpdate(deleteSQL);

    /* Show current rows in student_table */
    rs = st.executeQuery("SELECT ID, name, address, department_ID FROM student_table");
    System.out.println("============ RESULT ============");
    while (rs.next()) {
        System.out.print("ID : " + rs.getString(1) + ", ");
        System.out.print("Name : " + rs.getString(2) + ", ");
        System.out.print("Address : " + rs.getString(3) + ", ");
        System.out.print("Department_ID : " + rs.getString(4));
        System.out.println();
    }

    /* Drop table */
    String dropTableSQL = "DROP TABLE student_table";
    st.executeUpdate(dropTableSQL);

    preparedStmt.close();
    st.close();
    rs.close();
  }
}