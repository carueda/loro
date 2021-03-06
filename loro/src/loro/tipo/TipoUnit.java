package loro.tipo;

///////////////////////////////////////////////////////////
/**
 * Tipo para cuando no hay valor de retorno.
 */
class TipoUnit extends Tipo
{
	//////////////////////////////////////////////////////////
	private TipoUnit() {}
	
	//////////////////////////////////////////////////////////
	public boolean esUnit()
	{
		return true;
	}
	
	//////////////////////////////////////////////////////////
	public boolean igual(Tipo t)
	{
		return t instanceof TipoUnit;
	}
	
	//////////////////////////////////////////////////////////
	public Object obtValorDefecto()
	{
		throw new RuntimeException("TipoUnit.obtValorDefecto() llamado!");
	}
	
	//////////////////////////////////////////////////////////
	public String toString()
	{
		return "(Unit)";
	}

	/** Mi unica instancia */
	private static final Tipo instancia = new TipoUnit();

	//////////////////////////////////////////////////////////////////////
	static Tipo obtInstancia()
	{
		return instancia;
	}

	//////////////////////////////////////////////////////////////////////
	private Object readResolve()
	throws java.io.ObjectStreamException
	{
		return instancia;
	}
}