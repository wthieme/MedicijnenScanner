package nl.whitedove.medicijnenscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    companion object {
        private const val HUAWEI = "huawei"
        private const val MY_CAMERA_REQUEST_CODE = 6515
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        initScanner()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_REQUEST_CODE)
                return
            }
        }
        openCamera()
    }

    override fun handleResult(p0: Result?) {
        if (p0 != null) {
            Toast.makeText(this,p0.text, Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openCamera()
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                showCameraSnackBar()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.cmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun showCameraSnackBar() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            val rlRootScanView = findViewById<RelativeLayout>(R.id.rlRootScanView)
            val snackbar = Snackbar.make(rlRootScanView, resources.getString(R.string.camerapermissie), Snackbar.LENGTH_LONG)
            val view1 = snackbar.view
            view1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
            val textView = view1.findViewById<TextView>(android.support.design.R.id.snackbar_text)
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            snackbar.show()
        }
    }

    private fun openCamera() {
        val qrCodeScanner = findViewById<ZXingScannerView>(R.id.qrCodeScanner)
        qrCodeScanner.startCamera()
        qrCodeScanner.setResultHandler(this)
    }

    private fun initScanner()
    {
        val qrCodeScanner = findViewById<ZXingScannerView>(R.id.qrCodeScanner)
        qrCodeScanner.setFormats(listOf(BarcodeFormat.QR_CODE))
        qrCodeScanner.setAutoFocus(true)
        qrCodeScanner.setLaserColor(R.color.colorAccent)
        qrCodeScanner.setMaskColor(R.color.colorAccent)
        if (Build.MANUFACTURER.equals(HUAWEI, ignoreCase = true))
            qrCodeScanner.setAspectTolerance(0.5f)
    }
}