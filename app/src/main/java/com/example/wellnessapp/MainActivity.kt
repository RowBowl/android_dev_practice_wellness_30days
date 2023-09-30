package com.example.wellnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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



enum class ExpandableCardState(val expandedAmount: Float) {
    Expanded(1f),
    Halfway(0.5f),
    Collapsed(0f);

    override fun toString(): String {
        return when(this) {
            Expanded -> "Expanded"
            Halfway -> "Halfway"
            Collapsed -> "Collapsed"
        }
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

    val listState = rememberLazyListState()
    var showDialog by remember { mutableStateOf(false) }
    var allCollapsed by remember { mutableStateOf(false) }
    var onToggleDialog = { showDialog = !showDialog }
    var onCollapse = {allCollapsed = !allCollapsed}

    if(showDialog)
        HelpDialog(onDismissRequest = onToggleDialog)

    Scaffold (
        topBar = {
            WellnessAppTopBar(
                collapseState = allCollapsed,
                onHelp = onToggleDialog,
                onCollapse = onCollapse
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
            Text("All COllapsed: $allCollapsed")

            MotivationalList(
                listState = listState,
                isAllCollapsed = allCollapsed,
                onCollapse = onCollapse,
                modifier = Modifier
            )
        }

    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MotivationalList(listState: LazyListState = rememberLazyListState(),
                     isAllCollapsed: Boolean,
                     modifier: Modifier = Modifier,
                     onCollapse: () -> Unit) {

    //val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    val centerIndex by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.run {
                val firstVisibleIndex = listState.firstVisibleItemIndex
                if(isEmpty()) -1 else firstVisibleIndex + (last().index - firstVisibleIndex) / 2
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        items(30) {index ->

            val isCenterItem by remember {
                derivedStateOf {
                    index == centerIndex
                }
            }
            val currentExpandableState by remember {
                //TODO: Calculation of center is happening after state is changed to Halfway
                derivedStateOf {
                    if(isAllCollapsed)
                        ExpandableCardState.Collapsed
                    else{
                        if(listState.isScrollInProgress){
                            ExpandableCardState.Halfway
                        }
                        else if(isCenterItem)
                            ExpandableCardState.Expanded
                        else
                            ExpandableCardState.Collapsed
                    }
                }
            }
            val scale by animateFloatAsState(targetValue = if(isCenterItem) 1.1f else 1f, label = "")

            MotivationalCard(
                index,
                listState,
                currentExpandableState,
                isCenterItem,
                onCardClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(
                            if (isAllCollapsed)
                                index - 4 //TODO: play around with offset
                            else
                                index - 1
                        )
                    }
                },
                modifier = Modifier
                    .scale(scale = scale)
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotivationalCard(index: Int, listState: LazyListState,
                     currentExpandableState: ExpandableCardState,
                     isCenterItem: Boolean,
                     onCardClick: () -> Unit,
                     modifier: Modifier = Modifier) {

    Card (
        modifier = modifier
            .padding(16.dp)
            .border(if (currentExpandableState == ExpandableCardState.Expanded) 2.dp else 0.dp, Color.Magenta),
        onClick = onCardClick
    ) {
        Column (
            modifier = Modifier
        ) {
            Text(text = "Index: $index, State: $currentExpandableState",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(8.dp)

            )
            AnimatedContent(targetState = currentExpandableState, label = "") { state ->
                if(state ==  ExpandableCardState.Expanded || state == ExpandableCardState.Collapsed) {
                    AnimatedVisibility(visible = currentExpandableState != ExpandableCardState.Collapsed) {
                        Text(text = stringResource(R.string.example_body_string),
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Blue)
                                .padding(8.dp)
                        )
                    }
                }
                else {
                    AnimatedVisibility(visible = currentExpandableState != ExpandableCardState.Collapsed) {
                        Text(text = stringResource(R.string.example_body_string),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.Blue)
                                .padding(8.dp)
                        )
                    }
                }
            }
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
            shape = MaterialTheme.shapes.medium,
        ) {
            Text("Test")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessAppTopBar(
    modifier: Modifier = Modifier,
    onHelp: () -> Unit,
    onCollapse: () -> Unit,
    collapseState: Boolean
) {
    TopAppBar(modifier = modifier,
        title = {
            Text(
                text = "30 Day Wellness",
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = onCollapse)//onClick
            {
                AnimatedContent(targetState = collapseState, label = "") { state ->
                    if(state)
                        Icon(painter = painterResource(id = R.drawable.collapse_card_24px),
                        contentDescription = "Expand All Cards")
                    else
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




@Composable
fun MotivationalCardDescription(state: ExpandableCardState) {
    when(state) {
        ExpandableCardState.Expanded ->
            Box(modifier = Modifier) {
            Spacer(modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow))
        }
        ExpandableCardState.Halfway -> {
            Box(modifier = Modifier) {
                Spacer(modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .background(Color.Yellow))}
        }
        ExpandableCardState.Collapsed -> { }
    }

}


