package com.igor.tempero.ui.compras

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igor.tempero.data.local.ItemCompra
import com.igor.tempero.ui.componentes.EstadoVazio
import com.igor.tempero.ui.componentes.MensagemErro
import com.igor.tempero.ui.componentes.TituloTela
import com.igor.tempero.ui.componentes.ToastDeMensagem
import com.igor.tempero.ui.theme.CinzaTexto
import com.igor.tempero.ui.theme.Marrom

@Composable
fun TelaListaCompras(viewModel: ListaComprasViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ToastDeMensagem(uiState.mensagem) { viewModel.mensagemExibida() }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            TituloTela("Lista de compras", "${uiState.itens.size} itens • ${uiState.totalComprados} comprados")
        }
        item {
            LinearProgressIndicator(
                progress = { uiState.progresso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.novoItem,
                    onValueChange = { viewModel.onNovoItemChange(it) },
                    placeholder = { Text("Adicionar item...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { viewModel.adicionar() }, modifier = Modifier.height(56.dp)) { Text("+") }
            }
        }
        item { MensagemErro(uiState.erro) }

        if (uiState.itens.isEmpty()) {
            item { EstadoVazio("🛒", "Lista vazia", "Adicione itens aqui ou pela tela de uma receita.") }
        } else {
            items(uiState.itens, key = { it.id }) { item ->
                ItemDaLista(
                    item = item,
                    aoMarcar = { viewModel.alternarComprado(item) },
                    aoRemover = { viewModel.remover(item) }
                )
            }
            item {
                OutlinedButton(
                    onClick = { viewModel.limparComprados() },
                    enabled = uiState.totalComprados > 0,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("🧹 Limpar itens comprados") }
            }
        }
    }
}

@Composable
private fun ItemDaLista(item: ItemCompra, aoMarcar: () -> Unit, aoRemover: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = item.comprado, onCheckedChange = { aoMarcar() })
            Text(
                item.nome,
                color = if (item.comprado) CinzaTexto else Marrom,
                textDecoration = if (item.comprado) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = aoRemover) { Text("🗑") }
        }
    }
}
