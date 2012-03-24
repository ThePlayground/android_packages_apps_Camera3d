package com.android.amaze_camera;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.location.Location;
import java.util.List;

public class CameraController
{
  private static final int INIT_BRIGHTNESS = 0;
  private static final int INIT_CONTRAST = 5;
  private static final int INIT_SATURATION = 5;
  private static final int INIT_SHARPNESS = 15;
  public static final String KEY_GPU_EFFECT = "GPU-effect";
  public static final String KEY_GPU_EFFECT_PARAM_0 = "GE-param0";
  public static final String KEY_GPU_EFFECT_PARAM_1 = "GE-param1";
  public static final String KEY_GPU_EFFECT_PARAM_2 = "GE-param2";
  public static final String KEY_GPU_EFFECT_PARAM_3 = "GE-param3";
  private static final String TAG = "CameraController";
  private Camera mCamera;
  private Camera.Parameters mCameraParameters = null;
  private int mInjectParam0 = 0;
  private int mInjectParam1 = 0;

  public CameraController(Camera paramCamera)
  {
    this.mCamera = paramCamera;
    this.mCameraParameters = this.mCamera.getParameters();
  }

  public static boolean isSupported(String paramString, List<String> paramList)
  {
    int i;
    if (paramList == null)
      i = 0;
    while (true)
    {
      return i;
      if (paramList.indexOf(paramString) >= 0)
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  public static boolean supportFlashLight()
  {
    if (SupportedList.FlashMode != null);
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean supportScene()
  {
    if (SupportedList.SceneMode != null);
    for (int i = 1; ; i = 0)
      return i;
  }

  public void doSetCameraParameters()
  {
    if (this.mCameraParameters != null);
    try
    {
      this.mCamera.setParameters(this.mCameraParameters);
      return;
    }
    catch (Exception localException)
    {
      while (true)
        LOG.E("CameraController", "setParameters exception");
    }
  }

  public Camera.Size getPictureSizeParameter()
  {
    return this.mCameraParameters.getPictureSize();
  }

  public Camera.Size getPreviewSizeParameter()
  {
    return this.mCameraParameters.getPreviewSize();
  }

  public SettingInfo getSettingsInfo(String paramString)
  {
    SettingInfo localSettingInfo = new SettingInfo();
    if (paramString.equals("exposure-compensation"));
    while (true)
    {
      try
      {
        localSettingInfo.setMin(this.mCameraParameters.getMinExposureCompensation());
        localSettingInfo.setMax(this.mCameraParameters.getMaxExposureCompensation());
        localSettingInfo.setStep(this.mCameraParameters.getExposureCompensationStep());
        localSettingInfo.setDefault(0);
        localSettingInfo.setCurrent(this.mCameraParameters.getExposureCompensation());
        return localSettingInfo;
      }
      catch (Exception localException12)
      {
        LOG.E("CameraController", "exposure-compensation: set value failed !!", localException12);
        continue;
      }
      if (paramString.equals("saturation"))
        try
        {
          localSettingInfo.setMin(this.mCameraParameters.getInt("saturation-min"));
          localSettingInfo.setMax(this.mCameraParameters.getInt("saturation-max"));
          localSettingInfo.setStep(5.0F);
        }
        catch (Exception localException9)
        {
          try
          {
            localSettingInfo.setDefault(this.mCameraParameters.getInt("saturation-def"));
            try
            {
              localSettingInfo.setCurrent(this.mCameraParameters.getInt("saturation"));
            }
            catch (Exception localException11)
            {
              localSettingInfo.setCurrent(5);
              LOG.E("CameraController", "saturation: set current value failed !!", localException11);
            }
            continue;
            localException9 = localException9;
            localSettingInfo.setMin(0);
            localSettingInfo.setMax(25);
            LOG.E("CameraController", "saturation: set min, max value failed !!", localException9);
          }
          catch (Exception localException10)
          {
            while (true)
            {
              localSettingInfo.setDefault(5);
              LOG.E("CameraController", "saturation: set default value failed !!", localException10);
            }
          }
        }
      if (paramString.equals("contrast"))
        try
        {
          localSettingInfo.setMin(this.mCameraParameters.getInt("contrast-min"));
          localSettingInfo.setMax(this.mCameraParameters.getInt("contrast-max"));
          localSettingInfo.setStep(5.0F);
        }
        catch (Exception localException6)
        {
          try
          {
            localSettingInfo.setDefault(this.mCameraParameters.getInt("contrast-def"));
            try
            {
              localSettingInfo.setCurrent(this.mCameraParameters.getInt("contrast"));
            }
            catch (Exception localException8)
            {
              localSettingInfo.setCurrent(5);
              LOG.E("CameraController", "contrast: set current value failed !!", localException8);
            }
            continue;
            localException6 = localException6;
            localSettingInfo.setMin(0);
            localSettingInfo.setMax(25);
            LOG.E("CameraController", "contrast: set min, max value failed !!", localException6);
          }
          catch (Exception localException7)
          {
            while (true)
            {
              localSettingInfo.setDefault(5);
              LOG.E("CameraController", "contrast: set default value failed !!", localException7);
            }
          }
        }
      if (paramString.equals("sharpness"))
        try
        {
          localSettingInfo.setMin(this.mCameraParameters.getInt("sharpness-min"));
          localSettingInfo.setMax(this.mCameraParameters.getInt("sharpness-max"));
          localSettingInfo.setStep(5.0F);
        }
        catch (Exception localException3)
        {
          try
          {
            localSettingInfo.setDefault(this.mCameraParameters.getInt("sharpness-def"));
            try
            {
              localSettingInfo.setCurrent(this.mCameraParameters.getInt("sharpness"));
            }
            catch (Exception localException5)
            {
              localSettingInfo.setCurrent(15);
              LOG.E("CameraController", "sharpness: set current value failed !!", localException5);
            }
            continue;
            localException3 = localException3;
            localSettingInfo.setMin(0);
            localSettingInfo.setMax(25);
            LOG.E("CameraController", "sharpness: set min, max value failed !!", localException3);
          }
          catch (Exception localException4)
          {
            while (true)
            {
              localSettingInfo.setDefault(15);
              LOG.E("CameraController", "sharpness: set default value failed !!", localException4);
            }
          }
        }
      if (paramString.equals("taking-picture-zoom"))
        try
        {
          localSettingInfo.setMin(this.mCameraParameters.getInt("taking-picture-zoom-min"));
          localSettingInfo.setMax(this.mCameraParameters.getInt("taking-picture-zoom-max"));
          localSettingInfo.setStep(1.0F);
          localSettingInfo.setDefault(0);
          try
          {
            localSettingInfo.setCurrent(this.mCameraParameters.getInt("taking-picture-zoom"));
          }
          catch (Exception localException2)
          {
            localSettingInfo.setCurrent(0);
          }
        }
        catch (Exception localException1)
        {
          while (true)
          {
            localSettingInfo.setMin(0);
            localSettingInfo.setMax(30);
          }
        }
      localSettingInfo = null;
    }
  }

  public boolean injectGEParam()
  {
    if ((this.mCamera != null) && (this.mCameraParameters != null))
      LOG.V("CameraController", "injectGEParam");
    while (true)
    {
      try
      {
        this.mCamera.setParameters(this.mCameraParameters);
        i = 1;
        return i;
      }
      catch (Exception localException)
      {
        LOG.E("CameraController", "setParameters exception", localException);
        continue;
      }
      int i = 0;
    }
  }

  public int mapBarLevel2SettingValue(String paramString, int paramInt1, int paramInt2)
  {
    SettingInfo localSettingInfo = getSettingsInfo(paramString);
    int i = localSettingInfo.getDefault();
    int j = localSettingInfo.getMin();
    int k = localSettingInfo.getMax();
    int m = paramInt2 - 1;
    int n = (0 + m) / 2;
    int i1 = paramInt1;
    if (i1 > m)
      i1 = m;
    while (true)
    {
      return Math.round(Util.calcLagrange(0, j, n, i, m, k, i1));
      if (i1 >= 0)
        continue;
      int i2 = 0;
    }
  }

  public int mapSettingValue2BarLevel(String paramString, int paramInt1, int paramInt2)
  {
    SettingInfo localSettingInfo = getSettingsInfo(paramString);
    int i = localSettingInfo.getDefault();
    int j = localSettingInfo.getMin();
    int k = localSettingInfo.getMax();
    int m = paramInt1;
    if (m > k)
      m = k;
    while (true)
    {
      int n = paramInt2 - 1;
      int i1 = (0 + n) / 2;
      return Math.round(Util.calcLagrange(j, 0, i, i1, k, n, m));
      if (m >= j)
        continue;
      m = j;
    }
  }

  public void removeCameraParameter(String paramString)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    if (paramString != null)
      this.mCameraParameters.remove(paramString);
  }

  public void setAntibanding(String paramString)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    if (SupportedList.Antibanding == null)
      LOG.E("CameraController", "not support Antibanding !!");
    if ((paramString == null) || (!isSupported(paramString, SupportedList.Antibanding)))
      paramString = "auto";
    this.mCameraParameters.setAntibanding(paramString);
  }

  public void setCameraParameter(String paramString, int paramInt)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    if (paramString != null)
      this.mCameraParameters.set(paramString, paramInt);
  }

