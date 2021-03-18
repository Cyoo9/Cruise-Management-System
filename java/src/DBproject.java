/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Ship");
				System.out.println("2. Add Captain");
				System.out.println("3. Add Cruise");
				System.out.println("4. Book Cruise");
				System.out.println("5. List number of available seats for a given Cruise.");
				System.out.println("6. List total number of repairs per Ship in descending order");
				System.out.println("7. Find total number of passengers with a given status");
				System.out.println("8. < EXIT");
				
				switch (readChoice()){
					case 1: AddShip(esql); break;
					case 2: AddCaptain(esql); break;
					case 3: AddCruise(esql); break;
					case 4: BookCruise(esql); break;
					case 5: ListNumberOfAvailableSeats(esql); break;
					case 6: ListsTotalNumberOfRepairsPerShip(esql); break;
					case 7: FindPassengersCountWithStatus(esql); break;
					case 8: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static void AddShip(DBproject esql) {//1
		try {
			int shipid, shipage, shipseats;
			String shipmake, shipmodel;
			
			System.out.print("\tEnter ship id: "); 
			shipid = Integer.parseInt(in.readLine()); 
			
			System.out.print("\tEnter ship make: ");
			shipmake = in.readLine();
	
			System.out.print("\tEnter ship model: ");
			shipmodel = in.readLine();
			
			System.out.print("\tEnter ship age: ");
			shipage = Integer.parseInt(in.readLine());
			
			System.out.print("\tEnter number of seats: ");
			shipseats = Integer.parseInt(in.readLine());
			
			String query = "INSERT INTO Ship (id, make, model, age, seats) VALUES (" + shipid + ",'" + shipmake + "'," +  "'" + shipmodel + "'," + shipage + ',' + shipseats + ')';
			esql.executeQuery(query);  //insert the ship
			System.out.println("Ship inserted successfully!"); 
			
		} catch (Exception e) {
			System.err.println(e.getMessage()); 
		}
	} //end AddShip

	public static void AddCaptain(DBproject esql) {//2
		try {
			int capId; 
			String capName, capNation; 
			System.out.print("\tEnter captain id: ");
			capId = Integer.parseInt(in.readLine());
			
			System.out.print("\tEnter captain name: ");
			capName = in.readLine();
			
			System.out.print("\tEnter captain nationality: ");
			capNation = in.readLine(); 
			
			String query = "INSERT INTO Captain (id, fullname, nationality) VALUES (" + capId + ",'" + capName + "','" + capNation + "')";
			esql.executeQuery(query);
			System.out.println("Captain inserted successfully!"); 
		} catch (Exception e) {
			System.err.println(e.getMessage()); 
		}
		//end AddCaptain
	}

	public static void AddCruise(DBproject esql) {//3
		try {
			int cruiseNum, cruiseSold, cruiseStops, month, year, day, cruiseCost;
			String depDate, arrDate, arrPort, depPort;
			System.out.print("\tEnter cruise number: ");
			cruiseNum = Integer.parseInt(in.readLine());
			
			System.out.print("\tEnter cruise cost: ");
			cruiseCost = Integer.parseInt(in.readLine()); 
			while(!(cruiseCost > 0)) { 
				System.out.print("\tCruise cost can't be 0 or negative. Enter correct cost: ");
				cruiseCost = Integer.parseInt(in.readLine()); 
			}
			
			System.out.print("\tEnter cruises sold: "); 
			cruiseSold = Integer.parseInt(in.readLine());
			while(!(cruiseSold >= 0)) {
				System.out.print("\tCruises sold must be positive. Enter correct cruises sold: "); 
				cruiseSold = Integer.parseInt(in.readLine()); 
			}
			     
			System.out.print("\tEnter cruise stops: ");
			cruiseStops = Integer.parseInt(in.readLine());
			 
			System.out.print("\tEnter departure year: ");
			year = Integer.parseInt(in.readLine()); 
			System.out.print("\tEnter departure month: "); 
			month = Integer.parseInt(in.readLine()); 
			while(!(month >= 1 && month <= 12)) {
				System.out.print("\t Months must be between 1 and 12. Enter correct month: ");
				month = Integer.parseInt(in.readLine());
			}
			      
			System.out.print("\tEnter departure day: "); 
			day = Integer.parseInt(in.readLine()); 
			while(!(day >= 1 && day <= 31)) {
				System.out.print("\t Days must be between 1 and 31. Enter correct day: "); 
				day = Integer.parseInt(in.readLine()); 
			}
			depDate = year + '-' + month + '-' + day + ""; 
			      
			System.out.print("\tEnter arrival year: ");
			year = Integer.parseInt(in.readLine()); 
			System.out.print("\tEnter arrival month: "); 
			month = Integer.parseInt(in.readLine()); 
			while(!(month >= 1 && month <= 12)) {
				System.out.print("\t Months must be between 1 and 12. Enter correct month: ");
				month = Integer.parseInt(in.readLine());
			}
			      
			System.out.print("\tEnter arrival day: "); 
			day = Integer.parseInt(in.readLine()); 
			while(!(day >= 1 && day <= 31)) {
				System.out.print("\t Days must be between 1 and 31. Enter correct day: "); 
				day = Integer.parseInt(in.readLine()); 
			}
			      
			arrDate = year + '-' + month + '-' + day + ""; 
			      
			System.out.print("\tEnter arrival port: ");
			arrPort = in.readLine(); 
			
			System.out.print("\t Enter departure port: "); 
			depPort = in.readLine(); 
			      
			String query = "INSERT INTO Cruise (cnum, cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_port, departure_port) VALUES (" + cruiseNum + ',' + cruiseCost + ',' + cruiseSold + ',' + cruiseStops + ",'" + depDate + "','" + arrDate + "','" + arrPort + "','" + depPort + "')"; 
			esql.executeQuery(query);
			System.out.print("Cruise inserted successfully"); 
		}
		catch (Exception e) {
			System.err.println(e.getMessage()); 
		}
	}


	public static void BookCruise(DBproject esql) {//4
		// Given a customer and a Cruise that he/she wants to book, add a reservation to the DB
		
		// Given a customer and Cruise that he/she wants to book, determine the status of the
		// reservation (Waitlisted/Confirmed/Reserved) and add the reservation to the database with appropriate status.
		
		// Reservation(rnum, ccid, cid, status)
		// rnum = sequential id?
		// ccid = customer_id
		// cid = cruise number
		// status = W, C, or R
		
		try {
			int rnum, ccid, cid;
			String status;
			
			// if rnum generated by DB, need to assign sequentially w/ trigger most likely
			// for first run, assume given as input
			System.out.print("\tEnter reservation number: "); 
			rnum = Integer.parseInt(in.readLine()); 
			
			// need to check if customer exists
			System.out.print("\tEnter customer id: "); 
			ccid = Integer.parseInt(in.readLine()); 
			
			// need to check if cruise exists
			System.out.print("\tEnter cruise number: "); 
			cid = Integer.parseInt(in.readLine()); 
			
			// if reservation for cruise is full, status = waitlist 'W'
			// if not full, status = reserved 'R'
			// after cruise sails, status = confirmed 'C'
			// for first test, assume given as input
			
			System.out.print("\tEnter reservation status: "); 
			status = in.readLine();
			
			String query = "INSERT INTO Reservation (rnum, ccid, cid, status) VALUES ("
				+ rnum + "," + ccid + "," + cid + ",'" + status + "')";
			esql.executeQuery(query);
			System.out.println("Reservation inserted successfully!");
			
		} catch (Exception e) {
			System.err.println(e.getMessage()); 
		}
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) {//5
		// For Cruise number and date, find the number of available seats (i.e. total Ship capacity minus booked seats )
		try {
			// Cruise(cnum, cost, num_sold,, num_stops, actual_departure_date, actual_arrival_date
			//        arrival_port, departure_port)
			// Ship(id, make, model, age, seats)
			
			// need to check if cruise exists
			// assuming "date" means departure date
			// need to check if cruise departed already
			
			int cnum;
			int year, month, day;
			String depDate; 
			
			System.out.print("\tEnter cruise number: "); 
			cnum = Integer.parseInt(in.readLine()); 
			
			//need to create a function to parse and verify date to use throughout program!
			System.out.print("\tEnter departure year: ");
			year = Integer.parseInt(in.readLine()); 
			System.out.print("\tEnter departure month: "); 
			month = Integer.parseInt(in.readLine()); 
			while(!(month >= 1 && month <= 12)) {
				System.out.print("\t Months must be between 1 and 12. Enter correct month: ");
				month = Integer.parseInt(in.readLine());
			}
			      
			System.out.print("\tEnter departure day: "); 
			day = Integer.parseInt(in.readLine()); 
			while(!(day >= 1 && day <= 31)) {
				System.out.print("\t Days must be between 1 and 31. Enter correct day: "); 
				day = Integer.parseInt(in.readLine()); 
			}
			depDate = year + '-' + month + '-' + day + ""; 
			
			// total ship capacity
			// also check if cruise with exists?
			String searchShipQuery = "SELECT S.seats " +
						 "FROM Ship S, CruiseInfo CI " +
						 "WHERE CI.ship_id = S.id AND CI.cruise_id = " + cnum + ";";
			esql.executeQuery(searchShipQuery);
			
			// need to get result from above query to subtract below
			
			// booked seats
			// also output if cruise full?
			String bookedSeatsQuery = "SELECT COUNT(R.rnum) " + 
						  "FROM Reservation R " +
						  "WHERE R.status = 'R' AND R.cid = " + cnum + ";";
			esql.executeQuery(bookedSeatsQuery);
			
		} catch (Exception e) {
			System.err.println(e.getMessage()); 
		}
	}

	public static void ListsTotalNumberOfRepairsPerShip(DBproject esql) {//6
		// Count number of repairs per Ships and list them in descending order
		try {
			
		} catch (Exception e) {
			System.err.println(e.getMessage()); 
		}
	}

	
	public static void FindPassengersCountWithStatus(DBproject esql) {//7
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
		try {
			String status; 
			System.out.println("Enter status: ");
			status = in.readLine(); 
			
			String query = "SELECT COUNT(Customer.id) FROM Customer, Reservation WHERE Customer.id = Reservation.ccid AND Reservation.status =" + "'" + status + "'";
			int rowCount = esql.executeQuery(query);
			System.out.println ("total customers: " + rowCount);
		} catch(Exception e) {
			System.err.println(e.getMessage()); 
		}
	}
}
