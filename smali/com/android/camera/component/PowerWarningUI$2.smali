.class Lcom/android/camera/component/PowerWarningUI$2;
.super Ljava/lang/Object;
.source "PowerWarningUI.java"

# interfaces
.implements Lcom/android/camera/IEventHandler;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/camera/component/PowerWarningUI;->registerListeners()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/camera/component/PowerWarningUI;


# direct methods
.method constructor <init>(Lcom/android/camera/component/PowerWarningUI;)V
    .locals 0

    iput-object p1, p0, Lcom/android/camera/component/PowerWarningUI$2;->this$0:Lcom/android/camera/component/PowerWarningUI;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onEvent(Lcom/android/camera/Event;)V
    .locals 4

    move-object v0, p1

    check-cast v0, Lcom/android/camera/KeyEvent;

    move-object v1, v0

    iget-object v2, p0, Lcom/android/camera/component/PowerWarningUI$2;->this$0:Lcom/android/camera/component/PowerWarningUI;

    invoke-static {v2}, Lcom/android/camera/component/PowerWarningUI;->access$000(Lcom/android/camera/component/PowerWarningUI;)Z

    move-result v2

    if-eqz v2, :cond_0

    invoke-virtual {v1}, Lcom/android/camera/KeyEvent;->getKeyCode()I

    move-result v2

    const/4 v3, 0x4

    if-eq v2, v3, :cond_0

    invoke-virtual {v1}, Lcom/android/camera/KeyEvent;->setHandled()V

    :cond_0
    return-void
.end method
