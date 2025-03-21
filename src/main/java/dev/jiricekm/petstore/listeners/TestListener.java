package dev.jiricekm.petstore.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onTestStart(ITestResult result) {
        log("[STARTED] " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log("[PASSED] " + result.getMethod().getMethodName() + " - Duration: " +
                getDuration(result) + " ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log("[FAILED] " + result.getMethod().getMethodName());
        log("Error: " + result.getThrowable().getMessage());

        // Detailed stack trace for better debugging
        for (StackTraceElement element : result.getThrowable().getStackTrace()) {
            log("  at " + element.toString());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log("[SKIPPED] " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log("[COMPLETED] Test Suite Execution");
        log("Total Tests Passed: " + context.getPassedTests().size());
        log("Total Tests Failed: " + context.getFailedTests().size());
        log("Total Tests Skipped: " + context.getSkippedTests().size());
    }

    private void log(String message) {
        String timestamp = formatter.format(new Date());
        System.out.println(timestamp + " " + message);
        Reporter.log(timestamp + " " + message); // TestNG Report Integration
    }

    private long getDuration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}