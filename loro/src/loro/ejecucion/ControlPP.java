package loro.ejecucion;


///////////////////////////////////////////////////////////
/**
 * Control para paso a paso.
 */
public class ControlPP
{
	int currSenal = -1;
	boolean active = true;
	
	//////////////////////////////////////////////////
	public synchronized void setActive(boolean active)
	{
		this.active = active;
		notifyAll();
	}
	
	//////////////////////////////////////////////////
	public synchronized void ponSenal(int senal)
	throws InterruptedException
	{
		if ( !active )
			return;
		
		while ( active && currSenal >= 0 )
			wait();
		
		currSenal = senal;
		notifyAll();
	}

	//////////////////////////////////////////////////
	public synchronized int obtSenal()
	throws InterruptedException
	{
		if ( !active )
			return 0;
		
		while ( active && currSenal < 0 )
			wait();
		
		int senal = currSenal;
		currSenal = -1;
		notifyAll();
		return senal;
	}
}
	

