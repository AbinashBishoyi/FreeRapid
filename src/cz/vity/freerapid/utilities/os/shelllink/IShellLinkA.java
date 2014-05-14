package cz.vity.freerapid.utilities.os.shelllink;

import com4j.*;

/**
 * IShellLinkA interface
 */
@IID("{000214EE-0000-0000-C000-000000000046}")
interface IShellLinkA extends Com4jObject {

    /**
     * <p>
     * Retrieves the list of shell link item identifiers
     * </p>
     *
     * @return Returns a value of type int
     */

    @VTID(4)
    int getIDList();


    /**
     * <p>
     * Sets the list of shell link item identifiers
     * </p>
     *
     * @param pidl Mandatory int parameter.
     */

    @VTID(5)
    void setIDList(
            int pidl);


    /**
     * <p>
     * Retrieves the shell link description string
     * </p>
     *
     * @param pszName    Mandatory java.lang.String parameter.
     * @param cchMaxName Mandatory int parameter.
     */

    @VTID(6)
    void getDescription(
            @MarshalAs(NativeType.CSTR) java.lang.String pszName,
            int cchMaxName);


    /**
     * <p>
     * Sets the shell link description string
     * </p>
     *
     * @param pszName Mandatory java.lang.String parameter.
     */

    @VTID(7)
    void setDescription(
            @MarshalAs(NativeType.CSTR) java.lang.String pszName);


    /**
     * <p>
     * Retrieves the name of the shell link working directory
     * </p>
     *
     * @param pszDir     Mandatory java.lang.String parameter.
     * @param cchMaxPath Mandatory int parameter.
     */

    @VTID(8)
    void getWorkingDirectory(
            @MarshalAs(NativeType.CSTR) java.lang.String pszDir,
            int cchMaxPath);


    /**
     * <p>
     * Sets the name of the shell link working directory
     * </p>
     *
     * @param pszDir Mandatory java.lang.String parameter.
     */

    @VTID(9)
    void setWorkingDirectory(
            @MarshalAs(NativeType.CSTR) java.lang.String pszDir);


    /**
     * <p>
     * Retrieves the shell link command-line arguments
     * </p>
     *
     * @param pszArgs    Mandatory java.lang.String parameter.
     * @param cchMaxPath Mandatory int parameter.
     */

    @VTID(10)
    void getArguments(
            @MarshalAs(NativeType.CSTR) java.lang.String pszArgs,
            int cchMaxPath);


    /**
     * <p>
     * Sets the shell link command-line arguments
     * </p>
     *
     * @param pszArgs Mandatory java.lang.String parameter.
     */

    @VTID(11)
    void setArguments(
            @MarshalAs(NativeType.CSTR) java.lang.String pszArgs);


    /**
     * <p>
     * Retrieves or sets the shell link hot key
     * </p>
     * <p>
     * Getter method for the COM property "Hotkey"
     * </p>
     *
     * @return Returns a value of type short
     */

    @VTID(12)
    short hotkey();


    /**
     * <p>
     * Retrieves or sets the shell link hot key
     * </p>
     * <p>
     * Setter method for the COM property "Hotkey"
     * </p>
     *
     * @param pwHotkey Mandatory short parameter.
     */

    @VTID(13)
    void hotkey(
            short pwHotkey);


    /**
     * <p>
     * Retrieves the location (path and index) of the shell link icon
     * </p>
     *
     * @param pszIconPath Mandatory java.lang.String parameter.
     * @param cchIconPath Mandatory int parameter.
     * @param piIcon      Mandatory Holder<Integer> parameter.
     */

    @VTID(16)
    void getIconLocation(
            @MarshalAs(NativeType.CSTR) java.lang.String pszIconPath,
            int cchIconPath,
            Holder<Integer> piIcon);


    /**
     * <p>
     * Sets the location (path and index) of the shell link icon
     * </p>
     *
     * @param pszIconPath Mandatory java.lang.String parameter.
     * @param iIcon       Mandatory int parameter.
     */

    @VTID(17)
    void setIconLocation(
            @MarshalAs(NativeType.CSTR) java.lang.String pszIconPath,
            int iIcon);


    /**
     * <p>
     * Sets the shell link relative path
     * </p>
     *
     * @param pszPathRel Mandatory java.lang.String parameter.
     * @param dwReserved Mandatory int parameter.
     */

    @VTID(18)
    void setRelativePath(
            @MarshalAs(NativeType.CSTR) java.lang.String pszPathRel,
            int dwReserved);


    /**
     * <p>
     * Sets the shell link path and filename
     * </p>
     *
     * @param pszFile Mandatory java.lang.String parameter.
     */

    @VTID(20)
    void setPath(
            @MarshalAs(NativeType.CSTR) java.lang.String pszFile);


}
