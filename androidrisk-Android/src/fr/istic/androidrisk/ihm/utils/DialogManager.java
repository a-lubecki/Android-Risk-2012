package fr.istic.androidrisk.ihm.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;

/**
 * This class provides static methods to show different dialogs.
 */
public final class DialogManager {

	static final String TAG = "DialogManager";

	public static AlertDialog showErrorDialog(Activity currentActivity, String message) throws Exception{
		checkActivity(currentActivity, message);

		AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity)
		.setCancelable(true)
		.setTitle("Erreur")
		.setMessage(message);

		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}

	public static AlertDialog showErrorDialog(Activity currentActivity, String message, DialogInterface.OnCancelListener onCancelListener) throws Exception{

		AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity)
		.setCancelable(true)
		.setTitle("Erreur")
		.setOnCancelListener(onCancelListener)
		.setMessage(message);

		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}

	public static AlertDialog showOKDialog(Activity currentActivity, String title, String message, DialogInterface.OnClickListener onClickOK) throws Exception {
		checkActivity(currentActivity, message);

		AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);

		builder.setTitle(title)
		.setCancelable(false)
		.setMessage(message)
		.setPositiveButton("OK", onClickOK);

		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}

	public static AlertDialog showOkCancelDialog(Activity currentActivity, String message,  DialogInterface.OnClickListener onClickAnnuler, DialogInterface.OnClickListener onClickOK) throws Exception {
		checkActivity(currentActivity, message);

		AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);

		builder.setCancelable(true)
		.setMessage(message)
		.setNegativeButton("Annuler", onClickAnnuler)
		.setPositiveButton("OK", onClickOK);

		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}

	public static ProgressDialog showProgressDialog(Activity currentActivity, String message) throws Exception {
		checkActivity(currentActivity, message);

		return ProgressDialog.show(currentActivity, "", message, true, false);
	}

	public static ProgressDialog afficherProgressDialog(Activity currentActivity, String message, DialogInterface.OnCancelListener onCancel) throws Exception {
		checkActivity(currentActivity, message);

		return ProgressDialog.show(currentActivity, "", message, true, true, onCancel);
	}

	/**
	 * Check if the activity passed in parameters of methods is null.
	 * @param activity
	 * @param message The message of the dialog
	 * @throws Exception
	 */
	private static void checkActivity(Activity activity, String message) throws Exception {
		if(activity == null){
			Log.e(TAG, "Activity for Dialog is null. Message : " + message);
			throw new Exception("Current Activity is null.");
		}
	}

	/**
	 * Show an error dialog which will end the activity by clicking on it.
	 * @param currentActivity
	 * @param message
	 */
	public static void showActivityEndError(final Activity currentActivity, String message) {
		try {
			DialogManager.showOKDialog(
					currentActivity,
					"Erreur fatale",
					message + "\nL'application va maintenant quitter.",
					new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							currentActivity.finish();
						}
					});
		} catch (Exception e) {
			currentActivity.finish();
			return;
		}
	}

}
