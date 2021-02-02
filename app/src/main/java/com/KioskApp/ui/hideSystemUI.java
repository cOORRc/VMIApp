package com.KioskApp.ui;

import android.app.Activity;
import android.view.View;

public class hideSystemUI {
	public static void hideNavigations(Activity activity) {
//		View decorView = activity.getWindow().getDecorView();
//		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//				| View.SYSTEM_UI_FLAG_FULLSCREEN
//				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

		final View decorView = activity.getWindow().getDecorView();
		decorView.setOnSystemUiVisibilityChangeListener
				(new View.OnSystemUiVisibilityChangeListener() {
					@Override
					public void onSystemUiVisibilityChange(int visibility) {
						// Note that system bars will only be "visible" if none of the
						// LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
						if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
							// TODO: The system bars are visible. Make any desired
							// adjustments to your UI, such as showing the action bar or
							// other navigational controls.

							decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
									| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
									| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_FULLSCREEN
									| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

						} else {
							// TODO: The system bars are NOT visible. Make any desired
							// adjustments to your UI, such as hiding the action bar or
							// other navigational controls.
							decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
									| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
									| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_FULLSCREEN
									| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
						}
					}
				});

	}
}
