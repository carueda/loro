package loroedi.gui;

/**
 * Main entry point to integrated environment
 * @author Carlos Rueda
 */
public class Main {
	public static void main(String[] args) {
		String prs_directory = null; // para mirar preferencia
		if ( args.length == 2 && args[0].equals("-prsdir") ) {
			prs_directory = args[1];
		}
		else if ( args.length > 0 ) {
			System.out.println("\n" + "Main [-prsdir <prs-directory>]" + "\n");
			return;
		}
		GUI.init(prs_directory);
	}
}

