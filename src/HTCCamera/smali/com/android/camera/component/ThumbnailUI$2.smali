.class Lcom/android/camera/component/ThumbnailUI$2;
.super Ljava/lang/Object;
.source "ThumbnailUI.java"

# interfaces
.implements Lcom/android/camera/IEventHandler;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/camera/component/ThumbnailUI;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/camera/component/ThumbnailUI;


# direct methods
.method constructor <init>(Lcom/android/camera/component/ThumbnailUI;)V
    .locals 0

    iput-object p1, p0, Lcom/android/camera/component/ThumbnailUI$2;->this$0:Lcom/android/camera/component/ThumbnailUI;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onEvent(Lcom/android/camera/Event;)V
    .locals 2

    iget-object v0, p0, Lcom/android/camera/component/ThumbnailUI$2;->this$0:Lcom/android/camera/component/ThumbnailUI;

    invoke-static {v0}, Lcom/android/camera/component/ThumbnailUI;->access$100(Lcom/android/camera/component/ThumbnailUI;)V

    iget-object v0, p0, Lcom/android/camera/component/ThumbnailUI$2;->this$0:Lcom/android/camera/component/ThumbnailUI;

    const/4 v1, 0x0

    invoke-static {v0, v1}, Lcom/android/camera/component/ThumbnailUI;->access$202(Lcom/android/camera/component/ThumbnailUI;Z)Z

    return-void
.end method
