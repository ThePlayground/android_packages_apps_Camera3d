.class Lcom/android/camera/MenuHandler$10;
.super Ljava/lang/Object;
.source "MenuHandler.java"

# interfaces
.implements Landroid/view/View$OnFocusChangeListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/camera/MenuHandler;->createImagePropertyView()Landroid/view/View;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/camera/MenuHandler;


# direct methods
.method constructor <init>(Lcom/android/camera/MenuHandler;)V
    .locals 0

    iput-object p1, p0, Lcom/android/camera/MenuHandler$10;->this$0:Lcom/android/camera/MenuHandler;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onFocusChange(Landroid/view/View;Z)V
    .locals 2

    invoke-static {}, Lcom/android/camera/rotate/OrientationConfig;->getUIOrientation()I

    move-result v0

    if-eqz v0, :cond_0

    const/4 v1, 0x2

    if-ne v0, v1, :cond_1

    :cond_0
    iget-object v1, p0, Lcom/android/camera/MenuHandler$10;->this$0:Lcom/android/camera/MenuHandler;

    invoke-static {v1}, Lcom/android/camera/MenuHandler;->access$1100(Lcom/android/camera/MenuHandler;)Landroid/widget/TextView;

    move-result-object v1

    invoke-virtual {v1, p2}, Landroid/widget/TextView;->setSelected(Z)V

    :goto_0
    return-void

    :cond_1
    iget-object v1, p0, Lcom/android/camera/MenuHandler$10;->this$0:Lcom/android/camera/MenuHandler;

    invoke-static {v1}, Lcom/android/camera/MenuHandler;->access$1200(Lcom/android/camera/MenuHandler;)Landroid/widget/TextView;

    move-result-object v1

    invoke-virtual {v1, p2}, Landroid/widget/TextView;->setSelected(Z)V

    goto :goto_0
.end method
