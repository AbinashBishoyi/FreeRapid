**************************************************************
*   FreeRapid Downloader                                     *
*      by Ladislav Vitasek aka Vity                          *
*   Website/Forum/Bugtracker: http://wordrider.net/freerapid *
*   Mail: info@wordrider.net - suggestions                   *
*   Last change: 20th December 2008                          *
**************************************************************

=======================================
Table des matióres :
   I.   Qu'est-ce que FreeRapid Downloader?
  II.   Configuration requise
 III.   Comment lancer FreeRapid Downloader
  IV.   Problómes connus et limitations
   V.   Diagnostic
  VI.   Reporter un bug
 VII.   Faire un don
VIII.   FAQ
=======================================


I.    Qu'est-ce que FreeRapid Downloader?
=======================================

FreeRapid downloader est un simple téléchargeur en Java destiné au téléchargement d'archives de fichers partagés comme RapidShare.

Caractéristiques principales :
 - support pour des téléchargements parallóles de multiples services
 - téléchargement via une liste de proxy
 - historique de téléchargement
 - surveillance intelligente du Presse-Papiers
 - vérification automatique de l'existence d'un fichier sur le serveur
 - mises à jour automatique des plugins
 - reconnaissance de CAPTCHA simple
 - extinction automatique
 - beaucoup de réglages de l'UI.
 - tourne sous MS Windows, OS X et Linux
 - est magnifique (beaucoup de thómes de couleur)
 - ça fonctionne!

Div.:
 - Glisser&Déposer d'URLs
 - Interface de programmation (API) simple pour l'ajout d'autres services comme des plugins

Services supportés actuellement (par ordre alphabétique) :
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

 -  d'autres arrivent...
Note: Cette liste pourrait ne pas ôtre actuelle - la plus récente se trouve sur http://wordrider.net/freerapid/

 II.    Configuration requise
=======================================

Configuration recommandée :
    * Systóme d'exploitation Windows 2000/XP/Vista/7/Linux(kernel 2.4)* ou mieux
    * Processeur Pentium 800MHz
    * Résolution d'écran min 1024x768
    * 40 Mo de RAM libre
    * 10 Mo d'espace disque libre
    * Java 2 Platform - version 1.6 (Java SE 6 Runtime), au moins, installée

L'application a besoin de Sun Java 6.0 au moins pour démarrer (http://java.sun.com/javase/downloads/index.jsp , JRE 6).

Les utilisateurs d'un Linux Debian-like peuvent utiliser cette commande pour installer Java :
   sudo apt-get install sun-java6-jre

III.   Comment lancer FreeRapid Downloader
=======================================

Installation
------------
Extraire les fichiers n'importe oÿ, évitez les caractóres spéciaux (par exemple '+' ou '!') dans le chemin.
Si vous faites une mise à jour vers une version plus récente, vous pouvez effacer le dossier précédent. Les préférences utilisateur seront conservées. Ces derniers sont en effet sauvegardées dans les répertoires suivants :
MS Windows: c:\Documents and Settings\VOTRE_NOM_D_UTILISATEUR\application data\VitySoft\FRD

Linux: ~/.FRD

NE COPIEZ PAS une nouvelle version sur une ancienne. Effacez toujours l'ancien dossier avant.


Lancement
-----------
Windows
 Lancez simplement frd.exe ou double-cliquez sur frd.jar


Linux
 Lancez la commande ./frd.sh
 (n'oubliez pas de rendre le fichier exécutable avant)


Toutes plate-formes
 Lancez la commande java -jar frd.jar


Les paramótres additionnels de lancement sont :

java -jar frd.jar [-h -v -d -r -D<property>=<value> -m -p]

options
  -h (--help,-?)      affiche ce message
  -v (--version)      affiche les informations de version et quitte
  -d (--debug)        affiche des informations de debogage
  -r (--reset)        remet les propriétés utilisateur à zéro
  -m (--minim)        minimise la fenôtre principale au démarrage
  -Dproperty=value    passe les propriétés et leurs valeurs à l'application (utilisé principalement à des fins de test et de débogage)
  -p (--portable)     les fichiers de configuration seront stockés dans le dossier 'config', et tous les chemins de fichiers seront sauvegardés par rapport au dossier FRD, si possible (utile pour les clés USB)

Si la valeur de l'option -D est 'default' (sans les '), la valeur par défaut sera utilisée.

Exemple - lancement de l'application en mode de débogage :
  Windows OS: frd.exe --debug
  Linux/MacOS: java -jar frd.jar --debug


Plus d'infos :
  - Tutoriel non-officiel pour les utilisateurs Linux - Comment configurer FreeRapid Downloader sous Linux (en espagnol)
    http://manualinux.my-place.us/freerapid.html
    

IV.    Bugs connus et limitations
=======================================
- L'application ne démarre pas si elle est placée sur un chemin contenant des caractóres spéciaux ('+', '%',...)
  - Merci de la déplacer vers un endroit dépourvu de ces caractóres.
- L'antivirus ESET "Smart" sur Windows bloque le démarrage de FRD
  - Paramétrez correctement votre programme antivirus, ou lancez FRD de cette manióre : frd.exe -Doneinstance=false
