Loro Project README
-------------------
Carlos A. Rueda
$Id$

El proyecto Loro se compone de los siguientes módulos principales:
	loro             Núcleo del lenguaje
	loroedi          Entorno Integrado
	
Directorio de elementos "generados":
	Los diferentes build.properties definen la propiedad 'generated.dir' 
	para designar el directorio ../_GENERATED/ (relativo a la ubicación
	de este README.txt)	como base para poner todos los archivos generados.
	
Directorio lib/
	El directorio lib/ contiene los archivos foráneos de
	los cuales depende el proyecto Loro. Actualmente:
		nic.jar (no mas requerido, ver mas abajo)
		bsh-core-1.2b6.jar
		jgraph.jar
		kunststoff.jar
		skinlf.jar	

Sobre compilación completa del proyecto y otras notas, consultar
los respectivos README.txt bajo los subdirectorios loro/ y loroedi/

Componentes viejos
	Los siguientes son modulos viejos utilizados anteriormente pero que
	ya no son requeridos.
	
	nanoinstaller - Creador simple del instalador utilizado por LoroEDI
		Compilacion:
		$ cd nanoinstaller/src
		$ ant          # genera nic.jar
	simpledi - Viejo entorno de desarrollo
		Compilacion:
		$ cd simpledi
		$ ant              # genera el simpledi.jar. Ver README.txt.
		

