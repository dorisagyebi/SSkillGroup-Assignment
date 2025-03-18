package demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class SauceDemo {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @BeforeMethod
    public void login() {
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
        wait.until(ExpectedConditions.urlContains("inventory"));
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Login failed!");
    }

    @Test
    public void productPage() {
        Select filterDropdown = new Select(driver.findElement(By.className("product_sort_container")));
        filterDropdown.selectByVisibleText("Price (low to high)");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-to-cart-sauce-labs-onesie")));
        driver.findElement(By.id("add-to-cart-sauce-labs-onesie")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-to-cart-sauce-labs-bike-light")));
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();

        WebElement cartCounter = driver.findElement(By.xpath("//span[@class='shopping_cart_badge']"));
        Assert.assertEquals(cartCounter.getText(), "2", "Cart count not valid");
    }

    @Test
    public void checkoutForm() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='shopping_cart_link']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checkout"))).click();

        driver.findElement(By.id("first-name")).sendKeys("Nana");
        driver.findElement(By.id("last-name")).sendKeys("Afrah");
        driver.findElement(By.id("postal-code")).sendKeys("4455");
        driver.findElement(By.id("continue")).click();
    }

    @Test
    public void checkout() {

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='finish']"))).click();
        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("complete-header")));
        Assert.assertEquals(confirmationMessage.getText(), "Thank you for your order!", "Order confirmation failed!");
        driver.findElement(By.id("back-to-products")).click();
    }

    @Test
    public void logout() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-burger-menu-btn"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout_sidebar_link"))).click();
        wait.until(ExpectedConditions.urlContains("saucedemo"));

        String expectedUrl = "https://www.saucedemo.com/";
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, expectedUrl, "Current URL does not match expected URL");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
