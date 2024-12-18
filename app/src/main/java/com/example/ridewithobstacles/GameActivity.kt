package com.example.ridewithobstacles

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ridewithobstacles.Database.Models.UserModel
import kotlin.math.abs

class GameActivity : AppCompatActivity(), GestureDetector.OnGestureListener{
    companion object{
        var leftSwap:Boolean = false
        var rightSwap: Boolean = false
    }

    lateinit var gamePanel:GamePanel
    private lateinit var gestureDetector: GestureDetector
    private val swipeThreshold = 30
    private val swipeVelocityThreshold = 30

    private lateinit var mMoneyText: TextView
    private lateinit var mRideText: TextView
    private lateinit var retryDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_game)

        var layout = findViewById<LinearLayout>(R.id.gameLayout)

        mMoneyText = findViewById(R.id.currentMoney)
        mRideText = findViewById(R.id.currentRide)
        val dialogBinding = layoutInflater.inflate(R.layout.retry_dialog, null)
        retryDialog = Dialog(this)
        retryDialog.setContentView(dialogBinding)
        retryDialog.setCancelable(false)
        val menuButton:Button = findViewById(R.id.returnMenuButton)
        val pauseButton:Button = findViewById(R.id.pauseButton)

        val retryButton = retryDialog.findViewById<Button>(R.id.retry)
        val returnMenuButton = retryDialog.findViewById<Button>(R.id.mainMenu)



        val user = intent.getSerializableExtra("user") as UserModel
        val bitmapStream = resources.assets.open("cars/" + user.car.carName)
        val carBitmap = BitmapFactory.decodeStream(bitmapStream)

        gamePanel = GamePanel(this, mMoneyText, mRideText, retryDialog, carBitmap, user)
        layout.addView(gamePanel)



        gestureDetector = GestureDetector(this)

        menuButton.setOnClickListener{
            gamePanel.killThread()
            val user = gamePanel.returnUser()
            val resultIntent = Intent()
            resultIntent.putExtra("user", user)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        retryButton.setOnClickListener {
            retryDialog.dismiss()
            //gamePanel = GamePanel(this, mMoneyText, mRideText, retryDialog)
            gamePanel.reinitialize()
        }

        returnMenuButton.setOnClickListener {
            gamePanel.killThread()
            val user = gamePanel.returnUser()
            val resultIntent = Intent()
            resultIntent.putExtra("user", user)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        pauseButton.setOnClickListener {
            gamePanel.changePauseFlag()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        }
        else {
            super.onTouchEvent(event)
        }
    }

    override fun onDown(p0: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(p0: MotionEvent) {
        return
    }

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onLongPress(p0: MotionEvent) {
        return
    }

    override fun onFling(e2: MotionEvent?, e1: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        try {
            var diffX = 0F
            if (e2 != null){
                diffX = e2.x - e1.x
            }
            if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                if (diffX > 0) {
                    leftSwap = true
                }
                else {
                    rightSwap = true
                }
            }

        }
        catch (exception: Exception) {
            exception.printStackTrace()
        }
        return true
    }
}