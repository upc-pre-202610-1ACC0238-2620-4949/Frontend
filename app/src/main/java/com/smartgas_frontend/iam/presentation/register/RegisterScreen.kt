package com.smartgas_frontend.iam.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.smartgas_frontend.R
import com.smartgas_frontend.iam.presentation.components.AuthBenefit
import com.smartgas_frontend.iam.presentation.components.AuthPage
import com.smartgas_frontend.iam.presentation.components.CheckLine
import com.smartgas_frontend.shared.i18n.LocalStrings
import com.smartgas_frontend.shared.presentation.components.DropdownField
import com.smartgas_frontend.shared.presentation.components.FormError
import com.smartgas_frontend.shared.presentation.components.PrimaryButton
import com.smartgas_frontend.shared.presentation.components.TextInput
import com.smartgas_frontend.ui.theme.Green

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onGoToDashboard: () -> Unit,
    onLogin: () -> Unit
) {
    val strings = LocalStrings.current
    val form = viewModel.form.value
    val error = viewModel.error.value
    val success = viewModel.success.value
    val loading = viewModel.loading.value

    val accountTypeOptions = listOf(
        strings.t(R.string.commercialAccount) to "commercial",
        strings.t(R.string.domesticAccount) to "domestic"
    )

    AuthPage(
        title = strings.t(R.string.registerTitle),
        text = strings.t(R.string.registerBrandText),
        benefits = listOf(
            AuthBenefit(Icons.Outlined.Shield, strings.t(R.string.accountVerified), strings.t(R.string.authBenefitRealtimeText)),
            AuthBenefit(Icons.Outlined.CheckCircle, strings.t(R.string.dashboardReady), strings.t(R.string.authBenefitZonesText))
        )
    ) {
        if (success) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Green),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White)
            }
            Text(
                strings.t(R.string.accountCreatedTitle),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                strings.t(R.string.accountCreatedText),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            PrimaryButton(
                label = strings.t(R.string.goToDashboard),
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = onGoToDashboard,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    strings.t(R.string.alreadyAccount),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onLogin) {
                    Text(strings.t(R.string.login), fontWeight = FontWeight.Bold)
                }
            }

            Text(
                strings.t(R.string.register),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                strings.t(R.string.registerSubtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextInput(
                label = strings.t(R.string.fullName),
                value = form.fullName,
                onValueChange = { viewModel.onFormChanged(form.copy(fullName = it)) },
                placeholder = strings.t(R.string.fullNamePlaceholder),
                leadingIcon = Icons.Outlined.Person
            )
            TextInput(
                label = strings.t(R.string.email),
                value = form.email,
                onValueChange = { viewModel.onFormChanged(form.copy(email = it)) },
                placeholder = strings.t(R.string.emailPlaceholder),
                leadingIcon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email
            )
            DropdownField(
                label = strings.t(R.string.accountType),
                options = accountTypeOptions,
                selected = form.accountType,
                onSelected = { viewModel.onFormChanged(form.copy(accountType = it)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextInput(
                label = strings.t(R.string.businessName),
                value = form.businessName,
                onValueChange = { viewModel.onFormChanged(form.copy(businessName = it)) },
                placeholder = strings.t(R.string.businessNamePlaceholder),
                leadingIcon = Icons.Outlined.Business
            )
            TextInput(
                label = strings.t(R.string.password),
                value = form.password,
                onValueChange = { viewModel.onFormChanged(form.copy(password = it)) },
                leadingIcon = Icons.Outlined.Lock,
                password = true
            )
            TextInput(
                label = strings.t(R.string.confirmPassword),
                value = form.confirmPassword,
                onValueChange = { viewModel.onFormChanged(form.copy(confirmPassword = it)) },
                leadingIcon = Icons.Outlined.CheckCircle,
                password = true
            )

            CheckLine(
                text = strings.t(R.string.termsText),
                checked = form.acceptedTerms,
                onCheckedChange = { viewModel.onFormChanged(form.copy(acceptedTerms = it)) }
            )

            if (error != null) {
                FormError(strings.t(error))
            }

            PrimaryButton(
                label = if (loading) strings.t(R.string.loading) else strings.t(R.string.register),
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                loading = loading,
                onClick = viewModel::register,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
