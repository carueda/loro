package loro.misc.ant;

import loro.tools.LoroCompilador;
import loro.Loro;
import loro.LoroException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

////////////////////////////////////////////////////////
/**
 * Task to compile/anticompile Loro source files.
 * <p>
 * This task can take the following arguments:
 * <ul>
 * <li>srcdir (required) Source directory.
 * <li>destdir (optional) Destination directory.
 * <li>anticompile (optional, default=false). 
 * <li>save (optional, default=true).
 * <li>extdir (optional) Extension directory.
 * <li>log (optional, default=(none)").
 *      Value for property "loro.log": The name of file or one of the 
 *      special names "System.out" o "System.err".
 * <li>shownames (optional, default=false").
 * <li>includes (optional)
 * <li>excludes (optional)
 * <li>locale (optional, locale language).
 * </ul>
 *
 * <p>When this task executes, it will recursively scan the srcdir looking for
 *  Loro source files to compile.
 *
 * <p>Note that a selected file is always compiled.
 * Check for displayed messages to fix possible problems.
 *
 * <p>
 * Specify anticompile="true" if you want to "anticompile" the given files.
 * Anticompilation of a file will succeed only if a normal compilation fails, 
 * and vice versa. Use this to test the Loro system against programs known
 * to be invalid.
 * <p>
 * Specify save="false" for testing purposes.
 *
 * @author Carlos Rueda <a href="mailto:carueda@users.sf.net">carueda@users.sf.net</a>
 * @version $Id$
 */
public class LoroCompilerTask extends MatchingTask {
	/** The main Loro class. */
	private static final String loroClassName = "loro.Loro";
	
	/** The source directory. */
	private File srcDir = null;

	/** The destination directory. */
	private File destDir = null;

	/** The extension directory. */
	private File extDir = null;

	/** Make an anti-compile? */
	private boolean anticompile = false;

	/** Save compiled units? */
	private boolean save = true;
	
	/** Argument to "loro.log" property. */
	private String loro_log = null;

	/** Show filenames being compiled? */
	private boolean shownames = false;
	
	private List compileList;


	/** Sets the destination directory to put the compiled files. */
	public void setDestdir(String destDirName) {
		destDir = project.resolveFile(destDirName);
	}

	/** Sets the directory to read extension packages. */
	public void setExtdir(String extDirName) {
		extDir = project.resolveFile(extDirName);
	}

	/** Sets the source dir to find the source Loro files. */
	public void setSrcDir(String srcDirName) {
		srcDir = project.resolveFile(srcDirName);
	}

	/** Sets the anticompile flag. */
	public void setAnticompile(boolean anticompile) {
		this.anticompile = anticompile;
	}

	/** Sets the save flag. */
	public void setSave(boolean save) {
		this.save = save;
	}

	/** Sets the "loro.log" property. */
	public void setLog(String loro_log) {
		this.loro_log = loro_log;
		log("property 'log' set to '" +loro_log+ "'");
	}

	/** Sets the "user.language" system property. */
	public void setLocale(String locale) {
		Loro.setLocale(locale);
	}

	/** Sets the shownames flag. */
	public void setShownames(boolean shownames) {
		this.shownames = shownames;
	}

	/** Scans the source directory looking for source Loro files to be compiled. */
	private void scanDir(File srcDir, String[] files) {
		for (int i = 0; i < files.length; i++) {
			File srcFile = new File(srcDir, files[i]);
			String filename = files[i];
			if (filename.toLowerCase().endsWith(".loro")) {
				compileList.add(srcFile.getAbsolutePath());
			}
		}
	}

	/** Executes the task, i.e. does the actual compiler call */
	public void execute() throws BuildException {
		if (srcDir == null) {
			throw new BuildException("srcdir attribute must be set!");
		}
		if (!srcDir.exists()) {
			throw new BuildException("srcdir does not exist: " +srcDir);
		}
		if (!srcDir.isDirectory()) {
			throw new BuildException("srcdir is not a directory: " +srcDir);
		}
		try {
			Class.forName(loroClassName);
		}
		catch ( Exception ex ) {
			throw new BuildException(loroClassName+ " not found. Check your classpath");
		}

		// scan source dir
		DirectoryScanner ds = getDirectoryScanner(srcDir);
		String[] files = ds.getIncludedFiles();

		compileList = new ArrayList();
		scanDir(srcDir, files);

		int size = compileList.size();
		// compile the source files
		log((anticompile ? "Anticompiling " : "Compiling ") +size+ " source file"
			+ (size == 1 ? "" : "s") + " to " + destDir
		);
		if ( ! save ) {
			log("Not saving units ");
		}
		
		if ( loro_log != null ) {
			System.setProperty("loro.log", loro_log);
		}
		
		if ( size > 0 ) {
			if ( shownames ) {
				log("Files to compile:");
				for ( Iterator it = compileList.iterator(); it.hasNext(); ) {
					String name = (String) it.next();
					log(name+ " ");
				}
			}
			doLoroCompile();
		}
	}

	/** Peforms a compile using the Loro compiler. */
	private void doLoroCompile() throws BuildException {
		log("Using Loro " +Loro.obtVersion());
		int status = 0;
		try {
			if ( anticompile ) {
				status = LoroCompilador.anticompilar(
					compileList,
					destDir.getAbsolutePath(),
					extDir != null ? extDir.getAbsolutePath() : null
				);
			}
			else {
				status = LoroCompilador.compilar(
					compileList,
					destDir.getAbsolutePath(),
					extDir != null ? extDir.getAbsolutePath() : null,
					save
				);
			}
		}
		catch (LoroException ex ) {
			throw new BuildException("Nested exception was: " +ex.getMessage());
		}
		if ( status != 0 ) {
			throw new BuildException("loroc: Compile failed, messages should have been provided.");
		}
	}
}
