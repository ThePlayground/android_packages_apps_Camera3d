.class Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;
.super Landroid/widget/CursorAdapter;
.source "RotateHtcAlertController.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams;->createListView(Lcom/android/camera/rotate/RotateHtcAlertController;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field private final mIsCheckedIndex:I

.field private final mLabelIndex:I

.field final synthetic this$0:Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams;

.field final synthetic val$listView:Landroid/widget/ListView;


# direct methods
.method constructor <init>(Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams;Landroid/content/Context;Landroid/database/Cursor;ZLandroid/widget/ListView;)V
    .locals 2

    iput-object p1, p0, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;->this$0:Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams;

    iput-object p5, p0, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;->val$listView:Landroid/widget/ListView;

    invoke-direct {p0, p2, p3, p4}, Landroid/widget/CursorAdapter;-><init>(Landroid/content/Context;Landroid/database/Cursor;Z)V

    invoke-virtual {p0}, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;->getCursor()Landroid/database/Cursor;

    move-result-object v0

    iget-object v1, p0, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;->this$0:Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams;

    iget-object v1, v1, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams;->mLabelColumn:Ljava/lang/String;

    invoke-interface {v0, v1}, Landroid/database/Cursor;->getColumnIndexOrThrow(Ljava/lang/String;)I

    move-result v1

    iput v1, p0, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;->mLabelIndex:I

    iget-object v1, p0, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;->this$0:Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams;

    iget-object v1, v1, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams;->mIsCheckedColumn:Ljava/lang/String;

    invoke-interface {v0, v1}, Landroid/database/Cursor;->getColumnIndexOrThrow(Ljava/lang/String;)I

    move-result v1

    iput v1, p0, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;->mIsCheckedIndex:I

    return-void
.end method


# virtual methods
.method public bindView(Landroid/view/View;Landroid/content/Context;Landroid/database/Cursor;)V
    .locals 5

    const/4 v4, 0x1

    const v1, 0x1020014

    invoke-virtual {p1, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/CheckedTextView;

    iget v1, p0, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;->mLabelIndex:I

    invoke-interface {p3, v1}, Landroid/database/Cursor;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/CheckedTextView;->setText(Ljava/lang/CharSequence;)V

    iget-object v1, p0, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;->val$listView:Landroid/widget/ListView;

    invoke-interface {p3}, Landroid/database/Cursor;->getPosition()I

    move-result v2

    iget v3, p0, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;->mIsCheckedIndex:I

    invoke-interface {p3, v3}, Landroid/database/Cursor;->getInt(I)I

    move-result v3

    if-ne v3, v4, :cond_0

    move v3, v4

    :goto_0
    invoke-virtual {v1, v2, v3}, Landroid/widget/ListView;->setItemChecked(IZ)V

    return-void

    :cond_0
    const/4 v3, 0x0

    goto :goto_0
.end method

.method public newView(Landroid/content/Context;Landroid/database/Cursor;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 3

    iget-object v0, p0, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams$2;->this$0:Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams;

    iget-object v0, v0, Lcom/android/camera/rotate/RotateHtcAlertController$AlertParams;->mInflater:Landroid/view/LayoutInflater;

    const v1, 0x20900ac

    const/4 v2, 0x0

    invoke-virtual {v0, v1, p3, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v0

    return-object v0
.end method
