package com.project.farmingapp.view.socialmedia

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController // Import NavController
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project.farmingapp.R
import com.project.farmingapp.databinding.FragmentSmCreatePostBinding
import com.project.farmingapp.viewmodel.UserDataViewModel
import java.io.IOException
import java.util.*

class SMCreatePostFragment : Fragment() {

    // --- View Binding Setup ---
    private var _binding: FragmentSmCreatePostBinding? = null
    private val binding get() = _binding!!
    // --- End View Binding Setup ---

    private val PICK_MEDIA_REQUEST = 71
    private var filePath: Uri? = null
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userDataViewModel: UserDataViewModel

    private val db = FirebaseFirestore.getInstance()
    private val dataMap = HashMap<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        userDataViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Create Post"

        binding.progressCreatePost.visibility = View.GONE
        binding.progressTitle.visibility = View.GONE

        dataMap["uploadType"] = ""

        binding.uploadImagePreview.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*" // Changed to only images for simplicity, can be "image/* video/*"
            startActivityForResult(Intent.createChooser(intent, "Select Media"), PICK_MEDIA_REQUEST)
        }

        val user = firebaseAuth.currentUser
        if (user?.displayName.isNullOrEmpty()) {
            db.collection("users").document(user?.email ?: "")
                .get()
                .addOnSuccessListener {
                    dataMap["name"] = it.getString("name").orEmpty()
                    Log.d("UserNameFromDB", dataMap["name"].toString())
                }
        } else {
            dataMap["name"] = user?.displayName.orEmpty()
            Log.d("GoogleUserName", dataMap["name"].toString())
        }

        binding.createPostBtnSM.setOnClickListener {
            if (binding.postTitleSM.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show()
            } else {
                uploadMediaToFirebase()
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_MEDIA_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            filePath = data.data
            binding.uploadImagePreview.setImageURI(filePath)

            try {
                val type = requireContext().contentResolver.getType(filePath!!)
                dataMap["uploadType"] = when {
                    type?.startsWith("image") == true -> "image"
                    type?.startsWith("video") == true -> "video"
                    else -> "image" // Default to image if type is undetectable
                }
                Log.d("DetectedFileType", dataMap["uploadType"].toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadMediaToFirebase() {
        binding.progressCreatePost.visibility = View.VISIBLE
        binding.progressTitle.visibility = View.VISIBLE
        binding.createPostBtnSM.isEnabled = false // Disable button during upload

        val userEmail = firebaseAuth.currentUser?.email
        if (userEmail == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            showError("") // Hides progress
            return
        }

        if (filePath != null) {
            val uuid = UUID.randomUUID()
            val fileRef = storageReference.child("posts/$uuid")

            fileRef.putFile(filePath!!)
                .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                .addOnSuccessListener { uri ->
                    addPostToFirestore(uri.toString(), uuid.toString())
                }
                .addOnFailureListener {
                    showError("Media upload failed: ${it.message}")
                }
        } else {
            // If no image is selected, still allow posting
            addPostToFirestore(null, null)
        }
    }

    private fun addPostToFirestore(mediaUrl: String?, mediaId: String?) {
        val userEmail = firebaseAuth.currentUser?.email ?: return

        mediaUrl?.let { dataMap["imageUrl"] = it }
        mediaId?.let { dataMap["imageID"] = it }

        dataMap["userID"] = userEmail
        dataMap["timeStamp"] = System.currentTimeMillis()
        dataMap["title"] = binding.postTitleSM.text.toString()
        dataMap["description"] = binding.descPostSM.text.toString()

        db.collection("posts")
            .add(dataMap)
            .addOnSuccessListener { docRef ->
                db.collection("users")
                    .document(userEmail)
                    .update("posts", FieldValue.arrayUnion(docRef.id))
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Post Created", Toast.LENGTH_SHORT).show()

                        // FIX: Navigate back to the previous screen instead of replacing the fragment
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener {
                        showError("Post saved, but failed to update user data")
                    }
            }
            .addOnFailureListener {
                showError("Failed to save post: ${it.message}")
            }
    }

    private fun showError(message: String) {
        binding.progressCreatePost.visibility = View.GONE
        binding.progressTitle.visibility = View.GONE
        binding.createPostBtnSM.isEnabled = true // Re-enable button on error
        if (message.isNotEmpty()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            Log.e("SMCreatePostFragment", message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}