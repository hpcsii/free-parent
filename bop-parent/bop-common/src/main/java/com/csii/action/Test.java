/**
 * ModifiedBy:   hepeng
 * Date:     2018/8/3 下午3:57
 */
package com.csii.action;

/**
 * Hello world!
 */

public class Test {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Hello World!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}

