package test;

import transform.retrieveAllTables;

public class TestSqlite {
	public static void main(String[] args) {
		System.out.println("Pruebas");
		
		new retrieveAllTables("pruebadb.sqlite");
	}
}
