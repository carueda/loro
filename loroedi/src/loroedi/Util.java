package loroedi;

import javax.swing.*;
import java.io.*;
import java.util.*;

///////////////////////////////////////////////////////////////
/**
 * Algunas utilerias generales.
 *
 * @author Carlos Rueda
 */
public final class Util
{
	////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene un Icon.
	 */
	public static Icon getIcon(String filename)
	{
		Icon icon = null;
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		java.net.URL url = cl.getResource(filename);
		if ( url != null )
		{
			icon = new ImageIcon(url);
		}
		return icon;
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 * Primero intenta con Configuracion.getProperty(key); si no se
	 * resulve intenta con System.getProperty(key, "").
	 */
	public static String getProperty(String key)
	{
		String p = Configuracion.getProperty(key);
		if ( p.length() > 0 )
		{
			return p;
		}
		else
		{
			return System.getProperty(key, "");
		}
	}


	/////////////////////////////////////////////////////////////////////
	/**
	 * Returns a list of files (File's or String's).
	 * If level &lt;= 0 or the given file is not a directory, it just checks 
	 * if list is null to create a new list and return.
	 *
	 * @param file             Directory to search.
	 * @param names            true to add String objects (filenames), 
	 *                         false to add File objects.
	 * @param absolute         true to add absolute (file/string) paths. 
	 * @param inc_dirs         true to include directory elements. 
	 * @param inc_files        true to include normal file elements. 
	 * @param level            The level of recursion. 1 to just list the
	 *                         given directory.
	 * @param list             The list to add elements to. If null,
	 *                         a new list is created.
	 *
	 * @return                 A list of files (File's or String's). This
	 *                         is the same given list if not null. Otherwise
	 *                         is a list created internally.
	 */
	public static List listFiles(
		File file,
		boolean names,
		boolean absolute,
		boolean inc_dirs,
		boolean inc_files,
		int level,
		List list
	)
	{
		if ( list == null )
		{
			list = new ArrayList();
		}
		
		if ( level > 0 && file.isDirectory() )
		{
			String prefix;
			if ( absolute )
			{
				try
				{
					// canonical form, preferably
					prefix = file.getCanonicalPath();
				}
				catch(java.io.IOException ex)
				{
					// oh, absolute will be ok:
					prefix = file.getAbsolutePath();
				}
				
				if ( ! prefix.endsWith(File.separator) )
				{
					prefix += File.separator;
				}
			}
			else
			{
				prefix = "";
			}
			
			File[] dir = file.listFiles();
			if ( dir != null )
			{
				for ( int i = 0; i < dir.length; i++ )
				{
					_listFiles(list, dir[i], names, inc_dirs, inc_files, level, prefix);
				}
			}
		}
		
		return list;
	}


	/////////////////////////////////////////////////////////////////////
	/**
	 * Auxiliary function to listFiles().
	 * Returns a list of files (File's or String's).
	 *
	 * @param list             The list to add elements to. Must be != null.
	 * @param file             File to examine.
	 * @param names            true to add String objects (filenames), 
	 * @param inc_dirs         true to include directory elements. 
	 * @param inc_files        true to include normal file elements. 
	 * @param level            The level of recursion.
	 *                         If level &lt;= 0, it does nothing.
	 *
	 */
	private static void _listFiles(
		List list,
		File file,
		boolean names,
		boolean inc_dirs,
		boolean inc_files,
		int level,
		String prefix
	)
	{
		if ( level <= 0 )
		{
			return;
		}
		
		if ( inc_dirs && file.isDirectory()
		||   inc_files && file.isFile() )
		{
			String name = prefix + file.getName();
			list.add(names ? (Object) name
						   : (Object) new File(name)
			);
		}
		
		if ( level > 1 && file.isDirectory() )
		{
			prefix += file.getName() + File.separator; 
			File[] dir = file.listFiles();
			if ( dir != null )
			{
				for ( int i = 0; i < dir.length; i++ )
				{
					_listFiles(list, dir[i], names, inc_dirs, inc_files, level - 1, prefix);
				}
			}
		}
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Deletes an entire directory; USE CAREFULLY!
	 */
	public static void deleteDirectory(String directory)
	{
		File file = new File(directory);
		if ( file.isDirectory() )
		{
			// recurse
			String[] list = file.list();
			if ( list != null )
			{
				for (int i = 0; i < list.length; i++)
				{
					deleteDirectory(directory+ File.separator +list[i]);
				}
			}
		}

		// and remove this entry:
		file.delete();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Copies a file.
	 *
	 * @param file        Source file.
	 * @param dest_file   Destination file.
	 */
	public static void copyFile(File file, File dest_file)
	throws FileNotFoundException, IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
			new FileInputStream(file))
		);
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
			new FileOutputStream(dest_file))
		);
		byte[] buffer = new byte[1024];
		int read;
		while ( (read = in.read(buffer)) > 0 )
		{
			out.write(buffer, 0, read);
		}
		in.close();
		out.close();
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Copies an entire directory.
	 *
	 * @param dir        Source directory. Must exist.
	 * @param suffix     Obly filenames ending with this suffix will be included.
	 * @param dest_dir   Destination directory. Must exist.
	 * @param keepTime   true to set equal lastModified attribute.
	 */
	public static void copyDirectory(
		File dir,
		String suffix,
		File dest_dir, 
		boolean keepTime
	)
	throws FileNotFoundException, IOException
	{
		if ( !dir.isDirectory() )
		{
			throw new IOException(dir+ ": no es directorio");
		}
		if ( !dest_dir.isDirectory() )
		{
			throw new IOException(dest_dir+ ": no es directorio");
		}
//System.out.println(dir+ " -> " + dest_dir);
		
		File[] files = dir.listFiles();
		if ( files != null )
		{
			for (int i = 0; i < files.length; i++)
			{
				if ( files[i].isFile()  &&  files[i].getName().endsWith(suffix) )
				{
					File toFile = new File(dest_dir, files[i].getName());
					copyFile(files[i], toFile);
					if ( keepTime )
					{
						toFile.setLastModified(files[i].lastModified());
					}
//System.out.println(files[i]+ " -> " + toFile);
				}
				else if ( files[i].isDirectory() )
				{
					File subdir = new File(dest_dir, files[i].getName());
					subdir.mkdirs();
					if ( keepTime )
					{
						subdir.setLastModified(files[i].lastModified());
					}
					copyDirectory(files[i], suffix, subdir, keepTime);
				}
			}
		}
	}
