package cl.duoc.vozvisible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.duoc.vozvisible.navegacion.NavegacionApp
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VozVisibleTheme {
                NavegacionApp()
            }
        }
    }
}
