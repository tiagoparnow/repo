/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amazingbookstore.controller;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.amazingbookstore.dao.UsuarioDAO;
import com.amazingbookstore.model.Role;
import com.amazingbookstore.model.Usuario;

@SessionScoped
@ManagedBean(name = "usuarioController")
public class UsuarioController extends AbstractController{
    public static final String INJECTION_NAME = "#{usuarioController}";
    private Usuario user;

    public UsuarioController() {
    }
    
    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }
    
    public String logOut(){
        getRequest().getSession().invalidate();
        return "/login.xhtml";
    } 
    
    public String admin(){
        return "/pages/protected/admin/admin.xhtml?faces-redirect=true";
    }
    
    public String criarConta() {
    	user.setDataInclusao(new Date());
    	user.setPerfil(Role.USER.toString());
    	if (!new UsuarioDAO().inserirUsuario(user)) {
    		displayErrorMessage("Ocorreu erro ao criar conta");
    	}
    	return "login.xhtml?faces-redirect=true";
    }
    
    public String alterarSenha() {
		if (!new UsuarioDAO().alterarUsuario(user)) {
    		displayErrorMessage("Ocorreu erro ao alterar senha");
    	}
    	return "login.xhtml?faces-redirect=true";
    }
    
}
