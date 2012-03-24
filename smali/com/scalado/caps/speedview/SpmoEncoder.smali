.class public Lcom/scalado/caps/speedview/SpmoEncoder;
.super Lcom/scalado/caps/codec/encoder/JpegEncoder;
.source "SpmoEncoder.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/scalado/caps/speedview/SpmoEncoder$BaseSpmoEncoderStep;,
        Lcom/scalado/caps/speedview/SpmoEncoder$JpegEncoderRenderIterator2;,
        Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;,
        Lcom/scalado/caps/speedview/SpmoEncoder$SpmoEncoderIterator;
    }
.end annotation


# static fields
.field private static final COPY_CHUNK_SIZE:I = 0x6400

.field private static final DEFAULT_MODE:I = 0x0

.field private static final DEFAULT_QUALITY:I = 0x5

.field private static final TMP_JPEG_SIZE:I = 0x80000

.field private static final TRANSCODE_DST:I = 0x1

.field private static final TRANSCODE_INTERMEDIATE_BUFFER:I


# instance fields
.field private spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

.field private transcodeMode:I


# direct methods
.method public constructor <init>(Lcom/scalado/stream/Stream;Lcom/scalado/base/Size;)V
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    invoke-direct {p0}, Lcom/scalado/caps/codec/encoder/JpegEncoder;-><init>()V

    const/4 v0, 0x0

    iput v0, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->transcodeMode:I

    new-instance v0, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    const/4 v1, 0x0

    invoke-direct {v0, p2, v1, p1}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;-><init>(Lcom/scalado/base/Size;Lcom/scalado/stream/Stream;Lcom/scalado/stream/Stream;)V

    iput-object v0, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    return-void
.end method

.method public constructor <init>(Lcom/scalado/stream/Stream;Lcom/scalado/base/Size;Lcom/scalado/base/Size;)V
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    invoke-direct {p0, p1, p2}, Lcom/scalado/caps/codec/encoder/JpegEncoder;-><init>(Lcom/scalado/stream/Stream;Lcom/scalado/base/Size;)V

    const/4 v0, 0x0

    iput v0, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->transcodeMode:I

    new-instance v0, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    const/4 v1, 0x0

    invoke-direct {v0, p3, p1, v1}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;-><init>(Lcom/scalado/base/Size;Lcom/scalado/stream/Stream;Lcom/scalado/stream/Stream;)V

    iput-object v0, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    return-void
.end method

.method public constructor <init>(Lcom/scalado/stream/Stream;Lcom/scalado/stream/Stream;Lcom/scalado/base/Size;Lcom/scalado/base/Size;)V
    .locals 1
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    invoke-direct {p0, p1, p3}, Lcom/scalado/caps/codec/encoder/JpegEncoder;-><init>(Lcom/scalado/stream/Stream;Lcom/scalado/base/Size;)V

    const/4 v0, 0x0

    iput v0, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->transcodeMode:I

    new-instance v0, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    invoke-direct {v0, p4, p1, p2}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;-><init>(Lcom/scalado/base/Size;Lcom/scalado/stream/Stream;Lcom/scalado/stream/Stream;)V

    iput-object v0, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    return-void
.end method


