Loro README
Carlos A. Rueda
$Id$

Dentro del Proyecto Loro, el modulo "loro" designa al propio n�cleo 
del lenguaje (mientras que "loroedi" designa al entorno de desarrollo 
integrado).

El n�cleo ofrece las funcionalidades basicas asociadas a compilacion,
generacion de documentacion, manejo de unidades y extensiones, ejecucion,
etc., y esta diseniado como una API sobre la cual posibilitar la creacion
de aplicaciones cliente. Aunque se ofrece una interfaz de usuario basada 
en programas de linea de comandos, esta sirve mas para fines de pruebas de 
desarrollo internas por lo que se sugiere a los usuarios finales utilizar 
preferiblemente el Entorno Integrado LoroEDI.

Para la compilaci�n del n�cleo: 
	$ ant         # genera el .jar del n�cleo. Ver build.properties
	$ ant ext     # crea las extensiones .lar
	$ ant test    # hace pruebas

