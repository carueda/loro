package loroedi.help;

import loro.Loro;

import javax.swing.event.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


////////////////////////////////////////////////////////////////////////////
/**
 * BrowserPanel.
 *
 * @author Carlos Rueda
 */
public class BrowserPanel extends JPanel
{
	Stack stack_back = new Stack();
	Stack stack_forward = new Stack();

	JEditorPane editorPane;
	JScrollPane scrollPane; // contains editorPane
	Hyperactive listener;
	JFrame frame;
	URL home_url;
	JButton back;
	JButton forward;
	JButton refresh;
	JTextField location;
	IClickListener clickListener;

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea un Panel para navegacion.
	 *
	 * @param home_url
	 * @param refreshListener
	 *       Si no null, se agrega tambien un boton de
	 *       refresco y se asocia este objeto.
	 */
	public BrowserPanel(URL home_url, ActionListener refreshListener)
	{
		super(new BorderLayout());
		this.home_url = home_url;
		this.clickListener = null;

		add(scrollPane = new JScrollPane());
		listener = new Hyperactive();
		createEditorPane();

		JPanel info = new JPanel(new BorderLayout());
		add(info, "North");

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		info.add(buttons, "West");

		back = new JButton(loroedi.Util.getIcon("img/Back24.gif"));
		back.setActionCommand("back");
		back.addActionListener(listener);
		buttons.add(back);

		forward = new JButton(loroedi.Util.getIcon("img/Forward24.gif"));
		forward.setActionCommand("forward");
		forward.addActionListener(listener);
		buttons.add(forward);

		if ( home_url != null )
		{
			JButton home = new JButton(loroedi.Util.getIcon("img/Home24.gif"));
			home.setActionCommand("home");
			home.setToolTipText("Va a la pagina inicial");
			home.addActionListener(listener);
			buttons.add(home);
		}

		if ( refreshListener != null )
		{
			refresh = new JButton(loroedi.Util.getIcon("img/Refresh24.gif"));
			refresh.setActionCommand("refresh");
			refresh.setToolTipText("Actualiza el contenido");
			refresh.addActionListener(refreshListener);
			buttons.add(refresh);
		}

		updateButtons();

		location = new JTextField(home_url != null ? home_url.toString() : "");
		location.addActionListener(listener);
		//info.add(location, "Center");
		add(location, "South");

	}

	////////////////////////////////////////////////////////////////////////////
	public void setClickListener(IClickListener clickListener)
	{
		this.clickListener = clickListener;
	}
	
	////////////////////////////////////////////////////////////////////////////
	public void setLocationEditable(boolean editable)
	{
		location.setEditable(editable);
	}
	
	////////////////////////////////////////////////////////////////////////////
	private void createEditorPane()
	{
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(listener);
		scrollPane.setViewportView(editorPane);

	}
	
