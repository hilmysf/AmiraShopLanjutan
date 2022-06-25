package hilmysf.amirashoplanjutan.ui.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.ActivityNavigator
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.databinding.FragmentSignInBinding


@AndroidEntryPoint
class SignInFragment : Fragment(), TextWatcher {
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
        binding.emailValue.addTextChangedListener(this)
        binding.passwordValue.addTextChangedListener(this)
        checkFieldsForEmptyValues()
        binding.btnLogin.setOnClickListener {
            val email = binding.emailValue.text.toString()
            val password = binding.passwordValue.text.toString()
            Log.d(TAG, "onClick Login")
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password, context)
                viewModel.isSuccessful.observe(requireActivity(), {
                    if (it.equals(true)) {
                        val extras = ActivityNavigator.Extras.Builder()
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .build()
                        navController.navigate(
                            SignInFragmentDirections.actionSignInFragmentToHomeActivity(),
                            extras
                        )
                    }
                })
            }
        }
        binding.tvToSignup.setOnClickListener { navController.navigate(hilmysf.amirashoplanjutan.R.id.action_signInFragment_to_signUpFragment) }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkFieldsForEmptyValues()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    private fun checkFieldsForEmptyValues() {
        val email = binding.emailValue.text.toString()
        val password = binding.passwordValue.text.toString()
        binding.btnLogin.isEnabled = email.isNotEmpty() && password.isNotEmpty()
    }
}