.class Lcom/android/camera/HTCCamera$27;
.super Ljava/lang/Object;
.source "HTCCamera.java"

# interfaces
.implements Landroid/view/View$OnTouchListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/camera/HTCCamera;->doOnCreate_after_preview()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/camera/HTCCamera;


# direct methods
.method constructor <init>(Lcom/android/camera/HTCCamera;)V
    .locals 0

    iput-object p1, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onTouch(Landroid/view/View;Landroid/view/MotionEvent;)Z
    .locals 7

    const/4 v6, 0x1

    const/4 v4, 0x0

    const/16 v5, 0x42

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getX()F

    move-result v2

    float-to-int v0, v2

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getY()F

    move-result v2

    float-to-int v1, v2

    if-ltz v0, :cond_0

    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v2}, Lcom/android/camera/HTCCamera;->access$800(Lcom/android/camera/HTCCamera;)Landroid/widget/Button;

    move-result-object v2

    invoke-virtual {v2}, Landroid/widget/Button;->getWidth()I

    move-result v2

    if-gt v0, v2, :cond_0

    if-ltz v1, :cond_0

    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v2}, Lcom/android/camera/HTCCamera;->access$800(Lcom/android/camera/HTCCamera;)Landroid/widget/Button;

    move-result-object v2

    invoke-virtual {v2}, Landroid/widget/Button;->getHeight()I

    move-result v2

    if-le v1, v2, :cond_1

    :cond_0
    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    iget-object v3, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    iget-object v3, v3, Lcom/android/camera/HTCCamera;->mCameraThread:Lcom/android/camera/CameraThread;

    iget v3, v3, Lcom/android/camera/CameraThread;->mMode:I

    invoke-virtual {v2, v4, v3}, Lcom/android/camera/HTCCamera;->updateDOTCaptureIcon(ZI)V

    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v2}, Lcom/android/camera/HTCCamera;->access$000(Lcom/android/camera/HTCCamera;)Landroid/os/Handler;

    move-result-object v2

    invoke-static {v2, v5}, Lcom/android/camera/MessageHandler;->removeMessages(Landroid/os/Handler;I)V

    move v2, v6

    :goto_0
    return v2

    :cond_1
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getAction()I

    move-result v2

    packed-switch v2, :pswitch_data_0

    :cond_2
    :goto_1
    move v2, v6

    goto :goto_0

    :pswitch_0
    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v2, v4}, Lcom/android/camera/HTCCamera;->access$10602(Lcom/android/camera/HTCCamera;Z)Z

    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v2}, Lcom/android/camera/HTCCamera;->access$000(Lcom/android/camera/HTCCamera;)Landroid/os/Handler;

    move-result-object v2

    invoke-static {v2, v5}, Lcom/android/camera/MessageHandler;->removeMessages(Landroid/os/Handler;I)V

    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v2}, Lcom/android/camera/HTCCamera;->access$000(Lcom/android/camera/HTCCamera;)Landroid/os/Handler;

    move-result-object v2

    const-wide/16 v3, 0x3e8

    invoke-static {v2, v5, v3, v4}, Lcom/android/camera/MessageHandler;->sendEmptyMessageDelayed(Landroid/os/Handler;IJ)V

    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    iget-object v3, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    iget-object v3, v3, Lcom/android/camera/HTCCamera;->mCameraThread:Lcom/android/camera/CameraThread;

    iget v3, v3, Lcom/android/camera/CameraThread;->mMode:I

    invoke-virtual {v2, v6, v3}, Lcom/android/camera/HTCCamera;->updateDOTCaptureIcon(ZI)V

    goto :goto_1

    :pswitch_1
    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v2}, Lcom/android/camera/HTCCamera;->access$10600(Lcom/android/camera/HTCCamera;)Z

    move-result v2

    if-nez v2, :cond_2

    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v2}, Lcom/android/camera/HTCCamera;->access$000(Lcom/android/camera/HTCCamera;)Landroid/os/Handler;

    move-result-object v2

    invoke-static {v2, v5}, Lcom/android/camera/MessageHandler;->removeMessages(Landroid/os/Handler;I)V

    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    iget-object v3, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    iget-object v3, v3, Lcom/android/camera/HTCCamera;->mCameraThread:Lcom/android/camera/CameraThread;

    iget v3, v3, Lcom/android/camera/CameraThread;->mMode:I

    invoke-virtual {v2, v4, v3}, Lcom/android/camera/HTCCamera;->updateDOTCaptureIcon(ZI)V

    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    iget-object v2, v2, Lcom/android/camera/HTCCamera;->mCameraThread:Lcom/android/camera/CameraThread;

    if-eqz v2, :cond_3

    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    iget-object v2, v2, Lcom/android/camera/HTCCamera;->mCameraThread:Lcom/android/camera/CameraThread;

    iget v2, v2, Lcom/android/camera/CameraThread;->mMode:I

    if-nez v2, :cond_3

    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v2}, Lcom/android/camera/HTCCamera;->access$7500(Lcom/android/camera/HTCCamera;)V

    goto :goto_1

    :cond_3
    iget-object v2, p0, Lcom/android/camera/HTCCamera$27;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v2}, Lcom/android/camera/HTCCamera;->access$10700(Lcom/android/camera/HTCCamera;)V

    goto :goto_1

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method
