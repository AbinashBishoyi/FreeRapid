package cz.vity.freerapid.utilities;

import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.QuietMode;
import org.jdesktop.application.ResourceManager;
import org.jdesktop.application.ResourceMap;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Podpora prehravani zvuku. Zvuky se cachuji.
 *
 * @author Vity
 */
public class Sound {
    private final static Logger logger = Logger.getLogger(Sound.class.getName());

    private Sound() {
    }

    private static final Map<String, AudioClip> clipsMap = new HashMap<String, AudioClip>(1);

    private static void playSound(AudioClip clip) {
        if (clip != null)
            clip.play();
    }

    private static AudioClip getCachedAudioClip(final String fileName) {
        if (!clipsMap.containsKey(fileName)) {
            final AudioClip audioClip = getAudioClip(fileName);
            if (audioClip != null)
                clipsMap.put(fileName, audioClip);
            return audioClip;
        } else return clipsMap.get(fileName);
    }

    private static AudioClip getAudioClip(final String fileName) {
        final ResourceManager rm = MainApp.getAContext().getResourceManager();
        final ResourceMap resourceMap = rm.getResourceMap();
        String dir = resourceMap.getResourcesDir() + Consts.SOUNDS_DIR + "/" + fileName;
        final URL url = resourceMap.getClassLoader().getResource(dir);
        if (url == null)
            return null;
        return Applet.newAudioClip(url);
    }

    public static void playSound(final String clip) {
        playSound(clip, true);
    }

    public static void playSound(final String clip, final boolean considerQuietMode) {
        if (!considerQuietMode || (!QuietMode.getInstance().isSoundsDisabled() || !QuietMode.getInstance().isActive())) {
            final AudioClip audioClip = getCachedAudioClip(clip);
            if (audioClip == null)
                logger.warning("Unable to load " + clip);
            playSound(audioClip);
        }
    }

}