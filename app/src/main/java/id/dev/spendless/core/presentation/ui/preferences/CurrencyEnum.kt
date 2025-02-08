package id.dev.spendless.core.presentation.ui.preferences

enum class CurrencyEnum(val symbol:String, val currencyName: String) {
    USD("$", "US Dollar (USD)"),
    EUR("€", "Euro (EUR)"),
    GBP("£", "British Pound Sterling (GBP)"),
    JPY("¥", "Japanese Yen (JPY)"),
    CHF("CHF", "Swiss Franc (CHF)"),
    CAD("C$", "Canadian Dollar (CAD)"),
    AUD("A$", "Australian Dollar (AUD)"),
    CNY("CN¥", "Chinese Yuan Renminbi (CNY)"),
    INR("₹", "Indian Rupee (INR)"),
    IDR("Rp", "Indonesian Rupiah (IDR)"),
}