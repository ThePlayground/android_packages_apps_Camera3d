.class Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;
.super Landroid/database/DataSetObserver;
.source "AdapterView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/camera/sceneselector/AdapterView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = "AdapterDataSetObserver"
.end annotation


# instance fields
.field private mInstanceState:Landroid/os/Parcelable;

.field final synthetic this$0:Lcom/android/camera/sceneselector/AdapterView;


# direct methods
.method constructor <init>(Lcom/android/camera/sceneselector/AdapterView;)V
    .locals 1

    iput-object p1, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    invoke-direct {p0}, Landroid/database/DataSetObserver;-><init>()V

    const/4 v0, 0x0

    iput-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    return-void
.end method


# virtual methods
.method public clearSavedState()V
    .locals 1

    const/4 v0, 0x0

    iput-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    return-void
.end method

.method public onChanged()V
    .locals 2

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    const/4 v1, 0x1

    iput-boolean v1, v0, Lcom/android/camera/sceneselector/AdapterView;->mDataChanged:Z

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iget-object v1, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iget v1, v1, Lcom/android/camera/sceneselector/AdapterView;->mItemCount:I

    iput v1, v0, Lcom/android/camera/sceneselector/AdapterView;->mOldItemCount:I

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iget-object v1, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    invoke-virtual {v1}, Lcom/android/camera/sceneselector/AdapterView;->getAdapter()Landroid/widget/Adapter;

    move-result-object v1

    invoke-interface {v1}, Landroid/widget/Adapter;->getCount()I

    move-result v1

    iput v1, v0, Lcom/android/camera/sceneselector/AdapterView;->mItemCount:I

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    invoke-virtual {v0}, Lcom/android/camera/sceneselector/AdapterView;->getAdapter()Landroid/widget/Adapter;

    move-result-object v0

    invoke-interface {v0}, Landroid/widget/Adapter;->hasStableIds()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iget v0, v0, Lcom/android/camera/sceneselector/AdapterView;->mOldItemCount:I

    if-nez v0, :cond_0

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iget v0, v0, Lcom/android/camera/sceneselector/AdapterView;->mItemCount:I

    if-lez v0, :cond_0

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iget-object v1, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    invoke-static {v0, v1}, Lcom/android/camera/sceneselector/AdapterView;->access$000(Lcom/android/camera/sceneselector/AdapterView;Landroid/os/Parcelable;)V

    const/4 v0, 0x0

    iput-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    :goto_0
    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    invoke-virtual {v0}, Lcom/android/camera/sceneselector/AdapterView;->checkFocus()V

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    invoke-virtual {v0}, Lcom/android/camera/sceneselector/AdapterView;->requestLayout()V

    return-void

    :cond_0
    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    invoke-virtual {v0}, Lcom/android/camera/sceneselector/AdapterView;->rememberSyncState()V

    goto :goto_0
.end method

.method public onInvalidated()V
    .locals 6

    const-wide/high16 v4, -0x8000

    const/4 v3, 0x0

    const/4 v2, -0x1

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    const/4 v1, 0x1

    iput-boolean v1, v0, Lcom/android/camera/sceneselector/AdapterView;->mDataChanged:Z

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    invoke-virtual {v0}, Lcom/android/camera/sceneselector/AdapterView;->getAdapter()Landroid/widget/Adapter;

    move-result-object v0

    invoke-interface {v0}, Landroid/widget/Adapter;->hasStableIds()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    invoke-static {v0}, Lcom/android/camera/sceneselector/AdapterView;->access$100(Lcom/android/camera/sceneselector/AdapterView;)Landroid/os/Parcelable;

    move-result-object v0

    iput-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    :cond_0
    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iget-object v1, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iget v1, v1, Lcom/android/camera/sceneselector/AdapterView;->mItemCount:I

    iput v1, v0, Lcom/android/camera/sceneselector/AdapterView;->mOldItemCount:I

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iput v3, v0, Lcom/android/camera/sceneselector/AdapterView;->mItemCount:I

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iput v2, v0, Lcom/android/camera/sceneselector/AdapterView;->mSelectedPosition:I

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iput-wide v4, v0, Lcom/android/camera/sceneselector/AdapterView;->mSelectedRowId:J

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iput v2, v0, Lcom/android/camera/sceneselector/AdapterView;->mNextSelectedPosition:I

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iput-wide v4, v0, Lcom/android/camera/sceneselector/AdapterView;->mNextSelectedRowId:J

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    iput-boolean v3, v0, Lcom/android/camera/sceneselector/AdapterView;->mNeedSync:Z

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    invoke-virtual {v0}, Lcom/android/camera/sceneselector/AdapterView;->checkSelectionChanged()V

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    invoke-virtual {v0}, Lcom/android/camera/sceneselector/AdapterView;->checkFocus()V

    iget-object v0, p0, Lcom/android/camera/sceneselector/AdapterView$AdapterDataSetObserver;->this$0:Lcom/android/camera/sceneselector/AdapterView;

    invoke-virtual {v0}, Lcom/android/camera/sceneselector/AdapterView;->requestLayout()V

    return-void
.end method
