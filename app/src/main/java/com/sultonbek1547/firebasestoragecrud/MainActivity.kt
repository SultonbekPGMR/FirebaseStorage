package com.sultonbek1547.firebasestoragecrud

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sultonbek1547.firebasestoragecrud.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var reference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseStorage = FirebaseStorage.getInstance()
        reference = firebaseStorage.getReference("my_photos")


        binding.img.setOnClickListener {
            getImageContent.launch("image/*")
        }


    }


    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.progressBar.visibility = View.VISIBLE

                CoroutineScope(Dispatchers.IO).launch {
                    val uniqueId = UUID.randomUUID().toString()
                    val task = reference.child(uniqueId).putFile(uri)
                    task.addOnSuccessListener { taskSnapshot ->
                        binding.img.setImageURI(uri)
                        binding.progressBar.visibility = View.GONE
                        taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                            // link of image is ready
                            print(it)

                        }

                    }

                    withContext(Dispatchers.Main) {
                        task.addOnFailureListener {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()

                        }
                    }
                }


            }


        }

}