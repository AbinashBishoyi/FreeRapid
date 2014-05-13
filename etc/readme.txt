*********************************************************
*   FreeRapid Downloader                                *
*      by Ladislav Vitasek aka Vity                     *
*   Mail: info@wordrider.net - questions/suggestions    *
*   Forum: http://wordrider.net/forum/read.php?6,414    *
*   Last change: 10th September 2008                    *
*********************************************************

=======================================
Content:
  I.   What is FreeRapid Downloader
 II.   System requirements
III.   How to run FreeRapid
 IV.   Known problems and limitations
  V.   FAQ
=======================================



I.   What is FreeRapid Downloader
=======================================

FreeRapid downloader is an another simple Java downloader for support downloading from Rapidshare and other file share
archives.

Main features:
 - support for concurrent downloading from multiple services
 - downloading using proxy list
 - download history
 - programming interface (API) for adding other services like plugins
 - works on Linux

Misc.:
 - Drag&Drop URLs

Currently supported services are:
 -  Rapidshare.com
 -  FileFactory.com
 -  Uploaded.to
 -  ..others are coming


Bug report:
bugs@wordrider.net
http://bugtracker.wordrider.net/


II. System requirements
============================

Recommended configuration:
    * Windows 2000/XP/Linux(core 2.4)* or higher operating system
    * Pentium 800MHz processor
    * min 1024x768 screen resolution
    * 40 MB of free RAM
    * 6 MB free disk space
    * Java 2 Platform - version at least 1.6 (Java SE 6 Runtime) installed

Application needs at least Java 6.0 to start (http://java.sun.com/javase/downloads/index.jsp , JRE 6).


III.  How to run FreeRapid Downloader
=======================================

Unzip files to any of your directory.

Windows
 Simply launch frd.exe

Linux
 Run command ./frd.sh

All platforms
 Run command java -jar frd.jar



IV.   Known bugs and Limitations
=======================================

- Selection from "top to bottom" in the main table during dragging while downloading partly disappears
    X select table rows by ctrl+mouse click or select items from bottom to top
- Substance look and feel throws org.jvnet.substance.api.UiThreadingViolationException:
                                                              Component creation must be done on Event Dispatch Thread
    X ignore this exception in the app.log
- autocomplete combobox in AddUrls dialog throws exception
- This application does not work on MacOS, because Java 6 was not implemented on this platform yet.


V.   FAQ
=======================================

Q: Why did you create another "RapidShare Downloader"?
A: 1) Because I don't want to be addicted on the russian software, which is probably full of malware and spyware.
   2) Because I can simply fix automatic downloading myself.
   3) Because I can.

