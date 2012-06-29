/**
 * ------------------------------------------------------------------------
 *
 * (C) Copyright Ericsson AB, 2007
 *
 * The copyright to the computer program (s) herein is the property of
 * Ericsson AB, Sweden. The program (s) may be used and/or
 * copied only with the written permission from Ericsson AB
 * or in accordance with the terms and conditions stipulated in the
 * agreement/contract under which the program (s) has been supplied.
 * All rights reserved.
 *
 * ------------------------------------------------------------------------
 * Ericsson MA Composer - Java Downstream
 * ------------------------------------------------------------------------
 */

package foo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.OracleResultSetMetaData;


public class DBLink  {
	/***************************************************************************
	 * Link Constructor *
	 **************************************************************************/

	private String databaseUrl = null;
	private String aaaDBHost = null;
	private String aaaDBPort = null;
	private String aaaDBSID = null;
	private String aaaDBUserName = null;
	private String aaaDBPassword = null;

	private Connection connection = null;
	private Statement statement = null;
	private boolean debug;

	public DBLink() {
		System.out.println("DBLink()");
	}

	/***************************************************************************
	 * Set Configuration *
	 **************************************************************************/
	public void setConfiguration(String[] configurations)
			 {
		
		System.out.println("setConfiguration:");

		if (configurations.length != 6) {
			log("Configuration parameters are missing-----"
					+ configurations.length);
			
		}
		// nodeType = configurations[4];

		try {
			this.aaaDBHost = configurations[0];
			this.aaaDBPort = configurations[1];
			this.aaaDBSID = configurations[2];
			this.aaaDBUserName = configurations[3];
			this.aaaDBPassword = configurations[4];
		} catch (Exception e) {
			log("Failed to set the configuration for Link "
					+ e.getMessage());
			
		}

		this.debug = configurations[5].startsWith("true");
		this.databaseUrl = ("jdbc:oracle:thin:@" + this.aaaDBHost + ":"
				+ this.aaaDBPort + ":" + this.aaaDBSID);
		// this.databaseUrl = ("jdbc:oracle:thin://@" + this.aaaDBHost + ":" +
		// this.aaaDBPort + "/" + this.aaaDBSID);
		// jdbc:oracle:thin://@<host>:<port>/<sid>

		
	}

	/***************************************************************************
	 * Link Connection *
	 **************************************************************************/
	public void connect()  {
		// Set up the connection to the NE
		// A simple socket is used
		// user can implement any other protocols here
		System.out.println("trying to make connection");
		try {
			System.out.println("connect method..");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println(this.databaseUrl + "-----" + this.aaaDBUserName
					+ "------- " + this.aaaDBPassword);
			this.connection = DriverManager.getConnection(this.databaseUrl,
					this.aaaDBUserName, this.aaaDBPassword);
			System.out.println("connection made sucessfully.");
		} catch (ClassNotFoundException e) {
			log("Could not loaded Database driver('oracle.jdbc.driver.OracleDriver') - "
					+ e.getMessage());
			
		} catch (SQLException e) {
			log("Could not connect to database( server) - "
					+ e.getMessage());
			e.printStackTrace();
			
		}
		System.out.println("connection established sucessfully.");
	}

	/***************************************************************************
	 * Send Message *
	 **************************************************************************/
	public String sendMessage(String query) {
		// send MML command
		// to the NE and return the NE response
		System.out.println("receive:" + query);
		String resp = "";
		String response = "";
		// TODO Auto-generated method stub
		if ((query.toLowerCase().startsWith("insert"))
				|| (query.toLowerCase().startsWith("update"))
				|| (query.toLowerCase().startsWith("delete"))) {
			log("Going to execute query in server: " + query);
			response = executeOperation(query);
			log("Executed SQL query in server and response is = "
					+ response);
		} else if (query.toLowerCase().startsWith("select")) {
			log("Going to send select query to server: " + query);
			response = getOperation(query);
			System.out.println("aaaServerResponse" + response);
			if (!response.contains("ORA-")) {
				response = "QUERY EXECUTED :" + response;
			}
			log("Executed select query in server and response is = "
					+ response);
		} else {
			log("Invalid Database Operation");
		}

		System.out.println("returning String:" + response);
		return response;
	}

	/***************************************************************************
	 * Link Disconnection *
	 **************************************************************************/
	public void disconnect() {
		// To close the connection to the NE
		System.out.println("disconnect()");

		try {
			log("Going to close database connection");
			this.connection.close();
		} catch (SQLException e) {
			log("SqlException Occurred while closing  database server So retrying once more.");
			try {
				this.connection.close();
				log("successfully closed the database connection");
			} catch (SQLException e1) {
				log("Could not close Database connection  server : "
						+ e1.getMessage());
			
			}
		}

	}

