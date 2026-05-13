package webFTP.steps.serenity;

import net.thucydides.core.annotations.Step;
import webFTP.pages.UploadFilePage;

import java.io.File;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

public class UploadFilePageSteps {

    UploadFilePage uploadPage;

    @Step
    public void select_file(String resourceFileName) {
        String path = new File("src/test/resources/" + resourceFileName).getAbsolutePath();
        uploadPage.select_file(path);
    }

    @Step
    public void click_upload() {
        uploadPage.click_upload();
    }

    @Step
    public void back() {
        uploadPage.click_back();
    }

    @Step
    public void upload(String resourceFileName) {
        select_file(resourceFileName);
        click_upload();
        should_see_message("has been transferred to the FTP server");
        back();
    }

    @Step
    public void should_see_message(String message) {
        assertThat(uploadPage.getContent(), hasItem(containsString(message)));
    }
}
