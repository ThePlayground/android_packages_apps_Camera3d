.class public Lcom/scalado/base/Iterator$CommonIterator;
.super Lcom/scalado/caps/PeerBase;
.source "Iterator.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/scalado/base/Iterator;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x4
    name = "CommonIterator"
.end annotation


# instance fields
.field private isDone:Z

.field private iterationCount:I

.field final synthetic this$0:Lcom/scalado/base/Iterator;

.field private totalIterations:I


# direct methods
.method public constructor <init>(Lcom/scalado/base/Iterator;Lcom/scalado/base/Iterator;)V
    .locals 3

    const/4 v2, -0x1

    iput-object p1, p0, Lcom/scalado/base/Iterator$CommonIterator;->this$0:Lcom/scalado/base/Iterator;

    invoke-direct {p0}, Lcom/scalado/caps/PeerBase;-><init>()V

    const/4 v1, 0x0

    iput-boolean v1, p0, Lcom/scalado/base/Iterator$CommonIterator;->isDone:Z

    iput v2, p0, Lcom/scalado/base/Iterator$CommonIterator;->iterationCount:I

    iput v2, p0, Lcom/scalado/base/Iterator$CommonIterator;->totalIterations:I

    invoke-direct {p0, p2}, Lcom/scalado/base/Iterator$CommonIterator;->nativeCreate(Lcom/scalado/base/Iterator;)I

    move-result v0

    invoke-direct {p0}, Lcom/scalado/base/Iterator$CommonIterator;->nativeGetTotalIterations()I

    move-result v0

    return-void
.end method

.method private native nativeAbort()I
.end method

.method private native nativeBeginRender(Lcom/scalado/base/Image;Lcom/scalado/caps/Decoder;)I
.end method

.method private native nativeCreate(Lcom/scalado/base/Iterator;)I
.end method

.method private native nativeGetIterationCount()I
.end method

.method private native nativeGetTotalIterations()I
.end method

.method private native nativeStep()I
.end method


# virtual methods
.method public abort()V
    .locals 2

    iget-boolean v1, p0, Lcom/scalado/base/Iterator$CommonIterator;->isDone:Z

    if-nez v1, :cond_0

    invoke-direct {p0}, Lcom/scalado/base/Iterator$CommonIterator;->nativeAbort()I

    move-result v0

    const/4 v1, 0x1

    iput-boolean v1, p0, Lcom/scalado/base/Iterator$CommonIterator;->isDone:Z

    :cond_0
    return-void
.end method

.method public done()Z
    .locals 1

    iget-boolean v0, p0, Lcom/scalado/base/Iterator$CommonIterator;->isDone:Z

    return v0
.end method

.method public step()F
    .locals 5

    const/high16 v4, 0x3f80

    iget-boolean v2, p0, Lcom/scalado/base/Iterator$CommonIterator;->isDone:Z

    if-eqz v2, :cond_0

    new-instance v2, Ljava/lang/IllegalStateException;

    invoke-direct {v2}, Ljava/lang/IllegalStateException;-><init>()V

    throw v2

    :cond_0
    invoke-direct {p0}, Lcom/scalado/base/Iterator$CommonIterator;->nativeStep()I

    move-result v0

    iget-boolean v2, p0, Lcom/scalado/base/Iterator$CommonIterator;->isDone:Z

    if-eqz v2, :cond_1

    move v2, v4

    :goto_0
    return v2

    :cond_1
    iget v2, p0, Lcom/scalado/base/Iterator$CommonIterator;->totalIterations:I

    if-gtz v2, :cond_2

    move v2, v4

    goto :goto_0

    :cond_2
    invoke-direct {p0}, Lcom/scalado/base/Iterator$CommonIterator;->nativeGetIterationCount()I

    move-result v0

    iget v2, p0, Lcom/scalado/base/Iterator$CommonIterator;->iterationCount:I

    int-to-float v2, v2

    iget v3, p0, Lcom/scalado/base/Iterator$CommonIterator;->totalIterations:I

    int-to-float v3, v3

    div-float v1, v2, v3

    cmpl-float v2, v1, v4

    if-lez v2, :cond_3

    move v2, v4

    goto :goto_0

    :cond_3
    move v2, v1

    goto :goto_0
.end method
