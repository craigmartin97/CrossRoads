package com.kitkat.crossroads.ExternalClasses;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class GenericMethods
{
    /**
     * Creating a custom toast message for all Fragments to Access
     * @param message
     * @param fragmentActivity
     */
    public void customToastMessage(String message, FragmentActivity fragmentActivity)
    {
        Toast.makeText(fragmentActivity, message, Toast.LENGTH_SHORT).show();
    }
}
