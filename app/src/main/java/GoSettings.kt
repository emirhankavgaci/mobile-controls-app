
import android.content.Context
import android.content.Intent
import android.provider.Settings

class GoSettings {
    fun Setting(context: Context) {
        val intent = Intent(Settings.ACTION_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}