//	public static void main(String[] args)
//	throws Exception
//	{
//		File dir = new File(args[0]);
//		File dest_dir = new File(args[1]);
//		copyDirectory(dir, dest_dir);
//	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Reads an entire text file.
	 *
	 * @param file        Text file.
	 */
	public static String readFile(File file)
	throws FileNotFoundException, IOException
	{
		BufferedReader br = new BufferedReader(
			new InputStreamReader(new FileInputStream(file))
		);
		StringBuffer sb = new StringBuffer();
		String line;
		while ( (line = br.readLine()) != null )
		{
			sb.append(line+ "\n");
		}
		br.close();
		return sb.toString();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Writes an entire text file.
	 *
	 * @param file        Text file.
	 */
	public static void writeFile(File file, String text)
	throws IOException
	{
		PrintWriter s = new PrintWriter(
			new OutputStreamWriter(new FileOutputStream(file))
		);
		s.print(text);
		s.close();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Reads the properties from a file.
	 *
	 * @param file   The properties file.
	 * @param props  Initial properties. If not null, the properties
	 *               are loaded in this argument and returned. In null,
	 *               a new Properties object is created.
	 */
	public static Properties loadProperties(File file, Properties props)
	throws FileNotFoundException, IOException
	{
		if ( props == null )
		{
			props = new Properties();
		}
		FileInputStream s = new FileInputStream(file);
		props.load(s);
		s.close();
		return props;
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Stores the properties in a file.
	 *
	 * @param header The header.
	 * @param props  The properties.
	 * @param file   The properties file.
	 */
	public static void storeProperties(
		String header, Properties props, File file
	)
	throws FileNotFoundException, IOException
	{
		FileOutputStream s = new FileOutputStream(file);
		props.store(s, header);
		s.close();
	}

	////////////////////////////////////////////////////////////////////////////
	private static String currentDirectoryPath = null;

	////////////////////////////////////////////////////////////////////////////
	private static void _managePreference()
	{
		if ( currentDirectoryPath == null )
		{
			currentDirectoryPath = Preferencias.obtPreferencia(Preferencias.PRS_DIR);
		}
	}


	////////////////////////////////////////////////////////////////////////////
	static public String selectDirectory(JFrame frame, String title)
	{
		_managePreference();

		JFileChooser chooser = new JFileChooser(currentDirectoryPath);
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int retval = chooser.showDialog(frame, null);
		if(retval == JFileChooser.APPROVE_OPTION)
		{
			File theFile = chooser.getSelectedFile();
			if ( theFile != null )
			{
				currentDirectoryPath = theFile.getParent();
				String sel = chooser.getSelectedFile().getAbsolutePath();
				return sel;
			}
		}

		return null;
	}

	////////////////////////////////////////////////////////////////////////////
	static public String selectSaveFile(JFrame frame, String title, int mode)
	{
		_managePreference();

		JFileChooser chooser = new JFileChooser(currentDirectoryPath);
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(mode);

		int retval = chooser.showSaveDialog(frame);
		if(retval == JFileChooser.APPROVE_OPTION)
		{
			File theFile = chooser.getSelectedFile();
			if ( theFile != null )
			{
				String sel = chooser.getSelectedFile().getAbsolutePath();
				return sel;
			}
		}

		return null;
	}
	
	////////////////////////////////////////////////////////////////////////////
	static public String selectFile(JFrame frame, String title, int mode)
	{
		_managePreference();

		JFileChooser chooser = new JFileChooser(currentDirectoryPath);
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(mode);

		int retval = chooser.showDialog(frame, null);
		if(retval == JFileChooser.APPROVE_OPTION)
		{
			File theFile = chooser.getSelectedFile();
			if ( theFile != null )
			{
				currentDirectoryPath = theFile.getParent();
				String sel = chooser.getSelectedFile().getAbsolutePath();
				return sel;
			}
		}

		return null;
	}
}