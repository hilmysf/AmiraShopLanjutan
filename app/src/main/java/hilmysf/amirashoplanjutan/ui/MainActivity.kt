package hilmysf.amirashoplanjutan.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser
    }

    override fun onStart() {
        super.onStart()
        if(currentUser != null){
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
    }
}