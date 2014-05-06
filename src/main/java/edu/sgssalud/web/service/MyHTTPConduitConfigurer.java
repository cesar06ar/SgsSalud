/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sgssalud.web.service;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;

/**
 *
 * @author cesar
 */
public class MyHTTPConduitConfigurer implements HTTPConduitConfigurer {

    private final String username;
    private final String password;

    public MyHTTPConduitConfigurer(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void configure(String name, String address, HTTPConduit httpc) {
        System.out.println("AuthorizationHTTPConduitConfigurer.configure()");
        try {
            AuthorizationPolicy ap = new AuthorizationPolicy();
            ap.setUserName(username);
            ap.setPassword(password);
            httpc.setAuthorization(ap);
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

}
