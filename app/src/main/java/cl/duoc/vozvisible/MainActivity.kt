package cl.duoc.vozvisible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cl.duoc.vozvisible.data.Repositorios
import cl.duoc.vozvisible.navegacion.NavegacionApp
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Repositorios.inicializar(applicationContext)
        setContent {
            VozVisibleTheme {
                // safeDrawing deja libre la barra de estado, el notch y la de navegacion:
                // desde Android 15 el modo borde a borde es obligatorio y sin esto el
                // contenido queda tapado por la parte superior del dispositivo.
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                ) {
                    NavegacionApp()
                }
            }
        }
    }
}
