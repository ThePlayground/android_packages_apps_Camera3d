package com.android.amaze_camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import com.htc.htcjavaflag.HtcBuildFlag;

public class DisplayDevice
{
  public static int BUTTON_SIZE;
  public static int CAMERA_PIC_SIZE_FOR_SQUARE;
  public static int CAMERA_PREVIEW_HEIGHT_FOR_4x3;
  public static int CAMERA_PREVIEW_SIZE_FOR_CONTACT;
  public static int CAMERA_PREVIEW_WIDTH_FOR_4x3;
  public static CaptureButton CAPTURE_BUTTON;
  public static CustomMMS CUSTOM_MMS;
  public static int DEFALULT_CAMERA_PREVIEW_HEIGHT_FOR_3X;
  public static int DEFALULT_CAMERA_PREVIEW_HEIGHT_FOR_3X_2ND;
  public static int DEFALULT_CAMERA_PREVIEW_WITDH_FOR_3X;
  public static int DEFALULT_CAMERA_PREVIEW_WITDH_FOR_3X_2ND;
  public static int DEFAULT_3D_CAMERA_CAPTURE_HEIGHT;
  public static int DEFAULT_3D_CAMERA_CAPTURE_WIDTH;
  public static int DEFAULT_3D_CAMERA_PREVIEW_HEIGHT;
  public static int DEFAULT_3D_CAMERA_PREVIEW_WIDTH;
  public static int DIALOG_HEIGHT;
  public static int DIALOG_WIDTH;
  private static int FOCUS_HEIGHT;
  private static int FOCUS_WIDTH;
  public static int HINT_LANDSCAPE_LEFT_PADDING;
  public static int HINT_PORTRAIT_LEFT_PADDING;
  public static int HINT_TOP_PADDING;
  public static int INDICATORE_LAYOUT_MARGIN_NO_ZOOM_BAR;
  public static int INDICATORE_LAYOUT_MARGIN_WITH_ZOOM_BAR;
  public static int MARKER_HEIGHT;
  public static final boolean NOT_SENSE_2_0;
  public static int REMAINING_LAYOUT_MARGIN_NO_EFFECT_BAR;
  public static int REMAINING_LAYOUT_MARGIN_WITH_EFFECT_BAR;
  public static int REMAINING_TEXT_MARGIN;
  public static int REMAINING_TEXT_PADDING;
  public static int SCREEN_HEIGHT;
  public static ScreenRatio SCREEN_RATIO;
  public static Resolution SCREEN_RESOLUTION = Resolution.HVGA;
  public static int SCREEN_WIDTH;
  public static int SELF_PORTRAIT_HINT_MARGIN_DEFAULT;
  public static int SELF_PORTRAIT_HINT_MARGIN_MAIN_BAR;
  public static int SELF_PORTRAIT_HINT_WIDTH_FULL_SCREEN;
  public static final boolean SUPPORT_FPS;
  public static final boolean SUPPORT_GPS_INDICATOR;
  public static final boolean SUPPORT_PREVIEW_ANIMATION;
  public static int SWITCH_BUTTON_MARGIN_NO_ZOOM_BAR;
  public static int SWITCH_BUTTON_MARGIN_WITH_ZOOM_BAR;
  private static int TIMER_BASE_RECORDING_PORTRAIT;
  public static int TIMER_BASE_SELF_TIMER;
  public static int WHEEL_BOUNCING_PADDING;
  public static int WHEEL_ROTATE_PADDING;
  public static int WHEEL_VSIBLE_AREA_PADDING;

