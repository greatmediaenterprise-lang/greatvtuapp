package com.example.greatvtu.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.greatvtu.util.BiometricHelper
import com.example.greatvtu.viewmodel.AuthState
import com.example.greatvtu.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    biometricHelper: BiometricHelper,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (authState is AuthState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { viewModel.signUp(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (biometricHelper.isBiometricAvailable()) {
                TextButton(
                    onClick = {
                        biometricHelper.showBiometricPrompt(
                            activity = context as FragmentActivity,
                            onSuccess = {
                                onLoginSuccess()
                            },
                            onError = { _, err -> /* Handle error */ },
                            onFailed = { /* Handle failure */ }
                        )
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Fingerprint,
                        contentDescription = "Biometric Login"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Login with Biometrics")
                }
            }
        }

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        LaunchedEffect(authState) {
            if (authState is AuthState.Success) {
                onLoginSuccess()
            }
        }
    }
}
