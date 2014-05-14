package cz.vity.freerapid.sandbox;

import cz.vity.freerapid.utilities.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ladislav Vitasek
 */
public class Convertor {
    public static void main(String[] args) {
        final List<File> list = new LinkedList<File>();
        findFiles(new File("c:\\develope\\freerapid\\src\\cz\\vity\\freerapid\\gui\\dialogs\\resources"), list);
        for (File file : list) {
            final String f = Utils.loadFile(file, "Windows-1252");
            file.delete();
            writeFile(file, f);
        }
    }

    private static void writeFile(File file, String f) {
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(f.getBytes("UTF-8"));
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void findFiles(File dir, List<File> list) {
        final File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                findFiles(file, list);
            } else if (file.getAbsolutePath().endsWith("_PT.properties")) list.add(file);
        }
    }
}
