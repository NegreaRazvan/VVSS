package webFTP.features;

import org.junit.BeforeClass;

public abstract class TestSetup {

    @BeforeClass
    public static void setupClass() {
        // We manually inject the path into the system properties.
        // Serenity will see this property before it tries to launch the browser.
        // IMPORTANT: Ensure this path is 100% correct relative to your project root.
        System.setProperty("webdriver.edge.driver", "src/test/resources/windows/msedgedriver.exe");
    }
}