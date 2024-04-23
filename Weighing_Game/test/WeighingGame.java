import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class WeighingGame {


    private static WebDriver driver;
    private static List<Integer> goldCoins = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);
    private static List<String> weighings = new ArrayList<>();
    private static WebElement fakeCoin;
    private static WebElement realCoin;


    @Before
    public void setup() {
       // WebDriverManager.chromedriver().driverVersion("122.0.6261.94").setup();
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Sushmitha\\Downloads\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
    }


    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void functionalTest() throws InterruptedException {
        driver.get("http://sdetchallenge.fetch.com/");
        Assert.assertEquals("React App", driver.getTitle());

        List<WebElement> weighingPans = driver.findElements(By.className("game-board"));
        Assert.assertEquals(2, weighingPans.size());
        Assert.assertTrue(weighingPans.get(0).getText().contains("left bowl"));
        Assert.assertTrue(weighingPans.get(1).getText().contains("right bowl"));

        //filling 0-2 in left bowl and 3-5 bowl for minimal comparison steps
        fillWeighingPansThree(0);
        driver.findElement(By.id("weigh")).click();

        //Wait Time needed for the result to reflect
        WebElement result = driver.findElement(By.cssSelector(".result > button"));
        Thread.sleep(2000);
        System.out.println("result for first check :" + result.getText());
        Assert.assertNotEquals("?", result.getText());


        // Create weighings list
        String check1 = "[0,1,2] " + result.getText() + " [3,4,5]";
        weighings.add(check1);
        String resultOperator = result.getText();

        if (resultOperator.equals("=")) {
            goldCoins = goldCoins.subList(6, 9);
        } else if (resultOperator.equals("<")) {
            goldCoins = goldCoins.subList(0, 3);
        } else {
            goldCoins = goldCoins.subList(3, 6);
        }

        //Two buttons named reset present in the website hence using xpath
        driver.findElement(By.xpath("//button[contains(text(), 'Reset')]")).click();

        //goldcoins list is changed based on previous check
        fillWeighingPans(0);
        driver.findElement(By.id("weigh")).click();

        Thread.sleep(2000);
        Assert.assertNotEquals("?", result.getText());
        System.out.println("result for second check :" + result.getText());

        // Create weighings list
        String check2 = "[" + goldCoins.get(0) + "] " + result.getText() + " [" + goldCoins.get(1) + "]";
        weighings.add(check2);
        resultOperator = result.getText();

        int correctCoin;
        if (resultOperator.equals("=")) {
            fakeCoin = driver.findElement(By.id("coin_" + goldCoins.get(2)));
            correctCoin = goldCoins.get(2);
        } else if (resultOperator.equals("<")) {
            fakeCoin = driver.findElement(By.id("coin_" + goldCoins.get(0)));
            correctCoin = goldCoins.get(0);
        } else {
            fakeCoin = driver.findElement(By.id("coin_" + goldCoins.get(1)));
            correctCoin = goldCoins.get(1);
        }

        System.out.println("Fake Coin: " + fakeCoin.getAttribute("id"));
        fakeCoin.click();

        //Positive scenario check
        Alert alert = driver.switchTo().alert();
        Assert.assertEquals("Yay! You find it!", alert.getText());
        alert.accept();

        //Weighinglist check
        WebElement olElement = driver.findElement(By.cssSelector(".game-info ol"));
        java.util.List<WebElement> liElements = olElement.findElements(By.tagName("li"));
        int numberOfElements = liElements.size();

        System.out.println("Number of elements in the list: " + numberOfElements);
        Assert.assertEquals(weighings.size(),numberOfElements);
        Assert.assertEquals(weighings.get(0),liElements.get(0).getText());
        Assert.assertEquals(weighings.get(1),liElements.get(1).getText());

        // Select a random index other than correct index for Negative test case
        Random random = new Random();
        int[] validNumbers = IntStream.rangeClosed(0, 8).filter(n -> n != correctCoin).toArray();
        int randomIndex = random.nextInt(validNumbers.length);
        realCoin = driver.findElement(By.id("coin_" + validNumbers[randomIndex]));
        realCoin.click();

        //Negative scenario check
        alert = driver.switchTo().alert();
        Assert.assertEquals("Oops! Try Again!", alert.getText());
        alert.accept();
    }

    @Test
    public void testEmptyBowls() throws InterruptedException {
        driver.get("http://sdetchallenge.fetch.com/");
        Assert.assertEquals("React App", driver.getTitle());
        List<WebElement> weighingPans = driver.findElements(By.className("game-board"));
        Assert.assertEquals(2, weighingPans.size());
        Assert.assertTrue(weighingPans.get(0).getText().contains("left bowl"));
        Assert.assertTrue(weighingPans.get(1).getText().contains("right bowl"));

        driver.findElement(By.id("weigh")).click();
        Thread.sleep(2000);
        WebElement result = driver.findElement(By.cssSelector(".result > button"));

        System.out.println("result:" + result.getText());
        Assert.assertEquals("=", result.getText());
    }

    @Test
    public void testKeepSameCoinBothSides() throws InterruptedException {
        driver.get("http://sdetchallenge.fetch.com/");
        Assert.assertEquals("React App", driver.getTitle());
        List<WebElement> weighingPans = driver.findElements(By.className("game-board"));
        Assert.assertEquals(2, weighingPans.size());
        Assert.assertTrue(weighingPans.get(0).getText().contains("left bowl"));
        Assert.assertTrue(weighingPans.get(1).getText().contains("right bowl"));

        driver.findElement(By.id("left_0")).sendKeys("3");
        driver.findElement(By.id("right_0")).sendKeys("3");
        driver.findElement(By.id("weigh")).click();

        //Using Explicit wait until the alert appears - better than implicit sleep
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert();

        Assert.assertEquals("Inputs are invalid: Both sides have coin(s): 3", alert.getText());
        alert.accept();
    }

    @Test
    public void testKeepMultipleSameCoin() throws InterruptedException {
        driver.get("http://sdetchallenge.fetch.com/");
        Assert.assertEquals("React App", driver.getTitle());
        List<WebElement> weighingPans = driver.findElements(By.className("game-board"));
        Assert.assertEquals(2, weighingPans.size());
        Assert.assertTrue(weighingPans.get(0).getText().contains("left bowl"));
        Assert.assertTrue(weighingPans.get(1).getText().contains("right bowl"));

        //placing bars in random places in grid
        driver.findElement(By.id("left_1")).sendKeys("3");
        driver.findElement(By.id("left_6")).sendKeys("6");
        driver.findElement(By.id("right_2")).sendKeys("3");
        driver.findElement(By.id("right_8")).sendKeys("6");
        driver.findElement(By.id("weigh")).click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert();

        Assert.assertEquals("Inputs are invalid: Both sides have coin(s): 3,6", alert.getText());
        alert.accept();
    }

    @Test
    public void testKeepDuplicatesLeft() throws InterruptedException {
        driver.get("http://sdetchallenge.fetch.com/");
        Assert.assertEquals("React App", driver.getTitle());
        List<WebElement> weighingPans = driver.findElements(By.className("game-board"));
        Assert.assertEquals(2, weighingPans.size());
        Assert.assertTrue(weighingPans.get(0).getText().contains("left bowl"));
        Assert.assertTrue(weighingPans.get(1).getText().contains("right bowl"));

        driver.findElement(By.id("left_5")).sendKeys("3");
        driver.findElement(By.id("left_6")).sendKeys("3");
        driver.findElement(By.id("right_3")).sendKeys("6");
        driver.findElement(By.id("right_7")).sendKeys("2");
        driver.findElement(By.id("weigh")).click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert();

        Assert.assertEquals("Inputs are invalid: Left side has duplicates", alert.getText());
        alert.accept();

    }

    @Test
    public void testKeepDuplicatesRight() throws InterruptedException {
        driver.get("http://sdetchallenge.fetch.com/");
        Assert.assertEquals("React App", driver.getTitle());
        List<WebElement> weighingPans = driver.findElements(By.className("game-board"));
        Assert.assertEquals(2, weighingPans.size());
        Assert.assertTrue(weighingPans.get(0).getText().contains("left bowl"));
        Assert.assertTrue(weighingPans.get(1).getText().contains("right bowl"));

        driver.findElement(By.id("left_1")).sendKeys("5");
        driver.findElement(By.id("left_6")).sendKeys("8");
        driver.findElement(By.id("right_2")).sendKeys("3");
        driver.findElement(By.id("right_8")).sendKeys("3");
        driver.findElement(By.id("weigh")).click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert();

        Assert.assertEquals("Inputs are invalid: Right side has duplicates", alert.getText());
        alert.accept();

    }

    @Test
    public void testKeepEmptyOneSide() throws InterruptedException {
        driver.get("http://sdetchallenge.fetch.com/");
        Assert.assertEquals("React App", driver.getTitle());
        List<WebElement> weighingPans = driver.findElements(By.className("game-board"));
        Assert.assertEquals(2, weighingPans.size());
        Assert.assertTrue(weighingPans.get(0).getText().contains("left bowl"));
        Assert.assertTrue(weighingPans.get(1).getText().contains("right bowl"));

        driver.findElement(By.id("left_1")).sendKeys("3");
        driver.findElement(By.id("left_6")).sendKeys("4");
        driver.findElement(By.id("left_4")).sendKeys("8");
        driver.findElement(By.id("weigh")).click();

        Thread.sleep(2000);
        WebElement result = driver.findElement(By.cssSelector(".result > button"));

        System.out.println("result:" + result.getText());
        Assert.assertEquals(">", result.getText());

    }

    @Test
    public void testInvalidInput() throws InterruptedException {
        driver.get("http://sdetchallenge.fetch.com/");
        Assert.assertEquals("React App", driver.getTitle());
        List<WebElement> weighingPans = driver.findElements(By.className("game-board"));
        Assert.assertEquals(2, weighingPans.size());
        Assert.assertTrue(weighingPans.get(0).getText().contains("left bowl"));
        Assert.assertTrue(weighingPans.get(1).getText().contains("right bowl"));

        driver.findElement(By.id("left_1")).sendKeys("A");
        driver.findElement(By.id("right_6")).sendKeys("9");
        driver.findElement(By.id("left_5")).sendKeys("$");
        driver.findElement(By.id("right_3")).sendKeys("t");

        Assert.assertEquals("",  driver.findElement(By.id("left_1")).getText());
        Assert.assertEquals("",  driver.findElement(By.id("right_6")).getText());
        Assert.assertEquals("",  driver.findElement(By.id("left_5")).getText());
        Assert.assertEquals("",  driver.findElement(By.id("right_3")).getText());

    }

    @Test
    public void testResetButton() throws InterruptedException {
        driver.get("http://sdetchallenge.fetch.com/");
        Assert.assertEquals("React App", driver.getTitle());
        List<WebElement> weighingPans = driver.findElements(By.className("game-board"));
        Assert.assertEquals(2, weighingPans.size());
        Assert.assertTrue(weighingPans.get(0).getText().contains("left bowl"));
        Assert.assertTrue(weighingPans.get(1).getText().contains("right bowl"));

        driver.findElement(By.id("left_1")).sendKeys("3");
        driver.findElement(By.id("left_6")).sendKeys("4");
        driver.findElement(By.id("left_4")).sendKeys("8");
        driver.findElement(By.id("weigh")).click();

        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[contains(text(), 'Reset')]")).click();


        //Reset button clears the bowl
        Assert.assertEquals("",  driver.findElement(By.id("left_1")).getText());
        Assert.assertEquals("",  driver.findElement(By.id("right_6")).getText());
        Assert.assertEquals("",  driver.findElement(By.id("left_4")).getText());
    }

    private void fillWeighingPansThree(int start) throws InterruptedException {

        driver.findElement(By.id("left_0")).sendKeys(goldCoins.get(start).toString());
        driver.findElement(By.id("left_1")).sendKeys(goldCoins.get(start + 1).toString());
        driver.findElement(By.id("left_2")).sendKeys(goldCoins.get(start + 2).toString());

        driver.findElement(By.id("right_0")).sendKeys(goldCoins.get(start + 3).toString());
        driver.findElement(By.id("right_1")).sendKeys(goldCoins.get(start + 4).toString());
        driver.findElement(By.id("right_2")).sendKeys(goldCoins.get(start + 5).toString());
    }
    private void fillWeighingPans(int start) throws InterruptedException {

        driver.findElement(By.id("left_0")).sendKeys(goldCoins.get(start).toString());
        driver.findElement(By.id("right_0")).sendKeys(goldCoins.get(start + 1).toString());
    }
}
