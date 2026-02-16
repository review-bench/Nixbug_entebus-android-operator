package com.nixbug.entebus.presentation.screens.summary

import java.time.Instant
import java.time.temporal.ChronoUnit

val now = Instant.now()
val yesterday = Instant.now().minus(1, ChronoUnit.DAYS)

val sampleSummaryData = listOf(

    SummaryUiModel(
        id = 1,
        serviceId = 101,
        serviceName = "Mayyanad -> Kollam",
        startedOn = now.toString(),
        finishedOn = now.plus(30, ChronoUnit.MINUTES).toString(),
        regNo = "KL07AB1234",
        operatorId = 1,
        operatorName = "Rahul",
        collection = 5400,
    ),
    SummaryUiModel(
        id = 2,
        serviceId = 101,
        serviceName = "Mayyanad -> Kollam",
        startedOn = now.toString(),
        finishedOn = now.plus(30, ChronoUnit.MINUTES).toString(),
        regNo = "KL07AB1234",
        operatorId = 2,
        operatorName = "Arjun",
        collection = 6200,
    ),
    SummaryUiModel(
        id = 3,
        serviceId = 102,
        serviceName = "Kottarakkara -> Kollam",
        startedOn = now.toString(),
        finishedOn = now.plus(30, ChronoUnit.MINUTES).toString(),
        regNo = "KL07CD5678",
        operatorId = 3,
        operatorName = "Vivek",
        collection = 4800,
    ),

    // ================= YESTERDAY =================
    SummaryUiModel(
        id = 4,
        serviceId = 110,
        serviceName = "Edava -> Kollam",
        startedOn = yesterday.toString(),
        finishedOn = yesterday.plus(30, ChronoUnit.MINUTES).toString(),
        regNo = "KL07AB1234",
        operatorId = 1,
        operatorName = "Rahul",
        collection = 5100,
    ),
    SummaryUiModel(
        id = 5,
        serviceId = 111,
        serviceName = "Oachira -> Kollam",
        startedOn = yesterday.toString(),
        finishedOn = yesterday.plus(30, ChronoUnit.MINUTES).toString(),
        regNo = "KL07CD5678",
        operatorId = 3,
        operatorName = "Vivek",
        collection = 4500,
    ),
    SummaryUiModel(
        id = 6,
        serviceId = 103,
        serviceName = "Kollam -> Paravur",
        startedOn = yesterday.toString(),
        finishedOn = yesterday.plus(30, ChronoUnit.MINUTES).toString(),
        regNo = "KL07EF9012",
        operatorId = 2,
        operatorName = "Arjun",
        collection = 7000,
    )
)
