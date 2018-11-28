package nl.whitedove.medicijnenscanner

import android.annotation.SuppressLint
import java.util.HashMap

enum class RequestStateType(val value: Int) {
    Pending(1), Submitted(2), Completed(3);


    companion object {
        @SuppressLint("UseSparseArrays")
        private val map = HashMap<Int, RequestStateType>()

        init {
            for (weerType in RequestStateType.values()) {
                map[weerType.value] = weerType
            }
        }

        fun valueOf(weerType: Int): RequestStateType {
            return map[weerType] ?: Pending
        }
    }
}
