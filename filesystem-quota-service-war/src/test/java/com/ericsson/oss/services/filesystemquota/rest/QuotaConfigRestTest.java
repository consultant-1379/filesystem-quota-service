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

package com.ericsson.oss.services.filesystemquota.rest;

import com.ericsson.oss.services.filesystemquota.FileSystemQuotaConfigHandler;
import com.ericsson.oss.services.filesystemquota.exception.FileSystemQuotaException;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import javax.ws.rs.core.Response;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(MockitoJUnitRunner.class)
public class QuotaConfigRestTest {

    @InjectMocks
    private QuotaConfigRest quotaConfigRest;
    @Mock
    private FileSystemQuotaConfigHandler fileSystemQuotaConfigHandler;

    @Test
    public void readQuotaConfiguration_successfully() throws FileSystemQuotaException {
        when(fileSystemQuotaConfigHandler.getStringProperties()).thenReturn("getting string properties");
        Response result = quotaConfigRest.readQuotaConfiguration();
        assertEquals("getting string properties", result.getEntity());
        assertEquals(200, result.getStatus());
    }

    @Test
    public void readQuotaConfiguration_exceptionOccurred() throws FileSystemQuotaException {
        when(fileSystemQuotaConfigHandler.getStringProperties()).thenThrow(new FileSystemQuotaException(500, "Any Error"));
        Response result = quotaConfigRest.readQuotaConfiguration();
        assertEquals(null, result.getEntity());
        assertNotEquals(200, result.getStatus());
        assertEquals("Any Error", result.getHeaders().get("message").get(0));
    }

    @Test
    public void writeQuotaConfiguration_persistIssue() throws IOException, FileSystemQuotaException {
        InputPart inputPart = Mockito.mock(InputPart.class);
        when(inputPart.getBodyAsString()).thenReturn("some value");

        Map<String, List<InputPart>> uploadForm = new HashMap<>();
        List<InputPart> inputParts = new ArrayList<>();
        inputParts.add(inputPart);
        uploadForm.put("file", inputParts);

        MultipartFormDataInput multipartFormDataInput = Mockito.mock(MultipartFormDataInput.class);
        when(multipartFormDataInput.getFormDataMap()).thenReturn(uploadForm);
        doThrow(new FileSystemQuotaException(500, "Process Error")).when(fileSystemQuotaConfigHandler).persistSystemQuotaConfiguration("some value");
        Response result = quotaConfigRest.writeQuotaConfiguration(multipartFormDataInput);

        assertEquals(null, result.getEntity());
        assertNotEquals(200, result.getStatus());
        assertEquals("Process Error", result.getHeaders().get("message").get(0));
    }

    @Test
    public void writeQuotaConfiguration_success() throws IOException {
        InputPart inputPart = Mockito.mock(InputPart.class);
        when(inputPart.getBodyAsString()).thenReturn("Some Value");

        Map<String, List<InputPart>> uploadForm = new HashMap<>();
        List<InputPart> inputParts = new ArrayList<>();
        inputParts.add(inputPart);
        uploadForm.put("file", inputParts);

        MultipartFormDataInput multipartFormDataInput = Mockito.mock(MultipartFormDataInput.class);
        when(multipartFormDataInput.getFormDataMap()).thenReturn(uploadForm);
        Response result = quotaConfigRest.writeQuotaConfiguration(multipartFormDataInput);

        assertEquals("Data Sent", result.getEntity());
        assertEquals(200, result.getStatus());
    }

    @Test
    public void writeQuotaConfiguration_MultipartToStringConversionError() throws IOException {

        InputPart inputPart = Mockito.mock(InputPart.class);
        when(inputPart.getBodyAsString()).thenThrow(new IOException("Some Error"));

        Map<String, List<InputPart>> uploadForm = new HashMap<>();
        List<InputPart> inputParts = new ArrayList<>();
        inputParts.add(inputPart);
        uploadForm.put("file", inputParts);

        MultipartFormDataInput multipartFormDataInput = Mockito.mock(MultipartFormDataInput.class);
        when(multipartFormDataInput.getFormDataMap()).thenReturn(uploadForm);
        Response result = quotaConfigRest.writeQuotaConfiguration(multipartFormDataInput);

        assertNotEquals(200, result.getStatus());
        assertEquals("Failed to read Config data.,Some Error ,Check logs for more info...", result.getHeaders().get("message").get(0));
    }

}
