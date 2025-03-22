package id.dev.spendless.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import id.dev.spendless.R
import id.dev.spendless.main.MainActivity

class SpendLessAppWidgetUpdate : GlanceAppWidget() {
    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .appWidgetBackground()
                        .clickable(
                            actionStartActivity(
                                Intent(context, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    putExtra(FROM_WIDGET_KEY, true)
                                }
                            )
                        )
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.widget_background),
                        contentDescription = "Radial Gradient Background",
                        modifier = GlanceModifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .cornerRadius(20.dp)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.app_icon),
                            contentDescription = "app_icon",
                            modifier = GlanceModifier
                                .size(52.dp)
                        )
                        Spacer(modifier = GlanceModifier.height(12.dp))
                        Text(
                            context.getString(R.string.create_transaction),
                            style = TextStyle(color = ColorProvider(Color.White)),
                            modifier = GlanceModifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val FROM_WIDGET_KEY = "FROM_WIDGET_KEY"
    }
}