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
public class InfoDialog {

    // Information text.
    private final String text;

    /**
     * Interface for the dialog close event.
     */
    public interface OnDialogCloseListener {
        default void onOkClick() {
        }
    }

    /**
     * Constructor.
     *
     * @param text information text
     */
    public InfoDialog(String text) {
        this.text = text;
    }

    /**
     * Shows dialog for changing the password.
     *
     * @param activity activity
     */
    public void showDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_info);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView textTextView = dialog.findViewById(R.id.dialog_info_tv);
        textTextView.setText(text);

        FrameLayout frameOk = dialog.findViewById(R.id.dialog_info_frame_ok);
        frameOk.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Shows dialog for changing the password.
     *
     * @param activity              activity
     * @param onDialogCloseListener listener for dialog close event
     */
    public void showDialog(final Activity activity,
                           final InfoDialog.OnDialogCloseListener onDialogCloseListener) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_info);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView textTextView = dialog.findViewById(R.id.dialog_info_tv);
        textTextView.setText(text);

        FrameLayout frameOk = dialog.findViewById(R.id.dialog_info_frame_ok);
        frameOk.setOnClickListener(v -> {
            dialog.dismiss();
            onDialogCloseListener.onOkClick();
        });

        dialog.show();
    }
}
