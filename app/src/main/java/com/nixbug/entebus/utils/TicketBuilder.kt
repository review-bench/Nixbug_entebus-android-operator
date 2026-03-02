package com.nixbug.entebus.utils

class TicketBuilder {
    private var bytes = byteArrayOf()
    private var currentLineWidth = 32 // Default for Font A
    private fun clip(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            // Take enough characters to fit "..." at the end
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

    /** Text with automatic clipping if it exceeds the line width */
    fun text(content: String, newLine: Boolean = true): TicketBuilder {
        val clipped = clip(content, currentLineWidth)
        val formatted = clipped.replace("₹", 0x9F.toChar().toString())
        bytes += (if (newLine) "$formatted\n" else formatted).toByteArray(charset("ISO-8859-1"))
        return this
    }

    fun divider(): TicketBuilder {
        bytes += ("-".repeat(currentLineWidth) + "\n").toByteArray()
        return this
    }

    fun row3Column(left: String, center: String, right: String): TicketBuilder {
        val total = currentLineWidth
        // We give more space to the center column for Date/Time
        val colLeftWidth = total / 4          // ~8 chars
        val colRightWidth = total / 4         // ~8 chars
        val colCenterWidth = total - colLeftWidth - colRightWidth // Remaining ~16 chars

        val fLeft = left.take(colLeftWidth).padEnd(colLeftWidth)
        val fCenter = center.take(colCenterWidth).padStart((colCenterWidth + center.length) / 2)
            .padEnd(colCenterWidth)
        val fRight = right.take(colRightWidth).padStart(colRightWidth)

        bytes += (fLeft + fCenter + fRight + "\n").toByteArray(charset("ISO-8859-1"))
        return this
    }

    fun rowLandmarks(left: String, center: String, right: String): TicketBuilder {
        val total = currentLineWidth
        val centerW = 4
        val sideW = (total - centerW) / 2

        // Format each column
        val fLeft = formatColumn(left, sideW, false)
        val fCenter = center.padStart((centerW + center.length) / 2).padEnd(centerW)
        val fRight = formatColumn(right, sideW, true)

        bytes += (fLeft + fCenter + fRight + "\n").toByteArray(charset("ISO-8859-1"))
        return this
    }

    /** Helper to truncate text with ellipsis */
    private fun formatColumn(text: String, width: Int, isRightAligned: Boolean): String {
        return if (text.length > width) {
            // Clip with ellipsis if it overflows
            text.take(width - 3) + ".."
        } else {
            // Pad to fill the exact column width
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