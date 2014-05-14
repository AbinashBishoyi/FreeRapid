**************************************************************
*   FreeRapid Downloader                                     *
*      por Ladislav Vitasek aka Vity                          *
*   Web/Foro/Bugs: http://wordrider.net/freerapid *
*   Mail: info@wordrider.net - sugerencias                   *
*   �ltimo Cambio: 25th October 2008                         *
**************************************************************
======================================
Contenido:
   I.   �Qu� es FreeRapid Downloader?
  II.   Requisitos de sistema
 III.   C�mo ejecutar FreeRapid
  IV.   Problemas conocidos y limitaciones
   V.   Solucionando Problemas
  VI.   Informar Errores
 VII.   Donar
VIII.   Preguntas Frecuentes
======================================

I.    �Qu� es FreeRapid Downloader?
=======================================

FreeRapid Downloader es un software de descarga sencillo escrito en Java, para gestionar las descargas desde Rapidshare y 
otros sitios de descarga de archivos

Caracter�sticas Principales
 - soporte para descargas desde m�ltiples servicios
 - descarga usando listas de proxys
 - historial de descarga
 - monitoreo del portapapeles
 - interface de programaci�n (API) para agregar otros servicios mediante plugins
 - autoapagado
 - funciona en linux y MacOS

Misc.:
 - Arrastrar y Soltar URLs

Los servicios soportados actualmente son:
 - 2shared.com	             - jandown.com	             - share-rapid
 - 4shared.com	             - kewlshare.com	         - shareator.com
 - badongo.com	             - kitaupload.com	         - shareonline.biz
 - bagruj.cz	             - leteckaposta.cz	         - storage.to
 - bitroad.net	             - letitbit.net	             - stream.cz
 - cobrashare.sk	         - linkbucks.com	         - subory.sk
 - czshare.com	             - load.to	                 - tinyurl.com
 - czshare.com_profi	     - mediafire.com	         - ugotfile.com
 - dataup.de	             - megaupload.com	         - uloz.cz
 - depositfiles.com	         - myurl.in	                 - uloz.to
 - disperseit.com	         - nahraj.cz	             - ulozisko.sk
 - easyshare.com	         - netgull.com	             - ultrashare.net
 - edisk.cz	                 - netload.in	             - uploadbox.com
 - egoshare.com	             - o2musicstream.cz	         - uploaded.to
 - enterupload.com	         - paid4share.com	         - uploading.com
 - filebase.to	             - plunder.com	             - uploadjockey.com
 - filefactory.com	         - przeklej.pl	             - upnito.sk
 - fileflyer.com	         - quickshare.cz	         - uppit.com
 - filesend.net	             - radikal.ru	             - usercash.com
 - fileupload.eu	         - RapidShare.com	         - webshare.net
 - flyshare.cz	             - RapidShare.com_premium	 - wiiupload.net
 - hellshare.com	         - rapidshare.de	         - wikiupload.com
 - hellshare.com_full	     - rapidshareuser	         - xtraupload.de
 - hotfile.com	             - rsmonkey.com	             - yourfiles.biz
 - ifile.it	                 - savefile.com	             - youtube.com
 - imagebam.com	             - saveqube.com	             - ziddu.com
 - imagehaven.net	         - sdilej.to	             - zippyshare.com
 - indowebster.com	         - sendspace.com	         - zshare.net
 - iskladka.cz	             - sendspacepl.pl


 II.    Requisitos del sistema
=======================================

Configuraci�n recomendada:
    * Windows 2000/XP/Linux(core 2.4)* o posterior
    * Procesador Pentium 800MHz 
    * resoluci�n m�nima 1024x768 
    * 40 MB de memoria RAM
    * 10 MB de espacio libre en el disco 
    * Java 2 Platform - version 1.6 (Java SE 6 Runtime) 

