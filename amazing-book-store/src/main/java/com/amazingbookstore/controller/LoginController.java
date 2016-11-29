package com.amazingbookstore.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.amazingbookstore.dao.UsuarioDAO;
import com.amazingbookstore.model.Usuario;

@RequestScoped
@ManagedBean
public class LoginController extends AbstractController {

    @ManagedProperty(value = UsuarioController.INJECTION_NAME)
    private UsuarioController usuarioController;

    private String email;
    private String senha;
    
    public void setUsuarioController(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }
    
    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	private Usuario isValidLogin(String email) {
        Usuario user = new UsuarioDAO().findByEmail(email);

        if (user == null) {
            return null;
        }
        
        return user;
    }
    
	private boolean isValidPassword(Usuario usuario) {
		return !senha.equalsIgnoreCase(usuario.getSenha());
	}
	
	public String doLogin() {
		// Verifica se o e-mail e senha existem e se o usuario pode logar
		Usuario user = isValidLogin(email);

		// Caso não tenha retornado nenhum usuario, então mostramos um erro
		// e redirecionamos ele para a página login.xhtml
		// para ele realiza-lo novament
		if (user != null) {
			if (isValidPassword(user)) {
				displayErrorMessage("Sua senha está incorreta");
				return null;
			}
			
			// caso tenha retornado um usuario, setamos a variável loggedIn
			// como true e guardamos o usuario encontrado na variável
			// usuarioLogado. Depois de tudo, mandamos o usuário
			// para a página index.xhtml
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			request.getSession().setAttribute("user", user);
			usuarioController.setUser(user);
			return "index.xhtml?faces-redirect=true";
		}

		displayErrorMessage("Não encontramos uma conta associada a este endereço de e-mail");
		return null;
	}
    
	public String esquecerSenha() {
		Usuario user = isValidLogin(email);
		if (user == null) {
			displayErrorMessage("Não encontramos uma conta associada a este endereço de e-mail");
			return null;
		}
		usuarioController.setUser(user);
		return "alterar-senha.xhtml?faces-redirect=true";
	}
	
	public String criarNovaConta() {
		Usuario user = new Usuario();
		user.setEMail(email);
		usuarioController.setUser(user);
		return "criar-conta.xhtml?faces-redirect=true";
	}
	
	// Realiza o logout do usuário logado
	public String doLogout() {

		// Setamos a variável usuarioLogado como nulo, ou seja, limpamos
		// os dados do usuário que estava logado e depois setamos a variável
		// loggedIn como false para sinalizar que o usuário não está mais
		// logado
		usuarioController.setUser(null);
		// Mostramos um mensagem ao usuário e redirecionamos ele para a //página
		// de login
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		request.getSession().setAttribute("user", null);
		displayInfoMessage("Logout realizado com sucesso !");
		return "index.xhtml?faces-redirect=true";
	}

	public boolean isAdmin() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		Usuario user = (Usuario) request.getSession().getAttribute("user");
		if (user != null) {
			return user.isAdmin();
		}
		return false;
	}
	
	public boolean isLoggedIn() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		Usuario user = (Usuario) request.getSession().getAttribute("user");
		if (user != null) {
			return true;
		}
		return false;
	}
	
	public String realizarLogin() {
		return "login.xhtml?faces-redirect=true";
	}
	
}
