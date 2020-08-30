# muisti

A simple flashcard webapp 100% written in Kotlin.

![](https://img.shields.io/github/workflow/status/Juuxel/Muisti/Kotlin%20CI%20with%20Gradle?style=flat-square) ![](https://img.shields.io/github/license/Juuxel/Muisti?style=flat-square) ![](https://img.shields.io/github/v/tag/Juuxel/Muisti?style=flat-square)

## Running
You can run the server with Gradle directly using `./gradlew :server:run`,
or build Muisti using `./gradlew build` and run the output jar file in `server/build/libs`.

## Deck format

Each deck is a JSON file like this:
```json
{
  "title": "Deck title",
  "cards": [
    { "front": "Some front text", "back": "Some back text" },
    { "front": "lil tater", "back": "tiny potato" }
  ]
}
```

Deck files are placed in the `decks` directory inside the working directory.
