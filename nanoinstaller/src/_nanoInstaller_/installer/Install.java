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
//	The installer
//
////////////////////////////////////////////////////////////////////////////
package _nanoInstaller_.installer;

import javax.swing.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.*;
import java.net.URL;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;


////////////////////////////////////////////////////////////////////////////
/**
 * The main class for the installer.
 *
 * @author Carlos Rueda
 * @version 2002.03.05
 */
public class Install
{
    ////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args)
    throws Exception
    {
        Info info = new Info();
        System.out.println("[" +info.title+ "]");

        JFrame frame = new JFrame(
            info.getString("pretitle")+ " " +info.title
        );
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        frame.getContentPane().add(new GUI(frame, info), BorderLayout.CENTER);
        frame.pack();
        Dimension s = frame.getSize();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        frame.setLocation((d.width - s.width) / 2, (d.height - s.height) / 2 - 100);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}



////////////////////////////////////////////////////////////////////////////
/**
 * General information to perform the installation.
 */
class Info
{
    ClassLoader cl;
    String title;
    String description;
    String image1_name;
    Icon   image1;
    String directory;

    String initialDirectory;

    String readme;

    boolean is_windows;

    String conf_file;
    String conf_header;
    String conf_dir;

    Set onlyOnUnix;
    Set onlyOnDos;
    Set withtags;

    Vector filelist;

    Properties lang_props;


    ////////////////////////////////////////////////////////////////////////////
    Info()
    throws Exception
    {
        cl = this.getClass().getClassLoader();

        InputStream isp = cl.getResourceAsStream("_nanoInstaller_/properties");
        InputStream isf = cl.getResourceAsStream("_nanoInstaller_/filelist");
        if ( isp == null || isf == null )
        {
            throw new Exception(
"The installer archive has not been properly compiled.\n"+
"Resources _nanoInstaller_/properties and/or _nanoInstaller_/filelist not found.\n"
            );
        }

        InputStream isl = cl.getResourceAsStream("_nanoInstaller_/lang.properties");
        lang_props = getLanguageProperties(isl);

        Properties props = new Properties();
        props.load(isp);

//props.list(System.out);

        title       = props.getProperty("ni.pres.title", "unknown title");
        description = props.getProperty("ni.pres.description", "unknown description");
        image1_name = props.getProperty("ni.pres.image1");

        directory   = props.getProperty("ni.contents.directory", "");
        readme      = props.getProperty("ni.contents.readme");

        conf_file   = props.getProperty("ni.conf.file");
        conf_header = props.getProperty("ni.conf.header");
        conf_dir    = props.getProperty("ni.conf.dir");

        onlyOnUnix = new HashSet();
        addMutilple(props, onlyOnUnix, "ni.contents.onlyOnUnix.");

        onlyOnDos = new HashSet();
        addMutilple(props, onlyOnDos, "ni.contents.onlyOnDos.");

        withtags = new HashSet();
        addMutilple(props, withtags, "ni.contents.withtags.");

        if ( image1_name != null )
            image1 = new ImageIcon(cl.getResource(image1_name));

        String os = System.getProperty("os.name").toLowerCase();
        is_windows = os.indexOf("windows") >= 0;
        if ( is_windows )
        {
            //initialDirectory = "c:" +File.separator;
            initialDirectory = System.getProperty("user.home");

            if ( conf_file != null && conf_file.startsWith(".") )
            {
                conf_file = "_" + conf_file.substring(1);
            }
        }
        else
        {
            initialDirectory = System.getProperty("user.home");
        }

        if ( initialDirectory.endsWith(File.separator)
        ||   directory.startsWith(File.separator) )
            initialDirectory += directory;
        else
            initialDirectory += File.separator + directory;

        filelist = new Vector();

        BufferedReader br = new BufferedReader(
            new InputStreamReader(isf)
        );

        String line;

        while ( (line = br.readLine()) != null )
        {
            if ( line.startsWith("_nanoInstaller_")
            ||   line.startsWith("META-INF") )
            {
                continue;	// ignore
            }

            filelist.addElement(line);
        }
        br.close();
    }

    //////////////////////////////////////////////////////////////////////
    static Properties getLanguageProperties(InputStream isl)
    {
        Properties lang_props = new Properties();
        if ( isl != null )
        {
            try
            {
                lang_props.load(isl);
            }
            catch( Exception ex )
            {
                // ignore (?)
            }
        }
        return lang_props;
    }

