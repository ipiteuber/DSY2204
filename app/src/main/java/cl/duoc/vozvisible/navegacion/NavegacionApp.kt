package cl.duoc.vozvisible.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cl.duoc.vozvisible.ui.screens.AyudaScreen
import cl.duoc.vozvisible.ui.screens.BuscarDispositivoScreen
import cl.duoc.vozvisible.ui.screens.EscribirScreen
import cl.duoc.vozvisible.ui.screens.HablarScreen
import cl.duoc.vozvisible.ui.screens.HomeMenuScreen
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
                    navController.navigate(RutaHomeMenu(usuario.nombre, usuario.correo)) {
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
                    navController.navigate(RutaHomeMenu(usuario.nombre, usuario.correo)) {
                        popUpTo(RutaLogin) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable<RutaRecuperar> {
            RecuperarScreen(onVolver = { navController.popBackStack() })
        }

        composable<RutaHomeMenu> { entrada ->
            val ruta = entrada.toRoute<RutaHomeMenu>()
            HomeMenuScreen(
                nombreUsuario = ruta.nombre,
                correoUsuario = ruta.correo,
                onEscribir = { navController.navigate(RutaEscribir(ruta.correo)) },
                onHablar = { navController.navigate(RutaHablar(ruta.correo)) },
                onBuscarDispositivo = { navController.navigate(RutaBuscarDispositivo(ruta.correo)) },
                onAyuda = { navController.navigate(RutaAyuda) },
                onCerrarSesion = {
                    navController.navigate(RutaLogin) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<RutaEscribir> { entrada ->
            EscribirScreen(
                correoUsuario = entrada.toRoute<RutaEscribir>().correo,
                onVolver = { navController.popBackStack() }
            )
        }

        composable<RutaHablar> { entrada ->
            HablarScreen(
                correoUsuario = entrada.toRoute<RutaHablar>().correo,
                onVolver = { navController.popBackStack() }
            )
        }

        composable<RutaBuscarDispositivo> { entrada ->
            BuscarDispositivoScreen(
                correoUsuario = entrada.toRoute<RutaBuscarDispositivo>().correo,
                onVolver = { navController.popBackStack() }
            )
        }

        composable<RutaAyuda> {
            AyudaScreen(onVolver = { navController.popBackStack() })
        }
    }
}
