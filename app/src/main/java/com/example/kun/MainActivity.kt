package com.example.kun

import android.media.effect.Effect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.core.text.isDigitsOnly
import kotlin.math.ceil
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    private var interestRate : Float = 0.07F
    private var precision : Int = 0
    private val k = 1 / (1 - 0.0025).pow(2)

    private var inPrice : Float = 0F
    private lateinit var sellPrice : TextView
    private lateinit var minPrice : TextView
    private lateinit var minOkPrice : TextView
    private lateinit var maxOkPrice : TextView
    private lateinit var maxPrice : TextView

    private fun update() {
        fun roundString(x : Double) : String {
            fun Double.format(digits: Int) = "%.${digits}f".format(this)
            return if (precision == 0) ceil(x).format(0) else (0.01F*ceil(100*x)).format(2)
         }
        sellPrice.text = roundString(k * inPrice * (1 + interestRate))
        maxPrice.text = sellPrice.text
        val alpha = interestRate + 0.005 - 0.0025.pow(2)
        minPrice.text = roundString(inPrice * (1 - alpha / (1 + interestRate)))
        val kappa = alpha / (2 - alpha + 2 * interestRate)
        minOkPrice.text = roundString(inPrice * (1 - kappa))
        maxOkPrice.text = roundString(inPrice * (1 + kappa))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sellPrice = findViewById(R.id.SellPrice)
        minPrice = findViewById(R.id.MinPrice)
        minOkPrice = findViewById(R.id.MinOkPrice)
        maxOkPrice = findViewById(R.id.MaxOkPrice)
        maxPrice = findViewById(R.id.MaxPrice)

        val buyPrice : EditText = findViewById(R.id.BuyPrice)
        buyPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inPrice = if (start + count > 0) s.toString().toFloat() else 0F
                precision = if (start + count > 0) if (s.toString().contains(".")) 2 else 0 else 0
                update()
            }
        })

        val intRate : EditText = findViewById(R.id.InterestRate)
        intRate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                interestRate = if (start + count > 0) 0.01F * s.toString().toFloat() else 0.07F
                update()
            }
        })
        update()
    }
}