    //////////////////////////////////////////////////////////////////////
    static void addMutilple(Properties props, Set set, String prefix)
    {
        for ( int i = 0; true; i++ )
        {
            String val = props.getProperty(prefix +i);
            if ( val == null )
                break;

            set.add(val);
        }
    }

    //////////////////////////////////////////////////////////////////////
    String getString(String key)
    {
        return lang_props.getProperty(key, key);
    }
}

/////////////////////////////////////////////////////////////////////////////
/**
 * The GUI
 */
class GUI extends JPanel implements ActionListener
{
    Info info;
    JTextField dir_tf;
    JButton chooseDir_btn;
    JButton start_btn;
    JButton cancel_btn;
    JFrame frame;


    /////////////////////////////////////////////////////////////////////////////
    public GUI(JFrame frame, Info info)
    {
        super(new BorderLayout(10, 30));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        this.frame = frame;
        this.info = info;

        add(makeTitlePanel(), "West");
        add(makePanel());
        add(makeProgressPanel(), "South");

    }

    ////////////////////////////////////////////////////////////////////////////
    protected JComponent makeTitlePanel()
    {
        GridLayout lay = new GridLayout(3, 1);
        JPanel panel = new JPanel(lay);
        panel.setBorder(BorderFactory.createEtchedBorder());

        panel.add(new JLabel(info.getString("pretitle"), SwingConstants.CENTER));
        if ( info.image1 != null )
        {
            panel.add(new JLabel(info.image1));
        }
        else
        {
            JLabel label = new JLabel(info.title, SwingConstants.CENTER);
            Font font = label.getFont();
            label.setFont(new Font(font.getName(), font.getStyle(), font.getSize() + 2));
            panel.add(label);
        }

        panel.add(new JLabel(info.description, SwingConstants.CENTER));

        return panel;
    }


    ////////////////////////////////////////////////////////////////////////////
    protected JComponent makePanel()
    {
        GridBagLayout gridbag = new GridBagLayout();
        JPanel panel = new JPanel(gridbag);
        panel.setBorder(BorderFactory.createTitledBorder(info.getString("instdir")));
        GridBagConstraints c = new GridBagConstraints();
        JComponent comp;

        c.insets = new Insets(5, 5, 5, 5);

        c.fill = GridBagConstraints.BOTH;
        c.gridy = -1;

        // ROW
        c.gridy++;
        comp = new JLabel(info.getString("prompt.inst.dir1"));
        c.gridx = 0;
        c.gridwidth = 2;
        gridbag.setConstraints(comp, c);
        panel.add(comp);

        // ROW
        c.gridy++;
        comp = new JLabel(info.getString("prompt.inst.dir2"));
        c.gridx = 0;
        c.gridwidth = 2;
        gridbag.setConstraints(comp, c);
        panel.add(comp);

        // ROW
        c.gridy++;
        dir_tf = new JTextField(info.initialDirectory, 30);
        dir_tf.select(0, dir_tf.getText().length());
        comp = dir_tf;
        c.gridx = 0;
        gridbag.setConstraints(comp, c);
        panel.add(comp);

        // ROW
        c.gridy++;
        comp = chooseDir_btn = new JButton(info.getString("choose.inst.dir"));
        ((JButton) comp).setActionCommand("Escoger");
        ((JButton) comp).addActionListener(this);
        c.gridx = 0;
        gridbag.setConstraints(comp, c);
        panel.add(comp);

        return panel;
    }


    ////////////////////////////////////////////////////////////////////////////
    protected JComponent makeProgressPanel()
    {
        FlowLayout lay = new FlowLayout(FlowLayout.RIGHT, 10, 10);
        JPanel panel = new JPanel(lay);
        panel.setBorder(BorderFactory.createTitledBorder(""));
        JComponent comp;

        comp = start_btn = new JButton(info.getString("start"));
        ((JButton) comp).setActionCommand("Instalar");
        ((JButton) comp).addActionListener(this);
        panel.add(comp);

        comp = cancel_btn = new JButton(info.getString("cancel"));
        ((JButton) comp).setActionCommand("Cancelar");
        ((JButton) comp).addActionListener(this);
        panel.add(comp);

        return panel;
    }




