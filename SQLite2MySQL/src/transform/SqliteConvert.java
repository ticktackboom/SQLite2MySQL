package transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import sqlRepresentation.Columna;

public class SqliteConvert {
	private File theFile;
	private Connection connection;
	Statement stmn;

	private String mysqlSyntax;

	public String getMysqlSyntax() {
		return mysqlSyntax;
	}

	private HashMap<String, HashSet<Columna>> knownTables;

	public SqliteConvert(File file) throws SQLException, FileNotFoundException {
		mysqlSyntax = "";
		/*
		 * Si el fichero no existe, lanzo excepción para impedir que se ejecute
		 * la clase.
		 */
		if (!file.exists())
			throw new FileNotFoundException();

		/*
		 * Creo un nuevo controlador de SQLite, que será gracias al cual se
		 * leerá el fichero.
		 */
		connection = DriverManager.getConnection("jdbc:sqlite:" + file);

		/*
		 * Guardo la referencia al fichero en un atributo para poderlo usar en
		 * el futuro.
		 */
		theFile = file;

		stmn = connection.createStatement();

		/*
		 * Se rellenará el HashMap con las tablas existentes en el SQLite.
		 * 
		 * El formato es un HashMap que contiene como clave el nombre de la
		 * tabla, y como atributo un HashSet con el nombre de las columnas.
		 */

		registerTables();

		registerColumns();

		convertToMySQL();

	}

	private void registerTables() throws SQLException {

		String query = "select name from sqlite_master where type = 'table';";

		ResultSet res = stmn.executeQuery(query);

		knownTables = new HashMap<>();

		while (res.next())
			knownTables.put(res.getString("name"), new HashSet<Columna>());
	}

	public void registerColumns() throws SQLException {
		for (Entry<String, HashSet<Columna>> table : knownTables.entrySet()) {
			String query = "PRAGMA table_info(" + table.getKey() + ");";
			ResultSet res = stmn.executeQuery(query);

			while (res.next()) {
				HashSet<Columna> lasColumnas = table.getValue();
				Columna newCol = new Columna(res.getString("name"), res.getString("type"));
				newCol.setPrimaryKey(res.getBoolean("pk"));
				String dv = res.getString("dflt_value");
				if (dv != null)
					newCol.setDflt_value(dv);
				newCol.setNotNull(res.getBoolean("notnull"));
				lasColumnas.add(newCol);
			}
		}
	}

	public void convertToMySQL() throws SQLException {
		for (Entry<String, HashSet<Columna>> tabla : knownTables.entrySet()) {
			String nombreTabla = tabla.getKey();
			HashSet<Columna> nombresColumnas = tabla.getValue();

			mysqlSyntax += "CREATE TABLE " + nombreTabla + " (";

			int pos = 0;
			for (Columna col : nombresColumnas) {
				mysqlSyntax += col.getNombre() + " " + dataTypes(col.getTipo());
				
				if (dataTypes(col.getTipo()).equals("VARCHAR")) {
					ResultSet maxLength = stmn.executeQuery("select MAX(LENGTH(column2)) from pruebas;");
					int length = maxLength.getInt(1);
					mysqlSyntax += "("+length+")";
				}

				if (col.isNotNull())
					mysqlSyntax += " NOT NULL";

				if (col.getDflt_value() != null)
					mysqlSyntax += " DEFAULT " + col.getDflt_value();

				if (col.isUniq())
					mysqlSyntax += " UNIQUE";

				if (pos < nombresColumnas.size() - 1)
					mysqlSyntax += ",\n";
				pos++;
			}

			boolean hayPK = false;
			for (Columna col : nombresColumnas) {
				if (col.isPrimaryKey()) {
					if (!hayPK) {
						mysqlSyntax += ",\nPRIMARY KEY(";
						hayPK = true;
					}
					mysqlSyntax += col.getNombre() + ", ";

				}
			}

			if (hayPK) {
				mysqlSyntax = mysqlSyntax.substring(0, mysqlSyntax.length() - 2);
				mysqlSyntax += ")";
			}

			mysqlSyntax += ");";

			mysqlSyntax += "\n\n";

		}

	}

	private String dataTypes(String sqliteType) {
		switch (sqliteType) {
		case "TEXT":
			return "VARCHAR";
		case "INTEGER":
			return "INTEGER";
		case "BLOB":
			return "BINARY";
		case "REAL":
			return "DECIMAL";
		case "NUMERIC":
			return "DECIMAL";

		}
		return "TEXT";
	}

}