  public void setCameraParameter(String paramString1, String paramString2)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    if (paramString1 != null)
      this.mCameraParameters.set(paramString1, paramString2);
  }

  public void setColorEffect(String paramString)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    if (SupportedList.Effects == null)
      LOG.E("CameraController", "not support Effects !!");
    if ((paramString == null) || (!isSupported(paramString, SupportedList.Effects)))
      paramString = "none";
    this.mCameraParameters.setColorEffect(paramString);
  }

  public void setExposureCompensation(int paramInt)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    this.mCameraParameters.setExposureCompensation(paramInt);
  }

  public void setFlashMode(String paramString)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    if (SupportedList.FlashMode == null)
      LOG.E("CameraController", "not support FlashMode !!");
    if ((paramString == null) || (!isSupported(paramString, SupportedList.FlashMode)))
      paramString = "auto";
    if (paramString.equals("on"))
      paramString = "torch";
    this.mCameraParameters.setFlashMode(paramString);
  }

  public void setFlashModeIgor(String paramString)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    if (SupportedList.FlashMode == null)
      LOG.E("CameraController", "not support FlashMode !!");
    if ((paramString == null) || (!isSupported(paramString, SupportedList.FlashMode)))
      paramString = "auto";
    this.mCameraParameters.setFlashMode(paramString);
  }

  public boolean setGEParam(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    int i;
    if ((paramString != "GE-param0") && (paramString != "GE-param1"))
    {
      i = 0;
      return i;
    }
    if (paramString == "GE-param0")
      if (paramBoolean == true)
        this.mInjectParam0 = 1;
    while (true)
    {
      LOG.V("CameraController", "setGEParam ( " + paramString + " , " + paramInt1 + " , " + paramInt2 + " , " + paramInt3 + " , " + paramInt4 + " , " + paramBoolean + " )");
      this.mCameraParameters.set("GE-param3", this.mInjectParam0 + "," + this.mInjectParam1 + "," + paramInt3 + "," + paramInt4);
      this.mCameraParameters.set(paramString, paramInt1 + "," + paramInt2 + "," + paramInt3 + "," + paramInt4);
      i = 1;
      break;
      this.mInjectParam0 = 0;
      continue;
      if (paramString != "GE-param1")
        continue;
      if (paramBoolean == true)
      {
        this.mInjectParam1 = 1;
        continue;
      }
      this.mInjectParam1 = 0;
    }
  }

  public void setGpuEffectType(String paramString)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    this.mCameraParameters.set("GPU-effect", paramString);
  }

  public void setJpegQuality(int paramInt)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    this.mCameraParameters.setJpegQuality(paramInt);
  }

  public void setLocation(Location paramLocation)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    this.mCameraParameters.removeGpsData();
    int i;
    if (paramLocation != null)
    {
      double d1 = paramLocation.getLatitude();
      double d2 = paramLocation.getLongitude();
      if ((d1 != 0.0D) || (d2 != 0.0D))
      {
        i = 1;
        if (i == 0)
          break label172;
        this.mCameraParameters.setGpsLatitude(d1);
        this.mCameraParameters.setGpsLongitude(d2);
        this.mCameraParameters.setGpsProcessingMethod(paramLocation.getProvider().toUpperCase());
        if (!paramLocation.hasAltitude())
          break label159;
        this.mCameraParameters.setGpsAltitude(paramLocation.getAltitude());
        label114: if (paramLocation.getTime() != 0L)
        {
          long l = paramLocation.getTime() / 1000L;
          this.mCameraParameters.setGpsTimestamp(l);
        }
        LOG.V("CameraController", "add gps location on photo");
      }
    }
    while (true)
    {
      return;
      i = 0;
      break;
      label159: this.mCameraParameters.setGpsAltitude(0.0D);
      break label114;
      label172: LOG.V("CameraController", "not add gps location on photo - hasLatLon = false");
      continue;
      LOG.V("CameraController", "not add gps location on photo - loc = null");
    }
  }

  public void setPictureSizeParameter(int paramInt1, int paramInt2)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    this.mCameraParameters.setPictureSize(paramInt1, paramInt2);
  }

  public void setPreviewFrameRateParameter(int paramInt)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    this.mCameraParameters.setPreviewFrameRate(paramInt);
  }

  public void setPreviewSizeParameter(int paramInt1, int paramInt2)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    this.mCameraParameters.setPreviewSize(paramInt1, paramInt2);
  }

  public void setRotation(int paramInt)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    this.mCameraParameters.setRotation(paramInt);
  }

  public void setSceneMode(String paramString)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    if (SupportedList.SceneMode == null)
      LOG.E("CameraController", "not support SceneMode !!");
    if ((paramString == null) || (!isSupported(paramString, SupportedList.SceneMode)))
      paramString = "auto";
    this.mCameraParameters.setSceneMode(paramString);
  }

  public void setSupportedList()
  {
    if (this.mCameraParameters == null)
      LOG.E("CameraController", "setSupportedList() - mCameraParameters = null");
    SupportedList.FlashMode = this.mCameraParameters.getSupportedFlashModes();
    if (SupportedList.FlashMode == null)
      LOG.E("CameraController", "SupportedList.FlashMode == null");
    SupportedList.FocusModes = this.mCameraParameters.getSupportedFocusModes();
    if (SupportedList.FocusModes == null)
      LOG.E("CameraController", "SupportedList.FocusModes == null");
    SupportedList.WhiteBalance = this.mCameraParameters.getSupportedWhiteBalance();
    if (SupportedList.WhiteBalance == null)
      LOG.E("CameraController", "SupportedList.WhiteBalance == null");
    SupportedList.Effects = this.mCameraParameters.getSupportedColorEffects();
    if (SupportedList.Effects == null)
      LOG.E("CameraController", "SupportedList.Effects == null");
    SupportedList.Antibanding = this.mCameraParameters.getSupportedAntibanding();
    if (SupportedList.Antibanding == null)
      LOG.E("CameraController", "SupportedList.Antibanding == null");
    SupportedList.SceneMode = this.mCameraParameters.getSupportedSceneModes();
    if (SupportedList.SceneMode == null)
      LOG.E("CameraController", "SupportedList.SceneMode == null");
  }

  public void setWhiteBalance(String paramString)
  {
    if (this.mCameraParameters == null)
      this.mCameraParameters = this.mCamera.getParameters();
    if (SupportedList.WhiteBalance == null)
      LOG.E("CameraController", "not support WhiteBalance !!");
    if ((paramString == null) || (!isSupported(paramString, SupportedList.WhiteBalance)))
      paramString = "auto";
    this.mCameraParameters.setWhiteBalance(paramString);
  }

  public void showParameters()
  {
    LOG.W("CameraController", "params: " + this.mCameraParameters.flatten());
  }

  public void updateCameraParameters()
  {
    this.mCameraParameters = this.mCamera.getParameters();
  }

  public static class SupportedList
  {
    static List<String> Antibanding;
    static List<String> Effects;
    static List<String> FlashMode = null;
    static List<String> FocusModes = null;
    static List<String> SceneMode;
    static List<String> WhiteBalance = null;

    static
    {
      Effects = null;
      Antibanding = null;
      SceneMode = null;
    }
  }

  public static class Settings
  {
    public static final String FALSE = "false";
    public static final String OFF = "off";
    public static final String ON = "on";
    public static final String TRUE = "true";

    public static class MeterSettings
    {
      public static final String CAMERA_METER_AVERAGE = "meter-average";
      public static final String CAMERA_METER_CENTER = "meter-center";
      public static final String CAMERA_METER_SPOT = "meter-spot";
    }

    public static class FrontCamMode
    {
      public static final String MIRROR = "mirror";
      public static final String REVERSE = "reverse";
    }

    public static class CamMode
    {
      public static final int PHOTO = 0;
      public static final int VIDEO = 1;
      public static final int VIDEO_FAST_FPS = 2;
    }

    public static class DisableValue
    {
      public static final String FLASHMODE = "off";
    }

    public static class DefaultValue
    {
      public static final String ANTIBANDING = "auto";
      public static final String EFFECT = "none";
      public static final String FLASHMODE = "auto";
      public static final String ISO = "auto";
      public static final String SCENEMODE = "auto";
      public static final String WHITEBALANCE = "auto";
    }

    public static class Keys
    {
      public static final String BLINK_SMILE_DETECT = "ola-sbd-options";
      public static final String BRIGHTNESS = "exposure-compensation";
      public static final String CAM_MODE = "cam-mode";
      public static final String CONTINUE_AUTO_FOCUS = "enable-caf";
      public static final String CONTRAST = "contrast";
      public static final String DENOISE = "postproc-enable-denoise";
      public static final String FILE_FORMAT_3D = "3d-file-format";
      public static final String FRONT_CAMERA_MODE = "front-camera-mode";
      public static final String IMBOOST = "postproc-enable-imboost";
      public static final String ISO = "iso";
      public static final String METERMODE = "meter-mode";
      public static final String PHOTO_TIMESTAMP = "img-timestamp";
      public static final String PHOTO_TIMESTAMP_TEXT = "timestamp-text";
      public static final String PREVIEW_ISO = "preview-iso";
      public static final String SATURATION = "saturation";
      public static final String SHARPNESS = "sharpness";
      public static final String SHUTTER_SOUND_OFF = "sound-off";
      public static final String TOUCH_AEC = "touch-aec";
      public static final String TOUCH_FOCUS = "touch-focus";
      public static final String ZOOM = "taking-picture-zoom";
    }
  }

  public static class SettingInfo
  {
    private int current_value = 0;
    private int default_value = 0;
    private int max_value = 0;
    private int min_value = 0;
    private float step = 0.0F;

    public int getCurrent()
    {
      return this.current_value;
    }

    public int getDefault()
    {
      return this.default_value;
    }

    public int getMax()
    {
      return this.max_value;
    }

    public int getMin()
    {
      return this.min_value;
    }

    public float getStep()
    {
      return this.step;
    }

    public void setCurrent(int paramInt)
    {
      this.current_value = paramInt;
    }

    public void setDefault(int paramInt)
    {
      this.default_value = paramInt;
    }

    public void setMax(int paramInt)
    {
      this.max_value = paramInt;
    }

    public void setMin(int paramInt)
    {
      this.min_value = paramInt;
    }

    public void setStep(float paramFloat)
    {
      this.step = paramFloat;
    }

    public String toString()
    {
      String str1 = "min: " + this.min_value;
      String str2 = str1 + ", max: " + this.max_value;
      String str3 = str2 + ", step: " + this.step;
      String str4 = str3 + ", default: " + this.default_value;
      return str4 + ", current: " + this.current_value;
    }
  }
}

/* Location:           /Volumes/android/github-aosp_source/android_packages_apps_Camera3d/dex2jar.jar
 * Qualified Name:     com.android.amaze_camera.CameraController
 * JD-Core Version:    0.6.0
 */