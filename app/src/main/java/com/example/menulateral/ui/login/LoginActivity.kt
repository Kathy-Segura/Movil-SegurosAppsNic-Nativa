package com.example.menulateral.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.menulateral.MenuPrincipal
import com.example.menulateral.R
import com.example.menulateral.Registrate
import com.example.menulateral.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        // Observador del estado del formulario de inicio de sesión
        loginViewModel.loginFormState.observe(this@LoginActivity, Observer { loginState ->
            loginState ?: return@Observer

            // Habilitar o deshabilitar el botón de inicio de sesión
            login?.isEnabled = loginState.isDataValid

            // Mostrar errores en los campos
            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        // Observador del resultado del inicio de sesión
        loginViewModel.loginResult.observe(this@LoginActivity, Observer { loginResult ->
            val loginResult = loginResult ?: return@Observer

            // Ocultar el indicador de carga
            loading?.visibility = View.GONE

            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                // Opcional: Finalizar LoginActivity para que no se pueda regresar
                finish()
            }
        })

        // Configurar el botón de inicio de sesión
        login?.setOnClickListener {
            loading?.visibility = View.VISIBLE // Mostrar el indicador de carga
            loginViewModel.login(username.text.toString(), password.text.toString())
        }

        // Configurar el botón de login
        val btnMenu= findViewById<Button>(R.id.btn_login)
        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuPrincipal::class.java)
            startActivity(intent)
        }

        // Configurar el botón de registro
        val btnRegistro = findViewById<TextView>(R.id.registro)
        btnRegistro.setOnClickListener {
            val intent = Intent(this, Registrate::class.java)
            startActivity(intent)
        }

        // Configurar listeners para los campos de texto
        username.afterTextChanged {
            loginViewModel.loginDataChanged(username.text.toString(), password.text.toString())
        }

        password.afterTextChanged {
            loginViewModel.loginDataChanged(username.text.toString(), password.text.toString())
        }

        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
            false
        }
    }

    // Actualizar UI con la información del usuario
    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    // Mostrar un mensaje de error en el inicio de sesión
    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

// Extensión para simplificar la configuración de TextWatcher en EditText
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}