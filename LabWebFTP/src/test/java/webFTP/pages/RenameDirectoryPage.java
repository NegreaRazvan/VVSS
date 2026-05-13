package webFTP.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import java.util.List;
import java.util.stream.Collectors;

public class RenameDirectoryPage extends PageObject {

    @FindBy(name = "newNames[1]")
    private WebElementFacade newNameInput;

    @FindBy(xpath = "//*[@id=\"RenameForm\"]/a[2]/img")
    private WebElementFacade confirmButton;

    @FindBy(xpath = "//*[@id=\"RenameForm\"]/a[1]/img")
    private WebElementFacade backButton;

    public void enter_new_name(String name) {
        newNameInput.clear();
        newNameInput.type(name);
    }

    public void click_confirm() {
        confirmButton.click();
    }

    public void click_back() {
        backButton.click();
    }

    public void submit_with_empty_name() {
        ((JavascriptExecutor) getDriver()).executeScript(
            "document.getElementsByName('newNames[1]')[0].value = '';" +
            "document.forms['RenameForm'].submit();"
        );
    }

    public List<String> getContent() {
        WebElementFacade definitionList = find(By.tagName("div"));
        return definitionList.findElements(By.tagName("form")).stream()
                .map(element -> element.getText())
                .collect(Collectors.toList());
    }
}
