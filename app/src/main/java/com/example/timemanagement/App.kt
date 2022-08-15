package com.example.timemanagement

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timemanagement.loginScreen.LoginScreen
import com.example.timemanagement.loginScreen.LoginViewModel
import com.example.timemanagement.theme.TimeManagementTheme

@Composable
fun App() {
    val loginViewModel = viewModel(modelClass = LoginViewModel::class.java)

    TimeManagementTheme{
        Surface(color = MaterialTheme.colors.background) {
            Navigation(loginViewModel = loginViewModel)

        }
    }
}