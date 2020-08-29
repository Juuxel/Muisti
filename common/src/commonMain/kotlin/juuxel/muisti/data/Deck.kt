package juuxel.muisti.data

import kotlinx.serialization.Serializable

@Serializable
data class Deck(val title: String, val cards: Set<Card>)
