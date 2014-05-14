package cz.vity.freerapid.core.application;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.FWProp;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdesktop.application.AbstractBean;
import org.jdesktop.swingx.error.ErrorInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

/**
 * Info pro odeslani na server
 *
 * @author Vity
 */
public class SubmitErrorInfo extends AbstractBean {
    private String name = "";
    private String email = "";
    private String comment = "";
    private ErrorInfo errorInfo;

    public SubmitErrorInfo(ErrorInfo errorInfo) {
        super();
        this.errorInfo = errorInfo;
        this.setName(AppPrefs.getProperty(FWProp.SUBMIT_ERROR_NAME));
        this.setEmail(AppPrefs.getProperty(FWProp.SUBMIT_ERROR_EMAIL));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldValue = getName();
        this.name = name;
        firePropertyChange("name", oldValue, name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String oldValue = getEmail();
        this.email = email;
        firePropertyChange("email", oldValue, email);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        String oldValue = getComment();
        this.comment = comment;
        firePropertyChange("comment", oldValue, comment);
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

    private static String getUserInfo() {
        final StringBuilder builder = new StringBuilder();
        final String unknown = "Unknown";
        builder.append("java.version=").append(System.getProperty("java.version", unknown)).append('\n');
        builder.append("os.name=").append(System.getProperty("os.name", unknown)).append('\n');
        builder.append("user.language=").append(System.getProperty("user.language", unknown)).append('\n');
        builder.append("user.name=").append(System.getProperty("user.name", unknown)).append('\n');
        builder.append("user.dir=").append(System.getProperty("user.dir", unknown)).append('\n');
        builder.append("user.country=").append(System.getProperty("user.country", unknown)).append('\n');
        return builder.toString();
    }


    public void toURLPostData(PostMethod method) {
        String email = getEmail();
        if (email == null)
            email = "";
        AppPrefs.storeProperty(FWProp.SUBMIT_ERROR_EMAIL, email);
        String name = getName();
        if (name == null)
            name = "";
        AppPrefs.storeProperty(FWProp.SUBMIT_ERROR_NAME, name);


        method.addParameter("product", Consts.PRODUCT);
        method.addParameter("version", Consts.VERSION);
        method.addParameter("name", name);
        method.addParameter("locale", Locale.getDefault().getLanguage());
        method.addParameter("os", System.getProperty("os.name", "Unknown"));
        method.addParameter("comment", getComment());
        method.addParameter("email", email);
        method.addParameter("userinfo", getUserInfo());
        getErrorInfo().getErrorException().printStackTrace();
        final StringWriter sw = new StringWriter();
        getErrorInfo().getErrorException().printStackTrace(new PrintWriter(sw));
        method.addParameter("exception", sw.toString());
    }

}
