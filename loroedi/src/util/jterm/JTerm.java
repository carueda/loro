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

////////////////////////////////////////////////////////////////
/**
 * A JTerm object interacts with a ITextArea that is to be used as a source
 * for reading and as a target for writing text info.
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
 * @version 0.1 1999-04-15
 * @version 0.2 2000-01-10
 * @version 0.3 2000-01-17
 * @version 0.4 2001-02-23
 * @version 0.5 2001-10-09 - Prefix handling.
 * @version 0.6 2002-10-01 - Handling of given initial part for reading.
 */
public class JTerm
{
	/** The object used to synchronize operations on this stream. */
	protected Object lock;

	/** Where info is taken from and displayed to. */
	ITextArea ta;

	/** Has been ENTER typed? */
	boolean enter;

	/** Initial part to include when a new reading is about to start. */
	String initialStringToRead;

	/** Text entered by the user but not read yet. */
	String textNotRead;

	/** Is read() being executed?. */
	boolean reading;

	/** Where a read is started in the text area. */
	int pos;

	/** History of entered lines. */
	List history;

	/** Position navigating the history. */
	int posHistory;

	/** Is history being navigated currently? */
	boolean inHistory;


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
	String prefix;

	/** First use of prefix after setted with setPrefix? */
	boolean prefixToFirstUse;

	/** Something written after setted with setPrefix? */
	boolean somethingWritten;


	////////////////////////////////////////////////////////////////
	/**
	 * The reader associated with this jterm.
	 * You have to use JTerm.getReader() to get an instance of this
	 * inner class.
	 *
	 * @author Carlos Rueda
	 * @version 0.1 1999-04-15
	 * @version 0.2 2000-01-10
	 * @version 0.3 2000-01-17
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
	 *
	 *
	 * @author Carlos Rueda
	 * @version 0.1 1999-04-15
	 * @version 0.2 2000-01-10
	 * @version 0.3 2000-01-17
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
	class Key
	implements KeyListener
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
						processHistory(key_code == KeyEvent.VK_UP);
						break;

					case KeyEvent.VK_ESCAPE:
						e.consume();
						if ( !reading )
							break;
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

		this.ta = ta;

		ta.addKeyListener(new Key());
		ta.setEditable(true);
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
					ITextArea ta_ = JTerm.this.ta;
					int pos_now = ta_.getCaretPosition();
					if ( ta_.isEditable() != pos_now >= pos )
						ta_.setEditable(pos_now >= pos);
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
			if  ( !reading )
				ta.setText("");
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

		ta_append("\n");

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
				if ( prefix != null && prefixToFirstUse )
				{
					prefixToFirstUse = false;
					ta.append(prefix);
					ta.setCaretPosition(ta.getText().length());
				}
				
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
	 * This auxiliary function appends a string to the text area,
	 * and sets the caret position at the end of the text.
	 * Note: An append suffices with a java.awt.textArea; but
	 * with a javax.swing.JTextArea an explicit setCaretPosition
	 * is also necessary.
	 */
	private void ta_append(String s)
	{
		s = process_prefix(s);
		ta.append(s);
		ta.setCaretPosition(ta.getText().length());
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
	 */
	public void writeString(String s)
	{
		synchronized(lock)
		{
			if ( ! reading
			||   pos >= ta.getText().length() -1 )
			{
				ta_append(s);
			}
			else
			{
				s = process_prefix(s);
				ta.insert(s, pos);
				ta.setCaretPosition(ta.getText().length());
			}
			
			pos += s.length();
		}
	}

	///////////////////////////////////////////////////
	/**
	 * Process a string for possible prefixes.
	 */
	final String process_prefix(String s)
	{
		if ( prefix != null )
		{
			s = (prefixToFirstUse ? prefix : "")
				+replace(s, "\n", "\n" +prefix)
			;
			prefixToFirstUse = false;
		}
		somethingWritten = true;
		return s;
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
		this.prefix = prefix;
		prefixToFirstUse = prefix != null;
		somethingWritten = false;
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

	///////////////////////////////////////////////////
	/**
	 * Says if the prefix was used since the method setPrefix(String prefix)
	 * was called with prefix != null.
	 */
	public boolean somethingWritten()
	{
		return somethingWritten;
	}

	///////////////////////////////////////////////////////////
	/**
	 * Replace in 's' all occurrences of 'from' to 'to'.
	 */
	private static String replace(String s, String from, String to)
	{
		StringBuffer sb = new StringBuffer();
		int len = from.length();
		int i, p = 0;
		while ( (i = s.indexOf(from, p)) >= 0 )
		{
			sb.append(s.substring(p, i) + to);
			p = i + len;
		}
		sb.append(s.substring(p));
		return sb.toString();
	}
}