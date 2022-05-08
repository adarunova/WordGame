package com.wordgame.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wordgame.R;

/**
 * Dialog with "yes", "no" buttons.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class YesNoDialog {

    private final String title;
    private final String text;

    /**
     * Interface for the dialog close event.
     */
    public interface OnDialogCloseListener {
        default void onYesClick() {
        }

        default void onNoClick() {
        }
    }

    /**
     * Constructor.
     *
     * @param title title
     * @param text  text
     */
    public YesNoDialog(String title, String text) {
        this.title = title;
        this.text = text;
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
        dialog.setContentView(R.layout.dialog_yes_no);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView titleTextView = dialog.findViewById(R.id.dialog_yes_no_title);
        titleTextView.setText(title);

        TextView textTextView = dialog.findViewById(R.id.dialog_yes_no_text);
        textTextView.setText(text);

        FrameLayout frameNo = dialog.findViewById(R.id.dialog_yes_no_frame_no);
        frameNo.setOnClickListener(v -> {
            onDialogCloseListener.onNoClick();
            dialog.dismiss();
        });

        FrameLayout frameYes = dialog.findViewById(R.id.dialog_yes_no_frame_yes);
        frameYes.setOnClickListener(v -> {
            onDialogCloseListener.onYesClick();
            dialog.dismiss();
        });

        dialog.show();
    }
}
