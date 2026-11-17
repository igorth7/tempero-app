package com.igor.tempero.data.local

/** Receitas de exemplo que o Administrador pode carregar no Painel Admin. */
val receitasExemplo = listOf(
    Receita(
        nome = "Bolo de cenoura",
        categoria = "Doces",
        tempoMinutos = 50,
        porcoes = 10,
        ingredientes = "3 cenouras médias\n4 ovos\n1 xícara de óleo\n2 xícaras de açúcar\n2 xícaras de farinha de trigo\n1 colher de fermento",
        modoPreparo = "Bata no liquidificador a cenoura, os ovos e o óleo. Misture com o açúcar e a farinha, adicione o fermento e asse a 180 °C por 40 minutos."
    ),
    Receita(
        nome = "Strogonoff de frango",
        categoria = "Frango",
        tempoMinutos = 35,
        porcoes = 4,
        ingredientes = "500 g de peito de frango\n1 cebola\n1 caixa de creme de leite\n3 colheres de ketchup\n1 colher de mostarda",
        modoPreparo = "Refogue a cebola, doure o frango em cubos, junte ketchup e mostarda e finalize com o creme de leite.",
        favorita = true
    ),
    Receita(
        nome = "Lasanha da vó",
        categoria = "Massas",
        tempoMinutos = 60,
        porcoes = 6,
        ingredientes = "400 g de massa para lasanha\n500 g de carne moída\n2 xícaras de molho de tomate\n300 g de muçarela\n200 g de presunto",
        modoPreparo = "Monte camadas de massa, molho com carne, presunto e queijo. Leve ao forno por 40 minutos."
    ),
    Receita(
        nome = "Salada de grão-de-bico",
        categoria = "Saladas",
        tempoMinutos = 15,
        porcoes = 2,
        ingredientes = "1 lata de grão-de-bico\n1 tomate\n1/2 cebola roxa\nSalsinha\nAzeite e limão",
        modoPreparo = "Pique tudo, misture e tempere com azeite, limão e sal."
    ),
    Receita(
        nome = "Brigadeiro de colher",
        categoria = "Doces",
        tempoMinutos = 15,
        porcoes = 4,
        ingredientes = "1 lata de leite condensado\n2 colheres de cacau\n1 colher de manteiga",
        modoPreparo = "Leve tudo ao fogo baixo mexendo até desgrudar do fundo da panela.",
        favorita = true
    )
)
