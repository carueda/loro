Loro README
$Id$

NOTAS
	
	- Si un elemento dentro de un paquete foo necesita referenciar un
	  elemento baz que est� en el paquete an�nimo, debe hacer un 
	  'utiliza xxx baz;' correspondiente. Es decir, el paquete an�nimo
	  NO es visible autom�ticamente desde otros paquetes.
	  (Como contraste, en Java no es posible acceder al paquete an�nimo
	  desde un paquete nombrado ni siquiera con un 'import' --el import 
	  no acepta un nombre simple.)

////////////////////////////////////////////////////////////////////////////
2003-05-14 Version 0.7.91

	- Avances en interfaz para interpretaci�n interactiva.
	
	- Eliminado manejo de prefix (PrefixWriter) en 
	  InterpreteImpl.InteractiveInterpreter.

////////////////////////////////////////////////////////////////////////////
2003-05-13 Version 0.7.91

	- Revisar ChequeadorBase._aKindOf: eliminar o factorizar en Tipos.

	- InterpreteImpl.valorComillas: chequea si ``o instanceof String'' o
	  ``o instanceof Character''.
	  
	- Otros ajustes sobre es_instancia_de.
	
	- Bug 737350 corregido:
	  ``obj es_instancia_de Clase'' resulta en falso cuando obj es
	  instancia de una subclase de Clase.
	  Soluci�n: LoroEjecutor.visitar(NEsInstanciaDe) se basa ahora en el tipo
	  en tiempo de ejecuci�n asociado al objeto (antes utilizaba el tipo 
	  *declarado*). Se defini� nuevo servicio Tipos.aKindOf(NClase, TipoClase)
	  de apoyo.
	
////////////////////////////////////////////////////////////////////////////
2003-05-08 Version 0.7.91

	- IInterprete/InterpreteImpl: nuevo servicio getSymbolTable().
	
	- PrefixWriter: revisi�n (a�n incompleta)
	
////////////////////////////////////////////////////////////////////////////
2003-05-06 Version 0.7.91

	- Lenguaje: se permite ahora la asignaci�n de un literal entero a un
	  caracter siempre que el valor entero sea >= 0 y < 65536 (como en Java).
	  Ejs:    
	  		c : caracter := 76;
			['L', 111, 'r', 111]
	  Ajustes en Chequeador, ChequeadorBase._chequearAsignabilidad().
	  Nota: En el caso:
	  		para c:caracter en [111, 0] haga fin para
	  a�n se genera un error de posible conversi�n inv�lida. Para permitir
	  este caso habr�a que actualizar Chequeador.visitar(NForEach n) y
	  LoroEjecutor.visitar(NForEach n). (No muy necesario por lo pronto.)
	  
	- LoroIParser.jj:
		En e_prefijoPrimaria() se prob� posibilidad de explicitar el tipo
		de los elementos de una expresi�n arreglo, ej:
			[entero 45, 'c']
		lo que permitir�a indicar un arreglo vac�o, ej:
			[entero]
		pero se deja entre comentarios por ahora, para hacer el cambio
		completo despu�s.
		Otra posibilidad es permitir simplemente la expresi�n [] que ser�a
		compatible con arreglos de cualquier tipo (?). Pendiente.
	
	
////////////////////////////////////////////////////////////////////////////
2003-05-04 Version 0.7.91

	- ChequeadorBase.chequear(Nodo n) complementado para controlar 
	  empilamiento de �mbitos de la tabla de s�mbolos.
	
	- Nuevo m�todo LoroEjecutorBase.ejecutarNodo(Nodo n) por ahora s�lo para
	  invocar el m�todo de aceptaci�n (estilo ChequeadorBase.chequear(Nodo n)).
	  InterpreteImpl.eval() ahora invoca este m�todo.
	  
	- Bug 731674 corregido:
	  Soluci�n: LoroEjecutorBase._visitarAcciones() no controlaba completamente
	  el anidamiento de �mbitos. S�lo manejaba la tabSimb actual, pero se
	  necesitaba tambi�n anotar cu�l tabSimb operar puesto que �sta va cambiando
	  en operaciones _push/_pop. As� que ahora se anota la tabSimb actual y su
	  marca para restaurar (secci�n finally) este estado despu�s de visitar el 
	  bloque de acciones.
	
////////////////////////////////////////////////////////////////////////////
2003-04-28 Version 0.7.91

M�s avances en redise�o de interpretaci�n
	- Ajustes en InterpreteImpl/IInterprete.
	- Nueva interfaz IInterprete.IInteractiveInterpreter
	- Nueva interfaz IInterprete.IMetaListener
	- Nueva clase loro.util.PrefixWriter que define un flujo con manejo de prefix
	  al estilo de JTerm.
	- Pruebas preliminares siguen haci�ndose en loro.tools.InterpreteInteractivo
	  que ya empieza a funcionar aceptablemente (a�n no se modifican otros clientes).
	  
Misc.
	- Nuevo TODO.txt
	
////////////////////////////////////////////////////////////////////////////
2003-04-24 Version 0.7.91

	- Manejo del flag 'execute' a nivel de IInterprete.
	- Simplificado loro.tools.InterpreteInteractivo de acuerdo a nuevos
	  cambios en InterpreteImpl.
	- Ajuste menor a TablaSimbolos.toString: No "\n" final.

////////////////////////////////////////////////////////////////////////////
2003-04-23 Version 0.7.91

Reorganizaci�n de interpretaci�n: Se busca unificar el manejo de meta-comandos
desde el mismo n�cleo.

	- Complementada producci�n accionInterprete en LoroIParser.jj para
	  permitir meta-comandos, por lo menos en el estilo que se han
	  manejado hasta ahora.
	- InterpreteImpl: inicio de modificaciones para atender meta-comandos.
	  Incompleto.
	  Ver eval(), metaProcesar().
	  Pendiente: definici�n de interfaz con clientes que deseen atender
	  meta-comandos no entendidos por el n�cleo (como .limpiar).
	
////////////////////////////////////////////////////////////////////////////
2003-04-16 Version 0.7.91

	- LAlgoritmoImp.ejecutar():  Ajustes en proceso para definir nueva
	  ejecuci�n sobre un nuevo objeto/hilo correspondiente.
	  PENDIENTE: revisi�n general.
	
////////////////////////////////////////////////////////////////////////////
2003-04-15 Version 0.7.91

	- LoroEjecutor.ejecutarAlgoritmoBsh ahora enlaza tambien la variable
	  '$este' con el valor de 'este' al int�rprete bsh. En el c�digo
	  Java siempre ser� cierto que: $este == $amb.obtEste() .

////////////////////////////////////////////////////////////////////////////
2003-04-08 Version 0.7.9

	- Bug corregido: Un argumento de tipo primitivo en Loro (ejemplo,
	  caracter) llegaba como un objeto wrapper (Character) en el c�digo
	  interpretado por BeanShell.
	  
	  Soluci�n:
	  Revisado y ajustado el paso de argumentos a m�todo en BeanShell.
	  Ver LoroEjecutor.ejecutarAlgoritmoBsh()
	  Se hace un manejo directo de los tipos primitivos. 
	  Caso especial es el tipo 'caracter' en donde se usa directamente un
	  bsh.Primitive puesto que no hay (en BeanShell 1.2b6) un m�todo
	  correspondiente bsh.set(id, char) como s� los hay para otros primitivos 
	  como bsh.set(id ,int), etc.
	  
////////////////////////////////////////////////////////////////////////////
2003-04-03 Version 0.7.9

Revisi�n de manejo de objetos Loro.

	- Ahora se pueden utilizar algoritmos y objetos para interfaces en
	  donde se espera un loroI::sistema::Objeto.
	  
	  Implementaci�n:
	  	Nueva superclase TipoObjeto para todos los tipos que representan
		objetos en Loro: Tipo{Unidad,Arreglo,Cadena,Nulo}. En particular,
		redefine esObjeto() para retornar true, y redefine esConvertibleA(t)
		para ver si t es la clase raiz.
	  	En TipoClase, cuando se trata de la clase raiz, ahora esAsignable(t)
		y esConvertibleA(t) aceptan que t.esObjeto(). 
		
	- Modificados:
		LoroEjecutorBase
		LoroEjecutor
		Tipo*
		
	- Ahora se pueden crear objetos desde c�digo Java para interfaces Loro, 
	  es decir, sin necesidad de clases mediadoras.
		- Nueva interfaz LMethod
		- Nueva operaci�n LObjeto.getMethod(String nom)
		  En Objeto, getMethod retorna null.
		  La operaci�n est� ahora destinada a servir a otras implementaciones
		  de LObjeto. En un futuro podr�a unificarse el manejo y entonces 
		  se har�a una implementaci�n adecuada.
		
		
	Pendientes:
		- Definir interfaz (digamos ISecuencia) para arreglos y cadenas, de
		  tal modo que se maneje .longitud, .inf y .sup como operaciones: 
		  .longitud(), .inf(), .sup().
	
////////////////////////////////////////////////////////////////////////////
2003-03-31 Version 0.7.9

	- Objeto.obtMetodo(String nom): corregido para utilizar nombre
	  simple y no nombre completo del m�todo.
	
////////////////////////////////////////////////////////////////////////////
2003-03-22 Version 0.7.9

