package com.android.mycustomkeyboard.service

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.android.mycustomkeyboard.R
import com.android.mycustomkeyboard.databinding.KeyboardLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoftKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {
	private var keyboardLetters: Keyboard? = null
	private var keyboardSymbolsNumbers: Keyboard? = null
	private var keySymbolsShift: Keyboard? = null
	private var keyboardView: KeyboardView? = null
	private var lastShiftPressTS = 0L
	private var isShifted = false
	private var isSymbolsNumbersMode = false
	private var binding: KeyboardLayoutBinding? = null
	private var isSymbolsShiftMode = false

	companion object {
		private const val SHIFT_PERM_TOGGLE_SPEED = 0
		private const val KEYBOARD_SPACE = 32
		private const val KEYBOARD_SYMBOLS_SHIFT = -3
	}

	override fun onCreateInputView(): View {
		binding = KeyboardLayoutBinding.inflate(layoutInflater, null, false)
		keyboardLetters = Keyboard(this, R.xml.keyboard_letters)
		keyboardSymbolsNumbers = Keyboard(this, R.xml.keyboard_symbols_numbers)
		keySymbolsShift = Keyboard(this, R.xml.keys_symbols_shift)
		keyboardView = binding?.keyboardView
		keyboardView?.keyboard = keyboardLetters
		keyboardView?.setOnKeyboardActionListener(this)
		return keyboardView as View
	}

	override fun onPress(primaryCode: Int) {
		if (primaryCode == Keyboard.KEYCODE_SHIFT) {
			lastShiftPressTS = System.currentTimeMillis()
		}
	}

	override fun onRelease(primaryCode: Int) {
		if (primaryCode == Keyboard.KEYCODE_SHIFT) {
			if (System.currentTimeMillis() - lastShiftPressTS < SHIFT_PERM_TOGGLE_SPEED) {
				isShifted = !isShifted
				keyboardView?.isShifted = isShifted
			}
		}
	}

	override fun onKey(primaryCode: Int, keyCodes: IntArray) {
		val ic = currentInputConnection
		when (primaryCode) {
			Keyboard.KEYCODE_DONE -> ic.sendKeyEvent(
				KeyEvent(
					KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_ENTER
				)
			)

			Keyboard.KEYCODE_DELETE -> ic.deleteSurroundingText(1, 0)
			KEYBOARD_SPACE -> ic.commitText(" ", 1)
			Keyboard.KEYCODE_SHIFT -> {
				isShifted = !isShifted
				changeShiftKeyIconAfterClick(isShifted)
				keyboardView?.isShifted = isShifted
			}

			Keyboard.KEYCODE_MODE_CHANGE -> {
				isSymbolsNumbersMode = !isSymbolsNumbersMode
				keyboardView?.keyboard =
					if (isSymbolsNumbersMode) keyboardSymbolsNumbers else keyboardLetters
			}

			KEYBOARD_SYMBOLS_SHIFT -> {
				isSymbolsShiftMode = !isSymbolsShiftMode
				keyboardView?.keyboard =
					if (isSymbolsShiftMode) keySymbolsShift else keyboardSymbolsNumbers
			}

			else -> {
				val code = primaryCode.toChar()
				val capsCode = if (isShifted) code.uppercase() else code.lowercase()
				ic.commitText(capsCode, 1)
			}
		}
	}

	private fun changeShiftKeyIconAfterClick(isCaps: Boolean) {
		val shiftKey = keyboardView?.keyboard?.keys?.find { it.codes[0] == Keyboard.KEYCODE_SHIFT }
		shiftKey?.icon = keyboardView?.context?.let { context ->
			ContextCompat.getDrawable(
				context,
				if (isCaps) R.drawable.ic_keyboard_caps else R.drawable.ic_keyboard_caps_outline
			)
		}
		keyboardView?.invalidateAllKeys()
	}

	override fun onText(text: CharSequence?) {}

	override fun swipeLeft() {}

	override fun swipeRight() {}

	override fun swipeDown() {}

	override fun swipeUp() {}
}
