.class final enum Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;
.super Ljava/lang/Enum;
.source "StereoPanorama.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/scalado/caps/autorama/stereo/StereoPanorama;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x401a
    name = "DecoderSide"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Enum",
        "<",
        "Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;",
        ">;"
    }
.end annotation


# static fields
.field private static final synthetic ENUM$VALUES:[Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

.field public static final enum IS_LEFT:Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

.field public static final enum IS_RIGHT:Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;


# instance fields
.field private final value:I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    const/4 v3, 0x1

    const/4 v2, 0x0

    new-instance v0, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    const-string v1, "IS_LEFT"

    invoke-direct {v0, v1, v2, v2}, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;-><init>(Ljava/lang/String;II)V

    sput-object v0, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;->IS_LEFT:Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    new-instance v0, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    const-string v1, "IS_RIGHT"

    invoke-direct {v0, v1, v3, v3}, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;-><init>(Ljava/lang/String;II)V

    sput-object v0, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;->IS_RIGHT:Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    const/4 v0, 0x2

    new-array v0, v0, [Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    sget-object v1, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;->IS_LEFT:Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    aput-object v1, v0, v2

    sget-object v1, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;->IS_RIGHT:Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    aput-object v1, v0, v3

    sput-object v0, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;->ENUM$VALUES:[Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;II)V
    .locals 0

    invoke-direct {p0, p1, p2}, Ljava/lang/Enum;-><init>(Ljava/lang/String;I)V

    iput p3, p0, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;->value:I

    return-void
.end method

.method public static valueOf(Ljava/lang/String;)Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;
    .locals 1

    const-class v0, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    invoke-static {v0, p0}, Ljava/lang/Enum;->valueOf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;

    move-result-object p0

    check-cast p0, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    return-object p0
.end method

.method public static values()[Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;
    .locals 4

    const/4 v3, 0x0

    sget-object v0, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;->ENUM$VALUES:[Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    array-length v1, v0

    new-array v2, v1, [Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;

    invoke-static {v0, v3, v2, v3, v1}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    return-object v2
.end method


# virtual methods
.method public getValue()I
    .locals 1

    iget v0, p0, Lcom/scalado/caps/autorama/stereo/StereoPanorama$DecoderSide;->value:I

    return v0
.end method
