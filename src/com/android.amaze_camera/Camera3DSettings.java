package com.android.amaze_camera;

import android.content.ContentResolver;
import android.os.IDisplayService;
import android.os.IDisplayService.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;

public class Camera3DSettings
{
  public static final int DISPLAY_2D_BARRIER_MODE = 2;
  public static final int DISPLAY_3D_BARRIER_LANDSCAPE = 0;
  public static final int DISPLAY_3D_BARRIER_PORTRAIT = 1;
  public static final int HW_3D_SWITCH_BUTTON_2D_MODE = 1;
  public static final int HW_3D_SWITCH_BUTTON_3D_MODE = 0;
  public static final int HW_3D_SWITCH_CHECK_INTERVAL = 300;
  public static final int HW_3D_SWITCH_DELAY_TIME = 2000;
  private static final String TAG = "Camera3DSettings";

  public static int get3DInitialStatus(ContentResolver paramContentResolver)
  {
    int i = 1;
    try
    {
      int j = Settings.System.getInt(paramContentResolver, "htc_2d_3d_mode");
      i = j;
      return i;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException)
    {
      while (true)
      {
        LOG.E("Camera3DSettings", "Fail to get 2D/3D HW button status");
        localSettingNotFoundException.printStackTrace();
      }
    }
  }

  public static void set3DBarrier(boolean paramBoolean)
  {
    IDisplayService localIDisplayService = IDisplayService.Stub.asInterface(ServiceManager.getService("display"));
    if (paramBoolean);
    while (true)
    {
      try
      {
        localIDisplayService.set3DMode(0, "");
        return;
      }
      catch (RemoteException localRemoteException2)
      {
        LOG.E("Camera3DSettings", "Fail to switch 3D barrier");
        localRemoteException2.printStackTrace();
        continue;
      }
      try
      {
        localIDisplayService.set3DMode(2, "");
      }
      catch (RemoteException localRemoteException1)
      {
        LOG.E("Camera3DSettings", "Fail to switch 2D barrier");
        localRemoteException1.printStackTrace();
      }
    }
  }
}

/* Location:           /Users/TwistedZero/android-utility/working-folder/mod-here-multi/dex2jar.jar
 * Qualified Name:     com.android.amaze_camera.Camera3DSettings
 * JD-Core Version:    0.6.0
 */