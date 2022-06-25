package hilmysf.amirashoplanjutan.ui.auth

import android.content.ContentValues.TAG
import android.content.Context
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
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.databinding.FragmentSignUpBinding
import hilmysf.amirashoplanjutan.helper.Constant

@AndroidEntryPoint
class SignUpFragment : Fragment(), TextWatcher {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var firestore: FirebaseFirestore
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        val navController = Navigation.findNavController(requireView())
        mAuth = FirebaseAuth.getInstance()
        binding.tvToLogin.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        checkFieldsForEmptyValues()
        binding.emailValue.addTextChangedListener(this)
        binding.nameValue.addTextChangedListener(this)
        binding.passwordValue.addTextChangedListener(this)
        binding.btnSignup.setOnClickListener {
            val email = binding.emailValue.text.toString()
            val name = binding.nameValue.text.toString().lowercase()
            val password = binding.passwordValue.text.toString()
            Log.w(TAG, "email password $email $password")
            if (email.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty()) {
                storeUserName(name)
                viewModel.signUp(email, password, context)
                viewModel.isSuccessful.observe(requireActivity(), {
                    if (it.equals(true)) {
                        val userId = mAuth.currentUser!!.uid
                        val documentReference = firestore.collection("users").document(userId)
                        val user = hashMapOf<String, String>()
                        user[Constant.NAME] = name
                        user[Constant.EMAIL] = email
                        documentReference.set(user)
                            .addOnSuccessListener {
                                Log.d(
                                    TAG,
                                    "onSuccess: user profile is created for $userId"
                                )
                            }
                            .addOnFailureListener {

                            }
                        val extras = ActivityNavigator.Extras.Builder()
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .build()
                        navController.navigate(
                            SignUpFragmentDirections.actionSignUpFragmentToHomeActivity(),
                            extras
                        )
                    }
                }
                )
            }
        }
    }

    private fun storeUserName(userName: String) {
        val sharedPreference =
            activity?.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        sharedPreference?.edit()?.apply {
            putString(Constant.NAME, userName)
            apply()
        }
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
        val name = binding.nameValue.text.toString()
        val password = binding.passwordValue.text.toString()
        binding.btnSignup.isEnabled =
            email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()
    }
}