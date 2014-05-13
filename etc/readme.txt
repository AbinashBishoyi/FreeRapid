**************************************************************
*   FreeRapid Downloader                                     *
*      by Ladislav Vitasek aka Vity                          *
*   Website/Forum/Bugtracker: http://wordrider.net/freerapid *
*   Mail: info@wordrider.net - suggestions                   *
*   Last change: 25th October 2008                           *
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
 - clipboard monitoring 
 - programming interface (API) for adding other services like plugins
 - auto shutdown 
 - works on Linux and MacOS


Misc.:
 - Drag&Drop URLs

Currently supported services are:
 -  Rapidshare.com (for Premium account see Homepage for more details)
 -  FileFactory.com
 -  Uploaded.to
 -  MegaUpload.com
 -  DepositFiles.com
 -  NetLoad.in
 -  Megarotic.com and Sexuploader.com
 -  Share-online.biz
 -  Egoshare.com
 -  Easy-share.com
 -  Letibit.net
 -  XtraUpload.de
 -  Shareator.com
 -  Load.to
 -  Iskladka.cz
 -  Uloz.to

 II.    System requirements
=======================================

Recommended configuration:
    * Windows 2000/XP/Linux(core 2.4)* or higher operating system
    * Pentium 800MHz processor
    * min 1024x768 screen resolution
    * 40 MB of free RAM
    * 10 MB free disk space
    * Java 2 Platform - version at least 1.6 (Java SE 6 Runtime) installed

Application needs at least Java 6.0 to start (http://java.sun.com/javase/downloads/index.jsp , JRE 6).


III.   How to run FreeRapid Downloader
=======================================

Installation
------------
Unzip files to any of your directory, but beware special characters (like '+' or '!') on the path.
If you make an upgrade to higher version, you can delete previous folder. All user
settings are preserved. All user settings are saved in home directories:
MS Windows: c:\Documents and Settings\YOUR_USER_NAME\application data\VitySoft\FRD
            and in registry HKEY_CURRENT_USER\Software\JavaSoft\Prefs\vitysoft\frd
Linux: ~/.FRD

DO NOT copy new version over older one.


Launching
-----------
Windows
 Simply launch frd.exe

Linux
 Run command ./frd.sh

All platforms
 Run command java -jar frd.jar


additional parameters for launching are:

java -jar frd.jar [-h -v -d -D<property>=<value>]

options
  -h (--help,-?)      print this message
  -v (--version)      print the version information and exit
  -d (--debug)        print debugging information
  -r (--reset)        reset user properties to default values  
  -m (--minim)        minimize main window on start  
  -Dproperty=value    Passes properties and values to the application (mostly for debug or testing purposes)

If value of option -D is set 'default' (without ') default value will be used.

Example - running application in debug mode:
  Windows OS: frd.exe --debug
  Linux/MacOS: java -jar frd.jar --debug


More Info:
  - Unofficial tutorial for Linux users - How to configure FreeRapid Downloader on Linux (in Spanish)
    http://manualinux.my-place.us/freerapid.html
    

IV.    Known bugs and Limitations
=======================================
- Application will not start if it's placed on the path with special characters like '+' or '%'
  - X please move application to another location without such characters
- ESET "Smart" Antivirus on Windows OS blocks FRD to start
  - X make correct settings of your antivirus program or run FRD this way: frd.exe -Doneinstance=false
- Always close FRD properly otherwise you can loose your file list (eg. Windows shutdown with force option...)          
- Selection from "top to bottom" in the main table during dragging while downloading partly disappears :-(
    X select table rows by ctrl+mouse click or select items from bottom to top
- Substance look and feel throws org.jvnet.substance.api.UiThreadingViolationException:
                                                     Component creation must be done on Event Dispatch Thread
    X ignore this exception in the app.log
- java.lang.UnsupportedClassVersionError exception
    X You are using old Java version, you should use version Sun Java version 6 or newer
- DirectoryChooser throws java.lang.InternalError or freezes on Win Vista (64bit)
    X ignore this exception in the app.log
- java.lang.ClassCastException: java.awt.TrayIcon cannot be cast to java.awt.Component
    X ignore this exception in the app.log    
- Linux users reported not showing icon in tray on Linux
    X the only one known solution for this problem could be an upgrade JRE to version 1.6.0_10-rc or higher


IV.    Troubleshooting
=======================================
1. Check section IV - for already known bugs and limitations first
2. Have you tried to application turn off and on again? :-)
3. Check homepage http://wordrider.net/freerapid and/or issue tracker at http://bugtracker.wordrider.net/
   for possible new known bug
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


issue tracker: http://bugtracker.wordrider.net/
forum: http://wordrider.net/forum/list.php?7
mail: bugs@wordrider.net (your mail can be caught by spam filter, so this way is NOT preffered!)


VII.    Donate
=======================================
FreeRapid downloader is distributed as freeware, but if you wish to express your appreciation for the time and resources
the author has spent developing, we do accept and appreciate monetary donations.
We are students and we must pay for webhosting, bills for our girlfriends etc...

PayPal: http://wordrider.net/freerapid/paypal
   or
use bank account described on the homepage http://wordrider.net/freerapid/


VIII.   FAQ
=======================================

Q: Why did you create another "RapidShare Downloader"?
A: 1) Because I don't want to be dependant on the russian software, which is probably full of malware and spyware.
   2) Because I can simply fix automatic downloading myself.
   3) Because other existing downloaders have unintuitive user interface and missing important features.
   4) Because I can.

Q: How to enable a support for shutdown commands on Linux and MacOS?
A: Please see 'syscmd.properties' configuration file in application directory for more details.
