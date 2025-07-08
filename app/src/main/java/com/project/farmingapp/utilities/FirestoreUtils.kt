import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

object FirestoreUtils {
    private const val TAG = "FirestoreDebug"

    fun <T> queryFirestore(
        query: Query,
        context: Context,
        dataList: MutableList<T>,
        mapper: (QuerySnapshot) -> List<T>,
        onComplete: () -> Unit
    ) {
        Log.d(TAG, "Executing Firestore query...")

        query.get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Query success: Fetched ${result.size()} documents.")
                dataList.clear()
                dataList.addAll(mapper(result))
                onComplete()

                if (dataList.isEmpty()) {
                    Log.w(TAG, "No data found.")
                    Toast.makeText(context, "No data available", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Query failed: ${exception.message}", exception)
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                onComplete()
            }
    }

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        return cm.activeNetwork != null
    }
}