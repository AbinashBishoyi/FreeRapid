package cz.vity.freerapid.utilities.os;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Vity
 */
abstract class AbstractSystemCommander implements SystemCommander {

    protected Scanner runCommandAsScanner(final String cmd) throws IOException {
        final Process process = Runtime.getRuntime().exec(cmd);
        return new Scanner(process.getInputStream());
    }

    protected boolean findTopLevelWndow(String stringToFind, boolean caseSensitive, String command) throws IOException {
        if (!caseSensitive) {
            stringToFind = stringToFind.toLowerCase();
        }
        Scanner scanner = null;
        try {
            scanner = runCommandAsScanner(command);
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                if (!caseSensitive) {
                    s = s.toLowerCase();
                }
                if (s.contains(stringToFind)) {
                    return true;
                }
            }
        }
        finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return false;
    }

    protected List<String> getTopLevelWindowsList(String command) throws IOException {
        List<String> result = new LinkedList<String>();
        Scanner scanner = null;
        try {
            scanner = runCommandAsScanner(command);
            while (scanner.hasNextLine()) {
                result.add(scanner.nextLine());
            }
        }
        finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return result;
    }
}
