package cz.vity.freerapid.utilities.os.shelllink;

import com4j.COM4J;

/**
 * @author ntoskrnl
 */
public class ShellLink {

    private String file;
    private String target;
    private String workingDirectory;
    private String iconLocation;
    private String arguments;

    public ShellLink(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getIconLocation() {
        return iconLocation;
    }

    public void setIconLocation(String iconLocation) {
        this.iconLocation = iconLocation;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public void save() {
        final String file = this.file;
        if (file == null) {
            throw new NullPointerException("file cannot be null");
        }
        final IShellLinkA shellLink = COM4J.createInstance(IShellLinkA.class, "{00021401-0000-0000-C000-000000000046}");
        if (target != null) {
            shellLink.setPath(target);
        }
        if (workingDirectory != null) {
            shellLink.setWorkingDirectory(workingDirectory);
        }
        if (iconLocation != null) {
            shellLink.setIconLocation(iconLocation, 0);
        }
        if (arguments != null) {
            shellLink.setArguments(arguments);
        }
        final IPersistFile persistFile = shellLink.queryInterface(IPersistFile.class);
        persistFile.save(file, BOOL.APITRUE);
        persistFile.dispose();
        shellLink.dispose();
        COM4J.cleanUp();
    }

}
