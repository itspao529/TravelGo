package com.udb.travelgo.model

data class Destination(
    var id: String = "",
    var name: String = "",
    var country: String = "",
    var price: Double = 0.0,
    var description: String = "",
    var imageUrl: String = "",
    var userId: String = "",
    var createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "country" to country,
        "price" to price,
        "description" to description,
        "imageUrl" to imageUrl,
        "userId" to userId,
        "createdAt" to createdAt
    )
}
