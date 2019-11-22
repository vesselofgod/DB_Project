import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.util.*
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Project2 {
// scanner사용시 주의할 점! : nextInt같은 경우는 개행문자를 입력받지 않기 때문에 뒤에 nextLine을 쓰면 개행문자를 받아버려서 입력이 있는 것으로 처리됨 ( nextLine으로 받은 변수들은
// enter가 뒤에 들어있는 것을 알고 parsing이 필요함 )
	public static void main(String args[]) throws ClassNotFoundException, SQLException {
		String[] parse = new String[5];
		try 
		{
		// Connection file read
		// 주의사항. connection file안의 정보는 자기 컴퓨터에 있는 PW, DB_NAME등으로 바꿔서 돌릴 것
			File file = new File("./connection.txt");
			Scanner scan = new Scanner(file);
			int counter=0;
			while(scan.hasNextLine())
			{	
				String word= scan.nextLine();
				parse[counter] = word.split(":")[1];
				counter+=1;
			}
		}
		catch (FileNotFoundException e) 
		{
		System.out.println("File Not exist");
		}

		//Name of parameter of connecting
		String IP = parse[0].trim();
		String DB_NAME = parse[1].trim();
		String SCHEMA_NAME = parse[2].trim();
		String ID = parse[3].trim();
		String PW = parse[4].trim();
		String DB_DRIVER = "org.postgresql.Driver";
		String DB_CONNECTION_URL = "jdbc:postgresql://"+IP+"/"+DB_NAME;
		String DB_USER = ID;
		String DB_PASSWORD = PW;
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
		
		
		
		int instruction; //instruction input받는 숫자
		while(true) {
			Scanner sc = new Scanner(System.in);
			System.out.print("Please input the instruction number (1: Import from CSV, 2: Export to CSV, 3: Manipulate Data, 4: Exit) :");
			instruction = sc.nextInt();
			sc.nextLine();
			String table_name; // 수정하거나 참조하는 table name
			String file_name; // 참조하는 file name
			String CSV_file; // 쓰거나 참조하는 CSV file name
			int man_instruction; // 4번에서 사용하는 manipulate instruction
			switch(instruction) {
			case 1: // Import from CSV
				System.out.println("[Import from CSV]");
				System.out.print("Please specify the filename for table description) : ");
				file_name = sc.nextLine();
				System.out.println("Table is newly created as describled in the file");
				System.out.print("Please specify the CSV filename : ");
				CSV_file = sc.nextLine();
				// 실제 처리
				// 오류없이 돌아간 경우
				System.out.println("Data import completed. (Insertion Success : ");
				// 오류가 난 경우
				// failure 개수만큼 loop돌려서 출력해주기
				System.out.println();
				break;
			case 2: // Export to CSV
				System.out.println("[Export to CSV]");
				System.out.print("Please specify the table name : ");
				table_name = sc.nextLine();
				System.out.print("Please specify the CSV filename : ");
				CSV_file = sc.nextLine();
				// 실제처리
				ResultSet rs2 = st.executeQuery("SELECT * FROM " + table_name);
				ResultSetMetaData rs2_meta = rs2.getMetaData();
				
				try {
					BufferedWriter wr = Files.newBufferedWriter(Paths.get(CSV_file), Charset.forName("UTF-8"));
					int columnNumber = rs2_meta.getColumnCount();
					
					for(int i = 0;i < columnNumber - 1;i++) {
						wr.write(rs2_meta.getColumnName(i + 1) + ',');
					}
					wr.write(rs2_meta.getColumnName(columnNumber));
					wr.newLine();
					
					while(rs2.next()) {
						for(int i = 0; i < columnNumber - 1; i++) {
							wr.write(rs2.getString(i + 1) + ',');
						}
						wr.write(rs2.getString(columnNumber));
						wr.newLine();
					}
					
					wr.close();
					System.out.println("Data export completed.");
				}
				catch (Exception e) {
					System.out.println(e);
				}
				System.out.println();
				break;
			case 3: // Manipulate Data
				System.out.println("[Manipulate Data]");
				System.out.print("Please input the instruction number (1: Show Tables, 2: Describe Table, 3: Select, "
						+ "4: Insert, 5: Delete, 6: Update, 7: Drop Table, 8: Back to main) : ");
				man_instruction = sc.nextInt();
				sc.nextLine();
				String select; // select에 들어가는 string - parsing 필요
				String condition_column; // condition이 적용되는 column
				switch (man_instruction) {
				case 1: // Show tables
					System.out.println("=======");
					System.out.println("Table List");
					System.out.println("=======");
					//실제 연산에서 table의 이름들을 가져와서 출력함 (출력해야 할 table의 개수를 같이 리턴해주면 for문으로 돌리기)
					System.out.println();
					break;
				case 2: // Describe table
					System.out.print("Please specify the table name : ");
					table_name = sc.nextLine();
					System.out.println("==================================================================");
					System.out.println("Column Name | Data Type | Character Maximum Length(or Numeric Precision and Scale)");
					System.out.println("==================================================================");
					//실제 연산에서 column name과 data type을 가져와서 출력 (출력해야 할 column 개수를 같이 리턴해주면 for문으로 돌리기)
					System.out.println();
					break;
				case 3: // Select
					String order_column; // ordering이 필요한 column
					System.out.print("Please specify the table name : ");
					table_name = sc.nextLine();
					System.out.print("Please specify columns which you want to retrieve (All : *): ");
					select = sc.nextLine();
					System.out.print("Please specify the column which you want to make condition (Proess enter : skip) : ");
					condition_column = sc.nextLine();
					System.out.print("Please specify the column name for ordering (Press enter : skip) : ");
					order_column = sc.nextLine();
					System.out.print("Please specify the sorting criteria (Press enter : skip); : ");
					// 연산 수행 후 가져오는 column에 따라서 출력되는 format이 달라지게 됨
					// 연산 수행
					System.out.println();
					break;
				case 4: // Insert
					String values;
					System.out.print("Please specify the table name : ");
					table_name = sc.nextLine();
					System.out.print("Please specify all columns in order of which you want to insert : ");
					select = sc.nextLine();
					System.out.print("Please specify values for each column : ");
					values = sc.nextLine();
					//연산 후 몇개의 row가 변경되었는지에 대한 정보가 나와야 하므로 전달되어야 함
					//연산 실패시 error출력되어야 함
					System.out.println();
					break;
				case 5: // Delete
					String condition;
					System.out.print("Please specify the table name : ");
					table_name = sc.nextLine();
					System.out.print("Please specify the column which you want to make condition (Press enter : skip) : ");
					condition_column = sc.nextLine();
					System.out.print("Please specify the condition (1: = 2: >, 3: <, 4: >=, 5: <= 6: !=, 7: LIKE)");
					condition = sc.nextLine();
					System.out.print("Please specify the condition value (" + condition_column + " = ?) : ");
					// 이거 어떻게 할건지 이야기해봐야함 (전체 입력을 다 받고 나중에 query를 만들것인지 아니면 받는 순간에 query를 만들어놓고 변수를 재사용할 수 있게 할 건지)
					System.out.println();
					break;
				case 6: // Update
					System.out.print("Please specify the table name : ");
					table_name = sc.nextLine();
					System.out.print("Please specify the column which you want to make condition (Press enter : skip) : ");
					condition_column = sc.nextLine();
					System.out.print("Please specify the condition (1: =, 2: >, 3: <, 4: >=, 5: <=, 6: !=. 7: LIKE) : ");
					condition = sc.nextLine();
					System.out.println();
					break;
				case 7: // Drop table
					char sure; // 진짜로 table을 삭제할 것인가에 대해 물어보는 질문의 답을 저장
					System.out.print("Please specify the table name : ");
					table_name = sc.nextLine();
					do {
						System.out.print("If you delete this table, it is not guaranteed to recover again. Are you sure you want to delete this table (Y:yes, N:no)? ");
						sure = sc.nextLine().charAt(0);
					} while(sure != 'Y' && sure != 'N');
					if (sure == 'Y') {
						//실제 삭제 진행
						//연산
						System.out.println("<The table " + table_name + " is deleted>");
					}
					else {
						//삭제 안해
						System.out.println("<Deletion canceled>");
					}
					System.out.println();
					break;
				case 8: // Back to main
					break;
				}
				break;
			case 4: // Exit
				return;
			default:
				System.out.println();
				break;
			}
			st.close();
		}
	}
}
