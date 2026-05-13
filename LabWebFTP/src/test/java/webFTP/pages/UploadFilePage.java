package webFTP.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

public class UploadFilePage extends PageObject {

    @FindBy(xpath = "(//input[@type='file'])[1]")
    private WebElementFacade fileInput;

    @FindBy(xpath = "//*[@id=\"UploadForm\"]/a[2]/img")
    private WebElementFacade uploadButton;

    @FindBy(xpath = "//*[@id=\"UploadForm\"]/a[1]/img")
    private WebElementFacade backButton;

    public void select_file(String absoluteFilePath) {
        fileInput.sendKeys(absoluteFilePath);
    }

    public void click_upload() {
        uploadButton.click();
    }

    public void click_back() {
        backButton.click();
    }

    public List<String> getContent() {
        WebElementFacade definitionList = find(By.tagName("div"));
        return definitionList.findElements(By.tagName("form")).stream()
                .map(element -> element.getText())
                .collect(Collectors.toList());
    }
}