  static
  {
    SCREEN_RATIO = ScreenRatio.Ratio_3_2;
    CAPTURE_BUTTON = CaptureButton.Other;
    CUSTOM_MMS = CustomMMS.Default;
    SCREEN_WIDTH = 0;
    SCREEN_HEIGHT = 0;
    DEFALULT_CAMERA_PREVIEW_WITDH_FOR_3X = 0;
    DEFALULT_CAMERA_PREVIEW_HEIGHT_FOR_3X = 0;
    CAMERA_PREVIEW_WIDTH_FOR_4x3 = 0;
    CAMERA_PREVIEW_HEIGHT_FOR_4x3 = 0;
    DEFALULT_CAMERA_PREVIEW_WITDH_FOR_3X_2ND = 640;
    DEFALULT_CAMERA_PREVIEW_HEIGHT_FOR_3X_2ND = 384;
    DEFAULT_3D_CAMERA_CAPTURE_WIDTH = 1920;
    DEFAULT_3D_CAMERA_CAPTURE_HEIGHT = 1080;
    DEFAULT_3D_CAMERA_PREVIEW_WIDTH = 1280;
    DEFAULT_3D_CAMERA_PREVIEW_HEIGHT = 720;
    CAMERA_PREVIEW_SIZE_FOR_CONTACT = 0;
    CAMERA_PIC_SIZE_FOR_SQUARE = 0;
    BUTTON_SIZE = 0;
    REMAINING_TEXT_PADDING = 0;
    REMAINING_TEXT_MARGIN = 0;
    DIALOG_HEIGHT = 300;
    DIALOG_WIDTH = 320;
    WHEEL_VSIBLE_AREA_PADDING = 28;
    MARKER_HEIGHT = 4;
    WHEEL_BOUNCING_PADDING = 2;
    HINT_TOP_PADDING = 100;
    HINT_LANDSCAPE_LEFT_PADDING = 160;
    HINT_PORTRAIT_LEFT_PADDING = 20;
    REMAINING_LAYOUT_MARGIN_WITH_EFFECT_BAR = 60;
    REMAINING_LAYOUT_MARGIN_NO_EFFECT_BAR = 0;
    INDICATORE_LAYOUT_MARGIN_WITH_ZOOM_BAR = 60;
    INDICATORE_LAYOUT_MARGIN_NO_ZOOM_BAR = 0;
    SWITCH_BUTTON_MARGIN_WITH_ZOOM_BAR = 31;
    SWITCH_BUTTON_MARGIN_NO_ZOOM_BAR = 0;
    TIMER_BASE_SELF_TIMER = 172;
    SELF_PORTRAIT_HINT_MARGIN_DEFAULT = 2;
    SELF_PORTRAIT_HINT_MARGIN_MAIN_BAR = 718;
    SELF_PORTRAIT_HINT_WIDTH_FULL_SCREEN = 740;
    WHEEL_ROTATE_PADDING = 0;
    if (!"3.0".equals("2.0"));
    for (boolean bool = true; ; bool = false)
    {
      NOT_SENSE_2_0 = bool;
      SCREEN_RESOLUTION = Resolution.QHD;
      SCREEN_RATIO = ScreenRatio.Ratio_16_9;
      SCREEN_WIDTH = 960;
      SCREEN_HEIGHT = 544;
      DEFALULT_CAMERA_PREVIEW_WITDH_FOR_3X = 960;
      DEFALULT_CAMERA_PREVIEW_HEIGHT_FOR_3X = 544;
      CAMERA_PREVIEW_WIDTH_FOR_4x3 = 640;
      CAMERA_PREVIEW_HEIGHT_FOR_4x3 = 480;
      CAMERA_PREVIEW_SIZE_FOR_CONTACT = 272;
      DEFALULT_CAMERA_PREVIEW_WITDH_FOR_3X_2ND = 1280;
      DEFALULT_CAMERA_PREVIEW_HEIGHT_FOR_3X_2ND = 720;
      BUTTON_SIZE = 76;
      DIALOG_HEIGHT = -1;
      DIALOG_WIDTH = 480;
      REMAINING_TEXT_PADDING = 10;
      REMAINING_TEXT_MARGIN = 100;
      WHEEL_VSIBLE_AREA_PADDING = 3;
      MARKER_HEIGHT = 4;
      WHEEL_BOUNCING_PADDING = 2;
      HINT_TOP_PADDING = 208;
      HINT_LANDSCAPE_LEFT_PADDING = 70;
      HINT_PORTRAIT_LEFT_PADDING = 48;
      WHEEL_ROTATE_PADDING = 6;
      CAPTURE_BUTTON = CaptureButton.HWKey;
      CUSTOM_MMS = CustomMMS.Default;
      FOCUS_WIDTH = -1;
      FOCUS_HEIGHT = -1;
      TIMER_BASE_RECORDING_PORTRAIT = -1;
      return;
    }
  }

