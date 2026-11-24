package com.smartgas_frontend.iam.presentation.terms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.smartgas_frontend.shared.i18n.LocalStrings
import com.smartgas_frontend.shared.presentation.components.ContentPage
import com.smartgas_frontend.shared.presentation.components.PanelCard

private class TermsText(es: Boolean) {
    val title = if (es) "Términos y Condiciones" else "Terms and Conditions"
    val updated = if (es) "Última actualización: noviembre de 2026" else "Last updated: November 2026"
    val back = if (es) "Volver" else "Back"
    val sections = if (es) listOf(
        "1. Sobre SmartGas" to "SmartGas es un servicio de FireSecure que monitorea niveles de gas y temperatura en cocinas de hogares y restaurantes mediante sensores IoT, y envía alertas cuando detecta valores peligrosos.",
        "2. Uso del servicio" to "Te comprometes a registrar datos verdaderos, a mantener tus sensores instalados según las indicaciones y a no usar la app para fines distintos a la prevención de incidentes en tu cocina.",
        "3. Alertas y limitaciones" to "Las alertas dependen de que los sensores, la conexión a internet y el celular funcionen correctamente. SmartGas es una herramienta de prevención y no reemplaza a los bomberos, a los servicios de emergencia ni al mantenimiento de tus instalaciones de gas. Ante una fuga, ventila el ambiente y llama al 116.",
        "4. Planes y pagos" to "El plan elegido define cuántas zonas y sensores puedes registrar. Puedes cambiar de plan desde la app; el cambio se aplica en el siguiente periodo de facturación.",
        "5. Política de privacidad" to "Guardamos tu nombre, correo, teléfono, distrito, tus zonas, sensores, lecturas e incidentes para prestarte el servicio. No vendemos tus datos. Puedes pedir la eliminación de tu cuenta escribiendo a soporte@smartgas.pe, de acuerdo con la Ley N.° 29733 de Protección de Datos Personales.",
        "6. Datos en tu celular" to "La app guarda en tu teléfono tu sesión, tus preferencias y el último resumen del Home para funcionar sin conexión. Esta información se borra al cerrar sesión.",
        "7. Cambios en los términos" to "Si cambiamos estos términos te avisaremos dentro de la app. Seguir usando SmartGas significa que aceptas la versión vigente."
    ) else listOf(
        "1. About SmartGas" to "SmartGas is a FireSecure service that monitors gas and temperature levels in home and restaurant kitchens with IoT sensors, and sends alerts when it detects dangerous values.",
        "2. Use of the service" to "You agree to register truthful information, keep your sensors installed as instructed and use the app only to prevent incidents in your kitchen.",
        "3. Alerts and limitations" to "Alerts depend on the sensors, the internet connection and your phone working properly. SmartGas is a prevention tool and does not replace firefighters, emergency services or the maintenance of your gas installation. If there is a leak, ventilate the room and call 116.",
        "4. Plans and payments" to "Your plan defines how many zones and sensors you can register. You can change plans from the app; the change applies from the next billing period.",
        "5. Privacy policy" to "We store your name, email, phone, district, zones, sensors, readings and incidents to provide the service. We do not sell your data. You can request the deletion of your account at soporte@smartgas.pe, under Peruvian Law No. 29733 on Personal Data Protection.",
        "6. Data on your phone" to "The app stores your session, preferences and the latest Home summary on your phone so it can work offline. This information is deleted when you sign out.",
        "7. Changes to these terms" to "If we change these terms we will let you know in the app. Continuing to use SmartGas means you accept the current version."
    )
}

/** Terminos y Condiciones: se abren desde el registro y desde Ajustes. */
@Composable
fun TermsScreen(onBack: (() -> Unit)? = null, edgeToEdge: Boolean = true) {
    val pageText = TermsText(LocalStrings.current.isSpanish)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(if (edgeToEdge) Modifier.systemBarsPadding() else Modifier)
    ) {
        ContentPage {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = pageText.back)
                    }
                }
                Column {
                    Text(
                        pageText.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text(pageText.updated, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            PanelCard(title = pageText.title, icon = Icons.Outlined.Gavel) {
                pageText.sections.forEach { (title, body) ->
                    Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                    Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
