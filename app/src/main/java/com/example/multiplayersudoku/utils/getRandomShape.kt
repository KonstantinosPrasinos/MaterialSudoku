package com.example.multiplayersudoku.utils

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.graphics.shapes.RoundedPolygon
import generateRandomNumber

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun getRandomShape(): Shape {
    val allShapes = listOf(
        MaterialShapes.Gem,
        MaterialShapes.Sunny,
        MaterialShapes.Triangle,
        MaterialShapes.Slanted,
        MaterialShapes.Arch,
        MaterialShapes.Fan,
        MaterialShapes.Arrow,
        MaterialShapes.SemiCircle,
        MaterialShapes.Oval,
        MaterialShapes.Pill,
        MaterialShapes.Diamond,
        MaterialShapes.ClamShell,
        MaterialShapes.Pentagon,
        MaterialShapes.SoftBurst,
        MaterialShapes.Burst,
        MaterialShapes.Boom,
        MaterialShapes.Bun,
        MaterialShapes.Circle,
        MaterialShapes.Clover4Leaf,
        MaterialShapes.Cookie7Sided
    )

    val index = generateRandomNumber(0, allShapes.size - 1)
    val shape = allShapes[index]

    return shape.toShape();
}