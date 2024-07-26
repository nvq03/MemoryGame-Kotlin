package com.example.memorygame.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memorygame.Model.Card
import com.example.memorygame.R

import kotlinx.coroutines.delay

@Composable
fun MemoryCardGameScreen(modifier: Modifier = Modifier) {
    val images = listOf(
        ImageVector.vectorResource(R.drawable.apple),
        ImageVector.vectorResource(R.drawable.banana),
        ImageVector.vectorResource(R.drawable.cherry),
        ImageVector.vectorResource(R.drawable.grape),
        ImageVector.vectorResource(R.drawable.kiwi),
        ImageVector.vectorResource(R.drawable.mango),
//        ImageVector.vectorResource(R.drawable.orange),
//        ImageVector.vectorResource(R.drawable.pear),
//        ImageVector.vectorResource(R.drawable.pineapple),
//        ImageVector.vectorResource(R.drawable.plum),
//        ImageVector.vectorResource(R.drawable.strawberry),
//        ImageVector.vectorResource(R.drawable.watermelon)
    )

    val cardImages = images + images
    var shuffledCards = cardImages.shuffled().mapIndexed {
            index, image -> Card(index, image)
    }

    var cards by remember { mutableStateOf(shuffledCards) }
    var selectedCards by remember { mutableStateOf<List<Card>>(emptyList()) }
    var isCheckingMatch by remember { mutableStateOf(false) }

    var score by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedCards) {
        if (selectedCards.size == 2) {
            isCheckingMatch = true
            delay(500)
            if (selectedCards[0].image == selectedCards[1].image) {
                score += 20
                cards = cards.map {
                    if (it.id == selectedCards[0].id || it.id == selectedCards[1].id)
                        it.copy(isVisible = false)
                    else
                        it
                }
                selectedCards = emptyList()

                if (cards.none { it.isVisible }) {
                    shuffledCards = createShuffledCards(images)
                    cards = shuffledCards
                    selectedCards = emptyList()
                    isCheckingMatch = false
                    score = 0
                }

            } else {
                cards = cards.map {
                    if (it.id == selectedCards[0].id || it.id == selectedCards[1].id)
                        it.copy(isRevealed = false)
                    else
                        it
                }
                selectedCards = emptyList()
            }
            isCheckingMatch = false
        }
    }

    fun onCardClick(card: Card) {
        if (selectedCards.size == 2 || card.isRevealed || isCheckingMatch || !card.isVisible) return

        val updateCards = cards.map {
            if (it.id == card.id)
                it.copy(isRevealed = true)
            else
                it
        }

        cards = updateCards
        selectedCards = selectedCards + card
    }


    fun restartGame() {
        shuffledCards = createShuffledCards(images)
        cards = shuffledCards
        selectedCards = emptyList()
        isCheckingMatch = false
        score = 0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.bg),
                contentScale = ContentScale.FillBounds
            ),
                verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))

        Text(
            text = "Memory Card",
            style = TextStyle(
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = "Score: $score",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(painter = painterResource(id = R.drawable.coin), contentDescription = "", modifier = Modifier.height(30.dp).width(30.dp))
            }

            Spacer(Modifier.height(20.dp))

            TextButton(onClick = { restartGame() }, modifier = Modifier.background(
                color = Color.Yellow, RoundedCornerShape(10.dp)
            )) {
                Text(text = "Restart",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                )
            }
        }

        Spacer(Modifier.height(30.dp))

        Grid(
            columns = 3,
            cards = cards,
            onCardClick = ::onCardClick
        )
    }
}
fun createShuffledCards(images: List<ImageVector>): List<Card> {
    val cardImages = images + images
    return cardImages.shuffled().mapIndexed { index, image -> Card(index, image) }
}

@Composable
fun Grid(
    columns: Int,
    cards: List<Card>,
    modifier: Modifier = Modifier,
    onCardClick: (Card) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(cards) { card ->
            if (card.isVisible) {
                CardView(card, onCardClick)
            } else {
                Spacer(modifier = Modifier
                    .height(100.dp)
                    .width(60.dp))
            }
        }
    }
}
