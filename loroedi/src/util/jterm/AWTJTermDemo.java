package util.jterm;

import util.jterm.taimp.AWTTextArea;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.awt.Frame;
import java.awt.TextArea;

////////////////////////////////////////////////////////////////
/**
 * A JTerm demo on a java.awt.TextArea.
 *
 * @author Carlos Rueda
 * @version 0.1 2000-01-13
 */
public class AWTJTermDemo
{
	
	///////////////////////////////////////////////////////////////////////
	public static void main(String[] args)
	throws java.io.IOException
	{
		TextArea ta = new AWTTextArea();
		JTerm term = new JTerm((ITextArea) ta);

		PrintWriter pw = new PrintWriter(
			term.getWriter()
		);
		BufferedReader br = new BufferedReader(
			term.getReader()
		);

		Frame f = new Frame("AWT JTerm Demo");
		f.add(ta);
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
		System.exit(0);
	}
}