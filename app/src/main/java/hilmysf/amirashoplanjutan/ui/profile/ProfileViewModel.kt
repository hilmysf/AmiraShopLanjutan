package hilmysf.amirashoplanjutan.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hilmysf.amirashoplanjutan.data.repositories.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val productsRepository: ProductsRepository) :
    ViewModel() {
    fun getUser(userId: String) =
        productsRepository.getUser(userId)

    fun signOut() = viewModelScope.launch(Dispatchers.IO) { productsRepository.signOut() }

    fun getProfileLogs(userName: String) = productsRepository.getProfileLogs(userName)
}