package ru.pstest.junit.extension;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ConditionalExtension implements ExecutionCondition {
    @Override
    public org.junit.jupiter.api.extension.ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        System.out.println("conditional extension!");
        return System.getProperty("skip") != null
                ? ConditionEvaluationResult.disabled("is disabled")
                : ConditionEvaluationResult.enabled("is enabled");
    }
}
