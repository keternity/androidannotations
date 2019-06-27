package org.androidannotations.api.x2c;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.math.BigDecimal;

public class DensityConfig {

    private static DensityConfig sIntance = new DensityConfig();


    private static final String KEY_DESIGN_WIDTH = "design_width";
    private static final String KEY_DESIGN_HEIGHT = "design_height";

    private int mScreenWidth;
    private int mScreenHeight;

    private int mDesignWidth;
    private int mDesignHeight;

    private double densityWidth, densityHeight;

    private boolean useDeviceSize;


    private DensityConfig() {
    }

    public void checkParams() {
        if (mDesignHeight <= 0 || mDesignWidth <= 0) {
            throw new RuntimeException(
                    "you must set " + KEY_DESIGN_WIDTH + " and " + KEY_DESIGN_HEIGHT + "  in your manifest file.");
        }
    }

    public DensityConfig useDeviceSize() {
        useDeviceSize = true;
        return this;
    }


    public static DensityConfig getInstance() {
        return sIntance;
    }


    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public int getDesignWidth() {
        return mDesignWidth;
    }

    public int getDesignHeight() {
        return mDesignHeight;
    }


    public int getReallyWidth(Context context, String val) {
        init(context);
        checkParams();
        BigDecimal valBigDecimal = new BigDecimal(val);
        BigDecimal widthBigDecimal = new BigDecimal(mScreenWidth);
        int res = valBigDecimal.multiply(widthBigDecimal).intValue();
        if (res % mDesignWidth == 0) {
            return res / mDesignWidth;
        } else {
            return res / mDesignWidth + 1;
        }
    }

    public int getReallyWidth(Context context, int val) {
        return getReallyWidth(context, String.valueOf(val));
    }

    public int getReallyHeight(Context context, String val) {
        init(context);
        checkParams();
        BigDecimal valBigDecimal = new BigDecimal(val);
        BigDecimal heightBigDecimal = new BigDecimal(mScreenHeight);
        int res = valBigDecimal.multiply(heightBigDecimal).intValue();
        if (res % mDesignHeight == 0) {
            return res / mDesignHeight;
        } else {
            return res / mDesignHeight + 1;
        }
    }

    public int getReallyHeight(Context context, int val) {
        return getReallyHeight(context, String.valueOf(val));
    }

    public void init(Context context) {
        if (densityHeight == 0 || densityWidth == 0) {
            getMetaData(context);
            int[] screenSize = ScreenUtils.getScreenSize(context, useDeviceSize);
            mScreenWidth = screenSize[0];
            mScreenHeight = screenSize[1];
            BigDecimal widthBigDecimal = new BigDecimal(mScreenWidth);
            BigDecimal heightBigDecimal = new BigDecimal(mScreenHeight);
            BigDecimal designWidthBigDecimal = new BigDecimal(mDesignWidth);
            BigDecimal designHeightBigDecimal = new BigDecimal(mDesignHeight);
            densityWidth = widthBigDecimal.divide(designWidthBigDecimal, 5, BigDecimal.ROUND_UNNECESSARY).doubleValue();
            densityHeight = heightBigDecimal.divide(designHeightBigDecimal, 5, BigDecimal.ROUND_UNNECESSARY).doubleValue();
        }
    }

    private void getMetaData(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(context
                    .getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                mDesignWidth = (int) applicationInfo.metaData.get(KEY_DESIGN_WIDTH);
                mDesignHeight = (int) applicationInfo.metaData.get(KEY_DESIGN_HEIGHT);
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(
                    "you must set " + KEY_DESIGN_WIDTH + " and " + KEY_DESIGN_HEIGHT + "  in your manifest file.", e);
        }
    }

}
