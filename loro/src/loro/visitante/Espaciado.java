package loro.visitante;

import java.io.PrintWriter;
import java.io.Writer;
import java.io.OutputStream;

/**
 * Ayuda a manejar la indentación para generación
 * de código fuente (Java, por ejemplo).
 */
class Espaciado 
{
   final int INDENT_AMT;

   String spc = "";
   int indentLevel = 0;

   public Espaciado(int indentAmt) {
	  INDENT_AMT = indentAmt;
   }                                          
   public String toString() {
	  return spc;
   }                                          
   public void updateSpc(int numIndentLvls) {
	  indentLevel += numIndentLvls;

	  if ( numIndentLvls < 0 )
		 spc = spc.substring(-1 * numIndentLvls * INDENT_AMT);
	  else if ( numIndentLvls > 0 ) {
		 StringBuffer buf = new StringBuffer(spc);

		 for ( int i = 0; i < numIndentLvls * INDENT_AMT; ++i )
			buf.append(" ");

		 spc = buf.toString();
	  }
   }                                          
}