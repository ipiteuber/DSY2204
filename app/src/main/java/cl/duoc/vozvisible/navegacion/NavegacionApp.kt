package cl.duoc.vozvisible.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cl.duoc.vozvisible.ui.screens.InicioScreen
import cl.duoc.vozvisible.ui.screens.LoginScreen
import cl.duoc.vozvisible.ui.screens.RecuperarScreen
import cl.duoc.vozvisible.ui.screens.RegistroScreen

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RutaLogin) {

        composable<RutaLogin> {
            LoginScreen(
                onIngresar = { usuario ->
                    navController.navigate(RutaInicio(usuario.nombre)) {
                        // Saca el login de la pila para que el boton atras no vuelva a el
                        popUpTo(RutaLogin) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(RutaRegistro) },
                onIrARecuperar = { navController.navigate(RutaRecuperar) }
            )
        }

        composable<RutaRegistro> {
            RegistroScreen(
                onRegistrado = { usuario ->
                    navController.navigate(RutaInicio(usuario.nombre)) {
                        popUpTo(RutaLogin) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable<RutaRecuperar> {
            RecuperarScreen(onVolver = { navController.popBackStack() })
        }

        composable<RutaInicio> { entrada ->
            val ruta = entrada.toRoute<RutaInicio>()
            InicioScreen(
                nombreUsuario = ruta.nombre,
                onCerrarSesion = {
                    navController.navigate(RutaLogin) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
