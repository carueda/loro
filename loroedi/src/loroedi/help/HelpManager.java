package loroedi.help;

import loroedi.Info.Str;
import loroedi.Preferencias;
import loroedi.Configuracion;
import loro.Loro;

import javax.swing.Icon;
import javax.swing.event.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.*;


////////////////////////////////////////////////////////////////////////////
/**
 * HelpManager.
 *
 * @author Carlos Rueda
 * @version (8/23/01)
 */
public class HelpManager {
	static HelpManager hm = null;

	////////////////////////////////////////////////////////////////////////////
	public static void displayHelp() {
		try {
			if ( hm == null ) {
				hm = new HelpManager();
			}

			hm.display();
			hm.setHomePage();
		}
		catch ( Exception ex ) {
			Loro.log("LoroEDI: "+"displayHelp: " +ex.getMessage());
		}
	}


	//
	// Instance:
	//

	BrowserPanel bp;
	JFrame frame;
	URL home_url;

	////////////////////////////////////////////////////////////////////////////
	protected HelpManager() throws Exception {
		home_url = null;
		String dir = Configuracion.getProperty(Configuracion.DIR);
		String page = "file:" +dir+ "/doc/index.html";
		home_url = new URL(page);

		frame = new JFrame(Str.get("gui.help_title"));
		URL url = getClass().getClassLoader().getResource("img/icon.jpg");
		if ( url != null ) 
			frame.setIconImage(new ImageIcon(url).getImage());
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
			}
		});

		bp = new BrowserPanel(home_url, null);
		frame.getContentPane().add(bp);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// Manejo preferencia rectangulo sobre la pantalla:
		Rectangle rect = Preferencias.obtRectangulo(Preferencias.HELP_RECT);
		frame.setLocation(rect.x, rect.y);
		frame.setSize(rect.width, rect.height);
		frame.addComponentListener(new ComponentAdapter() {
			void common() {
				Rectangle rect_ = new Rectangle(frame.getLocationOnScreen(), frame.getSize());
				Preferencias.ponRectangulo(Preferencias.HELP_RECT, rect_);
			}
			public void componentResized(ComponentEvent e){common();}
			public void componentMoved(ComponentEvent e){common();}
		});

	}

	////////////////////////////////////////////////////////////////////////////
	void display() {
		if ( !frame.isShowing() ) {
			frame.setVisible(true);
		}
		frame.toFront();
	}

	////////////////////////////////////////////////////////////////////////////
	//
	void setHomePage() {
		bp.setHomePage();
	}
}
