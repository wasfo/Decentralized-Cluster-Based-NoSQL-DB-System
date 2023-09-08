package org.worker.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.worker.constants.FilePaths;

import java.io.File;



public class DbUtils {



    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<String>("message: " + responseMessage, httpStatus);
    }


    static public boolean isResponseSuccessful(ResponseEntity<?> response) {

        return responseIsNotError(response) &&
                responseIsNotServerError(response) &&
                responseIsNotBadRequest(response);

    }

    public static boolean responseIsNotError(ResponseEntity<?> response) {

        return !response.getStatusCode().isError();
    }

    public static boolean responseIsNotServerError(ResponseEntity<?> response) {

        return !response.getStatusCode().is5xxServerError();
    }

    public static boolean responseIsNotBadRequest(ResponseEntity<?> response) {

        return !response.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }
}
