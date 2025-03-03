package id.dev.spendless.main.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
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
    LaunchedEffect(isSessionExpired) {
        // TODO Handle save and restore state
        if (isSessionExpired) {
            val currentBackStack =
                navController.currentBackStack.value.map { it.destination.route ?: "" }
            onSaveBackStack(currentBackStack)

            navController.navigate(Screen.Session) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                    saveState = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        // TODO check logout with animation transition
        startDestination = if (isLoggedIn) Screen.Home else Screen.Auth,
        modifier = Modifier.fillMaxSize()
    ) {
        authGraph(navController)
        sessionGraph(navController, savedBackStack, promptManager)
        homeGraph(navController)
        transactionGraph(navController)
        settingsGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<Screen.Auth>(
        startDestination = Screen.Auth.Login
    ) {
        composable<Screen.Auth.Login> {
            LoginScreenRoot(
                onNavigateToRegister = {
                    navController.navigate(Screen.Auth.Register) {
                        popUpTo<Screen.Auth.Login> {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
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
        composable<Screen.Auth.Register> {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val backStackEntry = remember { it }
            val viewModel: RegisterViewModel =
                koinNavViewModel(viewModelStoreOwner = navBackStackEntry ?: backStackEntry)

            RegisterScreenRoot(
                onNavigateToLogin = {
                    navController.navigate(Screen.Auth.Login) {
                        popUpTo<Screen.Auth.Register> {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessCheckUsername = {
                    navController.navigate(Screen.Auth.Register.CreatePin)
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
        composable<Screen.Auth.Register.CreatePin> {
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
        composable<Screen.Auth.Register.RepeatPin> {
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
        composable<Screen.Auth.Register.OnboardingPreferences> {
            val backStackEntry = remember { navController.getBackStackEntry(Screen.Auth.Register) }
            val viewModel: RegisterViewModel =
                koinNavViewModel(viewModelStoreOwner = backStackEntry)

            OnboardingPreferencesScreenRoot(
                onBackClick = {
                    navController.navigate(Screen.Auth.Register.CreatePin) {
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
        composable<Screen.Home.Dashboard> {
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
    navController: NavHostController,
    savedBackStack: List<String>,
    promptManager: BiometricPromptManager
) {
    navigation<Screen.Session>(
        startDestination = Screen.Session.PinPrompt
    ) {
        composable<Screen.Session.PinPrompt> {
            PinPromptScreenRoot(
                onSuccessValidateSession = {
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
        composable<Screen.Transaction.AllTransaction> {
            AllTransactionScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
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
        composable<Screen.Settings.SettingsMenu> {
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
        composable<Screen.Settings.SettingsMenu.Preferences> {
            PreferencesScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.Settings.SettingsMenu.Security> {
            SecurityScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}