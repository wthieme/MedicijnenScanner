package nl.whitedove.medicijnenscanner

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFab()
        toonScanResult()
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

    private fun toonScanResult() {
        val tvResult = findViewById<TextView>(R.id.tvResult)
        tvResult.setText(Helper.scanResult)
    }
}
