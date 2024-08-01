package com.example.kotlincalculatorapp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var tvWork: TextView
    private lateinit var tvResult: TextView
    private var canAddOperation = false
    private var canAddDecimal = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up button to navigate to TipActivity
        findViewById<Button>(R.id.tip).setOnClickListener {
            startActivity(Intent(this, TipActivity::class.java))
        }
    }

    fun numberAction(view: View)
    {
        tvWork = findViewById(R.id.tvWorking)
        tvResult = findViewById(R.id.tvResults)

        if (view is Button)
        {
            if (view.text == ".")
            {
                if(canAddDecimal) {
                    tvWork.append(view.text)
                }
                canAddDecimal = false
            }
            else {
                tvWork.append(view.text)
            }
            canAddOperation = true
        }
    }

    fun operatorAction(view: View)
    {
        tvWork = findViewById(R.id.tvWorking)
        tvResult = findViewById(R.id.tvResults)

        if (view is Button && canAddOperation)
        {
            tvWork.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View)
    {
        tvWork = findViewById(R.id.tvWorking)
        tvResult = findViewById(R.id.tvResults)

        tvWork.text = ""
        tvResult.text = ""

        canAddOperation = false
        canAddDecimal = true
    }

    fun backspaceAction(view: View)
    {
        tvWork = findViewById(R.id.tvWorking)
        tvResult = findViewById(R.id.tvResults)

        val len = tvWork.length()
        if (len > 0)
        {
            tvWork.text = tvWork.text.subSequence(0, len - 1)
        }
    }

    fun equalsAction(view: View)
    {
        tvResult = findViewById(R.id.tvResults)

        tvResult.text = calculatorResults()
    }

    private fun calculatorResults() : String
    {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty())
        {
            return ""
        }

        val mulitpleDivide =  mulitpleDivideCalculate(digitsOperators)

        if (mulitpleDivide.isEmpty())
        {
            return ""
        }
        
        val result = addSubstractCalculate(mulitpleDivide)

        return result.toString()
    }

    private fun addSubstractCalculate(passedList: MutableList<Any>): Float
    {

        var result = passedList[0] as Float

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex)
            {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }

        return result
    }

    private fun mulitpleDivideCalculate(passedList: MutableList<Any>) : MutableList<Any>
    {
        var list = passedList
        while (list.contains('x') || list.contains('/'))
        {
            list = calcMultiDivd(list)
        }
        return list
    }

    private fun calcMultiDivd(passedList: MutableList<Any>): MutableList<Any>
    {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices)
        {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val lastDigit = passedList[i + 1] as Float

                when(operator)
                {
                    'x' ->
                    {
                        newList.add(prevDigit * lastDigit)
                        restartIndex = i + 1
                    }
                    '/' ->
                    {
                        newList.add(prevDigit / lastDigit)
                        restartIndex = i + 1
                    }
                    else ->
                    {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if(i > restartIndex)
            {
                newList.add(passedList[i])
            }
        }
        return  newList
    }

    private fun digitsOperators() : MutableList<Any>
    {
        val list = mutableListOf<Any>()
        var currentDight = ""

        for (character in tvWork.text)
        {
            if (character.isDigit() || character == '.')
            {
                currentDight = currentDight + character
            }
            else
            {
                list.add(currentDight.toFloat())
                currentDight = ""
                list.add(character)
            }
        }

        if (currentDight != "")
        {
            list.add(currentDight.toFloat())
        }
        return list
    }
}