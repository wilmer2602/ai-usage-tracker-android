package com.wilmer2602.aiusage.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wilmer2602.aiusage.ui.screens.add.AddEventScreen
import com.wilmer2602.aiusage.ui.screens.home.HomeScreen
import com.wilmer2602.aiusage.ui.screens.profile.ProfileScreen
import com.wilmer2602.aiusage.ui.screens.stats.StatsScreen
import com.wilmer2602.aiusage.viewmodel.ProfileViewModel
import com.wilmer2602.aiusage.viewmodel.UsageViewModel

@Composable
fun MainScreen(
    usageViewModel: UsageViewModel,
    profileViewModel: ProfileViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Add,
        BottomNavItem.Stats,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen(usageViewModel) }
            composable(BottomNavItem.Add.route) { AddEventScreen(usageViewModel) }
            composable(BottomNavItem.Stats.route) { StatsScreen(usageViewModel) }
            composable(BottomNavItem.Profile.route) { ProfileScreen(profileViewModel) }
        }
    }
}

sealed class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.ImageVector) {
    object Home : BottomNavItem("home", "记录", androidx.compose.material.icons.Icons.Filled.List)
    object Add : BottomNavItem("add", "添加", androidx.compose.material.icons.Icons.Filled.Add)
    object Stats : BottomNavItem("stats", "统计", androidx.compose.material.icons.Icons.Filled.BarChart)
    object Profile : BottomNavItem("profile", "画像", androidx.compose.material.icons.Icons.Filled.AccountCircle)
}
