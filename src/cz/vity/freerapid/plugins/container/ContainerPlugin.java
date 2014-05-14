package cz.vity.freerapid.plugins.container;

import cz.vity.freerapid.plugins.LibraryPlugin;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author ntoskrnl
 */
public abstract class ContainerPlugin extends LibraryPlugin {

    public abstract List<String[]> getSupportedFiles();

    public abstract List<FileInfo> read(InputStream is, String name) throws Exception;

    public abstract void write(List<FileInfo> files, OutputStream os, String name) throws Exception;

}
