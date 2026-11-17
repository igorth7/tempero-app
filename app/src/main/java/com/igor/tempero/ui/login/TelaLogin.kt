package com.igor.tempero.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igor.tempero.ui.componentes.MensagemErro
import com.igor.tempero.ui.theme.CinzaTexto
import com.igor.tempero.ui.theme.Creme
import com.igor.tempero.ui.theme.Laranja
import com.igor.tempero.ui.theme.LaranjaClaro
import com.igor.tempero.ui.theme.Marrom

@Composable
fun TelaLogin(
    viewModel: LoginViewModel,
    aoEntrar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Quando o ViewModel define um perfil, a tela navega
    LaunchedEffect(uiState.perfil) {
        if (uiState.logado) aoEntrar()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Creme)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🍲", fontSize = 64.sp)
        Text("Tempero", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Laranja)
        Text("Seu caderno de receitas", fontSize = 15.sp, color = CinzaTexto)

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.nome,
            onValueChange = { viewModel.onNomeChange(it) },
            label = { Text("Nome de usuário") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.senha,
            onValueChange = { viewModel.onSenhaChange(it) },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        MensagemErro(uiState.erro)
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.entrar() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = LaranjaClaro),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(14.dp)) {
                Text("Acesso de teste", fontWeight = FontWeight.Bold, color = Marrom)
                Text("👤 Usuário: qualquer nome + senha com 4 caracteres", fontSize = 13.sp, color = Marrom)
                Text("🛠️ Administrador: admin / 1234", fontSize = 13.sp, color = Marrom)
            }
        }
    }
}
