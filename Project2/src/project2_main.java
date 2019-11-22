  
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class project2_main {
  public static void main(String args[]) throws ClassNotFoundException, SQLException {
	int instruction;
	while(true) {
		Scanner sc = new Scanner(System.in);
		System.out.print("Please input the instruction number (1: Import from CSV, 2: Export to CSV, 3: Manipulate Data, 4: Exit) :");
		instruction = sc.nextInt();
		switch(instruction) {
		case 1:
			System.out.println(1);
			break;
		case 2:
			System.out.println(2);
			break;
		case 3:
			System.out.println(3);
			break;
		case 4:
			System.out.println(4);
			return;
		}
	}
  }
}
