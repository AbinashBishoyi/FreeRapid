package cz.vity.freerapid.utilities.os.shelllink;

import com4j.*;

/**
 * IPersistFile interface
 */
@IID("{0000010B-0000-0000-C000-000000000046}")
interface IPersistFile extends Com4jObject {

    /**
     * <p>
     * Checks for changes since last file write
     * </p>
     */

    @VTID(4)
    void isDirty();


    /**
     * <p>
     * Opens the specified file and initializes the object from its contents
     * </p>
     *
     * @param pszFileName Mandatory java.lang.String parameter.
     * @param dwMode      Mandatory int parameter.
     */

    @VTID(5)
    void load(
            @MarshalAs(NativeType.Unicode) java.lang.String pszFileName,
            int dwMode);


    /**
     * <p>
     * Saves the object into the specified file
     * </p>
     *
     * @param pszFileName Mandatory java.lang.String parameter.
     * @param fRemember   Mandatory BOOL parameter.
     */

    @VTID(6)
    void save(
            @MarshalAs(NativeType.Unicode) java.lang.String pszFileName,
            BOOL fRemember);


    /**
     * <p>
     * Notifies the object that save is completed
     * </p>
     *
     * @param pszFileName Mandatory java.lang.String parameter.
     */

    @VTID(7)
    void saveCompleted(
            @MarshalAs(NativeType.Unicode) java.lang.String pszFileName);


    /**
     * <p>
     * Gets the current name of the file associated with the object
     * </p>
     *
     * @param ppszFileName Mandatory int parameter.
     */

    @VTID(8)
    void getCurFile(
            int ppszFileName);


}
