package com.example.autorization.ui.login

import android.app.ActionBar
import android.app.Activity
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.autorization.R
import com.example.autorization.network.APPID
import com.example.autorization.network.WeatherReport
import com.example.autorization.network.getNetworkService
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar()?.setHomeAsUpIndicator(upArrow);

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            } else {
                username.error = null
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            } else {
                password.error = null
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                hideKeyboard(this)
                fetchWeatherData()
            }
            setResult(Activity.RESULT_OK)
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                                username.text.toString(),
                                password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            R.id.action_create -> {
                Toast.makeText(applicationContext, R.string.todo, Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val displayName = model.displayName
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun fetchWeatherData() {
        val city = "Kazan,ru"
        val call = getNetworkService().getWeatherApi()?.getWeatherForCity(city, APPID)
        val contextView : View = findViewById(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loading.visibility = View.VISIBLE
        call?.enqueue(object : Callback<WeatherReport?> {
            override fun onFailure(call: Call<WeatherReport?>, t: Throwable) {
                loading.visibility = View.GONE
                Snackbar.make(contextView, t.localizedMessage, Snackbar.LENGTH_SHORT).show();
            }

            override fun onResponse(
                call: Call<WeatherReport?>,
                response: Response<WeatherReport?>
            ) {
                loading.visibility = View.GONE
                if (response.code() == 200) {
                    val weatherResponse = response.body()!!
                    val text = (weatherResponse.main.temp - 273).roundToInt().toString() + "℃ " + weatherResponse.weather[0].main
                    Snackbar.make(contextView, text, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(contextView, R.string.error_message, Snackbar.LENGTH_SHORT).show();
                }
            }

        })

    }

}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
