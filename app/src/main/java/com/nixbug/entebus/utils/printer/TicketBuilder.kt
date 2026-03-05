package com.nixbug.entebus.utils.printer

class TicketBuilder {
    private var bytes = byteArrayOf()
    private var currentLineWidth = 32

    private fun truncateWithEllipsis(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            text.take(maxLength - 3) + "..."
        } else {
            text
        }
    }

    fun addCommand(command: ByteArray): TicketBuilder {
        if (command.contentEquals(PrinterManager.Commands.FONT_B)) currentLineWidth = 42
        if (command.contentEquals(PrinterManager.Commands.FONT_A)) currentLineWidth = 32
        bytes += command
        return this
    }

    fun text(content: String, newLine: Boolean = true): TicketBuilder {
        val clipped = truncateWithEllipsis(content, currentLineWidth)
        bytes += (if (newLine) "$clipped\n" else clipped).toByteArray(charset("ISO-8859-1"))
        return this
    }

    fun divider(): TicketBuilder {
        bytes += ("-".repeat(currentLineWidth) + "\n").toByteArray()
        return this
    }

    fun threeColumnRow(left: String, center: String, right: String): TicketBuilder {
        val total = currentLineWidth

        val colLeftWidth = total / 4
        val colRightWidth = total / 4
        val colCenterWidth = total - colLeftWidth - colRightWidth

        val fLeft = left.take(colLeftWidth).padEnd(colLeftWidth)
        val fCenter = center.take(colCenterWidth).padStart((colCenterWidth + center.length) / 2)
            .padEnd(colCenterWidth)
        val fRight = right.take(colRightWidth).padStart(colRightWidth)

        bytes += (fLeft + fCenter + fRight + "\n").toByteArray(charset("ISO-8859-1"))
        return this
    }

    fun routeRow(left: String, center: String, right: String): TicketBuilder {
        val total = currentLineWidth
        val centerW = 4
        val sideW = (total - centerW) / 2

        val fLeft = formatColumnJustified(left, sideW, false)
        val fCenter = center.padStart((centerW + center.length) / 2).padEnd(centerW)
        val fRight = formatColumnJustified(right, sideW, true)

        bytes += (fLeft + fCenter + fRight + "\n").toByteArray(charset("ISO-8859-1"))
        return this
    }

    fun leftRightRow(left: String, right: String): TicketBuilder {
        val total = currentLineWidth

        val spaceCount = total - left.length - right.length

        val line = if (spaceCount > 0) {
            left + " ".repeat(spaceCount) + right
        } else {
            val availableForLeft = total - right.length - 1
            val clippedLeft =
                if (availableForLeft > 0) truncateWithEllipsis(left, availableForLeft) else ""
            val finalSpace = total - clippedLeft.length - right.length
            clippedLeft + " ".repeat(finalSpace.coerceAtLeast(0)) + right
        }

        val formatted = line.replace("₹", 0x9F.toChar().toString())
        bytes += (formatted + "\n").toByteArray(charset("ISO-8859-1"))
        return this
    }

    private fun formatColumnJustified(text: String, width: Int, isRightAligned: Boolean): String {
        return if (text.length > width) {
            text.take(width - 3) + ".."
        } else {
            if (isRightAligned) text.padEnd(width) else text.padStart(width)
        }
    }

    fun build(): ByteArray {
        return PrinterManager.Commands.INIT +
                PrinterManager.Commands.CHAR_TABLE_PC858 +
                bytes +
                byteArrayOf(0x0A, 0x0A)
    }
}
