import kotlin.random.Random

fun generateRandomNumber(min: Int, max: Int): Int {
    val min = 0
    val max = 999_999

    return Random.nextInt(min, max + 1)
}