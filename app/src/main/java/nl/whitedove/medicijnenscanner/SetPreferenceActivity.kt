package nl.whitedove.medicijnenscanner

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class SetPreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, PrefsFragment())
                .commit()

    }
}