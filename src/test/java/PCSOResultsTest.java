import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

public class PCSOResultsTest {

    private WebDriver driver;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
    private LocalDate from;
    private LocalDate to;

    @BeforeClass
    public void setup() throws IOException {

        from = LocalDate.parse(PCSOProperties.getInstance().getPropertiesFile().getProperty("DRAW_DATE_FROM"), formatter);
        to = LocalDate.parse(PCSOProperties.getInstance().getPropertiesFile().getProperty("DRAW_DATE_TO"),formatter);
    }

    @Test
    public void searchByDate() throws ParseException, IOException {
        driver = new ChromeDriver();
        driver.get(PCSOProperties.getInstance().getPropertiesFile().getProperty("PCSO_RESULTS_URL"));
        driver.manage().window().maximize();
        SearchPage searchPage = new SearchPage(driver);
        searchPage.search(from, to);
        Assert.assertTrue(searchPage.isDataAvailable(), "Search results is displayed");
    }

    @Test(dependsOnMethods = {"searchByDate"})
    public void copyValuesFromtableToCSV() throws IOException {
        SearchPage searchPage = new SearchPage(driver);
        searchPage.extract();
        searchPage.transform();
    }


    @Test(dependsOnMethods = {"copyValuesFromtableToCSV"})
        public void saveToCSV() {
            SearchPage searchPage = new SearchPage(driver);
            searchPage.saveToCSV();
    }

    @AfterClass
    public void cleanUp() {
        if(driver!=null)
            driver.quit();
    }

}
