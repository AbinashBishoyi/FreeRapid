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
*      - Ladislav Vitásek alias Vity (c) 2008-2011           *
*Dom.stránka/Forum/Bugtracker: http://wordrider.net/freerapid*
*E-Mail: info@wordrider.net - návrhy                         *
*Poslední zmìna: 2011-10-29                                  *
**************************************************************

Dùležité - než zaènete èíst dále!!!:
Pokud jste FreeRapid Downloader stáhli odnìkud jinud než z oficiálních stránek na http://wordrider.net/freerapid,
nespouštìjte program a radìji si ho znovu stáhnìte.
Vyskytla se øada pøípadù, kdy záškodníci infikovali frd.exe záškodnickým plevelem a dále to šíøili jako vlastní distribuci.
Na oficiálních stránkách vždy najdete nejnovìjší verzi, která je v poøádku.
Toto doporuèení se netýká pouze tohoto softwaru, ale obecnì jakéhokoli.

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
-  jednoduché rozhraní pro programování aplikací (API) pro pøidání dalších služeb jako pluginy

V souèasné dobì jsou podporovány následující služby (øazeno abecednì):

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

Doporuèená konfigurace
    * Windows 2000/XP/Vista/7/Linux(jádro 2.4)* nebo vyšší operaèní systém
    * procesor Pentium 800MHz
    * minimální rozlišení obrazovky 1024x768
    * 40 MB volné operaèní pamìti
    * 10 MB volného prostoru na pevném disku
    * Java 2 Platform - nainstalovaná alespoò verze 1.6 (Java SE 6 Runtime)

Aplikace pro své spuštìní vyžaduje mít nainstalovanou alespoò Sun Javu 6.0 (http://java.sun.com/javase/downloads/index.jsp , JRE 6).

Uživatelé Linuxu (Debian) like mohou použít tento pøíkaz k instalaci Javy:
     sudo apt-get install sun-java6-jre


III.   Jak spustit FreeRapid
=======================================

Instalace
------------
Rozbalte archiv se soubory do libovolného adresáøe na pevném disku (cesta k aplikaci by nemìla obsahovat speciální znaky typu '+', '!').
V pøípadì pøechodu na novìjší verzi smažte pøedchozí složku aplikace. Veškerá uživatelská nastavení jsou
zachována. Pokud však již pøedchozí verzi používáte, je doporuèeno si konfiguraèní data zazálohovat pro pøípad vrácení ke starší verzi.
Uložená uživatelská nastavení naleznete:
MS Windows: c:\Dokumenty a nastavení\Vaše_uživatelské_jméno\Data aplikací\VitySoft\FRD

Linux: ~/.FRD

NEKOPÍRUJTE novou verzi programu pøes starou.

Pokud je použit pøepínaè -p (viz dále) je cesta ke konfiguraèním souborùm shodná s adresáøem programu.

Jak spustit FRD na Ubuntu - nastavení správné Javy

Použijte následující pøíkazy:
- apt-get update
- apt-get install sun-java6-jre
- update-java-alternatives -l
- update-java-alternatives -s java-6-sun

Spuštìní
-----------
Windows
 Spuste jednoduše frd.exe

Linux
 Spuste pøíkazem ./frd.sh

Všechny OS platformy
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
  - Uživatelská pøíruèka - http://wordrider.net/freerapid/help (lze také vyvolat stiskem klávesy F1 v programu)
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
- Výjimka IllegalArgumentException: 53687091 incompatible with Text-specific LCD contrast key
    X Je nutné si správnì nastavit (opravit) registry systému Windows, více viz (http://wordrider.net/forum/read.php?7,713,713#msg-713)     
- DirectoryChooser vyhazuje výjimku java.lang.InternalError nebo zamrzá na Windows Vista (64bit)
    X ignorujte tuto výjimku v souboru app.log
- java.lang.ClassCastException: java.awt.TrayIcon cannot be cast to java.awt.Component
    X ignorujte tuto výjimku v souboru app.log    
- Uživatelùm Linuxu se nezobrazuje ikona v oznamovací oblasti
    X jediným øešením tohoto problému by mohl být pøechod JRE na verzi 1.6.0_10-rc èi novìjší
- Splash screen pøi startu na pár sekund bliká
    X øesení je prozatím neznámé
- Nìkteøí uživatelé hlásili problém se zobrazením hlavního okna
    X odkomentujte položku #-DdecoratedFrames=false
- Nelze updatovat pluginy na Windows Vista/7 pokud je FRD adresáø umístìn v adresáøi 'Program files'
    X pøesuòte adresáø FRD do jiného adresáøe nebo spuste frd.exe s administrátorskými právy
      (pravé tlaèítko->vlastnosti->Spustit jako administrátor)

Pozn.: Tento seznam nemusí být vždy aktuální - aktuální seznam lze nalézt na http://wordrider.net/freerapid/bugs-and-features.html

V.    Øešení problémù
=======================================
1. Nejprve si projdìte sekci IV kvùli známým chybám a omezením
2. Pokusil(a) jste se již aplikaci ukonèit a opìt spustit? :-)
3. Navštivte domovskou stránku http://wordrider.net/freerapid/bugs-and-features.html a/nebo fórum a/nebo
   issue tracker na adrese http://bugtracker.wordrider.net/ pro vložení oznámení o nalezené chybì
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
Ignorujeme jednoduchá hlášení typu "Nejde mi spustit FRD. Co mám dìlat?". Popište Vaši situaci a chování aplikace,
jedinì tak Vám jsme schopni pomoci.


issue tracker: http://bugtracker.wordrider.net/
fórum: http://wordrider.net/forum/list.php?7
e-mail: bugs@wordrider.net (Vaše zpráva mùže být odchycena spamovým filtrem, proto tento zpùsob NEdoporuèuji!)


VII.    Jak podpoøit FreeRapid
=======================================
FreeRapid downloader je distribuován jako freeware. Pokud se Vám aplikace líbí a rádi byste chtìli ocenit tvùrce
nìjakou finanèní èástkou za èas a zdroje vynaložené do vývoje , velmi nás to potìší.
Jsme obyèejní studenti, kteøí musí platit webhosting, úèty za naše pøítelkynì atd...

PayPal: http://vity.cz/freerapid/paypal
   nebo
použijte bankovní úèet, jak je popsáno na domovské stránce http://wordrider.net/freerapid/donation.html

Dìkujeme.

VIII.   Èasto kladené otázky a odpovìdi
=======================================
Aktuální seznam FAQ lze nalézt zde: http://wordrider.net/freerapid/faq.html


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

Q: Uživatelé Windows - jak spustit program z pøíkazové øádky?
A: Start->Spustit
cmd ENTER
cd cesta_k_adresari_freerapidu ENTER
frd.exe ENTER
pokud chcete napsat parametry tak napø. frd.exe --debug 