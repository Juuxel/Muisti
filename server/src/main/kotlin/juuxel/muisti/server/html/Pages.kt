package juuxel.muisti.server.html

import juuxel.muisti.server.DeckIo
import juuxel.muisti.server.addStyle
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.details
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.meta
import kotlinx.html.pre
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import kotlinx.html.summary
import kotlinx.html.title
import java.io.PrintWriter
import kotlin.Exception

object Pages {
    fun buildHomePage(): String {
        val decks = DeckIo.listDecks()

        return buildString {
            appendHTML().html {
                head {
                    title("muisti")
                    addStyle()
                    meta(charset = "UTF-8")
                }

                body(classes = "home") {
                    h1 { +"muisti" }

                    div(classes = "deck-container") {
                        h2 { +"decks" }

                        div(classes = "deck-list") {
                            for ((path, deck) in decks.sortedBy { (_, deck) -> deck.title }) {
                                a(classes = "deck-entry", href = "/deck/$path") { +deck.title }
                            }
                        }
                    }
                }
            }
        }
    }

    fun buildDeckPage(id: String): String {
        val deck = DeckIo.parseDeck(id)

        return buildString {
            appendHTML().html {
                head {
                    title("muisti - ${deck.title}")
                    addStyle()
                    meta(charset = "UTF-8")
                    script(type = "application/javascript", src = "/script.js", block = {})
                }

                body {
                    h1 { +deck.title }
                    div(classes = "card") {
                        div(classes = "card-content")
                        div(classes = "card-button-area") {
                            div(classes = "card-indicator")
                            div(classes = "button text-button flip-button") {
                                +"flip"
                            }
                        }
                    }
                    div(classes = "buttons") {
                        div(classes = "button option-button yes") {
                            +"yes"
                        }
                        div(classes = "button option-button no") {
                            +"no"
                        }
                    }

                    div(classes = "button text-button flip-deck-button") {
                        +"flip all"
                    }

                    a(href = "/", classes = "button text-button home-button") {
                        +"home"
                    }
                }
            }
        }
    }

    fun buildErrorPage(code: Int, e: Exception): String = buildString {
        appendHTML().html {
            head {
                title("muisti")
                addStyle()
                meta(charset = "UTF-8")
            }

            body(classes = "error") {
                h1 {
                    +"$code: ${e.message ?: "No message"}"
                }

                details {
                    summary { +"See full stacktrace" }
                    pre {
                        PrintWriter(HtmlWriter(this)).use { writer ->
                            e.printStackTrace(writer)
                        }
                    }
                }
            }
        }
    }
}
