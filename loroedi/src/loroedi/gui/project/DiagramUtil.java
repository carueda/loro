package loroedi.gui.project;

import loroedi.gui.project.model.*;
import loroedi.gui.project.unit.*;

import com.jgraph.JGraph;
import com.jgraph.graph.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;


/////////////////////////////////////////////////////////
/** ~!~
 * @author Carlos Rueda
 */
public final class DiagramUtil
{
	private static FontRenderContext frc = new FontRenderContext(null, true, false);
	
	/////////////////////////////////////////////////////////////////////
	static Dimension getSize(
		String stereo, Font stereo_font, 
		String text, Font text_font
	)
	{
		double width = getBoundsWidth(stereo,stereo_font,text,text_font);
		double height = getBoundsHeight(stereo_font,text_font);
		return new Dimension((int) width, (int) height);
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la altura total para un cuadro
	 */
	static double getBoundsHeight(Font stereo_font, Font text_font)
	{
		double stereo_height = new TextLayout("H", stereo_font, frc)
			.getBounds().getHeight()
		;
		double text_height = new TextLayout("H", text_font, frc)
			.getBounds().getHeight()
		;
		double height = 4 + stereo_height + 8 + text_height + 2;
		
		return height;
	}

	/////////////////////////////////////////////////////////////////////
	static double getWidth(String str, Font font) {
		return new TextLayout(str, font, frc).getBounds().getWidth();
	}
	
	/////////////////////////////////////////////////////////////////////
	static double getBoundsWidth(
		String stereo, Font stereo_font, 
		String text, Font text_font
	)
	{
		double stereo_width = getWidth(stereo, stereo_font);
		double text_width   = getWidth(text, text_font);
		
		return 6 + Math.max(stereo_width + 2, text_width) + 6; 
	}
	
	/////////////////////////////////////////////////////////////////////
	static String getStereo(Object obj) {
		if ( obj instanceof IProjectUnit ) {
			return ((IProjectUnit) obj).getStereotype();
		}
		else {
			return "??????";
		}
	}
}

