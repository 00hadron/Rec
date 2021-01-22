package ru.hadron.rec.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.hadron.rec.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppTheme)
        setContentView(R.layout.activity_main)
    }
}