LoroEDI README
$Id$

NOTAS
	
	- Puede crearse la siguiente confusión cuando se exporta una extensión
	  de la cual se está visualizando su documentación normalmente:
	  Haberse indicado "incluir documentación (*.html)" y sin embargo no haberse
	  exportado dicha documentación. Lo que sucede ahora es que generalmente 
	  esta documentación no se está incluyendo en las extensiones creadas
	  externamente, particularmente desde los builds*.xml (ver por ejemplo el
	  build-ext.html del núcleo), pero se tiene generada en un directorio para
	  ello.
	  

////////////////////////////////////////////////////////////////////////////
2003-05-04 (0.8pre11)

	- Bug 732522 corregido: 
	  Compilar/ejecutar demo desde ventana de edición del demo
	  compilaba/ejecutaba siempre el demo asociado al último proyecto enfocado,
	  no necesariamente el correspondiente al que está en edición.
	  Solución: En GUI se separaron tareas para compilación/ejecución de un
	  código demos dado, y los correspondientes al projecto enficado.
	  Actualizadas las actions correspondientes.
	  
	- Compilación de demo ahora opera "comando por comando" al estilo de
	  como ha venido operando Workspace.executeCommands(), es decir, se
	  compilan los fragmentos por separado. Esto permite en particular
	  no terminar cada comando con ";". (Antes de esto era posible ejecutar
	  exitosamente un demo que no compilara bien, ya que la ejecución se
	  hace por fragmentos.)
	  
	- build.xml: target "test2" asume que loroedi.jar está al día.
	
////////////////////////////////////////////////////////////////////////////
2003-05-02 (0.8pre11)

	- JTerm: Modificaciones para permitir manejo de secuencias '\b' y '\r' 
	  en cadenas escritas a través del writer. A diferencia del manejo 
	  estándar en terminales típicas (que esencialmente "mueven" el cursor
	  pero no borran), en JTerm estas secuencias sí eliminan los caracteres 
	  precedentes correspondientes, y el cursor siempre queda al final cuando
	  se trata de la última línea actualizada.
	  Algunas comparaciones de resultados:
	  	   cadena          JTerm      terminal/cursor retrasado
		   ----------      -------    --------------------------
	  	   123\b           12         123   / 1 espacio
	  	   12345\rXY       XY         XY345 / 3 espacios 
	  	   12345\b\b       123        12345 / 2 espacios
	  	   12345\r                    12345 / al comienzo
	  Esto significa que NO hay compatibilidad completa. Por ejemplo:
	  	escribir("   procesados");	
		para i:entero := 1 hasta 100 haga
		     escribir("\r" + i);
		fin para
	  funcionará bien en una terminal típica pero no en JTerm puesto que
	  se borrará la parte derecha del mensaje. (Una solución completa
	  implicaría un rediseño de JTerm que no quiero hacer por ahora.) 
	  
	  NOTA: JTerm hace System.setProperty("line.separator", "\n") con el
	  fin de proceder a procesar los "\r" encontrados entendiéndose que
	  fueron escritos por el usuario explícitamente. (Los PrintWriter's
	  utilizan esta propiedad para los cambios de línea y en sistemas
	  DOS se presentaría confusión con los \r puestos automáticamente.)
	
	- JEditTextArea: Ajustes menores control caret visible. 
	  
////////////////////////////////////////////////////////////////////////////
2003-04-28 (0.8pre11)

	- Misc: Nuevo TODO.txt

////////////////////////////////////////////////////////////////////////////
2003-04-24 (0.8pre11)

	- Ajustes InterpreterWindow..

////////////////////////////////////////////////////////////////////////////
2003-04-23 (0.8pre11)

Ejecución de demo:

	- Reorganización de código.
	- Nuevo método auxiliar GUI._createCommands(): si la primera línea empieza
	  con $, se hace manejo de "enter": lectura de Enter para proceder con
	  cada comando por parte de workspace.executeCommands().
	- JTerm ahora tiene en cuenta el estado original de editabilidad del text area.
	- constructor InterpreterWindow recibe ahora también indicador editable
	  para el text area.
	- Workspace.executeCommands() pone no-editable la ventana de interpretación
	  y hace manejo de "enter" para cada comando a ejecutar.
	- InterpreterWindow.interpret() hace manejo de "enter" y permite también 
	  meta-comandos.
	  (Nota: Manejo de meta-comandos actualmente en revisión desde el núcleo.)
	
////////////////////////////////////////////////////////////////////////////
2003-04-16 (0.8pre11)

	- Algo de limpieza en GUI.java, Workspace.java: import de HiloAlgoritmo
	  ya no usados allí.
	
	- Bug 722895 corregido: Instalación de proyecto no sobreescribe pre-existente.
	  Solución: Efectivamente sí se actualizaba el directorio correspondiente
	  pero no se actualizaba la asociación nombre-prjm en el workspace y
	  entonces se seguía accediendo al prjm viejo durante la misma sesión.
	  Workspace.addProjectModelName(name) ahora siempre actualiza la
	  asociación en name_prj para que el nombre refleje el proyecto cargado.
	
////////////////////////////////////////////////////////////////////////////
2003-04-15 (0.8pre11)

	- Sólo cambio en build.properties para nueva versión y nuevo
	  enlace con núcleo 0.7.91.
	
////////////////////////////////////////////////////////////////////////////
2003-04-08 (0.8pre10)

	- Nueva preferencia PRJ_EXTERN_LAST para recordar el último proyecto
	  externo instalado.
	  
////////////////////////////////////////////////////////////////////////////
2003-03-31 (0.8pre10)

	- bug descrito en 2003-03-13 aún seguía ;-)
	  Workspace.saveDemoScript() ahora verifica que exista el código demo.
	  Corregido.
	
	- Revisión de Workspace.importSource()
	
////////////////////////////////////////////////////////////////////////////
2003-03-25 (0.8pre10)

	- Nuevo método GUI._saveMainIndexForProjects() que genera el index.html
	  principal con la lista de proyectos en el espacio de trabajo.
	  Externamente aún no se notifica al usuario sobre esto (pendiente
	  definir manejo). Este index.html lo uso ahora para el enlace
	  http://loro.sf.net/apiloro/
	  
////////////////////////////////////////////////////////////////////////////
2003-03-21 (0.8pre10)

	- Ahora se puede indicar si la descripción de un proyecto contiene o no
	  marcadores HTML. Por defecto (y para proyectos preexistentes) esta
	  propiedad no está establecida, por lo que la descripción se toma
	  como texto plano que se encierra entre <pre> ... </pre>.
	  Los tags @{...} siempre se procesan independientemente de si la
	  descripción está o no en HTML para seguir permitiendo los enlaces
	  correspondientes.
	  Principales ajustes en:
	  	IProjectModel.IInfo
	  	ProjectModel.Info
		Workspace
		GUI._editProperties()
		GUI._saveProjectDoc()
	  
