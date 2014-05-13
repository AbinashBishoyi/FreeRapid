package cz.vity.freerapid.plugins.webclient.hoster;

import org.jdesktop.application.AbstractBean;

import java.io.Serializable;

/**
 * Simple bean to represent user name and password
 *
 * @author Ladislav Vitasek
 */
public class PremiumAccount extends AbstractBean implements Serializable {
    /**
     * Field serialVersionUID
     */
    private final static long serialVersionUID = 1L;

    /**
     * Field username
     */
    private String username;
    /**
     * Field password
     */
    private String password;

    /**
     * Getter for property 'username'.
     *
     * @return Value for property 'username'.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for property 'username'.
     *
     * @param username Value to set for property 'username'.
     */
    public void setUsername(String username) {
        String oldValue = this.username;
        this.username = username;
        firePropertyChange("username", oldValue, this.username);
    }

    /**
     * Checks whether user name and password is set
     *
     * @return true if username and password has been set
     */
    public boolean isSet() {
        return isUserNameSet() && isPasswordSet();
    }

    /**
     * Checks whether password is not empty on this object
     *
     * @return true, if password has been set
     */
    public boolean isPasswordSet() {
        return this.password != null && !this.password.isEmpty();
    }

    /**
     * Checks whether user name is not empty on this object
     *
     * @return true, if user name has been set
     */
    public boolean isUserNameSet() {
        return this.username != null && !this.username.isEmpty();
    }

    /**
     * Getter for property 'password'.
     *
     * @return Value for property 'password'.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for property 'password'.
     *
     * @param password Value to set for property 'password'.
     */
    public void setPassword(String password) {
        String oldValue = this.password;
        this.password = password;
        firePropertyChange("password", oldValue, this.password);
    }

    /**
     * Factory method to create new premium account instance
     *
     * @param username user name
     * @param password password
     * @return new instance of PremiumAccount class
     */
    public static PremiumAccount create(String username, String password) {
        final PremiumAccount premiumAccount = new PremiumAccount();
        premiumAccount.setUsername(username);
        premiumAccount.setPassword(password);
        return premiumAccount;
    }
}
