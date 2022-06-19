package hilmysf.amirashoplanjutan.ui

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.databinding.ActivityMainBinding
import hilmysf.amirashoplanjutan.helper.Helper
import hilmysf.amirashoplanjutan.network.InternetChangeReceiver

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), InternetChangeReceiver.ConnectivityReceiverListener {
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    lateinit var userId: String
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(
            InternetChangeReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser
    }

    override fun onStart() {
        super.onStart()
        if (currentUser != null) {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        Helper.showNetworkMessage(isConnected, this, binding)
    }

    override fun onResume() {
        super.onResume()
        InternetChangeReceiver.connectivityReceiverListener = this
    }
}