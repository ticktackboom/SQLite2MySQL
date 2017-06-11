package sqlRepresentation;

import java.util.HashSet;

public class Tabla {
	private String nombre;
	private HashSet<Columna> columnas;
	private int numPK;
	private int numColumnas;
	
	public int getNumColumnas() {
		return numColumnas;
	}

	public void setNumColumnas(int numColumnas) {
		this.numColumnas = numColumnas;
	}

	public Tabla(String n) {
		this.nombre = n;
		columnas = new HashSet<>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public HashSet<Columna> getColumnas() {
		return columnas;
	}

	public void setColumnas(HashSet<Columna> columnas) {
		this.columnas = columnas;
	}

	public int getNumPK() {
		return numPK;
	}

	public void setNumPK(int numPK) {
		this.numPK = numPK;
	}
}
