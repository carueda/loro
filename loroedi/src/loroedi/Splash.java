package loroedi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;

/////////////////////////////////////////////////////////////////////
/**
 * Splash Window.
 * Reference: JavaWorld Tip 104.
 * @see http://www.javaworld.com/javaworld/javatips/jw-javatip104.html
 * @author Carlos Rueda
 */
public class Splash extends JWindow
{
	private JTextField status_label;
	
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Writes a status message. If the argument is null, then the
	 * splash window will be closed in at most one second approx.
	 */
	public void status(String status_text)
	{
		if ( status_text != null )
		{
			status_label.setText(status_text);
			return;
		}
		new Thread(getWaitRunner(1000)).start();
	}
	 
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a splash window.
	 */
	public Splash(String text, URL icon_url, Frame f, int waitTime)
	{
		super(f);
		ImageIcon ii = null;
		if ( icon_url != null )
			ii = new ImageIcon(icon_url);
		
		MouseListener ml = new MouseAdapter() 
		{
			public void mousePressed(MouseEvent e) 
			{
				setVisible(false);
				dispose();
			}
		};
		addMouseListener(ml);
		
		JLabel label = new JLabel(ii, JLabel.CENTER);
		getContentPane().add(label, BorderLayout.CENTER);

		JPanel fields = new JPanel(new GridLayout(2, 1)); 
		getContentPane().add(fields, BorderLayout.SOUTH);
		
		JTextField text_label = new JTextField(text);
		text_label.setFont(text_label.getFont().deriveFont(Font.BOLD));
		text_label.setHorizontalAlignment(JTextField.RIGHT);
		text_label.setBackground(Color.yellow);
		text_label.setEditable(false);
		text_label.addMouseListener(ml);
		fields.add(text_label);
		
		status_label = new JTextField("Iniciando...");
		status_label.setFont(status_label.getFont().deriveFont(10f));
		status_label.setHorizontalAlignment(JTextField.LEFT);
		status_label.setBackground(Color.yellow);
		status_label.setEditable(false);
		status_label.addMouseListener(ml);
		fields.add(status_label);

		pack();
		Dimension screenSize =
			Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = label.getPreferredSize();
		int locy = (screenSize.height - labelSize.height)/2 - 150;
		if ( locy < 0 )
		{
			locy = 0;
		}
		setLocation( (screenSize.width - labelSize.width)/2, locy);
		Runnable waitRunner = getWaitRunner(waitTime); 
		setVisible(true);
		new Thread(waitRunner, "SplashThread").start();
	}

	
	////////////////////////////////////////////////////////////////////////
	private Runnable getWaitRunner(final int pause)
	{
		final Runnable closerRunner = new Runnable() 
		{
			public void run() 
			{
				setVisible(false);
				dispose();
			}
		};
		Runnable waitRunner = new Runnable() 
		{
			public void run() 
			{
				try 
				{
					Thread.sleep(pause);
					SwingUtilities.invokeAndWait(closerRunner);
				}
				catch(Exception e) 
				{
				}
			}
		};
		return waitRunner;
	}
	
	////////////////////////////////////////////////////////////////////////
	/**
	 * Display the splash window. This window is closed automatically
	 * after a few seconds, or manually by clicking on it.
	 */
	public static Splash showSplash(JFrame frame)
	{
		String text = Info.obtNombre()+ "  " +Info.obtVersion();
		String filename = "img/splash.jpg";
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL icon_url = cl.getResource(filename);
		int delay = 30 * 1000;
			// este dalay desaparecerá: ahora la inicialización define
			// cuando cerrar el splash. Ver Splash.status.
			// El click sigue siendo la forma en que el usuario la
			// cierra manualmente. Por lo pronto, se pone un delay
			// arbitrariamente prolongado.   [2003-02-12]
		return new Splash(text, icon_url, frame, delay);
	}
}