////////////////////////////////////////////////////////////////////////////
2003-03-19 (0.8pre10)

	- GUI._saveProjectDoc: verificación de valores null en campos de 
	  descripción.
	  
	- Workspace.compileProjectModel(): si no hay unidades, se retorna
	  inmediatamente.
	  Pendiente: generar un aviso al usuario "no hay unidades definidas
	  en este proyecto" (ahora se obtiene "compilación bien!).
	
	- Mejorada inicialización de código en creación de algoritmo.
	  - Se simplifican nombres cuando el algoritmo está en mismo paquete
	    de la especificación.
	  - Incluye corrección de bug 701386 que generaba:
	  	utiliza especificación <nombre-simple-de-unidad-en-paquete-automático>
	  Ver MUtil.setSourceCodeTemplate().
	
////////////////////////////////////////////////////////////////////////////
2003-03-16 (0.8pre9)

Instalación de proyecto desde enlace .lar en página web:

	- Incluida navegación de página para elección de proyecto en el
	  comando "Instalar". GUI._chooseProjectInPage().
	- Ajustes menores en BrowserPanel.
	- Nueva preferencia para ventana de navegación.
	
////////////////////////////////////////////////////////////////////////////
2003-03-15 (0.8pre9)

	- Bug corregido: Al tratar de instalar un proyecto externo no
	  existente se generaba un NPE.
	  Ver en GUI._installProject() el chequeo del valor de retorno
	  de installFromURL().
	  GUI._installProject(): quitado printStackTrace innecesario.
	
////////////////////////////////////////////////////////////////////////////
2003-03-14 (0.8pre9)

	- Incluido procesamiento de inline tags @{...} en descripción
	  de proyectos para la generación de HTML. Ver:
		  GUI._saveProjectDoc()
	  
////////////////////////////////////////////////////////////////////////////
2003-03-13 (0.8pre9)

	- Bug corregido: Al compilar proyecto sin demo se podría generar un
	  error de compilación por tratar de compilar "null" como si fuera
	  el código fuente del demo. (En realidad, este "null" se estaba
	  guardando primero como código del demo, y en una sesión posterior
	  se presentaba el error.)
	  Ver GUI.createDemoEditor() y GUI._compileDemo().
	
////////////////////////////////////////////////////////////////////////////
2003-03-11 (0.8pre9)

  Documentación HTML:
  
	- La página para documentación de cada proyecto ahora incluye
	  la lista de paquetes (y sus contenidos).
	  Ver GUI._saveProjectDoc() y nuevo método GUI._getPackagesDoc().
	  
	- GUI.showProjectDocumentation() ahora hace _saveProjectDoc, de tal
	  manera que el comando de documentación del proyecto le permite
	  al usuario actualizar completamente la página del proyecto.
	  
////////////////////////////////////////////////////////////////////////////
2003-03-10 (0.8pre9)

	- UEditor: cambio menor para más consistencia con el patrón 
	  "null object" para el UEditorListener. 
	
////////////////////////////////////////////////////////////////////////////
2003-02-15 (0.8pre8)

	- Nueva preferencia I_RECT para ventana de interpretación destinada 
	  a ejecución de algoritmo/demo específico, tal que se diferencie de
	  la ventana de Interpretación Interactiva, cuyo código es II_RECT.
	
////////////////////////////////////////////////////////////////////////////
2003-02-14 (0.8pre8)

	- UEditor listener se inicializa ahora con un UEditorListener que no 
	  hace nada (un "null object"--see Patterns in Java by Grand); 
	  así, ObservadorPP.init no tiene que asociar ningún listener.
	
////////////////////////////////////////////////////////////////////////////
2003-02-13 (0.8pre8)

	- Ejecución paso-a-paso
		- Ahora se muestra un aviso al usuario cuando se está esperando 
		  una entrada de datos (lectura). 
	  	- Problema: A veces "pasar" en un nodo interno hace regresar a un
		  nivel menor y además propagarse su efecto más de lo
		  debido. En gran parte corregido, pero hay que probar y corregir
		  parejas enter/exit para mantener nivel de identación.
		- Problema: A veces se resalta mal cuando recién se regresa de
		  la invocación de un algoritmo. Debe definirse manejo repetido
		  exit() vs. pop().
	
	- GUI: Más ajustes menores en GUI.init() y en el splash.

////////////////////////////////////////////////////////////////////////////
2003-02-12 (0.8pre8)

Ajustes cosméticos.

	- GUI: Ahora se muestran mensajes de estado de inicio en el splash window.
	
	- GUI: Si al iniciar el entorno se tiene un proyecto vacío, se despliega
	  un mensaje de guía para el usuario refiriendo las opciones de
	  "abrir" e "instalar".
	  
	- GUI: Se despliega un mensaje justo al abrirse un proyecto.
	
	- Asociación de ícono (icon.jpg) a las diferentes ventanas.
	
	- GUI.init: Ahora se carga configuración y preferencias antes 
	  de procesar L&F y lo demás.
	  
	- Primeras pruebas con CompiereLookAndFeel (compiere.org) que
	  se ve interesante. Queda entre comentarios. Pendiente.
	  Pero por ahora sigo prefiriendo Kunststoff :-).
	
////////////////////////////////////////////////////////////////////////////
2003-02-11 (0.8pre8)

