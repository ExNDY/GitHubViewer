package app.thirtyninth.githubviewer.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.thirtyninth.githubviewer.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
    }
}