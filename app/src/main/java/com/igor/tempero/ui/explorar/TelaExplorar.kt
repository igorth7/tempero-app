package com.igor.tempero.ui.explorar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igor.tempero.data.remote.ReceitaRemota
import com.igor.tempero.ui.componentes.EstadoVazio
import com.igor.tempero.ui.componentes.MensagemErro
import com.igor.tempero.ui.componentes.TituloTela
import com.igor.tempero.ui.componentes.ToastDeMensagem
import com.igor.tempero.ui.theme.CinzaTexto
import com.igor.tempero.ui.theme.Marrom

private val sugestoes = listOf("chicken", "cake", "pasta", "beef", "soup", "salad")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaExplorar(viewModel: ExplorarViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ToastDeMensagem(uiState.mensagem) { viewModel.mensagemExibida() }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { TituloTela("Explorar receitas", "Receitas do mundo todo (API TheMealDB). Pesquise em inglês.") }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.termo,
                    onValueChange = { viewModel.onTermoChange(it) },
                    placeholder = { Text("Ex: chicken") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { viewModel.buscar() }, modifier = Modifier.height(56.dp)) { Text("Buscar") }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sugestoes) { sugestao ->
                    AssistChip(
                        onClick = {
                            viewModel.onTermoChange(sugestao)
                            viewModel.buscar()
                        },
                        label = { Text(sugestao) }
                    )
                }
            }
        }

        item { MensagemErro(uiState.erro) }

        when {
            uiState.carregando -> item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.jaBuscou && uiState.resultados.isEmpty() && uiState.erro == null -> item {
                EstadoVazio("🍽", "Nenhuma receita encontrada", "Tente outro termo, como \"cake\" ou \"rice\".")
            }
            !uiState.jaBuscou -> item {
                EstadoVazio("🌍", "Descubra receitas novas", "Busque um prato e salve no seu caderno.")
            }
            else -> items(uiState.resultados, key = { it.idExterno }) { receita ->
                CardReceitaRemota(receita) { viewModel.salvarNoCaderno(receita) }
            }
        }
    }
}

@Composable
private fun CardReceitaRemota(receita: ReceitaRemota, aoSalvar: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(receita.nome, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Marrom)
            Text("${receita.categoria} • ${receita.origem} • ${receita.ingredientes.size} ingredientes", fontSize = 13.sp, color = CinzaTexto)
            Text(
                receita.instrucoes,
                fontSize = 13.sp,
                color = Marrom,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Button(onClick = aoSalvar, modifier = Modifier.padding(top = 6.dp)) {
                Text("📥 Salvar no meu caderno")
            }
        }
    }
}
