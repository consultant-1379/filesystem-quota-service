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
import com.ericsson.oss.services.filesystemquota.rest.interceptor.Authorize;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;


/**
 * This class has REST endpoint to perform a specific operation.
 * This endpoint requires authorization.
 * @return A response containing the status code and a message indicating success or failure.
 */
@Path("/quotas-config")
public class QuotaConfigRest {

    private static final Logger logger = LoggerFactory.getLogger(QuotaConfigRest.class);

    private static final String FILE_FIELDNAME = "file";

    @Inject
    FileSystemQuotaConfigHandler fileSystemQuotaConfigHandler;

    @GET
    @Path("/readQuotaConfiguration")
    @Authorize(resource = "user_mgmt", action = "read", role = "SECURITY_ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readQuotaConfiguration() {
        Response response;
        try {
            String propertiesString = fileSystemQuotaConfigHandler.getStringProperties();
            response = Response.ok(propertiesString).build();
        } catch (FileSystemQuotaException fsqe) {
            logger.error("FileSystemQuotaException occurred: {}", fsqe.toString());
            response = Response.status(fsqe.getErrorCode()).header("message", fsqe.getMessage()).build();
        }
        return response;
    }


    @POST
    @Path("/writeQuotaConfiguration")
    @Authorize(resource = "user_mgmt", action = "read", role = "SECURITY_ADMIN")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response writeQuotaConfiguration(MultipartFormDataInput inputData) {
        Response responseResult;
        try {
            fileSystemQuotaConfigHandler.persistSystemQuotaConfiguration(readCustomerInputData(inputData));
            responseResult = Response.ok("Data Sent").build();
        } catch (FileSystemQuotaException fsqe) {
            logger.error("FileSystemQuotaException occurred: {}", fsqe.toString());
            responseResult = Response.status(fsqe.getErrorCode()).header("message", fsqe.getMessage()).build();
        }
        return responseResult;
    }

    protected String readCustomerInputData(MultipartFormDataInput inputData) throws FileSystemQuotaException {
        Map<String, List<InputPart>> uploadForm = inputData.getFormDataMap();
        try {
            return uploadForm.get(FILE_FIELDNAME).get(0).getBodyAsString();
        } catch (IOException e) {
            throw new FileSystemQuotaException(500, "Failed to read Config data.," + e.getMessage() + " ,Check logs for more info...", Arrays.asList(e.getStackTrace()).subList(0,9));
        }
    }
}