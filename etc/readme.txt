======================================
|  FreeRapid Downloader - by Vity    |
======================================

FreeRapid downloader is an another simple Java downloader for support downloading from Rapidshare and other file share archives.
Main features:
- support for concurrent downloading from multiple services
- downloading using proxy list
- download history
- programming interface (API) for adding other services like plugins

Currently supported services are:
Rapidshare.com
FileFactory.com
Uploaded.to

Bug report:
bugs@wordrider.net
http://bugtracker.wordrider.net/

Application needs at least Java 6.0 to start.

Release 0.5 notes
=================

Known problems:
-----------------

- Selection from top to down in the main table during downloading disappears
    X select table rows by ctrl+mouse click or select items from bottom to top
- Substance look and feel throws org.jvnet.substance.api.UiThreadingViolationException: Component creation must be done on Event Dispatch Thread
    X ignore this exception in the app.log
- autocomplete combobox in AddUrls dialog throws exception 
    
