Loro Project README
-------------------
Carlos Rueda - 2002-10-20

El proyecto Loro se compone actualmente de los siguientes m�dulos:

	loro             N�cleo del lenguaje
	loroedi          Entorno Integrado
	nanoinstaller    Creador simple del instalador utilizado por LoroEDI


Directorio de elementos "generados"
	Los diferentes build.properties definen la propiedad 'generated.dir' 
	para designar el directorio ../_GENERATED/ (relativo a la ubicaci�n
	de este README.txt)	como base para poner todos los archivos generados.
	
	NOTA: Hay una excepci�n: el compilador del nanoInstaller, nic.jar, se 
	crea directamente en el directorio lib/ .
	
Directorio lib/
	El directorio lib/ contiene los archivos for�neos (salvo nic.jar) de
	los cuales depende el proyecto Loro. Actualmente:
		nic.jar
		bsh-core-1.2b6.jar
		jgraph.jar
		kunststoff.jar
		skinlf.jar	

Compilaci�n completa del proyecto
	Realizar en orden los siguientes pasos para contruir el sistema completo:
	(pendiente un build.xml maestro para esto)
	
	N�cleo:
		$ cd loro     # el n�cleo propiamente
		$ ant         # genera el .jar del n�cleo. Ver build.properties
		$ ant ext     # crea las extensiones .lar
		$ ant test    # hace pruebas
		$ cd ..       # listo

	nanoInstaller:
		No es necesario hacer nada pues ya se tiene lib/nic.jar, utilizado por
		el entorno integrado para hacer el instalador. Por supuesto, si se hacen
		cambios a los fuentes del nanoInstaller, entonces los pasos ser�an:
		$ cd nanoinstaller/src
		$ ant          # genera nic.jar
		$ cd ../..     # listo
		
	Entorno integrado:
		$ cd loroedi
		$ ant              # genera el loroedi.jar. Ver build.properties
		$ ant test         # inicia pruebas
		$ ant test2        # contin�a pruebas
		$ ant test2        # repetir hasta que haya exito o realmente haya problemas :-}
		                   # (Algunos fuentes tienen dependencias mutuas y por eso
						   # se requieren varias pasadas para la compilaci�n completa.
						   # Est� pendiente hacer compilaci�n bajo el esquema de proyectos.)
		$ ant instalador   # crea el instalador para el usuario final
		$ cd ..            # listo		
		

