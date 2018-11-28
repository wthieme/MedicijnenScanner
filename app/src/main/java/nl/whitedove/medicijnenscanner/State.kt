package nl.whitedove.washetdroogofniet

import nl.whitedove.medicijnenscanner.StateType
import org.joda.time.DateTime

class NmvsState {
    var state: StateType = StateType.Closed
    var lastchange: DateTime = DateTime.now()
}
