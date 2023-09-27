package com.example.wellnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wellnessapp.ui.theme.WellnessAppTheme


/** App Design Notes
 * Theme: Wellness (encompasses fitness, physical and mental health)
 * The app will have a collection of motivational cards. Each card contains
 * text for the title and body. The body will be a short 2-3 paragraph description.
 * Instead of an image, I will have a background simple icon or image that relates
 * to the contents of the card.
 *
 * Card takes up most of the screen. Swiping up or down reveals new cards (much like tiktoks)
 *
 * can experiment with shader gradient backgrounds for cards
 *
 * can make it so that the list snaps to the "current" day item on app open
 *
 * use LazyColumn for vertical scrolling list
 *
 * test using dark theme preview ****
 *
 * Font: Using very light font for message body (Roboto light). Using Nunito Sans for title
 *
 * Theme: Chosen wth material theme builder
 *
 * Shapes: Rounded corner outline card for the "view port" . Within this view port, dragging up
 * or down scrolls the lazy column up or down respectivly. Actual list items are also cards that
 * fit perfectly on the "view port".
 *
 * Animations: After all parts of the design are programmed into the app, I will add animations
 * to expand or collapse the card element information. Or maybe a "tap to reveal" feature.
 *
 * **** Furthermore, when the user drags the screen vertically, the individual items of the
 * lazy list should shrink (and perhaps the parent as well), to show a kind of "spinning wheel"
 * selector.
 *
 *
 * Bottom app bar included. It will include icon buttons to apply things to currently viewing
 * item. Buttons for "Mark as done", or perhaps editing.
 */



enum class ArrowState(val isExpanded: Boolean) {
    Down(true),
    Up(false);

    operator fun not(): ArrowState {
        return if(this.isExpanded) ArrowState.Up else ArrowState.Down
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WellnessAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WellnessApp()
                }
            }
        }
    }
}

@Preview
@Composable
fun WellnessAppPreview() {
    WellnessAppTheme (useDarkTheme = false) {
        WellnessApp()
    }
}

@Preview
@Composable
fun WellnessAppDarkThemePreview() {
    WellnessAppTheme (useDarkTheme = true) {
        WellnessApp()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessApp() {
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(
                    text = "30 Day Wellness",
                    style = MaterialTheme.typography.titleLarge)
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) {innerPadding ->
        OutlinedCard (
            modifier = Modifier
                .fillMaxSize()
                //needed so last item not cut off by bottom bar
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = 24.dp,
                    start = 24.dp,
                    end = 24.dp
                )
        ) {
            MotivationalList("Hello World",
                modifier = Modifier)
        }

    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MotivationalList(displayString: String, modifier: Modifier = Modifier) {

    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    var currentState by remember { mutableStateOf(ArrowState.Down) }

    LazyColumn(
        state = listState,
        modifier = modifier
            .scale(if (listState.isScrollInProgress && currentState.isExpanded) 0.9f else 1f)
            .animateContentSize(),
        flingBehavior = flingBehavior,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(30) {index ->
            MotivationalCard(
                index = index,
                currentState = currentState,
                onClick = {
                    currentState = !currentState
                },
                modifier = Modifier
                    .conditional(currentState.isExpanded) {
                        fillParentMaxHeight()
                    }
                    .conditional(!currentState.isExpanded) {
                        wrapContentHeight()
                    }
                    .animateContentSize()
            )
        }
    }
}


@Composable
fun MotivationalCard(index: Int, currentState: ArrowState,
    onClick: () -> Unit, modifier: Modifier = Modifier) {

    Card (modifier = modifier) {
        when(currentState) {
            ArrowState.Down -> {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Column (
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Title: Index #${index + 1}",
                            style = MaterialTheme.typography.displayLarge,
                        )
                        Text(
                            stringResource(R.string.motivational_card_body_example),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)

                        )
                    }

                    Row (
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        BottomButtonRow(currentState,
                            onClick)
                    }
                }
            }
            ArrowState.Up -> {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        "Title: Index #${index + 1}",
                        style = MaterialTheme.typography.displayLarge,
                    )
                    Row (
                        modifier = Modifier
                    ) {
                        BottomButtonRow(currentState,
                            onClick = onClick)
                    }
                }
            }
        }
    }
}

@Composable
fun CollapsableCard(currentState: ArrowState, cardTitle: String, onClick: () -> Unit) {


}
@Composable
fun CollapsedCard() {
    TODO("Not yet implemented")
}



@Composable
fun BottomButtonRow(currentState: ArrowState, onClick: () -> Unit, modifier: Modifier = Modifier) {

    val transition = updateTransition(currentState, label = "Arrow State")
    val rotation by transition.animateFloat(label = "rotation") {state ->
        when(state) {
            ArrowState.Down -> 0f
            ArrowState.Up -> -180f
        }
    }

    OutlinedCard (
        modifier = modifier
    ) {
        Row (
            modifier = Modifier
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Outlined.Check, contentDescription = "Mark Complete Icon",
                modifier = Modifier.size(36.dp))
            Icon(Icons.Outlined.Star, contentDescription = "Mark Favorite Icon",
                modifier = Modifier.size(36.dp))
            Button(
               onClick = onClick
            ) {
                Icon(Icons.Outlined.ArrowDropDown, contentDescription = "Mark Favorite Icon",
                    modifier = Modifier
                        .size(36.dp)
                        .rotate(rotation)
                )
            }
        }
    }
}

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

