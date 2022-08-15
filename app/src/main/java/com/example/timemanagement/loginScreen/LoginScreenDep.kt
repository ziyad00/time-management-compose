package com.example.timemanagement.loginScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagement.theme.TimeManagementTheme

@Composable
fun LoginScreen2(
    viewModel: LoginViewModel = viewModel()
) {
    //val uiState by viewModel.uiState
    val scaffoldState = rememberScaffoldState()
    Scaffold(scaffoldState = scaffoldState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(value = "Email", onValueChange = {}, label = { Text(text = "Title") })
            Spacer(modifier = Modifier.padding(10.dp) )
            TextField(value = "Password", onValueChange = {}, label = { Text(text = "Title") })
            Spacer(modifier = Modifier.padding(10.dp) )

            Button(onClick = { /*TODO*/ }) {
                Text(text = "Login")
            }

        }
    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview3() {
    TimeManagementTheme {
        Surface(color = MaterialTheme.colors.background) {
            LoginScreen2()
        }
    }

}