package it_geeks.info.elgawla.util;

import android.app.Activity;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;

public class TourManager {

    public static void salonPageSequence(final Activity activity, View... targets) {
        if (SharedPrefManager.getInstance(activity).isSalonPageTourFinished())
        {
            return;
        }
        new TapTargetSequence(activity)
                .targets(TapTarget.forView(targets[0], activity.getString(R.string.tt_salon_level_title), activity.getString(R.string.tt_salon_level_description))
                                .textColor(android.R.color.white)
                                .outerCircleColor(R.color.colorLightPrimary)
                                .outerCircleAlpha(0.9f)
                                .targetRadius(140)
                                .transparentTarget(false)
                                .tintTarget(false)
                                .targetCircleColor(android.R.color.white)
                        , TapTarget.forView(targets[1], activity.getString(R.string.tt_salon_countdown_title), activity.getString(R.string.tt_salon_countdown_description))
                                .textColor(android.R.color.white)
                                .outerCircleColor(R.color.colorLightPrimary)
                                .outerCircleAlpha(0.9f)
                                .targetRadius(176)
                                .transparentTarget(false)
                                .tintTarget(false)
                                .targetCircleColor(android.R.color.white)
                )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        EventsManager.sendTutorialCompleteEvent(activity);
                        SharedPrefManager.getInstance(activity).setSalonPageTourFinished(true);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                })
                .continueOnCancel(true)
                .start();
    }

    public static void mainPageSequence(final Activity activity, View... targets) {
        if (SharedPrefManager.getInstance(activity).isMainPageTourFinished())
        {
            return;
        }

        EventsManager.sendTutorialBeginEvent(activity);
        new TapTargetSequence(activity)
                .targets(TapTarget.forView(targets[0], activity.getString(R.string.tt_notification_title), activity.getString(R.string.tt_notitfication_description))
                                .textColor(android.R.color.white)
                                .outerCircleColor(R.color.colorLightPrimary)
                                .targetCircleColor(R.color.colorSecondary)
                                .transparentTarget(false)
                                .tintTarget(false)
                                .outerCircleAlpha(0.9f)
                        , TapTarget.forView(targets[1], activity.getString(R.string.tt_user_image_title), activity.getString(R.string.tt_user_image_description))
                                .textColor(android.R.color.white)
                                .outerCircleColor(R.color.colorLightPrimary)
                                .targetCircleColor(R.color.colorSecondary)
                                .transparentTarget(false)
                                .tintTarget(false)
                                .outerCircleAlpha(0.9f)
                )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        SharedPrefManager.getInstance(activity).setMainPageTourFinished(true);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                })
                .continueOnCancel(true)
                .start();
    }
}
