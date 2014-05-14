package cz.vity.freerapid.plugins.container;

/**
 * @author ntoskrnl
 */
public class ContainerException extends Exception {

    public ContainerException() {
        super();
    }

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerException(Throwable cause) {
        super(cause);
    }

    /**
     * Indicates that file is corrupt or of unknown format and cannot be opened.
     *
     * @return new ContainerException
     */
    public static ContainerException fileIsCorrupt() {
        return new ContainerException("fileIsCorruptException");
    }

    /**
     * Indicates that file is not supported and cannot be opened.
     *
     * @return new ContainerException
     */
    public static ContainerException notSupported() {
        return new ContainerException("notSupportedException");
    }

}
