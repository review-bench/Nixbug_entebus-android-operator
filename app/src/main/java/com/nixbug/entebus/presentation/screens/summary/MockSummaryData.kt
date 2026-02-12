package com.nixbug.entebus.presentation.screens.summary

val sampleSummaryData = listOf(

    // ================= TODAY (All isAudited = false) =================
    SummaryUiModel(
        id = 1,
        serviceId = 101,
        serviceName = "Mayyanad -> Kollam",
        startedOn = "2026-02-12T08:00:00Z",
        finishedOn = "2026-02-12T11:30:00Z",
        regNO = "KL07AB1234",
        operatorId = 1,
        operatorName = "Rahul",
        collection = 5400,
        isAudited = false
    ),
    SummaryUiModel(
        id = 2,
        serviceId = 101, // Same service, different operator
        serviceName = "Mayyanad -> Kollam",
        startedOn = "2026-02-12T08:00:00Z",
        finishedOn = "2026-02-12T11:30:00Z",
        regNO = "KL07AB1234",
        operatorId = 2,
        operatorName = "Arjun",
        collection = 6200,
        isAudited = false
    ),
    SummaryUiModel(
        id = 3,
        serviceId = 102,
        serviceName = "Kottarakkara -> Kollam",
        startedOn = "2026-02-12T04:15:00Z",
        finishedOn = "2026-02-12T09:00:00Z",
        regNO = "KL07CD5678",
        operatorId = 3,
        operatorName = "Vivek",
        collection = 4800,
        isAudited = false
    ),

    // ================= YESTERDAY =================
    SummaryUiModel(
        id = 4,
        serviceId = 110,
        serviceName = "Edava -> Kollam",
        startedOn = "2026-02-11T03:45:00Z",
        finishedOn = "2026-02-11T07:30:00Z",
        regNO = "KL07AB1234",
        operatorId = 1,
        operatorName = "Rahul",
        collection = 5100,
        isAudited = true
    ),
    SummaryUiModel(
        id = 5,
        serviceId = 111,
        serviceName = "Oachira -> Kollam",
        startedOn = "2026-02-11T05:00:00Z",
        finishedOn = "2026-02-11T10:00:00Z",
        regNO = "KL07CD5678",
        operatorId = 3,
        operatorName = "Vivek",
        collection = 4500,
        isAudited = true
    ),
    SummaryUiModel(
        id = 6,
        serviceId = 103,
        serviceName = "Kollam -> Paravur",
        startedOn = "2026-02-08T06:00:00Z",
        finishedOn = "2026-02-09T12:15:00Z",
        regNO = "KL07EF9012",
        operatorId = 2,
        operatorName = "Arjun",
        collection = 7000,
        isAudited = false
    )
)