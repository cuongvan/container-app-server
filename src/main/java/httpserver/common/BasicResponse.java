/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 *
 * @author cuong
 */
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class BasicResponse {
    public final String error;
    public BasicResponse(String error) {
        this.error = error;
    }
    
    public static BasicResponse success() {
        return new BasicResponse("");
    }
    
    public static BasicResponse fail(String error) {
        return new BasicResponse(error);
    }
}
