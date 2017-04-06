package com.tspoon.traceur;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

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
            StreamFactory.createNullPointerExceptionObservable().blockingFirst();
        } catch (Throwable t) {
            final String exceptionAsString = exceptionAsString(t);
            printStackTrace("Default Stacktrace", exceptionAsString);

            assertThat(exceptionAsString).doesNotContain("StreamFactory");
        }

        // Enable Traceur and ensure call site is shown
        Traceur.enableLogging();
        try {
            StreamFactory.createNullPointerExceptionObservable().blockingFirst();
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
            StreamFactory.createNullPointerExceptionObservable().blockingFirst();
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
            StreamFactory.createNullPointerExceptionObservable().blockingFirst();
            Assertions.failBecauseExceptionWasNotThrown(Throwable.class);
        } catch (Throwable t) {
            final String exceptionAsString = exceptionAsString(t);
            printStackTrace("Exception with filtering", exceptionAsString);

            assertThat(exceptionAsString).doesNotContain("TraceurException.java");
            assertThat(exceptionAsString).doesNotContain("ObservableOnAssembly.<init>");
        }
    }

    @Test
    public void usingRetryDoesNotFail() {
        Traceur.enableLogging();

        StreamFactory.createNullPointerExceptionObservable()
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        // Needed to trigger error scenario
                    }
                })
                .retry(1)
                .test()
                .assertError(new Predicate<Throwable>() {
                    @Override
                    public boolean test(@NonNull Throwable throwable) throws Exception {
                        return throwable instanceof NullPointerException
                                && throwable.getCause() instanceof TraceurException;
                    }
                });

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
