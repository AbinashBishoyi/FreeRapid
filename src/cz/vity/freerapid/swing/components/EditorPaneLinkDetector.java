package cz.vity.freerapid.swing.components;

import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.utilities.Browser;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Komponenta, ktera zvyraznuje emaily v textu. Pri drzeni CTRL+click otevira email v klientu.
 *
 * @author Vity
 */
public class EditorPaneLinkDetector extends JEditorPane {
    private final static Logger logger = Logger.getLogger(EditorPaneLinkDetector.class.getName());
    private final static String EXAMPLE = "";
    private final static Pattern REGEXP_URL = Pattern.compile("(http|https)://([a-zA-Z0-9\\.\\- ]+(:[a-zA-Z0-9\\.:&%\\$\\- ]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])|([a-zA-Z0-9\\- ]+\\.)*[a-zA-Z0-9\\- ]+\\.[a-zA-Z ]{2,4})(:[0-9]+)?(/[^/][\\p{Lu}\\p{Ll}0-9\\[\\]\\.:,\\?'\\\\/\\+&%\\$#=~_\\-@ ]*)*");

    public EditorPaneLinkDetector() {
        super();
        final Action copyAction = this.getActionMap().get("copy");
        final Action pasteAction = this.getActionMap().get("paste");
        this.getInputMap().put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_INSERT), pasteAction);
        this.getInputMap().put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_INSERT), copyAction);
        final SyntaxDocument doc = new SyntaxDocument();
        this.setEditorKit(new StyledEditorKit() {
            public Document createDefaultDocument() {
                return doc;
            }
        });

        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                final JTextComponent source = (JTextComponent) e.getSource();
                if (source.isEditable())
                    return;
                final StyledDocument doc = (StyledDocument) source.getDocument();
                final Object attribute = doc.getCharacterElement(source.viewToModel(e.getPoint())).getAttributes().getAttribute("EMAIL");
                if (attribute != null) {
                    e.consume();
                    final String email = attribute.toString();
                    logger.info("EditorPaneLinkDetector opening email " + e);
                    Browser.openBrowser("mailto:" + email);
                }
            }

        });

        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    if (isEditable())
                        setEditable(false);
                } else {
                    if (!isEditable())
                        setEditable(true);
                }

            }

            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    setEditable(true);
                }

            }
        });
        insertExampleEmail(doc);

        this.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                final JTextComponent source = (JTextComponent) e.getSource();
                if (EXAMPLE.equals(source.getText()))
                    source.setText("");
            }

            public void focusLost(FocusEvent e) {
                final JTextComponent source = (JTextComponent) e.getSource();
                if (source.getText().length() <= 0) {
                    insertExampleEmail((StyledDocument) source.getDocument());
                }
            }
        });
    }

    public void setURLList(List<URL> urlList) {
        List<String> urlStringList = new LinkedList<String>();
        for (URL url : urlList) {
            urlStringList.add(url.toExternalForm());
        }
        this.setURLs(urlStringList);
    }

    private void insertExampleEmail(StyledDocument doc) {
        SimpleAttributeSet example = new SimpleAttributeSet();
        StyleConstants.setForeground(example, Color.GRAY);
        try {
            doc.insertString(0, EXAMPLE, example);
        } catch (BadLocationException e) {
            LogUtils.processException(logger, e);
        }
    }


    public void setURLs(java.util.List<String> list) {
        if (list.isEmpty())
            return;
        final Document document = this.getDocument();
        String s = "";
        try {
            s = document.getText(0, document.getLength());
        } catch (BadLocationException e) {
            //ignore
        }
        final StringBuilder builder = new StringBuilder();
        s = s.trim();
        builder.append(s);
        if (s.length() > 0) {
            builder.append('\n');
        }
        for (String item : list) {
            builder.append(item).append('\n');
        }
        final String str = builder.toString();
//        if (str.length() > 0)
//            this.setText(""); //pro pripad ze je tam demo
        try {
            this.setText("");
            document.insertString(0, str, null);
        } catch (BadLocationException e) {
            LogUtils.processException(logger, e);
        }
    }

    public void setURLs(String s) {
        final Pattern pattern = REGEXP_URL;
        final Matcher matcher = pattern.matcher(s);
        final java.util.List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            final String e = matcher.group();
            if (!EXAMPLE.equals(e))
                list.add(e);
        }
        setURLs(list);
    }

    public java.util.List<URL> getURLs() {
        final String s = this.getText();
        final String[] urls = s.split("\n|\t|(?:  )");
        List<URL> list = new ArrayList<URL>(urls.length);
        for (String url : urls) {
            if (!url.trim().isEmpty()) {
                final URL u = getURL(url);
                if (u != null)
                    list.add(u);
            }
        }
        return list;
    }

    public java.util.List<String> getURLsAsStringList() {
        final String s = this.getText();
        final String[] urls = s.split("\n|\t|(?:  )");
        List<String> list = new ArrayList<String>(urls.length);
        for (String url : urls) {
            if (!url.isEmpty()) {
                list.add(url);
            }
        }
        return list;
    }

    class SyntaxDocument extends DefaultStyledDocument {
        private DefaultStyledDocument doc;
        private Element rootElement;

        private MutableAttributeSet normal;
        private MutableAttributeSet keyword;
        private final Pattern EMAIL_PATTERN = REGEXP_URL;
        private static final String DELIMITERS = "\n\t";


        public SyntaxDocument() {
            doc = this;
            rootElement = doc.getDefaultRootElement();
            putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

            normal = new SimpleAttributeSet();
            StyleConstants.setForeground(normal, Color.RED);

            keyword = new SimpleAttributeSet();
            StyleConstants.setForeground(keyword, Color.BLUE);

        }

        /*
          *  Override to apply syntax highlighting after the document has been updated
          */
        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            super.insertString(offset, str, a);
            if (str.equals(EXAMPLE))
                return;
            processChangedLines(offset, str.length());
        }

        /*
          *  Override to apply syntax highlighting after the document has been updated
          */
        public void remove(int offset, int length) throws BadLocationException {
            super.remove(offset, length);
            processChangedLines(offset, 0);
        }

        /*
          *  Determine how many lines have been changed,
          *  then apply highlighting to each line
          */
        public void processChangedLines(int offset, int length)
                throws BadLocationException {
            String content = doc.getText(0, doc.getLength());

            //  The lines affected by the latest document update

            int startLine = rootElement.getElementIndex(offset);
            int endLine = rootElement.getElementIndex(offset + length);

            //  Do the actual highlighting

            for (int i = startLine; i <= endLine; i++) {
                applyHighlighting(content, i);
            }

        }

        /*
          *  Parse the line to determine the appropriate highlighting
          */
        private void applyHighlighting(String content, int line)
                throws BadLocationException {
            int startOffset = rootElement.getElement(line).getStartOffset();
            int endOffset = rootElement.getElement(line).getEndOffset() - 1;

            int lineLength = endOffset - startOffset;
            int contentLength = content.length();

            if (endOffset >= contentLength)
                endOffset = contentLength - 1;


            doc.setCharacterAttributes(startOffset, lineLength, normal, true);

            checkForTokens(content, startOffset, endOffset);
        }

        /*
        *	Parse the line for tokens to highlight
        */
        private void checkForTokens(String content, int startOffset, int endOffset) {
            while (startOffset <= endOffset) {
                //  skip the delimiters to find the start of a new token

                while (isDelimiter(content.substring(startOffset, startOffset + 1))) {
                    if (startOffset < endOffset)
                        startOffset++;
                    else
                        return;
                }

                startOffset = getOtherToken(content, startOffset, endOffset);
            }
        }

        /*
          *
          */
        private int getOtherToken(String content, int startOffset, int endOffset) {
            int endOfToken = startOffset + 1;

            while (endOfToken <= endOffset) {
                if (isDelimiter(content.substring(endOfToken, endOfToken + 1)))
                    break;

                endOfToken++;
            }

            String token = content.substring(startOffset, endOfToken);

            if (isKeyword(token)) {
                keyword.addAttribute("EMAIL", token);
                doc.setCharacterAttributes(startOffset, endOfToken - startOffset, keyword, false);
            }

            return endOfToken + 1;
        }

        /*
          *  Override for other languages
          */
        protected boolean isDelimiter(String character) {

            return DELIMITERS.indexOf(character.charAt(0)) != -1;
//            return Character.isWhitespace(character.charAt(0)) ||
//                    DELIMITERS.indexOf(character) != -1;
        }


        /*
          *  Override for other languages
          */
        protected boolean isKeyword(String token) {
            //return keywords.contains(token);
            //System.out.println("token = " + token);
            final Matcher match = EMAIL_PATTERN.matcher(token);
            return match.find() && getURL(match.group()) != null;
        }
    }


    private String checkURI(String url) throws URIException {
        final String defaultProtocolCharset = org.apache.commons.httpclient.URI.getDefaultProtocolCharset();
        try {
            return new org.apache.commons.httpclient.URI(url, true, defaultProtocolCharset).getEscapedURIReference();
        } catch (URIException e) {
            logger.warning(String.format("Invalid URL - '%s' does not match URI specification", url));
            try {
                return new org.apache.commons.httpclient.URI(URIUtil.encodePathQuery(url), true, defaultProtocolCharset).getEscapedURIReference();
            } catch (URIException e1) {
                throw e;
            }
        }
    }


    private URL getURL(String url) {

        try {
            return new URL(checkURI(url));
        } catch (MalformedURLException e) {
            //ignore
        } catch (URIException e) {
            //ignore
        }
        return null;
    }

}
