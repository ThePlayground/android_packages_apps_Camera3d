.class Lcom/android/camera/sceneselector/SceneSelectorActivity$1;
.super Ljava/lang/Object;
.source "SceneSelectorActivity.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/camera/sceneselector/SceneSelectorActivity;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/camera/sceneselector/SceneSelectorActivity;


# direct methods
.method constructor <init>(Lcom/android/camera/sceneselector/SceneSelectorActivity;)V
    .locals 0

    iput-object p1, p0, Lcom/android/camera/sceneselector/SceneSelectorActivity$1;->this$0:Lcom/android/camera/sceneselector/SceneSelectorActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 1

    iget-object v0, p0, Lcom/android/camera/sceneselector/SceneSelectorActivity$1;->this$0:Lcom/android/camera/sceneselector/SceneSelectorActivity;

    invoke-static {v0}, Lcom/android/camera/sceneselector/SceneSelectorActivity;->access$000(Lcom/android/camera/sceneselector/SceneSelectorActivity;)Lcom/android/camera/sceneselector/SceneMainGallery;

    move-result-object v0

    invoke-virtual {v0}, Lcom/android/camera/sceneselector/SceneMainGallery;->movePrevious()Z

    return-void
.end method
