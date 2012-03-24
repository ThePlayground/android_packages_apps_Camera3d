package com.android.amaze_camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.HtcCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Location;
import android.media.AudioManager;
import android.media.CameraProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.preference.PreferenceManager;
import com.android.amaze_camera.component.AutoSceneController;
import com.android.amaze_camera.component.BurstController;
import com.android.amaze_camera.component.Component;
import com.android.amaze_camera.component.ComponentManager;
import com.android.amaze_camera.component.DOTIndicatorController;
import com.android.amaze_camera.component.HandShakeController;
import com.android.amaze_camera.component.HappyShotController;
import com.android.amaze_camera.component.HdrController;
import com.android.amaze_camera.component.IComponentOwner;
import com.android.amaze_camera.component.PanoramaController;
import com.android.amaze_camera.component.PowerWarningController;
import com.android.amaze_camera.component.ReviewAnimationController;
import com.android.amaze_camera.component.ThumbnailController;
import com.android.amaze_camera.component.ZoomBarController;
import com.scalado.base.Image.Config;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

public class CameraThread extends Thread
  implements IComponentOwner, IEventManagerOwner
{
  public static final int CALCULATE_FPS = 25;
  public static final int CAMERA_MODE = 0;
  public static final int CAMERA_TYPE_FRONT = 2;
  public static final int CAMERA_TYPE_MAIN = 1;
  public static final int CAMERA_TYPE_MAIN_3D = 3;
  public static final int CAMERA_TYPE_UNKNOWN = 0;
  public static final int CANCEL_FOCUS = 6;
  public static final int CANNOT_STAT_ERROR = 2;
  private static final int CAPTURE_STATE_ERROR = 4;
  private static final int CAPTURE_STATE_IDLE = 0;
  private static final int CAPTURE_STATE_TAKING_PICTURE = 1;
  private static final int CAPTURE_STATE_WAITING_CLOSE_CAMERA = 2;
  private static final int CAPTURE_STATE_WAITING_QUIT_THREAD = 3;
  private static final int CHANGE_ZOOM = 70;
  public static final int CHECK_INTERNAL_STORAGE = 52;
  public static final int CHECK_RECORD_SIZE_LIMIT = 22;
  public static final int CHECK_STORAGE_STATUS = 53;
  public static final int CLOSE_CAMERA = 2;
  public static final int CLOSE_CAMERA_DELAYED = 24;
  private static final int CLOSE_CAMERA_DELAYED_TIME = 2000;
  public static final int CREATE_IMAGE_THUMB = 29;
  public static final int CREATE_THUMB = 57;
  public static final int CREATE_VIDEO_THUMB = 30;
  public static final int DISABLE_TOUCH_AEC = 31;
  public static final int ENABLE_CAF = 46;
  public static final int ENTER_VIDEO_MODE = 11;
  public static final String EVENT_AUTOSCENE_ENABLED = "AutoScene.Enabled";
  public static final String EVENT_AUTOSMILECAPTURE = "HTCCallback.AutoSmileCapture";
  public static final String EVENT_BLINKONOFF_CHANGED = "HTCCallback.BlinkOffChanged";
  public static final String EVENT_BLINK_CHANGED = "HTCCallback.BlinkChanged";
  public static final String EVENT_FOCUS_FINISHED = "Focus.Finished";
  public static final String EVENT_LOWLIGHT_CHANGED = "HTCCallback.LowLightChanged";
  public static final String EVENT_MACROFOCUS_CHANGED = "HTCCallback.MarcoFocusChanged";
  public static final String EVENT_MEDIA_DELETION_COMPLETED = "Media.DeletionCompleted";
  public static final String EVENT_MEDIA_SAVED = "Media.Saved";
  public static final String EVENT_MEDIA_SAVE_FAILED = "Media.SaveFailed";
  public static final String EVENT_POSTVIEW_IMAGE_CREATE = "ReviewAnimation.PostviewCreated";
  public static final String EVENT_POWER_REC_WARNING_RECEIVED = "PowerRecWarning.Received";
  public static final String EVENT_POWER_WARNING_RECEIVED = "PowerWarning.Received";
  public static final String EVENT_PREVIEW_STARTED = "Preview.Started";
  public static final String EVENT_PREVIEW_STARTING = "Preview.Starting";
  public static final String EVENT_PREVIEW_STOPPED = "Preview.Stopped";
  public static final String EVENT_PREVIEW_STOPPING = "Preview.Stopping";
  public static final String EVENT_RECORDING_STARTED = "Recording_Started";
  public static final String EVENT_REQUEST_CLOSE_CAMERA = "Request.CloseCamera";
  public static final String EVENT_REQUEST_DELETE_MEDIA = "Request.DeleteLatestMedia";
  public static final String EVENT_SMILE_CHANGED = "HTCCallback.SmileChanged";
  public static final String EVENT_TAKE_PICTURE_ENDED = "Capture.Ended";
  public static final String EVENT_ZOOM_CHANGED = "Zoom.Changed";
  public static final String EVENT_ZOOM_RANGE_RETRIEVED = "Zoom.RangeRetrieved";
  public static final int EXIT_VIDEO_MODE = 12;
  public static final int LOAD_SETTINGS = 28;
  public static final int NO_STORAGE_ERROR = 1;
  public static final int POST_PROCESSING = 55;
  public static final int QUIT_CAMERA_THREAD = 8;
  public static final int RECORDING_FINISH_AUTO_FOCUS = 45;
  public static final int RECORDING_MESSAGE = 10;
  public static final int RESET_ZOOM_VALUE = 48;
  public static final int SEND_POWER_WARNING_MSG = 59;
  public static final int SETTINGS_RELOAD = 1;
  public static final int SETTINGS_REMAIN = 0;
  public static final int SET_BLINK_DETECTION = 60;
  public static final int SET_BRIGHTNESS_VALUE = 17;
  public static final int SET_CUSTOM_EFFECT_PROP = 44;
  public static final int SET_EFFECT = 19;
  public static final int SET_FACE_OPTIONS = 32;
  public static final int SET_FLASH_MODE = 21;
  public static final int SET_GE_CONTRAST = 50;
  public static final int SET_GE_DEPTH = 36;
  public static final int SET_GE_DISTORTION = 37;
  public static final int SET_GE_DOTS = 49;
  public static final int SET_GE_NONE = 40;
  public static final int SET_GE_PARAMETER_FROM_CONTROL_BAR = 42;
  public static final int SET_GE_PARAMETER_FROM_CONTROL_CIRCLE = 41;
  public static final int SET_GE_SEPIA = 39;
  public static final int SET_GE_TS = 51;
  public static final int SET_GE_VIGNETTE = 38;
  public static final int SET_GE_VINTAGE_GREEN = 33;
  public static final int SET_GE_VINTAGE_HIGH = 34;
  public static final int SET_GE_VINTAGE_RED = 35;
  public static final int SET_IMAGE_PROPERTY = 20;
  public static final int SET_ISO = 18;
  public static final int SET_OLA_ORIENTATION_PARAMETER = 43;
  public static final int SET_RESOLUTION = 15;
  public static final int SET_SCENE_MODE = 58;
  public static final int SET_SMILE_CAPTURE = 61;
  public static final int SET_WHITE_BALANCE = 16;
  public static final int START_FOCUS = 5;
  public static final int START_PREVIEW = 0;
  public static final int START_RECORDING = 9;
  public static final int START_RECORDING_DELAY = 54;
  private static final int START_RECORDING_DELAY_TIME = 300;
  public static final int STEREO_RECORDING = 56;
  public static final int STOP_PREVIEW = 1;
  public static final int STOP_VIDEO_RECORDING_DELAY = 47;
  public static final int STORAGE_FULL = 3;
  public static final int STORAGE_OK = 0;
  public static int Storage_Status = 0;
  private static final String TAG = "CameraThread";
  public static final int TAKE_FOCUS = 4;
  public static final int TAKE_PICTURE = 7;
  public static final int TAKE_PREVIEW = 26;
  public static final String THUMB_FILE_PATH = "thumb_file_path";
  public static final int VIDEO_MODE = 1;
  public static final int WAIT_SELFTIMER = 3;
  public static final int ZOOM_UNKNOWN = -2147483648;
  public static boolean mEnableCAF;
  public static boolean mEnableTouchAEC;
  public static boolean mIsLastCameraClosed = true;
  private static Uri mLastContentUri;
  public static boolean mTakeFocus;
  private int Camera_Mode_Height;
  private int Camera_Mode_Width;
  private int Camera_Preview_Height;
  private int Camera_Preview_Width;
  private final int HTC_CALLBACK_AUTOSMILECAPTURE = 1;
  private final int HTC_CALLBACK_BLINKONOFF_CHANGED = 3;
  private final int HTC_CALLBACK_BLINK_CHANGED = 2;
  private final int HTC_CALLBACK_LOWLIGHT_CHANGED = 4;
  private final int HTC_CALLBACK_MACROFOCUS_CHANGED = 5;
  private final int HTC_CALLBACK_SMILE_CHANGED = 0;
  private final long MINIMUN_CAMERA_REMAIN_SPACE = 1048576L;
  private int Video_Mode_Height;
  private int Video_Mode_Width;
  private boolean bIsStereo = false;
  private boolean bShowFocusIcon = false;
  private boolean bShutterSound = false;
  private boolean bSwitchCamera = false;
  public int m3DButtonStatus = 1;
  private String m3DFileFormat = null;
  public int m3DPreviewStatus = 1;
  private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback(null);
  private CameraController mCamController = null;
  private HTCCamera mCameraActivity = null;
  private Camera mCameraDevice = null;
  MainHandler mCameraHandler = null;
  private boolean mCanStartPreview = true;
  private ICaptureHandler mCaptureHandler;
  private int mCaptureRotation = 0;
  private int mCaptureState = 0;
  private Thread mCheckingThread = null;
  private final CommonCaptureHandler mCommonCaptureHandler = new CommonCaptureHandler();
  private ComponentManager mComponentManager;
  private String mCurrentColorEffect = null;
  private Resolution mCurrentResolution = null;
  private int mEncoderType;
  private ErrorCallback mErrorCallback = new ErrorCallback(null);
  private ErrorListener mErrorListener = new ErrorListener(null);
  private int mFps = 0;
  private HtcCallback mHtcCallback = new HtcCallback(null);
  private InfoListener mInfoListener = new InfoListener(null);
  private boolean mIsCaptureInterrupted;
  private boolean mIsOnlySetResolution = false;
  private boolean mIsStart_fps = false;
  private int mJPEGQuality = 100;
  private String mJPEGQualityKey;
  private byte[] mJpegData = null;
  private Location mLocation;
  public int mMode = 0;
  private int mNumber = 0;
  public Bitmap mOneShotBitmap = null;
  private OneShotPreviewCallback mOneShotPreviewCallback = new OneShotPreviewCallback(null);
  private PreviewCallback mPreviewCallback = new PreviewCallback(null);
  private byte[] mPreviewData = null;
  private boolean mPreviewing = false;
  public boolean mRecPowerWarning = false;
  private MediaRecorder mRecorder = null;
  private boolean mRecording = false;
  private long mStartRecordingTime = 0L;
  public Object mSyncObject = new Object();
  private volatile boolean mTaking_picture = false;
  Handler mUIHandler = null;
  private String mVideoPath = null;
  private EventManager m_EventManager;
  private volatile int m_MaxZoom = -2147483648;
  private volatile int m_MinZoom = -2147483648;
  private boolean mbCanTakePicture = false;
  private boolean mbNoneEffect = true;
  private String mfilename = null;
  private String mfilepath = null;

  static
  {
    mEnableTouchAEC = false;
    mEnableCAF = false;
    mTakeFocus = false;
    mLastContentUri = null;
    Storage_Status = 0;
  }

  public CameraThread(Activity paramActivity, Handler paramHandler)
  {
    this.mCameraActivity = ((HTCCamera)paramActivity);
    this.mUIHandler = paramHandler;
  }

  private boolean NeedToApplyAMR_NB()
  {
    int i;
    if (this.mMode == 0)
      i = 0;
    while (true)
    {
      return i;
      Resolution localResolution = MovieModeHandler.getMovieModeHandler().getCurrentResolutionSettingValue(this.mCameraActivity);
      if (((this.mCameraActivity.isRequestName(IntentManager.RequestName.Mms) == true) || (localResolution.equals(Resolution.Video_QCIF))) && (DisplayDevice.CUSTOM_MMS == DisplayDevice.CustomMMS.Verizon))
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  private void capture()
  {
    LOG.V("CameraThread", "start to capture photo");
    this.mPreviewing = false;
    this.mIsCaptureInterrupted = false;
    if (this.mCamController == null)
    {
      LOG.V("CameraThread", "take picture , mCamController is null, return");
      this.mCaptureState = 4;
      return;
    }
    CameraController localCameraController = this.mCamController;
    String str = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_flash_mode");
    localCameraController.setFlashModeIgor(str);
    localCameraController.setJpegQuality(this.mJPEGQuality);
    int i = this.mCameraActivity.getFreezeOrientation();
    int j;
    if (i == -1)
    {
      LOG.W("CameraThread", "orientation = OrientationEventListener.ORIENTATION_UNKNOWN");
      j = 0;
      label96: if ((is3DMode()) && (!this.bSwitchCamera))
      {
        LOG.V("CameraThread", "set rotation landscape while 3D photo mode");
        j = 0;
      }
      this.mCaptureRotation = ImageManager.roundOrientation(j);
      LOG.W("CameraThread", "set rotation, mCaptureRotation = " + this.mCaptureRotation);
      this.mCamController.setRotation(this.mCaptureRotation);
      this.mLocation = LocationHandler.getLocation();
      this.mCamController.setLocation(this.mLocation);
      if (!isAddTimeStamp())
        break label370;
      this.mCamController.setCameraParameter(str, "1");
      LOG.V("CameraThread", "add time stamp on photo");
    }
    while (true)
    {
      while (true)
      {
        this.mCamController.doSetCameraParameters();
        LOG.V("CameraThread", "Camera - takePicture");
        TIME.ReadyTakePicture.End();
        Object localObject = this.mCaptureHandler;
        if (localObject == null)
        {
          LOG.V("CameraThread", "No capture handler is specified, use default handler");
          localObject = this.mCommonCaptureHandler;
        }
        LOG.V("CameraThread", "Before capturing, capture handler is " + localObject.getClass().getName());
        this.mCanStartPreview = false;
        try
        {
          ((ICaptureHandler)localObject).takePicture(this, this.mCameraDevice);
          this.mCameraActivity.prepareActionScreen();
        }
        catch (Exception localException)
        {
          this.mCaptureState = 4;
          LOG.V("CameraThread", "take picture exception - mCaptureState = CAPTURE_STATE_ERROR");
          LOG.E("CameraThread", "take picture exception. Camera app finished", localException);
          this.mCameraActivity.finish();
        }
      }
      break;
      j = i + 90;
      break label96;
      label370: this.mCamController.setCameraParameter(str, "0");
      LOG.V("CameraThread", "not add time stamp on photo");
    }
  }

  private void checkCanTakePicture()
  {
    this.mbCanTakePicture = true;
    this.mCheckingThread = new Thread(new Runnable()
    {
      public void run()
      {
        if (CameraThread.Storage_Status != 0)
        {
          LOG.E("CameraThread", "storage error");
          CameraThread.this.showStorageToast(true);
          if (CameraThread.Storage_Status != 0)
            CameraThread.access$2202(CameraThread.this, false);
        }
        while (true)
        {
          if ((DisplayDevice.contactsNoStorage() == true) && (CameraThread.this.mCameraActivity.isRequestName(IntentManager.RequestName.Contacts) == true))
            CameraThread.access$2202(CameraThread.this, true);
          return;
          CameraThread.access$2202(CameraThread.this, true);
          continue;
          if (CameraThread.this.calculatePicturesRemaining() < 1L)
          {
            CameraThread.Storage_Status = 3;
            CameraThread.this.showStorageToast(true);
            if (CameraThread.Storage_Status != 0)
            {
              CameraThread.access$2202(CameraThread.this, false);
              continue;
            }
            CameraThread.access$2202(CameraThread.this, true);
            continue;
          }
          CameraThread.access$2202(CameraThread.this, true);
        }
      }
    });
    this.mCheckingThread.start();
  }

  private void checkCanTakeRecorder()
  {
    this.mbCanTakePicture = true;
    this.mCheckingThread = new Thread(new Runnable()
    {
      public void run()
      {
        if (CameraThread.Storage_Status != 0)
        {
          LOG.E("CameraThread", "storage error");
          CameraThread.this.showStorageToast(true);
          if (CameraThread.Storage_Status != 0)
            CameraThread.access$2202(CameraThread.this, false);
        }
        while (true)
        {
          return;
          CameraThread.access$2202(CameraThread.this, true);
          continue;
          CameraThread.this.mCameraActivity.getRecordLimitCheck().setByteRate(CameraThread.this.mCameraActivity);
          CameraThread.this.mCameraActivity.getRecordLimitCheck().setRecordingLength(CameraThread.this.mCameraActivity);
          if (CameraThread.this.mCameraActivity.getRecordLimitCheck().remainTime(true) < 1L)
          {
            CameraThread.Storage_Status = 3;
            CameraThread.this.showStorageToast(true);
            if (CameraThread.Storage_Status != 0)
            {
              CameraThread.access$2202(CameraThread.this, false);
              continue;
            }
            CameraThread.access$2202(CameraThread.this, true);
            continue;
          }
          CameraThread.access$2202(CameraThread.this, true);
        }
      }
    });
    this.mCheckingThread.start();
  }

  private String checkSavedFileName(String paramString1, String paramString2, String paramString3)
  {
    File localFile = new File(paramString1 + "/" + paramString2);
    if ((localFile != null) && (localFile.exists()))
    {
      int i = paramString2.lastIndexOf(".");
      if (i >= 0)
        paramString2 = paramString2.substring(0, i);
      paramString2 = paramString2 + "-" + new Long(System.currentTimeMillis()).toString() + paramString3;
    }
    return paramString2;
  }

  private void checkStorageStatus()
  {
    hasStorageTest();
    if ((Storage_Status == 1) || (Storage_Status == 2))
    {
      MessageHandler.removeMessages(this.mUIHandler, 39);
      if (this.mCameraActivity.isActionScreenOpen())
      {
        LOG.W("CameraThread", "Storage Error!! exit action screen");
        MessageHandler.sendObtainMessage(this.mUIHandler, 39, 0, 0, "false");
      }
    }
    showStorageToast(false);
    if ((Storage_Status != 0) && ((!DisplayDevice.contactsNoStorage()) || (!this.mCameraActivity.isRequestName(IntentManager.RequestName.Contacts))))
    {
      LOG.W("CameraThread", "Storage Error!! close self-timer");
      MessageHandler.removeMessages(this.mCameraHandler, 7);
      MessageHandler.sendEmptyMessage(this.mUIHandler, 70);
    }
  }

  private int getJPEGQualityPreference()
  {
    String str = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_capture_quality");
    int i = 2;
    this.mJPEGQualityKey = "_super";
    if ((str == null) || (str.equals("_super")))
    {
      i = 2;
      this.mJPEGQualityKey = "_super";
    }
    while (true)
    {
      return CameraProfile.getJpegEncodingQualityParameter(i);
      if (str.equals("_fine"))
      {
        i = 1;
        this.mJPEGQualityKey = "_fine";
        continue;
      }
      if (!str.equals("_normal"))
        continue;
      i = 0;
      this.mJPEGQualityKey = "_normal";
    }
  }

  public static Uri getLastContentUri()
  {
    return mLastContentUri;
  }

  private DCFRuler.PathPackage getSavedFile(DCFInfo paramDCFInfo)
    throws IOException, DCFRuler.OverMaxDCFDirectoryNumberException
  {
    String str = DCFRuler.StorageCardControl.getCurStorageDirectory().toString();
    DCFRuler.PathPackage localPathPackage;
    if ((is3DMode()) && (!this.bSwitchCamera))
      if (this.m3DFileFormat == null)
      {
        localPathPackage = DCFRuler.getNextFilePath(this.mCameraActivity, str, DCFRuler.fileFormat.JPG, paramDCFInfo);
        localPathPackage.mFileName = checkSavedFileName(localPathPackage.mDirectoryName, localPathPackage.mFileName, ".jpg");
        LOG.V("CameraThread", "m3DFileFormat == null");
        LOG.V("CameraThread", "FOLDER = " + localPathPackage.mDirectoryName + ", NAME = " + localPathPackage.mFileName);
      }
    while (true)
    {
      return localPathPackage;
      if (this.m3DFileFormat.equals("jps"))
      {
        localPathPackage = DCFRuler.getNextFilePath(this.mCameraActivity, str, DCFRuler.fileFormat.JPS, paramDCFInfo);
        localPathPackage.mFileName = checkSavedFileName(localPathPackage.mDirectoryName, localPathPackage.mFileName, ".jps");
        break;
      }
      localPathPackage = DCFRuler.getNextFilePath(this.mCameraActivity, str, DCFRuler.fileFormat.MPO, paramDCFInfo);
      localPathPackage.mFileName = checkSavedFileName(localPathPackage.mDirectoryName, localPathPackage.mFileName, ".mpo");
      break;
      localPathPackage = DCFRuler.getNextFilePath(this.mCameraActivity, str, DCFRuler.fileFormat.JPG, paramDCFInfo);
      localPathPackage.mFileName = checkSavedFileName(localPathPackage.mDirectoryName, localPathPackage.mFileName, ".jpg");
      LOG.V("CameraThread", "FOLDER = " + localPathPackage.mDirectoryName + ", NAME = " + localPathPackage.mFileName);
    }
  }

  private int getVideoEncoder()
  {
    int i;
    String str;
    if ((is3DMode()) && (!this.bSwitchCamera))
    {
      i = 2;
    }
    else
    {
      str = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_capture_format_video");
      if (str != null)
        break label38;
      i = 3;
    }
    while (true)
    {
      return i;
      label38: if (str.equalsIgnoreCase("h263"))
      {
        i = 1;
        continue;
      }
      if (str.equalsIgnoreCase("h264"))
      {
        i = 2;
        continue;
      }
      i = 3;
    }
  }

  private void handleAutoFocus(boolean paramBoolean)
  {
    LOG.V("CameraThread", "handleAutoFocus() start");
    MessageHandler.removeMessages(this.mCameraHandler, 45);
    if (this.mCameraActivity.isCancelFocus() == true)
    {
      LOG.V("CameraThread", "handleAutoFocus() end - cancel focus");
      return;
    }
    if (DisplayDevice.hasAutoFocus())
    {
      if (!paramBoolean)
        break label205;
      LOG.V("CameraThread", " Now We have Focus");
      if ((!this.mCameraActivity.isAutoCaptureTask()) && (this.bShutterSound) && (!this.mRecording) && ((HTCCamera.mFocusMode == 1) || (DisplayDevice.forceFocusSound()) || (HTCCamera.bFocusFromPress)))
      {
        LOG.V("CameraThread", "Play Focused Sound - successful");
        MessageHandler.sendObtainMessage(this.mUIHandler, 21, 2131099650, 0, null);
      }
      if (this.bShowFocusIcon)
        MessageHandler.sendEmptyMessage(this.mUIHandler, 25);
    }
    while (true)
    {
      this.m_EventManager.raiseEvent(new OneValueEvent("Focus.Finished", Integer.valueOf(HTCCamera.mFocusMode)));
      if (this.bShowFocusIcon)
        MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 27, 200L);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 28, 200L);
      LOG.V("CameraThread", "handleAutoFocus() end");
      break;
      label205: LOG.V("CameraThread", " Now We don't have Focus");
      if (!this.bShowFocusIcon)
        continue;
      MessageHandler.sendEmptyMessage(this.mUIHandler, 26);
    }
  }

  private void hasStorageTest()
  {
    Storage_Status = 0;
    try
    {
      if (!ImageManager.hasStorage())
        Storage_Status = 1;
      if (!ImageManager.hasStorage(true, DCFRuler.getReleativeVideoPath()))
        Storage_Status = 1;
      return;
    }
    catch (Exception localException)
    {
      while (true)
      {
        LOG.E("CameraThread", "cannot know storage state", localException);
        Storage_Status = 2;
      }
    }
  }

  private boolean isAddTimeStamp()
  {
    boolean bool;
    if (this.mMode == 1)
      bool = false;
    while (true)
    {
      return bool;
      if ((!DisplayDevice.captrueFullSize()) && ((this.mCameraActivity.isRequestName(IntentManager.RequestName.Contacts)) || (this.mCameraActivity.isRequestName(IntentManager.RequestName.Square))))
      {
        bool = false;
        continue;
      }
      bool = HTCCameraAdvanceSetting.getPrefenceBoolean(this.mCameraActivity, "pref_camera_timestamp").booleanValue();
    }
  }

  private boolean isMMSRecording()
  {
    int i;
    if (this.mMode == 0)
      i = 0;
    while (true)
    {
      return i;
      Resolution localResolution = MovieModeHandler.getMovieModeHandler().getCurrentResolutionSettingValue(this.mCameraActivity);
      if ((this.mCameraActivity.isRequestName(IntentManager.RequestName.Mms) == true) || (localResolution.equals(Resolution.Video_QCIF)))
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  private void loadResolution()
  {
    LOG.V("CameraThread", "loadResolution() - start");
    PhotoModeHandler localPhotoModeHandler;
    Resolution localResolution;
    if (this.mMode == 0)
    {
      localPhotoModeHandler = PhotoModeHandler.getPhotoModeHandler();
      if ((!DisplayDevice.captrueFullSize()) && ((this.mCameraActivity.isRequestName(IntentManager.RequestName.Contacts)) || (this.mCameraActivity.isRequestName(IntentManager.RequestName.Square))))
      {
        localResolution = localPhotoModeHandler.getCurrentResolutionSettingValue(this.mCameraActivity);
        this.Camera_Mode_Width = localResolution.getWidth();
        this.Camera_Mode_Height = localResolution.getHeight();
        this.Camera_Preview_Width = DisplayDevice.CAMERA_PREVIEW_SIZE_FOR_CONTACT;
        this.Camera_Preview_Height = DisplayDevice.CAMERA_PREVIEW_SIZE_FOR_CONTACT;
      }
    }
    while (true)
    {
      LOG.V("CameraThread", "mEncoderType = " + this.mEncoderType);
      this.mCurrentResolution = localResolution;
      LOG.V("CameraThread", "loadResolution() - end");
      return;
      boolean bool;
      if ((!DisplayDevice.supportWideScreen2ndCamera()) && (this.bSwitchCamera == true))
      {
        bool = false;
        label151: localResolution = updateImageRatio(bool, localPhotoModeHandler);
        this.Camera_Mode_Width = localResolution.getWidth();
        this.Camera_Mode_Height = localResolution.getHeight();
        if (!bool)
          break label270;
        if ((!DisplayDevice.supportWideScreen2ndCamera()) || (!this.bSwitchCamera))
          break label253;
        this.Camera_Preview_Width = DisplayDevice.DEFALULT_CAMERA_PREVIEW_WITDH_FOR_3X_2ND;
        this.Camera_Preview_Height = DisplayDevice.DEFALULT_CAMERA_PREVIEW_HEIGHT_FOR_3X_2ND;
      }
      while (true)
      {
        if ((!is3DMode()) || (this.bSwitchCamera))
          break label285;
        this.Camera_Preview_Width = DisplayDevice.DEFAULT_3D_CAMERA_PREVIEW_WIDTH;
        this.Camera_Preview_Height = DisplayDevice.DEFAULT_3D_CAMERA_PREVIEW_HEIGHT;
        break;
        bool = HTCCameraAdvanceSetting.getPrefenceBoolean(this.mCameraActivity, "pref_camera_image_ratio").booleanValue();
        break label151;
        label253: this.Camera_Preview_Width = DisplayDevice.DEFALULT_CAMERA_PREVIEW_WITDH_FOR_3X;
        this.Camera_Preview_Height = DisplayDevice.DEFALULT_CAMERA_PREVIEW_HEIGHT_FOR_3X;
        continue;
        label270: this.Camera_Preview_Width = DisplayDevice.CAMERA_PREVIEW_WIDTH_FOR_4x3;
        this.Camera_Preview_Height = DisplayDevice.CAMERA_PREVIEW_HEIGHT_FOR_4x3;
      }
      label285: continue;
      localResolution = MovieModeHandler.getMovieModeHandler().getCurrentResolutionSettingValue(this.mCameraActivity);
      this.Video_Mode_Width = localResolution.getWidth();
      this.Video_Mode_Height = localResolution.getHeight();
      if (((localResolution.equals(Resolution.Video_QCIF)) || (localResolution.equals(Resolution.Video_QCIF_Service))) && (DisplayDevice.CUSTOM_MMS != DisplayDevice.CustomMMS.Verizon))
      {
        this.mEncoderType = 1;
        continue;
      }
      if ((localResolution.equals(Resolution.Video_720p)) || (localResolution.equals(Resolution.Video_720p_Online)))
      {
        if (DisplayDevice.supportQCT8x50())
        {
          this.mEncoderType = 3;
          continue;
        }
        this.mEncoderType = 2;
        continue;
      }
      if (DisplayDevice.notSupportH264())
      {
        this.mEncoderType = 3;
        continue;
      }
      this.mEncoderType = 2;
    }
  }

  private void loadSettings()
  {
    LOG.V("CameraThread", "Start to Load Settings to Set Camera ");
    DCFRuler.StorageCardControl.initStorageType(this.mCameraActivity);
    int i = this.mCameraActivity.getAudioManager().getStreamVolume(4);
    if ((!DisplayDevice.forceSutterSound()) || (i != 0))
      this.mCamController.setCameraParameter("sound-off", "true");
    String str2;
    String str3;
    label186: label202: label339: String str5;
    label283: label373: boolean bool2;
    if ((DisplayDevice.supportSpecific2ndCamera()) && (this.bSwitchCamera))
    {
      str2 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_white_balance_2nd");
      this.mCamController.setWhiteBalance(str2);
      setParameterFromBarLevel("pref_camera_brightness", "exposure-compensation", 5);
      getJPEGQualityPreference();
      this.mJPEGQuality = 100;
      if (CameraController.supportFlashLight() == true)
      {
        FlashRestriction localFlashRestriction = this.mCameraActivity.getFlashRestriction();
        if ((localFlashRestriction == null) || (localFlashRestriction.isDisableFlash() != true))
          break label737;
        this.mCamController.setFlashMode("off");
      }
      this.bShutterSound = PreferenceManager.getDefaultSharedPreferences(this.mCameraActivity).getBoolean("pref_play_shutter_sound", true);
      if (this.mMode != 0)
        break label795;
      str3 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_effect");
      if (!str3.equals("none"))
        break label810;
      this.mbNoneEffect = true;
      if (this.mbNoneEffect)
      {
        LOG.V("CameraThread", " Load Settings - ImageProperty ");
        setImageProperty();
      }
      String str4 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_flicker_adjustment");
      this.mCamController.setAntibanding(str4);
      if (DisplayDevice.supportISO())
      {
        if (this.mMode != 1)
          break label818;
        this.mCamController.setCameraParameter("preview-iso", "on");
        this.mCamController.setCameraParameter("iso", "auto");
      }
      if ((this.mMode == 0) && (DisplayDevice.NOT_SENSE_2_0 == true))
      {
        if (!HTCCameraAdvanceSetting.getPrefenceBoolean(this.mCameraActivity, "pref_post_processing").booleanValue())
          break label879;
        this.mCamController.setCameraParameter("postproc-enable-imboost", "on");
        this.mCamController.setCameraParameter("postproc-enable-denoise", "on");
      }
      if ((DisplayDevice.showSceneInMenu() == true) && (is2ndCamera() != true))
      {
        if (this.mMode != 0)
          break label908;
        str5 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_scene");
        if ((!str5.equals("auto")) && (str5 != null))
          break label923;
        this.m_EventManager.raiseEvent(new BooleanEvent("AutoScene.Enabled", true));
        label406: this.mCamController.setSceneMode(str5);
      }
      if (DisplayDevice.isDoubleShot())
      {
        boolean bool1 = HTCCameraAdvanceSetting.getPrefenceBoolean(this.mCameraActivity, "pref_blink detection").booleanValue();
        bool2 = HTCCameraAdvanceSetting.getPrefenceBoolean(this.mCameraActivity, "pref_smile_capture").booleanValue();
        if (!bool1)
          break label943;
        this.mCamController.setCameraParameter("ola-sbd-options", "103");
      }
      label469: if ((this.mMode == 1) && (DisplayDevice.supportStereoRecord()))
      {
        if (!HTCCameraAdvanceSetting.getPrefenceBoolean(this.mCameraActivity, "pref_stereo_recording").booleanValue())
          break label980;
        LOG.V("CameraThread", "Stereo = true");
      }
    }
    for (this.bIsStereo = true; ; this.bIsStereo = false)
    {
      if (is3DMode())
      {
        this.m3DFileFormat = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_3D_file_format");
        this.mCamController.setCameraParameter("3d-file-format", this.m3DFileFormat);
        LOG.V("CameraThread", "Set 3D file format: " + this.m3DFileFormat);
      }
      LOG.V("CameraThread", "End to Load Settings to Set Camera");
      return;
      if (!DisplayDevice.isDoubleShot())
      {
        str2 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_white_balance");
        break;
      }
      if (!this.mCameraActivity.isServiceMode());
      for (String str1 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_scene_ds"); ; str1 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_scene_service_ds"))
      {
        int j = 0;
        if ((str1 != null) && (!str1.equals("null")))
          j = Integer.parseInt(str1);
        if (j != 8)
          break label685;
        str2 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_white_balance_manual");
        break;
      }
      label685: if (is2ndCamera() != true)
      {
        HTCCameraAdvanceSetting.writePreference(this.mCameraActivity, "pref_camera_white_balance", "auto");
        str2 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_white_balance");
        break;
      }
      str2 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_white_balance_2nd");
      break;
      label737: String str7;
      if (this.mMode == 1)
        if (this.mCameraActivity.isTurnOnTorch())
          str7 = "torch";
      while (true)
      {
        this.mCamController.setFlashMode(str7);
        break;
        str7 = "off";
        continue;
        str7 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_flash_mode");
      }
      label795: str3 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_video_effect");
      break label186;
      label810: this.mbNoneEffect = false;
      break label202;
      label818: this.mCamController.setCameraParameter("preview-iso", "off");
      String str6 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_camera_iso");
      if ((str6 == null) || (str6.equals("null")))
        str6 = "auto";
      this.mCamController.setCameraParameter("iso", str6);
      break label283;
      label879: this.mCamController.setCameraParameter("postproc-enable-imboost", "off");
      this.mCamController.setCameraParameter("postproc-enable-denoise", "off");
      break label339;
      label908: str5 = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, "pref_video_scene");
      break label373;
      label923: this.m_EventManager.raiseEvent(new BooleanEvent("AutoScene.Enabled", false));
      break label406;
      label943: if (bool2)
      {
        this.mCamController.setCameraParameter("ola-sbd-options", "110");
        break label469;
      }
      this.mCamController.setCameraParameter("ola-sbd-options", "100");
      break label469;
      label980: LOG.V("CameraThread", "Stereo = false");
    }
  }

  private void releaseMediaRecorder()
  {
    LOG.V("CameraThread", "releaseMediaRecorder()");
    if (this.mRecorder != null);
    try
    {
      this.mRecorder.setOnInfoListener(null);
    }
    catch (RuntimeException localRuntimeException2)
    {
      try
      {
        this.mRecorder.reset();
      }
      catch (RuntimeException localRuntimeException2)
      {
        try
        {
          while (true)
          {
            this.mRecorder.release();
            this.mRecorder = null;
            return;
            localRuntimeException1 = localRuntimeException1;
            LOG.E("CameraThread", "mRecorder.setOnInfoListener(null - Runtime exception caught", localRuntimeException1);
            continue;
            localRuntimeException2 = localRuntimeException2;
            LOG.E("CameraThread", "mRecorder.reset() - Runtime exception caught", localRuntimeException2);
          }
        }
        catch (RuntimeException localRuntimeException3)
        {
          while (true)
            LOG.E("CameraThread", "mRecorder.release() - Runtime exception caught", localRuntimeException3);
        }
      }
    }
  }

  private void resetCameraFlag()
  {
    this.mIsOnlySetResolution = false;
    this.mTaking_picture = false;
  }

  private void setCounter(String paramString, int paramInt)
  {
    try
    {
      String str = new Integer(paramInt).toString();
      HTCCameraAdvanceSetting.writePreference(this.mCameraActivity, paramString, str);
      return;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      while (true)
        LOG.E("CameraThread", "incCounter: " + paramInt, localNumberFormatException);
    }
  }

  private void setImageProperty()
  {
    setParameterFromBarLevel("pref_camera_contrast", "contrast", 5);
    setParameterFromBarLevel("pref_camera_saturation", "saturation", 5);
    setParameterFromBarLevel("pref_camera_shaprness", "sharpness", 5);
  }

  private void setParameterFromBarLevel(String paramString1, String paramString2, int paramInt)
  {
    String str = HTCCameraAdvanceSetting.getPrefenceValue(this.mCameraActivity, paramString1);
    if ((str != null) && (!str.equals("null")));
    while (true)
    {
      try
      {
        int i = this.mCamController.mapBarLevel2SettingValue(paramString2, Integer.parseInt(str), paramInt);
        this.mCamController.setCameraParameter(paramString2, i);
        return;
      }
      catch (Exception localException)
      {
        LOG.E("CameraThread", "get level failed !!!", localException);
        CameraController.SettingInfo localSettingInfo2 = this.mCamController.getSettingsInfo(paramString2);
        this.mCamController.setCameraParameter(paramString2, localSettingInfo2.getDefault());
        continue;
      }
      CameraController.SettingInfo localSettingInfo1 = this.mCamController.getSettingsInfo(paramString2);
      this.mCamController.setCameraParameter(paramString2, localSettingInfo1.getDefault());
    }
  }

  private void stop_video_recording()
  {
    if (mTakeFocus);
    try
    {
      this.mCameraDevice.cancelAutoFocus();
      LOG.W("CameraThread", "stop recording, so cancel auto focus, mTakeFocus = false");
      mTakeFocus = false;
      if ((DisplayDevice.supportStereoRecord()) && (HTCCameraAdvanceSetting.getIsRecordWithAudio(this.mCameraActivity)))
        SoundEffect.disableSoundEffect();
      if (this.mRecorder == null);
    }
    catch (IOException localIOException)
    {
      try
      {
        this.mRecorder.stop();
      }
      catch (IOException localIOException)
      {
        try
        {
          if (!isRecPowerWarning())
            this.mCameraDevice.reconnect();
          LOG.V("CameraThread", "stop video recording : reconnect camera device");
        }
        catch (IOException localIOException)
        {
          try
          {
            while (true)
            {
              if (!isRecPowerWarning())
                this.mCameraDevice.stopPreview();
              this.mCameraActivity.restoreBackgrounddataSetting();
              if ((this.mCameraActivity.isRequestMode(IntentManager.RequestMode.Main) != true) && (this.mCameraActivity.isRequestName(IntentManager.RequestName.Album) != true))
                break label341;
              if (this.mCameraHandler == null)
                break;
              MessageHandler.sendEmptyMessageDelayed(this.mCameraHandler, 47, 500L);
              if ((DisplayDevice.supportThumbnailAlbumButton()) && ((this.mCameraActivity.isRequestMode(IntentManager.RequestMode.Main) == true) || (this.mCameraActivity.isRequestName(IntentManager.RequestName.Album) == true)) && ((!this.mCameraActivity.needsReview()) || (!this.mCameraActivity.isUIReady())))
                MessageHandler.sendObtainMessage(this.mCameraHandler, 30, 0, 0, this.mfilepath + "/" + this.mfilename);
              this.mCameraActivity.getAudioManager().setParameters("CAMCORDER_MODE=OFF");
              return;
              localException2 = localException2;
              LOG.E("CameraThread", "cancel focus failed", localException2);
              continue;
              localRuntimeException = localRuntimeException;
              LOG.E("CameraThread", "mRecorder.stop() - Runtime exception caught", localRuntimeException);
              continue;
              localIOException = localIOException;
              LOG.E("CameraThread", "camera reconnect exception.", localIOException);
            }
          }
          catch (Exception localException1)
          {
            while (true)
            {
              LOG.E("CameraThread", "stop preview exception." + localException1);
              continue;
              stop_video_recording_delay();
              continue;
              label341: stop_video_recording_delay();
            }
          }
        }
      }
    }
  }

  private void stop_video_recording_delay()
  {
    LOG.V("CameraThread", "stop_video_recording_delay start");
    try
    {
      String str1 = "video/3gpp";
      if ((DisplayDevice.supportOnlyMP4VideoFormat()) && (!isMMSRecording()))
        str1 = "video/mp4";
      if ((is3DMode()) && (!this.bSwitchCamera))
        str1 = "video/mp4-3d";
      DCFRuler.StorageCardControl.setURIType(1);
      mLastContentUri = ImageManager.instance().addVideo(this.mCameraActivity, this.mCameraActivity.getContentResolver(), DCFRuler.getFileNameWithoutExtension(this.mfilename), "", this.mStartRecordingTime, null, 0, this.mfilepath, this.mfilename, str1, this.mCameraActivity.getRecordMS());
      ImageManager.setVideoSize(this.mCameraActivity.getContentResolver(), mLastContentUri);
      String str2 = this.mfilepath + "/" + this.mfilename;
      EventManager localEventManager2 = this.m_EventManager;
      VideoSavedEvent localVideoSavedEvent = new VideoSavedEvent("Media.Saved", mLastContentUri, str2);
      localEventManager2.raiseEvent(localVideoSavedEvent);
      releaseMediaRecorder();
      setRecPowerWarning(false);
      LOG.V("CameraThread", "stop_video_recording_delay end");
      return;
    }
    catch (Exception localException)
    {
      while (true)
      {
        LOG.E("CameraThread", "stop_video_recording(), create uri failed!!", localException);
        EventManager localEventManager1 = this.m_EventManager;
        MediaSaveFailedEvent localMediaSaveFailedEvent = new MediaSaveFailedEvent("Media.SaveFailed", 0, localException);
        localEventManager1.raiseEvent(localMediaSaveFailedEvent);
      }
    }
  }

  private boolean store_image(byte[] paramArrayOfByte, StorePictureCallback paramStorePictureCallback, DCFInfo paramDCFInfo)
  {
    int i;
    try
    {
      LOG.V("CameraThread", "Store Jpeg Image...");
      DCFRuler.PathPackage localPathPackage = getSavedFile(paramDCFInfo);
      DCFRuler.StorageCardControl.setURIType(0);
      if ((is3DMode()) && (!this.bSwitchCamera))
        mLastContentUri = ImageManager.instance().addImage(this.mCameraActivity, this.mCameraActivity.getContentResolver(), DCFRuler.getFileNameWithoutExtension(localPathPackage.mFileName), "", System.currentTimeMillis(), this.mLocation, 0, localPathPackage.mDirectoryName, localPathPackage.mFileName, this.m3DFileFormat);
      while (true)
      {
        this.mfilename = localPathPackage.mFileName;
        this.mfilepath = localPathPackage.mDirectoryName;
        LOG.W("CameraThread", "Store image - directory name=" + localPathPackage.mDirectoryName + ",  file name = " + localPathPackage.mFileName);
        if (mLastContentUri != null)
          break;
        LOG.E("CameraThread", "Store image: mLastContentUri = null");
        mLastContentUri = Uri.parse("file://" + this.mfilepath + "/" + this.mfilename);
        ImageManager.instance().storeImage(mLastContentUri, this.mCameraActivity, this.mCameraActivity.getContentResolver(), 0, null, paramArrayOfByte).get();
        setCounter(paramDCFInfo.getFileCounterPreferenceKey(), localPathPackage.mImageCounter);
        setCounter(paramDCFInfo.getDirCounterPreferenceKey(), localPathPackage.mDirectoryCounter);
        if (paramStorePictureCallback == null)
          break label568;
        paramStorePictureCallback.onPictureStored(paramArrayOfByte, mLastContentUri, this.mfilepath + "/" + this.mfilename);
        break label568;
        mLastContentUri = ImageManager.instance().addImage(this.mCameraActivity, this.mCameraActivity.getContentResolver(), DCFRuler.getFileNameWithoutExtension(localPathPackage.mFileName), "", System.currentTimeMillis(), this.mLocation, 0, localPathPackage.mDirectoryName, localPathPackage.mFileName);
      }
    }
    catch (DCFRuler.OverMaxDCFDirectoryNumberException localOverMaxDCFDirectoryNumberException)
    {
      while (true)
      {
        LOG.E("CameraThread", "The number of the Files are over, please insert a new sdcard ");
        if (paramStorePictureCallback != null)
          paramStorePictureCallback.onError(paramArrayOfByte, localOverMaxDCFDirectoryNumberException);
        EventManager localEventManager3 = this.m_EventManager;
        MediaSaveFailedEvent localMediaSaveFailedEvent3 = new MediaSaveFailedEvent("Media.SaveFailed", 0, localOverMaxDCFDirectoryNumberException);
        localEventManager3.raiseEvent(localMediaSaveFailedEvent3);
        i = 0;
        break;
        LOG.W("CameraThread", "Store image: " + mLastContentUri);
      }
    }
    catch (IOException localIOException)
    {
      LOG.E("CameraThread", "Cannot find available image file location, please check the storage card", localIOException);
      if (paramStorePictureCallback != null)
        paramStorePictureCallback.onError(paramArrayOfByte, localIOException);
      EventManager localEventManager2 = this.m_EventManager;
      MediaSaveFailedEvent localMediaSaveFailedEvent2 = new MediaSaveFailedEvent("Media.SaveFailed", 0, localIOException);
      localEventManager2.raiseEvent(localMediaSaveFailedEvent2);
      i = 0;
    }
    catch (Exception localException)
    {
      LOG.E("CameraThread", "Exception while compressing image.", localException);
      if (paramStorePictureCallback != null)
        paramStorePictureCallback.onError(paramArrayOfByte, localException);
      EventManager localEventManager1 = this.m_EventManager;
      MediaSaveFailedEvent localMediaSaveFailedEvent1 = new MediaSaveFailedEvent("Media.SaveFailed", 0, localException);
      localEventManager1.raiseEvent(localMediaSaveFailedEvent1);
      i = 0;
    }
    while (true)
    {
      return i;
      label568: i = 1;
    }
  }

  private void store_image_on_data(byte[] paramArrayOfByte)
    throws Exception
  {
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream("/data/test_image.jpg"));
    localBufferedOutputStream.write(paramArrayOfByte, 0, paramArrayOfByte.length);
    localBufferedOutputStream.flush();
    localBufferedOutputStream.close();
  }

  private void store_image_on_sdcard(byte[] paramArrayOfByte)
    throws Exception
  {
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream("/sdcard/test_image.jpg"));
    localBufferedOutputStream.write(paramArrayOfByte, 0, paramArrayOfByte.length);
    localBufferedOutputStream.flush();
    localBufferedOutputStream.close();
  }

  private final void threadAccessCheck()
  {
    if (Thread.currentThread() != this)
      throw new RuntimeException("cross-thread access");
  }

  public boolean IsEqualOrAbove720p()
  {
    int i;
    if (this.mMode == 0)
      i = 0;
    while (true)
    {
      return i;
      Resolution localResolution = MovieModeHandler.getMovieModeHandler().getCurrentResolutionSettingValue(this.mCameraActivity);
      if ((localResolution.equals(Resolution.Video_720p)) || (localResolution.equals(Resolution.Video_720p_Online)))
      {
        i = 1;
        continue;
      }
      if ((localResolution.equals(Resolution.Video_1080p)) || (localResolution.equals(Resolution.Video_1080p_Online)))
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  public long calculatePicturesRemaining()
  {
    LOG.V("CameraThread", "calculatePicturesRemaining() - start");
    try
    {
      Resolution localResolution = PhotoModeHandler.getPhotoModeHandler().getCurrentResolutionSettingValue(this.mCameraActivity);
      long l2 = ResolutionSize.getInstance().getSize(localResolution.getNameResId(), this.mJPEGQualityKey);
      if (-1L == l2)
      {
        LOG.E("CameraThread", "getResolutionSize fail: " + localResolution.getKeyName() + ", " + this.mJPEGQualityKey + ", " + l2);
        l2 = 400000L;
      }
      StatFs localStatFs = new StatFs(DCFRuler.StorageCardControl.getCurStorageDirectory().toString());
      l1 = (localStatFs.getAvailableBlocks() * localStatFs.getBlockSize() - 1048576L) / l2;
      if (l1 < 0L)
        l1 = 0L;
      LOG.V("CameraThread", "return freeshot = " + l1);
      LOG.V("CameraThread", "calculatePicturesRemaining() - end");
      return l1;
    }
    catch (Exception localException)
    {
      while (true)
      {
        LOG.E("CameraThread", "catch - calculatePicturesRemaining with exception: ", localException);
        LOG.E("CameraThread", "return freeshot = -1");
        long l1 = -1L;
      }
    }
  }

  public boolean canTakePicture()
  {
    return this.mbCanTakePicture;
  }

  public final void changeZoom(int paramInt)
  {
    int i;
    if (Thread.currentThread() == this)
    {
      LOG.V("CameraThread", "changeZoom(" + paramInt + ") - start sync");
      i = HTCCameraAdvanceSetting.mZoomValue;
      if (i != paramInt);
    }
    while (true)
    {
      return;
      HTCCameraAdvanceSetting.mZoomValue = paramInt;
      if ((this.mCameraDevice == null) || (this.mCamController == null))
      {
        LOG.W("CameraThread", "Camera is not open, cannot change zoom");
        continue;
      }
      int k;
      if ((this.mRecording) && (!DisplayDevice.needDelayZooming()))
        if (i < paramInt)
        {
          k = i;
          label100: k = Math.min(k + 2, paramInt);
          this.mCamController.setCameraParameter("taking-picture-zoom", k);
          this.mCamController.doSetCameraParameters();
          if ((this.mCameraHandler != null) && (this.mCameraHandler.hasMessages(70)))
          {
            LOG.V("CameraThread", "Pending zooming request found, stop zooming at " + k);
            HTCCameraAdvanceSetting.mZoomValue = k;
          }
        }
      while (true)
      {
        this.m_EventManager.raiseEvent(new OneValueEvent("Zoom.Changed", Integer.valueOf(HTCCameraAdvanceSetting.mZoomValue)));
        LOG.V("CameraThread", "changeZoom(" + paramInt + ") - end sync");
        break;
        Thread.yield();
        if (k < paramInt)
          break label100;
        continue;
        int j = i;
        do
        {
          j = Math.max(j - 2, paramInt);
          this.mCamController.setCameraParameter("taking-picture-zoom", j);
          this.mCamController.doSetCameraParameters();
          if ((this.mCameraHandler != null) && (this.mCameraHandler.hasMessages(70)))
          {
            LOG.V("CameraThread", "Pending zooming request found, stop zooming at " + j);
            HTCCameraAdvanceSetting.mZoomValue = j;
            break;
          }
          Thread.yield();
        }
        while (j > paramInt);
        continue;
        this.mCamController.setCameraParameter("taking-picture-zoom", paramInt);
        this.mCamController.doSetCameraParameters();
        if (!DisplayDevice.needDelayZooming())
          continue;
        try
        {
          Thread.sleep(100L);
        }
        catch (Exception localException)
        {
        }
      }
      if (this.mCameraHandler != null)
      {
        LOG.V("CameraThread", "changeZoom(" + paramInt + ") - start async");
        MessageHandler.sendUniqueObtainMessage(this.mCameraHandler, 70, paramInt, 0, null);
        LOG.V("CameraThread", "changeZoom(" + paramInt + ") - end async");
        continue;
      }
      LOG.E("CameraThread", "Cannot change zoom because there is no handler");
    }
  }

  void close_camera()
  {
    if ((this.mRecording == true) && (this.mMode == 1))
    {
      stop_video_recording();
      this.mRecording = false;
    }
    if ((DisplayDevice.supportSharpSensor()) && (mTakeFocus == true));
    try
    {
      this.mCameraDevice.cancelAutoFocus();
      LOG.W("CameraThread", "cancel auto focus, mTakeFocus = false");
      mTakeFocus = false;
      if (this.mPreviewing == true)
      {
        LOG.V("CameraThread", "before stopPreview");
        this.mCameraDevice.stopPreview();
        this.mPreviewing = false;
        LOG.V("CameraThread", "after stopPreview");
      }
      this.mCurrentColorEffect = null;
      resetJpegData();
      if (this.mCameraDevice != null)
      {
        releaseMediaRecorder();
        LOG.V("CameraThread", "before release");
        localCloseCameraRequestEvent = new CloseCameraRequestEvent("Request.CloseCamera", this.mCameraDevice);
      }
    }
    catch (Exception localException)
    {
      while (true)
      {
        try
        {
          CloseCameraRequestEvent localCloseCameraRequestEvent;
          this.m_EventManager.raiseEvent(localCloseCameraRequestEvent);
          if (localCloseCameraRequestEvent.isHandled())
            continue;
          this.mCameraDevice.release();
          LOG.V("CameraThread", "after release");
          resetCameraFlag();
          this.mCameraDevice = null;
          this.mCamController = null;
          this.mCaptureState = 0;
          LOG.V("CameraThread", "close camera - mCaptureState = CAPTURE_STATE_IDLE");
          mIsLastCameraClosed = true;
          LOG.W("CameraThread", "Release Camera - set mIsLastCameraClosed to true");
          return;
          localException = localException;
          LOG.E("CameraThread", "cancel focus failed", localException);
        }
        catch (Throwable localThrowable)
        {
          LOG.E("CameraThread", "Exception was occurred while requesting close camera", localThrowable);
          continue;
        }
        LOG.W("CameraThread", "mCameraDevice == null in close_camera");
      }
    }
  }

  public final void createPostViewImage(Bitmap paramBitmap)
  {
    if ((this.mCameraActivity != null) && (this.mCameraActivity.isServiceMode() == true));
    while (true)
    {
      return;
      if (paramBitmap == null)
      {
        LOG.E("CameraThread", "!!!!!!!! bitmap == null !!!!!!!!");
        continue;
      }
      LOG.V("CameraThread", "bitmap != null");
      Bitmap localBitmap = Bitmap.createBitmap(paramBitmap);
      this.m_EventManager.raiseEvent(new PictureEvent("ReviewAnimation.PostviewCreated", null, localBitmap, false));
      localBitmap.recycle();
    }
  }

  public final void createPostViewImage(byte[] paramArrayOfByte)
  {
    if ((this.mCameraActivity != null) && (this.mCameraActivity.isServiceMode() == true));
    while (true)
    {
      return;
      if (paramArrayOfByte == null)
      {
        LOG.E("CameraThread", "!!!!!!!! postviewData == null !!!!!!!!");
        continue;
      }
      LOG.V("CameraThread", "postviewData != null, postviewData.length = " + paramArrayOfByte.length);
      Camera.Parameters localParameters = this.mCameraDevice.getParameters();
      if (localParameters == null)
        continue;
      String str = localParameters.get("postview-size");
      int i = str.indexOf('x');
      Bitmap localBitmap = ImageUtility.createBitmapFromRawData(Integer.parseInt(str.substring(0, i)), Integer.parseInt(str.substring(i + 1)), Image.Config.YVU_420SP, paramArrayOfByte);
      this.m_EventManager.raiseEvent(new PictureEvent("ReviewAnimation.PostviewCreated", paramArrayOfByte, localBitmap, false));
      localBitmap.recycle();
    }
  }

  public final void deleteLatestMedia()
  {
    if (Thread.currentThread() == this)
    {
      LOG.V("CameraThread", "deleteLatestMedia() - start sync");
      DeletionRequestEvent localDeletionRequestEvent = new DeletionRequestEvent("Request.DeleteLatestMedia");
      this.m_EventManager.raiseEvent(localDeletionRequestEvent);
      if (!localDeletionRequestEvent.isDeleted())
      {
        LOG.V("CameraThread", "Use default method to delete latest media");
        new Thread("Default media deletion thread", this.mCameraActivity, mLastContentUri)
        {
          public void run()
          {
            if ((this.val$context != null) && (this.val$contentUri != null));
            try
            {
              LOG.V("CameraThread", "Deleting media '" + this.val$contentUri + "'");
              this.val$context.getContentResolver().delete(this.val$contentUri, null, null);
              localMediaDeletionCompletedEvent = new MediaDeletionCompletedEvent("Media.DeletionCompleted", this.val$contentUri, null, true, true, null);
              if (CameraThread.this.mCameraHandler != null)
                CameraThread.this.mCameraHandler.post(new Runnable(localMediaDeletionCompletedEvent)
                {
                  public void run()
                  {
                    CameraThread.this.m_EventManager.raiseEvent(this.val$raisingEvent);
                  }
                });
              return;
            }
            catch (Throwable localThrowable)
            {
              while (true)
              {
                LOG.E("CameraThread", "Cannot delete media '" + this.val$contentUri + "'", localThrowable);
                MediaDeletionCompletedEvent localMediaDeletionCompletedEvent = new MediaDeletionCompletedEvent("Media.DeletionCompleted", this.val$contentUri, null, true, false, localThrowable);
              }
            }
          }
        }
        .start();
      }
      switch (this.mMode)
      {
      default:
        LOG.V("CameraThread", "deleteLatestMedia() - end sync");
      case 0:
      case 1:
      }
    }
    while (true)
    {
      return;
      DCFRuler.backToCounterForImage(this.mCameraActivity, DCFRuler.DefaultDCFInfo);
      break;
      DCFRuler.backToCounterForVideo(this.mCameraActivity);
      break;
      if (this.mCameraHandler != null)
      {
        LOG.V("CameraThread", "deleteLatestMedia() - start async");
        this.mCameraHandler.post(new Runnable()
        {
          public void run()
          {
            CameraThread.this.deleteLatestMedia();
          }
        });
        LOG.V("CameraThread", "deleteLatestMedia() - end async");
        continue;
      }
      LOG.E("CameraThread", "Cannot delete media because there is no handler");
    }
  }

  public void enableShutterSound(boolean paramBoolean)
  {
    this.bShutterSound = paramBoolean;
  }

  public final void endTakePicture()
  {
    threadAccessCheck();
    if (!this.mTaking_picture)
    {
      LOG.W("CameraThread", "mTaking_picture == false");
      return;
    }
    this.mTaking_picture = false;
    this.mCanStartPreview = true;
    if ((this.mCaptureState != 0) && (this.mCaptureState != 1))
    {
      LOG.W("CameraThread", "mCaptureState = " + this.mCaptureState + ", sending CLOSE_CAMERA message");
      MessageHandler.sendEmptyMessage(this.mCameraHandler, 2);
    }
    while (true)
    {
      this.m_EventManager.raiseEvent("Capture.Ended");
      break;
      LOG.V("CameraThread", "reset mCaptureState to CAPTURE_STATE_IDLE");
      this.mCaptureState = 0;
    }
  }

  public Camera getCamera()
  {
    return this.mCameraDevice;
  }

  public CameraController getCameraController()
  {
    return this.mCamController;
  }

  public final int getCameraType()
  {
    int i;
    if (is2ndCamera())
      i = 2;
    while (true)
    {
      return i;
      if (is3DMode())
      {
        i = 3;
        continue;
      }
      i = 1;
    }
  }

  public int getCaptureHeight()
  {
    if (this.mMode == 0);
    for (int i = this.Camera_Mode_Height; ; i = this.Video_Mode_Height)
      return i;
  }

  public int getCaptureWidth()
  {
    if (this.mMode == 0);
    for (int i = this.Camera_Mode_Width; ; i = this.Video_Mode_Width)
      return i;
  }

  public final CommonCaptureHandler getCommonCaptureHandler()
  {
    return this.mCommonCaptureHandler;
  }

  public final Component getComponent(String paramString)
  {
    return this.mComponentManager.getComponent(paramString);
  }

  public final ComponentManager getComponentManager()
  {
    return this.mComponentManager;
  }

  public final EventManager getEventManager()
  {
    return this.m_EventManager;
  }

  public Handler getHandler()
  {
    return this.mCameraHandler;
  }

  public byte[] getJpegData()
  {
    return this.mJpegData;
  }

  public final int getMaximumZoom()
  {
    return this.m_MaxZoom;
  }

  public final int getMinimumZoom()
  {
    return this.m_MinZoom;
  }

  public final int getMode()
  {
    return this.mMode;
  }

  public int getPreviewHeight()
  {
    if (this.mMode == 0);
    for (int i = this.Camera_Preview_Height; ; i = this.Video_Mode_Height)
      return i;
  }

  public int getPreviewWidth()
  {
    if (this.mMode == 0);
    for (int i = this.Camera_Preview_Width; ; i = this.Video_Mode_Width)
      return i;
  }

  public boolean getRecorderStatus()
  {
    return this.mRecording;
  }

  public long getRecordingTime()
  {
    return (System.currentTimeMillis() - this.mStartRecordingTime) / 1000L;
  }

  public String getStoredDirectoryPath()
  {
    return this.mfilepath;
  }

  public String getStoredFileName()
  {
    return this.mfilename;
  }

  String getStoredVideoPath()
  {
    return this.mVideoPath;
  }

  public boolean hasShutterSound()
  {
    return this.bShutterSound;
  }

  public final void interruptCapture()
  {
    LOG.V("CameraThread", "interruptCapture() - start");
    threadAccessCheck();
    if (!this.mTaking_picture)
      LOG.W("CameraThread", "Capturing is not in progress");
    while (true)
    {
      return;
      this.mIsCaptureInterrupted = true;
      LOG.V("CameraThread", "interruptCapture() - end");
    }
  }

  public boolean is2ndCamera()
  {
    return this.bSwitchCamera;
  }

  public boolean is3DMode()
  {
    int i;
    if (!DisplayDevice.support3DCamera())
      i = 0;
    while (true)
    {
      return i;
      if (this.m3DPreviewStatus == 1)
      {
        i = 0;
        continue;
      }
      i = 1;
    }
  }

  public boolean is720p()
  {
    int i;
    if (this.mMode == 0)
      i = 0;
    while (true)
    {
      return i;
      Resolution localResolution = MovieModeHandler.getMovieModeHandler().getCurrentResolutionSettingValue(this.mCameraActivity);
      if ((localResolution.equals(Resolution.Video_720p)) || (localResolution.equals(Resolution.Video_720p_Online)))
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  public boolean isCameraTakingPicture()
  {
    return this.mTaking_picture;
  }

  public boolean isLastCameraClosed()
  {
    int i;
    if (this.mCameraDevice == null)
    {
      if (mIsLastCameraClosed)
        break label47;
      LOG.W("CameraThread", "mIsLastCameraClosed = false, waiting for last camera close");
      i = 0;
    }
    while (true)
    {
      return i;
      if (this.mCaptureState != 0)
      {
        LOG.W("CameraThread", "mCaptureState != CAPTURE_STATE_IDLE, waiting for camera close");
        i = 0;
        continue;
      }
      label47: i = 1;
    }
  }

  public boolean isPreviewing()
  {
    return this.mPreviewing;
  }

  public boolean isRecPowerWarning()
  {
    return this.mRecPowerWarning;
  }

  public final boolean isShutterSoundNeeded()
  {
    if ((this.bShutterSound) || (this.mCameraActivity.isAutoCaptureTask()));
    for (int i = 1; ; i = 0)
      return i;
  }

  public final boolean isTakingFocus()
  {
    return mTakeFocus;
  }

  public final boolean isTakingPicture()
  {
    return this.mTaking_picture;
  }

  public final boolean isWaitingForTakingFocus()
  {
    int i;
    if (this.mCameraHandler != null)
      if ((this.mCameraHandler.hasMessages(4)) || (this.mCameraHandler.hasMessages(5)))
        i = 1;
    while (true)
    {
      return i;
      i = 0;
      continue;
      i = 0;
    }
  }

  public final boolean isWaitingForTakingPicture()
  {
    if (this.mCameraHandler != null);
    for (boolean bool = this.mCameraHandler.hasMessages(7); ; bool = false)
      return bool;
  }

  public final boolean isZoomRangeRetrieved()
  {
    if (this.m_MaxZoom != -2147483648);
    for (int i = 1; ; i = 0)
      return i;
  }

  public final void playShutterSound()
  {
    LOG.V("CameraThread", "Play Take Picture Sound");
    MessageHandler.sendObtainMessage(this.mUIHandler, 20, 2131099649, 0, null);
  }

  public void releaseCameraThread()
  {
    this.mCameraActivity = null;
    this.mUIHandler = null;
  }

  public void resetJpegData()
  {
    this.mJpegData = null;
  }

  public final void restartPreview(int paramInt)
  {
    if (Thread.currentThread() == this)
    {
      LOG.V("CameraThread", "restartPreview(" + paramInt + ") - start sync");
      stopPreview();
      startPreview(paramInt);
      LOG.V("CameraThread", "restartPreview() - end sync");
    }
    while (true)
    {
      return;
      if (this.mCameraHandler != null)
      {
        LOG.V("CameraThread", "restartPreview(" + paramInt + ") - start async");
        this.mCameraHandler.post(new Runnable(paramInt)
        {
          public void run()
          {
            CameraThread.this.restartPreview(this.val$status);
          }
        });
        LOG.V("CameraThread", "restartPreview() - end async");
        continue;
      }
      LOG.E("CameraThread", "Cannot re-start preview because there is no handler for camera thread");
    }
  }

  public void run()
  {
    LOG.V("CameraThread", "*************************************** run");
    Looper.prepare();
    this.mCameraHandler = new MainHandler(null);
    this.m_EventManager = new EventManager(this);
    this.mComponentManager = new ComponentManager(this);
    if ((!this.mCameraActivity.isServiceMode()) && (ThumbnailController.isSupported()))
      this.mComponentManager.addComponent(new ThumbnailController(this));
    if (PanoramaController.isSupported(this.mCameraActivity))
      this.mComponentManager.addComponent(new PanoramaController(this));
    if (HdrController.isSupported(this.mCameraActivity))
      this.mComponentManager.addComponent(new HdrController(this));
    if (BurstController.isSupported(this.mCameraActivity))
      this.mComponentManager.addComponent(new BurstController(this));
    if (HandShakeController.isSupported())
      this.mComponentManager.addComponent(new HandShakeController(this));
    if (AutoSceneController.isSupported())
      this.mComponentManager.addComponent(new AutoSceneController(this));
    if (DOTIndicatorController.isSupported())
      this.mComponentManager.addComponent(new DOTIndicatorController(this));
    if (ZoomBarController.isSupported())
      this.mComponentManager.addComponent(new ZoomBarController(this));
    if (PowerWarningController.isSupported())
      this.mComponentManager.addComponent(new PowerWarningController(this));
    if (DisplayDevice.isDoubleShot())
      this.mComponentManager.addComponent(new ReviewAnimationController(this));
    if (HappyShotController.isSupported(this.mCameraActivity))
      this.mComponentManager.addComponent(new HappyShotController(this));
    this.mComponentManager.enableAutoInitialization(true);
    if (this.mUIHandler != null)
      if (!this.mUIHandler.sendEmptyMessage(102))
        LOG.E("CameraThread", "Cannot notify UI that camera thread is running, because message cannot be sent");
    while (true)
    {
      Looper.loop();
      this.mComponentManager.deinitializeComponents();
      this.mComponentManager.removeComponents();
      return;
      LOG.E("CameraThread", "Cannot notify UI that camera thread is running, because there is no UI handler");
    }
  }

  public final void setCanStartPreview()
  {
    threadAccessCheck();
    this.mCanStartPreview = true;
  }

  public final void setCaptureHandler(ICaptureHandler paramICaptureHandler)
  {
    threadAccessCheck();
    if (this.mTaking_picture)
    {
      LOG.E("CameraThread", "Set capture handler while taking picture");
      throw new IllegalStateException("Set capture handler while taking picture");
    }
    this.mCaptureHandler = paramICaptureHandler;
  }

  public void setColorEffect(String paramString)
  {
    threadAccessCheck();
    if ((this.mCameraDevice == null) || (this.mCamController == null));
    while (true)
    {
      return;
      if ((paramString == null) || (!paramString.equals(this.mCurrentColorEffect)))
      {
        this.mCurrentColorEffect = paramString;
        this.mCamController.setColorEffect(paramString);
        this.mCamController.doSetCameraParameters();
        continue;
      }
    }
  }

  public final void setMode(int paramInt)
  {
    LOG.V("CameraThread", "setMode(" + paramInt + ")");
    if (this.mPreviewing)
    {
      LOG.E("CameraThread", "Cannot set mode during preview");
      throw new RuntimeException("Cannot set mode during preview");
    }
    if (this.mRecording)
    {
      LOG.E("CameraThread", "Cannot set mode during recording");
      throw new RuntimeException("Cannot set mode during recording");
    }
    this.mMode = paramInt;
  }

  public void setRecPowerWarning(boolean paramBoolean)
  {
    LOG.W("CameraThread", "set mRecPowerWarning " + paramBoolean);
    this.mRecPowerWarning = paramBoolean;
  }

  public void showStorageToast(boolean paramBoolean)
  {
    int j;
    if ((DCFRuler.StorageCardControl.bSupportPhoneStorage) && (Storage_Status != 0))
    {
      LOG.W("CameraThread", "no storage, ready to switch");
      j = Storage_Status;
      DCFRuler.StorageCardControl.toggleStorageType(this.mCameraActivity);
      hasStorageTest();
      if (Storage_Status == 0)
        if (paramBoolean == true)
        {
          if (this.mMode != 0)
            break label120;
          if (calculatePicturesRemaining() < 1L)
            Storage_Status = 3;
        }
    }
    label266: label570: 
    while (true)
    {
      int k;
      if (Storage_Status != 3)
      {
        MessageHandler.removeMessages(this.mUIHandler, 37);
        if (DCFRuler.StorageCardControl.getStorageType() == 0)
        {
          k = 2131362098;
          label97: MessageHandler.sendObtainMessage(this.mUIHandler, 37, k, 0, null);
          MessageHandler.sendEmptyMessage(this.mUIHandler, 103);
        }
      }
      while (true)
      {
        return;
        label120: if (this.mCameraActivity.getRecordLimitCheck() == null)
          break label570;
        this.mCameraActivity.getRecordLimitCheck().setByteRate(this.mCameraActivity);
        if (this.mCameraActivity.getRecordLimitCheck().remainTime(false) >= 1L)
          break;
        Storage_Status = 3;
        break;
        k = 2131362099;
        break label97;
        DCFRuler.StorageCardControl.toggleStorageType(this.mCameraActivity);
        Storage_Status = j;
        if ((DisplayDevice.contactsNoStorage() == true) && (this.mCameraActivity.isRequestName(IntentManager.RequestName.Contacts) == true))
        {
          LOG.W("CameraThread", "take picture on contact picture request - not show storage status");
          continue;
        }
        switch (Storage_Status)
        {
        default:
          break;
        case 1:
          String str;
          int i;
          if (DCFRuler.StorageCardControl.getStorageType() == 1)
          {
            str = Environment.getExternalStorageState();
            LOG.I("CameraThread", "@@@@ status: " + str);
            if (!str.equals("shared"))
              break label383;
            LOG.E("CameraThread", "media shared");
            MessageHandler.removeMessages(this.mUIHandler, 37);
            if (!DCFRuler.StorageCardControl.bSupportPhoneStorage)
              break label364;
            i = 2131361836;
            if (this.mMode == 1)
              i = 2131361837;
          }
          while (true)
          {
            MessageHandler.sendObtainMessage(this.mUIHandler, 37, i, 0, null);
            break;
            str = Environment.getPhoneStorageState();
            break label266;
            i = 2131361834;
            if (this.mMode != 1)
              continue;
            i = 2131361835;
          }
          LOG.E("CameraThread", "media status: " + str);
          MessageHandler.removeMessages(this.mUIHandler, 37);
          if ((str.equals("removed")) || (str.equals("bad_removal")) || (str.equals("unmounted")))
          {
            MessageHandler.sendObtainMessage(this.mUIHandler, 37, 34341025, 0, null);
            continue;
          }
          if (ImageManager.isNonWritable() == true)
          {
            MessageHandler.sendObtainMessage(this.mUIHandler, 37, 2131361831, 0, null);
            continue;
          }
          LOG.E("CameraThread", "no storage - unknown case, maybe prepare storage");
          break;
        case 2:
          LOG.E("CameraThread", "cannot know storage state");
          break;
        case 3:
          label364: label383: LOG.E("CameraThread", "storage full");
          MessageHandler.removeMessages(this.mUIHandler, 37);
          if (DCFRuler.StorageCardControl.getStorageType() == 1)
          {
            MessageHandler.sendObtainMessage(this.mUIHandler, 37, 2131361832, 0, null);
            continue;
          }
          MessageHandler.sendObtainMessage(this.mUIHandler, 37, 2131361833, 0, null);
        }
      }
    }
  }

  // ERROR //
  public void startPreview(int paramInt)
  {
    // Byte code:
    //   0: ldc_w 267
    //   3: ldc_w 2135
    //   6: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   9: aload_0
    //   10: getfield 419	com/android/amaze_camera/CameraThread:mPreviewing	Z
    //   13: iconst_1
    //   14: if_icmpne +46 -> 60
    //   17: ldc_w 267
    //   20: new 723	java/lang/StringBuilder
    //   23: dup
    //   24: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   27: ldc_w 2137
    //   30: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   33: aload_0
    //   34: getfield 419	com/android/amaze_camera/CameraThread:mPreviewing	Z
    //   37: invokevirtual 2061	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   40: ldc_w 2139
    //   43: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   46: aload_0
    //   47: getfield 417	com/android/amaze_camera/CameraThread:mMode	I
    //   50: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   53: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   56: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   59: return
    //   60: aload_0
    //   61: invokevirtual 1405	com/android/amaze_camera/CameraThread:isRecPowerWarning	()Z
    //   64: ifeq +15 -> 79
    //   67: ldc_w 267
    //   70: ldc_w 2141
    //   73: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   76: goto -17 -> 59
    //   79: getstatic 393	com/android/amaze_camera/CameraThread:Storage_Status	I
    //   82: iconst_3
    //   83: if_icmpne +7 -> 90
    //   86: iconst_0
    //   87: putstatic 393	com/android/amaze_camera/CameraThread:Storage_Status	I
    //   90: aload_0
    //   91: aload_0
    //   92: getfield 477	com/android/amaze_camera/CameraThread:m3DButtonStatus	I
    //   95: putfield 479	com/android/amaze_camera/CameraThread:m3DPreviewStatus	I
    //   98: aload_0
    //   99: aload_0
    //   100: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   103: ldc_w 2143
    //   106: invokestatic 1058	com/android/amaze_camera/HTCCameraAdvanceSetting:getPrefenceBoolean	(Landroid/app/Activity;Ljava/lang/String;)Ljava/lang/Boolean;
    //   109: invokevirtual 1063	java/lang/Boolean:booleanValue	()Z
    //   112: putfield 435	com/android/amaze_camera/CameraThread:bSwitchCamera	Z
    //   115: invokestatic 2146	com/android/amaze_camera/HTCCameraAdvanceSetting:isSetDefault	()Z
    //   118: iconst_1
    //   119: if_icmpne +8 -> 127
    //   122: aload_0
    //   123: iconst_0
    //   124: putfield 435	com/android/amaze_camera/CameraThread:bSwitchCamera	Z
    //   127: aload_0
    //   128: getfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   131: ifnonnull +192 -> 323
    //   134: ldc_w 267
    //   137: ldc_w 2148
    //   140: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   143: aload_0
    //   144: invokespecial 1753	com/android/amaze_camera/CameraThread:resetCameraFlag	()V
    //   147: aload_0
    //   148: getfield 435	com/android/amaze_camera/CameraThread:bSwitchCamera	Z
    //   151: iconst_1
    //   152: if_icmpne +962 -> 1114
    //   155: aload_0
    //   156: invokestatic 2153	com/android/amaze_camera/SensorHolder:getSecondCamID	()I
    //   159: invokestatic 2157	android/hardware/Camera:open	(I)Landroid/hardware/Camera;
    //   162: putfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   165: aload_0
    //   166: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   169: ifnull +11 -> 180
    //   172: aload_0
    //   173: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   176: iconst_0
    //   177: invokevirtual 2160	com/android/amaze_camera/HTCCamera:registerFocusSensor	(Z)V
    //   180: aload_0
    //   181: getfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   184: aload_0
    //   185: getfield 509	com/android/amaze_camera/CameraThread:mErrorCallback	Lcom/android/amaze_camera/CameraThread$ErrorCallback;
    //   188: invokevirtual 2164	android/hardware/Camera:setErrorCallback	(Landroid/hardware/Camera$ErrorCallback;)V
    //   191: aload_0
    //   192: getfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   195: aload_0
    //   196: getfield 512	com/android/amaze_camera/CameraThread:mHtcCallback	Lcom/android/amaze_camera/CameraThread$HtcCallback;
    //   199: invokevirtual 2168	android/hardware/Camera:setHtcCallback	(Landroid/hardware/Camera$HtcCallback;)V
    //   202: iconst_0
    //   203: putstatic 383	com/android/amaze_camera/CameraThread:mIsLastCameraClosed	Z
    //   206: ldc_w 267
    //   209: ldc_w 2170
    //   212: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   215: aload_0
    //   216: new 693	com/android/amaze_camera/CameraController
    //   219: dup
    //   220: aload_0
    //   221: getfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   224: invokespecial 2173	com/android/amaze_camera/CameraController:<init>	(Landroid/hardware/Camera;)V
    //   227: putfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   230: aload_0
    //   231: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   234: invokevirtual 2176	com/android/amaze_camera/CameraController:setSupportedList	()V
    //   237: aload_0
    //   238: getfield 411	com/android/amaze_camera/CameraThread:m_MaxZoom	I
    //   241: ldc_w 279
    //   244: if_icmpne +77 -> 321
    //   247: aload_0
    //   248: getfield 435	com/android/amaze_camera/CameraThread:bSwitchCamera	Z
    //   251: ifne +70 -> 321
    //   254: aload_0
    //   255: invokevirtual 713	com/android/amaze_camera/CameraThread:is3DMode	()Z
    //   258: ifne +63 -> 321
    //   261: aload_0
    //   262: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   265: ldc_w 1695
    //   268: invokevirtual 1378	com/android/amaze_camera/CameraController:getSettingsInfo	(Ljava/lang/String;)Lcom/android/amaze_camera/CameraController$SettingInfo;
    //   271: astore 15
    //   273: aload_0
    //   274: aload 15
    //   276: invokevirtual 2179	com/android/amaze_camera/CameraController$SettingInfo:getMin	()I
    //   279: putfield 413	com/android/amaze_camera/CameraThread:m_MinZoom	I
    //   282: aload_0
    //   283: aload 15
    //   285: invokevirtual 2182	com/android/amaze_camera/CameraController$SettingInfo:getMax	()I
    //   288: putfield 411	com/android/amaze_camera/CameraThread:m_MaxZoom	I
    //   291: aload_0
    //   292: getfield 651	com/android/amaze_camera/CameraThread:m_EventManager	Lcom/android/amaze_camera/EventManager;
    //   295: new 2184	com/android/amaze_camera/RangeEvent
    //   298: dup
    //   299: ldc 176
    //   301: aload_0
    //   302: getfield 413	com/android/amaze_camera/CameraThread:m_MinZoom	I
    //   305: invokestatic 1016	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   308: aload_0
    //   309: getfield 411	com/android/amaze_camera/CameraThread:m_MaxZoom	I
    //   312: invokestatic 1016	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   315: invokespecial 2187	com/android/amaze_camera/RangeEvent:<init>	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   318: invokevirtual 1025	com/android/amaze_camera/EventManager:raiseEvent	(Lcom/android/amaze_camera/Event;)V
    //   321: iconst_1
    //   322: istore_1
    //   323: iload_1
    //   324: iconst_1
    //   325: if_icmpne +64 -> 389
    //   328: aload_0
    //   329: getfield 435	com/android/amaze_camera/CameraThread:bSwitchCamera	Z
    //   332: ifne +915 -> 1247
    //   335: aload_0
    //   336: invokevirtual 713	com/android/amaze_camera/CameraThread:is3DMode	()Z
    //   339: ifne +908 -> 1247
    //   342: aload_0
    //   343: getfield 417	com/android/amaze_camera/CameraThread:mMode	I
    //   346: ifne +901 -> 1247
    //   349: ldc_w 267
    //   352: ldc_w 2189
    //   355: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   358: aload_0
    //   359: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   362: ldc_w 2191
    //   365: ldc_w 2193
    //   368: invokevirtual 760	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   371: aload_0
    //   372: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   375: ldc_w 2195
    //   378: iconst_1
    //   379: invokevirtual 1372	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;I)V
    //   382: aload_0
    //   383: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   386: invokevirtual 765	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
    //   389: aload_0
    //   390: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   393: aload_0
    //   394: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   397: invokestatic 2199	com/android/amaze_camera/HTCCameraAdvanceSetting:initPrefrenceFiles	(Landroid/app/Activity;Lcom/android/amaze_camera/CameraController;)Z
    //   400: pop
    //   401: invokestatic 1915	com/android/amaze_camera/DisplayDevice:support3DCamera	()Z
    //   404: ifeq +18 -> 422
    //   407: aload_0
    //   408: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   411: ldc_w 2201
    //   414: aload_0
    //   415: invokevirtual 713	com/android/amaze_camera/CameraThread:is3DMode	()Z
    //   418: invokestatic 2204	com/android/amaze_camera/HTCCameraAdvanceSetting:writePreference	(Landroid/app/Activity;Ljava/lang/String;Z)Z
    //   421: pop
    //   422: aload_0
    //   423: getfield 488	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
    //   426: bipush 31
    //   428: invokestatic 871	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
    //   431: aload_0
    //   432: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   435: ldc_w 2206
    //   438: ldc_w 1191
    //   441: invokevirtual 760	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   444: iconst_0
    //   445: putstatic 385	com/android/amaze_camera/CameraThread:mEnableTouchAEC	Z
    //   448: ldc_w 267
    //   451: ldc_w 2208
    //   454: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   457: ldc_w 267
    //   460: ldc_w 2210
    //   463: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   466: aload_0
    //   467: getfield 488	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
    //   470: bipush 46
    //   472: invokestatic 871	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
    //   475: aload_0
    //   476: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   479: ldc_w 2212
    //   482: ldc_w 1191
    //   485: invokevirtual 760	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   488: iconst_0
    //   489: putstatic 387	com/android/amaze_camera/CameraThread:mEnableCAF	Z
    //   492: aload_0
    //   493: getfield 421	com/android/amaze_camera/CameraThread:mIsOnlySetResolution	Z
    //   496: ifne +27 -> 523
    //   499: iload_1
    //   500: iconst_1
    //   501: if_icmpne +22 -> 523
    //   504: ldc_w 267
    //   507: ldc_w 2214
    //   510: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   513: aload_0
    //   514: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   517: ldc_w 1191
    //   520: invokevirtual 1194	com/android/amaze_camera/CameraController:setFlashMode	(Ljava/lang/String;)V
    //   523: aload_0
    //   524: getfield 417	com/android/amaze_camera/CameraThread:mMode	I
    //   527: iconst_1
    //   528: if_icmpne +788 -> 1316
    //   531: aload_0
    //   532: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   535: ldc_w 2216
    //   538: invokestatic 1058	com/android/amaze_camera/HTCCameraAdvanceSetting:getPrefenceBoolean	(Landroid/app/Activity;Ljava/lang/String;)Ljava/lang/Boolean;
    //   541: invokevirtual 1063	java/lang/Boolean:booleanValue	()Z
    //   544: iconst_1
    //   545: if_icmpne +20 -> 565
    //   548: aload_0
    //   549: invokevirtual 1248	com/android/amaze_camera/CameraThread:is2ndCamera	()Z
    //   552: ifne +13 -> 565
    //   555: aload_0
    //   556: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   559: invokevirtual 1289	com/android/amaze_camera/HTCCamera:isServiceMode	()Z
    //   562: ifeq +726 -> 1288
    //   565: aload_0
    //   566: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   569: ldc_w 2218
    //   572: iconst_1
    //   573: invokevirtual 1372	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;I)V
    //   576: aload_0
    //   577: getfield 435	com/android/amaze_camera/CameraThread:bSwitchCamera	Z
    //   580: iconst_1
    //   581: if_icmpne +32 -> 613
    //   584: aload_0
    //   585: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   588: ldc_w 2220
    //   591: invokestatic 1058	com/android/amaze_camera/HTCCameraAdvanceSetting:getPrefenceBoolean	(Landroid/app/Activity;Ljava/lang/String;)Ljava/lang/Boolean;
    //   594: invokevirtual 1063	java/lang/Boolean:booleanValue	()Z
    //   597: ifeq +733 -> 1330
    //   600: aload_0
    //   601: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   604: ldc_w 2222
    //   607: ldc_w 2224
    //   610: invokevirtual 760	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   613: aload_0
    //   614: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   617: invokevirtual 765	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
    //   620: aload_0
    //   621: invokespecial 2226	com/android/amaze_camera/CameraThread:loadResolution	()V
    //   624: aload_0
    //   625: getfield 417	com/android/amaze_camera/CameraThread:mMode	I
    //   628: ifne +718 -> 1346
    //   631: aload_0
    //   632: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   635: aload_0
    //   636: getfield 637	com/android/amaze_camera/CameraThread:Camera_Preview_Width	I
    //   639: aload_0
    //   640: getfield 654	com/android/amaze_camera/CameraThread:Camera_Preview_Height	I
    //   643: invokevirtual 2230	com/android/amaze_camera/CameraController:setPreviewSizeParameter	(II)V
    //   646: ldc_w 267
    //   649: new 723	java/lang/StringBuilder
    //   652: dup
    //   653: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   656: ldc_w 2232
    //   659: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   662: aload_0
    //   663: getfield 637	com/android/amaze_camera/CameraThread:Camera_Preview_Width	I
    //   666: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   669: ldc_w 2234
    //   672: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   675: aload_0
    //   676: getfield 654	com/android/amaze_camera/CameraThread:Camera_Preview_Height	I
    //   679: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   682: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   685: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   688: aload_0
    //   689: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   692: aload_0
    //   693: getfield 658	com/android/amaze_camera/CameraThread:Camera_Mode_Width	I
    //   696: aload_0
    //   697: getfield 661	com/android/amaze_camera/CameraThread:Camera_Mode_Height	I
    //   700: invokevirtual 2237	com/android/amaze_camera/CameraController:setPictureSizeParameter	(II)V
    //   703: ldc_w 267
    //   706: new 723	java/lang/StringBuilder
    //   709: dup
    //   710: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   713: ldc_w 2239
    //   716: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   719: aload_0
    //   720: getfield 658	com/android/amaze_camera/CameraThread:Camera_Mode_Width	I
    //   723: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   726: ldc_w 2234
    //   729: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   732: aload_0
    //   733: getfield 661	com/android/amaze_camera/CameraThread:Camera_Mode_Height	I
    //   736: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   739: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   742: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   745: ldc_w 267
    //   748: new 723	java/lang/StringBuilder
    //   751: dup
    //   752: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   755: ldc_w 2241
    //   758: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   761: aload_0
    //   762: getfield 637	com/android/amaze_camera/CameraThread:Camera_Preview_Width	I
    //   765: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   768: ldc_w 2234
    //   771: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   774: aload_0
    //   775: getfield 654	com/android/amaze_camera/CameraThread:Camera_Preview_Height	I
    //   778: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   781: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   784: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   787: aload_0
    //   788: getfield 490	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
    //   791: bipush 7
    //   793: aload_0
    //   794: getfield 637	com/android/amaze_camera/CameraThread:Camera_Preview_Width	I
    //   797: aload_0
    //   798: getfield 654	com/android/amaze_camera/CameraThread:Camera_Preview_Height	I
    //   801: aconst_null
    //   802: invokestatic 882	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
    //   805: aload_0
    //   806: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   809: ldc_w 1695
    //   812: invokevirtual 1378	com/android/amaze_camera/CameraController:getSettingsInfo	(Ljava/lang/String;)Lcom/android/amaze_camera/CameraController$SettingInfo;
    //   815: astore_3
    //   816: aload_3
    //   817: invokevirtual 2179	com/android/amaze_camera/CameraController$SettingInfo:getMin	()I
    //   820: istore 4
    //   822: aload_3
    //   823: invokevirtual 2182	com/android/amaze_camera/CameraController$SettingInfo:getMax	()I
    //   826: pop
    //   827: getstatic 2244	com/android/amaze_camera/HTCCameraAdvanceSetting:mIsInitZoom	Z
    //   830: iconst_1
    //   831: if_icmpne +14 -> 845
    //   834: aload_3
    //   835: invokevirtual 1383	com/android/amaze_camera/CameraController$SettingInfo:getDefault	()I
    //   838: putstatic 1682	com/android/amaze_camera/HTCCameraAdvanceSetting:mZoomValue	I
    //   841: iconst_0
    //   842: putstatic 2244	com/android/amaze_camera/HTCCameraAdvanceSetting:mIsInitZoom	Z
    //   845: aload_0
    //   846: invokevirtual 713	com/android/amaze_camera/CameraThread:is3DMode	()Z
    //   849: istore 6
    //   851: aload_0
    //   852: getfield 435	com/android/amaze_camera/CameraThread:bSwitchCamera	Z
    //   855: ifne +641 -> 1496
    //   858: iload 6
    //   860: ifne +636 -> 1496
    //   863: aload_0
    //   864: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   867: invokevirtual 2247	com/android/amaze_camera/HTCCamera:getFaceNumber	()I
    //   870: ifne +626 -> 1496
    //   873: aload_0
    //   874: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   877: ldc_w 1695
    //   880: getstatic 1682	com/android/amaze_camera/HTCCameraAdvanceSetting:mZoomValue	I
    //   883: invokevirtual 1372	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;I)V
    //   886: aload_0
    //   887: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   890: invokevirtual 765	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
    //   893: aload_0
    //   894: getfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   897: bipush 90
    //   899: invokevirtual 2250	android/hardware/Camera:setDisplayOrientation	(I)V
    //   902: aload_0
    //   903: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   906: invokevirtual 2254	com/android/amaze_camera/HTCCamera:getSurfaceHolder	()Landroid/view/SurfaceHolder;
    //   909: astore 7
    //   911: aload_0
    //   912: getfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   915: aload 7
    //   917: invokevirtual 2258	android/hardware/Camera:setPreviewDisplay	(Landroid/view/SurfaceHolder;)V
    //   920: aload_0
    //   921: getfield 651	com/android/amaze_camera/CameraThread:m_EventManager	Lcom/android/amaze_camera/EventManager;
    //   924: ldc 149
    //   926: invokevirtual 1865	com/android/amaze_camera/EventManager:raiseEvent	(Ljava/lang/String;)V
    //   929: ldc_w 267
    //   932: ldc_w 2260
    //   935: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   938: aload_0
    //   939: getfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   942: invokevirtual 2262	android/hardware/Camera:startPreview	()V
    //   945: ldc_w 267
    //   948: ldc_w 2264
    //   951: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   954: iconst_1
    //   955: istore 10
    //   957: aload_0
    //   958: getfield 421	com/android/amaze_camera/CameraThread:mIsOnlySetResolution	Z
    //   961: ifne +599 -> 1560
    //   964: iload_1
    //   965: iconst_1
    //   966: if_icmpne +594 -> 1560
    //   969: aload_0
    //   970: invokespecial 634	com/android/amaze_camera/CameraThread:loadSettings	()V
    //   973: aload_0
    //   974: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   977: invokevirtual 765	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
    //   980: aload_0
    //   981: invokevirtual 2267	com/android/amaze_camera/CameraThread:start_calculate_fps	()V
    //   984: aload_0
    //   985: iconst_1
    //   986: putfield 419	com/android/amaze_camera/CameraThread:mPreviewing	Z
    //   989: invokestatic 1261	com/android/amaze_camera/DisplayDevice:isDoubleShot	()Z
    //   992: ifeq +15 -> 1007
    //   995: aload_0
    //   996: getfield 490	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
    //   999: bipush 84
    //   1001: iconst_0
    //   1002: iconst_0
    //   1003: aconst_null
    //   1004: invokestatic 882	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
    //   1007: iload 10
    //   1009: ifeq +12 -> 1021
    //   1012: aload_0
    //   1013: getfield 651	com/android/amaze_camera/CameraThread:m_EventManager	Lcom/android/amaze_camera/EventManager;
    //   1016: ldc 146
    //   1018: invokevirtual 1865	com/android/amaze_camera/EventManager:raiseEvent	(Ljava/lang/String;)V
    //   1021: ldc_w 267
    //   1024: ldc_w 2269
    //   1027: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   1030: iconst_0
    //   1031: putstatic 389	com/android/amaze_camera/CameraThread:mTakeFocus	Z
    //   1034: aload_0
    //   1035: getfield 488	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
    //   1038: iconst_2
    //   1039: invokevirtual 1699	com/android/amaze_camera/CameraThread$MainHandler:hasMessages	(I)Z
    //   1042: ifne +44 -> 1086
    //   1045: aload_0
    //   1046: getfield 490	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
    //   1049: bipush 31
    //   1051: invokestatic 896	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
    //   1054: aload_0
    //   1055: getfield 490	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
    //   1058: bipush 35
    //   1060: invokestatic 896	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
    //   1063: aload_0
    //   1064: getfield 490	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
    //   1067: bipush 45
    //   1069: invokestatic 896	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
    //   1072: aload_0
    //   1073: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1076: ifnull +10 -> 1086
    //   1079: aload_0
    //   1080: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1083: invokevirtual 2272	com/android/amaze_camera/HTCCamera:updateFlashMode	()V
    //   1086: aload_0
    //   1087: getfield 417	com/android/amaze_camera/CameraThread:mMode	I
    //   1090: ifne +478 -> 1568
    //   1093: ldc_w 2274
    //   1096: ldc_w 2276
    //   1099: invokestatic 2103	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
    //   1102: ldc_w 267
    //   1105: ldc_w 2278
    //   1108: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   1111: goto -1052 -> 59
    //   1114: aload_0
    //   1115: invokevirtual 713	com/android/amaze_camera/CameraThread:is3DMode	()Z
    //   1118: ifeq +104 -> 1222
    //   1121: invokestatic 2008	com/android/amaze_camera/component/PowerWarningController:isSupported	()Z
    //   1124: ifeq +70 -> 1194
    //   1127: aload_0
    //   1128: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1131: invokevirtual 1184	com/android/amaze_camera/HTCCamera:getFlashRestriction	()Lcom/android/amaze_camera/FlashRestriction;
    //   1134: invokevirtual 2281	com/android/amaze_camera/FlashRestriction:checkLowPower	()Z
    //   1137: ifeq +57 -> 1194
    //   1140: ldc_w 267
    //   1143: ldc_w 2283
    //   1146: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   1149: aload_0
    //   1150: getfield 488	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
    //   1153: ifnull -1094 -> 59
    //   1156: aload_0
    //   1157: getfield 488	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
    //   1160: bipush 59
    //   1162: ldc2_w 1710
    //   1165: invokestatic 1031	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
    //   1168: goto -1109 -> 59
    //   1171: astore 14
    //   1173: ldc_w 267
    //   1176: ldc_w 2285
    //   1179: aload 14
    //   1181: invokestatic 810	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1184: aload_0
    //   1185: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1188: invokevirtual 813	com/android/amaze_camera/HTCCamera:finish	()V
    //   1191: goto -1132 -> 59
    //   1194: aload_0
    //   1195: invokestatic 2288	com/android/amaze_camera/SensorHolder:get3DCamID	()I
    //   1198: invokestatic 2157	android/hardware/Camera:open	(I)Landroid/hardware/Camera;
    //   1201: putfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   1204: aload_0
    //   1205: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1208: ifnull +27 -> 1235
    //   1211: aload_0
    //   1212: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1215: iconst_1
    //   1216: invokevirtual 2160	com/android/amaze_camera/HTCCamera:registerFocusSensor	(Z)V
    //   1219: goto -1039 -> 180
    //   1222: aload_0
    //   1223: invokestatic 2291	com/android/amaze_camera/SensorHolder:getMainCamID	()I
    //   1226: invokestatic 2157	android/hardware/Camera:open	(I)Landroid/hardware/Camera;
    //   1229: putfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   1232: goto -28 -> 1204
    //   1235: ldc_w 267
    //   1238: ldc_w 2293
    //   1241: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   1244: goto -1064 -> 180
    //   1247: ldc_w 267
    //   1250: ldc_w 2295
    //   1253: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   1256: aload_0
    //   1257: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   1260: ldc_w 2191
    //   1263: ldc_w 2297
    //   1266: invokevirtual 760	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   1269: goto -887 -> 382
    //   1272: astore 13
    //   1274: ldc_w 267
    //   1277: ldc_w 2299
    //   1280: aload 13
    //   1282: invokestatic 810	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1285: goto -896 -> 389
    //   1288: aload_0
    //   1289: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1292: invokestatic 1394	com/android/amaze_camera/HTCCameraAdvanceSetting:getIsRecordWithAudio	(Landroid/app/Activity;)Z
    //   1295: ifeq +7 -> 1302
    //   1298: invokestatic 1399	com/android/amaze_camera/SoundEffect:disableSoundEffect	()I
    //   1301: pop
    //   1302: aload_0
    //   1303: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   1306: ldc_w 2218
    //   1309: iconst_2
    //   1310: invokevirtual 1372	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;I)V
    //   1313: goto -737 -> 576
    //   1316: aload_0
    //   1317: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   1320: ldc_w 2218
    //   1323: iconst_0
    //   1324: invokevirtual 1372	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;I)V
    //   1327: goto -751 -> 576
    //   1330: aload_0
    //   1331: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   1334: ldc_w 2222
    //   1337: ldc_w 2301
    //   1340: invokevirtual 760	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   1343: goto -730 -> 613
    //   1346: aload_0
    //   1347: getfield 417	com/android/amaze_camera/CameraThread:mMode	I
    //   1350: iconst_1
    //   1351: if_icmpne -546 -> 805
    //   1354: invokestatic 538	com/android/amaze_camera/MovieModeHandler:getMovieModeHandler	()Lcom/android/amaze_camera/MovieModeHandler;
    //   1357: aload_0
    //   1358: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1361: invokevirtual 2304	com/android/amaze_camera/MovieModeHandler:IsLockMMSVideoInLandscape	(Lcom/android/amaze_camera/HTCCamera;)Z
    //   1364: ifeq +12 -> 1376
    //   1367: aload_0
    //   1368: getfield 490	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
    //   1371: bipush 104
    //   1373: invokestatic 896	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
    //   1376: aload_0
    //   1377: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   1380: aload_0
    //   1381: getfield 664	com/android/amaze_camera/CameraThread:Video_Mode_Width	I
    //   1384: aload_0
    //   1385: getfield 667	com/android/amaze_camera/CameraThread:Video_Mode_Height	I
    //   1388: invokevirtual 2230	com/android/amaze_camera/CameraController:setPreviewSizeParameter	(II)V
    //   1391: ldc_w 267
    //   1394: new 723	java/lang/StringBuilder
    //   1397: dup
    //   1398: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   1401: ldc_w 2306
    //   1404: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1407: aload_0
    //   1408: getfield 664	com/android/amaze_camera/CameraThread:Video_Mode_Width	I
    //   1411: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1414: ldc_w 2234
    //   1417: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1420: aload_0
    //   1421: getfield 667	com/android/amaze_camera/CameraThread:Video_Mode_Height	I
    //   1424: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1427: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1430: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   1433: ldc_w 267
    //   1436: new 723	java/lang/StringBuilder
    //   1439: dup
    //   1440: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   1443: ldc_w 2308
    //   1446: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1449: aload_0
    //   1450: getfield 664	com/android/amaze_camera/CameraThread:Video_Mode_Width	I
    //   1453: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1456: ldc_w 2234
    //   1459: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1462: aload_0
    //   1463: getfield 667	com/android/amaze_camera/CameraThread:Video_Mode_Height	I
    //   1466: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1469: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1472: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   1475: aload_0
    //   1476: getfield 490	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
    //   1479: bipush 7
    //   1481: aload_0
    //   1482: getfield 664	com/android/amaze_camera/CameraThread:Video_Mode_Width	I
    //   1485: aload_0
    //   1486: getfield 667	com/android/amaze_camera/CameraThread:Video_Mode_Height	I
    //   1489: aconst_null
    //   1490: invokestatic 882	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
    //   1493: goto -688 -> 805
    //   1496: aload_0
    //   1497: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   1500: ldc_w 1695
    //   1503: iload 4
    //   1505: invokevirtual 1372	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;I)V
    //   1508: goto -622 -> 886
    //   1511: astore 8
    //   1513: ldc_w 267
    //   1516: ldc_w 2310
    //   1519: aload 8
    //   1521: invokestatic 810	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1524: aload_0
    //   1525: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1528: invokevirtual 813	com/android/amaze_camera/HTCCamera:finish	()V
    //   1531: goto -611 -> 920
    //   1534: astore 9
    //   1536: ldc_w 267
    //   1539: ldc_w 2312
    //   1542: aload 9
    //   1544: invokestatic 810	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1547: aload_0
    //   1548: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1551: invokevirtual 813	com/android/amaze_camera/HTCCamera:finish	()V
    //   1554: iconst_0
    //   1555: istore 10
    //   1557: goto -600 -> 957
    //   1560: aload_0
    //   1561: iconst_0
    //   1562: putfield 421	com/android/amaze_camera/CameraThread:mIsOnlySetResolution	Z
    //   1565: goto -585 -> 980
    //   1568: ldc_w 2274
    //   1571: ldc_w 2314
    //   1574: invokestatic 2103	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
    //   1577: goto -475 -> 1102
    //
    // Exception table:
    //   from	to	target	type
    //   147	215	1171	java/lang/Exception
    //   1114	1168	1171	java/lang/Exception
    //   1194	1244	1171	java/lang/Exception
    //   382	389	1272	java/lang/RuntimeException
    //   911	920	1511	java/io/IOException
    //   929	954	1534	java/lang/Exception
  }

  public final void startScaladoPostProcessing(String paramString)
  {
  }

  public void start_calculate_fps()
  {
  }

  // ERROR //
  void start_video_recording(int paramInt)
  {
    // Byte code:
    //   0: ldc_w 267
    //   3: ldc_w 2318
    //   6: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   9: aload_0
    //   10: getfield 488	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
    //   13: ifnull +37 -> 50
    //   16: aload_0
    //   17: getfield 488	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
    //   20: bipush 47
    //   22: invokevirtual 1699	com/android/amaze_camera/CameraThread$MainHandler:hasMessages	(I)Z
    //   25: ifeq +25 -> 50
    //   28: ldc_w 267
    //   31: ldc_w 2320
    //   34: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   37: aload_0
    //   38: getfield 488	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
    //   41: bipush 47
    //   43: invokestatic 871	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
    //   46: aload_0
    //   47: invokespecial 641	com/android/amaze_camera/CameraThread:stop_video_recording_delay	()V
    //   50: aload_0
    //   51: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   54: ifnull +16 -> 70
    //   57: ldc_w 267
    //   60: ldc_w 2322
    //   63: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   66: aload_0
    //   67: invokespecial 1506	com/android/amaze_camera/CameraThread:releaseMediaRecorder	()V
    //   70: aload_0
    //   71: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   74: ifnonnull +14 -> 88
    //   77: aload_0
    //   78: new 1328	android/media/MediaRecorder
    //   81: dup
    //   82: invokespecial 2323	android/media/MediaRecorder:<init>	()V
    //   85: putfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   88: aload_0
    //   89: getfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   92: ifnonnull +13 -> 105
    //   95: ldc_w 267
    //   98: ldc_w 2325
    //   101: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   104: return
    //   105: aload_0
    //   106: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   109: ifnull +43 -> 152
    //   112: getstatic 999	com/android/amaze_camera/HTCCamera:mFocusMode	I
    //   115: iconst_1
    //   116: if_icmpeq +36 -> 152
    //   119: ldc_w 267
    //   122: ldc_w 2327
    //   125: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   128: aload_0
    //   129: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   132: ldc_w 2212
    //   135: ldc_w 1229
    //   138: invokevirtual 760	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   141: aload_0
    //   142: getfield 494	com/android/amaze_camera/CameraThread:mCamController	Lcom/android/amaze_camera/CameraController;
    //   145: invokevirtual 765	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
    //   148: iconst_1
    //   149: putstatic 387	com/android/amaze_camera/CameraThread:mEnableCAF	Z
    //   152: aload_0
    //   153: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   156: invokevirtual 1147	com/android/amaze_camera/HTCCamera:getAudioManager	()Landroid/media/AudioManager;
    //   159: ldc_w 2329
    //   162: invokevirtual 1445	android/media/AudioManager:setParameters	(Ljava/lang/String;)V
    //   165: aload_0
    //   166: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   169: invokevirtual 2332	com/android/amaze_camera/HTCCamera:isPowerWarning	()Z
    //   172: ifeq +15 -> 187
    //   175: ldc_w 267
    //   178: ldc_w 2334
    //   181: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   184: goto -80 -> 104
    //   187: aload_0
    //   188: iconst_1
    //   189: putfield 415	com/android/amaze_camera/CameraThread:mRecording	Z
    //   192: aload_0
    //   193: getfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   196: invokevirtual 2337	android/hardware/Camera:unlock	()V
    //   199: ldc_w 267
    //   202: ldc_w 2339
    //   205: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   208: aload_0
    //   209: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   212: aload_0
    //   213: getfield 492	com/android/amaze_camera/CameraThread:mCameraDevice	Landroid/hardware/Camera;
    //   216: invokevirtual 2342	android/media/MediaRecorder:setCamera	(Landroid/hardware/Camera;)V
    //   219: aload_0
    //   220: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   223: invokevirtual 705	com/android/amaze_camera/HTCCamera:getFreezeOrientation	()I
    //   226: istore_2
    //   227: iload_2
    //   228: bipush 255
    //   230: if_icmpne +800 -> 1030
    //   233: ldc_w 267
    //   236: ldc_w 707
    //   239: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   242: iconst_0
    //   243: istore_3
    //   244: aload_0
    //   245: invokevirtual 713	com/android/amaze_camera/CameraThread:is3DMode	()Z
    //   248: ifeq +21 -> 269
    //   251: aload_0
    //   252: getfield 435	com/android/amaze_camera/CameraThread:bSwitchCamera	Z
    //   255: ifne +14 -> 269
    //   258: ldc_w 267
    //   261: ldc_w 2344
    //   264: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   267: iconst_0
    //   268: istore_3
    //   269: aload_0
    //   270: iload_3
    //   271: invokestatic 721	com/android/amaze_camera/ImageManager:roundOrientation	(I)I
    //   274: putfield 457	com/android/amaze_camera/CameraThread:mCaptureRotation	I
    //   277: ldc_w 267
    //   280: new 723	java/lang/StringBuilder
    //   283: dup
    //   284: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   287: ldc_w 726
    //   290: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   293: aload_0
    //   294: getfield 457	com/android/amaze_camera/CameraThread:mCaptureRotation	I
    //   297: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   300: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   303: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   306: invokestatic 2347	com/android/amaze_camera/DisplayDevice:supportCamcorderRotate	()Z
    //   309: ifeq +30 -> 339
    //   312: invokestatic 538	com/android/amaze_camera/MovieModeHandler:getMovieModeHandler	()Lcom/android/amaze_camera/MovieModeHandler;
    //   315: aload_0
    //   316: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   319: invokevirtual 2304	com/android/amaze_camera/MovieModeHandler:IsLockMMSVideoInLandscape	(Lcom/android/amaze_camera/HTCCamera;)Z
    //   322: ifne +17 -> 339
    //   325: aload_0
    //   326: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   329: aload_0
    //   330: getfield 457	com/android/amaze_camera/CameraThread:mCaptureRotation	I
    //   333: invokestatic 721	com/android/amaze_camera/ImageManager:roundOrientation	(I)I
    //   336: invokevirtual 2350	android/media/MediaRecorder:setOrientationHint	(I)V
    //   339: aload_0
    //   340: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   343: invokevirtual 2254	com/android/amaze_camera/HTCCamera:getSurfaceHolder	()Landroid/view/SurfaceHolder;
    //   346: astore 4
    //   348: aload_0
    //   349: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   352: aload 4
    //   354: invokeinterface 2356 1 0
    //   359: invokevirtual 2359	android/media/MediaRecorder:setPreviewDisplay	(Landroid/view/Surface;)V
    //   362: aload_0
    //   363: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   366: invokestatic 1394	com/android/amaze_camera/HTCCameraAdvanceSetting:getIsRecordWithAudio	(Landroid/app/Activity;)Z
    //   369: istore 6
    //   371: iload 6
    //   373: ifeq +72 -> 445
    //   376: invokestatic 1272	com/android/amaze_camera/DisplayDevice:supportStereoRecord	()Z
    //   379: ifeq +58 -> 437
    //   382: aload_0
    //   383: getfield 439	com/android/amaze_camera/CameraThread:bIsStereo	Z
    //   386: ifeq +679 -> 1065
    //   389: iconst_1
    //   390: aload_0
    //   391: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   394: invokevirtual 705	com/android/amaze_camera/HTCCamera:getFreezeOrientation	()I
    //   397: invokestatic 2364	com/android/amaze_camera/rotate/OrientationConfig:mapOrientation_Event2UI	(I)I
    //   400: invokestatic 2368	com/android/amaze_camera/SoundEffect:setSoundEffect	(ZI)I
    //   403: pop
    //   404: invokestatic 2371	com/android/amaze_camera/SoundEffect:enableSoundEffect	()I
    //   407: pop
    //   408: ldc_w 267
    //   411: new 723	java/lang/StringBuilder
    //   414: dup
    //   415: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   418: ldc_w 2373
    //   421: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   424: aload_0
    //   425: getfield 439	com/android/amaze_camera/CameraThread:bIsStereo	Z
    //   428: invokevirtual 2061	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   431: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   434: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   437: aload_0
    //   438: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   441: iconst_1
    //   442: invokevirtual 2376	android/media/MediaRecorder:setAudioSource	(I)V
    //   445: aload_0
    //   446: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   449: iconst_1
    //   450: invokevirtual 2379	android/media/MediaRecorder:setVideoSource	(I)V
    //   453: invokestatic 1465	com/android/amaze_camera/DisplayDevice:supportOnlyMP4VideoFormat	()Z
    //   456: ifeq +660 -> 1116
    //   459: aload_0
    //   460: invokespecial 1467	com/android/amaze_camera/CameraThread:isMMSRecording	()Z
    //   463: ifne +653 -> 1116
    //   466: aload_0
    //   467: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   470: iconst_2
    //   471: invokevirtual 2382	android/media/MediaRecorder:setOutputFormat	(I)V
    //   474: new 2384	java/lang/StringBuffer
    //   477: dup
    //   478: bipush 50
    //   480: invokespecial 2385	java/lang/StringBuffer:<init>	(I)V
    //   483: astore 7
    //   485: aload_0
    //   486: invokestatic 2388	com/android/amaze_camera/DCFRuler:getVideoPath	()Ljava/lang/String;
    //   489: putfield 447	com/android/amaze_camera/CameraThread:mfilepath	Ljava/lang/String;
    //   492: aload_0
    //   493: aload_0
    //   494: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   497: aload 7
    //   499: invokestatic 2392	com/android/amaze_camera/DCFRuler:getVideoNameAndNumber	(Landroid/app/Activity;Ljava/lang/StringBuffer;)I
    //   502: putfield 455	com/android/amaze_camera/CameraThread:mNumber	I
    //   505: aload_0
    //   506: aload 7
    //   508: invokevirtual 2393	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   511: putfield 449	com/android/amaze_camera/CameraThread:mfilename	Ljava/lang/String;
    //   514: ldc_w 2395
    //   517: astore 9
    //   519: invokestatic 1465	com/android/amaze_camera/DisplayDevice:supportOnlyMP4VideoFormat	()Z
    //   522: ifeq +15 -> 537
    //   525: aload_0
    //   526: invokespecial 1467	com/android/amaze_camera/CameraThread:isMMSRecording	()Z
    //   529: ifne +8 -> 537
    //   532: ldc_w 2397
    //   535: astore 9
    //   537: aload_0
    //   538: new 723	java/lang/StringBuilder
    //   541: dup
    //   542: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   545: aload_0
    //   546: getfield 449	com/android/amaze_camera/CameraThread:mfilename	Ljava/lang/String;
    //   549: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   552: aload 9
    //   554: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   557: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   560: putfield 449	com/android/amaze_camera/CameraThread:mfilename	Ljava/lang/String;
    //   563: ldc_w 267
    //   566: new 723	java/lang/StringBuilder
    //   569: dup
    //   570: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   573: ldc_w 2399
    //   576: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   579: aload_0
    //   580: getfield 447	com/android/amaze_camera/CameraThread:mfilepath	Ljava/lang/String;
    //   583: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   586: ldc_w 1532
    //   589: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   592: aload_0
    //   593: getfield 449	com/android/amaze_camera/CameraThread:mfilename	Ljava/lang/String;
    //   596: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   599: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   602: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   605: aload_0
    //   606: new 723	java/lang/StringBuilder
    //   609: dup
    //   610: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   613: aload_0
    //   614: getfield 447	com/android/amaze_camera/CameraThread:mfilepath	Ljava/lang/String;
    //   617: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   620: ldc_w 831
    //   623: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   626: aload_0
    //   627: getfield 449	com/android/amaze_camera/CameraThread:mfilename	Ljava/lang/String;
    //   630: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   633: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   636: putfield 445	com/android/amaze_camera/CameraThread:mVideoPath	Ljava/lang/String;
    //   639: aload_0
    //   640: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   643: aload_0
    //   644: getfield 445	com/android/amaze_camera/CameraThread:mVideoPath	Ljava/lang/String;
    //   647: invokevirtual 2402	android/media/MediaRecorder:setOutputFile	(Ljava/lang/String;)V
    //   650: aload_0
    //   651: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   654: invokevirtual 2081	com/android/amaze_camera/HTCCamera:getRecordLimitCheck	()Lcom/android/amaze_camera/RecordLimitCheck;
    //   657: astore 10
    //   659: aload 10
    //   661: ifnull +559 -> 1220
    //   664: aload 10
    //   666: aload_0
    //   667: getfield 445	com/android/amaze_camera/CameraThread:mVideoPath	Ljava/lang/String;
    //   670: invokevirtual 2405	com/android/amaze_camera/RecordLimitCheck:setFilePath	(Ljava/lang/String;)V
    //   673: aload 10
    //   675: invokevirtual 2408	com/android/amaze_camera/RecordLimitCheck:calculateRemainSpace	()V
    //   678: aload 10
    //   680: invokevirtual 2411	com/android/amaze_camera/RecordLimitCheck:getVideoBitrate	()I
    //   683: istore 21
    //   685: ldc_w 267
    //   688: new 723	java/lang/StringBuilder
    //   691: dup
    //   692: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   695: ldc_w 2413
    //   698: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   701: iload 21
    //   703: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   706: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   709: invokestatic 710	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
    //   712: aload_0
    //   713: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   716: iload 21
    //   718: invokevirtual 2416	android/media/MediaRecorder:setVideoEncodingBitRate	(I)V
    //   721: aload_0
    //   722: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   725: sipush 1000
    //   728: aload 10
    //   730: invokevirtual 2419	com/android/amaze_camera/RecordLimitCheck:getMaxSeconds	()I
    //   733: imul
    //   734: invokevirtual 2422	android/media/MediaRecorder:setMaxDuration	(I)V
    //   737: aload 10
    //   739: iconst_1
    //   740: invokevirtual 2425	com/android/amaze_camera/RecordLimitCheck:setUseTimeOut_API	(Z)V
    //   743: aload_0
    //   744: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   747: aload 10
    //   749: invokevirtual 2428	com/android/amaze_camera/RecordLimitCheck:getMaxBytes	()J
    //   752: invokevirtual 2431	android/media/MediaRecorder:setMaxFileSize	(J)V
    //   755: invokestatic 1134	com/android/amaze_camera/DisplayDevice:supportQCT8x50	()Z
    //   758: iconst_1
    //   759: if_icmpeq +10 -> 769
    //   762: invokestatic 2434	com/android/amaze_camera/DisplayDevice:supportQCT7x30	()Z
    //   765: iconst_1
    //   766: if_icmpne +466 -> 1232
    //   769: aload_0
    //   770: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   773: bipush 30
    //   775: invokevirtual 2437	android/media/MediaRecorder:setVideoFrameRate	(I)V
    //   778: aload_0
    //   779: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   782: aload_0
    //   783: getfield 664	com/android/amaze_camera/CameraThread:Video_Mode_Width	I
    //   786: aload_0
    //   787: getfield 667	com/android/amaze_camera/CameraThread:Video_Mode_Height	I
    //   790: invokevirtual 2439	android/media/MediaRecorder:setVideoSize	(II)V
    //   793: ldc_w 267
    //   796: new 723	java/lang/StringBuilder
    //   799: dup
    //   800: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   803: ldc_w 2441
    //   806: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   809: aload_0
    //   810: getfield 664	com/android/amaze_camera/CameraThread:Video_Mode_Width	I
    //   813: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   816: ldc_w 1651
    //   819: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   822: ldc_w 2443
    //   825: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   828: aload_0
    //   829: getfield 667	com/android/amaze_camera/CameraThread:Video_Mode_Height	I
    //   832: invokevirtual 733	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   835: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   838: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   841: aload_0
    //   842: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   845: astore 11
    //   847: aload_0
    //   848: getfield 1087	com/android/amaze_camera/CameraThread:mEncoderType	I
    //   851: pop
    //   852: aload 11
    //   854: aload_0
    //   855: invokespecial 2445	com/android/amaze_camera/CameraThread:getVideoEncoder	()I
    //   858: invokevirtual 2448	android/media/MediaRecorder:setVideoEncoder	(I)V
    //   861: iload 6
    //   863: ifeq +62 -> 925
    //   866: aload_0
    //   867: invokevirtual 2450	com/android/amaze_camera/CameraThread:IsEqualOrAbove720p	()Z
    //   870: ifeq +405 -> 1275
    //   873: invokestatic 2453	com/android/amaze_camera/DisplayDevice:support128kBitrate	()Z
    //   876: iconst_1
    //   877: if_icmpeq +367 -> 1244
    //   880: aload_0
    //   881: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   884: sipush 32000
    //   887: invokevirtual 2456	android/media/MediaRecorder:setAudioEncodingBitRate	(I)V
    //   890: aload_0
    //   891: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   894: sipush 8000
    //   897: invokevirtual 2459	android/media/MediaRecorder:setAudioSamplingRate	(I)V
    //   900: aload_0
    //   901: invokespecial 2461	com/android/amaze_camera/CameraThread:NeedToApplyAMR_NB	()Z
    //   904: iconst_1
    //   905: if_icmpne +469 -> 1374
    //   908: ldc_w 267
    //   911: ldc_w 2463
    //   914: invokestatic 2103	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
    //   917: aload_0
    //   918: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   921: iconst_1
    //   922: invokevirtual 2466	android/media/MediaRecorder:setAudioEncoder	(I)V
    //   925: aload_0
    //   926: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   929: invokevirtual 2467	android/media/MediaRecorder:prepare	()V
    //   932: aload_0
    //   933: invokestatic 858	java/lang/System:currentTimeMillis	()J
    //   936: putfield 461	com/android/amaze_camera/CameraThread:mStartRecordingTime	J
    //   939: aload_0
    //   940: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   943: aload_0
    //   944: getfield 521	com/android/amaze_camera/CameraThread:mInfoListener	Lcom/android/amaze_camera/CameraThread$InfoListener;
    //   947: invokevirtual 1332	android/media/MediaRecorder:setOnInfoListener	(Landroid/media/MediaRecorder$OnInfoListener;)V
    //   950: aload_0
    //   951: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   954: aload_0
    //   955: getfield 524	com/android/amaze_camera/CameraThread:mErrorListener	Lcom/android/amaze_camera/CameraThread$ErrorListener;
    //   958: invokevirtual 2471	android/media/MediaRecorder:setOnErrorListener	(Landroid/media/MediaRecorder$OnErrorListener;)V
    //   961: aload_0
    //   962: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   965: invokevirtual 2472	android/media/MediaRecorder:start	()V
    //   968: ldc_w 2274
    //   971: ldc_w 2474
    //   974: invokestatic 2103	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
    //   977: aload_0
    //   978: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   981: invokevirtual 802	com/android/amaze_camera/HTCCamera:prepareActionScreen	()V
    //   984: aload_0
    //   985: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   988: ldc_w 2476
    //   991: new 1012	java/lang/Integer
    //   994: dup
    //   995: aload_0
    //   996: getfield 455	com/android/amaze_camera/CameraThread:mNumber	I
    //   999: invokespecial 1351	java/lang/Integer:<init>	(I)V
    //   1002: invokevirtual 1352	java/lang/Integer:toString	()Ljava/lang/String;
    //   1005: invokestatic 1304	com/android/amaze_camera/HTCCameraAdvanceSetting:writePreference	(Landroid/app/Activity;Ljava/lang/String;Ljava/lang/Object;)Z
    //   1008: pop
    //   1009: aload_0
    //   1010: getfield 651	com/android/amaze_camera/CameraThread:m_EventManager	Lcom/android/amaze_camera/EventManager;
    //   1013: ldc 158
    //   1015: invokevirtual 1865	com/android/amaze_camera/EventManager:raiseEvent	(Ljava/lang/String;)V
    //   1018: ldc_w 267
    //   1021: ldc_w 2478
    //   1024: invokestatic 679	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
    //   1027: goto -923 -> 104
    //   1030: iload_2
    //   1031: bipush 90
    //   1033: iadd
    //   1034: istore_3
    //   1035: goto -791 -> 244
    //   1038: astore 5
    //   1040: ldc_w 267
    //   1043: ldc_w 2480
    //   1046: aload 5
    //   1048: invokestatic 810	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1051: aload_0
    //   1052: invokespecial 1506	com/android/amaze_camera/CameraThread:releaseMediaRecorder	()V
    //   1055: aload_0
    //   1056: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1059: invokevirtual 813	com/android/amaze_camera/HTCCamera:finish	()V
    //   1062: goto -958 -> 104
    //   1065: iconst_0
    //   1066: aload_0
    //   1067: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1070: invokevirtual 705	com/android/amaze_camera/HTCCamera:getFreezeOrientation	()I
    //   1073: invokestatic 2364	com/android/amaze_camera/rotate/OrientationConfig:mapOrientation_Event2UI	(I)I
    //   1076: invokestatic 2368	com/android/amaze_camera/SoundEffect:setSoundEffect	(ZI)I
    //   1079: pop
    //   1080: invokestatic 2371	com/android/amaze_camera/SoundEffect:enableSoundEffect	()I
    //   1083: pop
    //   1084: ldc_w 267
    //   1087: new 723	java/lang/StringBuilder
    //   1090: dup
    //   1091: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   1094: ldc_w 2373
    //   1097: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1100: aload_0
    //   1101: getfield 439	com/android/amaze_camera/CameraThread:bIsStereo	Z
    //   1104: invokevirtual 2061	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   1107: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1110: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   1113: goto -676 -> 437
    //   1116: aload_0
    //   1117: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   1120: iconst_1
    //   1121: invokevirtual 2382	android/media/MediaRecorder:setOutputFormat	(I)V
    //   1124: goto -650 -> 474
    //   1127: astore 8
    //   1129: ldc_w 267
    //   1132: ldc_w 2482
    //   1135: aload 8
    //   1137: invokestatic 810	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1140: ldc_w 267
    //   1143: ldc_w 2484
    //   1146: aload 8
    //   1148: invokestatic 810	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1151: aload_0
    //   1152: invokespecial 1506	com/android/amaze_camera/CameraThread:releaseMediaRecorder	()V
    //   1155: aload_0
    //   1156: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1159: invokevirtual 813	com/android/amaze_camera/HTCCamera:finish	()V
    //   1162: goto -657 -> 505
    //   1165: astore 18
    //   1167: ldc_w 267
    //   1170: ldc_w 2486
    //   1173: aload 18
    //   1175: invokestatic 810	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1178: goto -457 -> 721
    //   1181: astore 19
    //   1183: ldc_w 267
    //   1186: ldc_w 2488
    //   1189: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   1192: aload 10
    //   1194: iconst_0
    //   1195: invokevirtual 2425	com/android/amaze_camera/RecordLimitCheck:setUseTimeOut_API	(Z)V
    //   1198: goto -455 -> 743
    //   1201: astore 20
    //   1203: ldc_w 267
    //   1206: ldc_w 2490
    //   1209: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   1212: aload 10
    //   1214: invokevirtual 2493	com/android/amaze_camera/RecordLimitCheck:restartCheckFile	()V
    //   1217: goto -462 -> 755
    //   1220: ldc_w 267
    //   1223: ldc_w 2495
    //   1226: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   1229: goto -474 -> 755
    //   1232: aload_0
    //   1233: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   1236: bipush 20
    //   1238: invokevirtual 2437	android/media/MediaRecorder:setVideoFrameRate	(I)V
    //   1241: goto -463 -> 778
    //   1244: aload_0
    //   1245: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   1248: ldc_w 2496
    //   1251: invokevirtual 2456	android/media/MediaRecorder:setAudioEncodingBitRate	(I)V
    //   1254: aload_0
    //   1255: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   1258: ldc_w 2497
    //   1261: invokevirtual 2459	android/media/MediaRecorder:setAudioSamplingRate	(I)V
    //   1264: aload_0
    //   1265: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1268: iconst_0
    //   1269: invokevirtual 2500	com/android/amaze_camera/HTCCamera:setBackgroundDataSetting	(Z)V
    //   1272: goto -372 -> 900
    //   1275: aload_0
    //   1276: invokespecial 2461	com/android/amaze_camera/CameraThread:NeedToApplyAMR_NB	()Z
    //   1279: ifne -379 -> 900
    //   1282: invokestatic 538	com/android/amaze_camera/MovieModeHandler:getMovieModeHandler	()Lcom/android/amaze_camera/MovieModeHandler;
    //   1285: astore 16
    //   1287: aload 16
    //   1289: ifnull +72 -> 1361
    //   1292: aload 16
    //   1294: aload_0
    //   1295: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1298: invokevirtual 542	com/android/amaze_camera/MovieModeHandler:getCurrentResolutionSettingValue	(Lcom/android/amaze_camera/HTCCamera;)Lcom/android/amaze_camera/Resolution;
    //   1301: astore 17
    //   1303: aload 17
    //   1305: getstatic 2503	com/android/amaze_camera/Resolution:Video_QHD	Lcom/android/amaze_camera/Resolution;
    //   1308: invokevirtual 561	com/android/amaze_camera/Resolution:equals	(Ljava/lang/Object;)Z
    //   1311: ifne +14 -> 1325
    //   1314: aload 17
    //   1316: getstatic 2506	com/android/amaze_camera/Resolution:Video_WVGA	Lcom/android/amaze_camera/Resolution;
    //   1319: invokevirtual 561	com/android/amaze_camera/Resolution:equals	(Ljava/lang/Object;)Z
    //   1322: ifeq +26 -> 1348
    //   1325: aload_0
    //   1326: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   1329: ldc_w 2507
    //   1332: invokevirtual 2456	android/media/MediaRecorder:setAudioEncodingBitRate	(I)V
    //   1335: aload_0
    //   1336: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   1339: ldc_w 2497
    //   1342: invokevirtual 2459	android/media/MediaRecorder:setAudioSamplingRate	(I)V
    //   1345: goto -445 -> 900
    //   1348: aload_0
    //   1349: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   1352: ldc_w 2508
    //   1355: invokevirtual 2456	android/media/MediaRecorder:setAudioEncodingBitRate	(I)V
    //   1358: goto -23 -> 1335
    //   1361: aload_0
    //   1362: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   1365: ldc_w 2508
    //   1368: invokevirtual 2456	android/media/MediaRecorder:setAudioEncodingBitRate	(I)V
    //   1371: goto -36 -> 1335
    //   1374: ldc_w 267
    //   1377: ldc_w 2510
    //   1380: invokestatic 2103	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
    //   1383: invokestatic 1272	com/android/amaze_camera/DisplayDevice:supportStereoRecord	()Z
    //   1386: ifeq +29 -> 1415
    //   1389: aload_0
    //   1390: getfield 439	com/android/amaze_camera/CameraThread:bIsStereo	Z
    //   1393: ifeq +22 -> 1415
    //   1396: aload_0
    //   1397: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   1400: iconst_2
    //   1401: invokevirtual 2513	android/media/MediaRecorder:setAudioChannels	(I)V
    //   1404: aload_0
    //   1405: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   1408: iconst_3
    //   1409: invokevirtual 2466	android/media/MediaRecorder:setAudioEncoder	(I)V
    //   1412: goto -487 -> 925
    //   1415: aload_0
    //   1416: getfield 496	com/android/amaze_camera/CameraThread:mRecorder	Landroid/media/MediaRecorder;
    //   1419: iconst_1
    //   1420: invokevirtual 2513	android/media/MediaRecorder:setAudioChannels	(I)V
    //   1423: goto -19 -> 1404
    //   1426: astore 13
    //   1428: ldc_w 267
    //   1431: new 723	java/lang/StringBuilder
    //   1434: dup
    //   1435: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   1438: ldc_w 2515
    //   1441: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1444: aload_0
    //   1445: getfield 447	com/android/amaze_camera/CameraThread:mfilepath	Ljava/lang/String;
    //   1448: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1451: ldc_w 831
    //   1454: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1457: aload_0
    //   1458: getfield 449	com/android/amaze_camera/CameraThread:mfilename	Ljava/lang/String;
    //   1461: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1464: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1467: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   1470: ldc_w 267
    //   1473: ldc_w 2484
    //   1476: aload 13
    //   1478: invokestatic 810	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1481: aload_0
    //   1482: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1485: invokevirtual 1416	com/android/amaze_camera/HTCCamera:restoreBackgrounddataSetting	()V
    //   1488: aload_0
    //   1489: invokespecial 1506	com/android/amaze_camera/CameraThread:releaseMediaRecorder	()V
    //   1492: aload_0
    //   1493: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1496: invokevirtual 813	com/android/amaze_camera/HTCCamera:finish	()V
    //   1499: goto -1395 -> 104
    //   1502: astore 14
    //   1504: ldc_w 267
    //   1507: new 723	java/lang/StringBuilder
    //   1510: dup
    //   1511: invokespecial 724	java/lang/StringBuilder:<init>	()V
    //   1514: ldc_w 2517
    //   1517: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1520: aload_0
    //   1521: getfield 447	com/android/amaze_camera/CameraThread:mfilepath	Ljava/lang/String;
    //   1524: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1527: ldc_w 831
    //   1530: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1533: aload_0
    //   1534: getfield 449	com/android/amaze_camera/CameraThread:mfilename	Ljava/lang/String;
    //   1537: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1540: invokevirtual 737	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1543: invokestatic 1458	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
    //   1546: ldc_w 267
    //   1549: ldc_w 2484
    //   1552: aload 14
    //   1554: invokestatic 810	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1557: aload_0
    //   1558: invokespecial 1506	com/android/amaze_camera/CameraThread:releaseMediaRecorder	()V
    //   1561: aload_0
    //   1562: getfield 423	com/android/amaze_camera/CameraThread:mCameraActivity	Lcom/android/amaze_camera/HTCCamera;
    //   1565: invokevirtual 813	com/android/amaze_camera/HTCCamera:finish	()V
    //   1568: goto -1464 -> 104
    //
    // Exception table:
    //   from	to	target	type
    //   348	362	1038	java/lang/Exception
    //   492	505	1127	java/io/IOException
    //   678	721	1165	java/lang/Exception
    //   721	743	1181	java/lang/RuntimeException
    //   743	755	1201	java/lang/RuntimeException
    //   925	932	1426	java/lang/Exception
    //   939	984	1502	java/lang/Exception
  }

  public final void stopPreview()
  {
    if (Thread.currentThread() == this)
    {
      LOG.V("CameraThread", "stopPreview() - start sync");
      if (this.mPreviewing)
      {
        this.mCameraDevice.stopPreview();
        stop_calculate_fps();
        this.mPreviewing = false;
      }
      LOG.V("CameraThread", "stopPreview() - end sync");
    }
    while (true)
    {
      return;
      if (this.mCameraHandler != null)
      {
        LOG.V("CameraThread", "stopPreview() - start async");
        this.mCameraHandler.post(new Runnable()
        {
          public void run()
          {
            CameraThread.this.stopPreview();
          }
        });
        LOG.V("CameraThread", "stopPreview() - end async");
        continue;
      }
      LOG.E("CameraThread", "Cannot stop preview because there is no handler for camera thread");
    }
  }

  public void stop_calculate_fps()
  {
  }

  public final void storeTakenPicture(byte[] paramArrayOfByte, StorePictureCallback paramStorePictureCallback, DCFInfo paramDCFInfo, boolean paramBoolean)
  {
    LOG.V("CameraThread", "storeTakenPicture() - start");
    threadAccessCheck();
    if (paramArrayOfByte == null)
    {
      LOG.E("CameraThread", "!!!!!!!! jpegData == null !!!!!!!!");
      if (paramDCFInfo == null)
      {
        LOG.V("CameraThread", "No DCF information specified, use default value");
        paramDCFInfo = DCFRuler.DefaultDCFInfo;
      }
      if ((this.mCameraActivity.isRequestSquare()) || (IntentManager.getSaveUri() != null) || (this.mCameraActivity.isUnknownServiceMode()))
        this.mJpegData = paramArrayOfByte;
      if (Storage_Status != 0)
        break label212;
      if (store_image(paramArrayOfByte, paramStorePictureCallback, paramDCFInfo))
      {
        String str = this.mfilepath + "/" + this.mfilename;
        this.m_EventManager.raiseEvent(new PictureSavedEvent("Media.Saved", paramArrayOfByte, paramBoolean, mLastContentUri, str));
      }
    }
    while (true)
    {
      if ((paramBoolean) && (DisplayDevice.supportThumbnailAlbumButton()) && (DisplayDevice.isDoubleShot()))
        MessageHandler.sendEmptyMessage(this.mCameraHandler, 57);
      LOG.V("CameraThread", "storeTakenPicture() - end");
      return;
      LOG.V("CameraThread", "jpegData != null, jpegData.length = " + paramArrayOfByte.length);
      break;
      label212: this.m_EventManager.raiseEvent(new MediaSaveFailedEvent("Media.SaveFailed", 1));
    }
  }

  public final void storeTakenPicture(byte[] paramArrayOfByte, StorePictureCallback paramStorePictureCallback, boolean paramBoolean)
  {
    storeTakenPicture(paramArrayOfByte, paramStorePictureCallback, null, paramBoolean);
  }

  public final void storeTakenPicture(byte[] paramArrayOfByte, DCFInfo paramDCFInfo, boolean paramBoolean)
  {
    storeTakenPicture(paramArrayOfByte, null, paramDCFInfo, paramBoolean);
  }

  public final void storeTakenPicture(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    storeTakenPicture(paramArrayOfByte, null, null, paramBoolean);
  }

  public Resolution updateImageRatio(boolean paramBoolean, ModeHandler paramModeHandler)
  {
    Resolution localResolution1 = paramModeHandler.getCurrentResolutionSettingValue(this.mCameraActivity);
    if ((!DisplayDevice.captrueFullSize()) && ((this.mCameraActivity.isRequestName(IntentManager.RequestName.Contacts)) || (this.mCameraActivity.isRequestName(IntentManager.RequestName.Square))))
      LOG.W("CameraThread", "Contacts request - directly get CONTACT_STYLE resolution");
    for (Object localObject = localResolution1; ; localObject = localResolution1)
    {
      return localObject;
      if ((paramBoolean != localResolution1.isWideRatio()) && ((!is3DMode()) || (is2ndCamera())))
        break;
      LOG.V("CameraThread", "not need to update image ratio");
    }
    LOG.V("CameraThread", "updateImageRatio - Current Resolution: " + localResolution1);
    LinkedList localLinkedList1 = paramModeHandler.getResolutionMenuItem(this.mCameraActivity);
    LinkedList localLinkedList2 = new LinkedList();
    LinkedList localLinkedList3 = new LinkedList();
    TreeSet localTreeSet = new TreeSet();
    localTreeSet.addAll(localLinkedList1);
    Iterator localIterator = localTreeSet.iterator();
    while (localIterator.hasNext())
    {
      ResolutionMenuItem localResolutionMenuItem = (ResolutionMenuItem)localIterator.next();
      if (localResolutionMenuItem.resolution.isWideRatio())
      {
        localLinkedList2.addFirst(localResolutionMenuItem);
        continue;
      }
      localLinkedList3.addFirst(localResolutionMenuItem);
    }
    if (localLinkedList2.size() != localLinkedList3.size())
      LOG.E("CameraThread", "Note: the number of resolution pairs for image ratio are not equal");
    Resolution localResolution2 = null;
    if (paramBoolean)
      for (int j = 0; j < localLinkedList3.size(); j++)
      {
        if (!localResolution1.equals(((ResolutionMenuItem)localLinkedList3.get(j)).resolution))
          continue;
        localResolution2 = ((ResolutionMenuItem)localLinkedList2.get(j)).resolution;
      }
    for (int i = 0; i < localLinkedList2.size(); i++)
    {
      if (!localResolution1.equals(((ResolutionMenuItem)localLinkedList2.get(i)).resolution))
        continue;
      localResolution2 = ((ResolutionMenuItem)localLinkedList3.get(i)).resolution;
    }
    if (localResolution2 == null)
      LOG.E("CameraThread", "update image ratio is fail");
    while (true)
    {
      LOG.V("CameraThread", "updateImageRatio - New Resolution: " + localResolution2);
      localObject = localResolution2;
      break;
      if (this.mCameraActivity.isRequestName(IntentManager.RequestName.Mms) == true)
        continue;
      HTCCameraAdvanceSetting.writePreference(this.mCameraActivity, paramModeHandler.getResolutionSettingString(this.mCameraActivity), localResolution2.getKeyName());
    }
  }

  private final class ErrorListener
    implements MediaRecorder.OnErrorListener
  {
    private ErrorListener()
    {
    }

    public void onError(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2)
    {
      if (paramInt1 == 976)
      {
        LOG.E("CameraThread", "*********** MediaRecorder.CAMERA_STEREO_PROHIBIT");
        CameraThread.this.setRecPowerWarning(true);
        CameraThread.access$902(CameraThread.this, false);
        MessageHandler.removeMessages(CameraThread.this.mUIHandler, 3);
        MessageHandler.removeMessages(CameraThread.this.mCameraHandler, 22);
        MessageHandler.sendEmptyMessage(CameraThread.this.mUIHandler, 82);
        CameraThread.this.m_EventManager.raiseEvent("PowerRecWarning.Received");
      }
      while (true)
      {
        return;
        LOG.E("CameraThread", "*********** Other Errors");
      }
    }
  }

  private final class InfoListener
    implements MediaRecorder.OnInfoListener
  {
    private InfoListener()
    {
    }

    public void onInfo(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2)
    {
      if (paramInt1 == 800)
      {
        LOG.W("CameraThread", "*********** MEDIA_RECORDER_INFO_MAX_DURATION_REACHED");
        if (!CameraThread.this.mRecording)
          LOG.W("CameraThread", "mRecording = false, do nothing");
      }
      while (true)
      {
        return;
        RecordLimitCheck localRecordLimitCheck2 = CameraThread.this.mCameraActivity.getRecordLimitCheck();
        if (localRecordLimitCheck2 == null)
        {
          LOG.W("CameraThread", "mRecordLimitCheck = null, do nothing");
          continue;
        }
        localRecordLimitCheck2.setTimeOut_API();
        switch (localRecordLimitCheck2.getLimitState())
        {
        default:
          LOG.E("CameraThread", "InfoListener, should not enter this state !!!!!!!!!!!!!!!!");
          break;
        case 1:
        case 2:
          LOG.W("CameraThread", "InfoListener, reach file size limit - do nothing");
          break;
        case 3:
          LOG.W("CameraThread", "Block Capture UI - InfoListener, reach time limit to stop recorder");
          CameraThread.this.mCameraActivity.setBlockCaptureUI(true);
          MessageHandler.sendEmptyMessage(CameraThread.this.mUIHandler, 4);
          MessageHandler.sendObtainMessage(CameraThread.this.mCameraHandler, 10, 0, 0, null);
          break;
        case 4:
          LOG.W("CameraThread", "InfoListener, api reach time limit - wait ui reach time limit");
          MessageHandler.removeMessages(CameraThread.this.mCameraHandler, 22);
          continue;
          if (paramInt1 != 801)
            continue;
          LOG.W("CameraThread", "*********** MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED");
          if (!CameraThread.this.mRecording)
          {
            LOG.W("CameraThread", "mRecording = false, do nothing");
            continue;
          }
          RecordLimitCheck localRecordLimitCheck1 = CameraThread.this.mCameraActivity.getRecordLimitCheck();
          if (localRecordLimitCheck1 == null)
          {
            LOG.W("CameraThread", "mRecordLimitCheck = null, do nothing");
            continue;
          }
          int i = localRecordLimitCheck1.getLimitState();
          if ((i == 1) || (i == 2))
          {
            LOG.W("CameraThread", "InfoListener, reach file size limit - do nothing");
            continue;
          }
          localRecordLimitCheck1.setFileSizelimit_API(false);
          switch (localRecordLimitCheck1.getLimitState())
          {
          case 2:
          default:
            LOG.E("CameraThread", "InfoListener, should not enter this state !!!!!!!!!!!!!!!!");
            break;
          case 5:
            MessageHandler.sendObtainMessage(CameraThread.this.mUIHandler, 37, 2131361843, 0, null);
            LOG.W("CameraThread", "InfoListener, reach FATA32 file 'system file size limitation");
          case 1:
            LOG.W("CameraThread", "Block Capture UI - InfoListener, reach max file size to stop recorder");
            CameraThread.this.mCameraActivity.setBlockCaptureUI(true);
            MessageHandler.sendEmptyMessage(CameraThread.this.mUIHandler, 4);
            MessageHandler.removeMessages(CameraThread.this.mCameraHandler, 22);
            MessageHandler.sendObtainMessage(CameraThread.this.mCameraHandler, 10, 0, 0, null);
            break;
          case 3:
            if (!localRecordLimitCheck1.getUseTimeOut_API())
            {
              LOG.W("CameraThread", "InfoListener, ui reach time limit, no api - do nothing");
              continue;
            }
            localRecordLimitCheck1.setFileSizelimit_API(true);
            LOG.W("CameraThread", "Block Capture UI - InfoListener, reach max file size to stop recorder");
            CameraThread.this.mCameraActivity.setBlockCaptureUI(true);
            MessageHandler.removeMessages(CameraThread.this.mUIHandler, 3);
            MessageHandler.removeMessages(CameraThread.this.mCameraHandler, 22);
            MessageHandler.sendObtainMessage(CameraThread.this.mCameraHandler, 10, 0, 0, null);
            break;
          case 4:
            LOG.W("CameraThread", "InfoListener, api reach time limit, do nothing");
          }
        }
      }
    }
  }

  private final class OneShotPreviewCallback
    implements Camera.PreviewCallback
  {
    private OneShotPreviewCallback()
    {
    }

    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
    {
      new CameraThread.DecodeThread(CameraThread.this, paramArrayOfByte, CameraThread.this.Camera_Preview_Width, CameraThread.this.Camera_Preview_Height).start();
      MessageHandler.sendObtainMessage(CameraThread.this.mCameraHandler, 4, 0, 0, null);
    }
  }

  class DecodeThread extends Thread
  {
    byte[] data = null;
    int height;
    int width;

    DecodeThread(byte[] paramInt1, int paramInt2, int arg4)
    {
      this.data = paramInt1;
      this.width = paramInt2;
      int i;
      this.height = i;
    }

    public void run()
    {
      int[] arrayOfInt = new int[this.width * this.height];
      this.data = null;
      synchronized (CameraThread.this.mSyncObject)
      {
        CameraThread.this.mOneShotBitmap = Bitmap.createBitmap(arrayOfInt, this.width, this.height, Bitmap.Config.ARGB_8888);
        LOG.V("CameraThread", "mOneShotBitmap = " + CameraThread.this.mOneShotBitmap);
        CameraThread.this.mSyncObject.notifyAll();
        LOG.W("CameraThread", "mOneShotBitmap != null - notifyAll()");
        return;
      }
    }
  }

  private final class PreviewCallback
    implements Camera.PreviewCallback
  {
    private PreviewCallback()
    {
    }

    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
    {
      CameraThread.access$2808(CameraThread.this);
    }
  }

  private final class HtcCallback
    implements Camera.HtcCallback
  {
    private HtcCallback()
    {
    }

    public void OnReceive(int paramInt1, int paramInt2, int paramInt3, Camera paramCamera)
    {
      LOG.W("CameraThread", "got HtcCallback !!! , callbackType is " + paramInt1);
      switch (paramInt1)
      {
      default:
        LOG.E("CameraThread", "Unknown HtcCallbackType !!!");
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      }
      while (true)
      {
        return;
        CameraThread.this.m_EventManager.raiseEvent(new HTCCallbackEvent("HTCCallback.SmileChanged", 0, paramInt2, paramInt3));
        continue;
        CameraThread.this.m_EventManager.raiseEvent(new HTCCallbackEvent("HTCCallback.AutoSmileCapture", 1, paramInt2, paramInt3));
        MessageHandler.sendEmptyMessage(CameraThread.this.mUIHandler, 83);
        continue;
        CameraThread.this.m_EventManager.raiseEvent(new HTCCallbackEvent("HTCCallback.BlinkChanged", 2, paramInt2, paramInt3));
        continue;
        CameraThread.this.m_EventManager.raiseEvent(new HTCCallbackEvent("HTCCallback.BlinkOffChanged", 3, paramInt2, paramInt3));
        continue;
        CameraThread.this.m_EventManager.raiseEvent(new HTCCallbackEvent("HTCCallback.LowLightChanged", 4, paramInt2, paramInt3));
        continue;
        CameraThread.this.m_EventManager.raiseEvent(new HTCCallbackEvent("HTCCallback.MarcoFocusChanged", 5, paramInt2, paramInt3));
      }
    }
  }

  private final class ErrorCallback
    implements Camera.ErrorCallback
  {
    private ErrorCallback()
    {
    }

    public void onError(int paramInt, Camera paramCamera)
    {
      LOG.E("CameraThread", "got ErrorCallback !!!");
      if (paramInt == 100)
        LOG.E("CameraThread", "error = Camera.CAMERA_ERROR_SERVER_DIED");
      while (true)
      {
        LOG.W("CameraThread", "got ErrorCallback - mCaptureState = CAPTURE_STATE_ERROR");
        CameraThread.access$102(CameraThread.this, 4);
        if (CameraThread.this.mCameraActivity != null)
        {
          LOG.E("CameraThread", "got ErrorCallback, Camera app finished");
          CameraThread.this.mCameraActivity.finish();
        }
        while (true)
        {
          return;
          if (paramInt != 976)
            break;
          LOG.E("CameraThread", "error = Camera.CAMERA_STEREO_PROHIBIT");
          CameraThread.access$102(CameraThread.this, 4);
          CameraThread.access$902(CameraThread.this, false);
          CameraThread.mTakeFocus = false;
          CameraThread.this.m_EventManager.raiseEvent("PowerWarning.Received");
        }
        if (paramInt == 1)
        {
          LOG.E("CameraThread", "error = Camera.CAMERA_ERROR_UNKNOWN");
          continue;
        }
        LOG.E("CameraThread", "unknown error");
      }
    }
  }

  private final class AutoFocusCallback
    implements Camera.AutoFocusCallback
  {
    private AutoFocusCallback()
    {
    }

    public void onAutoFocus(boolean paramBoolean, Camera paramCamera)
    {
      TIME.AutoFocusCallback.End();
      TIME.ReadyTakePicture.Start();
      LOG.V("CameraThread", "got AutoFocusCallback...");
      LOG.W("CameraThread", "got AutoFocusCallback, mTakeFocus = false");
      CameraThread.mTakeFocus = false;
      CameraThread.this.handleAutoFocus(paramBoolean);
      LOG.V("CameraThread", "AutoFocusCallback end");
    }
  }

  static class ScaladoThread extends Thread
  {
    static String TAG = "ScaladoThread";
    String mCmd;

    ScaladoThread(String paramString)
    {
      this.mCmd = paramString;
    }

    public void run()
    {
      LOG.V(TAG, "Start Scalado Thread cmd=" + this.mCmd);
      try
      {
        Runtime.getRuntime().exec(this.mCmd).waitFor();
        LOG.V(TAG, "Exit Scalado Thread");
        return;
      }
      catch (Exception localException)
      {
        while (true)
          LOG.E(TAG, "Exception", localException);
      }
    }
  }

  private class MainHandler extends Handler
  {
    private MainHandler()
    {
    }

    // ERROR //
    public void handleMessage(android.os.Message paramMessage)
    {
      // Byte code:
      //   0: aload_1
      //   1: getfield 36	android/os/Message:what	I
      //   4: tableswitch	default:+300 -> 304, 0:+548->552, 1:+2177->2181, 2:+2286->2290, 3:+812->816, 4:+1231->1235, 5:+1691->1695, 6:+1813->1817, 7:+1937->1941, 8:+2489->2493, 9:+2591->2595, 10:+2936->2940, 11:+3154->3158, 12:+3257->3261, 13:+300->304, 14:+300->304, 15:+301->305, 16:+3394->3398, 17:+3468->3472, 18:+3711->3715, 19:+3768->3772, 20:+3785->3789, 21:+3930->3934, 22:+4078->4082, 23:+300->304, 24:+2264->2268, 25:+4345->4349, 26:+1174->1178, 27:+300->304, 28:+4403->4407, 29:+5407->5411, 30:+5802->5806, 31:+6123->6127, 32:+6344->6348, 33:+6498->6502, 34:+6407->6411, 35:+6589->6593, 36:+6680->6684, 37:+6784->6788, 38:+6883->6887, 39:+6987->6991, 40:+7355->7359, 41:+7430->7434, 42:+7525->7529, 43:+7682->7686, 44:+7571->7575, 45:+1910->1914, 46:+6253->6257, 47:+7838->7842, 48:+7848->7852, 49:+7087->7091, 50:+7160->7164, 51:+7251->7255, 52:+7960->7964, 53:+8029->8033, 54:+2862->2866, 55:+3546->3550, 56:+3659->3663, 57:+4463->4467, 58:+8138->8142, 59:+8454->8458, 60:+8224->8228, 61:+8331->8335, 62:+300->304, 63:+300->304, 64:+300->304, 65:+300->304, 66:+300->304, 67:+300->304, 68:+300->304, 69:+300->304, 70:+8480->8484
      //   305: ldc 38
      //   307: new 40	java/lang/StringBuilder
      //   310: dup
      //   311: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   314: ldc 43
      //   316: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   319: aload_0
      //   320: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   323: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   326: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   329: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   332: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   335: aload_0
      //   336: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   339: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   342: ifnonnull +24 -> 366
      //   345: ldc 38
      //   347: ldc 69
      //   349: invokestatic 72	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
      //   352: aload_0
      //   353: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   356: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   359: iconst_0
      //   360: invokevirtual 82	com/android/amaze_camera/HTCCamera:setBlockCaptureUI	(Z)V
      //   363: goto -59 -> 304
      //   366: aload_1
      //   367: getfield 85	android/os/Message:arg1	I
      //   370: istore 164
      //   372: aload_1
      //   373: getfield 88	android/os/Message:arg2	I
      //   376: istore 165
      //   378: ldc 38
      //   380: new 40	java/lang/StringBuilder
      //   383: dup
      //   384: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   387: ldc 90
      //   389: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   392: iload 164
      //   394: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   397: ldc 92
      //   399: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   402: iload 165
      //   404: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   407: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   410: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   413: aload_0
      //   414: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   417: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   420: ifne +224 -> 644
      //   423: aload_0
      //   424: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   427: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   430: ldc 94
      //   432: invokestatic 100	com/android/amaze_camera/HTCCameraAdvanceSetting:getPrefenceBoolean	(Landroid/app/Activity;Ljava/lang/String;)Ljava/lang/Boolean;
      //   435: invokevirtual 106	java/lang/Boolean:booleanValue	()Z
      //   438: ifeq +181 -> 619
      //   441: aload_0
      //   442: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   445: getstatic 111	com/android/amaze_camera/DisplayDevice:DEFALULT_CAMERA_PREVIEW_WITDH_FOR_3X	I
      //   448: invokestatic 115	com/android/amaze_camera/CameraThread:access$302	(Lcom/android/amaze_camera/CameraThread;I)I
      //   451: pop
      //   452: aload_0
      //   453: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   456: getstatic 118	com/android/amaze_camera/DisplayDevice:DEFALULT_CAMERA_PREVIEW_HEIGHT_FOR_3X	I
      //   459: invokestatic 121	com/android/amaze_camera/CameraThread:access$402	(Lcom/android/amaze_camera/CameraThread;I)I
      //   462: pop
      //   463: aload_0
      //   464: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   467: iload 164
      //   469: invokestatic 124	com/android/amaze_camera/CameraThread:access$502	(Lcom/android/amaze_camera/CameraThread;I)I
      //   472: pop
      //   473: aload_0
      //   474: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   477: iload 165
      //   479: invokestatic 127	com/android/amaze_camera/CameraThread:access$602	(Lcom/android/amaze_camera/CameraThread;I)I
      //   482: pop
      //   483: aload_0
      //   484: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   487: invokestatic 131	com/android/amaze_camera/CameraThread:access$900	(Lcom/android/amaze_camera/CameraThread;)Z
      //   490: iconst_1
      //   491: if_icmpne +22 -> 513
      //   494: aload_0
      //   495: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   498: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   501: invokevirtual 136	android/hardware/Camera:stopPreview	()V
      //   504: aload_0
      //   505: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   508: iconst_0
      //   509: invokestatic 140	com/android/amaze_camera/CameraThread:access$902	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   512: pop
      //   513: aload_0
      //   514: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   517: iconst_1
      //   518: invokestatic 143	com/android/amaze_camera/CameraThread:access$1002	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   521: pop
      //   522: ldc 38
      //   524: new 40	java/lang/StringBuilder
      //   527: dup
      //   528: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   531: ldc 145
      //   533: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   536: aload_0
      //   537: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   540: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   543: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   546: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   549: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   552: ldc 38
      //   554: new 40	java/lang/StringBuilder
      //   557: dup
      //   558: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   561: ldc 147
      //   563: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   566: aload_0
      //   567: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   570: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   573: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   576: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   579: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   582: aload_1
      //   583: getfield 85	android/os/Message:arg1	I
      //   586: istore 163
      //   588: aload_0
      //   589: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   592: invokestatic 153	com/android/amaze_camera/CameraThread:access$1100	(Lcom/android/amaze_camera/CameraThread;)Z
      //   595: ifne +83 -> 678
      //   598: aload_0
      //   599: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   602: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   605: iconst_0
      //   606: iload 163
      //   608: iconst_0
      //   609: aconst_null
      //   610: ldc2_w 158
      //   613: invokestatic 165	com/android/amaze_camera/MessageHandler:sendObtainMessageDelayed	(Landroid/os/Handler;IIILjava/lang/Object;J)V
      //   616: goto -312 -> 304
      //   619: aload_0
      //   620: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   623: getstatic 168	com/android/amaze_camera/DisplayDevice:CAMERA_PREVIEW_WIDTH_FOR_4x3	I
      //   626: invokestatic 115	com/android/amaze_camera/CameraThread:access$302	(Lcom/android/amaze_camera/CameraThread;I)I
      //   629: pop
      //   630: aload_0
      //   631: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   634: getstatic 171	com/android/amaze_camera/DisplayDevice:CAMERA_PREVIEW_HEIGHT_FOR_4x3	I
      //   637: invokestatic 121	com/android/amaze_camera/CameraThread:access$402	(Lcom/android/amaze_camera/CameraThread;I)I
      //   640: pop
      //   641: goto -178 -> 463
      //   644: aload_0
      //   645: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   648: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   651: iconst_1
      //   652: if_icmpne -169 -> 483
      //   655: aload_0
      //   656: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   659: iload 164
      //   661: invokestatic 174	com/android/amaze_camera/CameraThread:access$702	(Lcom/android/amaze_camera/CameraThread;I)I
      //   664: pop
      //   665: aload_0
      //   666: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   669: iload 165
      //   671: invokestatic 177	com/android/amaze_camera/CameraThread:access$802	(Lcom/android/amaze_camera/CameraThread;I)I
      //   674: pop
      //   675: goto -192 -> 483
      //   678: aload_0
      //   679: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   682: invokevirtual 180	com/android/amaze_camera/CameraThread:isLastCameraClosed	()Z
      //   685: ifne +24 -> 709
      //   688: aload_0
      //   689: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   692: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   695: iconst_0
      //   696: iload 163
      //   698: iconst_0
      //   699: aconst_null
      //   700: ldc2_w 181
      //   703: invokestatic 165	com/android/amaze_camera/MessageHandler:sendObtainMessageDelayed	(Landroid/os/Handler;IIILjava/lang/Object;J)V
      //   706: goto -402 -> 304
      //   709: aload_0
      //   710: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   713: iload 163
      //   715: invokevirtual 186	com/android/amaze_camera/CameraThread:startPreview	(I)V
      //   718: aload_1
      //   719: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   722: ifnull +28 -> 750
      //   725: aload_1
      //   726: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   729: checkcast 192	java/lang/String
      //   732: invokestatic 196	java/lang/Boolean:parseBoolean	(Ljava/lang/String;)Z
      //   735: ifeq +15 -> 750
      //   738: aload_0
      //   739: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   742: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   745: bipush 67
      //   747: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   750: aload_0
      //   751: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   754: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   757: iconst_0
      //   758: invokevirtual 207	com/android/amaze_camera/HTCCamera:setBlock3DSwitch	(Z)V
      //   761: invokestatic 210	com/android/amaze_camera/DisplayDevice:support3DCamera	()Z
      //   764: ifeq +42 -> 806
      //   767: invokestatic 213	com/android/amaze_camera/DisplayDevice:support3DHWSwitch	()Z
      //   770: ifeq +36 -> 806
      //   773: aload_0
      //   774: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   777: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   780: bipush 73
      //   782: ldc2_w 214
      //   785: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   788: aload_0
      //   789: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   792: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   795: bipush 72
      //   797: iconst_1
      //   798: iconst_0
      //   799: aconst_null
      //   800: ldc2_w 220
      //   803: invokestatic 165	com/android/amaze_camera/MessageHandler:sendObtainMessageDelayed	(Landroid/os/Handler;IIILjava/lang/Object;J)V
      //   806: ldc 38
      //   808: ldc 223
      //   810: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   813: goto -509 -> 304
      //   816: ldc 38
      //   818: ldc 225
      //   820: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   823: aload_0
      //   824: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   827: invokestatic 228	com/android/amaze_camera/CameraThread:access$1200	(Lcom/android/amaze_camera/CameraThread;)V
      //   830: aload_0
      //   831: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   834: invokestatic 231	com/android/amaze_camera/CameraThread:access$1300	(Lcom/android/amaze_camera/CameraThread;)Z
      //   837: iconst_1
      //   838: if_icmpne +84 -> 922
      //   841: ldc 38
      //   843: ldc 233
      //   845: invokestatic 72	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
      //   848: ldc 38
      //   850: ldc 235
      //   852: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   855: aload_0
      //   856: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   859: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   862: bipush 52
      //   864: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   867: aload_0
      //   868: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   871: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   874: bipush 63
      //   876: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   879: aload_0
      //   880: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   883: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   886: bipush 35
      //   888: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   891: ldc 38
      //   893: ldc 237
      //   895: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   898: aload_0
      //   899: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   902: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   905: iconst_0
      //   906: invokevirtual 240	com/android/amaze_camera/HTCCamera:setFocusingState	(I)V
      //   909: aload_0
      //   910: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   913: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   916: invokevirtual 243	com/android/amaze_camera/HTCCamera:resetAutoCaptureTask	()V
      //   919: goto -615 -> 304
      //   922: ldc 38
      //   924: ldc 245
      //   926: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   929: aload_0
      //   930: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   933: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   936: iconst_1
      //   937: if_icmpne +84 -> 1021
      //   940: ldc 38
      //   942: ldc 247
      //   944: invokestatic 72	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
      //   947: ldc 38
      //   949: ldc 249
      //   951: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   954: aload_0
      //   955: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   958: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   961: bipush 52
      //   963: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   966: aload_0
      //   967: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   970: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   973: bipush 63
      //   975: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   978: aload_0
      //   979: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   982: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   985: bipush 35
      //   987: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   990: ldc 38
      //   992: ldc 251
      //   994: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   997: aload_0
      //   998: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1001: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   1004: iconst_0
      //   1005: invokevirtual 240	com/android/amaze_camera/HTCCamera:setFocusingState	(I)V
      //   1008: aload_0
      //   1009: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1012: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   1015: invokevirtual 243	com/android/amaze_camera/HTCCamera:resetAutoCaptureTask	()V
      //   1018: goto -714 -> 304
      //   1021: aload_0
      //   1022: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1025: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   1028: invokevirtual 255	com/android/amaze_camera/HTCCamera:getFocusingState	()I
      //   1031: iconst_2
      //   1032: if_icmpne +48 -> 1080
      //   1035: ldc 38
      //   1037: ldc_w 257
      //   1040: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   1043: aload_0
      //   1044: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1047: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   1050: iconst_1
      //   1051: invokevirtual 82	com/android/amaze_camera/HTCCamera:setBlockCaptureUI	(Z)V
      //   1054: aload_0
      //   1055: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1058: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   1061: bipush 34
      //   1063: iconst_0
      //   1064: iconst_0
      //   1065: aconst_null
      //   1066: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   1069: ldc 38
      //   1071: ldc_w 263
      //   1074: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1077: goto -773 -> 304
      //   1080: aload_0
      //   1081: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1084: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   1087: invokestatic 267	com/android/amaze_camera/HTCCameraAdvanceSetting:getSelfTimer	(Landroid/app/Activity;)I
      //   1090: istore 162
      //   1092: iload 162
      //   1094: ifle +47 -> 1141
      //   1097: ldc 38
      //   1099: new 40	java/lang/StringBuilder
      //   1102: dup
      //   1103: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   1106: ldc_w 269
      //   1109: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1112: iload 162
      //   1114: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   1117: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1120: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1123: aload_0
      //   1124: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1127: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   1130: iconst_5
      //   1131: iload 162
      //   1133: iconst_0
      //   1134: aconst_null
      //   1135: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   1138: goto -69 -> 1069
      //   1141: ldc 38
      //   1143: ldc_w 271
      //   1146: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   1149: aload_0
      //   1150: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1153: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   1156: iconst_1
      //   1157: invokevirtual 82	com/android/amaze_camera/HTCCamera:setBlockCaptureUI	(Z)V
      //   1160: aload_0
      //   1161: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1164: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   1167: bipush 7
      //   1169: iconst_0
      //   1170: iconst_0
      //   1171: aconst_null
      //   1172: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   1175: goto -106 -> 1069
      //   1178: ldc 38
      //   1180: ldc_w 273
      //   1183: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1186: aload_0
      //   1187: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1190: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   1193: ifnull +31 -> 1224
      //   1196: aload_0
      //   1197: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1200: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   1203: aload_0
      //   1204: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1207: invokestatic 277	com/android/amaze_camera/CameraThread:access$1400	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraThread$OneShotPreviewCallback;
      //   1210: invokevirtual 281	android/hardware/Camera:setOneShotPreviewCallback	(Landroid/hardware/Camera$PreviewCallback;)V
      //   1213: ldc 38
      //   1215: ldc_w 283
      //   1218: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1221: goto -917 -> 304
      //   1224: ldc 38
      //   1226: ldc_w 285
      //   1229: invokestatic 72	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
      //   1232: goto -19 -> 1213
      //   1235: ldc 38
      //   1237: ldc_w 287
      //   1240: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1243: aload_1
      //   1244: getfield 85	android/os/Message:arg1	I
      //   1247: istore 156
      //   1249: aload_1
      //   1250: getfield 88	android/os/Message:arg2	I
      //   1253: istore 157
      //   1255: ldc 38
      //   1257: new 40	java/lang/StringBuilder
      //   1260: dup
      //   1261: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   1264: ldc_w 289
      //   1267: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1270: iload 156
      //   1272: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   1275: ldc_w 291
      //   1278: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1281: iload 157
      //   1283: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   1286: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1289: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   1292: iload 156
      //   1294: iflt +8 -> 1302
      //   1297: iload 157
      //   1299: ifge +11 -> 1310
      //   1302: ldc 38
      //   1304: ldc_w 293
      //   1307: invokestatic 72	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
      //   1310: aload_0
      //   1311: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1314: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   1317: ifnull +296 -> 1613
      //   1320: aload_0
      //   1321: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1324: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   1327: ifnull +286 -> 1613
      //   1330: invokestatic 300	com/android/amaze_camera/DisplayDevice:hasAutoFocus	()Z
      //   1333: ifeq +108 -> 1441
      //   1336: aload_0
      //   1337: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1340: invokestatic 303	com/android/amaze_camera/CameraThread:access$1600	(Lcom/android/amaze_camera/CameraThread;)Z
      //   1343: ifeq +281 -> 1624
      //   1346: aload_0
      //   1347: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1350: invokestatic 306	com/android/amaze_camera/CameraThread:access$1700	(Lcom/android/amaze_camera/CameraThread;)Z
      //   1353: ifne +271 -> 1624
      //   1356: getstatic 309	com/android/amaze_camera/HTCCamera:mFocusMode	I
      //   1359: iconst_1
      //   1360: if_icmpeq +15 -> 1375
      //   1363: invokestatic 312	com/android/amaze_camera/DisplayDevice:forceFocusSound	()Z
      //   1366: ifne +9 -> 1375
      //   1369: getstatic 316	com/android/amaze_camera/HTCCamera:bFocusFromPress	Z
      //   1372: ifeq +252 -> 1624
      //   1375: ldc 38
      //   1377: ldc_w 318
      //   1380: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1383: aload_0
      //   1384: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1387: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   1390: bipush 21
      //   1392: ldc_w 319
      //   1395: iconst_0
      //   1396: aconst_null
      //   1397: ldc2_w 320
      //   1400: invokestatic 165	com/android/amaze_camera/MessageHandler:sendObtainMessageDelayed	(Landroid/os/Handler;IIILjava/lang/Object;J)V
      //   1403: aload_0
      //   1404: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1407: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   1410: ifeq +10 -> 1420
      //   1413: getstatic 309	com/android/amaze_camera/HTCCamera:mFocusMode	I
      //   1416: iconst_1
      //   1417: if_icmpne +251 -> 1668
      //   1420: aload_0
      //   1421: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1424: iconst_1
      //   1425: invokestatic 324	com/android/amaze_camera/CameraThread:access$1802	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   1428: pop
      //   1429: aload_0
      //   1430: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1433: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   1436: bipush 24
      //   1438: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   1441: new 40	java/lang/StringBuilder
      //   1444: dup
      //   1445: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   1448: iload 156
      //   1450: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   1453: ldc_w 326
      //   1456: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1459: iload 157
      //   1461: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   1464: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1467: astore 158
      //   1469: aload_0
      //   1470: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1473: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   1476: ldc_w 328
      //   1479: aload 158
      //   1481: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   1484: getstatic 309	com/android/amaze_camera/HTCCamera:mFocusMode	I
      //   1487: ifeq +66 -> 1553
      //   1490: aload_0
      //   1491: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1494: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   1497: ldc_w 335
      //   1500: ldc_w 337
      //   1503: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   1506: iconst_1
      //   1507: putstatic 340	com/android/amaze_camera/CameraThread:mEnableTouchAEC	Z
      //   1510: ldc 38
      //   1512: ldc_w 342
      //   1515: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   1518: getstatic 309	com/android/amaze_camera/HTCCamera:mFocusMode	I
      //   1521: iconst_1
      //   1522: if_icmpne +31 -> 1553
      //   1525: ldc 38
      //   1527: ldc_w 344
      //   1530: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   1533: aload_0
      //   1534: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1537: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   1540: ldc_w 346
      //   1543: ldc_w 348
      //   1546: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   1549: iconst_0
      //   1550: putstatic 351	com/android/amaze_camera/CameraThread:mEnableCAF	Z
      //   1553: aload_0
      //   1554: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1557: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   1560: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   1563: getstatic 357	com/android/amaze_camera/CameraThread:mTakeFocus	Z
      //   1566: ifeq +25 -> 1591
      //   1569: aload_0
      //   1570: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1573: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   1576: invokevirtual 360	android/hardware/Camera:cancelAutoFocus	()V
      //   1579: ldc 38
      //   1581: ldc_w 362
      //   1584: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   1587: iconst_0
      //   1588: putstatic 357	com/android/amaze_camera/CameraThread:mTakeFocus	Z
      //   1591: aload_0
      //   1592: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1595: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   1598: iconst_5
      //   1599: ldc2_w 220
      //   1602: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   1605: ldc 38
      //   1607: ldc_w 364
      //   1610: invokestatic 366	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
      //   1613: ldc 38
      //   1615: ldc_w 368
      //   1618: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1621: goto -1317 -> 304
      //   1624: aload_0
      //   1625: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1628: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   1631: invokevirtual 371	com/android/amaze_camera/HTCCamera:isAutoCaptureTask	()Z
      //   1634: ifeq -231 -> 1403
      //   1637: ldc 38
      //   1639: ldc_w 318
      //   1642: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1645: aload_0
      //   1646: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1649: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   1652: bipush 21
      //   1654: ldc_w 319
      //   1657: iconst_0
      //   1658: aconst_null
      //   1659: ldc2_w 320
      //   1662: invokestatic 165	com/android/amaze_camera/MessageHandler:sendObtainMessageDelayed	(Landroid/os/Handler;IIILjava/lang/Object;J)V
      //   1665: goto -262 -> 1403
      //   1668: aload_0
      //   1669: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1672: iconst_0
      //   1673: invokestatic 324	com/android/amaze_camera/CameraThread:access$1802	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   1676: pop
      //   1677: goto -236 -> 1441
      //   1680: astore 159
      //   1682: ldc 38
      //   1684: ldc_w 373
      //   1687: aload 159
      //   1689: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   1692: goto -113 -> 1579
      //   1695: ldc 38
      //   1697: ldc_w 378
      //   1700: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1703: getstatic 384	com/android/amaze_camera/TIME:AutoFocusCallback	Lcom/android/amaze_camera/TIME$Value;
      //   1706: invokevirtual 389	com/android/amaze_camera/TIME$Value:Start	()V
      //   1709: aload_0
      //   1710: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1713: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   1716: aload_0
      //   1717: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1720: invokestatic 393	com/android/amaze_camera/CameraThread:access$1900	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraThread$AutoFocusCallback;
      //   1723: invokevirtual 397	android/hardware/Camera:autoFocus	(Landroid/hardware/Camera$AutoFocusCallback;)V
      //   1726: ldc 38
      //   1728: ldc_w 399
      //   1731: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   1734: iconst_1
      //   1735: putstatic 357	com/android/amaze_camera/CameraThread:mTakeFocus	Z
      //   1738: aload_0
      //   1739: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1742: invokestatic 306	com/android/amaze_camera/CameraThread:access$1700	(Lcom/android/amaze_camera/CameraThread;)Z
      //   1745: ifeq +30 -> 1775
      //   1748: aload_0
      //   1749: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1752: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   1755: bipush 45
      //   1757: invokestatic 402	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
      //   1760: aload_0
      //   1761: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1764: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   1767: bipush 45
      //   1769: ldc2_w 158
      //   1772: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   1775: ldc 38
      //   1777: ldc_w 404
      //   1780: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1783: goto -1479 -> 304
      //   1786: astore 155
      //   1788: ldc 38
      //   1790: ldc_w 406
      //   1793: aload 155
      //   1795: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   1798: ldc 38
      //   1800: ldc_w 408
      //   1803: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   1806: aload_0
      //   1807: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1810: iconst_0
      //   1811: invokestatic 412	com/android/amaze_camera/CameraThread:access$2000	(Lcom/android/amaze_camera/CameraThread;Z)V
      //   1814: goto -39 -> 1775
      //   1817: ldc 38
      //   1819: ldc_w 414
      //   1822: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1825: aload_0
      //   1826: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1829: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   1832: bipush 45
      //   1834: invokestatic 402	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
      //   1837: aload_0
      //   1838: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1841: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   1844: iconst_5
      //   1845: invokestatic 402	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
      //   1848: getstatic 357	com/android/amaze_camera/CameraThread:mTakeFocus	Z
      //   1851: ifeq +25 -> 1876
      //   1854: aload_0
      //   1855: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1858: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   1861: invokevirtual 360	android/hardware/Camera:cancelAutoFocus	()V
      //   1864: ldc 38
      //   1866: ldc_w 416
      //   1869: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   1872: iconst_0
      //   1873: putstatic 357	com/android/amaze_camera/CameraThread:mTakeFocus	Z
      //   1876: aload_0
      //   1877: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1880: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   1883: bipush 29
      //   1885: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   1888: ldc 38
      //   1890: ldc_w 418
      //   1893: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1896: goto -1592 -> 304
      //   1899: astore 154
      //   1901: ldc 38
      //   1903: ldc_w 373
      //   1906: aload 154
      //   1908: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   1911: goto -47 -> 1864
      //   1914: ldc 38
      //   1916: ldc_w 420
      //   1919: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1922: aload_0
      //   1923: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1926: iconst_0
      //   1927: invokestatic 412	com/android/amaze_camera/CameraThread:access$2000	(Lcom/android/amaze_camera/CameraThread;Z)V
      //   1930: ldc 38
      //   1932: ldc_w 422
      //   1935: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1938: goto -1634 -> 304
      //   1941: ldc 38
      //   1943: ldc_w 424
      //   1946: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   1949: aload_0
      //   1950: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1953: invokestatic 428	com/android/amaze_camera/CameraThread:access$100	(Lcom/android/amaze_camera/CameraThread;)I
      //   1956: iconst_4
      //   1957: if_icmpne +14 -> 1971
      //   1960: ldc 38
      //   1962: ldc_w 430
      //   1965: invokestatic 72	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
      //   1968: goto -1664 -> 304
      //   1971: aload_0
      //   1972: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1975: iconst_1
      //   1976: invokestatic 433	com/android/amaze_camera/CameraThread:access$1302	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   1979: pop
      //   1980: aload_0
      //   1981: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1984: invokevirtual 436	com/android/amaze_camera/CameraThread:stop_calculate_fps	()V
      //   1987: aload_0
      //   1988: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   1991: invokestatic 440	com/android/amaze_camera/CameraThread:access$2100	(Lcom/android/amaze_camera/CameraThread;)Ljava/lang/Thread;
      //   1994: ifnull +13 -> 2007
      //   1997: aload_0
      //   1998: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2001: invokestatic 440	com/android/amaze_camera/CameraThread:access$2100	(Lcom/android/amaze_camera/CameraThread;)Ljava/lang/Thread;
      //   2004: invokevirtual 445	java/lang/Thread:join	()V
      //   2007: aload_0
      //   2008: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2011: aconst_null
      //   2012: invokestatic 449	com/android/amaze_camera/CameraThread:access$2102	(Lcom/android/amaze_camera/CameraThread;Ljava/lang/Thread;)Ljava/lang/Thread;
      //   2015: pop
      //   2016: aload_0
      //   2017: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2020: invokestatic 452	com/android/amaze_camera/CameraThread:access$2200	(Lcom/android/amaze_camera/CameraThread;)Z
      //   2023: ifne +123 -> 2146
      //   2026: aload_0
      //   2027: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2030: iconst_0
      //   2031: invokestatic 433	com/android/amaze_camera/CameraThread:access$1302	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   2034: pop
      //   2035: ldc 38
      //   2037: ldc_w 454
      //   2040: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   2043: aload_0
      //   2044: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2047: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2050: bipush 52
      //   2052: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2055: aload_0
      //   2056: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2059: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2062: bipush 63
      //   2064: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2067: aload_0
      //   2068: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2071: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2074: bipush 35
      //   2076: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2079: aload_0
      //   2080: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2083: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2086: bipush 15
      //   2088: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2091: aload_0
      //   2092: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2095: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2098: bipush 8
      //   2100: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2103: aload_0
      //   2104: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2107: invokevirtual 457	com/android/amaze_camera/CameraThread:start_calculate_fps	()V
      //   2110: aload_0
      //   2111: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2114: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   2117: invokevirtual 243	com/android/amaze_camera/HTCCamera:resetAutoCaptureTask	()V
      //   2120: ldc 38
      //   2122: ldc_w 459
      //   2125: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2128: goto -1824 -> 304
      //   2131: astore 150
      //   2133: ldc 38
      //   2135: ldc_w 461
      //   2138: aload 150
      //   2140: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   2143: goto -136 -> 2007
      //   2146: aload_0
      //   2147: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2150: iconst_1
      //   2151: invokestatic 464	com/android/amaze_camera/CameraThread:access$102	(Lcom/android/amaze_camera/CameraThread;I)I
      //   2154: pop
      //   2155: ldc 38
      //   2157: ldc_w 466
      //   2160: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2163: aload_0
      //   2164: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2167: invokestatic 469	com/android/amaze_camera/CameraThread:access$2300	(Lcom/android/amaze_camera/CameraThread;)V
      //   2170: ldc 38
      //   2172: ldc_w 471
      //   2175: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2178: goto -1874 -> 304
      //   2181: ldc 38
      //   2183: new 40	java/lang/StringBuilder
      //   2186: dup
      //   2187: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   2190: ldc_w 473
      //   2193: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   2196: aload_0
      //   2197: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2200: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   2203: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   2206: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   2209: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   2212: aload_0
      //   2213: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2216: invokestatic 131	com/android/amaze_camera/CameraThread:access$900	(Lcom/android/amaze_camera/CameraThread;)Z
      //   2219: iconst_1
      //   2220: if_icmpne +37 -> 2257
      //   2223: aload_0
      //   2224: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2227: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   2230: invokevirtual 136	android/hardware/Camera:stopPreview	()V
      //   2233: aload_0
      //   2234: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2237: invokevirtual 436	com/android/amaze_camera/CameraThread:stop_calculate_fps	()V
      //   2240: aload_0
      //   2241: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2244: iconst_0
      //   2245: invokestatic 140	com/android/amaze_camera/CameraThread:access$902	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   2248: pop
      //   2249: ldc 38
      //   2251: ldc_w 474
      //   2254: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2257: ldc 38
      //   2259: ldc_w 476
      //   2262: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   2265: goto -1961 -> 304
      //   2268: ldc 38
      //   2270: ldc_w 478
      //   2273: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2276: ldc2_w 479
      //   2279: invokestatic 484	java/lang/Thread:sleep	(J)V
      //   2282: ldc 38
      //   2284: ldc_w 486
      //   2287: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2290: ldc 38
      //   2292: new 40	java/lang/StringBuilder
      //   2295: dup
      //   2296: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   2299: ldc_w 488
      //   2302: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   2305: aload_0
      //   2306: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2309: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   2312: invokevirtual 53	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   2315: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   2318: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   2321: aload_0
      //   2322: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2325: invokestatic 428	com/android/amaze_camera/CameraThread:access$100	(Lcom/android/amaze_camera/CameraThread;)I
      //   2328: tableswitch	default:+28 -> 2356, 1:+78->2406, 2:+106->2434, 3:+133->2461
      //   2357: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2360: iconst_0
      //   2361: invokestatic 464	com/android/amaze_camera/CameraThread:access$102	(Lcom/android/amaze_camera/CameraThread;I)I
      //   2364: pop
      //   2365: ldc 38
      //   2367: ldc_w 490
      //   2370: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2373: aload_0
      //   2374: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2377: invokevirtual 493	com/android/amaze_camera/CameraThread:close_camera	()V
      //   2380: ldc 38
      //   2382: ldc_w 495
      //   2385: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   2388: goto -2084 -> 304
      //   2391: astore 147
      //   2393: ldc 38
      //   2395: ldc_w 461
      //   2398: aload 147
      //   2400: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   2403: goto -121 -> 2282
      //   2406: ldc 38
      //   2408: ldc_w 497
      //   2411: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   2414: aload_0
      //   2415: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2418: iconst_2
      //   2419: invokestatic 464	com/android/amaze_camera/CameraThread:access$102	(Lcom/android/amaze_camera/CameraThread;I)I
      //   2422: pop
      //   2423: ldc 38
      //   2425: ldc_w 499
      //   2428: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2431: goto -51 -> 2380
      //   2434: aload_0
      //   2435: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2438: iconst_0
      //   2439: invokestatic 464	com/android/amaze_camera/CameraThread:access$102	(Lcom/android/amaze_camera/CameraThread;I)I
      //   2442: pop
      //   2443: ldc 38
      //   2445: ldc_w 490
      //   2448: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2451: aload_0
      //   2452: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2455: invokevirtual 493	com/android/amaze_camera/CameraThread:close_camera	()V
      //   2458: goto -78 -> 2380
      //   2461: aload_0
      //   2462: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2465: iconst_0
      //   2466: invokestatic 464	com/android/amaze_camera/CameraThread:access$102	(Lcom/android/amaze_camera/CameraThread;I)I
      //   2469: pop
      //   2470: ldc 38
      //   2472: ldc_w 490
      //   2475: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2478: aload_0
      //   2479: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2482: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   2485: bipush 8
      //   2487: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2490: goto -110 -> 2380
      //   2493: ldc 38
      //   2495: ldc_w 501
      //   2498: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   2501: aload_0
      //   2502: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2505: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   2508: bipush 31
      //   2510: invokestatic 402	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
      //   2513: aload_0
      //   2514: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2517: invokestatic 428	com/android/amaze_camera/CameraThread:access$100	(Lcom/android/amaze_camera/CameraThread;)I
      //   2520: ifeq +42 -> 2562
      //   2523: aload_0
      //   2524: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2527: invokestatic 428	com/android/amaze_camera/CameraThread:access$100	(Lcom/android/amaze_camera/CameraThread;)I
      //   2530: iconst_4
      //   2531: if_icmpeq +31 -> 2562
      //   2534: ldc 38
      //   2536: ldc_w 503
      //   2539: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   2542: aload_0
      //   2543: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2546: iconst_3
      //   2547: invokestatic 464	com/android/amaze_camera/CameraThread:access$102	(Lcom/android/amaze_camera/CameraThread;I)I
      //   2550: pop
      //   2551: ldc 38
      //   2553: ldc_w 505
      //   2556: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2559: goto -2255 -> 304
      //   2562: aload_0
      //   2563: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2566: iconst_0
      //   2567: invokestatic 464	com/android/amaze_camera/CameraThread:access$102	(Lcom/android/amaze_camera/CameraThread;I)I
      //   2570: pop
      //   2571: aload_0
      //   2572: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2575: invokevirtual 493	com/android/amaze_camera/CameraThread:close_camera	()V
      //   2578: invokestatic 511	android/os/Looper:myLooper	()Landroid/os/Looper;
      //   2581: invokevirtual 514	android/os/Looper:quit	()V
      //   2584: ldc 38
      //   2586: ldc_w 516
      //   2589: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   2592: goto -2288 -> 304
      //   2595: ldc 38
      //   2597: ldc_w 518
      //   2600: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2603: aload_0
      //   2604: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2607: invokevirtual 436	com/android/amaze_camera/CameraThread:stop_calculate_fps	()V
      //   2610: aload_0
      //   2611: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2614: invokestatic 440	com/android/amaze_camera/CameraThread:access$2100	(Lcom/android/amaze_camera/CameraThread;)Ljava/lang/Thread;
      //   2617: ifnull +13 -> 2630
      //   2620: aload_0
      //   2621: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2624: invokestatic 440	com/android/amaze_camera/CameraThread:access$2100	(Lcom/android/amaze_camera/CameraThread;)Ljava/lang/Thread;
      //   2627: invokevirtual 445	java/lang/Thread:join	()V
      //   2630: aload_0
      //   2631: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2634: aconst_null
      //   2635: invokestatic 449	com/android/amaze_camera/CameraThread:access$2102	(Lcom/android/amaze_camera/CameraThread;Ljava/lang/Thread;)Ljava/lang/Thread;
      //   2638: pop
      //   2639: aload_0
      //   2640: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2643: invokestatic 452	com/android/amaze_camera/CameraThread:access$2200	(Lcom/android/amaze_camera/CameraThread;)Z
      //   2646: ifne +105 -> 2751
      //   2649: aload_0
      //   2650: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2653: iconst_0
      //   2654: invokestatic 521	com/android/amaze_camera/CameraThread:access$1702	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   2657: pop
      //   2658: ldc 38
      //   2660: ldc_w 454
      //   2663: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   2666: aload_0
      //   2667: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2670: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2673: bipush 52
      //   2675: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2678: aload_0
      //   2679: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2682: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2685: bipush 63
      //   2687: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2690: aload_0
      //   2691: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2694: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2697: bipush 35
      //   2699: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2702: aload_0
      //   2703: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2706: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2709: bipush 15
      //   2711: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2714: aload_0
      //   2715: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2718: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2721: bipush 64
      //   2723: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2726: aload_0
      //   2727: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2730: invokevirtual 457	com/android/amaze_camera/CameraThread:start_calculate_fps	()V
      //   2733: goto -2429 -> 304
      //   2736: astore 138
      //   2738: ldc 38
      //   2740: ldc_w 461
      //   2743: aload 138
      //   2745: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   2748: goto -118 -> 2630
      //   2751: aload_0
      //   2752: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2755: invokestatic 303	com/android/amaze_camera/CameraThread:access$1600	(Lcom/android/amaze_camera/CameraThread;)Z
      //   2758: ifeq +42 -> 2800
      //   2761: ldc 38
      //   2763: ldc_w 523
      //   2766: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2769: aload_0
      //   2770: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2773: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   2776: ldc_w 524
      //   2779: invokevirtual 527	com/android/amaze_camera/HTCCamera:PlaySound	(I)V
      //   2782: aload_0
      //   2783: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2786: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   2789: bipush 54
      //   2791: ldc2_w 220
      //   2794: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   2797: goto -2493 -> 304
      //   2800: aload_0
      //   2801: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2804: iconst_1
      //   2805: invokevirtual 530	com/android/amaze_camera/CameraThread:start_video_recording	(I)V
      //   2808: aload_0
      //   2809: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2812: invokestatic 306	com/android/amaze_camera/CameraThread:access$1700	(Lcom/android/amaze_camera/CameraThread;)Z
      //   2815: iconst_1
      //   2816: if_icmpne +39 -> 2855
      //   2819: aload_0
      //   2820: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2823: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2826: iconst_3
      //   2827: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2830: aload_0
      //   2831: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2834: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   2837: bipush 22
      //   2839: aload_0
      //   2840: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2843: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   2846: invokevirtual 534	com/android/amaze_camera/HTCCamera:getRecordLimitCheck	()Lcom/android/amaze_camera/RecordLimitCheck;
      //   2849: invokevirtual 540	com/android/amaze_camera/RecordLimitCheck:getCheckSizeDuration	()J
      //   2852: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   2855: ldc 38
      //   2857: ldc_w 542
      //   2860: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2863: goto -2559 -> 304
      //   2866: ldc 38
      //   2868: ldc_w 544
      //   2871: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2874: aload_0
      //   2875: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2878: iconst_1
      //   2879: invokevirtual 530	com/android/amaze_camera/CameraThread:start_video_recording	(I)V
      //   2882: aload_0
      //   2883: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2886: invokestatic 306	com/android/amaze_camera/CameraThread:access$1700	(Lcom/android/amaze_camera/CameraThread;)Z
      //   2889: iconst_1
      //   2890: if_icmpne +39 -> 2929
      //   2893: aload_0
      //   2894: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2897: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   2900: iconst_3
      //   2901: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   2904: aload_0
      //   2905: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2908: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   2911: bipush 22
      //   2913: aload_0
      //   2914: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2917: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   2920: invokevirtual 534	com/android/amaze_camera/HTCCamera:getRecordLimitCheck	()Lcom/android/amaze_camera/RecordLimitCheck;
      //   2923: invokevirtual 540	com/android/amaze_camera/RecordLimitCheck:getCheckSizeDuration	()J
      //   2926: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   2929: ldc 38
      //   2931: ldc_w 546
      //   2934: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2937: goto -2633 -> 304
      //   2940: ldc 38
      //   2942: ldc_w 548
      //   2945: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   2948: aload_0
      //   2949: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2952: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   2955: iconst_1
      //   2956: if_icmpeq +33 -> 2989
      //   2959: ldc 38
      //   2961: ldc_w 550
      //   2964: invokestatic 72	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
      //   2967: ldc 38
      //   2969: ldc_w 552
      //   2972: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   2975: aload_0
      //   2976: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2979: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   2982: iconst_0
      //   2983: invokevirtual 82	com/android/amaze_camera/HTCCamera:setBlockCaptureUI	(Z)V
      //   2986: goto -2682 -> 304
      //   2989: aload_0
      //   2990: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   2993: invokestatic 306	com/android/amaze_camera/CameraThread:access$1700	(Lcom/android/amaze_camera/CameraThread;)Z
      //   2996: ifne +36 -> 3032
      //   2999: aload_0
      //   3000: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3003: invokestatic 555	com/android/amaze_camera/CameraThread:access$2400	(Lcom/android/amaze_camera/CameraThread;)V
      //   3006: aload_0
      //   3007: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3010: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3013: bipush 9
      //   3015: iconst_0
      //   3016: iconst_0
      //   3017: aconst_null
      //   3018: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   3021: ldc 38
      //   3023: ldc_w 557
      //   3026: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   3029: goto -2725 -> 304
      //   3032: ldc 38
      //   3034: ldc_w 559
      //   3037: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   3040: aload_0
      //   3041: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3044: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3047: bipush 45
      //   3049: invokestatic 402	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
      //   3052: aload_0
      //   3053: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3056: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   3059: iconst_3
      //   3060: invokestatic 402	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
      //   3063: aload_0
      //   3064: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3067: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3070: bipush 22
      //   3072: invokestatic 402	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
      //   3075: aload_0
      //   3076: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3079: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3082: bipush 10
      //   3084: invokestatic 402	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
      //   3087: aload_0
      //   3088: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3091: iconst_0
      //   3092: invokestatic 521	com/android/amaze_camera/CameraThread:access$1702	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   3095: pop
      //   3096: aload_0
      //   3097: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3100: invokestatic 562	com/android/amaze_camera/CameraThread:access$2500	(Lcom/android/amaze_camera/CameraThread;)V
      //   3103: aload_0
      //   3104: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3107: iconst_0
      //   3108: invokestatic 140	com/android/amaze_camera/CameraThread:access$902	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   3111: pop
      //   3112: aload_0
      //   3113: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3116: invokestatic 303	com/android/amaze_camera/CameraThread:access$1600	(Lcom/android/amaze_camera/CameraThread;)Z
      //   3119: ifeq +24 -> 3143
      //   3122: ldc 38
      //   3124: ldc_w 564
      //   3127: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   3130: aload_0
      //   3131: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3134: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   3137: ldc_w 565
      //   3140: invokevirtual 527	com/android/amaze_camera/HTCCamera:PlaySound	(I)V
      //   3143: aload_0
      //   3144: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3147: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   3150: bipush 65
      //   3152: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   3155: goto -134 -> 3021
      //   3158: ldc 38
      //   3160: ldc_w 567
      //   3163: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   3166: aload_0
      //   3167: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3170: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   3173: iconst_1
      //   3174: if_icmpeq -2870 -> 304
      //   3177: aload_0
      //   3178: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3181: iconst_1
      //   3182: putfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   3185: aload_0
      //   3186: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3189: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3192: iconst_1
      //   3193: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   3196: aload_0
      //   3197: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3200: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3203: iconst_0
      //   3204: iconst_1
      //   3205: iconst_0
      //   3206: aconst_null
      //   3207: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   3210: invokestatic 570	com/android/amaze_camera/DisplayDevice:supportThumbnailAlbumButton	()Z
      //   3213: iconst_1
      //   3214: if_icmpne +21 -> 3235
      //   3217: invokestatic 573	com/android/amaze_camera/DisplayDevice:isDoubleShot	()Z
      //   3220: ifne +26 -> 3246
      //   3223: aload_0
      //   3224: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3227: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3230: bipush 30
      //   3232: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   3235: ldc 38
      //   3237: ldc_w 575
      //   3240: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   3243: goto -2939 -> 304
      //   3246: aload_0
      //   3247: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3250: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3253: bipush 57
      //   3255: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   3258: goto -23 -> 3235
      //   3261: ldc 38
      //   3263: ldc_w 577
      //   3266: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   3269: aload_0
      //   3270: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3273: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   3276: iconst_1
      //   3277: if_icmpne -2973 -> 304
      //   3280: aload_0
      //   3281: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3284: invokestatic 306	com/android/amaze_camera/CameraThread:access$1700	(Lcom/android/amaze_camera/CameraThread;)Z
      //   3287: ifeq +27 -> 3314
      //   3290: ldc 38
      //   3292: ldc_w 579
      //   3295: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   3298: aload_0
      //   3299: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3302: invokestatic 562	com/android/amaze_camera/CameraThread:access$2500	(Lcom/android/amaze_camera/CameraThread;)V
      //   3305: aload_0
      //   3306: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3309: iconst_0
      //   3310: invokestatic 521	com/android/amaze_camera/CameraThread:access$1702	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   3313: pop
      //   3314: aload_0
      //   3315: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3318: iconst_0
      //   3319: putfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   3322: aload_0
      //   3323: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3326: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3329: iconst_1
      //   3330: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   3333: aload_0
      //   3334: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3337: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3340: iconst_0
      //   3341: iconst_1
      //   3342: iconst_0
      //   3343: aconst_null
      //   3344: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   3347: invokestatic 570	com/android/amaze_camera/DisplayDevice:supportThumbnailAlbumButton	()Z
      //   3350: iconst_1
      //   3351: if_icmpne +21 -> 3372
      //   3354: invokestatic 573	com/android/amaze_camera/DisplayDevice:isDoubleShot	()Z
      //   3357: ifne +26 -> 3383
      //   3360: aload_0
      //   3361: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3364: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3367: bipush 29
      //   3369: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   3372: ldc 38
      //   3374: ldc_w 581
      //   3377: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   3380: goto -3076 -> 304
      //   3383: aload_0
      //   3384: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3387: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   3390: bipush 57
      //   3392: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   3395: goto -23 -> 3372
      //   3398: aload_0
      //   3399: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3402: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   3405: ifnull -3101 -> 304
      //   3408: aload_0
      //   3409: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3412: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3415: ifnull -3111 -> 304
      //   3418: aload_1
      //   3419: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   3422: checkcast 192	java/lang/String
      //   3425: astore 134
      //   3427: aload_0
      //   3428: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3431: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3434: aload 134
      //   3436: invokevirtual 585	com/android/amaze_camera/CameraController:setWhiteBalance	(Ljava/lang/String;)V
      //   3439: aload_0
      //   3440: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3443: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3446: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   3449: aload 134
      //   3451: ldc_w 587
      //   3454: invokevirtual 591	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   3457: ifeq -3153 -> 304
      //   3460: ldc_w 593
      //   3463: ldc_w 595
      //   3466: invokestatic 366	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
      //   3469: goto -3165 -> 304
      //   3472: aload_0
      //   3473: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3476: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   3479: ifnull -3175 -> 304
      //   3482: aload_0
      //   3483: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3486: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3489: ifnull -3185 -> 304
      //   3492: aload_1
      //   3493: getfield 85	android/os/Message:arg1	I
      //   3496: istore 132
      //   3498: aload_0
      //   3499: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3502: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3505: ldc_w 597
      //   3508: iload 132
      //   3510: iconst_5
      //   3511: invokevirtual 601	com/android/amaze_camera/CameraController:mapBarLevel2SettingValue	(Ljava/lang/String;II)I
      //   3514: istore 133
      //   3516: aload_0
      //   3517: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3520: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3523: iload 133
      //   3525: invokevirtual 604	com/android/amaze_camera/CameraController:setExposureCompensation	(I)V
      //   3528: aload_0
      //   3529: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3532: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3535: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   3538: ldc_w 593
      //   3541: ldc_w 606
      //   3544: invokestatic 366	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
      //   3547: goto -3243 -> 304
      //   3550: aload_0
      //   3551: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3554: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   3557: ifnull -3253 -> 304
      //   3560: aload_0
      //   3561: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3564: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3567: ifnull -3263 -> 304
      //   3570: aload_1
      //   3571: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   3574: checkcast 102	java/lang/Boolean
      //   3577: invokevirtual 106	java/lang/Boolean:booleanValue	()Z
      //   3580: ifeq +48 -> 3628
      //   3583: aload_0
      //   3584: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3587: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3590: ldc_w 608
      //   3593: ldc_w 337
      //   3596: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   3599: aload_0
      //   3600: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3603: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3606: ldc_w 610
      //   3609: ldc_w 337
      //   3612: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   3615: aload_0
      //   3616: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3619: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3622: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   3625: goto -3321 -> 304
      //   3628: aload_0
      //   3629: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3632: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3635: ldc_w 608
      //   3638: ldc_w 348
      //   3641: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   3644: aload_0
      //   3645: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3648: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3651: ldc_w 610
      //   3654: ldc_w 348
      //   3657: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   3660: goto -45 -> 3615
      //   3663: aload_0
      //   3664: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3667: aload_1
      //   3668: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   3671: checkcast 102	java/lang/Boolean
      //   3674: invokevirtual 106	java/lang/Boolean:booleanValue	()Z
      //   3677: invokestatic 613	com/android/amaze_camera/CameraThread:access$2602	(Lcom/android/amaze_camera/CameraThread;Z)Z
      //   3680: pop
      //   3681: ldc 38
      //   3683: new 40	java/lang/StringBuilder
      //   3686: dup
      //   3687: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   3690: ldc_w 615
      //   3693: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   3696: aload_0
      //   3697: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3700: invokestatic 618	com/android/amaze_camera/CameraThread:access$2600	(Lcom/android/amaze_camera/CameraThread;)Z
      //   3703: invokevirtual 621	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
      //   3706: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   3709: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   3712: goto -3408 -> 304
      //   3715: aload_0
      //   3716: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3719: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   3722: ifnull -3418 -> 304
      //   3725: aload_0
      //   3726: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3729: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3732: ifnull -3428 -> 304
      //   3735: aload_1
      //   3736: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   3739: checkcast 192	java/lang/String
      //   3742: astore 130
      //   3744: aload_0
      //   3745: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3748: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3751: ldc_w 623
      //   3754: aload 130
      //   3756: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   3759: aload_0
      //   3760: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3763: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3766: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   3769: goto -3465 -> 304
      //   3772: aload_0
      //   3773: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3776: aload_1
      //   3777: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   3780: checkcast 192	java/lang/String
      //   3783: invokevirtual 626	com/android/amaze_camera/CameraThread:setColorEffect	(Ljava/lang/String;)V
      //   3786: goto -3482 -> 304
      //   3789: aload_0
      //   3790: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3793: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   3796: ifnull -3492 -> 304
      //   3799: aload_0
      //   3800: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3803: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3806: ifnull -3502 -> 304
      //   3809: aload_1
      //   3810: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   3813: checkcast 192	java/lang/String
      //   3816: astore 127
      //   3818: aload_1
      //   3819: getfield 85	android/os/Message:arg1	I
      //   3822: istore 128
      //   3824: aload_0
      //   3825: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3828: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3831: aload 127
      //   3833: iload 128
      //   3835: iconst_5
      //   3836: invokevirtual 601	com/android/amaze_camera/CameraController:mapBarLevel2SettingValue	(Ljava/lang/String;II)I
      //   3839: istore 129
      //   3841: aload_0
      //   3842: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3845: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3848: aload 127
      //   3850: iload 129
      //   3852: invokevirtual 629	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;I)V
      //   3855: aload_0
      //   3856: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3859: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3862: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   3865: aload 127
      //   3867: ldc_w 631
      //   3870: invokevirtual 591	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   3873: ifeq +15 -> 3888
      //   3876: ldc_w 593
      //   3879: ldc_w 633
      //   3882: invokestatic 366	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
      //   3885: goto -3581 -> 304
      //   3888: aload 127
      //   3890: ldc_w 635
      //   3893: invokevirtual 591	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   3896: ifeq +15 -> 3911
      //   3899: ldc_w 593
      //   3902: ldc_w 637
      //   3905: invokestatic 366	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
      //   3908: goto -3604 -> 304
      //   3911: aload 127
      //   3913: ldc_w 639
      //   3916: invokevirtual 591	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   3919: ifeq -3615 -> 304
      //   3922: ldc_w 593
      //   3925: ldc_w 641
      //   3928: invokestatic 366	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
      //   3931: goto -3627 -> 304
      //   3934: aload_0
      //   3935: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3938: invokestatic 67	com/android/amaze_camera/CameraThread:access$200	(Lcom/android/amaze_camera/CameraThread;)Landroid/hardware/Camera;
      //   3941: ifnull -3637 -> 304
      //   3944: aload_0
      //   3945: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3948: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3951: ifnull -3647 -> 304
      //   3954: aload_0
      //   3955: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3958: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   3961: invokevirtual 645	com/android/amaze_camera/HTCCamera:getFlashRestriction	()Lcom/android/amaze_camera/FlashRestriction;
      //   3964: astore 125
      //   3966: aload 125
      //   3968: ifnull +38 -> 4006
      //   3971: aload 125
      //   3973: invokevirtual 650	com/android/amaze_camera/FlashRestriction:isDisableFlash	()Z
      //   3976: iconst_1
      //   3977: if_icmpne +29 -> 4006
      //   3980: aload_0
      //   3981: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3984: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   3987: ldc_w 348
      //   3990: invokevirtual 653	com/android/amaze_camera/CameraController:setFlashMode	(Ljava/lang/String;)V
      //   3993: aload_0
      //   3994: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   3997: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   4000: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   4003: goto -3699 -> 304
      //   4006: aload_1
      //   4007: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   4010: checkcast 192	java/lang/String
      //   4013: astore 126
      //   4015: aload 126
      //   4017: ifnull +54 -> 4071
      //   4020: ldc 38
      //   4022: new 40	java/lang/StringBuilder
      //   4025: dup
      //   4026: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   4029: ldc_w 655
      //   4032: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   4035: aload 126
      //   4037: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   4040: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   4043: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   4046: aload_0
      //   4047: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4050: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   4053: aload 126
      //   4055: invokevirtual 653	com/android/amaze_camera/CameraController:setFlashMode	(Ljava/lang/String;)V
      //   4058: aload_0
      //   4059: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4062: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   4065: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   4068: goto -3764 -> 304
      //   4071: ldc 38
      //   4073: ldc_w 657
      //   4076: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   4079: goto -3775 -> 304
      //   4082: aload_0
      //   4083: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4086: invokestatic 306	com/android/amaze_camera/CameraThread:access$1700	(Lcom/android/amaze_camera/CameraThread;)Z
      //   4089: ifne +14 -> 4103
      //   4092: ldc 38
      //   4094: ldc_w 659
      //   4097: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   4100: goto -3796 -> 304
      //   4103: aload_0
      //   4104: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4107: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   4110: invokevirtual 534	com/android/amaze_camera/HTCCamera:getRecordLimitCheck	()Lcom/android/amaze_camera/RecordLimitCheck;
      //   4113: astore 124
      //   4115: aload 124
      //   4117: ifnonnull +14 -> 4131
      //   4120: ldc 38
      //   4122: ldc_w 661
      //   4125: invokestatic 72	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
      //   4128: goto -3824 -> 304
      //   4131: aload 124
      //   4133: invokevirtual 664	com/android/amaze_camera/RecordLimitCheck:checkSizeLimit	()V
      //   4136: aload 124
      //   4138: invokevirtual 667	com/android/amaze_camera/RecordLimitCheck:getLimitState	()I
      //   4141: tableswitch	default:+35 -> 4176, 1:+80->4221, 2:+128->4269, 3:+197->4338, 4:+197->4338, 5:+55->4196
      //   4177: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4180: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   4183: bipush 22
      //   4185: aload 124
      //   4187: invokevirtual 540	com/android/amaze_camera/RecordLimitCheck:getCheckSizeDuration	()J
      //   4190: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   4193: goto -3889 -> 304
      //   4196: aload_0
      //   4197: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4200: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   4203: bipush 37
      //   4205: ldc_w 668
      //   4208: iconst_0
      //   4209: aconst_null
      //   4210: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   4213: ldc 38
      //   4215: ldc_w 670
      //   4218: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   4221: ldc 38
      //   4223: ldc_w 672
      //   4226: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   4229: aload_0
      //   4230: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4233: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   4236: iconst_1
      //   4237: invokevirtual 82	com/android/amaze_camera/HTCCamera:setBlockCaptureUI	(Z)V
      //   4240: aload_0
      //   4241: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4244: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   4247: iconst_4
      //   4248: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   4251: aload_0
      //   4252: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4255: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   4258: bipush 10
      //   4260: iconst_0
      //   4261: iconst_0
      //   4262: aconst_null
      //   4263: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   4266: goto -3962 -> 304
      //   4269: ldc 38
      //   4271: ldc_w 674
      //   4274: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   4277: aload_0
      //   4278: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4281: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   4284: iconst_1
      //   4285: invokevirtual 82	com/android/amaze_camera/HTCCamera:setBlockCaptureUI	(Z)V
      //   4288: aload_0
      //   4289: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4292: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   4295: iconst_4
      //   4296: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   4299: aload_0
      //   4300: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4303: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   4306: bipush 10
      //   4308: iconst_0
      //   4309: iconst_0
      //   4310: aconst_null
      //   4311: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   4314: iconst_3
      //   4315: putstatic 677	com/android/amaze_camera/CameraThread:Storage_Status	I
      //   4318: aload_0
      //   4319: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4322: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   4325: bipush 37
      //   4327: ldc_w 678
      //   4330: iconst_0
      //   4331: aconst_null
      //   4332: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   4335: goto -4031 -> 304
      //   4338: ldc 38
      //   4340: ldc_w 680
      //   4343: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   4346: goto -4042 -> 304
      //   4349: aload_0
      //   4350: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4353: invokestatic 683	com/android/amaze_camera/CameraThread:access$2700	(Lcom/android/amaze_camera/CameraThread;)Z
      //   4356: ifeq -4052 -> 304
      //   4359: aload_0
      //   4360: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4363: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   4366: bipush 40
      //   4368: aload_0
      //   4369: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4372: invokestatic 686	com/android/amaze_camera/CameraThread:access$2800	(Lcom/android/amaze_camera/CameraThread;)I
      //   4375: iconst_0
      //   4376: aconst_null
      //   4377: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   4380: aload_0
      //   4381: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4384: iconst_0
      //   4385: invokestatic 689	com/android/amaze_camera/CameraThread:access$2802	(Lcom/android/amaze_camera/CameraThread;I)I
      //   4388: pop
      //   4389: aload_0
      //   4390: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4393: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   4396: bipush 25
      //   4398: ldc2_w 690
      //   4401: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   4404: goto -4100 -> 304
      //   4407: ldc 38
      //   4409: ldc_w 693
      //   4412: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   4415: aload_0
      //   4416: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4419: invokestatic 696	com/android/amaze_camera/CameraThread:access$2900	(Lcom/android/amaze_camera/CameraThread;)V
      //   4422: aload_0
      //   4423: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4426: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   4429: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   4432: aload_0
      //   4433: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4436: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   4439: bipush 35
      //   4441: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   4444: aload_0
      //   4445: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4448: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   4451: bipush 45
      //   4453: invokestatic 204	com/android/amaze_camera/MessageHandler:sendEmptyMessage	(Landroid/os/Handler;I)V
      //   4456: ldc 38
      //   4458: ldc_w 698
      //   4461: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   4464: goto -4160 -> 304
      //   4467: ldc 38
      //   4469: ldc_w 700
      //   4472: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   4475: aconst_null
      //   4476: astore 87
      //   4478: invokestatic 705	com/android/amaze_camera/DCFRuler$StorageCardControl:getStorageType	()I
      //   4481: ifne +354 -> 4835
      //   4484: getstatic 711	android/provider/MediaStore$Images$Media:PHONE_CONTENT_URI	Landroid/net/Uri;
      //   4487: astore 87
      //   4489: getstatic 714	android/provider/MediaStore$Video$Media:PHONE_CONTENT_URI	Landroid/net/Uri;
      //   4492: astore 88
      //   4494: aload 87
      //   4496: ifnull -4192 -> 304
      //   4499: aload 88
      //   4501: ifnull -4197 -> 304
      //   4504: aload_0
      //   4505: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4508: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   4511: invokevirtual 718	com/android/amaze_camera/HTCCamera:getContentResolver	()Landroid/content/ContentResolver;
      //   4514: astore 89
      //   4516: iconst_3
      //   4517: anewarray 192	java/lang/String
      //   4520: astore 90
      //   4522: aload 90
      //   4524: iconst_0
      //   4525: ldc_w 720
      //   4528: aastore
      //   4529: aload 90
      //   4531: iconst_1
      //   4532: ldc_w 722
      //   4535: aastore
      //   4536: aload 90
      //   4538: iconst_2
      //   4539: ldc_w 724
      //   4542: aastore
      //   4543: iconst_1
      //   4544: anewarray 192	java/lang/String
      //   4547: astore 91
      //   4549: aload 91
      //   4551: iconst_0
      //   4552: ldc_w 726
      //   4555: aastore
      //   4556: aload 89
      //   4558: aload 87
      //   4560: aload 90
      //   4562: ldc_w 728
      //   4565: aload 91
      //   4567: ldc_w 730
      //   4570: invokevirtual 736	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
      //   4573: astore 92
      //   4575: aload_0
      //   4576: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   4579: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   4582: invokevirtual 718	com/android/amaze_camera/HTCCamera:getContentResolver	()Landroid/content/ContentResolver;
      //   4585: astore 93
      //   4587: iconst_3
      //   4588: anewarray 192	java/lang/String
      //   4591: astore 94
      //   4593: aload 94
      //   4595: iconst_0
      //   4596: ldc_w 720
      //   4599: aastore
      //   4600: aload 94
      //   4602: iconst_1
      //   4603: ldc_w 722
      //   4606: aastore
      //   4607: aload 94
      //   4609: iconst_2
      //   4610: ldc_w 724
      //   4613: aastore
      //   4614: aload 93
      //   4616: aload 88
      //   4618: aload 94
      //   4620: ldc_w 738
      //   4623: aconst_null
      //   4624: ldc_w 730
      //   4627: invokevirtual 736	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
      //   4630: astore 95
      //   4632: aconst_null
      //   4633: astore 96
      //   4635: aconst_null
      //   4636: astore 97
      //   4638: aconst_null
      //   4639: astore 98
      //   4641: aload 92
      //   4643: ifnull +276 -> 4919
      //   4646: aload 92
      //   4648: invokeinterface 743 1 0
      //   4653: ifle +266 -> 4919
      //   4656: aload 92
      //   4658: invokeinterface 746 1 0
      //   4663: pop
      //   4664: aload 92
      //   4666: aload 92
      //   4668: ldc_w 722
      //   4671: invokeinterface 750 2 0
      //   4676: invokeinterface 754 2 0
      //   4681: astore 97
      //   4683: aload 92
      //   4685: aload 92
      //   4687: ldc_w 720
      //   4690: invokeinterface 750 2 0
      //   4695: invokeinterface 754 2 0
      //   4700: pop
      //   4701: aload 92
      //   4703: aload 92
      //   4705: ldc_w 724
      //   4708: invokeinterface 750 2 0
      //   4713: invokeinterface 754 2 0
      //   4718: astore 122
      //   4720: aload 122
      //   4722: astore 98
      //   4724: aload_1
      //   4725: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   4728: pop
      //   4729: aconst_null
      //   4730: astore 100
      //   4732: aconst_null
      //   4733: astore 101
      //   4735: aload 95
      //   4737: ifnull +257 -> 4994
      //   4740: aload 95
      //   4742: invokeinterface 743 1 0
      //   4747: ifle +247 -> 4994
      //   4750: aload 95
      //   4752: invokeinterface 746 1 0
      //   4757: pop
      //   4758: aload 95
      //   4760: aload 95
      //   4762: ldc_w 722
      //   4765: invokeinterface 750 2 0
      //   4770: invokeinterface 754 2 0
      //   4775: astore 100
      //   4777: aload 95
      //   4779: aload 95
      //   4781: ldc_w 724
      //   4784: invokeinterface 750 2 0
      //   4789: invokeinterface 754 2 0
      //   4794: astore 117
      //   4796: aload 117
      //   4798: astore 101
      //   4800: aconst_null
      //   4801: astore 102
      //   4803: iconst_0
      //   4804: istore 103
      //   4806: aload 92
      //   4808: ifnonnull +197 -> 5005
      //   4811: aload 95
      //   4813: ifnonnull +192 -> 5005
      //   4816: iconst_0
      //   4817: istore 104
      //   4819: iload 104
      //   4821: ifne +318 -> 5139
      //   4824: ldc 38
      //   4826: ldc_w 756
      //   4829: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   4832: goto -4528 -> 304
      //   4835: invokestatic 705	com/android/amaze_camera/DCFRuler$StorageCardControl:getStorageType	()I
      //   4838: iconst_1
      //   4839: if_icmpne +3659 -> 8498
      //   4842: getstatic 759	android/provider/MediaStore$Images$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
      //   4845: astore 87
      //   4847: getstatic 760	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
      //   4850: astore 88
      //   4852: goto -358 -> 4494
      //   4855: astore 119
      //   4857: ldc 38
      //   4859: aload 119
      //   4861: invokevirtual 761	android/database/sqlite/SQLiteDiskIOException:toString	()Ljava/lang/String;
      //   4864: aload 119
      //   4866: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   4869: aload 92
      //   4871: invokeinterface 764 1 0
      //   4876: ldc 38
      //   4878: ldc_w 766
      //   4881: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   4884: goto -4580 -> 304
      //   4887: astore 118
      //   4889: ldc 38
      //   4891: aload 118
      //   4893: invokevirtual 767	java/lang/Exception:toString	()Ljava/lang/String;
      //   4896: aload 118
      //   4898: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   4901: aload 92
      //   4903: invokeinterface 764 1 0
      //   4908: ldc 38
      //   4910: ldc_w 766
      //   4913: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   4916: goto -4612 -> 304
      //   4919: ldc 38
      //   4921: ldc_w 769
      //   4924: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   4927: goto -203 -> 4724
      //   4930: astore 115
      //   4932: ldc 38
      //   4934: aload 115
      //   4936: invokevirtual 761	android/database/sqlite/SQLiteDiskIOException:toString	()Ljava/lang/String;
      //   4939: aload 115
      //   4941: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   4944: aload 95
      //   4946: invokeinterface 764 1 0
      //   4951: ldc 38
      //   4953: ldc_w 766
      //   4956: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   4959: goto -4655 -> 304
      //   4962: astore 114
      //   4964: ldc 38
      //   4966: aload 114
      //   4968: invokevirtual 767	java/lang/Exception:toString	()Ljava/lang/String;
      //   4971: aload 114
      //   4973: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   4976: aload 95
      //   4978: invokeinterface 764 1 0
      //   4983: ldc 38
      //   4985: ldc_w 766
      //   4988: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   4991: goto -4687 -> 304
      //   4994: ldc 38
      //   4996: ldc_w 771
      //   4999: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   5002: goto -202 -> 4800
      //   5005: aload 92
      //   5007: ifnonnull +25 -> 5032
      //   5010: aload 95
      //   5012: invokeinterface 743 1 0
      //   5017: ifne +9 -> 5026
      //   5020: iconst_0
      //   5021: istore 104
      //   5023: goto -204 -> 4819
      //   5026: iconst_2
      //   5027: istore 104
      //   5029: goto -210 -> 4819
      //   5032: aload 95
      //   5034: ifnonnull +25 -> 5059
      //   5037: aload 92
      //   5039: invokeinterface 743 1 0
      //   5044: ifne +9 -> 5053
      //   5047: iconst_0
      //   5048: istore 104
      //   5050: goto -231 -> 4819
      //   5053: iconst_1
      //   5054: istore 104
      //   5056: goto -237 -> 4819
      //   5059: aload 95
      //   5061: invokeinterface 743 1 0
      //   5066: ifne +19 -> 5085
      //   5069: aload 92
      //   5071: invokeinterface 743 1 0
      //   5076: ifne +9 -> 5085
      //   5079: iconst_0
      //   5080: istore 104
      //   5082: goto -263 -> 4819
      //   5085: aload 95
      //   5087: invokeinterface 743 1 0
      //   5092: ifne +9 -> 5101
      //   5095: iconst_1
      //   5096: istore 104
      //   5098: goto -279 -> 4819
      //   5101: aload 92
      //   5103: invokeinterface 743 1 0
      //   5108: ifne +9 -> 5117
      //   5111: iconst_2
      //   5112: istore 104
      //   5114: goto -295 -> 4819
      //   5117: aload 98
      //   5119: aload 101
      //   5121: invokevirtual 774	java/lang/String:compareTo	(Ljava/lang/String;)I
      //   5124: ifle +9 -> 5133
      //   5127: iconst_1
      //   5128: istore 104
      //   5130: goto -311 -> 4819
      //   5133: iconst_2
      //   5134: istore 104
      //   5136: goto -317 -> 4819
      //   5139: iload 104
      //   5141: iconst_1
      //   5142: if_icmpne +185 -> 5327
      //   5145: ldc 38
      //   5147: new 40	java/lang/StringBuilder
      //   5150: dup
      //   5151: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   5154: ldc_w 776
      //   5157: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   5160: aload 97
      //   5162: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   5165: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   5168: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   5171: aload 97
      //   5173: iconst_3
      //   5174: invokestatic 782	android/media/ThumbnailUtils:createImageThumbnail	(Ljava/lang/String;I)Landroid/graphics/Bitmap;
      //   5177: astore 113
      //   5179: aload 113
      //   5181: astore 96
      //   5183: aload 97
      //   5185: astore 102
      //   5187: iconst_0
      //   5188: istore 103
      //   5190: new 784	android/os/Bundle
      //   5193: dup
      //   5194: invokespecial 785	android/os/Bundle:<init>	()V
      //   5197: astore 106
      //   5199: aload 106
      //   5201: ldc_w 787
      //   5204: aload 102
      //   5206: invokevirtual 790	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
      //   5209: aload_0
      //   5210: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   5213: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   5216: invokevirtual 794	android/os/Handler:obtainMessage	()Landroid/os/Message;
      //   5219: astore 107
      //   5221: aload 107
      //   5223: bipush 50
      //   5225: putfield 36	android/os/Message:what	I
      //   5228: aload 107
      //   5230: iload 103
      //   5232: putfield 85	android/os/Message:arg1	I
      //   5235: aload 107
      //   5237: aload 106
      //   5239: invokevirtual 798	android/os/Message:setData	(Landroid/os/Bundle;)V
      //   5242: aload 107
      //   5244: aload 96
      //   5246: putfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   5249: aload_0
      //   5250: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   5253: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   5256: aload 107
      //   5258: invokevirtual 802	android/os/Handler:sendMessage	(Landroid/os/Message;)Z
      //   5261: pop
      //   5262: aload 92
      //   5264: ifnull +10 -> 5274
      //   5267: aload 92
      //   5269: invokeinterface 764 1 0
      //   5274: aload 95
      //   5276: ifnull +10 -> 5286
      //   5279: aload 95
      //   5281: invokeinterface 764 1 0
      //   5286: ldc 38
      //   5288: ldc_w 766
      //   5291: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   5294: goto -4990 -> 304
      //   5297: astore 112
      //   5299: ldc 38
      //   5301: ldc_w 804
      //   5304: aload 112
      //   5306: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   5309: goto -119 -> 5190
      //   5312: astore 111
      //   5314: ldc 38
      //   5316: ldc_w 806
      //   5319: aload 111
      //   5321: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   5324: goto -134 -> 5190
      //   5327: iload 104
      //   5329: iconst_2
      //   5330: if_icmpne -140 -> 5190
      //   5333: ldc 38
      //   5335: new 40	java/lang/StringBuilder
      //   5338: dup
      //   5339: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   5342: ldc_w 808
      //   5345: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   5348: aload 100
      //   5350: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   5353: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   5356: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   5359: aload 100
      //   5361: iconst_3
      //   5362: invokestatic 811	android/media/ThumbnailUtils:createVideoThumbnail	(Ljava/lang/String;I)Landroid/graphics/Bitmap;
      //   5365: astore 110
      //   5367: aload 110
      //   5369: astore 96
      //   5371: aload 100
      //   5373: astore 102
      //   5375: iconst_1
      //   5376: istore 103
      //   5378: goto -188 -> 5190
      //   5381: astore 109
      //   5383: ldc 38
      //   5385: ldc_w 813
      //   5388: aload 109
      //   5390: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   5393: goto -203 -> 5190
      //   5396: astore 105
      //   5398: ldc 38
      //   5400: ldc_w 815
      //   5403: aload 105
      //   5405: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   5408: goto -218 -> 5190
      //   5411: ldc 38
      //   5413: ldc_w 817
      //   5416: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   5419: aconst_null
      //   5420: astore 71
      //   5422: invokestatic 705	com/android/amaze_camera/DCFRuler$StorageCardControl:getStorageType	()I
      //   5425: ifne +261 -> 5686
      //   5428: getstatic 711	android/provider/MediaStore$Images$Media:PHONE_CONTENT_URI	Landroid/net/Uri;
      //   5431: astore 71
      //   5433: aload 71
      //   5435: ifnull -5131 -> 304
      //   5438: aload_0
      //   5439: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   5442: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   5445: invokevirtual 718	com/android/amaze_camera/HTCCamera:getContentResolver	()Landroid/content/ContentResolver;
      //   5448: astore 72
      //   5450: iconst_2
      //   5451: anewarray 192	java/lang/String
      //   5454: astore 73
      //   5456: aload 73
      //   5458: iconst_0
      //   5459: ldc_w 720
      //   5462: aastore
      //   5463: aload 73
      //   5465: iconst_1
      //   5466: ldc_w 722
      //   5469: aastore
      //   5470: aload 72
      //   5472: aload 71
      //   5474: aload 73
      //   5476: ldc_w 738
      //   5479: aconst_null
      //   5480: ldc_w 730
      //   5483: invokevirtual 736	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
      //   5486: astore 74
      //   5488: aconst_null
      //   5489: astore 75
      //   5491: aconst_null
      //   5492: astore 76
      //   5494: aload 74
      //   5496: ifnull +299 -> 5795
      //   5499: aload 74
      //   5501: invokeinterface 743 1 0
      //   5506: ifle +289 -> 5795
      //   5509: aload 74
      //   5511: invokeinterface 746 1 0
      //   5516: pop
      //   5517: aload 74
      //   5519: aload 74
      //   5521: ldc_w 722
      //   5524: invokeinterface 750 2 0
      //   5529: invokeinterface 754 2 0
      //   5534: astore 76
      //   5536: aload 74
      //   5538: aload 74
      //   5540: ldc_w 720
      //   5543: invokeinterface 750 2 0
      //   5548: invokeinterface 754 2 0
      //   5553: pop
      //   5554: ldc 38
      //   5556: new 40	java/lang/StringBuilder
      //   5559: dup
      //   5560: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   5563: ldc_w 776
      //   5566: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   5569: aload 76
      //   5571: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   5574: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   5577: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   5580: aload 76
      //   5582: iconst_3
      //   5583: invokestatic 782	android/media/ThumbnailUtils:createImageThumbnail	(Ljava/lang/String;I)Landroid/graphics/Bitmap;
      //   5586: astore 86
      //   5588: aload 86
      //   5590: astore 75
      //   5592: new 784	android/os/Bundle
      //   5595: dup
      //   5596: invokespecial 785	android/os/Bundle:<init>	()V
      //   5599: astore 77
      //   5601: aload 77
      //   5603: ldc_w 787
      //   5606: aload 76
      //   5608: invokevirtual 790	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
      //   5611: aload_0
      //   5612: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   5615: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   5618: invokevirtual 794	android/os/Handler:obtainMessage	()Landroid/os/Message;
      //   5621: astore 78
      //   5623: aload 78
      //   5625: bipush 50
      //   5627: putfield 36	android/os/Message:what	I
      //   5630: aload 78
      //   5632: iconst_0
      //   5633: putfield 85	android/os/Message:arg1	I
      //   5636: aload 78
      //   5638: aload 77
      //   5640: invokevirtual 798	android/os/Message:setData	(Landroid/os/Bundle;)V
      //   5643: aload 78
      //   5645: aload 75
      //   5647: putfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   5650: aload_0
      //   5651: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   5654: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   5657: aload 78
      //   5659: invokevirtual 802	android/os/Handler:sendMessage	(Landroid/os/Message;)Z
      //   5662: pop
      //   5663: aload 74
      //   5665: ifnull +10 -> 5675
      //   5668: aload 74
      //   5670: invokeinterface 764 1 0
      //   5675: ldc 38
      //   5677: ldc_w 819
      //   5680: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   5683: goto -5379 -> 304
      //   5686: invokestatic 705	com/android/amaze_camera/DCFRuler$StorageCardControl:getStorageType	()I
      //   5689: iconst_1
      //   5690: if_icmpne -257 -> 5433
      //   5693: getstatic 759	android/provider/MediaStore$Images$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
      //   5696: astore 71
      //   5698: goto -265 -> 5433
      //   5701: astore 81
      //   5703: ldc 38
      //   5705: aload 81
      //   5707: invokevirtual 761	android/database/sqlite/SQLiteDiskIOException:toString	()Ljava/lang/String;
      //   5710: aload 81
      //   5712: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   5715: aload 74
      //   5717: invokeinterface 764 1 0
      //   5722: ldc 38
      //   5724: ldc_w 819
      //   5727: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   5730: goto -5426 -> 304
      //   5733: astore 80
      //   5735: ldc 38
      //   5737: aload 80
      //   5739: invokevirtual 767	java/lang/Exception:toString	()Ljava/lang/String;
      //   5742: aload 80
      //   5744: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   5747: aload 74
      //   5749: invokeinterface 764 1 0
      //   5754: ldc 38
      //   5756: ldc_w 819
      //   5759: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   5762: goto -5458 -> 304
      //   5765: astore 85
      //   5767: ldc 38
      //   5769: ldc_w 804
      //   5772: aload 85
      //   5774: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   5777: goto -185 -> 5592
      //   5780: astore 84
      //   5782: ldc 38
      //   5784: ldc_w 806
      //   5787: aload 84
      //   5789: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   5792: goto -200 -> 5592
      //   5795: ldc 38
      //   5797: ldc_w 769
      //   5800: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   5803: goto -211 -> 5592
      //   5806: ldc 38
      //   5808: ldc_w 821
      //   5811: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   5814: aload_1
      //   5815: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   5818: astore 58
      //   5820: aconst_null
      //   5821: astore 59
      //   5823: aload 58
      //   5825: ifnonnull +152 -> 5977
      //   5828: aconst_null
      //   5829: astore 66
      //   5831: invokestatic 705	com/android/amaze_camera/DCFRuler$StorageCardControl:getStorageType	()I
      //   5834: ifne +128 -> 5962
      //   5837: getstatic 714	android/provider/MediaStore$Video$Media:PHONE_CONTENT_URI	Landroid/net/Uri;
      //   5840: astore 66
      //   5842: aload 66
      //   5844: ifnull -5540 -> 304
      //   5847: aload_0
      //   5848: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   5851: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   5854: invokevirtual 718	com/android/amaze_camera/HTCCamera:getContentResolver	()Landroid/content/ContentResolver;
      //   5857: astore 67
      //   5859: iconst_2
      //   5860: anewarray 192	java/lang/String
      //   5863: astore 68
      //   5865: aload 68
      //   5867: iconst_0
      //   5868: ldc_w 720
      //   5871: aastore
      //   5872: aload 68
      //   5874: iconst_1
      //   5875: ldc_w 722
      //   5878: aastore
      //   5879: aload 67
      //   5881: aload 66
      //   5883: aload 68
      //   5885: ldc_w 738
      //   5888: aconst_null
      //   5889: ldc_w 730
      //   5892: invokevirtual 736	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
      //   5895: astore 69
      //   5897: aload 69
      //   5899: ifnull +47 -> 5946
      //   5902: aload 69
      //   5904: invokeinterface 743 1 0
      //   5909: ifle +37 -> 5946
      //   5912: aload 69
      //   5914: invokeinterface 746 1 0
      //   5919: pop
      //   5920: aload 69
      //   5922: aload 69
      //   5924: ldc_w 722
      //   5927: invokeinterface 750 2 0
      //   5932: invokeinterface 754 2 0
      //   5937: astore 59
      //   5939: aload 69
      //   5941: invokeinterface 764 1 0
      //   5946: aload 59
      //   5948: ifnonnull +41 -> 5989
      //   5951: ldc 38
      //   5953: ldc_w 823
      //   5956: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   5959: goto -5655 -> 304
      //   5962: invokestatic 705	com/android/amaze_camera/DCFRuler$StorageCardControl:getStorageType	()I
      //   5965: iconst_1
      //   5966: if_icmpne -124 -> 5842
      //   5969: getstatic 760	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
      //   5972: astore 66
      //   5974: goto -132 -> 5842
      //   5977: aload_1
      //   5978: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   5981: checkcast 192	java/lang/String
      //   5984: astore 59
      //   5986: goto -40 -> 5946
      //   5989: ldc 38
      //   5991: new 40	java/lang/StringBuilder
      //   5994: dup
      //   5995: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   5998: ldc_w 808
      //   6001: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   6004: aload 59
      //   6006: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   6009: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   6012: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6015: aconst_null
      //   6016: astore 60
      //   6018: aload 59
      //   6020: iconst_3
      //   6021: invokestatic 811	android/media/ThumbnailUtils:createVideoThumbnail	(Ljava/lang/String;I)Landroid/graphics/Bitmap;
      //   6024: astore 65
      //   6026: aload 65
      //   6028: astore 60
      //   6030: new 784	android/os/Bundle
      //   6033: dup
      //   6034: invokespecial 785	android/os/Bundle:<init>	()V
      //   6037: astore 62
      //   6039: aload 62
      //   6041: ldc_w 787
      //   6044: aload 59
      //   6046: invokevirtual 790	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
      //   6049: aload_0
      //   6050: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6053: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   6056: invokevirtual 794	android/os/Handler:obtainMessage	()Landroid/os/Message;
      //   6059: astore 63
      //   6061: aload 63
      //   6063: bipush 50
      //   6065: putfield 36	android/os/Message:what	I
      //   6068: aload 63
      //   6070: iconst_1
      //   6071: putfield 85	android/os/Message:arg1	I
      //   6074: aload 63
      //   6076: aload 62
      //   6078: invokevirtual 798	android/os/Message:setData	(Landroid/os/Bundle;)V
      //   6081: aload 63
      //   6083: aload 60
      //   6085: putfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   6088: aload_0
      //   6089: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6092: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   6095: aload 63
      //   6097: invokevirtual 802	android/os/Handler:sendMessage	(Landroid/os/Message;)Z
      //   6100: pop
      //   6101: ldc 38
      //   6103: ldc_w 825
      //   6106: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6109: goto -5805 -> 304
      //   6112: astore 61
      //   6114: ldc 38
      //   6116: ldc_w 813
      //   6119: aload 61
      //   6121: invokestatic 376	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   6124: goto -94 -> 6030
      //   6127: getstatic 340	com/android/amaze_camera/CameraThread:mEnableTouchAEC	Z
      //   6130: ifeq -5826 -> 304
      //   6133: aload_0
      //   6134: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6137: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   6140: ifnull +55 -> 6195
      //   6143: aload_0
      //   6144: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6147: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   6150: invokevirtual 828	com/android/amaze_camera/HTCCamera:isBlockCaptureUI	()Z
      //   6153: iconst_1
      //   6154: if_icmpne +41 -> 6195
      //   6157: aload_0
      //   6158: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6161: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   6164: bipush 31
      //   6166: invokestatic 402	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
      //   6169: aload_0
      //   6170: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6173: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   6176: bipush 31
      //   6178: ldc2_w 181
      //   6181: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   6184: ldc 38
      //   6186: ldc_w 830
      //   6189: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   6192: goto -5888 -> 304
      //   6195: aload_0
      //   6196: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6199: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6202: ifnull +44 -> 6246
      //   6205: aload_0
      //   6206: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6209: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6212: ldc_w 335
      //   6215: ldc_w 348
      //   6218: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   6221: aload_0
      //   6222: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6225: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6228: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   6231: iconst_0
      //   6232: putstatic 340	com/android/amaze_camera/CameraThread:mEnableTouchAEC	Z
      //   6235: ldc 38
      //   6237: ldc_w 832
      //   6240: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   6243: goto -5939 -> 304
      //   6246: ldc 38
      //   6248: ldc_w 834
      //   6251: invokestatic 72	com/android/amaze_camera/LOG:E	(Ljava/lang/String;Ljava/lang/String;)V
      //   6254: goto -5950 -> 304
      //   6257: getstatic 351	com/android/amaze_camera/CameraThread:mEnableCAF	Z
      //   6260: ifne -5956 -> 304
      //   6263: aload_0
      //   6264: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6267: getfield 50	com/android/amaze_camera/CameraThread:mMode	I
      //   6270: ifeq -5966 -> 304
      //   6273: aload_0
      //   6274: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6277: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   6280: ifnull +17 -> 6297
      //   6283: aload_0
      //   6284: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6287: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   6290: invokevirtual 828	com/android/amaze_camera/HTCCamera:isBlockCaptureUI	()Z
      //   6293: iconst_1
      //   6294: if_icmpeq -5990 -> 304
      //   6297: aload_0
      //   6298: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6301: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6304: ifnull -6000 -> 304
      //   6307: ldc 38
      //   6309: ldc_w 836
      //   6312: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   6315: aload_0
      //   6316: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6319: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6322: ldc_w 346
      //   6325: ldc_w 337
      //   6328: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   6331: aload_0
      //   6332: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6335: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6338: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   6341: iconst_1
      //   6342: putstatic 351	com/android/amaze_camera/CameraThread:mEnableCAF	Z
      //   6345: goto -6041 -> 304
      //   6348: ldc 38
      //   6350: ldc_w 838
      //   6353: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6356: aload_1
      //   6357: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   6360: checkcast 192	java/lang/String
      //   6363: astore 57
      //   6365: aload_0
      //   6366: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6369: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6372: ifnull +28 -> 6400
      //   6375: aload_0
      //   6376: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6379: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6382: ldc_w 840
      //   6385: aload 57
      //   6387: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   6390: aload_0
      //   6391: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6394: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6397: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   6400: ldc 38
      //   6402: ldc_w 842
      //   6405: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6408: goto -6104 -> 304
      //   6411: ldc 38
      //   6413: ldc_w 844
      //   6416: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6419: aload_0
      //   6420: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6423: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6426: ifnull +65 -> 6491
      //   6429: aload_0
      //   6430: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6433: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6436: ldc_w 846
      //   6439: invokevirtual 849	com/android/amaze_camera/CameraController:setGpuEffectType	(Ljava/lang/String;)V
      //   6442: aload_0
      //   6443: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6446: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6449: ldc_w 851
      //   6452: iconst_1
      //   6453: iconst_0
      //   6454: iconst_0
      //   6455: iconst_0
      //   6456: iconst_1
      //   6457: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6460: pop
      //   6461: aload_0
      //   6462: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6465: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6468: ldc_w 857
      //   6471: iconst_0
      //   6472: iconst_0
      //   6473: iconst_0
      //   6474: iconst_0
      //   6475: iconst_0
      //   6476: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6479: pop
      //   6480: aload_0
      //   6481: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6484: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6487: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   6490: pop
      //   6491: ldc 38
      //   6493: ldc_w 862
      //   6496: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6499: goto -6195 -> 304
      //   6502: ldc 38
      //   6504: ldc_w 864
      //   6507: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6510: aload_0
      //   6511: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6514: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6517: ifnull +65 -> 6582
      //   6520: aload_0
      //   6521: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6524: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6527: ldc_w 846
      //   6530: invokevirtual 849	com/android/amaze_camera/CameraController:setGpuEffectType	(Ljava/lang/String;)V
      //   6533: aload_0
      //   6534: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6537: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6540: ldc_w 851
      //   6543: iconst_0
      //   6544: iconst_0
      //   6545: iconst_0
      //   6546: iconst_0
      //   6547: iconst_1
      //   6548: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6551: pop
      //   6552: aload_0
      //   6553: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6556: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6559: ldc_w 857
      //   6562: iconst_0
      //   6563: iconst_0
      //   6564: iconst_0
      //   6565: iconst_0
      //   6566: iconst_0
      //   6567: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6570: pop
      //   6571: aload_0
      //   6572: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6575: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6578: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   6581: pop
      //   6582: ldc 38
      //   6584: ldc_w 866
      //   6587: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6590: goto -6286 -> 304
      //   6593: ldc 38
      //   6595: ldc_w 868
      //   6598: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6601: aload_0
      //   6602: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6605: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6608: ifnull +65 -> 6673
      //   6611: aload_0
      //   6612: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6615: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6618: ldc_w 846
      //   6621: invokevirtual 849	com/android/amaze_camera/CameraController:setGpuEffectType	(Ljava/lang/String;)V
      //   6624: aload_0
      //   6625: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6628: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6631: ldc_w 851
      //   6634: iconst_2
      //   6635: iconst_0
      //   6636: iconst_0
      //   6637: iconst_0
      //   6638: iconst_1
      //   6639: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6642: pop
      //   6643: aload_0
      //   6644: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6647: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6650: ldc_w 857
      //   6653: iconst_0
      //   6654: iconst_0
      //   6655: iconst_0
      //   6656: iconst_0
      //   6657: iconst_0
      //   6658: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6661: pop
      //   6662: aload_0
      //   6663: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6666: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6669: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   6672: pop
      //   6673: ldc 38
      //   6675: ldc_w 870
      //   6678: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6681: goto -6377 -> 304
      //   6684: ldc 38
      //   6686: ldc_w 872
      //   6689: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6692: aload_0
      //   6693: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6696: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6699: ifnull +78 -> 6777
      //   6702: aload_0
      //   6703: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6706: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6709: ldc_w 874
      //   6712: invokevirtual 849	com/android/amaze_camera/CameraController:setGpuEffectType	(Ljava/lang/String;)V
      //   6715: aload_0
      //   6716: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6719: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6722: ldc_w 851
      //   6725: bipush 100
      //   6727: iconst_0
      //   6728: iconst_0
      //   6729: iconst_0
      //   6730: iconst_1
      //   6731: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6734: pop
      //   6735: aload_0
      //   6736: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6739: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6742: ldc_w 857
      //   6745: getstatic 877	com/android/amaze_camera/DisplayDevice:SCREEN_WIDTH	I
      //   6748: iconst_1
      //   6749: ishr
      //   6750: getstatic 880	com/android/amaze_camera/DisplayDevice:SCREEN_HEIGHT	I
      //   6753: iconst_1
      //   6754: ishr
      //   6755: sipush 450
      //   6758: sipush 290
      //   6761: iconst_1
      //   6762: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6765: pop
      //   6766: aload_0
      //   6767: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6770: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6773: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   6776: pop
      //   6777: ldc 38
      //   6779: ldc_w 882
      //   6782: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6785: goto -6481 -> 304
      //   6788: ldc 38
      //   6790: ldc_w 884
      //   6793: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6796: aload_0
      //   6797: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6800: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6803: ifnull +73 -> 6876
      //   6806: aload_0
      //   6807: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6810: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6813: ldc_w 886
      //   6816: invokevirtual 849	com/android/amaze_camera/CameraController:setGpuEffectType	(Ljava/lang/String;)V
      //   6819: aload_0
      //   6820: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6823: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6826: ldc_w 851
      //   6829: iconst_0
      //   6830: iconst_0
      //   6831: iconst_0
      //   6832: iconst_0
      //   6833: iconst_1
      //   6834: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6837: pop
      //   6838: aload_0
      //   6839: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6842: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6845: ldc_w 857
      //   6848: getstatic 877	com/android/amaze_camera/DisplayDevice:SCREEN_WIDTH	I
      //   6851: iconst_1
      //   6852: ishr
      //   6853: getstatic 880	com/android/amaze_camera/DisplayDevice:SCREEN_HEIGHT	I
      //   6856: iconst_1
      //   6857: ishr
      //   6858: iconst_0
      //   6859: iconst_0
      //   6860: iconst_1
      //   6861: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6864: pop
      //   6865: aload_0
      //   6866: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6869: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6872: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   6875: pop
      //   6876: ldc 38
      //   6878: ldc_w 888
      //   6881: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6884: goto -6580 -> 304
      //   6887: ldc 38
      //   6889: ldc_w 890
      //   6892: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6895: aload_0
      //   6896: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6899: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6902: ifnull +78 -> 6980
      //   6905: aload_0
      //   6906: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6909: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6912: ldc_w 892
      //   6915: invokevirtual 849	com/android/amaze_camera/CameraController:setGpuEffectType	(Ljava/lang/String;)V
      //   6918: aload_0
      //   6919: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6922: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6925: ldc_w 851
      //   6928: bipush 50
      //   6930: iconst_0
      //   6931: iconst_0
      //   6932: iconst_0
      //   6933: iconst_1
      //   6934: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6937: pop
      //   6938: aload_0
      //   6939: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6942: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6945: ldc_w 857
      //   6948: getstatic 877	com/android/amaze_camera/DisplayDevice:SCREEN_WIDTH	I
      //   6951: iconst_1
      //   6952: ishr
      //   6953: getstatic 880	com/android/amaze_camera/DisplayDevice:SCREEN_HEIGHT	I
      //   6956: iconst_1
      //   6957: ishr
      //   6958: sipush 530
      //   6961: sipush 370
      //   6964: iconst_1
      //   6965: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   6968: pop
      //   6969: aload_0
      //   6970: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   6973: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   6976: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   6979: pop
      //   6980: ldc 38
      //   6982: ldc_w 894
      //   6985: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6988: goto -6684 -> 304
      //   6991: ldc 38
      //   6993: ldc_w 896
      //   6996: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   6999: aload_0
      //   7000: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7003: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7006: ifnull +65 -> 7071
      //   7009: aload_0
      //   7010: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7013: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7016: ldc_w 846
      //   7019: invokevirtual 849	com/android/amaze_camera/CameraController:setGpuEffectType	(Ljava/lang/String;)V
      //   7022: aload_0
      //   7023: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7026: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7029: ldc_w 851
      //   7032: iconst_3
      //   7033: iconst_0
      //   7034: iconst_0
      //   7035: iconst_0
      //   7036: iconst_1
      //   7037: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   7040: pop
      //   7041: aload_0
      //   7042: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7045: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7048: ldc_w 857
      //   7051: iconst_0
      //   7052: iconst_0
      //   7053: iconst_0
      //   7054: iconst_0
      //   7055: iconst_0
      //   7056: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   7059: pop
      //   7060: aload_0
      //   7061: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7064: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7067: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   7070: pop
      //   7071: ldc_w 593
      //   7074: ldc_w 898
      //   7077: invokestatic 366	com/android/amaze_camera/LOG:I	(Ljava/lang/String;Ljava/lang/String;)V
      //   7080: ldc 38
      //   7082: ldc_w 900
      //   7085: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   7088: goto -6784 -> 304
      //   7091: ldc 38
      //   7093: ldc_w 902
      //   7096: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   7099: aload_0
      //   7100: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7103: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7106: ifnull +47 -> 7153
      //   7109: aload_0
      //   7110: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7113: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7116: ldc_w 904
      //   7119: invokevirtual 849	com/android/amaze_camera/CameraController:setGpuEffectType	(Ljava/lang/String;)V
      //   7122: aload_0
      //   7123: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7126: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7129: ldc_w 851
      //   7132: bipush 50
      //   7134: iconst_0
      //   7135: iconst_0
      //   7136: iconst_0
      //   7137: iconst_1
      //   7138: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   7141: pop
      //   7142: aload_0
      //   7143: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7146: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7149: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   7152: pop
      //   7153: ldc 38
      //   7155: ldc_w 906
      //   7158: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   7161: goto -6857 -> 304
      //   7164: ldc 38
      //   7166: ldc_w 908
      //   7169: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   7172: aload_0
      //   7173: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7176: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7179: ifnull +65 -> 7244
      //   7182: aload_0
      //   7183: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7186: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7189: ldc_w 910
      //   7192: invokevirtual 849	com/android/amaze_camera/CameraController:setGpuEffectType	(Ljava/lang/String;)V
      //   7195: aload_0
      //   7196: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7199: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7202: ldc_w 851
      //   7205: iconst_0
      //   7206: iconst_0
      //   7207: iconst_0
      //   7208: iconst_0
      //   7209: iconst_1
      //   7210: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   7213: pop
      //   7214: aload_0
      //   7215: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7218: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7221: ldc_w 857
      //   7224: iconst_0
      //   7225: iconst_0
      //   7226: iconst_0
      //   7227: iconst_0
      //   7228: iconst_0
      //   7229: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   7232: pop
      //   7233: aload_0
      //   7234: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7237: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7240: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   7243: pop
      //   7244: ldc 38
      //   7246: ldc_w 912
      //   7249: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   7252: goto -6948 -> 304
      //   7255: ldc 38
      //   7257: ldc_w 914
      //   7260: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   7263: aload_0
      //   7264: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7267: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7270: ifnull +78 -> 7348
      //   7273: aload_0
      //   7274: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7277: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7280: ldc_w 916
      //   7283: invokevirtual 849	com/android/amaze_camera/CameraController:setGpuEffectType	(Ljava/lang/String;)V
      //   7286: aload_0
      //   7287: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7290: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7293: ldc_w 851
      //   7296: bipush 100
      //   7298: iconst_0
      //   7299: iconst_0
      //   7300: iconst_0
      //   7301: iconst_1
      //   7302: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   7305: pop
      //   7306: aload_0
      //   7307: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7310: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7313: ldc_w 857
      //   7316: getstatic 877	com/android/amaze_camera/DisplayDevice:SCREEN_WIDTH	I
      //   7319: iconst_1
      //   7320: ishr
      //   7321: getstatic 880	com/android/amaze_camera/DisplayDevice:SCREEN_HEIGHT	I
      //   7324: iconst_1
      //   7325: ishr
      //   7326: sipush 450
      //   7329: sipush 290
      //   7332: iconst_1
      //   7333: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   7336: pop
      //   7337: aload_0
      //   7338: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7341: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7344: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   7347: pop
      //   7348: ldc 38
      //   7350: ldc_w 918
      //   7353: invokestatic 63	com/android/amaze_camera/LOG:V	(Ljava/lang/String;Ljava/lang/String;)V
      //   7356: goto -7052 -> 304
      //   7359: aload_0
      //   7360: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7363: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7366: ifnull -7062 -> 304
      //   7369: aload_0
      //   7370: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7373: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7376: ldc_w 920
      //   7379: invokevirtual 849	com/android/amaze_camera/CameraController:setGpuEffectType	(Ljava/lang/String;)V
      //   7382: aload_0
      //   7383: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7386: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7389: ldc_w 851
      //   7392: iconst_0
      //   7393: iconst_0
      //   7394: iconst_0
      //   7395: iconst_0
      //   7396: iconst_0
      //   7397: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   7400: pop
      //   7401: aload_0
      //   7402: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7405: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7408: ldc_w 857
      //   7411: iconst_0
      //   7412: iconst_0
      //   7413: iconst_0
      //   7414: iconst_0
      //   7415: iconst_0
      //   7416: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   7419: pop
      //   7420: aload_0
      //   7421: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7424: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7427: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   7430: pop
      //   7431: goto -7127 -> 304
      //   7434: aload_0
      //   7435: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7438: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7441: ifnull -7137 -> 304
      //   7444: aload_1
      //   7445: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   7448: checkcast 784	android/os/Bundle
      //   7451: astore 20
      //   7453: aload 20
      //   7455: ifnull -7151 -> 304
      //   7458: aload 20
      //   7460: ldc_w 922
      //   7463: invokevirtual 926	android/os/Bundle:getIntArray	(Ljava/lang/String;)[I
      //   7466: astore 21
      //   7468: aload 20
      //   7470: ldc_w 928
      //   7473: invokevirtual 931	android/os/Bundle:getBoolean	(Ljava/lang/String;)Z
      //   7476: istore 22
      //   7478: aload 21
      //   7480: ifnull -7176 -> 304
      //   7483: aload_0
      //   7484: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7487: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7490: ldc_w 857
      //   7493: aload 21
      //   7495: iconst_0
      //   7496: iaload
      //   7497: aload 21
      //   7499: iconst_1
      //   7500: iaload
      //   7501: aload 21
      //   7503: iconst_2
      //   7504: iaload
      //   7505: aload 21
      //   7507: iconst_3
      //   7508: iaload
      //   7509: iload 22
      //   7511: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   7514: pop
      //   7515: aload_0
      //   7516: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7519: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7522: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   7525: pop
      //   7526: goto -7222 -> 304
      //   7529: aload_0
      //   7530: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7533: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7536: ifnull -7232 -> 304
      //   7539: aload_0
      //   7540: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7543: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7546: ldc_w 851
      //   7549: aload_1
      //   7550: getfield 85	android/os/Message:arg1	I
      //   7553: iconst_0
      //   7554: iconst_0
      //   7555: iconst_0
      //   7556: iconst_1
      //   7557: invokevirtual 855	com/android/amaze_camera/CameraController:setGEParam	(Ljava/lang/String;IIIIZ)Z
      //   7560: pop
      //   7561: aload_0
      //   7562: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7565: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7568: invokevirtual 860	com/android/amaze_camera/CameraController:injectGEParam	()Z
      //   7571: pop
      //   7572: goto -7268 -> 304
      //   7575: aload_0
      //   7576: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7579: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7582: ifnull -7278 -> 304
      //   7585: aload_1
      //   7586: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   7589: checkcast 192	java/lang/String
      //   7592: astore 16
      //   7594: aload_1
      //   7595: getfield 85	android/os/Message:arg1	I
      //   7598: istore 17
      //   7600: ldc_w 631
      //   7603: aload 16
      //   7605: invokevirtual 591	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   7608: ifne +25 -> 7633
      //   7611: ldc_w 635
      //   7614: aload 16
      //   7616: invokevirtual 591	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   7619: ifne +14 -> 7633
      //   7622: ldc_w 639
      //   7625: aload 16
      //   7627: invokevirtual 591	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   7630: ifeq +30 -> 7660
      //   7633: aload_0
      //   7634: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7637: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7640: aload 16
      //   7642: iload 17
      //   7644: invokevirtual 629	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;I)V
      //   7647: aload_0
      //   7648: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7651: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7654: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   7657: goto -7353 -> 304
      //   7660: ldc_w 597
      //   7663: aload 16
      //   7665: invokevirtual 591	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   7668: ifeq -21 -> 7647
      //   7671: aload_0
      //   7672: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7675: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7678: iload 17
      //   7680: invokevirtual 604	com/android/amaze_camera/CameraController:setExposureCompensation	(I)V
      //   7683: goto -36 -> 7647
      //   7686: aload_0
      //   7687: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7690: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7693: ifnull -7389 -> 304
      //   7696: aload_0
      //   7697: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7700: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   7703: invokevirtual 828	com/android/amaze_camera/HTCCamera:isBlockCaptureUI	()Z
      //   7706: ifeq +33 -> 7739
      //   7709: aload_0
      //   7710: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7713: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   7716: bipush 43
      //   7718: invokestatic 402	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
      //   7721: aload_0
      //   7722: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7725: getfield 157	com/android/amaze_camera/CameraThread:mCameraHandler	Lcom/android/amaze_camera/CameraThread$MainHandler;
      //   7728: bipush 43
      //   7730: ldc2_w 181
      //   7733: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   7736: goto -7432 -> 304
      //   7739: aload_0
      //   7740: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7743: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   7746: invokevirtual 934	com/android/amaze_camera/HTCCamera:isUIFreeze	()Z
      //   7749: ifeq +68 -> 7817
      //   7752: aload_0
      //   7753: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7756: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   7759: invokevirtual 937	com/android/amaze_camera/HTCCamera:getFreezeOrientation	()I
      //   7762: istore 13
      //   7764: iload 13
      //   7766: bipush 255
      //   7768: if_icmpne +64 -> 7832
      //   7771: ldc 38
      //   7773: ldc_w 939
      //   7776: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   7779: iconst_0
      //   7780: istore 14
      //   7782: iload 14
      //   7784: invokestatic 945	com/android/amaze_camera/ImageManager:roundOrientation	(I)I
      //   7787: istore 15
      //   7789: aload_0
      //   7790: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7793: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7796: ldc_w 947
      //   7799: iload 15
      //   7801: invokevirtual 629	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;I)V
      //   7804: aload_0
      //   7805: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7808: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7811: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   7814: goto -7510 -> 304
      //   7817: aload_0
      //   7818: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7821: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   7824: invokevirtual 950	com/android/amaze_camera/HTCCamera:getLastOrientation	()I
      //   7827: istore 13
      //   7829: goto -65 -> 7764
      //   7832: iload 13
      //   7834: bipush 90
      //   7836: iadd
      //   7837: istore 14
      //   7839: goto -57 -> 7782
      //   7842: aload_0
      //   7843: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7846: invokestatic 953	com/android/amaze_camera/CameraThread:access$3000	(Lcom/android/amaze_camera/CameraThread;)V
      //   7849: goto -7545 -> 304
      //   7852: aload_0
      //   7853: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7856: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7859: ifnull -7555 -> 304
      //   7862: aload_0
      //   7863: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7866: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7869: ldc_w 955
      //   7872: invokevirtual 959	com/android/amaze_camera/CameraController:getSettingsInfo	(Ljava/lang/String;)Lcom/android/amaze_camera/CameraController$SettingInfo;
      //   7875: astore 11
      //   7877: aload_0
      //   7878: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7881: invokevirtual 962	com/android/amaze_camera/CameraThread:is3DMode	()Z
      //   7884: istore 12
      //   7886: aload_0
      //   7887: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7890: invokestatic 965	com/android/amaze_camera/CameraThread:access$3100	(Lcom/android/amaze_camera/CameraThread;)Z
      //   7893: ifne +50 -> 7943
      //   7896: iload 12
      //   7898: ifne +45 -> 7943
      //   7901: aload_0
      //   7902: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7905: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   7908: invokevirtual 968	com/android/amaze_camera/HTCCamera:getFaceNumber	()I
      //   7911: ifne +32 -> 7943
      //   7914: aload_0
      //   7915: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7918: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7921: ldc_w 955
      //   7924: getstatic 971	com/android/amaze_camera/HTCCameraAdvanceSetting:mZoomValue	I
      //   7927: invokevirtual 629	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;I)V
      //   7930: aload_0
      //   7931: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7934: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7937: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   7940: goto -7636 -> 304
      //   7943: aload_0
      //   7944: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7947: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   7950: ldc_w 955
      //   7953: aload 11
      //   7955: invokevirtual 976	com/android/amaze_camera/CameraController$SettingInfo:getMin	()I
      //   7958: invokevirtual 629	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;I)V
      //   7961: goto -31 -> 7930
      //   7964: ldc 38
      //   7966: ldc_w 978
      //   7969: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   7972: aload_0
      //   7973: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7976: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   7979: invokevirtual 981	com/android/amaze_camera/HTCCamera:checkInternalStorage	()Z
      //   7982: ifne +40 -> 8022
      //   7985: ldc 38
      //   7987: ldc_w 983
      //   7990: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   7993: aload_0
      //   7994: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   7997: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   8000: bipush 37
      //   8002: invokestatic 402	com/android/amaze_camera/MessageHandler:removeMessages	(Landroid/os/Handler;I)V
      //   8005: aload_0
      //   8006: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8009: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   8012: bipush 37
      //   8014: ldc_w 984
      //   8017: iconst_0
      //   8018: aconst_null
      //   8019: invokestatic 261	com/android/amaze_camera/MessageHandler:sendObtainMessage	(Landroid/os/Handler;IIILjava/lang/Object;)V
      //   8022: ldc 38
      //   8024: ldc_w 986
      //   8027: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   8030: goto -7726 -> 304
      //   8033: ldc 38
      //   8035: ldc_w 988
      //   8038: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   8041: aload_0
      //   8042: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8045: invokestatic 991	com/android/amaze_camera/CameraThread:access$3200	(Lcom/android/amaze_camera/CameraThread;)V
      //   8048: getstatic 677	com/android/amaze_camera/CameraThread:Storage_Status	I
      //   8051: ifne +51 -> 8102
      //   8054: aload_0
      //   8055: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8058: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   8061: astore 8
      //   8063: invokestatic 994	com/android/amaze_camera/DisplayDevice:showFocusWithoutDelay	()Z
      //   8066: ifeq +28 -> 8094
      //   8069: ldc2_w 995
      //   8072: lstore 9
      //   8074: aload 8
      //   8076: bipush 30
      //   8078: lload 9
      //   8080: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   8083: ldc 38
      //   8085: ldc_w 998
      //   8088: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   8091: goto -7787 -> 304
      //   8094: ldc2_w 158
      //   8097: lstore 9
      //   8099: goto -25 -> 8074
      //   8102: aload_0
      //   8103: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8106: getfield 200	com/android/amaze_camera/CameraThread:mUIHandler	Landroid/os/Handler;
      //   8109: astore 5
      //   8111: invokestatic 994	com/android/amaze_camera/DisplayDevice:showFocusWithoutDelay	()Z
      //   8114: ifeq +20 -> 8134
      //   8117: ldc2_w 995
      //   8120: lstore 6
      //   8122: aload 5
      //   8124: bipush 30
      //   8126: lload 6
      //   8128: invokestatic 219	com/android/amaze_camera/MessageHandler:sendEmptyMessageDelayed	(Landroid/os/Handler;IJ)V
      //   8131: goto -48 -> 8083
      //   8134: ldc2_w 158
      //   8137: lstore 6
      //   8139: goto -17 -> 8122
      //   8142: ldc 38
      //   8144: ldc_w 1000
      //   8147: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   8150: aload_0
      //   8151: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8154: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   8157: ifnull +60 -> 8217
      //   8160: aload_1
      //   8161: getfield 190	android/os/Message:obj	Ljava/lang/Object;
      //   8164: checkcast 192	java/lang/String
      //   8167: astore 4
      //   8169: ldc 38
      //   8171: new 40	java/lang/StringBuilder
      //   8174: dup
      //   8175: invokespecial 41	java/lang/StringBuilder:<init>	()V
      //   8178: ldc_w 1002
      //   8181: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   8184: aload 4
      //   8186: invokevirtual 47	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   8189: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   8192: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   8195: aload_0
      //   8196: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8199: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   8202: aload 4
      //   8204: invokevirtual 1005	com/android/amaze_camera/CameraController:setSceneMode	(Ljava/lang/String;)V
      //   8207: aload_0
      //   8208: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8211: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   8214: invokevirtual 354	com/android/amaze_camera/CameraController:doSetCameraParameters	()V
      //   8217: ldc 38
      //   8219: ldc_w 1007
      //   8222: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   8225: goto -7921 -> 304
      //   8228: aload_0
      //   8229: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8232: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   8235: ldc_w 1009
      //   8238: invokestatic 100	com/android/amaze_camera/HTCCameraAdvanceSetting:getPrefenceBoolean	(Landroid/app/Activity;Ljava/lang/String;)Ljava/lang/Boolean;
      //   8241: invokevirtual 106	java/lang/Boolean:booleanValue	()Z
      //   8244: istore_3
      //   8245: aload_0
      //   8246: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8249: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   8252: ifnull -7948 -> 304
      //   8255: iload_3
      //   8256: ifeq +22 -> 8278
      //   8259: aload_0
      //   8260: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8263: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   8266: ldc_w 1011
      //   8269: ldc_w 1013
      //   8272: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   8275: goto -7971 -> 304
      //   8278: aload_0
      //   8279: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8282: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   8285: ldc_w 1015
      //   8288: invokestatic 100	com/android/amaze_camera/HTCCameraAdvanceSetting:getPrefenceBoolean	(Landroid/app/Activity;Ljava/lang/String;)Ljava/lang/Boolean;
      //   8291: invokevirtual 106	java/lang/Boolean:booleanValue	()Z
      //   8294: ifeq +22 -> 8316
      //   8297: aload_0
      //   8298: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8301: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   8304: ldc_w 1011
      //   8307: ldc_w 1017
      //   8310: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   8313: goto -8009 -> 304
      //   8316: aload_0
      //   8317: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8320: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   8323: ldc_w 1011
      //   8326: ldc_w 1019
      //   8329: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   8332: goto -8028 -> 304
      //   8335: ldc 38
      //   8337: ldc_w 1021
      //   8340: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   8343: aload_0
      //   8344: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8347: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   8350: ldc_w 1015
      //   8353: invokestatic 100	com/android/amaze_camera/HTCCameraAdvanceSetting:getPrefenceBoolean	(Landroid/app/Activity;Ljava/lang/String;)Ljava/lang/Boolean;
      //   8356: invokevirtual 106	java/lang/Boolean:booleanValue	()Z
      //   8359: istore_2
      //   8360: aload_0
      //   8361: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8364: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   8367: ifnull +23 -> 8390
      //   8370: iload_2
      //   8371: ifeq +30 -> 8401
      //   8374: aload_0
      //   8375: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8378: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   8381: ldc_w 1011
      //   8384: ldc_w 1017
      //   8387: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   8390: ldc 38
      //   8392: ldc_w 1023
      //   8395: invokestatic 150	com/android/amaze_camera/LOG:W	(Ljava/lang/String;Ljava/lang/String;)V
      //   8398: goto -8094 -> 304
      //   8401: aload_0
      //   8402: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8405: invokestatic 76	com/android/amaze_camera/CameraThread:access$000	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/HTCCamera;
      //   8408: ldc_w 1009
      //   8411: invokestatic 100	com/android/amaze_camera/HTCCameraAdvanceSetting:getPrefenceBoolean	(Landroid/app/Activity;Ljava/lang/String;)Ljava/lang/Boolean;
      //   8414: invokevirtual 106	java/lang/Boolean:booleanValue	()Z
      //   8417: ifeq +22 -> 8439
      //   8420: aload_0
      //   8421: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8424: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   8427: ldc_w 1011
      //   8430: ldc_w 1013
      //   8433: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   8436: goto -46 -> 8390
      //   8439: aload_0
      //   8440: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8443: invokestatic 297	com/android/amaze_camera/CameraThread:access$1500	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/CameraController;
      //   8446: ldc_w 1011
      //   8449: ldc_w 1019
      //   8452: invokevirtual 333	com/android/amaze_camera/CameraController:setCameraParameter	(Ljava/lang/String;Ljava/lang/String;)V
      //   8455: goto -65 -> 8390
      //   8458: aload_0
      //   8459: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8462: invokestatic 1027	com/android/amaze_camera/CameraThread:access$3300	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/EventManager;
      //   8465: ifnull -8161 -> 304
      //   8468: aload_0
      //   8469: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8472: invokestatic 1027	com/android/amaze_camera/CameraThread:access$3300	(Lcom/android/amaze_camera/CameraThread;)Lcom/android/amaze_camera/EventManager;
      //   8475: ldc_w 1029
      //   8478: invokevirtual 1034	com/android/amaze_camera/EventManager:raiseEvent	(Ljava/lang/String;)V
      //   8481: goto -8177 -> 304
      //   8484: aload_0
      //   8485: getfield 14	com/android/amaze_camera/CameraThread$MainHandler:this$0	Lcom/android/amaze_camera/CameraThread;
      //   8488: aload_1
      //   8489: getfield 85	android/os/Message:arg1	I
      //   8492: invokevirtual 1037	com/android/amaze_camera/CameraThread:changeZoom	(I)V
      //   8495: goto -8191 -> 304
      //   8498: aconst_null
      //   8499: astore 88
      //   8501: goto -4007 -> 4494
      //
      // Exception table:
      //   from	to	target	type
      //   1569	1579	1680	java/lang/Exception
      //   1709	1775	1786	java/lang/Exception
      //   1854	1864	1899	java/lang/Exception
      //   1987	2007	2131	java/lang/InterruptedException
      //   2276	2282	2391	java/lang/Exception
      //   2610	2630	2736	java/lang/InterruptedException
      //   4656	4720	4855	android/database/sqlite/SQLiteDiskIOException
      //   4656	4720	4887	java/lang/Exception
      //   4750	4796	4930	android/database/sqlite/SQLiteDiskIOException
      //   4750	4796	4962	java/lang/Exception
      //   5145	5179	5297	java/lang/Exception
      //   5145	5179	5312	java/lang/OutOfMemoryError
      //   5333	5367	5381	java/lang/Exception
      //   5333	5367	5396	java/lang/OutOfMemoryError
      //   5509	5554	5701	android/database/sqlite/SQLiteDiskIOException
      //   5509	5554	5733	java/lang/Exception
      //   5554	5588	5765	java/lang/Exception
      //   5554	5588	5780	java/lang/OutOfMemoryError
      //   6018	6026	6112	java/lang/Exception
    }
  }

  public class CommonCaptureHandler
    implements ICaptureHandler
  {
    public CommonCaptureHandler()
    {
    }

    public void onJpegPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
      LOG.V("CameraThread", "got JpegPictureCallback...");
      TIME.JpegCallback.End();
      TIME.StoreJpegImage.Start();
      CameraThread.this.endTakePicture();
      if (!CameraThread.this.mCameraActivity.needsActionScreen())
      {
        CameraThread.this.mCameraActivity.showActionScreen();
        if ((CameraThread.this.mCaptureState == 0) && (CameraThread.this.mCameraActivity.isUIReady()))
          CameraThread.this.startPreview(0);
      }
      CameraThread.this.storeTakenPicture(paramArrayOfByte, true);
      LOG.V("CameraThread", "JpegPictureCallback end");
    }

    public void onPostviewPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
      if (!DisplayDevice.isDoubleShot());
      while (true)
      {
        return;
        LOG.V("CameraThread", "got PostViewPictureCallback...");
        CameraThread.this.createPostViewImage(paramArrayOfByte);
        LOG.V("CameraThread", "RawPictureCallback end");
      }
    }

    public void onRawPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
      LOG.V("CameraThread", "got RawPictureCallback...");
      TIME.RawCallback.End();
      TIME.JpegCallback.Start();
      if (CameraThread.this.mCameraActivity.needsActionScreen())
        CameraThread.this.mCameraActivity.showActionScreen(250);
      LOG.V("CameraThread", "RawPictureCallback end");
    }

    public void onShutter()
    {
      if (CameraThread.this.isShutterSoundNeeded())
        CameraThread.this.playShutterSound();
      TIME.ShutterCallback.End();
      TIME.RawCallback.Start();
    }

    public void takePicture(CameraThread paramCameraThread, Camera paramCamera)
    {
      TIME.ShutterCallback.Start();
      paramCamera.takePicture(new Camera.ShutterCallback()
      {
        public void onShutter()
        {
          CameraThread.CommonCaptureHandler.this.onShutter();
        }
      }
      , new Camera.PictureCallback()
      {
        public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
        {
          CameraThread.CommonCaptureHandler.this.onRawPictureTaken(paramArrayOfByte, paramCamera);
        }
      }
      , new Camera.PictureCallback()
      {
        public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
        {
          CameraThread.CommonCaptureHandler.this.onPostviewPictureTaken(paramArrayOfByte, paramCamera);
        }
      }
      , new Camera.PictureCallback()
      {
        public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
        {
          CameraThread.CommonCaptureHandler.this.onJpegPictureTaken(paramArrayOfByte, paramCamera);
        }
      });
    }
  }

  public static abstract interface StorePictureCallback
  {
    public abstract void onError(byte[] paramArrayOfByte, Exception paramException);

    public abstract void onPictureStored(byte[] paramArrayOfByte, Uri paramUri, String paramString);
  }
}

/* Location:           /Users/TwistedZero/android-utility/working-folder/mod-here-multi/dex2jar.jar
 * Qualified Name:     com.android.amaze_camera.CameraThread
 * JD-Core Version:    0.6.0
 */