LoroEDI README

PENDIENTES (m�s o menos en orden de prioridad)
	
	- Preparar los proyectos "incluidos" como archivos *.lar en la
	  distribuci�n del sistema.  (Ver opci�n "instalar")
	  
	- Guiones en Loro
	  En principio, la idea es restringir los guiones s�lo para fines de
	  demostraciones asociados a los proyectos. 
	  Un proyecto puede tener un guion de demostraci�n asociado.
	  Una demostraci�n es una lista de acciones para ser interpretadas
	  inmediatamente con resultado similar a si dichas acciones las
	  escribiera el usuario directamente.
	  Extendiendo el esquema, se abarcar�a la posibilidad de ejecutar
	  "cualquier" gui�n no necesariamente asociado ning�n proyecto en
	  particular.
	  Un gui�n (digamos, script.lsh) no est� asociado a ning�n paquete
	  en particular y no puede incluir la definici�n de ninguna unidad;
	  s�lo acepta las acciones aceptadas por un int�rprete.
	  
  	- Al compilar proyecto, despachar las posible lista de errores.
	  La idea es mantener un s�lo error por unidad, pero si varias
	  unidades tienen problemas, entonces s� permitir abrir los editores
	  correspondientes. Puede ser as�: si s�lo una unidad tiene problema,
	  entonces abrir autom�ticamente su editor; si son varias unidades
	  con errores, entonces disponer de un componente gr�fico con la
	  lista, y desde all� permitir abrir los editores deseados.
	
  	- Abrir editor ante error de ejecuci�n 
	
	- imprimir diagrama, c�digo fuente.
	  
	- Hacer m�s flexible manejo de oro-loaders:
			cambiar orden de busqueda, quitar, etc.
		
		Workspace.{compileProjectModel, compileUnit, executeAlgorithm}
		hacen compiler.ponDirectorioDestino(prj_dir.getAbsolutePath()); pero
		en el caso de executeAlgorithm esto es para hacer que el directorio
		se incluya en *busqueda*. El punto es que esto tambi�n estar�a
		incluido en un redise�o del esquema de b�squeda general.

	- Renovar/reasignar �conos de la GUI.
		
	- Asociar un "grado de complejidad" a cada proyecto de manera que se
	  puedan mostrar esta informaci�n al usuario cuando quiera instalar/abrir
	  un proyecto. Podr�a ser algo como "bajo", "intermedio", "alto".
	  
	- hacer Manejo del atributo "millis" en unidad compilada. Esto para
	  poder comparar entre archivo fuente y archivo compilado: el
	  fuente leyendo lastModified del archivo, y la unidad compilada
	  leyendo su "millis"

	  
NOTAS
	
	- Puede crearse la siguiente confusi�n cuando se exporta una extensi�n
	  de la cual se est� visualizando su documentaci�n normalmente:
	  Haberse indicado "incluir documentaci�n (*.html)" y sin embargo no haberse
	  exportado dicha documentaci�n. Lo que sucede ahora es que generalmente 
	  esta documentaci�n no se est� incluyendo en las extensiones creadas
	  externamente, particularmente desde los builds*.xml (ver por ejemplo el
	  build-ext.html del n�cleo), pero se tiene generada en un directorio para
	  ello.
	  
////////////////////////////////////////////////////////////////////////////
2003-02-12 (0.8pre8)

Ajustes cosm�ticos.

	- GUI: Ahora se muestran mensajes de estado de inicio en el splash window.
	
	- GUI: Si al iniciar el entorno se tiene un proyecto vac�o, se despliega
	  un mensaje de gu�a para el usuario refiriendo las opciones de
	  "abrir" e "instalar".
	  
	- GUI: Se despliega un mensaje justo al abrirse un proyecto.
	
	- Asociaci�n de �cono (icon.jpg) a las diferentes ventanas.
	
	- GUI.init: Ahora se carga configuraci�n y preferencias antes 
	  de procesar L&F y lo dem�s.
	
////////////////////////////////////////////////////////////////////////////
2003-02-11 (0.8pre8)

