  
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
		String table_name; // 수정하거나 참조하는 table name
		String file_name; // 참조하는 file name
		String CSV_file; // 쓰거나 참조하는 CSV file namee
		switch(instruction) {
		case 1: // Import from CSV
			System.out.println("[Import from CSV]");
			System.out.print("Please specify the filename for table description) : ");
			file_name = sc.nextLine();
			System.out.println("Table is newly created as describled in the file");
			System.out.print("Please specify the CSV filename : ");
			CSV_file = sc.nextLine();
			// 오류없이 돌아간 경우
			System.out.println("Data import completed. (Insertion Success : ");
			// 오류가 난 경우
			// failure 개수만큼 loop돌려서 출력해주기
			break;
		case 2: // Export to CSV
			System.out.println("[Export to CSV]");
			System.out.print("Please specify the table name : ");
			table_name = sc.nextLine();
			System.out.print("Please specify the CSV filename : ");
			CSV_file = sc.nextLine();
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
