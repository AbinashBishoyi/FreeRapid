**************************************************************
*   FreeRapid Downloader                                     *
*      by Ladislav Vitasek aka Vity                          *
*   Website/Forum/Bugtracker: http://wordrider.net/freerapid *
*   Mail: info@wordrider.net - sugerencias                   *
*   Ultimo Cámbio: 25th October 2008                         *
**************************************************************
======================================
Contenido:
   I.   ¿Qué es FreeRapid Downloader?
  II.   Requicitos de sistema
 III.   Como ejecutar FreeRapid
  IV.   Problemas conocidos y limitaciones
   V.   Solucionando Problemas
  VI.   Informar Errores
 VII.   Donar
VIII.   FAQ
======================================

I.    ¿Qué es FreeRapid Downloader?
=======================================

FreeRapid downloader es un software de descarga sencillo escrito en Java, para gestionar las descargas desde Rapidshare y 
otros sitions de descarga de archivos

Caracteristicas Principales
 - soporte para descargas desde multiples servicios
 - descarga usando listas de proxys
 - historial de descarga
 - monitoreo del portapapeles
 - interface de programacion (API) para agregar otros servicios mediante plugins
 - autoapagado
 - trabaja en linux y MacOS

Misc.:
 - Arrastrar y Soltar URLs

Los serivicios soportados actualmente son:
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


 II.    Requicitos del sistema
=======================================

Recommended configuration:
    * Windows 2000/XP/Linux(core 2.4)* o posterior
    * Procesador Pentium 800MHz 
    * resulción mínima 1024x768 
    * 40 MB de memoria RAM
    * 10 MB de espacio libre en el disco 
    * Java 2 Platform - version 1.6 (Java SE 6 Runtime) 

