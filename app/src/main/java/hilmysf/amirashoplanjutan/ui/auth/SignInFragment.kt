package hilmysf.amirashoplanjutan.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.databinding.FragmentSignInBinding

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentSignInBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        val navController = Navigation.findNavController(requireView())
        binding.btnLogin.setOnClickListener {
            val email = binding.emailValue.text.toString()
            val password = binding.passwordValue.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.btnLogin.isEnabled = true
                viewModel.login(email, password, context)
                viewModel.isSuccessful.observe(requireActivity(), {
                    if (it.equals(true)) {
                        navController.navigate(R.id.action_signInFragment_to_homeActivity)
                    }
                })
            } else {
                binding.btnLogin.isClickable = false
            }
        }
        binding.tvToSignup.setOnClickListener { navController.navigate(R.id.action_signInFragment_to_signUpFragment) }
    }
}