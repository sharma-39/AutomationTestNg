package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

@Listeners(SupplierTestListener.class)
public class CreateSuppilerTestScanerio extends LoginAndLocationTest {

    private final static HashMap<String, String> treeMap = new HashMap<>();

    @DataProvider(name = "supplierDataProvider")
    public Object[][] getSupplierDataProvider() {
        return new Object[][]{
                // Scenario 1: fill all value
                {createSupplierData(
                        generateRandomNumber("Supplier"), "example.com",
                        generateRandomNumber("SUP"), generateRondamNumber("DL"),
                        "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), true},
                // Scenario 2: Non-Mandatory Fields
                {createSupplierData(
                        null, "example.com", null, null, null, null, "1234567890", "GST123456",
                        "123 Main St", "77 west street", null, null, "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), false},

                // Scenario 3: Supplier Code null
                {createSupplierData(
                        generateRandomNumber("Medicine"),
                        "example.com", null, generateRandomNumber("DL")
                        , "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), true}
                // Scenario 4: Supplier Name Null
                ,
                {createSupplierData(
                        null, "example.com", generateRandomNumber("SUP"), generateRandomNumber("DL"), "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), true}

                // Scenario 5: drug licence  Null
                ,
                {createSupplierData(
                        generateRandomNumber("Medicine Supplier"), "example.com", generateRandomNumber("CD"), null, "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), true}
                // Scenario 6: phonenumber  Null
                ,
                {createSupplierData(
                        generateRandomNumber("Supplier"), "example.com",
                        generateRandomNumber("SUP"), generateRondamNumber("DL"), null, null, "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), true}
                // Scenario 7: State null
                ,
                {createSupplierData(
                        generateRandomNumber("Supplier"), "example.com",
                        generateRandomNumber("SUP"), generateRondamNumber("DL"), "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", null, "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), true}

                // Scenario 8: City is null
                ,
                {createSupplierData(
                        generateRandomNumber("Supplier"), "example.com",
                        generateRandomNumber("SUP"), generateRondamNumber("DL"),
                        "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", null, "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), true},

                //Scenario 9 : already exit suplier code
                {createSupplierData(
                        generateRandomNumber("SUP"), "example.com",
                        "SUP-20250314-188", generateRondamNumber("DL"),
                        "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "kumbaknam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), true}
        };
    }

    // Helper method to create supplier data
    private JsonNode createSupplierData(
            String supplierNamePrefix, String emailDomain, String supplierCodePrefix, String drugLicensePrefix,
            String phoneCountryCode, String phoneNumber, String fax, String gstNumber, String street, String location, String state,
            String city, String zipCode, String website, String pointOfContactName, String pointOfContactCountryCode,
            String pointOfContactNumber
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode supplierData = objectMapper.createObjectNode();

        supplierData.put("supplierNamePrefix", supplierNamePrefix);
        supplierData.put("emailDomain", emailDomain);
        supplierData.put("supplierCodePrefix", supplierCodePrefix);
        supplierData.put("drugLicensePrefix", drugLicensePrefix);

        ObjectNode phone = objectMapper.createObjectNode();
        phone.put("countryCode", phoneCountryCode);
        phone.put("phoneNumber", phoneNumber);
        supplierData.set("phone", phone);

        supplierData.put("fax", fax);
        supplierData.put("gstNumber", gstNumber);

        ObjectNode address = objectMapper.createObjectNode();
        address.put("street", street);
        address.put("location", location);
        address.put("state", state);
        address.put("city", city);
        address.put("zipCode", zipCode);
        supplierData.set("address", address);

        supplierData.put("website", website);

        ObjectNode pointOfContact = objectMapper.createObjectNode();
        pointOfContact.put("name", pointOfContactName);

        ObjectNode pointOfContactPhone = objectMapper.createObjectNode();
        pointOfContactPhone.put("countryCode", pointOfContactCountryCode);
        pointOfContactPhone.put("pointPhNumber", pointOfContactNumber);
        pointOfContact.set("phone", pointOfContactPhone);
        supplierData.set("pointOfContact", pointOfContact);


        System.out.println("Supplier Data" + supplierData);
        return supplierData;
    }

    @Test(priority = 3)
    public void testMenuOpen() {
        if (isLoginSuccessful) {
            menuPanelClick("Master", true, "Pharmacy");
        }
    }

    @BeforeClass
    public void printTestScenarios() {
        Object[][] testData = getSupplierDataProvider();
        System.out.println("=== Supplier Creation Test Scenarios ===");
        for (Object[] data : testData) {
            System.out.println("Data: " + data[0] + " | Expected Result: " + data[1]);
        }
    }

    @Test(priority = 3, dataProvider = "supplierDataProvider")
    public void testSupplierCreation(JsonNode supplierData, boolean expectedResult) {
        // Attempt to create the supplier
        String supplierCode = addSupplier(supplierData);

        // If the supplier creation is successful, log the result
        System.out.println("Supplier created with data: " + supplierData + ". Supplier Code: " + supplierCode);

        // Assert the result based on the expected outcome
        if (supplierCode != null && !supplierCode.equals("error")) {
            // If the test is expected to pass, ensure the supplier code is not null
            System.out.println(supplierData + "\nSuccessfull created: " + supplierCode);
        } else {
            getErrorMessage();
            String printErrorMessage = "";
            if (getErrorMessage().size() != 0) {
                printErrorMessage = printErrorMessage(getErrorMessage());
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            try {
                // Wait for the "Close" button to be clickable
                By closeButtonLocator = By.cssSelector("button[aria-label='Close']");
                WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(closeButtonLocator));

                // Scroll the button into view (if needed)
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", closeButton);

                // Click the "Close" button
                closeButton.click();
                System.out.println("Close button clicked successfully.");
            } catch (Exception e) {
                System.out.println("Failed to close the panel: " + e.getMessage());
                e.printStackTrace();
            }

            if (supplierCode != null && supplierCode.equals("error")) {
                printErrorMessage = "Supplier code already exits";
            }
            // If the test is expected to fail, ensure the supplier code is null
              Assert.fail("Supplier creation passed when it was expected to fail." + printErrorMessage.trim());

        }

    }

    private String printErrorMessage(List<WebElement> errorMessages) {
        StringBuilder allErrors = new StringBuilder();

        for (WebElement error : errorMessages) {
            if (!error.getText().trim().isEmpty()) {  // Avoid empty messages
                allErrors.append(error.getText()).append(" | ");  // Append error with a separator
            }
        }

        // Convert StringBuilder to String
        String errorString = allErrors.toString();

        // Remove the last separator (optional)
        if (errorString.endsWith(" | ")) {
            errorString = errorString.substring(0, errorString.length() - 3);
        }

        // Print or return the error message string
        System.out.println("All Error Messages: " + errorString);
        return errorString;
    }

    public String addSupplier(JsonNode supplierData) {

        verifyPanelName("Pharmacy Master");
        System.out.println("Successfully loaded Pharmacy Master");

        clickElement(By.xpath("//a[@id='Supplier' and contains(@class, 'nav-link')]"));

        clickElement(By.xpath("//button[contains(text(),'Add New')]"));

        // Print the supplier name prefix for debugging purposes
        System.out.println("data:- supplier name prefix:--" + supplierData.get("supplierNamePrefix").asText());

// Check if the supplier name prefix is not null and not empty
        String supplierNamePrefix = supplierData.get("supplierNamePrefix").asText();

        if (nullValidation(supplierNamePrefix)) {   // Generate a supplier name by combining the prefix and a random number
            enterText(By.cssSelector("input[title='Supplier Name']"), supplierNamePrefix, true);
            // Generate an email using the supplier name and email domain
            String email = generateEmail(supplierNamePrefix, supplierData.get("emailDomain").asText());
            enterText(By.cssSelector("input[formcontrolname='email']"), email, true);
        } else {
            // Handle the case where supplierNamePrefix is null or empty
            System.out.println("Supplier Name Prefix is null or empty. Skipping supplier name and email generation.");
        }

// Initialize a variable to store the supplier code
        String supplierCode = null;
        supplierCode = "";
        String supplierCodePrefix = supplierData.get("supplierCodePrefix").asText();
// Check if the supplier code prefix is not null and not empty
        if (nullValidation(supplierCodePrefix)) {
            // Generate a supplier code using the prefix
            supplierCode = supplierData.get("supplierCodePrefix").asText();
            // Enter the supplier code into the corresponding input field
            enterText(By.cssSelector("input[formcontrolname='code']"), supplierCode, true);
        }

// Extract the drug license prefix
        String drugPref = supplierData.get("drugLicensePrefix").asText();

// Check if the drug license prefix is not null
        if (nullValidation(drugPref)) {
            // Generate a drug license number using the prefix and enter it into the corresponding input field
            enterText(By.cssSelector("input[formcontrolname='drugLiscense']"),
                    supplierData.get("drugLicensePrefix").asText(), true);
        }

// Enter the phone country code into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='phoneCode']"),
                supplierData.get("phone").get("countryCode").asText(), true);

// Enter the phone number into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='phoneNumber']"), supplierData.get("phone").get("phoneNumber").asText(), true);

// Enter the fax number into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='fax']"),
                supplierData.get("fax").asText(), true);

// Enter the GST number into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='gst']"),
                supplierData.get("gstNumber").asText(), true);

// Enter the street address into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='address']"),
                supplierData.get("address").get("street").asText(), true);

// Enter the location into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='location']"),
                supplierData.get("address").get("location").asText(), true);

// Select the state from a dropdown or similar field
        selectField("state", supplierData.get("address").get("state").asText());

// Enter the city into the corresponding input field
        if (nullValidation(supplierData.get("address").get("city").asText())) {
            enterText(By.cssSelector("input[formcontrolname='city']"),
                    supplierData.get("address").get("city").asText(), true);
        }

// Enter the zip code into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='zipCode']"),
                supplierData.get("address").get("zipCode").asText(), true);

// Enter the website URL into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='website']"),
                supplierData.get("website").asText(), true);

// Enter the point of contact name into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='pointOfContactName']"),
                supplierData.get("pointOfContact").get("name").asText(), true);

// Enter the point of contact phone country code into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='pointOfContactPhoneCode']"),
                supplierData.get("pointOfContact").get("phone").get("countryCode").asText(), true);

// Enter the point of contact phone number into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='pointOfContactNumber']"), supplierData.get("pointOfContact").get("phone").get("pointPhNumber").asText(), true);

// Click the "Save & Close" button to submit the form
        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));
        threadTimer(500);
        if (getErrorMessage().size() == 0) {
            if (successMessage() != null && successMessage().getText().contains("Successfully")) {
                return supplierCode;
            } else if (successMessage() != null && !successMessage().getText().contains("Successfully")) {
                return "error";
            } else {
                return null;
            }
        } else {

            return null;
        }

    }

    private boolean nullValidation(String value) {
        return value != null &&
                !value.isEmpty() && !value.equals("null") && !value.equals("Null");
    }

    private void enterText(By locator, String text, boolean editable) {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        inputField.clear();
        inputField.sendKeys(text);
        threadTimer(500);
    }

    public String generateRondamNumber(String prefix) {
        String datePart = Instant.now()
                .atZone(ZoneId.systemDefault()) // Convert to system time zone
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")); // Format date

        int randomPart = 100 + new Random().nextInt(900); // Generate a 3-digit random number

        String generateNumber = prefix + "-" + datePart + "-" + randomPart; // Create invoice number

        System.out.println("Generated Number: " + generateNumber);
        return generateNumber;
    }

    private void selectField(String title, String value) {
        if (nullValidation(value)) {
            WebElement titleDropdown = driver.findElement(By.cssSelector("select[formcontrolname='" + title + "']"));
            Select select = new Select(titleDropdown);
            select.selectByVisibleText(value);
            threadTimer(500);
        }
    }

    public String generateRandomNumber(String prefix) {
        String datePart = Instant.now()
                .atZone(ZoneId.systemDefault()) // Convert to system time zone
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")); // Format date

        int randomPart = 100 + new Random().nextInt(900); // Generate a 3-digit random number

        String generateNumber = prefix + "-" + datePart + "-" + randomPart; // Create invoice number

        System.out.println("Generated Number: " + generateNumber);
        return generateNumber;
    }

    public String generatePhoneNumber() {
        Random random = new Random();
        int firstDigit = 7 + random.nextInt(3); // Ensures first digit is 7, 8, or 9
        StringBuilder phoneNumber = new StringBuilder();
        phoneNumber.append(firstDigit);
        for (int i = 0; i < 9; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        return phoneNumber.toString();
    }

    public String generateEmail(String supplierName, String emailDomain) {
        Random random = new Random();
        String formattedName = supplierName.toLowerCase().replace(" ", "").replaceAll("[^a-zA-Z0-9]", "");
        String[] domains = {"supplier.com", "traders.net", "business.org"};
        return formattedName + random.nextInt(1000) + "@" + domains[random.nextInt(domains.length)];
    }

    private void clickButtonInRow(String searchText, String buttonTitle) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

        // Wait for the table to be visible
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table")));


        // Get all rows in the table
        List<WebElement> rows = driver.findElements(By.xpath("//table//tr"));

        System.out.println("Total rows found: " + rows.size());

        for (WebElement row : rows) {
            if (row.getText().contains(searchText)) {
                System.out.println("Row Found containing: " + searchText);

                // Highlight the row
                highlightElement(row);

                // Scroll into view
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", row);
                System.out.println("Row Highlighted!");

                // Find the button in the row and click it
                WebElement button = row.findElement(By.xpath(".//button[@title='" + buttonTitle + "']"));
                clickElement(button);
                break;
            }
        }
    }

    private void highlightElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.backgroundColor = 'yellow'", element);
    }

    /**
     * Clicks an element with proper handling.
     */
    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            System.out.println("Normal click failed, using JavaScript click...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void selectRadioButton(String formControlName, String value) {
        try {
            WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@formcontrolname='" + formControlName + "'][@value='" + value + "'] | //label[span[contains(text(), '" + value + "')]]/input[@formcontrolname='" + formControlName + "'] ")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radioButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
            System.out.println("Selected radio button: " + value);
            threadTimer(500);
        } catch (TimeoutException e) {
            System.out.println("Radio button with value '" + value + "' not found!");
        }
    }

    private void clickElement(By locator) {
        threadTimer(500);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
            System.out.println("Normal click failed, using JavaScript click...");
        }
    }

    private List<WebElement> getErrorMessage() {
        List<WebElement> errorMessages = driver.findElements(By.xpath("//div[contains(@class, 'error-msg')]"));


        return errorMessages;

    }

    public void clearAllFields() {
        List<By> fields = List.of(
                By.cssSelector("input[formcontrolname='email']"),
                By.cssSelector("input[formcontrolname='code']"),
                By.cssSelector("input[formcontrolname='drugLiscense']"),
                By.cssSelector("input[formcontrolname='phoneCode']"),
                By.cssSelector("input[formcontrolname='phoneNumber']"),
                By.cssSelector("input[formcontrolname='fax']"),
                By.cssSelector("input[formcontrolname='gst']"),
                By.cssSelector("input[formcontrolname='address']"),
                By.cssSelector("input[formcontrolname='location']"),
                By.cssSelector("input[formcontrolname='city']"),
                By.cssSelector("input[formcontrolname='zipCode']"),
                By.cssSelector("input[formcontrolname='website']"),
                By.cssSelector("input[formcontrolname='pointOfContactName']"),
                By.cssSelector("input[formcontrolname='pointOfContactPhoneCode']"),
                By.cssSelector("input[formcontrolname='pointOfContactNumber']"),
                By.cssSelector("input[title='Supplier Name']")
        );

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        for (By field : fields) {
            try {
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(field));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                if (element.isEnabled()) {
                    element.clear();
                } else {
                    System.out.println("Element is disabled: " + field);
                }
            } catch (Exception e) {
                System.out.println("Failed to interact with element: " + field);
                e.printStackTrace();
            }
        }

        try {
            WebElement stateDropdown = driver.findElement(By.cssSelector("select[formcontrolname='state']"));
            Select select = new Select(stateDropdown);
            select.selectByValue("null");
        } catch (Exception e) {
            System.out.println("Failed to interact with state dropdown");
            e.printStackTrace();
        }
    }

    private WebElement successMessage() {

        try {

            WebElement resultElement = wait.until(driver -> {
                List<By> locators = List.of(
                        By.xpath("//div[contains(@class, 'container-2')]/p[contains(text(),'New Supplier Added Successfully')]"),
                        By.xpath("//div[contains(@class, 'container-2')]/p[contains(text(),'Supplier Code Already Exists')]")
                );

                for (By locator : locators) {
                    List<WebElement> elements = driver.findElements(locator);
                    if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                        //System.out.println("Elemenets"+elements.toString());
                        return elements.get(0);
                    }
                }
                return null;
            });
            return resultElement;
        } catch (Exception e) {
            return null;
        }
    }

}