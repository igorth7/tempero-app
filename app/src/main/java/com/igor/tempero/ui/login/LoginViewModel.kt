package com.igor.tempero.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Os dois perfis do app (bifurcação de acesso). */
enum class Perfil { USUARIO, ADMIN }

data class LoginUiState(
    val nome: String = "",
    val senha: String = "",
    val erro: String? = null,
    val perfil: Perfil? = null
) {
    val logado: Boolean get() = perfil != null
    val isAdmin: Boolean get() = perfil == Perfil.ADMIN
}

/**
 * Login SIMULADO: não existe servidor de usuários.
 * - admin / 1234       -> Modo Administrador
 * - qualquer outro nome (senha com 4+ caracteres) -> Modo Usuário
 */
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onNomeChange(novoNome: String) {
        _uiState.update { it.copy(nome = novoNome, erro = null) }
    }

    fun onSenhaChange(novaSenha: String) {
        _uiState.update { it.copy(senha = novaSenha, erro = null) }
    }

    fun entrar() {
        val estado = _uiState.value
        val nome = estado.nome.trim()

        when {
            nome.isBlank() || estado.senha.isBlank() ->
                _uiState.update { it.copy(erro = "Preencha o nome e a senha.") }

            nome.equals("admin", ignoreCase = true) && estado.senha == SENHA_ADMIN ->
                _uiState.update { it.copy(perfil = Perfil.ADMIN, erro = null) }

            nome.equals("admin", ignoreCase = true) ->
                _uiState.update { it.copy(erro = "Senha de administrador incorreta.") }

            estado.senha.length < 4 ->
                _uiState.update { it.copy(erro = "A senha precisa ter pelo menos 4 caracteres.") }

            else ->
                _uiState.update { it.copy(perfil = Perfil.USUARIO, erro = null) }
        }
    }

    fun sair() {
        _uiState.value = LoginUiState()
    }

    companion object {
        private const val SENHA_ADMIN = "1234"
    }
}
