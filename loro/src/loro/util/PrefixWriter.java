package loro.util;

import java.util.*;
import java.util.regex.Pattern;
import java.io.*;


///////////////////////////////////////////////////////////////
/**
 * A FilterWriter with prefix management.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class PrefixWriter extends FilterWriter
{
	/** current prefix. "" by default.*/
	private String prefix;
	
	/** last line written not including prefix*/
	private String lastLine;
	
	private static Pattern lf_pattern = Pattern.compile("\n");
	private static Pattern lr_pattern = Pattern.compile("\r");
	
	////////////////////////////////////////////////////////
	/**
	 * Creates a FilterWriter with prefix management.
	 */
	public PrefixWriter(Writer out)
	{
		super(out);
		prefix = "";
		lastLine = "";
	}

	///////////////////////////////////////////////////
	/**
	 * Sets the prefix to include in every line output.
	 *
	 * @param prefix The prefix.
	 */
	public void setPrefix(String prefix)
	{
		String old_prefix = this.prefix;
		this.prefix = prefix = prefix != null ? prefix : "";
		
		try
		{
			if ( lastLine.length() == 0 )
			{
				// nothing has been written in current line.
				// replace last line with new prefix if necessary:
				if ( ! old_prefix.equals(prefix) ) 
					super.write("\r" +prefix, 0, 1 + prefix.length());
			}
			else
			{
				// just start a new line:
				super.write("\n" +prefix, 0, 1 + prefix.length());
				lastLine = "";
			}
		}
		catch ( IOException ex )
		{
			// shouldn't happen
			System.err.println("setPrefix exception: " +ex.getMessage());
		}
	}


	///////////////////////////////////////////////////
	public void write(String str, int off, int len)
	throws IOException
	{
		str = str.substring(off, off + len);
		String[] lines = lf_pattern.split(str, -1);
		boolean lastLineReprocessed = false;
		if ( lines[0].indexOf('\r') >= 0 || lines[0].indexOf('\b') >= 0)
		{
			super.write("\r", 0, 1);
			lines[0] = lastLine + lines[0];
			lastLineReprocessed = true;
		}
		for ( int i = 0; i < lines.length; i++ )
		{
			// quite \b iniciales que estropearķan el prefix:
			lines[i] = lines[i].replaceAll("^\b+", "");
			lines[i] = lines[i].replaceAll("\r\b+", "\r");
		}
		
		lastLine = lines[lines.length - 1];
		lastLine = lastLine.substring(1 + lastLine.lastIndexOf('\r'));

		for ( int i = 0; i < lines.length; i++ )
			lines[i] = lines[i].replaceAll("\r", "\r" +prefix);

		if ( lastLineReprocessed )
			lines[0] = prefix + lines[0];
		super.write(lines[0], 0, lines[0].length());
		for ( int i = 1; i < lines.length; i++ )
		{
			lines[i] = "\n" +prefix+ lines[i];
			super.write(lines[i], 0, lines[i].length());
		}
	}
}
