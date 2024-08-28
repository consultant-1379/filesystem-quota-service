/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.filesystemquota.exception;

import java.util.List;

public class FileSystemQuotaException extends Exception {
    private final int errorCode;

    private final List<StackTraceElement> stackTraceElements;

    public int getErrorCode() {
        return errorCode;
    }
    public FileSystemQuotaException(int errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
        stackTraceElements = null;
    }

    public FileSystemQuotaException(int errorCode, final String message, final List<StackTraceElement> stackTraceElements) {
        super(message);
        this.stackTraceElements =stackTraceElements;
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "FileSystemQuotaException{" +
                "errorCode=" + this.errorCode +
                "errorMessage=" + this.getMessage() +
                "stackTrace=" + this.stackTraceElements+
                '}';
    }
}
