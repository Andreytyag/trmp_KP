package com.example.ridewithobstacles

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Looper
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.example.ridewithobstacles.Database.DbHelper
import com.example.ridewithobstacles.Database.Models.RideModel
import com.example.ridewithobstacles.Database.Models.UserModel
import com.example.ridewithobstacles.Database.Repositories.Implementations.RideRepository
import com.example.ridewithobstacles.GameObjects.Car
import com.example.ridewithobstacles.GameObjects.Coin
import com.example.ridewithobstacles.GameObjects.CollisionObject
import com.example.ridewithobstacles.GameObjects.Obstacle
import java.util.concurrent.CopyOnWriteArrayList


class GamePanel: SurfaceView, Runnable {
    companion object{
        var maxX: Int = 20
        var maxY: Int = 28
        var unitW: Float = 0f
        var unitH: Float = 0f
        var roadLanesWidth: Float = 0f
        val roadLanesCount: Int = 4
    }

    var collisionObjects: CopyOnWriteArrayList<CollisionObject> = CopyOnWriteArrayList<CollisionObject>()

    private var objectsGenerator: Thread = Thread{
        while(gameRunning) {

            while(gamePaused && gameRunning){
                Thread.sleep(20)
            }

//            for(collisionObject:CollisionObject in collisionObjects){
//                if(collisionObject.y > maxY){
//                    collisionObjects.remove(collisionObject)
//                }
//            }

            if (currentObstacleTime >= obstacleInterval) {
                collisionObjects.add(Obstacle(context))
                currentObstacleTime = 0
            }
            currentObstacleTime++

            if (currentCoinTime >= coinInterval) {
                collisionObjects.add(Coin(context))
                currentCoinTime = 0
            }
            currentCoinTime++
            Thread.sleep(17)
        }
    }


    private var dataSaver: Thread = Thread{
        while(gameRunning){
            currentRide.score = currentScore
            currentRide.moneyCollected = currentMoney
            rideRepository.update(currentRide)
            user.money += currentMoney - prevCurrentMoney
            prevCurrentMoney = currentMoney

            while(gamePaused){
                Thread.sleep(10)
            }

            Thread.sleep(1000)
        }
    }

    private var listCleaner = Thread{
        while(gameRunning){
            for(collisionObject:CollisionObject in collisionObjects){
                if(collisionObject.y > maxY){
                    collisionObjects.remove(collisionObject)
                }
            }
            while(gamePaused){
                Thread.sleep(17)
            }
            Thread.sleep(17)
        }
    }
    private val obstacleInterval: Int = 30
    private val coinInterval: Int = 10
    private var currentObstacleTime = 0
    private var currentCoinTime = 0
    private var currentMoney = 0
    private var prevCurrentMoney = 0
    private var currentScore = 0
    private var gameRunning = true
    private var gamePaused = false
    private var firstTime = true
    private var surfaceHolder: SurfaceHolder
    private lateinit var car: Car
    private var paint: Paint
    private lateinit var canvas: Canvas
    private var gameThread: Thread
    private lateinit var backgroundBitmap: Bitmap
    private val carBitmap: Bitmap
    private var user:UserModel

    private val mRideText: TextView
    private val mMoneyText:TextView
    private val retryDialog:Dialog
    private var currentRide: RideModel
    private val rideRepository: RideRepository


    constructor(context: Context, currentMoneyTextView:TextView, currentRideTextView:TextView, _retryDialog: Dialog, chooseCar: Bitmap, _user: UserModel) : super(context) {
        surfaceHolder = holder
        paint = Paint()
        paint.setColor(Color.RED)
        paint.textSize = 150f
        GameActivity.leftSwap = false
        GameActivity.rightSwap = false
        mRideText = currentRideTextView
        mMoneyText = currentMoneyTextView
        retryDialog = _retryDialog
        carBitmap = chooseCar
        user = _user

        val db = DbHelper(context).writableDatabase
        rideRepository = RideRepository(db)
        currentRide = RideModel(0, currentScore, currentMoney, user.id, false)

        initCounters()

        gameThread = Thread(this)
        //gameThread.priority = Thread.MAX_PRIORITY
        gameThread.start()

    }

