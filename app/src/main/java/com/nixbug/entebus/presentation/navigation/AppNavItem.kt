package com.nixbug.entebus.presentation.navigation

sealed class AppNavItem(val route: String) {
    data object Auth : AppNavItem("authScreen")
    data object LocationRequest : AppNavItem("locationRequestScreen")
    data object MainScreen : AppNavItem("mainScreen")
    data object TicketScreen : AppNavItem("ticketScreen")
}
