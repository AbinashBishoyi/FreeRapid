package cz.vity.freerapid.sandbox;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public class BugTest {

    public static void main(String[] args) throws IOException {
        final String s = "C:\\Temp\\frd    aaa\\FreeRapid-0.81\\tools\\nircmd/nircmd.exe";

        final String[] arg = {"C:\\Temp\\frd    aaa\\FreeRapid-0.81\\tools\\nircmd\\nircmd.exe", "\"cdrom open e:\""};

        final StringTokenizer stringTokenizer = new StringTokenizer("\"asdasd  asdasdasdasd\"");
        final String token = stringTokenizer.nextToken();
        System.out.println("token = " + token);
        //Runtime.getRuntime().exec(path + " cmdwait 2200");
        //Runtime.getRuntime().exec("nircmd.exe", null, file.getParentFile());
        out(arg);
//        new ProcessBuilder(arg).start();
        //Runtime.getRuntime().exec("new File(s).getPath() + cdrom open e:");

        final Matcher matcher = Pattern.compile("(\\\".*?\\\")|([^\\s]+)").matcher("\"a dasds  a   b\" bbb  cccc ddd \"fff   \" gggg  \"\" ");
        int start = 0;
        while (matcher.find(start)) {
            final String s1 = matcher.group();
            System.out.println("s1 = '" + s1 + "'");
            start = matcher.end();
        }
    }


    private static void out(String[] cmd) {
        // Win32 CreateProcess requires cmd[0] to be normalized
        cmd[0] = new File(cmd[0]).getPath();

        StringBuilder cmdbuf = new StringBuilder(80);
        for (int i = 0; i < cmd.length; i++) {
            if (i > 0) {
                cmdbuf.append(' ');
            }
            String s = cmd[i];
            if (s.indexOf(' ') >= 0 || s.indexOf('\t') >= 0) {
                if (s.charAt(0) != '"') {
                    cmdbuf.append('"');
                    cmdbuf.append(s);
                    if (s.endsWith("\\")) {
                        cmdbuf.append("\\");
                    }
                    cmdbuf.append('"');
                } else if (s.endsWith("\"")) {
                    /* The argument has already been quoted. */
                    cmdbuf.append(s);
                } else {
                    /* Unmatched quote for the argument. */
                    throw new IllegalArgumentException();
                }
            } else {
                cmdbuf.append(s);
            }
        }
        String cmdstr = cmdbuf.toString();
        System.out.println("cmdstr = " + cmdstr);

    }

}
