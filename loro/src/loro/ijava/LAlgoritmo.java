package loro.ijava;

/////////////////////////////////////////////////////////////
/**
 * Un algoritmo Loro.
 * LAmbiente ofrece, entre otros, servicios para obtener un 
 * algoritmo dado su nombre.
 *
 * @author Carlos Rueda
 */
public interface LAlgoritmo 
{
	//////////////////////////////////////////////////////////////
	/**
	 * Ejecuta este algoritmo sobre los argumentos dados.
	 * El objeto de retorno debera ser promovido al tipo
	 * adecuado correspondiente al tipo de retorno del
	 * algoritmo. Si la declaracion del algoritmo indica que
	 * no hay salida, se retorna null.
	 */
	public Object ejecutar(Object[] args)
	throws LException;
}