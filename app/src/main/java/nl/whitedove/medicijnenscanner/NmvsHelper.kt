package nl.whitedove.medicijnenscanner

import nl.whitedove.washetdroogofniet.NmvsState
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.joda.time.DateTime
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


object NmvsHelper {

    private fun getNmvsState(url: String, auth: String): String? {

        val client = OkHttpClient()

        val formBody = FormBody.Builder()
                .build()

        val request = Request.Builder()
                .addHeader("Cache-Control", "no-cache")
                .addHeader("X-Authorization", auth)
                .url(url)
                .post(formBody)
                .build()

        var nmvsData: String? = null
        val response: Response
        try {
            response = client.newCall(request).execute()
            if (response.isSuccessful)
                nmvsData = response.body()!!.string()
        } catch (ignored: IOException) {
        }
        return nmvsData
    }

    @Throws(JSONException::class)
    fun bepaalState(url: String, auth: String): NmvsState? {

        val stateJson = NmvsHelper.getNmvsState(url, auth)
        if (stateJson == null || stateJson.isEmpty()) {
            return null
        }

        val jObj = JSONObject(stateJson)

        val result = NmvsState()
        val state = jObj.getInt("State")
        result.state = StateType.valueOf(state)
        result.lastchange = DateTime.now()
        return result
    }
}