Ejecuci�n paso-a-paso

	- ObservadorPP ahora utiliza el nuevo servicio del n�cleo
	  Loro.getNodeDescription(INodo n) para describir cada nodo
	  durante la ejecuci�n. Tambi�n se hace un despliegue con
	  anidamiento en el �rea de mensajes de seguimiento.
	  
	- ObservadorPP ahora se encarga de hacer toda la sincronizaci�n
	  para el seguimiento. Para esto, se ayuda con una clase static
	  interna PPControl. Se hicieron los cambios correspondientes
	  en InterpreterWindow.actionPerformed.
	  
	- ObservadorPP hace editor.select(r.obtPosFin(), r.obtPosIni()),
	  es decir, en orden inverso de posiciones, para que el scroll
	  de visualizaci�n muestre el comienzo del fragmento de c�digo. 
	  
	- MessageArea ahora hace un control simple para prevenir una
	  sobrepetici�n de memoria, lo que podr�a suceder al hacer 
	  seguimiento a cualquier ejecuci�n prolongada (por ejemplo
	  si se llama fiboRecur(10) (!)
	  Una mejor implementaci�n utilizar�a alguna estrategia de
	  buffer circular para mantener las ultimas n l�neas escritas
	  en el �rea de mensajes. Pendiente.
	  
////////////////////////////////////////////////////////////////////////////
2003-02-10 (0.8pre8)

Ejecuci�n paso-a-paso

	- Ahora UEditorListener.execute recibe boolean trace.
	  Con esto UEditor agrega nueva opci�n ejecutar paso-a-paso.
	  Actualizadas clases implementadoras de UEditorListener.
	  
	- MessageArea ahora con font "monospaced".

	- Deshabilitaci�n de botones "paso" y "continuar" mientras se lee
	  una l�nea en InterpreterWindow.readLine(), que es el mecanismo
	  utilizado para completar l�nea de ejecuci�n ante entradas y/o
	  salidas del algoritmo. (Como estaba antes, se produc�a un 
	  congelamiento del controlpp cuando se hac�a loroii.nextStep(),
	  o sea, se presionaba "paso".)
	  
	- Reasignaci�n de rect�ngulos por defecto para organizar disposici�n
	  de las diferentes ventanas. Se definieron nuevas preferencias
	  para ventanas: edicion de fuente en seguimiento y edici�n del
	  c�digo de demostraci�n.
	  
	
////////////////////////////////////////////////////////////////////////////
2003-02-09 (0.8pre8)

Ejecuci�n paso-a-paso

	- Agregada funcionalidad de cambio de color de selecci�n en los
	  editores, que ObservadorPP utiliza para distinguir entre la
	  entrada y la salida de un nodo en el seguimiento.
	  
	- Actualizado ObservadorPP seg�n cambios en IObservadorPP.
	
////////////////////////////////////////////////////////////////////////////
2003-02-06 (0.8pre8)

Ejecuci�n paso-a-paso

	- Mejor sincronizacion seg�n cambios en IObservadorPP.java
	  del n�cleo.
	
	- Ajustes a SymbolTableWindow para diferenciar seg�n si es
	  instancia para top-level o para �mbito en curso.
	  
	- Asociado el texto del comando en int�rprete como source
	  para la NUnidadInterprete. De esta manera se muestra hora
	  el c�digo fuente en el editor de seguimiento del comando 
	  ingresado para ejecuci�n.
	  
	- Nuevas acciones para ejecuci�n paso-a-paso de algoritmo (Shift-F9)
	  y demo (Shift-M).
	
	- Nueva preferencia SYMTAB_TRACE_RECT para la ventana de declaraciones
	  del �mbito en curso.
	  
////////////////////////////////////////////////////////////////////////////
2003-02-05 (0.8pre8)

Ejecuci�n paso-a-paso

	- Nueva clase loroedi.ObservadorPP (implements IObservadorPP) para atender
	  los eventos asociados al seguimiento.
	  
	- Se abre ahora un editor (s�lo visualizaci�n) para mostrar el c�digo
	  fuente de la unidad en seguimiento, con el resaltado del nodo siendo
	  visitado.
	  
	- Se abre ahora un SymbolTableWindow para mostrar el estado de declaraciones
	  de la unidad en seguimiento.
	  (SymbolTableWindow dej� de ser "singleton" para este prop�sito.)
	
////////////////////////////////////////////////////////////////////////////
2003-02-04 (0.8pre8)

	- Manejo de IObservadorPP seg�n adici�n en el n�cleo.
	  En proceso de complemento...
	  
////////////////////////////////////////////////////////////////////////////
2002-12-05 (0.8pre8)

	- InterpreterWindow: Nuevos comandos para ejecuci�n dependiendo de si 
	  loroii.isTraceable(). En construcci�n. 
	
////////////////////////////////////////////////////////////////////////////
2002-11-27 (0.8pre8) Enlazando con nuevo n�cleo 0.7.7

	- Resaltado de: "interfaz".  Ver LoroTokenMarker.java.
	
////////////////////////////////////////////////////////////////////////////
2002-11-21 (0.8pre7)

	- Resaltado de: "lance", "intente", "atrape", "siempre" de acuerdo con adiciones
	  al n�cleo 0.7.6.  Ver LoroTokenMarker.java.
	
	- Bug corregido: Un proyecto nuevo no se estaba agregando a la lista de
	  proyectos disponibles al actualizarse en disco. 
	  Correcci�n: en Workspace.saveProjectModel se actualiza esta informaci�n.
	  
////////////////////////////////////////////////////////////////////////////
2002-11-18 (0.8pre6)

	- Complementando opci�n "instalar proyecto" para incluir proveniencia
	  externa: web o sistema local de archivos.
	  Ver GUI.installProject()
	  
	  Descripci�n general: (ver tambi�n ajustes en el n�cleo 0.7.5).
	  	Cada proyecto se empaqueta en un archivo .lar . Al instalar un
		proyecto xxx.lar, �ste se ubica bajo el directorio de proyectos
		de trabajo prs_dir/. Luego se expande para generar el directorio
		prs_dir/xxx/ y se agrega este nombre xxx a la lista de proyectos
		disponibles.
		Los archivos .lar que se encuentren bajo lib/ext/ se siguen
		accediendo directamente, o sea, no se expanden, y por lo tanto
		contin�an como proyectos no-modificables.
		NOTA: Los proyectos "incluidos" siguen por ahora en forma de
		directorios, pero queda pendiente tenerlos como *.lar para
		que se sometan a un manejo similar al efectuar su instalaci�n.
	
////////////////////////////////////////////////////////////////////////////
2002-11-17 (0.8pre6)

	- Ctrl-M accelerator to "ejecutar demo".
	
////////////////////////////////////////////////////////////////////////////
2002-11-07 (0.8pre5)

Completada opci�n "exportar proyecto":

	- Ya opera tambi�n cuando el proyecto a exportar es una extension.
	
	- Nuevas operaciones en IProjectModel:
		IOroLoader getOroLoader()
		setOroLoader(IOroLoader)
		
	- Ahora se confirma con el usuario si el destino de la exportaci�n ya existe.
	
////////////////////////////////////////////////////////////////////////////
2002-10-30 (0.8pre5)

Avances en opci�n "exportar proyecto"
	
	- GUI.exportProject() ahora ofrece un di�logo del siguiente estilo:
		Destino:
				(x) Archivo Proyecto    (o sea, a .lar)
				( ) Directorio  
		Incluir en la exportaci�n:
				[x] Fuentes (*.loro)
				[x] Compilados (*.oro)
				[x] Documentaci�n (*.html)
				
	- Workspace.exportProjectModel() actualizado para operar hacia archivo
	  extensi�n (.lar) o a directorio.
	  Pendiente completarlo: por ejemplo, s�lo opera si el proyecto a
	  exportarse viene de directorio (no de extension).
	  
	- loroedi.Util._managePreference() pone System.getProperty("user.home")
	  como directorio inicial para di�logos de elecci�n de archivos/dirs.
	
////////////////////////////////////////////////////////////////////////////
2002-10-25 (0.8pre5)

	- Implementados "compile demo", "reload demo source"
		UEditor.getTitle()
		GUI.compileDemo()
		
	- GUI.compileProject() actualizado para incluir el demo.
	
	- UEditor: nuevos argumentos executable y doc en constructor, y nueva
	  acci�n "execute".
	
	- UEditorListener: nueva operaci�n execute()
	
	- GUI._compileDemo() chequea si a�n no hay demo para simplemente
	  retornar true.
	  
	- GUI.installProject() ahora compila el proyecto reci�n instalado y
	  abierto. La idea es que los archivos generados de un proyecto (como
	  .oro, html) no deber�an incluirse en la distribuci�n del sistema, y
	  m�s bien generarse una vez instalado el proyecto.
	  Este manejo se complementar� cuando se incluya la instalaci�n desde
	  otras proveniencias.
	
	
////////////////////////////////////////////////////////////////////////////
2002-10-23 (0.8pre5)

	- Modificado TextAreaPainter.nextTabStop para verificar tabSize > 0
	  antes de dividir. Por alguna raz�n (quiz� sincronizaci�n) a veces
	  tabSize llegaba en cero al invocarse este m�todo produci�ndose un
	  ArithmeticException. 
	  
	- Nueva opci�n "Editar demo" complementaria de "Ejecutar demo".
	  Ahora se guarda el c�digo en prj.demo.lsh bajo el directorio
	  del proyecto correspondiente.
	  Este c�digo se edita a trav�s de un UEditor.
	  Esto ya es funcional pero queda pendiente:
	  	- compilaci�n
		- quitar action "view doc"
		- atender "reload"
	  
////////////////////////////////////////////////////////////////////////////
2002-10-22 (0.8pre5)

	- Utilizando n�cleo 0.7.4
	
	- Algunos ajustes por nuevo par�metro para indicar si se quiere una
	  tabla de s�mbolos nueva o compartida para los int�rpretes:
	     InterpreterWindow
		 Workspace.executeCommands
		 
	- Creando nueva funcionalidad para definici�n/ejecuci�n de "guiones".
	  Por ahora se tiene s�lo una opci�n "Ejecutar demo" que ejecuta
	  el fragmento de c�digo encerrado entre l�neas ``.inicio'' y
	  ``.fin'' de la descripci�n del proyecto. PROVISIONAL
	  Ver:
	  	GUI.runDemo() 
		Actions
		
	
////////////////////////////////////////////////////////////////////////////
2002-10-20 (0.8pre5)

	- Bug corregido: No se estaba agregando el listener del workspace a un
	  proyecto reci�n creado. Por lo tanto, no se guardaban adecuadamente
	  paquetes creados. Ver Workspace.getNewProjectModel().
	
////////////////////////////////////////////////////////////////////////////
2002-10-09 (0.8pre4)

	- Bug corregido: Al crear un algoritmo para una especificacion en el
	  paquete automatico, por ejemplo PRUEBA, se pon�a el nombre completo en 
	  la plantilla (lo cual no est� mal en principio), pero al compilar se 
	  obten�a el mensaje "se espera especificaci�n PRUEBA", es decir, se
	  esperaba el nombre en versi�n simple. 
	  Soluci�n: Se modific� plantilla para que ponga nombre simple en caso
	  de que la especificaci�n corresponda al paquete autom�tico.
	  Un riesgo de esto es la posible confusi�n con un nombre simple en el 
	  mismo paquete del algoritmo. PENDIENTE --queda en prueba.
	  
	- Bug corregido: Al crear una nueva unidad, no se estaba guardando en
	  disco inmediatamente, as� que se perd�a la plantilla inicial en caso
	  de terminar la sesi�n sin actualizar el c�digo fuente.
	
////////////////////////////////////////////////////////////////////////////
2002-10-06 (0.8pre3)

	- Mejorado Workspace.compileProjectModel para el manejo de pasadas.
	  Se tiene en cuenta ahora si hay progreso pasada tras pasada.
	  Un error de tipo sint�ctico (DerivacionException) detiene
	  *inmediatamente* la compilaci�n. Para errores sem�nticos se tiene: 
	  puesto que cada unidad compilada exitosamente no vuelve a compilarse
	  en el ciclo de pasadas, el criterio ahora para detener una compilaci�n 
	  con errores es que no se haya hecho ning�n progreso de una pasada a la 
	  
////////////////////////////////////////////////////////////////////////////
2002-10-06 (0.8pre3)

	- Complementado manejo relacionado con Workspace._setupPackageFromLoader.
	  El t�tulo de la ventana para el proyecto inv�lido se completa con
	  "incompatible". Adem�s, en la edici�n de propiedades se da una explicaci�n
	  m�s detallada de la situaci�n. A pesar que las unidades compiladas son
	  inaccesibles, es posible visualizar la documentaci�n y/o ver el c�digo
	  fuente del proyecto siempre que la extensi�n incluya esta informaci�n.
	
	- Project.createAlgorithm complementado para chequear indicaci�n de la
	  especificaci�n teniendo que exista y est� compilada.

////////////////////////////////////////////////////////////////////////////
2002-10-03 (0.8pre3)

	- Workspace._setupPackageFromLoader(IOroLoader, ...) actualizado para construir
	  elementos con base en los nombres de las unidades aunque las unidades
	  compiladas no puedan obtenerse.

////////////////////////////////////////////////////////////////////////////
2002-10-02 (0.8pre3)

	- Nuevo SymbolTableWindow: ventana que muestra la tabla de s�mbolos del 
	  entorno de ejecuci�n.
	  Esta se actualiza desde cualquiera de los int�rpretes activos por
	  nuevas declaraciones o borrados.
	  Tambi�n se actualiza por cualquier compilaci�n que comprometa las
	  declaraciones existentes. Esto se hace ahora s�lo a un primer nivel,
	  es decir, se eliminan las variables cuyo tipo est� relacionado
	  directamente con la unidad que se acaba de recompilar (exitosamente o
	  con errores).
	  Un control m�s completo incluir�a la revisi�n no s�lo de los tipos
	  declarados sino tambi�n de los *valores* asignados a dichas variables. 
	  Por ejemplo, una declaraci�n de variable de clase Baz con asignaci�n de
	  instancia que es SubBaz, y luego se altera SubBaz; o un algoritmo
	  gen�rico al que se le asigna un algoritmo particular; ... etc.  Pendiente.
	  
////////////////////////////////////////////////////////////////////////////
2002-10-01 (0.8pre3)

	- Nuevo JTerm.setInitialStringToRead(String s), que se usa en
	  InterpreterWindow.readLine(String s). Esto permite inicializar la
	  cadena de lectura con el nombre del algoritmo, ver Workspace.executeAlgorithm.
	  Asi se logra que el usuario pueda editar toda la l�nea de comando, por
	  ejemplo para ir al principio y hacer una asignaci�n del resultado del
	  algoritmo.
	
////////////////////////////////////////////////////////////////////////////
2002-09-29 (0.8pre3)

	- Nuevo InterpreterWindow con funcionalidad similar a la de Interprete
	  pero orientado a la ejecuci�n program�tica de acciones (no de manera
	  interactiva en principio).
	  
	- Con este elemento se hace la implementaci�n de la opci�n "probar proyecto" 
	  de tal manera que todos los algoritmos a probar se prueben en la misma ventana
	  Ver: GUI.testProject(), InterpreterWindow, Workspace.executeCommands().
	  
	- Tambi�n se actualiz� la misma ejecuci�n regular de algoritmos con base
	  en InterpreterWindow.
	  
////////////////////////////////////////////////////////////////////////////
2002-09-25 (0.8pre3)

	- Implementadas las opciones "Crear esquema para pruebas" (para
	  especificaci�n) y "Probar" (para algoritmo). 
	  Ver Project.createTestForSpecification(), GUI.testAlgorithm(),
	  MUtil.getSourceCodeTemplate{Spec,Alg}Test().
	  
////////////////////////////////////////////////////////////////////////////
2002-09-20 (0.8pre3)

	- Diagram: En cada paquete ahora se ubican de forma contigua los algoritmos 
	  que implementen la misma especificaci�n. En general se hace un ordenamiento
	  lexicogr�fico por el nombre simple de cada familia de unidades (specs, 
	  algoritmos, clases). En el caso de los algoritmos, �ste es el ordenamiento
	  cuando se trata de la misma especificaci�n; pero en caso contrario, se
	  utilizan los nombres de las especificaciones correspondiente para decidir 
	  la comparaci�n.
	  Ver Diagram.setupModel(). 
	  
	- Se hace confirmaci�n antes de salir del entorno. Ver GUI.quit().
	
	- En listas de selecci�n (para abrir/instalar proyecto) ahora se actualiza
	  el enfoque ante cambios en la selecci�n.
	
////////////////////////////////////////////////////////////////////////////
2002-09-19 (0.8pre3)

	- Bug corregido:
		Diagram: El comando 'recargar proyecto' hac�a que quedaran listeners
		repetidos asociados al modelo, lo cual provocaba la repetici�n de
		elementos que se agregaran en el diagrama. Esto tambi�n se presentaba
		despu�s de eliminar un paquete ya que esto hace un llamado a Diagram.reset()
		lo cual siempre agrega un cierto listener al modelo. Correcci�n: el atributo 
		listeners en ProjectModel es ahora un java.util.Set (antes una List), as� se
		garantiza que no puede haber listeners repetidos.
		
		
////////////////////////////////////////////////////////////////////////////
2002-09-18 (0.8pre3)

	- MUtil.setSourceCodeTemplate complementado para incluir 'utiliza' en
	  el caso de un algoritmo que est� en un paquete con nombre y que
	  implementa una especificaci�n en el paquete an�nimo. (El paquete
	  an�nimo no es visible autom�ticamente desde otros paquetes.)
	  
	
////////////////////////////////////////////////////////////////////////////
2002-09-17 (0.8pre3)

	- Renombrado ProjectRepository por Workspace (incluso en este readme).
	
	- Habilitada la escogencia de una especificaci�n teniendo en cuenta
	  todos los proyectos del espacio de trabajo.
	  Ver:
		Project.createAlgorithm()
		Project.chooseSpecification()
		Workspace.getAvailableSpecifications()
		GUI.getAllSpecs()
	  N�tese que GUI.getAllSpecs() obtiene *todas* las especificaciones como 
	  base para verificar lo que se escriba en el campo para el nombre de la
	  especificaci�n; mientras que en Project.chooseSpecification() se vuelven
	  a obtener *todas* las especificaciones pero agrupadas por proyectos
	  para efectos de los submen�es correspondientes. 
	  Puede que esto sea un poco redundante pero funciona bien :-)
	
