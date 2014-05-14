package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.BuildMethodException;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.URIUtil;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Method builder's purpose is to make creating POST and GET request as much as easy as possible.<br />
 * It uses several parsing methods for searching action method (extraction from <code>&lt;form&gt;</code>, <code>&lt;a&gt;</code>, <code>&lt;img&gt;</code> tag). <br/>
 * By default you can get new instance by calling getMethodBuilder() on your plugin "runner" instance. You can also use a constructor for special purposes (searching on the specific text content).<br/></p>
 * <p>
 * To get final POST or GET method you need to have full absolut URL to target server - the URL has to start with 'http'.<br /.
 * Final action is generated this way:<br />
 * <ul>
 * <li>1. If action starts with http - take it as the final URL<br /></li>
 * <li>2. Otherwise check for baseURL - take it if starts with 'http' + action as final URL <br/></li>
 * <li>3. Exception otherwise<br/></li>
 * </ul>
 * </p>
 * <p><b>Typical use:</b><br />
 * <code>HttpMethod method = getMethodBuilder().setActionFromFormByName("freeForm", true).setBaseURLForAction("http://freeupload.com/").toMethod();<br/>
 * if (makeRedirectedRequest(method))...</code>
 * </p>
 * <p>Many examples of using you can find in the class MethodBuilderTest.java in the main project.<br/></p>
 *
 * @author Vity
 * @since 0.82
 */
public final class MethodBuilder {
    private final static Logger logger = Logger.getLogger(MethodBuilder.class.getName());

    private final Map<String, String> parameters = new LinkedHashMap<String, String>(4);
    private final String content;
    private HttpDownloadClient client;
    private String referer;
    private static Pattern formPattern;
    private String action;
    private static Pattern parameterPatterns;
    private static Pattern parameterInputPattern;
    private HttpMethodEnum postMethod = HttpMethodEnum.POST;
    private static final int FORM_MATCHER_TITLE_GROUP = 1;
    private String baseURL;
    private static Pattern aHrefPattern;
    private static final int FORM_MATCHER_FORM_CONTENT = 2;
    private static Pattern parameterNamePattern;
    private boolean encodeParameters = false;
    private String encoding = "UTF-8";
    private boolean autoReplaceEntities = true;
    private static Pattern imgPattern;
    private static Pattern parameterValuePattern;
    private boolean encodePathAndQuery;

    /**
     * Returns actual set POST or GET method extracted from result. <br /> Its value is used in <code>toMethod()</code> method.<br/>
     * POST method is a default value.
     *
     * @return GET or POST method type
     */
    public HttpMethodEnum getMethodAction() {
        return postMethod;
    }

    /**
     * Enumeration of HTTP method possibilities
     */
    public enum HttpMethodEnum {
        POST, GET
    }

    /**
     * Constructor
     *
     * @param content string content - the parsed text
     * @param client  httpclient used for generating HTTPMethod
     */
    public MethodBuilder(String content, HttpDownloadClient client) {
        this.content = content;
        this.client = client;
    }

    /**
     * Constructor<br/>
     * The content from the last request is taken for parsing text.
     *
     * @param client httpclient used for generating HTTPMethod
     */
    public MethodBuilder(HttpDownloadClient client) {
        this(client.getContentAsString(), client);
    }