# virtual methods
.method protected render(Lcom/scalado/caps/Session;)Lcom/scalado/base/Iterator;
    .locals 12

    invoke-virtual {p1}, Lcom/scalado/caps/Session;->getDecoder()Lcom/scalado/caps/Decoder;

    move-result-object v4

    invoke-virtual {p0, p0, v4}, Lcom/scalado/caps/speedview/SpmoEncoder;->applyProperties(Lcom/scalado/caps/codec/encoder/JpegEncoder;Lcom/scalado/caps/Decoder;)V

    invoke-virtual {v4}, Lcom/scalado/caps/Decoder;->getDimensions()Lcom/scalado/base/Size;

    move-result-object v3

    iget-object v10, p0, Lcom/scalado/caps/codec/encoder/JpegEncoder;->dimensions:Lcom/scalado/base/Size;

    if-nez v10, :cond_0

    iput-object v3, p0, Lcom/scalado/caps/codec/encoder/JpegEncoder;->dimensions:Lcom/scalado/base/Size;

    :cond_0
    const/4 v2, 0x0

    instance-of v10, v4, Lcom/scalado/caps/codec/decoder/JpegDecoder;

    if-nez v10, :cond_2

    const/4 v2, 0x1

    :cond_1
    :goto_0
    const/4 v8, 0x0

    if-eqz v2, :cond_5

    invoke-virtual {p0, v4}, Lcom/scalado/caps/speedview/SpmoEncoder;->renderFromDecoder(Lcom/scalado/caps/Decoder;)Lcom/scalado/base/Iterator;

    move-result-object v10

    :goto_1
    return-object v10

    :cond_2
    invoke-virtual {v4}, Lcom/scalado/caps/Decoder;->hasChanged()Z

    move-result v10

    if-eqz v10, :cond_3

    const/4 v2, 0x1

    goto :goto_0

    :cond_3
    invoke-virtual {v3}, Lcom/scalado/base/Size;->getWidth()I

    move-result v10

    iget-object v11, p0, Lcom/scalado/caps/codec/encoder/JpegEncoder;->dimensions:Lcom/scalado/base/Size;

    invoke-virtual {v11}, Lcom/scalado/base/Size;->getWidth()I

    move-result v11

    if-ne v10, v11, :cond_4

    invoke-virtual {v3}, Lcom/scalado/base/Size;->getHeight()I

    move-result v10

    iget-object v11, p0, Lcom/scalado/caps/codec/encoder/JpegEncoder;->dimensions:Lcom/scalado/base/Size;

    invoke-virtual {v11}, Lcom/scalado/base/Size;->getHeight()I

    move-result v11

    if-eq v10, v11, :cond_1

    :cond_4
    const/4 v2, 0x1

    goto :goto_0

    :cond_5
    move-object v0, v4

    check-cast v0, Lcom/scalado/caps/codec/decoder/JpegDecoder;

    move-object v7, v0

    invoke-virtual {v7}, Lcom/scalado/caps/codec/decoder/JpegDecoder;->getStream()Lcom/scalado/stream/Stream;

    move-result-object v10

    instance-of v10, v10, Lcom/scalado/stream/BufferStream;

    if-eqz v10, :cond_6

    invoke-virtual {v7}, Lcom/scalado/caps/codec/decoder/JpegDecoder;->getStream()Lcom/scalado/stream/Stream;

    move-result-object v1

    check-cast v1, Lcom/scalado/stream/BufferStream;

    new-instance v8, Lcom/scalado/stream/BufferStream;

    invoke-virtual {v1}, Lcom/scalado/stream/BufferStream;->getBuffer()Lcom/scalado/base/Buffer;

    move-result-object v10

    const/4 v11, 0x0

    invoke-direct {v8, v10, v11}, Lcom/scalado/stream/BufferStream;-><init>(Lcom/scalado/base/Buffer;I)V

    :goto_2
    invoke-virtual {p0, v8}, Lcom/scalado/caps/speedview/SpmoEncoder;->renderFromStream(Lcom/scalado/stream/Stream;)Lcom/scalado/base/Iterator;

    move-result-object v10

    goto :goto_1

    :cond_6
    invoke-virtual {v7}, Lcom/scalado/caps/codec/decoder/JpegDecoder;->getStream()Lcom/scalado/stream/Stream;

    move-result-object v5

    check-cast v5, Lcom/scalado/stream/FileStream;

    :try_start_0
    new-instance v9, Lcom/scalado/stream/FileStream;

    invoke-virtual {v5}, Lcom/scalado/stream/FileStream;->getFilename()Ljava/lang/String;

    move-result-object v10

    sget-object v11, Lcom/scalado/stream/FileStream$Access;->READ:Lcom/scalado/stream/FileStream$Access;

    invoke-direct {v9, v10, v11}, Lcom/scalado/stream/FileStream;-><init>(Ljava/lang/String;Lcom/scalado/stream/FileStream$Access;)V
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_0

    move-object v8, v9

    goto :goto_2

    :catch_0
    move-exception v10

    move-object v6, v10

    const/4 v10, 0x0

    goto :goto_1
.end method

.method public render(Lcom/scalado/stream/Stream;)Lcom/scalado/base/Iterator;
    .locals 1

    invoke-virtual {p0, p1}, Lcom/scalado/caps/speedview/SpmoEncoder;->renderFromStream(Lcom/scalado/stream/Stream;)Lcom/scalado/base/Iterator;

    move-result-object v0

    return-object v0
