package base;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTest {
    protected WebDriver driver;
    protected static ExtentReports extent;
    protected static ExtentTest test;

    @BeforeSuite
    public void setupReport() {
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter("target/test-output/extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("Tester", "Automation Bot");
    }

    @AfterSuite
    public void tearDownReport() {

        extent.flush();
    }

    @BeforeMethod
    public void setUp(Method method) {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        test = extent.createTest(method.getName());
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.fail("Test Failed: " + result.getThrowable());
            String screenshotPath = takeScreenshot(result.getName());
            String relativePath = "../../test-output/screenshots/" + new File(screenshotPath).getName();
            test.addScreenCaptureFromPath(relativePath);
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test Passed");
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.skip("Test Skipped: " + result.getThrowable());
        }

        if (driver != null) {
            driver.quit();
        }
    }

    public String takeScreenshot(String testName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File src = ts.getScreenshotAs(OutputType.FILE);
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String destPath = "test-output/screenshots/" + testName + "_" + date + ".png";
        File dest = new File(destPath);
        try {
            Files.createDirectories(dest.getParentFile().toPath());
            Files.copy(src.toPath(), dest.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destPath;
    }
}
