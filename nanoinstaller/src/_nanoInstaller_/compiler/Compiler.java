/*
    nanoInstaller - A very simple installer tool in Java
    Copyright (C) 2002  Carlos Rueda - carueda@users.sf.net

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
////////////////////////////////////////////////////////////////////////////
//
//	nanoInstaller - An installer program
//	The nanoInstaller Compiler
//
////////////////////////////////////////////////////////////////////////////
package _nanoInstaller_.compiler;

import java.net.URL;
import java.util.*;
import java.util.zip.*;
import java.io.*;

////////////////////////////////////////////////////////////////////////////
/**
 * The main class for the compiler.
 *
 * @author Carlos Rueda
 * @version 2002.03.05
 */
public class Compiler
{
    /** The version of this program (read from a resource). */
    static String version;

    /** The build of this program (read from a resource). */
    static String build;

    /** A title containing name, version, build. */
    static String title;

    /** Properties file name for the installer. */
    String prop_file;

    /** Language properties file name for the installer. */
    String lang;

    /** Be verbose while compiling? */
    boolean verbose;

    /** Base directory. */
    File basedir;

    /** Executable installer name. */
    String installerName;

    /** To get my resources (compiled installer, language, version, build). */
    ClassLoader cl;


    ////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args)
    {
        try
        {
            new Compiler(args);
        }
        catch ( Exception ex )
        {
            System.err.println(ex.getMessage());
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    Compiler(String[] args)
    throws Exception
    {
        cl = this.getClass().getClassLoader();

        // get general info about this program:
        InputStream isi = cl.getResourceAsStream("_nanoInstaller_/resource/info.properties");
        if ( isi == null )
        {
            throw new Exception(
"Resource info.properties not found.\n" +
"Compiler archive has not been properly compiled."
            );
        }
        Properties info_props = new Properties();
        info_props.load(isi);
        isi.close();
        version = info_props.getProperty("nanoInstaller.version");
        build   = info_props.getProperty("nanoInstaller.build");
        title   = "nanoInstaller Compiler - Version " +version+ " (Build " +build+ ")";


        // now, start processing:
        if ( args.length < 3 )
        {
            if ( args.length == 1 && args[0].equalsIgnoreCase("-L") )
            {
                gpl();
            }
            else
            {
                usage();
            }
            return;
        }

        int arg = 0;
        lang = null;
		verbose = false;

        if ( args[arg].equalsIgnoreCase("-verbose") )
        {
            verbose = true;
            arg++;
        }

        if ( args[arg].equalsIgnoreCase("-lang") )
        {
            lang = args[++arg];
            if ( args.length < 5 )
            {
                usage();
                return;
            }
            arg++;
        }

        prop_file = args[arg++];
        String baseDirectory = args[arg++];
        installerName = args[arg++];

        basedir = new File(baseDirectory);
        if ( !basedir.isDirectory() )
        {
            System.err.println(baseDirectory+ " is not a valid directory");
            return;
        }

        if ( !installerName.endsWith(".jar") )
        {
            System.err.println(installerName+ " doesn't end with .jar");
            return;
        }

        compile();
    }

    ////////////////////////////////////////////////////////////////////////////
    void compile()
    throws Exception
    {
        System.out.println(title);

        InputStream isj = cl.getResourceAsStream("_nanoInstaller_/resource/ni.jar");
        if ( isj == null )
        {
            throw new Exception(
"Resource ni.jar not found.\n" +
"Compiler archive has not been properly compiled."
            );
        }


        BufferedOutputStream bos = new BufferedOutputStream(
            new FileOutputStream(installerName)
        );

        ZipInputStream zis = new ZipInputStream(isj);
        ZipOutputStream zos = new ZipOutputStream(bos);
        zos.setLevel(9);

        copyZipToZip(zis, zos);

        copyFileToZip("_nanoInstaller_/properties", prop_file, zos);


        if ( lang == null )
        {
            lang = "eng";
        }

        // first, try a given lang properties file:
        String filename = lang+ ".lang.properties";
        File file = new File(filename);
        if ( file.exists() )
        {
            copyFileToZip("_nanoInstaller_/lang.properties", lang, zos);
        }
        else
        {
            // now, try a resource:
            String res_name = "_nanoInstaller_/resource/" +filename;
            InputStream is = cl.getResourceAsStream(res_name);
            if ( is == null )
            {
                throw new Exception(
"You have chosen to use the '" +lang+ "' language for messages, but\n"+
"supporting resource " +res_name+ "  was not found.\n"
                );
            }
            copyResourceToZip("_nanoInstaller_/lang.properties", is, zos);
        }

        copyDirectoryToZip(basedir, zos);

        zos.close();
    }

    ////////////////////////////////////////////////////////////////////////////
    void copyDirectoryToZip(File basedir, ZipOutputStream zos)
    throws Exception
    {
        List list = getFilenames(basedir.getAbsolutePath(), true);

        // write filelist:
        ZipEntry entry = new ZipEntry("_nanoInstaller_/filelist");
        zos.putNextEntry(entry);

        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            String filename = (String) it.next();
            byte[] buffer = (filename + "\n").getBytes();
            zos.write(buffer, 0, buffer.length);
        }
        zos.closeEntry();

        // now, add files:
		System.out.println("  Packaging " +list.size()+ " files...");
        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            String filename = (String) it.next();
			if ( verbose )
			{
				System.out.println("  Adding " +filename+ " ...");
			}
            copyFileToZip(
                filename,
                basedir.getAbsolutePath()+ "/" +filename,
                zos
            );
        }


    }

