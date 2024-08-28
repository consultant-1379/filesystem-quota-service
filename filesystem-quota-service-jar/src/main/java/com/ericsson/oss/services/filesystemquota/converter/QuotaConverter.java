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

import java.util.Arrays;
import java.util.LinkedHashMap;

public abstract class QuotaConverter<T> implements Converter<T> {
    public static final String QUOTA_ENABLED="quotaEnabled";
    public static final String SIZE_GENERIC_QUOTA="sizeGenericQuota";
    public static final String CUSTOM_QUOTA = "customQuota";
    private static final Long MB_CONVERTING_VALUE = 1048576l;


    protected Boolean quotaEnabled;
    protected Long sizeGenericQuota;
    protected LinkedHashMap<String, Object> customQuotas = new LinkedHashMap<>();

    protected Boolean getQuotaEnabled() {
        return quotaEnabled;
    }

    protected Long getSizeGenericQuota() {
        return sizeGenericQuota;
    }
    protected LinkedHashMap<String, Object>  getCustomQuota() {
        return this.customQuotas;
    }

    public boolean isBoolean(String value) {
        return value != null && Arrays.stream(new String[]{"true", "false"})
                .anyMatch(b -> b.equalsIgnoreCase(value));
    }

    protected Long validateAndConvertSizeToBytes(String property) {
        try {
            Long value = Long.parseLong(String.valueOf(property));
            return (value >= 0l) ? (value * MB_CONVERTING_VALUE) : -1l;
        }catch(Exception e)
        {
            return -1l;
        }
    }

    protected Long convertSizeToMB(Long value){
        return (value / MB_CONVERTING_VALUE);
    }

    /** This method will show the attributes and their values
     *
     * @return String with all the values of each attribute of the class
     */
    @Override
    public String toString() {
        return "QuotaConverter{" +
                "quotaEnabled=" + quotaEnabled +
                ", sizeGenericQuota=" + sizeGenericQuota +
                ", customQuotas=" + customQuotas +
                '}';
    }

}