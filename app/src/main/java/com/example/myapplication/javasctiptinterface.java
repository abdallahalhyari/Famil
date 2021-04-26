package com.example.myapplication;

import android.webkit.JavascriptInterface;

import org.jetbrains.annotations.NotNull;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

import org.jetbrains.annotations.NotNull;

class javasctiptinterface {

    private calling callActivity;

    public javasctiptinterface(calling calling) {
        callActivity= calling;
    }

    @JavascriptInterface
    public void onPeerConnected() {
        callActivity.onPeerConnected();
    }

}


