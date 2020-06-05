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
        driver.get("http://10.230.230.60:8080/NETSales/");
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
            int localTime = 40000;
            while (true) {
                if(xpath.startsWith("/")||xpath.startsWith("(")) {
                    elements = driver.findElements(By.xpath(xpath));
                }else{
                    elements = driver.findElements(By.id(xpath));
                }
                if(elements.size()>0){
                    break;
                }
                localTime = localTime - 100;
                Thread.sleep(100);
                System.out.println("Remaining time to locate: "+localTime+"ms");
                if(localTime<=0){
                    break;
                }
            }
            int count = elements.size();
            if (count > 1) {
                System.out.println("The xpath '" + xpath + "' returned " + count + " elements");
            } else if (count == 1) {
                System.out.println("The xpath '" + xpath + "' returned one element");
            } else {
                System.out.println("The xpath '" + xpath + "' returned no elements");
            }
        } catch (InvalidSelectorException e) {
            System.out.println("The xpath '" + xpath + "' is not valid");
        }catch (WebDriverException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
