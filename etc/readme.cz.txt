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
*                                                            *
*     FreeRapid Downloader                                   *
*      - Ladislav Vitasek aka Vity (c) 2008                  *
*   Homepage/Forum/Bugtracker: http://wordrider.net/freerapid*
*   E-Mail: info@wordrider.net - suggestions                 *
*   Poslední zmìna: 2008-10-25                               *
**************************************************************

=======================================
Obsah:
   I.   Co je FreeRapid Downloader
  II.   Systémové poadavky
 III.   Jak spustit FreeRapid
  IV.   Známé problémy a omezení
   V.   Øešení problémù
  VI.   Hlášení chyb
 VII.   Jak podpoøit FreeRapid
VIII.   Èasto kladené otázky a odpovìdi
=======================================


I.    Co je FreeRapid Downloader
=======================================

FreeRapid downloader je jednoduchá aplikace napsaná v jazyku Java, která umoòuje pohodlné stahování souborù z datového uloištì Rapidshare a mnoha dalších slueb.

Hlavní vlastnosti:
 - stahování souborù z více slueb najednou
 - monost pouítí seznamu proxy serverù
 - historie stahování
 - sledování schránky
 - rozhraní pro programování aplikací (API) pro pøidání dalších slueb jako pluginy
 - automatické ukonèení
 - monost spuštìní pod operaèními systémy Windows, Linux a MacOS


Rùzné:
 - Drag&Drop URL adres

V souèasné dobì jsou podporovány následující sluby:
 -  Rapidshare.com
 -  FileFactory.com
 -  Uploaded.to
 -  MegaUpload.com
 -  DepositFiles.com
 -  NetLoad.in
 -  Megarotic.com a Sexuploader.com
 -  Share-online.biz
 -  Egoshare.com
 -  Easy-share.com
 -  Letibit.net
 -  XtraUpload.de
 -  Shareator.com
 -  Load.to
 -  Iskladka.cz
 -  Uloz.to
 -  HellShare.com
 -  QuickShare.cz


 II.    Systémové poadavky
=======================================

Doporuèená konfigurace
    * Windows 2000/XP/Linux(jádro 2.4)* nebo vyšší operaèní systém
    * procesor Pentium 800MHz
    * minimální rozlišení obrazovky 1024x768
    * 40 MB volné operaèní pamìti
    * 10 MB volného prostoru na pevném disku
    * Java 2 Platform - nainstalovaná alespoò verze 1.6 (Java SE 6 Runtime)

