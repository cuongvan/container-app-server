/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package externalapi.appcall.models;

/**
 *
 * @author cuong
 */
public enum CallStatus {
    STARTED, /* not finished yet */
    
    SUCCESS, FAILED, TIMEOUT, OUT_OF_MEMORY/* finished */
}
