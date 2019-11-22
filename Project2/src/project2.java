  
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class project2 {
  public static void main(String args[]) throws ClassNotFoundException, SQLException {
	int instruction;
	while(true) {
		Scanner sc = new Scanner(System.in);
		System.out.print("Please input the instruction number (1: Import from CSV, 2: Export to CSV, 3: Manipulate Data, 4: Exit) :");
		instruction = sc.nextInt();
		switch(instruction) {
		case 1: // Import from CSV
			String file_name;
			String CSV_file;
			System.out.println("[Import from CSV]");
			System.out.print("Please specify the filename for table description) : ");
			file_name = sc.nextLine(); // 
			System.out.println("Table is newly created as describled in the file");
			System.out.print("Please specify the CSV filename : ");
			CSV_file = sc.nextLine();
			break;
		case 2: // Export to CSV
			System.out.println(2);
			break;
		case 3: // Manipulate Data
			System.out.println(3);
			break;
		case 4: // Exit
			System.out.println(4);
			return;
		}
	}
  }
}
