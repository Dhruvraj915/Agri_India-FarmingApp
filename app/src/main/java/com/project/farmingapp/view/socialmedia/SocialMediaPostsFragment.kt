package com.project.farmingapp.view.socialmedia

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.farmingapp.R
import com.project.farmingapp.adapter.SMPostListAdapter
import com.project.farmingapp.databinding.FragmentSocialMediaPostsBinding
import com.project.farmingapp.viewmodel.SocialMediaViewModel

class SocialMediaPostsFragment : Fragment() {

    private var _binding: FragmentSocialMediaPostsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SocialMediaViewModel
    private lateinit var smCreatePostFragment: SMCreatePostFragment
    private var adapter: SMPostListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SocialMediaViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSocialMediaPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Social Media"

        getData()

        smCreatePostFragment = SMCreatePostFragment()
        binding.createPostFloating.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, smCreatePostFragment, "smCreate")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .addToBackStack("smCreate")
                .commit()
        }
    }

    private fun getData() {

        FirebaseFirestore.getInstance().collection("posts")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                Log.d("Posts data", result.documents.toString())
                adapter = SMPostListAdapter(requireContext(), result.documents)
                binding.postsRecycler.layoutManager = LinearLayoutManager(requireContext())
                binding.postsRecycler.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Failed to fetch posts: ${exception.message}")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SocialMediaPostsFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}