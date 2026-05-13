package webFTP.steps.serenity;

import net.thucydides.core.annotations.Step;
import webFTP.pages.RenameDirectoryPage;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

public class RenameDirectoryPageSteps {

    RenameDirectoryPage renamePage;

    @Step
    public void enter_new_name(String name) {
        renamePage.enter_new_name(name);
    }

    @Step
    public void confirm_rename() {
        renamePage.click_confirm();
    }

    @Step
    public void back() {
        renamePage.click_back();
    }

    @Step
    public void rename_directory(String oldName, String newName) {
        enter_new_name(newName);
        confirm_rename();
        should_see_message("was successfully renamed to");
        back();
    }

    @Step
    public void attempt_rename(String newName) {
        if (newName == null || newName.isEmpty()) {
            renamePage.submit_with_empty_name();
        } else {
            enter_new_name(newName);
            confirm_rename();
        }
    }

    @Step
    public void should_see_message(String message) {
        assertThat(renamePage.getContent(), hasItem(containsString(message)));
    }
}
