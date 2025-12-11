@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun AnimatedShape(shape1: RoundedPolygon, shape2: RoundedPolygon) {
    val infiniteAnimation = rememberInfiniteTransition(label = "infinite animation")
    val morphProgress = infiniteAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "morph"
    )

    Box(
        modifier = Modifier
            .drawWithCache {
                val morph = Morph(start = shape1, end = shape2)
                val morphPath = morph
                    .toPath(progress = morphProgress.value)
                    .asComposePath()

                onDrawBehind {
                    drawPath(morphPath, color = Color.Black)
                }
            }
            .fillMaxSize()
    )
}