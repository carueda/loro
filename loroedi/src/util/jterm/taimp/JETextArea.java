package util.jterm.taimp;





import loroedi.jedit.JEditTextArea;
import loroedi.jedit.DefaultInputHandler;
import loroedi.jedit.Loro;

//////////////////////////////////////////////////////////////
/**
 * This factory provides a JEditTextArea suited for JTerm.
 *
 * @author Carlos Rueda
 * @version 2001-10-09
 */
public class JETextArea
{
	//////////////////////////////////////////////////////////////
	public static JEditTextArea createJEditTextArea(
		String prompt,
		String prefix_special,
		String prefix_invalid,
		boolean paintInvalid
	)
	{
		return Loro.crearJEditTextArea(
			false,              // atender enter
			prompt,             // signo de prompt
			prefix_special,     // prefijo para poner atributo especial a toda la linea
			prefix_invalid,     // prefijo para poner atributo invalido a toda la linea
			paintInvalid        // pintar el ~ para lineas ficticias
		);
	}
}