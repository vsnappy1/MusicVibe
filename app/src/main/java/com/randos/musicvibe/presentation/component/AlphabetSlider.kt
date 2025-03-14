package com.randos.musicvibe.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AlphabetSlider(
    modifier: Modifier = Modifier,
    onSelectionChange: (Char) -> Unit,
    onSelectionChangeFinished: () -> Unit
) {
    val alphabets = ('A'..'Z').toList()
    var heightOfSingleItem by remember { mutableIntStateOf(0) }
    var selectedAlphabet by remember { mutableStateOf("A") }
    var isLargeAlphabetVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val positionOfLargeAlphabet = (selectedAlphabet[0].code - 65) * 18.4

    Row(modifier = modifier) {
        AnimatedVisibility(
            isLargeAlphabetVisible,
            enter = EnterTransition.None,
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp, top = positionOfLargeAlphabet.dp)
                    .size(60.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = selectedAlphabet,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), CircleShape)
                .padding(horizontal = 1.dp)
                .onGloballyPositioned { coordinate ->
                    heightOfSingleItem = coordinate.size.height / alphabets.size
                }
                .pointerInput(Unit) {
                    /**
                     * Detect vertical drag gesture on column, when drag starts enable flag to show
                     * large alphabet and disable when drag gesture ends.
                     */
                    /**
                     * Detect vertical drag gesture on column, when drag starts enable flag to show
                     * large alphabet and disable when drag gesture ends.
                     */
                    /**
                     * Detect vertical drag gesture on column, when drag starts enable flag to show
                     * large alphabet and disable when drag gesture ends.
                     */

                    /**
                     * Detect vertical drag gesture on column, when drag starts enable flag to show
                     * large alphabet and disable when drag gesture ends.
                     */
                    detectVerticalDragGestures(
                        onDragStart = { isLargeAlphabetVisible = true },
                        onDragEnd = {
                            coroutineScope.launch {
                                /**
                                 * Allow some delay for smooth animation
                                 */
                                /**
                                 * Allow some delay for smooth animation
                                 */
                                /**
                                 * Allow some delay for smooth animation
                                 */

                                /**
                                 * Allow some delay for smooth animation
                                 */
                                delay(250)
                                isLargeAlphabetVisible = false
                                onSelectionChangeFinished()
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                /**
                                 * Allow some delay for smooth animation
                                 */
                                /**
                                 * Allow some delay for smooth animation
                                 */
                                /**
                                 * Allow some delay for smooth animation
                                 */

                                /**
                                 * Allow some delay for smooth animation
                                 */
                                delay(250)
                                isLargeAlphabetVisible = false
                                onSelectionChangeFinished()
                            }
                        },
                        onVerticalDrag = { change, _ ->
                            /**
                             * Find the index of item user currently pointing to by dividing the
                             * offset.y with height of single item.
                             */
                            /**
                             * Find the index of item user currently pointing to by dividing the
                             * offset.y with height of single item.
                             */
                            /**
                             * Find the index of item user currently pointing to by dividing the
                             * offset.y with height of single item.
                             */

                            /**
                             * Find the index of item user currently pointing to by dividing the
                             * offset.y with height of single item.
                             */
                            val itemIndex = ((change.position.y) / heightOfSingleItem).toInt()

                            /**
                             * Get the alphabet based on the index.
                             */
                            /**
                             * Get the alphabet based on the index.
                             */
                            /**
                             * Get the alphabet based on the index.
                             */

                            /**
                             * Get the alphabet based on the index.
                             */
                            val alphabet =
                                if (itemIndex < 0) 'A' else if (itemIndex > 25) 'Z' else (65 + itemIndex).toChar() // Added 65 to itemIndex to ensure proper ASCII code

                            /**
                             * Do the assignment and invoke onSelectionChange only when user has
                             * scrolled to a new alphabet.
                             */

                            /**
                             * Do the assignment and invoke onSelectionChange only when user has
                             * scrolled to a new alphabet.
                             */

                            /**
                             * Do the assignment and invoke onSelectionChange only when user has
                             * scrolled to a new alphabet.
                             */

                            /**
                             * Do the assignment and invoke onSelectionChange only when user has
                             * scrolled to a new alphabet.
                             */
                            if (selectedAlphabet != alphabet.toString()) {
                                selectedAlphabet = alphabet.toString()
                                onSelectionChange(alphabet)
                            }
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            alphabets.forEach { alphabet ->
                Text(
                    text = "$alphabet",
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures { _ ->
                            /**
                             * Detect tap gesture
                             */
                            /**
                             * Detect tap gesture
                             */

                            /**
                             * Detect tap gesture
                             */
                            /**
                             * Detect tap gesture
                             */
                            /**
                             * Detect tap gesture
                             */
                            /**
                             * Detect tap gesture
                             */
                            /**
                             * Detect tap gesture
                             */

                            /**
                             * Detect tap gesture
                             */
                            selectedAlphabet = alphabet.toString()
                            onSelectionChange(alphabet)
                            isLargeAlphabetVisible = true
                            coroutineScope.launch {
                                /**
                                 * Allow some delay for smooth animation
                                 */
                                /**
                                 * Allow some delay for smooth animation
                                 */

                                /**
                                 * Allow some delay for smooth animation
                                 */
                                /**
                                 * Allow some delay for smooth animation
                                 */
                                /**
                                 * Allow some delay for smooth animation
                                 */
                                /**
                                 * Allow some delay for smooth animation
                                 */
                                /**
                                 * Allow some delay for smooth animation
                                 */

                                /**
                                 * Allow some delay for smooth animation
                                 */
                                delay(250)
                                isLargeAlphabetVisible = false
                                onSelectionChangeFinished()
                            }
                        }
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAlphabetSlider() {
    AlphabetSlider(onSelectionChange = {},
        onSelectionChangeFinished = {})
}