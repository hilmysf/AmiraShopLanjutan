package hilmysf.amirashoplanjutan.ui.profile

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.databinding.FragmentProfileBinding
import hilmysf.amirashoplanjutan.helper.Constant
import hilmysf.amirashoplanjutan.ui.auth.AuthViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userId: String
    private lateinit var userEmail: String
    private val viewModel: ProfileViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(requireView())
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        userEmail = mAuth.currentUser!!.email.toString()
        getUser()
        binding.btnLogout.setOnClickListener {
            viewModel.signOut()
            Log.d(TAG, "current user is null ${mAuth.currentUser == null}")
            navController.navigate(R.id.action_profile_to_nav_graph)
        }
    }

    private fun getUser() {
        viewModel.getUser(userId)
            .addOnSuccessListener { document ->
                if (document != null) {
//                    val name = document.getString(Constant.NAME)
                    val name = mAuth.currentUser!!.displayName.toString()
                    binding.tvUser.text = name
                    binding.tvEmail.text = userEmail
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}