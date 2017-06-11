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
import sqlRepresentation.Tabla;

public class SqliteConvert {
	private File theFile;
	private Connection connection;
	Statement stmn;

	private String mysqlSyntax;

	public String getMysqlSyntax() {
		return mysqlSyntax;
	}

	private HashSet<Tabla> knownTables;

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
		/*

		convertToMySQL();

		dumpContents();
		*/

	}

	private void registerTables() throws SQLException {
		String query = "select name from sqlite_master where type = 'table';";

		ResultSet res = stmn.executeQuery(query);

		knownTables = new HashSet<>();

		while (res.next()) {
			knownTables.add(new Tabla(res.getString("name")));
		}
	}

	public void registerColumns() throws SQLException {
		for (Tabla table : knownTables) {
			HashSet<Columna> lasColumnas = table.getColumnas();

			String query = "SELECT * from " + table.getNombre() + " LIMIT 1;";
			ResultSet res = stmn.executeQuery(query);
			
			table.setNumColumnas(res.getMetaData().getColumnCount());
			
			for (int i = 1; i <= table.getNumColumnas(); i++) {
				Columna newCol = new Columna(res.getMetaData().getColumnLabel(i));
				
				System.out.println("---------------");
				System.out.println("columnTypeName: " + res.getMetaData().getColumnTypeName(i));
				System.out.println("columnType: " + res.getMetaData().getColumnType(i));
				//System.out.println("columnDisplaySize: " + res.getMetaData().getColumnDisplaySize(i));
				System.out.println("columnLabel: " + res.getMetaData().getColumnLabel(i));
				System.out.println("columnName: " + res.getMetaData().getColumnName(i));
				System.out.println("scale: " + res.getMetaData().getScale(i));
				System.out.println("catalogName: " + res.getMetaData().getCatalogName(i));
				System.out.println("tableName: " + res.getMetaData().getTableName(i));
				//System.out.println("columnClassName: " + res.getMetaData().getColumnClassName(i));
				System.out.println("precision: " + res.getMetaData().getPrecision(i));
				System.out.println("schemaName: " + res.getMetaData().getSchemaName(i));
				System.out.println("nullable: " + res.getMetaData().isNullable(i));
				System.out.println("autoincrement: " + res.getMetaData().isAutoIncrement(i));
				System.out.println("caseSensitive: " + res.getMetaData().isCaseSensitive(i));
				System.out.println("currency: " + res.getMetaData().isCurrency(i));
				System.out.println("definetlyWrittable: " + res.getMetaData().isDefinitelyWritable(i));
				System.out.println("readOnly: " + res.getMetaData().isReadOnly(i));
				System.out.println("searchable: " + res.getMetaData().isSearchable(i));
				System.out.println("signed: " + res.getMetaData().isSigned(i));
				System.out.println("writtable: " + res.getMetaData().isWritable(i));
				System.out.println("---------");

				
				
				lasColumnas.add(newCol);
			}
/*
			while (res.next()) {
				Columna newCol = new Columna(res.getString("name"), res.getString("type"));
				newCol.setPrimaryKey(res.getBoolean("pk"));
				String dv = res.getString("dflt_value");
				if (dv != null)
					newCol.setDflt_value(dv);
				newCol.setNotNull(res.getBoolean("notnull"));
				lasColumnas.add(newCol);
			}
			*/
		}
	}

	private void convertToMySQL() throws SQLException {
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
					mysqlSyntax += "(" + length + ")";
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

	private void dumpContents() throws SQLException {
		for (Entry<String, HashSet<Columna>> tabla : knownTables.entrySet()) {
			String nombreTabla = tabla.getKey();
			ResultSet data = stmn.executeQuery("select * from " + nombreTabla);

			mysqlSyntax += "INSERT INTO " + nombreTabla + " (";

			int totalColumns = data.getMetaData().getColumnCount();

			for (int i = 1; i <= totalColumns; i++) {
				mysqlSyntax += data.getMetaData().getColumnName(i);
				if (i < totalColumns) {
					mysqlSyntax += ", ";
				}
			}

			mysqlSyntax += ")\nVALUES (";
			for (int i = 1; i <= totalColumns; i++) {
				System.out.println(data.getMetaData().getColumnType(i));
				mysqlSyntax += "-"+data.getMetaData().getColumnTypeName(i)+"-";
			}
			mysqlSyntax += ");\n\n";
		}
	}

}