////////////////////////////////////////////////////////////////////////////
2002-09-16 (0.8pre3)

	- Utilizados nuevos servicios compilador.derivarNombre() y compilador.derivarId()
	  como forma de validar nombres e identificadores para la creaci�n de paquetes,
	  especs, algoritmos y clases.
	  Ver ProjectModel.validateName(String name), PackageModel.validateId(String name).
	  N�tese el chequeo para evitar posible comentario.
	  
	- Ajustes necesarios hechos para revisar completamente los elementos esperados
	  al compilar dependiendo del tipo concreto de unidad.
	  Ver Workspace._compileUnit(), LoroControl.compile*().
	  
////////////////////////////////////////////////////////////////////////////
2002-08-31 (0.8pre2)
	
	- Complementando mecanismo sobre documentaci�n de los proyectos, desde
	  que se ejecuta por primera vez el sistema (versi�n actual), y se
	  instalan nuevos proyectos posteriormente.
	  Ver GUI._firstTime()
	  
////////////////////////////////////////////////////////////////////////////
2002-08-28 (0.8pre2)

	- Puesta versi�n 0.8pre2 (sobre n�cleo 0.7.1)
	
	- No s�lo se genera documentaci�n en doc_dir, sino tambi�n bajo el directorio
	  del respectivo proyecto. Ver GUI.docUnit() y flag docInProjectDirectory.
	  NOTA: Por ahora se hace as� siempre.
	  Esto puede degradar un poco la respuesta del sistema ante compilaciones,
	  pero facilita posteriores operaciones de instalaci�n/exportaci�n de
	  proyectos. Adem�s la degradaci�n no parece tan significativa como para
	  ameritar un mecanismo m�s sofisticado (por lo menos para el mediano plazo).
			
	- Al instalar un proyecto xxx:
		- se copia todo xxx/** al directorio a PRS_DIR/xxx/ (como se ven�a haciendo)
		- se copia todo xxx/**.html al directorio DOC_DIR/
		
	**Actualizaci�n de proyectos de demostraci�n**
	Aprovechando el esquema indicado, se procede de la siguiente manera:
	
		1) ant instalador       # armar instalador
		2) cd DIST/base_instalador/bin
		3) java -cp loroedi.jar loroedi.gui.Main -prsdir ../../../base_dist/lib/prs/
			# para abrir, modificar y (re)compilar directamente a los proyectos de
			# demostraci�n que se requieran actualizar.
		4) cd ../../../
		5) ant instalador    # para hacer el instalador final.
		
	Est� pendiente hacer la (re)compilaci�n y (re)generaci�n de doc autom�ticamente 
	desde build.xml, quiz� complementando la ant-task loroc.
	
		
////////////////////////////////////////////////////////////////////////////
2002-08-27

	- Habilitada la visualizaci�n de documentaci�n.
	  Se est� creando el .html para proyecto en GUI.compileProject()
	  con _saveProjectDoc().
	  Pendiente: C�mo mantener esta informaci�n? por ej, cuando se instala
	  un proyecto, la documentaci�n *debe* venir incluida y entonces
	  instalarse bajo doc_dir (as� como se instala el resto bajo prs_dir).
	  Ante actualizaciones en el entorno, esta info ya se est� actualizando.
	  
	- Actualizado BrowserPanel principalmente para permitir "refresco" de
	  URL actual ante cambios por nueva compilaci�n (sea de unidad o de
	  proyecto completo).
	  Vea BrowserPanel.refresh() y GUI.refreshDocIfNecessary().
	  
	  NOTE: Se est� generando un ArrayIndexOutOfBoundsException que parece ser
	  ser por alg�n problema de sincronizaci�n. El refresco termina bien pero
	  estar pendiente de esto.
	  
	- Cambiado color para comentarios /*...*/ para que se diferencien del nuevo
	  componente l�xico ''...''
	
