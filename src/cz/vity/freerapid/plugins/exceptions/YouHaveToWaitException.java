package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class YouHaveToWaitException extends ErrorDuringDownloadingException {
    private int howManySecondsToWait;

    public YouHaveToWaitException(String message, int howManySecondsToWait) {
        super(message);
        this.howManySecondsToWait = howManySecondsToWait;
    }

    public int getHowManySecondsToWait() {
        return howManySecondsToWait;
    }
}
