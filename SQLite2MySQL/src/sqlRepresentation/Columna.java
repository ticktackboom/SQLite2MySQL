package sqlRepresentation;

public class Columna {
	private String nombre;
	private String tipo;
	private boolean notNull;
	private String dflt_value;
	private boolean primaryKey;
	private boolean au;
	private boolean uniq;

	public Columna(String n, String t) {
		nombre = n;
		tipo = t;
	}

	@Override
	public String toString() {
		String resultado = "[Nombre: " + nombre + ", Tipo: " + tipo;

		resultado += ", ";
		if (notNull) {
			resultado += "NOT ";
		}
		resultado += "NULL";

		if (primaryKey)
			resultado += ", PRIMARY KEY";

		if (au)
			resultado += ", AUTOINCREMENT";

		if (uniq)
			resultado += ", UNIQUE";
		
		if (dflt_value != null)
			resultado += ", DEFAULT " + dflt_value;
		
		return resultado + "]";

	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public String getDflt_value() {
		return dflt_value;
	}

	public void setDflt_value(String dflt_value) {
		this.dflt_value = dflt_value;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isAu() {
		return au;
	}

	public void setAu(boolean au) {
		this.au = au;
	}

	public boolean isUniq() {
		return uniq;
	}

	public void setUniq(boolean uniq) {
		this.uniq = uniq;
	}

}
