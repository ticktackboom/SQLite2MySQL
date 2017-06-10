package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import transform.SqliteConvert;

public class TestSqlite {
	public static void main(String[] args) {
		System.out.println("Pruebas");
		
		try {
			SqliteConvert x = new SqliteConvert(new File("pruebadb.sqlite"));
			System.out.println(x.getMysqlSyntax());
		} catch (FileNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
