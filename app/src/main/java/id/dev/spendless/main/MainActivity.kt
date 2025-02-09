package id.dev.spendless.main

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.main.navigation.NavigationRoot
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.state.isCheckingAuth
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.4f,
                    0.0f
                ).apply {
                    interpolator = OvershootInterpolator()
                    duration = 500L
                    doOnEnd { screen.remove() }
                }

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.4f,
                    0.0f
                ).apply {
                    interpolator = OvershootInterpolator()
                    duration = 500L
                    doOnEnd { screen.remove() }
                }

                zoomX.start()
                zoomY.start()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SpendLessTheme {
                KoinContext {
                    val navController = rememberNavController()
                    viewModel.state.isLoggedIn?.let {
                        NavigationRoot(
                            navController = navController,
                            isLoggedIn = it,
                        )
                    }
                }
            }
        }
    }
}