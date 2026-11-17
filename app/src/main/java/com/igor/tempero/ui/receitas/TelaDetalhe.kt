package com.igor.tempero.ui.receitas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igor.tempero.data.local.Receita
import com.igor.tempero.ui.compras.ListaComprasViewModel
import com.igor.tempero.ui.componentes.EstadoVazio
import com.igor.tempero.ui.componentes.Etiqueta
import com.igor.tempero.ui.componentes.emojiCategoria
import com.igor.tempero.ui.theme.CinzaTexto
import com.igor.tempero.ui.theme.LaranjaClaro
import com.igor.tempero.ui.theme.Marrom
import com.igor.tempero.ui.theme.VermelhoErro

@Composable
fun TelaDetalhe(
    receitaId: Int,
    viewModel: ReceitasViewModel,
    listaComprasViewModel: ListaComprasViewModel,
    aoVoltar: () -> Unit,
    aoEditar: (Receita) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val receita = uiState.todas.find { it.id == receitaId }

    if (receita == null) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            EstadoVazio("🤷", "Receita não encontrada", "Ela pode ter sido excluída.")
            Button(onClick = aoVoltar) { Text("Voltar") }
        }
        return
    }

    val context = LocalContext.current
    val ingredientes = receita.listaIngredientes()
    // Estado local: quais ingredientes o usuário já separou (muda a tela em tempo real)
    val marcados = remember(receita.id) { mutableStateListOf(*Array(ingredientes.size) { false }) }
    var mostrarConfirmacao by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { CabecalhoDetalhe(receita, aoVoltar) { viewModel.alternarFavorita(receita) } }

        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Etiqueta(receita.categoria)
                Text(receita.nome, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Marrom)
                Text("Origem: ${receita.origem}", fontSize = 13.sp, color = CinzaTexto)
            }
        }

        item {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoReceita("⏱", "${receita.tempoMinutos} min", "Tempo", Modifier.weight(1f))
                InfoReceita("🍽", "${receita.porcoes}", "Porções", Modifier.weight(1f))
                InfoReceita("🧺", "${ingredientes.size}", "Ingredientes", Modifier.weight(1f))
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ingredientes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Marrom)
                Text("${marcados.count { it }} de ${ingredientes.size} separados", fontSize = 12.sp, color = CinzaTexto)
            }
        }

        itemsIndexed(ingredientes) { indice, ingrediente ->
            ItemIngrediente(
                texto = ingrediente,
                marcado = marcados.getOrElse(indice) { false },
                aoMarcar = { marcados[indice] = !marcados[indice] }
            )
        }

        item {
            OutlinedButton(
                onClick = {
                    listaComprasViewModel.adicionarIngredientes(ingredientes)
                    Toast.makeText(context, "Ingredientes enviados para a lista de compras 🛒", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) { Text("🛒 Adicionar à lista de compras") }
        }

        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text("Modo de preparo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Marrom)
                Text(
                    receita.modoPreparo.ifBlank { "Sem modo de preparo cadastrado." },
                    fontSize = 15.sp,
                    color = Marrom,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(onClick = { aoEditar(receita) }, modifier = Modifier.weight(1f)) { Text("✏️ Editar") }
                OutlinedButton(onClick = { mostrarConfirmacao = true }, modifier = Modifier.weight(1f)) {
                    Text("🗑 Excluir", color = VermelhoErro)
                }
            }
        }
    }

    if (mostrarConfirmacao) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacao = false },
            title = { Text("Excluir receita?") },
            text = { Text("\"${receita.nome}\" será removida do seu caderno.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmacao = false
                    viewModel.excluir(receita)
                    aoVoltar()
                }) { Text("Excluir", color = VermelhoErro) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacao = false }) { Text("Cancelar") }
            }
        )
    }
}

/** Topo da tela: Box com fundo colorido, emoji grande e botões por cima. */
@Composable
private fun CabecalhoDetalhe(receita: Receita, aoVoltar: () -> Unit, aoFavoritar: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(LaranjaClaro)
    ) {
        Text(emojiCategoria(receita.categoria), fontSize = 88.sp, modifier = Modifier.align(Alignment.Center))
        BotaoRedondo("←", Modifier.align(Alignment.TopStart).padding(16.dp), aoVoltar)
        BotaoRedondo(if (receita.favorita) "❤️" else "🤍", Modifier.align(Alignment.TopEnd).padding(16.dp), aoFavoritar)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BotaoRedondo(texto: String, modifier: Modifier, aoClicar: () -> Unit) {
    Surface(
        onClick = aoClicar,
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Text(texto, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
    }
}

@Composable
private fun InfoReceita(emoji: String, valor: String, rotulo: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 20.sp)
            Text(valor, fontWeight = FontWeight.Bold, color = Marrom)
            Text(rotulo, fontSize = 11.sp, color = CinzaTexto)
        }
    }
}

@Composable
private fun ItemIngrediente(texto: String, marcado: Boolean, aoMarcar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = marcado, onCheckedChange = { aoMarcar() })
            Text(
                texto,
                color = if (marcado) CinzaTexto else Marrom,
                textDecoration = if (marcado) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.padding(end = 12.dp)
            )
        }
    }
}
