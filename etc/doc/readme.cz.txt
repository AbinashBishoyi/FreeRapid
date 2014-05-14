**************************************************************
*  _____              ____             _     _               *
* |  ___| __ ___  ___|  _ \ __ _ _ __ (_) __| |              *
* | |_ | '__/ _ \/ _ \ |_) / _` | '_ \| |/ _` |              *
* |  _|| | |  __/  __/  _ < (_| | |_) | | (_| |              *
* |_|  |_|  \___|\___|_| \_\__,_| .__/|_|\__,_|              *
*  ____                      _  |_|            _             *
* |  _ \  _____      ___ __ | | ___   __ _  __| | ___ _ __   *
* | | | |/ _ \ \ /\ / / '_ \| |/ _ \ / _` |/ _` |/ _ \ '__|  *
* | |_| | (_) \ V  V /| | | | | (_) | (_| | (_| |  __/ |     *
* |____/ \___/ \_/\_/ |_| |_|_|\___/ \__,_|\__,_|\___|_|     *
*                                                            *
*                                                            *
*     FreeRapid Downloader                                   *
*      - Ladislav Vitásek alias Vity (c) 2008-2012           *
*Dom.stránka/Forum/Bugtracker: http://wordrider.net/freerapid*
*E-Mail: info@wordrider.net - návrhy                         *
*Poslední změna: 2012-10-10                                  *
**************************************************************

Důležité - než začnete číst dále!!!:
Pokud jste FreeRapid Downloader stáhli odněkud jinud než z oficiálních stránek na http://wordrider.net/freerapid,
nespouštějte program a raději si ho znovu stáhněte.
Vyskytla se řada případů, kdy záškodníci infikovali frd.exe záškodnickým plevelem a dále to šířili jako vlastní distribuci.
Na oficiálních stránkách vždy najdete nejnovější verzi, která je v pořádku.
Toto doporučení se netýká pouze tohoto softwaru, ale obecně jakéhokoli.

=======================================
Obsah:
   I.   Co je FreeRapid Downloader
  II.   Systémové požadavky
 III.   Jak spustit FreeRapid
  IV.   Známé problémy a omezení
   V.   Řešení problémů
  VI.   Hlášení chyb
 VII.   Jak podpořit FreeRapid
VIII.   Často kladené otázky a odpovědi
=======================================


I.    Co je FreeRapid Downloader
=======================================

FreeRapid downloader je jednoduchá aplikace napsaná v jazyku Java, která umožňuje pohodlné stahování souborů z datového uložiště Rapidshare a mnoha dalších služeb.

Hlavní vlastnosti:
 - stahování souborů z více služeb najednou
 - možnost použítí seznamu proxy serverů
 - historie stahování
 - inteligentní sledování schránky
 - automatická kontrola existence souborů (funkčnosti odkazů) na vzdáleném serveru
 - automatické aktualizace pluginů
 - automatické ukončení
 - mnoho uživatelských nastavení k přizpůsobení zvykům uživatele
 - možnost spuštění pod operačními systémy Windows, Linux a MacOS
 - vypadá skvěle a funguje! :-)


Různé:
 - Drag&Drop URL adres
-  jednoduché rozhraní pro programování aplikací (API) pro přidání dalších služeb jako pluginy

V současné době jsou podporovány následující služby (řazeno abecedně):

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


  
Pozn.: Tento seznam nemusí být vždy aktuální - aktuální seznam lze nalézt na http://wordrider.net/freerapid

 II.    Systémové požadavky                                                                	            
=======================================

Doporučená konfigurace
    * Windows 2000/XP/Vista/7/Linux(jádro 2.4)* nebo vyšší operační systém
    * procesor Pentium 800MHz
    * minimální rozlišení obrazovky 1024x768
    * 100 MB volné operační paměti
    * 20 MB volného prostoru na pevném disku
    * Java 2 Platform - nainstalovaná alespoň verze 1.6 (Java SE 6 Runtime), Java SE 7 je také podporována

Aplikace pro své spuštění vyžaduje mít nainstalovanou alespoň Sun Javu 6.0 (http://java.sun.com/javase/downloads/index.jsp , JRE 7).

Uživatelé Linuxu (Debian) like mohou použít tento příkaz k instalaci Javy:
     sudo apt-get install sun-java6-jre


III.   Jak spustit FreeRapid
=======================================

Instalace
------------
Rozbalte archiv se soubory do libovolného adresáře na pevném disku (cesta k aplikaci by neměla obsahovat speciální znaky typu '+', '!').
V případě přechodu na novější verzi smažte předchozí složku aplikace. Veškerá uživatelská nastavení jsou
zachována. Pokud však již předchozí verzi používáte, je doporučeno si konfigurační data zazálohovat pro případ vrácení ke starší verzi.
Uložená uživatelská nastavení naleznete:
MS Windows: c:\Dokumenty a nastavení\Vaše_uživatelské_jméno\Data aplikací\VitySoft\FRD

Linux: ~/.FRD

NEKOPÍRUJTE novou verzi programu přes starou.

Pokud je použit přepínač -p (viz dále) je cesta ke konfiguračním souborům shodná s adresářem programu.

Jak spustit FRD na Ubuntu - nastavení správné Javy

Použijte následující příkazy:
- apt-get update
- apt-get install sun-java6-jre
- update-java-alternatives -l
- update-java-alternatives -s java-6-sun

Spuštění
-----------
Windows
 Spusťte jednoduše frd.exe

Linux
 Spusťte příkazem ./frd.sh

Všechny OS platformy
 Spusťte příkazem java -jar frd.jar


volitelné parametry při spuštění:

java -jar frd.jar [-h -v -d -D<property>=<value> -p]

volby
  -h (--help,-?)      tisk této zprávy
  -v (--version)      tisk informací o verzi a ukončení
  -d (--debug)        tisk podrobnějších informací o běhu programu
  -r (--reset)        reset uživatelských nastavení do výchozího stavu
  -m (--minim)        minimalizovat aplikaci po startu  
  -Dproperty=value    Nastavení interních hodnot vlastností (většinou pro účely ladění, testování)
  -p (--portable)     konfigurační soubory budou uloženy v adresáři 'config' u programu,
                      všechny cesty (pokud je to možné) budou ukládány relativně vůči adresáři programu
                      - užitečné např. pro USB FLASH disky


Pokud je hodnota volby -D nastavena na 'default' (bez '), použije se výchozí hodnota.

Příklad - spuštění aplikace v ladícím módu:
  Windows OS: frd.exe --debug
  Linux/MacOS: java -jar frd.jar --debug


Více informací:
  - Uživatelská příručka - http://wordrider.net/freerapid/help (lze také vyvolat stiskem klávesy F1 v programu)
  - Neoficiální tutoriál pro uživatele Linuxu - Jak nakonfigurovat FreeRapid Downloader na Linuxu (ve španělštině)
    http://manualinux.my-place.us/freerapid.html


IV.    Známé problémy a omezení
=======================================
- MacOS: Nelze spustit aplikaci na MacOS. Dostavám hlášku o tom, že FreeRapid je poškozený.
  - X - Je třeba povolit instalaci aplikací třetích stran.
  - Návod s obrázky: https://kb.wisc.edu/helpdesk/page.php?id=25443

- Aplikace se nespustí, jestliže cesta k ní obsahuje speciální znaky typu '+', '%'
  - X změňte prosím umístění aplikace tak, aby cesta k ní neobsahovala speciální znaky
- ESET "Smart" Antivirus na operačním systému Windows blokuje spuštění FRD
  - X proveďte správné nastavení vašeho antivirového programu nebo spouštějte FRD tímto způsobem: frd.exe -Doneinstance=false
- Vždy řádně ukončete FRD, jinak může dojít ke ztrátě Vašeho seznamu souborů (eg. Windows shutdown with force option...)          
- Výběr stahovaných souborů tažením myší od shora dolů z nějakého důvodu zlobí :-(
    X vybírejte řádky v tabulce pomocí ctrl+kliknutí myší nebo vybírejte položky tažením od sdola nahoru
- Substance look and feel vyhazuje výjimku org.jvnet.substance.api.UiThreadingViolationException:
                                                     Component creation must be done on Event Dispatch Thread
    X ignorujte tuto výjimku v souboru app.log
- Výjimka java.lang.UnsupportedClassVersionError
    X pravděpodobně používáte starší verzi Javy (aplikace vyžaduje Sun Java verze 6 či novější)
- Výjimka IllegalArgumentException: 53687091 incompatible with Text-specific LCD contrast key
    X Je nutné si správně nastavit (opravit) registry systému Windows, více viz (http://wordrider.net/forum/read.php?7,713,713#msg-713)     
- DirectoryChooser vyhazuje výjimku java.lang.InternalError nebo zamrzá na Windows Vista (64bit)
    X ignorujte tuto výjimku v souboru app.log
- java.lang.ClassCastException: java.awt.TrayIcon cannot be cast to java.awt.Component
    X ignorujte tuto výjimku v souboru app.log    
- Uživatelům Linuxu se nezobrazuje ikona v oznamovací oblasti
    X jediným řešením tohoto problému by mohl být přechod JRE na verzi 1.6.0_10-rc či novější
- Splash screen při startu na pár sekund bliká
    X řesení je prozatím neznámé
- Někteří uživatelé hlásili problém se zobrazením hlavního okna
    X odkomentujte položku #-DdecoratedFrames=false
- Nelze updatovat pluginy na Windows Vista/7 pokud je FRD adresář umístěn v adresáři 'Program files'
    X přesuňte adresář FRD do jiného adresáře nebo spusťte frd.exe s administrátorskými právy
      (pravé tlačítko->vlastnosti->Spustit jako administrátor)

Pozn.: Tento seznam nemusí být vždy aktuální - aktuální seznam lze nalézt na http://wordrider.net/freerapid/bugs-and-features.html

V.    Řešení problémů
=======================================
1. Nejprve si projděte sekci IV kvůli známým chybám a omezením
2. Pokusil(a) jste se již aplikaci ukončit a opět spustit? :-)
3. Navštivte domovskou stránku http://wordrider.net/freerapid/bugs-and-features.html a/nebo fórum a/nebo
   issue tracker na adrese http://bugtracker.wordrider.net/ pro vložení oznámení o nalezené chybě
4. Můžete se pokusit odstranit konfigurační soubory (jejich umístění naleznete v sekci III - Instalace)
5. Spusťte aplikaci v ladícím módu:
   Windows OS: frd.exe --debug
   Linux/MacOS: java -jar frd.jar --debug
6. Nahlašte problém dle pokynů v sekci VI


VI.    Hlášení chyb
=======================================
Pokud objevíte chybu, nahlašte mi ji co nejdříve, aby mohla být opravena před vydáním další verze.
K nahlášení chyby  můžete použít issue tracker (preferováno), fóra projektu či můj osobní e-mail.

Napište prosím Vaši verzi JRE a operačního systému a přiložte soubor app.log (naleznete jej ve složce aplikace).
Usnadní nám to práci při identifikaci problému. Také nám velmi pomůže, pokud spustíte aplikaci s parametrem --debug.
Ignorujeme jednoduchá hlášení typu "Nejde mi spustit FRD. Co mám dělat?". Popište Vaši situaci a chování aplikace,
jedině tak Vám jsme schopni pomoci.


issue tracker: http://bugtracker.wordrider.net/
fórum: http://wordrider.net/forum/list.php?7
e-mail: bugs@wordrider.net (Vaše zpráva může být odchycena spamovým filtrem, proto tento způsob NEdoporučuji!)


VII.    Jak podpořit FreeRapid
=======================================
FreeRapid downloader je distribuován jako freeware. Pokud se Vám aplikace líbí a rádi byste chtěli ocenit tvůrce
nějakou finanční částkou za čas a zdroje vynaložené do vývoje , velmi nás to potěší.
Jsme obyčejní studenti, kteří musí platit webhosting, účty za naše přítelkyně atd...

PayPal: http://vity.cz/freerapid/paypal
   nebo
použijte bankovní účet, jak je popsáno na domovské stránce http://wordrider.net/freerapid/donation.html

Děkujeme.

VIII.   Často kladené otázky a odpovědi
=======================================
Aktuální seznam FAQ lze nalézt zde: http://wordrider.net/freerapid/faq.html


Q: Proč jsi vytvořil další "RapidShare Downloader"?
A: 1) Protože nechci používat ruský software, který je pravděpodobně plný malwaru a spywaru.
   2) Protože si můžu sám jednoduše opravit automatické stahování.
   3) Protože ostatní existující aplikace jsou uživatelsky nepřívětivé a často postrádají důležité funkce.
   4) Protože můžu. :-)

Q: Jak zapnout podporu příkazů pro vypínání na Linuxu a MacOS?
A: Pro více informací nahlédněte prosím do konfiguračního souboru 'syscmd.properties', který naleznete ve složce aplikace.

Q: Jak lze nastavit účet pro Rapidshare premium?
A: Jako výchozí je zapnut plugin pro FREE Rapidshare, proto je nutné aktivovat plugin pro Premium.
Jděte do Nastavení->Možnosti->Pluginy -> panel Nastavení , dále nalezněte plugin Rapidshare_premium a aktivujte ho (klikněte v prvním sloupci označeném jako X).
Dále klikněte na tlačítko možností pro zvolený RapidShare_premium plugin a vložte autentifikační údaje. Potvrďte tlačítkem OK.
Podporováno je pouze číselné uživatelské ID.

Q: Uživatelé Windows - jak spustit program z příkazové řádky?
A: Start->Spustit
cmd ENTER
cd cesta_k_adresari_freerapidu ENTER
frd.exe ENTER
pokud chcete napsat parametry tak např. frd.exe --debug 