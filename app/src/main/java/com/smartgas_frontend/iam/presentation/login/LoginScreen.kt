package com.smartgas_frontend.iam.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smartgas_frontend.R
import com.smartgas_frontend.iam.presentation.components.AuthBenefit
import com.smartgas_frontend.iam.presentation.components.AuthPage
import com.smartgas_frontend.iam.presentation.components.CheckLine
import com.smartgas_frontend.shared.i18n.LocalStrings
import com.smartgas_frontend.shared.presentation.components.FormError
import com.smartgas_frontend.shared.presentation.components.PrimaryButton
import com.smartgas_frontend.shared.presentation.components.TextInput

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoggedIn: () -> Unit,
    onRegister: () -> Unit
) {
    val strings = LocalStrings.current
    val email = viewModel.email.value
    val password = viewModel.password.value
    val remember = viewModel.remember.value
    val error = viewModel.error.value
    val loading = viewModel.loading.value

    AuthPage(
        title = strings.t(R.string.loginTitle),
        text = strings.t(R.string.loginBrandText),
        benefits = listOf(
            AuthBenefit(Icons.Outlined.Bolt, strings.t(R.string.authBenefitRealtime), strings.t(R.string.authBenefitRealtimeText)),
            AuthBenefit(Icons.Outlined.Notifications, strings.t(R.string.authBenefitAlerts), strings.t(R.string.authBenefitAlertsText)),
            AuthBenefit(Icons.Outlined.LocationOn, strings.t(R.string.authBenefitZones), strings.t(R.string.authBenefitZonesText))
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                strings.t(R.string.needAccount),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = onRegister) {
                Text(strings.t(R.string.register), fontWeight = FontWeight.Bold)
            }
        }

        Text(
            strings.t(R.string.login),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() }
        )
        Text(
            strings.t(R.string.loginSubtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        TextInput(
            label = strings.t(R.string.email),
            value = email,
            onValueChange = viewModel::onEmailChanged,
            placeholder = strings.t(R.string.emailPlaceholder),
            leadingIcon = Icons.Outlined.Email,
            keyboardType = KeyboardType.Email
        )

        TextInput(
            label = strings.t(R.string.password),
            value = password,
            onValueChange = viewModel::onPasswordChanged,
            placeholder = strings.t(R.string.passwordPlaceholder),
            leadingIcon = Icons.Outlined.Lock,
            password = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {}) {
                Text(strings.t(R.string.forgotPassword), style = MaterialTheme.typography.bodySmall)
            }
        }

        CheckLine(
            text = strings.t(R.string.rememberDevice),
            checked = remember,
            onCheckedChange = viewModel::onRememberChanged
        )

        if (error != null) {
            FormError(strings.t(error))
        }

        PrimaryButton(
            label = if (loading) strings.t(R.string.loading) else strings.t(R.string.login),
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            loading = loading,
            onClick = { viewModel.login(onLoggedIn) },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "${strings.t(R.string.demoAccess)}: usuario@smartgas.com / smartgas",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(10.dp)
        )
    }
}
