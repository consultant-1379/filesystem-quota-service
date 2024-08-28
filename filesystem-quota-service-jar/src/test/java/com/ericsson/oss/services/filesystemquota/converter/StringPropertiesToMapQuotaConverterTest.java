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

import com.ericsson.oss.services.filesystemquota.exception.FileSystemQuotaException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.LinkedHashMap;

class StringPropertiesToMapQuotaConverterTest {

    StringPropertiesToMapQuotaConverter stringPropertiesToMapQuotaConverter = new StringPropertiesToMapQuotaConverter();

    @Test
    void assignProperties_success() throws FileSystemQuotaException {

        String content = "quotaEnabled=true\nsizeGenericQuota=50\nuser1=22\nuser2=40";
        LinkedHashMap<String, Object> customQuotas = new LinkedHashMap<>();
        customQuotas.put("user1",23068672l);
        customQuotas.put("user2",41943040l);

        stringPropertiesToMapQuotaConverter.assignProperties(content);

        assertEquals(Boolean.TRUE, stringPropertiesToMapQuotaConverter.getQuotaEnabled());
        assertEquals(52428800l,stringPropertiesToMapQuotaConverter.getSizeGenericQuota());
        LinkedHashMap<String, Object> customQuotasResult =  stringPropertiesToMapQuotaConverter.getCustomQuota();
        customQuotas.entrySet().stream().forEach( u -> {
            assertTrue(customQuotasResult.containsKey(u.getKey()));
            assertEquals(u.getValue(), customQuotasResult.get(u.getKey()));
        });

        LinkedHashMap<String, Object> result = stringPropertiesToMapQuotaConverter.convert();
        assertEquals(3, result.size());
        assertEquals(2, ((LinkedHashMap<String, Object>)result.get("customQuotas")).size());

    }

    @Test
    void assignProperties_fieldsMissing() {

        String mockContent = "quotaEna=true\nsizeGenericQuota=50\nuser1=22\nuser2=40";
        FileSystemQuotaException exception = null;
        try {
            stringPropertiesToMapQuotaConverter.assignProperties(mockContent);
        } catch (FileSystemQuotaException fileSystemQuotaException){
            exception = fileSystemQuotaException;
        }
        assertEquals("Invalid File Content Provided,Mandatory Parameter(s) is(are) missing", exception.getMessage());
        assertEquals(400, exception.getErrorCode());
    }

    @Test
    void assignProperties_invalidCustomQuotaValues() {

        String mockContent = "quotaEnabled=tru\nsizeGenericQuota=50\nuser1=22m\nuser2=40";
        FileSystemQuotaException exception = null;
        try {
            stringPropertiesToMapQuotaConverter.assignProperties(mockContent);
        } catch (FileSystemQuotaException fileSystemQuotaException){
            exception = fileSystemQuotaException;
        }
        assertTrue(exception.getMessage().contains("user1"));
        assertEquals(400, exception.getErrorCode());
    }
}
