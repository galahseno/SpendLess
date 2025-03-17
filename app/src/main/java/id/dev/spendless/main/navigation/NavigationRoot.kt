package id.dev.spendless.main.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import androidx.navigation.toRoute
import id.dev.spendless.auth.presentation.login.LoginScreenRoot
import id.dev.spendless.auth.presentation.register.RegisterScreenRoot
import id.dev.spendless.auth.presentation.register.RegisterViewModel
import id.dev.spendless.auth.presentation.register.create_pin.CreatePinScreenRoot
import id.dev.spendless.auth.presentation.register.onboarding_preferences.OnboardingPreferencesScreenRoot
import id.dev.spendless.auth.presentation.register.repeat_pin.RepeatPinScreenRoot
import id.dev.spendless.core.presentation.pin_prompt.PinPromptScreenRoot
import id.dev.spendless.dashboard.presentation.DashboardScreenRoot
import id.dev.spendless.main.util.BiometricPromptManager
import id.dev.spendless.settings.presentation.SettingScreenRoot
import id.dev.spendless.settings.presentation.preferences.PreferencesScreenRoot
import id.dev.spendless.settings.presentation.security.SecurityScreenRoot
import id.dev.spendless.transaction.presentation.AllTransactionScreenRoot
import id.dev.spendless.transaction.presentation.export.ExportScreenRoot
import org.koin.androidx.compose.navigation.koinNavViewModel