////////////////////////////////////////////////////////////////////////////
2002-08-26

	- En "compilar proyecto" se guardan previamente las unidades que posiblemente se
	  encuentren con modificaciones.
	  
	- Nuevo men� "Ejecuci�n" en proyecto.
	
	- Complementados men�es en proyecto y unidad.
	
	- Resaltado de nuevos elementos l�xicos TEXT_DOC y GUIDE.
		TEXT_DOC, ''texto'', se resalta con color especial para cadenas de documentaci�n
		de forma distinta a LITERAL_CADENA, lo cual resulta mucho m�s claro y menos confuso.
		
		GUIDE, {{ texto }}, se utiliza en las plantillas de c�digo para indicar 
		puntos a ser remplazados.
		
	  Actualizados MUtil y algunos fuentes en loroedi.jedit.*
	  
////////////////////////////////////////////////////////////////////////////
2002-08-25

	- Utilizando versi�n modificada de inicializaci�n del n�cleo:
		Loro.configurar(ext_dir, paths_dir);
	  y su nuevo servicio:
	  	Loro.addDirectoryToPath(File dir)
	  para cuando se crea un nuevo proyecto y para cuando se instala un proyecto.
	  Ver GUI._initCore().
	  
	- Mejorado di�logo para "Instalar" proyecto. Incluye opci�n para
	  abrirlo inmediatamente.
	  
	  
