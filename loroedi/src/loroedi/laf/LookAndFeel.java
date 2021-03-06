package loroedi.laf;

import loroedi.Preferencias;
import loroedi.Configuracion;
import loro.Loro;

import java.io.File;
import javax.swing.UIManager;

/////////////////////////////////////////////////////////////////////
/**
 * LookAndFeel - Para establecer el L&F de la sesi�n LoroEDI.
 *
 * @author Carlos Rueda
 * @version 2002-05-21
 */
public class LookAndFeel
{
	/**
	 * Propiedad para identificar el L&amp;F a cargar.
	 * Esta propiedad tiene el nombre "loroedi.pref.laf".
	 */
	public static final String LOROEDI_PREF_LAF = "loroedi.pref.laf";

	/**
	 * Valor especial de la propiedad LOROEDI_PREF_LAF para designar el
	 * L&amp;F por defecto, en cuyo caso no se hace nada.
	 */
	public static final String IGNORE = "ignore";

	/////////////////////////////////////////////////////////////////////
	/**
	 * Pone el LookAndFeel de acuerdo con el valor de la la preferencia LOROEDI_PREF_LAF:
	 * <ul>
	 *	<li>Si esta preferencia no esta definida, se intenta mirando como propiedad
	 *      del sistema (System.getProperty).
	 *	<li>Si la preferencia es nula, se pone el L&amp;F de Kunststoff.
	 *	<li>Si es igual a IGNORE, no se hace nada. 
	 *	<li>Se interpreta ahora como ruta de un tema para SkinLF; si no existe
	 *		tal como se indica, se intenta finalmente anteponiendo el directorio
	 *		lib/themes/ bajo la instalaci�n del sistema.
	 * </ul>
	 */
	public static void setLookAndFeel()
	{
		String pref_laf = Preferencias.obtPreferencia(LOROEDI_PREF_LAF);
		
		if ( pref_laf.length() == 0 )  // no definida?
			pref_laf = System.getProperty(LOROEDI_PREF_LAF, "");
		
		if ( pref_laf.length() == 0 )  // no definida?
		{
			// Se pone Kunststoff:
			try
			{
				// sigo prefiriendo Kunststoff :-)
				UIManager.setLookAndFeel("com.incors.plaf.kunststoff.KunststoffLookAndFeel");
				//UIManager.setLookAndFeel("org.compiere.plaf.CompiereLookAndFeel");
			}
			catch(Exception e)
			{
				Loro.log("LoroEDI: cannot set Kunststoff L&F");
			}
			return;
		}

		// ignore?
		if ( pref_laf.equalsIgnoreCase(IGNORE) )
		{
			// no se hace nada:
			Loro.log("LoroEDI: " + "ignoring L&F");
			return;
		}

		// Intente como ruta hasta tema para SkinLF:
		File file = new File(pref_laf);
		if ( !file.exists() )
		{
			Loro.log("LoroEDI: " +file.getAbsolutePath()+ " doesn't exist");
			
			// intente prefijando directorio de temas:
			String dir_themes = Configuracion.getProperty(Configuracion.DIR)+ "/lib/themes/";
			file = new File(dir_themes, pref_laf);
			if ( !file.exists() )
			{
				Loro.log("LoroEDI: " +file.getAbsolutePath()+ " doesn't exist");
				Loro.log("LoroEDI: " + "No L&F");
				return;
			}
			pref_laf = file.getAbsolutePath();
		}
		
		// Intente usando SkinLF:
		try
		{
			// Primero revise que SkinLF sea accesible:
			Class.forName("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
			
			// ahora cargue la clase de interface:
			// (La carga dinamica permite que esta clase pueda compilarse
			// aun si LookAndFeel_SkinLF no puede.)
			Class clazz = Class.forName("loroedi.laf.LookAndFeel_SkinLF");

			ILookAndFeel laf = (ILookAndFeel) clazz.newInstance(); 
			laf.setLookAndFeel(pref_laf);
			return;
		}
		catch(Exception e)
		{
			Loro.log("LoroEDI: " + "skinlf not found");
		}
	}

	/////////////////////////////////////////////////////////////////////////
	/**
	 * Interface para "aligerar" dependencia en paquete no cr�tico.
	 */
	static interface ILookAndFeel
	{
		/////////////////////////////////////////////////////////////////////
		/**
		 * Pone el LookAndFeel del archivo indicado.
		 */
		public void setLookAndFeel(String pref_laf);
	}
}