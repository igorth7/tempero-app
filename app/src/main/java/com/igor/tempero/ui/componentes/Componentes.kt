package com.igor.tempero.ui.componentes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.igor.tempero.data.local.Receita
import com.igor.tempero.ui.theme.CinzaTexto
import com.igor.tempero.ui.theme.Laranja
import com.igor.tempero.ui.theme.LaranjaClaro
import com.igor.tempero.ui.theme.Linha
import com.igor.tempero.ui.theme.Marrom
import com.igor.tempero.ui.theme.VermelhoErro

/** Emoji de cada categoria (usado no lugar de fotos). */
fun emojiCategoria(categoria: String): String = when (categoria.lowercase()) {
    "doces" -> "🍰"
    "massas" -> "🍝"
    "frango" -> "🍗"
    "carnes" -> "🥩"
    "saladas", "vegetariana" -> "🥗"
    "frutos do mar" -> "🦐"
    "café da manhã" -> "🥞"
    "bebidas" -> "🥤"
    "lanches" -> "🥪"
    else -> "🍲"
}

/** Mostra um Toast quando chega uma mensagem no UiState e avisa o ViewModel. */
@Composable
fun ToastDeMensagem(mensagem: String?, aoExibir: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(mensagem) {
        if (mensagem != null) {
            Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show()
            aoExibir()
        }
    }
}

/** Card de receita usado na lista da Início. */
@Composable
fun CardReceita(
    receita: Receita,
    aoClicar: () -> Unit,
    aoFavoritar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { aoClicar() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ImagemCategoria(receita.categoria, tamanho = 64)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    receita.nome,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Marrom,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("⏱ ${receita.tempoMinutos} min  •  ${receita.porcoes} porções", fontSize = 13.sp, color = CinzaTexto)
                Etiqueta(receita.categoria)
            }
            TextButton(onClick = aoFavoritar) {
                Text(if (receita.favorita) "❤️" else "🤍", fontSize = 20.sp)
            }
        }
    }
}

/** Quadrado arredondado com o emoji da categoria (Box centralizando o conteúdo). */
@Composable
fun ImagemCategoria(categoria: String, tamanho: Int) {
    Box(
        modifier = Modifier
            .size(tamanho.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(LaranjaClaro),
        contentAlignment = Alignment.Center
    ) {
        Text(emojiCategoria(categoria), fontSize = (tamanho / 2).sp)
    }
}

/** Pequena etiqueta arredondada (ex: categoria). */
@Composable
fun Etiqueta(texto: String) {
    Text(
        texto,
        modifier = Modifier
            .padding(top = 4.dp)
            .clip(CircleShape)
            .background(LaranjaClaro)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        color = Laranja,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
    )
}

/** Card de número (usado em Perfil e Admin). */
@Composable
fun CardNumero(valor: String, rotulo: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(androidx.compose.ui.graphics.Color.White)
            .border(1.dp, Linha, RoundedCornerShape(14.dp))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Laranja)
        Text(rotulo, fontSize = 12.sp, color = CinzaTexto)
    }
}

@Composable
fun MensagemErro(texto: String?) {
    if (texto != null) {
        Text(
            "⚠️ $texto",
            color = VermelhoErro,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
fun EstadoVazio(emoji: String, titulo: String, descricao: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 48.sp)
        Text(titulo, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Marrom, modifier = Modifier.padding(top = 8.dp))
        Text(descricao, color = CinzaTexto, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun TituloTela(titulo: String, subtitulo: String? = null) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Marrom)
        if (subtitulo != null) Text(subtitulo, fontSize = 14.sp, color = CinzaTexto)
    }
}