Algo de refactoring y limpieza -- manejo de unidades compiladas.

	- ManejadorUnidades:
		- Eliminado atributo obsoleto rutaLeer.
		
		- OroLoaderManager se encarga ahora del coreOroLoader.
		
		- Eliminado cargarUnidadesDePaquete(): Documentador y
		  LoroDocumentador ahora piden mu.getOroLoaderManager() directamente.
		  
		- Eliminado cargarUnidadesDeApoyo(): DocumentadorImpl ahora
		  pide mu.getOroLoaderManager() directamente.
		  
		- Eliminado atributo dirGuardarOroLoader, que se asociaba
		  con dirGuardar.
		  
		- Eliminado verificarUnidadesCompiladas(): Loro ahora
		  pide mu.getOroLoaderManager() directamente.
		  
		- Movido cargarUnidadesDeZip() a Documentador.
		
		- Eliminado getPackageNamesFromZip() (no usado).
		  
	- Nuevo servicio OroLoaderMan.setFirstDirectory(dir), que es llamado
	  en ManejadorUnidades.ponDirGuardarCompilado().
	
////////////////////////////////////////////////////////////////////////////
2003-03-18 Version 0.7.9

	- LoroEjecutor.visitar(NEsInstanciaDe n) ahora utiliza Tipos.aKindOf().
	  Esto corrige bug 706065 que produc�a un resultado falso al ejecutar:
	  	unEmpleado  es_instancia_de  Persona
	  siendo Empleado extensi�n de Persona.
	  
	- Sintaxis: ahora ";" es opcional al final de:
		- indicaci�n de paquete
		- indicaci�n utiliza
		- cada atributo en definici�n de clase
	  
	- Nueva operaci�n getPrototype() para especificaciones y algoritmos.
	
////////////////////////////////////////////////////////////////////////////
2003-03-14 Version 0.7.8 (final)

	- Adici�n al lenguaje:
	  Incluida variante "for each" a iteraci�n "para":
	  	"para" ...  "en"  expresion haga ...
	  donde la expresion debe ser un arreglo o una cadena.
	  Ejemplo:
		para cad:cadena en ["hola", "mundo"] haga
			escribir(cad+ " --> ");
			para car:caracter en cad haga
				escribir("  " +car);
			fin para;
			escribirln("");
		fin para;
	  
	  Actualizados:
	  	LoroIParser.jj
		ConstructorArbol
	  	NForEach (nuevo nodo)
		IVisitante, y visitantes simples derivados
		Chequeador
		LoroEjecutor
	  
	- Mejoramiento de documentaci�n HTML, con correcci�n de formato
	  para s�mbolos especiales como ``&'' y ``<'' en c�digo Loro
	  llevado directamente a c�digo HTML, concretamente el caso de 
	  las pre y poscondiciones (VisitanteLoroDoc.afir()).
	  Ver:
	  	VisitanteLoroDoc
		Util.formatHtml()        -- nuevo
		Util.processInlineTags() -- movido de VisitanteLoroDoc
	
	NOTA: Las cadenas de documentaci�n NO son procesadas en este
	sentido pues se asume que es c�digo HTML desde el principio;
	por lo menos, esa es la situaci�n actual (luego habr� que
	estudiar este asunto).
	
	  
////////////////////////////////////////////////////////////////////////////
2003-03-13 Version 0.7.8

	- VisitanteLoroDoc: Puesto un </table> que faltaba.
	  
////////////////////////////////////////////////////////////////////////////
2003-03-11 Version 0.7.8

	- Mejoramiento de la apariencia de la documentaci�n.
	  Ver VisitanteLoroDoc.
	  
////////////////////////////////////////////////////////////////////////////
2003-02-25 Version 0.7.8

	- LoroEjecutor.ejecutarAlgoritmoBsh ahora hace:
	      res = _convertirRetornoDeJava(res);
	  para convertir debidamente el valor retornado por BeanShell (de
	  forma similar a ejecutarAlgoritmoJava()).


////////////////////////////////////////////////////////////////////////////
2003-03-09 Version 0.7.8

	- Chequeador.visitar(NNombre):
		- Ahora permite que el nombre sea el de un algoritmo utilizado 
		como un valor. Antes se generaba error estilo: "No se puede
		usar posible unidad..."
	  
	  	- Cambio menor en el mensaje de error cuando se trata de usar
		  una clase como un valor.
		  
	- Nuevo servicio Tipos.esReferencia(Tipo t) para indicar tipos cuyas 
	  variables admiten el valor nulo. Esto permite controlar los valores
	  de retorno al invocar  algoritmos implementados en otros lenguajes 
	  (LoroEjecutor.visitar(NAlgoritmo)) y poder decidir si se admite
	  o no un retorno nulo, que es el caso cuando la variable de salida es de
	  un tipo referencia.  La implementacion de Tipos.esReferencia(t) retorna
	  ahora lo mismo que t.obtValorDefecto() == null. 
	  Lo anterior corrige bug 698059. 

	
////////////////////////////////////////////////////////////////////////////
2003-02-15 Version 0.7.7  (final)

Sobre nuevo c�digo de implementaci�n "usr"

	- Gram�tica: Leve ajuste: Segunda cadena para "implementaci�n" es 
	  ahora opcional. Ver LoroIParser.jj
	  Dependencias en LoroEjecutor y NAlgoritmo ajustadas.
	  En particular el modo "usr" no requiere esta informacion adicional.
	  
	- Nuevo servicio UtilEjecucion._executeUsrAlgorithm() de apoyo a
	  LoroEjecutor.ejecutarAlgoritmoUsr().
	  Se trata de una primera versi�n de interacci�n con el usuario
	  para notificarle que el algoritmo con indicaci�n "usr" acaba de
	  ser invocado. Se le muestran los valores reales de los argumentos
	  y se le pide una expresi�n para asignar a la variable de salida
	  (en caso que la haya). Esta expresi�n es evaluada con un
	  int�rprete que se crea para el efecto, el cual se inicializa con
	  la tabla de s�mbolos vigente, es decir, con las correspondientes
	  declaraciones tanto de las entradas como de la salida.
	  Una posible complementaci�n a este esquema es permitirle al usuario
	  el acceso a las declaraciones del entorno global, es decir,
	  las que se han hecho a trav�s del int�rprete interactivo. Esto
	  podr�a hacerse a trav�s de un objeto especial con atributos
	  correspondientes a dichas declaraciones. El nombre de este
	  objeto especial podr�a ser una palabra reservada cuyo uso se
	  restringir�a solamente a la ejecuci�n de algoritmos "usr".
	  (A prop�sito, 'global' se encuentra como palabra reservada 
	  desde hace buen tiempo pero a�n no se usa para nada).
	  
	  A�n no se define una API que le permita a un cliente tener
	  alg�n control sobre la ejecuci�n de algoritmos "usr".
	  Se deja pendiente (no es prioritario).
		
////////////////////////////////////////////////////////////////////////////
2003-02-14 Version 0.7.7

	- PilaEjecucion.java: algo de limpieza de c�digo.
	
	- Nuevo c�digo de implementaci�n "usr".
	  Ver LoroEjecutor.ejecutarAlgoritmoUsr()  
	  Es un primer acercamiento a la posible implementaci�n del mecanismo 
	  de implementaci�n sin codificaci�n previa, es decir, cuando un
	  algoritmo "usr" es invocado, el sistema delegar� en el usuario real
	  la correspondiente ejecuci�n. El n�cleo ofrecer�a un despacho
	  simple por entrada/salida est�ndares para dialogar con el usuario:
	  mostr�ndole cu�l algoritmo (nombre) y con cu�les argumentos reales
	  ha sido invocado y le pedir� que responda de acuerdo con el
	  par�metro de salida. Un cliente gr�fico (loroedi concretamente)
	  deber� poder complementar esto para ofrecer una ventana
	  nueva de di�logo que haga m�s claro el evento. Posiblemente se
	  agregar�a la posibilidad de hacer en general una sesi�n de
	  interpretaci�n interactiva para que el usuario puedar llevar
	  a cabo acciones en el mismo lenguaje antes de retornar de la
	  invocaci�n.

	
////////////////////////////////////////////////////////////////////////////
2003-02-13 Version 0.7.7

Ejecuci�n paso-a-paso

	- PilaEjecucion.java: Se mantiene referencia al nodo pasado en
	  actualizarTope en cada marco de activaci�n. Esta informaci�n
	  es m�s completa (que s�lo la l�nea/columna), y adem�s permite
	  al EjecutorPP._popEvent() notificar tambi�n el nodo concreto
	  al que se regresa.
	  
////////////////////////////////////////////////////////////////////////////
2003-02-11 Version 0.7.7

	- Nuevo servicio Loro.getNodeDescription(INodo n) que obtiene una
	  descripci�n concisa del tipo del nodo dado.
	  Se apoya en una nueva clase loro.visitante.Descriptor, la que
	  a su vez se apoya en una clase static interna que opera como
	  visitante para obtener la descripci�n.
	  Pendiente: precisar estas descripciones (por ejemplo en un nodo
	  NMas convendr�a distinguir entre suma aritm�tica o concatenaci�n
	  de cadenas).
	  
	- Eliminado loro.ejecucion.ControlPP
	  A nivel del n�cleo el seguimiento paso-a-paso se reduce al
	  mecanismo de notificaci�n a un IObservadorPP, en quien recaer�
	  la responsabilidad de la sincronizaci�n de hilos, si es del caso.

////////////////////////////////////////////////////////////////////////////
2003-02-10 Version 0.7.7

Ejecuci�n paso-a-paso

	- EjecutorPP.java: Se hace _obtSenalPP() tanto en _pushEvent()
	  como en _popEvent().

