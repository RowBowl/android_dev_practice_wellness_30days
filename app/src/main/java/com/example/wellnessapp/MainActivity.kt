package com.example.wellnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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
 */




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
                }
            }
        }
    }
}
