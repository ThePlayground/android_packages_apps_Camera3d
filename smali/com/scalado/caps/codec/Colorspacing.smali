.class public final Lcom/scalado/caps/codec/Colorspacing;
.super Ljava/lang/Object;
.source "Colorspacing.java"


# static fields
.field public static YUV400:Lcom/scalado/caps/codec/Colorspacing;

.field public static YUV420:Lcom/scalado/caps/codec/Colorspacing;

.field public static YUV422:Lcom/scalado/caps/codec/Colorspacing;

.field public static YUV444:Lcom/scalado/caps/codec/Colorspacing;


# instance fields
.field value:I


# direct methods
.method static constructor <clinit>()V
    .locals 2

    new-instance v0, Lcom/scalado/caps/codec/Colorspacing;

    const v1, 0x221111

    invoke-direct {v0, v1}, Lcom/scalado/caps/codec/Colorspacing;-><init>(I)V

    sput-object v0, Lcom/scalado/caps/codec/Colorspacing;->YUV420:Lcom/scalado/caps/codec/Colorspacing;

    new-instance v0, Lcom/scalado/caps/codec/Colorspacing;

    const v1, 0x211111

    invoke-direct {v0, v1}, Lcom/scalado/caps/codec/Colorspacing;-><init>(I)V

    sput-object v0, Lcom/scalado/caps/codec/Colorspacing;->YUV422:Lcom/scalado/caps/codec/Colorspacing;

    new-instance v0, Lcom/scalado/caps/codec/Colorspacing;

    const v1, 0x111111

    invoke-direct {v0, v1}, Lcom/scalado/caps/codec/Colorspacing;-><init>(I)V

    sput-object v0, Lcom/scalado/caps/codec/Colorspacing;->YUV444:Lcom/scalado/caps/codec/Colorspacing;

    new-instance v0, Lcom/scalado/caps/codec/Colorspacing;

    const/high16 v1, 0x11

    invoke-direct {v0, v1}, Lcom/scalado/caps/codec/Colorspacing;-><init>(I)V

    sput-object v0, Lcom/scalado/caps/codec/Colorspacing;->YUV400:Lcom/scalado/caps/codec/Colorspacing;

    return-void
.end method

.method private constructor <init>(I)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput p1, p0, Lcom/scalado/caps/codec/Colorspacing;->value:I

    return-void
.end method


# virtual methods
.method public value()I
    .locals 1

    iget v0, p0, Lcom/scalado/caps/codec/Colorspacing;->value:I

    return v0
.end method
