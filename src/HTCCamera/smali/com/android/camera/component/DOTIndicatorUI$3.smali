.class Lcom/android/camera/component/DOTIndicatorUI$3;
.super Ljava/lang/Object;
.source "DOTIndicatorUI.java"

# interfaces
.implements Lcom/android/camera/IEventHandler;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/camera/component/DOTIndicatorUI;->registerListeners()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/camera/component/DOTIndicatorUI;


# direct methods
.method constructor <init>(Lcom/android/camera/component/DOTIndicatorUI;)V
    .locals 0

    iput-object p1, p0, Lcom/android/camera/component/DOTIndicatorUI$3;->this$0:Lcom/android/camera/component/DOTIndicatorUI;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onEvent(Lcom/android/camera/Event;)V
    .locals 2

    iget-object v1, p0, Lcom/android/camera/component/DOTIndicatorUI$3;->this$0:Lcom/android/camera/component/DOTIndicatorUI;

    invoke-static {v1}, Lcom/android/camera/component/DOTIndicatorUI;->access$1000(Lcom/android/camera/component/DOTIndicatorUI;)Z

    move-result v1

    if-eqz v1, :cond_0

    check-cast p1, Lcom/android/camera/BooleanEvent;

    invoke-virtual {p1}, Lcom/android/camera/BooleanEvent;->getBoolean()Z

    move-result v0

    if-eqz v0, :cond_1

    iget-object v1, p0, Lcom/android/camera/component/DOTIndicatorUI$3;->this$0:Lcom/android/camera/component/DOTIndicatorUI;

    invoke-static {v1}, Lcom/android/camera/component/DOTIndicatorUI;->access$1100(Lcom/android/camera/component/DOTIndicatorUI;)V

    :cond_0
    :goto_0
    return-void

    :cond_1
    iget-object v1, p0, Lcom/android/camera/component/DOTIndicatorUI$3;->this$0:Lcom/android/camera/component/DOTIndicatorUI;

    invoke-static {v1}, Lcom/android/camera/component/DOTIndicatorUI;->access$1200(Lcom/android/camera/component/DOTIndicatorUI;)V

    goto :goto_0
.end method
