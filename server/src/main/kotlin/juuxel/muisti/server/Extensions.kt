package juuxel.muisti.server

import io.javalin.http.Context
import juuxel.muisti.util.Result
import juuxel.muisti.util.orThrow
import kotlinx.html.HtmlHeadTag
import kotlinx.html.link

fun Context.html(html: Result<String, Exception>): Context = html(html.orThrow())

fun HtmlHeadTag.addStyle() {
    link(rel = "stylesheet", type = "text/css", href = "/style.css")
}
