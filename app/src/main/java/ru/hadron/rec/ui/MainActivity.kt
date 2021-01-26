package ru.hadron.rec.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.TypefaceCompat
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import kotlinx.android.synthetic.main.activity_main.*
import ru.hadron.rec.R
import ru.hadron.rec.others.Constants.ACTION_SHOW_RECORD_FRAGMENT

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppTheme)

        setContentView(R.layout.activity_main)

        this.resolveUiProblems()
        this.connectWithNavigationComponents()
        this.navigateToRecordFragmentIfNeeded(intent)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.navigateToRecordFragmentIfNeeded(intent)
    }

    private fun resolveUiProblems() {
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(1).isEnabled = false
    }

    private fun connectWithNavigationComponents() {
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
    }

    private fun navigateToRecordFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_RECORD_FRAGMENT) {
         navHostFragment.findNavController().navigate(R.id.action_global_Recording_fragment)
        }
    }
}