	/**
	 * 
	 * @param (create/update/insert) sql query
	 * @return Server (create/update/insert) query response
	 * 
	 */
	public String executeOperation(String query)  {
		String aaaServerResponse = "";
		try {
			connection.setAutoCommit(false);
			System.out.println("1");
			statement = connection.createStatement();
			System.out.println("2");
			int j = statement.executeUpdate(query);
			System.out.println("3");
			connection.commit();
			System.out.println("4");
			if (j == 0) {
				aaaServerResponse = "ORA-00002: Subscriber doesnot exist";
			} else {
				aaaServerResponse = "QUERY EXECUTED";
			}
		} catch (SQLException e) {
			aaaServerResponse = e.getMessage();
			log("Could not update the database - " + aaaServerResponse);
		} catch (Exception e) {
			log("Got DB exception :" + e.getMessage());
		} finally {
			if (statement != null) {
				try {
					System.out.println("closing statement");
					statement.close();
				} catch (SQLException e) {
					log("SqlException Occurred while closing  database server So retrying one more time.");
					try {
						connection.close();
						log("successfully closed the database connection");
					} catch (SQLException e1) {
						log("Could not close Database connection  server : "
								+ e1.getMessage());
						
					}
				}
			}
		}
		System.out.println("Insert/Update/Delete Query executed sucessfully ");
		System.out.println("aaaServerResponse :" + aaaServerResponse);
		return aaaServerResponse;
	}

	/**
	 * This method will fetch all the fields from the database .
	 * 
	 * @param select
	 *            sql query
	 * @return AAAServer select query response
	 * @throws DownStreamException
	 */
	/**
	 * @param query
	 * @return
	 * @throws DownStreamException
	 */
	public String getOperation(String query) {
		String aaaServerResponse = "";
		try {
//			statement = connection.createStatement(
//					ResultSet.TYPE_SCROLL_INSENSITIVE,
//					ResultSet.CONCUR_READ_ONLY);
			boolean flag = false;
			statement = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = statement.executeQuery(query);
			StringBuffer rec = new StringBuffer();
			String res = null;
			resultSet.last();
			int numrows = resultSet.getRow();
			if (numrows == 0) {
				log("Subscriber is not present in server ");
				aaaServerResponse = "ORA-00002: Subscriber doesnot exist";
			} else {
				resultSet.beforeFirst();
				OracleResultSetMetaData rmd = (OracleResultSetMetaData) resultSet.getMetaData();

				int columnCount = rmd.getColumnCount();
				while (resultSet.next()) {
					for (int i = 1; i <= columnCount; i++) {
						String columnName = rmd.getColumnName(i);

						if (columnName.equalsIgnoreCase("apnid")) {
//							System.out.println("1");
							System.out.println(resultSet.getString(columnName));
//							rec.append(resultSet.getString(columnName));
							if(flag == true)
							{
								res += ":" ;
							}
							
							
							res += resultSet.getString(columnName);
							flag = true;
							
							
						}

						if (columnName.equalsIgnoreCase("priority")) {
//							System.out.println("1");
							System.out.println(resultSet.getString(columnName));
							rec.append(resultSet.getString(columnName));
							res += "," ;
							res += resultSet.getString(columnName);
						}

						if (columnName.equalsIgnoreCase("associated")) {
//							System.out.println("1");
							System.out.println(resultSet.getString(columnName));
//							rec.append(resultSet.getString(columnName));
							res += "," ;
							res += resultSet.getString(columnName);
						}
						
					}
					// rec.append(":");
				}
				aaaServerResponse = res;
			
				// aaaServerResponse = rec.toString().substring(0,
				// rec.toString().length() - 1);
			}
		} catch (SQLException e) {
			aaaServerResponse = e.getMessage();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					log("SqlException Occurred while closing  database server So retrying one more time.");
					try {
						connection.close();
						log("successfully closed the database connection");
					} catch (SQLException e1) {
						log("Could not close Database connection  server : "
								+ e1.getMessage());
						
					}
				}
			}
		}
		System.out.println("Select Query executed sucessfully ");
		System.out.println("aaaServerResponse returned :" + aaaServerResponse);
		return aaaServerResponse;
	}

	/**
	 * To print a logs
	 * @param log
	 */
	private void log(String log) {
		if (this.debug)
			System.out.println("[ DBLink.java ] - " + log);
	}
}
