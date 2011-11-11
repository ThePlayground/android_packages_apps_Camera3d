.class Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;
.super Lcom/scalado/base/Iterator;
.source "PanoramaDecoder.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/scalado/caps/autorama/PanoramaDecoder;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "PanoramaDecoderIterator"
.end annotation


# instance fields
.field private decoder:Lcom/scalado/caps/autorama/PanoramaDecoder;

.field private isDone:Z

.field private stitcher:Lcom/scalado/caps/autorama/Stitcher;


# direct methods
.method constructor <init>(Lcom/scalado/caps/autorama/Stitcher;)V
    .locals 2

    const/4 v1, 0x0

    invoke-direct {p0}, Lcom/scalado/base/Iterator;-><init>()V

    iput-object v1, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->decoder:Lcom/scalado/caps/autorama/PanoramaDecoder;

    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->isDone:Z

    new-instance v0, Lcom/scalado/caps/autorama/PanoramaDecoder;

    invoke-direct {v0, v1}, Lcom/scalado/caps/autorama/PanoramaDecoder;-><init>(Lcom/scalado/caps/autorama/PanoramaDecoder;)V

    iput-object v0, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->decoder:Lcom/scalado/caps/autorama/PanoramaDecoder;

    invoke-direct {p0, p1}, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->nativeBeginCreate(Lcom/scalado/caps/autorama/Stitcher;)V

    iput-object p1, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->stitcher:Lcom/scalado/caps/autorama/Stitcher;

    return-void
.end method

.method private native nativeAbort()V
.end method

.method private native nativeBeginCreate(Lcom/scalado/caps/autorama/Stitcher;)V
.end method

.method private native nativeGetIterationCount()I
.end method

.method private native nativeGetTotalIterations()I
.end method

.method private native nativeStep(Lcom/scalado/caps/autorama/PanoramaDecoder;)Z
.end method


# virtual methods
.method public abort()V
    .locals 1

    iget-boolean v0, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->isDone:Z

    if-nez v0, :cond_0

    invoke-direct {p0}, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->nativeAbort()V

    const/4 v0, 0x1

    iput-boolean v0, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->isDone:Z

    :cond_0
    return-void
.end method

.method public done()Z
    .locals 1

    iget-boolean v0, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->isDone:Z

    return v0
.end method

.method public getObject()Ljava/lang/Object;
    .locals 1

    iget-boolean v0, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->isDone:Z

    if-nez v0, :cond_0

    new-instance v0, Ljava/lang/IllegalStateException;

    invoke-direct {v0}, Ljava/lang/IllegalStateException;-><init>()V

    throw v0

    :cond_0
    iget-object v0, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->decoder:Lcom/scalado/caps/autorama/PanoramaDecoder;

    return-object v0
.end method

.method public step()F
    .locals 4
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    const/high16 v3, 0x3f80

    iget-boolean v1, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->isDone:Z

    if-eqz v1, :cond_0

    new-instance v1, Ljava/lang/IllegalAccessException;

    invoke-direct {v1}, Ljava/lang/IllegalAccessException;-><init>()V

    throw v1

    :cond_0
    iget-object v1, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->decoder:Lcom/scalado/caps/autorama/PanoramaDecoder;

    invoke-direct {p0, v1}, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->nativeStep(Lcom/scalado/caps/autorama/PanoramaDecoder;)Z

    move-result v1

    if-eqz v1, :cond_1

    iget-object v1, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->decoder:Lcom/scalado/caps/autorama/PanoramaDecoder;

    iget-object v2, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->stitcher:Lcom/scalado/caps/autorama/Stitcher;

    invoke-static {v1, v2}, Lcom/scalado/caps/autorama/PanoramaDecoder;->access$1(Lcom/scalado/caps/autorama/PanoramaDecoder;Lcom/scalado/caps/autorama/Stitcher;)V

    const/4 v1, 0x1

    iput-boolean v1, p0, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->isDone:Z

    move v1, v3

    :goto_0
    return v1

    :cond_1
    invoke-direct {p0}, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->nativeGetTotalIterations()I

    move-result v1

    if-gtz v1, :cond_2

    move v1, v3

    goto :goto_0

    :cond_2
    invoke-direct {p0}, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->nativeGetIterationCount()I

    move-result v1

    int-to-float v1, v1

    invoke-direct {p0}, Lcom/scalado/caps/autorama/PanoramaDecoder$PanoramaDecoderIterator;->nativeGetTotalIterations()I

    move-result v2

    int-to-float v2, v2

    div-float v0, v1, v2

    cmpl-float v1, v0, v3

    if-lez v1, :cond_3

    move v1, v3

    goto :goto_0

    :cond_3
    move v1, v0

    goto :goto_0
.end method
