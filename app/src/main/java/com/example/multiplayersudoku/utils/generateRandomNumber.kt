import kotlin.random.Random

fun generateRandomNumber(min: Int = 0, max: Int = 999_999): Int {
    return Random.nextInt(min, max)
}