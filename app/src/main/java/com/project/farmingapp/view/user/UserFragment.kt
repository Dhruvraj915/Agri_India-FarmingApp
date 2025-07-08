package com.project.farmingapp.view.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.adapter.UserProfilePostsAdapter
import com.project.farmingapp.databinding.FragmentUserBinding
import com.project.farmingapp.viewmodel.UserDataViewModel
import com.project.farmingapp.viewmodel.UserProfilePostsViewModel

class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var userDataViewModel: UserDataViewModel
    private lateinit var userProfilePostsViewModel: UserProfilePostsViewModel
    private lateinit var userProfilePostsAdapter: UserProfilePostsAdapter
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "My Profile"

        userDataViewModel = ViewModelProvider(this)[UserDataViewModel::class.java]
        userProfilePostsViewModel = ViewModelProvider(this)[UserProfilePostsViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        firebaseAuth.currentUser?.uid?.let { userId ->
            userDataViewModel.getUserData(firebaseAuth.currentUser?.email ?: "")
            userProfilePostsViewModel.getAllPosts(userId)
        }
    }

    private fun observeViewModel() {
        userDataViewModel.userLiveData.observe(viewLifecycleOwner) { snapshot ->
            snapshot?.let {
                binding.userNameUserProfileFrag.text = it.getString("name") ?: "User Name"
                binding.userEmailUserProfileFrag.text = it.getString("email") ?: "Email"
                binding.userCityUserProfileFrag.text = it.getString("city") ?: "City"
                Glide.with(this)
                    .load(it.getString("profileImage"))
                    .into(binding.userImageUserFrag)
            }
        }

        userProfilePostsViewModel.userPostsLiveData.observe(viewLifecycleOwner) { posts: List<DocumentSnapshot> ->
            userProfilePostsAdapter.updatePosts(posts)
        }
    }

    private fun setupRecyclerView() {
        userProfilePostsAdapter = UserProfilePostsAdapter(requireContext(), emptyList())
        binding.userProfilePostsRecycler.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = userProfilePostsAdapter
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
