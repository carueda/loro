package loro;

/** Provides metainformation about the programming language. */
public interface ILanguageInfo {
	public String[] getKeywords1();
	public String[] getKeywords2();
	public String[] getKeywords3();
	public String[] getLiterals();
	
	/** Returns the fully qualified name of the automatic package. */
	public String getAutomaticPackageName();
	
	/** Returns the fully qualified name of the root class. */
	public String getRootClassName();
	
	/** Returns the fully qualified name of the root interface. */
	public String getRootInterfaceName();
}

