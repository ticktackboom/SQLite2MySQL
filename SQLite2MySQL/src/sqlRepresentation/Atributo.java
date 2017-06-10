package sqlRepresentation;

public class Atributo {
	private Tabla tablaPadre;
	private String nombre;
	private boolean isPrimaryKey;
	private String tipo;
	private boolean notNull;
	private String dflt_value;
	
	public Atributo(Tabla tbpadre, String n, String tipo) {
		tablaPadre = tbpadre;
		nombre = n;
		this.tipo = tipo;
	}
}
