?**************************************************************
*   FreeRapid Downloader                                     *
*      by Ladislav Vitasek aka Vity                          *
*   Website/Forum/Bugtracker: http://wordrider.net/freerapid *
*   Mail: info@wordrider.net - suggestions                   *
*   Last change: 20th December 2008                          *
**************************************************************

=======================================
Sadržaj:
   I.   Što je FreeRapid Downloader
  II.   Zahtjev Sistema
 III.   Kako pokrenuti FreeRapid
  IV.   Poznati problemi i ograničenja
   V.   Rješavanje problema
  VI.   Prijava Bugova
 VII.   Donacije
VIII.   FAQ (pitanja i odgovori)
=======================================


I.    Što je FreeRapid Downloader
=======================================

FreeRapid downloader je jednostavan Java downloader koji podržava preuzimanje s Rapidshare-a i drugih file share arhiva.

Glavne osobine:
 - Podržava istodobno preuzimanje sa više servisa
 - preuzimanje koristeći proxy listu
 - povijest preuzimanja
 - pametno praćenje sistemskog spremnika
 - automatska provjera postojanja datoteke na serveru
 - automatska nadogradnja pluginova
 - jednostavno CAPTCHA prepoznavanje
 - automatsko gašenje
 - puno UI postavki 
 - radi na MS Windowsima, Linuxu and MacOS-u
 - očaravajući izgled (puno šarenih stilova)
 - jednostavno - radi!

Misc.:
 - Drag&Drop URL-ova
 - lak programming interface (API) za dodavanje drugih servisa kao pluginova

Trenutačno podržani servisi su (abecednim redom):
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


Primjedba: Ova lista možda nije aktualna - Trenutačnu listu možete pronaći na http://wordrider.net/freerapid/

 II.    Zahtjev Sistema
=======================================

Preporučena konfiguracija:
    * Windows 2000/XP/Vista/7/Linux(core 2.4)* ili noviji operativni sistem
    * Pentium 800MHz procesor
    * min 1024x768 rezolucija ekrana
    * 40 MB slobodne RAM memorije
    * 10 MB slobodnog mjesta na disku
    * Java 2 Platforma - verzija najmanje 1.6 (Java SE 6 Runtime) instalirana

