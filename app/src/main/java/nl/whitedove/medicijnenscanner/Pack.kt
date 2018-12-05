package nl.whitedove.medicijnenscanner

class Pack {
    var packnr: String = ""
    var productCode: String = ""
    var batchId: String = ""
    var expiration: String = ""
    var result: String = "Geen scan"
    var ok: Boolean = false

    constructor (result: String) {
        this.result = result
    }

    constructor()
}
