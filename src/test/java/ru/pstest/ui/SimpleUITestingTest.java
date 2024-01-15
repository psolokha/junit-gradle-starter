package ru.pstest.ui;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class SimpleUITestingTest {

    @Test
    public void testSimpleUIText() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        open("https://www.google.ru/");

        $(By.xpath("//textarea")).sendKeys("tratata");
        $(By.xpath("//textarea")).submit();

    }

}
