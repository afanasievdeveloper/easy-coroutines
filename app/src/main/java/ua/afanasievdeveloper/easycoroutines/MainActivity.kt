package ua.afanasievdeveloper.easycoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import ua.afanasievdeveloper.easycoroutinescore.lonely.Lonely

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testButton.setOnClickListener { getSomeResult() }
    }

    private fun getSomeResult() {
        Lonely<String>(Dispatchers.IO, Dispatchers.Main).from { someSuspend() }
            .doOnSubscribe { testTextView.text = "Loading" }
            .subscribe(
                { testTextView.text = "Success: $it" },
                { testTextView.text = "Failure" }
            )
    }

    private suspend fun someSuspend(): String {
        return someAsync().await()
    }

    private fun someAsync(): Deferred<String> {
        Thread.sleep(3000)
        return CompletableDeferred("Current time ${System.currentTimeMillis()}")
    }

    private companion object {
        private const val TAG = "easycoroutines.TAG"
    }
}

