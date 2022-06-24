package hilmysf.amirashoplanjutan.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.databinding.FragmentLogBinding


@AndroidEntryPoint
class LogFragment : Fragment() {
    private lateinit var binding: FragmentLogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = binding.viewPager
        val tabLayout = binding.tabs
        tabLayout.setupWithViewPager(viewPager)
        val adapter = SectionsPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter
    }
}