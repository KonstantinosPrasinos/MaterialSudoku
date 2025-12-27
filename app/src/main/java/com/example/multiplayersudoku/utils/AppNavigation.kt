package com.example.multiplayersudoku.utils

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.multiplayersudoku.classes.Difficulty
import com.example.multiplayersudoku.classes.GameSettings
import com.example.multiplayersudoku.views.MainView
import com.example.multiplayersudoku.views.ProfileView
import com.example.multiplayersudoku.views.SudokuView.SudokuView
import com.example.multiplayersudoku.views.statisticsView.StatisticsView

enum class AppView {
    MAIN,
    SUDOKU_VIEW,
    PROFILE_VIEW,
    STATISTICS_VIEW
}

@Composable
fun AppNavigation() {
    // The NavController manages the navigation state and screen stack
    val navController = rememberNavController()

    val DIFFICULTY_ARG = "difficulty"
    val MISTAKES_ARG = "mistakes"
    val HINTS_ARG = "hints"

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
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            MainView(
                onNavigateToSudoku = { gameSettings ->
                    navController.navigate("${AppView.SUDOKU_VIEW.name}/${gameSettings.difficulty.name}/${gameSettings.mistakes}/${gameSettings.hints}")
                },
                onNavigateToProfile = {
                    navController.navigate(AppView.STATISTICS_VIEW.name)
                },
            )
        }

        // Screen 2: SudokuView
        composable(
            route = "${AppView.SUDOKU_VIEW.name}/{$DIFFICULTY_ARG}/{$MISTAKES_ARG}/{$HINTS_ARG}",
            arguments = listOf(
                navArgument(DIFFICULTY_ARG) { type = NavType.StringType },
                navArgument(MISTAKES_ARG) { type = NavType.IntType },
                navArgument(HINTS_ARG) { type = NavType.IntType }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val difficultyString = backStackEntry.arguments?.getString(DIFFICULTY_ARG)
            val mistakes = backStackEntry.arguments?.getInt(MISTAKES_ARG) ?: 3
            val hints = backStackEntry.arguments?.getInt(HINTS_ARG) ?: 5

            val difficultyObject = when {
                difficultyString == "EASY" -> Difficulty.EASY
                difficultyString == "MEDIUM" -> Difficulty.MEDIUM
                difficultyString == "HARD" -> Difficulty.HARD
                else -> Difficulty.EASY
            }

            val gameSettings = GameSettings()
            gameSettings.difficulty = difficultyObject
            gameSettings.mistakes = mistakes
            gameSettings.hints = hints

            SudokuView(
                onBack = {
                    navController.popBackStack()
                },
                gameSettings = gameSettings
            )
        }

        composable(
            route = AppView.PROFILE_VIEW.name,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            ProfileView(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToStatistics = {
                    navController.navigate(AppView.STATISTICS_VIEW.name)
                }
            )
        }

        composable(
            route = AppView.STATISTICS_VIEW.name,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            StatisticsView(
                onBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}