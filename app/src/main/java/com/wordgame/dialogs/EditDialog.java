package com.wordgame.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wordgame.R;

/**
 * Dialog with edit field and "yes", "no" buttons.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class EditDialog {
    private final String title;
    private final String hint;

    /**
     * Interface for the dialog close event.
     */
    public interface OnDialogCloseListener {
        default void onYesClick(EditText editText) {
        }

        default void onNoClick(EditText editText) {
        }
    }

    /**
     * Constructor.
     *
     * @param title title
     * @param hint  hint
     */
    public EditDialog(String title, String hint) {
        this.title = title;
        this.hint = hint;
    }

    /**
     * Shows dialog for changing the password.
     *
     * @param activity              activity
     * @param onDialogCloseListener listener for dialog close event
     */
    public void showDialog(final Activity activity,
                           final OnDialogCloseListener onDialogCloseListener) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_edit);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView titleTextView = dialog.findViewById(R.id.dialog_edit_title);
        titleTextView.setText(title);

        EditText editText = dialog.findViewById(R.id.dialog_edit_txt);
        editText.setHint(hint);

        FrameLayout frameNo = dialog.findViewById(R.id.dialog_info_frame_ok);
        frameNo.setOnClickListener(v -> {
            onDialogCloseListener.onNoClick(editText);
            dialog.dismiss();
        });

        FrameLayout frameYes = dialog.findViewById(R.id.dialog_edit_frame_yes);
        frameYes.setOnClickListener(v -> {
            onDialogCloseListener.onYesClick(editText);
            if (editText.getError() == null) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
