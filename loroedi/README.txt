LoroEDI README
Carlos A. Rueda
$Id$

LoroEDI es el Entorno de Desarrollo Integrado construido sobre
el nucleo del lenguaje.

Para compilar el Entorno integrado:
	Primero revisar la propiedades en build.properties
	$ ant              # genera el loroedi.jar
	$ ant test         # inicia pruebas
	$ ant test2        # contin�a pruebas
	$ ant test2        # repetir hasta que haya exito o realmente haya problemas :-}
					   # (Algunos fuentes tienen dependencias mutuas y por eso
					   # se requieren varias pasadas para la compilaci�n completa.
					   # Est� pendiente hacer compilaci�n bajo el esquema de proyectos.)
	$ ant installer    # crea el instalador para el usuario final

