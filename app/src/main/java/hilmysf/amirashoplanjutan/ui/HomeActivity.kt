package hilmysf.amirashoplanjutan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()
        val navController = findNavController(R.id.bottom_nav_host_fragment)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.menu_bottom)
        bottomNavigation.setupWithNavController(navController)
    }
}