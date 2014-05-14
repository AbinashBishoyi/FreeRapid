package cz.vity.freerapid.gui.managers.interfaces;

import cz.vity.freerapid.model.UserAccount;

import java.util.List;

/**
 * @author Vity
 * @since 0.87
 */
public interface UserAccountProvider {
    List<UserAccount> getUserAccounts(String pluginId);
    UserAccount getUserAccountByPriority(String pluginId);
}
