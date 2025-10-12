package com.mwinensoaa.schoolsiren.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun SirenAppNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem("Dev", Icons.Default.Person, "developer"),
        BottomNavItem("New Alarm", Icons.Default.AddAlarm, "new_alarm"),
        BottomNavItem("Alarms", Icons.Default.List, "alarm_list")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                val currentDestination = navController.currentBackStackEntryAsState().value?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.route == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                // Avoid building multiple copies of the same destination
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "new_alarm",
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            composable("developer") { AboutScreen() }
            composable("new_alarm") { ScheduleAlarmScreen(onSaved = {

            }) }
            composable("alarm_list") { AlarmListScreen(onBack = {}) }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)



