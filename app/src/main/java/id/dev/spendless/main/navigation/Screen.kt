package id.dev.spendless.main.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Auth {
        @Serializable
        data object Login

        @Serializable
        data object Register {
            @Serializable
            data class CreatePin(val fromRegister: Boolean)

            @Serializable
            data object RepeatPin

            @Serializable
            data object OnboardingPreferences
        }
    }

    @Serializable
    data object Session {
        @Serializable
        data object PinPrompt
    }

    @Serializable
    data object Home {
        @Serializable
        data object Dashboard
    }

    @Serializable
    data object Transaction {
        @Serializable
        data object AllTransaction
    }

    @Serializable
    data object Settings {
        @Serializable
        data object SettingsMenu {
            @Serializable
            data object Preferences

            @Serializable
            data object Security
        }
    }
}