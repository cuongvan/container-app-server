/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testjava;

import common.SupportLanguage;

/**
 *
 * @author cuong
 */
public class enum_print {
    public static void main(String[] args) {
        System.out.println(SupportLanguage.JavaScript);
        System.out.println(SupportLanguage.valueOf("JavaScript"));
    }
}
