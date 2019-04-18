package com.lauwba.surelabs.mishopcustomer.config;

import agency.tango.materialintroscreen.animations.ViewTranslationWrapper;
import android.view.View;

public class PermissionNotGrantedClickListener implements View.OnClickListener {
    private final MaterialIntroActivity activity;
    private final ViewTranslationWrapper translationWrapper;

    public PermissionNotGrantedClickListener(MaterialIntroActivity activity, ViewTranslationWrapper translationWrapper) {
        this.activity = activity;
        this.translationWrapper = translationWrapper;
    }

    @Override
    public void onClick(View v) {
        translationWrapper.error();
        activity.showPermissionsNotGrantedError();
    }
}
