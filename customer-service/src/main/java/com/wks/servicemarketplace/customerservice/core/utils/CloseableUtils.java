package com.wks.servicemarketplace.customerservice.core.utils;

import java.io.Closeable;
import java.util.function.Consumer;

public class CloseableUtils {

    public static void close(Closeable closeable, Consumer<Exception> closeExceptionHandler) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            if (closeExceptionHandler != null) {
                closeExceptionHandler.accept(e);
            }
            e.printStackTrace();
        }
    }

    public static void close(Closeable closeable) {
        CloseableUtils.close(closeable, null);
    }

    public static void close(AutoCloseable closeable, Consumer<Exception> closeExceptionHandler) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            if (closeExceptionHandler != null) {
                closeExceptionHandler.accept(e);
            }
            e.printStackTrace();
        }
    }

    public static void close(AutoCloseable closeable) {
        CloseableUtils.close(closeable, null);
    }
}
