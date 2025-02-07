package id.dev.spendless.main.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.dev.spendless.auth.presentation.login.LoginScreenRoot
import id.dev.spendless.auth.presentation.register.RegisterScreenRoot
import id.dev.spendless.auth.presentation.register.create_pin.CreatePinScreenRoot
import id.dev.spendless.auth.presentation.register.onboarding_preferences.OnboardingPreferencesScreenRoot
import id.dev.spendless.core.presentation.pin_prompt.PinPromptScreenRoot
import id.dev.spendless.dashboard.presentation.DashboardScreenRoot
import id.dev.spendless.settings.presentation.SettingScreenRoot
import id.dev.spendless.settings.presentation.preferences.PreferencesScreenRoot
import id.dev.spendless.settings.presentation.security.SecurityScreenRoot
import id.dev.spendless.transaction.presentation.add_transaction.AddTransactionScreenRoot
import id.dev.spendless.transaction.presentation.all_transaction.AllTransactionScreenRoot
import id.dev.spendless.transaction.presentation.all_transaction.export.ExportScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Auth,
        modifier = Modifier.fillMaxSize()
    ) {
        authGraph(navController)
        sessionGraph(navController)
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
                        popUpTo(Screen.Auth.Login) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
        composable<Screen.Auth.Register> {
            RegisterScreenRoot(
                onNavigateToLogin = {
                    navController.navigate(Screen.Auth.Login) {
                        popUpTo(Screen.Auth.Register) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
        composable<Screen.Auth.Register.CreatePin> {
            CreatePinScreenRoot(

            )
        }
        composable<Screen.Auth.Register.OnboardingPreferences> {
            OnboardingPreferencesScreenRoot(

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

            )
        }
    }
}

private fun NavGraphBuilder.sessionGraph(navController: NavHostController) {
    navigation<Screen.Session>(
        startDestination = Screen.Session.PinPrompt
    ) {
        composable<Screen.Session.PinPrompt> {
            PinPromptScreenRoot(

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

            )
        }
        composable<Screen.Transaction.AllTransaction.Export> {
            ExportScreenRoot(

            )
        }
        composable<Screen.Transaction.AddTransaction> {
            AddTransactionScreenRoot(

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

            )
        }
        composable<Screen.Settings.SettingsMenu.Preferences> {
            PreferencesScreenRoot(

            )
        }
        composable<Screen.Settings.SettingsMenu.Security> {
            SecurityScreenRoot(

            )
        }
    }
}