////////////////////////////////////////////////////////////////////////////
2002-08-24

	- Mejorada forma de llevar a cabo tareas de compilaci�n:
		- actualizaci�n de �reas de mensaje tanto en unidades abiertas como
		  en el proyecto.
	  	- Se abre el editor correspondiente a una unidad que presenta
	  	  problema de compilaci�n. 
	
	- Actualizados todos los ejemplos (anteriormente subdirectorio demo/)
	  como proyectos bajo lib/prs/ , los cuales quedan disponibles como
	  proyectos "instalables".
	  Actualizado tambi�n build.xml, target "test-demos".
	
////////////////////////////////////////////////////////////////////////////
2002-08-22

	- Opci�n "Importar" funcionando. Ahora s�lo acepta un archivo fuente
	  procediendo a adicionar las unidades que alli se encuentren.
	  El requerimiento es que dicho fuente debe compilar exitosamente
	  para poder completar la importaci�n. En un futuro se podr�a hacer
	  esto un poco m�s flexible: quiz� aceptar algunos errores y a�n as�
	  cargar las unidades definidas en el fuente. M�s posibilidades
	  incluyen la importaci�n de m�ltiples fuentes.
	  La carga desde un archivo zip (o lar) podr�a dejarse mejor para
	  la opci�n "Instalar".
	