Ejecución paso-a-paso

	- ObservadorPP ahora utiliza el nuevo servicio del núcleo
	  Loro.getNodeDescription(INodo n) para describir cada nodo
	  durante la ejecución. También se hace un despliegue con
	  anidamiento en el área de mensajes de seguimiento.
	  
	- ObservadorPP ahora se encarga de hacer toda la sincronización
	  para el seguimiento. Para esto, se ayuda con una clase static
	  interna PPControl. Se hicieron los cambios correspondientes
	  en InterpreterWindow.actionPerformed.
	  
	- ObservadorPP hace editor.select(r.obtPosFin(), r.obtPosIni()),
	  es decir, en orden inverso de posiciones, para que el scroll
	  de visualización muestre el comienzo del fragmento de código. 
	  
	- MessageArea ahora hace un control simple para prevenir una
	  sobrepetición de memoria, lo que podría suceder al hacer 
	  seguimiento a cualquier ejecución prolongada (por ejemplo
	  si se llama fiboRecur(10) (!)
	  Una mejor implementación utilizaría alguna estrategia de
	  buffer circular para mantener las ultimas n líneas escritas
	  en el área de mensajes. Pendiente.
	  
////////////////////////////////////////////////////////////////////////////
2003-02-10 (0.8pre8)

Ejecución paso-a-paso

	- Ahora UEditorListener.execute recibe boolean trace.
	  Con esto UEditor agrega nueva opción ejecutar paso-a-paso.
	  Actualizadas clases implementadoras de UEditorListener.
	  
	- MessageArea ahora con font "monospaced".

	- Deshabilitación de botones "paso" y "continuar" mientras se lee
	  una línea en InterpreterWindow.readLine(), que es el mecanismo
	  utilizado para completar línea de ejecución ante entradas y/o
	  salidas del algoritmo. (Como estaba antes, se producía un 
	  congelamiento del controlpp cuando se hacía loroii.nextStep(),
	  o sea, se presionaba "paso".)
	  
	- Reasignación de rectángulos por defecto para organizar disposición
	  de las diferentes ventanas. Se definieron nuevas preferencias
	  para ventanas: edicion de fuente en seguimiento y edición del
	  código de demostración.
	  
	
////////////////////////////////////////////////////////////////////////////
2003-02-09 (0.8pre8)

Ejecución paso-a-paso

	- Agregada funcionalidad de cambio de color de selección en los
	  editores, que ObservadorPP utiliza para distinguir entre la
	  entrada y la salida de un nodo en el seguimiento.
	  
	- Actualizado ObservadorPP según cambios en IObservadorPP.
	
////////////////////////////////////////////////////////////////////////////
2003-02-06 (0.8pre8)

Ejecución paso-a-paso

	- Mejor sincronizacion según cambios en IObservadorPP.java
	  del núcleo.
	
	- Ajustes a SymbolTableWindow para diferenciar según si es
	  instancia para top-level o para ámbito en curso.
	  
	- Asociado el texto del comando en intérprete como source
	  para la NUnidadInterprete. De esta manera se muestra hora
	  el código fuente en el editor de seguimiento del comando 
	  ingresado para ejecución.
	  
	- Nuevas acciones para ejecución paso-a-paso de algoritmo (Shift-F9)
	  y demo (Shift-M).
	
	- Nueva preferencia SYMTAB_TRACE_RECT para la ventana de declaraciones
	  del ámbito en curso.
	  
////////////////////////////////////////////////////////////////////////////
2003-02-05 (0.8pre8)

Ejecución paso-a-paso

	- Nueva clase loroedi.ObservadorPP (implements IObservadorPP) para atender
	  los eventos asociados al seguimiento.
	  
	- Se abre ahora un editor (sólo visualización) para mostrar el código
	  fuente de la unidad en seguimiento, con el resaltado del nodo siendo
	  visitado.
	  
	- Se abre ahora un SymbolTableWindow para mostrar el estado de declaraciones
	  de la unidad en seguimiento.
	  (SymbolTableWindow dejó de ser "singleton" para este propósito.)
	
////////////////////////////////////////////////////////////////////////////
2003-02-04 (0.8pre8)

	- Manejo de IObservadorPP según adición en el núcleo.
	  En proceso de complemento...
	  
////////////////////////////////////////////////////////////////////////////
2002-12-05 (0.8pre8)

	- InterpreterWindow: Nuevos comandos para ejecución dependiendo de si 
	  loroii.isTraceable(). En construcción. 
	
////////////////////////////////////////////////////////////////////////////
2002-11-27 (0.8pre8) Enlazando con nuevo núcleo 0.7.7

	- Resaltado de: "interfaz".  Ver LoroTokenMarker.java.
	
////////////////////////////////////////////////////////////////////////////
2002-11-21 (0.8pre7)

	- Resaltado de: "lance", "intente", "atrape", "siempre" de acuerdo con adiciones
	  al núcleo 0.7.6.  Ver LoroTokenMarker.java.
	
	- Bug corregido: Un proyecto nuevo no se estaba agregando a la lista de
	  proyectos disponibles al actualizarse en disco. 
	  Corrección: en Workspace.saveProjectModel se actualiza esta información.
	  
////////////////////////////////////////////////////////////////////////////
2002-11-18 (0.8pre6)

	- Complementando opción "instalar proyecto" para incluir proveniencia
	  externa: web o sistema local de archivos.
	  Ver GUI.installProject()
	  
	  Descripción general: (ver también ajustes en el núcleo 0.7.5).
	  	Cada proyecto se empaqueta en un archivo .lar . Al instalar un
		proyecto xxx.lar, éste se ubica bajo el directorio de proyectos
		de trabajo prs_dir/. Luego se expande para generar el directorio
		prs_dir/xxx/ y se agrega este nombre xxx a la lista de proyectos
		disponibles.
		Los archivos .lar que se encuentren bajo lib/ext/ se siguen
		accediendo directamente, o sea, no se expanden, y por lo tanto
		continúan como proyectos no-modificables.
		NOTA: Los proyectos "incluidos" siguen por ahora en forma de
		directorios, pero queda pendiente tenerlos como *.lar para
		que se sometan a un manejo similar al efectuar su instalación.
	
////////////////////////////////////////////////////////////////////////////
2002-11-17 (0.8pre6)

	- Ctrl-M accelerator to "ejecutar demo".
	
////////////////////////////////////////////////////////////////////////////
2002-11-07 (0.8pre5)

Completada opción "exportar proyecto":

	- Ya opera también cuando el proyecto a exportar es una extension.
	
	- Nuevas operaciones en IProjectModel:
		IOroLoader getOroLoader()
		setOroLoader(IOroLoader)
		
	- Ahora se confirma con el usuario si el destino de la exportación ya existe.
	
////////////////////////////////////////////////////////////////////////////
2002-10-30 (0.8pre5)

Avances en opción "exportar proyecto"
	
	- GUI.exportProject() ahora ofrece un diálogo del siguiente estilo:
		Destino:
				(x) Archivo Proyecto    (o sea, a .lar)
				( ) Directorio  
		Incluir en la exportación:
				[x] Fuentes (*.loro)
				[x] Compilados (*.oro)
				[x] Documentación (*.html)
				
	- Workspace.exportProjectModel() actualizado para operar hacia archivo
	  extensión (.lar) o a directorio.
	  Pendiente completarlo: por ejemplo, sólo opera si el proyecto a
	  exportarse viene de directorio (no de extension).
	  
	- loroedi.Util._managePreference() pone System.getProperty("user.home")
	  como directorio inicial para diálogos de elección de archivos/dirs.
	
////////////////////////////////////////////////////////////////////////////
2002-10-25 (0.8pre5)

	- Implementados "compile demo", "reload demo source"
		UEditor.getTitle()
		GUI.compileDemo()
		
	- GUI.compileProject() actualizado para incluir el demo.
	
	- UEditor: nuevos argumentos executable y doc en constructor, y nueva
	  acción "execute".
	
	- UEditorListener: nueva operación execute()
	
	- GUI._compileDemo() chequea si aún no hay demo para simplemente
	  retornar true.
	  
	- GUI.installProject() ahora compila el proyecto recién instalado y
	  abierto. La idea es que los archivos generados de un proyecto (como
	  .oro, html) no deberían incluirse en la distribución del sistema, y
	  más bien generarse una vez instalado el proyecto.
	  Este manejo se complementará cuando se incluya la instalación desde
	  otras proveniencias.
	
	
////////////////////////////////////////////////////////////////////////////
2002-10-23 (0.8pre5)

	- Modificado TextAreaPainter.nextTabStop para verificar tabSize > 0
	  antes de dividir. Por alguna razón (quizá sincronización) a veces
	  tabSize llegaba en cero al invocarse este método produciéndose un
	  ArithmeticException. 
	  
	- Nueva opción "Editar demo" complementaria de "Ejecutar demo".
	  Ahora se guarda el código en prj.demo.lsh bajo el directorio
	  del proyecto correspondiente.
	  Este código se edita a través de un UEditor.
	  Esto ya es funcional pero queda pendiente:
	  	- compilación
		- quitar action "view doc"
		- atender "reload"
	  
////////////////////////////////////////////////////////////////////////////
2002-10-22 (0.8pre5)

	- Utilizando núcleo 0.7.4
	
	- Algunos ajustes por nuevo parámetro para indicar si se quiere una
	  tabla de símbolos nueva o compartida para los intérpretes:
	     InterpreterWindow
		 Workspace.executeCommands
		 
	- Creando nueva funcionalidad para definición/ejecución de "guiones".
	  Por ahora se tiene sólo una opción "Ejecutar demo" que ejecuta
	  el fragmento de código encerrado entre líneas ``.inicio'' y
	  ``.fin'' de la descripción del proyecto. PROVISIONAL
	  Ver:
	  	GUI.runDemo() 
		Actions
		
	
////////////////////////////////////////////////////////////////////////////
2002-10-20 (0.8pre5)

	- Bug corregido: No se estaba agregando el listener del workspace a un
	  proyecto recién creado. Por lo tanto, no se guardaban adecuadamente
	  paquetes creados. Ver Workspace.getNewProjectModel().
	
////////////////////////////////////////////////////////////////////////////
2002-10-09 (0.8pre4)

	- Bug corregido: Al crear un algoritmo para una especificacion en el
	  paquete automatico, por ejemplo PRUEBA, se ponía el nombre completo en 
	  la plantilla (lo cual no está mal en principio), pero al compilar se 
	  obtenía el mensaje "se espera especificación PRUEBA", es decir, se
	  esperaba el nombre en versión simple. 
	  Solución: Se modificó plantilla para que ponga nombre simple en caso
	  de que la especificación corresponda al paquete automático.
	  Un riesgo de esto es la posible confusión con un nombre simple en el 
	  mismo paquete del algoritmo. PENDIENTE --queda en prueba.
	  
	- Bug corregido: Al crear una nueva unidad, no se estaba guardando en
	  disco inmediatamente, así que se perdía la plantilla inicial en caso
	  de terminar la sesión sin actualizar el código fuente.
	
////////////////////////////////////////////////////////////////////////////
2002-10-06 (0.8pre3)

	- Mejorado Workspace.compileProjectModel para el manejo de pasadas.
	  Se tiene en cuenta ahora si hay progreso pasada tras pasada.
	  Un error de tipo sintáctico (DerivacionException) detiene
	  *inmediatamente* la compilación. Para errores semánticos se tiene: 
	  puesto que cada unidad compilada exitosamente no vuelve a compilarse
	  en el ciclo de pasadas, el criterio ahora para detener una compilación 
	  con errores es que no se haya hecho ningún progreso de una pasada a la 
	  
////////////////////////////////////////////////////////////////////////////
2002-10-06 (0.8pre3)

	- Complementado manejo relacionado con Workspace._setupPackageFromLoader.
	  El título de la ventana para el proyecto inválido se completa con
	  "incompatible". Además, en la edición de propiedades se da una explicación
	  más detallada de la situación. A pesar que las unidades compiladas son
	  inaccesibles, es posible visualizar la documentación y/o ver el código
	  fuente del proyecto siempre que la extensión incluya esta información.
	
	- Project.createAlgorithm complementado para chequear indicación de la
	  especificación teniendo que exista y esté compilada.

////////////////////////////////////////////////////////////////////////////
2002-10-03 (0.8pre3)

	- Workspace._setupPackageFromLoader(IOroLoader, ...) actualizado para construir
	  elementos con base en los nombres de las unidades aunque las unidades
	  compiladas no puedan obtenerse.

////////////////////////////////////////////////////////////////////////////
2002-10-02 (0.8pre3)

	- Nuevo SymbolTableWindow: ventana que muestra la tabla de símbolos del 
	  entorno de ejecución.
	  Esta se actualiza desde cualquiera de los intérpretes activos por
	  nuevas declaraciones o borrados.
	  También se actualiza por cualquier compilación que comprometa las
	  declaraciones existentes. Esto se hace ahora sólo a un primer nivel,
	  es decir, se eliminan las variables cuyo tipo esté relacionado
	  directamente con la unidad que se acaba de recompilar (exitosamente o
	  con errores).
	  Un control más completo incluiría la revisión no sólo de los tipos
	  declarados sino también de los *valores* asignados a dichas variables. 
	  Por ejemplo, una declaración de variable de clase Baz con asignación de
	  instancia que es SubBaz, y luego se altera SubBaz; o un algoritmo
	  genérico al que se le asigna un algoritmo particular; ... etc.  Pendiente.
	  
////////////////////////////////////////////////////////////////////////////
2002-10-01 (0.8pre3)

	- Nuevo JTerm.setInitialStringToRead(String s), que se usa en
	  InterpreterWindow.readLine(String s). Esto permite inicializar la
	  cadena de lectura con el nombre del algoritmo, ver Workspace.executeAlgorithm.
	  Asi se logra que el usuario pueda editar toda la línea de comando, por
	  ejemplo para ir al principio y hacer una asignación del resultado del
	  algoritmo.
	
////////////////////////////////////////////////////////////////////////////
2002-09-29 (0.8pre3)

	- Nuevo InterpreterWindow con funcionalidad similar a la de Interprete
	  pero orientado a la ejecución programática de acciones (no de manera
	  interactiva en principio).
	  
	- Con este elemento se hace la implementación de la opción "probar proyecto" 
	  de tal manera que todos los algoritmos a probar se prueben en la misma ventana
	  Ver: GUI.testProject(), InterpreterWindow, Workspace.executeCommands().
	  
	- También se actualizó la misma ejecución regular de algoritmos con base
	  en InterpreterWindow.
	  
////////////////////////////////////////////////////////////////////////////
2002-09-25 (0.8pre3)

	- Implementadas las opciones "Crear esquema para pruebas" (para
	  especificación) y "Probar" (para algoritmo). 
	  Ver Project.createTestForSpecification(), GUI.testAlgorithm(),
	  MUtil.getSourceCodeTemplate{Spec,Alg}Test().
	  
////////////////////////////////////////////////////////////////////////////
2002-09-20 (0.8pre3)

	- Diagram: En cada paquete ahora se ubican de forma contigua los algoritmos 
	  que implementen la misma especificación. En general se hace un ordenamiento
	  lexicográfico por el nombre simple de cada familia de unidades (specs, 
	  algoritmos, clases). En el caso de los algoritmos, éste es el ordenamiento
	  cuando se trata de la misma especificación; pero en caso contrario, se
	  utilizan los nombres de las especificaciones correspondiente para decidir 
	  la comparación.
	  Ver Diagram.setupModel(). 
	  
	- Se hace confirmación antes de salir del entorno. Ver GUI.quit().
	
	- En listas de selección (para abrir/instalar proyecto) ahora se actualiza
	  el enfoque ante cambios en la selección.
	
////////////////////////////////////////////////////////////////////////////
2002-09-19 (0.8pre3)

	- Bug corregido:
		Diagram: El comando 'recargar proyecto' hacía que quedaran listeners
		repetidos asociados al modelo, lo cual provocaba la repetición de
		elementos que se agregaran en el diagrama. Esto también se presentaba
		después de eliminar un paquete ya que esto hace un llamado a Diagram.reset()
		lo cual siempre agrega un cierto listener al modelo. Corrección: el atributo 
		listeners en ProjectModel es ahora un java.util.Set (antes una List), así se
		garantiza que no puede haber listeners repetidos.
		
		
////////////////////////////////////////////////////////////////////////////
2002-09-18 (0.8pre3)

	- MUtil.setSourceCodeTemplate complementado para incluir 'utiliza' en
	  el caso de un algoritmo que está en un paquete con nombre y que
	  implementa una especificación en el paquete anónimo. (El paquete
	  anónimo no es visible automáticamente desde otros paquetes.)
	  
	
////////////////////////////////////////////////////////////////////////////
2002-09-17 (0.8pre3)

	- Renombrado ProjectRepository por Workspace (incluso en este readme).
	
	- Habilitada la escogencia de una especificación teniendo en cuenta
	  todos los proyectos del espacio de trabajo.
	  Ver:
		Project.createAlgorithm()
		Project.chooseSpecification()
		Workspace.getAvailableSpecifications()
		GUI.getAllSpecs()
	  Nótese que GUI.getAllSpecs() obtiene *todas* las especificaciones como 
	  base para verificar lo que se escriba en el campo para el nombre de la
	  especificación; mientras que en Project.chooseSpecification() se vuelven
	  a obtener *todas* las especificaciones pero agrupadas por proyectos
	  para efectos de los submenúes correspondientes. 
	  Puede que esto sea un poco redundante pero funciona bien :-)
	
