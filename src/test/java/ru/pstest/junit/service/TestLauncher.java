package ru.pstest.junit.service;

import org.junit.platform.commons.PreconditionViolationException;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.PrintWriter;

public class TestLauncher {
    public static void main(String[] args) throws PreconditionViolationException {
        // Создаем лончер
        Launcher launcher = LauncherFactory.create();
        //launcher.registerLauncherDiscoveryListeners();
        //launcher.registerTestExecutionListeners();

        // Создаем генератор тестов.
        // Листнер используется для сбора информации о тестировании.
        var summeryGeneratingListener = new SummaryGeneratingListener();
        //launcher.registerTestExecutionListeners(summeryGeneratingListener);


        //Используем лончер для запуска тестов
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                .request()
                // Добавляем селекторы для запуска тестов по классам, по пакетам
                .selectors(DiscoverySelectors.selectClass(UserServiceTest.class))
                .selectors(DiscoverySelectors.selectPackage("ru.pastest.junit.service"))
//                .filters(
//                        //Включаем или исключаем теги из тестирования:
//                        // .\gradlew clean test -DincludeTags='login'
////                        TagFilter.excludeTags("login")
//                        TagFilter.includeTags("login")
//                )
                .build();

        // Запускаем тесты
        launcher.execute(request, summeryGeneratingListener);

        // Выводим отчет на экран
        try (var writer = new PrintWriter(System.out)) {
            summeryGeneratingListener.getSummary().printTo(writer);
        }
    }
}
