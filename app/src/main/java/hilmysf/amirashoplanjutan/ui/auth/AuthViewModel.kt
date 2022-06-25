package hilmysf.amirashoplanjutan.ui.auth

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hilmysf.amirashoplanjutan.data.repositories.ProductsRepository
import hilmysf.amirashoplanjutan.helper.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val productsRepository: ProductsRepository) :
    ViewModel() {
    private var _isSuccessful = MutableLiveData<Boolean>()
    val isSuccessful: LiveData<Boolean> get() = _isSuccessful
    fun login(email: String, password: String, context: Context?) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.login(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    _isSuccessful.postValue(true)
                    Log.d(TAG, "Berhasil Login")
                } else {
                    Log.d(TAG, "gagal Login")
                    Helper.alertDialog(context, "Invalid Login", it.exception?.message)
                    _isSuccessful.postValue(false)
                }
            }
        }
    }

    fun signUp(email: String, password: String, context: Context?) {
        var errorMessage = ""
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.signUp(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    _isSuccessful.postValue(true)
                } else {
                    _isSuccessful.postValue(false)
                    Log.w(TAG, "createUserWithEmail:failure", it.exception)
                    Helper.alertDialog(context, "Invalid Sign Up", it.exception?.message)
                }
            }
        }
    }
}