    ////////////////////////////////////////////////////////////////////////////
    void copyZipToZip(ZipInputStream zis, ZipOutputStream zos)
    throws Exception
    {
        ZipEntry entry;
        byte[] buffer = new byte[1];
        while ( (entry = zis.getNextEntry()) != null )
        {
            zos.putNextEntry(new ZipEntry(entry.getName()));
            while ( zis.available() == 1 )
            {
                zis.read(buffer, 0, buffer.length);
                zos.write(buffer, 0, buffer.length);
            }
            zis.closeEntry();
            zos.closeEntry();
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    void copyFileToZip(String entryName, String filename, ZipOutputStream zos)
    throws Exception
    {
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);

        BufferedInputStream bis = new BufferedInputStream(
            new FileInputStream(filename)
        );

        int avail = bis.available();
        byte[] buffer = new byte[avail];
        bis.read(buffer, 0, avail);

        zos.write(buffer, 0, avail);

        zos.closeEntry();
    }

    ////////////////////////////////////////////////////////////////////////////
    void copyResourceToZip(String entryName, InputStream is, ZipOutputStream zos)
    throws Exception
    {
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);

        BufferedInputStream bis = new BufferedInputStream(is);

        int avail = bis.available();
        byte[] buffer = new byte[avail];
        bis.read(buffer, 0, avail);

        zos.write(buffer, 0, avail);

        zos.closeEntry();
    }



    /////////////////////////////////////////////////////////////////////
    /**
     * Gets the list of filenames in a directory.
     *
     * @param directory  The directory
     * @param recurse    Recurse into subdirectories?
     *
     * @return The list of filenames (String's).
     */
    static List getFilenames(String directory, boolean recurse)
    {
        List list = new ArrayList();
        File file = new File(directory);
        int level = recurse ? Integer.MAX_VALUE : 1;
        addFilenames(list, file, "", level);

        return list;
    }

    /////////////////////////////////////////////////////////////////////
    /**
     * Adds filenames to a list.
     *
     * @param list The list.
     * @param file The file to start with.
     * @param level The level of recursion. Must be >= 1
     */
    static void addFilenames(List list, File file, String basename, int level)
    {
        if ( file.isDirectory() )
        {
            if ( level > 0 )
            {
                File[] dir = file.listFiles();
                for ( int i = 0; i < dir.length; i++ )
                {
                    if ( dir[i].isDirectory() )
                    {
                        addFilenames(list, dir[i],
                            basename+ dir[i].getName()+ "/",
                            level - 1   // can be 0 in a recursive call
                        );
                    }
                    else
                    {
                        list.add(basename+ dir[i].getName());
                    }
                }
            }
        }
        else
        {
            list.add(basename+ file.getName());
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    static void usage()
    {
        System.out.println(
"\n"+
title+
"USAGE:\n"+
"  java -jar nic.jar [options] install-props baseDirectory installerName\n"+
"\n"+
"  options:\n"+
"     -verbose    Show file names included in the installer.\n"+
"     -lang code  code is the ISO3 code for the language that is to be used\n"+
"                 to display general messages during installation.\n"+
"                 The default is 'eng' (English).\n"+
"                 Before using an available language definition (included in\n"+
"                 the nanoInstaller), the compiler tries first to use a corresponding\n"+
"                 properties file with the name code.lang.properties in the\n"+
"                 current directory. This way, you can override an available\n"+
"                 definition, or just add a new language.\n"+
"                 Available languages:   eng,  spa\n"+
"\n"+
"  install-props  Properties for the installer\n"+
"  baseDirectory  Directory to include in the installer\n"+
"  installerName  Installer archive name\n"+
"\n"+
"   nanoInstaller - A very simple installer tool in Java\n"+
"   Copyright(c) 2002 Carlos Rueda\n"+
"   nanoInstaller comes with ABSOLUTELY NO WARRANTY;\n"+
"   This is free software, and you are welcome to redistribute it\n"+
"   under certain conditions; Give the -L option for details.\n"
        );
        return;
    }

    ////////////////////////////////////////////////////////////////////////////
    static void gpl()
    {
        System.out.println(
"\n"+
"   nanoInstaller - A very simple installer tool in Java\n"+
"   Copyright(c) 2002 Carlos Rueda\n"+
"\n"+
"   This program is free software; you can redistribute it and/or modify\n"+
"   it under the terms of the GNU General Public License as published by\n"+
"   the Free Software Foundation; either version 2 of the License, or\n"+
"   (at your option) any later version.\n"+
"\n"+
"   This program is distributed in the hope that it will be useful,\n"+
"   but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
"   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
"   GNU General Public License for more details.\n"+
"\n"+
"   You should have received a copy of the GNU General Public License\n"+
"   along with this program; if not, write to the Free Software\n"+
"   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA\n"
        );
    }

}


