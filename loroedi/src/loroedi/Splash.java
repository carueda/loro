package loroedi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/////////////////////////////////////////////////////////////////////
/**
 * Splash Window.
 * Reference: JavaWorld Tip 104.
 * @see http://www.javaworld.com/javaworld/javatips/jw-javatip104.html
 * @author Carlos Rueda
 */
public class Splash extends JWindow
{
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a splash window.
	 */
	public Splash(String text, ImageIcon ii, Frame f, int waitTime)
	{
		super(f);
		
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
		
		JTextField text_label = new JTextField(text);
		//text_label.setFont(text_label.getFont().deriveFont(Font.ITALIC));
		text_label.setFont(text_label.getFont().deriveFont(Font.BOLD));
		text_label.setHorizontalAlignment(JTextField.RIGHT);
		text_label.setBackground(Color.yellow);
		text_label.setEditable(false);
		text_label.addMouseListener(ml);
		getContentPane().add(text_label, BorderLayout.SOUTH);
		
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

		final int pause = waitTime;
		final Runnable closerRunner = new Runnable() {
			public void run() {
				setVisible(false);
				dispose();
			}
		};
		Runnable waitRunner = new Runnable() {
			public void run() {
				try {
					Thread.sleep(pause);
					SwingUtilities.invokeAndWait(closerRunner);
				}
				catch(Exception e) {
					//e.printStackTrace();
					// can catch InvocationTargetException
					// can catch InterruptedException
				}
			}
		};
		setVisible(true);
		Thread splashThread = new Thread(waitRunner, "SplashThread");
		splashThread.start();
	}

	////////////////////////////////////////////////////////////////////////
	/**
	 * Display the splash window. This window is closed automatically
	 * after a few seconds, or manually by clicking on it.
	 */
	public static void showSplash(JFrame frame)
	{
		String text = Info.obtNombre()+ "  " +Info.obtVersion();
		String filename = "img/splash.gif";
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		java.net.URL url = cl.getResource(filename);
		if ( url != null )
		{
			javax.swing.ImageIcon ii = new javax.swing.ImageIcon(url);
			int delay = 5 * 1000;
			new Splash(text, ii, frame, delay);
		}
	}
}