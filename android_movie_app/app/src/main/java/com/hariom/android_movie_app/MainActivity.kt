package com.hariom.android_movie_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.hariom.android_movie_app.navigation.MovieNavigation
import com.hariom.android_movie_app.ui.theme.Android_movie_appTheme

/**
 * Main activity for the Movie Showcase app
 * Sets up the navigation and theme
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Android_movie_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    MovieNavigation()
                }
            }
        }
    }
}