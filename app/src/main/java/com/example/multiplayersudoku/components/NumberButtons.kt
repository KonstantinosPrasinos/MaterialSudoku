package com.example.multiplayersudoku.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3ExpressiveApi
@Composable
@Preview
fun NumberButtons(onNumberClick: (Int) -> Unit = {}, isPaused: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        (1..9).forEach { number ->
            Button(
                onClick = { onNumberClick(number) },
                shapes = ButtonDefaults.shapes(),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.weight(1f),
                enabled = !isPaused
            ) {
                Text(number.toString())
            }
        }
    }
}