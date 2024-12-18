package com.example.ridewithobstacles

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ridewithobstacles.Database.DbHelper
import com.example.ridewithobstacles.Database.Models.CarModel
import com.example.ridewithobstacles.Database.Models.UserModel
import com.example.ridewithobstacles.Database.Repositories.Implementations.CarRepository
import com.example.ridewithobstacles.Database.Repositories.Implementations.UserRepository


class MainActivity : AppCompatActivity() {
    companion object {
        var IsPressed1: Boolean = false
        var IsPressed2: Boolean = false
        //var carId = R.drawable.shop_default_0
    }
    private lateinit var chooseCar: String
    private lateinit var userRepository: UserRepository
    private lateinit var carRepository: CarRepository
    private lateinit var user: UserModel

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            user = result.data?.getSerializableExtra("user") as UserModel
            userRepository.update(user)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        var menuLayout: LinearLayout = findViewById((R.id.menuLayout))
        var playButton: Button = findViewById(R.id.playButton)
        var shopButton: Button = findViewById(R.id.shopButton)
        var userDataButton: Button = findViewById(R.id.userDataButton)
        var statisticButton: Button = findViewById(R.id.statisticsButton)
        var exitButton: Button = findViewById(R.id.exitButton)
        var documentButton: Button = findViewById(R.id.documentationButton)

        val dbHelper = DbHelper(this@MainActivity)
        //this.deleteDatabase(DbHelper.DATABASE_NAME)
        val db = dbHelper.getWritableDatabase()
        userRepository = UserRepository(db)
        carRepository = CarRepository(db)


        initUsersDb()
        initUser()

        //val imageStream = resources.assets.open("cars/" + user.car.carName)
        //chooseCar = "cars/" + user.car.carName



        playButton.setOnClickListener{
            val game = Intent(this@MainActivity, GameActivity::class.java)
            chooseCar = "cars/" + user.car.carName
            //game.putExtra("CarBitmap", chooseCar)
            game.putExtra("user", user)
            startForResult.launch(game)
        }

        exitButton.setOnClickListener{
            finishAffinity()
        }

        documentButton.setOnClickListener {
            val documentation = Intent(this@MainActivity, DocumentationActivity::class.java)
            startActivity(documentation)
        }

        shopButton.setOnClickListener{
            val shop = Intent(this@MainActivity, ShopActivity::class.java)
            shop.putExtra("user", user)
            startForResult.launch(shop)

        }

        statisticButton.setOnClickListener {
            val statistic = Intent(this@MainActivity, StatisticActivity::class.java)
            statistic.putExtra("user", user)
            startActivity(statistic)
        }

        userDataButton.setOnClickListener{
            val userData = Intent(this@MainActivity, SyncActivity::class.java)
            userData.putExtra("user", user)
            startForResult.launch(userData)
        }

    }

    private fun initUsersDb(){
        var user = userRepository.getUser()
        if(user == null){
            carRepository.insert(CarModel(0, "default_0.png", 0, true, true))
            user = UserModel(0, "default", 0, CarModel(1, "default_0.png", 0, true, true), "default", "", false)
            userRepository.insert(user)
        }
    }

    private fun initUser(){
        user = userRepository.getUser() ?: throw IllegalArgumentException("User is null!")

    }

}