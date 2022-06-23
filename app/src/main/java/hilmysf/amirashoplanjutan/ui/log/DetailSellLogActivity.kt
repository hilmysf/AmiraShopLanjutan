package hilmysf.amirashoplanjutan.ui.log

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.databinding.ActivityDetailSellLogBinding

@AndroidEntryPoint
class DetailSellLogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailSellLogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSellLogBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}