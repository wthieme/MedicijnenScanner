package nl.whitedove.medicijnenscanner

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import nl.whitedove.washetdroogofniet.NmvsState
import org.json.JSONException
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toonNmvsState(NmvsState())
        initFab()
        toondataBackground()
        toonResult()
    }

    override fun onResume() {
        super.onResume()
        toonResult()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.cmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            val intent1 = Intent()
            intent1.setClass(this@MainActivity, PreferenceActivity::class.java)
            startActivityForResult(intent1, 0)
            return true
        } else super.onOptionsItemSelected(item)
    }

    private fun initFab() {
        val fabscan = findViewById<FloatingActionButton>(R.id.fabScan)
        fabscan.setOnClickListener({ openScanner() })
    }

    private fun openScanner() {
        val intent = Intent(this, ScanActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun toondataBackground() {
        networkActive()
        val cxt = applicationContext
        if (!Helper.testInternet(cxt)) {
            return
        }
        val context = applicationContext

        AsyncGetNmvsState(this).execute(context)
        val pack = NmvsHelper.mPack
        if (pack.packnr.isNullOrEmpty()) return
        AsyncVerifyPack(this).execute(Pair.create(context, pack))
    }

    private fun toonResult(pack: Pack) {
        networkActive()
        NmvsHelper.mPack = pack
        toonResult()
    }

    private fun toonResult() {
        val pack = NmvsHelper.mPack

        val tvBatchId = findViewById<TextView>(R.id.tvBatchId)
        val tvExpiration = findViewById<TextView>(R.id.tvExpiration)
        val tvPackNr = findViewById<TextView>(R.id.tvPackNr)
        val tvProductCode = findViewById<TextView>(R.id.tvProductCode)
        val tvIcon = findViewById<TextView>(R.id.tvIcon)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        tvBatchId.setText(pack.batchId)
        tvExpiration.setText(pack.expiration)
        tvPackNr.setText(pack.packnr)
        tvProductCode.setText(pack.productCode)

        val iconFont = FontManager.GetTypeface(this, FontManager.FONTAWESOME_SOLID)
        FontManager.MarkAsIconContainer(tvIcon, iconFont)

        if (pack.ok) {
            var color = ContextCompat.getColor(this, R.color.colorLightGreen)
            tvIcon.setText(getString(R.string.fa_check))
            tvIcon.setTextColor(color)
            tvResult.setTextColor(color)
        } else {
            var color = ContextCompat.getColor(this, R.color.colorLightRed)
            tvIcon.setText(getString(R.string.fa_nok))
            tvIcon.setTextColor(color)
            tvResult.setTextColor(color)
        }
        tvResult.setText(pack.result)
    }

    private fun toonNmvsState(nmvsState: NmvsState) {
        networkActive()
        val tvOnline = findViewById<TextView>(R.id.tvOnline)
        val iconFont = FontManager.GetTypeface(this, FontManager.FONTAWESOME_SOLID)
        FontManager.MarkAsIconContainer(tvOnline, iconFont)

        if (nmvsState.state == StateType.Closed) {
            var color = ContextCompat.getColor(this, R.color.colorRed)
            tvOnline.setTextColor(color)
        }
        if (nmvsState.state == StateType.Open) {
            var color = ContextCompat.getColor(this, R.color.colorGreen)
            tvOnline.setTextColor(color)
        }
    }

    private class AsyncGetNmvsState internal constructor(context: MainActivity) : AsyncTask<Context, Void, NmvsState>() {

        private val activityWeakReference: WeakReference<MainActivity> = WeakReference(context)

        override fun doInBackground(vararg params: Context): NmvsState {
            var nmvsState: NmvsState = NmvsState()
            val ctx = params[0]
            val url = Helper.getUrl(ctx)
            val auth = Helper.getAuthKey(ctx)
            try {
                nmvsState = NmvsHelper.bepaalState(url, auth)
            } catch (ignored: JSONException) {
            }

            return nmvsState
        }

        override fun onPostExecute(result: NmvsState) {
            val activity = activityWeakReference.get()
            activity?.toonNmvsState(result)
        }
    }

    private class AsyncVerifyPack internal constructor(context: MainActivity) : AsyncTask<Pair<Context, Pack>, Void, Pack>() {

        private val activityWeakReference: WeakReference<MainActivity> = WeakReference(context)

        override fun doInBackground(vararg params: Pair<Context, Pack>): Pack {
            val ctx = params[0].first
            var pack = params[0].second
            val url = Helper.getUrl(ctx)
            val auth = Helper.getAuthKey(ctx)
            try {
                pack = NmvsHelper.verifyPack(url, auth, pack)
            } catch (ignored: JSONException) {
            }

            return pack
        }

        override fun onPostExecute(pack: Pack) {
            val activity = activityWeakReference.get()
            activity?.toonResult(pack)
        }
    }

    private fun networkActive() {
        Helper.networkActive(this, findViewById<View>(R.id.tvBolt) as TextView)
    }
}
