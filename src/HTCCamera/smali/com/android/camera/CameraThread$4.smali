.class Lcom/android/camera/CameraThread$4;
.super Ljava/lang/Object;
.source "CameraThread.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/camera/CameraThread;->checkCanTakeRecorder()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/camera/CameraThread;


# direct methods
.method constructor <init>(Lcom/android/camera/CameraThread;)V
    .locals 0

    iput-object p1, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 6

    const/4 v5, 0x0

    const/4 v4, 0x1

    sget v0, Lcom/android/camera/CameraThread;->Storage_Status:I

    if-eqz v0, :cond_1

    const-string v0, "CameraThread"

    const-string v1, "storage error"

    invoke-static {v0, v1}, Lcom/android/camera/LOG;->E(Ljava/lang/String;Ljava/lang/String;)V

    iget-object v0, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-virtual {v0, v4}, Lcom/android/camera/CameraThread;->showStorageToast(Z)V

    sget v0, Lcom/android/camera/CameraThread;->Storage_Status:I

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-static {v0, v5}, Lcom/android/camera/CameraThread;->access$2202(Lcom/android/camera/CameraThread;Z)Z

    :goto_0
    return-void

    :cond_0
    iget-object v0, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-static {v0, v4}, Lcom/android/camera/CameraThread;->access$2202(Lcom/android/camera/CameraThread;Z)Z

    goto :goto_0

    :cond_1
    iget-object v0, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-static {v0}, Lcom/android/camera/CameraThread;->access$000(Lcom/android/camera/CameraThread;)Lcom/android/camera/HTCCamera;

    move-result-object v0

    invoke-virtual {v0}, Lcom/android/camera/HTCCamera;->getRecordLimitCheck()Lcom/android/camera/RecordLimitCheck;

    move-result-object v0

    iget-object v1, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-static {v1}, Lcom/android/camera/CameraThread;->access$000(Lcom/android/camera/CameraThread;)Lcom/android/camera/HTCCamera;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/android/camera/RecordLimitCheck;->setByteRate(Lcom/android/camera/HTCCamera;)V

    iget-object v0, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-static {v0}, Lcom/android/camera/CameraThread;->access$000(Lcom/android/camera/CameraThread;)Lcom/android/camera/HTCCamera;

    move-result-object v0

    invoke-virtual {v0}, Lcom/android/camera/HTCCamera;->getRecordLimitCheck()Lcom/android/camera/RecordLimitCheck;

    move-result-object v0

    iget-object v1, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-static {v1}, Lcom/android/camera/CameraThread;->access$000(Lcom/android/camera/CameraThread;)Lcom/android/camera/HTCCamera;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/android/camera/RecordLimitCheck;->setRecordingLength(Lcom/android/camera/HTCCamera;)V

    iget-object v0, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-static {v0}, Lcom/android/camera/CameraThread;->access$000(Lcom/android/camera/CameraThread;)Lcom/android/camera/HTCCamera;

    move-result-object v0

    invoke-virtual {v0}, Lcom/android/camera/HTCCamera;->getRecordLimitCheck()Lcom/android/camera/RecordLimitCheck;

    move-result-object v0

    invoke-virtual {v0, v4}, Lcom/android/camera/RecordLimitCheck;->remainTime(Z)J

    move-result-wide v0

    const-wide/16 v2, 0x1

    cmp-long v0, v0, v2

    if-gez v0, :cond_3

    const/4 v0, 0x3

    sput v0, Lcom/android/camera/CameraThread;->Storage_Status:I

    iget-object v0, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-virtual {v0, v4}, Lcom/android/camera/CameraThread;->showStorageToast(Z)V

    sget v0, Lcom/android/camera/CameraThread;->Storage_Status:I

    if-eqz v0, :cond_2

    iget-object v0, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-static {v0, v5}, Lcom/android/camera/CameraThread;->access$2202(Lcom/android/camera/CameraThread;Z)Z

    goto :goto_0

    :cond_2
    iget-object v0, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-static {v0, v4}, Lcom/android/camera/CameraThread;->access$2202(Lcom/android/camera/CameraThread;Z)Z

    goto :goto_0

    :cond_3
    iget-object v0, p0, Lcom/android/camera/CameraThread$4;->this$0:Lcom/android/camera/CameraThread;

    invoke-static {v0, v4}, Lcom/android/camera/CameraThread;->access$2202(Lcom/android/camera/CameraThread;Z)Z

    goto :goto_0
.end method
