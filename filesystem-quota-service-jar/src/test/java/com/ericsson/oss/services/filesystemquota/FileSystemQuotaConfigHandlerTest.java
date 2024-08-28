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

package com.ericsson.oss.services.filesystemquota;

import com.ericsson.oss.services.filesystemquota.converter.YamlToPropertiesStringQuotaConverter;
import com.ericsson.oss.services.filesystemquota.exception.FileSystemQuotaException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FileSystemQuotaConfigHandlerTest {

    @Mock
    YamlToPropertiesStringQuotaConverter yamlToPropertiesStringQuotaConverter;

    @InjectMocks
    FileSystemQuotaConfigHandler fileSystemQuotaConfigHandler;

    @Test
    public void getStringProperties_configFileDoesNotExist() throws FileSystemQuotaException {

        MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class);
        mockFiles.when(() -> Files.exists(any())).thenReturn(Boolean.FALSE);
        when(yamlToPropertiesStringQuotaConverter.convert()).thenReturn("output string");

        String stringPropertiesFSQC = fileSystemQuotaConfigHandler.getStringProperties();
        assertEquals("output string", stringPropertiesFSQC);

        Map<String, Object> yamlMap = new LinkedHashMap<>();
        yamlMap.put("quotaEnabled", false);
        yamlMap.put("sizeGenericQuota", 0);
        Mockito.verify(yamlToPropertiesStringQuotaConverter, times(1)).parseProperties(yamlMap);
        Mockito.verify(yamlToPropertiesStringQuotaConverter, times(1)).convert();
        mockFiles.close();
    }

    @Test
    public void getStringProperties_IOException_Error() {

        MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class);
        mockFiles.when(() -> Files.exists(any())).thenReturn(Boolean.TRUE);
        mockFiles.when(() -> Files.newInputStream(any())).thenThrow( new IOException("some exception"));
        assertThrows(FileSystemQuotaException.class, () -> {
            fileSystemQuotaConfigHandler.getStringProperties();
        });
        Mockito.verify(yamlToPropertiesStringQuotaConverter, times(0)).parseProperties(any());
        Mockito.verify(yamlToPropertiesStringQuotaConverter, times(0)).convert();

        mockFiles.close();
    }

    @Test
    public void getStringProperties_correct() throws FileSystemQuotaException {

        Path pathFSQC1 = Paths.get("src/test/resources/data/valid.yaml");

        MockedStatic<Paths> pathsMockedStatic = Mockito.mockStatic(Paths.class);
        pathsMockedStatic.when(() -> Paths.get(anyString())).thenReturn(pathFSQC1);
        when(yamlToPropertiesStringQuotaConverter.convert()).thenReturn("output string");

        String stringPropertiesFSQC = fileSystemQuotaConfigHandler.getStringProperties();

        assertEquals("output string", stringPropertiesFSQC);

        LinkedHashMap<String, Object> customQuotas = new LinkedHashMap<>();
        customQuotas.put("user1",42991616);
        customQuotas.put("user2",54525952);
        customQuotas.put("user3",314572800);

        Map<String, Object> yamlMap = new LinkedHashMap<>();
        yamlMap.put("quotaEnabled", true);
        yamlMap.put("sizeGenericQuota", 52991616);
        yamlMap.put("customQuotas", customQuotas);
        Mockito.verify(yamlToPropertiesStringQuotaConverter, times(1)).parseProperties(yamlMap);
        Mockito.verify(yamlToPropertiesStringQuotaConverter, times(1)).convert();
    }

}