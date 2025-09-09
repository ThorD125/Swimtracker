package com.thor.swim.tracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thor.swim.tracker.screens.HomeScreen
import com.thor.swim.tracker.screens.AddSwimScreen
import com.thor.swim.tracker.screens.TestScreen
import com.thor.swim.tracker.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
//                        startDestination = stringResource(R.string.screen_home),
                        startDestination = stringResource(R.string.screen_test),
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(getString(R.string.screen_home)) {
                            HomeScreen(
                                onNavigate = { navController.navigate(getString(R.string.screen_add_swim)) }
                            )
                        }
                        composable(getString(R.string.screen_add_swim)) {
                            AddSwimScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(getString(R.string.screen_test)) {
                            TestScreen()
                        }
                    }
                }
            }
        }
    }
}