////////////////////////////////////////////////////////////////////////////
2002-09-16 (0.8pre3)

	- Utilizados nuevos servicios compilador.derivarNombre() y compilador.derivarId()
	  como forma de validar nombres e identificadores para la creación de paquetes,
	  especs, algoritmos y clases.
	  Ver ProjectModel.validateName(String name), PackageModel.validateId(String name).
	  Nótese el chequeo para evitar posible comentario.
	  
	- Ajustes necesarios hechos para revisar completamente los elementos esperados
	  al compilar dependiendo del tipo concreto de unidad.
	  Ver Workspace._compileUnit(), LoroControl.compile*().
	  
////////////////////////////////////////////////////////////////////////////
2002-08-31 (0.8pre2)
	
	- Complementando mecanismo sobre documentación de los proyectos, desde
	  que se ejecuta por primera vez el sistema (versión actual), y se
	  instalan nuevos proyectos posteriormente.
	  Ver GUI._firstTime()
	  
////////////////////////////////////////////////////////////////////////////
2002-08-28 (0.8pre2)

	- Puesta versión 0.8pre2 (sobre núcleo 0.7.1)
	
	- No sólo se genera documentación en doc_dir, sino también bajo el directorio
	  del respectivo proyecto. Ver GUI.docUnit() y flag docInProjectDirectory.
	  NOTA: Por ahora se hace así siempre.
	  Esto puede degradar un poco la respuesta del sistema ante compilaciones,
	  pero facilita posteriores operaciones de instalación/exportación de
	  proyectos. Además la degradación no parece tan significativa como para
	  ameritar un mecanismo más sofisticado (por lo menos para el mediano plazo).
			
	- Al instalar un proyecto xxx:
		- se copia todo xxx/** al directorio a PRS_DIR/xxx/ (como se venía haciendo)
		- se copia todo xxx/**.html al directorio DOC_DIR/
		
	**Actualización de proyectos de demostración**
	Aprovechando el esquema indicado, se procede de la siguiente manera:
	
		1) ant instalador       # armar instalador
		2) cd DIST/base_instalador/bin
		3) java -cp loroedi.jar loroedi.gui.Main -prsdir ../../../base_dist/lib/prs/
			# para abrir, modificar y (re)compilar directamente a los proyectos de
			# demostración que se requieran actualizar.
		4) cd ../../../
		5) ant instalador    # para hacer el instalador final.
		
	Está pendiente hacer la (re)compilación y (re)generación de doc automáticamente 
	desde build.xml, quizá complementando la ant-task loroc.
	
		
////////////////////////////////////////////////////////////////////////////
2002-08-27

	- Habilitada la visualización de documentación.
	  Se está creando el .html para proyecto en GUI.compileProject()
	  con _saveProjectDoc().
	  Pendiente: Cómo mantener esta información? por ej, cuando se instala
	  un proyecto, la documentación *debe* venir incluida y entonces
	  instalarse bajo doc_dir (así como se instala el resto bajo prs_dir).
	  Ante actualizaciones en el entorno, esta info ya se está actualizando.
	  
	- Actualizado BrowserPanel principalmente para permitir "refresco" de
	  URL actual ante cambios por nueva compilación (sea de unidad o de
	  proyecto completo).
	  Vea BrowserPanel.refresh() y GUI.refreshDocIfNecessary().
	  
	  NOTE: Se está generando un ArrayIndexOutOfBoundsException que parece ser
	  ser por algún problema de sincronización. El refresco termina bien pero
	  estar pendiente de esto.
	  
	- Cambiado color para comentarios /*...*/ para que se diferencien del nuevo
	  componente léxico ''...''
	
