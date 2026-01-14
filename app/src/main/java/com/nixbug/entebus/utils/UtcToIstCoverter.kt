package com.nixbug.entebus.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun utcToIstConverter(utcTimestamp: String?): Pair<String, String>? {
    if (utcTimestamp == null) return null
    val instant = Instant.parse(utcTimestamp)
    val istZone = ZoneId.of("Asia/Kolkata")
    val dateTime = instant.atZone(istZone)

    val date = dateTime.toLocalDate()
    val time = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a"))

    val today = LocalDate.now(istZone)
    val yesterday = today.minusDays(1)
    val tomorrow = today.plusDays(1)

    val dateLabel = when (date) {
        today -> "Today"
        yesterday -> "Yesterday"
        tomorrow -> "Tomorrow"
        else -> date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
    }
    return Pair(dateLabel, time)
}
