.class public Lcom/scalado/caps/hdrimage/HDRImage$Registration;
.super Ljava/lang/Object;
.source "HDRImage.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/scalado/caps/hdrimage/HDRImage;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "Registration"
.end annotation


# static fields
.field public static final LIGHTEST:Lcom/scalado/caps/hdrimage/HDRImage$Registration;

.field public static final NONE:Lcom/scalado/caps/hdrimage/HDRImage$Registration;

.field public static final PREVIOUS:Lcom/scalado/caps/hdrimage/HDRImage$Registration;


# instance fields
.field private value:I


# direct methods
.method static constructor <clinit>()V
    .locals 2

    new-instance v0, Lcom/scalado/caps/hdrimage/HDRImage$Registration;

    const/4 v1, 0x0

    invoke-direct {v0, v1}, Lcom/scalado/caps/hdrimage/HDRImage$Registration;-><init>(I)V

    sput-object v0, Lcom/scalado/caps/hdrimage/HDRImage$Registration;->NONE:Lcom/scalado/caps/hdrimage/HDRImage$Registration;

    new-instance v0, Lcom/scalado/caps/hdrimage/HDRImage$Registration;

    const/4 v1, 0x1

    invoke-direct {v0, v1}, Lcom/scalado/caps/hdrimage/HDRImage$Registration;-><init>(I)V

    sput-object v0, Lcom/scalado/caps/hdrimage/HDRImage$Registration;->LIGHTEST:Lcom/scalado/caps/hdrimage/HDRImage$Registration;

    new-instance v0, Lcom/scalado/caps/hdrimage/HDRImage$Registration;

    const/4 v1, 0x2

    invoke-direct {v0, v1}, Lcom/scalado/caps/hdrimage/HDRImage$Registration;-><init>(I)V

    sput-object v0, Lcom/scalado/caps/hdrimage/HDRImage$Registration;->PREVIOUS:Lcom/scalado/caps/hdrimage/HDRImage$Registration;

    return-void
.end method

.method private constructor <init>(I)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput p1, p0, Lcom/scalado/caps/hdrimage/HDRImage$Registration;->value:I

    return-void
.end method

.method static synthetic access$0(Lcom/scalado/caps/hdrimage/HDRImage$Registration;)I
    .locals 1

    iget v0, p0, Lcom/scalado/caps/hdrimage/HDRImage$Registration;->value:I

    return v0
.end method
