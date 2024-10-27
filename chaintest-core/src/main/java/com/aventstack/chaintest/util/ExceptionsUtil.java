package com.aventstack.chaintest.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionsUtil {

    public static String readStackTrace(final Throwable e) {
        final StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}
