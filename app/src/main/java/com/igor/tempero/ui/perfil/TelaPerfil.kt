package com.igor.tempero.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.igor.tempero.data.local.Receita
import com.igor.tempero.ui.componentes.CardNumero
import com.igor.tempero.ui.componentes.Etiqueta
import com.igor.tempero.ui.theme.CinzaTexto
import com.igor.tempero.ui.theme.Laranja
import com.igor.tempero.ui.theme.LaranjaClaro
import com.igor.tempero.ui.theme.Marrom
import com.igor.tempero.ui.theme.VermelhoErro

@Composable
fun TelaPerfil(
    nome: String,
    isAdmin: Boolean,
    receitas: List<Receita>,
    totalItensCompra: Int,
    aoSair: () -> Unit
) {
    val favoritas = receitas.filter { it.favorita }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(LaranjaClaro),
            contentAlignment = Alignment.Center
        ) {
            Text(nome.take(1).uppercase(), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Laranja)
        }
        Text(nome, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Marrom)
        Etiqueta(if (isAdmin) "🛠️ Administrador" else "👩‍🍳 Cozinheiro(a)")

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CardNumero("${receitas.size}", "Receitas", Modifier.weight(1f))
            CardNumero("${favoritas.size}", "Favoritas", Modifier.weight(1f))
            CardNumero("$totalItensCompra", "Na lista", Modifier.weight(1f))
        }

        Column(Modifier.fillMaxWidth()) {
            Text("❤️ Minhas favoritas", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Marrom)
            if (favoritas.isEmpty()) {
                Text("Nenhuma favorita ainda.", color = CinzaTexto)
            } else {
                favoritas.sortedBy { it.nome }.forEach { receita ->
                    Text("• ${receita.nome}", color = Marrom, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        OutlinedButton(onClick = aoSair, modifier = Modifier.fillMaxWidth()) {
            Text("Sair da conta", color = VermelhoErro)
        }
    }
}
