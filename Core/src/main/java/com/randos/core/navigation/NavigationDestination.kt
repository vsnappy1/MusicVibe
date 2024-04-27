package com.randos.core.navigation

/**
 * An interface to be implemented by each screen in this project to facilitate ease and consisted
 * architecture for navigation.
 */
interface NavigationDestination {
    /**
     * Name of the screen.
     */
    val name: String

    /**
     * Route for the screen.
.     */
    val route: String
}

/**
 * An extension of [NavigationDestination] with a parameter.
 */
interface NavigationDestinationWithParams : NavigationDestination {
    /**
     * Name of parameter (i.e. index, id, etc...)
     */
    val param: String

    /**
     * Route with parameter
     */
    val routeWithParams: String
        get() = "$route/{$param}"
}