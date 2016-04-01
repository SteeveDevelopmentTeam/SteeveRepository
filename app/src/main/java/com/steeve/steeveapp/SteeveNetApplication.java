package com.steeve.steeveapp;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

@ReportsCrashes(
        formUri = "https://steevenet.cloudant.com/acra-steevenet/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "fewhedarnalaystivendessi",
        formUriBasicAuthPassword = "af486016fa17c458bb50a2557b59585bd1bbf181",
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.bug_report
        )
public class SteeveNetApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
    }
}