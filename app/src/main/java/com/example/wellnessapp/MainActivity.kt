package com.example.wellnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.wellnessapp.ui.theme.WellnessAppTheme
import kotlinx.coroutines.launch


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



enum class ExpandableCardState(val isExpanded: Boolean) {
    Expanded(true),
    Collapsed(false);

    operator fun not(): ExpandableCardState {
        return if(this.isExpanded) ExpandableCardState.Collapsed else ExpandableCardState.Expanded
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
    var currentCardState by remember { mutableStateOf(ExpandableCardState.Expanded) }
    var onExpandableButtonClick = {
        currentCardState = !currentCardState
    }

    var showDialog by remember { mutableStateOf(false) }
    var onToggleDialog = { showDialog = !showDialog }

    if(showDialog)
        HelpDialog(onDismissRequest = onToggleDialog)

    Scaffold (
        topBar = {
            WellnessAppTopBar(
                currentCardState = currentCardState,
                onClick = onExpandableButtonClick,
                onHelp = onToggleDialog
            )
        }
    ) {innerPadding ->
        OutlinedCard (
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = 24.dp,
                    start = 24.dp,
                    end = 24.dp
                )
        ) {
            MotivationalList(
                currentCardState = currentCardState,
                onExpandableButtonClick,
                modifier = Modifier
            )
        }

    }
}

@Composable
fun HelpDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text("Test")
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessAppTopBar(
    currentCardState: ExpandableCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onHelp: () -> Unit
) {
    TopAppBar(modifier = modifier,
        title = {
            Text(
                text = "30 Day Wellness",
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = onClick
            ) {
                when(currentCardState) {
                    ExpandableCardState.Expanded ->
                        Icon(painter = painterResource(id = R.drawable.collapse_card_24px),
                            contentDescription = "Expand All Cards")
                    ExpandableCardState.Collapsed ->
                        Icon(painter = painterResource(id = R.drawable.expand_card_24px),
                            contentDescription = "Collapse All Cards")
                }
            }
            IconButton(onClick = onHelp) {
                Icon(painter = painterResource(id = R.drawable.help_24px),
                    contentDescription = "Help")
            }

        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MotivationalList(currentCardState: ExpandableCardState,
                     onCardExpandClick: () -> Unit,
                     modifier: Modifier = Modifier) {

    val listState = rememberLazyListState()

    //https://stackoverflow.com/questions/71901039/snap-to-an-index-lazyrow/77005797#77005797
    val positionInLayout: Density.(Int, Int, Int) -> Int = { _, _, _ ->
        // This value tells where to snap on the x axis within the viewport
        // Setting it to 0 results in snapping of the first visible item to the left side (or right side if RTL)
        0
    }
    val snappingLayout = remember(listState) { SnapLayoutInfoProvider(lazyListState = listState, positionInLayout = positionInLayout) }
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        modifier = modifier
            .scale(if (listState.isScrollInProgress && currentCardState.isExpanded) 0.9f else 1f)
            .animateContentSize(),
        flingBehavior = flingBehavior,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(30) {index ->
            MotivationalCard(
                index,
                currentState = currentCardState,
                onCardClick = {
                    coroutineScope.launch {
                        if(listState.firstVisibleItemIndex != index)
                            listState.animateScrollToItem(index)
                        onCardExpandClick.invoke()
                    }
                },
                modifier = Modifier.fillHeightConditionally(currentCardState.isExpanded){
                    fillParentMaxHeight()
                }
            )
        }
    }
}

private fun Modifier.fillHeightConditionally(isExpanded: Boolean,
                                             fillParentMaxHeight: Modifier.() -> Modifier ): Modifier =
    if(isExpanded)
        this.fillParentMaxHeight() else this


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotivationalCard(index: Int, currentState: ExpandableCardState, onCardClick: () -> Unit, modifier: Modifier = Modifier) {

    Card (
        modifier = modifier
            .padding(16.dp),
        onClick = onCardClick
    ) {
        Column (
            modifier = Modifier
        ) {
            Row (
                modifier = Modifier.height(80.dp)
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .weight(3f)
                    .background(Color.Blue)
                ) {
                    Text(text = "Index: $index")
                }
                Spacer(modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.Red)
                )
            }
            AnimatedVisibility(visible = currentState.isExpanded) {
                Box(modifier = Modifier) {
                    Spacer(modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Yellow))
                }
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

