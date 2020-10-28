package io.nacular.measured.units

/**
 * Units to measure computer storage, bandwidth, etc..
 */
class BinarySize(suffix: String, ratio: Double = 1.0): Units(suffix, ratio) {
    operator fun div(other: GraphicsLength) = ratio / other.ratio

    companion object {
        val bytes     = BinarySize("B"                                  )

        // https://en.wikipedia.org/wiki/Kilobyte
        val kilobytes = BinarySize("kB",   1000.0                       )
        val megabytes = BinarySize("MB",   1000.0 * kilobytes `in` bytes)
        val gigabytes = BinarySize("GB",   1000.0 * megabytes `in` bytes)
        val terabytes = BinarySize("TB",   1000.0 * gigabytes `in` bytes)
        val petabytes = BinarySize("PB",   1000.0 * terabytes `in` bytes)

        val kibibytes = BinarySize("KiB",  1024.0                       )
        val mebibytes = BinarySize("MiB",  1024.0 * kibibytes `in` bytes)
        val gibibytes = BinarySize("GiB",  1024.0 * mebibytes `in` bytes)
        val tebibytes = BinarySize("TiB",  1024.0 * gibibytes `in` bytes)
        val pebibytes = BinarySize("PiB",  1024.0 * tebibytes `in` bytes)

        val bits      = BinarySize("bits", 1.0/8                        )

        // https://en.wikipedia.org/wiki/Kilobit
        val kilobits  = BinarySize("kbit", 1000.0 * bits      `in` bytes)
        val megabits  = BinarySize("Mbit", 1000.0 * kilobits  `in` bytes)
        val gigabits  = BinarySize("Gbit", 1000.0 * megabits  `in` bytes)
        val terabits  = BinarySize("Tbit", 1000.0 * gigabits  `in` bytes)
        val petabits  = BinarySize("Pbit", 1000.0 * terabits  `in` bytes)

        val kibibits  = BinarySize("Kibit",1024.0                       )
        val mebibits  = BinarySize("Mibit",1024.0 * kibibits  `in` bytes)
        val gibibits  = BinarySize("Gibit",1024.0 * mebibits  `in` bytes)
        val tebibits  = BinarySize("Tibit",1024.0 * gibibits  `in` bytes)
        val pebibits  = BinarySize("Tibit",1024.0 * tebibits  `in` bytes)
    }
}