@SuppressLint("RestrictedApi")
@Composable
fun NavigationRoot(
    navController: NavHostController,
    promptManager: BiometricPromptManager,
    isLoggedIn: Boolean,
    isSessionExpired: Boolean,
    savedBackStack: List<String>,
    onSaveBackStack: (List<String>) -> Unit,
) {
    val currentIsSessionExpired by rememberUpdatedState(newValue = isSessionExpired)
    var hasNavigated by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(currentIsSessionExpired) {
        if (currentIsSessionExpired && !hasNavigated) {
            val currentBackStack =
                navController.currentBackStack.value.map { it.destination.route ?: "" }
            onSaveBackStack(currentBackStack)

            navController.navigate(Screen.Session) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                    saveState = true
                }
            }
            hasNavigated = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home else Screen.Auth,
        modifier = Modifier.fillMaxSize()
    ) {
        authGraph(navController)
        sessionGraph(
            onSuccessValidation = {
                hasNavigated = false
            },
            navController,
            savedBackStack,
            promptManager
        )
        homeGraph(navController)
        transactionGraph(navController)
        settingsGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<Screen.Auth>(
        startDestination = Screen.Auth.Login
    ) {
        composable<Screen.Auth.Login>(
            enterTransition = {
                slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
            }
        ) {
            LoginScreenRoot(
                onNavigateToRegister = {
                    if (!this.transition.isRunning) {
                        navController.navigate(Screen.Auth.Register) {
                            popUpTo<Screen.Auth.Login> {
                                inclusive = true
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                },
                onSuccessLogin = {
                    navController.navigate(Screen.Home.Dashboard) {
                        popUpTo<Screen.Auth> {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable<Screen.Auth.Register>(
            enterTransition = {
                slideInVertically(initialOffsetY = { -it }) + fadeIn()
            },
            exitTransition = {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute?.substringAfterLast(".") == Screen.Auth.Login::class.java.simpleName) {
                    ExitTransition.None
                } else slideOutHorizontally(targetOffsetX = { -it / 2 })
            }
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val backStackEntry = remember { it }
            val viewModel: RegisterViewModel =
                koinNavViewModel(viewModelStoreOwner = navBackStackEntry ?: backStackEntry)

            RegisterScreenRoot(
                onNavigateToLogin = {
                    if (!this.transition.isRunning) {
                        navController.navigate(Screen.Auth.Login) {
                            popUpTo<Screen.Auth.Register> {
                                inclusive = true
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                },
                onSuccessCheckUsername = {
                    navController.navigate(Screen.Auth.Register.CreatePin(fromRegister = true))
                },
                onSuccessRegister = {
                    navController.navigate(Screen.Home.Dashboard) {
                        popUpTo<Screen.Auth> {
                            inclusive = true
                        }
                    }
                },
                viewModel = viewModel
            )
        }
        composable<Screen.Auth.Register.CreatePin>(
            enterTransition = {
                val fromRegister = navController
                    .getBackStackEntry<Screen.Auth.Register.CreatePin>()
                    .toRoute<Screen.Auth.Register.CreatePin>()
                    .fromRegister

                if (fromRegister) {
                    slideInHorizontally(initialOffsetX = { it })
                } else {
                    slideInHorizontally(initialOffsetX = { -it })
                }
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                fadeOut()
            }
        ) {
            val backStackEntry = remember { navController.getBackStackEntry(Screen.Auth.Register) }
            val viewModel: RegisterViewModel =
                koinNavViewModel(viewModelStoreOwner = backStackEntry)

            CreatePinScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                },
                onProcessToRepeatPin = {
                    navController.navigate(Screen.Auth.Register.RepeatPin)
                },
                onSuccessRegister = {
                    navController.navigate(Screen.Home.Dashboard) {
                        popUpTo<Screen.Auth> {
                            inclusive = true
                        }
                    }
                },
                viewModel = viewModel
            )
        }
        composable<Screen.Auth.Register.RepeatPin>(
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            val backStackEntry = remember { navController.getBackStackEntry(Screen.Auth.Register) }
            val viewModel: RegisterViewModel =
                koinNavViewModel(viewModelStoreOwner = backStackEntry)

            RepeatPinScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                },
                onProcessToOnboardingPreferences = {
                    navController.navigate(Screen.Auth.Register.OnboardingPreferences)
                },
                onSuccessRegister = {
                    navController.navigate(Screen.Home.Dashboard) {
                        popUpTo<Screen.Auth> {
                            inclusive = true
                        }
                    }
                },
                viewModel = viewModel
            )
        }
        composable<Screen.Auth.Register.OnboardingPreferences>(
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute?.substringAfterLast(".") == Screen.Home.Dashboard::class.java.simpleName) {
                    ExitTransition.None
                } else {
                    slideOutHorizontally(targetOffsetX = { it })
                }
            }
        ) {
            val backStackEntry = remember { navController.getBackStackEntry(Screen.Auth.Register) }
            val viewModel: RegisterViewModel =
                koinNavViewModel(viewModelStoreOwner = backStackEntry)

            OnboardingPreferencesScreenRoot(
                onBackClick = {
                    navController.navigate(Screen.Auth.Register.CreatePin(fromRegister = false)) {
                        popUpTo<Screen.Auth.Register>()
                    }
                },
                onSuccessRegister = {
                    navController.navigate(Screen.Home.Dashboard) {
                        popUpTo<Screen.Auth> {
                            inclusive = true
                        }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}

private fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    navigation<Screen.Home>(
        startDestination = Screen.Home.Dashboard
    ) {
        composable<Screen.Home.Dashboard>(
            enterTransition = {
                slideInVertically(initialOffsetY = { it }) + fadeIn()
            },
            exitTransition = {
                fadeOut()
            }
        ) {
            DashboardScreenRoot(
                onNavigateToAllTransaction = {
                    navController.navigate(Screen.Transaction.AllTransaction)
                },
                onNavigateToSettingScreen = {
                    navController.navigate(Screen.Settings)
                }
            )
        }
    }
}

private fun NavGraphBuilder.sessionGraph(
    onSuccessValidation: () -> Unit,
    navController: NavHostController,
    savedBackStack: List<String>,
    promptManager: BiometricPromptManager
) {
    navigation<Screen.Session>(
        startDestination = Screen.Session.PinPrompt
    ) {
        composable<Screen.Session.PinPrompt>(
            enterTransition = {
                fadeIn()
            },
        ) {
            PinPromptScreenRoot(
                onSuccessValidateSession = {
                    onSuccessValidation()
                    if (savedBackStack.isNotEmpty()) {
                        val filteredBackStack = savedBackStack.filter { route ->
                            route != "" && route != navController.graph.startDestinationRoute
                        }

                        filteredBackStack.forEach { route ->
                            navController.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(Screen.Session) {
                                    inclusive = true
                                }
                            }
                        }
                    } else {
                        navController.navigate(Screen.Home.Dashboard) {
                            popUpTo(Screen.Session) {
                                inclusive = true
                            }
                        }
                    }
                },
                promptManager = promptManager
            )
        }
    }
}

private fun NavGraphBuilder.transactionGraph(navController: NavHostController) {
    navigation<Screen.Transaction>(
        startDestination = Screen.Transaction.AllTransaction
    ) {
        composable<Screen.Transaction.AllTransaction>(
            enterTransition = {
                slideInVertically(initialOffsetY = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = { it })
            }
        ) {
            AllTransactionScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        // todo move to core since is bottom sheet and can be access from dashboard and transaction
        composable<Screen.Transaction.AllTransaction.Export> {
            ExportScreenRoot(

            )
        }
    }
}

private fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    navigation<Screen.Settings>(
        startDestination = Screen.Settings.SettingsMenu
    ) {
        composable<Screen.Settings.SettingsMenu>(
            enterTransition = {
                slideInVertically(initialOffsetY = { -it }) + fadeIn()
            },
            exitTransition = {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute?.substringAfterLast(".") == Screen.Auth.Login::class.java.simpleName) {
                    ExitTransition.None
                } else slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutVertically(targetOffsetY = { -it })
            }
        ) {
            SettingScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                },
                onPreferencesClick = {
                    navController.navigate(Screen.Settings.SettingsMenu.Preferences)
                },
                onSecurityClick = {
                    navController.navigate(Screen.Settings.SettingsMenu.Security)
                },
            )
        }
        composable<Screen.Settings.SettingsMenu.Preferences>(
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            PreferencesScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.Settings.SettingsMenu.Security>(
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            SecurityScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}