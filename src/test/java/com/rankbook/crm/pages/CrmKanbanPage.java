package com.rankbook.crm.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.rankbook.framework.config.ConfigManager;
import com.rankbook.framework.constants.FrameworkConstants;

public class CrmKanbanPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;
    private final Actions actions;

    // View Navigation & Modal
    private final By menuPipeline = By.xpath("//a[contains(@href, '/pipeline') or contains(., 'Pipeline')]");
    private final By btnNewLead = By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'new lead') or contains(., 'Lead') or contains(@data-testid, 'new-lead')]");
    private final By dialogModal = By.xpath("//div[@role='dialog']");

    // Contact Information
    private final By inputFullName = By.xpath("//div[@role='dialog']//input[@id='lf-name' or @name='name' or contains(@placeholder, 'name')]");
    private final By inputPhone = By.xpath("//div[@role='dialog']//input[@id='lf-phone' or @name='phone' or @type='tel']");
    private final By inputEmail = By.xpath("//div[@role='dialog']//input[@id='lf-email' or @name='email' or @type='email']");

    // Date of Birth
    private final By btnDobTrigger = By.xpath("//div[@role='dialog']//label[contains(., 'Date of Birth')]/..//button | //div[@role='dialog']//button[contains(., 'Select date of birth') or contains(@class, 'calendar')]");
    private final By dayInPicker = By.xpath("//div[contains(@class, 'popover') or @role='dialog']//button[not(@disabled)][contains(@class, 'day') or normalize-space()='15' or normalize-space()='1']");

    // Autocomplete Comboboxes (Input Tags)
    private final By inputAcademicLevel = By.xpath("//div[@role='dialog']//input[@id='lf-academicLevel' or contains(@placeholder, 'qualification')]");
    private final By inputSpecialization = By.xpath("//div[@role='dialog']//input[@id='lf-degree' or contains(@placeholder, 'specialization')]");

    // Academic Text Inputs
    private final By inputCollege = By.xpath("//div[@role='dialog']//input[@id='lf-college' or @name='college' or contains(@placeholder, 'college')]");
    private final By inputCity = By.xpath("//div[@role='dialog']//input[@id='lf-city' or @name='city' or contains(@placeholder, 'city')]");

    // Passing Out Year Trigger Button
    private final By btnGraduationYear = By.xpath("//div[@role='dialog']//*[@id='lf-graduationYear' or @role='combobox'][contains(., 'passing') or contains(., 'Passing')]");

    // Submit Button
    private final By btnSaveLead = By.xpath("//div[@role='dialog']//button[normalize-space()='Save Lead']");

    // Rejection & Feedback Locators
    private final By toastNotification = By.xpath("//div[contains(@class, 'toast') or contains(@role, 'status') or contains(@role, 'alert') or contains(@class, 'sonner')]");
    private final By btnCloseModal = By.xpath("//div[@role='dialog']//button[contains(@aria-label, 'Close') or contains(., 'Cancel') or contains(@class, 'close')]");

    public CrmKanbanPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, FrameworkConstants.EXPLICIT_WAIT);
        this.js = (JavascriptExecutor) driver;
        this.actions = new Actions(driver);
    }

    public void navigateToPipeline() {
        String baseUrl = ConfigManager.get("crm.web.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = FrameworkConstants.CRM_URL;
        }

        if (!driver.getCurrentUrl().contains("/pipeline")) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(menuPipeline)).click();
            } catch (Exception e) {
                driver.get(baseUrl + "/pipeline");
            }
        }

        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(btnNewLead),
            ExpectedConditions.urlContains("/pipeline")
        ));
        wait.until(ExpectedConditions.visibilityOfElementLocated(btnNewLead));
    }

    public boolean isLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(btnNewLead)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void createLead(String fullName, String phone, String email) {
        fillLeadForm(fullName, phone, email);

        WebElement saveBtn = wait.until(ExpectedConditions.presenceOfElementLocated(btnSaveLead));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", saveBtn);
        js.executeScript("arguments[0].click();", saveBtn);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(dialogModal));
    }

    public void submitLeadExpectingRejection(String fullName, String phone, String email) {
        fillLeadForm(fullName, phone, email);

        WebElement saveBtn = wait.until(ExpectedConditions.presenceOfElementLocated(btnSaveLead));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", saveBtn);
        js.executeScript("arguments[0].click();", saveBtn);
    }

    public boolean isLeadPresent(String leadName) {
        try {
            By leadCard = By.xpath("//*[contains(text(), '" + leadName + "')]");
            return wait.until(ExpectedConditions.presenceOfElementLocated(leadCard)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void moveLeadToColumn(String leadName, String targetColumnName) {
        // Broadened locators to support Tailwind / Next.js DOM structures
        By leadLocator = By.xpath("//*[normalize-space()='" + leadName + "' or contains(text(), '" + leadName + "')]");
        By columnLocator = By.xpath("//*[normalize-space()='" + targetColumnName + "' or contains(text(), '" + targetColumnName + "')]");

        WebElement sourceCard = wait.until(ExpectedConditions.presenceOfElementLocated(leadLocator));
        WebElement targetColumn = wait.until(ExpectedConditions.presenceOfElementLocated(columnLocator));

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", sourceCard);

        try {
            actions.moveToElement(sourceCard)
                   .pause(java.time.Duration.ofMillis(200))
                   .clickAndHold(sourceCard)
                   .pause(java.time.Duration.ofMillis(300))
                   .moveToElement(targetColumn, 0, 50)
                   .pause(java.time.Duration.ofMillis(300))
                   .release()
                   .build()
                   .perform();
        } catch (Exception e) {
            String dragDropScript = 
                "function createEvent(type) {" +
                "  var event = new CustomEvent('CustomEvent');" +
                "  event.initCustomEvent(type, true, true, null);" +
                "  event.dataTransfer = {" +
                "    data: {}," +
                "    setData: function(key, val) { this.data[key] = val; }," +
                "    getData: function(key) { return this.data[key]; }" +
                "  };" +
                "  return event;" +
                "}" +
                "var src = arguments[0], tgt = arguments[1];" +
                "var dragStart = createEvent('dragstart');" +
                "src.dispatchEvent(dragStart);" +
                "var dragOver = createEvent('dragover');" +
                "tgt.dispatchEvent(dragOver);" +
                "var drop = createEvent('drop');" +
                "drop.dataTransfer = dragStart.dataTransfer;" +
                "tgt.dispatchEvent(drop);" +
                "var dragEnd = createEvent('dragend');" +
                "src.dispatchEvent(dragEnd);";
            js.executeScript(dragDropScript, sourceCard, targetColumn);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
    }

    public boolean isModalStillOpen() {
        try {
            return driver.findElement(dialogModal).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getRejectionMessage() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(4));
            WebElement toast = shortWait.until(ExpectedConditions.visibilityOfElementLocated(toastNotification));
            return toast.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public void closeModalIfOpen() {
        try {
            List<WebElement> closeBtns = driver.findElements(btnCloseModal);
            if (!closeBtns.isEmpty()) {
                safeClick(closeBtns.get(0));
            } else {
                actions.sendKeys(Keys.ESCAPE).perform();
            }
            wait.until(ExpectedConditions.invisibilityOfElementLocated(dialogModal));
        } catch (Exception ignored) {}
    }

    private void fillLeadForm(String fullName, String phone, String email) {
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(btnNewLead));
        safeClick(addBtn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(dialogModal));

        typeText(inputFullName, fullName);
        typeText(inputPhone, phone);
        typeText(inputEmail, email);

        try {
            List<WebElement> dob = driver.findElements(btnDobTrigger);
            if (!dob.isEmpty()) {
                safeClick(dob.get(0));
                List<WebElement> days = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(dayInPicker));
                if (!days.isEmpty()) {
                    safeClick(days.get(0));
                }
                actions.sendKeys(Keys.ESCAPE).perform();
                Thread.sleep(300);
            }
        } catch (Exception ignored) {}

        selectCombobox(inputAcademicLevel, "Bachelor of Engineering");
        selectCombobox(inputSpecialization, "Computer Science");
        typeText(inputCollege, "Engineering College");
        typeText(inputCity, "Bengaluru");
        selectYear("2025");
    }

    private void selectCombobox(By locator, String searchText) {
        try {
            WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", input);
            safeClick(input);

            input.sendKeys(searchText);
            Thread.sleep(350);

            By opt = By.xpath("//*[contains(@id, 'base-ui') or @role='option'][contains(., '" + searchText + "')]");
            List<WebElement> matching = driver.findElements(opt);
            if (!matching.isEmpty()) {
                safeClick(matching.get(0));
            } else {
                input.sendKeys(Keys.ARROW_DOWN);
                input.sendKeys(Keys.ENTER);
            }
            Thread.sleep(250);
        } catch (Exception e) {
            try {
                WebElement input = driver.findElement(locator);
                input.sendKeys(Keys.ARROW_DOWN);
                input.sendKeys(Keys.ENTER);
            } catch (Exception ignored) {}
        }
    }

    private void selectYear(String year) {
        try {
            WebElement yearBtn = wait.until(ExpectedConditions.presenceOfElementLocated(btnGraduationYear));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", yearBtn);
            Thread.sleep(200);

            safeClick(yearBtn);
            Thread.sleep(300);

            actions.sendKeys(year).sendKeys(Keys.ENTER).perform();
            Thread.sleep(200);

            By opt = By.xpath("//*[@role='option'][normalize-space()='" + year + "'] | //div[contains(@id, '-list')]//*[normalize-space()='" + year + "']");
            List<WebElement> options = driver.findElements(opt);
            if (!options.isEmpty()) {
                safeClick(options.get(0));
            } else {
                js.executeScript(
                    "var btn = document.getElementById('lf-graduationYear') || document.querySelector('[aria-controls*=\"list\"]');" +
                    "if (btn) {" +
                    "    btn.removeAttribute('data-placeholder');" +
                    "    var span = btn.querySelector('[data-slot=\"select-value\"]');" +
                    "    if (span) { span.removeAttribute('data-placeholder'); span.textContent = arguments[0]; }" +
                    "    var hiddenInput = btn.parentElement.querySelector('input[type=\"hidden\"], input[tabindex=\"-1\"]');" +
                    "    if (hiddenInput) {" +
                    "        var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                    "        setter.call(hiddenInput, arguments[0]);" +
                    "        hiddenInput.dispatchEvent(new Event('input', { bubbles: true }));" +
                    "        hiddenInput.dispatchEvent(new Event('change', { bubbles: true }));" +
                    "    }" +
                    "}",
                    year
                );
            }
        } catch (Exception e) {
            System.err.println("[CrmKanbanPage] selectYear error: " + e.getMessage());
        }
    }

    private void typeText(By locator, String value) {
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", el);
        try {
            el.click();
            el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
            el.sendKeys(value);
        } catch (Exception e) {
            js.executeScript(
                "let setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                "setter.call(arguments[0], arguments[1]);" +
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                el, value
            );
        }
    }

    private void safeClick(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", element);
        }
    }
}