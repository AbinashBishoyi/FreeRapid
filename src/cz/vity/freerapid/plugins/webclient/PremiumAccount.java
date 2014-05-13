package cz.vity.freerapid.plugins.webclient;

import org.jdesktop.application.AbstractBean;

/**
 * @author Ladislav Vitasek
 */
public class PremiumAccount extends AbstractBean {
    private String username;
    private String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static PremiumAccount create(String username, String password) {
        final PremiumAccount premiumAccount = new PremiumAccount();
        premiumAccount.setUsername(username);
        premiumAccount.setPassword(password);
        return premiumAccount;
    }
}