la aplicaci�n necesita por lo menos Sun Java 6.0 para funcionar (http://java.sun.com/javase/downloads/index.jsp , JRE 6)


III.   C�mo ejecutar FreeRapid Downloader
=========================--==============

Instalaci�n
------------
Descomprima los archivos a cualquier directorio, pero cuidado con caracteres especiales (como  '+' o '!') en la ruta
si hace una actualizaci�n a una versi�n posterior, puede eliminar la carpeta anterior todas las configuraciones se 
almacenan en su directorio personal.
MS Windows: c:\Documents and Settings\SU_NOMBRE_DE_USUARIO\application data\VitySoft\FRD

Linux: ~/.FRD

NO COPIE UNA NUEVA VERSION A LA CARPETA DONDE ESTA UNA VERSION ANTERIOR

Ejecuci�n
-----------
Windows
 Simplemente ejecute frd.exe

Linux
 Ejecute el comando ./frd.sh

Todas las plataformas
 Ejecute el comando: java -jar frd.jar

par�mentros adicionales para ejecutar:

java -jar frd.jar [-h -v -d -D<propiedad>=<valor>


opciones
  -h (--help,-?)      muestra este mensaje
  -v (--version)      muestra la informaci�n de la versi�n y termina
  -d (--debug)        muestra informaci�n de depuraci�n  
  -r (--reset)        reinicia las propiedades de los usuarios a sus valores por defecto
  -m (--minim)        minimiza la ventana principal al iniciar 
  -Dproperty=value    Pasa la propiedad y su valor a la aplicaci�n (frecuentemente para prop�sitos de depuraci�n)

Si el valor de la propiedad -D es determinada como 'default' (sin ') se usar� el valor por defecto.

ejemplo - ejecuci�n de la aplicaci�n en modo de depuraci�n
  Windows : frd.exe --debug
  Linux/MacOS: java -jar frd.jar --debug


Ma�s Informaci�n:
  - Tutorial no oficial para usuarios - c�mo configurar FreeRapid Downloader en Linux (en espa�ol)
    http://manualinux.my-place.us/freerapid.html


    
IV.    Errores conocidos y limitaciones
========================================
- La aplicaci�n no iniciar� si est� localizada en una ruta con caracteres especiales, como "+" o "%"
   X Por favor, mueva la aplicaci�n a otra ubicaci�n sin tales caracteres
- ESET "Smart" Antivirus bloquea, en Windows, el inicio de la aplicaci�n
   X Corrija las configuraciones de su programa antivirus o ejecute el antivirus con la opci�n: frd.exe -Doneinstance=false
- Siempre cierre FRD apropiadamente, o puede perder su lista de archivos (ejemplo: apagar forzadamente Windows)
- Seleccionar, desde el final hasta el principio de la lista, en la ventana principal mientras descarga parcialmente 
  desaparece :-(
   X seleccione la fila de la tabla haciendo ctrl+click del rat�n o seleccione los �tems desde el inicio hasta el final
- El tema Substance lanza una excepci�n org.jvnet.substance.api.UiThreadingViolationException:
						Component creation must be done on Event Dispatch Thread
   X ignore esta excepci�n en el app.log
- java.lang.UnsupportedClassVersionError exception
    X usted est� usando una versi�n antigua de java, debe usar la versi�n 6 o posterior
- DirectoryChooser lanza java.lang.InternalError o se congela en Windows Vista (64bit)
    X ignore esta excepci�n en el app.log
- java.lang.ClassCastException: java.awt.TrayIcon cannot be cast to java.awt.Component
    X ignore esta excepci�n en el app.log
- Usuarios de linux informan que no se est� mostrando un icono en la bandeja
    X la �nica soluci�n para este problema podr�a ser una actualizaci�n del JRE a la version 1.6.0_10-rc o posterior

V.    Solucionando Problemas
=======================================
1. Revise la secci�n IV - por alg�n error conocido o limitaci�n
2. �Ha intentado salir de la aplicaci�n y volver a entrar? :)
3. Revise la p�gina http://wordrider.net/freerapid y/o el seguimiento de errores en http://bugtracker.wordrider.net/
   por un posible nuevo error conocido.
4. Puede intentar eliminar los archivos de configuraci�n (su ubicaci�n est� descrita en la secci�n VI -  Instalaci�n )  
5. Ejecute la aplicaci�n en modo de depuraci�n
   Windows OS: frd.exe --debug
   Linux/MacOS: java -jar frd.jar --debug
6. Informe el problema con el app.log como es descrito en la secci�n VI


VI.    Informe de errores
=======================================
Si usted encuentra alg�n error en la aplicaci�n, por favor, no asuma que ya lo s�, h�gamelo saber tan pronto sea posible
para solucionarlo antes de la pr�xima liberaci�n, ya que mis recursos son limitados, no puedo hacer correcciones de errores
a versiones anteriores, para informar un error, usted puede usar el issue tracker (recomendado), foros del proyecto o mi 
correo personal.

Por favor, indique su versi�n de JRE y de su sistema operativo y adjunte el archivo app.log (el cual est� en la carpeta de 
FreeRapid) esto puede ayudarnos a reconocer el problema. Usted puede Ayudarnos tambien si ejecuta la aplicaci�n con el 
parametro --debug. Siempre ignoramos preguntas como "No puedo ejecutar FRD. �Qu� puedo hacer?". Describa su situaci�n y el comportamiento de la aplicaci�n 

issue tracker: http://bugtracker.wordrider.net/
foros: http://wordrider.net/forum/list.php?7
mail: bugs@wordrider.net (su correo puede ser atrapado por el filtro antispam, as� que NO considere esta como la mejor forma)


VII.    Donaciones
=======================================
FreeRapid Downloader es distribuido como freeware, pero si desea expresar su apreciaci�n por el tiempo y recursos que el
autor ha utilizado desarroll�ndolo, lo aceptaremos y aceptamos donaciones monetarias.
Somos estudianes y debemos pagar nuestro hosting, la cuenta para nuestras novias etc...

PayPal: http://wordrider.net/freerapid/paypal
   o
use el n�mero de cuenta descrito en nuestra p�gina  http://wordrider.net/freerapid/


VIII.   Preguntas Frecuentes
=======================================
Q: �Por qu� cre� otro "RapidShare Downloader"?
A: 1) Por que no quiero depender de un software ruso que probablemente est� lleno de malware y spyware.
   2) Por que puedo simplemente solucionar las descargas autom�ticas por m� mismo
   3) Por que otros programas existentes tienen interfaces poco intuitivas y pierden caracter�sticas importantes.
   4) Por que Puedo.

Q: �C�mo puedo habilitar el soporte para comandos de apagado en Linux y MacOS?
A: Por favor, vea el archivo de configuraci�n 'syscmd.properties' en el directorio de la aplicaci�n para m�s detalles.

