package com.amazingbookstore.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.amazingbookstore.model.CarrinhoCompra;

@RequestScoped
@ManagedBean
public class CarrinhoComprasController extends AbstractController {

	@ManagedProperty(value = UsuarioController.INJECTION_NAME)
	private UsuarioController usuarioController;
	
	public void setUsuarioController(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }
	
	@PostConstruct
	public void init() {
		if (usuarioController != null && usuarioController.getUser() != null
				&& usuarioController.getUser().getCarrinhoCompra() == null) {
			usuarioController.getUser().setCarrinhoCompra(new CarrinhoCompra());
		}
	}
	
}
