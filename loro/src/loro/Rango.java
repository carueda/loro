package loro;

//////////////////////////////////////////////////////////////////
/**
 * Representa un zona de texto fuente.
 * En particular esta informaci�n est� disponible para cada nodo 
 * del �rbol de derivaci�n.
 *
 * <ul>   
 *	<li> Permite se�alar el texto comprometido ante un error de compilaci�n 
 *		o ejecuci�n.
 *
 *	<li> En un modo de ejecuci�n incremental, permite se�alar la zona de
 *		 texto de cada paso.
 *
 * </ul>
 *
 * @author Carlos Rueda
 */
public class Rango implements java.io.Serializable
{
	/** Posici�n inicial del rango. */
	protected int posIni;
	
	/** Posici�n final del rango. */
	protected int posFin;
	
	/** Columna final. */
	protected int finCol;
	/** Linea final. */
	protected int finLin;
	/** Columna inicial. */
	protected int iniCol;
	/** Linea inicial. */
	protected int iniLin;



	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la posicion final del rango.
	 */
	public int obtPosFin()
	{
		return posFin;
	}
	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la posicion final del rango.
	 */
	public int obtPosIni()
	{
		return posIni;
	}



	//////////////////////////////////////////////////////////////////
	/**
	 * Crea un rango que cubre otros rangos.
	 *
	 * @param ini Rango inicial.
	 * @param fin Rango final.
	 */
	public Rango(Rango ini, Rango fin)
	{
		iniLin = ini.obtIniLin();
		iniCol = ini.obtIniCol();

		finLin = fin.obtFinLin();
		finCol = fin.obtFinCol();

		posIni = ini.obtPosIni();
		posFin = fin.obtPosFin();

	}

	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la columna final.
	 */
	public int obtFinCol()
	{
		return finCol;
	}

	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la columna final.
	 */
	public int obtFinLin()
	{
		return finLin;
	}

	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la columna inicial.
	 */
	public int obtIniCol()
	{
		return iniCol;
	}

	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la linea inicial.
	 */
	public int obtIniLin()
	{
		return iniLin;
	}



	//////////////////////////////////////////////////////////////////
	/**
	 * Crea un rango. 
	 * Este constructor recibe todos los atributos asociados.
	 *
	 * @param posLin Posicion inicial
	 * @param posFin Posicion final
	 * @param iniLin L�nea inicial
	 * @param iniCol Columna inicial
	 * @param finLin L�nea final
	 * @param finCol Columna final
	 */
	public Rango(
		int posIni,
		int posFin,
		int iniLin, int iniCol,
		int finLin, int finCol
	)
	{
		this.posIni = posIni;
		this.posFin = posFin;

		this.iniLin = iniLin;
		this.iniCol = iniCol;
		this.finLin = finLin;
		this.finCol = finCol;
	}
}