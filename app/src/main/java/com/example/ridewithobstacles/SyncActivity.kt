package com.example.ridewithobstacles

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ridewithobstacles.ApiService.ApiRepository
import com.example.ridewithobstacles.ApiService.DataModels.LoginRequest
import com.example.ridewithobstacles.ApiService.DataModels.RegisterRequest
import com.example.ridewithobstacles.ApiService.DataModels.RecordDto
import com.example.ridewithobstacles.Database.DbHelper
import com.example.ridewithobstacles.Database.Models.UserModel
import com.example.ridewithobstacles.Database.Repositories.Implementations.RideRepository
import com.example.ridewithobstacles.Database.Repositories.Implementations.UserRepository
import kotlinx.coroutines.launch

class SyncActivity : AppCompatActivity() {
    private lateinit var user:UserModel
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var operationStatusTextView: TextView
    private lateinit var userSynced: TextView
    private lateinit var apiRepository: ApiRepository
    private lateinit var rideRepository: RideRepository
    private lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sync)
        user = intent.getSerializableExtra("user") as UserModel
        apiRepository = ApiRepository()
        val dbHelper = DbHelper(this)
        val db = dbHelper.writableDatabase
        rideRepository = RideRepository(db)
        userRepository = UserRepository(db)

        val menuButton: Button = findViewById(R.id.mainMenuButton)
        val authorizeButton: Button = findViewById(R.id.authorizeButton)
        val registerButton: Button = findViewById(R.id.registerButton)
        val syncDataButton:Button = findViewById(R.id.syncDataButton)
        val showPasswordCheckbox: CheckBox = findViewById(R.id.checkboxShowPassword)
        operationStatusTextView = findViewById(R.id.operationStatusTextView)
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        usernameEditText.setText(user.username)
        passwordEditText.setText(user.password)
        userSynced = findViewById(R.id.userStatusTextView)

        if(user.synced) {
            userSynced.setText("Пользователь синхронизирован")
        }
        else{
            userSynced.setText("Пользователь не синхронизирован")
        }


        showPasswordCheckbox.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked){
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            else{
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        authorizeButton.setOnClickListener{
            lifecycleScope.launch {
                user.username = usernameEditText.text.toString()
                user.password = passwordEditText.text.toString()
                operationStatusTextView.setText("Выполняется авторизация ... ")
                val response = apiRepository.login(LoginRequest(user.username, user.password)) ?: throw IllegalArgumentException("Ошибка соединения с сервером!")
                if (response.isSuccess){
                    user.token = response.getOrThrow()!!.token
                    user.synced = true
                    userRepository.update(user)
                    operationStatusTextView.setText("Авторизация успешна!")
                }
                else{
                    operationStatusTextView.setText(response.exceptionOrNull()!!.message)
                }


            }

        }

        registerButton.setOnClickListener{
            lifecycleScope.launch {
                user.username = usernameEditText.text.toString()
                user.password = passwordEditText.text.toString()
                operationStatusTextView.setText("Выполняется регистрация ... ")
                val response = apiRepository.register(RegisterRequest(user.username, user.password))
                if(response.isSuccess){
                    operationStatusTextView.setText("Регистрация успешна")
                    user.token = response.getOrThrow()!!.token
                    user.synced = true
                    userRepository.update(user)
                }
                else{
                    operationStatusTextView.setText(response.exceptionOrNull()!!.message)
                }
            }
        }

        syncDataButton.setOnClickListener {
            lifecycleScope.launch {
                operationStatusTextView.setText("Синхронизация данных")
                val record = rideRepository.getRecord()
                record.username = user.username
                val response = apiRepository.sendRides(
                    user.token,
                    record
                )
                if(response.isSuccess){
                    operationStatusTextView.setText(response.getOrThrow().toString())
                }
                else{
                    operationStatusTextView.setText(response.exceptionOrNull()!!.message)
                }

            }
        }

        menuButton.setOnClickListener{
            val resultIntent = Intent()
            resultIntent.putExtra("user", user)
            setResult(RESULT_OK, resultIntent)

            finish()
        }
    }
}