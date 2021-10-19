import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Very ugly code. Will be refactored.
 */
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
    @FindBy(id="cphContainer_cpContent_btnSearch")
    private WebElement searchButton;
    private WebElement htmlTable;

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

        searchButton.click();
        return this;
    }

    public boolean isDataAvailable() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        htmlTable = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cphContainer_cpContent_GridView1")));
        System.out.println(htmlTable!=null? "FOUND":"NOT FOUND");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", htmlTable);
        return htmlTable!=null;
    }

    private static List<HashMap<String,String>> table;
    public void extract() {
        int totalColumns = 5;
        int totalRows = driver.findElements(By.tagName("tr")).size();
        System.out.println("Elements found: " + totalRows);
        table = new ArrayList<HashMap<String,String>>();
        for(int i=2; i<=totalRows; i++){
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//table/tbody/tr["+i+"]")));

            HashMap row = new HashMap();
            row.put("game", driver.findElement(By.xpath("//table/tbody/tr["+i+"]/td[1]")).getText());
            row.put("result", driver.findElement(By.xpath("//table/tbody/tr["+i+"]/td[2]")).getText());
            row.put("datetime", driver.findElement(By.xpath("//table/tbody/tr["+i+"]/td[3]")).getText());
            row.put("jackpot", driver.findElement(By.xpath("//table/tbody/tr["+i+"]/td[4]")).getText());
            row.put("winners", driver.findElement(By.xpath("//table/tbody/tr["+i+"]/td[5]")).getText());
            table.add(row);
        }
        for(Map<String,String> entry: table)
            System.out.println(entry.get("game") + "," + entry.get("result") + "," + entry.get("datetime") + "," + entry.get("jackpot") + "," + entry.get("winners"));
        System.out.println("Total added: " + table.size());
    }


    // TODO: Use java 8 streams!
    private static Set<String> latestGameId = new HashSet<>();
    public void transform() throws IOException {

        // GAME NAME TO DB ID MAPPING
        HashMap<String, String> gameNameMap = new HashMap<String, String>();
        gameNameMap.put("Ultra Lotto 6/58","5");
        gameNameMap.put("Superlotto 6/49","7");
        gameNameMap.put("Megalotto 6/45","6");
        gameNameMap.put("Lotto 6/42","1");
        gameNameMap.put("Grand Lotto 6/55","8");
        gameNameMap.put("6Digit","9");
        gameNameMap.put("4Digit","10");
        gameNameMap.put("Suertres Lotto 11AM","11");
        gameNameMap.put("Suertres Lotto 4PM","11");
        gameNameMap.put("Suertres Lotto 9PM","11");
        gameNameMap.put("EZ2 Lotto 11AM","12");
        gameNameMap.put("EZ2 Lotto 4PM","12");
        gameNameMap.put("EZ2 Lotto 9PM","12");

        // Must come first before transforming the game name because relies on raw value of game name containing "11AM", "4PM"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        for(Map<String,String> result: table) {
            LocalDate date = LocalDate.parse(result.get("datetime"), formatter);
            // Add 'featured' boolean value
            if(PCSOProperties.getInstance().getPropertiesFile().getProperty("LATEST").equalsIgnoreCase(result.get("datetime"))) {
                result.put("latest", "true");
                latestGameId.add(gameNameMap.get(result.get("game")));
            } else {
                result.put("latest", "false");
            }
            LocalDateTime dateTime;
            if(result.get("game").contains("11AM"))
                dateTime = LocalDateTime.of(date, LocalTime.of(14,0));
            else if (result.get("game").contains("4PM"))
                dateTime = LocalDateTime.of(date, LocalTime.of(17,0));
            else
                dateTime = LocalDateTime.of(date, LocalTime.of(21,0));
            result.put("datetime", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").format(dateTime));
        }

        // Transform game name to STRAPI ID
        for(Map<String,String> result: table) {
            result.put("game", gameNameMap.get(result.get("game")));
        }

        // Remove commas from currency
        // Transform game name to STRAPI ID
        for(Map<String,String> result: table) {
            result.put("jackpot", result.get("jackpot").replace(",",""));
        }


        System.out.println("==============================");
        for(Map<String,String> entry: table)
            System.out.println(entry.get("datetime") + "," +entry.get("game") + "," + entry.get("result") + "," +  entry.get("jackpot") + "," + entry.get("winners") + "," + entry.get("latest"));


        System.out.println("Total added: " + table.size());
        System.out.println("Games na ereset ang 'latest' flag: " + latestGameId);

    }

    public void saveToCSV() {
        // Save the data
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./data.csv"));
            writer.write("game,result,datetime,jackpot,winners,latest\n");
            for(Map<String,String> entry: table)
                writer.write(entry.get("datetime") + "," + entry.get("game") + "," + entry.get("result") + "," +  entry.get("jackpot") + "," + entry.get("winners")+","+ entry.get("latest")+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Save the list of game ids that have been updated para gamiton ni sa pagreset sa 'latest' flag sa db
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./latest_game_ids.csv"));
            writer.write("latest\n");
            for(String gameId: latestGameId)
                writer.write(gameId + "\n");
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