////////////////////////////////////////////////////////////////////////////
2002-08-26

	- En "compilar proyecto" se guardan previamente las unidades que posiblemente se
	  encuentren con modificaciones.
	  
	- Nuevo menú "Ejecución" en proyecto.
	
	- Complementados menúes en proyecto y unidad.
	
	- Resaltado de nuevos elementos léxicos TEXT_DOC y GUIDE.
		TEXT_DOC, ''texto'', se resalta con color especial para cadenas de documentación
		de forma distinta a LITERAL_CADENA, lo cual resulta mucho más claro y menos confuso.
		
		GUIDE, {{ texto }}, se utiliza en las plantillas de código para indicar 
		puntos a ser remplazados.
		
	  Actualizados MUtil y algunos fuentes en loroedi.jedit.*
	  
////////////////////////////////////////////////////////////////////////////
2002-08-25

	- Utilizando versión modificada de inicialización del núcleo:
		Loro.configurar(ext_dir, paths_dir);
	  y su nuevo servicio:
	  	Loro.addDirectoryToPath(File dir)
	  para cuando se crea un nuevo proyecto y para cuando se instala un proyecto.
	  Ver GUI._initCore().
	  
	- Mejorado diálogo para "Instalar" proyecto. Incluye opción para
	  abrirlo inmediatamente.
	  
	  
