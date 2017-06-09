package transform;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;

public class retrieveAllTables {
	private Connection conection;
	private HashSet<String> tableNames;

	public retrieveAllTables(String fileName) {
		try {
			conection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
			Statement stmn = conection.createStatement();

			String allTables = "select name from sqlite_master where type = 'table';";
			ResultSet res = stmn.executeQuery(allTables);
			
			tableNames = new HashSet<>();

			while (res.next())
				tableNames.add(res.getString("name"));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public HashSet<String> list() {
		return tableNames;
	}
}