- Fermez toujours FRD de manióre propre et correcte, sans quoi vous pourriez perdre votre liste de fichiers (i.e. n'éteignez pas brutalement votre ordinateur)
- La sélection de "haut en bas" dans le tableau principal disparait partiellement pendant un déplacement de la fenôtre :-(
    X Selectionnez les colonnes du tableau par ctrl+clic gauche ou sélectionnez les items de bas en haut.
- Substance look and feel throws org.jvnet.substance.api.UiThreadingViolationException:
                                                     Component creation must be done on Event Dispatch Thread
    X ignorez cette exception dans le app.log
- java.lang.UnsupportedClassVersionError exception
    X Vous utilisez une vieille version de Java, vous devriez utiliser Sun Java Version 6 ou plus récent
- DirectoryChooser throws java.lang.InternalError ou fige sous Windows Vista (64bit)
    X ignorez cette exception dans le app.log
- java.lang.ClassCastException: java.awt.TrayIcon cannot be cast to java.awt.Component
    X ignorez cette exception dans le app.log    
- Des utilisateurs Linux ont reporté que l'icône dans le tableau de bord qui ne s'affichait pas
    X La seule solution connue pour ce problóme pourrait ôtre une mise-à-jour de JRE vers la version 1.6.0_10-rc ou plus récent
- limitation: la version 0.7x n'est pas utilisable à partir du moment oÿ vous utilisez la version 0.8 ou plus récent (la seule solution possible est de supprimer les anciens fichiers de configuration)

V.    Diagnostic
=======================================
1. Vérifiez la section IV - pour voir si votre problóme ne figure pas déjà dedans
2. Avez-vous essayé de redémarrer l'application? :-)
3. Vérifiez le site http://wordrider.net/freerapid et/ou http://bugtracker.wordrider.net/
   pour d'éventuels nouveaux bugs connus
4. Vous pouvez essayer de supprimer les fichiers de configuration (leur emplacement est décrit dans la section III - Installation)  
5. Lancez l'application en mode de déboguage :
   Windows OS: frd.exe --debug
   Linux/MacOS: java -jar frd.jar --debug
6. Reportez le problóme avec app.log comme décrit dans la section VI


VI.    Reporter un bug
=======================================
Si vous trouvez un bug, merci de ne pas supposer que je le connais. Informez m'en aussitôt que possible, afin que je puisse le résoudre avant la version suivante. 
Etant donné que mes ressources sont limitées, je ne peux rétro-porter les résolutions de bug sur des anciennes versions.
Pour reporter un bug, vous pouvez utiliser le tracker de bug (recommandé), les forums du projet, ou mon e-mail personnel (en anglais).

Merci de préciser votre systóme d'exploitation, sa version ainsi que celle de votre JRE, et de joindre le fichier app.log (qui se trouve dans le dossier de FRD).
Il peut nous aider à reconnaitre un problóme. Vous pouvez également nous aider en lançant l'application avec le paramótre --debug.
Nous ignorons les reports simple du style "Je ne peux lancer FRD. Que dois-je faire?". Décrivez votre situation et le comportement de l'application. Soyez précis.


issue tracker: http://bugtracker.wordrider.net/
forum: http://wordrider.net/forum/list.php?7
mail: bugs@wordrider.net (votre mail peut ne jamais arriver à destination à cause des filtres anti-spam, cette voie n'est par conséquent PAS recommandée!)


VII.    Faire un don
=======================================
FreeRapid downloader est distribué en tant que gratuiciel (freeware), mais si vous voulez exprimer votre appréciation pour le temps et les ressources que l'auteur a investi dans le développement de ce logiciel, nous acceptons et apprécions les dons financiers.
Nous sommes des étudiants, et devons payer pour l'hébergement web, les factures de nos copines, etc...

PayPal: http://wordrider.net/freerapid/paypal
   ou
Utilisez le compte bancaire décrit sur notre page d'accueil : http://wordrider.net/freerapid/donation.html


VIII.   FAQ
=======================================

Q: Pourquoi avez-vous créé un autre "RapidShare Downloader"?
A: 1) Parce que je ne veux pas ôtre dépendant d'un logiciel russe probablement plein de malware et autres spywares.
   2) Parce que je peux simplement régler des téléchargements automatisés moi-môme.
   3) Parce que d'autres téléchargeurs existants ont des interfaces utilisateurs peu intuitives, et/ou manquent de fonctionnalités importantes.
   4) Parce que je peux le faire.

Q: Comment activer un support pour des commandes au démarrage/à l'extinction sur Linux et MacOS?
A: Merci de lire le fichier de configuration 'syscmd.properties' dans le dossier de l'application pour plus de détails.

Q: Oÿ sont situés les fichiers de configuration?
A: Cf. section III/Installation.

Q: Comment utiliser un compte RapidShare Premium?
A: Par défaut, un plugin RapidShare Free est utilisé.
Pour utiliser un compte premium, allez à Options>Préférences>Plugins>Settings, trouvez le plugin Rapidshare_premium et activez-le.
Pour entrer les informations de login, cliquez sur "Options". Entrez-les et cliquez sur "OK" pour confirmer.

Seules les ID de comptes numériques sont supportés. Les autres types de plugins premium ne sont pas encore implémentés.

Q: Utilisateurs Windows : Comment lancer le programme en ligne de commande?
A: Allez à Démarrer>Exécuter... et tapez :
cmd ENTER
cd path_to_freerapid_directory' ENTER
frd.exe ENTER

Si vous avez besoin d'entrer des paramótres, utilisez par exemple "frd.exe --portable"
