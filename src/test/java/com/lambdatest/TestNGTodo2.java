package com.lambdatest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestNGTodo2 {

    private RemoteWebDriver driver;
    private String Status = "failed";
    String sessionId = "";
    String username = "";
    String authkey = "";
    @BeforeMethod
    public void setup(Method m, ITestContext ctx) throws MalformedURLException {
        username = System.getenv("LT_USERNAME") == null ? "Your LT Username" : System.getenv("LT_USERNAME");
        authkey = System.getenv("LT_ACCESS_KEY") == null ? "Your LT AccessKey" : System.getenv("LT_ACCESS_KEY");
        ;
        

        String hub = "@hub.lambdatest.com/wd/hub";

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platform", "Windows 10");
        caps.setCapability("browserName", "chrome");
        caps.setCapability("version", "latest");
        caps.setCapability("build", "TestNG With Java");
        caps.setCapability("name", m.getName() + this.getClass().getName());
        caps.setCapability("plugin", "git-testng");
        caps.setCapability("performance", "true"); // For Light house report
        caps.setCapability("network", true);
        caps.setCapability("selenium_version", "latest");
        caps.setCapability("accessibility", true); // Enable accessibility testing
        caps.setCapability("accessibility.wcagVersion", "wcag21a"); // Specify WCAG version (e.g., WCAG 2.1 Level A)
        caps.setCapability("accessibility.bestPractice", false); // Exclude best practice issues from results
       caps.setCapability("accessibility.needsReview", true); // Include issues that need review

        String[] Tags = new String[] { "Feature", "Magicleap", "Severe" };
        caps.setCapability("tags", Tags);

        driver = new RemoteWebDriver(new URL("https://" + username + ":" + authkey + hub), caps);
    }

    @Test
    public void basicTest() throws InterruptedException {
        String spanText;
        System.out.println("Loading Url");

        driver.get("https://lambdatest.github.io/sample-todo-app/");

        System.out.println("Checking Box");
        driver.findElement(By.name("li1")).click();

        System.out.println("Checking Another Box");
        driver.findElement(By.name("li2")).click();

        System.out.println("Checking Box");
        driver.findElement(By.name("li3")).click();

        System.out.println("Checking Another Box");
        driver.findElement(By.name("li4")).click();

        driver.findElement(By.id("sampletodotext")).sendKeys(" List Item 6");
        driver.findElement(By.id("addbutton")).click();

        driver.findElement(By.id("sampletodotext")).sendKeys(" List Item 7");
        driver.findElement(By.id("addbutton")).click();

        driver.findElement(By.id("sampletodotext")).sendKeys(" List Item 8");
        driver.findElement(By.id("addbutton")).click();

        System.out.println("Checking Another Box");
        driver.findElement(By.name("li1")).click();

        System.out.println("Checking Another Box");
        driver.findElement(By.name("li3")).click();

        System.out.println("Checking Another Box");
        driver.findElement(By.name("li7")).click();

        System.out.println("Checking Another Box");
        driver.findElement(By.name("li8")).click();

        System.out.println("Entering Text");
        driver.findElement(By.id("sampletodotext")).sendKeys("Get Taste of Lambda and Stick to It");

        driver.findElement(By.id("addbutton")).click();

        System.out.println("Checking Another Box");
        driver.findElement(By.name("li9")).click();

        // Let's also assert that the todo we added is present in the list.

        spanText = driver.findElementByXPath("/html/body/div/div/div/ul/li[9]/span").getText();
        Assert.assertEquals("Get Taste of Lambda and Stick to It", spanText);
        Status = "passed";
        Thread.sleep(15000);

        System.out.println("TestFinished");
        sessionId = driver.getSessionId().toString();
        System.out.println("Session Id is"+sessionId);
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        driver.executeScript("lambda-status=" + Status);
        driver.quit();
        
       //  String apiUrl = "https://api.lambdatest.com/automation/api/v1/sessions/"+sessionId;

        // Use your preferred method (e.g., HttpURLConnection, OkHttp, etc.) to make the API call
        // Here's an example using HttpURLConnection
      /* * try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("Authorization", "<Update your token . get it from API doc>");
            // Process the API response as needed
            InputStream inputStream = connection.getInputStream();
            String response = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            System.out.println("Response Message is"+response);
            // ...

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            String apiUrl = "https://api.lambdatest.com/automation/api/v1/sessions/"+sessionId;
            String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + authkey).getBytes());

            // Define Common Headers
            Header acceptHeader = new Header("accept", "application/json");
            Header authorizationHeader = new Header("Authorization", authHeader);
    
            // First API Call to fetch Test_id
            Response response = RestAssured
                    .given()
                    .header(acceptHeader)
                    .header(authorizationHeader)
                    .get(apiUrl);
            // First API Call
           // Response response = RestAssured.get(apiUrl);
            
            // Extract Test_id from response
            JsonPath jsonPath = response.jsonPath();
        String testId = jsonPath.getString("data.test_id");
            System.out.println("Original Test_id: " + testId);
            
            // Modify Test_id
            String modifiedTestId = "AUT_" + testId;
            System.out.println("Modified Test_id: " + modifiedTestId);
            
            // Second API Call with modified Test_id
            Thread.sleep(50000);
            Response secondResponse = RestAssured
                    .given()
                    .header(acceptHeader)
                .header(authorizationHeader)
                    .queryParam("bestPractice", "false")
                .queryParam("needsReview", "true")
                .get("https://api.lambdatest.com/accessibility/api/v1/test-issue/"+modifiedTestId);
            
            // Fetch and print the response from the second API
            String responseBody = secondResponse.getBody().asString();
            System.out.println("Second API Response: " + responseBody);
            
        } finally {
            // Close WebDriver
            driver.quit();
        }
    }

}