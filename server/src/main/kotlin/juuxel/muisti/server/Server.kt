package juuxel.muisti.server

import io.javalin.Javalin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.io.FileNotFoundException
import java.util.Properties

private val LOGGER: Logger = LoggerFactory.getLogger("muisti-server")

@CommandLine.Command(name = "muisti-server", mixinStandardHelpOptions = true, versionProvider = VersionProvider::class)
private class Server : Runnable {
    @CommandLine.Parameters(index = "0", description = ["the port for the server to use"], defaultValue = "7000", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private var port: Int = -1

    override fun run() {
        val app = Javalin.create {
            it.addStaticFiles("/static")
        }.start(port)

        app.get("/") { ctx ->
            ctx.html(Pages.buildHomePage())
        }

        app.get("/deck/:deck") { ctx ->
            ctx.html(Pages.buildDeckPage(ctx.pathParam("deck")))
        }

        app.get("/data/:deck") { ctx ->
            ctx.contentType("application/json").result(DeckIo.readDeck(ctx.pathParam("deck")))
        }

        app.exception(FileNotFoundException::class.java) { e, ctx ->
            ctx.status(404).html(Pages.buildErrorPage(404, e))
        }

        app.exception(Exception::class.java) { e, ctx ->
            ctx.status(500).html(Pages.buildErrorPage(500, e))
        }

        LOGGER.info("Server started! Check it out at http://127.0.0.1:$port")
    }
}

private class VersionProvider : CommandLine.IVersionProvider {
    private val _version by lazy {
        val props = Properties()
        VersionProvider::class.java.getResourceAsStream("/version.properties").use(props::load)
        props.getProperty("version")
    }

    override fun getVersion(): Array<String> = arrayOf(_version)
}

fun main(args: Array<String>) {
    CommandLine(Server()).execute(*args)
}