////////////////////////////////////////////////////////////////////////////
2003-02-09 Version 0.7.7

Ejecuci�n paso-a-paso

	- Nuevo visitante Seguidor que extiende LoroEjecutor con el fin
	  de proporcionar notificaciones al entrar-a y salir-de cada nodo
	  visitado. Ahora EjecutorTerminable extiende Seguidor.
	  EjecutorPP (que extiende EjecutorTerminable) desde luego tambi�n
	  hace uso de este esquema.
	  
	- En DerivadorJavaCC.derivarFuente() corregida la asociaci�n de 
	  c�digo fuente para cada unidad. (Antes se pon�a s�lo el fragmento
	  propio de la unidad, pero debe ponerse todo el c�digo fuente
	  de donde proviene.)
	  
	- ControlPP.setActive(boolean active) no estaba llamando notifyAll().
	  Corregido. (Este bug hacia congelar la ejecuci�n cuando se
	  invocaba ejecutor.resume().)
	
////////////////////////////////////////////////////////////////////////////
2003-02-06 Version 0.7.7

Ejecuci�n paso-a-paso

	- Mejoramiento general.
	
////////////////////////////////////////////////////////////////////////////
2003-02-05 Version 0.7.7

Ejecuci�n paso-a-paso

	- En EjecutorPP atenci�n de eventos asociados a la pila de ejecucion:
	  _pushEvent, _popEvent (llamados desde la superclase LoroEjecutorBase).

	- M�s argumentos para IObservadorPP.ver:
		IObservadorPP.ver(IUbicable u, ISymbolTable symbTab, String src);
		
	- Aun falta sincronizar todo el asunto, por ejemplo revisar qu� nodos
	  no se "visitan" directamente y por lo tanto se requiere alg�n
	  mecanismo para notificar su proceso...
	
////////////////////////////////////////////////////////////////////////////
2003-02-04 Version 0.7.7

Ejecuci�n paso-a-paso

	- El constructor de InterpreteImpl recibe ahora un IObservadorPP obspp
	  para crear un EjecutorPP. Si el par�metro es nulo, se crea entonces
	  un EjecutorTerminable.
	  
	- El servicio Loro.crearInterprete ahora recibe a su vez el observador
	  para seguimiento paso-a-paso, de tal manera que se delega en el cliente
	  la decisi�n de si quiere una ejecuci�n con o sin seguimiento paso-a-paso, 
	  dependiendo de si suministra un observador o no (null).
	  

////////////////////////////////////////////////////////////////////////////
2002-12-05 Version 0.7.7

	- Dise�o preliminar de ejecuci�n paso-a-paso.
	  	ControlPP
		EjecutorPP
		IObservadorPP
	  Compilaci�n bien, pero se deshabilita por ahora. 
	  En el constructor de InterpreteImpl basta con cambiar una condici�n
	  (ver nota all�) para habilitar de nuevo las pruebas de paso-a-paso.
		
	
////////////////////////////////////////////////////////////////////////////
2002-11-27 Version 0.7.7

	- TablaSimbolos: Quitado el manejo basado en stack de marcas. Este manejo
	  se hace ahora s�lo en el estilo:
	  	int marca = tabSimb.marcar();
		try ...
		finally { tabSimb.irAMarca(marca); }
	  Esto da m�s claridad para garantizar el debido empilamiento con base
	  en el impl�cito empilamiento de la ejecuci�n Java.
	  
	- Nuevo palabra reservada "interfaz", que es la palabra correcta
	  seg�n la RAE.  Por ahora se deja tambi�n como sin�nimo "interface".
	
Algunos avances en manejo de m�todos, pero a�n incompleto.

	- Manejo de "�ste" como expresi�n.  Ver LoroIParser.jj.
	- Nuevo nodo NEste.
	- Actualizados varios fuentes (visitantes, ConstructorArbol, ...)
	- NAlgoritmo fue modificado, as� que deja de haber compatibidad binaria.
	
	- Ahora todo acceso a los atributos de una clase se debe hacer a trav�s
	  de "�ste", tanto en constructores como en m�todos. Anteriormente las
	  declaraciones de los atributos segu�an vigentes durante las visita
	  tanto a contructores como a m�todos. Un efecto de esto es que ahora
	  es posible declarar par�metros repitiendo nombres de atributos.
	  Este cambio parece interesante por disciplina para ense�anza pero
	  hay que someterlo a m�s consideraci�n.
	  
	- Nueva operaci�n LAmbiente.obtEste() para permitirle a c�digo Java
	  acceder al objeto en implementaci�n de m�todos. Su implementaci�n, como
	  otros, est� en LoroEjecutor.
	  Pendiente acceso a m�todos desde la interfaz en loro.ijava.
	
	- Siguen m�s pruebas para decidir hasta donde dejar este manejo para
	  una primera versi�n aunque no completa.  
	  
////////////////////////////////////////////////////////////////////////////
2002-11-21 Version 0.7.6

Nuevo manejo de excepciones

	- Nuevas palabras reservadas y sintaxis correspondiente para:
		lance, intente, atrape, siempre
	  Ver LoroIParser.jj. 
	  
	  La sintaxis para "atrapar" es:
		"intente"
			acciones
			(
				"atrape" "(" declaracion ")"
					acciones
				[ "fin" "atrape" ]
			)*
			[
				"siempre"
					acciones
			]
		"fin" "intente"
	  
	  y para "lanzar":
	  	"lance" expresion
	  
	- Nuevos nodos para el �rbol: NLance, NIntente, NAtrape.
	- Nueva ControlLanceException.
	- Actualizados: ConstructorArbol, IVisitante, VisitanteProfundidad, Chequeador, 
	  LoroEjecutor, ControlException, InterpreteImpl.
	  
	Descripci�n
		En esta primera versi�n (digamos, exploratoria) no se incluye la declaraci�n de
		si un proceso lanza o no excepciones. Adem�s, se permite el lanzamiento de
		cualquier tipo de expresi�n, incluso primitivos.
	
	
	
	  
////////////////////////////////////////////////////////////////////////////
2002-11-18 Version 0.7.5

	- Nuevo Loro.expandExtensionFile(File file)
	  
	- Nuevo Loro._expandExtensions() que expande cada archivo paths_dir/xxx.lar
	  en paths_dir/xxx/ en caso que paths_dir/xxx/ no exista.
	  La idea es s�lo referenciar subdirectorios de b�squeda bajo paths_dir
	  (y no *.lar). En la inicializaci�n de _crearManejadorUnidades() s�lo se 
	  mantienen para b�squeda los *.lar encontrados bajo ext_dir/.

	- En Loro._crearManejadorUnidades() se entonces el siguiente preparativo
	  relacionado con los archivos *.lar y directorios de b�squeda:
	  
	    - Se invoca primero _expandExtensions()
		
	  	- Se tienen en cuenta s�lo los directorios encontrados bajo el directorio
		  indicado en configurar().
		  
		- Se agregan los *.lar encontrados bajo ext_dir pero estos van despu�s en
		  la lista de b�squeda para que tengan menos prioridad.
		  
		- NOTA: los posibles archivos *.jar (java) se siguen buscando s�lo
		  bajo ext_dir.  PENDIENTE unificar este manejo.
		
	  
////////////////////////////////////////////////////////////////////////////
2002-11-17 Version 0.7.5

	- Ajustes menores a OroLoaderManager.java y Loro.java
	
	- Nuevo ManejadorUnidades.addExtensionToPath(File file)
	- Nuevo Loro.addExtensionToPath(File file)
	
////////////////////////////////////////////////////////////////////////////
2002-11-07 Version 0.7.4

	- Nueva operaci�n IOroLoader.getFilenames() para obtener lista completa
	  de los nombres de los elementos del cargador.
	  Actualizados: CoreOroLoader, DirectoryOroLoader, ZipFileOroLoader.
	  
	- Nuevos m�todos en loro.util.Util para copiar elementos de una extensi�n
	  a un directorio o a otra extensi�n (.lar).
	  
	- Actualizado build-ext.html: ahora se utiliza <zip> a cambio de <jar>
	  (El MANIFEST.MF no se necesita).
	
////////////////////////////////////////////////////////////////////////////
2002-10-30 Version 0.7.4

	- En loro.util.Util:
		- Nuevo m�todo similar a getFilenames, pero recibiendo tambi�n
		  un filename filter: getFilenames(String, boolean, FilenameFilter)
		- Nuevos m�todos para agregar entradas a un archivo zip:
			copyDirectoryToZip(File, ZipOutputStream, FilenameFilter)
			copyFileToZip(String, String, ZipOutputStream)
	
////////////////////////////////////////////////////////////////////////////
2002-10-25 Version 0.7.4

	- Modificado InterpreteImpl para permitir reader y/o writer nulos.
	
	- Modificada gramatica para aceptar coma "," al final de la lista de
	  expresiones en definici�n de arreglo enumerado.
		
