package com.android.mycustomkeyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import com.android.mycustomkeyboard.databinding.KeyboardLayoutBinding

class SoftKeyboard : InputMethodService(), KeyboardView.OnKeyboardActionListener {
	private var keyboard: Keyboard? = null
	private var keyboardView: KeyboardView? = null
	private var SHIFT_PERM_TOGGLE_SPEED = 0
	private val KEYBOARD_LETTERS = 0
	private val KEYBOARD_SYMBOLS = 1
	private val KEYBOARD_SYMBOLS_SHIFT = 2
	private var lastShiftPressTS = 0L
	private var keyboardMode = KEYBOARD_LETTERS
	private var binding: KeyboardLayoutBinding? = null
	private var shiftEnabled = false

	override fun onCreateInputView(): View {
		binding = KeyboardLayoutBinding.inflate(layoutInflater, null, false)
		keyboard = Keyboard(this, R.xml.keyboard)
		keyboardView = binding?.keyboardView
		keyboardView?.keyboard = keyboard
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
				shiftEnabled = !shiftEnabled
				keyboardView?.isShifted = shiftEnabled
			}
		}
	}

	override fun onKey(primaryCode: Int, keyCodes: IntArray) {
		val ic = currentInputConnection
		when (primaryCode) {
			Keyboard.KEYCODE_DELETE -> ic.deleteSurroundingText(1, 0)
			Keyboard.KEYCODE_SHIFT -> {
				shiftEnabled = !shiftEnabled
				keyboardView?.isShifted = shiftEnabled
			}

			else -> {
				val code = primaryCode.toChar()
				val capsCode = if (shiftEnabled) code.uppercase() else code.lowercase()
				ic.commitText(capsCode.toString(), 1)
			}
		}
	}

	override fun onText(text: CharSequence?) {}

	override fun swipeLeft() {
		if (keyboardMode == KEYBOARD_SYMBOLS_SHIFT) {
			keyboardMode = KEYBOARD_LETTERS
			keyboard = Keyboard(this, R.xml.keyboard)
			keyboardView?.keyboard = keyboard
		} else if (keyboardMode == KEYBOARD_SYMBOLS) {
			keyboardMode = KEYBOARD_LETTERS
			keyboard = Keyboard(this, R.xml.keyboard)
			keyboardView?.keyboard = keyboard
		}
	}

	override fun swipeRight() {}

	override fun swipeDown() {}

	override fun swipeUp() {}
}