////////////////////////////////////////////////////////////////////////////
2002-08-24

	- Mejorada forma de llevar a cabo tareas de compilación:
		- actualización de áreas de mensaje tanto en unidades abiertas como
		  en el proyecto.
	  	- Se abre el editor correspondiente a una unidad que presenta
	  	  problema de compilación. 
	
	- Actualizados todos los ejemplos (anteriormente subdirectorio demo/)
	  como proyectos bajo lib/prs/ , los cuales quedan disponibles como
	  proyectos "instalables".
	  Actualizado también build.xml, target "test-demos".
	
////////////////////////////////////////////////////////////////////////////
2002-08-22

	- Opción "Importar" funcionando. Ahora sólo acepta un archivo fuente
	  procediendo a adicionar las unidades que alli se encuentren.
	  El requerimiento es que dicho fuente debe compilar exitosamente
	  para poder completar la importación. En un futuro se podría hacer
	  esto un poco más flexible: quizá aceptar algunos errores y aún así
	  cargar las unidades definidas en el fuente. Más posibilidades
	  incluyen la importación de múltiples fuentes.
	  La carga desde un archivo zip (o lar) podría dejarse mejor para
	  la opción "Instalar".
	
////////////////////////////////////////////////////////////////////////////
2002-08-21

	- Nueva opción "Instalar" que permite habilitar un proyecto "incluido"
	  de manera que quede disponible como un proyecto regular. Los proyectos
	  incluidos se encuentran bajo la distribución en lib/prs/ .
	  Instalar significa copiar un directorio de alli, digamos lib/prs/xxx
	  al directorio del usuario loro/prs/ que es en donde se buscan proyectos 
	  para abrir.
	
////////////////////////////////////////////////////////////////////////////
2002-08-20
	- Workspace:
		Source y CompiledUnit puestos a las unidades cuando se carga
		un proyecto.
		
////////////////////////////////////////////////////////////////////////////
2002-08-17

	- Revisados nombres "esperados" de paquete y de unidad. 
	
////////////////////////////////////////////////////////////////////////////
2002-08-15

	- Guardados elementos en disco tan pronto el usuario los crea.
	
	- Mejorada implementación del patrón "observer":
	  Los siguientes son "listeners" sobre un IProjectModel:
	  	- Diagram
		- Workspace
	- Toda adición/eliminación de elementos en un proyecto es reflejada en disco
	  inmediatamente.
	  
////////////////////////////////////////////////////////////////////////////
2002-08-14

	- Nueva interface ICell para mantener información con el diagrama a nivel
	  de celdas y facilitar por ejemplo el borrado de elementos.
	  
	- Ya se borran elementos (unidades y paquetes).
	  Un elemento que tenga dependencias (por ejemplo una espec que tenga algoritmos
	  o un paquete no vacío) no puede ser borrado.
	  La ventana de edición de una unidad también es eliminada en caso de que exista.
	  Los archivos correspondientes también son borrados: .oro, .loro, .html para
	  unidades, y los pkg.props para paquetes.
	  
	- Se atienden teclas Delete, Enter y Ctrl-F9 en el diagrama.
	
////////////////////////////////////////////////////////////////////////////
2002-08-12

	- IProjectUnit ahora incluye asociación con IUnidad, la cual se establece
	  en compilación y en carga, y se obtiene para ejecución (y después para
	  documentación posiblemente).
	  
	- Empezando compilación tanto de unidades individuales como del proyecto
	  completo.
	  
	- Workspace.compileProjectModel ahora hace una compilación
	  "ordenada" buscando minimizar el riesgo de dependencias mutuas:
	  	- todas las clases
	  	- todas las especs
	  	- todas los algoritmos
	  pero esta solución es incompleta --sigue pendiente diseñar un sistema
	  completo de dependencias para compilación.
	  
////////////////////////////////////////////////////////////////////////////
2002-08-11

	- Ajustes a build.xml para reflejar nuevo esquema de manejo de extensiones
	  definidos desde el mismo núcleo.
	  
	- Nuevo subdirectorio src/prs/ para poner "proyectos" a incluir en
	  la distribución. Ahora se tienen:
	  	src/prs/Adivina     -- adivinadores
	  Este esquema remplazará al tradicional subdirectorio "demo" --pendiente.
	  
	- loroedi.gui.Main recibe ahora un argumento opcional para indicar el
	  directorio de base para proyectos, lo cual facilita el desarrollo de
	  estos proyectos para posterior distribución.
	  
	- Primeras pruebas de compilación.
	  Ver Workspace.
	
////////////////////////////////////////////////////////////////////////////
2002-08-08

	- Modificaciones hechas en Workspace de acuerdo con el rediseño 
	  actual del núcleo en cuanto a IOroLoader's.
	  
	- ant test ; ant test2 ; ant test2   -> OK
	  
	  
////////////////////////////////////////////////////////////////////////////
2002-08-04

	- Workspace.exportProjectModel:
		Empezando a operar, por ahora a un directorio.
		

////////////////////////////////////////////////////////////////////////////
2002-08-03

	- Workspace.saveProjectModel:
		Ya se guardan los paquetes: por cada paquete su pkg.props
		así como los código fuente de cada unidad.
		
	- Nuevo Project.EditorListenerImpl para atender las acciones
	  sobre una ventana de edición de una unidad.
		
	- Diagram ahora tiene en cuenta las asociaciones spec-algorithm
	  de tal manera que si una especificación es agregada *después*
	  del algoritmo, puede hacer la conexión correspondiente.

////////////////////////////////////////////////////////////////////////////
2002-08-01

	- Inicio de Workspace.saveProjectModel: ahora guarda sólo
	  las propiedades generales prj.props
		
	- "name" NO es más una de las propiedades dentro de prj.props:
	  ahora se toma directamente o bien el nombre del archivo de extensión
	  o bien el nombre del directorio dentro de PRS_DIR.
	  Esto permite hacer un manejo estilo código para dicho nombre de tal
	  manera que se puede hacer una identificación de cada proyecto.