////////////////////////////////////////////////////////////////////////////
2002-08-21

	- Nueva opci�n "Instalar" que permite habilitar un proyecto "incluido"
	  de manera que quede disponible como un proyecto regular. Los proyectos
	  incluidos se encuentran bajo la distribuci�n en lib/prs/ .
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
	
	- Mejorada implementaci�n del patr�n "observer":
	  Los siguientes son "listeners" sobre un IProjectModel:
	  	- Diagram
		- Workspace
	- Toda adici�n/eliminaci�n de elementos en un proyecto es reflejada en disco
	  inmediatamente.
	  
////////////////////////////////////////////////////////////////////////////
2002-08-14

	- Nueva interface ICell para mantener informaci�n con el diagrama a nivel
	  de celdas y facilitar por ejemplo el borrado de elementos.
	  
	- Ya se borran elementos (unidades y paquetes).
	  Un elemento que tenga dependencias (por ejemplo una espec que tenga algoritmos
	  o un paquete no vac�o) no puede ser borrado.
	  La ventana de edici�n de una unidad tambi�n es eliminada en caso de que exista.
	  Los archivos correspondientes tambi�n son borrados: .oro, .loro, .html para
	  unidades, y los pkg.props para paquetes.
	  
	- Se atienden teclas Delete, Enter y Ctrl-F9 en el diagrama.
	
////////////////////////////////////////////////////////////////////////////
2002-08-12

	- IProjectUnit ahora incluye asociaci�n con IUnidad, la cual se establece
	  en compilaci�n y en carga, y se obtiene para ejecuci�n (y despu�s para
	  documentaci�n posiblemente).
	  
	- Empezando compilaci�n tanto de unidades individuales como del proyecto
	  completo.
	  
	- Workspace.compileProjectModel ahora hace una compilaci�n
	  "ordenada" buscando minimizar el riesgo de dependencias mutuas:
	  	- todas las clases
	  	- todas las especs
	  	- todas los algoritmos
	  pero esta soluci�n es incompleta --sigue pendiente dise�ar un sistema
	  completo de dependencias para compilaci�n.
	  
////////////////////////////////////////////////////////////////////////////
2002-08-11

	- Ajustes a build.xml para reflejar nuevo esquema de manejo de extensiones
	  definidos desde el mismo n�cleo.
	  
	- Nuevo subdirectorio src/prs/ para poner "proyectos" a incluir en
	  la distribuci�n. Ahora se tienen:
	  	src/prs/Adivina     -- adivinadores
	  Este esquema remplazar� al tradicional subdirectorio "demo" --pendiente.
	  
	- loroedi.gui.Main recibe ahora un argumento opcional para indicar el
	  directorio de base para proyectos, lo cual facilita el desarrollo de
	  estos proyectos para posterior distribuci�n.
	  
	- Primeras pruebas de compilaci�n.
	  Ver Workspace.
	
////////////////////////////////////////////////////////////////////////////
2002-08-08

	- Modificaciones hechas en Workspace de acuerdo con el redise�o 
	  actual del n�cleo en cuanto a IOroLoader's.
	  
	- ant test ; ant test2 ; ant test2   -> OK
	  
	  
////////////////////////////////////////////////////////////////////////////
2002-08-04

	- Workspace.exportProjectModel:
		Empezando a operar, por ahora a un directorio.
		

////////////////////////////////////////////////////////////////////////////
2002-08-03

	- Workspace.saveProjectModel:
		Ya se guardan los paquetes: por cada paquete su pkg.props
		as� como los c�digo fuente de cada unidad.
		
	- Nuevo Project.EditorListenerImpl para atender las acciones
	  sobre una ventana de edici�n de una unidad.
		
	- Diagram ahora tiene en cuenta las asociaciones spec-algorithm
	  de tal manera que si una especificaci�n es agregada *despu�s*
	  del algoritmo, puede hacer la conexi�n correspondiente.

////////////////////////////////////////////////////////////////////////////
2002-08-01

	- Inicio de Workspace.saveProjectModel: ahora guarda s�lo
	  las propiedades generales prj.props
		
	- "name" NO es m�s una de las propiedades dentro de prj.props:
	  ahora se toma directamente o bien el nombre del archivo de extensi�n
	  o bien el nombre del directorio dentro de PRS_DIR.
	  Esto permite hacer un manejo estilo c�digo para dicho nombre de tal
	  manera que se puede hacer una identificaci�n de cada proyecto.