.end method

.method protected renderFromDecoder(Lcom/scalado/caps/Decoder;)Lcom/scalado/base/Iterator;
    .locals 10

    const/4 v9, 0x1

    const/4 v8, 0x0

    iget-object v1, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    invoke-static {v1}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;->access$2(Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;)Lcom/scalado/stream/Stream;

    move-result-object v1

    if-eqz v1, :cond_0

    iget-object v1, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    invoke-static {v1}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;->access$3(Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;)Lcom/scalado/stream/Stream;

    move-result-object v1

    if-eqz v1, :cond_0

    move v5, v9

    :goto_0
    new-instance v0, Lcom/scalado/caps/speedview/SpmoEncoder$JpegEncoderRenderIterator2;

    iget-object v1, p0, Lcom/scalado/caps/codec/encoder/JpegEncoder;->dimensions:Lcom/scalado/base/Size;

    iget-object v2, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    invoke-static {v2}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;->access$2(Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;)Lcom/scalado/stream/Stream;

    move-result-object v3

    iget v4, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->transcodeMode:I

    const/4 v6, 0x0

    move-object v2, p1

    invoke-direct/range {v0 .. v6}, Lcom/scalado/caps/speedview/SpmoEncoder$JpegEncoderRenderIterator2;-><init>(Lcom/scalado/base/Size;Lcom/scalado/caps/Decoder;Lcom/scalado/stream/Stream;IZLcom/scalado/caps/speedview/SpmoEncoder$JpegEncoderRenderIterator2;)V

    invoke-virtual {v0}, Lcom/scalado/caps/speedview/SpmoEncoder$JpegEncoderRenderIterator2;->begin()V

    const/4 v1, 0x2

    new-array v7, v1, [Lcom/scalado/caps/speedview/SpmoEncoder$BaseSpmoEncoderStep;

    aput-object v0, v7, v8

    iget-object v1, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    aput-object v1, v7, v9

    new-instance v1, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoEncoderIterator;

    invoke-direct {v1, v7}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoEncoderIterator;-><init>([Lcom/scalado/caps/speedview/SpmoEncoder$BaseSpmoEncoderStep;)V

    return-object v1

    :cond_0
    move v5, v8

    goto :goto_0
.end method

.method protected renderFromStream(Lcom/scalado/stream/Stream;)Lcom/scalado/base/Iterator;
    .locals 3

    iget-object v1, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    invoke-static {v1, p1}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;->access$4(Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;Lcom/scalado/stream/Stream;)V

    iget-object v1, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    invoke-virtual {v1}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;->begin()V

    const/4 v1, 0x1

    new-array v0, v1, [Lcom/scalado/caps/speedview/SpmoEncoder$BaseSpmoEncoderStep;

    const/4 v1, 0x0

    iget-object v2, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    aput-object v2, v0, v1

    new-instance v1, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoEncoderIterator;

    invoke-direct {v1, v0}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoEncoderIterator;-><init>([Lcom/scalado/caps/speedview/SpmoEncoder$BaseSpmoEncoderStep;)V

    return-object v1
.end method

.method public setMode(I)V
    .locals 1

    if-ltz p1, :cond_0

    const/16 v0, 0xa

    if-le p1, v0, :cond_1

    :cond_0
    new-instance v0, Ljava/lang/IllegalArgumentException;

    invoke-direct {v0}, Ljava/lang/IllegalArgumentException;-><init>()V

    throw v0

    :cond_1
    iget-object v0, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    invoke-static {v0, p1}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;->access$0(Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;I)V

    return-void
.end method

.method public setQuality(I)V
    .locals 1

    if-ltz p1, :cond_0

    const/16 v0, 0xa

    if-le p1, v0, :cond_1

    :cond_0
    new-instance v0, Ljava/lang/IllegalArgumentException;

    invoke-direct {v0}, Ljava/lang/IllegalArgumentException;-><init>()V

    throw v0

    :cond_1
    iget-object v0, p0, Lcom/scalado/caps/speedview/SpmoEncoder;->spmoCreateStep:Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;

    invoke-static {v0, p1}, Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;->access$1(Lcom/scalado/caps/speedview/SpmoEncoder$SpmoCreateIterator;I)V

    return-void
.end method
