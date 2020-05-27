package httpserver.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SuccessResponse {

    public final String error = "";

    public static final SuccessResponse OK = new SuccessResponse();

}
