Loro Project - http://loro.sf.net
---------------------------------
Carlos A. Rueda
$Id$

The Loro Project comprises the following main modules:
	loro             Language core
	loroedi          Integrated programming environment
	
Generated directory:
	The various build.properties define 'generated.dir' 
	to designate the directory ../_GENERATED/ (relative to this
	README.txt). All generated files are put under this directory.
	
lib/ directory
	Contains required third-part libraries. Currently:
		bsh-core-1.2b6.jar    (BeanShell)
		jgraph.jar            (JGraph)
		kunststoff.jar        (look and feel--optional)
		skinlf.jar	          (look and feel--optional)
		nic.jar (not required any more, see below)

About compilation and other information, please refer to other
README files under this directory.

loroprjs/ directory
	Contains some demo projects in Loro and documents
	for http://loro.sf.net/projects/

Old components
	These modules are not longer maintained or used:
	
	nanoinstaller - A very simple installer creator
		$ cd nanoinstaller/src
		$ ant          # generates nic.jar
	simpledi - Old development environment
		$ cd simpledi
		$ ant              # generates simpledi.jar
		