Aplikace pro své spuštìní vyaduje mít nainstalovanou alespoò Javu 6.0 (http://java.sun.com/javase/downloads/index.jsp , JRE 6).

Uivatelé Linuxu (Debian) like mohou pouít tento pøíkaz k instalaci Javy:
     sudo apt-get install sun-java6-jre

III.   Jak spustit FreeRapid
=======================================

Instalace
------------
Rozbalte archiv se soubory do libovolného adresáøe na pevném disku (cesta k aplikaci by nemìla obsahovat speciální znaky typu '+', '!').
V pøípadì pøechodu na novìjší verzi smate pøedchozí sloku aplikace. Veškerá uivatelská nastavení jsou
zachována. Uloená uivatelská nastavení naleznete:
MS Windows: c:\Documents and Settings\YOUR_USER_NAME\application data\VitySoft\FRD
            and in registry HKEY_CURRENT_USER\Software\JavaSoft\Prefs\vitysoft\frd
Linux: ~/.FRD

NEKOPÍRUJTE novou verzi programu pøes starou.


Spuštìní
-----------
Windows
 Spuste jednoduše frd.exe

Linux
 Spuste pøíkazem ./frd.sh

All platforms
 Spuste pøíkazem java -jar frd.jar


volitelné parametry pøi spuštìní:

java -jar frd.jar [-h -v -d -D<property>=<value>]

volby
  -h (--help,-?)      tisk této zprávy
  -v (--version)      tisk informací o verzi a ukonèení
  -d (--debug)        tisk podrobnìjších informací o bìhu programu
  -r (--reset)        reset uivatelskıch nastavení do vıchozího stavu
  -m (--minim)        minimalizovat aplikaci po startu  
  -Dproperty=value    Nastavení interních hodnot vlastností (vìtšinou pro úèely ladìní, testování)

Pokud je hodnota volby -D nastavena na 'default' (bez '), pouije se vıchozí hodnota.

Pøíklad - spuštìní aplikace v ladícím módu:
  Windows OS: frd.exe --debug
  Linux/MacOS: java -jar frd.jar --debug


Více informací:
  - Neoficiální tutoriál pro uivatele Linuxu - Jak nakonfigurovat FreeRapid Downloader na Linuxu (ve španìlštinì)
    http://manualinux.my-place.us/freerapid.html


IV.    Známé problémy a omezení
=======================================
- Aplikace se nespustí, jestlie cesta k ní obsahuje speciální znaky typu '+', '%'
  - X zmìòte prosím umístìní aplikace tak, aby cesta k ní neobsahovala speciální znaky
- ESET "Smart" Antivirus na operaèním systému Windows blokuje spuštìní FRD
  - X proveïte správné nastavení vašeho antivirového programu nebo spouštìjte FRD tímto zpùsobem: frd.exe -Doneinstance=false
- Vdy øádnì ukonèete FRD, jinak mùe dojít ke ztrátì Vašeho seznamu souborù (eg. Windows shutdown with force option...)          
- Vıbìr stahovanıch souborù taením myší od shora dolù z nìjakého dùvodu zlobí :-(
    X vybírejte øádky v tabulce pomocí ctrl+kliknutí myší nebo vybírejte poloky taením od sdola nahoru
- Substance look and feel vyhazuje vıjimku org.jvnet.substance.api.UiThreadingViolationException:
                                                     Component creation must be done on Event Dispatch Thread
    X ignorujte tuto vıjimku v souboru app.log
- Vıjimka java.lang.UnsupportedClassVersionError
    X pravdìpodobnì pouíváte starší verzi Javy (aplikace vyaduje Sun Java verze 6 èi novìjší)
- DirectoryChooser vyhazuje vıjimku java.lang.InternalError nebo zamrzá na Windows Vista (64bit)
    X ignorujte tuto vıjimku v souboru app.log
- java.lang.ClassCastException: java.awt.TrayIcon cannot be cast to java.awt.Component
    X ignorujte tuto vıjimku v souboru app.log    
- Uivatelùm Linuxu se nezobrazuje ikona v oznamovací oblasti
    X jedinım øešením tohoto problému by mohl bıt pøechod JRE na verzi 1.6.0_10-rc èi novìjší


V.    Øešení problémù
=======================================
1. Nejprve si projdìte sekci IV kvùli známım chybám a omezením
2. Pokusil(a) jste se ji aplikaci ukonèit a opìt spustit? :-)
3. Navštivte domovskou stránku http://wordrider.net/freerapid a/nebo issue tracker na adrese http://bugtracker.wordrider.net/
   pro vloení oznámení o nalezené chybì
4. Mùete se pokusit odstranit konfiguraèní soubory (jejich umístìní naleznete v sekci III - Instalace)
5. Spuste aplikaci v ladícím módu:
   Windows OS: frd.exe --debug
   Linux/MacOS: java -jar frd.jar --debug
6. Nahlašte problém dle pokynù v sekci VI


VI.    Hlášení chyb
=======================================
Pokud objevíte chybu, nahlašte mi ji co nejdøíve, aby mohla bıt opravena pøed vydáním další verze.
K nahlášení chyby  mùete pouít issue tracker (preferováno), fóra projektu èi mùj osobní e-mail.

Napište prosím Vaši verzi JRE a operaèního systému a pøilote soubor app.log (naleznete jej ve sloce aplikace).
Usnadní nám to práci pøi identifikaci problému. Také nám velmi pomùe, pokud spustíte aplikaci s parametrem --debug.
Ignorujeme jednoduchá hlášení typu "Nejde mi spustit FRD. Co mám dìlat?". Popište Vaši situaci a chování aplikace.


issue tracker: http://bugtracker.wordrider.net/
fórum: http://wordrider.net/forum/list.php?7
e-mail: bugs@wordrider.net (Vaše zpráva mùe bıt odchycena spamovım filtrem, proto tento zpùsob NEdoporuèuji!)


VII.    Jak podpoøit FreeRapid
=======================================
FreeRapid downloader je distribuován jako freeware. Pokud se Vám aplikace líbí a rádi byste chtìli ocenit tvùrce
nìjakou finanèní èástkou za èas a zdroje vynaloené do vıvoje , velmi nás to potìší.
Jsme obyèejní studenti, kteøí musí platit webhosting, úèty za naše pøítelkynì atd...

PayPal: http://wordrider.net/freerapid/paypal
   nebo
pouijte bankovní úèet, jak je popsáno na domovské stránce http://wordrider.net/freerapid/


VIII.   Èasto kladené otázky a odpovìdi
=======================================

Q: Proè jsi vytvoøil další "RapidShare Downloader"?
A: 1) Protoe nechci pouívat ruskı software, kterı je pravdìpodobnì plnı malwaru a spywaru.
   2) Protoe si mùu sám jednoduše opravit automatické stahování.
   3) Protoe ostatní existující aplikace jsou uivatelsky nepøívìtivé a èasto postrádají dùleité funkce.
   4) Protoe mùu. :-)

Q: Jak zapnout podporu pøíkazù pro vypínání na Linuxu a MacOS?
A: Pro více informací nahlédnìte prosím do konfiguraèního souboru 'syscmd.properties', kterı naleznete ve sloce aplikace.
