package locator;

import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
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
        Set<String> windows = driver.getWindowHandles();
        System.out.println("Amount of pages found: " + windows.size());
        boolean encontrou = false;
        for (String pagina : windows) {
            try {
                System.out.println("Trying the page: " + pagina);
                driver.switchTo().window(pagina);
                Thread.sleep(500);
                int quantidadeDeFrames = driver.findElements(By.xpath("//frame")).size();
                if (quantidadeDeFrames > 0) {
                    System.out.println("Amount of pages found: " + quantidadeDeFrames + "");
                    for (int i = 0; i < quantidadeDeFrames; i++) {
                        driver.switchTo().frame(i);
                        try {
                            elements = driver.findElements(By.xpath(xpath));
                            break;
                        } catch (IllegalArgumentException e1) {
                            driver.switchTo().alert();
                        } catch (WebDriverException ex) {
                            System.out.println("frame: " + i);
                        }
                    }
                } else {
                    elements = driver.findElements(By.xpath(xpath));
                    break;
                }
            }catch (InvalidSelectorException e) {
                if (disableLog.length == 0) {
                    System.out.println("The xpath '" + xpath + "' is not valid");
                }
            } catch (IllegalArgumentException e1) {
                driver.switchTo().alert();
            } catch (WebDriverException ex) {
                System.out.println("Could not be found on page: '" + pagina + "'");
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

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
