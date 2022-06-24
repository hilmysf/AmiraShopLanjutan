package hilmysf.amirashoplanjutan.ui.profile

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.data.source.entities.Logs
import hilmysf.amirashoplanjutan.databinding.FragmentProfileBinding
import hilmysf.amirashoplanjutan.helper.Constant
import hilmysf.amirashoplanjutan.helper.Helper
import hilmysf.amirashoplanjutan.ui.log.opname.LogAdapter

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private var logAdapter: LogAdapter? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userId: String
    private lateinit var userEmail: String
    private lateinit var firestore: FirebaseFirestore
    private lateinit var options: FirestoreRecyclerOptions<Logs>
    private val viewModel: ProfileViewModel by viewModels()
    private var userName: String = ""
    private lateinit var storageReference: StorageReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(requireView())
        val sharedPreferences =
            activity?.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        userName = sharedPreferences?.getString(Constant.NAME, "User").toString()
        storageReference = Firebase.storage.reference
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        userEmail = mAuth.currentUser!!.email.toString()
        getUser()
        getLogsList(storageReference, userName)
        binding.btnLogout.setOnClickListener {
            viewModel.signOut()
            Log.d(TAG, "current user is null ${mAuth.currentUser == null}")
            navController.navigate(R.id.action_profile_to_mainActivity)
        }
    }


    private fun getUser() {
        viewModel.getUser(userId)
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = Helper.camelCase(document.getString(Constant.NAME).toString())
                    val email = document.getString(Constant.EMAIL)
                    binding.tvUser.text = name
                    binding.tvEmail.text = email
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun getLogsList(storageReference: StorageReference, userName: String) {
        Log.d(TAG, "userName: $userName")
        options = viewModel.getProfileLogs(userName)
        Log.d(TAG, "DocumentSnapshot Home: $options")
        logAdapter = LogAdapter(context, options, storageReference)
        with(binding.rvProfileLogs) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = logAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        logAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        logAdapter?.stopListening()
    }
}