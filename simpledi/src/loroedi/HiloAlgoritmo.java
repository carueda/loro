package loroedi;

import util.consola.ConsolaFactory;
import util.consola.Consola;

import loro.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.PrintWriter;


//////////////////////////////////////////////////////////////////
/**
 * Hilo para ejecución de algoritmos en una consola.
 *
 * @version 2002-09-29
 * @author Carlos Rueda
 */
public class HiloAlgoritmo extends Thread
{
	/** Lista de items a ejecutar. */
	List items;
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Crea un hilo para ejecución de una unidad con argumentos dados.
	 *
	 * @param alg La unidad a ejecutar.
	 * @param sargs Argumentos como objetos.
	 */
	public HiloAlgoritmo(IUnidad alg, Object[] args)
	{
		super("Hilo de ejecución de algoritmos ");
		items = new ArrayList();
		addItem(alg, args);
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Crea un hilo para ejecución de una unidad con argumentos dados.
	 *
	 * @param item Item con algoritmo y argumentos.
	 */
	public HiloAlgoritmo(Item item)
	{
		super("Hilo de ejecución de algoritmos ");
		items = new ArrayList();
		addItem(item);
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Adiciona un ítem de ejecución.
	 *
	 * @param alg La unidad a ejecutar.
	 * @param args Argumentos como objetos.
	 */
	public void addItem(IUnidad alg, Object[] args)
	{
		items.add(new Item(alg, args));
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Adiciona un ítem de ejecución.
	 *
	 * @param item Item con algoritmo y argumentos.
	 */
	public void addItem(Item item)
	{
		items.add(item);
	}

	////////////////////////////////////////////////////////////////////////
	/**
	 * Método de ejecución de este hilo.
	 * Siempre se crea una consola nueva.
	 */
	public void run()
	{
		IEjecutor ejecutor = Loro.crearEjecutorTerminableExternamente();
		Consola consola = ConsolaFactory.crearConsolaJTerm();
		consola.ponEjecutor(ejecutor);
		consola.abrir();
		ejecutor.ponEntradaEstandar(consola.obtReader());
		ejecutor.ponSalidaEstandar(consola.obtWriter());
		PrintWriter pw = new PrintWriter(consola.obtWriter());

		try
		{
			for ( Iterator it = items.iterator(); it.hasNext(); )
			{
				Item item = (Item) it.next();
				consola.setTitle("En ejecución " +item.alg);
				if ( item.args != null )
				{
					ejecutor.ejecutarAlgoritmo(item.alg, item.args);
				}
				else
				{
					ejecutor.ejecutarAlgoritmoArgumentosCadena(
						item.alg, new String[0]
					);
				}
				consola.setTitle("Terminado " +item.alg);
				pw.println();
			}
		}
		catch(EjecucionException e)
		{
			if ( e.esTerminacionExterna() )
			{
				pw.println("Ejecución terminada");
			}
			else
			{
				pw.println("Hubo error en ejecución");
				pw.println(e.getMessage());
				e.printStackTrace(pw);
			}
		}
		catch(RuntimeException e)
		{
			pw.println("Hubo error en ejecución (" +e.getClass()+ ")");
			pw.println(e.getMessage());
			e.printStackTrace(pw);
		}

		consola.ejecucionTerminada();
	}

	////////////////////////////////////////////////////////////////////////
	/**
	 * Define un ítem a ser ejecutado.
	 */
	public static class Item
	{
		/** La unidad a ejecutar. */
		IUnidad alg;
	
		/** Lista de argumentos. */
		Object[] args;
		
		public Item(IUnidad alg, Object[] args)
		{
			this.alg = alg;
			this.args = args;
		}
		public IUnidad getAlgorithm()
		{
			return alg;
		}
		public Object[] getArgs()
		{
			return args;
		}
	}
}