la aplicacion necesita por lo menos Java 6.0 para funcionar (http://java.sun.com/javase/downloads/index.jsp , JRE 6)


III.   Como ejecutar FreeRapid Downloader
=========================--==============

Instalación
------------
Descomprima los archivos a cualquier directorio, pero cuidado con caracteres especiales (como  '+' o '!') en la ruta
si hace una actualización a una versión posterior, puede eliminar la carpeta anterior todas las configuraciones se 
almacenan en su directorio.ç
MS Windows: c:\Documents and Settings\YOUR_USER_NAME\application data\VitySoft\FRD
            y en el registro: HKEY_CURRENT_USER\Software\JavaSoft\Prefs\vitysoft\frd

Linux: ~/.FRD

NO COPIE UNA NUEVA VERSION A LA CARPETA DONDE ESTA UNA VERSION ANTERIOR

Ejecición
-----------
Windows
 Simplemente ejecute frd.exe

Linux
 Ejecute el comando ./frd.sh

Todas las plataformas
 Ejecute el comando: java -jar frd.jar

paramentros adicionales para ejecutar son:

java -jar frd.jar [-h -v -d -D<property>=<value>


opciones
  -h (--help,-?)      muestra este mensaje
  -v (--version)      muestra la información de la versión y termina
  -d (--debug)        muestra información de depuración  
  -r (--reset)        reinicia las propiedades de los usuarios a sus valores por defecto
  -m (--minim)        minimiza la ventana principal al iniciar 
  -Dproperty=value    Pasa el la propiedad y su valor a la aplicación (frecuentemente para propocitos de depuración)

f value of option -D is set 'default' (without ') default value will be used.

ejemplo - ejecucion de la aplicacion en modo de depuración
  Windows : frd.exe --debug
  Linux/MacOS: java -jar frd.jar --debug


Mas Informacón:
  - Tutorial no oficial para usuarios - como confugurar FreeRapid Downloader en Linux (en español)
    http://manualinux.my-place.us/freerapid.html


    
IV.    Errores conocidos y limitaciones
========================================
- La aplicación no iniciara si esta localizada en una ruta con caracteres especiales como "+" o "%"
   X Por favor, mueva la aplicación a otra ubicación sin tales caracteres
- ESET "Smart" Antivirus en Windows bloquea el inicio de la plicacion
   X Corrija las configuraciones de su programa antivirus o ejecute antivirus con la opción: frd.exe -Doneinstance=false 
- Siempre cierre FRD apropiadamente, o puede perder su lista de archivos (ejemplo apagar forzadamente Windows)
- Selecionar desde el final hasta el principio de la lista en la ventana principal mientras descarga parcialmente 
  desaparece :-(
   X seleccione la fila de la tabla hjaciendo ctrl+click del raton o seleccione los items desde el inicio hasta el final
- substance look and feel lanza una excepción org.jvnet.substance.api.UiThreadingViolationException:
						Component creation must be done on Event Dispatch Thread
   X ignore esta excepción en el app.log
- java.lang.UnsupportedClassVersionError exception
    X uste esta usando una versión antigua de java, usted debe usar la version 6 o posterior de Java
- DirectoryChooser lanza java.lang.InternalError o se congela en Windows Vista (64bit)
    X ignore esta excepción en el app.log
- java.lang.ClassCastException: java.awt.TrayIcon cannot be cast to java.awt.Component
    X ignore esta excepción en el app.log
- Usuarios de linux informan que no se esta mostrando un icono en la bandeja en linux
    X la única solución para este problema podria ser una actualización del JRE a la version 1.6.0_10-rc o posterior

V.    Troubleshooting
=======================================
1. Revise la sección IV - para algun error conocido o limitación
2. ¿Haz intentado salir de la aplicación y volver a entrar? :)
3. Revisa la pagina http://wordrider.net/freerapid y/o el seguimiento de errores en http://bugtracker.wordrider.net/
   para un posible nuevo error conocido.
4. Puede intentar eliminar los archivos de configuración (su ubicación esta descrita en la sección VI -  Instalación )  
5. Ejecute la aplicacion en modo de depuración
   Windows OS: frd.exe --debug
   Linux/MacOS: java -jar frd.jar --debug
6. Informe el problema con el app.log como es descrito en la sección VI


VI.    Bug report
=======================================
If you see a bug, please do not assume that i know about it. Let me know as soon as possible so that i can fix it before
the next release. Since my resources are limited, i can not backport bug fixes to earlier releases.
To report a bug, you can use the issue tracker (preferred), project forums or my personal e-mail.

Please report your JRE and OS version and attach file app.log (which is located in FreeRapid's folder).
It can help us to recognize a problem. You can also help us if you run application with --debug parameter.
We ignore simple reports like "I cannot run FRD. What should I do?". Describe your situation and application behaviour.


issue tracker: http://bugtracker.wordrider.net/
forum: http://wordrider.net/forum/list.php?7
mail: bugs@wordrider.net (your mail can be caught by spam filter, so this way is NOT preffered!

VI.    Informe de errores
=======================================
Si usted encuentra algun error en la aplicación, por favor no asuma que ya lo se, hagamelo saber tan pronto sea posible
para solucionarlo antes de la proxima liberación, ya que mis recursos son limitados, no puedo hacer correcciones a errores
a versiones anteriores, para informar un error, usted puede usar el issue tracker (recomendado), foros del proyecto o mi 
correo personal.

Por favor, indique su version de JRE y de su sistema operativo y adjunte el archivo app.log (el cual esta en la carpeta de 
FreeRapid) esto puede ayudarnos a reconocer el problema. Usted puede Ayudarnos tambien si ejecuta la aplicación con el 
parametro --debug. Siempre ignoramos preguntas como "No puedo ejecutar FRD. ¿Qué puedo hacer?". Describa su situacion y el comportamiento de la aplicación 

issue tracker: http://bugtracker.wordrider.net/
foros: http://wordrider.net/forum/list.php?7
mail: bugs@wordrider.net (su correo puede ser atrapado por el filtro antispam, asi que esta forma así que NO 
prefiera esta forma)



VII.    Donate
=======================================
FreeRapid downloader is distributed as freeware, but if you wish to express your appreciation for the time and resources
the author has spent developing, we do accept and appreciate monetary donations.
We are students and we must pay for webhosting, bills for our girlfriends etc...

PayPal: http://wordrider.net/freerapid/paypal
   or
use bank account described on the homepage http://wordrider.net/freerapid/

VII.    Donaciones
=======================================
FreeRapid Downloader es distribuido como freeware, pero sio desea expresar su apreciación por el tiempo y recursos que el
autor ha utilizado desarrollandolo, lo aceptaremos y aceptamos donaciones monetarias.
Somos estudianes y debemos pagar nuestro hosting, la cuenta para nuestras noviasm etc...

PayPal: http://wordrider.net/freerapid/paypal
   o
use el numero de cuenta descrito en nuestra pagina  http://wordrider.net/freerapid/


VIII.   FAQ
=======================================

Q: Why did you create another "RapidShare Downloader"?
A: 1) Because I don't want to be dependant on the russian software, which is probably full of malware and spyware.
   2) Because I can simply fix automatic downloading myself.
   3) Because other existing downloaders have unintuitive user interface and missing important features.
   4) Because I can.

Q: How to enable a support for shutdown commands on Linux and MacOS?
A: Please see 'syscmd.properties' configuration file in application directory for more details.

VIII.   FAQ
=======================================
Q: ¿Por qué creó otro "RapidShare Downloader"?
A: 1) Por que no quiero depender de un software ruso que probablemente esté lleno de malware y spyware.
   2) Por que puedo simplemente solucionar las descargas automaticas por mi mismo
   3) Por que otros programas existentes tienen interfaces poco intuitivas y pierden caracteristicas importantes.
   4) Por que Puedo.

Q: Como puedo habilitar el soporte para comandos de apagado en Linux y MacOS?
A: Por favor, vea el archivo de configuración 'syscmd.properties' en el directorio de la aplicación para mas detalles

