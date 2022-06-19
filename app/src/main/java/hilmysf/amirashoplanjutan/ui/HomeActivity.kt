package hilmysf.amirashoplanjutan.ui

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.databinding.ActivityHomeBinding
import hilmysf.amirashoplanjutan.helper.Helper
import hilmysf.amirashoplanjutan.network.InternetChangeReceiver

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), InternetChangeReceiver.ConnectivityReceiverListener {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(
            InternetChangeReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        supportActionBar?.hide()
        val navController = findNavController(R.id.bottom_nav_host_fragment)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.menu_bottom)
        bottomNavigation.setupWithNavController(navController)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        Helper.showNetworkMessage(isConnected, this, binding)
    }

    override fun onResume() {
        super.onResume()
        InternetChangeReceiver.connectivityReceiverListener = this
    }
}