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
*      - Ladislav Vitasek alias Vity (c) 2008                *
*Dom.stránka/Forum/Bugtracker: http://wordrider.net/freerapid*
*E-Mail: info@wordrider.net - návrhy/                        *
*Poslední zmìna: 2008-12-25                                  *
**************************************************************

=======================================
Obsah:
   I.   Co je FreeRapid Downloader
  II.   Systémové požadavky
 III.   Jak spustit FreeRapid
  IV.   Známé problémy a omezení
   V.   Øešení problémù
  VI.   Hlášení chyb
 VII.   Jak podpoøit FreeRapid
VIII.   Èasto kladené otázky a odpovìdi
=======================================


I.    Co je FreeRapid Downloader
=======================================

FreeRapid downloader je jednoduchá aplikace napsaná v jazyku Java, která umožòuje pohodlné stahování souborù z datového uložištì Rapidshare a mnoha dalších služeb.

Hlavní vlastnosti:
 - stahování souborù z více služeb najednou
 - možnost použítí seznamu proxy serverù
 - historie stahování
 - inteligentní sledování schránky
 - automatická kontrola existence souborù (funkènosti odkazù) na vzdáleném serveru
 - automatické aktualizace pluginù
 - automatické ukonèení
 - mnoho uživatelských nastavení k pøizpùsobení zvykùm uživatele
 - možnost spuštìní pod operaèními systémy Windows, Linux a MacOS
 - vypadá skvìle a funguje! :-)


Rùzné:
 - Drag&Drop URL adres
-  jednoduche rozhraní pro programování aplikací (API) pro pøidání dalších služeb jako pluginy

V souèasné dobì jsou podporovány následující služby:
 -  Rapidshare.com (+ premium úèet)
 -  MegaUpload.com
 -  Megarotic.com and Sexuploader.com
 -  NetLoad.in
 -  MediaFire.com
 -  FileFactory.com
 -  Uploaded.to
 -  DepositFiles.com
 -  Share-online.biz
 -  Egoshare.com
 -  Easy-share.com
 -  XtraUpload.de
 -  Shareator.com
 -  SaveFile.com
 -  Load.to
 -  Iskladka.cz
 -  HellShare.com
 -  QuickShare.cz
 -  FlyShare.cz
 -  Edisk.cz
 -  Uloz.to
 -  Upnito.sk


 II.    Systémové požadavky
=======================================

Doporuèená konfigurace
    * Windows 2000/XP/Linux(jádro 2.4)* nebo vyšší operaèní systém
    * procesor Pentium 800MHz
    * minimální rozlišení obrazovky 1024x768
    * 40 MB volné operaèní pamìti
    * 10 MB volného prostoru na pevném disku
    * Java 2 Platform - nainstalovaná alespoò verze 1.6 (Java SE 6 Runtime)