////////////////////////////////////////////////////////////////////////////
2002-07-31

	- En "Crear paquete" campo en blanco permite crear el paquete an�nimo.

	- Nuevas Preferencias: PRJ_RECENT, PRJ_RECT, PRS_DIR
	
	- Organizaci�n de proyectos bajo PRS_DIR:
		PRS_DIR/
		       my_project/
		                 prj.props
		                 prj.description
						 
		                 pkg.props              -- del paquete an�nimo
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
	- pkg.description: descripci�n m�s larga del proyecto.
	

////////////////////////////////////////////////////////////////////////////
2002-07-30 

	- Publicaci�n como Versi�n 0.7.0.
	  Los cambios etiquetados abajo como "hacia 0.8pre1" a�n no son
	  p�blicos. La GUI sigue siendo la acostumbrada.

////////////////////////////////////////////////////////////////////////////
2002-07-30 (hacia 0.8pre1)

	- Workspace est� obteniendo ya informaci�n sobre extensiones 
	  a trav�s de nuevo servicio del n�cleo.
	
////////////////////////////////////////////////////////////////////////////
2002-07-26 (hacia 0.8pre1)

	- Nuevo menu "Ventana". Ahora permite navegar entre las ventanas
	  asociadas a proyectos.
	  
	- Atendido acci�n "Nuevo" para crear un nuevo proyecto.
	
////////////////////////////////////////////////////////////////////////////
2002-07-25 (hacia 0.8pre1)

	- Agregados controles sobre acciones habilitadas dependiendo de si
	  el proyecto enfocado es modificable o no.
	  
////////////////////////////////////////////////////////////////////////////
2002-07-24 (hacia 0.8pre1)

	- Nueva clase GUI que hace el control general.
	  Siempre hay por lo menos un proyecto abierto, que ser� el proyecto
	  "base" en caso de "nuevo" proyecto o justo despu�s de cerrarse
	  el �ltimo proyecto. Ver GUI.closeProject()
	  
////////////////////////////////////////////////////////////////////////////
2002-07-18 (hacia 0.8pre1)

	- Diagram.MyJGraph.MyView.update() sobreescritura para controlar 
	  posici�n vertical de los v�rtices.

	- Posicionamiento inicial simple pero aceptable.
	  
////////////////////////////////////////////////////////////////////////////
2002-07-13 (hacia 0.8pre1)

	- Mejorando el dise�o:
		- ProjectModel
		- PackageModel
		- Mejor esquema de eventos

////////////////////////////////////////////////////////////////////////////
2002-07-11 (hacia 0.8pre1)

	- Nueva interface ProjectUnit con clases SpecificationUnit, AlgorithmUnit
	  y ClassUnit, y ajustes generales alrededor de estos elementos.
	- Mejoramiento de di�logos.
	
////////////////////////////////////////////////////////////////////////////
2002-07-10 (hacia 0.8pre1)

	- Nuevo ProjectDialog para petici�n de datos con un primer mecanismo para
	  permitir validaci�n inmediata. Gracias a AbstractProjectModel, se est�
	  validando ahora que un nuevo elemento (espec o algoritmo) no vaya a
	  tener un nombre repetido. Esto por ahora dentro del mismo proyecto.
	  Pendiente: definir los chequeos generales.
	  
	- Diagram:
		- Primera versi�n simple para ubicaci�n autom�tica de nuevos elementos
		  incluyendo conexi�n algoritmo->especificaci�n.
		- anti-aliasing on; dashPattern = {4, 2}; Bezier line style.
	
