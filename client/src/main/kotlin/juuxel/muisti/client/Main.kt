package juuxel.muisti.client

import juuxel.muisti.data.Deck
import juuxel.muisti.game.Game
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

lateinit var game: Game

// Specific elements
lateinit var cardContent: Element
lateinit var cardIndicator: Element

inline fun <T> ItemArrayLike<T>.forEach(fn: (T) -> Unit) {
    for (i in 0 until length) {
        item(i)?.let(fn)
    }
}

fun main() {
    document.onreadystatechange = {
        cardContent = document.querySelector(".card-content") ?: error("Could not find .card-content!")
        cardIndicator = document.querySelector(".card-indicator") ?: error("Could not find .card-indicator!")

        document.getElementsByClassName("flip-button").forEach { button ->
            button.addEventListener("click", {
                game.flipCard()
            })
        }

        document.getElementsByClassName("flip-deck-button").forEach { button ->
            button.addEventListener("click", {
                game.flipAll()
            })
        }

        document.getElementsByClassName("option-button").forEach { button ->
            button.addEventListener("click", {
                it.stopImmediatePropagation()
                game.moveToNext(button.hasClass("no"))
            })
        }

        val req = XMLHttpRequest()
        req.onload = {
            if (req.status == 200.toShort()) {
                val deck = Json.decodeFromString<Deck>(req.responseText)
                game = Game(deck, ::render)
                game.start()
            }
        }

        req.open("GET", window.location.href.replace(Regex("^(.+)/deck/(.+)$"), "$1/data/$2"))
        req.send()
    }
}

private fun render(game: Game) {
    if (game.hasRemainingCards()) {
        showCardContent {
            game.currentCard.writeTo(this, game.side)
        }
    } else {
        showCardContent {
            div(classes = "finished-panel") {
                p {
                    +buildString {
                        append("Finished! You knew ")

                        if (game.unknownCards.isEmpty()) {
                            append("all of the cards! \uD83E\uDD73")
                        } else {
                            append(game.cards.size - game.unknownCards.size)
                            append("\u00A0/\u00A0")
                            append(game.cards.size)
                            append(" cards.")
                        }
                    }
                }

                if (game.unknownCards.isNotEmpty()) {
                    div(classes = "button text-button") {
                        +"revise unknown cards"

                        onClickFunction = {
                            game.start(reviseUnknowns = true)
                        }
                    }
                }

                div(classes = "button text-button") {
                    +"restart"

                    onClickFunction = {
                        game.start(reviseUnknowns = false)
                    }
                }
            }
        }
    }

    // Update card indicator
    cardIndicator.textContent = if (game.hasRemainingCards()) "${game.currentCardIndex + 1} / ${game.cards.size}" else null
}

private fun showCardContent(block: TagConsumer<*>.() -> Unit) {
    cardContent.childNodes.forEach(cardContent::removeChild)
    cardContent.append { block() }
}
