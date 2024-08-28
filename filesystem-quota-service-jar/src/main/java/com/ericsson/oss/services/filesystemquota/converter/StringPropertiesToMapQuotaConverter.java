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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Properties;


public class StringPropertiesToMapQuotaConverter extends QuotaConverter<LinkedHashMap<String, Object>> {

    public StringPropertiesToMapQuotaConverter() {
        super();
    }
    
    /**
    *  Validates the input data and assigns properties to instance variables.
     * @throws FileSystemQuotaException if any error encountered custom exception will be thrown
    */
    public void assignProperties(String stringProperties) throws FileSystemQuotaException {
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(stringProperties));
        } catch (IOException e) {
            throw new FileSystemQuotaException(500, "Failed to read Config data," + e.getMessage() + ",Check logs for more info...", Arrays.asList(e.getStackTrace()).subList(0,9));
        }

        List<String> invalidPropertiesList = new ArrayList<>();
        if(properties.containsKey(QUOTA_ENABLED) && properties.containsKey(SIZE_GENERIC_QUOTA)) {
            processQuotaEnabled(invalidPropertiesList, properties);
            processLongValues(invalidPropertiesList, SIZE_GENERIC_QUOTA, properties.getProperty(SIZE_GENERIC_QUOTA));
            properties.keySet().stream()
                    .filter( key -> !key.equals(QUOTA_ENABLED) && !key.equals(SIZE_GENERIC_QUOTA))
                    .forEach( key -> processLongValues(invalidPropertiesList, key.toString(), properties.getProperty(key.toString())));
        }
        else{
            throw new FileSystemQuotaException(400, "Invalid File Content Provided,Mandatory Parameter(s) is(are) missing");
        }
        if(!invalidPropertiesList.isEmpty())
        {
            throw new FileSystemQuotaException(400, "Invalid File Content Provided,Wrong values for these parameters:," + invalidPropertiesList);
        }
    }
    private void processQuotaEnabled(List<String> invalidPropertiesList, Properties properties) {
        if(isBoolean(properties.getProperty(QUOTA_ENABLED))) {
            this.quotaEnabled = Boolean.parseBoolean(properties.getProperty(QUOTA_ENABLED));
        }
        else {
            invalidPropertiesList.add(QUOTA_ENABLED);
        }
    }

    private void processLongValues(List<String> invalidPropertiesList, String propertyName, String propertyValue) {
        Long longValue = validateAndConvertSizeToBytes(propertyValue);
        if(longValue != -1l)
        {
            if(propertyName.equals(SIZE_GENERIC_QUOTA))
            {
                this.sizeGenericQuota = longValue;
            }
            else {
                this.customQuotas.put(propertyName, longValue);
            }
        }else{
            invalidPropertiesList.add(propertyName);
        }
    }

    /**
    * Populates a map where the values are obtained by calling the getter method.
    * */
    @Override
    public LinkedHashMap<String, Object> convert() {
        LinkedHashMap<String, Object> yamlMap = new LinkedHashMap<>();
        yamlMap.put(QUOTA_ENABLED, getQuotaEnabled());
        yamlMap.put(SIZE_GENERIC_QUOTA, getSizeGenericQuota());
        yamlMap.put(CUSTOM_QUOTA, getCustomQuota());
        return yamlMap;
    }
}
