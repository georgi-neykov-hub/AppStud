package appstud.neykov.com.appstudassigment.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ViewUtils {

    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable drawable){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            view.setBackground(drawable);
        }else{
            view.setBackgroundDrawable(drawable);
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static void hideSoftwareKeyboard(Activity activity){
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            hideSoftwareKeyboard(focusedView);
        }
    }

    public static void showSoftwareKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideSoftwareKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int getThemeAttribute(Resources.Theme theme, int themeAttr) {
        final TypedValue value = new TypedValue();
        theme.resolveAttribute(themeAttr, value, true);
        return value.resourceId;
    }

    public static int getAppCompatThemeAccentColor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.colorAccent, value, true);
        return ContextCompat.getColor(context, value.resourceId);
    }
}
