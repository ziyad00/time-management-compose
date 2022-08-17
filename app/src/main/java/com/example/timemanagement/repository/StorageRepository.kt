package com.example.timemanagement.repository
import android.util.Log
import com.example.timemanagement.models.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val TASKS_COLLECTION_REF = "tasks"

class StorageRepository {

    fun user() = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val tasksRef: CollectionReference = Firebase
        .firestore.collection(TASKS_COLLECTION_REF)

    fun getUserTasks(
        userId: String,
    ): Flow<Resources<List<Task>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null

        try {
            snapshotStateListener = tasksRef
                 .orderBy("timestamp")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val tasks = snapshot?.toObjects(Task::class.java)
                        Resources.Success(data = tasks)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)


                }


        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }


    }

    fun getTask(
        taskId:String,
        onError:(Throwable?) -> Unit,
        onSuccess: (Task?) -> Unit
    ){
        tasksRef
            .document(taskId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Task::class.java))
            }
            .addOnFailureListener {result ->
                onError.invoke(result.cause)
            }


    }

    fun addTask(
        userId: String,
        title: String,
        description: String,
        timestamp: Timestamp,
        onComplete: (Boolean) -> Unit,
    ){
        val documentId = tasksRef.document().id
        val task = Task(
            userId,
            title,
            description,
            timestamp,
            documentId = documentId
        )
        tasksRef
            .document(documentId)
            .set(task)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }


    }

    fun deleteTask(taskId: String,onComplete: (Boolean) -> Unit){
        tasksRef.document(taskId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateTask(
        title: String,
        desc:String,
        taskId: String,
        onResult:(Boolean) -> Unit
    ){
        val updateData = hashMapOf<String,Any>(
            "description" to desc,
            "title" to title,
        )

        tasksRef.document(taskId)
            .update(updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }



    }

    fun signOut() = Firebase.auth.signOut()


}


sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)

}