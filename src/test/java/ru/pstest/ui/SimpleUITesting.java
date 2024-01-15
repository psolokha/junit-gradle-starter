package ru.pstest.ui;

import com.codeborne.selenide.selector.ByText;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.*;
public class SimpleUITesting {

    @Test
    public void testSimpleUIText() {
        open("https://www.google.ru/");

        $(By.xpath("//textarea")).sendKeys("tratata");
        $(By.xpath("//textarea")).submit();

    }

}
