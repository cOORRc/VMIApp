package com.KioskApp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class hideKeyboard {
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager =
				(InputMethodManager) activity.getSystemService(
						Activity.INPUT_METHOD_SERVICE);

		/// edit 08/02/64 because NullPointerException: Attempt to invoke virtual method 'android.os.IBinder
		// android.view.View.getWindowToken()' on a null object reference
		/*			inputMethodManager.hideSoftInputFromWindow(
					activity.getCurrentFocus().getWindowToken(), 0);*/

		View focusedView = activity.getCurrentFocus();
		if (focusedView!=null){
			inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		if (focusedView==null) {
			Log.d("debug","hide");
		}

	}

	public static void setupUI(View view, final Activity activity) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new View.OnTouchListener() {
				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(activity);
					return false;
				}
			});
		}

		//If a layout container, iterate over children and seed recursion.(view instanceof ViewGroup)
		else {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView, activity);
			}
		}
	}
}
