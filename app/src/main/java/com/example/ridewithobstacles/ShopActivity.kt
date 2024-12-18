package com.example.ridewithobstacles

import android.content.Intent
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ridewithobstacles.Database.DbHelper
import com.example.ridewithobstacles.Database.Models.CarModel
import com.example.ridewithobstacles.Database.Models.UserModel
import com.example.ridewithobstacles.Database.Repositories.Implementations.CarRepository
import com.example.ridewithobstacles.Database.Repositories.Implementations.UserRepository
import com.example.ridewithobstacles.Shop.CarElement
import com.example.ridewithobstacles.Shop.ListAdapter.CarElementAdapter
import com.example.ridewithobstacles.Shop.OnItemClickListener

class ShopActivity : AppCompatActivity(), OnItemClickListener {
    private var moneyBalance:Int = 0
    private lateinit var moneyBalanceText: TextView
    private lateinit var carRecyclerView: RecyclerView
    private lateinit var adapter: CarElementAdapter
    private lateinit var carList: List<CarElement>
    private lateinit var db: SQLiteDatabase
    private lateinit var carRepository: CarRepository
    private lateinit var userRepository: UserRepository
    private lateinit var user: UserModel
    private var selectedPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shop)

        user = intent.getSerializableExtra("user") as UserModel
        val dbHelper = DbHelper(this@ShopActivity)
        //this.deleteDatabase(DbHelper.DATABASE_NAME)
        db = dbHelper.writableDatabase
        carRepository = CarRepository(db)
        userRepository = UserRepository(db)

        moneyBalanceText = findViewById(R.id.moneyBalance)
        //user.money = 1000
        moneyBalanceText.setText(user.money.toString())
        moneyBalance = user.money
        val menuButton = findViewById<Button>(R.id.menuButton)

        carRecyclerView = findViewById(R.id.carRecyclerView)
        carRecyclerView.layoutManager = LinearLayoutManager(this)

        //val resId = resources.getIdentifier("car", "drawable", packageName)

        initCarDatabase()
        carList = getCarElements()
//        carList = listOf(
//            CarElement("car1", resId, 100, 1, false),
//            CarElement("car2", resId, 200, 2, false),
//            CarElement("car3", resId, 300, 3, false),
//            CarElement("car4", resId, 400, 4, false)
//        )

        adapter = CarElementAdapter(carList)

        carRecyclerView.setAdapter(adapter)

        adapter.setOnItemClickListener(this)

        menuButton.setOnClickListener{
            val resultIntent = Intent()
            resultIntent.putExtra("user", user)
            setResult(RESULT_OK, resultIntent)
            this.finish()
        }
    }

    override fun onItemBuy(position: Int, price: Int) {

        if(moneyBalance < price){
            Toast.makeText(this, "Не хватает монет на покупку", Toast.LENGTH_SHORT).show()
        }
        else {
            val car: CarModel = carRepository.getById(carList[position].id) ?: throw IllegalArgumentException("Car object is null!")
            car.buyed = true
            carRepository.update(car)
            carList[position].isBuyed = true
            adapter.notifyItemChanged(position)
            moneyBalance -= price
            user.money = moneyBalance
            userRepository.update(user)
            moneyBalanceText.setText(moneyBalance.toString())
        }
    }

    override fun onItemChoose(position: Int) {
        val selectedCar = carRepository.getById(carList[position].id) ?: throw IllegalArgumentException("Car object is null!")
        selectedCar.choosed = true
        //val user = userRepository.getUser() ?: throw IllegalArgumentException("User is null!")
        val currentCarId = user.car.id
        var currentCar = carRepository.getById(currentCarId) ?: throw IllegalArgumentException("Car object is null!")
        currentCar.choosed = false
        carRepository.update(currentCar)
        carRepository.update(selectedCar)
        user.car = selectedCar
        //userRepository.update(user)
        carList[selectedPosition].isChoosed = false
        carList[position].isChoosed = true
        adapter.notifyItemChanged(position)
        adapter.notifyItemChanged(selectedPosition)
        selectedPosition = position
    }

    private fun initCarDatabase(){
        val cars = carRepository.getAll().map {it.carName}.toSet()
        val availableCars: List<String> = getDrawablesFromAssets()

        val addingCars: List<String> = availableCars.filter { it !in cars }

        for(car in addingCars){
            val carName = car.split(".")[0]
            val carPrice = car.split("_", ".")[1]
            val carModel: CarModel
            carModel = CarModel(0, car, carPrice.toInt(), false, false)
            carRepository.insert(carModel)
        }
    }

    private fun getCarElements(): List<CarElement>{
        val cars = carRepository.getAll()
        var carElementList = cars.mapIndexed{index, it ->
            val imageStream = resources.assets.open("cars/" + it.carName)
            val imageBitmap = BitmapFactory.decodeStream(imageStream)
            if(it.id == user.car.id) {
                selectedPosition = index
            }
            CarElement(
                it.id,
                it.carName,
                imageBitmap,
                it.price,
                it.buyed,
                it.choosed
            )

        }.toList()

        return carElementList
    }

    private fun getDrawables(): List<String>{
        val drawableField = R.drawable::class.java.declaredFields
        val drawableResources = mutableListOf<String>()


        for (field in drawableField) {
            try {
                val resourceName = field.name

                if (resourceName.contains("shop")) {
                    //val resourceId = field.getInt(null)
                    drawableResources.add(resourceName)
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }

        return drawableResources
    }

    private fun getDrawablesFromAssets(): List<String>{
        val drawableList = mutableListOf<String>()
        try {
            val assetManager = this.assets
            val files = assetManager.list("cars")
            files?.let {
                drawableList.addAll(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return drawableList
    }


}