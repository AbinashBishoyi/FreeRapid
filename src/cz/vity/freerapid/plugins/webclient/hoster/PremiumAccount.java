package cz.vity.freerapid.plugins.webclient.hoster;

import org.jdesktop.application.AbstractBean;

import java.io.Serializable;

/**
 * @author Ladislav Vitasek
 */
public class PremiumAccount extends AbstractBean implements Serializable {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        String oldValue = this.username;
        this.username = username;
        firePropertyChange("username", oldValue, this.username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        String oldValue = this.password;
        this.password = password;
        firePropertyChange("password", oldValue, this.password);
    }

    public static PremiumAccount create(String username, String password) {
        final PremiumAccount premiumAccount = new PremiumAccount();
        premiumAccount.setUsername(username);
        premiumAccount.setPassword(password);
        return premiumAccount;
    }
}
