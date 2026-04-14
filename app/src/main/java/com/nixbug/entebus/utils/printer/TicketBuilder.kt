package com.nixbug.entebus.utils.printer

import java.io.ByteArrayOutputStream

class TicketBuilder {
    private val outputStream = ByteArrayOutputStream()
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

        outputStream.write(command)
        return this
    }

    fun text(content: String, newLine: Boolean = true): TicketBuilder {
        val clipped = truncateWithEllipsis(content, currentLineWidth)
        val stringToWrite = if (newLine) "$clipped\n" else clipped
        outputStream.write(stringToWrite.toByteArray(charset("UTF-8")))
        return this
    }

    fun divider(): TicketBuilder {
        val line = "-".repeat(currentLineWidth) + "\n"
        outputStream.write(line.toByteArray(charset("UTF-8")))
        return this
    }

    fun threeColumnRow(left: String, center: String, right: String): TicketBuilder {
        val total = currentLineWidth
        val colLeftWidth = 10
        val colRightWidth = 10
        val colCenterWidth = total - colLeftWidth - colRightWidth

        val fLeft = left.take(colLeftWidth).padEnd(colLeftWidth)
        val clippedCenter = center.take(colCenterWidth)
        val fCenter = clippedCenter
            .padStart((colCenterWidth + clippedCenter.length) / 2)
            .padEnd(colCenterWidth)
        val fRight = right.take(colRightWidth).padStart(colRightWidth)

        outputStream.write((fLeft + fCenter + fRight + "\n").toByteArray(charset("UTF-8")))
        return this
    }

    fun routeRow(left: String, center: String, right: String): TicketBuilder {
        val total = currentLineWidth
        val centerW = 4
        val sideW = (total - centerW) / 2

        val fLeft = formatColumnJustified(left, sideW, false)// right align
        val fCenter = center.padStart((centerW + center.length) / 2).padEnd(centerW)
        val fRight = formatColumnJustified(right, sideW, true)// left align

        // Intentionally align route text towards the center arrow
        outputStream.write((fLeft + fCenter + fRight + "\n").toByteArray(charset("UTF-8")))
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

        outputStream.write((line + "\n").toByteArray(charset("UTF-8")))
        return this
    }

    private fun formatColumnJustified(text: String, width: Int, alignLeft: Boolean): String {
        return if (text.length > width) {
            text.take(width - 2) + ".."
        } else {
            if (alignLeft) text.padEnd(width) else text.padStart(width)
        }
    }

    fun build(): ByteArray {
        val finalResult = ByteArrayOutputStream()
        finalResult.write(PrinterManager.Commands.INIT)
        finalResult.write(outputStream.toByteArray())
        finalResult.write(byteArrayOf(0x0A, 0x0A))

        return finalResult.toByteArray()
    }
}
