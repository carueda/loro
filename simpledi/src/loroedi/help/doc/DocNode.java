package loroedi.help.doc;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.Enumeration;
import java.awt.event.*;
import java.io.File;


////////////////////////////////////////////////////////////////////////////
public class DocNode implements TreeNode
{
	Object name;
	DocNode[] elems = null;
	boolean isLeaf;

	////////////////////////////////////////////////////////////////////////////
	DocNode(Object name)
	{
		this(name, null);
	}

	////////////////////////////////////////////////////////////////////////////
	DocNode(Object name, DocNode[] elems)
	{
		this.name = name;
		this.elems = elems;
		isLeaf = elems == null;
	}

	////////////////////////////////////////////////////////////////////////////
	public Enumeration children()
	{
		if ( isLeaf )
			return null;

		return new Enumeration()
		{
			int index = 0;
			public boolean hasMoreElements()
			{
				return index < elems.length;
			}
			public Object nextElement()
			{
				return elems[index++];
			}
		};
	}

	////////////////////////////////////////////////////////////////////////////
	public boolean getAllowsChildren()
	{
		return !isLeaf;
	}

	////////////////////////////////////////////////////////////////////////////
	public TreeNode getChildAt(int i)
	{
		if ( isLeaf )
			return null;

		return elems[i];
	}

	////////////////////////////////////////////////////////////////////////////
	public int getChildCount()
	{
		return isLeaf ? 0 : elems.length;
	}

	////////////////////////////////////////////////////////////////////////////
	public int getIndex(TreeNode node)
	{
		return 0;
	}

	////////////////////////////////////////////////////////////////////////////
	public TreeNode getParent()
	{
		return null;
	}

	////////////////////////////////////////////////////////////////////////////
	public boolean isLeaf()
	{
		return isLeaf;
	}

	////////////////////////////////////////////////////////////////////////////
	public String toString()
	{
		if ( name instanceof File )
		{
			// drop extension
			String s = ((File) name).getName();
			int i = s.indexOf('.');
			if ( i >= 0 )
			{
				s = s.substring(0, i);
			}
			return s;
		}
		else
		{
			return name.toString();
		}
	}
}
