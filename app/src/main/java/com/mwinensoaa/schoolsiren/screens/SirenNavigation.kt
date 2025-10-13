package com.mwinensoaa.schoolsiren.screens



import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp






@Composable
fun SirenAppNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem("About", Icons.Default.Person, "developer"),
        BottomNavItem("New Alarm", Icons.Default.AddAlarm, "new_alarm"),
        BottomNavItem("Alarms", Icons.Default.List, "alarm_list")
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
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
                .pointerInput(currentRoute) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        // dragAmount < 0 means swipe left → go to next screen
                        // dragAmount > 0 means swipe right → go to previous screen
                        if (dragAmount < -80) { // swipe left
                            when (currentRoute) {
                                "developer" -> navController.navigate("new_alarm")
                                "new_alarm" -> navController.navigate("alarm_list")
                            }
                        } else if (dragAmount > 80) { // swipe right
                            when (currentRoute) {
                                "alarm_list" -> navController.navigate("new_alarm")
                                "new_alarm" -> navController.navigate("developer")
                            }
                        }
                    }
                }
        ) {
            composable("developer") { AboutScreen() }
            composable("new_alarm") { ScheduleAlarmScreen() }
            composable("alarm_list") { AlarmListScreen() }
        }
    }
}


data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)



