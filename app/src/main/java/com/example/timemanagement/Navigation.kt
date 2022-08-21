package com.example.timemanagement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timemanagement.homeScreen.HomeViewModel
import com.example.timemanagement.loginScreen.LoginScreen
import com.example.timemanagement.loginScreen.LoginViewModel
import com.example.timemanagement.loginScreen.SignUpScreen

enum class LoginRoutes {
    Signup,
    SignIn
}

enum class HomeRoutes {
    Home,
}
sealed class Screen(val route: String?, val title: String?, val icon: ImageVector?) {
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Home : Screen("Home", "Home", Icons.Default.Home)
    object SignIn : Screen("SignIn", "SignIn", Icons.Default.Person)
    object SignUp : Screen("SignUp", "SignUp", Icons.Default.Person)


}
val items = listOf(
    Screen.Home,
    Screen.Profile,


    )

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SignIn.route!!
    ) {
//        composable(route = LoginRoutes.SignIn.name) {
        composable(route = Screen.SignIn.route!!) {
            LoginScreen(onNavToHomePage = {
                navController.navigate(Screen.Home.route!!) {
                    launchSingleTop = true
                    popUpTo(route = Screen.SignIn.route!!) {
                        inclusive = true
                    }
                }
            },
                viewModel = loginViewModel

            ) {
                navController.navigate(Screen.SignUp.route!!) {
                    launchSingleTop = true
                    popUpTo(Screen.SignIn.route!!) {
                        inclusive = true
                    }
                }
            }
        }

//        composable(route = LoginRoutes.Signup.name) {
        composable(route = Screen.SignUp.route!!) {
            SignUpScreen(onNavToHomePage = {
                navController.navigate(Screen.Home.route!!) {
                    popUpTo(Screen.SignUp.route!!) {
                        inclusive = true
                    }
                }
            },
                viewModel = loginViewModel
            ) {
                navController.navigate(Screen.SignIn.route!!)
            }

        }

//        composable(route = HomeRoutes.Home.name) {
        composable(route = Screen.Home.route!!) {
            HomeScreen(viewModel = homeViewModel, navController = navController)
        }

    }


}
