package util.jterm.process;

import util.jterm.taimp.AWTTextArea;
import util.jterm.*;

import java.io.*;
import javax.swing.*;

////////////////////////////////////////////////////////////////
/**
 * A JTerm to serve as a front end to a process.
 *
 * @author Carlos Rueda
 * @version 0.1 2000-03-13
 */
public class JTermProcess extends JFrame
implements Runnable
{
	JTerm term;
	String processName;
	String cmdToFinish;
	String cmdToClear;
	
	///////////////////////////////////////////////////////////////////////
	public JTermProcess(String process_name, String cmdToFinish, String cmdToClear)
	{
		super("JTerm Front-end to " +process_name);
		this.processName = process_name;
		this.cmdToFinish = cmdToFinish;
		this.cmdToClear = cmdToClear;
		JTextArea ta = new util.jterm.taimp.SwingTextArea();
		term = new JTerm((ITextArea) ta);
		javax.swing.JScrollPane sp = new javax.swing.JScrollPane(ta);
		getContentPane().add(sp);
		setSize(600, 300);
		setVisible(true);
	}
	///////////////////////////////////////////////////////////////////////
	public static void main(String[] args)
	throws Exception
	{
		if ( args.length != 3 )
		{
			System.out.println(
"JTermProcess 0.1 (2001-03-13) - carueda\n"+
"A JTerm front-end to dispatch a process via its stardard I/O streams\n"+
"\n"+
"USAGE:\n"+
"	java util.jterm.process.JTermProcess <process> <exit> <clear>\n"+
"\n"+
" <process> program to run\n"+
" <exit>    command to finish the program (this closes its input stream)\n"+
" <clear>   command to clear the console\n"+
"\n"+
" Note that is not possible to send a Ctrl+C (interrupt) to the process;\n"+
" to interrupt the process, you have to kill it using your operating system,\n"+
" and interrupt this Java program as well.\n"+
"\n"
			);
			return;
		}

		JTermProcess jtp = new JTermProcess(args[0], args[1], args[2]);
		jtp.run();
		System.exit(0);

	}
	///////////////////////////////////////////////////////////////////////
	public void run()
	{
		PrintWriter pw = new PrintWriter(term.getWriter());
		BufferedReader br = new BufferedReader(term.getReader());

		try
		{
			Process proc = Runtime.getRuntime().exec(processName);
			java.io.InputStream is = proc.getInputStream();
			java.io.OutputStream os = proc.getOutputStream();

			PrintWriter pw_proc = new PrintWriter(os);
			
			while ( true )
			{
				int n = is.available();
				if  ( n == 0 )
				{
					Thread.sleep(100);
					continue;
				}

				while ( (n = is.available()) > 0 ) 
				{
					byte[] a = new byte[n];
					is.read(a);
					String s = new String(a);
					pw.print(s);
					pw.flush();
				}
				
				String line;
				again:while ( (line = br.readLine()) != null ) 
				{	
					if (cmdToFinish != null && line.equals(cmdToFinish))
					{
						pw_proc.close();
						return;
					}
					else if (cmdToClear != null && line.equals(cmdToClear))
					{
						term.clear();
						continue again;
					}
					else
					{
						pw_proc.println(line);
						pw_proc.flush();
						break again;
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("An exception occurred: " +e.getClass());
			System.out.println(e.getMessage());
		}

	}
}