package loro.arbol;

import loro.Rango;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

public class NQualifiedName extends NExpresion {
	public void aceptar(IVisitante v) throws VisitanteException {
		v.visitar(this);
	}

	String what;
	TNombre nombre;

	/**
	 */
	public NQualifiedName(Rango rango, String what, TNombre tnom) {
		super(rango);
		this.what = what;
		this.nombre = tnom;
	}

	public String getWhat() {
		return what;
	}
	
	public TNombre obtNombre() {
		return nombre;
	}
}
