package com.igor.tempero.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igor.tempero.ui.componentes.CardNumero
import com.igor.tempero.ui.componentes.ImagemCategoria
import com.igor.tempero.ui.componentes.MensagemErro
import com.igor.tempero.ui.componentes.TituloTela
import com.igor.tempero.ui.componentes.ToastDeMensagem
import com.igor.tempero.ui.theme.CinzaTexto
import com.igor.tempero.ui.theme.Marrom
import com.igor.tempero.ui.theme.VermelhoErro

@Composable
fun TelaAdmin(viewModel: AdminViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ToastDeMensagem(uiState.mensagem) { viewModel.mensagemExibida() }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { TituloTela("Painel do Administrador", "Relatórios e gerenciamento da base de receitas") }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CardNumero("${uiState.totalReceitas}", "Receitas", Modifier.weight(1f))
                CardNumero("${uiState.totalFavoritas}", "Favoritas", Modifier.weight(1f))
                CardNumero("${uiState.tempoMedio} min", "Tempo médio", Modifier.weight(1f))
            }
        }

        item { MensagemErro(uiState.erro) }

        item {
            Button(onClick = { viewModel.carregarExemplos() }, modifier = Modifier.fillMaxWidth()) {
                Text("📚 Carregar receitas de exemplo")
            }
        }

        if (uiState.porCategoria.isNotEmpty()) {
            item { Text("Receitas por categoria", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Marrom) }
            items(uiState.porCategoria) { (categoria, quantidade) ->
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(categoria, color = Marrom)
                        Text("$quantidade", color = CinzaTexto)
                    }
                    LinearProgressIndicator(
                        progress = { quantidade.toFloat() / uiState.totalReceitas },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                    )
                }
            }
        }

        item { Text("Todas as receitas", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Marrom) }

        items(uiState.receitas, key = { it.id }) { receita ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ImagemCategoria(receita.categoria, tamanho = 44)
                    Column(Modifier.weight(1f)) {
                        Text(receita.nome, fontWeight = FontWeight.SemiBold, color = Marrom)
                        Text("#${receita.id} • ${receita.categoria} • ${receita.origem}", fontSize = 12.sp, color = CinzaTexto)
                    }
                    TextButton(onClick = { viewModel.excluir(receita) }) {
                        Text("Excluir", color = VermelhoErro)
                    }
                }
            }
        }
    }
}