////////////////////////////////////////////////////////////////////////////
2002-07-31

	- En "Crear paquete" campo en blanco permite crear el paquete anónimo.

	- Nuevas Preferencias: PRJ_RECENT, PRJ_RECT, PRS_DIR
	
	- Organización de proyectos bajo PRS_DIR:
		PRS_DIR/
		       my_project/
		                 prj.props
		                 prj.description
						 
		                 pkg.props              -- del paquete anónimo
		                 pkg.description
		                 unit.c.loro  -- source
		                 /foo/bar/
		                         pkg.props
		                         pkg.description
		                         unit.c.loro  -- source
		                         unit.c.oro   -- compiled
		                         unit.c.html  -- API
									
	- prj.props contiene propiedades generales de un proyecto.
	- pkg.props indica las unidades definidas dentro del paquete.
	- pkg.description: descripción más larga del proyecto.
	

////////////////////////////////////////////////////////////////////////////
2002-07-30 

	- Publicación como Versión 0.7.0.
	  Los cambios etiquetados abajo como "hacia 0.8pre1" aún no son
	  públicos. La GUI sigue siendo la acostumbrada.

////////////////////////////////////////////////////////////////////////////
2002-07-30 (hacia 0.8pre1)

	- Workspace está obteniendo ya información sobre extensiones 
	  a través de nuevo servicio del núcleo.
	
////////////////////////////////////////////////////////////////////////////
2002-07-26 (hacia 0.8pre1)

	- Nuevo menu "Ventana". Ahora permite navegar entre las ventanas
	  asociadas a proyectos.
	  
	- Atendido acción "Nuevo" para crear un nuevo proyecto.
	
////////////////////////////////////////////////////////////////////////////
2002-07-25 (hacia 0.8pre1)

	- Agregados controles sobre acciones habilitadas dependiendo de si
	  el proyecto enfocado es modificable o no.
	  
////////////////////////////////////////////////////////////////////////////
2002-07-24 (hacia 0.8pre1)

	- Nueva clase GUI que hace el control general.
	  Siempre hay por lo menos un proyecto abierto, que será el proyecto
	  "base" en caso de "nuevo" proyecto o justo después de cerrarse
	  el último proyecto. Ver GUI.closeProject()
	  
////////////////////////////////////////////////////////////////////////////
2002-07-18 (hacia 0.8pre1)

	- Diagram.MyJGraph.MyView.update() sobreescritura para controlar 
	  posición vertical de los vértices.

	- Posicionamiento inicial simple pero aceptable.
	  
////////////////////////////////////////////////////////////////////////////
2002-07-13 (hacia 0.8pre1)

	- Mejorando el diseño:
		- ProjectModel
		- PackageModel
		- Mejor esquema de eventos

////////////////////////////////////////////////////////////////////////////
2002-07-11 (hacia 0.8pre1)

	- Nueva interface ProjectUnit con clases SpecificationUnit, AlgorithmUnit
	  y ClassUnit, y ajustes generales alrededor de estos elementos.
	- Mejoramiento de diálogos.
	
////////////////////////////////////////////////////////////////////////////
2002-07-10 (hacia 0.8pre1)

	- Nuevo ProjectDialog para petición de datos con un primer mecanismo para
	  permitir validación inmediata. Gracias a AbstractProjectModel, se está
	  validando ahora que un nuevo elemento (espec o algoritmo) no vaya a
	  tener un nombre repetido. Esto por ahora dentro del mismo proyecto.
	  Pendiente: definir los chequeos generales.
	  
	- Diagram:
		- Primera versión simple para ubicación automática de nuevos elementos
		  incluyendo conexión algoritmo->especificación.
		- anti-aliasing on; dashPattern = {4, 2}; Bezier line style.
	
