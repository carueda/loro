package loro.util;

import java.util.*;
import java.io.*;


///////////////////////////////////////////////////////////////
/**
 * A FilterWriter with prefix management.
 */
public class PrefixWriter extends FilterWriter
{
	/** current prefix. null for no prefix management. */
	private String prefix;
	
	/** last char was line feed? (regardless of prefix) */
	private boolean ln;
	
	////////////////////////////////////////////////////////
	/**
	 */
	public PrefixWriter(Writer out)
	{
		super(out);
		prefix = null;
		ln = false;
	}

	///////////////////////////////////////////////////
	/**
	 * Sets the prefix to include in every line output.
	 *
	 * @param prefix The prefix. this is put after each '\n' except if it's
	 *      the last in every string written with write(String str, int off, int len).
	 *      If null, no prefix inclusion is performed.
	 */
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}


	///////////////////////////////////////////////////
	public void write(String str, int off, int len)
	throws IOException
	{
		int last = off + len -1;
		boolean new_ln = 0 <= last && last < str.length() && str.charAt(last) == '\n';
		
		if ( prefix == null )
		{
			super.write(str, off, len);
		}
		else
		{
			if ( ln )
				super.write(prefix, 0, prefix.length());
				
			if ( new_ln )
				len--;
			
			if ( len > 0 )
			{
				str = str.substring(off, len);
				str = Util.replace(str, "\n", "\n" +prefix);
			}
			
			if ( new_ln )
				str += "\n";	
			super.write(str, 0, str.length());
		}
		ln = new_ln;
	}
	
	
}
