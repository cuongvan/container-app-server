/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author cuong
 */
@FunctionalInterface
public interface ThrowingConsumer<T> {
    void acceptThrows(T elem) throws Exception;
}
