package juuxel.muisti.server

import kotlinx.html.HtmlHeadTag
import kotlinx.html.link

fun HtmlHeadTag.addStyle() {
    link(rel = "stylesheet", type = "text/css", href = "/style.css")
}