////////////////////////////////////////////////////////////////////////////
2002-07-08 (hacia 0.8pre1)

	- Inicio de atención de eventos
	- Inicio de ubicación de nodos en grafo
	- AbstractProjectModel con funcionalidades básicas (por ejemplo, manejo
	  de los ProjectModelListener's).
	- NewProjectModel (subclase de AbstractProjectModel) será el proyecto
	  a despachar cuando se entre por primera vez al EDI o se escoja la
	  opción "Nuevo (proyecto)".
	  Primera idea sobre esto: Ante la primera modificación que vaya a
	  guardarse, se pedirá al usuario un nombre concreto para el proyecto.
	
////////////////////////////////////////////////////////////////////////////
2002-07-06 (hacia 0.8pre1)

	- Nueva clase Diagram destinada a visualizar (jgraph) las relaciones
	  de un ProjectModel.
	  Por ahora solo muestra un diagrama simple de prueba.
	  
	- Incluido jgraph.jar en la compilacion (build.*) y en la distribucion.

	Para ejecución de pruebas:
		java -cp loroedi.jar loroedi.gui.project.NewProjectModel
		

////////////////////////////////////////////////////////////////////////////
2002-07-04 (hacia 0.8pre1)

	El esquema inicial es:
		- Una ventana de despacho para proyectos conteniendo los paquetes/unidades
		  que conforman el proyecto, incluyendo un grafo y un área de ejecución 
		  interactiva.
		  
		- Una ventana de edición para cada unidad particular.
		
	- loroedi.gui.editor
	  Manejo del editor de unidad:
	  	UEditor              El editor como tal
	  	UEditorListener      interface para clientes de UEditor
		UEditorListenerImpl  Una implementación sencilla.  tiene main de prueba
		
	- loroedi.gui.project
	  Manejo de proyectos:
	  	Project              Ventana para un projecto
	  	ProjectModel         Datos para el proyecto

***********************************************
* Diseño de interface gráfica integrada       *
* Paquete de base:  loroedi.gui               *
* Fecha de inicio: 2002-07-04                 *
***********************************************


////////////////////////////////////////////////////////////////////////////
2002-05-26 (0.7s2)

	- Actualizado LoroControl.verificarConfiguracionNucleo para considerar
	  interfaces y objetos (aunque el manejo de estos elementos aún está
	  incompleto en la versión 0.7s2 del núcleo).
	  
////////////////////////////////////////////////////////////////////////////
2002-05-21 (0.7s2)

	- Incluido Kunststoff Look&Feel (http://www.incors.com), que se establece 
	  si la propiedad LOROEDI_PREF_LAF NO está definida (es decir, éste será el
	  L&F por defecto). Si la propiedad sí está definida y es igual a "ignore",
	  entonces no se establece explícitamente ningún L&F (queda el que ponga
	  el sistema Java); en otro caso, se interpreta el valor como el nombre de un 
	  "theme" para SkinLF: primero tomándolo directamente y, si no existe, se 
	  intenta prefijando el directorio (nuevo) lib/themes/ bajo la instalación 
	  de LoroEDI.  
	  
	  Ver loroedi.laf.LookAndFeel.
	  
	  Actualizados build.properties y build.xml
	  
	  Bajo lib/ se está incluyendo ahora entonces para la distribución:
		kunststoff.jar  (nuevo)
		skinlf.jar (desde hace algún tiempo aunque sin publicación)
	  	themes/aquathemepack.zip (aunque tiene algunos bugs leves)
	  
	- Agregado comando .version al intérprete interactivo para mostrar información
	  sobre versión del sistema (LoroEDI y núcleo Loro).
	  
	- Enlazando con núcleo 0.7s2.
	
////////////////////////////////////////////////////////////////////////////
2002-05-05 (0.7s2)

	- Actualizado Splash para mostrar versión del LoroEDI, y ajustados
	  algunos constructores en LoroEDI y EDI para que el "parent" del
	  splash sea la ventana principal del entorno.
	  
	- Actualizado DocNodeFactory para crear entradas para posibles
	  unidades: interfaces, objetos, clases, especificaciones, algoritmos.
	  
////////////////////////////////////////////////////////////////////////////
2002-04-25

	- loroedi.help.BrowserPanel.Hyperactive.hyperlinkUpdate() ahora atiende
	  también eventos ENTERED y EXITED con el fin de mostrar la dirección
	  del enlace sobre el que está apuntando el ratón.
	  El mensaje de error cuando un enlace no es visualizable incluye la
	  dirección correspondiente.
	
////////////////////////////////////////////////////////////////////////////
2002-04-25 (0.7s1)

	- Nuevo método loroControl.verificarConfiguracionNucleo() para hacer uso
	  de nuevo servicio de Loro.verificarConfiguracion().
	  Este método se invoca en LoroEDI.despacharPrimeraVez(), y también a
	  través de una nueva opción "Verificar sistema" bajo el menú "Ayuda".
	
////////////////////////////////////////////////////////////////////////////
2002-04-15

	- Nuevo comando .gc para el intérprete.
	
////////////////////////////////////////////////////////////////////////////
2002-04-11 (0.7r9)

	- Cuando se instala una nueva versión del sistema, en particular, cuando
	  el formato de las unidades compiladas ha cambiado (ver núcleo), cómo
	  actualizar esto para el usuario?
	  
	  En la primera ejecución de la nueva versión, se conocería la 
	  incompatibilidad (por comparación de versión, o a mano, etc.)
	  Entonces se mostraria al usuario un mensaje estilo:
	  
	  	Es necesario volver a compilar sus programas para que sean 
		compatibles con esta versión del sistema.
		Loro procederá con esta recompilación automáticamente.
		                       [aceptar]
							   
	  Pero ahora no hay forma de saber cuáles archivos fuentes compilar:
	  aunque las unidades previas contienen esta información, no es
	  posible leerlas por la incompatibilidad de la serialización.
	  Esto es lo que se quiere arreglar para las próximas versiones!

	- Idea: Mostrar nuevo elemento gráfico para la "navegación de unidades",
	  similar a como sucede ahora con la documentación, pero permitiendo
	  otras operaciones, como "ejecutar" cuando se trata de algoritmos,
	  "instanciar" para clases, "crear nuevo algoritmo" para especificaciones,
	  etc.
	
////////////////////////////////////////////////////////////////////////////
2002-04-05

	- Actualizado LoroTokenMarker para resaltar nuevas palabras clave
	  "objeto", "operación" y "método".
	- Enlazando con versión 0.7r9 del núcleo.
	
	- Actualizado build.xml con nuevo target "test2" que hace una nueva
	  pasada para posible resolución de dependencias. El target "test"
	  siempre borra primero todos los compilados previos posibles para
	  iniciar una prueba completa.
	  
	- Actualizados algunos documentos de distribución.
	
	- Corregido bug: Ante error sintáctico/léxico al puro final del fuente,
	  no se llevaba el curso hasta allí. Ver Editor.select().
	  
	- Pendiente: Activar siempre el logger en algún archivo predeterminado.
	  Esto puede requerir una nueva API del núcleo (quien solo activa el logger
	  si encuentra la propiedad del sistema "loro.log").
	
////////////////////////////////////////////////////////////////////////////
2002-03-27 (0.7r8)

	- Actualizado LoroTokenMarker para resaltar nueva palabra clave "como".
	- Enlace con nueva version 0.7r8 del nucleo.
	- Revisados los ejemplos de demostracion de acuerdo con los cambios de
	  gramatica en Loro.

////////////////////////////////////////////////////////////////////////////
2002-03-21 (0.7r7)

	- Actualizados build.properties y build.xml ya que el nombre del JAR del
	  nucleo Loro viene ahora como loro-XXXX.jar donde XXXX es su version.
	  La nueva propiedad es 'nucleo.jar'.
	  Se opto tambien por generar desde el build.xml el 'manifest' para
	  loroedi.jar (ya que incluye la indicacion del nucleo en su classpath.)

////////////////////////////////////////////////////////////////////////////
2002-03-19 (0.7r7)

	- Actualizado LoroTokenMarker para resaltar nueva palabra clave "si_no_si".

////////////////////////////////////////////////////////////////////////////
2002-03-14

	- Nuevos comandos en Interprete Interactivo:
		.verobj nivel - Pone maximo nivel para visualizar objetos
		.verarr long  - Pone maxima longitud para visualizar arreglos
	  (ahora todos los metacomandos empiezan con punto . para unificar)

////////////////////////////////////////////////////////////////////////////
2002-03-07

	- Incluidas pruebas de compilacion de fuentes Loro de demostracion que
	  se incluyen en la distribucion:
		ant test
	  quiza haya que ejecutar esto mas de una vez (para resolver dependencias).
	  
////////////////////////////////////////////////////////////////////////////
2002-03-06

	- No mas manejo del archivo de "apoyo" de Loro, que ahora ha quedado
	  controlado internamente por ese sistema.
	
	- Ahora solo basta con actualizar build.properties sobre la version
	  del sistema para que se refleje en todas partes.
	  
////////////////////////////////////////////////////////////////////////////
2002-03-02   (Creado este archivo)
	NOTA
		Lo que venia llamandose integralmente como proyecto "Loro", ahora se
		ha dividido en el nucleo como tal, que continuara llamandose "Loro", 
		y este proyecto, LoroEDI, el Entorno de Desarrollo Integrado.
		Las notas de desarrollo DEVNOTES.txt continuaran a cargo de LoroEDI
		hasta que se actualice adecuadamente la informacion. (Luego
		Loro tendra su propio DEVNOTES).
	
	- La construccion de LoroEDI (este proyecto) ha sido completamente
	  revisada: ahora esta mejor configurada (y configurable) y se hace un
	  control completo de dependencias, y construccion del instalador
	  con base en el nanoInstaller.
	  Vea build*