    fun killThread(){
        gameRunning = false
    }

    fun returnUser(): UserModel{
        return user
    }

    fun changePauseFlag(){
        gamePaused = !gamePaused
    }

    fun initCounters(){
        currentScore = 0
        currentMoney = 0
        prevCurrentMoney = 0
        currentRide = RideModel(0, currentScore, currentMoney, user.id, false)
        val id = rideRepository.insert(currentRide)

        currentRide.id = id.toInt()
        mRideText.setText("0")
        mMoneyText.setText("0")
    }

    fun reinitialize(){
        GameActivity.leftSwap = false
        GameActivity.rightSwap = false
        collisionObjects.clear()
        gameRunning = true
        gamePaused = false
        initCounters()
        //car = Car(getContext(), carBitmap)
        car.setDefaultLocation()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean{
        return super.onTouchEvent(event)
    }

    fun update(){
        if(!firstTime) {
            car.update();
        }

        for(collisionObject:CollisionObject in collisionObjects){
            collisionObject.update()
        }
    }

    private fun control() {
        try {
            Thread.sleep(5)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun draw(){
        if (surfaceHolder.getSurface().isValid()) {

            if(firstTime){
                firstTime = false;
                unitW = (surfaceHolder.surfaceFrame.width()/maxX).toFloat();
                unitH = (surfaceHolder.surfaceFrame.height()/maxY).toFloat();

                var cBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.road);

                backgroundBitmap = Bitmap.createScaledBitmap(
                    cBitmap, (surfaceHolder.surfaceFrame.width()), surfaceHolder.surfaceFrame.height(), false
                )

                roadLanesWidth = maxX / roadLanesCount.toFloat()
                car = Car(getContext(), carBitmap);

                objectsGenerator.start()
                dataSaver.start()
                listCleaner.start()


            }

            canvas = surfaceHolder.lockCanvas();
            canvas.drawBitmap(backgroundBitmap, 0f, 0f, paint)

            car.drow(paint, canvas);


//            for(collisionObject:CollisionObject in collisionObjects){
//                if(collisionObject.y > maxY){
//                    collisionObjects.remove(collisionObject)
//                }
//                else {
//                    collisionObject.drow(paint, canvas)
//                }
//            }

            for(collisionObject:CollisionObject in collisionObjects){
                collisionObject.drow(paint, canvas)
            }

            checkCollision()

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    override fun toString(): String {
        return "gamePanel"
    }


    private fun checkCollision() {
        for (collisionObject: CollisionObject in collisionObjects) {
            if (collisionObject.checkCollision(car.x, car.y, car.sizeX, car.sizeY)) {
                if(collisionObject is Obstacle) {
                    gamePaused = true
                    val moneyScored = retryDialog.findViewById<TextView>(R.id.moneyScored)
                    val pointsScored = retryDialog.findViewById<TextView>(R.id.pointsScored)
                    moneyScored.setText(currentMoney.toString())
                    pointsScored.setText(currentScore.toString())

                    val handler = android.os.Handler(Looper.getMainLooper())
                    handler.post {
                        retryDialog.show()
                        val window: Window =
                            retryDialog.getWindow() ?: throw Exception("Window is null")
                        window.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )

                    }
                }
                else if(collisionObject is Coin){
                    currentMoney += 1
                    mMoneyText.setText(currentMoney.toString())
                    collisionObjects.remove(collisionObject)
                }
            }
        }
        currentScore++
        mRideText.setText(currentScore.toString())

    }

    override fun run() {
        while (gameRunning) {
            update()
            draw()
            control()
            while(gamePaused){
                Thread.sleep(20)
            }
        }
    }

}