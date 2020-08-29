package juuxel.muisti.server

import juuxel.muisti.data.Deck
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.asSequence

object DeckIo {
    val DECK_DIRECTORY = Paths.get("decks")

    fun parseDeck(id: String): Deck =
        parseDeck(DECK_DIRECTORY.resolve("$id.json"))

    fun parseDeck(path: Path): Deck =
        Json.decodeFromString(readDeck(path))

    fun readDeck(id: String): String =
        readDeck(DECK_DIRECTORY.resolve("$id.json"))

    fun readDeck(path: Path): String {
        if (Files.notExists(path)) {
            throw FileNotFoundException("Deck file not found: $path")
        }

        return String(Files.readAllBytes(path), Charsets.UTF_8)
    }

    fun getDeckName(path: Path): String =
        path.fileName.toString().removeSuffix(".json")

    fun listDecks(): Sequence<Pair<String, Deck>> =
        listDeckFiles().map { path -> getDeckName(path) to parseDeck(path) }

    fun listDeckFiles(): Sequence<Path> = Files.list(DECK_DIRECTORY).asSequence()
}
