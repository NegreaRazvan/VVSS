package webFTP.features.scenario;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import webFTP.features.TestSetup;
import webFTP.steps.serenity.*;

@RunWith(SerenityRunner.class)
public class FullScenarioTest extends TestSetup {

    @Managed(uniqueSession = true)
    public WebDriver webdriver;

    @Steps
    public LoginPageSteps loginPage;

    @Steps
    public AccountPageSteps accountPage;

    @Steps
    public NewDirectoryPageSteps newDirectory;

    @Steps
    public RenameDirectoryPageSteps renameDirectory;

    @Steps
    public UploadFilePageSteps uploadFile;

    @Steps
    public DeleteDirectoryPageSteps deleteDirectory;

    @Steps
    public LogoutPageSteps logoutPage;

    private static final String UPLOAD_FILE = "test_upload.txt";

    @Test
    public void fullScenario() {
        long ts = System.currentTimeMillis();
        String dirName = "scenarioDir_" + ts;
        String renamedDir = "scenarioRenamed_" + (ts + 1);

        // Step 1: Login
        webdriver.get("https://vvss:strugure@scs.ubbcluj.ro/vvta/net2ftp/index.php");
        loginPage.click_saveCookies();
        loginPage.login_steps("localhost", "vvta1", "vvta1");
        accountPage.should_be_in_user_directory("/home/vvta1");

        // Step 2: Create directory
        accountPage.newDirectory();
        newDirectory.createDirectory(dirName);
        accountPage.should_be_able_to_see_new_directory(dirName);

        // Step 3: Rename directory
        accountPage.select_directory_to_delete(dirName);
        accountPage.rename_selected_directory();
        renameDirectory.rename_directory(dirName, renamedDir);
        accountPage.should_be_able_to_see_new_directory(renamedDir);
        accountPage.should_not_be_able_to_see_new_directory(dirName);

        // Step 4: Navigate into renamed directory
        accountPage.navigate_into_directory(renamedDir);
        accountPage.should_be_in_directory(renamedDir);

        // Step 5: Upload file
        accountPage.upload_file();
        uploadFile.upload(UPLOAD_FILE);
        accountPage.should_be_able_to_see_new_directory(UPLOAD_FILE);

        // Step 6: Navigate back
        accountPage.navigate_back();
        accountPage.should_be_in_user_directory("/home/vvta1");

        // Step 7: Delete renamed directory (recursively removes uploaded file too)
        accountPage.select_directory_to_delete(renamedDir);
        accountPage.delete_selected_directory();
        deleteDirectory.delete_directory(renamedDir);
        accountPage.should_not_be_able_to_see_new_directory(renamedDir);

        // Step 8: Logout
        accountPage.logout();
        logoutPage.should_see_logout_message("You have logged out from the FTP server.");
    }
}
