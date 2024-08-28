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

import com.ericsson.oss.services.filesystemquota.converter.StringPropertiesToMapQuotaConverter;
import com.ericsson.oss.services.filesystemquota.converter.YamlToPropertiesStringQuotaConverter;
import com.ericsson.oss.services.filesystemquota.exception.FileSystemQuotaException;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import static com.ericsson.oss.services.filesystemquota.converter.QuotaConverter.QUOTA_ENABLED;
import static com.ericsson.oss.services.filesystemquota.converter.QuotaConverter.SIZE_GENERIC_QUOTA;

public class FileSystemQuotaConfigHandler {
    private static final String CONFIG_FILE_PATH = "/ericsson/tor/no_rollback/ceph_quotas/ceph_quotas.yaml";

    @Inject
    YamlToPropertiesStringQuotaConverter yamlToPropertiesStringQuotaConverter;
    @Inject
    StringPropertiesToMapQuotaConverter stringPropertiesToMapQuotaConverter;

    /**
     *  Opens an inputstream for the specified file path and converts it into a Properties of String format.
     *
     * @see #CONFIG_FILE_PATH The global variable specifying the Yaml file path
     * @throws FileSystemQuotaException if any error encountered while loading the content to a inputstream
     * @return A String representing the properties in key=value
     */
    public String getStringProperties() throws FileSystemQuotaException{
        String response;
        if (Files.exists(Paths.get(CONFIG_FILE_PATH))) {
            try{
               InputStream isFSQC = Files.newInputStream(Paths.get(CONFIG_FILE_PATH));
                response = loadYamlAsPropertiesString(isFSQC);
            } catch (IOException e) {
                throw new FileSystemQuotaException(500, "Failed to read Config data.\n " + e.getMessage() + " \nCheck logs for more info...", Arrays.asList(e.getStackTrace()).subList(0,9));
            }
        }
        else {
            StringBuilder noConfigFoundConfiguration = new StringBuilder();
            noConfigFoundConfiguration.append(QUOTA_ENABLED).append(": ").append(Boolean.FALSE).append("\n");
            noConfigFoundConfiguration.append(SIZE_GENERIC_QUOTA).append(": 0");
            response = loadYamlAsPropertiesString (new ByteArrayInputStream(noConfigFoundConfiguration.toString().getBytes()));
        }
        return response;
    }

    /**
     * Loads YAML data from an InputStream, converts it into string properties, and returns the result.
     *
     * @see #CONFIG_FILE_PATH The global variable specifying the Yaml file path
     * @throws FileSystemQuotaException if any error encountered while loading the content or during concersation
     * @return A String representing the properties in key=value
     */
    private String loadYamlAsPropertiesString(InputStream isFSQC) throws FileSystemQuotaException {
        Yaml yaml = new Yaml();
        Map<String, Object> yamlMap ;
        try {
            yamlMap = yaml.load(isFSQC);
        } catch (RuntimeException yamlException){
            throw new FileSystemQuotaException(500, "Failed to parsing Config data," + yamlException.getMessage() + ",Check logs for more info...", Arrays.asList(yamlException.getStackTrace()).subList(0,9));
        }
        yamlToPropertiesStringQuotaConverter.parseProperties(yamlMap);
        return yamlToPropertiesStringQuotaConverter.convert();
    }


    /**
    * This method will converts a properties from string format to YAML format and writes the YAML Content to a file
     *
     *  @param stringProperties The string containing properties in key=value format.
     *  @throws FileSystemQuotaException If there's a problem during the conversion process or while writing the YAML file.
    */

    public void persistSystemQuotaConfiguration (String stringProperties) throws FileSystemQuotaException {
        stringPropertiesToMapQuotaConverter.assignProperties(stringProperties);
        writeFilesystemQuotaConfigfile (stringPropertiesToMapQuotaConverter.convert());
    }

    /**
     *  Writes the provided YAML content to a YAML file.
     *
     * @param yamlContent The map representing the Yaml content to be written
     * @see #CONFIG_FILE_PATH The global variable specifying the file path
     * @throws FileSystemQuotaException if any error encountered while writing the content to a file
    */
    private void writeFilesystemQuotaConfigfile(LinkedHashMap<String, Object> yamlContent) throws FileSystemQuotaException{
        try (FileWriter fwFSQC = new FileWriter(CONFIG_FILE_PATH)) {
            Yaml yaml = new Yaml();
            yaml.dump(yamlContent, fwFSQC);
            fwFSQC.flush();
        } catch (IOException e) {
            throw new FileSystemQuotaException(500, "Failed to write Config data," + e.getMessage() + ",Check logs for more info...", Arrays.asList(e.getStackTrace()).subList(0,9));
        }
    }
}
