package foo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Hello world!
 * 
 */
public class App {

	

	
	public static employee emp = new employee();
	static Scanner scanner = new Scanner(System.in);
//	private static Logger log;
	
	

	public static String configurations[] = { "127.0.0.1", "1521", "emadb0",
			"username", "password", "true" };

	
	/**
	 * This is our first method of employee class, where all handling starts
	 */
	public static void executeEmployee() {
		
		String ip = null;
		String port = null;
		String sid = null;
		String username = null;
		String password = null;
		
		String currentDir = new File("").getAbsolutePath();
		System.out.println(currentDir);
		File file = new File(currentDir + "\\bbconfig.properties");
		Properties prop = new Properties();
		try{
		prop.load(new FileInputStream(file));
		System.out.println(file.getAbsoluteFile().getParentFile().getAbsolutePath());
		System.setProperty("log.home", file.getAbsoluteFile().getParentFile().getAbsolutePath());
		
		ip = prop.getProperty("endpoint");
		port = prop.getProperty("port");
		sid = prop.getProperty("sid");
		username = prop.getProperty("username");
		password = prop.getProperty("password");
	
		configurations[0] = ip;
		configurations[1] = port;
		configurations[2] = sid;
		configurations[3] = username;
		configurations[4] = password;
		configurations[5] = "true";
		
		System.out.println("configuration pass words changed according to read from file");
	}catch (FileNotFoundException e) {
//		log.error(e.getMessage());
		e.printStackTrace();
	} catch (IOException e) {
//		log.error(e.getMessage());
		e.printStackTrace();
	}
		
		String operation = null;
		String temp = null;
		System.out.println("IPAdress/port and other parameters have to be changed in code.");
		System.out.println("waiting for input from user...");
		System.out
				.println("enter one of add/search/delete/help/quit operations");

		// open up standard input

		// operation = scanner.nextLine();

		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(System.in));

		// operation = br.readLine();
		operation = scanner.nextLine();

		System.out.println("operation selected by user is:" + operation);

		if (operation == "add" || operation.equals("add")) {
			doAddOperation();
			System.out.println("Values have been added sucessfully");

		} else if (operation.equals("search")) {
			System.out
					.println("enter one option ,search by name/mobile/address");
			temp = scanner.nextLine();
			doSearchOperation(temp);
			System.out.println("query executed sucessfully.");
		} else if (operation.equals("delete")) {
			System.out
					.println("enter one option ,delete by name/mobile/address");
			temp = scanner.nextLine();
			doDeleteOperation(temp);
			System.out.println("Delete operation executed sucessfully.");
		} else if (operation.equals("help")) {
			System.out
					.println("For help you can contact arvind.kharkongor@ericsson .com");
		} else if (operation.equals("quit")) {
			System.out.println("quitting application...");
			System.exit(0);

		}

	}

	/**
	 * Delete Operation
	 * @param temp
	 */
	private static void doDeleteOperation(String temp) {
		// TODO Auto-generated method stub
		String query = null;
		boolean isMobOK = false;
		query = "DELETE * FROM test WHERE";
		System.out.println("searching by temp...");

		if (temp.equals("name")) {
			System.out.println("enter name:");
			temp = scanner.nextLine();
			query += " Name = ";
			query += "'";
			query += emp.getName().trim();
			query += "'";
			query += ";";
		} else if (temp.equals("mobile")) {
			System.out.println("enter mobile:");
			temp = scanner.nextLine();
			isMobOK = isInteger(temp);
			if(!isMobOK)
			{
				System.out.println("please enter correct mobile number, exiting");
				System.exit(0);
			}
			query += " Mobile = ";
			query += emp.getMobile().trim();
			query += ";";
		} else if (temp.equals("address")) {
			System.out.println("enter address:");
			temp = scanner.nextLine();
			query += " Address = ";
			query += "'";
			query += emp.getHome_address().trim();
			query += "'";
			query += ";";

		}

		System.out.println("query to be executed is:" + query);

		DBLink link = new DBLink();
		link.setConfiguration(configurations);
		try {
			link.sendMessage(query);
			System.out.println("query executed sucessfully");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Search operation
	 * @param temp
	 *            is to search by name/mobile/address
	 */
	private static void doSearchOperation(String temp) {
		// TODO Auto-generated method stub
		String query = null;
		boolean isMobOK = false;
		query = "SELECT * FROM test WHERE";
		System.out.println("searching by temp...");

		if (temp.equals("name")) {
			System.out.println("enter name:");
			temp = scanner.nextLine();
			query += " Name = ";
			query += "'";
			query += emp.getName().trim();
			query += "'";
			query += ";";
		} else if (temp.equals("mobile")) {
			System.out.println("enter mobile:");
			temp = scanner.nextLine();
			
			isMobOK = isInteger(temp);
			if(!isMobOK)
			{
				System.out.println("please enter correct mobile number, exiting");
				System.exit(0);
			}
			
			query += " Mobile = ";
			query += emp.getMobile().trim();
			query += ";";
		} else if (temp.equals("address")) {
			System.out.println("enter address:");
			temp = scanner.nextLine();
			query += " Address = ";
			query += "'";
			query += emp.getHome_address().trim();
			query += "'";
			query += ";";

		}

		System.out.println("query to be executed is:" + query);

		DBLink link = new DBLink();
		link.setConfiguration(configurations);
		try {
			link.sendMessage(query);
			System.out.println("query executed sucessfully");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * adds a employee
	 */
	private static void doAddOperation() {
		// TODO Auto-generated method stub
		String query = null;
		boolean isMobOK = false;
		System.out.println("enter name:");
		emp.setName(scanner.nextLine());
		System.out.println("enter mobile:");
		emp.setMobile(scanner.nextLine());
		isMobOK = isInteger(emp.getMobile());
		if(!isMobOK)
		{
			System.out.println("please enter correct mobile number, exiting");
			System.exit(0);
		}
		
		System.out.println("enter addres:");
		emp.setHome_address(scanner.nextLine());

		query = "INSERT INTO test VALUES(";
		query += "'";
		query += emp.getName().trim();
		query += "'";
		query += ",";
		query += emp.getMobile().trim();
		query += ",";
		query += "'";
		query += emp.getHome_address().trim();
		query += "')";
		query += ";";
		System.out.println("query to be executed is:" + query);

		DBLink link = new DBLink();
		link.setConfiguration(configurations);
		try {
			link.sendMessage(query);
			System.out.println("values inserted successfully");
		} catch (Exception e) {
			if (e.getMessage().contains("ORA-00942")) {
				System.out
						.println("table or view does not exists so creating table");
				try {
					link
							.sendMessage("CREATE TABLE test(Name VARCHAR(20),Mobile INTEGER,Address VARCHAR(50));");
					System.out.println("table created sucessfully");

					link.sendMessage(query);
					System.out.println("values inserted successfully");
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}

		}
		System.out.println("address entry added");
	}
	public static boolean isInteger(String num){
		
		int i = 0;
		try{
		 i = Integer.parseInt(num); 
		 System.out.println("integer test passed, mobile number is integer");
		 return true;
		}catch(NumberFormatException e)
		{
			System.out.println("mob is not an integer, so returning false");
			return false;
		}
		
	}
}
