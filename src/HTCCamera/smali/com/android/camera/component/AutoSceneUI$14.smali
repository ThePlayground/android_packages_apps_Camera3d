.class Lcom/android/camera/component/AutoSceneUI$14;
.super Ljava/lang/Object;
.source "AutoSceneUI.java"

# interfaces
.implements Lcom/android/camera/IEventHandler;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/camera/component/AutoSceneUI;->registerListeners()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/camera/component/AutoSceneUI;


# direct methods
.method constructor <init>(Lcom/android/camera/component/AutoSceneUI;)V
    .locals 0

    iput-object p1, p0, Lcom/android/camera/component/AutoSceneUI$14;->this$0:Lcom/android/camera/component/AutoSceneUI;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onEvent(Lcom/android/camera/Event;)V
    .locals 3

    iget-object v0, p0, Lcom/android/camera/component/AutoSceneUI$14;->this$0:Lcom/android/camera/component/AutoSceneUI;

    check-cast p1, Lcom/android/camera/BooleanEvent;

    invoke-virtual {p1}, Lcom/android/camera/BooleanEvent;->getBoolean()Z

    move-result v1

    if-nez v1, :cond_0

    const/4 v1, 0x1

    :goto_0
    invoke-static {v0, v1}, Lcom/android/camera/component/AutoSceneUI;->access$302(Lcom/android/camera/component/AutoSceneUI;Z)Z

    iget-object v0, p0, Lcom/android/camera/component/AutoSceneUI$14;->this$0:Lcom/android/camera/component/AutoSceneUI;

    iget-object v1, p0, Lcom/android/camera/component/AutoSceneUI$14;->this$0:Lcom/android/camera/component/AutoSceneUI;

    invoke-static {v1}, Lcom/android/camera/component/AutoSceneUI;->access$300(Lcom/android/camera/component/AutoSceneUI;)Z

    move-result v1

    invoke-static {}, Lcom/android/camera/rotate/OrientationConfig;->getUIOrientation()I

    move-result v2

    invoke-static {v0, v1, v2}, Lcom/android/camera/component/AutoSceneUI;->access$400(Lcom/android/camera/component/AutoSceneUI;ZI)V

    return-void

    :cond_0
    const/4 v1, 0x0

    goto :goto_0
.end method