Aplikace pro své spuštìní vyžaduje mít nainstalovanou alespoò Javu 6.0 (http://java.sun.com/javase/downloads/index.jsp , JRE 6).

Uživatelé Linuxu (Debian) like mohou použít tento pøíkaz k instalaci Javy:
     sudo apt-get install sun-java6-jre

III.   Jak spustit FreeRapid
=======================================

Instalace
------------
Rozbalte archiv se soubory do libovolného adresáøe na pevném disku (cesta k aplikaci by nemìla obsahovat speciální znaky typu '+', '!').
V pøípadì pøechodu na novìjší verzi smažte pøedchozí složku aplikace. Veškerá uživatelská nastavení jsou
zachována. Uložená uživatelská nastavení naleznete:
MS Windows: c:\Dokumenty a nastavení\Vaše_uživatelské_jméno\Data aplikací\VitySoft\FRD
            a v registru HKEY_CURRENT_USER\Software\JavaSoft\Prefs\vitysoft\frd
Linux: ~/.FRD

NEKOPÍRUJTE novou verzi programu pøes starou.

Pokud je použit pøepínaè -p (viz dále) je cesta ke konfiguraèním souborùm shodná s adresáøem programu.

Spuštìní
-----------
Windows
 Spuste jednoduše frd.exe

Linux
 Spuste pøíkazem ./frd.sh

All platforms
 Spuste pøíkazem java -jar frd.jar


volitelné parametry pøi spuštìní:

java -jar frd.jar [-h -v -d -D<property>=<value> -p]

volby
  -h (--help,-?)      tisk této zprávy
  -v (--version)      tisk informací o verzi a ukonèení
  -d (--debug)        tisk podrobnìjších informací o bìhu programu
  -r (--reset)        reset uživatelských nastavení do výchozího stavu
  -m (--minim)        minimalizovat aplikaci po startu  
  -Dproperty=value    Nastavení interních hodnot vlastností (vìtšinou pro úèely ladìní, testování)
  -p (--portable)     konfiguraèní soubory budou uloženy v adresáøi 'config' u programu,
                      všechny cesty (pokud je to možné) budou ukládány relativnì vùèi adresáøi programu
                      - užiteèné napø. pro USB FLASH disky


Pokud je hodnota volby -D nastavena na 'default' (bez '), použije se výchozí hodnota.

Pøíklad - spuštìní aplikace v ladícím módu:
  Windows OS: frd.exe --debug
  Linux/MacOS: java -jar frd.jar --debug


Více informací:
  - Neoficiální tutoriál pro uživatele Linuxu - Jak nakonfigurovat FreeRapid Downloader na Linuxu (ve španìlštinì)
    http://manualinux.my-place.us/freerapid.html


IV.    Známé problémy a omezení
=======================================
- Aplikace se nespustí, jestliže cesta k ní obsahuje speciální znaky typu '+', '%'
  - X zmìòte prosím umístìní aplikace tak, aby cesta k ní neobsahovala speciální znaky
- ESET "Smart" Antivirus na operaèním systému Windows blokuje spuštìní FRD
  - X proveïte správné nastavení vašeho antivirového programu nebo spouštìjte FRD tímto zpùsobem: frd.exe -Doneinstance=false
- Vždy øádnì ukonèete FRD, jinak mùže dojít ke ztrátì Vašeho seznamu souborù (eg. Windows shutdown with force option...)          
- Výbìr stahovaných souborù tažením myší od shora dolù z nìjakého dùvodu zlobí :-(
    X vybírejte øádky v tabulce pomocí ctrl+kliknutí myší nebo vybírejte položky tažením od sdola nahoru
- Substance look and feel vyhazuje výjimku org.jvnet.substance.api.UiThreadingViolationException:
                                                     Component creation must be done on Event Dispatch Thread
    X ignorujte tuto výjimku v souboru app.log
- Výjimka java.lang.UnsupportedClassVersionError
    X pravdìpodobnì používáte starší verzi Javy (aplikace vyžaduje Sun Java verze 6 èi novìjší)
- DirectoryChooser vyhazuje výjimku java.lang.InternalError nebo zamrzá na Windows Vista (64bit)
    X ignorujte tuto výjimku v souboru app.log
- java.lang.ClassCastException: java.awt.TrayIcon cannot be cast to java.awt.Component
    X ignorujte tuto výjimku v souboru app.log    
- Uživatelùm Linuxu se nezobrazuje ikona v oznamovací oblasti
    X jediným øešením tohoto problému by mohl být pøechod JRE na verzi 1.6.0_10-rc èi novìjší


V.    Øešení problémù
=======================================
1. Nejprve si projdìte sekci IV kvùli známým chybám a omezením
2. Pokusil(a) jste se již aplikaci ukonèit a opìt spustit? :-)
3. Navštivte domovskou stránku http://wordrider.net/freerapid a/nebo issue tracker na adrese http://bugtracker.wordrider.net/
   pro vložení oznámení o nalezené chybì
4. Mùžete se pokusit odstranit konfiguraèní soubory (jejich umístìní naleznete v sekci III - Instalace)
5. Spuste aplikaci v ladícím módu:
   Windows OS: frd.exe --debug
   Linux/MacOS: java -jar frd.jar --debug
6. Nahlašte problém dle pokynù v sekci VI


VI.    Hlášení chyb
=======================================
Pokud objevíte chybu, nahlašte mi ji co nejdøíve, aby mohla být opravena pøed vydáním další verze.
K nahlášení chyby  mùžete použít issue tracker (preferováno), fóra projektu èi mùj osobní e-mail.

Napište prosím Vaši verzi JRE a operaèního systému a pøiložte soubor app.log (naleznete jej ve složce aplikace).
Usnadní nám to práci pøi identifikaci problému. Také nám velmi pomùže, pokud spustíte aplikaci s parametrem --debug.
Ignorujeme jednoduchá hlášení typu "Nejde mi spustit FRD. Co mám dìlat?". Popište Vaši situaci a chování aplikace.


issue tracker: http://bugtracker.wordrider.net/
fórum: http://wordrider.net/forum/list.php?7
e-mail: bugs@wordrider.net (Vaše zpráva mùže být odchycena spamovým filtrem, proto tento zpùsob NEdoporuèuji!)


VII.    Jak podpoøit FreeRapid
=======================================
FreeRapid downloader je distribuován jako freeware. Pokud se Vám aplikace líbí a rádi byste chtìli ocenit tvùrce
nìjakou finanèní èástkou za èas a zdroje vynaložené do vývoje , velmi nás to potìší.
Jsme obyèejní studenti, kteøí musí platit webhosting, úèty za naše pøítelkynì atd...

PayPal: http://wordrider.net/freerapid/paypal
   nebo
použijte bankovní úèet, jak je popsáno na domovské stránce http://wordrider.net/freerapid/


VIII.   Èasto kladené otázky a odpovìdi
=======================================

Q: Proè jsi vytvoøil další "RapidShare Downloader"?
A: 1) Protože nechci používat ruský software, který je pravdìpodobnì plný malwaru a spywaru.
   2) Protože si mùžu sám jednoduše opravit automatické stahování.
   3) Protože ostatní existující aplikace jsou uživatelsky nepøívìtivé a èasto postrádají dùležité funkce.
   4) Protože mùžu. :-)

Q: Jak zapnout podporu pøíkazù pro vypínání na Linuxu a MacOS?
A: Pro více informací nahlédnìte prosím do konfiguraèního souboru 'syscmd.properties', který naleznete ve složce aplikace.

Q: Jak lze nastavit úèet pro Rapidshare premium?
A: Jako výchozí je zapnut plugin pro FREE Rapidshare, proto je nutné aktivovat plugin pro Premium.
Jdìte do Nastavení->Možnosti->Pluginy -> panel Nastavení , dále naleznìte plugin Rapidshare_premium a aktivujte ho (kliknìte v prvním sloupci oznaèeném jako X).
Dále kliknìte na tlaèítko možností pro zvolený RapidShare_premium plugin a vložte autentifikaèní údaje. Potvrïte tlaèítkem OK.
Podporováno je pouze èíselné uživatelské ID.