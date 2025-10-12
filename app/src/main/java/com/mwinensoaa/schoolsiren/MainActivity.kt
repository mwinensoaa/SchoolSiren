package com.mwinensoaa.schoolsiren

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mwinensoaa.schoolsiren.alarm.AlarmScheduler
import com.mwinensoaa.schoolsiren.alarm.AlarmViewModel
import com.mwinensoaa.schoolsiren.screens.AlarmListScreen
import com.mwinensoaa.schoolsiren.screens.ScheduleAlarmScreen
import com.mwinensoaa.schoolsiren.screens.SirenAppNavigation
import com.mwinensoaa.schoolsiren.ui.theme.SchoolSirenTheme



class MainActivity : ComponentActivity() {

    private val bellViewModel: AlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchoolSirenTheme {
               // SchoolSirenApp(bellViewModel)
                SirenAppNavigation()
            }
        }
    }
}



@Composable
fun SchoolSirenApp(viewModel: AlarmViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {
        composable("schedule") {
            ScheduleAlarmScreen(
                onSaved = { navController.navigate("list") },
                viewModel
            )
        }
        composable("list") {
            AlarmListScreen(
                onBack = { navController.navigate("schedule") }
            )
        }
    }
}
