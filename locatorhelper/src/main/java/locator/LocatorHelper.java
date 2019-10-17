package locator;

import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class LocatorHelper {
    private static WebDriver driver = null;

    public static void main(String[] args) {
        createIE();
        driver.get("http://www.google.com.br/");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Scanner scan = new Scanner(System.in);
        String mainXpath = "";
        String lastXpath = "";

        while (!mainXpath.equals("exit")) {
            System.out.println("Type an xpath: ");
            lastXpath = mainXpath;
            mainXpath = scan.nextLine();
            if (lastXpath.length() > 0) {
                List<WebElement> elements = getElements(lastXpath, true);
                if (elements != null) {
                    for (WebElement element : elements) {
                        js.executeScript("arguments[0].style.border='1px solid orange'", element);
                    }
                }
            }
            List<WebElement> elements = getElements(mainXpath);
            if (elements != null) {
                for (WebElement element : elements) {
                    js.executeScript("arguments[0].style.border='3px solid red'", element);
                }
            }
            System.out.println("---------------------------------------------------------------------------------------------------");
        }
    }

    private static List<WebElement> getElements(String xpath, boolean... disableLog) {
        List<WebElement> elements = null;
        try {
            elements = driver.findElements(By.xpath(xpath));
            int count = elements.size();
            if (disableLog.length == 0) {
                if (count > 1) {
                    System.out.println("The xpath '" + xpath + "' returned " + count + " elements");
                } else if (count == 1) {
                    System.out.println("The xpath '" + xpath + "' returned one element");
                } else {
                    System.out.println("The xpath '" + xpath + "' returned no elements");
                }
            }
        } catch (InvalidSelectorException e) {
            if (disableLog.length == 0) {
                System.out.println("The xpath '" + xpath + "' is not valid");
            }
        }
        return elements;
    }

    private static void createIE() {
        System.setProperty("webdriver.ie.driver", "src/main/resources/drivers/IEDriverServerNew.exe");
        DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
        caps.setCapability("ignoreZoomSetting", true);
        caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        caps.setCapability(InternetExplorerDriver.ELEMENT_SCROLL_BEHAVIOR, true);
        DesiredCapabilities.internetExplorer().setCapability("ignoreProtectedModeSettings", true);
        caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        caps.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
        caps.setJavascriptEnabled(true);

        driver = new InternetExplorerDriver(caps);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }
}
