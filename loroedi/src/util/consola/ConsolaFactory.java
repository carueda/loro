package util.consola;

import java.awt.Point;
import java.awt.Frame;
import javax.swing.JFrame;


///////////////////////////////////////////////////////////
/**
 * Provee métodos estáticos para obtener diversos tipos
 * de consolas.
 *
 * @author Carlos Rueda
 * @version 0.1 (2001-02-23)
 * @version 0.2 (2001-09-18) - cambio de localizacion entre llamadas.
 */
public class ConsolaFactory
{
	static Point location = new Point(0, 0);

	///////////////////////////////////////////////
	/**
	 * Crea y retorna una consola en donde se separan las
	 * zonas de entrada y salida estándar.
	 */
	public static Consola crearConsola2()
	{
		Consola c = new Consola2();
		if ( c instanceof Frame )
		{
			Frame f = (Frame) c;
			f.setLocation(location);
			updateLocation();
		}
		return c;
	}
	///////////////////////////////////////////////
	/**
	 * Crea y retorna una consola basada en JTerm.
	 */
	public static Consola crearConsolaJTerm()
	{
		Consola c = new ConsolaJTerm();
		if ( c instanceof JFrame )
		{
			JFrame f = (JFrame) c;
			f.setLocation(location);
			updateLocation();
		}
		return c;
	}
	///////////////////////////////////////////////
	private static void updateLocation()
	{
		location.x += 10;
		location.y += 10;
		if ( location.x > 150 )
		{
			location.x = 0;
			location.y = 0;
		}
	}
}