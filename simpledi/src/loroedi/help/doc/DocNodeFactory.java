package loroedi.help.doc;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.Enumeration;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

////////////////////////////////////////////////////////////////////////////
public class DocNodeFactory
{
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a JTree for a base doc directory.
	 */
	public static JTree getJTree(String directory, String extension)
	{
		JTree tree = new JTree(createDocNode(directory, extension));

		tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
		tree.putClientProperty("JTree.lineStyle", "Angled");

		return tree;

	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the DocNode for a tree.
	 * This helps update a tree.
	 */
	public static void setDocNode(JTree jtree, DocNode dn)
	{
		jtree.setModel(new DefaultTreeModel(dn));
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates the DocNode for a base doc directory.
	 */
	public static DocNode createDocNode(String directory, String extension)
	{
		File baseDir = new File(directory);
		List /*File*/ dirs = new ArrayList();
		searchDirs(baseDir, dirs);
		List elems = new ArrayList();
		for ( int i = 0; i < dirs.size(); i++ )
		{
			File dir = (File) dirs.get(i);
			DocNode n = createDocNodeOnDirectory(dir, baseDir, extension);
			if ( n != null )
			{
				elems.add(n);
			}
		}
		DocNode[] array = (DocNode[]) elems.toArray(EMPTY_DOCNODE_ARRAY);
		return new DocNode("root", array);
	}



	////////////////////////////////////////////////////////////////////////////
	/**
	 * Process a given directory: that is, creates a doctree with the branches:
	 * <ul>
	 *	<li>interfaces
	 *	<li>clases
	 *	<li>objetos
	 * 	<li>especificaciones 
	 *	<li>algoritmos
	 * </ul>
	 */
	static DocNode createDocNodeOnDirectory(File dir, File baseDir, String extension)
	{
		File[] files = dir.listFiles();
		List elems = new ArrayList();

		String[] exts =
		{
			// title             // pre-extension
			"interfaces",        ".i",
			"objetos",           ".o",
			"clases",            ".c",
			"especificaciones",  ".e",
			"algoritmos",        ".a",
		};
		for ( int i = 0; i < exts.length; i += 2 )
		{
			DocNode[] list = getElements(files, exts[i + 1], extension);
			if ( list != null )
			{
				elems.add(new DocNode(exts[i], list));
			}
		}

		// arme el nombre del paquete:
		String package_name;
		if ( dir.equals(baseDir) )
		{
			package_name = "(paquete anónimo)";
		}
		else
		{
			// a dir se le ignora el baseDir y se remplaza File.separator por "::"
			String b = baseDir.getAbsolutePath();
			String s = dir.getAbsolutePath().substring(b.length());
			if ( s.startsWith(File.separator) )
			{
				s = s.substring(1);
			}
			package_name = replace(s, File.separator, "::");
		}

		if ( elems.size() > 0 )
		{
			DocNode[] array = (DocNode[]) elems.toArray(EMPTY_DOCNODE_ARRAY);
			return new DocNode(package_name, array);
		}
		else
		{
			// no cree nodo para paquete vacio:
			return null;
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the list of leaves with the given filename extension.
	 */
	static DocNode[] getElements(File[] files, String pre_extension, String extension)
	{
		List elems = new ArrayList();

		for ( int i = 0; i < files.length; i++ )
		{
			String name = files[i].getName();
			if ( files[i].isDirectory() || ! name.endsWith(pre_extension+extension) )
			{
				continue;
			}
			name = name.substring(0, name.length() - extension.length());
			elems.add(new DocNode(name));
			//elems.add(new DocNode(files[i]));
		}
		if ( elems.size() == 0 )
		{
			return null;
		}
		else
		{
			DocNode[] array = (DocNode[]) elems.toArray(EMPTY_DOCNODE_ARRAY);
			return array;
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a "direct" tree associated to the given file. NOT USED.  WAS THE FIRST TEST.
	 */
	static DocNode createDirectTree(File file)
	{
		String name = file.getName();
		if ( ! file.exists() )
		{
			return new DocNode(name+ "(doesn't exist!)");
		}
		else if ( file.isDirectory() )
		{
			File[] files = file.listFiles();
			DocNode[] elems = new DocNode[files.length];
			for ( int i = 0; i < files.length; i++ )
			{
				elems[i] = createDirectTree(files[i]);
			}
			return new DocNode(file, elems);
		}
		else
		{
			return new DocNode(file);
		}
	}

	/** Para toArray */
	private static final DocNode[] EMPTY_DOCNODE_ARRAY = new DocNode[0];

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds all directories to dirs starting at the given file.
	 */
	static void searchDirs(File file, List dirs)
	{
		if ( ! file.isDirectory() )
		{
			return;
		}

		dirs.add(file);

		File[] files = file.listFiles();
		for ( int i = 0; i < files.length; i++ )
		{
			searchDirs(files[i], dirs);
		}
	}
	
	
	///////////////////////////////////////////////////////////
	/**
	 * Replace in 's' all occurrences of 'from' to 'to'.
	 */
	private  static String replace(String s, String from, String to)
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
