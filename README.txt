Loro Project README
-------------------
Carlos Rueda - 2002-10-20

El proyecto Loro se compone actualmente de los siguientes módulos:

	loro             Núcleo del lenguaje
	loroedi          Entorno Integrado
	nanoinstaller    Creador simple del instalador utilizado por LoroEDI


Directorio de elementos "generados"
	Los diferentes build.properties definen la propiedad 'generated.dir' 
	para designar el directorio ../_GENERATED/ (relativo a la ubicación
	de este README.txt)	como base para poner todos los archivos generados.
	
	NOTA: Hay una excepción: el compilador del nanoInstaller, nic.jar, se 
	crea directamente en el directorio lib/ .
	
Directorio lib/
	El directorio lib/ contiene los archivos foráneos (salvo nic.jar) de
	los cuales depende el proyecto Loro. Actualmente:
		nic.jar
		bsh-core-1.2b6.jar
		jgraph.jar
		kunststoff.jar
		skinlf.jar	

Compilación completa del proyecto
	Realizar en orden los siguientes pasos para contruir el sistema completo:
	(pendiente un build.xml maestro para esto)
	
	Núcleo:
		$ cd loro     # el núcleo propiamente
		$ ant         # genera el .jar del núcleo. Ver build.properties
		$ ant ext     # crea las extensiones .lar
		$ ant test    # hace pruebas
		$ cd ..       # listo

	nanoInstaller:
		No es necesario hacer nada pues ya se tiene lib/nic.jar, utilizado por
		el entorno integrado para hacer el instalador. Por supuesto, si se hacen
		cambios a los fuentes del nanoInstaller, entonces los pasos serían:
		$ cd nanoinstaller/src
		$ ant          # genera nic.jar
		$ cd ../..     # listo
		
	Entorno integrado:
		$ cd loroedi
		$ ant              # genera el loroedi.jar. Ver build.properties
		$ ant test         # inicia pruebas
		$ ant test2        # continúa pruebas
		$ ant test2        # repetir hasta que haya exito o realmente haya problemas :-}
		                   # (Algunos fuentes tienen dependencias mutuas y por eso
						   # se requieren varias pasadas para la compilación completa.
						   # Está pendiente hacer compilación bajo el esquema de proyectos.)
		$ ant instalador   # crea el instalador para el usuario final
		$ cd ..            # listo		
		

