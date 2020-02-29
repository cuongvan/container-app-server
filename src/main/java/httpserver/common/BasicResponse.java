/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.common;

/**
 *
 * @author cuong
 */
public class BasicResponse {
    public final String error;
    public static final BasicResponse OK = new BasicResponse("");

    public BasicResponse(String error) {
        this.error = error;
    }
}
