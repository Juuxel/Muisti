package juuxel.muisti.server

import juuxel.muisti.data.Deck
import juuxel.muisti.util.Result
import juuxel.muisti.util.lift
import juuxel.muisti.util.liftB
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.asSequence

object DeckIo {
    val DECK_DIRECTORY = Paths.get("decks")

    fun parseDeck(id: String): Result<Deck, Exception> =
        parseDeck(DECK_DIRECTORY.resolve("$id.json"))

    fun parseDeck(path: Path): Result<Deck, Exception> =
        readDeck(path).flatMap {
            Result.catching { Json.decodeFromString(it) }
        }

    fun readDeck(id: String): Result<String, Exception> =
        readDeck(DECK_DIRECTORY.resolve("$id.json"))

    fun readDeck(path: Path): Result<String, Exception> = Result.catching {
        if (Files.notExists(path)) {
            throw FileNotFoundException("Deck file not found: $path")
        }

        String(Files.readAllBytes(path), Charsets.UTF_8)
    }

    fun getDeckName(path: Path): String =
        path.fileName.toString().removeSuffix(".json")

    fun listDecks(): Result<Sequence<Pair<String, Deck>>, Exception> = listDeckFiles().flatMap {
        it.map { path -> Pair(getDeckName(path), parseDeck(path)).liftB() }.lift()
    }

    fun listDeckFiles(): Result<Sequence<Path>, Exception> = Result.catching {
        Files.list(DECK_DIRECTORY).asSequence()
    }
}