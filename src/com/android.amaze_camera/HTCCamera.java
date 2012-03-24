package com.android.amaze_camera;

import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.android.amaze_camera.actionscreen.ActionScreen;
import com.android.amaze_camera.actionscreen.ActionScreenEvent;
import com.android.amaze_camera.actionscreen.CommonActionScreen;
import com.android.amaze_camera.actionscreen.RequestActionScreen;
import com.android.amaze_camera.component.AutoSceneController;
import com.android.amaze_camera.component.AutoSceneUI;
import com.android.amaze_camera.component.BatteryWatcher;
import com.android.amaze_camera.component.BurstController;
import com.android.amaze_camera.component.BurstUI;
import com.android.amaze_camera.component.Component;
import com.android.amaze_camera.component.ComponentManager;
import com.android.amaze_camera.component.DOTIndicatorController;
import com.android.amaze_camera.component.DOTIndicatorUI;
import com.android.amaze_camera.component.HandShakeController;
import com.android.amaze_camera.component.HandShakeUI;
import com.android.amaze_camera.component.HappyShotController;
import com.android.amaze_camera.component.HappyShotUI;
import com.android.amaze_camera.component.HdrController;
import com.android.amaze_camera.component.HdrUI;
import com.android.amaze_camera.component.HwCameraSwitchButton;
import com.android.amaze_camera.component.IComponentOwner;
import com.android.amaze_camera.component.MediaScannerWatcher;
import com.android.amaze_camera.component.PanoramaController;
import com.android.amaze_camera.component.PanoramaUI;
import com.android.amaze_camera.component.PowerWarningController;
import com.android.amaze_camera.component.PowerWarningUI;
import com.android.amaze_camera.component.RemovableStorageWatcher;
import com.android.amaze_camera.component.ReviewAnimation;
import com.android.amaze_camera.component.ThumbnailUI;
import com.android.amaze_camera.component.ZoomBarController;
import com.android.amaze_camera.component.ZoomBarUI;
import com.android.amaze_camera.rotate.OrientationConfig;
import com.android.amaze_camera.rotate.RotateDialog;
import com.android.amaze_camera.rotate.RotateDialog.Builder;
import com.android.amaze_camera.rotate.RotateImageView;
import com.android.amaze_camera.rotate.RotateLinearLayout;
import com.android.amaze_camera.rotate.RotateRelativeLayout;
import com.android.amaze_camera.rotate.RotateToast;
import com.android.amaze_camera.sceneselector.SceneSelectorActivity;
import com.android.amaze_camera.widget.CameraFocusWidget;
import com.android.amaze_camera.widget.Panel;
import com.android.amaze_camera.widget.Panel.OnPanelListener;
import com.android.amaze_camera.widget.ScrollInterface.PositionChangeListner;
import com.android.amaze_camera.widget.SlidingDrawer;
import com.android.amaze_camera.widget.SlidingDrawer.OnCaptureListener;
import com.android.amaze_camera.widget.SlidingDrawer.OnDrawerCloseListener;
import com.android.amaze_camera.widget.SlidingDrawer.OnDrawerOpenListener;
import com.android.amaze_camera.widget.SlidingDrawer.OnDrawerScrollListener;
import com.android.amaze_camera.widget.SlidingMenu;
import com.android.amaze_camera.widget.SlidingMenu.OnItemClickListener;
import com.android.amaze_camera.widget.SlidingMenu.OnMenuStateChangedListener;
import com.android.amaze_camera.widget.SlidingMenuItem;
import com.android.amaze_camera.widget.ZoomBar;
import dalvik.system.VMRuntime;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HTCCamera extends Activity
  implements SurfaceHolder.Callback, IComponentOwner, IEventManagerOwner, IEventHandler
{
  public static final String ACTION_ORIENTATION_LIGHT = "com.htc.content.intent.action.ORIENTATION_LIGHT";
  public static final int ACTION_SCREEN_FADEOUT = 39;
  public static final int AUTO_SMILE_CAPTURE = 83;
  private static final int BEEP_NO_SOUND = 255;
  private static final int BEEP_TIME = 200;
  public static final int CAMERA_THREAD_RUNNING = 102;
  public static final int CANCEL_FOCUS_END = 29;
  private static final int CAPTURE_BEEP_COUNT = 5;
  public static final int CHANGE_INDICATOR_LAYOUT = 11;
  public static final int CHECK_FACE_DETECTION = 35;
  public static final int CHECK_SCENE_CHANGE = 32;
  public static final int CLEAN_FACE_FOCUS = 54;
  public static final int CLOSE_CAPTURE_UI = 16;
  public static final int CLOSE_EXTENSION_MENUS = 100;
  public static final int CLOSE_SELF_TIMER = 70;
  public static final int COUNT_DOWN_SELFTIMER = 5;
  public static final int DOT_SCENE_ACTION = 6;
  public static final int DOT_SCENE_AUTO = 0;
  public static final int DOT_SCENE_BURST = 4;
  public static final int DOT_SCENE_HAPPYSHOT = 1;
  public static final int DOT_SCENE_HDR = 3;
  public static final int DOT_SCENE_MACRO = 7;
  public static final int DOT_SCENE_MANUAL = 9;
  public static final int DOT_SCENE_NIGHT = 5;
  public static final int DOT_SCENE_PANORAMA = 2;
  public static final int DOT_SCENE_PORTRAIT = 8;
  public static final int DO_CREATE_AFTER_PREVIEW = 46;
  public static final int DO_CREATE_RESUME_AFTER_PREVIEW = 45;
  public static final int DO_RESUME_AFTER_PREVIEW = 47;
  public static final int EFFECT_CONTROL_TIMEOUT = 5000;
  public static final int EFFECT_PANEL_FADEOUT = 59;
  public static final int EFFECT_SWITCH_CAMERA = 67;
  public static final int ENABLE_GARBAGE_COLLECTION = 56;
  public static final int ENABLE_SENSOR_FOCUS = 30;
  public static final String EVENT_ACTION_SCREEN_CLOSED = "ActionScreen.Closed";
  public static final String EVENT_ACTION_SCREEN_CLOSING = "ActionScreen.Closing";
  public static final String EVENT_ACTION_SCREEN_OPEN = "ActionScreen.Open";
  public static final String EVENT_AUTOSCENE_ENABLED = "AutoScene.Enabled";
  public static final String EVENT_CAMERA_THREAD_RUNNING = "CameraActivity.CameraThreadRunning";
  public static final String EVENT_CAPTURE_UI_CLOSED = "CaptureUI.Closed";
  public static final String EVENT_CAPTURE_UI_OPEN = "CaptureUI.Open";
  public static final String EVENT_CAPTURE_UI_UNBLOCK = "CaptureUI.Unblock";
  public static final String EVENT_DEVICE_ORIENTATION_CHANGED = "DeviceOrientation.Changed";
  public static final String EVENT_EFFECT_PANEL_CLOSED = "EffectPanel.Closed";
  public static final String EVENT_EFFECT_PANEL_OPENING = "EffectPanel.Opening";
  public static final String EVENT_FOCUS_FINISHED = "Focus.Finished";
  public static final String EVENT_KEY_DOWN = "Key.Down";
  public static final String EVENT_KEY_UP = "Key.Up";
  public static final String EVENT_MEDIA_SCANNER_FINISHED = "MediaScanner.Finished";
  public static final String EVENT_MEDIA_SCANNER_STARTED = "MediaScanner.Started";
  public static final String EVENT_MENU_CLOSED = "Menu.Closed";
  public static final String EVENT_MENU_OPENING = "Menu.Opening";
  public static final String EVENT_NEW_INTENT = "CameraActivity.NewIntent";
  public static final String EVENT_PAUSING = "CameraActivity.Pausing";
  public static final String EVENT_PREVIEW_STARTED = "CameraActivity.PreviewStarted";
  public static final String EVENT_PROTRAIT_MODE_CHANGED = "PortraitMode.Changed";
  public static final String EVENT_QUERY_CONFIRM_DELETING_MEDIA_MESSAGE = "Query.ConfirmDeletingMediaMessage";
  public static final String EVENT_QUERY_IS_ZOOM_LOCK_NEEDED = "Query.IsZoomLockNeeded";
  public static final String EVENT_RECORD_WITH_AUDIO_CHANGED = "RecordWithAudio.Changed";
  public static final String EVENT_REMAINING_HIDE_ANIMATION_END = "RemainingLayout.HideEnd";
  public static final String EVENT_REMAINING_SHOW_ANIMATION_START = "RemainingLayout.ShowStart";
  public static final String EVENT_REMOVABLE_STORAGE_EJECTED = "RemovableStorage.Ejected";
  public static final String EVENT_REMOVABLE_STORAGE_MOUNTED = "RemovableStorage.Mounted";
  public static final String EVENT_REMOVABLE_STORAGE_UNMOUNTED = "RemovableStorage.Unmounted";
  public static final String EVENT_REQUEST_RESET_TO_DEFAULT = "Request.ResetToDefault";
  public static final String EVENT_RESTART_CAMERA = "Camera.Restart";
  public static final String EVENT_RESUMING = "CameraActivity.Resuming";
  public static final String EVENT_ROTATION_CHANGED = "CameraActivity.RotateChanged";
  public static final String EVENT_SECOND_LAYOUT_CLOSE = "SecondLayout.Closed";
  public static final String EVENT_SECOND_LAYOUT_OPEN = "SecondLayout.Opened";
  public static final String EVENT_SELF_TIMER_CHANGED = "SelfTimer.Changed";
  public static final String EVENT_SENSOR_VALUE_CHANGED = "SensorValue.RotateChanged";
  public static final String EVENT_SWITCH_3D_MODE = "3DMode.Switched";
  public static final String EVENT_SWITCH_MODE = "Mode.Switched";
  public static final String EVENT_THUMBNAIL_BUTTON_CLICKED = "ThumbnailButton.Clicked";
  public static final String EVENT_TRIGGER_RECORD_STARTED = "TriggerRecord.Started";
  public static final String EVENT_TRIGGER_RECORD_STOPPED = "TriggerRecord.Stopped";
  public static final String EVENT_VOL_DECREASE_ZOOM = "Zoom.Decreased";
  public static final String EVENT_VOL_INCREASE_ZOOM = "Zoom.Increased";
  public static final String EVENT_ZOOM_CHANGED = "Zoom.Changed";
  public static final String EVENT_ZOOM_LOCKED_CHANGED = "Zoom.LockedStateChanged";
  public static final int EXTENSION_MENU_TIMEOUT = 4000;
  public static final String EXTRA_MANUAL = "manual";
  public static final String EXTRA_ORIENTATION = "orientation";
  public static final String EXTRA_PACKAGE_NAME = "package_name";
  public static final int FADEOUT_SCENE_HDR = 85;
  public static final int FADEOUT_SCENE_LANDSCAPE = 76;
  public static final int FADEOUT_SCENE_PORTRAIT = 74;
  public static final int FADEOUT_SCENE_SUN_NIGHT = 75;
  public static final int FINISH_FOCUS = 28;
  public static final int FOCUSING_NO_ACTION = 1;
  public static final int FOCUSING_PRESS_CAPTURE = 3;
  public static final int FOCUSING_PRESS_RECORD = 4;
  public static final int FOCUSING_TAP_CAPTURE = 2;
  public static final int FOCUS_MODE_FACE = 2;
  public static final int FOCUS_MODE_SENSOR = 0;
  public static final int FOCUS_MODE_TOUCH = 1;
  public static final int FOCUS_MODE_WAIT_FACE = 3;
  public static final int HIDE_CAPTURE_REVIEW = 58;
  public static final int HIDE_EFFECT_CONTROL = 60;
  public static final int HTC_CAM_SWITCH_CHANGE = 14;
  public static final int HW_3D_BUTTON_SWITCH = 72;
  private static final int ID_MENU_ITEM_FLASH_AUTO = 1;
  private static final int ID_MENU_ITEM_FLASH_OFF = 3;
  private static final int ID_MENU_ITEM_FLASH_ON = 2;
  private static final int ID_MENU_ITEM_MAIN_CAMCORDER = 2;
  private static final int ID_MENU_ITEM_MAIN_CAMERA = 0;
  private static final int ID_MENU_ITEM_SECOND_CAMCORDER = 3;
  private static final int ID_MENU_ITEM_SECOND_CAMERA = 1;
  public static final int INCREASE_ZOOM_POSITION = 55;
  public static final int INCREASE_ZOOM_TIMEOUT = 150;
  public static final String INTENT_ACTION_ALBUM = "FROM_CAMERA";
  private static final int INTENT_ALBUM_RESULT = 0;
  private static final String INTENT_KEY_JPEG_DATA = "jpeg_data";
  public static final String INTENT_MODE_CAMCORDER = "comcorder";
  public static final String INTENT_MODE_CAMERA = "camera";
  public static final String INTENT_MODE_KEY = "capture_mode";
  public static final String INTENT_PREVIEW_CAPTURE = "capture";
  public static final String INTENT_PREVIEW_FILMSTRIP = "filmstrip";
  public static final String INTENT_PREVIEW_KEY = "preview_mode";
  private static final String INTENT_REVIEW_DURATION = "review_duration";
  public static final int LOAD_LATEST_THUMBNAIL = 51;
  public static final int LONG_PRESS_AREA = 15;
  public static final long LONG_PRESS_PERIOD = 400L;
  public static final int LONG_PRESS_SW_CAPTURE = 66;
  public static final int LONG_PRESS_SW_CAPTURE_TIMEOUT = 1000;
  public static final int MENU_3D_CAMERA_SWITCH = 79;
  private static final int MIN_VOLUME = 1;
  public static final int NO_FOCUSING = 0;
  private static final int NO_KEEP_VOLUME = 255;
  public static final int OPEN_CAPTURE_UI = 15;
  public static final int PANEL_FADEOUT = 22;
  public static final int PANEL_FADEOUT_TIMEOUT = 8000;
  public static final int PLAY_FOCUS_SOUND = 21;
  public static final int PLAY_SOUND = 20;
  public static final int PLAY_SOUND_POOL = 69;
  public static final int PREPARE_FOCUS_BEFORE_CAPTURE = 34;
  public static final int PREVIEW_DUPLICATE_END = 43;
  public static final int PREVIEW_DUPLICATE_START = 42;
  public static final int PROGRESS_STATUS = 13;
  public static final int REMAINING_FADEOUT_TIMEOUT = 2000;
  public static final int REMAINING_LAYOUT_FADEOUT = 62;
  private static final int REQUEST_CODE_CROP_MSG = 1;
  public static final int REQUEST_SURFACEVIEW_LAYOUT = 6;
  public static final int RESET_GRID_VIEW = 8;
  public static final int RESET_SURFACEVIEW_LAYOUT = 7;
  public static final int RESTART_CHECK_SENSOR_FOCUS = 31;
  public static final int RESUME_PREVIOUS_EFFECT = 71;
  public static final int REVIEW_DURATION_NO_TIME = 255;
  public static final int ROTATE_ONSCREEN_CAMERA_SWITCH_BTN = 101;
  public static final int SCENE_GUIDE_TIMEOUT = 3000;
  private static final int SCENE_SELECTOR_RESULT = 2;
  public static final int SCREEN_SAVE = 36;
  public static final int SELECT_ACTION_SCREEN = 38;
  public static final int SET_LANDSCAPE = 104;
  public static final int SET_ZOOM_MIN_MAX = 1;
  public static final int SHOW_3D_HINT = 80;
  public static final int SHOW_CAPTURE_REVIEW = 57;
  public static final int SHOW_FOCUSING = 24;
  public static final int SHOW_FOCUS_END = 27;
  public static final int SHOW_FOCUS_FAIL = 26;
  public static final int SHOW_FOCUS_SUCCESS = 25;
  public static final int SHOW_FPS = 40;
  public static final int SHOW_GPS_INDICATOR = 41;
  public static final int SHOW_RECORDING_INDICATOR = 2;
  public static final int SHOW_REMAINING_LAYOUT = 61;
  public static final int SHOW_STABLE_ICON = 77;
  public static final int SHOW_STABLE_ICON_PORT = 78;
  public static final int SHOW_TOAST = 37;
  public static final int START_ALBUM_CAMERA = 17;
  public static final int START_ALBUM_VIDEO = 18;
  public static final int START_FACE_FOCUS = 53;
  public static final int START_RECORDING_INDICATOR = 3;
  public static final int START_SELF_PORTRAIT = 68;
  public static final int START_SENSOR_FOCUS = 33;
  public static final int STATUS_END = 0;
  public static final int STATUS_RESUME = 1;
  public static final int STOP_RECORDING_FINISH = 65;
  public static final int STOP_RECORDING_INDICATOR = 4;
  public static final int SUSPEND_ON_RECORDING = 64;
  public static final int SWITCH_3D_PREVIEW_READY = 73;
  private static final String TAG = "HTCCamera";
  public static final int TRIGGER_STOP_RECORD = 82;
  public static final int UNBLOCK_CAPTURE_UI = 52;
  public static final int UNFREEZE_UI = 63;
  public static final int UPDATE_CAPTURE_MODE_LAYOUT = 12;
  public static final int UPDATE_FLASH_FROM_RESTRICTION = 44;
  public static final int UPDATE_SCENE_ICON = 84;
  public static final int UPDATE_STORAGE_LOCATION_MENU = 103;
  public static final int UPDATE_THUMBNAIL_BUTTON = 50;
  public static final int UPDATE_ZOOM_BAR_VISIBLE = 10;
  public static final int ZOOMBAR_FADEOUT = 23;
  public static final int ZOOMBAR_FADEOUT_TIMEOUT = 5000;
  private static final int ZOOM_ACTION_KEY_DIVIDER = 5;
  public static boolean bFocusFromPress;
  public static boolean bHoldFocusKey;
  private static int mBeepCount;
  private static boolean mBlock3DSwitch;
  private static boolean mBlockCaptureUI = false;
  private static int mCurrentBeepLevel;
  public static int mFocusMode;
  private static int mFocus_Screen_X;
  private static int mFocus_Screen_Y;
  private static boolean mIsKeyguardShow;
  private static boolean mIsSelfPortraitTask;
  private static boolean mIsSelfTimerTask;
  private static boolean mIsWaitKeyguardBeforePreview;
  private static boolean mTurnOnTorch_Camcorder;
  private int CHECK_SWING_DURATION = 30;
  public int Display_Orientation;
  private float From_ratio_land = 0.5F;
  private float From_ratio_port = 0.5F;
  private int SCENE_INDICATOR_RIGHT = 20;
  private int SCENE_INDICATOR_TOP = 65;
  public int SCREEN_DELAY = 120000;
  private int SWING_LIMIT_ANGLE = 30;
  private float SWING_LIMIT_RATIO = this.SWING_LIMIT_ANGLE / this.SWING_STEP;
  private int SWING_STABLE_THRESHOLD = 1;
  private float SWING_STEP = 180.0F;
  private float To_ratio_land = 0.5F;
  private float To_ratio_port = 0.5F;
  private int animation_DURATION = 0;
  private boolean bCancelFocus = false;
  private ConnectivityManager connManager = null;
  private float image_shift_ratio;
  private boolean keep_backgroundDataSetting = false;
  private float leftBound_ratio = 0.5F - this.SWING_LIMIT_RATIO;
  private int m3DButtonStatus = 1;
  private final BroadcastReceiver m3DKeySwitchReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      String str = paramIntent.getAction();
      LOG.V("HTCCamera", "m3DKeySwitchReceiver: " + str);
      if ((DisplayDevice.support3DCamera()) && ("com.htc.intent.action.CAM_SWITCH_CHANGE".equals(paramIntent.getAction())))
      {
        LOG.V("HTCCamera", "onReceive() - com.htc.content.Intent.ACTION_HTC_CAM_SWITCH_CHANGE");
        if (!HTCCamera.this.mForce2DMode)
          break label65;
      }
      while (true)
      {
        return;
        label65: android.view.KeyEvent localKeyEvent = (android.view.KeyEvent)paramIntent.getParcelableExtra("android.intent.extra.KEY_EVENT");
        if (localKeyEvent.getKeyCode() != 14)
          continue;
        HTCCamera.access$11302(HTCCamera.this, localKeyEvent.getAction());
        if (HTCCamera.mIsKeyguardShow)
        {
          LOG.V("HTCCamera", "mIsKeyguardShow is true, return");
          continue;
        }
        if ((!HTCCamera.this.m3DPreviewReady) || (HTCCamera.mBlock3DSwitch))
          continue;
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 72);
        MessageHandler.sendObtainMessageDelayed(HTCCamera.this.mUIHandler, 72, 0, 0, null, 300L);
      }
    }
  };
  private boolean m3DOptimalLandscape = false;
  private boolean m3DPreviewReady = true;
  private boolean m3DStatusInitialized = false;
  private boolean m3DTriggerCapture = false;
  private ActionScreen mActionScreen;
  private boolean mActivityOnPause = false;
  private View mArrowHintLayout = null;
  private RotateRelativeLayout mArrowImgLayoutLand = null;
  private RotateRelativeLayout mArrowImgLayoutPort = null;
  private RotateRelativeLayout mArrowTextLayoutLand = null;
  private RotateRelativeLayout mArrowTextLayoutPort = null;
  private MediaPlayer.OnCompletionListener mAudioCompletionListener = new MediaPlayer.OnCompletionListener()
  {
    public void onCompletion(MediaPlayer paramMediaPlayer)
    {
      if (paramMediaPlayer != null)
      {
        paramMediaPlayer.release();
        if (paramMediaPlayer == HTCCamera.this.mAudioPlayer)
        {
          HTCCamera.access$102(HTCCamera.this, null);
          if ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.getMode() == 0))
            HTCCamera.this.getAudioManager().abandonAudioFocus(HTCCamera.this.mAudioFocusListener);
        }
      }
    }
  };
  private MediaPlayer.OnErrorListener mAudioErrorListener = new MediaPlayer.OnErrorListener()
  {
    public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
    {
      if (paramMediaPlayer != null)
      {
        LOG.W("HTCCamera", "Error occurred when playing audio, what = " + paramInt1 + ", extra = " + paramInt2);
        paramMediaPlayer.release();
        if (paramMediaPlayer == HTCCamera.this.mAudioPlayer)
          HTCCamera.access$102(HTCCamera.this, null);
      }
      return false;
    }
  };
  private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener()
  {
    public void onAudioFocusChange(int paramInt)
    {
      switch (paramInt)
      {
      case 0:
      default:
        LOG.E("HTCCamera", "Unknown audio focus change code");
      case -1:
      case -2:
      case -3:
      case 1:
      }
      while (true)
      {
        return;
        LOG.V("HTCCamera", "AudioFocus: received AUDIOFOCUS_LOSS");
        continue;
        LOG.V("HTCCamera", "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT");
        continue;
        LOG.V("HTCCamera", "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
        continue;
        LOG.V("HTCCamera", "AudioFocus: received AUDIOFOCUS_GAIN");
      }
    }
  };
  private AudioManager mAudioManager = null;
  private MediaPlayer mAudioPlayer;
  private int mAudioStreamType = 2;
  private final Object mAudioSyncRoot = new Object();
  private View mAutoCaptureLayout = null;
  private boolean mBlockPowerWarning = false;
  Camera mCameraDevice;
  Handler mCameraHandler = null;
  private RelativeLayout mCameraLayout = null;
  CameraThread mCameraThread = null;
  private final IEventHandler mCameraThreadEventHandler = new IEventHandler()
  {
    public void onEvent(Event paramEvent)
    {
      if (HTCCamera.this.mUIHandler != null)
        HTCCamera.this.mUIHandler.post(new Runnable(paramEvent)
        {
          public void run()
          {
            HTCCamera.this.onEvent(this.val$event);
          }
        });
      while (true)
      {
        return;
        LOG.E("HTCCamera", "Cannot handle event '" + paramEvent.getName() + "' because there is no handler for UI");
      }
    }
  };
  private boolean mCanShowFocusView = false;
  private Button mCapture_btn_ds = null;
  private View mCapture_combine_ds = null;
  private ImageView mCapture_icon = null;
  Drawable mCapture_press = null;
  Drawable mCapture_press_ds = null;
  int mCapture_rest = 2130837831;
  int mCapture_rest_ds = 2130837602;
  private final Runnable mCloseActionScreenRunnable = new Runnable()
  {
    public void run()
    {
      HTCCamera.this.closeActionScreen();
    }
  };
  private boolean mCloseTouchEvent = false;
  private ComponentManager mComponentManager;
  private Dialog mCurrentDialog = null;
  private Panel mEffectPanel = null;
  private View mEffectReceiver;
  private Button mEffect_btn = null;
  private ImageView mEffect_icon = null;
  private boolean mEnableSensorFocus = false;
  private FaceDetection mFaceDetection = null;
  private int mFaceNumber;
  private ImageButton mFilmstrip_btn = null;
  private View mFilmstrip_combine = null;
  private View mFilmstrip_layout = null;
  private ImageView mFilmstrip_thumbnail = null;
  private FlashRestriction mFlashRestriction = null;
  private SlidingMenu mFlash_ExtensionMenu;
  private Button mFlash_btn = null;
  private ImageView mFlash_icon = null;
  private LinearLayout mFlash_layout_ds = null;
  private TextView mFlash_text_ds = null;
  private View mFocusAnimation = null;
  private int mFocusPos_X = 0;
  private int mFocusPos_Y = 0;
  public int mFocusingState = 1;
  private boolean mForce2DMode = false;
  private TextView mFpsText = null;
  private int mFreezeOrientation = -1;
  private boolean mFreezeUI = true;
  private RelativeLayout mFront_land;
  private RelativeLayout mFront_port;
  private ImageView mGpsIndicator = null;
  private GpuEffectController mGpuEffectContr;
  private View mGridLayout = null;
  private boolean mIS_LEVEL = false;
  private boolean mIS_LEVEL_PORT = false;
  private boolean mIdle = false;
  private View mIndicatorLayout = null;
  private View mIndicatorLayout_Camcorder = null;
  private View mIndicatorLayout_Camera = null;
  private int mInstanceOrientation;
  private IntentManager mIntentManager = null;
  private boolean mIsBackQuit = false;
  private boolean mIsCameraThreadRunning;
  private boolean mIsCaptureTriggered = false;
  private boolean mIsCaptureUIOpen;
  private boolean mIsFlashDisabled;
  private boolean mIsFlashlightOffByScene = false;
  private boolean mIsFullDataPartition = false;
  private boolean mIsPowerWarning = false;
  private boolean mIsPreviewStarted;
  private boolean mIsSipExist = false;
  private boolean mIsTouchScreen = false;
  private boolean mIsUIReady = false;
  private boolean mIsZoomLocked;
  DialogInterface.OnKeyListener mKeyListener = new DialogInterface.OnKeyListener()
  {
    public boolean onKey(DialogInterface paramDialogInterface, int paramInt, android.view.KeyEvent paramKeyEvent)
    {
      int i = 1;
      switch (paramInt)
      {
      default:
        i = 0;
      case 24:
      case 25:
      case 82:
      case 84:
      }
      return i;
    }
  };
  private final BroadcastReceiver mKeyguardReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      String str = paramIntent.getAction();
      LOG.V("HTCCamera", "################ mKeyguardReceiver: " + str);
      if (str.equals("android.intent.action.USER_PRESENT"))
      {
        LOG.W("HTCCamera", "################ unlock screen !!");
        if (HTCCamera.mIsKeyguardShow == true)
        {
          HTCCamera.access$14902(false);
          if ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.mMode == 1) && (HTCCamera.mTurnOnTorch_Camcorder))
            MessageHandler.sendObtainMessage(HTCCamera.this.mCameraHandler, 21, 0, 0, "torch");
          if (HTCCamera.mIsWaitKeyguardBeforePreview == true)
          {
            LOG.W("HTCCamera", "unlock screen - can show UI");
            if (HTCCamera.this.mIdle)
              break label144;
            MessageHandler.sendEmptyMessage(HTCCamera.this.mUIHandler, 45);
          }
        }
      }
      while (true)
      {
        HTCCamera.setWaitKeyguardBeforePreview(false);
        return;
        label144: HTCCamera.this.activate();
      }
    }
  };
  private int mLastOrientation = -1;
  private String mLastSavedMediaPath;
  private LocationHandler mLocationHandler = null;
  private View mMainBar = null;
  private View mMainBar_item = null;
  private View mMainBar_receiver = null;
  private View mMainLayout = null;
  Thread mMainThread;
  private MenuHandler mMenuHandler = null;
  private SlidingMenu mMode_ExtensionMenu;
  private Button mMode_btn = null;
  private ImageView mMode_icon = null;
  private LinearLayout mMode_layout_ds = null;
  private TextView mMode_text_ds = null;
  private View mMuteIndicatorLayout = null;
  private boolean mNeed_doOnCreate = false;
  private boolean mNeed_doOnResume = false;
  private boolean mNeed_initOnCreate = false;
  private boolean mNeed_unregisterReceiver = false;
  private RotateRelativeLayout mOnScreenCameraSwitchBtnContainer;
  private ImageView mOnScreenCameraSwitchBtnIcon;
  private Button mOnScreenSwitch_btn = null;
  private RelativeLayout mOnScreenSwitch_receiver;
  OrientationEventListener mOrientationListener;
  private int mOriginalVolume_Alarm = -1;
  private int mOriginalVolume_Ring = -1;
  public SlidingDrawer mPanel = null;
  private final BroadcastReceiver mPartitonReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      String str = paramIntent.getAction();
      LOG.V("HTCCamera", "################ mPartitonReceiver: " + str);
    }
  };
  private Button mPhoto_btn = null;
  private ImageView mPhoto_icon = null;
  private long mPlayBeepTime = 0L;
  private final Runnable mPrepareActionScreenRunnable = new Runnable()
  {
    public void run()
    {
      HTCCamera.this.prepareActionScreen();
    }
  };
  private ImageView mPreviewDuplicate = null;
  private View mPreviewFilterLayout = null;
  private View mPreviewVisibleArea = null;
  private ProgressDialog mProgressDialog = null;
  private RecordLimitCheck mRecordLimitCheck = null;
  private long mRecord_sec = 0L;
  private TextView mRecording_Hour = null;
  private boolean mRecording_Shining = false;
  private TextView mRecording_Time = null;
  private View mRemainingLayout = null;
  private TextView mRemainingText = null;
  private int mReviewDuration = -1;
  private RotateLinearLayout mRotateLayout_Indicator = null;
  private RotateRelativeLayout mRotateLayout_Recording = null;
  private RotateLinearLayout mRotateLayout_Remaining = null;
  private RotateRelativeLayout mRotateLayout_SelfPortrait = null;
  private RotateLinearLayout mRotateLayout_Setting = null;
  private RotateLinearLayout mRotateLayout_SubMenu_Setting = null;
  private RotateRelativeLayout mRotateLayout_SwitchBtn = null;
  private Runnable mRunnable_Swing = null;
  private final BroadcastReceiver mSIPReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
    }
  };
  private View mSceneGuideLayout = null;
  private RotateRelativeLayout mSceneHDRLayout = null;
  private View mSceneIndicatorLayout = null;
  private RotateRelativeLayout mSceneLandscapeLayout = null;
  private RotateRelativeLayout mSceneLandscapePortLayout = null;
  private RotateRelativeLayout mSceneLandscapeTipLayout = null;
  private TextView mSceneLandscapeTipText = null;
  private RelativeLayout mSceneLandscapeTipTextLayout = null;
  private RotateRelativeLayout mScenePortraitLayout = null;
  private RotateRelativeLayout mSceneSunNightLayout = null;
  private int mScene_Idx = 0;
  private Button mScene_btn_ds = null;
  private ImageView mScene_icon_ds = null;
  private LinearLayout mScene_layout_ds = null;
  private TextView mScene_text_ds = null;
  private View mScreensave = null;
  private TextView mScreensaveText = null;
  private RotateRelativeLayout mScreensaveTextContainer;
  private View mSecondLayout = null;
  private LinearLayout mSelfPortraitHint_layout = null;
  private TextView mSelfPortraitHint_text = null;
  private SensorHandler mSensorHandler = null;
  private final Runnable mShowActionScreenRunnable = new Runnable()
  {
    public void run()
    {
      HTCCamera.this.showActionScreen(0);
    }
  };
  private int mSoundID_beep;
  private int mSoundID_beep2;
  private SoundPool mSoundPool = null;
  private int mSoundPoolVolume;
  private long mStartCountTime = 0L;
  private boolean mStartTapCapture = false;
  private long mStartTime = 0L;
  private int mStreamID_beep = 0;
  private int mStreamID_beep2 = 0;
  private SlidingDrawer mSubMenu_layout = null;
  private boolean mSurfaceCreated = false;
  private int mSurfaceHeight;
  SurfaceHolder mSurfaceHolder = null;
  SurfaceView mSurfaceView = null;
  private int mSurfaceWidth;
  private ImageView mSwitchBtn_icon = null;
  private Button mSwitch_btn = null;
  private LinearLayout mSwitch_layout_ds = null;
  private TextView mSwitch_text_ds = null;
  private ThumbnailController mThumbController;
  private ImageView mTimerBase = null;
  private RotateToast mToast = null;
  private int mTouchDown_X = 0;
  private int mTouchDown_Y = 0;
  private Handler mUIHandler = null;
  private long mUpdateCountDuration = 1000L;
  private Bitmap mVideoThumbnailBitmap = null;
  private ImageView mVideoThumbnailView = null;
  private boolean mWaitResetSettings = false;
  private boolean mWillCloseCamera = false;
  private int mZoomActionStep = 1;
  private ZoomBar mZoomBar = null;
  private RotateImageView mZoomBarIn = null;
  private RotateImageView mZoomBarOut = null;
  private View mZoomLayout = null;
  private ZoomTouch mZoomTouch = null;
  boolean m_CancelFocusFromTouch = false;
  private EventManager m_EventManager;
  boolean m_NeedTriggerRecord = false;
  private RotateRelativeLayout m_sceneContainer;
  private ImageView m_sceneImage;
  private ImageView mbackIcon_land;
  private ImageView mbackIcon_port;
  private ImageView mfrontIcon_land;
  private ImageView mfrontIcon_port;
  private ImageView mivFaceOne = null;
  private ImageView mivFaceTwo = null;
  private ImageView mivFocusedView = null;
  private ImageView mivFocusingView = null;
  private ImageView mivGrid = null;
  private ImageView mivMute = null;
  private ImageView mivSelfTimer = null;
  private TextView mtvSelfTimer = null;
  private float rightBound_ratio = 0.5F + this.SWING_LIMIT_RATIO;

  static
  {
    mBlock3DSwitch = false;
    mIsKeyguardShow = false;
    mIsWaitKeyguardBeforePreview = false;
    mTurnOnTorch_Camcorder = false;
    mIsSelfTimerTask = false;
    mIsSelfPortraitTask = false;
    mFocusMode = 0;
    bFocusFromPress = false;
    mFocus_Screen_X = 0;
    mFocus_Screen_Y = 0;
    bHoldFocusKey = false;
    mCurrentBeepLevel = -1;
    mBeepCount = 0;
  }

  private void QueryToggleStorage()
  {
    if (DCFRuler.StorageCardControl.getStorageType() == 0);
    for (String str = getString(2131362097); ; str = getString(2131362096))
    {
      this.mCurrentDialog = new RotateDialog.Builder(this).setTitle(2131362095).setIcon(17301543).setMessage(str).setPositiveButton(34341180, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          DCFRuler.StorageCardControl.toggleStorageType(HTCCamera.this);
          if (DCFRuler.StorageCardControl.getStorageType() == 0);
          for (String str = "main_memory"; ; str = "sdcard")
          {
            HTCCameraAdvanceSetting.writePreference(HTCCamera.this, "pref_camera_storage_location", str);
            HTCCamera.this.mMenuHandler.updateStorageLocationItem();
            MessageHandler.sendEmptyMessage(HTCCamera.this.mCameraHandler, 53);
            MessageHandler.sendEmptyMessage(HTCCamera.this.mUIHandler, 51);
            return;
          }
        }
      }).setNegativeButton(34341181, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
        }
      }).setOnKeyListener(this.mKeyListener).create();
      ((RotateDialog)this.mCurrentDialog).setOrientation(OrientationConfig.getUIOrientation());
      this.mCurrentDialog.show();
      return;
    }
  }

  private void WriteModePreference()
  {
    if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 0));
    for (String str = "camera_capture_mode_photo"; ; str = "camera_capture_mode_movie")
    {
      HTCCameraAdvanceSetting.writePreference(this, "camera_capture_mode", str);
      return;
    }
  }

  private void broadcastStopFM()
  {
  }

  private void broadcastStopMusic()
  {
  }

  private void broadcastStopVoiceRecording()
  {
    LOG.W("HTCCamera", "!!!! @@@@ broadcastStopVoiceRecording() - start");
    Intent localIntent = new Intent("com.htc.soundrecorder.recordingservicecommand");
    localIntent.putExtra("command", "stoprecord");
    sendBroadcast(localIntent);
    LOG.W("HTCCamera", "!!!! @@@@ broadcastStopVoiceRecording() - end");
  }

  private void changeIndicatorLayout(int paramInt, boolean paramBoolean)
  {
    boolean bool = true;
    if (this.mIndicatorLayout == null)
    {
      this.mIndicatorLayout = ((ViewStub)this.mSecondLayout.findViewById(2131231091)).inflate();
      this.mRotateLayout_Indicator = ((RotateLinearLayout)this.mIndicatorLayout.findViewById(2131230920));
    }
    label164: RotateRelativeLayout localRotateRelativeLayout;
    int i;
    if (paramInt == 0)
    {
      if (this.mIndicatorLayout_Camera == null)
      {
        this.mIndicatorLayout_Camera = ((ViewStub)this.mIndicatorLayout.findViewById(2131230921)).inflate();
        this.mAutoCaptureLayout = this.mIndicatorLayout.findViewById(2131230817);
        this.mivSelfTimer = ((ImageView)this.mIndicatorLayout.findViewById(2131230818));
        updateIndicatorLayout_AutoCapture();
        if (DisplayDevice.isDoubleShot())
          setIconPosition(OrientationConfig.getUIOrientation(), this.mAutoCaptureLayout);
      }
      if (this.mFaceNumber > 0)
      {
        showSelfPortraitHint();
        this.mIndicatorLayout_Camera.setVisibility(0);
        if (this.mIndicatorLayout_Camcorder != null)
          this.mIndicatorLayout_Camcorder.setVisibility(8);
        if ((paramBoolean) && (this.mIndicatorLayout.getVisibility() == 0))
          AnimationManager.showAlphaAnimation(this.mIndicatorLayout, 0.0F, 1.0F, 300, 300);
        if (DisplayDevice.showSceneInMenu() == bool)
        {
          showSceneIndicator(false);
          localRotateRelativeLayout = this.m_sceneContainer;
          i = OrientationConfig.getUIOrientation();
          if (this.m_sceneContainer.getVisibility() != 0)
            break label369;
        }
      }
    }
    while (true)
    {
      showSceneIndicatorAnimation(localRotateRelativeLayout, 1.0F, 0.0F, 0, 400, i, bool, paramInt);
      return;
      hideSelfPortraitHint();
      break;
      if (this.mIndicatorLayout_Camcorder == null)
      {
        this.mIndicatorLayout_Camcorder = ((ViewStub)this.mIndicatorLayout.findViewById(2131230923)).inflate();
        this.mMuteIndicatorLayout = this.mIndicatorLayout.findViewById(2131230759);
        this.mivMute = ((ImageView)this.mIndicatorLayout.findViewById(2131230760));
        updateIndicatorLayout_RecordMute();
        if (DisplayDevice.isDoubleShot())
          setIconPosition(OrientationConfig.getUIOrientation(), this.mMuteIndicatorLayout);
      }
      this.mIndicatorLayout_Camcorder.setVisibility(0);
      hideSelfPortraitHint();
      if (this.mIndicatorLayout_Camera == null)
        break label164;
      this.mIndicatorLayout_Camera.setVisibility(8);
      break label164;
      label369: bool = false;
    }
  }

  private void checkFaceDetection()
  {
    MessageHandler.removeMessages(this.mUIHandler, 35);
    if (this.mActivityOnPause == true);
    while (true)
    {
      return;
      if (mBlockCaptureUI == true)
      {
        MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 35, 100L);
        continue;
      }
      if ((!DisplayDevice.supportSensorFocus()) || (!DisplayDevice.supportFaceDetection()))
        continue;
      int i;
      label71: boolean bool1;
      if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 0))
      {
        i = 1;
        bool1 = HTCCameraAdvanceSetting.getPrefenceBoolean(this, "pref_camera_switch").booleanValue();
        if ((DisplayDevice.CAPTURE_BUTTON != DisplayDevice.CaptureButton.HWKey) && (!HTCCameraAdvanceSetting.getPrefenceBoolean(this, "pref_camera_auto_focus").booleanValue()))
          break label241;
      }
      label241: for (int j = 1; ; j = 0)
      {
        boolean bool2 = HTCCameraAdvanceSetting.getPrefenceBoolean(this, "pref_face_detection").booleanValue();
        if ((getFaceNumber() <= 0) && ((i != 1) || ((bool1) && (!DisplayDevice.isDoubleShot())) || (j != 1) || (bool2 != true) || (hasSpecificEffect())))
          break label246;
        if ((getFaceNumber() <= 0) || (this.mFaceDetection == null))
        {
          this.mFaceDetection = new FaceDetection();
          this.mFaceDetection.initFaceDetection(this, this.mCameraThread);
        }
        this.mFaceDetection.startFaceDetection();
        if ((isPanelReady() != true) || (this.mPanel.isOpened()) || (mFocusMode != 0))
          break;
        this.mFaceDetection.startCheckLoop();
        break;
        i = 0;
        break label71;
      }
      label246: if (this.mFaceDetection == null)
        continue;
      this.mFaceDetection.stopCheckLoop();
      this.mFaceDetection.stopFaceDetection();
      if (getFaceNumber() > 0)
        continue;
      updateFaceIcon(0);
    }
  }

  private void checkTapCapture(int paramInt1, int paramInt2)
  {
    if ((this.mCameraThread == null) || (this.mCameraThread.mMode == 1));
    while (true)
    {
      return;
      if (this.mStartTapCapture != true)
      {
        Rect localRect = new Rect();
        this.mSurfaceView.getHitRect(localRect);
        if (!localRect.contains(paramInt1, paramInt2))
        {
          this.mStartTime = System.currentTimeMillis();
          continue;
        }
        if ((Math.abs(paramInt1 - this.mFocusPos_X) <= 15) && (Math.abs(paramInt2 - this.mFocusPos_Y) <= 15))
        {
          if (System.currentTimeMillis() - this.mStartTime < 400L)
            continue;
          if (!canTriggerFocus())
          {
            resetTapCapture(paramInt1, paramInt2);
            continue;
          }
          this.mStartTapCapture = true;
          this.mFocusPos_X = paramInt1;
          this.mFocusPos_Y = paramInt2;
          LOG.W("HTCCamera", "Tap & Capture - mFocusingState = FOCUSING_TAP_CAPTURE");
          this.mFocusingState = 2;
          triggerTakePicture();
          continue;
        }
        resetTapCapture(paramInt1, paramInt2);
        continue;
      }
    }
  }

  private void closeCaptureUI()
  {
    LOG.V("HTCCamera", "closeCaptureUI()");
    MessageHandler.removeMessages(this.mUIHandler, 15);
    if (this.mFilmstrip_layout != null)
      this.mFilmstrip_layout.setVisibility(4);
    if (this.mFpsText != null)
      this.mFpsText.setVisibility(4);
    setPanelVisible(false);
    closeAllExtensionMenus(0);
    if (this.mOnScreenCameraSwitchBtnContainer != null)
    {
      this.mOnScreenCameraSwitchBtnContainer.setVisibility(4);
      this.mOnScreenSwitch_receiver.setVisibility(4);
    }
    if ((this.mEffectPanel != null) && ((this.mEffectPanel.isOpen()) || (!this.mEffectPanel.isReady())))
      this.mGpuEffectContr.openEffectMenu(false, false);
    hideEffectControl();
    if (this.mMainBar != null)
      this.mMainBar.setVisibility(4);
    if (this.mCapture_icon != null)
      this.mCapture_icon.setImageResource(this.mCapture_rest);
    closeSecondLayout(false);
    this.mIsCaptureUIOpen = false;
    if (DisplayDevice.showSceneInMenu() == true)
      showSceneIndicator(false);
    this.m_EventManager.raiseEvent("CaptureUI.Closed");
    hide_gps_indicator();
    if (DisplayDevice.isDoubleShot())
    {
      hideAllScene();
      if (this.mCapture_btn_ds != null)
        this.mCapture_btn_ds.setEnabled(false);
      updateDOTCaptureIcon(false, this.mCameraThread.mMode);
    }
  }

  private void closeSecondLayout(boolean paramBoolean)
  {
    if ((this.mSecondLayout == null) || (this.mSecondLayout.getVisibility() != 0));
    while (true)
    {
      return;
      this.mSecondLayout.clearAnimation();
      if (paramBoolean == true)
        AnimationManager.showAlphaAnimation(this.mSecondLayout, 1.0F, 0.0F, 0, 300);
      this.mSecondLayout.setVisibility(4);
      this.m_EventManager.raiseEvent("SecondLayout.Closed");
      hideRemainingLayout();
    }
  }

  private void closeSelfTimer()
  {
    LOG.V("HTCCamera", "closeSelfTimer() - start");
    MessageHandler.removeMessages(this.mUIHandler, 5);
    if (this.mtvSelfTimer == null)
    {
      LOG.W("HTCCamera", "closeSelfTimer() - mtvSelfTimer = null, return");
      return;
    }
    this.mtvSelfTimer.setVisibility(4);
    hideTimerBase(false);
    if ((isRequestName(IntentManager.RequestName.Contacts) == true) || (isRequestName(IntentManager.RequestName.Square) == true));
    for (boolean bool = false; ; bool = HTCCameraAdvanceSetting.getPrefenceBoolean(this, "pref_grid").booleanValue())
    {
      if ((bool == true) && (this.mivGrid != null))
        this.mivGrid.setVisibility(0);
      openCaptureUI();
      enableMainBarItems(true);
      showMainBarItems(true, true);
      resetAutoCaptureTask();
      LOG.W("HTCCamera", "UnBlock Capture UI - close self timer");
      mBlockCaptureUI = false;
      this.mIsCaptureTriggered = false;
      LOG.V("HTCCamera", "closeSelfTimer() - end");
      break;
    }
  }

  private void doAfterSceneSelect(int paramInt)
  {
    Resources localResources = getResources();
    TypedArray localTypedArray = localResources.obtainTypedArray(2131165198);
    String[] arrayOfString = localResources.getStringArray(2131165197);
    switch (this.mScene_Idx)
    {
    default:
      this.mScene_Idx = paramInt;
      if (this.mScene_Idx == 0)
      {
        this.m_EventManager.raiseEvent(new BooleanEvent("AutoScene.Enabled", true));
        label85: updateSceneGuide(paramInt);
        if ((this.mScene_Idx == 0) || (this.mScene_Idx == 9) || (this.mCameraThread.getMode() != 0))
          break label294;
        if (this.mGpuEffectContr == null)
          break;
        if (this.mScene_Idx != 8)
          break label280;
        this.mGpuEffectContr.setUpEffect("depth", 0);
      }
    case 2:
    case 3:
    case 4:
    case 1:
    }
    while (true)
    {
      if (this.mMenuHandler != null)
      {
        if ((this.mPanel != null) && (!this.mPanel.isOpened()))
        {
          this.mMenuHandler.exitMenuHandler();
          this.mMenuHandler.resetMenuHandler();
        }
        this.mMenuHandler.checkMenuSetting();
      }
      this.mScene_icon_ds.setImageResource(localTypedArray.getResourceId(paramInt, 0));
      this.mScene_text_ds.setText(arrayOfString[paramInt]);
      ViewUtil.enableMainButton(this.mScene_icon_ds, this.mScene_btn_ds, this.mScene_text_ds);
      localTypedArray.recycle();
      return;
      exitPanoramaMode();
      break;
      exitHdrMode();
      break;
      exitBurstMode();
      break;
      exitHappyShotMode();
      break;
      this.m_EventManager.raiseEvent(new BooleanEvent("AutoScene.Enabled", false));
      break label85;
      label280: this.mGpuEffectContr.setUpEffect("none", 0);
      continue;
      label294: if ((this.mGpuEffectContr == null) || (this.mCameraThread == null) || (this.mCameraThread.getMode() != 0) || (this.mCameraThread.is2ndCamera() == true))
        continue;
      String str = HTCCameraAdvanceSetting.getPrefenceValue(this, "pref_camera_effect_manual");
      this.mGpuEffectContr.setUpEffect(str, 0);
    }
  }

  private void doOnCreate_after_preview()
  {
    LOG.W("HTCCamera", "doOnCreate_after_preview() - start");
    if (!this.mNeed_doOnCreate)
    {
      LOG.W("HTCCamera", "doOnCreate_after_preview() - not need to do");
      return;
    }
    this.mNeed_doOnCreate = false;
    this.mRecordLimitCheck = new RecordLimitCheck(this.mCameraThread);
    this.mLocationHandler = new LocationHandler(this);
    label84: int k;
    label171: label608: ViewStub localViewStub1;
    if ((DisplayDevice.supportSensorFocus() == true) && (DisplayDevice.hasAutoFocus() == true))
    {
      this.mSensorHandler = new SensorHandler(this);
      if (this.mFilmstrip_btn != null)
        this.mFilmstrip_btn.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            LOG.W("HTCCamera", "Click button to filmstrip");
            if (HTCCamera.this.mCameraThread == null)
              return;
            if ((DisplayDevice.canCancelFocus()) && (HTCCamera.this.mFocusingState == 1))
            {
              LOG.W("HTCCamera", "Press filmstrip button when focusing, cancel focus");
              HTCCamera.this.cancelAutoFocus();
            }
            while (true)
            {
              if (HTCCamera.this.isRequestName(IntentManager.RequestName.Album) != true)
                break label171;
              LOG.W("HTCCamera", "End - Return to album after pressing filmstrip button");
              Intent localIntent2 = new Intent("FROM_CAMERA");
              localIntent2.putExtra("preview_mode", "filmstrip");
              HTCCamera.this.setResult(-1, localIntent2);
              HTCCamera.this.finish();
              break;
              if ((HTCCamera.mBlockCaptureUI == true) || (HTCCamera.this.mCameraThread.getRecorderStatus() == true) || ((HTCCamera.this.mtvSelfTimer != null) && (HTCCamera.this.mtvSelfTimer.getVisibility() == 0)))
                break;
              LOG.W("HTCCamera", "Block Capture UI - press filmstrip button");
              HTCCamera.access$1702(true);
            }
            label171: LOG.W("HTCCamera", "Start - Go to album after pressing filmstrip button");
            Intent localIntent1 = new Intent("com.htc.album.action.VIEW_PHOTO_FROM_CAMERA");
            if (HTCCamera.this.mCameraThread.mMode == 0)
              localIntent1.putExtra("capture_mode", "camera");
            while (true)
            {
              while (true)
              {
                localIntent1.setDataAndType(CameraThread.getLastContentUri(), "image/jpeg");
                try
                {
                  HTCCamera.this.startActivity(localIntent1);
                }
                catch (ActivityNotFoundException localActivityNotFoundException)
                {
                  LOG.W("HTCCamera", "Start - Go to album after pressing filmstrip button : " + localActivityNotFoundException);
                }
              }
              break;
              localIntent1.putExtra("capture_mode", "comcorder");
            }
          }
        });
      if (DisplayDevice.supportThumbnailAlbumButton())
      {
        this.mThumbController = new ThumbnailController(getResources(), this.mFilmstrip_thumbnail, getContentResolver(), this.mUIHandler);
        if (!DisplayDevice.isDoubleShot())
        {
          if (!DisplayDevice.isDelayCreateImageThumb())
            break label1178;
          Handler localHandler2 = this.mCameraHandler;
          if (this.mCameraThread.mMode != 0)
            break label1171;
          k = 29;
          MessageHandler.sendEmptyMessageDelayed(localHandler2, k, 5000L);
        }
      }
      if (this.mMode_ExtensionMenu != null)
      {
        this.mMode_ExtensionMenu.setOnItemClickListener(new SlidingMenu.OnItemClickListener()
        {
          public void onItemClick(SlidingMenu paramSlidingMenu, SlidingMenuItem paramSlidingMenuItem)
          {
            int i = paramSlidingMenuItem.getID();
            boolean bool;
            if ((i == 1) || (i == 3))
            {
              bool = true;
              if ((i != 0) && (i != 1))
                break label49;
            }
            label49: for (int j = 0; ; j = 1)
            {
              HTCCamera.this.switchMode(bool, j);
              return;
              bool = false;
              break;
            }
          }
        });
        this.mMode_ExtensionMenu.setOnMenuStateChangedListener(new SlidingMenu.OnMenuStateChangedListener()
        {
          public void onClosing(SlidingMenu paramSlidingMenu)
          {
            if (HTCCamera.this.mMode_btn != null)
              HTCCamera.this.mMode_btn.setBackgroundDrawable(ViewUtil.getCustomDrawable(HTCCamera.this, 2131361794, 2130837711));
          }

          public void onOpening(SlidingMenu paramSlidingMenu)
          {
            HTCCamera.this.prepareOpeningExtensionMenu(paramSlidingMenu);
            for (int i = paramSlidingMenu.getItemCount() - 1; i >= 0; i--)
              paramSlidingMenu.getItem(i).highlight(false);
            if (HTCCamera.this.mCameraThread != null)
            {
              if (HTCCamera.this.mCameraThread.mMode != 0)
                break label114;
              if (!HTCCamera.this.mCameraThread.is2ndCamera())
                break label102;
              paramSlidingMenu.getItem(1).highlight(true);
            }
            while (true)
            {
              if (HTCCamera.this.mMode_btn != null)
                HTCCamera.this.mMode_btn.setBackgroundResource(2130837774);
              return;
              label102: paramSlidingMenu.getItem(0).highlight(true);
              continue;
              label114: if (HTCCamera.this.mCameraThread.is2ndCamera())
              {
                paramSlidingMenu.getItem(3).highlight(true);
                continue;
              }
              paramSlidingMenu.getItem(2).highlight(true);
            }
          }
        });
      }
      if (this.mFlash_ExtensionMenu != null)
      {
        this.mFlash_ExtensionMenu.setOnItemClickListener(new SlidingMenu.OnItemClickListener()
        {
          public void onItemClick(SlidingMenu paramSlidingMenu, SlidingMenuItem paramSlidingMenuItem)
          {
            if ((HTCCamera.this.mFlashRestriction != null) && (HTCCamera.this.mFlashRestriction.isDisableFlash()))
            {
              MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 37);
              MessageHandler.sendObtainMessage(HTCCamera.this.mUIHandler, 37, HTCCamera.this.mFlashRestriction.getRestrictionHint(), 0, null);
            }
            while (true)
            {
              return;
              Resources localResources = HTCCamera.this.getResources();
              String[] arrayOfString = localResources.getStringArray(2131165194);
              TypedArray localTypedArray = localResources.obtainTypedArray(2131165193);
              int i = paramSlidingMenuItem.getID() - 1;
              HTCCameraAdvanceSetting.writePreference(HTCCamera.this, "pref_camera_flash_mode", arrayOfString[i]);
              MessageHandler.sendObtainMessage(HTCCamera.this.mCameraHandler, 21, 0, 0, arrayOfString[i]);
              if (HTCCamera.this.mFlash_icon != null)
              {
                HTCCamera.this.mFlash_icon.setImageResource(localTypedArray.getResourceId(i, 0));
                ViewUtil.enableMainButton(HTCCamera.this.mFlash_icon, HTCCamera.this.mFlash_btn);
              }
              localTypedArray.recycle();
            }
          }
        });
        this.mFlash_ExtensionMenu.setOnMenuStateChangedListener(new SlidingMenu.OnMenuStateChangedListener()
        {
          public void onClosing(SlidingMenu paramSlidingMenu)
          {
            if (HTCCamera.this.mFlash_btn != null)
              HTCCamera.this.mFlash_btn.setBackgroundDrawable(ViewUtil.getCustomDrawable(HTCCamera.this, 2131361794, 2130837711));
          }

          public void onOpening(SlidingMenu paramSlidingMenu)
          {
            HTCCamera.this.prepareOpeningExtensionMenu(paramSlidingMenu);
            String[] arrayOfString = HTCCamera.this.getResources().getStringArray(2131165194);
            String str = HTCCameraAdvanceSetting.getPrefenceValue(HTCCamera.this, "pref_camera_flash_mode");
            for (int i = arrayOfString.length - 1; i >= 0; i--)
              paramSlidingMenu.getItem(i).highlight(arrayOfString[i].equals(str));
            if (HTCCamera.this.mFlash_btn != null)
              HTCCamera.this.mFlash_btn.setBackgroundResource(2130837774);
          }
        });
      }
      this.mMode_btn.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          LOG.V("HTCCamera", "Click button to change camera mode");
          if (HTCCamera.this.mMode_ExtensionMenu != null)
            HTCCamera.this.mMode_ExtensionMenu.toggle();
          do
          {
            return;
            HTCCamera.this.closeAllExtensionMenus();
          }
          while (((!HTCCamera.this.isRequestMode(IntentManager.RequestMode.Main)) && (!HTCCamera.this.isRequestName(IntentManager.RequestName.Album))) || (HTCCamera.this.mCameraThread == null));
          if (HTCCamera.this.mCameraThread.mMode == 0);
          for (int i = 1; ; i = 0)
          {
            HTCCamera.this.switchMode(HTCCamera.this.mCameraThread.is2ndCamera(), i);
            break;
          }
        }
      });
      if (this.mFlash_btn != null)
        this.mFlash_btn.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            LOG.V("HTCCamera", "Click button to change flash mode");
            if ((HTCCamera.this.mFlash_ExtensionMenu != null) && (HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.mMode == 0))
              HTCCamera.this.mFlash_ExtensionMenu.toggle();
            while (true)
            {
              return;
              HTCCamera.this.closeAllExtensionMenus();
              if ((HTCCamera.this.mFlashRestriction != null) && (HTCCamera.this.mFlashRestriction.isDisableFlash() == true) && (!HTCCamera.this.mIsFlashDisabled) && (!HTCCamera.this.mIsFlashlightOffByScene))
              {
                MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 37);
                MessageHandler.sendObtainMessage(HTCCamera.this.mUIHandler, 37, HTCCamera.this.mFlashRestriction.getRestrictionHint(), 0, null);
                continue;
              }
              if ((HTCCamera.mBlockCaptureUI) && (HTCCamera.this.mFocusingState != 1))
                continue;
              HTCCamera.this.switchFlashMode();
            }
          }
        });
      if (this.mSwitch_btn != null)
        this.mSwitch_btn.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            HTCCamera.this.closeAllExtensionMenus();
            HTCCamera localHTCCamera;
            if (HTCCamera.this.mCameraThread != null)
            {
              localHTCCamera = HTCCamera.this;
              if (HTCCamera.this.mCameraThread.is2ndCamera())
                break label77;
            }
            label77: for (boolean bool = true; ; bool = false)
            {
              localHTCCamera.switchMode(bool, HTCCamera.this.mCameraThread.mMode);
              if (DisplayDevice.isDoubleShot())
                HTCCamera.this.updateSwitchIcon(HTCCamera.this.mCameraThread.mMode);
              return;
            }
          }
        });
      if (this.mScene_btn_ds != null)
        this.mScene_btn_ds.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            HTCCamera.this.startSceneSelector();
          }
        });
      if (this.mEffect_btn != null)
        this.mEffect_btn.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            HTCCamera.this.closeAllExtensionMenus();
            if (HTCCamera.this.mFocusingState == 1)
            {
              if (DisplayDevice.canCancelFocus() == true)
              {
                LOG.W("HTCCamera", "press effect button when focusing, cancel focus");
                HTCCamera.this.cancelAutoFocus();
                LOG.W("HTCCamera", "UnBlock Capture UI - press effect button and cancel focus");
                HTCCamera.access$1702(false);
              }
            }
            else
              if (HTCCamera.mBlockCaptureUI != true)
                break label69;
            while (true)
            {
              return;
              LOG.W("HTCCamera", "press effect button when focusing, but device can't cancel focus");
              continue;
              label69: if (HTCCamera.this.mEffectPanel == null)
                HTCCamera.this.initEffectPanel();
              if (HTCCamera.this.mEffectPanel.isOpen())
              {
                HTCCamera.this.mGpuEffectContr.openEffectMenu(false, true);
                LOG.V("HTCCamera", "mEffectPanel.setOpen(false");
                continue;
              }
              HTCCamera.this.mGpuEffectContr.openEffectMenu(true, true);
              LOG.V("HTCCamera", "mEffectPanel.setOpen(true");
            }
          }
        });
      if (this.mPhoto_btn != null)
        this.mPhoto_btn.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            HTCCamera.this.closeAllExtensionMenus();
            LOG.W("HTCCamera", "Click button to filmstrip");
            if ((DisplayDevice.canCancelFocus()) && (HTCCamera.this.mFocusingState == 1))
            {
              LOG.W("HTCCamera", "Press filmstrip button when focusing, cancel focus");
              HTCCamera.this.cancelAutoFocus();
            }
            Intent localIntent1;
            while (true)
              if (HTCCamera.this.isRequestName(IntentManager.RequestName.Album) == true)
              {
                LOG.W("HTCCamera", "End - Return to album after pressing filmstrip button");
                Intent localIntent2 = new Intent("FROM_CAMERA");
                localIntent2.putExtra("preview_mode", "filmstrip");
                HTCCamera.this.setResult(-1, localIntent2);
                HTCCamera.this.finish();
                return;
                if ((HTCCamera.mBlockCaptureUI == true) || ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.getRecorderStatus() == true)) || ((HTCCamera.this.mtvSelfTimer != null) && (HTCCamera.this.mtvSelfTimer.getVisibility() == 0)))
                  break;
                LOG.W("HTCCamera", "Block Capture UI - press filmstrip button");
                HTCCamera.access$1702(true);
                continue;
              }
              else
              {
                LOG.W("HTCCamera", "Start - Go to album after pressing filmstrip button");
                localIntent1 = new Intent("com.htc.album.action.VIEW_PHOTO_FROM_CAMERA");
                if ((HTCCamera.this.mCameraThread == null) || (HTCCamera.this.mCameraThread.mMode != 0))
                  break label292;
                localIntent1.putExtra("capture_mode", "camera");
              }
            while (true)
            {
              while (true)
              {
                localIntent1.setDataAndType(CameraThread.getLastContentUri(), "image/jpeg");
                localIntent1.putExtra("preview_mode", "filmstrip");
                try
                {
                  HTCCamera.this.startActivity(localIntent1);
                }
                catch (ActivityNotFoundException localActivityNotFoundException)
                {
                  LOG.W("HTCCamera", "Start - Go to album after pressing filmstrip button : " + localActivityNotFoundException);
                }
              }
              break;
              break;
              label292: localIntent1.putExtra("capture_mode", "comcorder");
            }
          }
        });
      if (this.mOnScreenCameraSwitchBtnContainer != null)
        this.mOnScreenSwitch_btn.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            HTCCamera.this.closeAllExtensionMenus();
            HTCCamera localHTCCamera;
            if (HTCCamera.this.mCameraThread != null)
            {
              localHTCCamera = HTCCamera.this;
              if (HTCCamera.this.mCameraThread.is2ndCamera())
                break label54;
            }
            label54: for (boolean bool = true; ; bool = false)
            {
              localHTCCamera.switchMode(bool, HTCCamera.this.mCameraThread.mMode);
              return;
            }
          }
        });
      if (this.mFilmstrip_layout != null)
      {
        this.mFilmstrip_btn.setFocusable(false);
        this.mFilmstrip_layout.setFocusable(false);
      }
      if (this.mFilmstrip_thumbnail != null)
      {
        this.mFilmstrip_thumbnail.setFocusable(false);
        this.mFilmstrip_combine.setFocusable(false);
      }
      this.mFpsText = null;
      this.mToast = RotateToast.makeText(this, "", 100000);
      if (DisplayDevice.isDoubleShot())
        break label1215;
      enlargeTouchArea(this.mMainBar.findViewById(2131230943), this.mMode_btn);
      if (this.mSwitch_btn != null)
        enlargeTouchArea(this.mMainBar.findViewById(2131230942), this.mSwitch_btn);
      if (this.mFlash_btn != null)
        enlargeTouchArea(this.mMainBar.findViewById(2131230940), this.mFlash_btn);
      enlargeTouchArea(this.mMainBar.findViewById(2131230939), this.mEffect_btn);
      if (this.mPhoto_btn != null)
        enlargeTouchArea(this.mMainBar.findViewById(2131230938), this.mPhoto_btn);
      enlargeTouchArea(this.mFilmstrip_combine, this.mFilmstrip_btn);
      enlargeTouchArea(this.mOnScreenSwitch_receiver, this.mOnScreenSwitch_btn);
      if (this.mGpuEffectContr == null)
      {
        this.mGpuEffectContr = new GpuEffectController();
        ViewStub localViewStub2 = (ViewStub)findViewById(2131230775);
        this.mGpuEffectContr.init(localViewStub2, this.mCameraHandler, this, this.mCameraThread.getCameraController());
      }
      if (this.mPanel == null)
      {
        if (DisplayDevice.isDoubleShot())
          break label1308;
        localViewStub1 = (ViewStub)this.mMainBar.findViewById(2131230935);
        label687: View localView3 = localViewStub1.inflate();
        localView3.setVisibility(0);
        if (!DisplayDevice.isDoubleShot())
          this.mMainBar.findViewById(2131230934).setVisibility(8);
        this.mRotateLayout_Setting = ((RotateLinearLayout)localView3.findViewById(2131230997));
        this.mSubMenu_layout = ((SlidingDrawer)localView3.findViewById(2131230999));
        this.mRotateLayout_SubMenu_Setting = ((RotateLinearLayout)localView3.findViewById(2131231001));
        if (!DisplayDevice.isDoubleShot())
          this.mSubMenu_layout.setHandleDrawable(getResources().getDrawable(2130837843), getResources().getDrawable(2130837843), getResources().getDrawable(2130837842), ViewUtil.getCustomDrawable(this, 2131361804, 2130837844), ViewUtil.getCustomDrawable(this, 2131361803, 2130837845));
        this.mSubMenu_layout.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener()
        {
          public void onDrawerOpened()
          {
            HTCCamera.this.mSubMenu_layout.requestFocus();
          }
        });
        this.mMenuHandler = new MenuHandler();
        this.mMenuHandler.initMenuHandler(this, this.mCameraThread, localView3, this.mGpuEffectContr);
        this.mPanel = ((SlidingDrawer)localView3.findViewById(2131230995));
        if (!DisplayDevice.isDoubleShot())
          this.mPanel.setHandleDrawable(getResources().getDrawable(2130837829), getResources().getDrawable(2130837758), getResources().getDrawable(2130837757), ViewUtil.getCustomDrawable(this, 2131361806, 2130837759), ViewUtil.getCustomDrawable(this, 2131361805, 2130837760));
        this.mPanel.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener()
        {
          public void onDrawerOpened()
          {
            LOG.V("HTCCamera", "Panel is opened");
            if (HTCCamera.this.mMainBar_receiver != null)
              HTCCamera.this.mMainBar_receiver.setVisibility(4);
            HTCCamera.this.showMainBarItems(false, false);
            HTCCamera.this.checkFaceDetection();
            if (HTCCamera.this.mFaceDetection != null)
              HTCCamera.this.mFaceDetection.stopCheckLoop();
            MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 22);
            MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 22, 8000L);
            HTCCamera.this.mPanel.setDescendantFocusability(131072);
            if ((HTCCamera.this.mEffectPanel != null) && ((HTCCamera.this.mEffectPanel.isOpen()) || (!HTCCamera.this.mEffectPanel.isReady())))
              HTCCamera.this.mGpuEffectContr.openEffectMenu(false, true);
            HTCCamera.this.closeAllExtensionMenus();
          }
        });
        this.mPanel.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener()
        {
          public void onDrawerClosed()
          {
            LOG.V("HTCCamera", "Panel is closed");
            HTCCamera.this.m_EventManager.raiseEvent("Menu.Closed");
            if (HTCCamera.this.mMainBar_receiver != null)
              HTCCamera.this.mMainBar_receiver.setVisibility(0);
            if (!DisplayDevice.isDoubleShot())
              HTCCamera.this.showMainBarItems(true, false);
            while (true)
            {
              if (HTCCamera.this.mCapture_icon != null)
                HTCCamera.this.mCapture_icon.setImageResource(HTCCamera.this.mCapture_rest);
              HTCCamera.this.checkFaceDetection();
              if (HTCCamera.this.mFaceDetection != null)
                HTCCamera.this.mFaceDetection.startCheckLoop();
              if (HTCCamera.this.mMenuHandler != null)
                HTCCamera.this.mMenuHandler.exitMenuHandler();
              if (!HTCCamera.this.mWaitResetSettings)
              {
                if (HTCCamera.this.mMenuHandler != null)
                  HTCCamera.this.mMenuHandler.resetMenuHandler();
                HTCCamera.this.showEffectControl();
              }
              MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 22);
              return;
              HTCCamera.this.showMainBarItems(true, true);
            }
          }
        });
        this.mPanel.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener()
        {
          public void onScrollEnded()
          {
            LOG.V("HTCCamera", "Panel is moving, end");
          }

          public void onScrollStarted()
          {
            LOG.V("HTCCamera", "Panel is moving, start");
            HTCCamera.this.m_EventManager.raiseEvent("Menu.Opening");
            MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 37);
            if (HTCCamera.this.mToast != null)
              HTCCamera.this.mToast.cancel();
            if (HTCCamera.this.mMainBar_receiver != null)
              HTCCamera.this.mMainBar_receiver.setVisibility(4);
            if ((HTCCamera.this.mtvSelfTimer != null) && (HTCCamera.this.mtvSelfTimer.getVisibility() == 0))
              HTCCamera.this.closeSelfTimer();
            HTCCamera.this.showMainBarItems(false, false);
            if ((HTCCamera.this.mRemainingLayout != null) && (HTCCamera.this.mRemainingLayout.getVisibility() == 0))
              HTCCamera.this.hideRemainingLayout();
            HTCCamera.this.hideEffectControl();
            if (HTCCamera.this.mFocusingState == 1)
            {
              if (DisplayDevice.canCancelFocus() == true)
              {
                LOG.W("HTCCamera", "onPanelMove when focusing, cancel focus");
                HTCCamera.this.cancelAutoFocus();
                LOG.W("HTCCamera", "UnBlock Capture UI - onPanelMove and cancel focus");
                HTCCamera.access$1702(false);
              }
            }
            else
            {
              MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 35);
              if (HTCCamera.this.mFaceDetection != null)
              {
                HTCCamera.this.mFaceDetection.stopCheckLoop();
                HTCCamera.this.mFaceDetection.stopFaceDetection();
              }
              HTCCamera.this.mPanel.setDescendantFocusability(393216);
              if (HTCCamera.this.mMenuHandler != null)
                HTCCamera.this.mMenuHandler.closeSubMenu(false);
              MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 66);
            }
            while (true)
            {
              return;
              LOG.E("HTCCamera", "onPanelMove when focusing, but device can't cancel focus");
            }
          }
        });
        if (DisplayDevice.isDoubleShot())
          break label1326;
        int i = this.mCapture_icon.getWidth() / 8;
        Rect localRect = new Rect();
        this.mCapture_icon.getHitRect(localRect);
        this.mPanel.setOnCaptureListener(new SlidingDrawer.OnCaptureListener(localRect)
        {
          public boolean onCapture(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
          {
            int i;
            if ((HTCCamera.this.isPanelReady()) && (HTCCamera.this.mPanel.isOpened()))
              i = 0;
            while (true)
            {
              return i;
              if (HTCCamera.this.mFocusingState == 3)
              {
                i = 1;
                continue;
              }
              if (HTCCamera.this.isActionScreenOpen())
              {
                i = 1;
                continue;
              }
              if ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.mMode == 1) && (((HTCCamera.mBlockCaptureUI == true) && (HTCCamera.this.mFocusingState != 1)) || (HTCCamera.this.mCameraThread.getRecorderStatus() == true)))
              {
                if (this.val$rect.contains(paramInt1, paramInt2))
                  if (paramMotionEvent.getAction() == 0)
                  {
                    HTCCamera.access$10602(HTCCamera.this, false);
                    HTCCamera.this.mCapture_icon.setImageDrawable(HTCCamera.this.mCapture_press);
                    MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 66);
                    MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 66, 1000L);
                  }
                while (true)
                {
                  i = 1;
                  break;
                  if ((paramMotionEvent.getAction() != 1) || (HTCCamera.this.mStartTapCapture))
                    continue;
                  HTCCamera.this.mCapture_icon.setImageResource(HTCCamera.this.mCapture_rest);
                  MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 66);
                  HTCCamera.this.onTouchCapture_Camcorder();
                  continue;
                  HTCCamera.this.mCapture_icon.setImageResource(HTCCamera.this.mCapture_rest);
                  MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 66);
                }
              }
              if (this.val$rect.contains(paramInt1, paramInt2))
                break;
              HTCCamera.this.mCapture_icon.setImageResource(HTCCamera.this.mCapture_rest);
              MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 66);
              i = 1;
            }
            switch (paramMotionEvent.getAction())
            {
            default:
            case 0:
            case 2:
            case 1:
            }
            while (true)
            {
              i = 1;
              break;
              HTCCamera.this.mCapture_icon.setImageDrawable(HTCCamera.this.mCapture_press);
              HTCCamera.access$14002(HTCCamera.this, paramInt1);
              HTCCamera.access$14102(HTCCamera.this, paramInt2);
              HTCCamera.access$10602(HTCCamera.this, false);
              MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 66);
              MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 66, 1000L);
              continue;
              if ((!HTCCamera.this.mStartTapCapture) || (goto 364) || (HTCCamera.this.mStartTapCapture))
                continue;
              MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 66);
              HTCCamera.this.mCapture_icon.setImageResource(HTCCamera.this.mCapture_rest);
              if ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.mMode == 0))
              {
                HTCCamera.this.onTouchCapture_Camera();
                continue;
              }
              HTCCamera.this.onTouchCapture_Camcorder();
            }
          }
        });
        this.mSubMenu_layout.setOnCaptureListener(new SlidingDrawer.OnCaptureListener(i)
        {
          public boolean onCapture(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
          {
            int i;
            if ((!HTCCamera.this.isPanelReady()) || (!HTCCamera.this.mPanel.isOpened()))
            {
              i = 0;
              return i;
            }
            Rect localRect = HTCCamera.this.mSubMenu_layout.getHandleArea();
            if ((paramInt1 < localRect.width()) && (paramInt2 < localRect.height()))
              HTCCamera.this.mSubMenu_layout.setHandleSlideOpened();
            switch (paramMotionEvent.getAction())
            {
            default:
            case 0:
            case 2:
            case 1:
            case 3:
            }
            while (true)
            {
              i = 1;
              break;
              HTCCamera.access$14002(HTCCamera.this, paramInt1);
              HTCCamera.access$14102(HTCCamera.this, paramInt2);
              continue;
              int m = HTCCamera.this.mTouchDown_X - paramInt1;
              int n = HTCCamera.this.mTouchDown_Y - paramInt2;
              if (Math.sqrt(m * m + n * n) <= this.val$threshold)
                continue;
              i = 0;
              break;
              HTCCamera.this.mSubMenu_layout.setHandleOpened();
              if ((HTCCamera.this.mTouchDown_X >= localRect.width()) || (HTCCamera.this.mTouchDown_Y >= localRect.height()))
                continue;
              int j = HTCCamera.this.mTouchDown_X - paramInt1;
              int k = HTCCamera.this.mTouchDown_Y - paramInt2;
              if (Math.sqrt(j * j + k * k) < this.val$threshold)
              {
                HTCCamera.this.closeSettingsPanel();
                continue;
              }
              if (HTCCamera.this.mMenuHandler == null)
                continue;
              HTCCamera.this.mMenuHandler.closeSubMenu(true);
            }
          }
        });
      }
    }
    while (true)
    {
      if (this.mRotateLayout_SwitchBtn != null)
      {
        Button localButton = (Button)this.mRotateLayout_SwitchBtn.findViewById(2131230747);
        localButton.setBackgroundDrawable(ViewUtil.getCustomDrawable(this, 2131361807, 2130837693));
        enlargeTouchArea(this.mRotateLayout_SwitchBtn, localButton);
        localButton.setFocusable(false);
        localButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (HTCCamera.this.mFocusingState == 1)
            {
              if (DisplayDevice.canCancelFocus() == true)
              {
                LOG.W("HTCCamera", "press effect button when focusing, cancel focus");
                HTCCamera.this.cancelAutoFocus();
                LOG.W("HTCCamera", "UnBlock Capture UI - press effect button and cancel focus");
                HTCCamera.access$1702(false);
              }
            }
            else
              if (HTCCamera.mBlockCaptureUI != true)
                break label62;
            while (true)
            {
              return;
              LOG.W("HTCCamera", "press effect button when focusing, but device can't cancel focus");
            }
            label62: HTCCamera.access$1702(true);
            LOG.W("HTCCamera", "Block Capture UI - restartCamera()");
            HTCCamera.this.resetFocusMode();
            HTCCamera.this.releaseFaceDetection();
            HTCCamera.access$9102(HTCCamera.this, true);
            LOG.W("HTCCamera", "restartCamera() - set mWaitResetSettings to true");
            HTCCamera.this.closeSettingsPanel();
            if ((HTCCamera.this.mCameraThread != null) && (!HTCCamera.this.mCameraThread.is2ndCamera()));
            for (boolean bool = true; ; bool = false)
            {
              HTCCameraAdvanceSetting.writePreference(HTCCamera.this, "pref_camera_switch", bool);
              if (bool)
                HTCCamera.this.hideZoomBar(false);
              MessageHandler.sendEmptyMessage(HTCCamera.this.mCameraHandler, 2);
              MessageHandler.sendObtainMessage(HTCCamera.this.mCameraHandler, 0, 1, 0, "true");
              break;
            }
          }
        });
      }
      if (DisplayDevice.isDoubleShot())
        enableSceneIcon(this.mCameraThread.getMode(), this.mCameraThread.is2ndCamera());
      LOG.W("HTCCamera", "doOnCreate_after_preview() - end");
      break;
      this.mSensorHandler = null;
      break label84;
      label1171: k = 30;
      break label171;
      label1178: Handler localHandler1 = this.mCameraHandler;
      if (this.mCameraThread.mMode == 0);
      for (int j = 29; ; j = 30)
      {
        MessageHandler.sendEmptyMessage(localHandler1, j);
        break;
      }
      label1215: enlargeTouchArea(this.mMainBar.findViewById(2131230962), this.mMode_btn);
      View localView1 = this.mMainBar.findViewById(2131230964);
      if (this.mFlash_btn != null)
        enlargeTouchArea(localView1, this.mFlash_btn);
      View localView2 = this.mMainBar.findViewById(2131230963);
      if (this.mSwitch_btn != null)
        enlargeTouchArea(localView2, this.mSwitch_btn);
      enlargeTouchArea(this.mMainBar.findViewById(2131230965), this.mScene_btn_ds);
      break label608;
      label1308: localViewStub1 = (ViewStub)this.mMainBar.findViewById(2131230966);
      break label687;
      label1326: this.mCapture_btn_ds.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          int i = (int)paramMotionEvent.getX();
          int j = (int)paramMotionEvent.getY();
          int k;
          if ((i < 0) || (i > HTCCamera.this.mCapture_btn_ds.getWidth()) || (j < 0) || (j > HTCCamera.this.mCapture_btn_ds.getHeight()))
          {
            HTCCamera.this.updateDOTCaptureIcon(false, HTCCamera.this.mCameraThread.mMode);
            MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 66);
            k = 1;
            return k;
          }
          switch (paramMotionEvent.getAction())
          {
          default:
          case 0:
          case 1:
          }
          while (true)
          {
            k = 1;
            break;
            HTCCamera.access$10602(HTCCamera.this, false);
            MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 66);
            MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 66, 1000L);
            HTCCamera.this.updateDOTCaptureIcon(true, HTCCamera.this.mCameraThread.mMode);
            continue;
            if (HTCCamera.this.mStartTapCapture)
              continue;
            MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 66);
            HTCCamera.this.updateDOTCaptureIcon(false, HTCCamera.this.mCameraThread.mMode);
            if ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.mMode == 0))
            {
              HTCCamera.this.onTouchCapture_Camera();
              continue;
            }
            HTCCamera.this.onTouchCapture_Camcorder();
          }
        }
      });
    }
  }

  private void doOnDestory()
  {
    LOG.W("HTCCamera", "doOnDestroy() - start");
    this.mComponentManager.deinitializeComponents();
    this.mComponentManager.removeComponents();
    try
    {
      this.mCameraThread.join();
      label30: if (this.mMenuHandler != null)
      {
        this.mMenuHandler.releaseMenuHandler();
        this.mMenuHandler = null;
      }
      if (this.mZoomBar != null)
      {
        this.mZoomBar.releaseResource();
        this.mZoomBar = null;
      }
      if (this.mLocationHandler != null)
      {
        this.mLocationHandler.releaseLocationHandler();
        this.mLocationHandler = null;
      }
      if (this.mSensorHandler != null)
      {
        this.mSensorHandler.releaseSensorHandler();
        this.mSensorHandler = null;
      }
      if (this.mIntentManager != null)
      {
        this.mIntentManager.releaseIntentManager();
        this.mIntentManager = null;
      }
      closeActionScreen();
      if (this.mThumbController != null)
      {
        this.mThumbController.close();
        this.mThumbController = null;
      }
      if (this.mCameraThread != null)
      {
        this.mCameraThread.releaseCameraThread();
        this.mCameraHandler = null;
        this.mCameraThread = null;
      }
      synchronized (this.mAudioSyncRoot)
      {
        if (this.mAudioManager != null)
        {
          this.mAudioManager.setParameters("CAMCORDER_MODE=OFF");
          this.mAudioManager = null;
        }
        LOG.W("HTCCamera", "doOnDestroy() - end");
        return;
      }
    }
    catch (Exception localException)
    {
      break label30;
    }
  }

  private void doOnResume_after_preview()
  {
    LOG.W("HTCCamera", "doOnResume_after_preview() - start");
    if (!this.mNeed_doOnResume)
      LOG.W("HTCCamera", "doOnResume_after_preview() - not need to do");
    while (true)
    {
      return;
      this.mNeed_doOnResume = false;
      IntentFilter localIntentFilter1 = new IntentFilter("android.intent.action.DEVICE_STORAGE_LOW");
      localIntentFilter1.addAction("android.intent.action.DEVICE_STORAGE_OK");
      IntentFilter localIntentFilter2 = new IntentFilter("HTC_IME_CURRENT_STATE");
      try
      {
        registerReceiver(this.mPartitonReceiver, localIntentFilter1);
        registerReceiver(this.mSIPReceiver, localIntentFilter2);
        if ((this.mCameraThread != null) && (this.mLocationHandler != null) && (this.mCameraThread.mMode == 0))
        {
          this.mLocationHandler.startReceivingLocationUpdates();
          this.mEnableSensorFocus = false;
          registerFocusSensor(true);
          MessageHandler.sendEmptyMessage(this.mCameraHandler, 52);
          MessageHandler.sendEmptyMessage(this.mCameraHandler, 53);
          closeActionScreen();
          if (this.mGpuEffectContr != null)
            this.mGpuEffectContr.resume(this.mCameraThread.mMode);
          if (is3DCameraActivated())
            lockOrientation(1);
          openCaptureUI();
          if (this.mPreviewDuplicate != null)
          {
            this.mPreviewDuplicate.clearAnimation();
            this.mPreviewDuplicate.setImageBitmap(null);
            this.mPreviewDuplicate.setVisibility(8);
          }
          if ((this.mCameraThread != null) && (this.mCameraThread.mOneShotBitmap != null))
          {
            this.mCameraThread.mOneShotBitmap.recycle();
            this.mCameraThread.mOneShotBitmap = null;
          }
          if ((CameraController.supportFlashLight() == true) && (this.mFlashRestriction == null))
          {
            this.mFlashRestriction = new FlashRestriction();
            this.mFlashRestriction.initFlashRestriction(this);
          }
          if (this.mIndicatorLayout != null)
            this.mIndicatorLayout.setVisibility(0);
          if (this.mRotateLayout_SwitchBtn != null)
            this.mRotateLayout_SwitchBtn.setVisibility(0);
          if (!DisplayDevice.supportSecondCamera())
            loadSoundPool();
          if (IsFirstTimeLaunch())
            HTCCameraAdvanceSetting.writePreference(this, "pref_first_time_launch", false);
          if ((DCFRuler.StorageCardControl.bSupportPhoneStorage) && (DCFRuler.StorageCardControl.getStorageType() == 0))
          {
            String str = HTCCameraAdvanceSetting.getPrefenceValue(this, "pref_sdcard_state");
            if ((str != null) && (!str.equals("mounted")) && (DCFRuler.StorageCardControl.getSDCardState().equals("mounted")))
              QueryToggleStorage();
          }
          saveSDCardState();
          LOG.W("HTCCamera", "doOnResume_after_preview() - end");
        }
      }
      catch (Exception localException)
      {
        while (true)
        {
          LOG.E("HTCCamera", "registerReceiver failed!!", localException);
          continue;
          hide_gps_indicator();
        }
      }
    }
  }

  private void enableMainBarItems(boolean paramBoolean)
  {
    if (this.mMainBar_item == null);
    while (true)
    {
      return;
      if ((isRequestMode(IntentManager.RequestMode.Main) == true) || (isRequestName(IntentManager.RequestName.Album) == true))
      {
        if (this.mMode_btn != null)
          this.mMode_btn.setEnabled(paramBoolean);
        if (this.mPhoto_btn != null)
          this.mPhoto_btn.setEnabled(paramBoolean);
      }
      if ((CameraController.supportFlashLight()) && ((this.mCameraThread == null) || (!this.mCameraThread.is2ndCamera())) && (this.mFlash_btn != null))
        this.mFlash_btn.setEnabled(paramBoolean);
      if (this.mSwitch_btn != null)
        this.mSwitch_btn.setEnabled(paramBoolean);
      if (this.mEffect_btn == null)
        continue;
      this.mEffect_btn.setEnabled(paramBoolean);
    }
  }

  private void enableSceneIcon(int paramInt, boolean paramBoolean)
  {
    if (this.mScene_icon_ds == null);
    while (true)
    {
      return;
      if ((paramInt == 0) && (!paramBoolean))
      {
        String str;
        if (!isServiceMode())
        {
          str = HTCCameraAdvanceSetting.getPrefenceValue(this, "pref_camera_scene_ds");
          label31: this.mScene_Idx = 0;
        }
        try
        {
          if (str.equals("null"));
          for (this.mScene_Idx = 0; ; this.mScene_Idx = Integer.parseInt(str))
          {
            doAfterSceneSelect(this.mScene_Idx);
            ViewUtil.enableMainButton(this.mScene_icon_ds, this.mScene_btn_ds, this.mScene_text_ds);
            break;
            str = HTCCameraAdvanceSetting.getPrefenceValue(this, "pref_camera_scene_service_ds");
            break label31;
          }
        }
        catch (NumberFormatException localNumberFormatException)
        {
          while (true)
            localNumberFormatException.printStackTrace();
        }
      }
      doAfterSceneSelect(0);
      ViewUtil.disableMainButton(this.mScene_icon_ds, this.mScene_btn_ds, this.mScene_text_ds);
    }
  }

  private void enlargeTouchArea(View paramView1, View paramView2)
  {
    if ((paramView1 == null) || (paramView2 == null));
    while (true)
    {
      return;
      Rect localRect = new Rect();
      paramView1.getDrawingRect(localRect);
      paramView1.setTouchDelegate(new TouchDelegate(localRect, paramView2));
    }
  }

  private int getAdjustedVolume(int paramInt)
  {
    if (((DisplayDevice.forceSutterSound()) || (isAutoCaptureTask())) && (HeadsetHelper.isHeadsetPlugged()));
    for (int i = (int)Math.ceil(NAN.0D * paramInt); ; i = (int)Math.ceil(0.7D * paramInt))
      return i;
  }

  private String getTimeString(long paramLong)
  {
    long l1 = paramLong % 60L;
    long l2 = paramLong / 60L % 60L;
    long l3 = paramLong / 3600L;
    Object[] arrayOfObject2;
    if (l3 == 0L)
    {
      arrayOfObject2 = new Object[2];
      arrayOfObject2[0] = Long.valueOf(l2);
      arrayOfObject2[1] = Long.valueOf(l1);
    }
    Object[] arrayOfObject1;
    for (String str = String.format("%02d:%02d", arrayOfObject2); ; str = String.format("%02d:%02d:%02d", arrayOfObject1))
    {
      return str;
      arrayOfObject1 = new Object[3];
      arrayOfObject1[0] = Long.valueOf(l3);
      arrayOfObject1[1] = Long.valueOf(l2);
      arrayOfObject1[2] = Long.valueOf(l1);
    }
  }

  private String getTimeString(long paramLong, TextView paramTextView)
  {
    long l1 = paramLong % 60L;
    long l2 = paramLong / 60L % 60L;
    long l3 = paramLong / 3600L;
    if ((paramTextView != null) && (l3 > 0L))
    {
      Object[] arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = Long.valueOf(l3);
      paramTextView.setText(String.format("%d hr", arrayOfObject2));
      paramTextView.setVisibility(0);
    }
    Object[] arrayOfObject1 = new Object[2];
    arrayOfObject1[0] = Long.valueOf(l2);
    arrayOfObject1[1] = Long.valueOf(l1);
    return String.format("%02d:%02d", arrayOfObject1);
  }

  private String getTimeString_ds(long paramLong)
  {
    long l1 = paramLong % 60L;
    long l2 = paramLong / 60L % 60L;
    long l3 = paramLong / 3600L;
    Object[] arrayOfObject2;
    if (l3 > 0L)
    {
      arrayOfObject2 = new Object[3];
      arrayOfObject2[0] = Long.valueOf(l3);
      arrayOfObject2[1] = Long.valueOf(l2);
      arrayOfObject2[2] = Long.valueOf(l1);
    }
    Object[] arrayOfObject1;
    for (String str = String.format("%02d:%02d:%02d", arrayOfObject2); ; str = String.format("%02d:%02d", arrayOfObject1))
    {
      return str;
      arrayOfObject1 = new Object[2];
      arrayOfObject1[0] = Long.valueOf(l2);
      arrayOfObject1[1] = Long.valueOf(l1);
    }
  }

  private void handleSensorFocus()
  {
    LOG.V("HTCCamera", "handleSensorFocus() - start");
    LOG.W("HTCCamera", "Block Capture UI - take focus start");
    mBlockCaptureUI = true;
    MessageHandler.removeMessages(this.mUIHandler, 35);
    if (this.mFaceDetection != null)
    {
      this.mFaceDetection.stopCheckLoop();
      this.mFaceDetection.stopFaceDetection();
    }
    if (this.mFocusingState == 0)
    {
      LOG.W("HTCCamera", "take focus - mFocusingState = FOCUSING_NO_ACTION");
      this.mFocusingState = 1;
    }
    mFocusMode = 0;
    Rect localRect = new Rect();
    this.mSurfaceView.getHitRect(localRect);
    int i;
    if (this.Display_Orientation == 1)
      i = DisplayDevice.SCREEN_WIDTH;
    for (int j = DisplayDevice.SCREEN_HEIGHT; ; j = DisplayDevice.SCREEN_WIDTH)
    {
      int k = i / 2;
      int m = j / 2;
      if (this.mGpuEffectContr != null)
      {
        int[] arrayOfInt = this.mGpuEffectContr.getCircleCenter();
        if (arrayOfInt != null)
        {
          k = arrayOfInt[0];
          m = arrayOfInt[1];
        }
      }
      updateFocusView(k, m);
      mapFocusPoint_Screen2Preview(k - localRect.left, m - localRect.top, localRect);
      LOG.V("HTCCamera", "handleSensorFocus(" + k + "," + m + ") - end");
      return;
      i = DisplayDevice.SCREEN_HEIGHT;
    }
  }

  private void hideAllScene()
  {
    hideSunNightSceneGuide(false);
    hideHDRSceneGuide(false);
    if (this.mRunnable_Swing != null)
      this.mUIHandler.removeCallbacks(this.mRunnable_Swing);
    this.mIS_LEVEL = false;
    this.mIS_LEVEL_PORT = false;
  }

  private void hideHDRSceneGuide(boolean paramBoolean)
  {
    if (this.mSceneHDRLayout == null)
      return;
    if (paramBoolean)
    {
      AlphaAnimation localAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
      localAlphaAnimation.setStartOffset(0L);
      localAlphaAnimation.setDuration(400L);
      localAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnimation)
        {
          HTCCamera.this.mSceneHDRLayout.setVisibility(4);
        }

        public void onAnimationRepeat(Animation paramAnimation)
        {
        }

        public void onAnimationStart(Animation paramAnimation)
        {
        }
      });
      this.mSceneHDRLayout.startAnimation(localAlphaAnimation);
    }
    while (true)
    {
      MessageHandler.removeMessages(this.mUIHandler, 85);
      break;
      this.mSceneHDRLayout.setVisibility(4);
    }
  }

  private void hideLandscapeSceneGuide(boolean paramBoolean)
  {
    MessageHandler.removeMessages(this.mUIHandler, 76);
    if ((this.mSceneLandscapeLayout == null) || (this.mSceneLandscapePortLayout == null) || (this.mSceneLandscapeTipLayout == null));
    while (true)
    {
      return;
      int i = OrientationConfig.mapOrientation_Event2UI(this.mLastOrientation);
      if ((i == 0) || (i == 2));
      for (RotateRelativeLayout localRotateRelativeLayout = this.mSceneLandscapePortLayout; ; localRotateRelativeLayout = this.mSceneLandscapeLayout)
      {
        if (!paramBoolean)
          break label127;
        AlphaAnimation localAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
        localAlphaAnimation.setStartOffset(0L);
        localAlphaAnimation.setDuration(400L);
        localAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
          public void onAnimationEnd(Animation paramAnimation)
          {
            HTCCamera.this.mSceneLandscapeLayout.setVisibility(4);
            HTCCamera.this.mSceneLandscapePortLayout.setVisibility(4);
            HTCCamera.this.mSceneLandscapeTipLayout.setVisibility(4);
          }

          public void onAnimationRepeat(Animation paramAnimation)
          {
          }

          public void onAnimationStart(Animation paramAnimation)
          {
          }
        });
        localRotateRelativeLayout.startAnimation(localAlphaAnimation);
        this.mSceneLandscapeTipLayout.startAnimation(localAlphaAnimation);
        break;
      }
      label127: this.mSceneLandscapeLayout.setVisibility(4);
      this.mSceneLandscapePortLayout.setVisibility(4);
      this.mSceneLandscapeTipLayout.setVisibility(4);
    }
  }

  private void hidePortraitSceneGuide(boolean paramBoolean)
  {
    if (this.mScenePortraitLayout == null)
      return;
    if (paramBoolean)
    {
      AlphaAnimation localAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
      localAlphaAnimation.setStartOffset(0L);
      localAlphaAnimation.setDuration(400L);
      localAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnimation)
        {
          HTCCamera.this.mScenePortraitLayout.setVisibility(4);
        }

        public void onAnimationRepeat(Animation paramAnimation)
        {
        }

        public void onAnimationStart(Animation paramAnimation)
        {
        }
      });
      this.mScenePortraitLayout.startAnimation(localAlphaAnimation);
    }
    while (true)
    {
      MessageHandler.removeMessages(this.mUIHandler, 74);
      break;
      this.mScenePortraitLayout.setVisibility(4);
    }
  }

  private void hideSelfPortraitHint()
  {
    if (this.mRotateLayout_SelfPortrait != null)
      this.mRotateLayout_SelfPortrait.setVisibility(4);
  }

  private void hideSunNightSceneGuide(boolean paramBoolean)
  {
    if (this.mSceneSunNightLayout == null)
      return;
    if (paramBoolean)
    {
      AlphaAnimation localAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
      localAlphaAnimation.setStartOffset(0L);
      localAlphaAnimation.setDuration(400L);
      localAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnimation)
        {
          HTCCamera.this.mSceneSunNightLayout.setVisibility(4);
        }

        public void onAnimationRepeat(Animation paramAnimation)
        {
        }

        public void onAnimationStart(Animation paramAnimation)
        {
        }
      });
      this.mSceneSunNightLayout.startAnimation(localAlphaAnimation);
    }
    while (true)
    {
      MessageHandler.removeMessages(this.mUIHandler, 75);
      break;
      this.mSceneSunNightLayout.setVisibility(4);
    }
  }

  private void hideTimerBase(boolean paramBoolean)
  {
    if (this.mTimerBase == null);
    while (true)
    {
      return;
      if (this.mTimerBase.getVisibility() == 0)
      {
        this.mTimerBase.setVisibility(4);
        continue;
      }
    }
  }

  private void initEffectPanel()
  {
    if (this.mEffectPanel == null)
    {
      this.mEffectPanel = ((Panel)((ViewStub)this.mMainLayout.findViewById(2131230979)).inflate());
      this.mEffectPanel.setOnPanelListener(new Panel.OnPanelListener()
      {
        public void onPanelClosed(Panel paramPanel)
        {
          LOG.V("HTCCamera", "Effect Panel is closed");
          if ((!HTCCamera.mBlockCaptureUI) && (HTCCamera.this.mOnScreenCameraSwitchBtnContainer != null) && (HTCCamera.this.mOnScreenCameraSwitchBtnContainer.getVisibility() == 4))
          {
            HTCCamera.this.mOnScreenCameraSwitchBtnContainer.setVisibility(0);
            HTCCamera.this.mOnScreenSwitch_receiver.setVisibility(0);
            AnimationManager.showAlphaAnimation(HTCCamera.this.mOnScreenCameraSwitchBtnContainer, 0.0F, 1.0F, 0, 400);
          }
          if (HTCCamera.this.mMenuHandler != null)
          {
            HTCCamera.this.mMenuHandler.exitMenuHandler();
            HTCCamera.this.mMenuHandler.resetMenuHandler();
            HTCCamera.this.mMenuHandler.updateDisableItems();
          }
          HTCCamera.this.mEffect_btn.setBackgroundDrawable(ViewUtil.getCustomDrawable(HTCCamera.this, 2131361794, 2130837711));
          HTCCamera.this.checkFaceDetection();
          if ((HTCCamera.this.isPanelReady() == true) && (!HTCCamera.this.mPanel.isOpened()) && (HTCCamera.this.mFaceDetection != null))
            HTCCamera.this.mFaceDetection.startCheckLoop();
          if (!HTCCamera.mBlockCaptureUI)
            HTCCamera.this.openSecondLayout(true);
          HTCCamera.this.m_EventManager.raiseEvent("EffectPanel.Closed");
          if (DisplayDevice.showSceneInMenu() == true)
            HTCCamera.this.updateSceneIndicator(HTCCamera.this.getPrefSceneType(HTCCamera.this.mCameraThread.mMode));
          MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 59);
        }

        public void onPanelMove(Panel paramPanel)
        {
          LOG.V("HTCCamera", "Effect Panel is moving");
          if (HTCCamera.this.mFocusingState == 1)
          {
            if (DisplayDevice.canCancelFocus() == true)
            {
              LOG.W("HTCCamera", "onPanelMove when focusing, cancel focus");
              HTCCamera.this.cancelAutoFocus();
              LOG.W("HTCCamera", "UnBlock Capture UI - onPanelMove and cancel focus");
              HTCCamera.access$1702(false);
            }
          }
          else
          {
            HTCCamera.this.mEffect_btn.setBackgroundResource(2130837774);
            MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 35);
            if (HTCCamera.this.mFaceDetection != null)
            {
              HTCCamera.this.mFaceDetection.stopCheckLoop();
              HTCCamera.this.mFaceDetection.stopFaceDetection();
            }
            HTCCamera.this.closeSecondLayout(false);
            if (HTCCamera.this.mOnScreenCameraSwitchBtnContainer != null)
            {
              HTCCamera.this.mOnScreenCameraSwitchBtnContainer.setVisibility(4);
              HTCCamera.this.mOnScreenSwitch_receiver.setVisibility(4);
            }
            HTCCamera.this.m_EventManager.raiseEvent("EffectPanel.Opening");
            if (DisplayDevice.showSceneInMenu() == true)
              HTCCamera.this.showSceneIndicator(false);
          }
          while (true)
          {
            return;
            LOG.E("HTCCamera", "onPanelMove when focusing, but device can't cancel focus");
          }
        }

        public void onPanelOpened(Panel paramPanel)
        {
          LOG.V("HTCCamera", "Effect Panel is opened");
          HTCCamera.this.checkFaceDetection();
          if (HTCCamera.this.mFaceDetection != null)
            HTCCamera.this.mFaceDetection.stopCheckLoop();
          MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 59);
          MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 59, 5000L);
          HTCCamera.this.closeAllExtensionMenus();
        }
      });
      if (this.mGpuEffectContr != null)
        this.mGpuEffectContr.setEffectMenuBar(this.mEffectPanel);
    }
  }

  private void initOnCreate_after_preview()
  {
    LOG.W("HTCCamera", "initOnCreate_after_preview() - start");
    if (!this.mNeed_initOnCreate)
    {
      LOG.W("HTCCamera", "initOnCreate_after_preview() - not need to do");
      return;
    }
    this.mNeed_initOnCreate = false;
    ViewStub localViewStub;
    if (this.mMainLayout == null)
    {
      LOG.W("HTCCamera", "mMainLayout == null - initiate mMainLayout");
      if (!DisplayDevice.isDoubleShot())
      {
        localViewStub = (ViewStub)findViewById(2131230787);
        label64: this.mMainLayout = localViewStub.inflate();
      }
    }
    else
    {
      LOG.W("HTCCamera", "initOnCreate_after_preview() - set mMainLayout visible");
      this.mMainLayout.setVisibility(0);
      if (this.mSecondLayout == null)
        this.mSecondLayout = ((ViewStub)findViewById(2131230777)).inflate();
      this.mComponentManager.enableAutoInitialization(true);
      if ((DisplayDevice.isDoubleShot()) && (this.mSceneGuideLayout == null))
        this.mSceneGuideLayout = ((ViewStub)findViewById(2131230784)).inflate();
      if (this.mMainBar == null)
      {
        if (DisplayDevice.isDoubleShot())
          break label931;
        this.mMainBar = ((ViewStub)this.mMainLayout.findViewById(2131230969)).inflate();
        this.mMainBar_receiver = this.mMainBar.findViewById(2131230937);
        this.mMainBar_item = this.mMainBar.findViewById(2131230925);
        this.mMode_icon = ((ImageView)this.mMainBar.findViewById(2131230933));
        this.mMode_btn = ((Button)this.mMainBar.findViewById(2131230932));
        if (DisplayDevice.supportSecondCamera())
        {
          this.mSwitchBtn_icon = ((ImageView)this.mMainBar.findViewById(2131230748));
          this.mSwitch_btn = ((Button)this.mMainBar.findViewById(2131230747));
        }
        this.mCapture_icon = ((ImageView)this.mMainBar.findViewById(2131230941));
        if (CameraController.supportFlashLight())
        {
          this.mFlash_icon = ((ImageView)this.mMainBar.findViewById(2131230931));
          this.mFlash_btn = ((Button)this.mMainBar.findViewById(2131230930));
        }
        this.mEffect_icon = ((ImageView)this.mMainBar.findViewById(2131230929));
        this.mEffect_btn = ((Button)this.mMainBar.findViewById(2131230928));
        if (!com.android.amaze_camera.component.ThumbnailController.isSupported())
        {
          this.mPhoto_icon = ((ImageView)this.mMainBar.findViewById(2131230927));
          this.mPhoto_btn = ((Button)this.mMainBar.findViewById(2131230926));
        }
        this.mMode_btn.setBackgroundDrawable(ViewUtil.getCustomDrawable(this, 2131361794, 2130837711));
        if (this.mFlash_btn != null)
          this.mFlash_btn.setBackgroundDrawable(ViewUtil.getCustomDrawable(this, 2131361794, 2130837711));
        if (this.mSwitch_btn != null)
          this.mSwitch_btn.setBackgroundDrawable(ViewUtil.getCustomDrawable(this, 2131361794, 2130837711));
        this.mEffect_btn.setBackgroundDrawable(ViewUtil.getCustomDrawable(this, 2131361794, 2130837711));
        if (this.mPhoto_btn != null)
          this.mPhoto_btn.setBackgroundDrawable(ViewUtil.getCustomDrawable(this, 2131361794, 2130837711));
      }
      label523: if ((DisplayDevice.isDoubleShot()) && (this.mArrowHintLayout == null))
      {
        this.mArrowHintLayout = ((ViewStub)this.mMainLayout.findViewById(2131230985)).inflate();
        this.mArrowImgLayoutPort = ((RotateRelativeLayout)this.mArrowHintLayout.findViewById(2131230740));
        this.mArrowImgLayoutLand = ((RotateRelativeLayout)this.mArrowHintLayout.findViewById(2131230742));
        this.mArrowTextLayoutPort = ((RotateRelativeLayout)this.mArrowHintLayout.findViewById(2131230741));
        this.mArrowTextLayoutLand = ((RotateRelativeLayout)this.mArrowHintLayout.findViewById(2131230743));
      }
      if (DisplayDevice.showSceneInMenu() == true)
      {
        this.mSceneIndicatorLayout = ((ViewStub)findViewById(2131230800)).inflate();
        this.m_sceneContainer = ((RotateRelativeLayout)this.mSceneIndicatorLayout.findViewById(2131231064));
        this.m_sceneImage = ((ImageView)findViewById(2131231065));
      }
      if ((this.mFilmstrip_btn == null) && (DisplayDevice.supportThumbnailAlbumButton()) && (!DisplayDevice.isDoubleShot()))
      {
        this.mFilmstrip_layout = ((ViewStub)this.mMainLayout.findViewById(2131230975)).inflate();
        this.mFilmstrip_btn = ((ImageButton)this.mFilmstrip_layout.findViewById(2131230749));
        this.mFilmstrip_thumbnail = ((ImageView)this.mFilmstrip_layout.findViewById(2131230750));
        this.mFilmstrip_combine = ((View)this.mFilmstrip_btn.getParent());
      }
      if ((DisplayDevice.supportSwitchButton()) && (this.mRotateLayout_SwitchBtn == null))
      {
        this.mRotateLayout_SwitchBtn = ((RotateRelativeLayout)((ViewStub)this.mSecondLayout.findViewById(2131231093)).inflate().findViewById(2131230746));
        this.mRotateLayout_SwitchBtn.getCachePaint().setFilterBitmap(true);
        this.mSwitchBtn_icon = ((ImageView)this.mRotateLayout_SwitchBtn.findViewById(2131230748));
      }
      if (this.mCameraThread == null)
        break label1410;
      updateModeIcon(this.mCameraThread.mMode);
      updateCaptureIcon(this.mCameraThread.mMode);
      changeIndicatorLayout(this.mCameraThread.mMode, false);
      if (DisplayDevice.isDoubleShot())
        enableSceneIcon(this.mCameraThread.mMode, this.mCameraThread.is2ndCamera());
    }
    while (true)
    {
      openCaptureUI();
      LOG.W("HTCCamera", "initOnCreate_after_preview() - end");
      break;
      localViewStub = (ViewStub)findViewById(2131230789);
      break label64;
      label931: this.mMainBar = ((ViewStub)this.mMainLayout.findViewById(2131230981)).inflate();
      this.mMainBar_receiver = this.mMainBar.findViewById(2131230961);
      this.mMainBar_item = this.mMainBar.findViewById(2131230944);
      this.mMode_icon = ((ImageView)this.mMainBar.findViewById(2131230947));
      this.mMode_btn = ((Button)this.mMainBar.findViewById(2131230945));
      this.mMode_btn.setFocusable(false);
      this.mMode_text_ds = ((TextView)this.mMainBar.findViewById(2131230948));
      this.mMode_layout_ds = ((LinearLayout)this.mMainBar.findViewById(2131230946));
      this.mSwitchBtn_icon = ((ImageView)this.mMainBar.findViewById(2131230951));
      this.mSwitch_btn = ((Button)this.mMainBar.findViewById(2131230949));
      this.mSwitch_btn.setFocusable(false);
      this.mSwitch_text_ds = ((TextView)this.mMainBar.findViewById(2131230952));
      this.mSwitch_layout_ds = ((LinearLayout)this.mMainBar.findViewById(2131230950));
      this.mFlash_icon = ((ImageView)this.mMainBar.findViewById(2131230955));
      this.mFlash_btn = ((Button)this.mMainBar.findViewById(2131230953));
      this.mFlash_btn.setFocusable(false);
      this.mFlash_text_ds = ((TextView)this.mMainBar.findViewById(2131230956));
      this.mFlash_layout_ds = ((LinearLayout)this.mMainBar.findViewById(2131230954));
      this.mCapture_combine_ds = ((RelativeLayout)this.mMainLayout.findViewById(2131230987));
      this.mCapture_btn_ds = ((Button)this.mMainLayout.findViewById(2131230988));
      this.mCapture_btn_ds.setFocusable(false);
      this.mScene_icon_ds = ((ImageView)this.mMainBar.findViewById(2131230959));
      this.mScene_btn_ds = ((Button)this.mMainBar.findViewById(2131230957));
      this.mScene_btn_ds.setFocusable(false);
      this.mScene_text_ds = ((TextView)this.mMainBar.findViewById(2131230960));
      this.mScene_layout_ds = ((LinearLayout)this.mMainBar.findViewById(2131230958));
      this.mMode_btn.setBackgroundDrawable(ViewUtil.getCustomDrawable(this, 2131361794, 2130837712));
      if (this.mSwitch_btn != null)
        this.mSwitch_btn.setBackgroundDrawable(ViewUtil.getCustomDrawable(this, 2131361794, 2130837712));
      if (this.mFlash_btn != null)
        this.mFlash_btn.setBackgroundDrawable(ViewUtil.getCustomDrawable(this, 2131361794, 2130837712));
      this.mScene_btn_ds.setBackgroundDrawable(ViewUtil.getCustomDrawable(this, 2131361794, 2130837712));
      break label523;
      label1410: LOG.E("HTCCamera", "mCameraThread == null, can't update layout based on capture mode !!!");
    }
  }

  private void initZoomBar()
  {
    if (this.mZoomLayout == null)
    {
      this.mZoomLayout = ((ViewStub)this.mSecondLayout.findViewById(2131231095)).inflate();
      this.mZoomBar = ((ZoomBar)this.mZoomLayout.findViewById(2131231122));
      this.mZoomBar.setOnPositionChangeListner(new ScrollInterface.PositionChangeListner()
      {
        public void onProgressChanged(View paramView, int paramInt)
        {
          if ((HTCCamera.this.mSecondLayout == null) || (HTCCamera.this.mSecondLayout.getVisibility() != 0));
          while (true)
          {
            return;
            if ((HTCCamera.this.mZoomLayout != null) && (HTCCamera.this.mZoomLayout.getVisibility() == 0) && (HTCCamera.this.mCameraThread != null))
            {
              boolean bool = HTCCamera.this.mCameraThread.is3DMode();
              if (((HTCCamera.this.mCameraThread != null) && ((HTCCamera.this.mCameraThread.is2ndCamera()) || (bool) || (HTCCamera.this.getFaceNumber() > 0))) || (HTCCamera.mBlockCaptureUI == true))
                continue;
              HTCCamera.this.mCameraThread.changeZoom(paramInt);
              continue;
            }
          }
        }
      });
      this.mZoomBar.setFocusable(false);
      this.mZoomBarIn = ((RotateImageView)this.mZoomLayout.findViewById(2131231123));
      this.mZoomBarOut = ((RotateImageView)this.mZoomLayout.findViewById(2131231121));
      this.mZoomBarIn.setFocusable(false);
      this.mZoomBarOut.setFocusable(false);
      this.mZoomLayout.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          int i;
          if ((HTCCamera.this.mCameraThread != null) && ((HTCCamera.this.mCameraThread.is2ndCamera()) || (HTCCamera.this.getFaceNumber() > 0) || (HTCCamera.this.mIsZoomLocked)))
            i = 1;
          while (true)
          {
            return i;
            if (HTCCamera.this.mFocusingState == 1)
            {
              if (DisplayDevice.canCancelFocus() == true)
              {
                LOG.W("HTCCamera", "control Zoom bar when focusing, cancel focus");
                HTCCamera.this.cancelAutoFocus();
                LOG.W("HTCCamera", "UnBlock Capture UI - control Zoom bar and cancel focus");
                HTCCamera.access$1702(false);
              }
            }
            else
            {
              if (HTCCamera.mBlockCaptureUI != true)
                break label115;
              i = 1;
              continue;
            }
            LOG.W("HTCCamera", "control Zoom bar when focusing, but device can't cancel focus");
            i = 1;
            continue;
            label115: HTCCamera.this.mZoomBar.setTouchPosition((int)paramMotionEvent.getX() - HTCCamera.this.mZoomBarIn.getWidth());
            i = 1;
          }
        }
      });
      if ((this.mCameraThread != null) && (this.mCameraThread.isZoomRangeRetrieved()))
      {
        int j = this.mCameraThread.getMinimumZoom();
        int k = this.mCameraThread.getMaximumZoom();
        this.mZoomBar.setMinMax(j, k);
      }
    }
    int i = HTCCameraAdvanceSetting.mZoomValue;
    this.mZoomBar.setPosition(i);
  }

  public static boolean isKeyguardShow()
  {
    return mIsKeyguardShow;
  }

  private void loadSoundPool()
  {
    if (this.mSoundPool != null);
    while (true)
    {
      return;
      try
      {
        this.mSoundPoolVolume = getAudioManager().getStreamMaxVolume(4);
        this.mSoundPool = new SoundPool(2, 4, 0);
        this.mSoundID_beep = this.mSoundPool.load(this, 2131099648, 1);
        this.mSoundID_beep2 = this.mSoundPool.load(this, 2131099650, 1);
      }
      catch (Exception localException)
      {
        LOG.E("HTCCamera", "loadSoundPool() failed!!", localException);
      }
    }
  }

  private void mapFocusPoint_Screen2Preview(int paramInt1, int paramInt2, Rect paramRect)
  {
    if (this.mCameraThread == null)
      return;
    LOG.V("HTCCamera", "mapFocusPoint Screen - size: (" + paramRect.width() + ", " + paramRect.height() + ") point: (" + paramInt1 + ", " + paramInt2 + ")");
    int i = this.mCameraThread.getPreviewWidth();
    int j = this.mCameraThread.getPreviewHeight();
    int k;
    if (this.Display_Orientation == 1)
      k = i * paramInt1 / paramRect.width();
    for (int m = j * paramInt2 / paramRect.height(); ; m = j * (paramRect.width() - paramInt1) / paramRect.width())
    {
      LOG.V("HTCCamera", "mapFocusPoint Preview - size: (" + i + ", " + j + ") point: (" + k + ", " + m + ")");
      sendFocusMessage(k, m);
      break;
      k = i * paramInt2 / paramRect.height();
    }
  }

  private void onActionScreenClosed(ActionScreenEvent paramActionScreenEvent)
  {
    this.mActionScreen = null;
    if (this.mCameraThread == null);
    while (true)
    {
      return;
      if (isPowerWarning())
      {
        mBlockCaptureUI = false;
        continue;
      }
      if (this.mActivityOnPause)
      {
        LOG.W("HTCCamera", "Activity is already paused");
        continue;
      }
      if ((paramActionScreenEvent != null) && (paramActionScreenEvent.getCloseReason() != 2) && (this.mCameraThread.getMode() == 0) && (!isServiceMode()) && (this.mLastSavedMediaPath != null))
        this.mCameraThread.startScaladoPostProcessing(this.mLastSavedMediaPath);
      if (mIsKeyguardShow)
      {
        LOG.V("HTCCamera", "Key-guard is visible, start preview later");
        continue;
      }
      mBlockCaptureUI = true;
      MessageHandler.sendObtainMessage(this.mCameraHandler, 0, 0, 0, null);
      openCaptureUI();
    }
  }

  private void onCameraThreadRunning()
  {
    LOG.V("HTCCamera", "onCameraThreadRunning() - start");
    if (this.mCameraThread == null)
    {
      LOG.E("HTCCamera", "There is no camera thread");
      return;
    }
    EventManager localEventManager = this.mCameraThread.getEventManager();
    if (localEventManager != null)
    {
      localEventManager.addEventHandlerAsync("Media.Saved", this.mCameraThreadEventHandler);
      localEventManager.addEventHandlerAsync("Zoom.Changed", this.mCameraThreadEventHandler);
    }
    while (true)
    {
      this.m_EventManager.raiseEvent("CameraActivity.CameraThreadRunning");
      LOG.V("HTCCamera", "onCameraThreadRunning() - end");
      break;
      LOG.E("HTCCamera", "eventManager == null");
    }
  }

  private void onRemovableStorageEjected()
  {
    this.mMenuHandler.updateStorageLocationItem();
    saveSDCardState();
    if (CameraThread.Storage_Status == 0)
    {
      if ((this.mCameraThread == null) || (!this.mCameraThread.getRecorderStatus()))
        break label48;
      LOG.W("HTCCamera", "storage eject, Camera app finished on recording");
      finish();
    }
    while (true)
    {
      return;
      label48: MessageHandler.sendEmptyMessage(this.mCameraHandler, 53);
    }
  }

  private void onRemovableStorageMounted()
  {
    this.mMenuHandler.updateStorageLocationItem();
    saveSDCardState();
    if ((DCFRuler.StorageCardControl.bSupportPhoneStorage) && (DCFRuler.StorageCardControl.getStorageType() == 0) && (Environment.getExternalStorageState().equals("mounted")))
      QueryToggleStorage();
    while (true)
    {
      return;
      MessageHandler.sendEmptyMessage(this.mCameraHandler, 53);
    }
  }

  private void onRemovableStorageUnmounted()
  {
    this.mMenuHandler.updateStorageLocationItem();
    saveSDCardState();
    if ((this.mCameraThread != null) && (this.mCameraThread.getRecorderStatus()))
    {
      LOG.W("HTCCamera", "storage unmounted, Camera app finished on recording");
      finish();
    }
    while (true)
    {
      return;
      MessageHandler.sendEmptyMessage(this.mCameraHandler, 53);
    }
  }

  private void onRotateChanged(int paramInt)
  {
    if (((is3DCameraActivated()) && (paramInt != 1)) || (this.mNeed_initOnCreate))
      return;
    Resources localResources;
    int k;
    label235: label369: boolean bool1;
    label512: boolean bool2;
    RotateRelativeLayout localRotateRelativeLayout;
    if (!OrientationConfig.isEqual_UIOrientation(paramInt))
    {
      LOG.W("HTCCamera", "onRotateChanged old ui orientation = " + OrientationConfig.getUIOrientation() + ", set orientation = " + paramInt);
      float f1 = OrientationConfig.animation_fromDegrees(paramInt);
      float f2 = OrientationConfig.animation_toDegrees(paramInt);
      startRotateAnimation(f1, f2);
      if (DisplayDevice.isVirtualHWKeyRotated())
        updateVirtualHwKeysOrientation(paramInt, true);
      if (this.mPanel != null)
      {
        if (this.mMenuHandler != null)
          this.mMenuHandler.updateOrientation(paramInt);
        localResources = getResources();
        if ((paramInt != 0) && (paramInt != 2))
          break label767;
        if (this.mRotateLayout_Setting != null)
        {
          ViewGroup.LayoutParams localLayoutParams4 = this.mRotateLayout_Setting.getLayoutParams();
          localLayoutParams4.height = (int)localResources.getDimension(2131427476);
          this.mRotateLayout_Setting.setLayoutParams(localLayoutParams4);
          this.mRotateLayout_Setting.setOrientation(paramInt);
        }
        if (this.mRotateLayout_SubMenu_Setting != null)
        {
          ViewGroup.LayoutParams localLayoutParams3 = this.mRotateLayout_SubMenu_Setting.getLayoutParams();
          localLayoutParams3.height = (int)localResources.getDimension(2131427476);
          this.mRotateLayout_SubMenu_Setting.setLayoutParams(localLayoutParams3);
          this.mRotateLayout_SubMenu_Setting.setOrientation(paramInt);
        }
        k = (int)localResources.getDimension(2131427472);
        ViewGroup.LayoutParams localLayoutParams1 = this.mSubMenu_layout.getLayoutParams();
        localLayoutParams1.height = k;
        this.mSubMenu_layout.setLayoutParams(localLayoutParams1);
        ViewGroup.LayoutParams localLayoutParams2 = this.mPanel.getLayoutParams();
        localLayoutParams2.height = k;
        this.mPanel.setLayoutParams(localLayoutParams2);
      }
      if (this.mGpuEffectContr != null)
        this.mGpuEffectContr.setUiOrientation(paramInt);
      if (this.mRotateLayout_Indicator != null)
      {
        if ((this.mIndicatorLayout == null) || (this.mIndicatorLayout.getVisibility() != 0))
          break label895;
        AlphaAnimation localAlphaAnimation = AnimationManager.showAlphaAnimation(this.mRotateLayout_Indicator, 1.0F, 0.0F, 0, 400);
        29 local29 = new Animation.AnimationListener(paramInt)
        {
          public void onAnimationEnd(Animation paramAnimation)
          {
            if (DisplayDevice.isDoubleShot())
            {
              HTCCamera.this.setIconPosition(this.val$orientation, HTCCamera.this.mAutoCaptureLayout);
              HTCCamera.this.setIconPosition(this.val$orientation, HTCCamera.this.mMuteIndicatorLayout);
            }
          }

          public void onAnimationRepeat(Animation paramAnimation)
          {
          }

          public void onAnimationStart(Animation paramAnimation)
          {
          }
        };
        localAlphaAnimation.setAnimationListener(local29);
        this.mRotateLayout_Indicator.setOrientationDelay(paramInt);
      }
      if (this.mRotateLayout_Remaining != null)
        this.mRotateLayout_Remaining.setOrientation(paramInt);
      if (this.mFilmstrip_combine != null)
        ((RotateRelativeLayout)this.mFilmstrip_combine).setOrientationDelay(paramInt);
      if (DisplayDevice.isDoubleShot())
        updateSceneGuideOrientation(paramInt);
      updateSelfPortraitHint(paramInt);
      updateRotateViews(paramInt);
      if (this.mMode_ExtensionMenu != null)
        this.mMode_ExtensionMenu.setItemOrientation(paramInt);
      if (this.mFlash_ExtensionMenu != null)
        this.mFlash_ExtensionMenu.setItemOrientation(paramInt);
      if (this.mOnScreenCameraSwitchBtnContainer != null)
      {
        this.mUIHandler.removeMessages(101);
        if (this.mOnScreenCameraSwitchBtnContainer.getVisibility() != 0)
          break label930;
        MessageHandler.sendObtainMessageDelayed(this.mUIHandler, 101, paramInt, 0, null, 400L);
        AnimationManager.showAlphaAnimation(this.mOnScreenCameraSwitchBtnContainer, 1.0F, 0.0F, 0, 400);
      }
      int i = OrientationConfig.getUIOrientation();
      OrientationConfig.setUIOrientation(paramInt);
      this.m_EventManager.raiseEvent(new OrientationEvent("CameraActivity.RotateChanged", i, paramInt, f1, f2));
      if (DisplayDevice.showSceneInMenu() == true)
      {
        bool1 = HTCCameraAdvanceSetting.getIsRecordWithAudio(this);
        int j = HTCCameraAdvanceSetting.getSelfTimer(this);
        bool2 = false;
        if (j > 0)
          bool2 = true;
        if (this.m_sceneContainer != null)
        {
          if (this.m_sceneContainer.getVisibility() != 0)
            break label945;
          localRotateRelativeLayout = this.m_sceneContainer;
          if (this.m_sceneContainer.getVisibility() != 0)
            break label939;
        }
      }
    }
    label930: label939: for (boolean bool4 = true; ; bool4 = false)
    {
      showSceneIndicatorAnimation(localRotateRelativeLayout, 1.0F, 0.0F, 0, 400, paramInt, bool4, this.mCameraThread.mMode);
      MessageHandler.removeMessages(this.mCameraHandler, 43);
      MessageHandler.sendEmptyMessage(this.mCameraHandler, 43);
      if (this.mUIHandler != null)
      {
        if (this.mUIHandler.hasMessages(22))
        {
          MessageHandler.removeMessages(this.mUIHandler, 22);
          MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 22, 8000L);
        }
        if (this.mUIHandler.hasMessages(59))
        {
          MessageHandler.removeMessages(this.mUIHandler, 59);
          MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 59, 5000L);
        }
        if (this.mUIHandler.hasMessages(100))
        {
          MessageHandler.removeMessages(this.mUIHandler, 100);
          MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 100, 4000L);
        }
      }
      updateRecordingTimerOrientation(paramInt);
      break;
      label767: if (this.mRotateLayout_Setting != null)
      {
        ViewGroup.LayoutParams localLayoutParams6 = this.mRotateLayout_Setting.getLayoutParams();
        localLayoutParams6.height = (int)localResources.getDimension(2131427477);
        this.mRotateLayout_Setting.setLayoutParams(localLayoutParams6);
        this.mRotateLayout_Setting.setOrientation(paramInt);
      }
      if (this.mRotateLayout_SubMenu_Setting != null)
      {
        ViewGroup.LayoutParams localLayoutParams5 = this.mRotateLayout_SubMenu_Setting.getLayoutParams();
        localLayoutParams5.height = (int)localResources.getDimension(2131427477);
        this.mRotateLayout_SubMenu_Setting.setLayoutParams(localLayoutParams5);
        this.mRotateLayout_SubMenu_Setting.setOrientation(paramInt);
      }
      if (!DisplayDevice.isDoubleShot())
      {
        k = (int)localResources.getDimension(2131427473);
        break label235;
      }
      k = (int)localResources.getDimension(2131427572);
      break label235;
      label895: this.mRotateLayout_Indicator.setOrientation(paramInt);
      if (!DisplayDevice.isDoubleShot())
        break label369;
      setIconPosition(paramInt, this.mAutoCaptureLayout);
      setIconPosition(paramInt, this.mMuteIndicatorLayout);
      break label369;
      rotateOnScreenCameraSwitchButton(paramInt, false);
      break label512;
    }
    label945: setLayoutForTimer(bool2, paramInt, this.mCameraThread.mMode);
    if (!bool1);
    for (boolean bool3 = true; ; bool3 = false)
    {
      setLayoutForRecordIcon(bool3, OrientationConfig.getUIOrientation(), this.mCameraThread.mMode);
      this.m_sceneContainer.setOrientation(paramInt);
      break;
    }
  }

  private void onTouchCapture_Camcorder()
  {
    LOG.V("HTCCamera", "Touch button to start video recording");
    this.mIsCaptureTriggered = true;
    if (this.mFocusingState == 1)
      if ((is3DCameraActivated()) && (this.m3DOptimalLandscape == true))
      {
        LOG.W("HTCCamera", "onTouchCapture_Camcorder, 3D portrait capture !!!");
        int i = OrientationConfig.mapOrientation_Event2UI(this.mLastOrientation);
        MessageHandler.sendObtainMessage(this.mUIHandler, 80, 2131361848, i, null);
      }
    while (true)
    {
      return;
      this.m3DTriggerCapture = true;
      if ((DisplayDevice.canCancelFocus()) && (this.mCameraThread != null) && (this.mCameraThread.getRecorderStatus()))
      {
        cancelAutoFocus();
        LOG.W("HTCCamera", "UnBlock Capture UI - stop recorder and cancel focus");
        mBlockCaptureUI = false;
        MessageHandler.removeMessages(this.mUIHandler, 15);
        triggerRecord();
        continue;
      }
      LOG.W("HTCCamera", "Press Capture when focusing - mFocusingState = FOCUSING_PRESS_CAPTURE");
      this.mFocusingState = 3;
      closeCaptureUI();
      if (this.mMainBar != null)
      {
        this.mMainBar.setVisibility(0);
        setPanelVisible(true);
      }
      this.mIsCaptureTriggered = false;
    }
  }

  private void onTouchCapture_Camera()
  {
    LOG.V("HTCCamera", "Touch button to take picture");
    this.mIsCaptureTriggered = true;
    if ((getFaceNumber() > 0) && (!mIsSelfPortraitTask))
    {
      MessageHandler.removeMessages(this.mUIHandler, 37);
      MessageHandler.sendObtainMessage(this.mUIHandler, 37, 2131362091, 0, null);
      this.mIsCaptureTriggered = false;
    }
    while (true)
    {
      return;
      if ((is3DCameraActivated()) && (this.m3DOptimalLandscape == true))
      {
        LOG.W("HTCCamera", "onTouchCapture_Camera, 3D portrait capture !!!");
        int i = OrientationConfig.mapOrientation_Event2UI(this.mLastOrientation);
        MessageHandler.sendObtainMessage(this.mUIHandler, 80, 2131361848, i, null);
        continue;
      }
      this.m3DTriggerCapture = true;
      if (this.mFocusingState == 1)
      {
        LOG.W("HTCCamera", "Press Capture when focusing - mFocusingState = FOCUSING_PRESS_CAPTURE");
        this.mFocusingState = 3;
        if (this.mivGrid != null)
          this.mivGrid.setVisibility(4);
        closeCaptureUI();
        if ((!DisplayDevice.isDoubleShot()) && (this.mMainBar != null))
        {
          this.mMainBar.setVisibility(0);
          setPanelVisible(true);
          enableMainBarItems(false);
          showMainBarItems(false, true);
        }
        this.mIsCaptureTriggered = false;
        continue;
      }
      if ((this.mtvSelfTimer != null) && (this.mtvSelfTimer.getVisibility() == 0))
      {
        closeSelfTimer();
        this.mIsCaptureTriggered = false;
        continue;
      }
      MessageHandler.removeMessages(this.mUIHandler, 15);
      triggerTakePicture();
    }
  }

  private void openSecondLayout(boolean paramBoolean)
  {
    if ((this.mSecondLayout == null) || (this.mSecondLayout.getVisibility() == 0));
    while (true)
    {
      return;
      this.mSecondLayout.clearAnimation();
      if (paramBoolean == true)
        AnimationManager.showAlphaAnimation(this.mSecondLayout, 0.0F, 1.0F, 0, 300);
      this.mSecondLayout.setVisibility(0);
      if (!DisplayDevice.isDoubleShot())
        updateZoomBarVisible();
      this.m_EventManager.raiseEvent("SecondLayout.Opened");
    }
  }

  private void playSoundPool(int paramInt)
  {
    if (this.mSoundPool == null)
      LOG.E("HTCCamera", "playSoundPool() - mSoundPool = null");
    while (true)
    {
      return;
      try
      {
        getAudioManager();
        switch (paramInt)
        {
        case 2131099648:
          if (this.mStreamID_beep != 0)
            this.mSoundPool.stop(this.mStreamID_beep);
          this.mStreamID_beep = this.mSoundPool.play(this.mSoundID_beep, 1.0F, 1.0F, 1, 0, 1.0F);
        case 2131099650:
        case 2131099649:
        }
      }
      catch (Exception localException)
      {
        LOG.E("HTCCamera", "playSoundPool() failed!!", localException);
      }
      continue;
      if (this.mStreamID_beep2 != 0)
        this.mSoundPool.stop(this.mStreamID_beep2);
      this.mStreamID_beep2 = this.mSoundPool.play(this.mSoundID_beep2, 1.0F, 1.0F, 1, 0, 1.0F);
      continue;
    }
  }

  private void prepareOpeningExtensionMenu(SlidingMenu paramSlidingMenu)
  {
    closeAllExtensionMenus();
    if ((this.mEffectPanel != null) && ((this.mEffectPanel.isOpen()) || (!this.mEffectPanel.isReady())))
      this.mGpuEffectContr.openEffectMenu(false, true);
    MessageHandler.removeMessages(this.mUIHandler, 100);
    MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 100, 4000L);
  }

  private void releaseSoundPool()
  {
    if (this.mSoundPool != null)
    {
      this.mSoundPool.release();
      this.mSoundPool = null;
    }
  }

  private void resetFocusView()
  {
    LOG.V("HTCCamera", "resetFocusView() - start");
    this.mCanShowFocusView = false;
    MessageHandler.removeMessages(this.mUIHandler, 24);
    MessageHandler.removeMessages(this.mUIHandler, 25);
    MessageHandler.removeMessages(this.mUIHandler, 26);
    MessageHandler.removeMessages(this.mUIHandler, 27);
    MessageHandler.removeMessages(this.mUIHandler, 21);
    if (!DisplayDevice.isDoubleShot())
      if ((this.mivFocusingView != null) && (this.mivFocusedView != null))
      {
        this.mivFocusingView.setVisibility(4);
        this.mivFocusedView.setVisibility(4);
      }
    while (true)
    {
      LOG.V("HTCCamera", "resetFocusView() - end");
      return;
      if ((this.mFocusAnimation == null) || (this.m_CancelFocusFromTouch))
        continue;
      ((CameraFocusWidget)this.mFocusAnimation).setStatus(3, 0.0F, 0.0F);
    }
  }

  private void resetGridView()
  {
    boolean bool;
    if ((isRequestName(IntentManager.RequestName.Contacts) == true) || (isRequestName(IntentManager.RequestName.Square) == true))
    {
      bool = false;
      if ((this.mCameraThread == null) || (this.mCameraThread.mMode != 0) || (!bool))
        break label185;
      if (this.mGridLayout == null)
        this.mGridLayout = ((ViewStub)findViewById(2131230768)).inflate();
      if (this.mivGrid != null)
        this.mivGrid.setVisibility(8);
      if (!HTCCameraAdvanceSetting.getPrefenceBoolean(this, "pref_camera_image_ratio").booleanValue())
        break label156;
      LOG.V("HTCCamera", "Reset View: gridview_wide_landscape");
      this.mivGrid = ((ImageView)this.mGridLayout.findViewById(2131230872));
      label124: this.mivGrid.setVisibility(0);
      LOG.V("HTCCamera", "Reset View: Grid Visible");
    }
    while (true)
    {
      return;
      bool = HTCCameraAdvanceSetting.getPrefenceBoolean(this, "pref_grid").booleanValue();
      break;
      label156: LOG.V("HTCCamera", "Reset View: gridview_4by3_landscape");
      this.mivGrid = ((ImageView)this.mGridLayout.findViewById(2131230873));
      break label124;
      label185: if (this.mivGrid != null)
        this.mivGrid.setVisibility(4);
      LOG.V("HTCCamera", "Reset View: Grid InVisible");
    }
  }

  private void resetTapCapture(int paramInt1, int paramInt2)
  {
    this.mStartTime = System.currentTimeMillis();
    this.mStartTapCapture = false;
    this.mFocusPos_X = paramInt1;
    this.mFocusPos_Y = paramInt2;
  }

  private void reset_surface_view(int paramInt1, int paramInt2)
  {
    ViewGroup.LayoutParams localLayoutParams = this.mSurfaceView.getLayoutParams();
    LOG.V("HTCCamera", "reset_surface_view before reset, lp.width=" + localLayoutParams.width + ", lp.height=" + localLayoutParams.height);
    localLayoutParams.width = -1;
    localLayoutParams.height = -1;
    if (this.Display_Orientation == 1)
    {
      LOG.V("HTCCamera", "reset_surface_view - landscape");
      localLayoutParams.width = (paramInt1 * DisplayDevice.SCREEN_HEIGHT / paramInt2);
      this.mSurfaceWidth = localLayoutParams.width;
    }
    for (this.mSurfaceHeight = DisplayDevice.SCREEN_HEIGHT; ; this.mSurfaceHeight = localLayoutParams.height)
    {
      do
      {
        LOG.V("HTCCamera", "reset_surface_view lp width=" + localLayoutParams.width + "lp Height=" + localLayoutParams.height + " width=" + paramInt1 + "height=" + paramInt2);
        this.mSurfaceView.requestLayout();
        return;
      }
      while ((this.Display_Orientation != 0) && (this.Display_Orientation != 3));
      LOG.V("HTCCamera", "reset_surface_view - portrait");
      localLayoutParams.height = (paramInt1 * DisplayDevice.SCREEN_HEIGHT / paramInt2);
      this.mSurfaceWidth = DisplayDevice.SCREEN_HEIGHT;
    }
  }

  private void restoreVolume()
  {
    AudioManager localAudioManager = getAudioManager();
    if (this.mOriginalVolume_Alarm != -1)
    {
      LOG.W("HTCCamera", "restore AudioManager.STREAM_ALARM, mOriginalVolume_Alarm = " + this.mOriginalVolume_Alarm);
      localAudioManager.setStreamVolume(4, this.mOriginalVolume_Alarm, 0);
      this.mOriginalVolume_Alarm = -1;
    }
    if (this.mOriginalVolume_Ring != -1)
    {
      LOG.W("HTCCamera", "restore AudioManager.STREAM_RING, mOriginalVolume_Ring = " + this.mOriginalVolume_Ring);
      localAudioManager.setStreamVolume(2, this.mOriginalVolume_Ring, 0);
      this.mOriginalVolume_Ring = -1;
    }
  }

  private void rotateOnScreenCameraSwitchButton(int paramInt, boolean paramBoolean)
  {
    if (this.mOnScreenCameraSwitchBtnContainer == null)
      return;
    this.mOnScreenCameraSwitchBtnContainer.setOrientation(paramInt);
    if (paramBoolean)
      AnimationManager.showAlphaAnimation(this.mOnScreenCameraSwitchBtnContainer, 0.0F, 1.0F, 0, 400);
    RelativeLayout.LayoutParams localLayoutParams1 = (RelativeLayout.LayoutParams)this.mOnScreenCameraSwitchBtnContainer.getLayoutParams();
    Resources localResources = getResources();
    RelativeLayout.LayoutParams localLayoutParams2 = (RelativeLayout.LayoutParams)this.mOnScreenSwitch_receiver.getLayoutParams();
    if ((paramInt == 0) || (paramInt == 2))
    {
      localLayoutParams1.width = localResources.getDimensionPixelSize(2131427440);
      localLayoutParams1.height = localResources.getDimensionPixelSize(2131427441);
      localLayoutParams1.topMargin = localResources.getDimensionPixelSize(2131427442);
      localLayoutParams1.leftMargin = localResources.getDimensionPixelSize(2131427444);
      localLayoutParams2.topMargin = localResources.getDimensionPixelSize(2131427448);
    }
    for (localLayoutParams2.leftMargin = localResources.getDimensionPixelSize(2131427450); ; localLayoutParams2.leftMargin = localResources.getDimensionPixelSize(2131427451))
    {
      this.mOnScreenCameraSwitchBtnContainer.setLayoutParams(localLayoutParams1);
      break;
      localLayoutParams1.width = localResources.getDimensionPixelSize(2131427441);
      localLayoutParams1.height = localResources.getDimensionPixelSize(2131427440);
      localLayoutParams1.topMargin = localResources.getDimensionPixelSize(2131427443);
      localLayoutParams1.leftMargin = localResources.getDimensionPixelSize(2131427445);
      localLayoutParams2.topMargin = localResources.getDimensionPixelSize(2131427449);
    }
  }

  private final void saveSDCardState()
  {
    HTCCameraAdvanceSetting.writePreference(this, "pref_sdcard_state", DCFRuler.StorageCardControl.getSDCardState());
  }

  private void sendFocusMessage(int paramInt1, int paramInt2)
  {
    if (this.mSensorHandler != null)
      this.mSensorHandler.setifWaitFocus(true);
    if (!this.mIdle)
    {
      MessageHandler.removeMessages(this.mUIHandler, 36);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 36, this.SCREEN_DELAY);
    }
    MessageHandler.sendObtainMessage(this.mCameraHandler, 4, paramInt1, paramInt2, null);
  }

  private void setLayoutForRecordIcon(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (this.mCameraThread == null)
      return;
    Resources localResources;
    if (paramInt2 == 1)
    {
      localResources = getResources();
      if (!paramBoolean)
        break label158;
      if (paramInt1 != 0)
        break label62;
      this.SCENE_INDICATOR_TOP = localResources.getDimensionPixelSize(2131427518);
      this.SCENE_INDICATOR_RIGHT = localResources.getDimensionPixelSize(2131427519);
    }
    while (true)
    {
      setLayoutPosition(this.m_sceneContainer);
      break;
      break;
      label62: if (paramInt1 == 1)
      {
        this.SCENE_INDICATOR_TOP = localResources.getDimensionPixelSize(2131427520);
        this.SCENE_INDICATOR_RIGHT = localResources.getDimensionPixelSize(2131427521);
        continue;
      }
      if (paramInt1 == 2)
      {
        this.SCENE_INDICATOR_TOP = localResources.getDimensionPixelSize(2131427522);
        this.SCENE_INDICATOR_RIGHT = localResources.getDimensionPixelSize(2131427523);
        continue;
      }
      if (paramInt1 != 3)
        continue;
      this.SCENE_INDICATOR_TOP = localResources.getDimensionPixelSize(2131427524);
      this.SCENE_INDICATOR_RIGHT = localResources.getDimensionPixelSize(2131427525);
      continue;
      label158: this.SCENE_INDICATOR_TOP = localResources.getDimensionPixelSize(2131427509);
      this.SCENE_INDICATOR_RIGHT = localResources.getDimensionPixelSize(2131427508);
    }
  }

  private void setLayoutForTimer(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (this.mCameraThread == null)
      return;
    Resources localResources;
    if (paramInt2 == 0)
    {
      localResources = getResources();
      if (!paramBoolean)
        break label157;
      if (paramInt1 != 0)
        break label61;
      this.SCENE_INDICATOR_TOP = localResources.getDimensionPixelSize(2131427510);
      this.SCENE_INDICATOR_RIGHT = localResources.getDimensionPixelSize(2131427511);
    }
    while (true)
    {
      setLayoutPosition(this.m_sceneContainer);
      break;
      break;
      label61: if (paramInt1 == 1)
      {
        this.SCENE_INDICATOR_TOP = localResources.getDimensionPixelSize(2131427512);
        this.SCENE_INDICATOR_RIGHT = localResources.getDimensionPixelSize(2131427513);
        continue;
      }
      if (paramInt1 == 2)
      {
        this.SCENE_INDICATOR_TOP = localResources.getDimensionPixelSize(2131427514);
        this.SCENE_INDICATOR_RIGHT = localResources.getDimensionPixelSize(2131427515);
        continue;
      }
      if (paramInt1 != 3)
        continue;
      this.SCENE_INDICATOR_TOP = localResources.getDimensionPixelSize(2131427516);
      this.SCENE_INDICATOR_RIGHT = localResources.getDimensionPixelSize(2131427517);
      continue;
      label157: this.SCENE_INDICATOR_TOP = localResources.getDimensionPixelSize(2131427509);
      this.SCENE_INDICATOR_RIGHT = localResources.getDimensionPixelSize(2131427508);
    }
  }

  private void setLayoutPosition(RotateRelativeLayout paramRotateRelativeLayout)
  {
    if (paramRotateRelativeLayout != null)
    {
      RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)paramRotateRelativeLayout.getLayoutParams();
      localLayoutParams.addRule(11);
      localLayoutParams.addRule(10);
      localLayoutParams.topMargin = this.SCENE_INDICATOR_TOP;
      localLayoutParams.rightMargin = this.SCENE_INDICATOR_RIGHT;
      paramRotateRelativeLayout.setLayoutParams(localLayoutParams);
    }
  }

  private void setMaxBrightness()
  {
    LOG.V("HTCCamera", "setMaxBrightness()");
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS");
    LOG.W("########## HtcSettingsReceiver ##########", localSimpleDateFormat.format(Calendar.getInstance().getTime()) + "(ms) onReceive-start");
    Intent localIntent = new Intent();
    localIntent.setAction("com.android.settings.request.BRIGHTNESS_MAX");
    sendBroadcast(localIntent);
  }

  private void setOldBrightness()
  {
    LOG.V("HTCCamera", "setOldBrightness()");
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS");
    LOG.W("########## HtcSettingsReceiver ##########", localSimpleDateFormat.format(Calendar.getInstance().getTime()) + "(ms) onReceive-start");
    Intent localIntent = new Intent();
    localIntent.setAction("com.android.settings.request.BRIGHTNESS_NORMAL");
    sendBroadcast(localIntent);
  }

  private void setPanelVisible(boolean paramBoolean)
  {
    if (this.mPanel == null);
    while (true)
    {
      return;
      if (paramBoolean == true)
      {
        this.mPanel.setVisibility(0);
        continue;
      }
      if ((this.mPanel.isOpened()) || (this.mPanel.isMoving()))
        this.mPanel.close();
      this.mPanel.setVisibility(4);
      MessageHandler.removeMessages(this.mUIHandler, 66);
    }
  }

  private void setSceneGuidePosition(int paramInt, RotateRelativeLayout paramRotateRelativeLayout)
  {
    Resources localResources;
    RelativeLayout.LayoutParams localLayoutParams;
    int i;
    int j;
    int k;
    int m;
    if (paramRotateRelativeLayout != null)
    {
      localResources = getResources();
      localLayoutParams = (RelativeLayout.LayoutParams)paramRotateRelativeLayout.getLayoutParams();
      i = 0;
      j = 0;
      k = 0;
      m = 0;
      switch (paramInt)
      {
      default:
      case 0:
      case 2:
      case 1:
      case 3:
      }
    }
    while (true)
    {
      localLayoutParams.setMargins(i, j, k, m);
      paramRotateRelativeLayout.setLayoutParams(localLayoutParams);
      paramRotateRelativeLayout.setOrientation(paramInt);
      return;
      localLayoutParams.addRule(11, -1);
      localLayoutParams.addRule(12, 0);
      i = localResources.getDimensionPixelSize(2131427550);
      j = localResources.getDimensionPixelSize(2131427551);
      continue;
      localLayoutParams.addRule(11, -1);
      localLayoutParams.addRule(12, 0);
      j = localResources.getDimensionPixelSize(2131427552);
      k = localResources.getDimensionPixelSize(2131427553);
      continue;
      localLayoutParams.addRule(11, 0);
      localLayoutParams.addRule(12, -1);
      i = localResources.getDimensionPixelSize(2131427554);
      k = localResources.getDimensionPixelSize(2131427555);
      m = localResources.getDimensionPixelSize(2131427556);
      continue;
      localLayoutParams.addRule(11, 0);
      localLayoutParams.addRule(12, -1);
      i = localResources.getDimensionPixelSize(2131427557);
    }
  }

  private void setTipTextPosition(int paramInt, RelativeLayout paramRelativeLayout)
  {
    LinearLayout.LayoutParams localLayoutParams;
    if (paramRelativeLayout != null)
    {
      localLayoutParams = (LinearLayout.LayoutParams)paramRelativeLayout.getLayoutParams();
      if ((paramInt != 0) && (paramInt != 1))
        break label33;
    }
    label33: for (localLayoutParams.leftMargin = 70; ; localLayoutParams.leftMargin = 0)
    {
      paramRelativeLayout.setLayoutParams(localLayoutParams);
      return;
    }
  }

  /** @deprecated */
  public static void setWaitKeyguardBeforePreview(boolean paramBoolean)
  {
    monitorenter;
    try
    {
      mIsWaitKeyguardBeforePreview = paramBoolean;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  private void showArrowLayout(int paramInt)
  {
    if (this.mArrowImgLayoutPort == null);
    while (true)
    {
      return;
      AlphaAnimation localAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
      localAlphaAnimation.setStartOffset(5000L);
      localAlphaAnimation.setDuration(1000L);
      localAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnimation)
        {
          HTCCamera.this.mArrowImgLayoutPort.setVisibility(4);
          HTCCamera.this.mArrowImgLayoutLand.setVisibility(4);
          HTCCamera.this.mArrowTextLayoutPort.setVisibility(4);
          HTCCamera.this.mArrowTextLayoutLand.setVisibility(4);
        }

        public void onAnimationRepeat(Animation paramAnimation)
        {
        }

        public void onAnimationStart(Animation paramAnimation)
        {
        }
      });
      if ((paramInt == 0) || (paramInt == 2))
      {
        this.mArrowImgLayoutPort.setVisibility(0);
        this.mArrowImgLayoutPort.startAnimation(localAlphaAnimation);
        this.mArrowTextLayoutPort.setVisibility(0);
        this.mArrowTextLayoutPort.startAnimation(localAlphaAnimation);
        continue;
      }
      this.mArrowImgLayoutLand.setVisibility(0);
      this.mArrowImgLayoutLand.startAnimation(localAlphaAnimation);
      this.mArrowTextLayoutLand.setOrientation(1);
      this.mArrowTextLayoutLand.setVisibility(0);
      this.mArrowTextLayoutLand.startAnimation(localAlphaAnimation);
    }
  }

  private void showHDRSceneGuide()
  {
    if (this.mSceneGuideLayout == null);
    while (true)
    {
      return;
      if (this.mSceneHDRLayout == null)
        this.mSceneHDRLayout = ((RotateRelativeLayout)((ViewStub)this.mSceneGuideLayout.findViewById(2131231057)).inflate().findViewById(2131231063));
      this.mSceneHDRLayout.setVisibility(0);
      MessageHandler.removeMessages(this.mUIHandler, 85);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 85, 3000L);
    }
  }

  private void showLandscapeSceneGuide()
  {
    if (this.mSceneGuideLayout == null);
    while (true)
    {
      return;
      if ((this.mSceneLandscapeLayout == null) && (this.mSceneLandscapePortLayout == null))
      {
        View localView2 = ((ViewStub)this.mSceneGuideLayout.findViewById(2131231059)).inflate();
        this.mSceneLandscapeLayout = ((RotateRelativeLayout)localView2.findViewById(2131231066));
        this.mbackIcon_land = ((ImageView)this.mSceneLandscapeLayout.findViewById(2131231067));
        this.mfrontIcon_land = ((ImageView)this.mSceneLandscapeLayout.findViewById(2131231069));
        this.mFront_land = ((RelativeLayout)this.mSceneLandscapeLayout.findViewById(2131231068));
        this.mSceneLandscapePortLayout = ((RotateRelativeLayout)localView2.findViewById(2131231070));
        this.mbackIcon_port = ((ImageView)this.mSceneLandscapePortLayout.findViewById(2131231071));
        this.mfrontIcon_port = ((ImageView)this.mSceneLandscapePortLayout.findViewById(2131231073));
        this.mFront_port = ((RelativeLayout)this.mSceneLandscapePortLayout.findViewById(2131231072));
        init_swing();
      }
      if (this.mSceneLandscapeTipLayout == null)
      {
        View localView1 = ((ViewStub)this.mSceneGuideLayout.findViewById(2131231061)).inflate();
        this.mSceneLandscapeTipLayout = ((RotateRelativeLayout)localView1.findViewById(2131231074));
        this.mSceneLandscapeTipTextLayout = ((RelativeLayout)localView1.findViewById(2131231076));
        this.mSceneLandscapeTipText = ((TextView)localView1.findViewById(2131231077));
      }
      this.mUIHandler.postDelayed(this.mRunnable_Swing, this.CHECK_SWING_DURATION);
    }
  }

  private void showPortraitSceneGuide()
  {
    if (this.mSceneGuideLayout == null);
    while (true)
    {
      return;
      if (this.mScenePortraitLayout == null)
        this.mScenePortraitLayout = ((RotateRelativeLayout)((ViewStub)this.mSceneGuideLayout.findViewById(2131231053)).inflate().findViewById(2131231078));
      this.mScenePortraitLayout.setVisibility(0);
      MessageHandler.removeMessages(this.mUIHandler, 74);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 74, 3000L);
    }
  }

  private void showSceneIndicator(boolean paramBoolean)
  {
    if (this.m_sceneContainer == null);
    while (true)
    {
      return;
      if ((paramBoolean) && (this.mCameraThread.is2ndCamera() != true))
      {
        this.m_sceneContainer.setVisibility(0);
        continue;
      }
      this.m_sceneContainer.setVisibility(4);
    }
  }

  private void showSelfPortraitHint()
  {
    if (this.mSecondLayout == null);
    while (true)
    {
      return;
      if (this.mRotateLayout_SelfPortrait == null)
      {
        View localView = ((ViewStub)this.mSecondLayout.findViewById(2131231099)).inflate();
        this.mRotateLayout_SelfPortrait = ((RotateRelativeLayout)localView.findViewById(2131231102));
        this.mSelfPortraitHint_layout = ((LinearLayout)localView.findViewById(2131231103));
      }
      if (this.mRotateLayout_SelfPortrait.getVisibility() == 0)
        continue;
      this.mRotateLayout_SelfPortrait.setVisibility(0);
      updateSelfPortraitHint(OrientationConfig.getUIOrientation());
    }
  }

  private void showSunNightSceneGuide()
  {
    if (this.mSceneGuideLayout == null);
    while (true)
    {
      return;
      if (this.mSceneSunNightLayout == null)
        this.mSceneSunNightLayout = ((RotateRelativeLayout)((ViewStub)this.mSceneGuideLayout.findViewById(2131231055)).inflate().findViewById(2131231088));
      this.mSceneSunNightLayout.setVisibility(0);
      MessageHandler.removeMessages(this.mUIHandler, 75);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 75, 3000L);
    }
  }

  private void showTimerBase(boolean paramBoolean)
  {
    if (this.mTimerBase == null)
      this.mTimerBase = ((ImageView)((ViewStub)findViewById(2131230779)).inflate().findViewById(2131231114));
    if (this.mTimerBase.getVisibility() == 0);
    while (true)
    {
      return;
      updateTimerBase();
      this.mTimerBase.setVisibility(0);
    }
  }

  private void showZoomBar(boolean paramBoolean)
  {
    if (this.mCameraThread == null);
    while (true)
    {
      return;
      if ((!this.mCameraThread.is2ndCamera()) && (getFaceNumber() <= 0) && (!this.mIsZoomLocked) && (this.mSecondLayout != null) && (this.mSecondLayout.getVisibility() == 0))
      {
        initZoomBar();
        this.mZoomLayout.clearAnimation();
        if (this.mZoomLayout.getVisibility() == 0)
          continue;
        if (this.mIndicatorLayout != null)
        {
          int j = DisplayDevice.INDICATORE_LAYOUT_MARGIN_WITH_ZOOM_BAR;
          ViewGroup.MarginLayoutParams localMarginLayoutParams2 = (ViewGroup.MarginLayoutParams)this.mIndicatorLayout.getLayoutParams();
          localMarginLayoutParams2.setMargins(localMarginLayoutParams2.leftMargin, j, localMarginLayoutParams2.rightMargin, localMarginLayoutParams2.bottomMargin);
          this.mIndicatorLayout.setLayoutParams(localMarginLayoutParams2);
        }
        if (this.mRotateLayout_SwitchBtn != null)
        {
          int i = DisplayDevice.SWITCH_BUTTON_MARGIN_WITH_ZOOM_BAR;
          ViewGroup.MarginLayoutParams localMarginLayoutParams1 = (ViewGroup.MarginLayoutParams)this.mRotateLayout_SwitchBtn.getLayoutParams();
          localMarginLayoutParams1.setMargins(localMarginLayoutParams1.leftMargin, i, localMarginLayoutParams1.rightMargin, localMarginLayoutParams1.bottomMargin);
          this.mRotateLayout_SwitchBtn.setLayoutParams(localMarginLayoutParams1);
        }
        if (paramBoolean == true)
          AnimationManager.showAlphaAnimation(this.mZoomLayout, 0.0F, 1.0F, 0, 300);
        this.mZoomLayout.setVisibility(0);
        continue;
      }
    }
  }

  private void startFocusFromLongPressKey()
  {
    if ((this.mCameraThread == null) || (this.mCameraThread.mMode == 1));
    while (true)
    {
      return;
      MessageHandler.removeMessages(this.mUIHandler, 31);
      MessageHandler.removeMessages(this.mUIHandler, 32);
      MessageHandler.removeMessages(this.mUIHandler, 33);
      bFocusFromPress = true;
      if (mFocusMode == 1)
      {
        handleTouchFocus(mFocus_Screen_X, mFocus_Screen_Y);
        continue;
      }
      if ((this.mFaceDetection != null) && (this.mFaceDetection.isStartDetection() == true) && (this.mFaceDetection.quickFocus() == true))
        continue;
      MessageHandler.removeMessages(this.mCameraHandler, 31);
      MessageHandler.sendEmptyMessage(this.mCameraHandler, 31);
      handleSensorFocus();
    }
  }

  private void startRotateAnimation(float paramFloat1, float paramFloat2)
  {
    LOG.W("HTCCamera", "startRotateAnimation from_degree = " + paramFloat1 + " to_degree = " + paramFloat2);
    if (!DisplayDevice.isDoubleShot())
    {
      if (this.mFilmstrip_combine != null)
        AnimationManager.showRotateAnimation(this.mFilmstrip_combine, 0.0F, paramFloat2 - paramFloat1, 0, 400, false);
      if (this.mSwitchBtn_icon != null)
        AnimationManager.showRotateAnimation(this.mSwitchBtn_icon, paramFloat1, paramFloat2, 0, 400, true);
      if (this.mMode_icon != null)
        AnimationManager.showRotateAnimation(this.mMode_icon, paramFloat1, paramFloat2, 0, 400, true);
      if (this.mFlash_icon != null)
        AnimationManager.showRotateAnimation(this.mFlash_icon, paramFloat1, paramFloat2, 0, 400, true);
      if (this.mEffect_icon != null)
        AnimationManager.showRotateAnimation(this.mEffect_icon, paramFloat1, paramFloat2, 0, 400, true);
      if (this.mPhoto_icon != null)
        AnimationManager.showRotateAnimation(this.mPhoto_icon, paramFloat1, paramFloat2, 0, 400, true);
    }
    while (true)
    {
      return;
      if (this.mMode_layout_ds != null)
        AnimationManager.showRotateAnimation(this.mMode_layout_ds, paramFloat1, paramFloat2, 0, 400, true);
      if (this.mSwitch_layout_ds != null)
        AnimationManager.showRotateAnimation(this.mSwitch_layout_ds, paramFloat1, paramFloat2, 0, 400, true);
      if (this.mFlash_layout_ds != null)
        AnimationManager.showRotateAnimation(this.mFlash_layout_ds, paramFloat1, paramFloat2, 0, 400, true);
      if (this.mScene_layout_ds != null)
        AnimationManager.showRotateAnimation(this.mScene_layout_ds, paramFloat1, paramFloat2, 0, 400, true);
      if (this.mFilmstrip_combine == null)
        continue;
      AnimationManager.showRotateAnimation(this.mFilmstrip_combine, paramFloat1, paramFloat2, 0, 400, true);
    }
  }

  private void startSceneSelector()
  {
    if ((this.mScene_icon_ds == null) || (this.mScene_text_ds == null))
      return;
    LOG.W("HTCCamera", "Start - Go to Scene Selector");
    Intent localIntent = new Intent(getApplicationContext(), SceneSelectorActivity.class);
    localIntent.putExtra("default_position", mapSceneNumToSelector(this.mScene_Idx));
    if (!isServiceMode())
      localIntent.putExtra("scene_selector_mode", 0);
    while (true)
    {
      try
      {
        startActivityForResult(localIntent, 2);
      }
      catch (ActivityNotFoundException localActivityNotFoundException)
      {
        LOG.W("HTCCamera", "Start - Go to Scene Selector : " + localActivityNotFoundException);
      }
      break;
      localIntent.putExtra("scene_selector_mode", 1);
    }
  }

  private void stopNotesRecording()
  {
    Intent localIntent = new Intent("com.htc.notes.notesrecordingstopped");
    localIntent.putExtra("command", "stoprecord");
    sendBroadcast(localIntent);
  }

  private void stopRecordingIndicator()
  {
    LOG.V("HTCCamera", "stopRecordingIndicator()");
    MessageHandler.removeMessages(this.mUIHandler, 3);
    MessageHandler.removeMessages(this.mUIHandler, 2);
    closeSecondLayout(false);
    if (this.mRotateLayout_SwitchBtn != null)
      this.mRotateLayout_SwitchBtn.setVisibility(0);
    if (this.mRotateLayout_Recording != null)
      this.mRotateLayout_Recording.setVisibility(4);
    hideTimerBase(false);
  }

  private void stopSoundPool()
  {
    if (this.mSoundPool == null)
      LOG.E("HTCCamera", "stopSoundPool() - mSoundPool = null");
    while (true)
    {
      return;
      try
      {
        if (this.mStreamID_beep != 0)
          this.mSoundPool.stop(this.mStreamID_beep);
        if (this.mStreamID_beep2 == 0)
          continue;
        this.mSoundPool.stop(this.mStreamID_beep2);
      }
      catch (Exception localException)
      {
        LOG.E("HTCCamera", "stopSoundPool() failed!!", localException);
      }
    }
  }

  private void swing()
  {
    int i = OrientationConfig.mapOrientation_Event2UI(this.mLastOrientation);
    int j;
    if ((i == 0) || (i == 2))
      if ((this.mLastOrientation <= 90) || (this.mLastOrientation >= 270))
      {
        j = Math.abs(this.mLastOrientation);
        if (this.mLastOrientation >= 270)
        {
          this.To_ratio_port = ((this.mLastOrientation - 270) / this.SWING_STEP);
          if (this.To_ratio_port <= this.leftBound_ratio)
            this.To_ratio_port = this.leftBound_ratio;
          if (this.To_ratio_port >= this.rightBound_ratio)
            this.To_ratio_port = this.rightBound_ratio;
          this.To_ratio_port -= this.image_shift_ratio;
          TranslateAnimation localTranslateAnimation1 = new TranslateAnimation(2, this.From_ratio_port, 2, this.To_ratio_port, 2, 0.0F, 2, 0.0F);
          localTranslateAnimation1.setDuration(this.animation_DURATION);
          localTranslateAnimation1.setFillAfter(true);
          localTranslateAnimation1.setAnimationListener(new Animation.AnimationListener()
          {
            public void onAnimationEnd(Animation paramAnimation)
            {
              HTCCamera.access$15702(HTCCamera.this, HTCCamera.this.To_ratio_port);
            }

            public void onAnimationRepeat(Animation paramAnimation)
            {
            }

            public void onAnimationStart(Animation paramAnimation)
            {
            }
          });
          this.mFront_port.startAnimation(localTranslateAnimation1);
          if (j > this.SWING_STABLE_THRESHOLD)
            break label288;
          if (!this.mIS_LEVEL_PORT)
          {
            this.mIS_LEVEL_PORT = true;
            MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 78, 300L);
          }
        }
      }
    while (true)
    {
      this.mUIHandler.postDelayed(this.mRunnable_Swing, this.CHECK_SWING_DURATION);
      return;
      this.To_ratio_port = ((90 + this.mLastOrientation) / this.SWING_STEP);
      break;
      j = Math.abs(this.mLastOrientation - 180);
      this.To_ratio_port = ((270 - this.mLastOrientation) / this.SWING_STEP);
      break;
      label288: MessageHandler.removeMessages(this.mUIHandler, 78);
      MessageHandler.removeMessages(this.mUIHandler, 76);
      this.mbackIcon_port.setImageResource(2130837512);
      this.mSceneLandscapeTipText.setText(2131362160);
      this.mfrontIcon_port.setVisibility(0);
      this.mSceneLandscapePortLayout.setVisibility(0);
      this.mSceneLandscapeTipLayout.setVisibility(0);
      this.mIS_LEVEL_PORT = false;
      continue;
      if ((i != 1) && (i != 3))
        continue;
      int k;
      if ((this.mLastOrientation <= 359) && (this.mLastOrientation >= 180))
        k = Math.abs(this.mLastOrientation - 270);
      for (this.To_ratio_land = ((360 - this.mLastOrientation) / this.SWING_STEP); ; this.To_ratio_land = ((180 - this.mLastOrientation) / this.SWING_STEP))
      {
        if (this.To_ratio_land <= this.leftBound_ratio)
          this.To_ratio_land = this.leftBound_ratio;
        if (this.To_ratio_land >= this.rightBound_ratio)
          this.To_ratio_land = this.rightBound_ratio;
        this.To_ratio_land -= this.image_shift_ratio;
        TranslateAnimation localTranslateAnimation2 = new TranslateAnimation(2, 0.0F, 2, 0.0F, 2, this.From_ratio_land, 2, this.To_ratio_land);
        localTranslateAnimation2.setDuration(this.animation_DURATION);
        localTranslateAnimation2.setFillAfter(true);
        localTranslateAnimation2.setAnimationListener(new Animation.AnimationListener()
        {
          public void onAnimationEnd(Animation paramAnimation)
          {
            HTCCamera.access$15902(HTCCamera.this, HTCCamera.this.To_ratio_land);
          }

          public void onAnimationRepeat(Animation paramAnimation)
          {
          }

          public void onAnimationStart(Animation paramAnimation)
          {
          }
        });
        this.mFront_land.startAnimation(localTranslateAnimation2);
        if (k > this.SWING_STABLE_THRESHOLD)
          break label606;
        if (this.mIS_LEVEL)
          break;
        this.mIS_LEVEL = true;
        MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 77, 300L);
        break;
        k = Math.abs(this.mLastOrientation - 90);
      }
      label606: MessageHandler.removeMessages(this.mUIHandler, 77);
      MessageHandler.removeMessages(this.mUIHandler, 76);
      this.mbackIcon_land.setImageResource(2130837511);
      this.mSceneLandscapeTipText.setText(2131362160);
      this.mfrontIcon_land.setVisibility(0);
      this.mSceneLandscapeLayout.setVisibility(0);
      this.mSceneLandscapeTipLayout.setVisibility(0);
      this.mIS_LEVEL = false;
    }
  }

  private void switch3DMode()
  {
    this.m_EventManager.raiseEvent("3DMode.Switched");
    if (this.mForce2DMode)
      return;
    if (this.mCameraThread != null)
    {
      mBlockCaptureUI = true;
      resetFocusMode();
      releaseFaceDetection();
      this.mWaitResetSettings = true;
      LOG.W("HTCCamera", "restartCamera() - set mWaitResetSettings to true");
      closeSettingsPanel();
      if (!is3DCameraActivated())
        break label140;
      lockOrientation(1);
      label67: if ((this.mCameraThread == null) || (this.mCameraThread.m3DButtonStatus != 1))
        break label147;
      HTCCameraAdvanceSetting.writePreference(this, "pref_camera_3D_status", false);
      label94: hideZoomBar(false);
      this.m3DPreviewReady = false;
      if (this.mIdle)
        break label159;
      MessageHandler.sendEmptyMessage(this.mCameraHandler, 2);
      MessageHandler.sendObtainMessage(this.mCameraHandler, 0, 1, 0, "true");
    }
    while (true)
    {
      this.m3DOptimalLandscape = false;
      break;
      break;
      label140: unlockOrientation();
      break label67;
      label147: HTCCameraAdvanceSetting.writePreference(this, "pref_camera_3D_status", true);
      break label94;
      label159: activate();
    }
  }

  private void threadAccessCheck()
  {
    if (Thread.currentThread() != this.mMainThread)
      throw new RuntimeException("cross-thread access");
  }

  private void togglePanelState()
  {
    if ((this.mPanel == null) || (this.mPanel.getVisibility() != 0))
      LOG.W("HTCCamera", "togglePanelState() - failed, mPanel is not visible");
    while (true)
    {
      return;
      if ((!this.mPanel.isOpened()) && (this.mMenuHandler != null))
        this.mMenuHandler.updateDisableItems();
      if (!this.mPanel.isMoving())
        this.mPanel.animateToggle();
      closeAllExtensionMenus();
    }
  }

  private void triggerRecord()
  {
    LOG.V("HTCCamera", "triggerRecord() - start");
    if (mBlockCaptureUI == true)
      LOG.W("HTCCamera", "triggerRecord() - end, UI Block !!!");
    while (true)
    {
      return;
      if (isActionScreenOpen())
      {
        LOG.W("HTCCamera", "triggerRecord() - end, action screen open !!!");
        continue;
      }
      if ((is3DCameraActivated()) && (!this.m3DTriggerCapture) && (this.m3DOptimalLandscape == true))
      {
        LOG.W("HTCCamera", "triggerRecord() - end, 3D portrait capture !!!");
        int i = OrientationConfig.mapOrientation_Event2UI(this.mLastOrientation);
        MessageHandler.sendObtainMessage(this.mUIHandler, 80, 2131361848, i, null);
        continue;
      }
      this.m3DTriggerCapture = false;
      if ((this.mCameraThread == null) || (this.mCameraThread.getRecorderStatus()))
        break;
      LOG.I("[ANALYTIC_com.android.amaze_camera]", "[press_jogball]recording");
      if ((this.mPanel != null) && (this.mPanel.getVisibility() == 0) && ((this.mPanel.isOpened() == true) || (this.mPanel.isMoving() == true)))
        continue;
      if (!checkInternalStorage())
      {
        LOG.W("HTCCamera", "low internal storage ...");
        MessageHandler.removeMessages(this.mUIHandler, 37);
        MessageHandler.sendObtainMessage(this.mUIHandler, 37, 17040172, 0, null);
        MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 15, 200L);
        continue;
      }
      if (CameraThread.Storage_Status != 0)
      {
        if (this.mCameraThread != null)
          this.mCameraThread.showStorageToast(false);
        MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 15, 200L);
        continue;
      }
      if (isPowerWarning())
      {
        LOG.W("HTCCamera", "triggerRecord() - end, PowerWarning !!!");
        continue;
      }
      setBlockPowerWarning(true);
      LOG.W("HTCCamera", "Block Capture UI - Start Recorder");
      mBlockCaptureUI = true;
      mBlock3DSwitch = true;
      this.mIsCaptureTriggered = true;
      this.mLastSavedMediaPath = null;
      MessageHandler.sendObtainMessage(this.mCameraHandler, 10, 0, 0, null);
      this.m_EventManager.raiseEvent("TriggerRecord.Started");
      closeCaptureUI();
      ViewUtil.disableMainButton(this.mMode_icon, this.mMode_btn, this.mMode_text_ds);
      ViewUtil.disableMainButton(this.mPhoto_icon, this.mPhoto_btn);
      ViewUtil.disableMainButton(this.mEffect_icon, this.mEffect_btn);
      if (this.mSwitch_btn != null)
        ViewUtil.disableMainButton(this.mSwitchBtn_icon, this.mSwitch_btn, this.mSwitch_text_ds);
      if (this.mMainBar != null)
      {
        this.mMainBar.setVisibility(0);
        setPanelVisible(true);
      }
      MessageHandler.removeMessages(this.mUIHandler, 35);
      if (this.mFaceDetection != null)
      {
        this.mFaceDetection.stopCheckLoop();
        this.mFaceDetection.stopFaceDetection();
      }
      registerFocusSensor(false);
      MessageHandler.removeMessages(this.mUIHandler, 63);
      LOG.W("HTCCamera", "Freeze UI - Recorder");
      this.mFreezeUI = true;
      this.mFreezeOrientation = this.mLastOrientation;
    }
    while (true)
    {
      LOG.V("HTCCamera", "triggerRecord() - end");
      break;
      LOG.I("[ANALYTIC_com.android.amaze_camera]", "[press_jogball]stop_record");
      LOG.W("HTCCamera", "Block Capture UI - Stop Recorder");
      mBlockCaptureUI = true;
      MessageHandler.sendObtainMessage(this.mCameraHandler, 10, 0, 0, null);
      stopRecordingIndicator();
      registerFocusSensor(true);
      this.m_EventManager.raiseEvent("TriggerRecord.Stopped");
    }
  }

  private void triggerTakePicture()
  {
    LOG.I("[ANALYTIC_com.android.amaze_camera]", "[press_jogball]take_picture");
    LOG.V("HTCCamera", "triggerTakePicture() - start");
    if (mBlockCaptureUI == true)
      LOG.W("HTCCamera", "triggerTakePicture() - end, UI Block !!!");
    while (true)
    {
      return;
      if ((this.mPanel == null) || (this.mPanel.getVisibility() != 0) || ((this.mPanel.isOpened() != true) && (this.mPanel.isMoving() != true)))
      {
        if ((is3DCameraActivated()) && (!this.m3DTriggerCapture) && (this.m3DOptimalLandscape == true))
        {
          LOG.W("HTCCamera", "triggerTakePicture() - end, 3D portrait capture !!!");
          int j = OrientationConfig.mapOrientation_Event2UI(this.mLastOrientation);
          MessageHandler.sendObtainMessage(this.mUIHandler, 80, 2131361848, j, null);
          continue;
        }
        this.m3DTriggerCapture = false;
        if (!checkInternalStorage())
        {
          LOG.W("HTCCamera", "low internal storage ...");
          MessageHandler.removeMessages(this.mUIHandler, 37);
          MessageHandler.sendObtainMessage(this.mUIHandler, 37, 17040172, 0, null);
          MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 15, 200L);
          resetAutoCaptureTask();
          LOG.W("HTCCamera", "low internal storage - mFocusingState = NO_FOCUSING");
          this.mFocusingState = 0;
          continue;
        }
        if (((!DisplayDevice.contactsNoStorage()) || (!isRequestName(IntentManager.RequestName.Contacts))) && (CameraThread.Storage_Status != 0))
        {
          if (this.mCameraThread != null)
            this.mCameraThread.showStorageToast(false);
          MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 15, 200L);
          resetAutoCaptureTask();
          LOG.W("HTCCamera", "Storage_Status != STORAGE_OK - mFocusingState = NO_FOCUSING");
          this.mFocusingState = 0;
          continue;
        }
        if (isPowerWarning())
        {
          LOG.W("HTCCamera", "triggerTakePicture() - end, PowerWarning !!!");
          continue;
        }
        setBlockPowerWarning(true);
        LOG.W("HTCCamera", "Block Capture UI - Take Picture");
        mBlockCaptureUI = true;
        mBlock3DSwitch = true;
        this.mIsCaptureTriggered = true;
        this.mLastSavedMediaPath = null;
        int i = HTCCameraAdvanceSetting.getSelfTimer(this);
        if ((this.mFocusingState == 2) || (i <= 0))
        {
          MessageHandler.removeMessages(this.mUIHandler, 35);
          if (this.mFaceDetection != null)
          {
            this.mFaceDetection.stopCheckLoop();
            this.mFaceDetection.stopFaceDetection();
          }
        }
        if (this.mFocusingState != 3)
        {
          if (this.mivGrid != null)
            this.mivGrid.setVisibility(4);
          closeCaptureUI();
        }
        if (i <= 0)
          break label476;
        if ((this.mFocusingState == 2) || (this.mFocusingState == 3))
          break;
        if (this.mMainBar != null)
          this.mMainBar.setVisibility(0);
        setPanelVisible(true);
        enableMainBarItems(false);
        showMainBarItems(false, true);
      }
    }
    while (true)
    {
      MessageHandler.sendObtainMessage(this.mCameraHandler, 3, 0, 0, null);
      LOG.V("HTCCamera", "triggerTakePicture() - end");
      break;
      break;
      label476: if (this.mFocusingState == 2)
        continue;
      if ((this.mFocusingState == 3) && (this.mMainBar != null))
        this.mMainBar.setVisibility(4);
      if (DisplayDevice.isDoubleShot())
        this.mCapture_combine_ds.setVisibility(4);
      MessageHandler.removeMessages(this.mUIHandler, 63);
      LOG.W("HTCCamera", "Freeze UI - Take Picture");
      lockOrientation();
    }
  }

  private void updateCaptureIcon(int paramInt)
  {
    if (!DisplayDevice.isDoubleShot())
      if (paramInt == 0)
      {
        this.mCapture_press = ViewUtil.getCustomDrawable(this, 2131361792, 2130837830);
        this.mCapture_rest = 2130837831;
        if (this.mCapture_icon != null)
          this.mCapture_icon.setImageResource(this.mCapture_rest);
      }
    while (true)
    {
      return;
      this.mCapture_press = getResources().getDrawable(2130837798);
      this.mCapture_rest = 2130837799;
      break;
      if (this.mCapture_btn_ds == null)
        continue;
      Resources localResources = getResources();
      ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mCapture_btn_ds.getLayoutParams();
      updateDOTCaptureIcon(false, paramInt);
      if (paramInt == 0)
      {
        localMarginLayoutParams.setMargins(localResources.getDimensionPixelSize(2131427504), localResources.getDimensionPixelSize(2131427505), 0, 0);
        this.mCapture_btn_ds.setLayoutParams(localMarginLayoutParams);
        continue;
      }
      localMarginLayoutParams.setMargins(localResources.getDimensionPixelSize(2131427506), localResources.getDimensionPixelSize(2131427507), 0, 0);
      this.mCapture_btn_ds.setLayoutParams(localMarginLayoutParams);
    }
  }

  private void updateFocusView(int paramInt1, int paramInt2)
  {
    LOG.V("HTCCamera", "updateFocusView - start");
    initFocusView();
    mFocus_Screen_X = paramInt1;
    mFocus_Screen_Y = paramInt2;
    this.mCanShowFocusView = true;
    int i;
    int j;
    int k;
    if (this.Display_Orientation == 1)
    {
      i = DisplayDevice.SCREEN_WIDTH;
      j = DisplayDevice.SCREEN_HEIGHT;
      k = DisplayDevice.getFocusWidth(this);
    }
    for (int m = DisplayDevice.getFocusHeight(this); ; m = DisplayDevice.getFocusWidth(this))
    {
      if (!DisplayDevice.isDoubleShot())
      {
        int n = paramInt1 - k / 2;
        int i1 = paramInt2 - m / 2;
        int i2 = i - paramInt1 - k / 2;
        int i3 = j - paramInt2 - m / 2;
        RelativeLayout.LayoutParams localLayoutParams1 = (RelativeLayout.LayoutParams)this.mivFocusingView.getLayoutParams();
        localLayoutParams1.addRule(9, -1);
        localLayoutParams1.addRule(10, -1);
        localLayoutParams1.addRule(11, 0);
        localLayoutParams1.addRule(12, 0);
        localLayoutParams1.addRule(13, 0);
        localLayoutParams1.setMargins(n, i1, i2, i3);
        this.mivFocusingView.setLayoutParams(localLayoutParams1);
        RelativeLayout.LayoutParams localLayoutParams2 = (RelativeLayout.LayoutParams)this.mivFocusedView.getLayoutParams();
        localLayoutParams2.addRule(9, -1);
        localLayoutParams2.addRule(10, -1);
        localLayoutParams2.addRule(11, 0);
        localLayoutParams2.addRule(12, 0);
        localLayoutParams2.addRule(13, 0);
        localLayoutParams2.setMargins(n, i1, i2, i3);
        this.mivFocusedView.setLayoutParams(localLayoutParams2);
        LOG.V("HTCCamera", "updateFocusView - end");
      }
      return;
      i = DisplayDevice.SCREEN_HEIGHT;
      j = DisplayDevice.SCREEN_WIDTH;
      k = DisplayDevice.getFocusHeight(this);
    }
  }

  private void updateFreeCount()
  {
    if ((DisplayDevice.contactsNoStorage() == true) && (isRequestName(IntentManager.RequestName.Contacts) == true))
      LOG.W("HTCCamera", "support no sdcard, not need to update free count");
    while (true)
    {
      return;
      if (this.mRemainingLayout == null)
        this.mRemainingLayout = ((ViewStub)this.mSecondLayout.findViewById(2131231097)).inflate();
      if (this.mRemainingText == null)
        this.mRemainingText = ((TextView)this.mRemainingLayout.findViewById(2131231047));
      if (this.mRotateLayout_Remaining == null)
        this.mRotateLayout_Remaining = ((RotateLinearLayout)this.mRemainingLayout.findViewById(2131231046));
      if ((this.mRotateLayout_Remaining != null) && (DisplayDevice.supportIconRotate()))
        this.mRotateLayout_Remaining.setOrientation(OrientationConfig.getUIOrientation());
      if (!DisplayDevice.supportIconRotate())
        this.mRotateLayout_Remaining.setOrientation(1);
      if ((CameraThread.Storage_Status != 0) && (CameraThread.Storage_Status != 3))
      {
        this.mRemainingText.setVisibility(4);
        continue;
      }
      String str = null;
      if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 0))
      {
        long l2 = this.mCameraThread.calculatePicturesRemaining();
        if (l2 >= 0L)
          str = String.valueOf(l2);
      }
      while (true)
      {
        if (str == null)
          break label310;
        this.mRemainingText.setText(str);
        this.mRemainingText.setVisibility(0);
        break;
        if (isRequestName(IntentManager.RequestName.Mms) == true)
        {
          LOG.W("HTCCamera", "Request from mms, not need to update free length");
          break;
        }
        if (isRequestName(IntentManager.RequestName.Notes) == true)
        {
          LOG.W("HTCCamera", "Request from Notes, not need to update free length");
          break;
        }
        this.mRecordLimitCheck.setByteRate(this);
        long l1 = this.mRecordLimitCheck.remainTime(false);
        if (l1 < 0L)
          continue;
        str = getTimeString(l1);
      }
      label310: this.mRemainingText.setVisibility(4);
    }
  }

  private void updateModeIcon(int paramInt)
  {
    if (this.mMode_icon == null)
      return;
    if ((!isRequestMode(IntentManager.RequestMode.Main)) && (!isRequestName(IntentManager.RequestName.Album)))
    {
      if (paramInt == 0)
      {
        if (!DisplayDevice.isDoubleShot())
          if (isRequestName(IntentManager.RequestName.Contacts))
            this.mMode_icon.setImageResource(2130837715);
        while (true)
        {
          ViewUtil.disableMainButton(this.mMode_icon, this.mMode_btn, this.mMode_text_ds);
          ViewUtil.disableMainButton(this.mPhoto_icon, this.mPhoto_btn);
          break;
          if (isRequestName(IntentManager.RequestName.Square))
          {
            this.mMode_icon.setImageResource(2130837717);
            continue;
          }
          if ((isRequestName(IntentManager.RequestName.Mms)) || (isRequestName(IntentManager.RequestName.Mail)) || (isRequestName(IntentManager.RequestName.FriendStream)))
          {
            this.mMode_icon.setImageResource(2130837713);
            continue;
          }
          this.mMode_icon.setImageResource(2130837718);
          continue;
          this.mMode_icon.setImageResource(2130837575);
          this.mMode_text_ds.setText(2131362126);
        }
      }
      if (!DisplayDevice.isDoubleShot())
        this.mMode_icon.setImageResource(2130837719);
      while (true)
      {
        ViewUtil.disableMainButton(this.mMode_icon, this.mMode_btn, this.mMode_text_ds);
        break;
        this.mMode_icon.setImageResource(2130837613);
        this.mMode_text_ds.setText(2131362127);
      }
    }
    if (paramInt == 0)
    {
      if (!DisplayDevice.isDoubleShot())
        this.mMode_icon.setImageResource(2130837714);
      while (true)
      {
        ViewUtil.enableMainButton(this.mMode_icon, this.mMode_btn, this.mMode_text_ds);
        ViewUtil.enableMainButton(this.mPhoto_icon, this.mPhoto_btn);
        break;
        this.mMode_icon.setImageResource(2130837575);
        this.mMode_text_ds.setText(2131362126);
      }
    }
    if (!DisplayDevice.isDoubleShot())
      this.mMode_icon.setImageResource(2130837720);
    while (true)
    {
      ViewUtil.enableMainButton(this.mMode_icon, this.mMode_btn, this.mMode_text_ds);
      break;
      this.mMode_icon.setImageResource(2130837613);
      this.mMode_text_ds.setText(2131362127);
    }
  }

  private void updateRecordingTimerOrientation()
  {
    updateRecordingTimerOrientation(OrientationConfig.getUIOrientation());
  }

  private void updateRecordingTimerOrientation(int paramInt)
  {
    Resources localResources;
    ViewGroup.LayoutParams localLayoutParams;
    if ((!DisplayDevice.isDoubleShot()) && (this.mRotateLayout_Recording != null))
    {
      localResources = getResources();
      localLayoutParams = this.mRotateLayout_Recording.getLayoutParams();
      if ((localLayoutParams instanceof RelativeLayout.LayoutParams))
      {
        if ((paramInt != 0) && (paramInt != 2))
          break label90;
        ((RelativeLayout.LayoutParams)localLayoutParams).setMargins(0, 0, 0, localResources.getDimensionPixelSize(2131427461));
        localLayoutParams.width = localResources.getDimensionPixelSize(2131427457);
      }
    }
    for (localLayoutParams.height = localResources.getDimensionPixelSize(2131427459); ; localLayoutParams.height = localResources.getDimensionPixelSize(2131427460))
    {
      this.mRotateLayout_Recording.setLayoutParams(localLayoutParams);
      return;
      label90: ((RelativeLayout.LayoutParams)localLayoutParams).setMargins(0, 0, 0, localResources.getDimensionPixelSize(2131427462));
      localLayoutParams.width = localResources.getDimensionPixelSize(2131427458);
    }
  }

  private void updateRotateViews(int paramInt)
  {
    if ((this.mToast != null) && (this.mToast.isShowing()))
    {
      this.mToast.cancel();
      this.mToast.updateOrientation(paramInt);
      this.mToast.show();
    }
    if (this.mZoomBarIn != null)
      this.mZoomBarIn.invalidate();
    if (this.mZoomBarOut != null)
      this.mZoomBarOut.invalidate();
  }

  private void updateSceneGuide(int paramInt)
  {
    hideAllScene();
    switch (paramInt)
    {
    case 1:
    case 2:
    case 3:
    case 4:
    default:
      MessageHandler.removeMessages(this.mCameraHandler, 58);
      switch (paramInt)
      {
      default:
      case 2:
      case 3:
      case 4:
      case 1:
      }
    case 0:
    case 5:
    case 6:
    case 7:
    case 8:
    case 9:
    }
    while (true)
    {
      return;
      String str = "auto";
      enableFlash();
      while (true)
      {
        updateSceneGuideOrientation(OrientationConfig.getUIOrientation());
        MessageHandler.removeMessages(this.mCameraHandler, 58);
        MessageHandler.sendObtainMessage(this.mCameraHandler, 58, 0, 0, str);
        break;
        str = "night";
        disableFlash();
        showSunNightSceneGuide();
        continue;
        str = "action";
        enableFlash();
        continue;
        str = "flowers";
        enableFlash();
        continue;
        str = "portrait";
        enableFlash();
        continue;
        str = "auto";
        enableFlash();
      }
      if (((PanoramaUI)getComponent("Panorama UI")).isPanoramaModeEntered())
        continue;
      MessageHandler.sendObtainMessage(this.mCameraHandler, 58, 0, 0, "auto");
      enterPanoramaMode();
      continue;
      if (!((HdrUI)getComponent("HDR UI")).isHdrModeEntered())
      {
        MessageHandler.sendObtainMessage(this.mCameraHandler, 58, 0, 0, "auto");
        enterHdrMode();
      }
      showHDRSceneGuide();
      updateSceneGuideOrientation(OrientationConfig.getUIOrientation());
      continue;
      if (((BurstUI)getComponent("Burst UI")).isBurstModeEntered())
        continue;
      MessageHandler.sendObtainMessage(this.mCameraHandler, 58, 0, 0, "action");
      enterBurstMode();
      continue;
      if (((HappyShotUI)getComponent("Happy Shot UI")).isHappyShotModeEntered())
        continue;
      MessageHandler.sendObtainMessage(this.mCameraHandler, 58, 0, 0, "action");
      enterHappyShotMode();
    }
  }

  private void updateSceneGuideOrientation(int paramInt)
  {
    LOG.V("HTCCamera", "updateSceneGuideOrientation : " + paramInt);
    setSceneGuidePosition(paramInt, this.mScenePortraitLayout);
    setSceneGuidePosition(paramInt, this.mSceneSunNightLayout);
    setSceneGuidePosition(paramInt, this.mSceneLandscapeTipLayout);
    setSceneGuidePosition(paramInt, this.mSceneHDRLayout);
    setTipTextPosition(paramInt, this.mSceneLandscapeTipTextLayout);
    if ((this.mSceneLandscapeLayout != null) && (this.mSceneLandscapePortLayout != null) && ((this.mSceneLandscapeLayout.getVisibility() == 0) || (this.mSceneLandscapePortLayout.getVisibility() == 0)))
    {
      if ((paramInt == 0) || (paramInt == 2))
      {
        this.mSceneLandscapeLayout.setVisibility(4);
        this.mSceneLandscapePortLayout.setOrientation(paramInt);
        this.mSceneLandscapePortLayout.setVisibility(0);
      }
    }
    else
      return;
    this.mSceneLandscapePortLayout.setVisibility(4);
    if (paramInt == 1)
      this.mSceneLandscapeLayout.setOrientation(0);
    while (true)
    {
      this.mSceneLandscapeLayout.setVisibility(0);
      break;
      this.mSceneLandscapeLayout.setOrientation(2);
    }
  }

  private void updateSelfPortraitHint(int paramInt)
  {
    if ((this.mRotateLayout_SelfPortrait == null) || (this.mRotateLayout_SelfPortrait.getVisibility() != 0))
      return;
    RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)this.mSelfPortraitHint_layout.getLayoutParams();
    switch (paramInt)
    {
    default:
    case 0:
    case 2:
      while (true)
      {
        this.mSelfPortraitHint_layout.setLayoutParams(localLayoutParams);
        this.mRotateLayout_SelfPortrait.setOrientation(paramInt);
        break;
        localLayoutParams.width = -1;
        localLayoutParams.addRule(9, 0);
        localLayoutParams.addRule(11, 0);
        localLayoutParams.addRule(14, -1);
        localLayoutParams.setMargins(0, DisplayDevice.SELF_PORTRAIT_HINT_MARGIN_DEFAULT, 0, 0);
        continue;
        localLayoutParams.width = -1;
        localLayoutParams.addRule(9, 0);
        localLayoutParams.addRule(11, 0);
        localLayoutParams.addRule(14, -1);
        localLayoutParams.setMargins(0, DisplayDevice.SELF_PORTRAIT_HINT_MARGIN_MAIN_BAR, 0, 0);
      }
    case 1:
      if ((this.mSurfaceHeight == DisplayDevice.SCREEN_WIDTH) && (!hasPreviewFilterLayout()));
      for (int j = DisplayDevice.SELF_PORTRAIT_HINT_WIDTH_FULL_SCREEN; ; j = -1)
      {
        localLayoutParams.width = j;
        localLayoutParams.addRule(9, -1);
        localLayoutParams.addRule(11, 0);
        localLayoutParams.addRule(14, 0);
        localLayoutParams.setMargins(0, DisplayDevice.SELF_PORTRAIT_HINT_MARGIN_DEFAULT, 0, 0);
        break;
      }
    case 3:
    }
    if ((this.mSurfaceHeight == DisplayDevice.SCREEN_WIDTH) && (!hasPreviewFilterLayout()));
    for (int i = DisplayDevice.SELF_PORTRAIT_HINT_WIDTH_FULL_SCREEN; ; i = -1)
    {
      localLayoutParams.width = i;
      localLayoutParams.addRule(9, 0);
      localLayoutParams.addRule(11, -1);
      localLayoutParams.addRule(14, 0);
      localLayoutParams.setMargins(0, DisplayDevice.SELF_PORTRAIT_HINT_MARGIN_DEFAULT, 0, 0);
      break;
    }
  }

  private void updateSwitchIcon(int paramInt)
  {
    if (this.mSwitchBtn_icon == null)
      return;
    if (!DisplayDevice.isDoubleShot())
      if (paramInt == 0)
        this.mSwitchBtn_icon.setImageResource(2130837659);
    while (true)
    {
      ViewUtil.enableMainButton(this.mSwitchBtn_icon, this.mSwitch_btn, this.mSwitch_text_ds);
      break;
      this.mSwitchBtn_icon.setImageResource(2130837859);
      continue;
      if (HTCCameraAdvanceSetting.getPrefenceBoolean(this, "pref_camera_switch").booleanValue())
      {
        this.mSwitchBtn_icon.setImageResource(2130837584);
        this.mSwitch_text_ds.setText(2131362129);
        continue;
      }
      this.mSwitchBtn_icon.setImageResource(2130837569);
      this.mSwitch_text_ds.setText(2131362128);
    }
  }

  private void updateThumbnailButton(Bitmap paramBitmap, String paramString, int paramInt)
  {
    this.mThumbController.setData(paramBitmap, paramString, paramInt);
    this.mThumbController.updateDisplayIfNeeded();
  }

  private void updateTimerBase()
  {
    if (this.mTimerBase == null);
    while (true)
    {
      return;
      int i = DisplayDevice.getRecordingTimerBaseHeight(this, OrientationConfig.getUIOrientation());
      ViewGroup.LayoutParams localLayoutParams = this.mTimerBase.getLayoutParams();
      localLayoutParams.height = i;
      this.mTimerBase.setLayoutParams(localLayoutParams);
    }
  }

  private void updateVirtualHwKeysOrientation(int paramInt, boolean paramBoolean)
  {
    sendBroadcast(new Intent("com.htc.content.intent.action.ORIENTATION_LIGHT").putExtra("package_name", "com.android.amaze_camera").putExtra("manual", paramBoolean).putExtra("orientation", paramInt));
  }

  private void updateZoomBarVisible()
  {
    if (this.mCameraThread == null);
    while (true)
    {
      return;
      if ((this.mCameraThread.is2ndCamera()) || (this.mCameraThread.is3DMode()) || (getFaceNumber() > 0) || (this.mIsZoomLocked))
      {
        hideZoomBar(false);
        continue;
      }
      showZoomBar(false);
    }
  }

  public boolean IsFirstTimeLaunch()
  {
    int i;
    if (!HTCCameraAdvanceSetting.ContainsKey(this, "pref_first_time_launch").booleanValue())
      i = 1;
    while (true)
    {
      return i;
      if (HTCCameraAdvanceSetting.getPrefenceBoolean(this, "pref_first_time_launch").booleanValue() == true)
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  public void PlaySound(int paramInt)
  {
    LOG.W("HTCCamera", "PlaySound() - start");
    if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 0))
      getAudioManager().requestAudioFocus(this.mAudioFocusListener, 3, 2);
    if (!this.mIsUIReady)
      LOG.W("HTCCamera", "mIsUIReady = false - PlaySound return");
    while (true)
    {
      return;
      this.mAudioPlayer = new MediaPlayer();
      if (this.mAudioPlayer != null)
        break;
      LOG.E("HTCCamera", "PlaySound() - mAudioPlayer == null");
    }
    while (true)
    {
      AudioManager localAudioManager;
      try
      {
        AssetFileDescriptor localAssetFileDescriptor = getResources().openRawResourceFd(paramInt);
        LOG.W("HTCCamera", "PlaySound() - set data source");
        this.mAudioPlayer.setDataSource(localAssetFileDescriptor.getFileDescriptor(), localAssetFileDescriptor.getStartOffset(), localAssetFileDescriptor.getLength());
        if ((!DisplayDevice.forceSutterSound()) && (!isAutoCaptureTask()))
          continue;
        this.mAudioStreamType = 4;
        LOG.V("HTCCamera", "PlaySound() - StreamType : STREAM_ALARM");
        this.mAudioPlayer.setAudioStreamType(this.mAudioStreamType);
        this.mAudioPlayer.setOnCompletionListener(this.mAudioCompletionListener);
        this.mAudioPlayer.setOnErrorListener(this.mAudioErrorListener);
        this.mAudioPlayer.prepare();
        localAudioManager = getAudioManager();
        if ((localAudioManager.getRingerMode() != 2) && (!DisplayDevice.forceSutterSound()) && (!isAutoCaptureTask()))
          continue;
        LOG.V("HTCCamera", "Set stream volume");
        int i = localAudioManager.getStreamMaxVolume(this.mAudioStreamType);
        if (this.mAudioStreamType != 4)
          break label376;
        if (this.mOriginalVolume_Alarm != -1)
          continue;
        this.mOriginalVolume_Alarm = localAudioManager.getStreamVolume(this.mAudioStreamType);
        LOG.W("HTCCamera", "mOriginalVolume_Alarm = " + this.mOriginalVolume_Alarm);
        localAudioManager.setStreamVolume(this.mAudioStreamType, getAdjustedVolume(i), 0);
        this.mAudioPlayer.start();
        LOG.W("HTCCamera", "PlaySound() - end");
        break;
        this.mAudioStreamType = 2;
        LOG.V("HTCCamera", "PlaySound() - StreamType : STREAM_RING");
        continue;
      }
      catch (Exception localException)
      {
        LOG.E("HTCCamera", "PlaySound failed", localException);
        this.mAudioPlayer.release();
        this.mAudioPlayer = null;
      }
      break;
      label376: if (this.mOriginalVolume_Ring != -1)
        continue;
      this.mOriginalVolume_Ring = localAudioManager.getStreamVolume(this.mAudioStreamType);
      LOG.W("HTCCamera", "mOriginalVolume_Ring = " + this.mOriginalVolume_Ring);
    }
  }

  public int RequestVideoQualityLevel()
  {
    int i;
    if (this.mIntentManager == null)
    {
      LOG.E("HTCCamera", "isRequestHighQualityVideo() - mIntentManager == null");
      i = -1;
    }
    while (true)
    {
      return i;
      if ((this.mIntentManager.mRequestName.equals(IntentManager.RequestName.Notes)) || (this.mIntentManager.mRequestName.equals(IntentManager.RequestName.Unknown_Service)))
      {
        if (IntentManager.IsHighVideoQuality())
        {
          i = 1;
          continue;
        }
        i = 0;
        continue;
      }
      i = -1;
    }
  }

  public void activate()
  {
    LOG.W("HTCCamera", "activate!!!!");
    if ((this.mCameraThread == null) || (this.mCameraHandler == null))
      LOG.E("HTCCamera", "activate - mCameraThread = null or mCameraHandler = null");
    while (true)
    {
      return;
      if (this.mUIHandler == null)
      {
        LOG.E("HTCCamera", "activate - mUIHandler = null");
        continue;
      }
      if (!this.mIdle)
      {
        LOG.E("HTCCamera", "activate in mIdle == false");
        continue;
      }
      this.mIdle = false;
      if (this.mScreensave != null)
        this.mScreensave.setVisibility(8);
      getWindow().addFlags(128);
      LOG.W("HTCCamera", "Block Capture UI - activate!!!!");
      mBlockCaptureUI = true;
      MessageHandler.sendObtainMessage(this.mCameraHandler, 0, 1, 0, null);
      MessageHandler.removeMessages(this.mUIHandler, 36);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 36, this.SCREEN_DELAY);
      setMaxBrightness();
      registerFocusSensor(true);
    }
  }

  public boolean canSensorFocus()
  {
    int i;
    if (this.mCameraThread == null)
      i = 0;
    while (true)
    {
      return i;
      if ((getFaceNumber() <= 0) && (!HTCCameraAdvanceSetting.getPrefenceBoolean(this, "pref_camera_auto_focus").booleanValue()))
      {
        i = 0;
        continue;
      }
      if (!canTriggerFocus())
      {
        i = 0;
        continue;
      }
      if ((!this.mEnableSensorFocus) || ((this.mIsTouchScreen == true) && (getFaceNumber() == 0)))
      {
        i = 0;
        continue;
      }
      i = 1;
    }
  }

  public boolean canTriggerFocus()
  {
    int i;
    if (!DisplayDevice.hasAutoFocus())
      i = 0;
    while (true)
    {
      return i;
      if (this.mCameraThread == null)
      {
        i = 0;
        continue;
      }
      if ((DisplayDevice.supportSecondCamera() == true) && (this.mCameraThread.is2ndCamera() == true))
      {
        i = 0;
        continue;
      }
      if ((mBlockCaptureUI) || (this.mCameraThread.isCameraTakingPicture()) || ((this.mtvSelfTimer != null) && (this.mtvSelfTimer.getVisibility() == 0)) || (this.mIdle) || (bHoldFocusKey) || (isActionScreenOpen()) || ((this.mEffectPanel != null) && (this.mEffectPanel.isOpen())) || (this.mIsSipExist == true) || (!this.mIsUIReady))
      {
        i = 0;
        continue;
      }
      if ((this.mPanel != null) && (this.mPanel.getVisibility() == 0) && ((!isPanelReady()) || (this.mPanel.isOpened() == true)))
      {
        i = 0;
        continue;
      }
      i = 1;
    }
  }

  public void cancelAutoFocus()
  {
    LOG.V("HTCCamera", "cancelAutoFocus() - start");
    if (!DisplayDevice.canCancelFocus())
      LOG.W("HTCCamera", "cancelAutoFocus() return - DisplayDevice.canCancelFocus() = false");
    while (true)
    {
      return;
      if (this.mFocusingState != 1)
      {
        LOG.W("HTCCamera", "cancelAutoFocus() return - mFocusingState != FOCUSING_NO_ACTION");
        continue;
      }
      this.bCancelFocus = true;
      bFocusFromPress = false;
      resetFocusView();
      MessageHandler.removeMessages(this.mUIHandler, 28);
      MessageHandler.removeMessages(this.mUIHandler, 29);
      MessageHandler.removeMessages(this.mCameraHandler, 4);
      MessageHandler.removeMessages(this.mCameraHandler, 5);
      MessageHandler.removeMessages(this.mCameraHandler, 6);
      MessageHandler.sendEmptyMessage(this.mCameraHandler, 6);
      if (this.mSensorHandler != null)
        this.mSensorHandler.setifWaitFocus(false);
      this.mFocusingState = 0;
      if (this.mFaceDetection != null)
        this.mFaceDetection.setStateWithFocus();
      MessageHandler.removeMessages(this.mUIHandler, 54);
      MessageHandler.sendEmptyMessage(this.mUIHandler, 54);
      MessageHandler.removeMessages(this.mUIHandler, 35);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 35, 500L);
      MessageHandler.removeMessages(this.mUIHandler, 31);
      MessageHandler.removeMessages(this.mCameraHandler, 31);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 31, 500L);
      LOG.V("HTCCamera", "cancelAutoFocus() - end");
    }
  }

  public final void changeZoom(int paramInt)
  {
    threadAccessCheck();
    if (this.mIsZoomLocked)
      LOG.W("HTCCamera", "Zoom is locked");
    while (true)
    {
      return;
      if (this.mCameraThread == null)
      {
        LOG.W("HTCCamera", "There is no camera thread");
        continue;
      }
      this.mCameraThread.changeZoom(paramInt);
    }
  }

  public void changetoCameraMode()
  {
    LOG.V("HTCCamera", "Change mode to Camera");
    if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 0))
      LOG.E("HTCCamera", "Change mode to Camera - but current mode is Camera !!");
    while (true)
    {
      return;
      MessageHandler.sendObtainMessage(this.mCameraHandler, 12, 0, 0, null);
      if (this.mMenuHandler != null)
        this.mMenuHandler.exitMenuHandler();
      LOG.W("HTCCamera", "changetoCameraMode() - set mWaitResetSettings to true");
      this.mWaitResetSettings = true;
      this.mIsPreviewStarted = false;
      updateModeIcon(0);
      updateCaptureIcon(0);
      updateSwitchIcon(0);
      changeIndicatorLayout(0, true);
      if (this.mLocationHandler != null)
        this.mLocationHandler.startReceivingLocationUpdates();
      if (this.mOnScreenCameraSwitchBtnIcon != null)
        this.mOnScreenCameraSwitchBtnIcon.setImageResource(2130837659);
      getAudioManager().abandonAudioFocus(this.mAudioFocusListener);
    }
  }

  public void changetoVideoMode()
  {
    LOG.V("HTCCamera", "Change mode to Video");
    if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 1))
      LOG.E("HTCCamera", "Change mode to Video - but current mode is Video !!");
    while (true)
    {
      return;
      broadcastStopFM();
      broadcastStopMusic();
      stopNotesRecording();
      getAudioManager().requestAudioFocus(this.mAudioFocusListener, 3, 1);
      if (this.mivGrid != null)
        this.mivGrid.setVisibility(4);
      MessageHandler.sendObtainMessage(this.mCameraHandler, 11, 0, 0, null);
      if (this.mMenuHandler != null)
        this.mMenuHandler.exitMenuHandler();
      LOG.W("HTCCamera", "changetoVideoMode() - set mWaitResetSettings to true");
      this.mWaitResetSettings = true;
      this.mIsPreviewStarted = false;
      updateModeIcon(1);
      updateCaptureIcon(1);
      updateSwitchIcon(1);
      changeIndicatorLayout(1, true);
      if (this.mLocationHandler != null)
        this.mLocationHandler.stopReceivingLocationUpdates();
      hide_gps_indicator();
      if (!DisplayDevice.supportCamcorderRotate())
        lockOrientation(1);
      if (this.mOnScreenCameraSwitchBtnIcon == null)
        continue;
      this.mOnScreenCameraSwitchBtnIcon.setImageResource(2130837859);
    }
  }

  public void checkCamcorderButtonRecord()
  {
    if (this.m_NeedTriggerRecord)
    {
      this.m_NeedTriggerRecord = false;
      triggerRecord();
    }
  }

  public boolean checkInternalStorage()
  {
    LOG.V("HTCCamera", "Check Internal Storage");
    try
    {
      StatFs localStatFs = new StatFs("/data");
      long l = localStatFs.getAvailableBlocks() * localStatFs.getBlockSize();
      LOG.V("HTCCamera", "internal memory: " + l);
      if (l > 1048576L);
      for (i = 1; ; i = 0)
        return i;
    }
    catch (Exception localException)
    {
      while (true)
      {
        LOG.E("HTCCamera", "catch - checkInternalStorage with exception: ", localException);
        int i = 0;
      }
    }
  }

  public final void closeActionScreen()
  {
    if (Thread.currentThread() == this.mMainThread)
      if (this.mActionScreen == null)
        LOG.V("HTCCamera", "No action screen to close");
    while (true)
    {
      return;
      this.mActionScreen.close(false);
      this.mActionScreen = null;
      continue;
      if (this.mUIHandler != null)
      {
        this.mUIHandler.post(this.mCloseActionScreenRunnable);
        continue;
      }
      LOG.E("HTCCamera", "Cannot close action screen because there is no handler");
    }
  }

  public void closeAllExtensionMenus()
  {
    closeAllExtensionMenus(200);
  }

  public void closeAllExtensionMenus(int paramInt)
  {
    if (this.mMode_ExtensionMenu != null)
      this.mMode_ExtensionMenu.close(paramInt);
    if (this.mFlash_ExtensionMenu != null)
      this.mFlash_ExtensionMenu.close(paramInt);
  }

  public void closeSettingsPanel()
  {
    if ((this.mPanel != null) && (this.mPanel.getVisibility() == 0))
    {
      if ((this.mPanel.isOpened()) || (!this.mPanel.isMoving()))
        break label45;
      this.mPanel.close();
    }
    while (true)
    {
      return;
      label45: if ((this.mPanel.isOpened()) && (!this.mPanel.isMoving()))
      {
        this.mPanel.animateClose();
        continue;
      }
    }
  }

  public void deactivate()
  {
    LOG.W("HTCCamera", "deactivate!!!!");
    if ((this.mCameraThread == null) || (this.mCameraHandler == null))
      LOG.E("HTCCamera", "deactivate - mCameraThread = null or mCameraHandler = null");
    while (true)
    {
      return;
      if (this.mUIHandler == null)
      {
        LOG.E("HTCCamera", "deactivate - mUIHandler = null");
        continue;
      }
      if (this.mIdle != true)
        break;
      LOG.E("HTCCamera", "deactivate in mIdle = true");
    }
    this.mIdle = true;
    closeActionScreen();
    if (this.mScreensave == null)
    {
      this.mScreensave = ((ViewStub)findViewById(2131230808)).inflate();
      this.mScreensave.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          LOG.V("HTCCamera", "onClick: activate ");
          HTCCamera.this.activate();
          MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 71);
          MessageHandler.sendEmptyMessage(HTCCamera.this.mUIHandler, 71);
        }
      });
      this.mScreensaveTextContainer = ((RotateRelativeLayout)this.mScreensave.findViewById(2131231089));
      this.mScreensaveText = ((TextView)this.mScreensaveTextContainer.findViewById(2131231090));
    }
    if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 1))
      if (DisplayDevice.CAPTURE_BUTTON == DisplayDevice.CaptureButton.ActionKey)
        this.mScreensaveText.setText(2131362082);
    while (true)
    {
      this.mScreensaveTextContainer.setOrientation(OrientationConfig.getUIOrientation());
      this.mScreensave.setVisibility(0);
      getWindow().clearFlags(128);
      mFocusMode = 0;
      releaseFaceDetection();
      MessageHandler.removeMessages(this.mCameraHandler, 0);
      MessageHandler.sendEmptyMessage(this.mCameraHandler, 2);
      setOldBrightness();
      registerFocusSensor(false);
      break;
      this.mScreensaveText.setText(2131362080);
      continue;
      if (DisplayDevice.CAPTURE_BUTTON == DisplayDevice.CaptureButton.ActionKey)
      {
        this.mScreensaveText.setText(2131362081);
        continue;
      }
      this.mScreensaveText.setText(2131362079);
    }
  }

  public final void disableFlash()
  {
    if (this.mIsFlashDisabled);
    while (true)
    {
      return;
      this.mIsFlashDisabled = true;
      updateFlashMode();
    }
  }

  public void disableTouchAEC()
  {
    if (!this.mActivityOnPause)
    {
      MessageHandler.removeMessages(this.mCameraHandler, 31);
      MessageHandler.sendEmptyMessage(this.mCameraHandler, 31);
    }
  }

  public boolean dispatchKeyEvent(android.view.KeyEvent paramKeyEvent)
  {
    if (!this.mIdle)
    {
      MessageHandler.removeMessages(this.mUIHandler, 36);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 36, this.SCREEN_DELAY);
    }
    if ((this.mPanel != null) && (this.mPanel.isOpened() == true))
    {
      MessageHandler.removeMessages(this.mUIHandler, 22);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 22, 8000L);
    }
    if ((paramKeyEvent.getAction() == 1) && (paramKeyEvent.getKeyCode() == 80))
      bHoldFocusKey = false;
    return super.dispatchKeyEvent(paramKeyEvent);
  }

  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mIdle)
    {
      MessageHandler.removeMessages(this.mUIHandler, 36);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 36, this.SCREEN_DELAY);
    }
    if ((this.mPanel != null) && (this.mPanel.isOpened() == true))
    {
      MessageHandler.removeMessages(this.mUIHandler, 22);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 22, 8000L);
      if ((this.mSubMenu_layout != null) && (this.mSubMenu_layout.isOpened()))
        this.mSubMenu_layout.requestFocus();
    }
    else
    {
      if (paramMotionEvent.getAction() != 1)
        break label124;
    }
    label124: for (this.mIsTouchScreen = false; ; this.mIsTouchScreen = true)
    {
      return super.dispatchTouchEvent(paramMotionEvent);
      this.mPanel.requestFocus();
      break;
    }
  }

  public void doOnPause()
  {
    LOG.W("HTCCamera", "doOnPause() - start");
    if (this.mCurrentDialog != null)
    {
      this.mCurrentDialog.cancel();
      this.mCurrentDialog = null;
    }
    registerFocusSensor(false);
    if (this.mOnScreenCameraSwitchBtnContainer != null)
    {
      this.mOnScreenCameraSwitchBtnContainer.setVisibility(4);
      this.mOnScreenSwitch_receiver.setVisibility(4);
      this.mOnScreenCameraSwitchBtnIcon.setImageDrawable(null);
    }
    if (this.mGpuEffectContr != null)
      this.mGpuEffectContr.pause();
    if (this.mToast != null)
      this.mToast.cancel();
    if ((this.mtvSelfTimer != null) && (this.mtvSelfTimer.getVisibility() == 0))
      closeSelfTimer();
    if (this.mivGrid != null)
      this.mivGrid.setVisibility(4);
    if (this.mRemainingLayout != null)
    {
      this.mRemainingLayout.clearAnimation();
      this.mRemainingLayout.setVisibility(8);
    }
    if (this.mRotateLayout_Recording != null)
      this.mRotateLayout_Recording.setVisibility(4);
    hideTimerBase(false);
    resetFocusView();
    if (this.mPreviewDuplicate != null)
    {
      this.mPreviewDuplicate.clearAnimation();
      this.mPreviewDuplicate.setImageBitmap(null);
      this.mPreviewDuplicate.setVisibility(8);
    }
    if ((this.mCameraThread != null) && (this.mCameraThread.mOneShotBitmap != null))
    {
      this.mCameraThread.mOneShotBitmap.recycle();
      this.mCameraThread.mOneShotBitmap = null;
    }
    LOG.W("HTCCamera", "OnPause - hide UI, set mMainLayout invisible");
    if (this.mMainLayout != null)
      this.mMainLayout.setVisibility(4);
    closeCaptureUI();
    if (this.mMainBar_item != null)
      this.mMainBar_item.clearAnimation();
    if (this.mIndicatorLayout != null)
    {
      this.mIndicatorLayout.clearAnimation();
      this.mIndicatorLayout.setVisibility(4);
    }
    if (this.mRotateLayout_SwitchBtn != null)
    {
      this.mRotateLayout_SwitchBtn.clearAnimation();
      this.mRotateLayout_SwitchBtn.setVisibility(4);
    }
    if (this.mMenuHandler != null)
      this.mMenuHandler.exitMenuHandler();
    if (this.mNeed_unregisterReceiver == true);
    try
    {
      if (!isPowerWarning())
      {
        unregisterReceiver(this.mPartitonReceiver);
        unregisterReceiver(this.mSIPReceiver);
      }
      if ((DisplayDevice.support3DCamera()) && (DisplayDevice.support3DHWSwitch()))
        unregisterReceiver(this.m3DKeySwitchReceiver);
      if (this.mLocationHandler != null)
        this.mLocationHandler.stopReceivingLocationUpdates();
      if (this.mFlashRestriction != null)
      {
        this.mFlashRestriction.releaseFlashRestriction();
        this.mFlashRestriction = null;
      }
      releaseSoundPool();
      LOG.W("HTCCamera", "doOnPause() - end");
      return;
    }
    catch (Exception localException)
    {
      while (true)
        LOG.E("HTCCamera", "unregisterReceiver failed!!", localException);
    }
  }

  public void effectSwitchCamera()
  {
    if (this.mGpuEffectContr != null)
      this.mGpuEffectContr.effectSwitchCamera();
  }

  public final void enableFlash()
  {
    if (!this.mIsFlashDisabled);
    while (true)
    {
      return;
      this.mIsFlashDisabled = false;
      updateFlashMode();
    }
  }

  public void enableGeoTagging(boolean paramBoolean)
  {
    if (this.mLocationHandler == null);
    while (true)
    {
      return;
      if ((paramBoolean == true) && (this.mCameraThread != null) && (this.mCameraThread.mMode == 0))
      {
        this.mLocationHandler.startReceivingLocationUpdates();
        continue;
      }
      this.mLocationHandler.stopReceivingLocationUpdates();
      hide_gps_indicator();
    }
  }

  public final void enterBurstMode()
  {
    ((BurstUI)getComponent("Burst UI")).enterBurstMode();
  }

  public final void enterHappyShotMode()
  {
    ((HappyShotUI)getComponent("Happy Shot UI")).enterHappyShotMode();
  }

  public final void enterHdrMode()
  {
    ((HdrUI)getComponent("HDR UI")).enterHdrMode();
  }

  public final void enterPanoramaMode()
  {
    ((PanoramaUI)getComponent("Panorama UI")).enterPanoramaMode();
  }

  public final void exitBurstMode()
  {
    ((BurstUI)getComponent("Burst UI")).exitBurstMode();
  }

  public final void exitHappyShotMode()
  {
    ((HappyShotUI)getComponent("Happy Shot UI")).exitHappyShotMode();
  }

  public final void exitHdrMode()
  {
    ((HdrUI)getComponent("HDR UI")).exitHdrMode();
  }

  public final void exitPanoramaMode()
  {
    ((PanoramaUI)getComponent("Panorama UI")).exitPanoramaMode();
  }

  public final ActionScreen getActionScreen()
  {
    return this.mActionScreen;
  }

  public final AudioManager getAudioManager()
  {
    synchronized (this.mAudioSyncRoot)
    {
      if (this.mAudioManager == null)
        this.mAudioManager = ((AudioManager)getSystemService("audio"));
      return this.mAudioManager;
    }
  }

  public boolean getBackgroundDataSetting()
  {
    return this.keep_backgroundDataSetting;
  }

  public ViewGroup getCameraLayout()
  {
    if (this.mCameraLayout == null)
      this.mCameraLayout = ((RelativeLayout)findViewById(2131230761));
    return this.mCameraLayout;
  }

  public final CameraThread getCameraThread()
  {
    return this.mCameraThread;
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

  public int getFaceNumber()
  {
    if ((this.mCameraThread == null) || (this.mCameraThread.mMode == 1));
    for (int i = 0; ; i = this.mFaceNumber)
      return i;
  }

  public FlashRestriction getFlashRestriction()
  {
    return this.mFlashRestriction;
  }

  public int getFocusingState()
  {
    return this.mFocusingState;
  }

  public int getFreezeOrientation()
  {
    return this.mFreezeOrientation;
  }

  public GpuEffectController getGpuEffectContr()
  {
    return this.mGpuEffectContr;
  }

  public int getLastOrientation()
  {
    return this.mLastOrientation;
  }

  public View getMainBarLayout()
  {
    return this.mMainBar;
  }

  public Handler getMainHandler()
  {
    return this.mUIHandler;
  }

  public View getMainLayout()
  {
    return this.mMainLayout;
  }

  public String getPrefSceneType(int paramInt)
  {
    if (paramInt == 0);
    for (String str = HTCCameraAdvanceSetting.getPrefenceValue(this, "pref_camera_scene"); ; str = HTCCameraAdvanceSetting.getPrefenceValue(this, "pref_video_scene"))
      return str;
  }

  public int getPreviewSurfaceHeight()
  {
    if (this.mSurfaceView != null);
    for (int i = this.mSurfaceView.getHeight(); ; i = 0)
      return i;
  }

  public int getPreviewSurfaceWidth()
  {
    if (this.mSurfaceView != null);
    for (int i = this.mSurfaceView.getWidth(); ; i = 0)
      return i;
  }

  public RecordLimitCheck getRecordLimitCheck()
  {
    return this.mRecordLimitCheck;
  }

  public long getRecordMS()
  {
    return 1000L * this.mRecord_sec;
  }

  public IntentManager.RequestMode getRequestMode()
  {
    if (this.mIntentManager == null)
      LOG.E("HTCCamera", "getRequestMode() - mIntentManager == null");
    for (IntentManager.RequestMode localRequestMode = null; ; localRequestMode = this.mIntentManager.mRequestMode)
      return localRequestMode;
  }

  public IntentManager.RequestName getRequestName()
  {
    if (this.mIntentManager == null)
      LOG.E("HTCCamera", "getRequestName() - mIntentManager == null");
    for (IntentManager.RequestName localRequestName = null; ; localRequestName = this.mIntentManager.mRequestName)
      return localRequestName;
  }

  public int getReviewDuration()
  {
    return this.mReviewDuration;
  }

  public View getSecondLayout()
  {
    return this.mSecondLayout;
  }

  public final int getSelfTimerInterval()
  {
    String str = HTCCameraAdvanceSetting.getPrefenceValue(this, "pref_camera_self_timer");
    int i;
    if (str != null)
      if (str.equals("2s"))
        i = 2;
    while (true)
    {
      return i;
      if (str.equals("10s"))
      {
        i = 10;
        continue;
      }
      i = 0;
    }
  }

  public SurfaceHolder getSurfaceHolder()
  {
    return this.mSurfaceHolder;
  }

  public SurfaceView getSurfaceView()
  {
    return this.mSurfaceView;
  }

  public ViewGroup getUpperLayout()
  {
    return (RelativeLayout)findViewById(2131230765);
  }

  public void handleFaceFocus(FaceDetection.Face paramFace)
  {
    LOG.V("HTCCamera", "handleFaceFocus() - start");
    LOG.W("HTCCamera", "Block Capture UI - take focus start");
    mBlockCaptureUI = true;
    if (this.mFocusingState == 0)
    {
      LOG.W("HTCCamera", "take focus - mFocusingState = FOCUSING_NO_ACTION");
      this.mFocusingState = 1;
    }
    mFocusMode = 2;
    updateFocusView(paramFace.focus_X, paramFace.focus_Y);
    sendFocusMessage(paramFace.range.centerX(), paramFace.range.centerY());
    LOG.V("HTCCamera", "handleFaceFocus() - end");
  }

  public boolean handleTouchFocus(int paramInt1, int paramInt2)
  {
    LOG.V("HTCCamera", "handleTouchFocus - X: " + paramInt1 + ", Y: " + paramInt2);
    int i;
    if (this.mSurfaceView == null)
    {
      LOG.E("HTCCamera", "handleTouchFocus - mSurfaceView = null");
      i = 0;
    }
    while (true)
    {
      return i;
      Rect localRect = new Rect();
      this.mSurfaceView.getHitRect(localRect);
      LOG.V("HTCCamera", "handleTouchFocus - mSurfaceView left: " + localRect.left + ", right: " + localRect.right);
      LOG.V("HTCCamera", "handleTouchFocus - mSurfaceView top: " + localRect.top + ", bottom: " + localRect.bottom);
      if (!localRect.contains(paramInt1, paramInt2))
      {
        LOG.W("HTCCamera", "handleTouchFocus - touch point not in mSurfaceView");
        i = 0;
        continue;
      }
      LOG.W("HTCCamera", "Block Capture UI - take focus start");
      mBlockCaptureUI = true;
      MessageHandler.removeMessages(this.mUIHandler, 35);
      if (this.mFaceDetection != null)
      {
        this.mFaceDetection.stopCheckLoop();
        this.mFaceDetection.stopFaceDetection();
      }
      if (this.mFocusingState == 0)
      {
        LOG.W("HTCCamera", "take focus - mFocusingState = FOCUSING_NO_ACTION");
        this.mFocusingState = 1;
      }
      MessageHandler.removeMessages(this.mUIHandler, 37);
      if (this.mToast != null)
        this.mToast.cancel();
      mFocusMode = 1;
      updateFocusView(paramInt1, paramInt2);
      mapFocusPoint_Screen2Preview(paramInt1 - localRect.left, paramInt2 - localRect.top, localRect);
      if (this.mSensorHandler != null)
        this.mSensorHandler.setTouchThreshold(true);
      i = 1;
    }
  }

  public final boolean hasFlash()
  {
    if ((this.mCameraThread != null) && (CameraController.supportFlashLight()) && (!this.mCameraThread.is2ndCamera()));
    for (int i = 1; ; i = 0)
      return i;
  }

  public boolean hasModeExtensionMenu()
  {
    if (this.mMode_ExtensionMenu != null);
    for (int i = 1; ; i = 0)
      return i;
  }

  public boolean hasOnScreenCameraSwitchButton()
  {
    if (this.mOnScreenCameraSwitchBtnContainer != null);
    for (int i = 1; ; i = 0)
      return i;
  }

  public boolean hasPreviewFilterLayout()
  {
    if (this.mPreviewFilterLayout != null);
    for (int i = 1; ; i = 0)
      return i;
  }

  public boolean hasSpecificEffect()
  {
    if ((this.mGpuEffectContr != null) && (this.mGpuEffectContr.hasEffectControl()));
    for (int i = 1; ; i = 0)
      return i;
  }

  public boolean hasSwitchButton()
  {
    if (this.mSwitch_btn != null);
    for (int i = 1; ; i = 0)
      return i;
  }

  public void hideEffectControl()
  {
    if (this.mGpuEffectContr != null)
      this.mGpuEffectContr.hideEffectControl();
  }

  public void hideRemainingLayout()
  {
    if (this.mRemainingLayout != null)
    {
      this.mRemainingLayout.clearAnimation();
      this.mRemainingLayout.setVisibility(4);
      MessageHandler.removeMessages(this.mUIHandler, 61);
      MessageHandler.removeMessages(this.mUIHandler, 62);
    }
  }

  public void hideZoomBar(boolean paramBoolean)
  {
    MessageHandler.removeMessages(this.mUIHandler, 23);
    if ((this.mZoomLayout == null) || (this.mZoomLayout.getVisibility() != 0));
    while (true)
    {
      return;
      this.mZoomLayout.clearAnimation();
      if (paramBoolean == true)
        AnimationManager.showAlphaAnimation(this.mZoomLayout, 1.0F, 0.0F, 0, 300);
      this.mZoomLayout.setVisibility(4);
      if (this.mIndicatorLayout != null)
      {
        int j = DisplayDevice.INDICATORE_LAYOUT_MARGIN_NO_ZOOM_BAR;
        ViewGroup.MarginLayoutParams localMarginLayoutParams2 = (ViewGroup.MarginLayoutParams)this.mIndicatorLayout.getLayoutParams();
        localMarginLayoutParams2.setMargins(localMarginLayoutParams2.leftMargin, j, localMarginLayoutParams2.rightMargin, localMarginLayoutParams2.bottomMargin);
        this.mIndicatorLayout.setLayoutParams(localMarginLayoutParams2);
      }
      if (this.mRotateLayout_SwitchBtn == null)
        continue;
      int i = DisplayDevice.SWITCH_BUTTON_MARGIN_NO_ZOOM_BAR;
      ViewGroup.MarginLayoutParams localMarginLayoutParams1 = (ViewGroup.MarginLayoutParams)this.mRotateLayout_SwitchBtn.getLayoutParams();
      localMarginLayoutParams1.setMargins(localMarginLayoutParams1.leftMargin, i, localMarginLayoutParams1.rightMargin, localMarginLayoutParams1.bottomMargin);
      this.mRotateLayout_SwitchBtn.setLayoutParams(localMarginLayoutParams1);
    }
  }

  public void hide_gps_indicator()
  {
  }

  public void initFocusView()
  {
    if (DisplayDevice.isDoubleShot())
      if (this.mFocusAnimation != null)
        LOG.W("HTCCamera", "mFocusAnimation != null");
    while (true)
    {
      return;
      LinearLayout localLinearLayout = (LinearLayout)findViewById(2131230774);
      this.mFocusAnimation = new CameraFocusWidget(this);
      localLinearLayout.addView(this.mFocusAnimation, new LinearLayout.LayoutParams(-1, -1));
      continue;
      if ((this.mivFocusingView != null) && (this.mivFocusedView != null))
        continue;
      View localView = ((ViewStub)findViewById(2131230772)).inflate();
      this.mivFocusingView = ((ImageView)localView.findViewById(2131230868));
      this.mivFocusedView = ((ImageView)localView.findViewById(2131230869));
    }
  }

  public void init_swing()
  {
    int i = (int)(30.0D / this.SWING_LIMIT_RATIO);
    RelativeLayout.LayoutParams localLayoutParams1 = (RelativeLayout.LayoutParams)this.mSceneLandscapeLayout.getLayoutParams();
    localLayoutParams1.height = i;
    this.mSceneLandscapeLayout.setLayoutParams(localLayoutParams1);
    RelativeLayout.LayoutParams localLayoutParams2 = (RelativeLayout.LayoutParams)this.mSceneLandscapePortLayout.getLayoutParams();
    localLayoutParams2.width = i;
    this.mSceneLandscapePortLayout.setLayoutParams(localLayoutParams2);
    this.image_shift_ratio = (0.5F * 24 / i);
    this.mRunnable_Swing = new Runnable()
    {
      public void run()
      {
        HTCCamera.this.swing();
      }
    };
  }

  public final boolean is3DCameraActivated()
  {
    int i;
    if ((this.mCameraThread == null) || (!DisplayDevice.support3DCamera()))
      i = 0;
    while (true)
    {
      return i;
      if ((this.mCameraThread.m3DButtonStatus == 0) && (!this.mCameraThread.is2ndCamera()))
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  public final boolean isActionScreenOpen()
  {
    if ((this.mActionScreen != null) && (this.mActionScreen.isOpen()));
    for (int i = 1; ; i = 0)
      return i;
  }

  public boolean isAutoCaptureTask()
  {
    return mIsSelfPortraitTask;
  }

  public boolean isBlockCaptureUI()
  {
    return mBlockCaptureUI;
  }

  public boolean isBlockPowerWarning()
  {
    return this.mBlockPowerWarning;
  }

  public final boolean isCameraThreadRunning()
  {
    return this.mIsCameraThreadRunning;
  }

  public boolean isCancelFocus()
  {
    return this.bCancelFocus;
  }

  public boolean isCaptureTriggered()
  {
    return this.mIsCaptureTriggered;
  }

  public final boolean isCaptureUIOpen()
  {
    return this.mIsCaptureUIOpen;
  }

  public final boolean isEffectPanelOpen()
  {
    if (this.mEffectPanel == null);
    for (boolean bool = false; ; bool = this.mEffectPanel.isOpen())
      return bool;
  }

  public final boolean isFlashEnabled()
  {
    if ((hasFlash()) && ((this.mFlashRestriction == null) || (!this.mFlashRestriction.isDisableFlash())) && (!this.mIsFlashlightOffByScene) && (!this.mIsFlashDisabled));
    for (int i = 1; ; i = 0)
      return i;
  }

  public final boolean isMenuClosed()
  {
    if ((!isMenuOpen()) && (!isMenuMoving()));
    for (int i = 1; ; i = 0)
      return i;
  }

  public final boolean isMenuMoving()
  {
    if ((this.mPanel != null) && (this.mPanel.isMoving()));
    for (int i = 1; ; i = 0)
      return i;
  }

  public final boolean isMenuOpen()
  {
    if ((this.mPanel != null) && (this.mPanel.isOpened()));
    for (int i = 1; ; i = 0)
      return i;
  }

  public final boolean isOrientationLockNeeded()
  {
    int i;
    if (!DisplayDevice.supportIconRotate())
      i = 1;
    while (true)
    {
      return i;
      PanoramaUI localPanoramaUI = (PanoramaUI)getComponent("Panorama UI");
      if ((localPanoramaUI != null) && (localPanoramaUI.isPanoramaModeEntered()) && (!PanoramaController.isNonLandscapeSupported()))
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  public boolean isPanelReady()
  {
    int i;
    if (this.mPanel == null)
      i = 0;
    while (true)
    {
      return i;
      if (!this.mPanel.isMoving())
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  public boolean isPanelVisible()
  {
    int i;
    if (this.mPanel == null)
      i = 0;
    while (true)
    {
      return i;
      if (this.mPanel.getVisibility() == 0)
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  public final boolean isPaused()
  {
    return this.mActivityOnPause;
  }

  public boolean isPowerWarning()
  {
    return this.mIsPowerWarning;
  }

  public final boolean isPreviewStarted()
  {
    return this.mIsPreviewStarted;
  }

  public boolean isRequestMode(IntentManager.RequestMode paramRequestMode)
  {
    int i;
    if (this.mIntentManager == null)
    {
      LOG.E("HTCCamera", "isRequestMode() - mIntentManager == null");
      i = 0;
    }
    while (true)
    {
      return i;
      if (this.mIntentManager.mRequestMode == paramRequestMode)
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  public boolean isRequestName(IntentManager.RequestName paramRequestName)
  {
    int i;
    if (this.mIntentManager == null)
    {
      LOG.E("HTCCamera", "isRequestName() - mIntentManager == null");
      i = 0;
    }
    while (true)
    {
      return i;
      if (this.mIntentManager.mRequestName == paramRequestName)
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  public final boolean isRequestSquare()
  {
    if ((isRequestName(IntentManager.RequestName.Contacts)) || (isRequestName(IntentManager.RequestName.Square)));
    for (int i = 1; ; i = 0)
      return i;
  }

  public final boolean isSelfTimerOn()
  {
    if (getSelfTimerInterval() > 0);
    for (int i = 1; ; i = 0)
      return i;
  }

  public final boolean isServiceMode()
  {
    if ((!isRequestMode(IntentManager.RequestMode.Main)) && (!isRequestName(IntentManager.RequestName.Album)));
    for (int i = 1; ; i = 0)
      return i;
  }

  public boolean isTurnOnTorch()
  {
    if (mIsKeyguardShow);
    for (boolean bool = false; ; bool = mTurnOnTorch_Camcorder)
      return bool;
  }

  public boolean isUIFreeze()
  {
    return this.mFreezeUI;
  }

  public boolean isUIReady()
  {
    return this.mIsUIReady;
  }

  public final boolean isUnknownServiceMode()
  {
    return isRequestName(IntentManager.RequestName.Unknown_Service);
  }

  public final boolean isZoomLockNeeded()
  {
    int i;
    if (this.mCameraThread == null)
      i = 1;
    while (true)
    {
      return i;
      if ((this.mCameraThread.is2ndCamera()) || (is3DCameraActivated()))
      {
        i = 1;
        continue;
      }
      ConditionalQueryEvent localConditionalQueryEvent = new ConditionalQueryEvent("Query.IsZoomLockNeeded", 1, true);
      this.m_EventManager.raiseEvent(localConditionalQueryEvent);
      if ((localConditionalQueryEvent.hasResult()) && (((Boolean)localConditionalQueryEvent.getResult()).booleanValue()))
      {
        i = 1;
        continue;
      }
      i = 0;
    }
  }

  public final boolean isZoomLocked()
  {
    return this.mIsZoomLocked;
  }

  public final void lockOrientation()
  {
    if (is3DCameraActivated())
      lockOrientation(1);
    while (true)
    {
      return;
      this.mFreezeUI = true;
      this.mFreezeOrientation = this.mLastOrientation;
    }
  }

  public final void lockOrientation(int paramInt)
  {
    if ((!DisplayDevice.supportIconRotate()) || (is3DCameraActivated()))
      paramInt = 1;
    if (paramInt == -1)
    {
      LOG.W("HTCCamera", "unlock UI orientation");
      this.mFreezeUI = false;
    }
    while (true)
    {
      return;
      LOG.W("HTCCamera", "lock UI orientation in " + paramInt);
      onRotateChanged(paramInt);
      MessageHandler.removeMessages(this.mUIHandler, 63);
      this.mFreezeUI = true;
      this.mFreezeOrientation = -1;
    }
  }

  public final void lockZoom()
  {
    LOG.V("HTCCamera", "lockZoom() - start");
    threadAccessCheck();
    if (this.mIsZoomLocked)
    {
      LOG.W("HTCCamera", "Zoom is already be locked");
      return;
    }
    this.mIsZoomLocked = true;
    if (this.mCameraThread != null)
      if (this.mCameraThread.isZoomRangeRetrieved())
        this.mCameraThread.changeZoom(this.mCameraThread.getMinimumZoom());
    while (true)
    {
      if (!DisplayDevice.isDoubleShot())
        updateZoomBarVisible();
      this.m_EventManager.raiseEvent(new OneValueEvent("Zoom.LockedStateChanged", Boolean.valueOf(true)));
      LOG.V("HTCCamera", "lockZoom() - end");
      break;
      LOG.W("HTCCamera", "There is no camera thread");
    }
  }

  public int mapSceneNumFromSelector(int paramInt)
  {
    if (isServiceMode())
      switch (paramInt)
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      }
    while (true)
    {
      return paramInt;
      paramInt = 0;
      continue;
      paramInt = 3;
      continue;
      paramInt = 5;
      continue;
      paramInt = 6;
      continue;
      paramInt = 7;
      continue;
      paramInt = 8;
      continue;
      paramInt = 9;
    }
  }

  public int mapSceneNumToSelector(int paramInt)
  {
    if (isServiceMode())
      switch (paramInt)
      {
      case 1:
      case 2:
      case 4:
      default:
      case 0:
      case 3:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      }
    while (true)
    {
      return paramInt;
      paramInt = 0;
      continue;
      paramInt = 1;
      continue;
      paramInt = 2;
      continue;
      paramInt = 3;
      continue;
      paramInt = 4;
      continue;
      paramInt = 5;
      continue;
      paramInt = 6;
    }
  }

  public final boolean needsActionScreen()
  {
    this.mReviewDuration = -1;
    int i;
    if (isPowerWarning())
      i = 0;
    while (true)
    {
      return i;
      if (isServiceMode())
      {
        i = 1;
        continue;
      }
      if (mIsSelfPortraitTask)
      {
        i = 1;
        continue;
      }
      String str = HTCCameraAdvanceSetting.getPrefenceValue(this, "pref_camera_review_duration");
      if (str.equals("2s"))
      {
        LOG.V("HTCCamera", "advanced settings - Review is 2 seconds");
        this.mReviewDuration = 2;
        i = 1;
        continue;
      }
      if (str.equals("3s"))
      {
        LOG.V("HTCCamera", "advanced settings - Review is 3 seconds");
        this.mReviewDuration = 3;
        i = 1;
        continue;
      }
      if (str.equals("5s"))
      {
        LOG.V("HTCCamera", "advanced settings - Review is 5 seconds");
        this.mReviewDuration = 5;
        i = 1;
        continue;
      }
      if (str.equals("10s"))
      {
        LOG.V("HTCCamera", "advanced settings - Review is 10 seconds");
        this.mReviewDuration = 10;
        i = 1;
        continue;
      }
      if (str.equals("no_limit"))
      {
        LOG.V("HTCCamera", "advanced settings - Review is no limit");
        i = 1;
        continue;
      }
      LOG.V("HTCCamera", "advanced settings - Review is no review");
      i = 0;
    }
  }

  public boolean needsReview()
  {
    this.mReviewDuration = -1;
    int i;
    if (isServiceMode())
    {
      LOG.V("HTCCamera", "not requested from album - Review is off");
      i = 0;
    }
    while (true)
    {
      return i;
      if (mIsSelfPortraitTask)
      {
        LOG.V("HTCCamera", "self portrait - Review is no limit");
        i = 1;
        continue;
      }
      String str = HTCCameraAdvanceSetting.getPrefenceValue(this, "pref_camera_review_duration");
      if (str.equals("2s"))
      {
        LOG.V("HTCCamera", "advanced settings - Review is 2 seconds");
        this.mReviewDuration = 2;
        i = 1;
        continue;
      }
      if (str.equals("3s"))
      {
        LOG.V("HTCCamera", "advanced settings - Review is 3 seconds");
        this.mReviewDuration = 3;
        i = 1;
        continue;
      }
      if (str.equals("5s"))
      {
        LOG.V("HTCCamera", "advanced settings - Review is 5 seconds");
        this.mReviewDuration = 5;
        i = 1;
        continue;
      }
      if (str.equals("10s"))
      {
        LOG.V("HTCCamera", "advanced settings - Review is 10 seconds");
        this.mReviewDuration = 10;
        i = 1;
        continue;
      }
      if (str.equals("no_limit"))
      {
        LOG.V("HTCCamera", "advanced settings - Review is no limit");
        i = 1;
        continue;
      }
      LOG.V("HTCCamera", "advanced settings - Review is no review");
      i = 0;
    }
  }

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    LOG.W("HTCCamera", "onActivityResult: " + paramInt2);
    if (paramInt2 != -1);
    int i;
    do
    {
      while (true)
      {
        return;
        switch (paramInt1)
        {
        default:
          break;
        case 0:
          openCaptureUI();
          break;
        case 1:
          setResult(-1, paramIntent);
          finish();
        case 2:
        }
      }
      i = paramIntent.getIntExtra("selected_scene", 0);
    }
    while ((i == 1) || (i == 4) || ((this.m3DButtonStatus == 0) && ((i == 2) || (i == 8))));
    LOG.V("HTCCamera", "Get scene_num from SceneSelector: " + i);
    int j = mapSceneNumFromSelector(i);
    if (!isServiceMode())
      HTCCameraAdvanceSetting.writePreference(this, "pref_camera_scene_ds", Integer.valueOf(j));
    while (true)
    {
      doAfterSceneSelect(j);
      break;
      HTCCameraAdvanceSetting.writePreference(this, "pref_camera_scene_service_ds", Integer.valueOf(j));
    }
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (paramConfiguration.orientation == 2)
    {
      LOG.W("HTCCamera", "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% onConfigurationChanged LANDSCAPE");
      this.Display_Orientation = 1;
    }
    while (true)
    {
      super.onConfigurationChanged(paramConfiguration);
      return;
      if (paramConfiguration.orientation == 1)
      {
        LOG.W("HTCCamera", "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% onConfigurationChanged PORTRAIT");
        this.Display_Orientation = 0;
        continue;
      }
      LOG.E("HTCCamera", "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% onConfigurationChanged Other");
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    this.mMainThread = Thread.currentThread();
    LOG.I("[ANALYTIC_com.android.amaze_camera]", "[press_widget]launch");
    LOG.W("HTCCamera", "onCreate()");
    super.onCreate(paramBundle);
    VMRuntime.getRuntime().setMinimumHeapSize(8388608L);
    requestWindowFeature(2);
    setContentView(2130903049);
    this.mSurfaceView = ((SurfaceView)findViewById(2131230762));
    SurfaceHolder localSurfaceHolder = this.mSurfaceView.getHolder();
    localSurfaceHolder.addCallback(this);
    localSurfaceHolder.setType(3);
    this.mUIHandler = new MainHandler(null);
    this.mCameraThread = new CameraThread(this, this.mUIHandler);
    this.mIntentManager = new IntentManager(this, this.mCameraThread);
    this.mIntentManager.initManager(getIntent());
    this.mCameraThread.start();
    IntentFilter localIntentFilter;
    if (DisplayDevice.support3DCamera())
    {
      if ((isRequestName(IntentManager.RequestName.Contacts) == true) || (isRequestName(IntentManager.RequestName.Square) == true) || (isRequestName(IntentManager.RequestName.Mms) == true) || (isRequestName(IntentManager.RequestName.FriendStream) == true) || (isRequestName(IntentManager.RequestName.Unknown_Service) == true))
        this.mForce2DMode = true;
      if (!DisplayDevice.support3DHWSwitch())
        break label983;
      if (!this.mForce2DMode)
        break label941;
      this.m3DButtonStatus = 1;
      this.mCameraThread.m3DButtonStatus = this.m3DButtonStatus;
      if (this.m3DButtonStatus != 1)
        break label955;
      HTCCameraAdvanceSetting.writePreference(this, "pref_camera_3D_status", false);
      LOG.V("HTCCamera", "Initial m3DButtonStatus = " + this.m3DButtonStatus);
      localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("com.htc.intent.action.CAM_SWITCH_CHANGE");
      localIntentFilter.setPriority(1001);
    }
    while (true)
    {
      try
      {
        registerReceiver(this.m3DKeySwitchReceiver, localIntentFilter);
        this.m3DStatusInitialized = true;
        HTCCameraAdvanceSetting.writePreference(this, "pref_camera_switch", false);
        HTCCameraAdvanceSetting.writePreference(this, "pref_front_camera_mode", true);
        HTCCameraAdvanceSetting.writePreference(this, "pref_camera_effect", "none");
        HTCCameraAdvanceSetting.writePreference(this, "pref_video_effect", "none");
        if (DisplayDevice.isDoubleShot() != true)
          continue;
        HTCCameraAdvanceSetting.writePreference(this, "pref_camera_effect_manual", "none");
        HTCCameraAdvanceSetting.writePreference(this, "pref_camera_self_portrait", false);
        HTCCameraAdvanceSetting.writePreference(this, "pref_camera_self_timer", "none");
        HTCCameraAdvanceSetting.writePreference(this, "pref_camera_face_number", "none");
        if (!DisplayDevice.showSceneInMenu())
          continue;
        HTCCameraAdvanceSetting.writePreference(this, "pref_camera_scene", "auto");
        HTCCameraAdvanceSetting.writePreference(this, "pref_video_scene", "auto");
        resetIndicatorLayout();
        OrientationConfig.setUIOrientation(0);
        this.mInstanceOrientation = 0;
        if (DisplayDevice.supportSensor() != true)
          continue;
        this.mOrientationListener = new OrientationEventListener(this)
        {
          public void onOrientationChanged(int paramInt)
          {
            if (paramInt == -1)
              LOG.W("HTCCamera", "Unknown orientation !!!");
            while (true)
            {
              return;
              HTCCamera.access$10302(HTCCamera.this, paramInt);
              if ((!DisplayDevice.supportIconRotate()) || ((!DisplayDevice.supportCamcorderRotate()) && (HTCCamera.this.mCameraThread.mMode == 1)))
                continue;
              if (HTCCamera.this.m_EventManager.hasHandlers("DeviceOrientation.Changed"))
                HTCCamera.this.m_EventManager.raiseEvent(new OneValueEvent("DeviceOrientation.Changed", Integer.valueOf(paramInt)));
              int i = OrientationConfig.mapOrientation_Event2UI(paramInt);
              if (HTCCamera.this.is3DCameraActivated())
              {
                if ((!HTCCamera.this.m3DOptimalLandscape) && (!OrientationConfig.isEqual_UIOrientation(i)))
                {
                  HTCCamera.access$13002(HTCCamera.this, true);
                  MessageHandler.sendObtainMessage(HTCCamera.this.mUIHandler, 80, 2131361848, i, null);
                  continue;
                }
                if (OrientationConfig.isEqual_UIOrientation(i) != true)
                  continue;
                HTCCamera.access$13002(HTCCamera.this, false);
                continue;
              }
              if (HTCCamera.this.mFreezeUI == true)
                continue;
              HTCCamera.this.onRotateChanged(i);
            }
          }
        };
        HTCCameraAdvanceSetting.mIsInitZoom = true;
        mTurnOnTorch_Camcorder = false;
        this.mNeed_initOnCreate = true;
        this.mNeed_doOnCreate = true;
        getWindow().setBackgroundDrawable(null);
        this.m_EventManager = new EventManager(this);
        this.mComponentManager = new ComponentManager(this);
        if (isServiceMode())
          break label995;
        this.mComponentManager.addComponent(new CommonActionScreen(this));
        if ((isServiceMode()) || (!com.android.amaze_camera.component.ThumbnailController.isSupported()))
          continue;
        this.mComponentManager.addComponent(new ThumbnailUI(this));
        if (!HwCameraSwitchButton.isSupported(this))
          continue;
        this.mComponentManager.addComponent(new HwCameraSwitchButton(this));
        if (!PanoramaController.isSupported(this))
          continue;
        this.mComponentManager.addComponent(new PanoramaUI(this));
        if (!HdrController.isSupported(this))
          continue;
        this.mComponentManager.addComponent(new HdrUI(this));
        if (!BurstController.isSupported(this))
          continue;
        this.mComponentManager.addComponent(new BurstUI(this));
        if (!HandShakeController.isSupported())
          continue;
        this.mComponentManager.addComponent(new HandShakeUI(this));
        if (!AutoSceneController.isSupported())
          continue;
        this.mComponentManager.addComponent(new AutoSceneUI(this));
        if (!ZoomBarController.isSupported())
          continue;
        this.mComponentManager.addComponent(new ZoomBarUI(this));
        if (!DOTIndicatorController.isSupported())
          continue;
        this.mComponentManager.addComponent(new DOTIndicatorUI(this));
        this.mComponentManager.addComponent(new RemovableStorageWatcher(this));
        this.mComponentManager.addComponent(new MediaScannerWatcher(this));
        this.mComponentManager.addComponent(new BatteryWatcher(this));
        if (!PowerWarningController.isSupported())
          continue;
        this.mComponentManager.addComponent(new PowerWarningUI(this));
        if (!DisplayDevice.isDoubleShot())
          continue;
        this.mComponentManager.addComponent(new ReviewAnimation(this));
        if (!HappyShotController.isSupported(this))
          continue;
        this.mComponentManager.addComponent(new HappyShotUI(this));
        this.m_EventManager.addEventHandler("ActionScreen.Closed", this);
        this.m_EventManager.addEventHandler("RemovableStorage.Ejected", this);
        this.m_EventManager.addEventHandler("RemovableStorage.Mounted", this);
        this.m_EventManager.addEventHandler("RemovableStorage.Unmounted", this);
        MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 56, 5000L);
        if ((!DisplayDevice.isDoubleShot()) || (!IsFirstTimeLaunch()))
          continue;
        startCameraTutorial();
        return;
        label941: this.m3DButtonStatus = Camera3DSettings.get3DInitialStatus(getContentResolver());
        break;
        label955: HTCCameraAdvanceSetting.writePreference(this, "pref_camera_3D_status", true);
      }
      catch (Exception localException)
      {
        LOG.E("HTCCamera", "registerReceiver failed!!", localException);
        continue;
      }
      label983: HTCCameraAdvanceSetting.writePreference(this, "pref_camera_3D_status", false);
      continue;
      label995: this.mComponentManager.addComponent(new RequestActionScreen(this));
    }
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    return true;
  }

  public void onDestroy()
  {
    LOG.W("HTCCamera", "onDestroy() - start");
    LOG.I("[ANALYTIC_com.android.amaze_camera]", "[press_back]close");
    MessageHandler.sendObtainMessage(this.mCameraHandler, 8, 0, 0, null);
    if (this.mUIHandler != null)
      this.mUIHandler.postDelayed(new Runnable()
      {
        public void run()
        {
          HTCCamera.this.doOnDestory();
        }
      }
      , 500L);
    while (true)
    {
      MessageHandler.removeMessages(this.mUIHandler, 36);
      super.onDestroy();
      LOG.W("HTCCamera", "onDestroy() - end");
      LOG.I("[ANALYTIC_com.android.amaze_camera]", "[close_camera]complete");
      return;
      doOnDestory();
    }
  }

  public final void onEvent(Event paramEvent)
  {
    String str = paramEvent.getName();
    if (str.equals("ActionScreen.Closed"))
      onActionScreenClosed((ActionScreenEvent)paramEvent);
    while (true)
    {
      return;
      if (str.equals("Media.Saved"))
      {
        onMediaSaved((MediaEvent)paramEvent);
        continue;
      }
      if (str.equals("RemovableStorage.Ejected"))
      {
        onRemovableStorageEjected();
        continue;
      }
      if (str.equals("RemovableStorage.Mounted"))
      {
        onRemovableStorageMounted();
        continue;
      }
      if (str.equals("RemovableStorage.Unmounted"))
      {
        onRemovableStorageUnmounted();
        continue;
      }
      if (!str.equals("Zoom.Changed"))
        continue;
    }
  }

  public boolean onKeyDown(int paramInt, android.view.KeyEvent paramKeyEvent)
  {
    KeyEvent localKeyEvent = new KeyEvent("Key.Down", paramKeyEvent);
    this.m_EventManager.raiseEvent(localKeyEvent);
    boolean bool1;
    if (localKeyEvent.isHandled())
    {
      LOG.V("HTCCamera", "KeyDown event is interrupted by event listener");
      bool1 = true;
    }
    while (true)
    {
      return bool1;
      if (this.mFocusingState == 1)
        switch (paramInt)
        {
        default:
        case 80:
        case 129:
        case 27:
        case 23:
        case 66:
        case 82:
        case 4:
        case 19:
        case 20:
        case 24:
        case 25:
        }
      while (true)
      {
        if (mBlockCaptureUI != true)
          break label815;
        LOG.W("HTCCamera", "onKeyDown - UI Block !!!");
        bool1 = true;
        break;
        if (paramKeyEvent.getRepeatCount() != 3)
          continue;
        bHoldFocusKey = true;
        continue;
        if (paramKeyEvent.getRepeatCount() > 0)
        {
          bool1 = true;
          break;
        }
        if ((getFaceNumber() > 0) && (!mIsSelfPortraitTask))
          continue;
        if ((DisplayDevice.canCancelFocus()) && (this.mCameraThread != null) && (this.mCameraThread.getRecorderStatus()))
        {
          cancelAutoFocus();
          LOG.W("HTCCamera", "UnBlock Capture UI - stop recorder and cancel focus");
          mBlockCaptureUI = false;
          continue;
        }
        if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 0))
        {
          if (isServiceMode())
          {
            bool1 = true;
            break;
          }
          this.m_NeedTriggerRecord = true;
          switchMode(1);
        }
        while (true)
        {
          bool1 = true;
          break;
          LOG.W("HTCCamera", "Press Record when focusing - mFocusingState = FOCUSING_PRESS_CAPTURE");
          this.mFocusingState = 4;
          closeCaptureUI();
          if ((DisplayDevice.isDoubleShot()) || (this.mMainBar == null))
            continue;
          this.mMainBar.setVisibility(0);
          setPanelVisible(true);
        }
        if (paramKeyEvent.getRepeatCount() > 0)
        {
          bool1 = true;
          break;
        }
        if ((getFaceNumber() > 0) && (!mIsSelfPortraitTask))
        {
          MessageHandler.removeMessages(this.mUIHandler, 37);
          MessageHandler.sendObtainMessage(this.mUIHandler, 37, 2131362091, 0, null);
          continue;
        }
        if ((DisplayDevice.canCancelFocus()) && (this.mCameraThread != null) && (this.mCameraThread.getRecorderStatus()))
        {
          cancelAutoFocus();
          LOG.W("HTCCamera", "UnBlock Capture UI - stop recorder and cancel focus");
          mBlockCaptureUI = false;
          continue;
        }
        if ((is3DCameraActivated()) && (this.m3DOptimalLandscape == true))
        {
          LOG.W("HTCCamera", "onKeyDown, 3D portrait capture !!!");
          int i = OrientationConfig.mapOrientation_Event2UI(this.mLastOrientation);
          MessageHandler.sendObtainMessage(this.mUIHandler, 80, 2131361848, i, null);
          continue;
        }
        this.m3DTriggerCapture = true;
        LOG.W("HTCCamera", "Press Capture when focusing - mFocusingState = FOCUSING_PRESS_CAPTURE");
        this.mFocusingState = 3;
        if (this.mivGrid != null)
          this.mivGrid.setVisibility(4);
        closeCaptureUI();
        if (!DisplayDevice.isDoubleShot())
        {
          if ((this.mCameraThread == null) || (this.mCameraThread.mMode != 0))
            break label599;
          if ((this.mMainBar != null) && (HTCCameraAdvanceSetting.getSelfTimer(this) > 0))
          {
            this.mMainBar.setVisibility(0);
            setPanelVisible(true);
            enableMainBarItems(false);
            showMainBarItems(false, true);
          }
        }
        while (true)
        {
          bool1 = true;
          break;
          label599: if (this.mMainBar == null)
            continue;
          this.mMainBar.setVisibility(0);
          setPanelVisible(true);
        }
        if ((!DisplayDevice.canCancelFocus()) || (paramKeyEvent.getRepeatCount() >= 1) || (this.mPanel.getVisibility() != 0) || (this.mCameraThread == null) || (this.mCameraThread.getRecorderStatus()))
          continue;
        togglePanelState();
        bool1 = true;
        break;
        if ((!DisplayDevice.canCancelFocus()) || (paramKeyEvent.getRepeatCount() >= 1))
          continue;
        cancelAutoFocus();
        LOG.W("HTCCamera", "UnBlock Capture UI - press back key and cancel focus");
        mBlockCaptureUI = false;
        continue;
        if (DisplayDevice.CAPTURE_BUTTON != DisplayDevice.CaptureButton.ActionKey)
        {
          bool1 = true;
          break;
        }
        if ((this.mSecondLayout == null) || (this.mSecondLayout.getVisibility() != 0))
        {
          bool1 = true;
          break;
        }
        if ((!DisplayDevice.isDoubleShot()) && (this.mZoomLayout == null))
        {
          bool1 = true;
          break;
        }
        if (DisplayDevice.canCancelFocus() != true)
          break label800;
        LOG.W("HTCCamera", "control Zoom bar when focusing, cancel focus");
        cancelAutoFocus();
        LOG.W("HTCCamera", "UnBlock Capture UI - control Zoom bar and cancel focus");
        mBlockCaptureUI = false;
      }
      label800: LOG.W("HTCCamera", "control Zoom bar when focusing, but device can't cancel focus");
      bool1 = true;
    }
    switch (paramInt)
    {
    default:
      label815: if ((this.mPanel == null) || (!this.mPanel.isOpened()))
        break;
      if ((this.mSubMenu_layout != null) && (this.mSubMenu_layout.isOpened()))
        this.mSubMenu_layout.requestFocus();
    case 80:
    case 129:
    case 27:
    case 23:
    case 66:
    case 20:
    case 25:
    case 19:
    case 24:
    case 4:
    case 82:
    case 84:
    }
    while (true)
    {
      bool1 = true;
      break;
      if ((paramKeyEvent.getRepeatCount() == 3) && (canTriggerFocus() == true))
      {
        startFocusFromLongPressKey();
        bHoldFocusKey = true;
      }
      label1399: 
      do
      {
        while (true)
        {
          bool1 = super.onKeyDown(paramInt, paramKeyEvent);
          break;
          if (paramKeyEvent.getRepeatCount() > 0)
          {
            bool1 = true;
            break;
          }
          if (this.mIdle == true)
          {
            if ((this.mScreensave != null) && (this.mScreensave.getVisibility() == 0) && ((DisplayDevice.CAPTURE_BUTTON == DisplayDevice.CaptureButton.ActionKey) || (DisplayDevice.CAPTURE_BUTTON == DisplayDevice.CaptureButton.HWKey)))
            {
              LOG.V("HTCCamera", "screen save mode -- action key pressed so activate camera");
              activate();
            }
            LOG.W("HTCCamera", "save screen - block camcorder key");
            bool1 = true;
            break;
          }
          if ((this.mtvSelfTimer != null) && (this.mtvSelfTimer.getVisibility() == 0))
          {
            bool1 = true;
            break;
          }
          if (this.mIsSipExist == true)
          {
            LOG.I("HTCCamera", "SIP is shown, not handle center button");
            bool1 = true;
            break;
          }
          if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 0))
          {
            if (isServiceMode())
            {
              bool1 = true;
              break;
            }
            this.m_NeedTriggerRecord = true;
            switchMode(1);
          }
          while (true)
          {
            bool1 = true;
            break;
            MessageHandler.removeMessages(this.mUIHandler, 15);
            LOG.V("HTCCamera", "Press camcorder button to start video recording");
            triggerRecord();
          }
          if (paramKeyEvent.getRepeatCount() > 0)
          {
            bool1 = true;
            break;
          }
          if (this.mIdle == true)
          {
            if ((this.mScreensave != null) && (this.mScreensave.getVisibility() == 0) && ((DisplayDevice.CAPTURE_BUTTON == DisplayDevice.CaptureButton.ActionKey) || (DisplayDevice.CAPTURE_BUTTON == DisplayDevice.CaptureButton.HWKey)))
            {
              LOG.V("HTCCamera", "screen save mode -- action key pressed so activate camera");
              activate();
            }
            LOG.W("HTCCamera", "save screen - block capture key");
            bool1 = true;
            break;
          }
          if ((this.mtvSelfTimer != null) && (this.mtvSelfTimer.getVisibility() == 0))
          {
            closeSelfTimer();
            bool1 = true;
            break;
          }
          if (this.mIsSipExist == true)
          {
            LOG.I("HTCCamera", "SIP is shown, not handle center button");
            bool1 = true;
            break;
          }
          if ((this.mCameraThread == null) || (this.mCameraThread.mMode != 0))
            break label1427;
          if ((getFaceNumber() <= 0) || (mIsSelfPortraitTask))
            break label1399;
          MessageHandler.removeMessages(this.mUIHandler, 37);
          MessageHandler.sendObtainMessage(this.mUIHandler, 37, 2131362091, 0, null);
        }
        MessageHandler.removeMessages(this.mUIHandler, 15);
        LOG.V("HTCCamera", "Press center button to take picture");
        triggerTakePicture();
        while (true)
        {
          bool1 = true;
          break;
          MessageHandler.removeMessages(this.mUIHandler, 15);
          LOG.V("HTCCamera", "Press center button to start video recording");
          triggerRecord();
        }
        if ((this.mPanel != null) && (this.mPanel.isOpened()))
        {
          if ((this.mSubMenu_layout != null) && (this.mSubMenu_layout.isOpened()))
            this.mSubMenu_layout.requestFocus();
          while (true)
          {
            bool1 = true;
            break;
            this.mPanel.requestFocus();
          }
        }
        if (DisplayDevice.CAPTURE_BUTTON != DisplayDevice.CaptureButton.ActionKey)
        {
          bool1 = true;
          break;
        }
        if (is3DCameraActivated())
        {
          bool1 = true;
          break;
        }
        if (!DisplayDevice.isDoubleShot())
        {
          if (((this.mZoomLayout != null) && (this.mZoomLayout.getVisibility() == 0)) || (this.mIsZoomLocked))
            break label1590;
          showZoomBar(true);
        }
        while (true)
        {
          this.m_EventManager.raiseEvent("Zoom.Decreased");
          bool1 = true;
          break;
          MessageHandler.removeMessages(this.mUIHandler, 55);
          MessageHandler.sendObtainMessage(this.mUIHandler, 55, -1, 0, null);
        }
        if ((this.mPanel != null) && (this.mPanel.isOpened()))
        {
          if ((this.mSubMenu_layout != null) && (this.mSubMenu_layout.isOpened()))
            this.mSubMenu_layout.requestFocus();
          while (true)
          {
            bool1 = true;
            break;
            this.mPanel.requestFocus();
          }
        }
        if (DisplayDevice.CAPTURE_BUTTON != DisplayDevice.CaptureButton.ActionKey)
        {
          bool1 = true;
          break;
        }
        if (is3DCameraActivated())
        {
          bool1 = true;
          break;
        }
        if (!DisplayDevice.isDoubleShot())
        {
          if (((this.mZoomLayout != null) && (this.mZoomLayout.getVisibility() == 0)) || (this.mIsZoomLocked))
            break label1753;
          showZoomBar(true);
        }
        while (true)
        {
          this.m_EventManager.raiseEvent("Zoom.Increased");
          bool1 = true;
          break;
          MessageHandler.removeMessages(this.mUIHandler, 55);
          MessageHandler.sendObtainMessage(this.mUIHandler, 55, 1, 0, null);
        }
        if ((this.mCameraThread != null) && (this.mCameraThread.getRecorderStatus() == true))
        {
          LOG.V("HTCCamera", "Press back key to stop video recording");
          triggerRecord();
          bool1 = true;
          break;
        }
        if ((this.mEffectPanel != null) && (this.mEffectPanel.isOpen()))
        {
          this.mGpuEffectContr.openEffectMenu(false, true);
          bool1 = true;
          break;
        }
        if ((this.mMode_ExtensionMenu != null) && (this.mMode_ExtensionMenu.isOpen()))
        {
          this.mMode_ExtensionMenu.close();
          bool1 = true;
          break;
        }
        if ((this.mFlash_ExtensionMenu != null) && (this.mFlash_ExtensionMenu.isOpen()))
        {
          this.mFlash_ExtensionMenu.close();
          bool1 = true;
          break;
        }
        if ((this.mtvSelfTimer != null) && (this.mtvSelfTimer.getVisibility() == 0))
        {
          closeSelfTimer();
          bool1 = true;
          break;
        }
        if ((this.mPanel == null) || (this.mPanel.getVisibility() != 0) || (!this.mPanel.isOpened()) || (this.mPanel.isMoving()))
          continue;
        boolean bool2 = false;
        if (this.mMenuHandler != null)
          bool2 = this.mMenuHandler.closeSubMenu(true);
        if (!bool2)
          this.mPanel.animateClose();
        bool1 = true;
        break;
      }
      while ((paramKeyEvent.getRepeatCount() >= 1) || (isRequestMode(IntentManager.RequestMode.Main) != true));
      label1427: label1590: label1753: if ((this.mIntentManager != null) && (this.mIntentManager.isFromCamcorder() == true))
        LOG.W("HTCCamera", "go Back - to finish CamcoderEntry");
      while (true)
      {
        this.mIsBackQuit = true;
        break;
        LOG.W("HTCCamera", "go Back - to finish CameraEntry");
      }
      if (paramKeyEvent.getRepeatCount() > 0)
      {
        bool1 = true;
        break;
      }
      if (this.mCameraThread == null)
      {
        LOG.W("HTCCamera", "!! Menu Key block !! - mCameraThread = null");
        bool1 = true;
        break;
      }
      if ((mBlockCaptureUI) || (this.mCameraThread.getRecorderStatus()) || (this.mCameraThread.isCameraTakingPicture()) || (this.mIdle) || (isActionScreenOpen()))
      {
        LOG.W("HTCCamera", "!! Menu Key block !!");
        bool1 = true;
        break;
      }
      if (!checkInternalStorage())
      {
        LOG.W("HTCCamera", "!! Menu Key block !! - low internal storage ...");
        MessageHandler.removeMessages(this.mUIHandler, 37);
        MessageHandler.sendObtainMessage(this.mUIHandler, 37, 17040172, 0, null);
        bool1 = true;
        break;
      }
      togglePanelState();
      bool1 = true;
      break;
      bool1 = true;
      break;
      this.mPanel.requestFocus();
    }
  }

  public boolean onKeyUp(int paramInt, android.view.KeyEvent paramKeyEvent)
  {
    KeyEvent localKeyEvent = new KeyEvent("Key.Up", paramKeyEvent);
    this.m_EventManager.raiseEvent(localKeyEvent);
    boolean bool;
    if (localKeyEvent.isHandled())
    {
      LOG.V("HTCCamera", "KeyUp event is interrupted by event listener");
      bool = true;
    }
    while (true)
    {
      return bool;
      if ((paramInt == 4) && (paramKeyEvent.getRepeatCount() < 1))
      {
        if ((this.mIsBackQuit) && (!paramKeyEvent.isCanceled()) && (this.mSurfaceView != null))
          this.mSurfaceView.setVisibility(4);
        this.mIsBackQuit = false;
        bool = super.onKeyUp(paramInt, paramKeyEvent);
        continue;
      }
      this.mIsBackQuit = false;
      if (mBlockCaptureUI == true)
      {
        LOG.W("HTCCamera", "onKeyUp - UI Block !!!");
        bool = true;
        continue;
      }
      switch (paramInt)
      {
      default:
        bool = super.onKeyUp(paramInt, paramKeyEvent);
        break;
      case 24:
      case 25:
        bool = true;
        break;
      case 82:
        bool = true;
      }
    }
  }

  public void onMediaSaved(MediaEvent paramMediaEvent)
  {
    LOG.V("HTCCamera", "onMediaSaved() - start");
    this.mLastSavedMediaPath = paramMediaEvent.getFilePath();
    if (((paramMediaEvent instanceof PictureSavedEvent)) && (this.mActionScreen == null) && (!isServiceMode()) && (((PictureSavedEvent)paramMediaEvent).isLastPicture()))
    {
      if (this.mCameraThread == null)
        break label76;
      this.mCameraThread.startScaladoPostProcessing(this.mLastSavedMediaPath);
    }
    while (true)
    {
      LOG.V("HTCCamera", "onMediaSaved() - end");
      return;
      label76: LOG.E("HTCCamera", "Cannot start Scalado post-processing because there is no camera thread");
    }
  }

  protected void onNewIntent(Intent paramIntent)
  {
    LOG.W("HTCCamera", "onNewIntent: " + paramIntent.getComponent().getClassName());
    super.onNewIntent(paramIntent);
    if (this.mNeed_doOnCreate == true)
      LOG.W("HTCCamera", "need to do create, not to initate mode onNewIntent!!");
    while (true)
    {
      return;
      this.mIntentManager.initManager(paramIntent);
      mTurnOnTorch_Camcorder = false;
      HTCCameraAdvanceSetting.mIsInitZoom = true;
      HTCCameraAdvanceSetting.writePreference(this, "pref_camera_switch", false);
      HTCCameraAdvanceSetting.writePreference(this, "pref_camera_effect", "none");
      HTCCameraAdvanceSetting.writePreference(this, "pref_video_effect", "none");
      if (DisplayDevice.isDoubleShot() == true)
        HTCCameraAdvanceSetting.writePreference(this, "pref_camera_effect_manual", "none");
      HTCCameraAdvanceSetting.writePreference(this, "pref_front_camera_mode", true);
      HTCCameraAdvanceSetting.writePreference(this, "pref_camera_self_portrait", false);
      HTCCameraAdvanceSetting.writePreference(this, "pref_camera_self_timer", "none");
      HTCCameraAdvanceSetting.writePreference(this, "pref_camera_face_number", "none");
      if (DisplayDevice.showSceneInMenu())
      {
        HTCCameraAdvanceSetting.writePreference(this, "pref_camera_scene", "auto");
        HTCCameraAdvanceSetting.writePreference(this, "pref_video_scene", "auto");
      }
      resetIndicatorLayout();
      MessageHandler.sendUniqueEmptyMessage(this.mUIHandler, 12);
      MessageHandler.sendUniqueEmptyMessage(this.mUIHandler, 11);
      this.m_EventManager.raiseEvent("CameraActivity.NewIntent");
      LOG.W("HTCCamera", "onNewIntent: " + paramIntent.getComponent().getClassName());
    }
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    return true;
  }

  public void onOptionsMenuClosed(Menu paramMenu)
  {
  }

  protected void onPause()
  {
    LOG.W("HTCCamera", "onPause() - start");
    this.mActivityOnPause = true;
    this.mIsUIReady = false;
    this.m3DOptimalLandscape = false;
    this.m3DStatusInitialized = false;
    this.m3DTriggerCapture = false;
    this.mIsCaptureTriggered = false;
    this.mIsFlashDisabled = false;
    this.mIsPreviewStarted = false;
    this.m_NeedTriggerRecord = false;
    LOG.W("HTCCamera", "onPause() - mIsUIReady = false");
    this.m_EventManager.raiseEvent("CameraActivity.Pausing");
    if (this.mSurfaceView != null)
      this.mSurfaceView.setVisibility(4);
    getWindow().clearFlags(128);
    this.mInstanceOrientation = OrientationConfig.getUIOrientation();
    MessageHandler.removeMessages(this.mUIHandler, 45);
    MessageHandler.removeMessages(this.mUIHandler, 46);
    MessageHandler.removeMessages(this.mUIHandler, 47);
    if (this.mUIHandler != null)
      this.mUIHandler.removeCallbacks(this.mPrepareActionScreenRunnable);
    setOldBrightness();
    restoreVolume();
    MessageHandler.removeMessages(this.mUIHandler, 3);
    MessageHandler.removeMessages(this.mUIHandler, 2);
    MessageHandler.removeMessages(this.mUIHandler, 4);
    MessageHandler.removeMessages(this.mUIHandler, 65);
    MessageHandler.removeMessages(this.mUIHandler, 30);
    MessageHandler.removeMessages(this.mUIHandler, 44);
    MessageHandler.removeMessages(this.mUIHandler, 37);
    MessageHandler.removeMessages(this.mUIHandler, 5);
    MessageHandler.removeMessages(this.mUIHandler, 10);
    MessageHandler.removeMessages(this.mUIHandler, 23);
    MessageHandler.removeMessages(this.mUIHandler, 61);
    MessageHandler.removeMessages(this.mUIHandler, 62);
    if ((DisplayDevice.canCancelFocus() == true) && (this.mFocusingState != 0))
      MessageHandler.sendEmptyMessage(this.mCameraHandler, 6);
    this.bCancelFocus = false;
    bFocusFromPress = false;
    LOG.W("HTCCamera", "onPause() - mFocusingState = NO_FOCUSING");
    this.mFocusingState = 0;
    MessageHandler.removeMessages(this.mUIHandler, 28);
    MessageHandler.removeMessages(this.mUIHandler, 34);
    MessageHandler.removeMessages(this.mUIHandler, 31);
    MessageHandler.removeMessages(this.mUIHandler, 22);
    MessageHandler.removeMessages(this.mUIHandler, 100);
    LOG.W("HTCCamera", "OnPause - set mWaitResetSettings to false");
    this.mWaitResetSettings = false;
    mIsKeyguardShow = false;
    mIsWaitKeyguardBeforePreview = false;
    releaseFaceDetection();
    MessageHandler.removeMessages(this.mUIHandler, 52);
    if ((this.mCameraThread != null) && (this.mCameraThread.getRecorderStatus() == true))
    {
      MessageHandler.removeMessages(this.mUIHandler, 64);
      MessageHandler.sendEmptyMessage(this.mUIHandler, 64);
    }
    if (!this.mIdle)
    {
      LOG.W("HTCCamera", "onPause mIdle is false");
      MessageHandler.removeMessages(this.mCameraHandler, 0);
      MessageHandler.sendEmptyMessage(this.mCameraHandler, 2);
      closeActionScreen();
      this.mIdle = true;
    }
    while (true)
    {
      MessageHandler.removeMessages(this.mUIHandler, 36);
      if ((isRequestMode(IntentManager.RequestMode.Main) == true) || (isRequestName(IntentManager.RequestName.Album) == true))
        WriteModePreference();
      try
      {
        unregisterReceiver(this.mKeyguardReceiver);
        if ((!this.mNeed_doOnResume) || (isPowerWarning()))
        {
          this.mNeed_unregisterReceiver = true;
          MessageHandler.removeMessages(this.mUIHandler, 63);
          LOG.W("HTCCamera", "OnPause - Freeze UI !!!");
          this.mFreezeUI = true;
          this.mFreezeOrientation = -1;
          if (DisplayDevice.supportSensor() == true)
            this.mOrientationListener.disable();
          if (DisplayDevice.isVirtualHWKeyRotated())
            updateVirtualHwKeysOrientation(0, false);
          Intent localIntent = new Intent("com.htc.eas.intent.resumeSync");
          localIntent.putExtra("com.htc.eas.extra.tag", "com.android.amaze_camera.HTCCamera");
          sendBroadcast(localIntent);
          if (this.mRunnable_Swing != null)
            this.mUIHandler.removeCallbacks(this.mRunnable_Swing);
          if (DisplayDevice.isDoubleShot())
            HTCCameraAdvanceSetting.writePreference(this, "pref_camera_scene_service_ds", Integer.valueOf(0));
          if (DisplayDevice.supportFastFrameRecording())
          {
            HTCCameraAdvanceSetting.writePreference(this, "pref_fast_fps", false);
            HTCCameraAdvanceSetting.writePreference(this, "pref_fast_frame_resolution_temp", "null");
          }
          if (DisplayDevice.support3DCamera())
            MessageHandler.removeMessages(this.mUIHandler, 72);
          MessageHandler.removeMessages(this.mUIHandler, 68);
          MessageHandler.removeMessages(this.mUIHandler, 69);
          doOnPause();
          super.onPause();
          LOG.W("HTCCamera", "onPause() - end");
          return;
          LOG.W("HTCCamera", "onPause mIdle is true");
          if (this.mScreensave == null)
            continue;
          this.mScreensave.setVisibility(8);
        }
      }
      catch (Exception localException)
      {
        while (true)
        {
          LOG.E("HTCCamera", "unregisterReceiver failed - mKeyguardReceiver", localException);
          continue;
          this.mNeed_unregisterReceiver = false;
        }
      }
    }
  }

  public boolean onPrepareOptionsMenu(Menu paramMenu)
  {
    return true;
  }

  public void onRestart()
  {
    LOG.W("HTCCamera", "onRestart() - start");
    super.onRestart();
    LOG.W("HTCCamera", "onRestart() - end");
  }

  public void onResume()
  {
    LOG.W("HTCCamera", "onResume() - start");
    super.onResume();
    IntentFilter localIntentFilter1 = new IntentFilter("android.intent.action.USER_PRESENT");
    try
    {
      registerReceiver(this.mKeyguardReceiver, localIntentFilter1);
      Intent localIntent = new Intent("com.htc.eas.intent.pauseSync");
      localIntent.putExtra("com.htc.eas.extra.tag", "com.android.amaze_camera.HTCCamera");
      sendBroadcast(localIntent);
      broadcastStopVoiceRecording();
      this.connManager = ((ConnectivityManager)getSystemService("connectivity"));
      if (this.connManager != null)
      {
        this.keep_backgroundDataSetting = this.connManager.getBackgroundDataSetting();
        LOG.V("HTCCamera", " keep backgroundDataSetting: " + this.keep_backgroundDataSetting);
        if ((this.mCameraThread == null) || (this.mCameraThread.mMode != 1))
          break label317;
        broadcastStopFM();
        broadcastStopMusic();
        stopNotesRecording();
        OrientationConfig.setUIOrientation(this.mInstanceOrientation);
        if (DisplayDevice.isVirtualHWKeyRotated())
          updateVirtualHwKeysOrientation(this.mInstanceOrientation, true);
        this.Display_Orientation = ((WindowManager)getSystemService("window")).getDefaultDisplay().getRotation();
        LOG.V("HTCCamera", "Initial Display Orientation: " + this.Display_Orientation);
        if (DisplayDevice.supportSensor() == true)
          this.mOrientationListener.enable();
        SensorHandler.createFocusFile();
        LOG.W("HTCCamera", "OnResume - reset UI, immediately set mMainLayout invisible");
        if (this.mMainLayout != null)
          this.mMainLayout.setVisibility(4);
        while (this.mCameraHandler == null)
          this.mCameraHandler = this.mCameraThread.getHandler();
      }
    }
    catch (Exception localException1)
    {
      while (true)
      {
        LOG.E("HTCCamera", "registerReceiver failed - mKeyguardReceiver", localException1);
        continue;
        LOG.E("HTCCamera", "connManager is null");
        continue;
        label317: if (DisplayDevice.isHalfPCB() != true)
          continue;
        broadcastStopFM();
      }
      setMaxBrightness();
      if (!DisplayDevice.support3DCamera())
        break label531;
    }
    IntentFilter localIntentFilter2;
    if (!this.m3DStatusInitialized)
    {
      if ((isRequestName(IntentManager.RequestName.Contacts) == true) || (isRequestName(IntentManager.RequestName.Square) == true) || (isRequestName(IntentManager.RequestName.Mms) == true) || (isRequestName(IntentManager.RequestName.FriendStream) == true) || (isRequestName(IntentManager.RequestName.Unknown_Service) == true))
        this.mForce2DMode = true;
      if (!DisplayDevice.support3DHWSwitch())
        break label842;
      if (!this.mForce2DMode)
        break label800;
      this.m3DButtonStatus = 1;
      if (this.mCameraThread != null)
        this.mCameraThread.m3DButtonStatus = this.m3DButtonStatus;
      if (this.m3DButtonStatus != 1)
        break label814;
      HTCCameraAdvanceSetting.writePreference(this, "pref_camera_3D_status", false);
      LOG.V("HTCCamera", "Initial m3DButtonStatus = " + this.m3DButtonStatus);
      localIntentFilter2 = new IntentFilter();
      localIntentFilter2.addAction("com.htc.intent.action.CAM_SWITCH_CHANGE");
      localIntentFilter2.setPriority(1001);
    }
    while (true)
    {
      try
      {
        registerReceiver(this.m3DKeySwitchReceiver, localIntentFilter2);
        this.m3DStatusInitialized = true;
        label531: if ((!PowerWarningController.isSupported()) || (this.mFlashRestriction != null))
          continue;
        this.mFlashRestriction = new FlashRestriction();
        LOG.W("HTCCamera", "initFlashRestriction - start");
        this.mFlashRestriction.initFlashRestriction(this);
        LOG.W("HTCCamera", "initFlashRestriction - end");
        this.mComponentManager.getComponent("PowerWarning UI").initialize();
        LOG.W("HTCCamera", "Block Capture UI - onResume(), and unblock after start preview");
        mBlockCaptureUI = true;
        this.mIsSipExist = false;
        LOG.W("HTCCamera", "onResume() - mFocusingState = NO_FOCUSING");
        this.mFocusingState = 0;
        KeyguardManager localKeyguardManager = (KeyguardManager)getSystemService("keyguard");
        if ((localKeyguardManager == null) || (!localKeyguardManager.keyguardIsShowing()))
          break label854;
        LOG.W("HTCCamera", "keyguard is shown !!!!!");
        mIsKeyguardShow = true;
        this.mActivityOnPause = false;
        this.mNeed_doOnResume = true;
        this.mLastSavedMediaPath = null;
        this.mIsCaptureTriggered = false;
        LOG.W("HTCCamera", "OnResume - set mWaitResetSettings to true");
        this.mWaitResetSettings = true;
        if (this.mIndicatorLayout_Camera == null)
          continue;
        updateIndicatorLayout_AutoCapture();
        if (this.mIdle)
          break label870;
        getWindow().addFlags(128);
        MessageHandler.removeMessages(this.mUIHandler, 36);
        MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 36, this.SCREEN_DELAY);
        if (this.mSurfaceView == null)
          continue;
        this.mSurfaceView.setVisibility(0);
        this.m_EventManager.raiseEvent("CameraActivity.Resuming");
        if (DisplayDevice.DisablePen() != true)
          continue;
        Util.disableSketcher(this);
        this.mIsBackQuit = false;
        LOG.W("HTCCamera", "onResume() - end");
        return;
        label800: this.m3DButtonStatus = Camera3DSettings.get3DInitialStatus(getContentResolver());
        break;
        label814: HTCCameraAdvanceSetting.writePreference(this, "pref_camera_3D_status", true);
      }
      catch (Exception localException2)
      {
        LOG.E("HTCCamera", "registerReceiver failed!!", localException2);
        continue;
      }
      label842: HTCCameraAdvanceSetting.writePreference(this, "pref_camera_3D_status", false);
      continue;
      label854: LOG.W("HTCCamera", "keyguard is not shown !!!!!");
      mIsKeyguardShow = false;
      continue;
      label870: if (!mIsKeyguardShow)
      {
        LOG.V("HTCCamera", "mIsKeyguardShow is false, activate");
        activate();
        continue;
      }
      LOG.V("HTCCamera", "mIsKeyguardShow is true, setWaitKeyguardBeforePreview(true)");
      setWaitKeyguardBeforePreview(true);
    }
  }

  public void onStart()
  {
    super.onStart();
    LOG.W("HTCCamera", "onStart() - start");
    if (!this.mSurfaceCreated)
      this.mIdle = false;
    if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 1))
      getAudioManager().requestAudioFocus(this.mAudioFocusListener, 3, 1);
    LOG.W("HTCCamera", "onStart() - end");
  }

  public void onStop()
  {
    LOG.W("HTCCamera", "onStop() - start");
    super.onStop();
    if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 1))
      getAudioManager().abandonAudioFocus(this.mAudioFocusListener);
    LOG.W("HTCCamera", "onStop() - end");
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    closeSettingsPanel();
    if ((DisplayDevice.canCancelFocus()) && (this.mFocusingState == 1) && (paramMotionEvent.getAction() == 0) && (getFaceNumber() == 0))
    {
      this.m_CancelFocusFromTouch = true;
      cancelAutoFocus();
      this.m_CancelFocusFromTouch = false;
      LOG.W("HTCCamera", "UnBlock Capture UI - touch down and cancel focus");
      mBlockCaptureUI = false;
    }
    if (mBlockCaptureUI == true)
      LOG.W("HTCCamera", "onTouchEvent - UI Block !!!");
    for (boolean bool = true; ; bool = true)
    {
      return bool;
      if (this.mIsSipExist != true)
        break;
      LOG.I("HTCCamera", "onTouchEvent - SIP is shown, UI Block !!!");
    }
    if ((this.mGpuEffectContr != null) && ((this.mtvSelfTimer == null) || (this.mtvSelfTimer.getVisibility() != 0)))
      this.mGpuEffectContr.handleTouchBehavior(paramMotionEvent);
    switch (paramMotionEvent.getAction())
    {
    default:
    case 0:
    case 2:
    case 1:
    }
    while (true)
    {
      bool = super.onTouchEvent(paramMotionEvent);
      break;
      this.mCloseTouchEvent = false;
      resetTapCapture((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
      if ((this.mMainLayout == null) || (DisplayDevice.isDoubleShot()))
        continue;
      Rect localRect = new Rect();
      this.mMainLayout.getHitRect(localRect);
      if (localRect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY()))
        continue;
      this.mCloseTouchEvent = true;
      continue;
      if ((this.mCloseTouchEvent == true) || (!DisplayDevice.supportTapScreenCapture()) || ((this.mGpuEffectContr != null) && (this.mGpuEffectContr.isControlBarOnTouch())))
        continue;
      checkTapCapture((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
      continue;
      if ((this.mCloseTouchEvent == true) || (hasSpecificEffect()) || (getFaceNumber() != 0) || (!canTriggerFocus()))
        continue;
      handleTouchFocus((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
    }
  }

  public boolean onTrackballEvent(MotionEvent paramMotionEvent)
  {
    if (2 == paramMotionEvent.getAction())
    {
      this.mUIHandler.removeMessages(59);
      this.mUIHandler.sendEmptyMessageDelayed(59, 5000L);
    }
    if (this.mUIHandler.hasMessages(39))
    {
      this.mUIHandler.removeMessages(39);
      MessageHandler.sendEmptyMessageDelayed(this.mUIHandler, 39, 1000 * getReviewDuration());
    }
    return super.onTrackballEvent(paramMotionEvent);
  }

  public void openCaptureUI()
  {
    LOG.V("HTCCamera", "openCaptureUI()");
    MessageHandler.removeMessages(this.mUIHandler, 16);
    if ((this.mOnScreenCameraSwitchBtnContainer != null) && (this.mCameraThread != null))
      if (this.mOnScreenCameraSwitchBtnIcon.getDrawable() == null)
      {
        if (this.mCameraThread.mMode == 0)
          this.mOnScreenCameraSwitchBtnIcon.setImageResource(2130837659);
      }
      else
      {
        this.mOnScreenCameraSwitchBtnContainer.setVisibility(0);
        this.mOnScreenSwitch_receiver.setVisibility(0);
        label78: if (this.mFilmstrip_layout != null)
        {
          if ((isRequestMode(IntentManager.RequestMode.Main)) || (isRequestName(IntentManager.RequestName.Album)))
            break label358;
          this.mFilmstrip_layout.setVisibility(4);
        }
        label113: if (this.mFpsText != null)
          this.mFpsText.setVisibility(0);
        if (this.mCapture_icon != null)
          this.mCapture_icon.setVisibility(0);
        if (this.mMainBar != null)
          this.mMainBar.setVisibility(0);
        enableMainBarItems(true);
        showMainBarItems(true, false);
        if (this.mCapture_combine_ds != null)
          this.mCapture_combine_ds.setVisibility(0);
        setPanelVisible(true);
        openSecondLayout(true);
        showEffectControl();
        this.mIsCaptureUIOpen = true;
        if (DisplayDevice.showSceneInMenu() == true)
        {
          if (this.mCameraThread.is2ndCamera())
            break label369;
          updateSceneIndicator(getPrefSceneType(this.mCameraThread.mMode));
        }
      }
    while (true)
    {
      this.m_EventManager.raiseEvent("CaptureUI.Open");
      if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 0) && (this.mLocationHandler != null) && (this.mLocationHandler.hasGeoTagging() == true) && (DisplayDevice.isDisplayGPSindicator()))
        show_gps_indicator();
      if (DisplayDevice.isDoubleShot())
      {
        updateSceneGuide(this.mScene_Idx);
        if (this.mCapture_btn_ds != null)
          this.mCapture_btn_ds.setEnabled(true);
      }
      return;
      this.mOnScreenCameraSwitchBtnIcon.setImageResource(2130837859);
      break;
      if (this.mCameraThread.mMode == 0)
      {
        updateSwitchIcon(0);
        break label78;
      }
      updateSwitchIcon(1);
      break label78;
      label358: this.mFilmstrip_layout.setVisibility(0);
      break label113;
      label369: showSceneIndicator(false);
    }
  }

  public void powerWarningOn(boolean paramBoolean)
  {
    if ((this.mCameraThread == null) || (this.mCameraHandler == null))
      LOG.E("HTCCamera", "deactivate - mCameraThread = null or mCameraHandler = null");
    while (true)
    {
      return;
      if (this.mUIHandler == null)
      {
        LOG.E("HTCCamera", "deactivate - mUIHandler = null");
        continue;
      }
      setPowerWarning(true);
      getWindow().clearFlags(128);
      mFocusMode = 0;
      releaseFaceDetection();
      MessageHandler.removeMessages(this.mUIHandler, 36);
      MessageHandler.removeMessages(this.mCameraHandler, 0);
      if (!paramBoolean)
        MessageHandler.sendEmptyMessage(this.mCameraHandler, 2);
      setOldBrightness();
      registerFocusSensor(false);
    }
  }

  public final void prepareActionScreen()
  {
    if (Thread.currentThread() == this.mMainThread)
      if (!this.mIsUIReady)
        LOG.V("HTCCamera", "mIsUIReady = false");
    while (true)
    {
      return;
      if (!needsActionScreen())
      {
        LOG.V("HTCCamera", "Action screen is not needed");
        continue;
      }
      if (!isServiceMode());
      for (this.mActionScreen = ((ActionScreen)this.mComponentManager.getComponent("Common Action Screen")); ; this.mActionScreen = ((ActionScreen)this.mComponentManager.getComponent("Request Action Screen")))
      {
        if (this.mActionScreen == null)
          break label107;
        this.mActionScreen.prepare();
        break;
      }
      label107: LOG.E("HTCCamera", "No available action screen");
      continue;
      if (this.mUIHandler != null)
      {
        this.mUIHandler.post(this.mPrepareActionScreenRunnable);
        continue;
      }
      LOG.E("HTCCamera", "Cannot prepare action screen because there is no handler");
    }
  }

  public boolean reachBeepCount()
  {
    if (mBeepCount >= 5);
    for (int i = 1; ; i = 0)
      return i;
  }

  public void registerFocusSensor(boolean paramBoolean)
  {
    if (this.mSensorHandler == null)
      LOG.W("HTCCamera", "mSensorHandler == null in registerFocusSensor()");
    while (true)
    {
      return;
      if (paramBoolean == true)
      {
        this.mSensorHandler.registerSensor();
        continue;
      }
      this.mSensorHandler.unregisterSensor();
    }
  }

  public void releaseFaceDetection()
  {
    LOG.W("HTCCamera", "releaseFaceDetection()");
    MessageHandler.removeMessages(this.mUIHandler, 35);
    if (this.mFaceDetection != null)
    {
      this.mFaceDetection.stopCheckLoop();
      this.mFaceDetection.stopFaceDetection();
      this.mFaceDetection.releaseFaceDetection();
      this.mFaceDetection = null;
    }
  }

  public void resetAutoCaptureTask()
  {
    mIsSelfTimerTask = false;
    mIsSelfPortraitTask = false;
  }

  public void resetBeepCount()
  {
    mBeepCount = 0;
  }

  public void resetFocusMode()
  {
    mFocusMode = 0;
  }

  public void resetForDOT()
  {
    this.mSwitchBtn_icon.setImageResource(2130837569);
    this.mSwitch_text_ds.setText(2131362128);
    HTCCameraAdvanceSetting.writePreference(this, "pref_camera_scene_ds", Integer.valueOf(0));
    HTCCameraAdvanceSetting.writePreference(this, "pref_camera_scene_service_ds", Integer.valueOf(0));
    doAfterSceneSelect(0);
    if (this.mCameraThread.mMode == 0)
      ViewUtil.enableMainButton(this.mScene_icon_ds, this.mScene_btn_ds, this.mScene_text_ds);
    while (true)
    {
      return;
      ViewUtil.disableMainButton(this.mScene_icon_ds, this.mScene_btn_ds, this.mScene_text_ds);
    }
  }

  public void resetIndicatorLayout()
  {
    if (this.mAutoCaptureLayout != null)
      this.mAutoCaptureLayout.setVisibility(4);
    if ((this.mivMute != null) && (HTCCameraAdvanceSetting.getIsRecordWithAudio(this)))
      this.mivMute.setVisibility(4);
    this.mFaceNumber = 0;
    if (DisplayDevice.showSceneInMenu() == true)
    {
      setLayoutForTimer(false, OrientationConfig.getUIOrientation(), this.mCameraThread.mMode);
      setLayoutForRecordIcon(false, OrientationConfig.getUIOrientation(), this.mCameraThread.mMode);
      showSceneIndicator(false);
      this.mIsFlashlightOffByScene = false;
      if (this.mGpuEffectContr != null)
        this.mGpuEffectContr.resetOldSceneValue();
    }
    hideSelfPortraitHint();
  }

  public void reset_layout_from_surface(int paramInt1, int paramInt2)
  {
    LOG.W("HTCCamera", "reset_layout_from_surface - width: " + paramInt1 + ", height: " + paramInt2);
    ViewStub localViewStub;
    if (this.mMainLayout == null)
    {
      LOG.V("HTCCamera", "mMainLayout == null - initiate mMainLayout");
      if (!DisplayDevice.isDoubleShot())
      {
        localViewStub = (ViewStub)findViewById(2131230787);
        this.mMainLayout = localViewStub.inflate();
      }
    }
    else
    {
      this.mMainLayout.setVisibility(0);
      LOG.W("HTCCamera", "Reset layout end, set mMainLayout visible");
      if (this.mSecondLayout == null)
        this.mSecondLayout = ((ViewStub)findViewById(2131230777)).inflate();
      if ((DisplayDevice.isDoubleShot()) && (this.mSceneGuideLayout == null))
        this.mSceneGuideLayout = ((ViewStub)findViewById(2131230784)).inflate();
      if ((DisplayDevice.captrueFullSize()) && ((isRequestName(IntentManager.RequestName.Contacts) == true) || (isRequestName(IntentManager.RequestName.Square) == true)))
      {
        if (this.mPreviewFilterLayout == null)
        {
          this.mPreviewFilterLayout = ((ViewStub)findViewById(2131230763)).inflate();
          this.mPreviewVisibleArea = this.mPreviewFilterLayout.findViewById(2131231039);
        }
        if (paramInt1 >= paramInt2)
          break label269;
      }
    }
    label269: for (int i = paramInt1; ; i = paramInt2)
    {
      ViewGroup.LayoutParams localLayoutParams = this.mPreviewVisibleArea.getLayoutParams();
      localLayoutParams.width = i;
      localLayoutParams.height = i;
      this.mPreviewVisibleArea.setLayoutParams(localLayoutParams);
      return;
      localViewStub = (ViewStub)findViewById(2131230789);
      break;
    }
  }

  public void restoreBackgrounddataSetting()
  {
    if (this.connManager != null)
      LOG.V("HTCCamera", "restoreBackgrounddataSetting: " + this.keep_backgroundDataSetting);
  }

  public final void returnRequestedMedia()
  {
    if (!isServiceMode())
    {
      LOG.W("HTCCamera", "Returning media from non-service mode");
      return;
    }
    Uri localUri = IntentManager.getSaveUri();
    if (IntentManager.getCropValue() != null)
    {
      LOG.V("HTCCamera", "Has Crop Extras , pass to CropImage Activity");
      Bundle localBundle = getIntent().getExtras();
      if (localBundle != null)
      {
        if (localUri == null)
          break label116;
        localBundle.putParcelable("output", localUri);
      }
      while (true)
      {
        Intent localIntent2 = new Intent("com.android.amaze_camera.action.CROP");
        localIntent2.setType("image/*");
        localIntent2.setData(CameraThread.getLastContentUri());
        if (localBundle != null)
          localIntent2.putExtras(localBundle);
        startActivityForResult(localIntent2, 1);
        break;
        label116: localBundle.putBoolean("return-data", true);
      }
    }
    byte[] arrayOfByte = null;
    if (this.mCameraThread != null)
      arrayOfByte = this.mCameraThread.getJpegData();
    Intent localIntent1 = new Intent();
    if (isRequestName(IntentManager.RequestName.Contacts))
      if (arrayOfByte == null)
        LOG.W("HTCCamera", "contacts - jpeg data is null !!");
    while (true)
    {
      if (this.mCameraThread != null)
        this.mCameraThread.resetJpegData();
      setResult(-1, localIntent1);
      finish();
      break;
      if (DisplayDevice.captrueFullSize())
      {
        Bitmap localBitmap3 = Util.cropSquareImg(arrayOfByte, this.mCameraThread.getCaptureWidth(), this.mCameraThread.getCaptureHeight(), Resolution.CONTACT_STYLE.getWidth());
        if (localBitmap3 != null)
        {
          localIntent1.setAction("inline-data").putExtra("data", localBitmap3);
          continue;
        }
        LOG.E("HTCCamera", "return jpeg decode error!!");
        continue;
      }
      localIntent1.putExtra("jpeg_data", arrayOfByte);
      continue;
      if (isRequestName(IntentManager.RequestName.Square))
      {
        if (arrayOfByte == null)
        {
          LOG.W("HTCCamera", "square - jpeg data is null !!");
          continue;
        }
        if (DisplayDevice.captrueFullSize())
        {
          Bitmap localBitmap2 = Util.cropSquareImg(arrayOfByte, this.mCameraThread.getCaptureWidth(), this.mCameraThread.getCaptureHeight(), DisplayDevice.CAMERA_PIC_SIZE_FOR_SQUARE);
          if (localBitmap2 != null)
          {
            localIntent1.setAction("inline-data").putExtra("data", localBitmap2);
            continue;
          }
          LOG.E("HTCCamera", "return jpeg decode error!!");
          continue;
        }
        localIntent1.setData(CameraThread.getLastContentUri());
        LOG.W("HTCCamera", "return request:" + CameraThread.getLastContentUri());
        continue;
      }
      OutputStream localOutputStream;
      if (localUri != null)
        if (arrayOfByte != null)
          localOutputStream = null;
      while (true)
      {
        try
        {
          LOG.V("HTCCamera", "sent MediaStore.EXTRA_OUTPUT - start");
          localOutputStream = getContentResolver().openOutputStream(localUri);
          localOutputStream.write(arrayOfByte);
          localOutputStream.close();
          LOG.V("HTCCamera", "sent MediaStore.EXTRA_OUTPUT - end");
          Util.closeSilently(localOutputStream);
          if ((!isRequestName(IntentManager.RequestName.Unknown_Service)) || (this.mCameraThread == null) || (this.mCameraThread.getMode() != 0))
            break;
          if (arrayOfByte == null)
            break label630;
          Bitmap localBitmap1 = Util.makeBitmap(arrayOfByte, -1, 102400, Bitmap.Config.ARGB_8888);
          if (localBitmap1 == null)
            break label618;
          localIntent1.setAction("inline-data").putExtra("data", localBitmap1);
          break;
        }
        catch (IOException localIOException)
        {
          LOG.V("HTCCamera", "sent JpegData fail");
          Util.closeSilently(localOutputStream);
          continue;
        }
        finally
        {
          Util.closeSilently(localOutputStream);
        }
        LOG.W("HTCCamera", "EXTRA_OUTPUT - jpeg data is null !!");
        continue;
        localIntent1.setData(CameraThread.getLastContentUri());
        LOG.W("HTCCamera", "return request:" + CameraThread.getLastContentUri());
      }
      label618: LOG.E("HTCCamera", "return jpeg decode error!!");
      continue;
      label630: LOG.W("HTCCamera", "inline-data - jpeg data is null !!");
    }
  }

  public void setBackgroundDataSetting(boolean paramBoolean)
  {
    if (this.connManager != null)
      LOG.V("HTCCamera", "set BackgroundDataSetting: " + paramBoolean);
  }

  public void setBlock3DSwitch(boolean paramBoolean)
  {
    mBlock3DSwitch = paramBoolean;
  }

  public void setBlockCaptureUI(boolean paramBoolean)
  {
    mBlockCaptureUI = paramBoolean;
  }

  public void setBlockPowerWarning(boolean paramBoolean)
  {
    LOG.W("HTCCamera", "setBlockPowerWarning " + paramBoolean);
    this.mBlockPowerWarning = paramBoolean;
  }

  public void setFocusingState(int paramInt)
  {
    this.mFocusingState = paramInt;
  }

  public void setIconPosition(int paramInt, View paramView)
  {
    if (paramView == null)
      return;
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, -1);
    switch (paramInt)
    {
    default:
    case 0:
    case 2:
    case 1:
    case 3:
    }
    while (true)
    {
      paramView.setLayoutParams(localLayoutParams);
      break;
      localLayoutParams.setMargins(0, 111, 367, 0);
      continue;
      localLayoutParams.setMargins(367, 0, 0, 111);
      continue;
      localLayoutParams.setMargins(120, 4, 0, 0);
      continue;
      localLayoutParams.setMargins(0, 0, 120, 4);
    }
  }

  public void setPowerWarning(boolean paramBoolean)
  {
    LOG.W("HTCCamera", "setPowerWarning " + paramBoolean);
    this.mIsPowerWarning = paramBoolean;
  }

  public void setTurnOnTorch(boolean paramBoolean)
  {
    mTurnOnTorch_Camcorder = paramBoolean;
  }

  public void setWaitResetSettings(boolean paramBoolean)
  {
    this.mWaitResetSettings = paramBoolean;
  }

  public Dialog setasIntentChooser()
  {
    if (this.mIntentManager == null)
      LOG.E("HTCCamera", "setasIntentChooser() - mIntentManager == null");
    for (Dialog localDialog = null; ; localDialog = this.mIntentManager.setasIntentChooser())
      return localDialog;
  }

  public Dialog shareIntentChooser(int paramInt)
  {
    if (this.mIntentManager == null)
      LOG.E("HTCCamera", "shareIntentChooser() - mIntentManager == null");
    for (Dialog localDialog = null; ; localDialog = this.mIntentManager.shareIntentChooser(paramInt))
      return localDialog;
  }

  public final void showActionScreen()
  {
    showActionScreen(0);
  }

  public final void showActionScreen(int paramInt)
  {
    if (Thread.currentThread() == this.mMainThread)
    {
      this.mIsCaptureTriggered = false;
      if (!needsActionScreen())
        onActionScreenClosed(null);
    }
    while (true)
    {
      return;
      mBlockCaptureUI = false;
      if (this.mActionScreen != null)
      {
        this.mActionScreen.open();
        continue;
      }
      LOG.E("HTCCamera", "No action screen to open");
      continue;
      if (this.mUIHandler != null)
      {
        if (paramInt <= 0)
        {
          this.mUIHandler.post(this.mShowActionScreenRunnable);
          continue;
        }
        this.mUIHandler.postDelayed(this.mShowActionScreenRunnable, paramInt);
        continue;
      }
      LOG.E("HTCCamera", "Cannot show action screen because there is no handler");
    }
  }

  public void showEffectControl()
  {
    if (this.mGpuEffectContr != null)
      this.mGpuEffectContr.showEffectControl(false);
  }

  public final void showMainBarItems(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mMainBar_item == null);
    while (true)
    {
      return;
      if (paramBoolean1)
      {
        if (this.mMainBar_item.getVisibility() == 0)
          continue;
        this.mMainBar_item.setVisibility(0);
        if (paramBoolean2)
        {
          if (!DisplayDevice.isDoubleShot())
          {
            AnimationManager.slideIn(this.mMainBar_item, 3, 0L, 200L);
            continue;
          }
          AnimationManager.slideIn(this.mMainBar_item, 2, 0L, 200L);
          continue;
        }
        this.mMainBar_item.clearAnimation();
        continue;
      }
      if (this.mMainBar_item.getVisibility() != 0)
        continue;
      this.mMainBar_item.setVisibility(4);
      if (paramBoolean2)
      {
        if (!DisplayDevice.isDoubleShot())
        {
          AnimationManager.slideOut(this.mMainBar_item, 3, 0L, 200L);
          continue;
        }
        AnimationManager.slideOut(this.mMainBar_item, 2, 0L, 200L);
        continue;
      }
      this.mMainBar_item.clearAnimation();
    }
  }

  public void showSceneIndicatorAnimation(View paramView, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, int paramInt4)
  {
    boolean bool1 = true;
    paramView.clearAnimation();
    if (paramBoolean == bool1)
    {
      AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat1, paramFloat2);
      localAlphaAnimation.setStartOffset(paramInt1);
      localAlphaAnimation.setDuration(paramInt2);
      paramView.startAnimation(localAlphaAnimation);
      localAlphaAnimation.setAnimationListener(new Animation.AnimationListener(paramInt3, paramInt4)
      {
        public void onAnimationEnd(Animation paramAnimation)
        {
          boolean bool1 = HTCCameraAdvanceSetting.getIsRecordWithAudio(HTCCamera.this);
          int i = HTCCameraAdvanceSetting.getSelfTimer(HTCCamera.this);
          boolean bool2 = false;
          if (i > 0)
            bool2 = true;
          HTCCamera.this.setLayoutForTimer(bool2, this.val$orientation, this.val$nMode);
          HTCCamera localHTCCamera = HTCCamera.this;
          if (!bool1);
          for (boolean bool3 = true; ; bool3 = false)
          {
            localHTCCamera.setLayoutForRecordIcon(bool3, OrientationConfig.getUIOrientation(), this.val$nMode);
            HTCCamera.this.m_sceneContainer.setOrientation(this.val$orientation);
            HTCCamera.this.updateSceneIndicator(HTCCamera.this.getPrefSceneType(this.val$nMode));
            return;
          }
        }

        public void onAnimationRepeat(Animation paramAnimation)
        {
        }

        public void onAnimationStart(Animation paramAnimation)
        {
        }
      });
      return;
    }
    boolean bool2 = HTCCameraAdvanceSetting.getIsRecordWithAudio(this);
    int i = HTCCameraAdvanceSetting.getSelfTimer(this);
    boolean bool3 = false;
    if (i > 0)
      bool3 = true;
    setLayoutForTimer(bool3, paramInt3, paramInt4);
    if (!bool2);
    while (true)
    {
      setLayoutForRecordIcon(bool1, OrientationConfig.getUIOrientation(), paramInt4);
      this.m_sceneContainer.setOrientation(paramInt3);
      if (this.mFocusingState != 0)
        break;
      updateSceneIndicator(getPrefSceneType(paramInt4));
      break;
      bool1 = false;
    }
  }

  public void show_gps_indicator()
  {
  }

  public final void startAlbum()
  {
    if (isRequestName(IntentManager.RequestName.Album));
    for (String str = "FROM_CAMERA"; ; str = "com.htc.album.action.VIEW_PHOTO_FROM_CAMERA")
    {
      startAlbum(str, CameraThread.getLastContentUri(), "image/jpeg");
      return;
    }
  }

  public final void startAlbum(String paramString1, Uri paramUri, String paramString2)
  {
    if (isRequestName(IntentManager.RequestName.Album))
    {
      LOG.V("HTCCamera", "Returning to album");
      Intent localIntent2 = new Intent(paramString1);
      localIntent2.putExtra("preview_mode", "filmstrip");
      setResult(-1, localIntent2);
      finish();
      return;
    }
    LOG.V("HTCCamera", "Going to album");
    Intent localIntent1 = new Intent(paramString1);
    if ((this.mCameraThread != null) && (this.mCameraThread.mMode == 0))
      localIntent1.putExtra("capture_mode", "camera");
    while (true)
    {
      while (true)
      {
        localIntent1.setDataAndType(paramUri, paramString2);
        localIntent1.putExtra("preview_mode", "filmstrip");
        try
        {
          startActivity(localIntent1);
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          LOG.W("HTCCamera", "Cannot start album : " + localActivityNotFoundException);
        }
      }
      break;
      localIntent1.putExtra("capture_mode", "comcorder");
    }
  }

  public void startCameraTutorial()
  {
    Intent localIntent = new Intent();
    localIntent.setAction("com.android.amaze_camera.tutorial.CAMERA_MODE");
    startActivity(localIntent);
  }

  public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
  {
    LOG.W("HTCCamera", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ surfaceChanged $$$$$$$$$$$$$$$$$$$$$$$$$$$ w=" + paramInt2 + " h=" + paramInt3);
  }

  public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
  {
    LOG.W("HTCCamera", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ surfaceCreated $$$$$$$$$$$$$$$$$$$$$$$$$$$" + paramSurfaceHolder);
    this.mSurfaceHolder = paramSurfaceHolder;
    this.mSurfaceCreated = true;
    if ((this.mCameraThread != null) && (!this.mActivityOnPause) && ((!mIsKeyguardShow) || (!this.mIdle)))
    {
      LOG.W("HTCCamera", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ surfaceCreated $$$$$$$$$$$$$$$$$$$$$$$$$$$  start preview");
      MessageHandler.sendObtainMessage(this.mCameraHandler, 0, 1, 0, null);
    }
    while (true)
    {
      return;
      LOG.W("HTCCamera", "surfaceCreated before onResume - set mIdle = true");
      this.mIdle = true;
    }
  }

  public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
  {
    LOG.W("HTCCamera", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ surfaceDestroyed $$$$$$$$$$$$$$$$$$$$$$$$$$$");
    this.mSurfaceCreated = false;
    this.mSurfaceHolder = null;
  }

  public final void switchFlashMode()
  {
    String str1;
    String str2;
    if (isFlashEnabled())
    {
      if (this.mCameraThread.getMode() != 0)
        break label77;
      str1 = HTCCameraAdvanceSetting.getPrefenceValue(this, "pref_camera_flash_mode");
      if (!str1.equals("auto"))
        break label53;
      str2 = "on";
    }
    while (true)
    {
      HTCCameraAdvanceSetting.writePreference(this, "pref_camera_flash_mode", str2);
      updateFlashMode();
      return;
      label53: if (str1.equals("on"))
      {
        str2 = "off";
        continue;
      }
      str2 = "auto";
    }
    label77: if (!mTurnOnTorch_Camcorder);
    for (boolean bool = true; ; bool = false)
    {
      mTurnOnTorch_Camcorder = bool;
      break;
    }
  }

  public boolean switchMode(int paramInt)
  {
    if (this.mCameraThread != null);
    for (boolean bool = switchMode(this.mCameraThread.is2ndCamera(), paramInt); ; bool = false)
      return bool;
  }

  public boolean switchMode(boolean paramBoolean, int paramInt)
  {
    this.m_EventManager.raiseEvent("Mode.Switched");
    int i;
    if (this.mFocusingState == 1)
    {
      if (DisplayDevice.canCancelFocus())
      {
        LOG.W("HTCCamera", "change camera mode when focusing, cancel focus");
        cancelAutoFocus();
        LOG.W("HTCCamera", "UnBlock Capture UI - change camera mode and cancel focus");
        mBlockCaptureUI = false;
      }
    }
    else
    {
      if ((this.mCameraThread != null) && (!this.mCameraThread.getRecorderStatus()))
        break label85;
      i = 0;
    }
    while (true)
    {
      return i;
      LOG.W("HTCCamera", "change camera mode when focusing, but device can't cancel focus");
      i = 0;
      continue;
      label85: if (this.mWaitResetSettings)
      {
        i = 0;
        continue;
      }
      if (!mBlockCaptureUI)
        break;
      i = 0;
    }
    mBlockCaptureUI = true;
    resetFocusMode();
    releaseFaceDetection();
    int j;
    if (paramBoolean != this.mCameraThread.is2ndCamera())
    {
      j = 1;
      label134: if (j != 0)
      {
        this.mWaitResetSettings = true;
        this.mIsPreviewStarted = false;
        LOG.W("HTCCamera", "restartCamera() - set mWaitResetSettings to true");
        closeSettingsPanel();
        HTCCameraAdvanceSetting.writePreference(this, "pref_camera_switch", paramBoolean);
        if (paramBoolean)
          hideZoomBar(false);
        if (DisplayDevice.support3DCamera())
        {
          if (this.mCameraThread.m3DButtonStatus != 0)
            break label299;
          lockOrientation(1);
        }
        label201: MessageHandler.sendEmptyMessage(this.mCameraHandler, 2);
      }
      if (paramInt == this.mCameraThread.getMode())
        break label360;
      switch (paramInt)
      {
      default:
        LOG.E("HTCCamera", "Try switching to unknown mode : " + paramInt);
        if (j == 0)
          break;
        MessageHandler.sendObtainMessage(this.mCameraHandler, 0, 1, 0, "true");
      case 0:
      case 1:
      }
    }
    label402: 
    while (true)
    {
      i = 1;
      break;
      j = 0;
      break label134;
      label299: unlockOrientation();
      break label201;
      closeSecondLayout(false);
      changetoCameraMode();
      if (this.mGpuEffectContr == null)
        continue;
      this.mGpuEffectContr.effectChangeMode(0);
      continue;
      closeSecondLayout(false);
      changetoVideoMode();
      if (this.mGpuEffectContr == null)
        continue;
      this.mGpuEffectContr.effectChangeMode(1);
      continue;
      label360: if (j != 0)
        MessageHandler.sendObtainMessage(this.mCameraHandler, 0, 1, 0, "true");
      while (true)
      {
        if (DisplayDevice.showSceneInMenu() != true)
          break label402;
        updateSceneIndicator(getPrefSceneType(paramInt));
        break;
        mBlockCaptureUI = false;
      }
    }
  }

  public final void unlockOrientation()
  {
    lockOrientation(-1);
  }

  public final void unlockZoom()
  {
    LOG.V("HTCCamera", "unlockZoom() - start");
    threadAccessCheck();
    if (!this.mIsZoomLocked)
      LOG.W("HTCCamera", "Zoom is already be unlocked");
    while (true)
    {
      return;
      this.mIsZoomLocked = false;
      if (!DisplayDevice.isDoubleShot())
        updateZoomBarVisible();
      this.m_EventManager.raiseEvent(new OneValueEvent("Zoom.LockedStateChanged", Boolean.valueOf(false)));
      LOG.V("HTCCamera", "unlockZoom() - end");
    }
  }

  public void updateAutoScene_ds(int paramInt)
  {
    if (this.mScene_icon_ds == null)
      return;
    if (this.mScene_Idx == 0)
      if (this.mCameraThread != null)
      {
        if ((this.mCameraThread.getMode() != 1) && (!this.mCameraThread.is2ndCamera()))
          break label63;
        this.mScene_icon_ds.setImageResource(2130837567);
      }
    while (true)
    {
      this.mScene_layout_ds.invalidate();
      break;
      break;
      switch (paramInt)
      {
      default:
        break;
      case 0:
        this.mScene_icon_ds.setImageResource(2130837567);
        break;
      case 1:
        this.mScene_icon_ds.setImageResource(2130837579);
        break;
      case 2:
        this.mScene_icon_ds.setImageResource(2130837596);
        break;
      case 3:
        label63: this.mScene_icon_ds.setImageResource(2130837600);
      }
    }
  }

  public void updateBeepLevel(int paramInt)
  {
    int i;
    if (MessageHandler.hasMessages(this.mUIHandler, 69))
    {
      i = -1;
      switch (paramInt)
      {
      default:
        if (i != 200)
          mBeepCount = 0;
        if (mCurrentBeepLevel != i)
          break;
      case 1:
      case 2:
      }
    }
    while (true)
    {
      return;
      i = 200;
      break;
      i = -1;
      break;
      mCurrentBeepLevel = i;
      MessageHandler.removeMessages(this.mUIHandler, 69);
      if (i != 200)
        continue;
      long l = System.currentTimeMillis() - this.mPlayBeepTime;
      MessageHandler.removeMessages(this.mUIHandler, 69);
      if (l > 200L)
      {
        MessageHandler.sendObtainMessage(this.mUIHandler, 69, 2131099648, 0, null);
        continue;
      }
      MessageHandler.sendObtainMessageDelayed(this.mUIHandler, 69, 2131099648, 0, null, 200L - l);
    }
  }

  public void updateDOTCaptureIcon(boolean paramBoolean, int paramInt)
  {
    if ((this.mCameraThread == null) || (this.mCapture_btn_ds == null));
    while (true)
    {
      return;
      if (paramBoolean)
      {
        if (paramInt == 0)
        {
          Drawable localDrawable = ViewUtil.getCustomDrawable(this, 2131361792, 2130837601);
          this.mCapture_btn_ds.setBackgroundDrawable(localDrawable);
          continue;
        }
        this.mCapture_btn_ds.setBackgroundResource(2130837856);
        continue;
      }
      if (paramInt == 0)
      {
        this.mCapture_btn_ds.setBackgroundResource(2130837602);
        continue;
      }
      this.mCapture_btn_ds.setBackgroundResource(2130837857);
    }
  }

  public void updateFaceIcon(int paramInt)
  {
    if (mIsSelfPortraitTask == true);
    while (true)
    {
      return;
      if ((!DisplayDevice.isDoubleShot()) && (this.mCameraThread != null) && (this.mCameraThread.mMode == 0) && (this.mMainBar_item != null) && (this.mMainBar_item.getVisibility() == 0) && (this.mMode_icon != null) && (this.mMode_icon.getVisibility() == 0) && ((isRequestMode(IntentManager.RequestMode.Main) == true) || (isRequestName(IntentManager.RequestName.Album) == true)))
      {
        if (paramInt <= 0)
          break label165;
        this.mMode_icon.setImageResource(2130837716);
      }
      while (true)
      {
        if ((this.mAutoCaptureLayout == null) || (this.mivFaceOne == null) || (this.mivFaceTwo == null) || (this.mAutoCaptureLayout.getVisibility() != 0))
          break label176;
        if (paramInt <= 0)
          break label178;
        if (MessageHandler.hasMessages(this.mUIHandler, 69))
          break;
        MessageHandler.sendObtainMessage(this.mUIHandler, 69, 2131099648, 0, null);
        break;
        label165: this.mMode_icon.setImageResource(2130837714);
      }
      label176: continue;
      label178: MessageHandler.removeMessages(this.mUIHandler, 69);
    }
  }

  public final void updateFlashMode()
  {
    if (Thread.currentThread() != this.mMainThread)
      if (this.mUIHandler != null)
        this.mUIHandler.post(new Runnable()
        {
          public void run()
          {
            HTCCamera.this.updateFlashMode();
          }
        });
    while (true)
    {
      return;
      LOG.E("HTCCamera", "Cannot update flash mode, because there is no handler for UI");
      continue;
      MessageHandler.removeMessages(this.mCameraHandler, 21);
      if (isFlashEnabled())
        break;
      MessageHandler.sendObtainMessage(this.mCameraHandler, 21, 0, 0, "off");
      ImageView localImageView;
      if (this.mFlash_icon != null)
      {
        localImageView = this.mFlash_icon;
        if (DisplayDevice.isDoubleShot())
          break label175;
      }
      label175: for (int i1 = 2130837708; ; i1 = 2130837582)
      {
        localImageView.setImageResource(i1);
        if ((DisplayDevice.isDoubleShot()) && (this.mFlash_text_ds != null))
          this.mFlash_text_ds.setText(2131362132);
        if (!hasFlash())
          break label183;
        ViewUtil.enableMainButton(this.mFlash_icon, this.mFlash_btn, this.mFlash_text_ds);
        ViewUtil.disableImageView(this.mFlash_icon);
        if (!DisplayDevice.isDoubleShot())
          break;
        ViewUtil.disableTextView(this.mFlash_text_ds);
        break;
      }
      label183: ViewUtil.disableMainButton(this.mFlash_icon, this.mFlash_btn, this.mFlash_text_ds);
    }
    String str;
    String[] arrayOfString1;
    String[] arrayOfString2;
    int k;
    label273: TypedArray localTypedArray;
    int m;
    if (this.mCameraThread.getMode() == 0)
    {
      str = HTCCameraAdvanceSetting.getPrefenceValue(this, "pref_camera_flash_mode");
      if (this.mFlash_icon != null)
      {
        if (this.mCameraThread.getMode() != 0)
          break label428;
        Resources localResources = getResources();
        arrayOfString1 = localResources.getStringArray(2131165194);
        arrayOfString2 = localResources.getStringArray(2131165195);
        if (DisplayDevice.isDoubleShot())
          break label414;
        k = 2131165193;
        localTypedArray = localResources.obtainTypedArray(k);
        m = -1;
      }
    }
    for (int n = arrayOfString1.length - 1; ; n--)
    {
      if (n >= 0)
      {
        if (!str.equals(arrayOfString1[n]))
          continue;
        m = n;
      }
      if (m < 0)
      {
        m = 0;
        str = "auto";
      }
      this.mFlash_icon.setImageResource(localTypedArray.getResourceId(m, 0));
      if (DisplayDevice.isDoubleShot())
        this.mFlash_text_ds.setText(arrayOfString2[m]);
      localTypedArray.recycle();
      ViewUtil.enableMainButton(this.mFlash_icon, this.mFlash_btn, this.mFlash_text_ds);
      MessageHandler.sendObtainMessage(this.mCameraHandler, 21, 0, 0, str);
      break;
      if (mTurnOnTorch_Camcorder);
      for (str = "torch"; ; str = "off")
        break;
      label414: k = 2131165196;
      break label273;
    }
    label428: int i;
    if (!mTurnOnTorch_Camcorder)
    {
      if (!DisplayDevice.isDoubleShot())
      {
        i = 2130837708;
        label444: if (DisplayDevice.isDoubleShot())
          break label484;
      }
      label484: for (j = 0; ; j = 2131362132)
      {
        this.mFlash_icon.setImageResource(i);
        if (!DisplayDevice.isDoubleShot())
          break;
        this.mFlash_text_ds.setText(j);
        break;
        i = 2130837582;
        break label444;
      }
    }
    if (!DisplayDevice.isDoubleShot())
    {
      i = 2130837709;
      label501: if (DisplayDevice.isDoubleShot())
        break label519;
    }
    label519: for (int j = 0; ; j = 2131362131)
    {
      break;
      i = 2130837583;
      break label501;
    }
  }

  public void updateIndicatorLayout_AutoCapture()
  {
    if (this.mAutoCaptureLayout == null);
    do
      return;
    while ((this.mCameraThread == null) || (this.mCameraThread.mMode != 0));
    this.mAutoCaptureLayout.setVisibility(0);
    boolean bool;
    switch (getSelfTimerInterval())
    {
    default:
      this.mivSelfTimer.setImageResource(2130837756);
      bool = false;
      label76: if (!bool)
        break;
      this.mivSelfTimer.setVisibility(0);
      label88: if (DisplayDevice.showSceneInMenu() == true)
      {
        setLayoutForTimer(bool, OrientationConfig.getUIOrientation(), this.mCameraThread.mMode);
        if (this.m_sceneContainer != null)
          this.m_sceneContainer.setOrientation(OrientationConfig.getUIOrientation());
      }
      this.mFaceNumber = HTCCameraAdvanceSetting.getFaceNumber(this);
      if (this.mFaceNumber > 0)
        showSelfPortraitHint();
    case 2:
    case 10:
    }
    while (!DisplayDevice.isDoubleShot())
    {
      updateZoomBarVisible();
      break;
      this.mivSelfTimer.setImageResource(2130837755);
      bool = true;
      break label76;
      this.mivSelfTimer.setImageResource(2130837754);
      bool = true;
      break label76;
      this.mivSelfTimer.setVisibility(8);
      break label88;
      if (!bool)
        this.mAutoCaptureLayout.setVisibility(8);
      hideSelfPortraitHint();
    }
  }

  public void updateIndicatorLayout_RecordMute()
  {
    if (this.mivMute == null);
    while (true)
    {
      return;
      if ((this.mCameraThread == null) || (this.mCameraThread.mMode != 1))
        break;
      boolean bool1 = HTCCameraAdvanceSetting.getIsRecordWithAudio(this);
      if (bool1)
        break label128;
      LOG.V("HTCCamera", "Reset View: Record Sound Indicator - off");
      this.mivMute.setImageResource(2130837836);
      this.mivMute.setVisibility(0);
      label62: this.m_EventManager.raiseEvent(new BooleanEvent("RecordWithAudio.Changed", bool1));
      if (DisplayDevice.showSceneInMenu() != true)
        continue;
      if (bool1)
        break label149;
    }
    label128: label149: for (boolean bool2 = true; ; bool2 = false)
    {
      setLayoutForRecordIcon(bool2, OrientationConfig.getUIOrientation(), this.mCameraThread.mMode);
      if (this.m_sceneContainer == null)
        break;
      this.m_sceneContainer.setOrientation(OrientationConfig.getUIOrientation());
      break;
      break;
      LOG.V("HTCCamera", "Reset View: Record Sound Indicator - on");
      this.mivMute.setVisibility(8);
      break label62;
    }
  }

  public void updateSceneIndicator(String paramString)
  {
    if (paramString.equals("auto"))
    {
      if (this.m_sceneContainer != null)
        this.m_sceneContainer.setVisibility(4);
      this.mIsFlashlightOffByScene = false;
    }
    while (true)
    {
      if (this.mCameraThread.mMode == 0)
        updateFlashMode();
      return;
      if (paramString.equals("portrait"))
      {
        this.m_sceneImage.setImageResource(2130837599);
        this.mIsFlashlightOffByScene = false;
      }
      while (true)
      {
        if (this.m_sceneContainer == null)
          break label352;
        if (HTCCameraAdvanceSetting.getPrefenceBoolean(this, "pref_camera_switch").booleanValue() == true)
          break label354;
        this.m_sceneContainer.setVisibility(0);
        break;
        if (paramString.equals("landscape"))
        {
          this.m_sceneImage.setImageResource(2130837589);
          this.mIsFlashlightOffByScene = false;
          continue;
        }
        if (paramString.equals("sports"))
        {
          this.m_sceneImage.setImageResource(2130837609);
          this.mIsFlashlightOffByScene = false;
          continue;
        }
        if (paramString.equals("flowers"))
        {
          this.m_sceneImage.setImageResource(2130837593);
          this.mIsFlashlightOffByScene = false;
          continue;
        }
        if (paramString.equals("backlight"))
        {
          this.m_sceneImage.setImageResource(2130837570);
          this.mIsFlashlightOffByScene = false;
          continue;
        }
        if (paramString.equals("beach"))
        {
          this.m_sceneImage.setImageResource(2130837571);
          this.mIsFlashlightOffByScene = false;
          continue;
        }
        if (paramString.equals("snow"))
        {
          this.m_sceneImage.setImageResource(2130837608);
          this.mIsFlashlightOffByScene = false;
          continue;
        }
        if (paramString.equals("sunset"))
        {
          this.m_sceneImage.setImageResource(2130837611);
          this.mIsFlashlightOffByScene = false;
          continue;
        }
        if (paramString.equals("candlelight"))
        {
          this.m_sceneImage.setImageResource(2130837576);
          this.mIsFlashlightOffByScene = false;
          continue;
        }
        if (!paramString.equals("night"))
          continue;
        this.m_sceneImage.setImageResource(2130837592);
        this.mIsFlashlightOffByScene = true;
      }
      label352: continue;
      label354: this.m_sceneContainer.setVisibility(4);
    }
  }

  private class MainHandler extends Handler
  {
    private MainHandler()
    {
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      case 6:
      case 9:
      case 14:
      case 19:
      case 38:
      case 39:
      case 48:
      case 49:
      case 57:
      case 58:
      case 81:
      case 86:
      case 87:
      case 88:
      case 89:
      case 90:
      case 91:
      case 92:
      case 93:
      case 94:
      case 95:
      case 96:
      case 97:
      case 98:
      case 99:
      default:
      case 1:
      case 2:
      case 3:
      case 82:
      case 4:
      case 65:
      case 64:
      case 5:
      case 7:
      case 8:
      case 10:
      case 11:
      case 12:
      case 13:
      case 15:
      case 16:
      case 17:
      case 18:
      case 20:
      case 22:
      case 59:
      case 100:
      case 101:
      case 60:
      case 21:
      case 23:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      case 32:
      case 33:
      case 53:
      case 68:
      case 54:
      case 34:
      case 35:
      case 36:
      case 37:
      case 80:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 50:
      case 51:
      case 52:
      case 56:
      case 55:
      case 61:
      case 62:
      case 63:
      case 66:
      case 71:
      case 67:
      case 69:
      case 70:
      case 72:
      case 73:
      case 79:
      case 74:
      case 75:
      case 85:
      case 76:
      case 77:
      case 78:
      case 83:
      case 102:
      case 84:
      case 103:
      case 104:
      }
      while (true)
      {
        return;
        if (HTCCamera.this.mZoomTouch != null)
          continue;
        int i14 = paramMessage.arg1;
        int i15 = paramMessage.arg2;
        int i16 = i15 - i14;
        (i16 % 5);
        HTCCamera.access$402(HTCCamera.this, i16 / 5);
        HTCCamera.access$302(HTCCamera.this, new ZoomTouch());
        HTCCamera.this.mZoomTouch.initZoomTouch(HTCCamera.this.getWindowManager().getDefaultDisplay().getWidth(), HTCCamera.this.getWindowManager().getDefaultDisplay().getHeight(), i14, i15);
        continue;
        if (!HTCCamera.this.mIsUIReady)
        {
          LOG.V("HTCCamera", "UIHandler Message - SHOW_RECORDING_INDICATOR end - mIsUIReady = false");
          continue;
        }
        if (HTCCamera.this.mRecording_Shining == true)
        {
          if (!DisplayDevice.isDoubleShot())
            HTCCamera.this.mCapture_icon.setImageResource(HTCCamera.this.mCapture_rest);
          while (true)
          {
            HTCCamera.access$602(HTCCamera.this, false);
            MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 2, 1000L);
            break;
            HTCCamera.this.mCapture_btn_ds.setBackgroundResource(2130837857);
          }
        }
        if (!DisplayDevice.isDoubleShot())
          HTCCamera.this.mCapture_icon.setImageDrawable(HTCCamera.this.mCapture_press);
        while (true)
        {
          HTCCamera.access$602(HTCCamera.this, true);
          break;
          HTCCamera.this.mCapture_btn_ds.setBackgroundResource(2130837856);
        }
        if (!HTCCamera.this.mIsUIReady)
        {
          LOG.V("HTCCamera", "UIHandler Message - START_RECORDING_INDICATOR end - mIsUIReady = false");
          continue;
        }
        if ((HTCCamera.this.mCameraThread != null) && (!HTCCamera.this.mCameraThread.getRecorderStatus()))
        {
          LOG.V("HTCCamera", "UIHandler Message - START_RECORDING_INDICATOR end - mRecording = false");
          continue;
        }
        label867: long l3;
        label1027: label1180: String str7;
        label1204: int i13;
        if (HTCCamera.this.mRotateLayout_Recording == null)
        {
          if (!DisplayDevice.isDoubleShot())
          {
            View localView3 = ((ViewStub)HTCCamera.this.findViewById(2131230805)).inflate();
            HTCCamera.access$902(HTCCamera.this, (RotateRelativeLayout)localView3.findViewById(2131231042));
            HTCCamera.access$1002(HTCCamera.this, (TextView)localView3.findViewById(2131231043));
            HTCCamera.access$1102(HTCCamera.this, (TextView)localView3.findViewById(2131231044));
            if ((!DisplayDevice.supportCamcorderRotate()) || (MovieModeHandler.getMovieModeHandler().IsLockMMSVideoInLandscape(HTCCamera.this)))
              HTCCamera.this.mRotateLayout_Recording.setOrientation(1);
            HTCCamera.this.updateRecordingTimerOrientation();
          }
        }
        else
        {
          if (HTCCamera.this.mRotateLayout_Recording.getVisibility() != 4)
            break label1414;
          HTCCamera.this.mRecording_Time.setTextColor(HTCCamera.this.getResources().getColor(2131296258));
          HTCCamera.access$602(HTCCamera.this, false);
          if ((DisplayDevice.supportCamcorderRotate()) && (!MovieModeHandler.getMovieModeHandler().IsLockMMSVideoInLandscape(HTCCamera.this)))
            HTCCamera.this.mRotateLayout_Recording.setOrientation(OrientationConfig.getUIOrientation());
          HTCCamera.this.mRotateLayout_Recording.setVisibility(0);
          if (!DisplayDevice.isDoubleShot())
          {
            HTCCamera.this.mRecording_Hour.setVisibility(4);
            HTCCamera.this.showTimerBase(false);
          }
          HTCCamera.access$1402(HTCCamera.this, System.currentTimeMillis());
          HTCCamera.access$1502(HTCCamera.this, 1000L);
          HTCCamera.access$1602(HTCCamera.this, 0L);
          MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 2, 1000L);
          if (HTCCamera.this.mRecord_sec == 1L)
          {
            LOG.W("HTCCamera", "UnBlock Capture UI - start recorder, unblock time = " + HTCCamera.this.mRecord_sec);
            HTCCamera.access$1702(false);
            HTCCamera.this.m_EventManager.raiseEvent("CaptureUI.Unblock");
            if (DisplayDevice.isDoubleShot())
              HTCCamera.this.mCapture_btn_ds.setEnabled(true);
            if (HTCCamera.this.mRotateLayout_SwitchBtn != null)
              HTCCamera.this.mRotateLayout_SwitchBtn.setVisibility(4);
            HTCCamera.this.openSecondLayout(true);
          }
          MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 36);
          if (HTCCamera.this.mCameraThread == null)
            break label1524;
          l3 = HTCCamera.this.mRecordLimitCheck.checkTimeOut_UI(HTCCamera.this.mCameraThread.getRecordingTime());
          if (DisplayDevice.isDoubleShot())
            break label1546;
          str7 = HTCCamera.this.getTimeString(l3, HTCCamera.this.mRecording_Hour);
          if ((HTCCamera.this.mRecordLimitCheck.getMaxSeconds() != -1) && (l3 == 10L))
            HTCCamera.this.mRecording_Time.setTextColor(HTCCamera.this.getResources().getColor(2131296262));
          HTCCamera.this.mRecording_Time.setText(str7);
          if (l3 > 0L)
            break label1560;
          i13 = 1;
          label1275: if ((HTCCamera.this.mRecord_sec != 0L) && (i13 != 0))
            break label1566;
          MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 3, HTCCamera.this.mUpdateCountDuration);
        }
        while (true)
        {
          if (!MovieModeHandler.getMovieModeHandler().IsLockMMSVideoInLandscape(HTCCamera.this))
            break label1709;
          HTCCamera.this.mRotateLayout_Recording.setOrientation(1);
          break;
          View localView2 = ((ViewStub)HTCCamera.this.findViewById(2131230791)).inflate();
          HTCCamera.access$902(HTCCamera.this, (RotateRelativeLayout)localView2.findViewById(2131231045));
          HTCCamera.access$1002(HTCCamera.this, (TextView)localView2.findViewById(2131231043));
          if (DisplayDevice.supportCamcorderRotate())
            break label867;
          HTCCamera.this.mRotateLayout_Recording.setOrientation(1);
          break label867;
          label1414: HTCCamera.access$1608(HTCCamera.this);
          long l2 = System.currentTimeMillis() - HTCCamera.this.mStartCountTime;
          HTCCamera.access$1502(HTCCamera.this, 1000L);
          HTCCamera.access$1522(HTCCamera.this, l2 - 1000L * HTCCamera.this.mRecord_sec);
          if (HTCCamera.this.mUpdateCountDuration < 800L)
          {
            HTCCamera.access$1502(HTCCamera.this, 800L);
            break label1027;
          }
          if (HTCCamera.this.mUpdateCountDuration <= 1000L)
            break label1027;
          HTCCamera.access$1502(HTCCamera.this, 1000L);
          break label1027;
          label1524: l3 = HTCCamera.this.mRecordLimitCheck.checkTimeOut_UI(HTCCamera.this.mRecord_sec);
          break label1180;
          label1546: str7 = HTCCamera.this.getTimeString_ds(l3);
          break label1204;
          label1560: i13 = 0;
          break label1275;
          label1566: switch (HTCCamera.this.mRecordLimitCheck.getLimitState())
          {
          default:
            LOG.E("HTCCamera", "should not enter this state !!!!!!!!!!!!!!!!");
            MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 3, HTCCamera.this.mUpdateCountDuration);
            break;
          case 1:
          case 2:
            LOG.W("HTCCamera", "reach file size limit - stop to update recording indicator");
            break;
          case 3:
            MessageHandler.removeMessages(HTCCamera.this.mCameraHandler, 22);
            if (HTCCamera.this.mRecordLimitCheck.getUseTimeOut_API() == true)
            {
              LOG.W("HTCCamera", "ui reach time limit - wait api reach time limit");
              continue;
            }
            LOG.W("HTCCamera", "ui reach time limit - and not use api");
          case 4:
            LOG.W("HTCCamera", "reach time limit to stop recorder");
            HTCCamera.this.triggerRecord();
          }
        }
        label1709: continue;
        LOG.V("HTCCamera", "UIHandler Message - TRIGGER_STOP_RECORD start");
        LOG.W("HTCCamera", "Block Capture UI - Stop Recorder");
        HTCCamera.access$1702(true);
        HTCCamera.this.setPowerWarning(true);
        MessageHandler.sendObtainMessage(HTCCamera.this.mCameraHandler, 10, 0, 0, null);
        MessageHandler.removeMessages(HTCCamera.this.mCameraHandler, 0);
        MessageHandler.sendEmptyMessage(HTCCamera.this.mCameraHandler, 2);
        HTCCamera.this.stopRecordingIndicator();
        HTCCamera.access$2602(false);
        LOG.V("HTCCamera", "UIHandler Message - TRIGGER_STOP_RECORD end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - STOP_RECORDING_INDICATOR start");
        if (!HTCCamera.this.mIsUIReady)
        {
          LOG.V("HTCCamera", "UIHandler Message - STOP_RECORDING_INDICATOR end - mIsUIReady = false");
          continue;
        }
        HTCCamera.this.stopRecordingIndicator();
        LOG.V("HTCCamera", "UIHandler Message - STOP_RECORDING_INDICATOR end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - STOP_RECORDING_FINISH start");
        if (!HTCCamera.this.mIsUIReady)
        {
          LOG.V("HTCCamera", "UIHandler Message - STOP_RECORDING_FINISH end - mIsUIReady = false");
          continue;
        }
        if ((HTCCamera.this.mCameraThread != null) && (!HTCCamera.this.mCameraThread.getRecorderStatus()))
        {
          MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 36);
          MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 36, HTCCamera.this.SCREEN_DELAY);
          if ((HTCCamera.this.isRequestMode(IntentManager.RequestMode.Main) == true) || (HTCCamera.this.isRequestName(IntentManager.RequestName.Album) == true))
          {
            ViewUtil.enableMainButton(HTCCamera.this.mMode_icon, HTCCamera.this.mMode_btn, HTCCamera.this.mMode_text_ds);
            ViewUtil.enableMainButton(HTCCamera.this.mPhoto_icon, HTCCamera.this.mPhoto_btn);
          }
          if (HTCCamera.this.mSwitchBtn_icon != null)
            ViewUtil.enableMainButton(HTCCamera.this.mSwitchBtn_icon, HTCCamera.this.mSwitch_btn, HTCCamera.this.mSwitch_text_ds);
          ViewUtil.enableMainButton(HTCCamera.this.mEffect_icon, HTCCamera.this.mEffect_btn);
          if (HTCCamera.this.needsActionScreen())
          {
            HTCCamera.this.closeCaptureUI();
            if (HTCCamera.this.mCapture_combine_ds != null)
              HTCCamera.this.mCapture_combine_ds.setVisibility(4);
          }
          HTCCamera.this.showActionScreen();
        }
        LOG.V("HTCCamera", "UIHandler Message - STOP_RECORDING_FINISH end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - SUSPEND_ON_RECORDING start");
        if ((HTCCamera.this.isRequestMode(IntentManager.RequestMode.Main) == true) || (HTCCamera.this.isRequestName(IntentManager.RequestName.Album) == true))
        {
          ViewUtil.enableMainButton(HTCCamera.this.mMode_icon, HTCCamera.this.mMode_btn, HTCCamera.this.mMode_text_ds);
          ViewUtil.enableMainButton(HTCCamera.this.mPhoto_icon, HTCCamera.this.mPhoto_btn);
        }
        ViewUtil.enableMainButton(HTCCamera.this.mEffect_icon, HTCCamera.this.mEffect_btn);
        if (HTCCamera.this.mRotateLayout_SwitchBtn != null)
          HTCCamera.this.mRotateLayout_SwitchBtn.setVisibility(0);
        LOG.V("HTCCamera", "UIHandler Message - SUSPEND_ON_RECORDING end");
        continue;
        if (!HTCCamera.this.mIsUIReady)
        {
          LOG.I("HTCCamera", "UIHandler Message - COUNT_DOWN_SELFTIMER end - mIsUIReady = false");
          continue;
        }
        if (((CameraThread.Storage_Status == 1) || (CameraThread.Storage_Status == 2)) && ((!DisplayDevice.contactsNoStorage()) || (!HTCCamera.this.isRequestName(IntentManager.RequestName.Contacts))))
        {
          LOG.W("HTCCamera", "UIHandler Message - COUNT_DOWN_SELFTIMER end - storage error");
          continue;
        }
        int i11 = paramMessage.arg1;
        if (HTCCamera.this.mtvSelfTimer == null)
        {
          View localView1 = ((ViewStub)HTCCamera.this.findViewById(2131230803)).inflate();
          HTCCamera.access$3902(HTCCamera.this, (TextView)localView1.findViewById(2131231104));
        }
        if ((i11 > 0) && (HTCCamera.this.isPanelReady()) && (!HTCCamera.this.mPanel.isOpened()))
        {
          if ((HTCCamera.this.mCameraThread != null) && (!HTCCamera.this.mCameraThread.canTakePicture()))
          {
            HTCCamera.this.closeSelfTimer();
            LOG.W("HTCCamera", "UIHandler Message - COUNT_DOWN_SELFTIMER end - canTakePicture() = false");
            continue;
          }
          int i12 = HTCCameraAdvanceSetting.getSelfTimer(HTCCamera.this);
          if (i12 == i11)
          {
            HTCCamera.access$1402(HTCCamera.this, System.currentTimeMillis());
            HTCCamera.access$1502(HTCCamera.this, 1000L);
          }
          while (true)
          {
            HTCCamera.this.mtvSelfTimer.setVisibility(0);
            TextView localTextView = HTCCamera.this.mtvSelfTimer;
            Integer localInteger = new Integer(i11);
            localTextView.setText(localInteger.toString());
            HTCCamera.this.showTimerBase(false);
            MessageHandler.sendObtainMessageDelayed(HTCCamera.this.mUIHandler, 5, i11 - 1, 0, null, HTCCamera.this.mUpdateCountDuration);
            HTCCamera.access$4102(true);
            if ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.hasShutterSound()))
            {
              LOG.V("HTCCamera", "Play Countdown Sound");
              HTCCamera.this.PlaySound(2131099652);
            }
            if ((DisplayDevice.isDoubleShot()) && (HTCCamera.this.mCapture_btn_ds != null))
              HTCCamera.this.mCapture_btn_ds.setEnabled(true);
            LOG.W("HTCCamera", "UnBlock Capture UI - self timer countdown");
            HTCCamera.access$1702(false);
            break;
            long l1 = System.currentTimeMillis() - HTCCamera.this.mStartCountTime;
            HTCCamera.access$1502(HTCCamera.this, 1000L);
            HTCCamera.access$1522(HTCCamera.this, l1 - 1000 * (i12 - i11));
            if (HTCCamera.this.mUpdateCountDuration < 800L)
            {
              HTCCamera.access$1502(HTCCamera.this, 800L);
              continue;
            }
            if (HTCCamera.this.mUpdateCountDuration <= 1000L)
              continue;
            HTCCamera.access$1502(HTCCamera.this, 1000L);
          }
        }
        LOG.V("HTCCamera", "StartSelfTimer: Time Out");
        LOG.W("HTCCamera", "Block Capture UI - self timer timeout, take picture");
        HTCCamera.access$1702(true);
        HTCCamera.this.mtvSelfTimer.setVisibility(4);
        HTCCamera.this.hideTimerBase(false);
        if (HTCCamera.this.mMainBar != null)
        {
          HTCCamera.this.setPanelVisible(false);
          HTCCamera.this.mMainBar.setVisibility(4);
        }
        if ((DisplayDevice.isDoubleShot()) && (HTCCamera.this.mCapture_combine_ds != null))
          HTCCamera.this.mCapture_combine_ds.setVisibility(4);
        HTCCamera.this.enableMainBarItems(true);
        HTCCamera.this.showMainBarItems(true, false);
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 35);
        if (HTCCamera.this.mFaceDetection != null)
        {
          HTCCamera.this.mFaceDetection.stopCheckLoop();
          HTCCamera.this.mFaceDetection.stopFaceDetection();
        }
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 63);
        LOG.W("HTCCamera", "Freeze UI - self timer timeout, take picture");
        HTCCamera.this.lockOrientation();
        HTCCamera.access$4702(HTCCamera.this, false);
        MessageHandler.sendObtainMessage(HTCCamera.this.mCameraHandler, 7, 0, 0, null);
        continue;
        LOG.V("HTCCamera", "UIHandler Message - RESET_SURFACEVIEW_LAYOUT start");
        int i9 = paramMessage.arg1;
        int i10 = paramMessage.arg2;
        HTCCamera.this.reset_surface_view(i9, i10);
        HTCCamera.this.reset_layout_from_surface(HTCCamera.this.mSurfaceWidth, HTCCamera.this.mSurfaceHeight);
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 8);
        MessageHandler.sendEmptyMessage(HTCCamera.this.mUIHandler, 8);
        LOG.V("HTCCamera", "UIHandler Message - RESET_SURFACEVIEW_LAYOUT end");
        continue;
        HTCCamera.this.resetGridView();
        continue;
        if (DisplayDevice.isDoubleShot())
          continue;
        HTCCamera.this.updateZoomBarVisible();
        continue;
        if (HTCCamera.this.mCameraThread == null)
          continue;
        HTCCamera.this.changeIndicatorLayout(HTCCamera.this.mCameraThread.mMode, false);
        continue;
        if (HTCCamera.this.mCameraThread == null)
          continue;
        HTCCamera.this.updateModeIcon(HTCCamera.this.mCameraThread.mMode);
        HTCCamera.this.updateCaptureIcon(HTCCamera.this.mCameraThread.mMode);
        HTCCamera.this.updateSwitchIcon(HTCCamera.this.mCameraThread.mMode);
        if (!DisplayDevice.isDoubleShot())
          continue;
        HTCCamera.this.enableSceneIcon(HTCCamera.this.mCameraThread.mMode, false);
        continue;
        LOG.V("HTCCamera", "UIHandler Message - PROGRESS_STATUS start");
        if (paramMessage.arg1 == 1)
        {
          HTCCamera.access$5802(HTCCamera.this, new ProgressDialog(HTCCamera.this));
          HTCCamera.this.mProgressDialog.setMessage("Please Wait");
          HTCCamera.this.mProgressDialog.setIndeterminate(true);
          HTCCamera.this.mProgressDialog.setCancelable(true);
          HTCCamera.this.mProgressDialog.show();
        }
        while (true)
        {
          LOG.V("HTCCamera", "UIHandler Message - PROGRESS_STATUS end");
          break;
          if (HTCCamera.this.mProgressDialog == null)
            continue;
          HTCCamera.this.mProgressDialog.dismiss();
          HTCCamera.access$5802(HTCCamera.this, null);
        }
        LOG.V("HTCCamera", "UIHandler Message - OPEN_CAPTURE_UI start");
        HTCCamera.this.openCaptureUI();
        LOG.V("HTCCamera", "UIHandler Message - OPEN_CAPTURE_UI end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - CLOSE_CAPTURE_UI start");
        HTCCamera.this.closeCaptureUI();
        LOG.V("HTCCamera", "UIHandler Message - CLOSE_CAPTURE_UI end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - START_ALBUM_CAMERAstart");
        if (HTCCamera.this.isRequestName(IntentManager.RequestName.Album) == true)
        {
          LOG.V("HTCCamera", "End - Return to album after taking picture");
          Intent localIntent3 = new Intent("FROM_CAMERA");
          localIntent3.putExtra("preview_mode", "capture");
          HTCCamera.this.setResult(-1, localIntent3);
          HTCCamera.this.finish();
        }
        while (true)
        {
          LOG.V("HTCCamera", "UIHandler Message - START_ALBUM_CAMERA end");
          break;
          LOG.V("HTCCamera", "Start - Go to album after taking picture");
          Intent localIntent2 = new Intent("FROM_CAMERA");
          localIntent2.setClassName("com.htc.album", "com.htc.album.MainActivity");
          localIntent2.putExtra("preview_mode", "capture");
          localIntent2.putExtra("review_duration", -1);
          HTCCamera.this.startActivity(localIntent2);
        }
        LOG.V("HTCCamera", "UIHandler Message - Play video after recording video start");
        Intent localIntent1 = new Intent("android.intent.action.VIEW", CameraThread.getLastContentUri());
        int i8 = 1;
        try
        {
          localIntent1.setClassName("com.htc.video", "com.htc.video.ViewVideo");
          localIntent1.putExtra("landscape", true);
          HTCCamera.this.startActivity(localIntent1);
          if (i8 != 0);
        }
        catch (Exception localException2)
        {
          try
          {
            localIntent1.setClassName("com.htc.album", "com.htc.album.TabPluginDevice.ViewVideo");
            localIntent1.putExtra("landscape", true);
            HTCCamera.this.startActivity(localIntent1);
            LOG.V("HTCCamera", "UIHandler Message - Play video after recording video end");
            continue;
            localException2 = localException2;
            i8 = 0;
            LOG.E("HTCCamera", "com.htc.video - play video fail", localException2);
          }
          catch (Exception localException3)
          {
            while (true)
              LOG.E("HTCCamera", "com.htc.album - play video fail", localException3);
          }
        }
        int i7 = paramMessage.arg1;
        HTCCamera.this.PlaySound(i7);
        continue;
        LOG.V("HTCCamera", "UIHandler Message - PANEL_FADEOUT start");
        HTCCamera.this.togglePanelState();
        LOG.V("HTCCamera", "UIHandler Message - PANEL_FADEOUT end");
        continue;
        if ((HTCCamera.this.mEffectPanel == null) || (!HTCCamera.this.mEffectPanel.isOpen()))
          continue;
        HTCCamera.this.mGpuEffectContr.openEffectMenu(false, true);
        continue;
        HTCCamera.this.closeAllExtensionMenus();
        continue;
        HTCCamera.this.rotateOnScreenCameraSwitchButton(paramMessage.arg1, true);
        continue;
        HTCCamera.this.hideEffectControl();
        continue;
        LOG.I("HTCCamera", "UIHandler Message - PLAY_FOCUS_SOUND start");
        if ((!HTCCamera.this.mCanShowFocusView) || (HTCCamera.this.bCancelFocus == true))
          continue;
        int i6 = paramMessage.arg1;
        HTCCamera.this.PlaySound(i6);
        LOG.I("HTCCamera", "UIHandler Message - PLAY_FOCUS_SOUND end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - ZOOMBAR_FADEOUT start");
        HTCCamera.this.hideZoomBar(true);
        LOG.V("HTCCamera", "UIHandler Message - ZOOMBAR_FADEOUT end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - SHOW_FOCUSING start");
        if ((!HTCCamera.this.mCanShowFocusView) || (HTCCamera.this.bCancelFocus == true) || (HTCCamera.this.mFocusingState == 0))
          continue;
        if (HTCCamera.mFocusMode != 2)
        {
          if (DisplayDevice.isDoubleShot())
            break label3897;
          HTCCamera.this.mivFocusingView.setVisibility(0);
        }
        while (true)
        {
          LOG.V("HTCCamera", "UIHandler Message - SHOW_FOCUSING end");
          break;
          label3897: ((CameraFocusWidget)HTCCamera.this.mFocusAnimation).setStatus(0, HTCCamera.mFocus_Screen_X, HTCCamera.mFocus_Screen_Y);
        }
        LOG.V("HTCCamera", "UIHandler Message - SHOW_FOCUS_SUCCESS start");
        if ((!HTCCamera.this.mCanShowFocusView) || (HTCCamera.this.bCancelFocus == true) || (HTCCamera.this.mFocusingState == 0))
          continue;
        if (HTCCamera.mFocusMode != 2)
          if (!DisplayDevice.isDoubleShot())
          {
            HTCCamera.this.mivFocusingView.setVisibility(4);
            HTCCamera.this.mivFocusedView.setImageResource(2130837832);
            HTCCamera.this.mivFocusedView.setVisibility(0);
          }
        while (true)
        {
          LOG.V("HTCCamera", "UIHandler Message - SHOW_FOCUS_SUCCESS end");
          break;
          ((CameraFocusWidget)HTCCamera.this.mFocusAnimation).setStatus(2, 0.0F, 0.0F);
          continue;
          if ((HTCCamera.this.mFaceDetection == null) || (HTCCamera.this.isPanelReady() != true))
            continue;
          MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 54);
          HTCCamera.this.mFaceDetection.setFocusFace();
        }
        LOG.V("HTCCamera", "UIHandler Message - SHOW_FOCUS_FAIL start");
        if ((!HTCCamera.this.mCanShowFocusView) || (HTCCamera.this.bCancelFocus == true) || (HTCCamera.this.mFocusingState == 0))
          continue;
        if (HTCCamera.mFocusMode != 2)
          if (!DisplayDevice.isDoubleShot())
          {
            HTCCamera.this.mivFocusingView.setVisibility(4);
            HTCCamera.this.mivFocusedView.setImageResource(2130837833);
            HTCCamera.this.mivFocusedView.setVisibility(0);
          }
        while (true)
        {
          LOG.V("HTCCamera", "UIHandler Message - SHOW_FOCUS_FAIL end");
          break;
          ((CameraFocusWidget)HTCCamera.this.mFocusAnimation).setStatus(2, 0.0F, 0.0F);
          continue;
          if ((HTCCamera.this.mFaceDetection == null) || (HTCCamera.this.isPanelReady() != true))
            continue;
          MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 54);
          HTCCamera.this.mFaceDetection.setFocusFace();
        }
        LOG.V("HTCCamera", "UIHandler Message - SHOW_FOCUS_END start");
        if ((HTCCamera.mFocusMode != 2) && (!DisplayDevice.isDoubleShot()))
          HTCCamera.this.mivFocusedView.setVisibility(4);
        LOG.V("HTCCamera", "UIHandler Message - SHOW_FOCUS_END end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - FINISH_FOCUS start");
        if (!HTCCamera.this.mIsUIReady)
        {
          LOG.V("HTCCamera", "UIHandler Message - FINISH_FOCUS end - mIsUIReady = false");
          continue;
        }
        if (HTCCamera.this.bCancelFocus == true)
        {
          LOG.V("HTCCamera", "UIHandler Message - FINISH_FOCUS end - bCancelFocus = true");
          continue;
        }
        if (HTCCamera.this.mFocusingState == 0)
        {
          LOG.W("HTCCamera", "Enter this state only when interrupt focus !!!");
          LOG.V("HTCCamera", "UIHandler Message - FINISH_FOCUS end - mFocusingState = NO_FOCUSING");
          continue;
        }
        if (HTCCamera.this.mSensorHandler != null)
          HTCCamera.this.mSensorHandler.setifWaitFocus(false);
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 33);
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 31);
        MessageHandler.removeMessages(HTCCamera.this.mCameraHandler, 31);
        if (HTCCamera.this.mFaceDetection != null)
          HTCCamera.this.mFaceDetection.setStateWithFocus();
        switch (HTCCamera.this.mFocusingState)
        {
        default:
          LOG.E("HTCCamera", "unknown focus state !!!");
        case 1:
        case 2:
        case 4:
        case 3:
        }
        while (true)
        {
          LOG.W("HTCCamera", "Finish Focus - mFocusingState = NO_FOCUSING");
          HTCCamera.this.mFocusingState = 0;
          HTCCamera.bFocusFromPress = false;
          HTCCamera.this.m_EventManager.raiseEvent("Focus.Finished");
          LOG.V("HTCCamera", "UIHandler Message - FINISH_FOCUS end");
          break;
          HTCCamera.this.checkFaceDetection();
          LOG.W("HTCCamera", "UnBlock Capture UI - take focus end");
          HTCCamera.access$1702(false);
          continue;
          if ((HTCCamera.this.mCameraThread == null) || (HTCCamera.this.mCameraThread.mMode != 0))
            continue;
          LOG.W("HTCCamera", "take focus end, and then take picture");
          MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 63);
          LOG.W("HTCCamera", "Freeze UI - take focus end, and then take picture");
          HTCCamera.this.lockOrientation();
          HTCCamera.access$4702(HTCCamera.this, false);
          MessageHandler.sendObtainMessage(HTCCamera.this.mCameraHandler, 7, 0, 0, null);
          continue;
          if ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.mMode == 0))
          {
            LOG.W("HTCCamera", "UnBlock Capture UI - switch to video mode after focusing");
            HTCCamera.access$1702(false);
            if ((DisplayDevice.isDoubleShot()) && (HTCCamera.this.mFocusAnimation != null))
              ((CameraFocusWidget)HTCCamera.this.mFocusAnimation).setStatus(3, 0.0F, 0.0F);
            HTCCamera.this.m_NeedTriggerRecord = true;
            HTCCamera.this.switchMode(1);
            continue;
          }
          LOG.W("HTCCamera", "UnBlock Capture UI - record after focusing");
          HTCCamera.access$1702(false);
          HTCCamera.this.triggerRecord();
          continue;
          if ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.mMode == 0))
          {
            LOG.W("HTCCamera", "UnBlock Capture UI - take picture after focusing");
            HTCCamera.access$1702(false);
            if ((DisplayDevice.isDoubleShot()) && (HTCCamera.this.mFocusAnimation != null))
              ((CameraFocusWidget)HTCCamera.this.mFocusAnimation).setStatus(3, 0.0F, 0.0F);
            HTCCamera.this.triggerTakePicture();
            continue;
          }
          LOG.W("HTCCamera", "UnBlock Capture UI - record after focusing");
          HTCCamera.access$1702(false);
          HTCCamera.this.triggerRecord();
        }
        LOG.V("HTCCamera", "UIHandler Message - CANCEL_FOCUS_END start");
        if (!HTCCamera.this.mIsUIReady)
        {
          LOG.V("HTCCamera", "UIHandler Message - CANCEL_FOCUS_END end - mIsUIReady = false");
          continue;
        }
        HTCCamera.access$6402(HTCCamera.this, false);
        if (!DisplayDevice.isDoubleShot())
          if ((HTCCamera.this.mivFocusingView != null) && (HTCCamera.this.mivFocusedView != null))
          {
            HTCCamera.this.mivFocusingView.setVisibility(4);
            HTCCamera.this.mivFocusedView.setVisibility(4);
          }
        while (true)
        {
          LOG.V("HTCCamera", "UIHandler Message - CANCEL_FOCUS_END end");
          break;
          if (HTCCamera.this.mFocusAnimation == null)
            continue;
        }
        LOG.V("HTCCamera", "UIHandler Message - ENABLE_SENSOR_FOCUS start");
        HTCCamera.access$7302(HTCCamera.this, true);
        LOG.V("HTCCamera", "UIHandler Message - ENABLE_SENSOR_FOCUS end");
        continue;
        if (HTCCamera.this.mSensorHandler == null)
          continue;
        HTCCamera.this.mSensorHandler.restartCheckSensorFocus();
        continue;
        if (HTCCamera.this.mSensorHandler == null)
          continue;
        LOG.W("HTCCamera", ">>>>>>>>>>>>>>>>>>>>> sensor stable, check scene file !!");
        HTCCamera.this.mSensorHandler.enterSceneChangeState();
        HTCCamera.this.mSensorHandler.checkSceneChange();
        continue;
        LOG.V("HTCCamera", "UIHandler Message - START_SENSOR_FOCUS start");
        if (HTCCamera.this.mCameraThread == null)
          continue;
        if (!HTCCamera.this.canSensorFocus())
        {
          if (HTCCamera.this.mSensorHandler == null)
            continue;
          HTCCamera.this.mSensorHandler.restartCheckSensorFocus();
          continue;
        }
        if (HTCCamera.this.mCameraThread.getRecorderStatus())
        {
          MessageHandler.removeMessages(HTCCamera.this.mCameraHandler, 46);
          MessageHandler.sendEmptyMessage(HTCCamera.this.mCameraHandler, 46);
          continue;
        }
        if ((HTCCamera.this.mFaceDetection != null) && (HTCCamera.this.mFaceDetection.isStartDetection()))
        {
          if (!HTCCamera.this.mFaceDetection.isCheckLoop())
          {
            HTCCamera.mFocusMode = 3;
            HTCCamera.this.mFaceDetection.startCheckLoop();
            continue;
          }
          if (HTCCamera.this.getFaceNumber() > 0)
          {
            if (HTCCamera.this.mFaceDetection.getFaceState() != FaceDetection.STATE.STABLE_NO_FACE)
            {
              HTCCamera.mFocusMode = 3;
              continue;
            }
          }
          else if ((HTCCamera.this.mFaceDetection.getFaceState() != FaceDetection.STATE.NO_FACE) && (HTCCamera.this.mFaceDetection.getFaceState() != FaceDetection.STATE.STABLE_NO_FACE))
          {
            HTCCamera.mFocusMode = 3;
            continue;
          }
        }
        HTCCamera.this.handleSensorFocus();
        LOG.V("HTCCamera", "UIHandler Message - START_SENSOR_FOCUS end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - START_FACE_FOCUS start");
        if ((HTCCamera.this.mCameraThread == null) || (HTCCamera.this.mFaceDetection == null))
          continue;
        if ((DisplayDevice.supportSecondCamera()) && (HTCCamera.this.mCameraThread.is2ndCamera()))
          if (!DisplayDevice.isDoubleShot())
          {
            if (HTCCamera.this.getFaceNumber() <= 0)
              LOG.E("HTCCamera", "enter this state - must enable auto-capture !!!");
            if ((HTCCamera.mBlockCaptureUI == true) || ((HTCCamera.this.mtvSelfTimer != null) && (HTCCamera.this.mtvSelfTimer.getVisibility() == 0)) || ((HTCCamera.this.mPanel != null) && (HTCCamera.this.mPanel.getVisibility() == 0) && ((HTCCamera.this.mPanel.isOpened() == true) || (HTCCamera.this.mPanel.isMoving() == true))))
            {
              HTCCamera.this.mFaceDetection.startCheckLoop();
              continue;
            }
            HTCCamera.this.mFaceDetection.setStateWithFocus();
            HTCCamera.this.onTouchCapture_Camera();
          }
        while (true)
        {
          LOG.V("HTCCamera", "UIHandler Message - START_FACE_FOCUS end");
          break;
          if (!HTCCamera.this.canSensorFocus())
          {
            HTCCamera.this.mFaceDetection.startCheckLoop();
            break;
          }
          if (HTCCamera.this.getFaceNumber() > 0)
          {
            MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 69);
            HTCCamera.access$7602(true);
            HTCCamera.this.stopSoundPool();
            HTCCamera.this.handleFaceFocus((FaceDetection.Face)paramMessage.obj);
            HTCCamera.this.onTouchCapture_Camera();
            continue;
          }
          HTCCamera.this.handleFaceFocus((FaceDetection.Face)paramMessage.obj);
        }
        LOG.V("HTCCamera", "UIHandler Message - START_SELF_PORTRAIT start");
        if (!HTCCamera.this.mIsUIReady)
        {
          LOG.V("HTCCamera", "UIHandler Message - START_SELF_PORTRAIT end - mIsUIReady = false");
          continue;
        }
        HTCCamera.access$7602(true);
        HTCCamera.this.stopSoundPool();
        HTCCamera.this.handleFaceFocus((FaceDetection.Face)paramMessage.obj);
        HTCCamera.this.onTouchCapture_Camera();
        LOG.V("HTCCamera", "UIHandler Message - START_SELF_PORTRAIT end");
        continue;
        if ((HTCCamera.this.mCameraThread == null) || (HTCCamera.this.mFaceDetection == null))
          continue;
        if (!HTCCamera.mBlockCaptureUI)
        {
          HTCCamera.this.mFaceDetection.clearFocusFace();
          continue;
        }
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 54);
        MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 54, 100L);
        continue;
        LOG.V("HTCCamera", "UIHandler Message - PREPARE_FOCUS_BEFORE_CAPTURE start");
        if (!HTCCamera.this.mIsUIReady)
        {
          LOG.V("HTCCamera", "UIHandler Message - PREPARE_FOCUS_BEFORE_CAPTURE end - mIsUIReady = false");
          continue;
        }
        if (HTCCamera.this.mFocusingState != 2)
          LOG.E("HTCCamera", "REPARE_FOCUS_BEFORE_CAPTURE - mFocusingState != FOCUSING_TAP_CAPTURE");
        HTCCamera.this.handleTouchFocus(HTCCamera.this.mFocusPos_X, HTCCamera.this.mFocusPos_Y);
        LOG.V("HTCCamera", "UIHandler Message - PREPARE_FOCUS_BEFORE_CAPTURE end");
        continue;
        LOG.I("HTCCamera", "CameraHandler Message - CHECK_FACE_DETECTION start");
        HTCCamera.this.checkFaceDetection();
        LOG.I("HTCCamera", "CameraHandler Message - CHECK_FACE_DETECTION end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - SCREEN_SAVE start");
        HTCCamera.this.deactivate();
        LOG.V("HTCCamera", "UIHandler Message - SCREEN_SAVE end");
        continue;
        String str5 = HTCCamera.this.getString(paramMessage.arg1);
        String str6 = (String)paramMessage.obj;
        if (str6 != null)
        {
          Object[] arrayOfObject3 = new Object[1];
          arrayOfObject3[0] = str6;
          str5 = String.format(str5, arrayOfObject3);
        }
        LOG.W("HTCCamera", "SHOW_TOAST - " + str5);
        HTCCamera.this.mToast.cancel();
        HTCCamera.this.mToast.updateOrientation(OrientationConfig.getUIOrientation());
        HTCCamera.this.mToast.setText(str5);
        HTCCamera.this.mToast.show();
        continue;
        if (HTCCamera.this.isPowerWarning())
          continue;
        String str3 = HTCCamera.this.getString(paramMessage.arg1);
        int i5 = paramMessage.arg2;
        String str4 = (String)paramMessage.obj;
        if (str4 != null)
        {
          Object[] arrayOfObject2 = new Object[1];
          arrayOfObject2[0] = str4;
          str3 = String.format(str3, arrayOfObject2);
        }
        LOG.W("HTCCamera", "SHOW_3D_HINT - " + str3);
        if (HTCCamera.this.mToast == null)
          continue;
        HTCCamera.this.mToast.cancel();
        HTCCamera.this.mToast.updateOrientation(i5);
        HTCCamera.this.mToast.setText(str3);
        HTCCamera.this.mToast.show();
        continue;
        if (HTCCamera.this.mFpsText == null)
          continue;
        int i4 = paramMessage.arg1;
        Object[] arrayOfObject1 = new Object[1];
        arrayOfObject1[0] = Integer.valueOf(i4);
        String str2 = String.format("%d fps", arrayOfObject1);
        HTCCamera.this.mFpsText.setText(str2);
        continue;
        if (HTCCamera.this.mGpsIndicator == null)
          continue;
        int i2 = paramMessage.arg1;
        if (LocationHandler.getLocation() != null)
        {
          HTCCamera.this.mGpsIndicator.setImageLevel(5);
          continue;
        }
        HTCCamera.this.mGpsIndicator.setImageLevel(i2);
        int i3 = i2 + 1;
        if (i3 > 4)
          i3 = 0;
        MessageHandler.sendObtainMessageDelayed(HTCCamera.this.mUIHandler, 41, i3, 0, null, 500L);
        continue;
        LOG.V("HTCCamera", "UIHandler Message - PREVIEW_DUPLICATE_START start");
        if (HTCCamera.this.mCameraThread == null)
        {
          LOG.E("HTCCamera", "mCameraThread == null");
          continue;
        }
        if (HTCCamera.this.mPreviewDuplicate == null)
        {
          ViewStub localViewStub = (ViewStub)HTCCamera.this.findViewById(2131230766);
          HTCCamera.access$8302(HTCCamera.this, (ImageView)localViewStub.inflate().findViewById(2131231038));
        }
        synchronized (HTCCamera.this.mCameraThread.mSyncObject)
        {
          while (true)
          {
            Bitmap localBitmap2 = HTCCamera.this.mCameraThread.mOneShotBitmap;
            if (localBitmap2 == null)
              try
              {
                HTCCamera.this.mCameraThread.mSyncObject.wait();
                LOG.W("HTCCamera", "mOneShotBitmap = null - wait()");
              }
              catch (Exception localException1)
              {
                LOG.E("HTCCamera", "Exception", localException1);
              }
          }
        }
        monitorexit;
        HTCCamera.this.mPreviewDuplicate.clearAnimation();
        AnimationSet localAnimationSet = new AnimationSet(true);
        if (HTCCamera.this.Display_Orientation == 0)
        {
          Matrix localMatrix = new Matrix();
          localMatrix.setRotate(90.0F, DisplayDevice.SCREEN_HEIGHT / 2, DisplayDevice.SCREEN_WIDTH / 2);
          HTCCamera.this.mCameraThread.mOneShotBitmap = Bitmap.createBitmap(HTCCamera.this.mCameraThread.mOneShotBitmap, 0, 0, HTCCamera.this.mCameraThread.mOneShotBitmap.getWidth(), HTCCamera.this.mCameraThread.mOneShotBitmap.getHeight(), localMatrix, true);
        }
        HTCCamera.this.mPreviewDuplicate.setImageBitmap(HTCCamera.this.mCameraThread.mOneShotBitmap);
        localAnimationSet.addAnimation(new ScaleAnimation(1.0F, 0.0F, 1.0F, 0.0F, 1, 0.5F, 1, 0.5F));
        if (HTCCamera.this.Display_Orientation == 0)
        {
          TranslateAnimation localTranslateAnimation1 = new TranslateAnimation(0.0F, -1 * DisplayDevice.SCREEN_HEIGHT / 2, 0.0F, DisplayDevice.SCREEN_WIDTH);
          localAnimationSet.addAnimation(localTranslateAnimation1);
        }
        while (true)
        {
          localAnimationSet.setDuration(1000);
          HTCCamera.this.mPreviewDuplicate.setAnimation(localAnimationSet);
          HTCCamera.this.mPreviewDuplicate.setVisibility(0);
          MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 43, 1000);
          LOG.V("HTCCamera", "UIHandler Message - PREVIEW_DUPLICATE_START end");
          break;
          TranslateAnimation localTranslateAnimation2 = new TranslateAnimation(0.0F, -1 * DisplayDevice.SCREEN_WIDTH / 2, 0.0F, DisplayDevice.SCREEN_HEIGHT);
          localAnimationSet.addAnimation(localTranslateAnimation2);
        }
        LOG.V("HTCCamera", "UIHandler Message - PREVIEW_DUPLICATE_END start");
        if (HTCCamera.this.mPreviewDuplicate != null)
        {
          HTCCamera.this.mPreviewDuplicate.clearAnimation();
          HTCCamera.this.mPreviewDuplicate.setImageBitmap(null);
          HTCCamera.this.mPreviewDuplicate.setVisibility(8);
        }
        if ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.mOneShotBitmap != null))
        {
          HTCCamera.this.mCameraThread.mOneShotBitmap.recycle();
          HTCCamera.this.mCameraThread.mOneShotBitmap = null;
        }
        LOG.V("HTCCamera", "UIHandler Message - PREVIEW_DUPLICATE_END end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - UPDATE_FLASH_FROM_RESTRICTION start");
        if (!HTCCamera.this.mIsUIReady)
        {
          LOG.V("HTCCamera", "UIHandler Message - UPDATE_FLASH_FROM_RESTRICTION end - mIsUIReady = false");
          continue;
        }
        if ((!CameraController.supportFlashLight()) || (HTCCamera.this.mFlashRestriction == null))
          continue;
        if ((HTCCamera.mBlockCaptureUI == true) && (HTCCamera.this.mFocusingState != 1))
        {
          LOG.W("HTCCamera", "UI Block - wait UI unBlock to update flash from restriction");
          MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 44, 100L);
          continue;
        }
        if ((HTCCamera.this.mFlashRestriction.isDisableFlash()) && (!HTCCamera.this.mIsFlashDisabled) && (!HTCCamera.this.mIsFlashlightOffByScene))
        {
          MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 37);
          MessageHandler.sendObtainMessage(HTCCamera.this.mUIHandler, 37, HTCCamera.this.mFlashRestriction.getRestrictionHint(), 0, null);
        }
        HTCCamera.this.updateFlashMode();
        LOG.V("HTCCamera", "UIHandler Message - UPDATE_FLASH_FROM_RESTRICTION end");
        continue;
        LOG.W("HTCCamera", "UIHandler Message - DO_CREATE_RESUME_AFTER_PREVIEW start");
        if (HTCCamera.this.mActivityOnPause == true)
        {
          LOG.W("HTCCamera", "mActivityOnPause == true, UIHandler Message - DO_CREATE_RESUME_AFTER_PREVIEW end");
          continue;
        }
        if (HTCCamera.isKeyguardShow() == true)
        {
          LOG.W("HTCCamera", "mIsKeyguardShow = true, waiting for unlock screen");
          HTCCamera.setWaitKeyguardBeforePreview(true);
          continue;
        }
        HTCCamera.this.initOnCreate_after_preview();
        MessageHandler.sendEmptyMessage(HTCCamera.this.mUIHandler, 46);
        LOG.W("HTCCamera", "UIHandler Message - DO_CREATE_RESUME_AFTER_PREVIEW end");
        continue;
        LOG.W("HTCCamera", "UIHandler Message - DO_CREATE_AFTER_PREVIEW start");
        if (HTCCamera.this.mActivityOnPause == true)
        {
          LOG.W("HTCCamera", "mActivityOnPause == true, UIHandler Message - DO_CREATE_AFTER_PREVIEW end");
          continue;
        }
        HTCCamera.this.doOnCreate_after_preview();
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 63);
        MessageHandler.sendEmptyMessage(HTCCamera.this.mUIHandler, 63);
        HTCCamera.this.openSecondLayout(true);
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 10);
        MessageHandler.sendEmptyMessage(HTCCamera.this.mUIHandler, 10);
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 61);
        MessageHandler.sendEmptyMessage(HTCCamera.this.mUIHandler, 61);
        MessageHandler.sendEmptyMessage(HTCCamera.this.mUIHandler, 47);
        LOG.W("HTCCamera", "UIHandler Message - DO_CREATE_AFTER_PREVIEW end");
        continue;
        LOG.W("HTCCamera", "UIHandler Message - DO_RESUME_AFTER_PREVIEW start");
        if (HTCCamera.this.mActivityOnPause == true)
        {
          LOG.W("HTCCamera", "mActivityOnPause == true, UIHandler Message - DO_RESUME_AFTER_PREVIEW end");
          continue;
        }
        HTCCamera.this.doOnResume_after_preview();
        HTCCamera.access$4702(HTCCamera.this, true);
        if (HTCCamera.this.mWaitResetSettings == true)
        {
          LOG.W("HTCCamera", "mWaitResetSettings = true, reset panel and set mWaitResetSettings to false");
          HTCCamera.this.setPanelVisible(true);
          HTCCamera.access$9102(HTCCamera.this, false);
          if (HTCCamera.this.mMenuHandler != null)
          {
            HTCCamera.this.mMenuHandler.exitMenuHandler();
            HTCCamera.this.mMenuHandler.resetMenuHandler();
          }
          HTCCamera.this.showEffectControl();
        }
        HTCCamera.this.resetAutoCaptureTask();
        HTCCamera.this.updateSelfPortraitHint(OrientationConfig.getUIOrientation());
        HTCCamera.bHoldFocusKey = false;
        HTCCamera.access$1702(false);
        LOG.W("HTCCamera", "UnBlock Capture UI - after start preview");
        HTCCamera.access$502(HTCCamera.this, true);
        if (DisplayDevice.isDoubleShot())
        {
          MessageHandler.removeMessages(HTCCamera.this.mCameraHandler, 43);
          MessageHandler.sendEmptyMessage(HTCCamera.this.mCameraHandler, 43);
        }
        HTCCamera.this.m_EventManager.raiseEvent("CameraActivity.PreviewStarted");
        if (DisplayDevice.supportCamcorderHWButton())
          HTCCamera.this.checkCamcorderButtonRecord();
        LOG.W("HTCCamera", "DO_RESUME_AFTER_PREVIEW end - mIsUIReady = true");
        if ((HTCCamera.this.mCameraThread.mMode == 1) && (MovieModeHandler.getMovieModeHandler().IsLockMMSVideoInLandscape(HTCCamera.this)))
          MessageHandler.sendEmptyMessage(HTCCamera.this.mUIHandler, 104);
        LOG.W("HTCCamera", "UIHandler Message - DO_RESUME_AFTER_PREVIEW end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - UPDATE_THUMBNAIL_BUTTON start");
        Bitmap localBitmap1 = (Bitmap)paramMessage.obj;
        if (HTCCamera.this.mThumbController == null)
        {
          LOG.W("HTCCamera", "UIHandler Message - UPDATE_THUMBNAIL_BUTTON end, mThumbController == null");
          if (localBitmap1 == null)
            continue;
          localBitmap1.recycle();
          continue;
        }
        String str1 = paramMessage.getData().getString("thumb_file_path");
        int i1 = paramMessage.arg1;
        HTCCamera.this.updateThumbnailButton(localBitmap1, str1, i1);
        LOG.V("HTCCamera", "UIHandler Message - UPDATE_THUMBNAIL_BUTTON end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - LOAD_LATEST_THUMBNAIL start");
        Handler localHandler;
        if ((HTCCamera.this.mThumbController != null) && (HTCCamera.this.mCameraThread != null))
        {
          HTCCamera.this.mThumbController.clearButtonImage();
          if (!DisplayDevice.isDoubleShot())
          {
            localHandler = HTCCamera.this.mCameraHandler;
            if (HTCCamera.this.mCameraThread.mMode != 0)
              break label7710;
          }
        }
        label7710: for (int n = 29; ; n = 30)
        {
          MessageHandler.sendEmptyMessage(localHandler, n);
          LOG.V("HTCCamera", "UIHandler Message - LOAD_LATEST_THUMBNAIL end");
          break;
        }
        LOG.V("HTCCamera", "UIHandler Message - UNBLOCK_CAPTURE_UI start");
        HTCCamera.access$1702(false);
        HTCCamera.this.m_EventManager.raiseEvent("CaptureUI.Unblock");
        LOG.V("HTCCamera", "UIHandler Message - UNBLOCK_CAPTURE_UI end");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - ENABLE_GARBAGE_COLLECTION start");
        LOG.V("HTCCamera", "UIHandler Message - ENABLE_GARBAGE_COLLECTION end");
        continue;
        if ((HTCCamera.this.mSecondLayout == null) || (HTCCamera.this.mSecondLayout.getVisibility() != 0) || (HTCCamera.this.mZoomLayout == null) || (HTCCamera.this.mZoomLayout.getVisibility() != 0) || ((HTCCamera.this.mCameraThread != null) && ((HTCCamera.this.mCameraThread.is2ndCamera()) || (HTCCamera.this.getFaceNumber() > 0) || (HTCCamera.this.mIsZoomLocked))) || (HTCCamera.mBlockCaptureUI == true))
          continue;
        int k = paramMessage.arg1;
        int m = paramMessage.arg2;
        if (HTCCamera.this.mZoomBar != null)
          HTCCamera.this.mZoomBar.increasePosition(k);
        if (m != 1)
          continue;
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 55);
        MessageHandler.sendObtainMessageDelayed(HTCCamera.this.mUIHandler, 55, k, 1, null, 150L);
        continue;
        if ((HTCCamera.this.mSecondLayout == null) || (HTCCamera.this.mSecondLayout.getVisibility() != 0) || ((HTCCamera.this.mRemainingLayout != null) && (HTCCamera.this.mRemainingLayout.getVisibility() == 0)))
          continue;
        if ((HTCCamera.this.mPanel != null) && (HTCCamera.this.mPanel.isOpened()))
        {
          MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 61);
          MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 61, 100L);
          continue;
        }
        HTCCamera.this.updateFreeCount();
        if (HTCCamera.this.mRemainingLayout == null)
          continue;
        int j = DisplayDevice.REMAINING_LAYOUT_MARGIN_NO_EFFECT_BAR;
        if ((HTCCamera.this.mGpuEffectContr != null) && (HTCCamera.this.mGpuEffectContr.isEffectBarVisible()))
          j = DisplayDevice.REMAINING_LAYOUT_MARGIN_WITH_EFFECT_BAR;
        ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)HTCCamera.this.mRemainingLayout.getLayoutParams();
        localMarginLayoutParams.setMargins(localMarginLayoutParams.leftMargin, localMarginLayoutParams.topMargin, localMarginLayoutParams.rightMargin, j);
        HTCCamera.this.mRemainingLayout.setLayoutParams(localMarginLayoutParams);
        AlphaAnimation localAlphaAnimation2 = AnimationManager.showAlphaAnimation(HTCCamera.this.mRemainingLayout, 0.0F, 1.0F, 0, 300);
        1 local1 = new Animation.AnimationListener()
        {
          public void onAnimationEnd(Animation paramAnimation)
          {
          }

          public void onAnimationRepeat(Animation paramAnimation)
          {
          }

          public void onAnimationStart(Animation paramAnimation)
          {
            HTCCamera.this.m_EventManager.raiseEvent("RemainingLayout.ShowStart");
          }
        };
        localAlphaAnimation2.setAnimationListener(local1);
        HTCCamera.this.mRemainingLayout.setVisibility(0);
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 62);
        MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 62, 2000L);
        continue;
        if ((HTCCamera.this.mRemainingLayout == null) || (HTCCamera.this.mRemainingLayout.getVisibility() != 0))
          continue;
        AlphaAnimation localAlphaAnimation1 = AnimationManager.showAlphaAnimation(HTCCamera.this.mRemainingLayout, 1.0F, 0.0F, 0, 300);
        2 local2 = new Animation.AnimationListener()
        {
          public void onAnimationEnd(Animation paramAnimation)
          {
            HTCCamera.this.m_EventManager.raiseEvent("RemainingLayout.HideEnd");
          }

          public void onAnimationRepeat(Animation paramAnimation)
          {
          }

          public void onAnimationStart(Animation paramAnimation)
          {
          }
        };
        localAlphaAnimation1.setAnimationListener(local2);
        HTCCamera.this.mRemainingLayout.setVisibility(4);
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 62);
        continue;
        if (!HTCCamera.this.mFreezeUI)
          continue;
        if ((DisplayDevice.supportIconRotate()) && (!HTCCamera.this.isOrientationLockNeeded()))
          if (DisplayDevice.supportCamcorderRotate())
          {
            HTCCamera.this.onRotateChanged(OrientationConfig.mapOrientation_Event2UI(HTCCamera.this.mLastOrientation));
            LOG.W("HTCCamera", "Unfreeze UI !!!");
            HTCCamera.access$10202(HTCCamera.this, false);
          }
        while (true)
        {
          MessageHandler.removeMessages(HTCCamera.this.mCameraHandler, 43);
          MessageHandler.sendEmptyMessage(HTCCamera.this.mCameraHandler, 43);
          break;
          if ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.mMode == 0))
          {
            HTCCamera.this.onRotateChanged(OrientationConfig.mapOrientation_Event2UI(HTCCamera.this.mLastOrientation));
            LOG.W("HTCCamera", "Unfreeze UI !!!");
            HTCCamera.access$10202(HTCCamera.this, false);
            continue;
          }
          HTCCamera.this.lockOrientation(1);
          continue;
          HTCCamera.this.lockOrientation(1);
        }
        LOG.V("HTCCamera", "UIHandler Message - LONG_PRESS_SW_CAPTURE start");
        if (HTCCamera.this.canTriggerFocus())
          HTCCamera.this.startFocusFromLongPressKey();
        HTCCamera.access$10602(HTCCamera.this, true);
        if (!DisplayDevice.isDoubleShot())
        {
          HTCCamera.this.mCapture_icon.setImageResource(HTCCamera.this.mCapture_rest);
          label8541: if ((HTCCamera.this.mCameraThread == null) || (HTCCamera.this.mCameraThread.mMode != 0))
            break label8603;
          HTCCamera.this.onTouchCapture_Camera();
        }
        while (true)
        {
          LOG.V("HTCCamera", "UIHandler Message - LONG_PRESS_SW_CAPTURE end");
          break;
          HTCCamera.this.updateDOTCaptureIcon(false, HTCCamera.this.mCameraThread.mMode);
          break label8541;
          label8603: HTCCamera.this.onTouchCapture_Camcorder();
        }
        if (HTCCamera.this.mGpuEffectContr != null)
        {
          HTCCamera.this.mGpuEffectContr.resume(HTCCamera.this.mCameraThread.mMode);
          continue;
        }
        LOG.V("HTCCamera", "UIHandler Message - mGpuEffectContr is not created now.");
        continue;
        LOG.V("HTCCamera", "UIHandler Message - EFFECT_SWITCH_CAMERA start");
        HTCCamera.this.effectSwitchCamera();
        LOG.V("HTCCamera", "UIHandler Message - EFFECT_SWITCH_CAMERA end");
        continue;
        if ((HTCCamera.this.getFaceNumber() <= 0) || (HTCCamera.this.mFaceDetection == null))
          continue;
        int i = HTCCamera.this.mFaceDetection.getSelfPortraitState();
        HTCCamera.access$10802(-1);
        switch (i)
        {
        default:
          label8744: if (HTCCamera.mCurrentBeepLevel != 200)
            break;
          HTCCamera.access$10908();
        case 1:
        case 2:
        }
        while (HTCCamera.mCurrentBeepLevel != -1)
        {
          HTCCamera.access$11002(HTCCamera.this, System.currentTimeMillis());
          HTCCamera.this.playSoundPool(paramMessage.arg1);
          MessageHandler.sendObtainMessageDelayed(HTCCamera.this.mUIHandler, 69, paramMessage.arg1, paramMessage.arg2, null, HTCCamera.mCurrentBeepLevel);
          break;
          HTCCamera.access$10802(200);
          break label8744;
          HTCCamera.access$10802(-1);
          break label8744;
          HTCCamera.access$10902(0);
        }
        if ((HTCCamera.this.mtvSelfTimer == null) || (HTCCamera.this.mtvSelfTimer.getVisibility() != 0))
          continue;
        HTCCamera.this.closeSelfTimer();
        continue;
        if (paramMessage.arg1 == 1)
          HTCCamera.access$11202(HTCCamera.this, true);
        if (HTCCamera.this.m3DPreviewReady)
        {
          MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 72);
          if (HTCCamera.mBlock3DSwitch)
          {
            MessageHandler.sendObtainMessageDelayed(HTCCamera.this.mUIHandler, 72, 1, 0, null, 2000L);
            continue;
          }
          if ((HTCCamera.this.mCameraThread == null) || (HTCCamera.this.mCameraThread.m3DButtonStatus == HTCCamera.this.m3DButtonStatus))
            continue;
          HTCCamera.this.mCameraThread.m3DButtonStatus = HTCCamera.this.m3DButtonStatus;
          if (HTCCamera.this.mCameraThread.is2ndCamera())
            continue;
          HTCCamera.this.switch3DMode();
          continue;
        }
        LOG.V("HTCCamera", "mIsPreviewReady = false");
        continue;
        HTCCamera.access$11202(HTCCamera.this, true);
        continue;
        if ((HTCCamera.this.mCameraThread == null) || (HTCCamera.this.mCameraThread.is2ndCamera()))
          continue;
        if (((Boolean)paramMessage.obj).booleanValue());
        for (HTCCamera.this.mCameraThread.m3DButtonStatus = 0; ; HTCCamera.this.mCameraThread.m3DButtonStatus = 1)
        {
          LOG.V("HTCCamera", "[MENU_3D_CAMERA_SWITCH] m3DButtonStatus = " + HTCCamera.this.mCameraThread.m3DButtonStatus);
          HTCCamera.this.switch3DMode();
          break;
        }
        HTCCamera.this.hidePortraitSceneGuide(true);
        continue;
        HTCCamera.this.hideSunNightSceneGuide(true);
        continue;
        HTCCamera.this.hideHDRSceneGuide(true);
        continue;
        LOG.V("HTCCamera", "FADEOUT_SCENE_LANDSCAPE");
        HTCCamera.this.hideLandscapeSceneGuide(true);
        continue;
        LOG.V("HTCCamera", "~~~~~~~SHOW_STABLE_ICON~~~~~~~");
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 77);
        HTCCamera.this.mbackIcon_land.setImageResource(2130837507);
        HTCCamera.this.mfrontIcon_land.setVisibility(4);
        HTCCamera.this.mSceneLandscapeTipText.setText(2131362161);
        HTCCamera.this.mSceneLandscapeLayout.setVisibility(0);
        HTCCamera.this.mSceneLandscapeTipLayout.setVisibility(0);
        MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 76, 3000L);
        continue;
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 78);
        HTCCamera.this.mbackIcon_port.setImageResource(2130837508);
        HTCCamera.this.mfrontIcon_port.setVisibility(4);
        HTCCamera.this.mSceneLandscapeTipText.setText(2131362161);
        HTCCamera.this.mSceneLandscapePortLayout.setVisibility(0);
        HTCCamera.this.mSceneLandscapeTipLayout.setVisibility(0);
        MessageHandler.sendEmptyMessageDelayed(HTCCamera.this.mUIHandler, 76, 3000L);
        continue;
        LOG.V("HTCCamera", "UIHandler Mesage - AUTO_SMILE_CAPTURE start");
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 83);
        if ((HTCCamera.this.mCameraThread != null) && (HTCCamera.this.mCameraThread.mMode == 0))
          HTCCamera.this.triggerTakePicture();
        while (true)
        {
          LOG.V("HTCCamera", "UIHandler Message - AUTO_SMILE_CAPTURE end");
          break;
          LOG.W("HTCCamera", "Unable to do auto smile capture in this mode");
        }
        HTCCamera.this.onCameraThreadRunning();
        continue;
        if (!DisplayDevice.isDoubleShot())
          continue;
        HTCCamera.this.enableSceneIcon(HTCCamera.this.mCameraThread.getMode(), HTCCamera.this.mCameraThread.is2ndCamera());
        continue;
        if (HTCCamera.this.mMenuHandler == null)
          continue;
        HTCCamera.this.mMenuHandler.updateStorageLocationItem();
        continue;
        MessageHandler.removeMessages(HTCCamera.this.mUIHandler, 104);
        if (!HTCCamera.this.mIsUIReady)
          continue;
        HTCCamera.this.onRotateChanged(1);
        LOG.W("HTCCamera", "changetoVideoMode, set UI landscape, Freeze UI !!!");
        HTCCamera.access$10202(HTCCamera.this, true);
        HTCCamera.access$12802(HTCCamera.this, OrientationConfig.mapUIOrientationToDegree(1));
      }
    }
  }

  public static enum CaptureCategory
  {
    static
    {
      BurstMode = new CaptureCategory("BurstMode", 1);
      SportMode = new CaptureCategory("SportMode", 2);
      MovieMode = new CaptureCategory("MovieMode", 3);
      MMSMovieMode = new CaptureCategory("MMSMovieMode", 4);
      ContactPicMode = new CaptureCategory("ContactPicMode", 5);
      ThemeMode = new CaptureCategory("ThemeMode", 6);
      PanoramaMode = new CaptureCategory("PanoramaMode", 7);
      CaptureCategory[] arrayOfCaptureCategory = new CaptureCategory[8];
      arrayOfCaptureCategory[0] = NormalMode;
      arrayOfCaptureCategory[1] = BurstMode;
      arrayOfCaptureCategory[2] = SportMode;
      arrayOfCaptureCategory[3] = MovieMode;
      arrayOfCaptureCategory[4] = MMSMovieMode;
      arrayOfCaptureCategory[5] = ContactPicMode;
      arrayOfCaptureCategory[6] = ThemeMode;
      arrayOfCaptureCategory[7] = PanoramaMode;
      $VALUES = arrayOfCaptureCategory;
    }
  }
}

/* Location:           /Users/TwistedZero/android-utility/working-folder/mod-here-multi/dex2jar.jar
 * Qualified Name:     com.android.amaze_camera.HTCCamera
 * JD-Core Version:    0.6.0
 */