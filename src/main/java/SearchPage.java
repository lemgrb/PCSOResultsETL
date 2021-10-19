import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class SearchPage {

    private final WebDriver driver;

    @FindBy(id="cphContainer_cpContent_ddlStartMonth")
    private WebElement startMonth;
    @FindBy(id="cphContainer_cpContent_ddlStartDate")
    private WebElement startDate;
    @FindBy(id="cphContainer_cpContent_ddlStartYear")
    private WebElement startYear;
    @FindBy(id="cphContainer_cpContent_ddlEndMonth")
    private WebElement endMonth;
    @FindBy(id="cphContainer_cpContent_ddlEndDay")
    private WebElement endDay;
    @FindBy(id="cphContainer_cpContent_ddlEndYear")
    private WebElement endYear;

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public SearchPage search(LocalDate from, LocalDate to) {
        Actions actionProvider = new Actions(driver);
        actionProvider.moveToElement(driver.findElement(By.id("cphContainer_cpContent_ddlStartMonth"))).build().perform();

        new Select(startMonth).selectByValue(StringUtils.capitalize(String.valueOf(from.getMonth()).toLowerCase()));
        new Select(startDate).selectByValue(String.valueOf(from.getDayOfMonth()));
        new Select(startYear).selectByValue(String.valueOf(from.getYear()));

        new Select(endMonth).selectByValue(StringUtils.capitalize(String.valueOf(to.getMonth()).toLowerCase()));
        new Select(endDay).selectByValue(String.valueOf(to.getDayOfMonth()));
        new Select(endYear).selectByValue(String.valueOf(to.getYear()));

        driver.findElement(By.id("cphContainer_cpContent_btnSearch")).click();

        return this;
    }

    public boolean pageHasLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cphContainer_cpContent_lblError"))) !=null;
    }

}
