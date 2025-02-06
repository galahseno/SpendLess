package id.dev.spendless.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.main.navigation.NavigationRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpendLessTheme {
                val navController = rememberNavController()
                NavigationRoot(navController = navController)
            }
        }
    }
}