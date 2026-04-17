package com.wenwu.week_2_class

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //val myButton: Button = findViewById<Button>(R.id.buttonId)
        //var diceImage: ImageView = findViewById<ImageView>(R.id.imageId)

        /*myButton.setOnClickListener {
            val images = arrayOf( -1,
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four,
                R.drawable.five,
                R.drawable.six,)

            val myDie = Random.nextInt(1, 7)

            diceImage.setImageResource(images[myDie])
            }*/

            // assignment 2 rock, paper, scissors game

        val rockButton: Button = findViewById<Button>(R.id.RockButton)
        val paperButton: Button = findViewById<Button>(R.id.PaperButton)
        val scissorsButton: Button = findViewById<Button>(R.id.ScissorsButton)
        val playerImage: ImageView = findViewById<ImageView>(R.id.PlayerChoice)
        val scoreBox1: ImageView = findViewById<ImageView>(R.id.greenBoxId)
        val scoreBox2: ImageView = findViewById<ImageView>(R.id.greenBoxId2)
        val scoreBox3: ImageView = findViewById<ImageView>(R.id.greenBoxId3)

        var playerChoice: Int
        var aiChoice = 0
        var attempts = 3

        // 1 beats 3
        // 2 beats 1
        // 3 beats 2

        val imageArray = arrayOf(-1,
            R.drawable.rock,
            R.drawable.paper,
            R.drawable.scissors,)

        val delayer = Handler()
        delayer.postDelayed({

        }, 3000)


        rockButton.setOnClickListener {
            playerImage.setImageResource(imageArray[1])
        }
        paperButton.setOnClickListener {
            playerImage.setImageResource(imageArray[2])
        }
        scissorsButton.setOnClickListener {
            playerImage.setImageResource(imageArray[3])
        }

        // game logic





    }
}