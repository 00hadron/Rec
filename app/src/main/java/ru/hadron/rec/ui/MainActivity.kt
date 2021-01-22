package ru.hadron.rec.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import kotlinx.android.synthetic.main.activity_main.*
import ru.hadron.rec.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppTheme)

        setContentView(R.layout.activity_main)
        this.resolveUiProblems()

    }

    private fun resolveUiProblems() {
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(1).isEnabled = false
    }
}