package com.example.ridewithobstacles

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ridewithobstacles.ApiService.ApiRepository
import com.example.ridewithobstacles.ApiService.DataModels.RecordDto
import com.example.ridewithobstacles.Database.DbHelper
import com.example.ridewithobstacles.Database.Models.UserModel
import com.example.ridewithobstacles.Database.Repositories.Implementations.RideRepository
import com.example.ridewithobstacles.Statistic.StatisticAdapter
import kotlinx.coroutines.launch

class StatisticActivity : AppCompatActivity() {
    private lateinit var user: UserModel
    private lateinit var ridesCountText: TextView
    private lateinit var moneyCountText: TextView
    private lateinit var scoreCountText: TextView
    private lateinit var maxScoreText: TextView
    private lateinit var maxMoneyText: TextView
    private lateinit var avgScoreText: TextView
    private lateinit var avgMoneyText: TextView
    private lateinit var usernameText: TextView
    private lateinit var userStatisticList: List<RecordDto>
    private lateinit var userStatisticView: RecyclerView
    private lateinit var userStatisticAdapter: StatisticAdapter
    private lateinit var rideRepository: RideRepository
    private lateinit var apiRepository: ApiRepository
    private lateinit var emptyStateView: View
    private lateinit var coinsStatisticChekbox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_statistic)
        user = intent.getSerializableExtra("user") as UserModel

        val container = findViewById<FrameLayout>(R.id.container)
        emptyStateView = container.findViewById(R.id.empty_state_view)
        val dbHelper = DbHelper(this@StatisticActivity)
        val db = dbHelper.writableDatabase
        rideRepository = RideRepository(db)
        apiRepository = ApiRepository()

        usernameText = findViewById(R.id.usernameTextView)
        ridesCountText = findViewById(R.id.ridesCountTextView)
        moneyCountText = findViewById(R.id.moneyCountTextView)
        scoreCountText = findViewById(R.id.scoreCountTextView)
        maxScoreText = findViewById(R.id.maxScoreTextView)
        maxMoneyText = findViewById(R.id.maxMoneyTextView)
        avgScoreText = findViewById(R.id.avgScoreTextView)
        avgMoneyText = findViewById(R.id.avgMoneyTextView)
        userStatisticView = findViewById(R.id.statisticRecyclerView)

        val record = rideRepository.getRecord()

        usernameText.text = user.username
        ridesCountText.text = rideRepository.getRidesCount().toString()
        scoreCountText.text = rideRepository.getScoreSum().toString()
        moneyCountText.text = rideRepository.getMoneySum().toString()
        maxMoneyText.text = record.moneyCollected.toString()
        maxScoreText.text = record.scoreCollected.toString()
        avgMoneyText.text = record.avgMoney.toString()
        avgScoreText.text = record.avgScore.toString()


        coinsStatisticChekbox = findViewById(R.id.coinsStatisticCheckbox)
        coinsStatisticChekbox.setOnCheckedChangeListener { _, b ->
            if(b){
                loadMoneyRecordsFromServer()
            }
            else{
                loadScoreRecordsFromServer()
            }
        }

        userStatisticView.layoutManager = LinearLayoutManager(this)


        userStatisticAdapter = StatisticAdapter()
        userStatisticView.adapter = userStatisticAdapter

        setListData()



        val menuButton: Button = findViewById(R.id.returnMenuButton)


        menuButton.setOnClickListener {
            finish()
        }


    }

    private fun setListData(){
        lifecycleScope.launch {
            if (coinsStatisticChekbox.isActivated){
                loadMoneyRecordsFromServer()
            }
            else{
                loadScoreRecordsFromServer()
            }


        }
    }

    private fun showEmptyState(show: Boolean) {
        userStatisticView.visibility = if (!show) View.VISIBLE else View.GONE
        emptyStateView.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun loadScoreRecordsFromServer() {
        lifecycleScope.launch {
            val response = apiRepository.getRecordsByScore(user.token)
            if(response.isSuccess){
                showEmptyState(false)
                userStatisticList = response.getOrThrow()
                userStatisticAdapter.setData(userStatisticList)

            }
            else{
                showEmptyState(true)
            }
        }
    }

    private fun loadMoneyRecordsFromServer() {
        lifecycleScope.launch {
            val response = apiRepository.getRecordsByMoney(user.token)
            if(response.isSuccess){
                showEmptyState(false)
                userStatisticList = response.getOrThrow()
                userStatisticAdapter.setData(userStatisticList)
            }
            else{
                showEmptyState(true)
            }
        }
    }
}