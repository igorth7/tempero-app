package com.igor.tempero.ui.receitas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igor.tempero.ui.componentes.MensagemErro
import com.igor.tempero.ui.theme.CinzaTexto
import com.igor.tempero.ui.theme.Marrom

private val categoriasSugeridas = listOf("Doces", "Massas", "Carnes", "Frango", "Saladas", "Lanches", "Bebidas", "Café da manhã")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFormReceita(
    viewModel: ReceitasViewModel,
    aoVoltar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val form = uiState.form

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = aoVoltar) { Text("←", fontSize = 22.sp) }
            Text(form.titulo, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Marrom)
        }

        OutlinedTextField(
            value = form.nome,
            onValueChange = { viewModel.onFormChange(form.copy(nome = it)) },
            label = { Text("Nome da receita") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Categoria", fontWeight = FontWeight.SemiBold, color = Marrom)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categoriasSugeridas) { categoria ->
                FilterChip(
                    selected = form.categoria == categoria,
                    onClick = { viewModel.onFormChange(form.copy(categoria = categoria)) },
                    label = { Text(categoria) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = form.tempo,
                onValueChange = { viewModel.onFormChange(form.copy(tempo = it.filter { c -> c.isDigit() })) },
                label = { Text("Tempo (min)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = form.porcoes,
                onValueChange = { viewModel.onFormChange(form.copy(porcoes = it.filter { c -> c.isDigit() })) },
                label = { Text("Porções") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = form.ingredientes,
            onValueChange = { viewModel.onFormChange(form.copy(ingredientes = it)) },
            label = { Text("Ingredientes") },
            supportingText = { Text("Um ingrediente por linha", color = CinzaTexto) },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.modoPreparo,
            onValueChange = { viewModel.onFormChange(form.copy(modoPreparo = it)) },
            label = { Text("Modo de preparo") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        MensagemErro(form.erro)

        Button(
            onClick = { viewModel.salvar(aoSalvar = aoVoltar) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(form.textoBotao, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
