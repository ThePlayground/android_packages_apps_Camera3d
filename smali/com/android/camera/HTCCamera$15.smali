.class Lcom/android/camera/HTCCamera$15;
.super Ljava/lang/Object;
.source "HTCCamera.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


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

    iput-object p1, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 5

    const/16 v4, 0x25

    const/4 v2, 0x1

    const-string v0, "HTCCamera"

    const-string v1, "Click button to change flash mode"

    invoke-static {v0, v1}, Lcom/android/camera/LOG;->V(Ljava/lang/String;Ljava/lang/String;)V

    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v0}, Lcom/android/camera/HTCCamera;->access$13500(Lcom/android/camera/HTCCamera;)Lcom/android/camera/widget/SlidingMenu;

    move-result-object v0

    if-eqz v0, :cond_1

    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    iget-object v0, v0, Lcom/android/camera/HTCCamera;->mCameraThread:Lcom/android/camera/CameraThread;

    if-eqz v0, :cond_1

    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    iget-object v0, v0, Lcom/android/camera/HTCCamera;->mCameraThread:Lcom/android/camera/CameraThread;

    iget v0, v0, Lcom/android/camera/CameraThread;->mMode:I

    if-nez v0, :cond_1

    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v0}, Lcom/android/camera/HTCCamera;->access$13500(Lcom/android/camera/HTCCamera;)Lcom/android/camera/widget/SlidingMenu;

    move-result-object v0

    invoke-virtual {v0}, Lcom/android/camera/widget/SlidingMenu;->toggle()V

    :cond_0
    :goto_0
    return-void

    :cond_1
    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-virtual {v0}, Lcom/android/camera/HTCCamera;->closeAllExtensionMenus()V

    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v0}, Lcom/android/camera/HTCCamera;->access$8400(Lcom/android/camera/HTCCamera;)Lcom/android/camera/FlashRestriction;

    move-result-object v0

    if-eqz v0, :cond_2

    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v0}, Lcom/android/camera/HTCCamera;->access$8400(Lcom/android/camera/HTCCamera;)Lcom/android/camera/FlashRestriction;

    move-result-object v0

    invoke-virtual {v0}, Lcom/android/camera/FlashRestriction;->isDisableFlash()Z

    move-result v0

    if-ne v0, v2, :cond_2

    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v0}, Lcom/android/camera/HTCCamera;->access$8500(Lcom/android/camera/HTCCamera;)Z

    move-result v0

    if-nez v0, :cond_2

    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v0}, Lcom/android/camera/HTCCamera;->access$8600(Lcom/android/camera/HTCCamera;)Z

    move-result v0

    if-nez v0, :cond_2

    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v0}, Lcom/android/camera/HTCCamera;->access$000(Lcom/android/camera/HTCCamera;)Landroid/os/Handler;

    move-result-object v0

    invoke-static {v0, v4}, Lcom/android/camera/MessageHandler;->removeMessages(Landroid/os/Handler;I)V

    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v0}, Lcom/android/camera/HTCCamera;->access$000(Lcom/android/camera/HTCCamera;)Landroid/os/Handler;

    move-result-object v0

    iget-object v1, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-static {v1}, Lcom/android/camera/HTCCamera;->access$8400(Lcom/android/camera/HTCCamera;)Lcom/android/camera/FlashRestriction;

    move-result-object v1

    invoke-virtual {v1}, Lcom/android/camera/FlashRestriction;->getRestrictionHint()I

    move-result v1

    const/4 v2, 0x0

    const/4 v3, 0x0

    invoke-static {v0, v4, v1, v2, v3}, Lcom/android/camera/MessageHandler;->sendObtainMessage(Landroid/os/Handler;IIILjava/lang/Object;)V

    goto :goto_0

    :cond_2
    invoke-static {}, Lcom/android/camera/HTCCamera;->access$1700()Z

    move-result v0

    if-eqz v0, :cond_3

    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    iget v0, v0, Lcom/android/camera/HTCCamera;->mFocusingState:I

    if-ne v0, v2, :cond_0

    :cond_3
    iget-object v0, p0, Lcom/android/camera/HTCCamera$15;->this$0:Lcom/android/camera/HTCCamera;

    invoke-virtual {v0}, Lcom/android/camera/HTCCamera;->switchFlashMode()V

    goto :goto_0
.end method
