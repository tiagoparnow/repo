/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amazingbookstore.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.amazingbookstore.model.Usuario;

public class SessionUtil {
    public static HttpSession getSession(){
        return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    }
    
    public static HttpServletRequest getRequest(){
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }
    
    public static Integer getUserName(){
        return ((Usuario)getSession().getAttribute("user")).getIdUsuario();
    }
    
    public static Usuario getUser(){
        return ((Usuario)getSession().getAttribute("user"));
    }
}