////////////////////////////////////////////////////////////////////////////
2002-10-22 Version 0.7.4
	
	- Bug 621638 corregido: No se hac�a asociaci�n simple/compuesto para
	  el nombre de la superclase (extiende) en la definici�n de una clase.
	  Esto provocaba errores de "(super)clase no encontrada" tanto en
	  compilaci�n como en ejecuci�n.
	  Ver: Chequeador.visitar(NClase n).
	  
	- En declaraci�n de atributo de clase ahora es opcional el ":" antes
	  de la cadena de documentaci�n. Ver LoroIParser.jj

	- Permitida ahora una lista de acciones como entrada para interpretaci�n
	  interactiva (antes s�lo una acci�n era permitida). Ej:
	    $ cad: cadena := "hola "; cad := cad + "mundo"; escribir(cad); cad
	  que es equivalente a:
	    $ cad: cadena := "blah blah";
		$ cad = cad + "mundo";
	    $ escribir(cad);
	    $ cad
	  SALVO que s�lo se muestra el valor de la �ltima acci�n si aplica.
	  El ";" es obligado como separador pero es opcional al final de la lista.
	  Ver:
		LoroIParser.jj
		DerivadorJavaCC.java
		IDerivador.java
		InterpreteImpl.java
		
	- Loro.crearInterprete ahora recibe tambi�n un par�metro que indica si se
	  crea una nueva tabla de s�mbolos o bien se comparte una com�n.
	  
////////////////////////////////////////////////////////////////////////////
2002-10-09 Version 0.7.3

	- Agregada posibilidad de implementar algoritmos utilizando BeanShell.
	  (Se utiliza ahora bsh-core-1.2b6.jar)

		- El identificador para la implementaci�n es "bsh".
		
		- Nuevo LoroEjecutor.ejecutarAlgoritmoBsh().
			
		- Agregado elemento l�xico IMPL:  {%texto%} para info o c�digo
		  en implementaci�n usando otro lenguaje, con el fin de hacer
		  m�s clara la escritura del c�digo (no tener que "escapar" las
		  comillas dobles, por ejemplo).  (Las comillas dobles se
		  siguen aceptando.)
		  
		- Pendiente hacer suficientes pruebas.
		  
		
	- Agregado Loro.getAutomaticPackageName() para facilitar posibles 
	  controles asociados a los clientes del n�cleo.
	
////////////////////////////////////////////////////////////////////////////
2002-10-07 Version 0.7.2

	- Movido DerivacionException al paquete loro. (Antes en loro.derivacion.)
	
	- Bug corregido:  No conversi�n en retorno de valor en algoritmo.
	  Soluci�n:
	  ControlRetorneException ahora incluye las expresiones como tales
	  para poder acceder a sus tipos en LoroEjecutor.visitar(NAlgoritmo),
	  fragmento catch(ControlRetorneException) y hacer la conversi�n de
	  tipo correspondiente.
	
