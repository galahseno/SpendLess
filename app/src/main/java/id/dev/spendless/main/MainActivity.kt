package id.dev.spendless.main

import android.animation.ObjectAnimator
import android.content.ComponentCallbacks2
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.main.navigation.NavigationRoot
import id.dev.spendless.main.util.BiometricPromptManager
import id.dev.spendless.widget.SpendLessAppWidgetUpdate.Companion.FROM_WIDGET_KEY
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.KoinContext
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity(), ComponentCallbacks2 {
    private val viewModel by viewModel<MainViewModel>()
    private val promptManager: BiometricPromptManager by inject { parametersOf(this) }

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
                    doOnEnd {
                        screen.remove()
                        handleIntent(intent)
                    }
                }

                zoomX.start()
                zoomY.start()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        application.registerComponentCallbacks(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SpendLessTheme {
                KoinContext {
                    val navController = rememberNavController()
                    viewModel.state.isLoggedIn?.let {
                        NavigationRoot(
                            navController = navController,
                            promptManager = promptManager,
                            isLoggedIn = it,
                            isSessionExpired = viewModel.state.isSessionExpired,
                            savedBackStack = viewModel.state.backStack,
                            onSaveBackStack = { backStack ->
                                viewModel.captureBackStack(backStack)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            viewModel.resetSession()
        }
    }

    private fun handleIntent(intent: Intent) {
        intent.getBooleanExtra(FROM_WIDGET_KEY, false).let { fromWidget ->
            if (fromWidget) {
                viewModel.actionCreateTransaction()
                intent.removeExtra(FROM_WIDGET_KEY)
            }
        }
    }
}