package util.jterm;

///////////////////////////////////////////////////////////////
/**
 * A JTermReader notifies a JTermReaderListener
 * when the method read(char[], int, int) is called.
 */
public interface JTermListener
{
	/////////////////////////////////////////////////////////////////
	/**
	 * Called with true when entering in the method
	 * JTermReader.read(char[],int,int); with false when exiting.
	 */
	public void waitingRead(boolean reading);
}