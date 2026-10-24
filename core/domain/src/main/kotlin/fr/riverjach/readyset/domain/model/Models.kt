package fr.riverjach.readyset.domain.model

@JvmInline
value class TripId(val value: String) {
    init {
        require(value.isNotBlank()) { "TripId cannot be blank" }
    }
}

@JvmInline
value class ItemId(val value: String) {
    init {
        require(value.isNotBlank()) { "ItemId cannot be blank" }
    }
}

enum class ItemCategory(val displayName: String) {
    CLOTHES("Vêtements"),
    TOILETRIES("Trousse de toilette"),
    DOCUMENTS("Documents & Papiers"),
    TECH("Électronique"),
    MISC("Divers")
}

data class PackingItem(
    val id: ItemId,
    val tripId: TripId,
    val name: String,
    val category: ItemCategory,
    val quantity: Int = 1,
    val isPacked: Boolean = false
) {
    init {
        require(name.isNotBlank()) { "Item name cannot be blank" }
        require(quantity >= 1) { "Quantity must be at least 1" }
    }
}

data class Trip(
    val id: TripId,
    val title: String,
    val destination: String? = null,
    val departureDateEpochDays: Long? = null,
    val isArchived: Boolean = false
) {
    init {
        require(title.isNotBlank()) { "Trip title cannot be blank" }
    }
}

data class TripDetail(
    val trip: Trip,
    val items: List<PackingItem>
) {
    val totalItemsCount: Int get() = items.size
    val packedItemsCount: Int get() = items.count { it.isPacked }
    val progressRatio: Float get() = if (totalItemsCount == 0) 0f else packedItemsCount.toFloat() / totalItemsCount
    val progressPercentage: Int get() = (progressRatio * 100).toInt()
}

data class TemplateItem(
    val name: String,
    val category: ItemCategory,
    val defaultQuantity: Int = 1
)

data class PackingTemplate(
    val id: String,
    val title: String,
    val description: String,
    val items: List<TemplateItem>
)
