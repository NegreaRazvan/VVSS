package webFTP.features.rename;

import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.annotations.UseTestDataFrom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import webFTP.features.TestSetup;
import webFTP.steps.serenity.*;

@RunWith(SerenityParameterizedRunner.class)
@UseTestDataFrom("src\\test\\resources\\invalidRenameData.csv")
public class RenameDirectoryInvalidTest extends TestSetup {

    @Managed(uniqueSession = true)
    public WebDriver webdriver;

    @Steps
    public LoginPageSteps loginPage;

    @Steps
    public AccountPageSteps accountPage;

    @Steps
    public NewDirectoryPageSteps newDirectory;

    @Steps
    public RenameDirectoryPageSteps renamePage;

    @Steps
    public DeleteDirectoryPageSteps deleteDirectory;

    @Steps
    public LogoutPageSteps logoutPage;

    String dirName, invalidNewName, errorMessage;

    @Test
    public void renameDirectoryInvalid() {
        long ts = System.currentTimeMillis();
        String uniqueDirName = dirName + "_" + ts;

        webdriver.get("https://vvss:strugure@scs.ubbcluj.ro/vvta/net2ftp/index.php");
        loginPage.click_saveCookies();
        loginPage.login_steps("localhost", "vvta1", "vvta1");
        accountPage.should_be_in_user_directory("/home/vvta1");

        accountPage.newDirectory();
        newDirectory.createDirectory(uniqueDirName);
        accountPage.should_be_able_to_see_new_directory(uniqueDirName);

        accountPage.select_directory_to_delete(uniqueDirName);
        accountPage.rename_selected_directory();
        renamePage.attempt_rename(invalidNewName);
        renamePage.should_see_message(errorMessage);
        renamePage.back();

        accountPage.select_directory_to_delete(uniqueDirName);
        accountPage.delete_selected_directory();
        deleteDirectory.delete_directory(uniqueDirName);
        accountPage.should_not_be_able_to_see_new_directory(uniqueDirName);

        accountPage.logout();
        logoutPage.should_see_logout_message("You have logged out from the FTP server.");
    }
}
