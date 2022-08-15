package com.example.timemanagement.loginScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagement.theme.TimeManagementTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel? = null,
    onNavToHomePage: () -> Unit,
    onNavToSignUpPage: () -> Unit,
) {
    val loginUiState = viewModel?.loginUiState
    val isError = loginUiState?.loginError != null
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.h3,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colors.primary
        )

        if (isError) {
            Text(
                text = loginUiState?.loginError ?: "unknown error",
                color = Color.Red,
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = loginUiState?.userName ?: "",
            onValueChange = { viewModel?.onUserNameChange(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Email")
            },
            isError = isError
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = loginUiState?.password ?: "",
            onValueChange = { viewModel?.onPasswordNameChange(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )

        Button(onClick = { viewModel?.loginUser(context) }) {
            Text(text = "Sign In")
        }
        Spacer(modifier = Modifier.size(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(text = "Don't have an Account?")
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = { onNavToSignUpPage.invoke() }) {
                Text(text = "SignUp")
            }


        }

        if (loginUiState?.isLoading == true) {
            CircularProgressIndicator()
        }

        LaunchedEffect(key1 = viewModel?.hasUser) {
            if (viewModel?.hasUser == true) {
                onNavToHomePage.invoke()
            }
        }


    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview4() {
    TimeManagementTheme {
        Surface(color = MaterialTheme.colors.background) {
            LoginScreen(onNavToHomePage = { /*TODO*/ }) {

            }
        }
    }

}