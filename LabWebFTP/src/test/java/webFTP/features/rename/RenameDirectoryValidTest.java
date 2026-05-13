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
@UseTestDataFrom("src\\test\\resources\\validRenameData.csv")
public class RenameDirectoryValidTest extends TestSetup {

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

    String oldName, newName;

    @Test
    public void renameDirectory() {
        long ts = System.currentTimeMillis();
        String uniqueOld = oldName + "_" + ts;
        String uniqueNew = newName + "_" + (ts + 1);

        webdriver.get("https://vvss:strugure@scs.ubbcluj.ro/vvta/net2ftp/index.php");
        loginPage.click_saveCookies();
        loginPage.login_steps("localhost", "vvta1", "vvta1");
        accountPage.should_be_in_user_directory("/home/vvta1");

        accountPage.newDirectory();
        newDirectory.createDirectory(uniqueOld);
        accountPage.should_be_able_to_see_new_directory(uniqueOld);

        accountPage.select_directory_to_delete(uniqueOld);
        accountPage.rename_selected_directory();
        renamePage.rename_directory(uniqueOld, uniqueNew);

        accountPage.should_be_able_to_see_new_directory(uniqueNew);
        accountPage.should_not_be_able_to_see_new_directory(uniqueOld);

        accountPage.select_directory_to_delete(uniqueNew);
        accountPage.delete_selected_directory();
        deleteDirectory.delete_directory(uniqueNew);
        accountPage.should_not_be_able_to_see_new_directory(uniqueNew);

        accountPage.logout();
        logoutPage.should_see_logout_message("You have logged out from the FTP server.");
    }
}
