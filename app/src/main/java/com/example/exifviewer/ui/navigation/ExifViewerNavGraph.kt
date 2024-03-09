package com.example.exifviewer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.exifviewer.ui.edit.EditDestination
import com.example.exifviewer.ui.edit.EditScreen
import com.example.exifviewer.ui.home.HomeDestination
import com.example.exifviewer.ui.home.HomeEmptyDestination
import com.example.exifviewer.ui.home.HomeEmptyScreen
import com.example.exifviewer.ui.home.HomeScreen
import com.example.exifviewer.ui.viewer.ViewerDestination
import com.example.exifviewer.ui.viewer.ViewerScreen
import com.microsoft.device.dualscreen.twopanelayout.Screen
import com.microsoft.device.dualscreen.twopanelayout.TwoPaneLayoutNav
import com.microsoft.device.dualscreen.twopanelayout.TwoPaneMode
import com.microsoft.device.dualscreen.twopanelayout.twopanelayoutnav.composable

interface NavigationDestination {
    val route: String
    val titleResourceId: Int
}

@Composable
fun ExifViewerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    TwoPaneLayoutNav(
        navController = navController,
        paneMode = TwoPaneMode.HorizontalSingle,
        singlePaneStartDestination = HomeDestination.route,
        pane1StartDestination = HomeDestination.route,
        pane2StartDestination = HomeEmptyDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateTo = {
                    navController.navigateTo("${ViewerDestination.route}/${it}", Screen.Pane2)
                },
            )
        }
        composable(route = HomeEmptyDestination.route) {
            if (isSinglePane)
                navController.navigateTo(HomeDestination.route, Screen.Pane1)
            else
                HomeEmptyScreen()
        }
        composable(
            route = ViewerDestination.fullRoute,
        ) { twoPaneBackStack ->
            // Extracting the argument
            val uriArg =
                twoPaneBackStack.arguments?.getString(ViewerDestination.uriArg).toString()

            ViewerScreen(
                { navController.navigateBack() },
                { navController.navigateTo("${EditDestination.route}/${it}", Screen.Pane2) },
                uriArg
            )
        }
        composable(
            route = EditDestination.fullRoute,
        ) { twoPaneBackStack ->
            // Extracting the argument
            val uriArg =
                twoPaneBackStack.arguments?.getString(ViewerDestination.uriArg).toString()

            EditScreen(
                { navController.navigateBack() },
                uriArg
            )
        }
    }
}