    /**
     * Searches content for form tag with given value of ID or name attribute and extracts the <code>action</code> attribute.<br/>
     * All <code>&amp;amp;</code> entity is replaced to <code>&</code> by default.
     *
     * @param formIDOrName      <code>form</code> ID or name
     * @param useFormParameters if true then it extracts all input parameters from the <code>form</code> tag
     * @return builder instance
     * @throws cz.vity.freerapid.plugins.exceptions.BuildMethodException
     *          when no such FORM was found
     */
    public MethodBuilder setActionFromFormByName(final String formIDOrName, final boolean useFormParameters) throws BuildMethodException {
        final Matcher formMatcher = getFormMatcher();
        int start = 0;
        boolean found = false;
        final Pattern namePattern = Pattern.compile("(?:name|id)\\s?=\\s?(?:\"|')?" + formIDOrName + "(?:\"|'|\\s|>|$)", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        while (formMatcher.find(start)) {
            final String title = formMatcher.group(FORM_MATCHER_TITLE_GROUP);
            if (namePattern.matcher(title).find()) {
                inputForm(useFormParameters, title, formMatcher.group(2));
                found = true;
                break;
            }
            start = formMatcher.end();
        }
        if (!found)
            throw new BuildMethodException("Tag <Form> with a name or ID '" + formIDOrName + "' was not found!");
        return this;
    }

    /**
     * Searches content for form tag with given text in action attribute and extracts the <code>action</code> attribute.<br/>
     * <p>
     * Content:<br/>
     * <code>&lt;form action="http://blabla/" &gt<br /></code>
     * <p/>
     * <code>setActionFromFormWhereActionContains("BLABLA")</code> - an action <code>http://blabla/</code> will be extracted
     * </p>
     * <p>All <code>&amp;amp;</code> entity is replaced to <code>&</code> by default.</p>
     *
     * @param text              <code>form</code> ID or name
     * @param useFormParameters if true then it extracts all input parameters from the <code>form</code> tag
     * @return builder instance
     * @throws cz.vity.freerapid.plugins.exceptions.BuildMethodException
     *          when no such FORM with action was found
     */
    public MethodBuilder setActionFromFormWhereActionContains(final String text, final boolean useFormParameters) throws BuildMethodException {
        final Matcher formMatcher = getFormMatcher();
        boolean found = false;
        int start = 0;
        final String lower = text.toLowerCase();
        while (formMatcher.find(start)) {
            final String title = formMatcher.group(FORM_MATCHER_TITLE_GROUP);
            final String action = extractAction(title);
            if (action != null && action.toLowerCase().contains(lower)) {
                inputForm(useFormParameters, title, formMatcher.group(FORM_MATCHER_FORM_CONTENT));
                found = true;
                break;
            }
            start = formMatcher.end();
        }
        if (!found)
            throw new BuildMethodException("<Form> with defined action containing '" + text + "' was not found!");
        return this;
    }

    /**
     * Searches content for form tag with given text and extracts the <code>action</code> attribute.<br/>
     * <p>
     * <b>Content:</b><br/>
     * <code>&lt;form class="xx" action="http://blabla/" &gt<br />hahahaha&lt;form></code>
     * <br /><b>Using:</b>
     * <code><br />setActionFromFormWhereTagContains("class=\"xx\"")</code> - an action <code>http://blabla/</code> will be extracted<br/>
     * <i>also this is possible:</i>
     * <code><br />setActionFromFormWhereTagContains("hahahaha")</code> - an action <code>http://blabla/</code> will be extracted<br/>
     * </p>
     * <p>all <code>&amp;amp;</code> entity is replaced to <code>&</code> by default.</p>
     *
     * @param text              a string searched in the <code>form</code> tag
     * @param useFormParameters if true then it extracts all input parameters from the <code>form</code> tag
     * @return builder instance
     * @throws cz.vity.freerapid.plugins.exceptions.BuildMethodException
     *          when no such FORM with text in the tag was found
     */
    public MethodBuilder setActionFromFormWhereTagContains(final String text, final boolean useFormParameters) throws BuildMethodException {
        final Matcher formMatcher = getFormMatcher();
        boolean found = false;
        int start = 0;
        final String lower = text.toLowerCase();
        while (formMatcher.find(start)) {
            final String title = formMatcher.group(FORM_MATCHER_TITLE_GROUP);
            if (title.toLowerCase().contains(lower) || formMatcher.group(FORM_MATCHER_FORM_CONTENT).toLowerCase().contains(lower)) {
                inputForm(useFormParameters, title, formMatcher.group(FORM_MATCHER_FORM_CONTENT));
                found = true;
                break;
            }
            start = formMatcher.end();
        }
        if (!found)
            throw new BuildMethodException("Tag <Form> with containing '" + text + "' in the title was not found!");
        return this;
    }

    /**
     * Extracts string between other 2 strings and sets it as an action
     *
     * @param textBefore the text before extracted action
     * @param textAfter  the text after extracted action
     * @return builder instance
     * @throws BuildMethodException if there was no such text found between selected strings
     */
    public MethodBuilder setActionFromTextBetween(final String textBefore, final String textAfter) throws BuildMethodException {
        if (textBefore == null)
            throw new IllegalArgumentException("The text before the searched string cannot be null");
        if (textAfter == null)
            throw new IllegalArgumentException("The text after the searched string cannot be null");
        final Matcher matcher = Pattern.compile(Pattern.quote(Utils.rtrim(textBefore)) + "\\s*(.+?)\\s*" + Pattern.quote(Utils.ltrim(textAfter)), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(this.content);
        if (matcher.find()) {
            this.action = matcher.group(1).trim();
        } else
            throw new BuildMethodException(String.format("The searched text between string '%s' and '%s' was not found.", textBefore, textAfter));
        this.postMethod = HttpMethodEnum.GET;
        if (autoReplaceEntities)
            this.action = PlugUtils.replaceEntities(this.action);
        return this;
    }

    /**
     * Searches content for <code>&lt;A&gt;</code> tag with given text and extracts the <code>href</code> attribute.<br/>
     * <p>
     * <b>Content:</b><br/>
     * <code>&lt;a class="xx" href="http://blabla/" &gt<br />hahahaha&lt;a></code>
     * <br /><b>Using:</b>
     * <code><br />setActionFromAHrefWhereATagContains("hahahaha")</code> - an action <code>http://blabla/</code> will be extracted<br/>
     * </p>
     * <p>all <code>&amp;amp;</code> entity is replaced to <code>&</code> by default.</p>
     *
     * @param text a string searched in the <code>&lt;A&gt;</code> tag
     * @return builder instance
     * @throws cz.vity.freerapid.plugins.exceptions.BuildMethodException
     *          when no such <code>&lt;A&gt;</code> with text in the tag was found
     */
    public MethodBuilder setActionFromAHrefWhereATagContains(final String text) throws BuildMethodException {
        final Matcher matcher = getAHrefMatcher();
        boolean found = false;
        int start = 0;
        final String lower = text.toLowerCase();
        while (matcher.find(start)) {
            final String content = matcher.group(2);
            if (content.toLowerCase().contains(lower)) {
                this.action = matcher.group(1);
                if (autoReplaceEntities)
                    this.action = PlugUtils.replaceEntities(this.action);
                found = true;
                this.postMethod = HttpMethodEnum.GET;
                break;
            }
            start = matcher.end();
        }
        if (!found)
            throw new BuildMethodException("Tag <A> with containing '" + text + "' was not found!");
        return this;
    }

    /**
     * Searches content for <code>&lt;Img&gt;</code> tag with given text and extracts the <code>src</code> attribute.<br/>
     * <p>
     * <b>Content:</b><br/>
     * <code>&lt;mig class="xx" href="http://blabla/" &gt</code>
     * <br /><b>Using:</b>
     * <code><br />setActionFromImgSrcWhereTagContains("class=\"xx\"")</code> - an action <code>http://blabla/</code> will be extracted<br/>
     * <i>also this is possible:</i>
     * <code><br />setActionFromImgSrcWhereTagContains("/blabla")</code> - an action <code>http://blabla/</code> will be extracted<br/></p>
     * <p/>
     * <p>All <code>&amp;amp;</code> entity is replaced to <code>&</code> by default.</p>
     *
     * @param text a string searched in the <code>&lt;Img&gt;</code> tag
     * @return builder instance
     * @throws cz.vity.freerapid.plugins.exceptions.BuildMethodException
     *          when no such <code>&lt;Img&gt;</code> with text in the tag was found
     */
    public MethodBuilder setActionFromImgSrcWhereTagContains(final String text) throws BuildMethodException {
        if (imgPattern == null)
            imgPattern = Pattern.compile("(<img(?:.*?)src\\s?=\\s?(?:\"|')(.+?)(?:\"|')(?:.*?)>)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        final Matcher matcher = imgPattern.matcher(content);
        boolean found = false;
        int start = 0;
        final String lower = text.toLowerCase();
        while (matcher.find(start)) {
            final String content = matcher.group(1);
            if (content.toLowerCase().contains(lower)) {
                this.action = matcher.group(2);
                if (autoReplaceEntities)
                    this.action = PlugUtils.replaceEntities(this.action);
                found = true;
                this.postMethod = HttpMethodEnum.GET;
                break;
            }
            start = matcher.end();
        }
        if (!found)
            throw new BuildMethodException("Tag <img> with containing '" + text + "' was not found!");
        return this;
    }

    /**
     * Searches content for form tag with given text and extracts the <code>action</code> attribute.<br/>
     * This method is useful where the FORM tags has empty body or there is only 1 FORM tag in the content.
     * <p>
     * <b>Content:</b><br/>
     * <code>...<br/>&lt;form action="http://blabla1/" &gt;hahahaha&lt;form><br />
     * &lt;form action="http://blabla2/" &gt;hahahaha&lt;form><br/>...<br/></code>
     * <br /><b>Using:</b>
     * <code><br />setActionFromFormByIndex(1)</code> - an action <code>http://blabla1/</code> will be extracted<br/>
     * <i>also this is possible:</i>
     * <code><br />setActionFromFormByIndex(2)</code> - an action <code>http://blabla2/</code> will be extracted<br/>
     * </p>
     * <p>All <code>&amp;amp;</code> entity is replaced to <code>&</code> by default.</p>
     *
     * @param index             index of the form tag in the content - the lowest index has number 1
     * @param useFormParameters if true then it extracts all input parameters from the <code>form</code> tag
     * @return builder instance
     * @throws cz.vity.freerapid.plugins.exceptions.BuildMethodException
     *          when no such FORM with text in the tag was found
     */
    public MethodBuilder setActionFromFormByIndex(final int index, final boolean useFormParameters) throws BuildMethodException {
        if (index < 1)
            throw new IllegalArgumentException("Index must be higher or equal to 1");
        final Matcher formMatcher = getFormMatcher();
        int start = 0;
        int count = 1;
        boolean found = false;
        while (formMatcher.find(start)) {
            if (count++ == index) {
                found = true;
                inputForm(useFormParameters, formMatcher.group(FORM_MATCHER_TITLE_GROUP), formMatcher.group(FORM_MATCHER_FORM_CONTENT));
                break;
            }
            start = formMatcher.end();
        }
        if (!found) {
            throw new BuildMethodException("<Form> with index " + index + " from the top was not found");
        }
        return this;
    }

    private HttpMethodEnum extractMethod(String title) {
        final Pattern actionPattern = Pattern.compile("method\\s?=\\s?(?:\"|')?\"(POST|GET)(?:\"|'|\\s*>)", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        final Matcher matcher = actionPattern.matcher(title);
        if (matcher.find()) {
            if ("POST".equalsIgnoreCase(matcher.group(1)))
                return HttpMethodEnum.POST;
            else
                return HttpMethodEnum.GET;
        }
        return HttpMethodEnum.POST;
    }

    /**
     * Sets "form" action for builder.
     *
     * @param action action as string value
     * @return builder instance
     */
    public MethodBuilder setMethodAction(String action) {
        this.action = action;
        return this;
    }

    /**
     * Sets base url for the final method.<br/>
     * Slashes (characters '/') at the end of the string to produce final method action are handled automatically.
     *
     * @param baseURL base URL as text
     * @return builder instance
     * @throws cz.vity.freerapid.plugins.exceptions.BuildMethodException
     *          if base url does not match URI string
     */
    public MethodBuilder setBaseURL(String baseURL) throws BuildMethodException {
        if (baseURL == null) {
            this.baseURL = null;
            return this;
        }

        try {
            new URI(baseURL);
        } catch (URISyntaxException e) {
            throw new BuildMethodException(e.getMessage());
        }
        this.baseURL = baseURL;
        return this;
    }

    /**
     * Searches for the parameters in the content and adds them (with their values) to the final result HTTP method.
     *
     * @param parameters parameter names that should be added to generate within the result HTTP method
     * @return builder instance
     * @throws BuildMethodException thrown when any of the parameter was not found
     */
    public MethodBuilder addParameters(String... parameters) throws BuildMethodException {
        if (parameters.length == 0)
            throw new IllegalArgumentException("You have to provide some parameter names");
        final Set<String> set = new HashSet<String>(parameters.length);
        set.addAll(Arrays.asList(parameters));
        if (parameterPatterns == null)
            parameterPatterns = Pattern.compile("name\\s?=\\s?(?:\"|')?(.*?)(?:\"|'|\\s).*?value\\s?=\\s?(?:\"|')?(.*?)(?:\"|'|\\s*>)", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

        final Matcher matcher = parameterPatterns.matcher(content);
        int start = 0;
        String param;
        while (matcher.find(start)) {
            param = matcher.group(1);
            if (set.contains(param)) {
                set.remove(param);
                this.parameters.put(param, matcher.group(2));
            }
            if (set.isEmpty())
                break;
            start = matcher.end();
        }
        if (!set.isEmpty()) {
            throw new BuildMethodException("Following parameters: " + Arrays.toString(set.toArray()) + " were not found");
        }
        return this;
    }

    /**
     * Removes POST/GET parameter from the result method.
     *
     * @param name name of parameter
     * @return builder instance
     */
    public MethodBuilder removeParameter(final String name) {
        parameters.remove(name);
        return this;
    }

    /**
     * All <code>&amp;amp;</code> entity is replaced to <code>&</code>.
     *
     * @return builder instance
     */
    public MethodBuilder replaceEntitiesInAction() {
        action = PlugUtils.replaceEntities(action);
        return this;
    }

    /**
     * Removes all parameters from the result method. <br />
     * Note - only extracted parameters and manually added parameters are removed. Parameters in action value are not removed.
     *
     * @return builder instance
     */
    public MethodBuilder clearParameters() {
        parameters.clear();
        return this;
    }

    /**
     * Adds new parameter to result HTTP method.
     * Note: Value of the parameter is not encoded - for that purpose you should use <code>setAndEncodeParameter</code> method.
     *
     * @param name  name of parameter
     * @param value value of the parameter; if the value is null, parameter is removed
     * @return builder instance
     * @see cz.vity.freerapid.plugins.webclient.MethodBuilder#setAndEncodeParameter(String, String)
     */
    public MethodBuilder setParameter(String name, String value) {
        if (value == null) {
            return removeParameter(name);
        } else {
            parameters.put(name, value);
        }
        return this;
    }

    /**
     * Adds new parameter to result HTTP method.
     * Note: Value of the parameter is encoded - for not doing that you should use <code>setParameter</code> method.
     *
     * @param name  name of parameter
     * @param value value of the parameter; if the value is null, parameter is removed
     * @return builder instance
     * @see cz.vity.freerapid.plugins.webclient.MethodBuilder#setParameter(String, String)
     */
    public MethodBuilder setAndEncodeParameter(String name, String value) throws BuildMethodException {
        return setParameter(name, encode(value));
    }

    private String encode(String value) throws BuildMethodException {
        String encoded;
        try {
            encoded = URLEncoder.encode(value, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new BuildMethodException(e.getMessage());
        }
        return encoded;
    }

    /**
     * Calls URLEncoder to over the last part of the action or base URL.<br />
     * Replaces everything after the last '/' character.
     *
     * @return builder instance
     * @throws BuildMethodException if action or base url is null or no '/' was found.
     * @deprecated
     */
    public MethodBuilder encodeLastPartOfAction() throws BuildMethodException {
        setEncodePathAndQuery(true);
        return this;
    }

    /**
     * Calls URLEncoder to over path and query part of the action or base URL.<br />
     * <b>Note:</b> This method just sets the flag that the result action in the httpmethod should be encoded.
     *
     * @param b
     * @return builder instance
     * @throws BuildMethodException if action or base url is null or no '/' was found.
     */
    public MethodBuilder setEncodePathAndQuery(boolean b) throws BuildMethodException {
        this.encodePathAndQuery = b;
        return this;
    }


    /**
     * Setter for property 'referer'.
     *
     * @param referer Value to set for property 'referer'.
     */
    public MethodBuilder setReferer(String referer) {
        this.referer = referer;
        return this;
    }

    /**
     * Getter for property 'referer'.
     *
     * @return Value for property 'referer'.
     */
    public String getReferer() {
        return referer;
    }

    /**
     * Returns result GET method.<br/>
     * Result GET method is composed by baseURL + action (if baseURL is not null).<br/>
     * At least one of the parameter has to be not null and the string has to start with 'http'.
     *
     * @return new instance of HttpMethod with GET request
     * @throws BuildMethodException
     */
    public HttpMethod toGetMethod() throws BuildMethodException {
        if (referer != null)
            client.setReferer(referer);

        String uri = generateURL();
        if (!parameters.isEmpty()) {
            final StringBuilder builder = new StringBuilder(uri);
            if (!uri.contains("?"))
                builder.append('?');
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                final char lastChar = builder.charAt(builder.length() - 1);
                if (lastChar != '&' && lastChar != '?' && lastChar != '/')
                    builder.append('&');
                builder.append(entry.getKey()).append('=');
                if (!encodeParameters)
                    builder.append(entry.getValue());
                else
                    builder.append(encode(entry.getValue()));
            }
            uri = builder.toString();
        }
        String s;
        try {
            s = (encodePathAndQuery) ? URIUtil.encodePathQuery(uri, encoding) : uri;
        } catch (URIException e) {
            throw new BuildMethodException("Cannot create URI");
        }
        uri = checkURI(s);
        return client.getGetMethod(uri);
    }

    private void inputForm(boolean useFormParameters, String title, String content) {
        this.action = extractAction(title);
        if (this.action != null) {
            if (autoReplaceEntities)
                this.action = PlugUtils.replaceEntities(this.action);
        } else logger.info("Form has no defined action attribute");
        this.postMethod = extractMethod(title);
        if (useFormParameters)
            populateParameters(content);
    }

    private void populateParameters(final String content) {
        if (parameterInputPattern == null)
            parameterInputPattern = Pattern.compile("<input (.+?>)", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (parameterNamePattern == null)
            parameterNamePattern = Pattern.compile("name\\s?=\\s?(?:\"|')?(.*?)(?:\"|'|\\s|>)", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (parameterValuePattern == null)
            parameterValuePattern = Pattern.compile("value\\s?=\\s?(?:\"|')?(.*?)(?:\"|'|\\s|>)", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        int start = 0;
        Matcher matcher = parameterInputPattern.matcher(content);
        while (matcher.find(start)) {
            final String input = matcher.group(1);
            final Matcher matchName = parameterNamePattern.matcher(input);
            if (matchName.find()) {
                String paramName = matchName.group(1);
                final Matcher matchValue = parameterValuePattern.matcher(input);
                if (matchValue.find()) {
                    parameters.put(paramName, matchValue.group(1));
                } else
                    parameters.put(paramName, "");
            }
            start = matcher.end();
        }
    }

    private String extractAction(String title) {
        final Pattern actionPattern = Pattern.compile("action\\s?=\\s?(?:\"|')?\"(.+?)(?:\"|'|\\s*>)", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        final Matcher matcher = actionPattern.matcher(title);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            //logger.info("No action find");
            return null;
        }
    }

    private String generateURL() throws BuildMethodException {
        if (baseURL == null && action == null)
            throw new BuildMethodException("Both action and base url has to be not null");
        if (action != null) {
            if (action.toLowerCase().startsWith("http")) {
                return action;
            } else {
                if (baseURL != null && baseURL.toLowerCase(Locale.ENGLISH).startsWith("http")) {
                    if (action != null) {
                        String ac = action;
                        if (action.startsWith("/"))
                            ac = action.substring(1);
                        else if (action.startsWith("./"))
                            ac = action.substring(2);
                        if (!baseURL.endsWith("/"))
                            return baseURL + "/" + ac;
                        else
                            return baseURL + ac;
                    }
                } else
                    throw new BuildMethodException("Cannot build method. No base URL defined - no action (baseURL or action) starts with 'http'");
            }
        }
        return baseURL;
    }

    private String checkURI(String url) throws BuildMethodException {
        final String uriCharset = client.getHTTPClient().getParams().getUriCharset();
        try {
            return new org.apache.commons.httpclient.URI(url, true, uriCharset).toString();
        } catch (URIException e) {
            logger.warning(String.format("Invalid URL - '%s' does not match URI specification", url));
            try {
                return new org.apache.commons.httpclient.URI(URIUtil.encodePathQuery(url, encoding), true, uriCharset).toString();
            } catch (URIException e1) {
                throw new BuildMethodException("Invalid URL - does not match URI specification:" + url);
            }
        }
    }

    /**
     * Getter for property 'formMatcher'.
     *
     * @return Value for property 'formMatcher'.
     */
    private Matcher getFormMatcher() {
        if (formPattern == null)
            formPattern = Pattern.compile("<form(.*?)>(.*?)</form", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        return formPattern.matcher(content);
    }

    /**
     * Getter for property 'formMatcher'.
     *
     * @return Value for property 'formMatcher'.
     */
    private Matcher getAHrefMatcher() {
        if (aHrefPattern == null)
            aHrefPattern = Pattern.compile("<a(?:.*?)href\\s?=\\s?(?:\"|')(.+?)(?:\"|')(?:.*?)>(.*?)</a>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        return aHrefPattern.matcher(content);
    }

    /**
     * Returns result POST method.<br/>
     * Result POST method is composed by baseURL + action (if baseURL is not null).<br/>
     * All parameters are set and encoded.
     * At least one of the parameter has to be not null and the string has to start with 'http'.
     *
     * @return new instance of HttpMethod with POST request
     * @throws BuildMethodException
     */
    public HttpMethod toPostMethod() throws BuildMethodException {
        if (referer != null)
            client.setReferer(referer);
        String s = generateURL();
        if (encodePathAndQuery)
            try {
                s = URIUtil.encodePathQuery(s, encoding);
            } catch (URIException e) {
                throw new BuildMethodException("Cannot create URI");
            }
        s = checkURI(s);
        final PostMethod postMethod = client.getPostMethod(s);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            postMethod.addParameter(entry.getKey(), (encodeParameters) ? encode(entry.getValue()) : entry.getValue());
        }
        return postMethod;
    }

    /**
     * Returns result HTTP method.<br/>
     * Result method is composed by baseURL + action (if baseURL is not null).<br/>
     * All parameters are set and encoded.
     * At least one of the parameter has to be not null and the string has to start with 'http'.
     *
     * @return new instance of HttpMethod - result method is given by postMethod settings - how it was extracted from the content - default method is POST
     * @throws BuildMethodException
     */
    public HttpMethod toHttpMethod() throws BuildMethodException {
        if (postMethod == HttpMethodEnum.POST) {
            return toPostMethod();
        } else return toGetMethod();
    }


    /**
     * Getter for property 'action'.
     *
     * @return Value for property 'action'.
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets new action
     *
     * @param action
     * @return
     */
    public MethodBuilder setAction(String action) {
        this.action = action;
        if (action.toLowerCase().startsWith("http")) {
            this.postMethod = HttpMethodEnum.GET;
        }
        if (isAutoReplaceEntitiesEnabled())
            replaceEntitiesInAction();
        return this;
    }

    /**
     * Getter for property 'baseURL'.
     *
     * @return Value for property 'baseURL'.
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Getter for property 'client'.
     *
     * @return Value for property 'client'.
     */
    public HttpDownloadClient getClient() {
        return client;
    }

    /**
     * Setter for property 'client'.
     *
     * @param client Value to set for property 'client'.
     */
    public void setClient(HttpDownloadClient client) {
        this.client = client;
    }

    /**
     * Getter for property 'encodeParameters'.
     *
     * @return Value for property 'encodeParameters'.
     */
    public boolean isEncodeParameters() {
        return encodeParameters;
    }

    /**
     * Setter for property 'encodeParameters'.
     *
     * @param encodeParameters Value to set for property 'encodeParameters'.
     */
    public MethodBuilder setEncodeParameters(boolean encodeParameters) {
        this.encodeParameters = encodeParameters;
        return this;
    }

    /**
     * Getter for property 'encoding'.
     *
     * @return Value for property 'encoding'.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Setter for property 'encoding'.
     *
     * @param encoding Value to set for property 'encoding'.
     */
    public MethodBuilder setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    /**
     * Getter for property 'autoReplaceEntities'.
     *
     * @return Value for property 'autoReplaceEntities'.
     */
    public boolean isAutoReplaceEntitiesEnabled() {
        return autoReplaceEntities;
    }

    /**
     * Setter for property 'autoReplaceEntities'.
     *
     * @param autoReplaceEntities Value to set for property 'autoReplaceEntities'.
     */
    public void setAutoReplaceEntities(boolean autoReplaceEntities) {
        this.autoReplaceEntities = autoReplaceEntities;
    }

    /**
     * Returns parameter map - name/value
     *
     * @return hash map with key pair name and value
     */
    public Map<String, String> getParameters() {
        return parameters;
    }
}
