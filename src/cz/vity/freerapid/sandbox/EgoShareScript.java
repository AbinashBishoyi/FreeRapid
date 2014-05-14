package cz.vity.freerapid.sandbox;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ludkiz
 */
final class EgoShareScript {

    private EgoShareScript() {
    }

    public static String decode(String input) {

        final StringBuilder output = new StringBuilder();
        int chr1;
        int chr2;
        int chr3;
        int enc1;
        int enc2;
        int enc3;
        int enc4;
        int i = 0;
        final String _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

        input = input.replaceAll("[^A-Za-z0-9+/=]", "");

        final int length = input.length();
        while (i < length) {

            enc1 = _keyStr.indexOf(input.charAt(i++));
            enc2 = _keyStr.indexOf(input.charAt(i++));
            enc3 = _keyStr.indexOf(input.charAt(i++));
            enc4 = _keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output.append((char) chr1);

            if (enc3 != 64) {
                output.append((char) chr2);
            }
            if (enc4 != 64) {
                output.append((char) chr3);
            }
        }

        //output = URLDecoder.decode(output, "UTF-8");
        try {
            return new String(output.toString().getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EgoShareScript.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            final Process process = Runtime.getRuntime().exec("c:\\develope\\freerapid\\etc\\tools\\find\\find.exe freerapid");
            final Scanner scanner = new Scanner(process.getInputStream());
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //loadfilelink.decode("aHR0cDovL2VnbzgtMS5lZ29zaGFyZS5jb20vZ2V0ZmlsZS5waHA/aWQ9OTc5NDkmYWNjZXNzX2tleT02MTQwN2Q4MDA5YjM1MDk1NzM1MGVjOTQ5ZGFiOTc5NyZ0PTQ4ZTY4ZGM2Jm89MEVBQUI2NjI3NzZGQjY1QzVFNzZDQkMzOUQ1OEIzNzgzNkQwNUQ3MzI0RDgxNDQ3MkE5NzlDQ0RDM0QxOUE1MEJBMTBCN0IyMEUzRDIyRjU1QzNBMDlENkRFODI1OEFBJm5hbWU9aGlvLXNuLjQwMy5kb3QucGFydDEucmFy");
        //System.out.println(EgoShareScript.decode("aHR0cDovL2VnbzUtMS5lZ29zaGFyZS5jb20vZ2V0ZmlsZS5waHA/aWQ9OTY2MTcmYWNjZXNzX2tleT1iM2JjMjBiNzQ2YTVhNjI4OTk4NjBlYTBkNTdhNzNkMSZ0PTQ4ZTY4ZjMwJm89QkI4NEIwMkY3QTREQTEzRkZDQUQ0QTBFNTU0RERENDdCRTkwQjcyQTcyNERCQzIzRTFCODQyNkUzNjNFREUzNUJFOTFBRTQ5MTE0REExM0ZGRkFEJm5hbWU9aGlvLWtyLjEwMi5sb2wucGFydDMucmFy"));

    }

}