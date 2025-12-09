package com.example.multiplayersudoku.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.multiplayersudoku.classes.Difficulty
import com.example.multiplayersudoku.views.MainView
import com.example.multiplayersudoku.views.SudokuView.SudokuView

enum class AppView {
    MAIN,
    SUDOKU_VIEW
}

@Composable
fun AppNavigation() {
    // The NavController manages the navigation state and screen stack
    val navController = rememberNavController()

    val DIFFICULTY_ARG = "difficulty"

    // NavHost links the NavController to a navigation graph
    NavHost(
        navController = navController,
        startDestination = AppView.MAIN.name
    ) {
        // Screen 1: Main
        composable(
            route = AppView.MAIN.name,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            }
        ) {
            MainView(
                onNavigateToSudoku = { difficulty ->
                    navController.navigate("${AppView.SUDOKU_VIEW.name}/${difficulty.name}")
                }
            )
        }

        // Screen 2: SudokuView
        composable(
            route = "${AppView.SUDOKU_VIEW.name}/{$DIFFICULTY_ARG}",
            arguments = listOf(navArgument(DIFFICULTY_ARG) { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            }
        ) { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString(DIFFICULTY_ARG)

            val difficultyObject = when {
                difficulty == "EASY" -> Difficulty.EASY
                difficulty == "MEDIUM" -> Difficulty.MEDIUM
                difficulty == "HARD" -> Difficulty.HARD
                else -> Difficulty.EASY
            }
            SudokuView(
                onBack = {
                    navController.popBackStack()
                },
                difficulty = difficultyObject
            )
        }
    }
}