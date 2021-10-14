package com.sito.bugs360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sito.bugs360.ui.theme.Bugs360Theme
import kotlin.math.roundToInt

val ImageList = arrayOf<Int>(
    R.drawable.ixodus_ricinus_a,
    R.drawable.ixodus_ricinus_b,
    R.drawable.ixodus_ricinus_c,
    R.drawable.ixodus_ricinus_d,
    R.drawable.ixodus_ricinus_e,
    R.drawable.ixodus_ricinus_f,
    R.drawable.ixodus_ricinus_g,
    R.drawable.ixodus_ricinus_h,
    R.drawable.ixodus_ricinus_i,
    R.drawable.ixodus_ricinus_j,
    R.drawable.ixodus_ricinus_k,
    R.drawable.ixodus_ricinus_l,
    R.drawable.ixodus_ricinus_m,
    R.drawable.ixodus_ricinus_n,
    R.drawable.ixodus_ricinus_o,
    R.drawable.ixodus_ricinus_p,
    R.drawable.ixodus_ricinus_q,
    R.drawable.ixodus_ricinus_r
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Bugs360Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Photo360View(ImageList)
                }
            }
        }
    }
}

@Composable
fun Photo360View(listOfImages: Array<Int>) {
    var currentImage by remember {
        mutableStateOf(listOfImages[0])
    }

    val beginningImage = listOfImages[0]

    var lastDelta by remember {
        mutableStateOf(0f)
    }

    var currentIndex = 0

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        if (scale < 1f) {
            scale = 1f
        }
    }

    Column() {
        Image(
            modifier = Modifier
                //drag
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (scale == 1f) {
                                scale = 2f
                            } else {
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            }
                        },
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        if (scale > 1f) {
                            change.consumeAllChanges()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        } else {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    }

                }
                // apply other transformations like rotation and zoom
                // on the pizza slice emoji
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                )
                // add transformable to listen to multitouch transformation events
                // after offset
                .transformable(state = state)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        if (scale == 1f) {
                            currentIndex = currentImage
                            lastDelta = delta
                            if (lastDelta > 0) {
                                currentIndex++
                            } else {
                                currentIndex--
                            }
                            if (currentIndex > listOfImages[listOfImages.size - 1])
                                currentIndex = beginningImage
                            else if (currentIndex < beginningImage)
                                currentIndex = listOfImages[listOfImages.size - 1]
                            currentImage = currentIndex
                            lastDelta = delta
                        }
                    }
                )
                .fillMaxSize()
                .padding(5.dp),
            contentDescription = "",
            painter = painterResource(id = currentImage)
        )
    }
}