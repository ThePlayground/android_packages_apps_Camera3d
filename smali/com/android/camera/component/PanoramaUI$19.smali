.class Lcom/android/camera/component/PanoramaUI$19;
.super Ljava/lang/Object;
.source "PanoramaUI.java"

# interfaces
.implements Lcom/android/camera/IEventHandler;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/camera/component/PanoramaUI;->registerEventListeners()V
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

    iput-object p1, p0, Lcom/android/camera/component/PanoramaUI$19;->this$0:Lcom/android/camera/component/PanoramaUI;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onEvent(Lcom/android/camera/Event;)V
    .locals 3

    const/4 v2, 0x0

    iget-object v0, p0, Lcom/android/camera/component/PanoramaUI$19;->this$0:Lcom/android/camera/component/PanoramaUI;

    invoke-static {v0, v2, v2}, Lcom/android/camera/component/PanoramaUI;->access$300(Lcom/android/camera/component/PanoramaUI;ZZ)V

    iget-object v0, p0, Lcom/android/camera/component/PanoramaUI$19;->this$0:Lcom/android/camera/component/PanoramaUI;

    invoke-static {v0}, Lcom/android/camera/component/PanoramaUI;->access$400(Lcom/android/camera/component/PanoramaUI;)Landroid/view/View;

    move-result-object v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/android/camera/component/PanoramaUI$19;->this$0:Lcom/android/camera/component/PanoramaUI;

    iget-object v1, p0, Lcom/android/camera/component/PanoramaUI$19;->this$0:Lcom/android/camera/component/PanoramaUI;

    invoke-static {v1}, Lcom/android/camera/component/PanoramaUI;->access$400(Lcom/android/camera/component/PanoramaUI;)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v0, v1, v2, v2}, Lcom/android/camera/component/PanoramaUI;->showUI(Landroid/view/View;ZZ)V

    :cond_0
    return-void
.end method