  public static boolean DisablePen()
  {
    return false;
  }

  public static boolean EnableGeoTagByDefault()
  {
    return true;
  }

  public static boolean NoResolutionChoiceIn2ndCam()
  {
    if ((HtcBuildFlag.Htc_DEVICE_flag == 40) || (HtcBuildFlag.Htc_DEVICE_flag == 33) || (HtcBuildFlag.Htc_DEVICE_flag == 34) || (HtcBuildFlag.Htc_DEVICE_flag == 16) || (HtcBuildFlag.Htc_DEVICE_flag == 66));
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean apply720PPreview()
  {
    return true;
  }

  public static boolean canCancelFocus()
  {
    return true;
  }

  public static boolean captrueFullSize()
  {
    return true;
  }

  public static boolean contactsNoStorage()
  {
    return true;
  }

  public static boolean forceFocusSound()
  {
    return false;
  }

  public static boolean forceSutterSound()
  {
    return false;
  }

  public static int getFocusHeight(Context paramContext)
  {
    if (FOCUS_HEIGHT < 0)
    {
      Drawable localDrawable = paramContext.getResources().getDrawable(2130837832);
      FOCUS_WIDTH = localDrawable.getIntrinsicWidth();
      FOCUS_HEIGHT = localDrawable.getIntrinsicHeight();
    }
    return FOCUS_HEIGHT;
  }

  public static int getFocusWidth(Context paramContext)
  {
    if (FOCUS_WIDTH < 0)
    {
      Drawable localDrawable = paramContext.getResources().getDrawable(2130837832);
      FOCUS_WIDTH = localDrawable.getIntrinsicWidth();
      FOCUS_HEIGHT = localDrawable.getIntrinsicHeight();
    }
    return FOCUS_WIDTH;
  }

  public static int getRecordingTimerBaseHeight(Context paramContext, int paramInt)
  {
    if (TIMER_BASE_RECORDING_PORTRAIT < 0)
      TIMER_BASE_RECORDING_PORTRAIT = paramContext.getResources().getDrawable(2130837848).getIntrinsicHeight();
    if ((paramInt == 0) || (paramInt == 2));
    for (int i = TIMER_BASE_RECORDING_PORTRAIT; ; i = 10 + TIMER_BASE_RECORDING_PORTRAIT)
      return i;
  }

  public static boolean hasAutoFocus()
  {
    return true;
  }

  public static boolean hasLimit250KB()
  {
    return false;
  }

  public static boolean isDefaultQuality720p()
  {
    if (!support720p());
    for (int i = 0; ; i = 0)
      return i;
  }

  public static boolean isDelayCreateImageThumb()
  {
    return false;
  }

  public static boolean isDelayRecording()
  {
    return false;
  }

  public static boolean isDisplayGPSindicator()
  {
    return true;
  }

  public static boolean isDoubleShot()
  {
    return true;
  }

  public static boolean isHalfPCB()
  {
    return true;
  }

  public static boolean isVirtualHWKeyRotated()
  {
    return false;
  }

  public static boolean isWideScreen()
  {
    if (SCREEN_RATIO == ScreenRatio.Ratio_4_3);
    for (int i = 0; ; i = 1)
      return i;
  }

  public static final boolean needDelayZooming()
  {
    return false;
  }

  public static boolean notSupportH264()
  {
    if (supportQCT7x27());
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean removeMMS()
  {
    return false;
  }

  public static boolean showEffectInMenu()
  {
    if (isDoubleShot());
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean showFocusWithoutDelay()
  {
    return true;
  }

  public static boolean showISOInMenu()
  {
    return false;
  }

  public static boolean showSceneInMenu()
  {
    if (isDoubleShot());
    for (int i = 0; ; i = 1)
      return i;
  }

  public static boolean support128kBitrate()
  {
    return true;
  }

  public static boolean support1M2ndCam()
  {
    if (supportSpecific2ndCamera());
    for (int i = 0; ; i = 1)
      return i;
  }

  public static boolean support2M2ndCam()
  {
    if ((HtcBuildFlag.Htc_DEVICE_flag == -83) || (HtcBuildFlag.Htc_DEVICE_flag == -119));
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean support2ndCamAutoEnhance()
  {
    if (HtcBuildFlag.Htc_DEVICE_flag == -119);
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean support2ndCamNoneMirror()
  {
    return true;
  }

  public static boolean support3DCamera()
  {
    return true;
  }

  public static boolean support3DHWSwitch()
  {
    return true;
  }

  public static boolean support720p()
  {
    if ((supportQCT8x50()) || (supportQCT7x30()) || (supportQCT8x60()));
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean support720p2ndCam()
  {
    if ((HtcBuildFlag.Htc_DEVICE_flag == -83) || (HtcBuildFlag.Htc_DEVICE_flag == 62) || (HtcBuildFlag.Htc_DEVICE_flag == -120) || (HtcBuildFlag.Htc_DEVICE_flag == -119) || (HtcBuildFlag.Htc_DEVICE_flag == -118));
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean supportAutoUpload()
  {
    return false;
  }

  public static boolean supportCamcorderHWButton()
  {
    return true;
  }

  public static boolean supportCamcorderHWSwitch()
  {
    return false;
  }

  public static boolean supportCamcorderRotate()
  {
    if (supportIconRotate());
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean supportFaceDetection()
  {
    return true;
  }

  public static boolean supportFastFrameRecording()
  {
    return false;
  }

  public static boolean supportGpuEffect()
  {
    return true;
  }

  public static boolean supportH264()
  {
    if ((supportQCT8x50()) || (supportQCT7x30()));
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean supportHTCMediaUploader()
  {
    if ("3.0".equals("3.0"));
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean supportHVGARecording()
  {
    return true;
  }

  public static boolean supportHWShareButton()
  {
    return false;
  }

  public static boolean supportISO()
  {
    return true;
  }

  public static boolean supportISO1250()
  {
    return true;
  }

  public static boolean supportIconRotate()
  {
    return true;
  }

  public static boolean supportMMSVedioRecording()
  {
    return true;
  }

  public static boolean supportOnlyMP4VideoFormat()
  {
    return true;
  }

  public static boolean supportQCT7201()
  {
    return false;
  }

  public static boolean supportQCT7225()
  {
    return false;
  }

  public static boolean supportQCT7x27()
  {
    return false;
  }

  public static boolean supportQCT7x30()
  {
    return false;
  }

  public static boolean supportQCT8x50()
  {
    return false;
  }

  public static boolean supportQCT8x60()
  {
    return true;
  }

  public static boolean supportScalado()
  {
    return true;
  }

  public static boolean supportSecondCamera()
  {
    return true;
  }

  public static boolean supportSensor()
  {
    return true;
  }

  public static boolean supportSensorFocus()
  {
    return true;
  }

  public static boolean supportSharpSensor()
  {
    return true;
  }

  public static boolean supportSharpSensorResolution()
  {
    return true;
  }

  public static boolean supportSpecific2ndCamera()
  {
    if ((HtcBuildFlag.Htc_DEVICE_flag == 48) || (HtcBuildFlag.Htc_DEVICE_flag == -108) || (HtcBuildFlag.Htc_DEVICE_flag == 123) || (HtcBuildFlag.Htc_DEVICE_flag == 125) || (HtcBuildFlag.Htc_DEVICE_flag == 28) || (HtcBuildFlag.Htc_DEVICE_flag == 66) || (HtcBuildFlag.Htc_DEVICE_flag == 16) || (HtcBuildFlag.Htc_DEVICE_flag == -99) || (HtcBuildFlag.Htc_DEVICE_flag == 14) || (HtcBuildFlag.Htc_DEVICE_flag == 15));
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean supportStereoRecord()
  {
    return true;
  }

  public static boolean supportSwitchButton()
  {
    if (!supportSecondCamera());
    for (int i = 0; ; i = 0)
      return i;
  }

  public static boolean supportTapScreenCapture()
  {
    return true;
  }

  public static boolean supportThumbnailAlbumButton()
  {
    return false;
  }

  public static boolean supportTrackBallFocus()
  {
    return false;
  }

  public static boolean supportVideoFormatChoosing()
  {
    return false;
  }

  public static boolean supportWideScreen2ndCamera()
  {
    if ((HtcBuildFlag.Htc_DEVICE_flag == 48) || (HtcBuildFlag.Htc_DEVICE_flag == 123) || (HtcBuildFlag.Htc_DEVICE_flag == 125) || (HtcBuildFlag.Htc_DEVICE_flag == -108) || (HtcBuildFlag.Htc_DEVICE_flag == 14) || (HtcBuildFlag.Htc_DEVICE_flag == 15));
    for (int i = 0; ; i = 1)
      return i;
  }

  public static enum CustomMMS
  {
    static
    {
      ATT = new CustomMMS("ATT", 1);
      Sprint = new CustomMMS("Sprint", 2);
      Verizon = new CustomMMS("Verizon", 3);
      CustomMMS[] arrayOfCustomMMS = new CustomMMS[4];
      arrayOfCustomMMS[0] = Default;
      arrayOfCustomMMS[1] = ATT;
      arrayOfCustomMMS[2] = Sprint;
      arrayOfCustomMMS[3] = Verizon;
      $VALUES = arrayOfCustomMMS;
    }
  }

  public static enum CaptureButton
  {
    static
    {
      CaptureButton[] arrayOfCaptureButton = new CaptureButton[3];
      arrayOfCaptureButton[0] = ActionKey;
      arrayOfCaptureButton[1] = HWKey;
      arrayOfCaptureButton[2] = Other;
      $VALUES = arrayOfCaptureButton;
    }
  }

  public static enum ScreenRatio
  {
    static
    {
      Ratio_3_2 = new ScreenRatio("Ratio_3_2", 1);
      Ratio_5_3 = new ScreenRatio("Ratio_5_3", 2);
      Ratio_16_9 = new ScreenRatio("Ratio_16_9", 3);
      Tablet_Ratio_16_9 = new ScreenRatio("Tablet_Ratio_16_9", 4);
      ScreenRatio[] arrayOfScreenRatio = new ScreenRatio[5];
      arrayOfScreenRatio[0] = Ratio_4_3;
      arrayOfScreenRatio[1] = Ratio_3_2;
      arrayOfScreenRatio[2] = Ratio_5_3;
      arrayOfScreenRatio[3] = Ratio_16_9;
      arrayOfScreenRatio[4] = Tablet_Ratio_16_9;
      $VALUES = arrayOfScreenRatio;
    }
  }

  public static enum Resolution
  {
    static
    {
      HVGA = new Resolution("HVGA", 1);
      QVGA = new Resolution("QVGA", 2);
      WQVGA = new Resolution("WQVGA", 3);
      WSVGA = new Resolution("WSVGA", 4);
      QHD = new Resolution("QHD", 5);
      HD = new Resolution("HD", 6);
      Resolution[] arrayOfResolution = new Resolution[7];
      arrayOfResolution[0] = WVGA;
      arrayOfResolution[1] = HVGA;
      arrayOfResolution[2] = QVGA;
      arrayOfResolution[3] = WQVGA;
      arrayOfResolution[4] = WSVGA;
      arrayOfResolution[5] = QHD;
      arrayOfResolution[6] = HD;
      $VALUES = arrayOfResolution;
    }
  }
}

/* Location:           /Volumes/android/github-aosp_source/android_packages_apps_Camera3d/dex2jar.jar
 * Qualified Name:     com.android.amaze_camera.DisplayDevice
 * JD-Core Version:    0.6.0
 */