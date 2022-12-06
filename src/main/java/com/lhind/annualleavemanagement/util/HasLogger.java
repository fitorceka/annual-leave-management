package com.lhind.annualleavemanagement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface HasLogger {

    /**
     * Returns a default logger instance dedicated to the implementing class.
     *
     * @return Logger instance
     * @see org.slf4j.Logger
     */
    default Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }
}
