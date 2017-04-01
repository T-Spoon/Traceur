package com.tspoon.traceur;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

public class TraceurTest {

    @After
    public void tearDown() {
        Traceur.disableLogging();
    }

    @Test
    public void callSiteIsShownInStackTrace() throws Exception {
        // Assumption - Call site not shown when Traceur not enabled
        try {
            StreamFactory.createErrorObservable().blockingFirst();
        } catch (Throwable t) {
            final String exceptionAsString = exceptionAsString(t);
            printStackTrace("Default Stacktrace", exceptionAsString);

            assertThat(exceptionAsString).doesNotContain("StreamFactory");
        }

        // Enable Traceur and ensure call site is shown
        Traceur.enableLogging();
        try {
            StreamFactory.createErrorObservable().blockingFirst();
            Assertions.failBecauseExceptionWasNotThrown(Throwable.class);
        } catch (Throwable t) {
            final String exceptionAsString = exceptionAsString(t);
            printStackTrace("Traceur Stacktrace", exceptionAsString);

            assertThat(exceptionAsString).contains("StreamFactory");
        }
    }

    @Test
    public void filtersStackTraces() throws Exception {
        // Assumption - Shims are not filtered when filtering is disabled
        Traceur.enableLogging(new TraceurConfig(false));
        try {
            StreamFactory.createErrorObservable().blockingFirst();
            Assertions.failBecauseExceptionWasNotThrown(Throwable.class);
        } catch (Throwable t) {
            final String exceptionAsString = exceptionAsString(t);
            printStackTrace("Exception without filtering", exceptionAsString);

            assertThat(exceptionAsString).contains("TraceurException.java");
            assertThat(exceptionAsString).contains("ObservableOnAssembly.<init>");
        }

        // Enable filtering and ensure shims are filtered
        Traceur.enableLogging(new TraceurConfig(true));
        try {
            StreamFactory.createErrorObservable().blockingFirst();
            Assertions.failBecauseExceptionWasNotThrown(Throwable.class);
        } catch (Throwable t) {
            final String exceptionAsString = exceptionAsString(t);
            printStackTrace("Exception with filtering", exceptionAsString);

            assertThat(exceptionAsString).doesNotContain("TraceurException.java");
            assertThat(exceptionAsString).doesNotContain("ObservableOnAssembly.<init>");
        }
    }

    private static void printStackTrace(String sectionName, String exceptionAsString) {
        printSectionSeparator(sectionName);
        System.out.println(exceptionAsString);
    }


    private static String exceptionAsString(Throwable t) {
        final StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private static void printSectionSeparator(String sectionName) {
        System.out.println("============== " + sectionName + " ==============");
    }
}