	////////////////////////////////////////////////////////////////////////////
	public void setTextHTML(String s)
	{
		editorPane.setContentType("text/html");
		editorPane.setText(s);
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the current displayed URL.
	 */
	public URL getPage()
	{
		return editorPane.getPage();
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Refreshes the current displayed URL, if any.
	 */
	public void refresh()
	{
		URL url = editorPane.getPage();
		if ( url != null )
		{
			createEditorPane();
			try
			{
				editorPane.setPage(url);
			}
			catch (IOException ex)
			{
				javax.swing.JOptionPane.showOptionDialog(
					null,
					"Error al refrescar página\n\n" +ex.getMessage(),
					"Error",
					javax.swing.JOptionPane.DEFAULT_OPTION,
					javax.swing.JOptionPane.ERROR_MESSAGE,
					null,
					null,
					null
				);
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	// Returns true iff Ok.
	public boolean setPage(URL url)
	{
		URL prev = editorPane.getPage();
		if ( !setPageSimple(url) )
		{
			return false;
		}

		if ( prev != null )
		{
			stack_forward.removeAllElements();
			stack_back.push(prev);
			updateButtons();
		}
		editorPane.repaint();

		return true;
	}
	////////////////////////////////////////////////////////////////////////////
	// Returns true iff Ok.
	boolean setPageSimple(URL url)
	{
		if ( url == null )
		{
			return false;
		}

		try
		{
			editorPane.setPage(url);
			location.setText(url.toString());
		}
		catch (IOException ex)
		{
			javax.swing.JOptionPane.showOptionDialog(
				null,
				"Error al cargar página\n\n" +ex.getMessage(),
				"Error",
				javax.swing.JOptionPane.DEFAULT_OPTION,
				javax.swing.JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);
			return false;
		}
		return true;
	}
	////////////////////////////////////////////////////////////////////////////
	//
	public void setHomePage()
	{
		setPageSimple(home_url);
	}


	////////////////////////////////////////////////////////////////////////////
	//
	void updateButtons()
	{
		if ( stack_back.empty() )
		{
			back.setEnabled(false);
			back.setToolTipText("Va a la página anterior visitada");
		}
		else
		{
			back.setEnabled(true);
			URL url = (URL) stack_back.peek();
			back.setToolTipText(url.getFile());
		}
		if ( stack_forward.empty() )
		{
			forward.setEnabled(false);
			forward.setToolTipText("Va a la página siguiente visitada");
		}
		else
		{
			forward.setEnabled(true);
			URL url = (URL) stack_forward.peek();
			forward.setToolTipText(url.getFile());

		}
	}


	// Inner class

	////////////////////////////////////////////////////////////////////////////
	/**
	 * To attend clicks.
	 *
	 * @author Carlos Rueda
	 * @version (8/23/01)
	 */
	class Hyperactive implements HyperlinkListener, ActionListener
	{
		////////////////////////////////////////////////////////////////////////////
		public void hyperlinkUpdate(HyperlinkEvent e)
		{
			HyperlinkEvent.EventType et = e.getEventType();
			if (et == HyperlinkEvent.EventType.ACTIVATED)
			{
				URL url = editorPane.getPage();
	
				URL link = e.getURL();
				if ( clickListener != null
				&&  clickListener.click(link) )
				{
					return;
				}
					
				if ( !isTextPage(link)
				||   !setPageSimple(link) )
				{
					return;
				}
				stack_back.push(url);
				stack_forward.removeAllElements();
				location.setText(link.toString());
				updateButtons();
			}
			else if (et == HyperlinkEvent.EventType.ENTERED)
			{
				URL link = e.getURL();
				location.setText(link.toString());
			}
			else if (et == HyperlinkEvent.EventType.EXITED)
			{
				URL url = editorPane.getPage();
				location.setText(url.toString());
			}
		}

		////////////////////////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			String cmd = e.getActionCommand();
			URL url = null;

			if ( cmd.equals("back") )
			{
				if ( ! stack_back.empty() )
				{
					url = editorPane.getPage();
					stack_forward.push(url);
					url = (URL) stack_back.pop();
				}
				else
				{
					return;
				}
			}
			else if ( cmd.equals("forward") )
			{
				if ( ! stack_forward.empty() )
				{
					url = editorPane.getPage();
					stack_back.push(url);
					url = (URL) stack_forward.pop();
				}
				else
				{
					return;
				}
			}
			else if ( cmd.equals("home") )
			{
				url = editorPane.getPage();
				stack_back.push(url);
				stack_forward.removeAllElements();
				url = home_url;
			}
			else if ( cmd.equals("refresh") && (url = editorPane.getPage()) != null )
			{
				revalidate(); // ???
				//setTextHTML("(Recargando " +url.toString()+ "...)");
			}
			else if ( e.getSource() == location )
			{
				URL loc_url = getTextURL(location.getText());
				if ( loc_url == null )
				{
					return;
				}
				// loc_url Ok.

				url = editorPane.getPage();

				if ( !setPageSimple(loc_url) )
				{
					// problem
					return;
				}

				// abajo en la seccion final se vuelve a hacer setPage
				// pero no importa.

				stack_forward.removeAllElements();
				stack_back.push(url);

				url = loc_url;
			}
			else
			{
				return;
			}

			setPageSimple(url);
			location.setText(url.toString());

			updateButtons();
		}

	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Analizes s to be a valid URL, and to have a
	 * "text/..." content-type.
	 */
	URL getTextURL(String s)
	{
		URL url = null;

		Loro.log("LoroEDI: "+"Intentando visualizar " +s);

		try
		{
			url = new URL(s);

			if ( ! isTextPage(url) )
			{
				url = null;
			}
		}
		catch ( Exception ex )
		{
			javax.swing.JOptionPane.showOptionDialog(
				null,
				"Error con el URL indicado\n\n"+ex.getMessage(),
				"Error",
				javax.swing.JOptionPane.DEFAULT_OPTION,
				javax.swing.JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);
			Loro.log("LoroEDI: "+"  Exception: " +ex.getMessage());
		}

		return url;

	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if a url has "text/..." content-type.
	 */
	boolean isTextPage(URL url)
	{
		try
		{
			String ct = url.openConnection().getContentType().toLowerCase();
			String s = url.toString();

			Loro.log("LoroEDI: "+"  content-type: " +ct);

			if ( ! ct.startsWith("text/")
			||   s.endsWith(".jar")        // <<-- Sourceforge no pone bien para jar!
			||   s.endsWith(".lar") )      // <<-- por si acaso :-)
			{
				javax.swing.JOptionPane.showOptionDialog(
					null,
					s+ "\n"
					+"Este enlace no se puede visualizar en esta ventana.\n"
					+"\n"
					+"Puedes utilizar tu navegador convencional o abrir\n"
					+"este enlace con la aplicación que le corresponda.\n",
					"Mensaje",
					javax.swing.JOptionPane.DEFAULT_OPTION,
					javax.swing.JOptionPane.WARNING_MESSAGE,
					null,
					null,
					null
				);
				Loro.log("LoroEDI: "+"  No visualizable");
				return false;
			}
		}
		catch ( Exception ex )
		{
			Loro.log("LoroEDI: "+"  Exception: " +ex.getMessage());
			return false;
		}

		return true;
	}

	////////////////////////////////////////////////////////////
	public static interface IClickListener
	{
		public boolean click(URL hyperlink);
	}
}