/**
 * Takes an integer and returns it as a string, padded with leading zeros 
 * up to the specified total length.
 * * @param number The integer to pad (e.g., 4)
 * @param totalLength The desired final length of the string (e.g., 6)
 * @return The zero-padded string (e.g., "000004")
 */
fun padWithZeros(number: Int, totalLength: Int): String {
    // 1. Convert the Int to a String
    // 2. Use padStart to add characters ('0') to the beginning 
    //    until the totalLength is reached.
    return number.toString().padStart(totalLength, '0')
}