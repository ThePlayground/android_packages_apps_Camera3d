.class Lcom/android/camera/component/PanoramaUI$5;
.super Ljava/lang/Object;
.source "PanoramaUI.java"

# interfaces
.implements Lcom/android/camera/IEventHandler;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/camera/component/PanoramaUI;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/camera/component/PanoramaUI;


# direct methods
.method constructor <init>(Lcom/android/camera/component/PanoramaUI;)V
    .locals 0

    iput-object p1, p0, Lcom/android/camera/component/PanoramaUI$5;->this$0:Lcom/android/camera/component/PanoramaUI;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onEvent(Lcom/android/camera/Event;)V
    .locals 3

    const/4 v2, 0x1

    iget-object v0, p0, Lcom/android/camera/component/PanoramaUI$5;->this$0:Lcom/android/camera/component/PanoramaUI;

    invoke-static {v0}, Lcom/android/camera/component/PanoramaUI;->access$000(Lcom/android/camera/component/PanoramaUI;)Z

    move-result v0

    if-nez v0, :cond_1

    :cond_0
    :goto_0
    return-void

    :cond_1
    invoke-static {}, Lcom/android/camera/DisplayDevice;->isDoubleShot()Z

    move-result v0

    if-eqz v0, :cond_2

    iget-object v0, p0, Lcom/android/camera/component/PanoramaUI$5;->this$0:Lcom/android/camera/component/PanoramaUI;

    invoke-static {v0}, Lcom/android/camera/component/PanoramaUI;->access$700(Lcom/android/camera/component/PanoramaUI;)Lcom/android/camera/IEventHandler;

    move-result-object v0

    invoke-interface {v0, p1}, Lcom/android/camera/IEventHandler;->onEvent(Lcom/android/camera/Event;)V

    :cond_2
    iget-object v0, p0, Lcom/android/camera/component/PanoramaUI$5;->this$0:Lcom/android/camera/component/PanoramaUI;

    invoke-static {v0}, Lcom/android/camera/component/PanoramaUI;->access$400(Lcom/android/camera/component/PanoramaUI;)Landroid/view/View;

    move-result-object v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/android/camera/component/PanoramaUI$5;->this$0:Lcom/android/camera/component/PanoramaUI;

    iget-object v1, p0, Lcom/android/camera/component/PanoramaUI$5;->this$0:Lcom/android/camera/component/PanoramaUI;

    invoke-static {v1}, Lcom/android/camera/component/PanoramaUI;->access$400(Lcom/android/camera/component/PanoramaUI;)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v0, v1, v2, v2}, Lcom/android/camera/component/PanoramaUI;->showUI(Landroid/view/View;ZZ)V

    goto :goto_0
.end method
