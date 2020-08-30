package juuxel.muisti.game

import juuxel.muisti.data.Card
import juuxel.muisti.data.Deck

class Game(private val deck: Deck, private val renderer: Renderer) {
    // Sides
    var mainSide: Card.Side = Card.Side.FRONT
        private set
    var side: Card.Side = mainSide
        private set

    // Cards
    val currentCard: Card get() = cards[currentCardIndex]
    lateinit var cards: List<Card>
        private set
    val unknownCards: List<Card> get() = _unknownCards
    var currentCardIndex: Int = 0
        private set

    private val _unknownCards: MutableList<Card> = ArrayList()

    fun start(reviseUnknowns: Boolean = false) {
        side = mainSide
        currentCardIndex = 0
        cards =
            if (reviseUnknowns) _unknownCards.shuffled()
            else deck.cards.shuffled()
        _unknownCards.clear()

        render()
    }

    fun hasRemainingCards(): Boolean =
        currentCardIndex <= cards.lastIndex

    fun moveToNext(unknown: Boolean) {
        if (hasRemainingCards()) {
            if (unknown) {
                _unknownCards += cards[currentCardIndex]
            }

            currentCardIndex++
            side = mainSide
            render()
        }
    }

    fun flipCard() {
        side = side.flip()
        render()
    }

    fun flipAll() {
        mainSide = mainSide.flip()
        side = side.flip()
        render()
    }

    private fun render() {
        renderer.render(this)
    }
}
