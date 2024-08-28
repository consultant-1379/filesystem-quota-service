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

import java.util.LinkedHashMap;
import java.util.Map;

public class YamlToPropertiesStringQuotaConverter extends QuotaConverter<String> {

    public YamlToPropertiesStringQuotaConverter(){
        super();
    }

    /**
     * Assigns input data Map to instance variables.
     */
    public void parseProperties(Map<String, Object> yamlMap){
        this.quotaEnabled = (Boolean) yamlMap.get(QUOTA_ENABLED);
        this.sizeGenericQuota = Long.valueOf(yamlMap.get(SIZE_GENERIC_QUOTA).toString());
        this.customQuotas = (LinkedHashMap<String, Object>) yamlMap.get(CUSTOM_QUOTA);
    }

    /**
     * Converts the value obtained from the getter method into a string using a StringBuilder.
     */
    @Override
    public String convert() {
        StringBuilder propertiesString = new StringBuilder();
        propertiesString.append(QUOTA_ENABLED).append("=").append(getQuotaEnabled()).append("\n");
        propertiesString.append(SIZE_GENERIC_QUOTA).append("=").append(convertSizeToMB(getSizeGenericQuota()));
        if (getCustomQuota() != null) {
            getCustomQuota().entrySet().forEach(entry -> propertiesString.append("\n").append(entry.getKey()).append("=").append(convertSizeToMB(Long.valueOf(entry.getValue().toString()))));
        }
        return propertiesString.toString();
    }

}
