package nl.whitedove.medicijnenscanner

import android.annotation.SuppressLint
import java.util.HashMap

enum class StateType(val value: Int) {
    Open(1), Closed(2);


    companion object {
        @SuppressLint("UseSparseArrays")
        private val map = HashMap<Int, StateType>()

        init {
            for (stateType in StateType.values()) {
                map[stateType.value] = stateType
            }
        }

        fun valueOf(weerType: Int): StateType {
            return map[weerType] ?: Closed
        }
    }
}
