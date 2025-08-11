// app/src/main/java/com/thor/swimtracker/MainActivity.kt
package com.thor.swimtracker

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
import com.thor.swimtracker.screens.HomeScreen
import com.thor.swimtracker.screens.AddSwimScreen
import com.thor.swimtracker.ui.theme.MyApplicationTheme

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
                        startDestination = stringResource(R.string.home),
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(getString(R.string.home)) {
                            HomeScreen(
                                onNavigate = { navController.navigate(getString(R.string.addswimscreen)) }
                            )
                        }
                        composable(getString(R.string.addswimscreen)) {
                            AddSwimScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
