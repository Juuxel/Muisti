package juuxel.muisti.server.html

import kotlinx.html.Tag
import java.io.Writer

/**
 * A writer that writes to a kotlinx.html [Tag].
 */
class HtmlWriter(private val html: Tag) : Writer() {
    override fun write(cbuf: CharArray, off: Int, len: Int) {
        html.text(String(cbuf, off, len))
    }

    override fun close() {
        // NO-OP
    }

    override fun flush() {
        // NO-OP
    }
}
