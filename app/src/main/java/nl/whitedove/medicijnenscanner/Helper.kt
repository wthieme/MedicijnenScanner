package nl.whitedove.medicijnenscanner

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.widget.TextView
import android.widget.Toast

internal object Helper {

    const val DEBUG = false

    fun l(log: String) {
        if (Helper.DEBUG) {
            println(log)
        }
    }

    fun testInternet(ctx: Context): Boolean {
        val result: Boolean
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        result = netInfo != null && netInfo.isConnected
        if (!result) Helper.showMessage(ctx, ctx.getString(R.string.NoInternet))
        return result
    }

    fun tryParseInt(value: String): Boolean {
        return try {
            Integer.parseInt(value)
            true
        } catch (e: NumberFormatException) {
            false
        }

    }

    fun showMessage(cxt: Context, melding: String) {
        Helper.l(melding)
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(cxt, melding, duration)
        toast.view.setBackgroundColor(ContextCompat.getColor(cxt, R.color.colorPrimary))
        val text = toast.view.findViewById(android.R.id.message) as TextView
        text.setTextColor(ContextCompat.getColor(cxt, R.color.colorAccent))
        toast.show()
    }

    fun getUrl(cxt: Context): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(cxt)
        val url = preferences.getString("Url", "")
        return url
    }

    fun getAuthKey(cxt: Context): String{
        val preferences = PreferenceManager.getDefaultSharedPreferences(cxt)
        val auth = preferences.getString("Autorization", "")
        return auth
    }

    fun networkActive(context: Context, tvBolt: TextView) {
        val iconFont = FontManager.GetTypeface(context, FontManager.FONTAWESOME_SOLID)
        FontManager.MarkAsIconContainer(tvBolt, iconFont)
        tvBolt.animate().alpha(1.0f).setDuration(50)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        tvBolt.animate().alpha(0.0f).setDuration(100).startDelay = 50
                    }
                })
    }
}
