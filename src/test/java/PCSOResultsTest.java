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
    private Properties properties = new Properties();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
    private LocalDate from;
    private LocalDate to;

    @BeforeClass
    public void setup() throws IOException {
        FileReader reader = new FileReader("pcso.properties");
        properties.load(reader);
        from = LocalDate.parse(properties.getProperty("DRAW_DATE_FROM"), formatter);
        to = LocalDate.parse(properties.getProperty("DRAW_DATE_TO"),formatter);
    }

    @Test
    public void openPCSOWebsite() throws ParseException {
        driver = new ChromeDriver();
        driver.get(properties.getProperty("PCSO_RESULTS_URL"));
        driver.manage().window().maximize();
        SearchPage searchPage = new SearchPage(driver);
        searchPage.search(from, to);
        Assert.assertTrue(searchPage.pageHasLoaded(), "Search results is displayed");
    }



    @AfterClass
    public void cleanUp() {
        if(driver!=null)
            driver.quit();
    }

}
