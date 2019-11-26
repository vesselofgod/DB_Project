import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Time;
import java.sql.Types;
import java.math. *;

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
			scan.close();
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
		conn.setAutoCommit(true);
		/* Create SQL statement object */
		
		
		
		String instruction; //instruction input받는 숫자
		Scanner sc = new Scanner(System.in);
		while(true) {
			Statement st = conn.createStatement();
			System.out.print("Please input the instruction number (1: Import from CSV, 2: Export to CSV, 3: Manipulate Data, 4: Exit) :");
			instruction = sc.nextLine();
			String table_name=""; // 수정하거나 참조하는 table name
			String file_name=""; // 참조하는 file name
			String CSV_file=""; // 쓰거나 참조하는 CSV file name
			String man_instruction=""; // 4번에서 사용하는 manipulate instruction
			switch(instruction) {
			case "1": // Import from CSV
				System.out.println("[Import from CSV]");
				System.out.print("Please specify the filename for table description) : ");
				file_name = sc.nextLine();
				
				List<String> column_name = new ArrayList<String>();
				List<String> column_type = new ArrayList<String>();
				// file에서 table 정보 가져오기 
				try {
					File table_info_f = new File(file_name);
					FileReader table_info_fr = new FileReader (table_info_f);
					BufferedReader table_info= new BufferedReader(table_info_fr);
					//table name 받기 
					String table_namet = table_info.readLine();
					String[] a = table_namet.split(":");
					table_name =  a[1].trim()  ;
					
					//column info 받기 
					String table_t = table_info.readLine();
					String[] a1 = table_t.split(":");
					while (a1[0].trim().charAt(0) !='P') {
						column_name.add("\""+a1[1].trim()+"\"");   // column_name "    " 상태 
						
						table_t = table_info.readLine();
						a1 = table_t.split(":");
						column_type.add(a1[1].trim());
						
						table_t = table_info.readLine();
						a1 = table_t.split(":");
					}
					
					//PK 되는 것 받기 
					String querypk = "";
					//List<String> pk = new ArrayList<String> ();  // 여기에 PK의 이름 다 넣기 
					String[] pks = a1[1].split(",");
					for (int i=0; i<pks.length; i++) {
						//pk.add("\""+pks[i].trim()+"\"");
						querypk += "\""+pks[i].trim()+"\""+ ",";
					}
					querypk = querypk.substring(0, querypk.length()-1) ;
					
					//NOT NULL 받기 
					List<Integer> nn = new ArrayList<Integer> ();  // 여기에 NOT NULL 인 column index 다 넣기 
					table_t = table_info.readLine();
					a1 = table_t.split(":");
					String[] nns = a1[1].split(",");
					for(int i=0; i<nns.length; i++) {
						nn.add(column_name.indexOf("\""+nns[i].trim()+"\""));
					}
					
					
					//column type + not null 합치기 
					for (int i=0; i<nn.size(); i++) {
						if (nn.get(i) != -1) {
							String a3 = column_type.get(nn.get(i));
							column_type.set(nn.get(i), a3+ " not null");
						}
					}
					
					//column name + column type 합치기 
					String name_type = "";
					for (int i=0; i< column_name.size(); i++) {
						name_type +=  column_name.get(i)  + column_type.get(i) + ",";
					}
					
					//SQL QUERY 실행 
					st.execute("create table \"" +table_name+ "\"(" +name_type + 
							"primary key (" +querypk+"));");
					table_info.close();
					table_info_fr.close();
					System.out.println("Table is newly created as describled in the file");
					
				}catch (FileNotFoundException e1) {
					System.out.println(e1);
				}catch (IOException e2) {
					System.out.println(e2);
				}catch (org.postgresql.util.PSQLException e) {
					System.out.println("Table already exists.");
				}
				
				
				// CSV file에서 data 받아오는 과정 start
				System.out.print("Please specify the CSV filename : ");
				CSV_file = sc.nextLine();
				// 실제 처리
				
				int succ_num = 0;   // 넣는 데에 성공한 tuple의 개수
				int fail_num = 0;   // 넣는 데에 실패한 tuple의 개수 
				List<Integer> fail_line_num = new ArrayList<Integer> () ;
				List<String> fail_line_info = new ArrayList<String> ();
				
				try {
					BufferedReader bf = 
							new BufferedReader (new InputStreamReader (new FileInputStream(CSV_file), "UTF-8"));
					String column_names = bf.readLine();
					String[] column_namearr = column_names.split(",");
					
					if (column_namearr.length != column_name.size()) {
						System.out.println
						("Data inport failure. (The number of columns does not match between the table description and the CSV file.)\n");
						continue;
					}
					String datacol = "";
					
					// column_namearr -> data type 4가지로 나누기 
					List<Integer> col_data_type = new ArrayList<Integer> ();
					//column_namearr의 column 대소문자 구별 및 양옆 공백 없애기 
					for (int i=0 ; i<column_namearr.length; i++) {
						column_namearr[i] =  "\""+ (column_namearr[i].trim()) +"\"";
						datacol = datacol +column_namearr[i] + ",";
						
						int index = column_name.indexOf(column_namearr[i]);
						
						if (column_type.get(index).contains("Date")) {
							col_data_type.add(2);
							continue;
						} else if (column_type.get(index).contains("Time")){
							col_data_type.add(3);
							continue;
						} else if (column_type.get(index).contains("char")) {
							col_data_type.add(1);
							continue;
						} else if (column_type.get(index).contains("int")){
							col_data_type.add(0);
							continue;
						} else {// numeric value
							col_data_type.add(4);
							continue;
						}
							
					}
					
					
					// 0 :숫자 관련  1: string 관련 2: date 관련 3: time 관련 
					// 이에 따라 query 만들기 
					datacol = datacol.substring(0, datacol.length()-1);
					//read csv file line by line 
					// 읽을 때마다 집어 넣고 success fail check 
					
					int k = 1;
					String data = bf.readLine();
					k ++;
					String question_marks = "(";
					for (int i=0; i<column_namearr.length; i++) {
						question_marks += "?,";
					}
					question_marks = question_marks.substring(0, question_marks.length() -1);
					question_marks += ")";
					while (data != null) {
						String query = "insert into \"" + table_name + "\" (" + datacol + ")"+ "values "+question_marks;
						PreparedStatement pstmt = conn.prepareStatement(query);
						String[] dataarr = data.split(",");
						for (int i=1; i<dataarr.length + 1; i++) {
							if (dataarr[i-1] == "") {
								pstmt.setNull(i, java.sql.Types.NULL);
								continue;
							}
							ResultSet imprrset = st.executeQuery("select * from "+table_name);
							ResultSetMetaData imprrsetmet = imprrset.getMetaData();
							//int location1 = column_name.indexOf(column_namearr[0]) + 1;
							int location = column_name.indexOf(column_namearr[i-1]) + 1;
							String datacoltype = imprrsetmet.getColumnTypeName(location);
							//String fortestmy = imprrsetmet.getColumnTypeName(column_name.indexOf(column_namearr[0])+1);
							
							
							if (datacoltype.contains("int")) {
								pstmt.setInt(i, Integer.parseInt(dataarr[i-1].trim()));
							}
							if (datacoltype.contains("date")) {
								pstmt.setDate(i, Date.valueOf(dataarr[i-1].trim()));
							}
							if (datacoltype.contains("time")) {
								String[] timetoconvert = dataarr[i-1].split(":");
								int hourtoconvert = Integer.parseInt(timetoconvert[0].trim());
								int minutetoconvert = Integer.parseInt(timetoconvert[1].trim());
								java.sql.Time inputtime = new java.sql.Time(hourtoconvert, minutetoconvert, 0);
								pstmt.setTime(i, inputtime);
							}
							if (datacoltype.contains("char")){
								if ((dataarr[i-1].trim().charAt(0) == '"' )&&(dataarr[i-1].trim().charAt(dataarr[i-1].trim().length()-1)=='"')) {
									String realdatach = dataarr[i-1].trim().replaceAll("^\"|\"$", "");;
									pstmt.setString(i,realdatach);
								} else {
									pstmt.setString(i,  dataarr[i-1]);
								}
							}
							if (datacoltype.contains("numeric")) {
								BigDecimal fdata = new BigDecimal(dataarr[i-1].trim());
								pstmt.setObject(i, fdata, Types.NUMERIC);
							}
							
						}
						if (dataarr.length < column_namearr.length) {
							for (int i= dataarr.length+1; i<column_namearr.length+1; i++) {
								pstmt.setNull(i, java.sql.Types.NULL);
							}
						}
						
						try {
							pstmt.executeUpdate();
							pstmt.clearParameters();
							succ_num ++;
						} catch (org.postgresql.util.PSQLException e) {
							fail_num ++;
							fail_line_num.add(k);
							fail_line_info.add(data);
						}
						
						data = bf.readLine();
						pstmt.close();
						k++;
					}
					// 오류없이 돌아간 경우
					System.out.println("Data import completed. (Insertion Success : " + succ_num+ ", Insertion Failure : " + fail_num+ ")");
					// 오류가 난 경우
					// failure 개수만큼 loop돌려서 출력해주기	
					if (fail_num != 0) {
						for (int i=0; i<fail_num; i++) {
							System.out.println("Failed tuple : " + fail_line_num.get(i) + " line in CSV - " + fail_line_info.get(i));
						}
					}
					bf.close();
				} catch (FileNotFoundException e1) {
					System.out.println(e1);
				}catch (IOException e2) {
					System.out.println(e2);
				} 
				
				System.out.println();
				break;
			case "2": // Export to CSV
				System.out.println("[Export to CSV]");
				System.out.print("Please specify the table name : ");
				table_name = sc.nextLine();
				System.out.print("Please specify the CSV filename : ");
				CSV_file = sc.nextLine();
				String[] checker = CSV_file.split("\\.");
				
				if(checker[1].toLowerCase().compareTo("csv") == 0)
				{
					// 실제처리 (CSV파일이 잘못 들어오거나 없는 table을 건드는 경우 어떻게 해야하는가?)
					try {
						ResultSet rs2 = st.executeQuery("SELECT * FROM " + "\"" + table_name + "\"");
						ResultSetMetaData rs2_meta = rs2.getMetaData();
						
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
						System.out.println("<Error Detected>");
					}
				}
				else {
					System.out.println("<Error Detected>");
				}
				System.out.println();
				break;
			case "3": // Manipulate Data
				while(true) {
					System.out.println("[Manipulate Data]");
					System.out.print("Please input the instruction number (1: Show Tables, 2: Describe Table, 3: Select, "
							+ "4: Insert, 5: Delete, 6: Update, 7: Drop Table, 8: Back to main) : ");
					man_instruction = sc.nextLine();
					String select; // select에 들어가는 string - parsing 필요
					
					switch (man_instruction) {
					case "1": // Show tables
						System.out.println("=======");
						System.out.println("Table List");
						System.out.println("=======");
						ResultSet rset2 = st.executeQuery
								("select table_name FROM information_schema.tables WHERE table_schema= '" + SCHEMA_NAME +"'");

						while(rset2.next()) {
							System.out.println(rset2.getString("table_name"));
						}

						//실제 연산에서 table의 이름들을 가져와서 출력함 (출력해야 할 table의 개수를 같이 리턴해주면 for문으로 돌리기)
						System.out.println();
						break;
					case "2": // Describe table
						System.out.print("Please specify the table name : ");
						table_name = sc.nextLine().trim();

						System.out.println("==================================================================");
						System.out.println("Column Name | Data Type | Character Maximum Length(or Numeric Precision and Scale)");
						System.out.println("==================================================================");
						//실제 연산에서 column name과 data type을 가져와서 출력 (출력해야 할 column 개수를 같이 리턴해주면 for문으로 돌리기)
						ResultSet rset1 = st.executeQuery("select column_name, data_type, character_maximum_length, numeric_precision, numeric_scale from information_schema.columns where table_name = '"+table_name+"'");
						while (rset1.next()) {
							if ((rset1.getInt("character_maximum_length")== 0) && (rset1.getInt("numeric_precision") ==0) && (rset1.getInt("numeric_scale")==0)){
								System.out.println(rset1.getString("column_name")+", "+ rset1.getString("data_type"));
								continue;
							} 
							if (rset1.getInt("character_maximum_length")== 0) {
								System.out.println(rset1.getString("column_name")+", "+ rset1.getString("data_type")+", " +"("+
										rset1.getInt("numeric_precision")+","+rset1.getInt("numeric_scale")+")");
							} else {
								System.out.println(rset1.getString("column_name")+", "+ rset1.getString("data_type")+", "+ rset1.getInt("character_maximum_length"));
							}

						}
						System.out.println();
						break;
					case "3": // Select
						String select_query = "SELECT "; 
						String s_order_column; //select query의 ordering이 필요한 column
						boolean s_condcheck=true; //condition boolean flag
						boolean s_existWhere=false; //select문에서 where절이 필요한지 check함.
						boolean s_skipCondition=true;
						String s_conditions=""; //select문의 where절의 문장.
						
						System.out.print("Please specify the table name : ");
						table_name = sc.nextLine();
						System.out.print("Please specify columns which you want to retrieve (All : *): ");
						select = sc.nextLine();
						
						//선택할 테이블 쿼리 생성 SELECT,FROM part
						String[] tableList = table_name.split(",");	
						for(int i=0;i<tableList.length;i++) { tableList[i] = tableList[i].trim(); }
						
						String[] select_column = select.split(","); //select할 column name을 가지고 있는 array						
						for(int i=0;i<select_column.length;i++) { select_column[i] = select_column[i].trim();}
						
						//select절 쿼리생성
						for(int i=0;i<select_column.length-1;i++) 
						{
							if(!(select_column[i].equals("*")))
							{
								select_query=select_query+"\""+select_column[i]+"\"" + ", "; 
							}
							else {select_query=select_query+select_column[i]+ ", "; }
						}
						
						if(!(select_column[select_column.length-1].equals("*")))
						{
							select_query = select_query +"\"" +select_column[select_column.length-1]+"\"" + " FROM ";
						}
						else {select_query = select_query +select_column[select_column.length-1] + " FROM ";}
						
						//from절 퀴리 생성
						for(int i=0;i<tableList.length - 1;i++) { select_query = select_query + SCHEMA_NAME +"."+"\""+tableList[i] + "\"" + ", "; }
						select_query =select_query + SCHEMA_NAME+"."+"\""+tableList[tableList.length-1]+"\"";
						//condition 부분은 따로 처리할 것.
						
						while(s_condcheck)
						{	
							String condition_column; // condition이 적용되는 column
							if(s_skipCondition)	
							{ 
								System.out.print("Please specify the column which you want to make condition (Proess enter : skip) : "); 
								condition_column = sc.nextLine().trim();
								if ( condition_column.isEmpty()) break;//만약 그냥 enter칠 경우 where절 부분 생략
							}
							else
							{
								while(true)
								{
									System.out.print("Please specify the column which you want to make condition : ");
									condition_column = sc.nextLine().trim();
									if ( !(condition_column.isEmpty())) break;//만약 그냥 enter칠 경우 where절 부분 생략
								}
							}
							s_existWhere=true;//where절이 들어감을 표시함
							int compareOp;
							String Operator="";
							
							while(true)
							{	//잘못된 숫자 들어오면 다시
								System.out.print("Please specify the condition (1: = , 2: >, 3: <, 4: >=, 5: <=, 6: !=, 7: LIKE) : ");
								String compareOps = sc.nextLine();
								compareOp=Integer.parseInt(compareOps);
								if (compareOp<8 && compareOp>0)
								{
									if (compareOp==1) Operator="=";
									else if (compareOp==2) Operator=">";
									else if (compareOp==3) Operator="<";
									else if (compareOp==4) Operator=">=";
									else if (compareOp==5) Operator="<=";
									else if (compareOp==6) Operator="!=";
									else if (compareOp==7) Operator="LIKE";
									break;
								}
							}
							
							System.out.print("Please specify the condition value (" + condition_column + Operator + " ?) : ");
							//도대체 왜 input을 안받고 지나가는거지...
							String compareValue = sc.nextLine();
							if(compareOp==7) compareValue="'" +compareValue+"'";//String을 LIKE 'regEx'형식에 맞춰줌.
							
							String boolOp="";
							while(true)
							{
								System.out.print("Please specify the condition value (1: AND, 2: OR, 3: finish) : ");
								String selectboolOps=sc.nextLine();
								if(selectboolOps.equals("1")){
									boolOp = "AND ";
									s_skipCondition=false;
									break;
								}
								else if(selectboolOps.equals("2")){
									boolOp = "OR ";
									s_skipCondition=false;
									break;
								}
								else if (selectboolOps.equals("3")) {
									//이 경우에는 condition loop를 빠져나옴
									s_condcheck=false;
									break;
								}
							}

							//condition query만들기 ''처리
							try {
								ResultSet[] rs_all_table = new ResultSet[tableList.length];
								ResultSetMetaData[] rs_meta = new ResultSetMetaData[tableList.length];
								int table_index = 0;
								int column_index = 1;
								for(int i=0;i<tableList.length;i++) {
									String get_all_table = "SELECT * FROM \"" + tableList[i] + "\"";
									rs_all_table[i] = st.executeQuery(get_all_table);
									rs_meta[i] = rs_all_table[i].getMetaData();
								}
								for(int i=0;i<tableList.length;i++) {
									for(int j=1;j<=rs_meta[i].getColumnCount();j++) {
										if(rs_meta[i].getColumnName(j).compareTo(condition_column) == 0) {
											table_index = i;
											column_index = j;
										}
									}
								}
								
								if(rs_meta[table_index].getColumnType(column_index) == 4 | rs_meta[table_index].getColumnType(column_index) == 2) {
									s_conditions=s_conditions+"\""+condition_column+"\"" + " " + Operator + " " + compareValue + " " + boolOp; 
								}
								else {
									s_conditions=s_conditions+"\""+condition_column+"\"" + " " + Operator + " '" + compareValue + "' " + boolOp; 
								}
							}
							catch (Exception e) { }
						}
						
						if (s_existWhere) select_query = select_query+" WHERE "+s_conditions;
						
						//order by part
						System.out.print("Please specify the column name for ordering (Press enter : skip) : ");
						s_order_column = sc.nextLine();
						if ( !(s_order_column.trim().isEmpty()))
						{
							select_query += " ORDER BY ";
							String[] orderAttributes = s_order_column.split(",");
							System.out.print("Please specify the sorting criteria (Press enter : skip); : ");
							
							String orderCondition = sc.nextLine();//정렬 방향을 정해줌.
							if ( !(s_order_column.trim().isEmpty()))
							{
								String[] orderList = orderCondition.split(",");
								for(int i=0;i<orderList.length - 1;i++) 
								{ 	if(orderList[i].equals("DESCEND")) 
									{
										select_query = select_query +"\""+orderAttributes[i]+"\""+" DESC, ";
									}
									else
									{
										select_query = select_query +"\""+orderAttributes[i]+"\""+" ASC, ";
									}
								}
								if(orderList[orderList.length-1].equals("DESCEND")) 
								{
									select_query =select_query+"\"" +orderAttributes[orderAttributes.length-1]+"\""+" DESC";
								}
								else
								{
									select_query = select_query +"\""+orderAttributes[orderAttributes.length-1]+"\""+" ASC";
								}
								
							}
						}
						select_query+=";";
						try 
						{
							ResultSet rset = st.executeQuery(select_query);//쿼리를 실행시킴, rset에 결과 tuple저장
							ResultSetMetaData rsmd = rset.getMetaData();//메타데이터 객체 생성
							int columnsNumber = rsmd.getColumnCount();
							String getColumnNames="";
							for(int i=1;i<columnsNumber;i++) {getColumnNames=getColumnNames+rsmd.getColumnName(i)+" | ";}
							getColumnNames+=rsmd.getColumnName(columnsNumber);
							System.out.println("=====================================================");
							System.out.println(getColumnNames);
							System.out.println("=====================================================");
							int countRow=0;
							while(rset.next())
							{
								
								for(int i=1; i<columnsNumber+1 ;i++)
								{
									System.out.print(rset.getString(i)+" ");
								}
								countRow+=1;
								if(countRow%10==0)
								{
									//10줄씩 끊어서 출력하는 매커니즘
									System.out.println();
									System.out.print("<Press enter>");
									sc.nextLine();//별 의미 없이 엔터를 치기 전까지 끊어주는 용도.
								}
								System.out.println();
							}
							if(countRow>=1) { System.out.println(countRow+" rows selected"); }
							else {System.out.println(countRow+" row selected");}
							

						}
						catch (Exception e){
							System.out.println("<error detected>");
						}						
						System.out.println();
						break;
					case "4": // Insert
						String values;
						System.out.print("Please specify the table name : ");
						table_name = sc.nextLine();
						System.out.print("Please specify all columns in order of which you want to insert : ");
						select = sc.nextLine();
						System.out.print("Please specify values for each column : ");
						values = sc.nextLine();
						
						//parsing으로 sql 쿼리 만들기
						String[] insert_column = select.split(","); //insert할 column name을 가지고 있는 array
						String[] insert_value = values.split(","); //insert할 value들을 가지고 있는 array
						
						//parsing
						for(int i=0;i<insert_column.length;i++) {
							insert_column[i] = insert_column[i].trim();
						}
						for(int i=0;i<insert_value.length;i++) {
							insert_value[i] = insert_value[i].trim();
						}
						
						//query만들기
						String insert_query = "INSERT INTO ";
						insert_query = insert_query +SCHEMA_NAME+"." + "\"" + table_name + "\"" + "(";
						for(int i=0;i<insert_column.length - 1;i++) {
							insert_query = insert_query + "\"" + insert_column[i] + "\"" + ", ";
						}
						insert_query = insert_query + "\"" + insert_column[insert_column.length - 1] + "\"" + ") VALUES (";
						
						// integer면 그대로, string이면 ''를 붙여서 query만들기
						for(int i=0;i<insert_value.length - 1;i++) {
							try {
							     Float.parseFloat(insert_value[i]);
							     insert_query = insert_query + insert_value[i] + ", ";
							}
							catch (NumberFormatException ex) {
							     insert_query = insert_query + "'" + insert_value[i] + "'" + ", ";
							}
						}
						try {
							Float.parseFloat(insert_value[insert_value.length - 1]);
							insert_query = insert_query + insert_value[insert_value.length - 1] +  ");";
						}
						catch (NumberFormatException ex) {
							insert_query = insert_query + "'" + insert_value[insert_value.length - 1] + "');";
						}
						
						try {
							int updated_columns = st.executeUpdate(insert_query);
							
							if(updated_columns ==0) {
								System.out.println("<0 row inserted due to error>");
							}
							else if(updated_columns == 1) {
								System.out.println("<1 row inserted>");
							}
							else {
								System.out.println("<" + updated_columns + " rows inserted>");
							}
						}
						catch (Exception e){
							System.out.println("<0 row inserted due to error>");
						}
						
						//연산 후 몇개의 row가 변경되었는지에 대한 정보가 나와야 하므로 전달되어야 함
						//연산 실패시 error출력되어야 함
						System.out.println();
						break;
					case "5": // Delete
						String delete_query = "DELETE FROM ";
						boolean d_condcheck=true; //condition boolean flag
						boolean d_existWhere=false; //delete문에 where절이 필요한지 check함.
						boolean d_skipCondition=true;
						String d_conditions=""; //where절의 문장.
						
						System.out.print("Please specify the table name : ");
						table_name = sc.nextLine();
						
						//삭제할 테이블 쿼리 생성 DELETE from 
						String[] d_tableList = table_name.split(",");	
						for(int i=0;i<d_tableList.length;i++) { d_tableList[i] = d_tableList[i].trim(); }
						for(int i=0;i<d_tableList.length - 1;i++) { delete_query = delete_query +SCHEMA_NAME+"." +"\""+d_tableList[i]+"\"" + ", "; }
						delete_query =delete_query +SCHEMA_NAME + "." + "\""+d_tableList[d_tableList.length-1]+"\"";
						
						while(d_condcheck)
						{	
							String condition_column; // condition이 적용되는 column
							if(d_skipCondition)	
							{ 
								System.out.print("Please specify the column which you want to make condition (Proess enter : skip) : "); 
								condition_column = sc.nextLine().trim();
								if ( condition_column.isEmpty()) break;//만약 그냥 enter칠 경우 where절 부분 생략
							}
							else
							{
								while(true)
								{
									System.out.print("Please specify the column which you want to make condition : ");
									condition_column = sc.nextLine().trim();
									if ( !(condition_column.isEmpty())) break;//만약 그냥 enter칠 경우 where절 부분 생략
								}
							}

							d_existWhere=true;
							int compareOp;
							String Operator="";
							
							while(true)
							{	//잘못된 숫자 들어오면 다시
								System.out.print("Please specify the condition (1: = , 2: >, 3: <, 4: >=, 5: <=, 6: !=, 7: LIKE) : ");
								String compareOps = sc.nextLine();
								compareOp=Integer.parseInt(compareOps);
								if (compareOp<8 && compareOp>0)
								{
									if (compareOp==1) Operator="=";
									else if (compareOp==2) Operator=">";
									else if (compareOp==3) Operator="<";
									else if (compareOp==4) Operator=">=";
									else if (compareOp==5) Operator="<=";
									else if (compareOp==6) Operator="!=";
									else if (compareOp==7) Operator="LIKE";
									break;
								}
							}
							
							System.out.print("Please specify the condition value (" + condition_column + Operator + " ?) : ");
							String compareValue = sc.nextLine();
							if(compareOp==7) compareValue="'" +compareValue+"'";//String을 LIKE 'regEx'형식에 맞춰줌.
							
							String boolOp="";
							while(true)
							{
								System.out.print("Please specify the condition value (1: AND, 2: OR, 3: finish) : ");
								String selectboolOps=sc.nextLine();
								if(selectboolOps.equals("1")){
									boolOp = "AND ";
									d_skipCondition=false;
									break;
								}
								else if(selectboolOps.equals("2")){
									boolOp = "OR ";
									d_skipCondition=false;
									break;
								}
								else if (selectboolOps.equals("3")) {
									//이 경우에는 condition loop를 빠져나옴
									d_condcheck=false;
									break;
								}
							}
							try {
								ResultSet[] rs_all_table = new ResultSet[d_tableList.length];
								ResultSetMetaData[] rs_meta = new ResultSetMetaData[d_tableList.length];
								int table_index = 0;
								int column_index = 1;
								for(int i=0;i<d_tableList.length;i++) {
									String get_all_table = "SELECT * FROM \"" + d_tableList[i] + "\"";
									rs_all_table[i] = st.executeQuery(get_all_table);
									rs_meta[i] = rs_all_table[i].getMetaData();
								}
								for(int i=0;i<d_tableList.length;i++) {
									for(int j=1;j<=rs_meta[i].getColumnCount();j++) {
										if(rs_meta[i].getColumnName(j).compareTo(condition_column) == 0) {
											table_index = i;
											column_index = j;
										}
									}
								}
								
								if(rs_meta[table_index].getColumnType(column_index) == 4 | rs_meta[table_index].getColumnType(column_index) == 2) {
									d_conditions=d_conditions+"\""+condition_column+"\"" + " " + Operator + " " + compareValue + " " + boolOp; 
								}
								else {
									d_conditions=d_conditions+"\""+condition_column+"\"" + " " + Operator + " '" + compareValue + "' " + boolOp; 
								}
							}
							catch (Exception e) { }					
						}
						
						if (d_existWhere) 
						{
							delete_query  = delete_query+" WHERE "+ d_conditions; 
						}
						delete_query+=";";
						
						//delete 쿼리 실행, 성공한 row의 갯수반환
						try {
							int deleted_columns = st.executeUpdate(delete_query);
							
							if(deleted_columns <= 1) {
								System.out.println("<"+deleted_columns+" row deleted>");
							}
							else {
								System.out.println("<" + deleted_columns + " rows deleted>");
							}
						}
						catch (Exception e){
							System.out.println("<error detected>");
						}
						System.out.println();
						break;
					case "6": // Update
						String update_query = "UPDATE ";
						boolean u_condcheck=true; //condition boolean flag
						boolean u_existWhere=false; //update에 where절이 필요한지 check함.
						boolean u_skipCondition = true;
						String u_conditions=""; //where절의 문장.
						
						System.out.print("Please specify the table name : ");
						table_name = sc.nextLine();
						
						//업데이트할 테이블을 쿼리에 추가함 
						String[] u_tableList = table_name.split(",");	
						for(int i=0;i<u_tableList.length;i++) { u_tableList[i] = u_tableList[i].trim(); }
						for(int i=0;i<u_tableList.length - 1;i++) { update_query = update_query +SCHEMA_NAME+"."+ "\""+u_tableList[i]+"\"" + ", "; }
						update_query =update_query + SCHEMA_NAME + "." +"\""+u_tableList[u_tableList.length-1]+"\"";
						
						//WHERE절, 조건확인
						while(u_condcheck)
						{	
							String condition_column; // condition이 적용되는 column
							if(u_skipCondition)	
							{ 
								System.out.print("Please specify the column which you want to make condition (Proess enter : skip) : "); 
								condition_column = sc.nextLine().trim();
								if ( condition_column.isEmpty()) break;//만약 그냥 enter칠 경우 where절 부분 생략
							}
							else
							{
								while(true)
								{
									System.out.print("Please specify the column which you want to make condition : ");
									condition_column = sc.nextLine().trim();
									if ( !(condition_column.isEmpty())) break;//만약 그냥 enter칠 경우 where절 부분 생략
								}
							}
							u_existWhere=true;
							int compareOp;
							String Operator="";
							
							while(true)
							{	//잘못된 숫자 들어오면 다시
								System.out.print("Please specify the condition (1: = , 2: >, 3: <, 4: >=, 5: <=, 6: !=, 7: LIKE) : ");
								String compareOps = sc.nextLine();
								compareOp=Integer.parseInt(compareOps);
								if (compareOp<8 && compareOp>0)
								{
									//사실 5.5같은 수가 들어오면 커버 못하지만 그렇게까지 나올까?
									if (compareOp==1) Operator="=";
									else if (compareOp==2) Operator=">";
									else if (compareOp==3) Operator="<";
									else if (compareOp==4) Operator=">=";
									else if (compareOp==5) Operator="<=";
									else if (compareOp==6) Operator="!=";
									else if (compareOp==7) Operator="LIKE";
									break;
								}
							}
							
							System.out.print("Please specify the condition value (" + condition_column + Operator + " ?) : ");
							String compareValue = sc.nextLine();
							if(compareOp==7) compareValue="'" +compareValue+"'";//String을 LIKE 'regEx'형식에 맞춰줌.
							
							String boolOp="";
							while(true)
							{
								System.out.print("Please specify the condition value (1: AND, 2: OR, 3: finish) : ");
								String selectboolOps=sc.nextLine();
								if(selectboolOps.equals("1")){
									boolOp = "AND ";
									u_skipCondition=false;
									break;
								}
								else if(selectboolOps.equals("2")){
									boolOp = "OR ";
									u_skipCondition=false;
									break;
								}
								else if (selectboolOps.equals("3")) {
									//이 경우에는 condition loop를 빠져나옴
									u_condcheck=false;
									break;
								}
							}
							//조건문 생성
							//condition query만들기 ''처리
							try {
								ResultSet[] rs_all_table = new ResultSet[u_tableList.length];
								ResultSetMetaData[] rs_meta = new ResultSetMetaData[u_tableList.length];
								int table_index = 0;
								int column_index = 1;
								for(int i=0;i<u_tableList.length;i++) {
									String get_all_table = "SELECT * FROM \"" + u_tableList[i] + "\"";
									rs_all_table[i] = st.executeQuery(get_all_table);
									rs_meta[i] = rs_all_table[i].getMetaData();
								}
								for(int i=0;i<u_tableList.length;i++) {
									for(int j=1;j<=rs_meta[i].getColumnCount();j++) {
										if(rs_meta[i].getColumnName(j).compareTo(condition_column) == 0) {
											table_index = i;
											column_index = j;
										}
									}
								}

								if(rs_meta[table_index].getColumnType(column_index) == 4 | rs_meta[table_index].getColumnType(column_index) == 2) {
									u_conditions=u_conditions+"\""+condition_column+"\"" + " " + Operator + " " + compareValue + " " + boolOp; 
								}
								else {
									u_conditions=u_conditions+"\""+condition_column+"\"" + " " + Operator + " '" + compareValue + "' " + boolOp; 
								}
							}
							catch (Exception e) { }							
						}
						update_query +=" SET ";

						//업데이트할 Attribute들을 입력함.
						System.out.print("Please specify column names which you want to update : ");
						String updateAttribute=sc.nextLine();
						String[] updateAttributes = updateAttribute.split(",");
						for(int i=0;i<updateAttributes.length;i++) { updateAttributes[i] = "\""+updateAttributes[i].trim()+"\""; }
						
						//업데이트할 값들을 입력함.
						System.out.print("Please specify the value which you want to put : ");
						String updateValue=sc.nextLine();
						String[] updateValues = updateValue.split(",");
						for(int i=0;i<updateValues.length;i++) { updateValues[i] = updateValues[i].trim(); }
						
						//update 쿼리 생성
						for(int i=0;i<updateAttributes.length-1;i++) { update_query = update_query+ updateAttributes[i] +"="+ updateValues[i]+ " , "; }
						update_query = update_query+ updateAttributes[updateAttributes.length-1] +"="+ updateValues[updateAttributes.length-1];
						
						if (u_existWhere) 
						{
							update_query  = update_query+" WHERE "+ u_conditions; 
						}
						
						update_query+=";";
						

						try {
							int update_columns = st.executeUpdate(update_query);
							
							if(update_columns <= 1) {
								System.out.println("<"+update_columns+" row updated>");
							}
							else {
								System.out.println("<" + update_columns + " rows updated>");
							}
						}
						catch (Exception e){
							System.out.println("<error detected>"+e);
						}
						System.out.println();
						break;
					case "7": // Drop table
						char sure; // 진짜로 table을 삭제할 것인가에 대해 물어보는 질문의 답을 저장
						System.out.print("Please specify the table name : ");
						table_name = sc.nextLine();
						do {
							System.out.print("If you delete this table, it is not guaranteed to recover again. Are you sure you want to delete this table (Y:yes, N:no)? ");
							sure = sc.nextLine().charAt(0);
						} while(sure != 'Y' && sure != 'N');
						if (sure == 'Y') {
							//실제 삭제 진행
							try {
								String drop_query = "DROP TABLE " + SCHEMA_NAME + "." + "\"" + table_name + "\"" + ";";
								st.executeUpdate(drop_query);
								System.out.println("<The table " + table_name + " is deleted>");
							}
							catch(SQLException se) {
								System.out.println("<ERROR>");
							}
							//연산
						}
						else {
							//삭제 안해
							System.out.println("<Deletion canceled>");
						}
						System.out.println();
						break;
					case "8": // Back to main
						break;
					}
					try {
						if(Integer.parseInt(man_instruction) == 8) {
							break;
						}
					}
					catch(NumberFormatException s) {s.printStackTrace();}
				}
				System.out.println();
				break;
			case "4": // Exit
				sc.close();
				System.exit(0);
				break;
			default:
				System.out.println();
				break;
			}
			st.close();
		}
	}
}
