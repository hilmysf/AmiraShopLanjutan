package hilmysf.amirashoplanjutan.ui.auth

import android.content.ContentValues.TAG
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.databinding.FragmentSignUpBinding
import android.util.Log
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.helper.Constant

@AndroidEntryPoint
class SignUpFragment : Fragment() {
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
        binding.btnSignup.setOnClickListener {
            val email = binding.emailValue.text.toString()
            val name = binding.nameValue.text.toString()
            val password = binding.passwordValue.text.toString()
            Log.w(TAG, "email password $email $password")
            if (email.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty()) {
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
                        navController.navigate(R.id.action_signUpFragment_to_homeActivity)
                    }
                }
                )
            } else {
                binding.btnSignup.isClickable = false
            }
        }
    }
}