package com.randos.core.navigation

interface NavigationDestination {
    val name: String
    val route: String
}

interface NavigationDestinationWithParams : NavigationDestination {
    val argument: String
    val routeWithParams: String
        get() = "$route/{$argument}"
}