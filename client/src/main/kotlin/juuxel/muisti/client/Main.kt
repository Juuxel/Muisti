package juuxel.muisti.client

import juuxel.muisti.data.Card
import juuxel.muisti.data.Deck
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.hasClass
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import kotlinx.html.p
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.Element
import org.w3c.dom.ItemArrayLike
import org.w3c.xhr.XMLHttpRequest

// Specific elements
lateinit var cardContent: Element
lateinit var cardIndicator: Element

// Sides
var mainSide: Card.Side = Card.Side.FRONT
var side: Card.Side = mainSide

// Cards
lateinit var deck: Deck
var currentCard: Int = 0
lateinit var cards: List<Card>
val unknownCards: MutableList<Card> = ArrayList()

inline fun <T> ItemArrayLike<T>.forEach(fn: (T) -> Unit) {
    for (i in 0 until length) {
        item(i)?.let(fn)
    }
}

fun main() {
    document.onreadystatechange = {
        cardContent = document.querySelector(".card-content") ?: error("Could not find .card-content!")
        cardIndicator = document.querySelector(".card-indicator") ?: error("Could not find .card-indicator!")

        document.getElementsByClassName("flip-button").forEach {
            it.addEventListener("click", {
                side = side.flip()
                showCurrentCard()
            })
        }

        document.getElementsByClassName("flip-deck-button").forEach {
            it.addEventListener("click", {
                mainSide = mainSide.flip()
                side = side.flip()
                showCurrentCard()
            })
        }

        document.getElementsByClassName("option-button").forEach { button ->
            button.addEventListener("click", {
                it.stopImmediatePropagation()

                if (button.hasClass("no")) {
                    cards.getOrNull(currentCard)?.let(unknownCards::add)
                }

                currentCard++
                side = mainSide
                showCurrentCard()
            })
        }

        val req = XMLHttpRequest()
        req.onload = {
            if (req.status == 200.toShort()) {
                deck = Json.decodeFromString(req.responseText)
                run()
            }
        }

        req.open("GET", window.location.href.replace(Regex("^(.+)/deck/(.+)$"), "$1/data/$2"))
        req.send()
    }
}

fun run(useUnknowns: Boolean = false) {
    currentCard = 0
    cards = if (useUnknowns) unknownCards.shuffled() else deck.cards.shuffled()
    unknownCards.clear()
    side = mainSide
    showCurrentCard()
}

private fun showCurrentCard() {
    val finished = currentCard > cards.lastIndex

    if (finished) {
        showCardContent {
            div(classes = "finished-panel") {
                p {
                    +buildString {
                        append("Finished! You knew ")

                        if (unknownCards.isEmpty()) {
                            append("all of the cards! \uD83E\uDD73")
                        } else {
                            append(cards.size - unknownCards.size)
                            append("\u00A0/\u00A0")
                            append(cards.size)
                            append(" cards.")
                        }
                    }
                }

                if (unknownCards.isNotEmpty()) {
                    div(classes = "button text-button") {
                        +"revise unknown cards"

                        onClickFunction = {
                            run(useUnknowns = true)
                        }
                    }
                }

                div(classes = "button text-button") {
                    +"restart"

                    onClickFunction = {
                        run(useUnknowns = false)
                    }
                }
            }
        }
    } else {
        showCardContent {
            cards[currentCard].writeTo(this, side)
        }
    }

    // Update card indicator
    cardIndicator.textContent = if (finished) null else "${currentCard + 1} / ${cards.size}"
}

private fun showCardContent(block: TagConsumer<*>.() -> Unit) {
    cardContent.childNodes.forEach(cardContent::removeChild)
    cardContent.append { block() }
}
