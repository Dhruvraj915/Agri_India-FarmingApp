package com.project.farmingapp.view.ecommerce

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.databinding.ActivityRazorPayBinding
import com.project.farmingapp.view.dashboard.DashboardActivity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class RazorPayActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityRazorPayBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private var orderData = mapOf<String, Any>()  // Used instead of finalOrderData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRazorPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Checkout.preload(applicationContext)

        // Get order data from Intent (optional)
        @Suppress("UNCHECKED_CAST")
        orderData = intent.getSerializableExtra("orderData") as? Map<String, Any> ?: emptyMap()

        startPayment()
    }

    private fun startPayment() {
        val checkout = Checkout()
        checkout.setKeyID("YOUR_RAZORPAY_KEY_ID") // Replace with your key

        val options = JSONObject().apply {
            put("name", "Farming App")
            put("description", "Order Payment")
            put("currency", "INR")
            put("amount", orderData["amount"] ?: 10000) // amount in paisa
            put("prefill", JSONObject().apply {
                put("email", firebaseAuth.currentUser?.email ?: "")
                put("contact", orderData["phone"] ?: "")
            })
        }

        checkout.open(this, options)
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
        saveOrderToFirestore("success", razorpayPaymentID)
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_LONG).show()
        saveOrderToFirestore("failed", response)
    }

    private fun saveOrderToFirestore(status: String, paymentId: String?) {
        val orderWithStatus = orderData.toMutableMap().apply {
            put("status", status)
            put("paymentId", paymentId ?: "N/A")
            put("timestamp", System.currentTimeMillis())
        }

        firebaseAuth.currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid)
                .collection("orders").add(orderWithStatus)
                .addOnSuccessListener {
                    Toast.makeText(this, "Order saved!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save order.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
