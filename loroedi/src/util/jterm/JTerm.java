package util.jterm;

import java.awt.event.*;

import java.awt.Font;
import java.io.Reader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Writer;
import java.io.PrintWriter;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

////////////////////////////////////////////////////////////////
/**
 * A JTerm object interacts with a ITextArea to be used both as a source
 * for reading from and as a target for writing to text data.
 *
 * Once a JTerm object is created, an application will get the writer
 * (method getWriter()), and reader (method getReader()), to write to and
 * read info from the ITextArea.
 *
 * The user can use the arrow keys to navigate prior lines input into the text area.
 * The info displayed before the reading point can be copied but cannot be
 * edited. Other keys dispatched are:
 *		F9 to switch between bold and normal font;
 *		F10 to switch between italics and normal font;
 *		F11 to increase the font size;
 *		F12 to decrease the font size.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class JTerm
{
	/** The object used to synchronize operations on this stream. */
	protected Object lock;

	/** Where info is taken from and displayed to. */
	protected ITextArea ta;
	
	/**
	 * Is this jterm editable? 
	 * If false, ta.setEditable() will not be called. 
	 */
	protected boolean jtermIsEditable;

	/** Has been ENTER typed? */
	protected boolean enter;

	/** Initial part to include when a new reading is about to start. */
	protected String initialStringToRead;

	/** Text entered by the user but not read yet. */
	protected String textNotRead;

	/** Is read() being executed?. */
	protected boolean reading;

	/** Where a read is started in the text area. */
	protected int pos;

	/** History of entered lines. */
	protected List history;

	/** Position navigating the history. */
	protected int posHistory;

	/** Is history being navigated currently? */
	protected boolean inHistory;


	/**
	 * Is a simple (eg. no history) read to be done?
	 * Not yet implemented. This flag is to be updated somehow
	 * by the jshell, i.e., set to false when jshell is about to
	 * read a new command, and set to false to start a command.
	 */
	private boolean simpleRead;


	/** The reader associated with this jterm. */
	private JTermReader jtreader;

	/** The writer associated with this jterm. */
	private JTermWriter jtwriter;

	/** the listener of this jterm. */
	private JTermListener listener;

	/** Prefix. */
	protected String prefix;

	private static Pattern lf_pattern = Pattern.compile("\n");


	////////////////////////////////////////////////////////////////
	/**
	 * Creates a JTerm on a ITextArea.
	 *
	 * @param ta	The text area manipulated.
	 */
	public JTerm(ITextArea ta)
	{
		super();
		lock = this;
		
		System.setProperty("line.separator", "\n");

		prefix = "";
		
		this.ta = ta;
		jtermIsEditable = true;
		
		ta.addKeyListener(new Key());
		ta.setFont(new Font("monospaced", Font.PLAIN, 14));

		history = new ArrayList();
		posHistory = 0;
		inHistory = false;

		reset();

		jtreader = new JTermReader();
		jtwriter = new JTermWriter();

		ta.addMouseListener
		(	new MouseAdapter()
			{
				////////////////////////////////////////////////////
				// Auxiliar method to mouse dispatching.
				void aux()
				{
					if ( jtermIsEditable )
					{
						ITextArea ta_ = JTerm.this.ta;
						int pos_now = ta_.getCaretPosition();
						if ( ta_.isEditable() != pos_now >= pos )
							ta_.setEditable(pos_now >= pos);
					}
				}

				////////////////////////////////////////////////////
				public void mousePressed(MouseEvent e) { aux(); }

				////////////////////////////////////////////////////
				public void mouseReleased(MouseEvent e) { aux(); }
			}
		);

	}

	////////////////////////////////////////////////////////////////
	/**
	 * Sets is this jterm is editable. By default it is.
	 */
	public void setEditable(boolean jtermIsEditable)
	{
		this.jtermIsEditable = jtermIsEditable;
		ta.setEditable(jtermIsEditable);
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 */
	public void addJTermListener(JTermListener listener)
	{
		this.listener = listener;
	}
	
	///////////////////////////////////////////////////
	/**
	 * Clears the text area.
	 * This has effect only if there is not a current reading operation.
	 */
	public void  clear()
	{
		synchronized(lock)
		{
			if  ( jtermIsEditable && !reading )
			{
				ta.setText("");
			}
		}
	}
	
	////////////////////////////////////////////////////////////////
	public void decrementFontSize()
	{
		processIncrementFontSize(-1);
	}
	
	///////////////////////////////////////////////////
	/**
	 * Gets the reader associated with this jterm.
	 */
	public Reader getReader()
	{
		return jtreader;
	}
	
	///////////////////////////////////////////////////
	/**
	 * Gets the writer associated with this jterm.
	 */
	public Writer getWriter()
	{
		return jtwriter;
	}
	
	////////////////////////////////////////////////////////////////
	public void incrementFontSize()
	{
		processIncrementFontSize(1);
	}
	
	////////////////////////////////////////////////////////////////
	private void processBoldFont()
	{
		Font font = ta.getFont();
		int font_size = font.getSize();
		int font_style = font.getStyle();
		if ( (font_style & Font.BOLD) == Font.BOLD )
			font_style &= ~Font.BOLD;
		else
			font_style |= Font.BOLD;
		font = new Font("monospaced", font_style, font_size);
		//font = font.deriveFont(font_style, font_size);
		//	deriveFont is 1.2.2 ?
		ta.setFont(font);
	}
	
	////////////////////////////////////////////////////////////////
	private void processEnter()
	{
		enter = true;

		// Update text
		String t = ta.getText();
		String new_cmd = "";
		if ( pos < t.length() )
			new_cmd = t.substring(pos);

		if ( !simpleRead )
		{
			inHistory = false;

			// Add new_cmd to history as long as it is not empty
			// and not equal to the last memorized:
			if ( new_cmd.length() > 0 )
			{
				if ( history.size() > 0 )
				{
					t = (String) history.get(history.size() -1);
					if ( !new_cmd.equals(t) )
						history.add(new_cmd);
				}
				else
				{
					history.add(new_cmd);
				}
			}
		}

		new_cmd += "\n";
		textNotRead += new_cmd;

		ta_append_lf();

		pos += new_cmd.length();

		// Notify the ENTER
		notifyAll();
	}
	////////////////////////////////////////////////////////////////
	private void processEscape()
	{
		String t = ta.getText();
		if ( pos <= t.length() )
		{
			textNotRead = "";
			ta.replaceRange(textNotRead, pos, t.length());
		}
	}
	
	////////////////////////////////////////////////////////////////
	private void processHistory(boolean up)
	{
		String t = ta.getText();
		if ( pos <= t.length() && history.size() > 0 )
		{
			if ( inHistory )
			{
				if ( up )
				{
					if ( posHistory == 0 )
						return;

					posHistory--;
				}
				else
				{
					if ( posHistory == history.size() -1 )
						return;

					posHistory++;
				}
			}
			else
			{
				inHistory = true;
				posHistory = history.size() -1;
			}

			String texto = (String) history.get(posHistory);
			ta.replaceRange(texto, pos, t.length());
		}
	}
	
	////////////////////////////////////////////////////////////////
	private void processIncrementFontSize(int inc)
	{
		Font font = ta.getFont();
		int font_size = font.getSize();
		int font_style = font.getStyle();
		font_size += inc;
		if ( font_size <= 0 )		// 2001-08-11
		{
			// Ignore
			return;
		}
		Font new_font = new Font("monospaced", font_style, font_size);
		//new_font = font.deriveFont(font_style, font_size);
		//	deriveFont is 1.2.2 ?
		ta.setFont(new_font);
	}
	
	////////////////////////////////////////////////////////////////
	private void processItalicFont()
	{
		Font font = ta.getFont();
		int font_size = font.getSize();
		int font_style = font.getStyle();
		if ( (font_style & Font.ITALIC) == Font.ITALIC )
			font_style &= ~Font.ITALIC;
		else
			font_style |= Font.ITALIC;
		font = new Font("monospaced", font_style, font_size);
		//font = font.deriveFont(font_style, font_size);
		//	deriveFont is 1.2.2 ?
		ta.setFont(font);
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Makes a read as specified in java.io.Reader.
	 * Internally, waits until an ENTER is pressed
	 * (keyPressed notifies about that), and takes textNotRead
	 * to fill in cbuf.
	 */
	public int read(char[] cbuf, int off, int len)
	throws IOException
	{
		synchronized(lock)
		{
			ta.requestFocus();

			if  ( !reading )
			{
				pos = ta.getCaretPosition();
				
				if ( initialStringToRead != null )
				{
					ta.append(initialStringToRead);
					ta.setCaretPosition(ta.getText().length());
					initialStringToRead = null;
				}
			}

			reading = true;

			while ( !enter )
			{
				reading = true;
				if ( listener != null )
					listener.waitingRead(reading);

				try
				{
					wait();
				}
				catch(InterruptedException e)
				{
					reading = false;
					if ( listener != null )
					{
						listener.waitingRead(reading);
					}

					throw new InterruptedIOException(e.getMessage());
				}
			}

			// Control flow comes from keyPressed()'s notifyAll();
			// textNotRead contains text not yet read.

			int texLen = textNotRead.length();
			int num_leidos = Math.min(texLen, len);

			// Fill in cbuf
			textNotRead.getChars(0, num_leidos, cbuf, off);

			// textNotRead may continue being non-empty:
			if ( texLen == num_leidos )
				enter = false;

			// Update textNotRead with suffix not taken:
			textNotRead = textNotRead.substring(num_leidos);

			reading = false;
			if ( listener != null )
				listener.waitingRead(reading);

			return num_leidos;
		}
	}
	
	///////////////////////////////////////////////////
	/**
	 */
	public void  requestFocus()
	{
		ta.requestFocus();
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Resets this manager.
	 */
	public void reset()
	{
		synchronized(lock)
		{
			enter = false;
			reading = false;
			textNotRead = "";
			simpleRead = false;
		}
	}
	
	////////////////////////////////////////////////////////////////
	void setSimpleRead(boolean simpleRead)
	{
		this.simpleRead = simpleRead;
	}
	
	///////////////////////////////////////////////////
	/**
	 * Appends a \n to the text area.
	 */
	private void ta_append_lf()
	{
		ta.append("\n" + prefix);
		String text = ta.getText();
		int len = text.length();
		ta.setCaretPosition(len);
	}
	
	////////////////////////////////////////////////////////////////
	public void toggleBoldFont()
	{
		processBoldFont();
	}
	
	////////////////////////////////////////////////////////////////
	public void toggleItalicFont()
	{
		processItalicFont();
	}
	
	
	///////////////////////////////////////////////////
	/**
	 * Writes a string to the text area.
	 * Called by JTermWriter.write()
	 */
	private void writeString(String s)
	{
		synchronized(lock)
		{
			String[] lines = lf_pattern.split(s, -1);
			boolean append = ! reading
				||   pos >= ta.getText().length() -1
			;
			
			if ( append )
			{
				String text = ta.getText();
				int idx = 1 + text.lastIndexOf('\n');
				String lastLine = text.substring(idx);
				if ( lastLine.startsWith(prefix) )
				{
					idx += prefix.length();
					lastLine = lastLine.substring(prefix.length());
				}
				
				// process first line in affected area:
				lines[0] = process_br(lastLine + lines[0]);

				// process \b,\r for remaining lines:
				for ( int i = 1; i < lines.length; i++ )
					lines[i] = process_br(lines[i]);
				
				// process prefixes and get resulting area:
				s = process_prefix(lines);
				
				// replace after idx:
				ta.replaceRange(s, idx, text.length());
			}
			else
			{
				// Only process prefixes (NO \b,\r processing):
				s = process_prefix(lines);
				ta.insert(s, pos);
			}

			ta.setCaretPosition(ta.getText().length());
			
			pos += s.length();
		}
	}

	///////////////////////////////////////////////////
	/**
	 * Process lines for possible prefixes.
	 */
	private String process_prefix(String[] lines)
	{
		StringBuffer sb = new StringBuffer(lines[0]);
		for ( int i = 1; i < lines.length; i++ )
		{
			lines[i] = prefix + lines[i];
			sb.append("\n" +lines[i]);
		}
		return sb.toString();
	}

	///////////////////////////////////////////////////
	/**
	 * \b and \r processing.
	 * No prefix management at all.
	 */
	private String process_br(String line)
	{
		line = line.substring(1 + line.lastIndexOf('\r'));

		String line2;
		do
		{
			line2 = line;
			line = line2.replaceAll("[^\b]\b", "");
		
		} while ( !line.equals(line2) ) ;
		
		line = line.replaceAll("\b", "");
		
		return line;
	}
	
	///////////////////////////////////////////////////
	/**
	 * Sets the prefix to include in every line output.
	 *
	 * @param prefix The prefix. If != null, this is put after
	 *      each \n in every string written to the JTerm, and also in
	 *      front of the very first string written.
	 *      If null, no prefix inclusion is performed.
	 */
	public void setPrefix(String prefix)
	{
		String old_prefix = this.prefix;
		this.prefix = prefix = prefix != null ? prefix : "";
		
		String text = ta.getText();
		int idx = 1 + text.lastIndexOf('\n');
		String lastLine = text.substring(idx);
		if ( lastLine.equals(old_prefix) )
			//replace last line with new prefix:
			ta.replaceRange(prefix, idx, text.length());
		else
			ta.append("\n" +prefix);
	}

	///////////////////////////////////////////////////
	/**
	 * Sets the initial part of a reading.
	 * With no effect if a reading is in progress.
	 */
	public void setInitialStringToRead(String s)
	{
		synchronized(lock)
		{
			if ( !reading )
				initialStringToRead = s;
		}
	}

	///////////////////////////////////////////////////
	/**
	 * Gets the current prefix associated.
	 */
	public String getPrefix()
	{
		return prefix;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * The reader associated with this jterm.
	 * You have to use JTerm.getReader() to get an instance of this
	 * inner class.
	 */
	public class JTermReader extends Reader
	{

		/** Is this reader closed? */
		private boolean closed;


		////////////////////////////////////////////////////////////////
		/**
		 * The constructor available to JTerm.
		 */
		JTermReader()
		{
			closed = false;
		}

		////////////////////////////////////////////////////////////////
		public void close()
		throws IOException
		{
			closed = true;
		}
		////////////////////////////////////////////////////////////////
		public int read(char[] cbuf, int off, int len)
		throws IOException
		{
			if ( closed )
				throw new IOException("JTermReader closed");

			return JTerm.this.read(cbuf, off, len);
		}
		////////////////////////////////////////////////////////////////
		public void reset()
		{
			JTerm.this.reset();
		}

	}


	///////////////////////////////////////////////////
	/**
	 * The writer associated with this jterm.
	 * You have to use JTerm.getWriter() to get an instance of this
	 * inner class.
	 */
	public class JTermWriter extends Writer
	{
		/** Is this writer closed? */
		private boolean closed;

		////////////////////////////////////////////////////////////////
		/**
		 * The constructor available to JTerm.
		 */
		JTermWriter()
		{
			closed = false;
		}

		///////////////////////////////////////////////////
		public void close()
		throws IOException
		{
			closed = true;
		}
		///////////////////////////////////////////////////
		public void flush()
		throws IOException
		{
		}
		///////////////////////////////////////////////////
		public void write(char[] cbuf, int off, int len)
		throws IOException
		{
			if ( closed )
				throw new IOException("JTermWriter closed");

			JTerm.this.writeString(new String(cbuf, off, len));
		}
	}


	///////////////////////////////////////////////////
	/**
	 * To attend key events.
	 */
	class Key implements KeyListener
	{
		////////////////////////////////////////////////////////////////
		/**
		 * Updates the textNotRead when an ENTER is pressed;
		 * dispatches the history and other keys.
		 */
		public void keyPressed(KeyEvent e)
		{
			synchronized(lock)
			{
				if ( jtermIsEditable )
				{
					if ( !reading )
					{
						ta.setEditable(false);
					}
					else
					{
						int pos_now = ta.getCaretPosition();
						if ( ta.isEditable() != pos_now >= pos )
							ta.setEditable(pos_now >= pos);
					}
				}

				if ( !enter )
				{
					String t;
					Font font;
					int font_size;
					int font_style;

					int key_code = e.getKeyCode();
					int modifiers = e.getModifiers();
					switch ( key_code )
					{
					case KeyEvent.VK_ENTER:
						e.consume();
						if ( !reading )
							break;
						processEnter();
						break;

					case KeyEvent.VK_UP:
					case KeyEvent.VK_DOWN:
						if ( !reading )
							break;
						if ( simpleRead )
						{
							e.consume();
							break;
						}
						if ( e.getModifiers() != 0 )
							break;
						e.consume();
						if ( jtermIsEditable )
							processHistory(key_code == KeyEvent.VK_UP);
						break;

					case KeyEvent.VK_ESCAPE:
						e.consume();
						if ( !reading )
							break;
						if ( jtermIsEditable )
							processEscape();
						break;

					case KeyEvent.VK_HOME:
						if ( simpleRead )
						{
							e.consume();
							break;
						}
						if ( e.getModifiers() != 0 )
							break;
						if ( !reading )
							break;
						e.consume();
						t = ta.getText();
						if ( pos <= t.length() )
						{
							ta.setCaretPosition(pos);
						}
						break;

					case KeyEvent.VK_BACK_SPACE:
						if ( !reading )
							break;
						if ( ta.getCaretPosition() <= pos )
						{
							e.consume();
						}
						break;

					case KeyEvent.VK_F9:
						e.consume();
						processBoldFont();
						break;

					case KeyEvent.VK_F10:
						e.consume();
						processItalicFont();
						break;

					case KeyEvent.VK_F11:
					case KeyEvent.VK_F12:
						e.consume();
						processIncrementFontSize(
							(key_code == KeyEvent.VK_F11) ? 1 : -1
						);
						break;

					case KeyEvent.VK_INSERT:
						if ( (modifiers & KeyEvent.CTRL_MASK) != 0 )
						{
							ta.copy();
						}
						else if ( (modifiers & KeyEvent.SHIFT_MASK) != 0 )
						{
							if ( ta.isEditable() )
								ta.paste();
						}
						else
						{
							break;
						}
						e.consume();
						break;

					case KeyEvent.VK_DELETE:
						if ( (modifiers & ~KeyEvent.VK_SHIFT) != 0 )
						{
							if ( ta.isEditable() )
								ta.cut();

							e.consume();
						}
						break;
					}
				}
			}
		}

		////////////////////////////////////////////////////////////////
		/**
		 * Consumes the event if we are not reading.
		 */
		public void keyTyped(KeyEvent e)
		{
			synchronized(lock)
			{
				if ( !reading )
				{
					e.consume();
					return;
				}
			}
		}

		//////////////////////////////////////////////////////////
		public void keyReleased(KeyEvent e) {}
	}

}