package id.dev.spendless

import android.app.Application
import id.dev.spendless.auth.data.di.authDataModule
import id.dev.spendless.auth.presentation.di.authPresentationModule
import id.dev.spendless.core.data.di.coreDataModule
import id.dev.spendless.core.presentation.di.corePresentationModule
import id.dev.spendless.dashboard.data.di.dashboardDataModule
import id.dev.spendless.dashboard.presentation.di.dashboardPresentationModule
import id.dev.spendless.main.di.appModule
import id.dev.spendless.settings.data.di.settingsDataModule
import id.dev.spendless.settings.presentation.di.settingsPresentationModule
import id.dev.spendless.transaction.data.di.transactionDataModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SpendLessApp: Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SpendLessApp)
            modules(
                appModule,
                coreDataModule,
                corePresentationModule,
                authDataModule,
                authPresentationModule,
                dashboardDataModule,
                dashboardPresentationModule,
                transactionDataModule,
                transactionDataModule,
                settingsDataModule,
                settingsPresentationModule
            )
        }
    }
}