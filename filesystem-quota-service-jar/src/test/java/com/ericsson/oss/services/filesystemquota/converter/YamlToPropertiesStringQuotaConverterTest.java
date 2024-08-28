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

package com.ericsson.oss.services.filesystemquota.converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class YamlToPropertiesStringQuotaConverterTest {

    YamlToPropertiesStringQuotaConverter yamlToPropertiesStringQuotaConverter = new YamlToPropertiesStringQuotaConverter();

    @Test
    void parseProperties_success() {
        Map<String, Object> mockMap = new HashMap<>();

        LinkedHashMap<String, Object> customQuotas = new LinkedHashMap<>();
        customQuotas.put("user1", 23458010);
        customQuotas.put("user2",42429988);
        customQuotas.put("user3", 92425768);

        mockMap.put("quotaEnabled", Boolean.TRUE);
        mockMap.put("sizeGenericQuota",52428800);
        mockMap.put("customQuotas",customQuotas);


        yamlToPropertiesStringQuotaConverter.parseProperties(mockMap);
        yamlToPropertiesStringQuotaConverter.toString();

        assertAll(
                "Grouped Assertions of assigning Properties",
                () -> assertEquals(Boolean.TRUE, yamlToPropertiesStringQuotaConverter.getQuotaEnabled()),
                () -> assertEquals(52428800, yamlToPropertiesStringQuotaConverter.getSizeGenericQuota()),
                () -> assertEquals(customQuotas, yamlToPropertiesStringQuotaConverter.getCustomQuota())
        );

    }

    @Test
    void convert_test()
    {
        Map<String, Object> mockMap = new HashMap<>();

        LinkedHashMap<String, Object> customQuotas = new LinkedHashMap<>();
        customQuotas.put("user1", 23458010);
        customQuotas.put("user2",42429988);
        customQuotas.put("user3", 92425768);

        mockMap.put("quotaEnabled", Boolean.TRUE);
        mockMap.put("sizeGenericQuota",52428800);
        mockMap.put("customQuotas",customQuotas);

        yamlToPropertiesStringQuotaConverter.parseProperties(mockMap);

        String result = yamlToPropertiesStringQuotaConverter.convert();

        assertTrue(result.contains("quotaEnabled=true"));
        assertTrue(result.contains("sizeGenericQuota=50"));
        assertTrue(result.contains("user1=22"));
        assertTrue(result.contains("user2=40"));
        assertTrue(result.contains("user3=88"));
    }
}
