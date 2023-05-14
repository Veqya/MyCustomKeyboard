package com.android.mycustomkeyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.android.mycustomkeyboard.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
	private var binding: ActivityMainBinding? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater).also { binding ->
			setContentView(binding.root)
			with(binding) {
				activityMainSettingsButton.setOnClickListener {
					enableKeyboard()
				}
				activityMainKeyboardButton.setOnClickListener {
					chooseKeyboard()
				}
			}
		}

	}

	private fun chooseKeyboard() {
		val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
		inputMethodManager.showInputMethodPicker()
	}

	private fun enableKeyboard() {
		val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
		startActivity(intent)
	}
}