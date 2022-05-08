package com.wordgame.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.wordgame.R;
import com.wordgame.utils.UserException;
import com.wordgame.utils.UserUtils;

/**
 * Dialog for changing the password.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class ChangePasswordDialog {

    /**
     * Interface for the dialog close event.
     */
    public interface OnDialogCloseListener {
        default void onYesClick(String oldPassword, String newPassword) {
        }

        default void onNoClick(String oldPassword, String newPassword) {
        }
    }

    /**
     * Shows dialog for changing the password.
     *
     * @param activity              activity
     * @param onDialogCloseListener listener for dialog close event
     */
    public void showDialog(final Activity activity,
                           final ChangePasswordDialog.OnDialogCloseListener onDialogCloseListener) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_change_password);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        EditText passwordEditText = dialog.findViewById(R.id.dialog_password_old_txt);
        EditText newPasswordEditText = dialog.findViewById(R.id.dialog_password_new_txt);

        FrameLayout frameNo = dialog.findViewById(R.id.dialog_password_frame_no);
        frameNo.setOnClickListener(v -> {
            onDialogCloseListener.onNoClick(
                    passwordEditText.getText().toString(),
                    newPasswordEditText.getText().toString()
            );
            dialog.dismiss();
        });

        FrameLayout frameYes = dialog.findViewById(R.id.dialog_password_frame_yes);
        frameYes.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            if (password.isEmpty()) {
                passwordEditText.setError(activity.getResources().getString(R.string.password_empty_exception));
                return;
            }
            try {
                UserUtils.checkPassword(activity, newPassword);
            } catch (UserException e) {
                newPasswordEditText.setError(e.getMessage());
                return;
            }

            onDialogCloseListener.onYesClick(password, newPassword);
            dialog.dismiss();
        });

        dialog.show();
    }
}
