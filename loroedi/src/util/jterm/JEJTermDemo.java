package util.jterm;


import util.jterm.taimp.JETextArea;

import java.io.PrintWriter;
import java.io.BufferedReader;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import loroedi.jedit.JEditTextArea;

////////////////////////////////////////////////////////////////
/**
 * A JTerm demo on a JEdit.
 *
 * @author Carlos Rueda
 * @version 2000-09-25
 */
public class JEJTermDemo
{

	///////////////////////////////////////////////////////////////////////
	public static void main(String[] args)
	throws java.io.IOException
	{
		String PROMPT = "Write something ('bye' to finish demo): ";
		JEditTextArea ta = JETextArea.createJEditTextArea(
			PROMPT,
			"  ",	//prefix_special,
			"!!",	//prefix_invalid,
			false	//paintInvalid ~
		);
		JTerm term = new JTerm((ITextArea) ta);

		PrintWriter pw = new PrintWriter(
			term.getWriter()
		);
		BufferedReader br = new BufferedReader(
			term.getReader()
		);

		JFrame f = new JFrame("JEdit JTerm Demo");
		f.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		javax.swing.JScrollPane sp = new javax.swing.JScrollPane(ta);
		f.getContentPane().add(sp);
		f.setSize(500, 300);
		f.setVisible(true);
		String s;
		do
		{
			pw.print(PROMPT);
			s = br.readLine();
			pw.println("You wrote: " +s);

		} while ( !s.equals("bye") );

		f.dispose();
	}
}