package cz.vity.freerapid.plugins.webclient;

/**
 * Enum class with states those indicates availability of file on the server
 *
 * @author Ladislav Vitasek
 */
public enum FileState {
    //important note, order of these values is used in the table to sort column by priority
    /**
     * file was succesfuly checked on the server and is available for download
     */
    CHECKED_AND_EXISTING,

    /**
     * file is not avaiable on the server anymore
     */
    FILE_NOT_FOUND,
    /**
     * file was not checked on the server yet
     */
    NOT_CHECKED,
    /**
     * error during checking of file
     */
    ERROR_GETTING_INFO
}
