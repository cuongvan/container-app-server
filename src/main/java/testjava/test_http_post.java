/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testjava;

import java.io.IOException;
import utils.HttpUtil;

/**
 *
 * @author cuong
 */
public class test_http_post {
    public static class Req {
        public final String name = "Ye Mo";
        public final int age = 999;
    }
    public static void main(String[] args) throws IOException {
        HttpUtil.post("http://localhost:5000/hahaha", new Req());
    }
}
