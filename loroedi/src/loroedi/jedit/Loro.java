package loroedi.jedit;

/////////////////////////////////////////////////////
/**
 * Preparativos necesarios para Loro.
 *
 * @author Carlos Rueda
 * @version 2001-10-09
 */
public class Loro
{
	///////////////////////////////////////////////////////
	/**
	 * Crea un area de texto para Loro con las caracteristica
	 * tipicas para el editor principal.
	 */
	public static JEditTextArea crearJEditTextArea()
	{
		JEditTextArea ta = new JEditTextArea();
		ta.setTokenMarker(new LoroTokenMarker());

		return ta;
	}

	///////////////////////////////////////////////////////
	/**
	 * Crea un area de texto para Loro con las algunas caracteristicas
	 * indicadas.
	 *
	 * @param enter   Si es true, se atiende el enter.
	 * @param prompt  Si es !- null, se resalta esta cadena de manera especial.
	 *                Vea el manejo de token-marker en loro.jedit.
	 */
	public static JEditTextArea crearJEditTextArea(
		boolean enter,
		String prompt,
		String prefix_special,
		String prefix_invalid,
		boolean paintInvalid
	)
	{
		TextAreaDefaults tad = TextAreaDefaults.getDefaults();
		tad.inputHandler = new DefaultInputHandler();
		((DefaultInputHandler) tad.inputHandler).addDefaultKeyBindings(enter);
		tad.paintInvalid = paintInvalid;

		JEditTextArea ta = new JEditTextArea(tad);
		ta.setTokenMarker(new LoroTokenMarker(prompt, prefix_special, prefix_invalid));

		return ta;
	}
}