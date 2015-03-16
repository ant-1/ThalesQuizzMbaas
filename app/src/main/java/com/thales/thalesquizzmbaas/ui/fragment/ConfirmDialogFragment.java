package com.thales.thalesquizzmbaas.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

public class ConfirmDialogFragment extends DialogFragment {

    private String mQuestionText;
    private String mOkText;
    private String mCancelText;
    private ConfirmDialogFragmentListener mConfirmDialogFragmentListener;

    public interface ConfirmDialogFragmentListener {
        void onOkClicked();

        void onCancelClicked();
    }

    public static ConfirmDialogFragment newInstance() {
        ConfirmDialogFragment frag = new ConfirmDialogFragment();
        return frag;
    }

    public void setQuestionText(String questionText) {
        this.mQuestionText = questionText;
    }

    public void setOkText(String okText) {
        this.mOkText = okText;
    }

    public void setCancelText(String cancelText) {
        this.mCancelText = cancelText;
    }

    public void setConfirmDialogFragmentListener(ConfirmDialogFragmentListener confirmDialogFragmentListener) {
        this.mConfirmDialogFragmentListener = confirmDialogFragmentListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mQuestionText)
                .setPositiveButton(mOkText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mConfirmDialogFragmentListener != null) {
                            mConfirmDialogFragmentListener.onOkClicked();
                        }
                    }
                });
        if (!TextUtils.isEmpty(mCancelText)) {
            builder.setNegativeButton(mCancelText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (mConfirmDialogFragmentListener != null) {
                        mConfirmDialogFragmentListener.onCancelClicked();
                    }
                }
            });
        }
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
