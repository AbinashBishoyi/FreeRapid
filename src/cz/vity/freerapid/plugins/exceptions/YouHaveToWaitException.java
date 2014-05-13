package cz.vity.freerapid.plugins.exceptions;

/**
 * User has to wait some given time to be able download again
 *
 * @author Vity
 */
public class YouHaveToWaitException extends ErrorDuringDownloadingException {
    /**
     * Field howManySecondsToWait - how many second user has to wait
     */
    private int howManySecondsToWait;

    /**
     * Constructor - creates a new YouHaveToWaitException instance.
     *
     * @param message              message to user
     * @param howManySecondsToWait time to wait - in seconds
     */
    public YouHaveToWaitException(String message, int howManySecondsToWait) {
        super(message);
        this.howManySecondsToWait = howManySecondsToWait;
    }

    /**
     * Method getHowManySecondsToWait returns the howManySecondsToWait of this YouHaveToWaitException object.
     *
     * @return the howManySecondsToWait (type int) of this YouHaveToWaitException object.
     */
    public int getHowManySecondsToWait() {
        return howManySecondsToWait;
    }


    /**
     * Setter for property 'howManySecondsToWait'.
     *
     * @param howManySecondsToWait Value to set for property 'howManySecondsToWait'.
     */
    public void setHowManySecondsToWait(int howManySecondsToWait) {
        this.howManySecondsToWait = howManySecondsToWait;
    }
}