////////////////////////////////////////////////////////////////////////////
2002-07-08 (hacia 0.8pre1)

	- Inicio de atenci�n de eventos
	- Inicio de ubicaci�n de nodos en grafo
	- AbstractProjectModel con funcionalidades b�sicas (por ejemplo, manejo
	  de los ProjectModelListener's).
	- NewProjectModel (subclase de AbstractProjectModel) ser� el proyecto
	  a despachar cuando se entre por primera vez al EDI o se escoja la
	  opci�n "Nuevo (proyecto)".
	  Primera idea sobre esto: Ante la primera modificaci�n que vaya a
	  guardarse, se pedir� al usuario un nombre concreto para el proyecto.
	
////////////////////////////////////////////////////////////////////////////
2002-07-06 (hacia 0.8pre1)

	- Nueva clase Diagram destinada a visualizar (jgraph) las relaciones
	  de un ProjectModel.
	  Por ahora solo muestra un diagrama simple de prueba.
	  
	- Incluido jgraph.jar en la compilacion (build.*) y en la distribucion.

	Para ejecuci�n de pruebas:
		java -cp loroedi.jar loroedi.gui.project.NewProjectModel
		

////////////////////////////////////////////////////////////////////////////
2002-07-04 (hacia 0.8pre1)

	El esquema inicial es:
		- Una ventana de despacho para proyectos conteniendo los paquetes/unidades
		  que conforman el proyecto, incluyendo un grafo y un �rea de ejecuci�n 
		  interactiva.
		  
		- Una ventana de edici�n para cada unidad particular.
		
	- loroedi.gui.editor
	  Manejo del editor de unidad:
	  	UEditor              El editor como tal
	  	UEditorListener      interface para clientes de UEditor
		UEditorListenerImpl  Una implementaci�n sencilla.  tiene main de prueba
		
	- loroedi.gui.project
	  Manejo de proyectos:
	  	Project              Ventana para un projecto
	  	ProjectModel         Datos para el proyecto

***********************************************
* Dise�o de interface gr�fica integrada       *
* Paquete de base:  loroedi.gui               *
* Fecha de inicio: 2002-07-04                 *
***********************************************


////////////////////////////////////////////////////////////////////////////
2002-05-26 (0.7s2)

	- Actualizado LoroControl.verificarConfiguracionNucleo para considerar
	  interfaces y objetos (aunque el manejo de estos elementos a�n est�
	  incompleto en la versi�n 0.7s2 del n�cleo).
	  
////////////////////////////////////////////////////////////////////////////
2002-05-21 (0.7s2)

	- Incluido Kunststoff Look&Feel (http://www.incors.com), que se establece 
	  si la propiedad LOROEDI_PREF_LAF NO est� definida (es decir, �ste ser� el
	  L&F por defecto). Si la propiedad s� est� definida y es igual a "ignore",
	  entonces no se establece expl�citamente ning�n L&F (queda el que ponga
	  el sistema Java); en otro caso, se interpreta el valor como el nombre de un 
	  "theme" para SkinLF: primero tom�ndolo directamente y, si no existe, se 
	  intenta prefijando el directorio (nuevo) lib/themes/ bajo la instalaci�n 
	  de LoroEDI.  
	  
	  Ver loroedi.laf.LookAndFeel.
	  
	  Actualizados build.properties y build.xml
	  
	  Bajo lib/ se est� incluyendo ahora entonces para la distribuci�n:
		kunststoff.jar  (nuevo)
		skinlf.jar (desde hace alg�n tiempo aunque sin publicaci�n)
	  	themes/aquathemepack.zip (aunque tiene algunos bugs leves)
	  
	- Agregado comando .version al int�rprete interactivo para mostrar informaci�n
	  sobre versi�n del sistema (LoroEDI y n�cleo Loro).
	  
	- Enlazando con n�cleo 0.7s2.
	
////////////////////////////////////////////////////////////////////////////
2002-05-05 (0.7s2)

	- Actualizado Splash para mostrar versi�n del LoroEDI, y ajustados
	  algunos constructores en LoroEDI y EDI para que el "parent" del
	  splash sea la ventana principal del entorno.
	  
	- Actualizado DocNodeFactory para crear entradas para posibles
	  unidades: interfaces, objetos, clases, especificaciones, algoritmos.
	  
////////////////////////////////////////////////////////////////////////////
2002-04-25

	- loroedi.help.BrowserPanel.Hyperactive.hyperlinkUpdate() ahora atiende
	  tambi�n eventos ENTERED y EXITED con el fin de mostrar la direcci�n
	  del enlace sobre el que est� apuntando el rat�n.
	  El mensaje de error cuando un enlace no es visualizable incluye la
	  direcci�n correspondiente.
	
////////////////////////////////////////////////////////////////////////////
2002-04-25 (0.7s1)

	- Nuevo m�todo loroControl.verificarConfiguracionNucleo() para hacer uso
	  de nuevo servicio de Loro.verificarConfiguracion().
	  Este m�todo se invoca en LoroEDI.despacharPrimeraVez(), y tambi�n a
	  trav�s de una nueva opci�n "Verificar sistema" bajo el men� "Ayuda".
	
////////////////////////////////////////////////////////////////////////////
2002-04-15

	- Nuevo comando .gc para el int�rprete.
	
////////////////////////////////////////////////////////////////////////////
2002-04-11 (0.7r9)

	- Cuando se instala una nueva versi�n del sistema, en particular, cuando
	  el formato de las unidades compiladas ha cambiado (ver n�cleo), c�mo
	  actualizar esto para el usuario?
	  
	  En la primera ejecuci�n de la nueva versi�n, se conocer�a la 
	  incompatibilidad (por comparaci�n de versi�n, o a mano, etc.)
	  Entonces se mostraria al usuario un mensaje estilo:
	  
	  	Es necesario volver a compilar sus programas para que sean 
		compatibles con esta versi�n del sistema.
		Loro proceder� con esta recompilaci�n autom�ticamente.
		                       [aceptar]
							   
	  Pero ahora no hay forma de saber cu�les archivos fuentes compilar:
	  aunque las unidades previas contienen esta informaci�n, no es
	  posible leerlas por la incompatibilidad de la serializaci�n.
	  Esto es lo que se quiere arreglar para las pr�ximas versiones!

	- Idea: Mostrar nuevo elemento gr�fico para la "navegaci�n de unidades",
	  similar a como sucede ahora con la documentaci�n, pero permitiendo
	  otras operaciones, como "ejecutar" cuando se trata de algoritmos,
	  "instanciar" para clases, "crear nuevo algoritmo" para especificaciones,
	  etc.
	
////////////////////////////////////////////////////////////////////////////
2002-04-05

	- Actualizado LoroTokenMarker para resaltar nuevas palabras clave
	  "objeto", "operaci�n" y "m�todo".
	- Enlazando con versi�n 0.7r9 del n�cleo.
	
	- Actualizado build.xml con nuevo target "test2" que hace una nueva
	  pasada para posible resoluci�n de dependencias. El target "test"
	  siempre borra primero todos los compilados previos posibles para
	  iniciar una prueba completa.
	  
	- Actualizados algunos documentos de distribuci�n.
	
	- Corregido bug: Ante error sint�ctico/l�xico al puro final del fuente,
	  no se llevaba el curso hasta all�. Ver Editor.select().
	  
	- Pendiente: Activar siempre el logger en alg�n archivo predeterminado.
	  Esto puede requerir una nueva API del n�cleo (quien solo activa el logger
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
