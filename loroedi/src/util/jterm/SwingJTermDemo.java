package util.jterm;

import util.jterm.taimp.SwingTextArea;

import java.io.PrintWriter;
import java.io.BufferedReader;
import javax.swing.JFrame;
import javax.swing.JTextArea;

////////////////////////////////////////////////////////////////
/**
 * A JTerm demo on a javax.swing.JTextArea.
 *
 * @author Carlos Rueda
 * @version 0.1 2000-01-13
 */
public class SwingJTermDemo
{
	
	///////////////////////////////////////////////////////////////////////
	public static void main(String[] args)
	throws java.io.IOException
	{
		JTextArea ta = new SwingTextArea();
		JTerm term = new JTerm((ITextArea) ta);

		PrintWriter pw = new PrintWriter(
			term.getWriter()
		);
		BufferedReader br = new BufferedReader(
			term.getReader()
		);

		JFrame f = new JFrame("Swing JTerm Demo");
		f.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		javax.swing.JScrollPane sp = new javax.swing.JScrollPane(ta);
		f.getContentPane().add(sp);
		f.setSize(500, 300);
		f.setVisible(true);
		String s;
		do
		{
			pw.print("Write something ('bye' to finish demo): ");
			s = br.readLine();
			pw.println("You wrote: " +s);
		
		} while ( !s.equals("bye") );

		f.dispose();
	}
}