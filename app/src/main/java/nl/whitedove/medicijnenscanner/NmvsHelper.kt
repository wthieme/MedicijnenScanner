package nl.whitedove.medicijnenscanner

import nl.whitedove.washetdroogofniet.NmvsState
import okhttp3.*
import org.joda.time.DateTime
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*


object NmvsHelper {

    var mPack: Pack = Pack()

    fun bepaalState(url: String, auth: String): NmvsState {

        val stateJson = NmvsHelper.nmvsStateRequest(url, auth)
        if (stateJson == null || stateJson.isEmpty()) {
            return NmvsState()
        }

        val jObj = JSONObject(stateJson)
        val result = NmvsState()
        val state = jObj.getInt("State")
        result.state = StateType.valueOf(state)
        result.lastchange = DateTime.now()
        return result
    }

    private fun nmvsStateRequest(url: String, auth: String): String? {

        val client = OkHttpClient()

        val formBody = FormBody.Builder()
                .build()

        val request = Request.Builder()
                .addHeader("Cache-Control", "no-cache")
                .addHeader("X-Authorization", auth)
                .url(url + "NmvsState")
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

    fun verifyPack(url: String, auth: String, pack: Pack): Pack {
        val verifyJson = NmvsHelper.verifyRequest(url, auth, pack)
        if (verifyJson == null || verifyJson.isEmpty()) {
            pack.result = "Geen verify resultaat ontvangen"
            pack.ok = false;
            return pack
        }

        val jObj = JSONObject(verifyJson)
        val jArr = jObj.getJSONArray("VerifyResults")
        val jVerify = jArr.getJSONObject(0)
        val resultDescription = jVerify.getString("ResultDescription")
        val resultCode = jVerify.getInt("ResultCode")
        pack.ok = (resultCode == 53)
        pack.result = resultDescription
        return pack
    }

    private fun verifyRequest(url: String, auth: String, pack: Pack): String? {

        val json = JSONObject()
        val jArr = JSONArray()
        val jVerify = JSONObject()

        val guid = UUID.randomUUID().toString().replace("-", "")

        jVerify.put("Id", guid)
        jVerify.put("ProductScheme", 1)
        jVerify.put("ProductCode", pack.productCode)
        jVerify.put("BatchId", pack.batchId)
        jVerify.put("BatchExpirationDate", pack.expiration)
        jVerify.put("PackSerialNumber", pack.packnr)
        jArr.put(jVerify)
        json.put("VerifyRequests", jArr)

        val client = OkHttpClient()
        val mediaType = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(mediaType, json.toString())

        val request = Request.Builder()
                .addHeader("Cache-Control", "no-cache")
                .addHeader("X-Authorization", auth)
                .url(url + "Verify")
                .post(body)
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

    fun splitPackdata(packData: String) {
        val pack = Pack()
        pack.result = "Geen medicijngegevens"
        pack.ok = false
        val i = 29
        val splitchar = i.toChar()
        val splitted = packData.split(splitchar)
        for (s in splitted) {
            if (s.isEmpty() || s.length < 2) continue
            val first2 = s.substring(0, 2)
            if (first2 == "01") {
                // Productcode
                if (s.length > 26 && s.substring(16, 18) == "17" && s.substring(24, 26) == "10") {
                    pack.productCode = s.substring(2, 16)
                    pack.expiration = s.substring(18, 24)
                    pack.batchId = s.substring(26)
                }
                if (s.length > 26 && s.substring(16, 18) == "21") {
                    pack.productCode = s.substring(2, 16)
                    pack.packnr = s.substring(18)
                }
            }
            if (first2 == "17") {
                // Batch exp
                if (s.length > 10 && s.substring(8, 10) == "10") {
                    pack.expiration = s.substring(2, 8)
                    pack.batchId = s.substring(10)
                }
                if (s.length == 8) {
                    pack.expiration = s.substring(2)
                }
            }

            if (first2 == "10") {
                // Batch exp
                if (s.length > 4) {
                    pack.batchId = s.substring(2)
                }
            }

            if (first2 == "21") {
                if (s.length > 2) {
                    // Packnr
                    pack.packnr = s.substring(2)
                }
            }
        }
        pack.ok = (!pack.packnr.isEmpty() && !pack.batchId.isEmpty() && !pack.expiration.isEmpty() && !pack.productCode.isEmpty())
        if (pack.ok) pack.result = ""
        mPack = pack
    }
}
