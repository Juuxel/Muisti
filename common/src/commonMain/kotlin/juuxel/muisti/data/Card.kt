package juuxel.muisti.data

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.unsafe
import kotlinx.serialization.Serializable

@Serializable
data class Card(val front: String, val back: String) {
    fun getContent(side: Side): String =
        when (side) {
            Side.FRONT -> front
            Side.BACK -> back
        }

    fun writeTo(consumer: TagConsumer<*>, side: Side) = consumer.apply {
        div {
            unsafe {
                // Append the card content as raw content.
                // It's local, so it's actually safe.
                raw(getContent(side))
            }
        }
    }

    enum class Side {
        FRONT, BACK;

        fun flip(): Side = when (this) {
            FRONT -> BACK
            BACK -> FRONT
        }
    }
}
