package loroedi.laf;

import loro.Loro;

import javax.swing.UIManager;

import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;


/////////////////////////////////////////////////////////////////////
/**
 * LookAndFeel_SkinLF - L&F basado en SkinLF - http://www.L2FProd.com.
 *
 * @author Carlos Rueda
 * @version 2002-02-27
 */
class LookAndFeel_SkinLF implements LookAndFeel.ILookAndFeel
{
	/////////////////////////////////////////////////////////////////////
	/**
	 * Pone el LookAndFeel del archivo indicado.
	 */
	public void setLookAndFeel(String path)
	{
		try
		{
			Loro.log("LoroEDI: "+"Using skinlf library version " + SkinLookAndFeel.VERSION);
			Loro.log("LoroEDI: "+"L&F: " +path);
			Skin theme = SkinLookAndFeel.loadThemePack(path);
			SkinLookAndFeel.setSkin(theme);
			UIManager.setLookAndFeel(new SkinLookAndFeel());
		}
		catch(Exception e)
		{
			Loro.log("LoroEDI: "+"error to set L&F: " +e.getMessage());
		}
	}
}
