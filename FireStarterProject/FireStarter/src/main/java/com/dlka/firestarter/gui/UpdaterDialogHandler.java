package com.dlka.firestarter.gui;

import android.app.Activity;
import android.app.ProgressDialog;

//import com.dlka.firestarter.R;
import com.dlka.firestarter.R;
import com.dlka.firestarter.tools.FireStarterUpdater;
import com.dlka.firestarter.tools.Updater;

/**
 * Handles the update-dialogs in case of an update
 */
public class UpdaterDialogHandler {
    /**
     * Check for update progress
     */
    ProgressDialog mCheckForUpdateProgress = null;
    /**
     * Update progress
     */
    ProgressDialog mUpdateProgress = null;
    /**
     * Listener for checkforupdate finished event
     */
    Updater.OnCheckForUpdateFinishedListener mCheckForUpdateFinishedListener = null;
    /**
     * Updater of the dialog handler
     */
    private Updater mUpdater;
    /**
     * Context of the current dialog handler
     */
    private Activity mActivity;
    /**
     * Handle check for update
     */
    Updater.OnCheckForUpdateFinishedListener mOnCheckForUpdateFinishedListener = new FireStarterUpdater.OnCheckForUpdateFinishedListener() {
        @Override
        public void onCheckForUpdateFinished(final String message) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mCheckForUpdateProgress != null) {
                        mCheckForUpdateProgress.dismiss();
                        mCheckForUpdateProgress = null;
                    }
                    mCheckForUpdateFinishedListener.onCheckForUpdateFinished(message);
                }
            });
        }
    };
    /**
     * Handle update progress
     */
    Updater.OnUpdateProgressListener mOnUpdateProgressListener = new FireStarterUpdater.OnUpdateProgressListener() {
        @Override
        public void onUpdateProgress(final Boolean isError, final Integer percent, final String message) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isError) {
                        mUpdateProgress.setProgress(100);
                        mUpdateProgress.setMessage(message);
                        mUpdateProgress.setCancelable(true);
                    } else {
                        mUpdateProgress.setProgress(percent);
                        mUpdateProgress.setMessage(message);
                        if (percent >= 100) {
                            mUpdateProgress.setCancelable(true);
                            mUpdateProgress.dismiss();
                        }
                    }
                }
            });
        }
    };

    public UpdaterDialogHandler(Activity activity, Updater updater) {
        mActivity = activity;
        mUpdater = updater;

        mUpdater.setOnCheckForUpdateFinishedListener(mOnCheckForUpdateFinishedListener);
        mUpdater.setOnUpdateProgressListener(mOnUpdateProgressListener);
    }

    public void setCheckForUpdateFinishedListener(Updater.OnCheckForUpdateFinishedListener listener) {
        mCheckForUpdateFinishedListener = listener;
    }

    public void checkForUpdate() {
        mCheckForUpdateProgress = ProgressDialog.show(mActivity, mActivity.getResources().getString(R.string.update_checkfortitle), mActivity.getResources().getString(R.string.update_checkfordesc), true);
        mUpdater.checkForUpdate();
    }

    public void performUpdate() {
        mUpdateProgress = new ProgressDialog(mActivity);
        mUpdateProgress.setMessage(mActivity.getResources().getString(R.string.update_checkformessage));
        mUpdateProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mUpdateProgress.setCancelable(false);
        mUpdateProgress.setProgress(0);
        mUpdateProgress.show();

        mUpdater.update(mActivity);
    }

}