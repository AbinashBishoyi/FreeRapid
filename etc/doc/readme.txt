**************************************************************
 _____              ____             _     _                 *
|  ___| __ ___  ___|  _ \ __ _ _ __ (_) __| |                *
| |_ | '__/ _ \/ _ \ |_) / _` | '_ \| |/ _` |                *
|  _|| | |  __/  __/  _ < (_| | |_) | | (_| |                *
|_|  |_|  \___|\___|_| \_\__,_| .__/|_|\__,_|                *
 ____                      _  |_|            _               *
|  _ \  _____      ___ __ | | ___   __ _  __| | ___ _ __     *
| | | |/ _ \ \ /\ / / '_ \| |/ _ \ / _` |/ _` |/ _ \ '__|    *
| |_| | (_) \ V  V /| | | | | (_) | (_| | (_| |  __/ |       *
|____/ \___/ \_/\_/ |_| |_|_|\___/ \__,_|\__,_|\___|_|       *
*                                                            *
*   FreeRapid Downloader - readme.txt - English version      *
*      by Ladislav Vitasek aka Vity                          *
*   Website/Forum/Bugtracker: http://wordrider.net/freerapid *
*   Mail: info@wordrider.net - suggestions                   *
*   Last change: 10th October 2012                           *
**************************************************************

=======================================
Content:
   I.   What is FreeRapid Downloader
  II.   System requirements
 III.   How to run FreeRapid
  IV.   Known problems and limitations
   V.   Troubleshooting
  VI.   Bug report
 VII.   Donate
VIII.   FAQ
=======================================

I.    What is FreeRapid Downloader
=======================================

FreeRapid downloader is a simple Java downloader for support downloading from Rapidshare and other file share archives.

Main features:
 - support for concurrent downloading from multiple services
 - downloading using proxy list
 - download history
 - resume downloads support
 - smart clipboard monitoring
 - automatic checking for file's existence on server
 - automatic plugins updates
 - simple CAPTCHA recognition
 - auto shutdown
 - multilanguage enviroment (more than 20 languages supported)
 - many UI settings 
 - works on MS Windows, Linux and MacOS
 - looks amazingly (many color styles)
 - simply works!

Misc.:
 - Drag&Drop URLs
 - easy programming interface (API) for adding other services like plugins

Currently supported services list quite large (in alphabetical order) - more than 400 websites are supported
 - 2shared.com	             - jandown.com	             - share-rapid
 - 4shared.com	             - kewlshare.com	         - shareator.com
 - badongo.com	             - kitaupload.com	         - shareonline.biz
 - bagruj.cz	             - leteckaposta.cz	         - storage.to
 - bitroad.net	             - letitbit.net	             - stream.cz
 - cobrashare.sk	         - linkbucks.com	         - subory.sk
 - czshare.com	             - load.to	                 - tinyurl.com
 - czshare.com_profi	     -                           - ugotfile.com
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

 -  others are coming...
Note: This list might be not actual - the current list you can find at http://wordrider.net/freerapid/

 II.    System requirements
=======================================

Recommended configuration:
    * Windows 2000/XP/Vista/7/Linux(core 2.4)* or higher operating system
    * Pentium 800MHz processor
    * min 1024x768 screen resolution
    * 100 MB of free RAM
    * 20 MB free disk space
    * Java 2 Platform - version at least 1.6 (Java SE 6 Runtime) installed, version 1.7 (=7) is supported too

Application needs at least Sun Java 7.0 to start (http://java.sun.com/javase/downloads/index.jsp , JRE 7).

Linux Debian like users can use this command to intall Java:
   sudo apt-get install sun-java6-jre
  - sometimes Linux users need to uninstall their other versions of Java -
    see http://wordrider.net/freerapid/faq.html#ubuntu-install for more details.

III.   How to run FreeRapid Downloader
=======================================

Installation
------------
Unzip files to any of your directory, but beware special characters (like '+' or '!') on the path.
If you make an upgrade to higher version, you can delete previous installation folder. All user
settings are preserved, but it's recommended to backup them.
All user settings are saved in home directories:
MS Windows: c:\Documents and Settings\YOUR_USER_NAME\application data\VitySoft\FRD
            
Linux: ~/.FRD

DO NOT copy new FRD version over older one.

How to get the latest Sun Java on Linux (Ubuntu)?

Run these commands:
- apt-get update
- apt-get install sun-java6-jre
- update-java-alternatives -l
- update-java-alternatives -s java-6-sun

Launching
-----------
Windows
 Simply launch frd.exe


Linux
 Run command ./frd.sh
 (as first put correct executable rights on this file )


All platforms
 Run command java -jar frd.jar


additional parameters for launching are:

java -jar frd.jar [-h -v -d -r -D<property>=<value> -m -p]

options
  -h (--help,-?)      print this message
  -v (--version)      print the version information and exit
  -d (--debug)        print debugging information
  -r (--reset)        reset user properties to default values  
  -m (--minim)        minimize main window on start  
  -Dproperty=value    Passes properties and values to the application (mostly for debug or testing purposes)
  -p (--portable)     configuration files will be stored in the 'config'
                      folder, all file paths will be saved relatively to FRD
                      folder (if possible) - useful for USB FLASH drives

If value of option -D is set 'default' (without ') default value will be used.

Example - running application in debug mode:
  Windows OS: frd.exe --debug
  Linux/MacOS: java -jar frd.jar --debug

For setting properties you can also use file startup.properties, which is placed in FRD's directory. See its header for
 more information about content of this file.

More Info:
  - Unofficial tutorial for Linux users - How to configure FreeRapid Downloader on Linux (in Spanish)
    http://manualinux.my-place.us/freerapid.html




IV.    Known bugs and Limitations
=======================================
Always close FRD properly otherwise you can loose your file list (eg. Windows shutdown with force option...)


- MacOS: Cannot run on MacOS. FreeRapid distribution is damaged.
  - Solution - You need to change the security and privacy permission to any developer.
  - See this tutorial: https://kb.wisc.edu/helpdesk/page.php?id=25443

- Application will not start if it's placed on the path with special characters like '+' or '%'
  - X please move application to another location without such characters

- ESET "Smart" Antivirus on Windows OS blocks FRD to start
  - X make correct settings of your antivirus program or run FRD this way: frd.exe -Doneinstance=false

- Substance look and feel throws org.jvnet.substance.api.UiThreadingViolationException:
                                                     Component creation must be done on Event Dispatch Thread
    X ignore this exception in the app.log

- java.lang.UnsupportedClassVersionError exception
    X You are using old Java version, you should use Sun Java version 6 or newer

- IllegalArgumentException: 53687091 incompatible with Text-specific LCD contrast key
    X Fix your registry settings, please see forum thread (http://wordrider.net/forum/read.php?7,713,713#msg-713)

- DirectoryChooser throws java.lang.InternalError or freezes on Win Vista (64bit)
    X ignore this exception in the app.log

- IllegalStateException - Cannot open system clipboard
    X ignore this exception in the app.log

- java.lang.ClassCastException: java.awt.TrayIcon cannot be cast to java.awt.Component
    X ignore this exception in the app.log    

- Linux users reported not showing icon in tray on Linux
    X the only one known solution for this problem could be an upgrade JRE to version 1.6.0_10-rc or higher

- limitation: version 0.7x is not usable once you are using version 0.8 or newer (the only possible solution is to remove configuration files)

- FRD can't update plugins - Access denied on Windows Vista/7 if FRD's directory is placed in 'Program files' directory
    X Either move FRD to another directory or run FRD as administrator (right click->Properties->Run as administrator).

- Windows tray icon disappear when explorer.exe crashes
    X solution is unknown at this time

- FRD's GUI looks crazy on some systems (probably because of graphics drivers, DX problems...)
    X uncomment line #-DdecoratedFrames=false in startup.properties file

- Splash screen blinks for some time on start - Windows
    X Solution is unknown at this time      

IV.    Troubleshooting
=======================================
1. Check section IV - for already known bugs and limitations first
2. Have you tried to application turn off and on again? :-)
3. Check homepage http://wordrider.net/freerapid (specifically http://wordrider.net/freerapid/bugs-and-features.html)
   -  and/or issue tracker at http://bugtracker.wordrider.net/ or forum for possible new known bug
4. You can try to delete configuration files (its location is described in section VI - Installation )  
5. Run application in debug mode:
   Windows OS: frd.exe --debug
   Linux/MacOS: java -jar frd.jar --debug
6. Report problem with app.log as described in section VI


VI.    Bug report
=======================================
If you see a bug, please do not assume that i know about it. Let me know as soon as possible so that i can fix it before
the next release. Since my resources are limited, i can not backport bug fixes to earlier releases.
To report a bug, you can use the issue tracker (preferred), project forums or my personal e-mail.

Please report your JRE and OS version and attach file app.log (which is located in FreeRapid's folder).
It can help us to recognize a problem. You can also help us if you run application with --debug parameter.
We ignore simple reports like "I cannot run FRD. What should I do?". Describe your situation and application behaviour.


forum: http://wordrider.net/forum/list.php?7 (preffered)
issue tracker: http://bugtracker.wordrider.net/
mail: bugs@wordrider.net (your mail can be caught by spam filter, so this way is NOT preffered!)


VII.    Donate
=======================================
FreeRapid downloader is distributed as freeware, but if you wish to express your appreciation for the time and resources
the author has spent developing, we do accept and appreciate monetary donations.
We are students and we must pay for webhosting, bills for our girlfriends etc...

PayPal: http://vity.cz/freerapid/paypal
   or
use bank account described on the homepage http://wordrider.net/freerapid/donation.html


VIII.   FAQ
=======================================
More updated FAQs you can find in FAQ section at http://wordrider.net/freerapid


Q: Why did you create another "RapidShare Downloader"?
A: 1) Because I don't want to be dependant on the russian software, which is probably full of malware and spyware.
   2) Because I can simply fix automatic downloading myself.
   3) Because other existing downloaders have unintuitive user interface and missing important features.
   4) Because I can.

Q: How to enable a support for shutdown/restart commands on Linux and MacOS?
A: Please see 'syscmd.properties' configuration file in application directory for more details.

Q: Where are configuration files located?
A: See Installation section.

Q: How to setup Rapidshare Premium account?
A: By default Free Rapidshare plugin is used.
For using premium account go to Options->Preferences->Plugins->Settings panel, then find Rapidshare_premium plugin and activate it (click on the checkbox in the first column - X).
To setup your login details click on the Options button. Confirm account details by OK button.

Only numerical account ID is supported. Other premium plugins are not implemented yet.

Q: Windows users: How to run program from command line?
A: Go to Start->Run and type:
cmd ENTER
cd path_to_freerapid_directory' ENTER
frd.exe ENTER

if you need to enter parameters, use eg. frd.exe --portable