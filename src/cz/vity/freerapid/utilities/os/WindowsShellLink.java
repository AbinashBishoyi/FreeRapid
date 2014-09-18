/*
 * Copyright (c) 2012 ntoskrnl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package cz.vity.freerapid.utilities.os;

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.Ole32Util;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

import java.util.logging.Logger;

/**
 * @author ntoskrnl
 */
class WindowsShellLink {

    private static final Logger logger = Logger.getLogger(WindowsShellLink.class.getName());

    private static final String CLSID_ShellLink = "{00021401-0000-0000-C000-000000000046}";
    private static final String IID_IShellLinkW = "{000214F9-0000-0000-C000-000000000046}";
    private static final String IID_IPersistFile = "{0000010b-0000-0000-C000-000000000046}";
    private static final int COINIT_APARTMENTTHREADED = 0x2;
    private static final int CLSCTX_INPROC_SERVER = 0x1;

    private String file;
    private String target;
    private String workingDirectory;
    private String iconLocation;
    private String arguments;

    public WindowsShellLink(String file) {
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

    public boolean save() {
        final String file = this.file;
        if (file == null) {
            throw new NullPointerException("file cannot be null");
        }
        WinNT.HRESULT h;
        h = Ole32.INSTANCE.CoInitializeEx(null, COINIT_APARTMENTTHREADED);
        if (!W32Errors.S_OK.equals(h)) {
            logger.warning("CoInitializeEx failed (" + h + ")");
            return false;
        }
        IShellLinkW shellLink = null;
        IPersistFile persistFile = null;
        try {
            final PointerByReference shellLinkRef = new PointerByReference();
            final PointerByReference persistFileRef = new PointerByReference();
            h = Ole32.INSTANCE.CoCreateInstance(
                    Ole32Util.getGUIDFromString(CLSID_ShellLink),
                    null,
                    CLSCTX_INPROC_SERVER,
                    Ole32Util.getGUIDFromString(IID_IShellLinkW),
                    shellLinkRef);
            if (!W32Errors.S_OK.equals(h)) {
                logger.warning("CoCreateInstance failed (" + h + ")");
                return false;
            }
            shellLink = new IShellLinkW(shellLinkRef.getValue());
            shellLink.autoRead();
            shellLink.setAutoWrite(false);
            h = invoke(shellLink.vtbl.QueryInterface,
                    shellLink, Ole32Util.getGUIDFromString(IID_IPersistFile), persistFileRef);
            if (!W32Errors.S_OK.equals(h)) {
                logger.warning("IShellLinkW_QueryInterface failed (" + h + ")");
                return false;
            }
            persistFile = new IPersistFile(persistFileRef.getValue());
            persistFile.autoRead();
            persistFile.setAutoWrite(false);

            if (target != null) {
                h = invoke(shellLink.vtbl.SetPath, shellLink, new WString(target));
                if (!W32Errors.S_OK.equals(h)) {
                    logger.warning("IShellLinkW_SetPath failed (" + h + ")");
                    return false;
                }
            }
            if (workingDirectory != null) {
                h = invoke(shellLink.vtbl.SetWorkingDirectory, shellLink, new WString(workingDirectory));
                if (!W32Errors.S_OK.equals(h)) {
                    logger.warning("IShellLinkW_SetWorkingDirectory failed (" + h + ")");
                    return false;
                }
            }
            if (iconLocation != null) {
                h = invoke(shellLink.vtbl.SetIconLocation, shellLink, new WString(iconLocation), 0);
                if (!W32Errors.S_OK.equals(h)) {
                    logger.warning("IShellLinkW_SetIconLocation failed (" + h + ")");
                    return false;
                }
            }
            if (arguments != null) {
                h = invoke(shellLink.vtbl.SetArguments, shellLink, new WString(arguments));
                if (!W32Errors.S_OK.equals(h)) {
                    logger.warning("IShellLinkW_SetArguments failed (" + h + ")");
                    return false;
                }
            }

            h = invoke(persistFile.vtbl.Save, persistFile, new WString(file), 1);
            if (!W32Errors.S_OK.equals(h)) {
                logger.warning("IPersistFile_Save failed (" + h + ")");
                return false;
            }
        } finally {
            if (shellLink != null) {
                invoke(shellLink.vtbl.Release, shellLink);
            }
            if (persistFile != null) {
                invoke(persistFile.vtbl.Release, persistFile);
            }
            Ole32.INSTANCE.CoUninitialize();
        }
        return true;
    }

    private static WinNT.HRESULT invoke(final Pointer pointer, final Object... args) {
        return (WinNT.HRESULT) Function.getFunction(pointer).invoke(WinNT.HRESULT.class, args);
    }

    public static class IShellLinkW extends Structure {
        public IShellLinkWVtbl.ByReference vtbl;

        public IShellLinkW(final Pointer p) {
            super(p);
        }
    }

    @SuppressWarnings("unused")
    public static class IShellLinkWVtbl extends Structure {
        public Pointer QueryInterface;
        public Pointer AddRef;
        public Pointer Release;
        public Pointer GetPath;
        public Pointer GetIDList;
        public Pointer SetIDList;
        public Pointer GetDescription;
        public Pointer SetDescription;
        public Pointer GetWorkingDirectory;
        public Pointer SetWorkingDirectory;
        public Pointer GetArguments;
        public Pointer SetArguments;
        public Pointer GetHotkey;
        public Pointer SetHotkey;
        public Pointer GetShowCmd;
        public Pointer SetShowCmd;
        public Pointer GetIconLocation;
        public Pointer SetIconLocation;
        public Pointer SetRelativePath;
        public Pointer Resolve;
        public Pointer SetPath;

        public static class ByReference extends IShellLinkWVtbl implements Structure.ByReference {
        }
    }

    public static class IPersistFile extends Structure {
        public IPersistFileVtbl.ByReference vtbl;

        public IPersistFile(final Pointer p) {
            super(p);
        }
    }

    @SuppressWarnings("unused")
    public static class IPersistFileVtbl extends Structure {
        public Pointer QueryInterface;
        public Pointer AddRef;
        public Pointer Release;
        public Pointer GetClassID;
        public Pointer IsDirty;
        public Pointer Load;
        public Pointer Save;
        public Pointer SaveCompleted;
        public Pointer GetCurFile;

        public static class ByReference extends IPersistFileVtbl implements Structure.ByReference {
        }
    }

}
