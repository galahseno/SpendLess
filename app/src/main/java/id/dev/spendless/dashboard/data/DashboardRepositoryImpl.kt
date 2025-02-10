package id.dev.spendless.dashboard.data

import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.dashboard.domain.DashboardRepository

class DashboardRepositoryImpl(
    private val settingPreferences: SettingPreferences,
) : DashboardRepository {

}