package com.sgrailways.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.MockitoAnnotations;

public class MockitoRule implements TestRule {
    private final Object testClass;

    public MockitoRule(Object testClass) {
        this.testClass = testClass;
    }

    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override public void evaluate() throws Throwable {
                MockitoAnnotations.initMocks(testClass);
                base.evaluate();
            }
        };
    }
}
