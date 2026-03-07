package com.nixbug.entebus.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Converts a UTC timestamp string to Indian Standard Time (IST) and formats it
 * into a user-friendly date label and time string.
 *
 * This helper:
 * - Safely parses the given UTC timestamp (`ISO-8601` format) into an [Instant]
 * - Converts it to the `Asia/Kolkata` time zone
 * - Formats the time in 12-hour format (`hh:mm a`)
 * - Returns a human-readable date label:
 *   - `"Today"` if the date is the current IST date
 *   - `"Yesterday"` if the date is one day before the current IST date
 *   - `"Tomorrow"` if the date is one day after the current IST date
 *   - Otherwise, formats the date as `MMM dd yyyy`
 *
 * @param utcTimestamp UTC timestamp string in ISO-8601 format
 *                     (e.g. `"2026-01-15T08:30:00Z"`).
 *                     If `null` or malformed, the function returns `null`.
 *
 * @return A [Pair] where:
 * - `first` is the formatted date label
 * - `second` is the formatted time in IST
 * Returns `null` if parsing fails or if [utcTimestamp] is `null`.
 */
fun utcToIstConverter(utcTimestamp: String?): Pair<String, String>? {
    if (utcTimestamp == null) return null

    return try {
        val instant = Instant.parse(utcTimestamp)
        val istZone = ZoneId.of("Asia/Kolkata")
        val dateTime = instant.atZone(istZone)

        val date = dateTime.toLocalDate()
        val time = dateTime
            .toLocalTime()
            .format(DateTimeFormatter.ofPattern("hh:mm a"))

        val today = LocalDate.now(istZone)
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)

        val dateLabel = when (date) {
            today -> "Today"
            yesterday -> "Yesterday"
            tomorrow -> "Tomorrow"
            else -> date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
        }

        Pair(dateLabel, time)
    } catch (e: Exception) {
        null
    }
}

fun utcToIstWithActualDate(utcTimestamp: String?): Pair<String, String>? {
    if (utcTimestamp == null) return null

    return try {
        val instant = Instant.parse(utcTimestamp)
        val istZone = ZoneId.of("Asia/Kolkata")
        val dateTime = instant.atZone(istZone)

        val date = dateTime
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))

        val time = dateTime
            .toLocalTime()
            .format(DateTimeFormatter.ofPattern("hh:mm a"))

        Pair(date, time)

    } catch (e: Exception) {
        null
    }
}