Aplikacija zahtjeva najmanje Sun Java 6.0 za pokretanje (http://java.sun.com/javase/downloads/index.jsp , JRE 6).

Linux Debian korisnici mogu upotrijebiti ovu komandu da bi instalirali Javu:
   sudo apt-get install sun-java6-jre

III.   Kako pokrenuti FreeRapid?
=======================================

Instalacija
------------
Razipujte datoteke u bilo koju mapu, ali ne koristite specijalne karaktere (kao '+' ili '!') u putanji.
Ako radite nadogradnju na višu verziju, možete obrisati prethodnu mapu. Sve korisničke postavke su sačuvane. Sve korisničke postavke su pohranjene u mapama:
MS Windows: c:\Documents and Settings\YOUR_USER_NAME\application data\VitySoft\FRD

Linux: ~/.FRD

NEMOJTE prepisati novu verziju preko stare.


Pokretanje
-----------
Windows
 Jednostavno pokrenite frd.exe ili dvostruki klik na frd.jar


Linux
 Pokrenite komandu ./frd.sh
 (najprije postavite ispravna izvršna prava nad ovom datotekom )


Sve platforme
 Pokrenite komandu java -jar frd.jar

dodatni parametri za pokretanje su:

java -jar frd.jar [-h -v -d -r -D<svojstva>=<vrijednost> -m -p]

opcije
  -h (--help,-?)      prikazuje ovu poruku
  -v (--version)      prikazuje informaciju o verziji i izlazi
  -d (--debug)        prikazuje debuging informaciju
  -r (--reset)        postavlja korisničke postavke na početne vrijednosti  
  -m (--minim)        minimizira glavni prozor ns startu  
  -Dsvojstva=vrijednost    dodaje svojstva i vrijednosti aplikaciji (najčešće za debugiranje ili za testiranje)
  -p (--portable)     konfiguracijske datoteke biti će spremljene u 'config'
                      mapu, sve putanje datoteka biti će spremljene u odnosu s FRD
                      mapom (ako je moguće) - korisno za USB FLASH drive

Ako je vrijednost opcije -D namještena na 'default' (bez ') biti će korištene početne postavke.

Primjer - Pokretanje aplikacije u debug modu:
  Windows OS: frd.exe --debug
  Linux/MacOS: java -jar frd.jar --debug


Još Informacija:
  - Nezvanični tutorijal za Linux korisnike - kako konfigurirati FreeRapid Downloader na Linux (na Španjolskom)
    http://manualinux.my-place.us/freerapid.html
    

IV.    Poznati problemi i ograničenja
=======================================
- Aplikacija se neće pokrenuti ako je smještena u putanji sa posebnim znakovima kao '+' ili '%'
  - X molimo premjestite aplikaciju na drugu lokaciju bez takvih znakova
- ESET "Smart" Antivirus na Windows OS-u spriječava pokretanje FRD-a
  - X postavite vaš antivirusni program ili pokrenite FRD na ovaj način: frd.exe -Doneinstance=false
- Uvijek izlazite iz FRD-a normalno inače možete izgubiti listu datoteka (npr. Windows gašenje sa force opcijom...)          
- odabir od "vrha do dna" u glavnoj tablici u toku odvlačenja preuzimanjedjelimično nestane :-(
    X odaberite tablične redove s ctrl+klik mišem ili odaberite od dna do vrha
- Suština izgleda i osjećaja vraća org.jvnet.substance.api.UiThreadingViolationException:
                                                     Component creation must be done on Event Dispatch Thread
    X ignorirajte ovaj exception u app.log
- java.lang.UnsupportedClassVersionError exception
    X Koristite stariju Java verziju, trebate koristiti Sun Java verziju 6 ili noviju
- DirectoryChooser throws java.lang.InternalError ili zaledi na Win Vista (64bit)
    X ignorirajte ovaj exception u app.log
- java.lang.ClassCastException: java.awt.TrayIkona ne može biti postavljena java.awt.Component
    X ignorirajte ovaj exception u app.log    
- Linux korisnici prijavljuju da se ne prikazuje tray ikona u Linux-u
    X jedino poznato rješenje ovog problema može biti nadogradnja JRE u verziju 1.6.0_10-rc ili noviju
- ograničenja: verzija 0.7x je neupotrebljiva ako koristite verziju 0.8 ili noviju (jedino moguće rješenje je je da uklonite konfiguracijske datoteke)

IV.    Rješavanje problema
=======================================
1. Provjerite odlomak IV - za već poznate bugove i ograničenja
2. Jeste li probali ugasiti i ponovno pokrenuti aplikaciju? :-)
3. Provjerite web stranicu http://wordrider.net/freerapid i/ili problem traker at http://bugtracker.wordrider.net/
   za mogući poznati bug
4. Možete probati obrisati konfiguracijske datoteke (njihova lokacija je opisana u odlomku VI - (Instalacija )  
5. Pokrenite aplikaciju u debug modu:
   Windows OS: frd.exe --debug
   Linux/MacOS: java -jar frd.jar --debug
6. prijavite problem s app.log kao što je opisano u odlomku VI


VI.    Prijava Bugova
=======================================
Ako primjetite bug, molim vas nemojte pretpostaviti da znam za njega. Obavijestite me što je prije moguće kako bih to mogao popraviti prije izlaska
slijedeće verzije. Kako su moji resursi ograničeni,ne mogu podržati ispravak Bugova u ranijim verzijama.
Kako bi prijavili bug, možete koristiti issue tracker (preporučljivo), forums o projektu ili moj osobni e-mail.

Molim prijavite Vaš JRE i OS verziju i pridodajte datoteku app.log (koja je smještena u FreeRapid mapi).
Možete i pomoći da pronađemo problem. Također možete pomoći ako pokrenete aplikaciju s  --debug parametrom.
Ne uzimamo u obzir jednostavna izvješća kao "Ne mogu pokrenuti FRD. Što da radim?". Opišite vaš problem i ponašanje aplikacije.


issue tracker: http://bugtracker.wordrider.net/
forum: http://wordrider.net/forum/list.php?7
mail: bugs@wordrider.net (Vaša mail poruka može biti uhvaćena u spam filter, tako da tim putem NIJE preporučeno!)


VII.    Donacije
=======================================
FreeRapid downloader se distribuira kao freeware, ali ako želite izraziti svoju zahvalnost za vrijeme i resurse autoru koje je utrošio u razvoj, prihvaćaju se novčane donacije.
Mi smo studenti i moramo platiti za webhosting, računi za naše djevojke itd...

PayPal: http://wordrider.net/freerapid/paypal
   ili
koristite račun banke opisan na homepage-u
http://wordrider.net/freerapid/donation.html


VIII.   FAQ (pitanja i odgovori)
=======================================

Q: Zašto ste stvorili još jedan "RapidShare Downloader"?
A: 1) zato što nisam htio biti ovisan o ruskom softveru, koji je najvjerojatnije pun of malware-a i spyware-a.
   2) Zato što jednostavno sam mogu napraviti automatski downloader.
   3) Zato što drugi postojeći downloaderi imaju nemaštovito korisničko sučelje i fale im važne funkcije.
   4) Zato što mogu.

Q: Kako omogućiti podršku za gašenje/restart komande na Linuxu i MacOSu?
A: Molim pogledajte 'syscmd.properties' konfiguracijsku datoteku u mapi aplikacije za više detalja.

Q: Gdje su smještene konfiguracijske datoteke?
A: Vidi odlomak o Instalaciji.

Q: Kako postaviti Rapidshare Premium account?
A: Free Rapidshare plugin se koristi kao početni.
Za korištenje premium accounta idite na Opcije->Postavke->Pluginovi->panel postavki, onda pronađite Rapidshare_premium plugin i aktivirajte ga (klik na kvadratić u prvoj koloni- X).
Kako bi postavili vaše login podatke kliknite na gumb Opcije. Potvrdite detalje o accountu
s OK gumbom.

Samo brojčani account ID je podržan. Ostali premium pluginovi još nisu implementirani.

Q: Windows korisnici: Kako pokrenuti program iz komandne linije?
A: Idite na Start->Run i otipkajte:
cmd ENTER
cd putanja_u_freerapid_mapu' ENTER
frd.exe ENTER

Ako trebate unijeti neke paremetre, koristite npr. frd.exe --portable