    ////////////////////////////////////////////////////////////////////////////
    public void actionPerformed(ActionEvent e)
    {
        String actionCommand = e.getActionCommand();

        if ( actionCommand.equalsIgnoreCase("Escoger") )
        {
            String sel = selectFile(info.getString("choose.dir.title"));
            if ( sel != null )
            {
                dir_tf.setText(sel);
            }
        }
        else if ( actionCommand.equalsIgnoreCase("Instalar") )
        {
            dir_tf.setEditable(false);
            start_btn.setEnabled(false);
            chooseDir_btn.setEnabled(false);
            cancel_btn.setEnabled(false);

            String dir = dir_tf.getText();
            Installer installer = new Installer(frame, info, dir);
            installer.start();
        }
        else if ( actionCommand.equalsIgnoreCase("Cancelar") )
        {
            System.exit(0);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    static public String selectFile(String title)
    {
        String dir = System.getProperty("user.home");

        JFileChooser chooser = new JFileChooser(dir);
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int retval = chooser.showDialog(null, null);
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


}


////////////////////////////////////////////////////////////////////////////
/**
 * The installer thread.
 */
class Installer extends Thread
{
    JFrame frame;
    Info info;
    String directory;


    ////////////////////////////////////////////////////////////////////////////
    Installer(JFrame frame, Info info, String directory)
    {
        this.frame = frame;
        this.info = info;
        this.directory = directory;
    }

    ////////////////////////////////////////////////////////////////////////////
    public void run()
    {
        try
        {
            install();
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(
                frame,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        System.exit(0);
    }


    ////////////////////////////////////////////////////////////////////////////
    void showReadme()
    throws Exception
    {
        if ( info.readme == null )
        {
            JOptionPane.showMessageDialog(
                frame,
                info.getString("finished.title"),
                info.getString("finished.title"),
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        JDialog dial = new JDialog(frame,
            info.getString("finished.title"),
            true
        );
        dial.getContentPane().setLayout(new BorderLayout(20, 10));

        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);

        String urls = "file:" +directory+ "/" +info.readme;
//System.out.println(urls);
        URL url = new URL(urls);
        editorPane.setPage(url);
        editorPane.addHyperlinkListener(new Hyperactive());

        dial.getContentPane().add(new JScrollPane(editorPane));

        JButton ready = new JButton(info.getString("finish"));
        ready.setActionCommand("Exit");
        ready.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        dial.getContentPane().add(ready, "South");

        dial.setSize(600, 400);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        Dimension s = dial.getSize();
        dial.setLocation((d.width - s.width) / 2, (d.height - s.height) / 2 - 80);
        dial.setVisible(true);

    }

    ////////////////////////////////////////////////////////////////////////////
    void install()
    throws Exception
    {
        try
        {
            installFiles(directory);

            String conf_filename = null;

            if ( info.conf_file != null )
            {
                String home = System.getProperty("user.home");

                int fs = info.conf_file.lastIndexOf('/');
                if ( fs >= 0 )
                {
                    // info.conf_file has subdirectory; make directory first:
                    String conf_directory = home+ "/" +info.conf_file.substring(0, fs);
                    new File(conf_directory).mkdirs();
                }
                conf_filename = home + "/" +info.conf_file;

                Properties conf_props = new Properties();

                // check if there is an existent conf file:
                if ( new File(conf_filename).exists() )
                {
                    // keep previous properties:
                    InputStream os = new FileInputStream(conf_filename);
                    conf_props.load(os);
                    os.close();
                }

                conf_props.setProperty(info.conf_dir, directory);

                OutputStream os = new FileOutputStream(conf_filename);
                conf_props.store(os, info.conf_header);
                os.close();
System.err.println("Conf file: " +conf_filename);
            }

            showReadme();
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
            throw ex;
        }

    }

    ////////////////////////////////////////////////////////////////////////////
    void installFiles(String directory)
    throws Exception
    {
        Vector filelist = info.filelist;

        final ProgressMonitor pm = new ProgressMonitor(
            frame,
            info.getString("installing"),
            "",
            0,
            filelist.size() -1
        );

        pm.setMillisToDecideToPopup(0);
        pm.setMillisToPopup(0);

        try{ Thread.sleep(100); }
        catch(Exception e){}

        new File(directory).mkdirs();


        for ( int i = 0; i < filelist.size(); i++ )
        {
            final int k = i;
            final String element = (String) filelist.elementAt(i);

            doUpdate(new Runnable()
            {
                public void run()
                {
                    pm.setProgress(k);
                    pm.setNote(element);
                }
            });

            String line = element;

            if ( info.onlyOnUnix.contains(line) && info.is_windows
            ||   info.onlyOnDos.contains(line) && !info.is_windows )
            {
                    continue;
            }
            boolean processTags = info.withtags.contains(line);

            InputStream in = info.cl.getResourceAsStream(line);

            if ( ! line.startsWith("/") )
                line = "/" + line;


            String dest = directory + line;

            if ( line.endsWith("/") )
            {
                new File(dest).mkdirs();
                continue;
            }

            File parent_file = new File(dest).getParentFile();

            if ( parent_file != null )
            {
                parent_file.mkdirs();
            }

            if ( processTags )
            {
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(in)
                );

                PrintWriter pw = new PrintWriter(
                    new FileOutputStream(dest)
                );

                copyTextFile(br, pw, processTags);
                br.close();
                pw.close();
            }
            else
            {
                BufferedInputStream is = new BufferedInputStream(in);

                BufferedOutputStream os = new BufferedOutputStream(
                    new FileOutputStream(dest)
                );

                copyBinaryFile(is, os);
                is.close();
                os.close();
            }
        }

        pm.close();
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Copy a text file.
     * If processTags is true, it converts tags found.
     */
    void copyTextFile(BufferedReader br, PrintWriter pw, boolean processTags)
    throws Exception
    {
        String line;
        while ( (line = br.readLine()) != null )
        {
            if ( processTags )
            {
                line = replaceTags(line);
            }

            if ( info.is_windows )
            {
                pw.print(line+ "\r\n");
                pw.flush();
            }
            else
            {
                pw.println(line);
            }
        }

    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Copy a binary file.
     */
    void copyBinaryFile(BufferedInputStream is, BufferedOutputStream os)
    throws Exception
    {
            int c;
            while ( (c = is.read()) != -1 )
            {
                os.write(c);
            }
    }


    //////////////////////////////////////////////////////////////////////////
    /**
     * Replaces an inline tag.
     * DIR  -->  Instalation directory
     * xxx  -->  System.getProperty(xxx) provided it's not null
     * xxx  -->  xxx (none of the above)
     */
    String replaceTag(String it)
    {
        if ( it.equals("DIR") )
        {
            return directory;
        }
        String val = System.getProperty(it);
        return val != null ? val : it;
    }

    //////////////////////////////////////////////////////////////////////////
    /**
     * Replaces all ``${...}'' tags.
     */
    String replaceTags(String s)
    {
        StringBuffer sb = new StringBuffer();
        int len = "${".length();
        int i, p = 0;
        while ( (i = s.indexOf("${", p)) >= 0 )
        {
            // keep first part:
            sb.append(s.substring(p, i));

            // find ending "}"
            int e = s.indexOf("}", i + len);
            if ( e < 0 )
            {
                // malformed inline tag!. Leave remaining like came
                p = i;
                break;
            }
            String it = s.substring(i + len, e);
            sb.append(replaceTag(it));
            p = e + 1;
        }
        sb.append(s.substring(p));
        return sb.toString();
    }
    ////////////////////////////////////////////////////////////////////////////
    /**
     * do GUI updates
     * (http://developer.java.sun.com/developer/TechTips/2000/tt0912.html#tip2).
     */
    void doUpdate(Runnable r)
    {
        try
        {
            SwingUtilities.invokeAndWait(r);
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }


    // inner class:
    ////////////////////////////////////////////////////////////////////////////
    /**
     * To attend clicks.
     */
    class Hyperactive implements HyperlinkListener
    {
        public void hyperlinkUpdate(HyperlinkEvent e)
        {
            JEditorPane pane = (JEditorPane) e.getSource();
            String s = e.getDescription();


            if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
                return;

            if ( ! s.startsWith("ni:") )
            {
                setPage(pane, e.getURL());
                return;
            }

            s = s.substring("ni:".length());

            if ( s.startsWith("ifwindows:") )
            {
                URL page = pane.getPage();

                s = s.substring("ifwindows:".length());
                StringTokenizer st = new StringTokenizer(s, ":");
                String win = st.nextToken();
                String lin = st.nextToken();

                String urls;

                if ( info.is_windows )
                {
                    urls = "file:" +directory+ "/" +win;
                }
                else
                {
                    urls = "file:" +directory+ "/" +lin;
                }

                try
                {
                    URL url = new URL(urls);
                    setPage(pane, url);
                }
                catch ( Exception ex )
                {
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////////
        void setPage(JEditorPane pane, URL url)
        {
            try
            {
                pane.setPage(url);
            }
            catch (IOException t)
            {
                //t.printStackTrace();
            }
        }
    } // class Hyperactive

}