////////////////////////////////////////////////////////////////////////////
2002-10-06 Version 0.7.2

	- CompiladorImpl.compileAlgorithm ahora tiene en cuenta que
	  expected_specname sea posiblemente el nombre simple de una especificaci�n
	  en el mismo paquete del algoritmo o bien en el paquete autom�tico.

	- Nuevo Loro.getClass(String qname).
	
	- Loro.get{Specification,Algorithm,Class}(String qname) agregan posibilidad
	  que el nombre sea en el paquete autom�tico, en el caso que no se haya
	  encontrado la unidad con el nombre pasado directamente (verificando
	  primero que el argumento sea un nombre simple).
	  
	- Modificado LoroIParser.jj:
	
		- para permitir tambi�n una cadena de documentaci�n en el contenido de
		  una afirmaci�n. Ajustados visitantes Chequeador y LoroEjecutor. 
		  La idea es que se utilicen cadenas de documentaci�n en vez de literales 
		  cadena para afirmaciones que se aceptan inmediatamente. Por compatibilidad,
		  las cadenas literales se siguen aceptando por ahora.
		  Tambi�n se actualizaron ConstructorArbol y NAfirmacion para esto.
		  
		- Ahora se acepta s�lo una comilla sencilla (') al final de un ID para
		  fines de indicar variables sem�nticas. Esto para evitar controles adicionales
		  innecesariamente complicados para resolver posible confusi�n con cadenas de
		  documentaci�n (que utilizan las comillas sencillas como delimitadores).
	
////////////////////////////////////////////////////////////////////////////
2002-10-03 Version 0.7.2

	- Ajustes a interface IOroLoader para permitir a los clientes acceder
	  a los *nombres* de las unidades a pesar posiblemente de que las 
	  unidades compiladas como tales no puedan accederse (por ejemplo,
	  por problemas de compatibilidad).

////////////////////////////////////////////////////////////////////////////
2002-10-02 Version 0.7.2

	- Chequeador: Ahora se hace _borrarCompilado(n) en chequeo de unidades
	  antes de proceder con la compilaci�n como tal. Esta llamada se hace
	  justo despu�s de n.ponPaquete(paqueteActual), que es cuando se sabe 
	  el nombre completo de la unidad para saber qu� archivo borrar.
	  Esto evita los problemas de confusi�n cuando una compilaci�n exitosa
	  es seguida de una con errores. Anteriormente el archivo compilado
	  "sobreviv�a", por lo que pod�a cargarse la unidad "exitosamente"
	  a pesar de haber fallado una posterior compilaci�n.
	  
Modificaciones para permitir que m�ltiples int�rpretes compartan una misma
tabla de s�mbolos:

	- Loro.crearInterprete ahora maneja una misma tabla de s�mbolos para
	  todos los int�rpretes creados.
	- TablaSimbolos.irAMarca modificado: ahora tiene en cuenta el tama�o
	  actual de la tabla antes de empezar a sacar marcas de la pila.
	  Esto fue necesario para el siguiente cambio.
	- ChequeadorBase.{<init>,reiniciar}() modificados de tal manera que
	  se tiene en cuenta ahora el tama�o (marca) original de la tabla
	  de s�mbolos suministrada para efectos de reiniciar el chequeador.
	  Antes se hac�a simplemente tabSimb.reiniciar().

  (NOTA: Ning�n cambio fue necesario en este sentido para efectos de ejecuci�n.)
	  
////////////////////////////////////////////////////////////////////////////
2002-10-01 Version 0.7.2

	- Algunos ajustes generales en Chequeador/LoroEjecutor sobre manejo de 
	  �mbitos de declaraciones de variables y etiquetas en iteraciones,
	  necesarios para prevenir posibles anomal�as principalmente a nivel de
	  interpretaci�n interactiva.

////////////////////////////////////////////////////////////////////////////
2002-09-26 Version 0.7.2

	- Nuevos Loro.{getNumArguments,hasReturnValue}(IUnidad u).
	
	- Modificados UtilEjecucion._pedirArgumentosParaAlgoritmo y 
	  LoroEjecutor.ejecutarAlgoritmo para mejorar un poco el despliegue de 
	  los mensajes al usuario.
	
////////////////////////////////////////////////////////////////////////////
2002-09-25 Version 0.7.2

	- Ajustado ParseException.getMessage() para mejorar el mensaje de error
	  cuando se trata del caso de marca de gu�a:  {{ bla bla bla }}
	  
	- Nuevo Loro.getAlgorithm(String qname)
	  
////////////////////////////////////////////////////////////////////////////
2002-09-23 Version 0.7.2

	- Operador 'implementa' habilitado ahora pero s�lo para revisar si una
	  expresi�n es un algoritmo que implementa una cierta especificaci�n. (*)
	  Esta operaci�n estaba en mora de incluirse; sobre todo cuando se tienen
	  variables de tipo algoritmo gen�rico.
	  
	  Un ejemplo:
	  	alg: algoritmo := fiboIter;
		...
		si alg implementa algoritmo para fibo entonces
			f := (alg como algoritmo para fibo)(20);
		...
		
	  Ver Chequeador.visitar(NImplementa), LoroEjecutor.visitar(NImplementa)
	  
	  
	  (*) Sigue pendiente en cuanto a clases/interfaces.
	
////////////////////////////////////////////////////////////////////////////
2002-09-18 Version 0.7.2

	- Se chequea ahora que el nombre de un paquete no contenga una variable
	  sem�ntica. Actualizada la implementaci�n de ICompilador.derivarNombre()
	  y Chequador.visitar(NPaquete).
	  Tambi�n ICompilador.derivarId() no permite que sea variable sem�ntica.
	
////////////////////////////////////////////////////////////////////////////
2002-09-17 Version 0.7.2

	- Nuevo Loro.getSpecification(String qname)
	
////////////////////////////////////////////////////////////////////////////
2002-09-16 Version 0.7.2

	- Nuevos ICompilador.{derivarId,derivarNombre}() para facilitar validaci�n 
	  sint�ctica de identificadores (id <EOF>) y nombres (id1::id2::id3 <EOF>).
	  
	- NDeclaracion.toString() y NDeclDesc.toString() incluyen ahora 'constante'
	  si es del caso.
	  
	- Eliminado ICompilador.compileUnit(); ahora se tienen los servicios
	  concretos: compileSpecification, compileAlgorithm, compileClass.
	  Ver implementaci�n en CompiladorImpl.java.
	
////////////////////////////////////////////////////////////////////////////
2002-09-09 Version 0.7.2

	- Bug corregido. 'continue' dentro de 'para' no estaba actualizando
	  variable de control.
	  
	- Modificado LAmbiente.obtAlgoritmo(nombre): ahora no se declara con 
	  throws LException. M�s bien simplementa retornar� null si el algoritmo 
	  no se encuentra.
	  (La implementaci�n es LoroEjecutor.obtAlgoritmo(nombre).)

////////////////////////////////////////////////////////////////////////////
2002-08-31 Version 0.7.1

	- Control completo de posible recursi�n al obtener versi�n cadena de un
	  objeto. Ver UtilValor. Ej:
		arr : [] Objeto;
		obj : Objeto;
		arr := ["hola", "mundo'];
		obj := arr;
		arr[1] := obj;
	Si se imprime obj (o arr, pues son el mismo objeto):
		["hola",[!]]
	el s�mbolo [!] indica que se trata de un arreglo que ya se se est� procesando 
	(o ya se proces�). Para objetos que son miembros de otros objetos, se utiliza
	el s�mbolo {!}.
	
////////////////////////////////////////////////////////////////////////////
2002-08-28 Version 0.7.1

	- No hay cambios; s�lo puesta la version 0.7.1
	
////////////////////////////////////////////////////////////////////////////
2002-08-26

	- LoroIParser.jj actualizado para incluir nuevos elementos l�xicos:
		 TEXT_DOC: ''texto''
		 para cadenas de documentaci�n. Usado en la producci�n tdoc().
		 En tdoc() todav�a se acepta <LITERAL_CADENA> por compatibilidad.
	  
		 GUIDE: {{texto}}
		 Este elemento no se usa en ninguna parte de la sintaxis, as� que se
		 provocar� un error sint�ctico en donde se encuentre.
		 Es un simple mecanismo como ayuda para plantillas de c�digo.
		 
////////////////////////////////////////////////////////////////////////////
2002-08-25 (hacia 0.8.x)

	- Inicializaci�n del n�cleo es ahora:
		Loro.configurar(ext_dir, paths_dir);
	  
	  De ext_dir se toman todos sus archivos de extensi�n (*.lar, *.jar) 
	  de la manera en que se ha venido haciendo.
	  
	  De paths_dir su toman todos sus subdirectorios para incluirlos en
	  la ruta de b�squeda.
	  
	  El par�metro anterior doc_dir se ha quitado y est� pendiente completar
	  revisi�n de manejo de tal directorio desde otras interfaces como
	  IDocumentado (que parece estar debidamente preparado...)
	  
	- Agregado Loro.addDirectoryToPath(File dir).


////////////////////////////////////////////////////////////////////////////
2002-08-24 (hacia 0.8.x)

	- Quitado par�metro 'oro_dir' para Loro.configurar. 
	  El mecanismo para lograr el efecto de indicar el directorio de destino
	  para guardar compilados es:
		ICompilador compilador = Loro.obtCompilador();
		compilador.ponDirectorioDestino(oro_dir);
		
	  Compilaci�n *completa* OK (incluso proyectos clientes).
		
	CONTINUAR EN ESTA MISMA LINEA:
		- Quitar doc_dir y pasar esto a IDocumentador
		- En cuanto a dir_ext, definir una operacion completa para establecer
		  en general la "ruta de b�squeda"  !!!!!! URGENTE
	  
	
	- Ahora se revisan las expresiones que se usan como acciones:
	 * Ya que una expresi�n puede ser una acci�n, aqu� se revisa que 
	 * dicha expresi�n sea permitida. Las expresiones permitidas
	 * como acciones son:
	 * <ul>
	 * <li> NAsignacion
	 * <li> NInvocacion
	 * <li> NCrearArreglo
	 * <li> NCrearObjeto
	 * <li> NCondicion
	 * </ul>
	 * En general cualquier expresi�n puede tener efectos secundarios que
	 * deber�an respetarse. Sin embargo, s�lo se permite que la posibilidad
	 * de este efecto secundario sea a trav�s de una de las posibilidades
	 * indicadas.
	 Ver Chequeador.visitarAccion(Nodo).
	 En particular, esto controla el caso muy com�n de intentar hacer una
	 asignaci�n (:=) con el operador de igualdad (=). Antes no se reportaba
	 nada!
	
	
	- Bug corregido: obj.xxx() generaba error de invocaci�n sobre objeto nulo.
	  Este error se introdujo en los cambios preliminares para incluir manejo
	  de m�todos en donde tal invocacion se tomaba inmediatamente como
	  refiriendo a un m�todo, ignorando que podr�a ser un atributo que ha
	  sido declarado como algoritmo.
	  La situaci�n general y la soluci�n se describen a continuaci�n.
	
	- Consid�rese la siguiente declaraci�n de una clase:
	
		clase UnaClase
			...
			xxx: algoritmo para ...
			
			m�todo xxx()...
			...
		fin clase
		
	  C�mo proceder si se encuentra la siguiente invocaci�n (donde obj es
	  instancia de UnaClase):
	  
		obj.xxx()
		
	  Es xxx un "m�todo" como tal? o es un atributo declarado como "algoritmo"?
	  V�ase LoroEjecutor.visitar(NSubId n). All� se intenta siempre primero
	  resolver xxx como un atributo; si no lo es, entonces se procede a
	  resolver xxx como un m�todo pero s�lo si se est� en una invocaci�n.
	  Pendiente. (Una idea inicial es simplemente hacer que los nombres
	  de m�todos y de atributos pertenezcan al mismo espacio de nombres, en
	  donde no ser�a posible la definici�n de UnaClase como est� arriba.)
	  
////////////////////////////////////////////////////////////////////////////
2002-08-20

	- Se agreg� atributo "millis" a una unidad compilada.

////////////////////////////////////////////////////////////////////////////
2002-08-12 (hacia 0.8.x)

	- Agregada posibilidad de compilar directamente una unidad.
	  Ver: ICompilador, ChequeadorBase, CompiladorImpl
	  Estrategia: 
	  	- Control a nivel sem�ntico.
		- Nuevo m�todo setExpectedPackageName(String pkgname) para
		  establecer el paquete que debe aparecer en el c�digo fuente.
		- CompiladorImpl.compileUnit revisa que s�lo se defina UNA unidad.
	
////////////////////////////////////////////////////////////////////////////
2002-08-11 (hacia 0.8.x)

	- Manejo de fuentes asociados a las unidades:
		IOroLoader.getUnitSource
		
	- Reorganizaci�n de programas en Loro:
		- El n�cleo se conforma ahora s�lo del paquete loroI::sistema en la
		  extensi�n especial loroI.lar (que sigue almacen�ndose junto con el
		  .jar del n�cleo).
		- El paquete loroI::mat pasa a ser como extensi�n regular mat.lar
		- El paquete loroI::entsal pasa a ser como extensi�n regular entsal.lar
	  Ahora los clientes del n�cleo requerir�n adem�s del propio .jar con las
	  clases Java del n�cleo (loro-XXXX.jar), los paquetes de extensi�n.
	  En build.xml esto se establecido as�:
	  	${dist}/
		       /loro-XXXX.jar
			   /ext/*
	  El cliente LoroEDI ya est� ajustado a esto.
		  
////////////////////////////////////////////////////////////////////////////
2002-08-08 (hacia 0.8.x)

	- Redise�o del manejo de unidades a nivel de cargadores.
	  Nueva interface IOroLoader con implementaciones:
	  	CoreOroLoader        para unidades del n�cleo
		DirectoryOroLoader   para un directorio
		ZipFileOroLoader     para extensiones
	- ManejadorUnidades: esta clase se ha reescrito en lo concerniente a
	  la b�squeda de unidades compiladas de acuerdo al esquema indicado.
	- Nueva clase OroLoaderManager que maneja la lista de IOroLoader's
	  asociados a zip-files.
	  
	- ant test -> OK
	  
	
////////////////////////////////////////////////////////////////////////////
2002-08-01 (hacia 0.8.x)

	- Un primer ensayo para obtener las extensiones del n�cleo, incluyendo
	  tambi�n el lar de apoyo.
	
	- PENDIENTE: Separar paquetes que pueden quedar como extensiones
	  normales (loroI::mat, loroI::entsal).
	
////////////////////////////////////////////////////////////////////////////
2002-07-30 (0.7.0)

	- Bug corregido: No se estaba permitiendo:
		clase A ...
		...
		obj : Objeto;
		...
		obj := crear A;
		obj como A       <- error de compilaci�n
	  Ver TipoClase.esConvertibleA

	- Nuevos:  
		interface IExtension
		m�todo Loro.getExtensions()
	  Esto permite obtener informaci�n sobre las extensiones.
	  
////////////////////////////////////////////////////////////////////////////
2002-07-23

	- BUG corregido:
		LoroEjecutorBase._convertirValor NO estaba revisando el caso 'null',
		el cual debe retornarse inmediatamente tal cual.

	- Hechas algunas adiciones necesarias para el desarrollo del nuevo entorno 
	  integrado (por publicarse en versi�n 0.8.x)
	
////////////////////////////////////////////////////////////////////////////
2002-06-05 (0.7s2)

	- La ant-task loroc ahora acepta tambi�n el par�metro "save" para indicar si
	  se desean guardar o no las unidades compiladas en disco.

	- save="false" sobre test/valid/ hace fallar algunos de los fuentes 
	  que tienen interdependencias, que era lo de esperarse.
	  Pero puso en evidencia la situaci�n que se comenta m�s abajo.
	  
	- Separados los programas bajo test/valid/ en dos subdirectorios:
	
	  	test/valid/indep/
			cada fuente debe compilar bien por s� solo. Se pone save="false"
			
		test/valid/interdep/
			Los fuentes tiene alg�n interdependencia. Se pone save="true"
			
	  Actualizados: directorio test/valid/ y build-test.xml

	- save="false" delat� la siguiente situaci�n:
		Algunos fuentes que se saben "independientes" como:
			Ver: test/valid/indep/clases/forward.loro
			     test/valid/indep/analisintantico.loro
		NO est�n compilando con la opci�n save="false" !
		Aparentemente el mecanismo de caches del manejador de unidades no
		est� funcionando como deber�a !!!   PENDIENTE revisi�n
		POR AHORA: se mueven estas clases a test/valid/interdep/ mientras
		se ataca este asunto exhaustivamente. (N�tese que externamente s�lo 
		se trata de un asunto de eficiencia en la compilaci�n.)
			
- - - - - - -

	- Bug detectado y corregido:
	     Una definici�n de clase A recursiva (con un miembro de clase A), con un
		 constructor asignando al miembro: produc�a error de compilaci�n asociado 
		 a la NO resoluci�n del tipo recursivo.
	  Soluci�n: En ChequeadorBase._chequearAsignabilidad se revisa el caso en que
	  los tipos en cuesti�n est�n relacionados con la unidad actual en compilaci�n.

	- Corregido UtilEjecucion._pedirArgumentosParaAlgoritmo: desplegaba mal 
	  las cadenas de di�logo para pedir los valores.

	- Nueva clase loro.compilacion.Ubicacion (implementa IUbicable) para ofrecer
	  m�s opciones de ubicaci�n para errores. Usado ahora en Chequeador para
	  obtener la ubicaci�n que cubre el arreglo de entradas (o descripciones)
	  en constructores y especificaciones. 
	  
	- Complementado Util.quote para desplegar comillas internas seg�n sea el
	  quote (' o "). Bien.
		
	- En clase loro.Rango quitado m�todo obtPos y movido al paquete m�s apropiado
	  loro.parsers.javacc (en la clase ContructorArbol), ya que su implementaci�n
	  tiene dependencias en el modo en que opera JavaCC. La construcci�n de un 
	  Rango requiere ahora la indicaci�n de todos sus atributos (expl�citamente
	  o a trav�s de otros Rangos).
	  
	- Algunas veces se presentaba todav�a una mala ubicaci�n de un rango.
	  Revisando de nuevo en JavaCC encontr� que SimpleCharStream.UpdateLineColumn()
	  es quien hace un ajuste asociado al tabulador '\t':
	    ...
		case '\t' :
		   column--;
		   column += (8 - (column & 07));
		   break;
		...
	  Decid� comentar este fragmento y simplificar el c�lculo en el m�todo
	  ContructorArbol.obtPos() para tampoco considerar el '\t' como especial.
	  El resultado es que ahora ya se calcula siempre bien el rango!
	  Tener cuidado en un futuro ante cambios que se hagan en las clases
	  generadas del JavaCC, y tener esto del '\t' tambi�n presente para otras posibles
	  herramientas de derivaci�n que se lleguen a utilizar.
	  (Por qu� Javacc hace un manejo espacial del \t ?)
	  
	- En Loro.obtEstrategiaAlgoritmo corregido NPE al acceder a la estrategia de
	  un algoritmo que no es implementado el Loro ya que estos no incluyen cadena 
	  para  tal fin.  

	Test completo, OK.
	  
////////////////////////////////////////////////////////////////////////////
2002-06-04 (0.7s2)

	- En LoroCompilador.main corregido el valor por defecto para la opci�n -d
	  que establece el directorio de destino para los compilados: ahora s� 
	  es el directorio actual "." (como se ven�a documentando).
	  (Antes se dejaba "" provocando algunos problemas de hallazgo de unidades.)
	  
	- Actualizado InterpreteInteractivo.main para recibir par�metros en el
	  mismo estilo de LoroCompilador.	  
	  
	CONTINUANDO CON TESTING
	
	- Revisadas y corregidas la implementaciones de:
		Chequeador.visitar(NId)
		Chequeador.visitar(NNombre)
		ChequeadorBase._resolverIdEnAmbiente(NId n)
	  para evitar:
	  	- Acceder a atributos a trav�s de los identificadores de las clases
		    (sea nombres simples o compuestos);
		- _resolverIdEnAmbiente(NId n) s�lo intenta resolver el id como un
		  algoritmo.

	- Cambiado Chequeador.visitar(NId) para siempre primero intentar por la
	  tabla de s�mbolos y luego por el ambiente.
	  La tabla de s�mbolos es el �mbito m�s inmediato. (Antes se intentaba
	  primero el ambiente cuando se trataba de una invocaci�n.)
	  Una caso ilustrativo: ver test/invalid/id-iguales.loro
	  PENDIENTE: El test completo actual es OK bajo este nuevo esquema PERO
	  estar pendiente ante nuevos ejemplos.
	  
////////////////////////////////////////////////////////////////////////////
2002-06-02 (0.7s2)

**Complementaci�n del mecanismo para testing**

	- ICompilador ahora ofrece tambi�n un servicio de "anti-compilaci�n"
	  anticompilarListaArchivos(...) orientado a apoyar tareas de pruebas del
	  sistema con respecto a programas inv�lidos. 
	  Anti-compilar significa compilar en espera de alg�n error de compilaci�n
	  (aunque en esta primera versi�n a�n no se puede indicar cu�l error
	  espec�fico deber�a generarse).
	  Esto se ha habilitado a trav�s de la ant-task pero no desde el compilador 
	  de l�nea de comandos (loro.tools.LoroCompilador) pues no se hace tan
	  necesario ahora.
	  
	- Ahora se puede disponer de m�s de una sesi�n con el n�cleo Loro durante la
	  misma ejecuci�n de la m�quina virtual de Java. Cambios en: ManejadorUnidades,
	  Loro, ChequeadorBase, Tipos (principalmente control sobre la �nica instancia
	  de manejador de unidades que es compartida como atributo static en algunas
	  clases).
	  Esto permite en particular flexibilizar la invocaci�n de las ant-tasks de
	  compilaci�n y anticompilaci�n principalmente para el testing.
	  
	- Actualizados LoroCompilerTask.java, build.xml y build-test.xml.
	
	CONTINUAR CON TESTING
	  
////////////////////////////////////////////////////////////////////////////
2002-06-01 (0.7s2)

	- Actualizados build*.xml para prueba de fuentes Loro inv�lidos.
	  
	- Revisi�n general del c�digo asociado a mecanismo para interface/objetos
	  con el �nimo de reportar errores de "No implementado a�n" mientras se
	  hace un plan de implementaci�n completa para estos nuevos elementos.
	  Chequeador genera este error en los siguientes casos:
	  	- visitar(NInterface).
		- visitar(NClase) cuando se trata de definici�n de objeto.
		- visitar(NImplementa).
		- visitar(NTipoInterface).

////////////////////////////////////////////////////////////////////////////
2002-05-26 (0.7s2)  (en VAJ, y actualizado disco)

	- Pruebas adicionales para mecanismo interface/objetos:
	  Ver error en invocacion de operacion en interface-clase.loro.
	  
	- Hay problemas cuando el nombre de una variable coincide con el
	  nombre de una unidad:
	   $ foo: interace IFoo := crear Foo();
	   $ foo
	   clase Foo      <-- "foo" no deberia resolverse en la clase Foo !
	  Pendiente arreglar esto [CORREGIDO 2002-06-04]
	  
////////////////////////////////////////////////////////////////////////////
2002-05-25 (0.7s2)  (en VAJ, y actualizado disco)

	- Nuevas clases TipoInterface, NImplementa.
	  La gram�tica incluye ahora:  expr() "implementa" ntipo()
	  pero en ntipo() se ha dejado una sintaxis provisional para interfaces:
	  	"interface" tnombre()
	  mientras se complementa en una versi�n futura el nuevo manejo de objetos.
	
	- Renombrado TipoAlgoritmo por TipoEspecificacion.
	  TipoEspecificacion es m�s adecuado: una especificaci�n establece un tipo,
	  y va m�s acorde con el nuevo TipoInterface.
	  
	- Renombrado ObjetoClase por Objeto.  Objeto es m�s adecuado.
	  Nuevo m�todo: boolean Objeto.implementa(TipoInterface ti)
	  pero pendiente su implementaci�n :-)
	  Pendiente tambi�n mejorar la representaci�n de Objeto (enlace paterno,
	  mapeo de atributos/m�todos propios, etc.).
	  
	- En Chequeador.visitar(NImplementa) s�lo se verifica que los argumentos
	  correspondan a tipos interface. La verificaci�n completa se deja para
	  tiempo de ejecuci�n: LoroEjecutor.visitar(NImplementa).
	  Maneejo completo aplazado para una versi�n futura.
	  
	- Restringida la visibilidad de todos los contructores de tipos (loro.tipo.*)
	  y manejados los siguientes tipos como verdaderos "singletons" (incluyendo 
	  control como serializables--readResolve): 
	  	TipoBooleano, TipoCaracter, TipoEntero, TipoReal
		TipoCadena, TipoNulo, TipoUnit
	  
	  En general, el acceso/creaci�n de todos los tipos se hace a trav�s de
	  m�todos "static" en loro.tipo.Tipo. Este mejor dise�o permite ya
	  la siguiente optimizaci�n: 
	  	- implementar t.igual(s) como t == s. 
	  que ya se est� haciendo con los tipos "singleton" mencionados arriba. 
	  Queda pendiente (aunque no es prioritario) mejorar el manejo para
	  TipoArreglo y (subclases de) TipoUnidad.
	  
////////////////////////////////////////////////////////////////////////////
2002-05-23 (0.7s2)

	- Actualizados NInterface/NClase y sus elementos internos
	  m�todos/operaciones) en cuanto a asociaci�n necesaria en creaci�n/lectura.
	  
	- Actualizado Chequeador: ahora no se guardan operaciones/m�todos por separado
	  si pertenecen a interfaces/clases dadas.
	  
////////////////////////////////////////////////////////////////////////////
2002-05-21 (0.7s2)

	- Actualizados build*.xml (encoding="iso-8859-1").
	
////////////////////////////////////////////////////////////////////////////
2002-05-20 (0.7s2)
2002-05-19

	REVISION DEL SISTEMA DE TIPOS
	
	- Eliminados atributos 'nodos' en la jerarqu�a de tipos. Esto ha corregido
	  finalmente las referencias mutuas en el �rbol de derivaci�n.
	- Entre otros efectos positivos, esto tambi�n acelera la compilaci�n.
	  
	- 'ant test' -- OK
		
///////////////////////////////////////////////////////////////////////////
2002-05-18 (0.7s2)

	- Bug corregido: NPE al construir nodo para "contin�e".
	- Nueva clase ChequeadorBase que le sirve de base al visitante Chequeador.
	- Nueva clase LoroEjecutorBase que le sirve de base al visitante LoroEjecutor.
	- En InterpreteImpl.reiniciar agregada invocaci�n:
		ui.iniciarAsociacionesSimpleCompuesto();
	  con el �nimo de eliminar comandos previos "utiliza". Faltan pruebas.
	
	- Puesto "transient" atributo 'clase' en NConstructor, y agregado m�todo 
	  readObject() en NClase para actualizar sus constructores con respecto
	  a dicho atributo. Esto evita referencias mutuas en serializable.
	
////////////////////////////////////////////////////////////////////////////
2002-05-07

	- Complementada gram�tica para permitir "clase" u "objeto" intercambiablemente.
	  Sin embargo, para la definici�n de "objeto" se genera ahora un error del 
	  estilo "Definici�n de objeto no implementado a�n".
		
	- Actualizado parcialmente VisitanteLoroDoc.
	
////////////////////////////////////////////////////////////////////////////
2002-05-05

**Adiciones preliminares para incluir manejo de interfaces y m�todos**
Ejercicio realizado no necesariamente para hacer una implementaci�n completa
en este sentido, pero s� para tener una primera aproximaci�n que haga m�s
claro c�mo proceder en una futura versi�n.

	- Complementada gram�tica para:
		- aceptar "interfaces"
		- permitir alternativas:
			- "especificaci�n" | "operaci�n" --> para especificacion()
			-  "algoritmo" | "m�todo" --> para algoritmo()
		- lista de interfaces para una clase, y metodos
			
	- Una "interface" es una lista de operaciones (especificaciones).
	  Pendiente:
		- Ajustar chequeo de una especificaci�n dentro de una interface:
		  	- para no guardar en disco aisladamente;
			- para revisar repetici�n de nombre en la misma interface;
		- Generar documentaci�n (ahora solamente se muestra descripci�n
		  pero no se recorre la lista de operaciones).
		  
	- Pendiente para clases:
		- Ajustar chequeo de un algoritmo (m�todo) dentro de una clase:
		  	- para no guardar en disco aisladamente;
			- para revisar repetici�n de nombre en la misma clase;
		- Chequear que se implementen las operaciones de las interfaces
		- Completar revisi�n de m�todos (que son algoritmos)
		- Completar documentaci�n
	
////////////////////////////////////////////////////////////////////////////
2002-04-27

	- Bug corregido: En chequeo no se revisaban asignaciones a nuevos
	  atributos autom�ticos para arreglos (longitud, inf, sup).
	  Ver _esConstanteExpresion(NExpresion), visitar(NAsignacion)
	  en Chequeador.
	
	- Mejorado reporte de errores de ejecuci�n asociados a subindizaciones.
	  Ver _obtValorDeArreglo, _ponValorAArreglo en LoroEjecutor.
	  
////////////////////////////////////////////////////////////////////////////
2002-04-25 (0.7s1)

	- Nuevo servicio del n�cleo: Loro.verificarConfiguracion(), que se apoya
	  a su vez en nuevo servicio ManejadorUnidades.verificarUnidadesCompiladas().
	  
	- El ManejadorUnidades utiliza ahora el siguiente formato para el
	  almacenamiento de una unidad compilada en disco:
	  
		(1) String version         -- versi�n actual del n�cleo
		(2) String nombreFuente    -- nombre completo del fuente
		(3) String nombreCompleto  -- nombre completo de la unidad
		(4) NUnidad unidad         -- la unidad como tal.
	  
	  Una siguiente versi�n de Loro podr� utilizar el dato (2) para intentar una 
	  recompilaci�n autom�tica (aunque es posible que el fuente correspondiente
	  ya no se encuentre o no tenga nada que ver con la unidad).
	  Este es una soluci�n simple que permite un control aceptable de los problemas
	  de compatibilidad bajo el esquema actual de almacenamiento de unidades.
	  Sin embargo, en un futuro, este esquema podr� ser totalmente distinto (sobre
	  todo buscando eliminar la base sobre serializaci�n). 
	  
	
////////////////////////////////////////////////////////////////////////////
2002-04-23 (0.7s0)

	- Inicio de control de versiones en unidades compiladas:
		ManejadorUnidades
		NUnidad
	  Esquema preliminar:
		- Se asocia a cada unidad una versi�n de compilaci�n (que coincide ahora
		  con la version del n�cleo utilizado para hacer la compilaci�n).
		- En el archivo compilado se guarda un encabezado de datos (de tipo b�sico)
		  antes de la propia unidad. El primer dato del encabezado es (y ser�) la versi�n
		  de compilaci�n. En la actual versi�n 0.7s0 se tienen tambi�n:
		  el nombre del fuente y el nombre completo de la unidad.
		- Agregar un servicio al ManejadorUnidades (y eventualmente a la interface
		  ICargadorUnidades) para chequear unidades existentes (en archivos .oro
		  y ZIP), de tal manera que se pueda advertir al usuario en caso de problemas
		  de versi�n y/o efectuar un intento de compilaci�n autom�tica de los
		  fuentes asociados, como ayuda para la actualizaci�n.

////////////////////////////////////////////////////////////////////////////
2002-04-11 (0.7r9)

	- Agregada operaci�n ICompilador.compilarListaArchivos (y su correspondiente
	  implementaci�n en CompiladorImpl).
	  loro.tools.LoroCompilador ahora utiliza este servicio.
	  
	  La idea es preparar terreno para facilitar la compilaci�n de m�ltiples
	  archivos fuentes (por ejemplo, bajo directorios completos).
	  Esto facilitar� la implementaci�n de "proyectos" por parte de un cliente
	  (como LoroEDI).
	  
	- Nueva interface loro.ICargadorUnidades que abstrae algunas de las
	  operaciones del ManejadorUnidades para ponerlas a disposici�n del
	  cliente. Se obtiene con loro.Loro.obtCargadorUnidades().
	  La idea es que el cliente tenga el mecanismo para navegar por las
	  unidades compiladas disponibles (por jerarqu�a de paquetes por ejemplo).
	  Actualmente, LoroEDI utiliza la jerarqu�a de archivos generada por
	  el documentador para crear su despliegue jer�rquico; la idea es canalizar
	  este manejo directamente sobre las unidades, no s�lo para visualizar
	  la documentaci�n, sino para otras operaciones como ejecuci�n, edici�n
	  sobre fuente asociado, etc.
	  Pendiente: apenas en inicio.
	
////////////////////////////////////////////////////////////////////////////
2002-04-06

	- Nuevas palabras clave "objeto", "operaci�n" y "m�todo" (preparando terreno
	  para unificaci�n para POO).
	  
	- Agregado proceso en loroI::mat para elevar una base a una potencia real:
	    potr(base: real, p: real) -> res: real
		
	- Bug corregido: Se permit�a acceder a un atributo a trav�s del identificador 
	  de la clase.
	  
	- Bug corregido: Se permitia: Clase es_instancia_de Clase 
	  
	- Bug corregido: Posible compilaci�n parcial de prototipo de una especificaci�n
	  por error en el cuerpo de condiciones (pre/pos): Por ejemplo, cuando en la
	  PRE se invoca un algoritmo indefinido, la variable de salida queda sin procesar.
	  En una primera pasada, el error se permite (lo que va de acuerdo con la estrategia
	  para permitir "pendientes" dentro de un mismo fuente). Pero si m�s adelante 
	  se compila un algoritmo para esta especificaci�n, sucede que la variable de
	  salida de la especificaci�n no tiene asociado ning�n tipo (es nulo). As� que
	  se genera error de "tipos distintos".
	  Corregido: Ver Chequeador.visitar(NEspecificacion).
	  
	- Bug corregido: De nuevo se muestra posici�n [lin,col] para ubicaci�n en mensajes
	  de error. Ver Rango.

	- Bug corregido:	  
	  Situaci�n:
	    Sea e una especificaci�n;
		Sea a un algoritmo para e;
		No existe algoritmo e para e (del mismo nombre);

	  El siguiento c�digo compila bien:
	  	e : algoritmo para e := a;
	  	e(...);
		
	  pero provoca error en ejecuci�n "invocaci�n de una especificaci�n".
	  Corregido: Ver LoroEjecutor.visitar(NId n)
	  
	- Bug detectado similar:      [ver 2002-06-04]
	  Situaci�n:
	  	Similar al anterior cuando SI existe un algoritmo de nombre e pero
		para una especificaci�n distinta de e. Esto produce conflictos.
	  PENDIENTE.
	  
	- Agregado loro.tools.InterpreteInteractivo, que opera sobre i/o est�ndares.
	  Se tom� de base el interprete del LoroEDI. 
	  Acepta argumentos: -ext, -oro, -ayuda
	  Pendiente depurarse: ideas: quiza definir un filter-output general para
	  manejar mecanismo de prefijos, de tal manera que el servicio se preste
	  desde aqui (casi) completo incluso para el LoroEDI. [baja prioridad]
	  
	- Bug corregido:
	  Sucedia:
		obj: UnaClase := crear UnaClase(...);
		obj como cadena
		! Expresion de tipo 'UnaClase' no es convertible al tipo 'cadena'
	  Correccion: TipoClase.esConvertibleA(Tipo t) ahora permite tambien
	  que t sea cadena. Notese que aun falta definir operaciones relacionadas
	  con jerarquia de clases y luego con interfaces.
	  
	- Pendiente: control de mayusculas/minusculas: Por ejemplo, suponiendo
	  que exite la clase A:     [corregido 2002-06-04]
		$ a
		= clase A            -- No deberia resolverse asi.
		$ a: A               -- ok
		$ a                  
		! Variable 'a' no tiene asignacion   -- ok: primero se mira tabla simbolos.
	  Ver comentario en ManejadorUnidades._cargarUnidadDeDirectorio().
		
	
////////////////////////////////////////////////////////////////////////////
2002-03-27 (0.7r8)

	- Nueva palabra clave "como" para conversion de tipo:	
			expresion "como" ntipo
	   YA no se acepta la sintaxis:
			ntipo "(" expresion ")"
	  
	- Aumentada gramatica para aceptar expresiones arreglo, ej:
		[1,3,6,2]
	  El arreglo debe tener por lo menos una expresion.; de esta
	  manera se puede deducir el tipo. Siempre tendra subindexacion desde 0.
	  Si se requiere un arreglo vacio, el programador debera usar el mecanismo 
	  "crear" explicitamente, por ej:
	  	crear [0] entero
		
	  Todo el soporte esta listo.
	  
	- Corregidos diversos bugs sobre conversion de valores.
	
	- Bug encontrado:    [corregido seg�n testing 2002-06-04]
		$ escribir como entero
		!  Expresion de tipo 'algoritmo:null' no es convertible al tipo 'entero'
		Notese el null. Ver TipoAlgoritmo.java
		Pendiente correccion!
	
	
////////////////////////////////////////////////////////////////////////////
2002-03-21 (0.7r7)

	- Actualizado build.xml para generar el nucleo con el nombre 
	  loro-${VERSION}.jar (en esta fecha esto es loro-0.7r7.jar) y no
	  simplemente loro.jar

////////////////////////////////////////////////////////////////////////////
2002-03-19

	- Aumentada gramatica para admitir elementos "si_no_si" opcionales en una
	  decision:
	  	"si" expresion "entonces" acciones
		( "si_no_si" expresion "entonces" acciones )*          <- nuevo
		[ "si_no" acciones ]
		"fin" "si"
	  Todo el soporte esta listo.
		
	- Ahora son opcionales la palabras "descripcion" y "estrategia"
	  que van antes de la cadena correspondiente.
	- Ahora la palabra antes de la cadena de descripcion de un algoritmo puede
	  ser tambien "descripcion" ("estrategia" todavia se admite pero quiza
	  se elimine en un futuro --"descripcion" es apropiada para el efecto.).

////////////////////////////////////////////////////////////////////////////
2002-03-14

	- Nueva clase loro.util.UtilValor, encargada de factorizar algunos
	  servicios asociados a obtener versiones cadena de los valores de Loro.
	  
	- Mejorada la version cadena de la TablaSimbolos (para fines del interprete 
	  interactivo).
	  
	- TipoClase.toString se simplifica a solo el nombre de la clase, por ejemplo
	  foo::Baz (ya no ``instancia de clase foo::Baz'').
	
////////////////////////////////////////////////////////////////////////////
2002-03-13

	- Por completitud con ``crear[5..10] ...'', se agregaron 'operaciones'
	  sobre arreglos con el mecanismo punto:
		arreglo.inf        obtiene el indice inferior del arreglo
		arreglo.sup        obtiene el indice superior del arreglo
		arreglo.longitud   obtiene la longitud del arreglo
	  Tambien se hizo para las cadenas aunque para estas siempre:
	    x.inf = 0  y  x.sup = x.longitud - 1
	  
	- En LoroEjecutor se agrego un limite (longitudVerObjeto) para la version 
	  cadena de los arreglos, similar a como se ha hecho con objetos.
	  PENDIENTE: Permitir cambiar los valores de nivelVerObjeto y longitudVerObjeto,
	  digamos como preferencias y/o a traves de metacomandos en el II.
		
	- Bug corregido: Se producia error de ejecucion si un algoritmo retornaba
	  'nulo' diciendo que no habia asignacion a la variable de salida. 
	  Faltaba utilizar el servicio tabSimb.obtAsignado para saber concretamente
	  si hubo o no asignacion en LoroEjecutor.visitar(NAlgoritmo).
	
	- Bug corregido: Se producia error de compilacion si un algoritmo retornaba
	  'nulo' para una variable de salida tipo cadena.
	  Se utiliza ahora _chequearAsignabilidad() como para asignacion.
	
////////////////////////////////////////////////////////////////////////////
2002-03-10

	- OJO: Definir modelo de resolucion de referencias en unidades compiladas,
	  como los tipos. (Ahora, NTipo contiene campo Tipo y TipoClase
	  tiene campo NClase; esto provoca autoreferencias.) Esto tiene relacion 
	  directa con la estrategia "serializable" utilizada para guardar las
	  unidades: el sintoma es que las unidades se vuelven mas y mas grandes
	  a medida se recompilan!
	  
	
////////////////////////////////////////////////////////////////////////////
2002-03-07

	- Actualizado LoroCompilador: ahora es completamente un cliente del
	  sistema (solo hace import loro.*).
	  Ofrece un metodo separado compilar() con el servicio completo tanto 
	  para su propio main() como para la "ant task".
	  (Pendiente: hacer actualizacion similar para LoroDocumentador y
	  las otras herramientas en loro.tools)
	  
	- Actualizado loro.misc.ant.LoroCompilerTask: ahora incluye argumento
	  para indicar directorio de extensiones (no probado aun), e invoca el 
	  nuevo metodo LoroCompilador.compilar().
	  
	  **Pendiente:   [Disponible 2002-06-02] 
	  Completar terminacion Loro.cerrar() para corregir por
	  ejemplo lo siguiente: la tarea loroc puede invocarse SOLA una vez 
	  dentro de un "build" porque una segunda vez provoca excepciones asociadas 
	  a inicializacions estaticas del nucleo.

	- Incluidas pruebas de compilacion de fuentes en Loro: build-test.xml,
	  que usa la anttask "loroc". El subdirectorio test/ contiene
	  diversos fuentes para pruebas generales. En particular, todos los
	  fuentes bajo el subdirectorio test/valid/ deben compilar
	  exitosamente, tarea que se realiza con ``ant test-valid''.
	  
	  La estructura bajo test/ es:
	  	test/valid/      -- Deben compilar exitosamente
		                    (En test/valid/fixed/ se ponen fuentes que antes delataban
							bugs del sistema.)
		test/invalid/    -- NO deben compilar exitosamente.
		test/bugs/       -- Actualmente con problemas para Loro.
		test/ext/        -- Mecanismo de extension (implica Java--prueba "a mano").
		test/pending/    -- Con posibles nuevas caracteristicas.
		
	  Pendiente:     [Disponible 2002-06-04] 
	  Automatizar el chequeo de test/invalid/
	
////////////////////////////////////////////////////////////////////////////
2002-03-06

	- Varias pruebas de compilacion/ejecucion (despues del cambio de integrar el
	  recurso de apoyo) han sido OK.
	  Seguir haciendo pruebas (enfocadas principalmente hacia el ManejadorUnidades
	  para verificar normalizacion de todos los nombres en los caches al estilo "::").
	  
	- Ahora solo basta con actualizar build.properties sobre la version
	  del sistema para que se refleje en todas partes.
	  
	- En el build.xml se hace primero un loro-sin-apoyo.jar que involucra toda
	  la compilacion de fuentes excepto los .loro de apoyo, y luego el
	  propio loro.lar que incluye ahora el recurso de apoyo. OK.
	
////////////////////////////////////////////////////////////////////////////
2002-03-05

	- Ahora loroI.lar se maneja como un recurso dentro del mismo loro.jar
	  (ya no hay que indicarlo externamente).
	  
	- En la inicializacion del ManejadorUnidades se cargan *todas* las unidades 
	  de apoyo en el cacheZip. Funciona OK pero tenerlo presente para cuando
	  el tamano de este apoyo se vuelva considerable en un futuro (pendiente).
	  
////////////////////////////////////////////////////////////////////////////	  
2002-03-02   (Creado este archivo)
	NOTA
		Lo que venia llamandose integralmente como proyecto "Loro", ahora se
		ha dividido en el nucleo como tal, que continuara llamandose "Loro", 
		y el Entorno de Desarrollo Integrado, que ahora se llama LoroEDI.
		Las notas de desarrollo DEVNOTES.txt continuaran a cargo de LoroEDI
		hasta que se se actualice adecuadamente la informacion. (Luego
		Loro tendra su propio DEVNOTES).
	
	- La construccion del nucleo de Loro (este proyecto) ha sido completamente
	  revisada: ahora esta mejor configurado (y configurable) y se hace un
	  control completo de dependencias.
	  Vea build*
