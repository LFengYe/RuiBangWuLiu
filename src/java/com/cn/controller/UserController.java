package com.cn.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LFeng
 */
public class UserController {
    
    public String userLoginSuccess(String username, String password) {
        try {
            String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
            File file = new File(path + "menu_data.json");
            StringBuilder builder = new StringBuilder();
            Scanner scanner = new Scanner(file, "utf-8");
            while(scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }
            return builder.toString();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String returnFileContext(String fileName) {
        try {
            String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
            File file = new File(path + fileName);
            StringBuilder builder = new StringBuilder();
            Scanner scanner = new Scanner(file, "utf-8");
            while(scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }
            return builder.toString();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
