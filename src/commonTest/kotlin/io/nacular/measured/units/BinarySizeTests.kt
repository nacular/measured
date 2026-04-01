package io.nacular.measured.units

import io.nacular.measured.units.BinarySize.Companion.bits
import io.nacular.measured.units.BinarySize.Companion.bytes
import io.nacular.measured.units.BinarySize.Companion.gigabits
import io.nacular.measured.units.BinarySize.Companion.gigabytes
import io.nacular.measured.units.BinarySize.Companion.gibibits
import io.nacular.measured.units.BinarySize.Companion.gibibytes
import io.nacular.measured.units.BinarySize.Companion.kilobits
import io.nacular.measured.units.BinarySize.Companion.kilobytes
import io.nacular.measured.units.BinarySize.Companion.kibibits
import io.nacular.measured.units.BinarySize.Companion.kibibytes
import io.nacular.measured.units.BinarySize.Companion.megabits
import io.nacular.measured.units.BinarySize.Companion.megabytes
import io.nacular.measured.units.BinarySize.Companion.mebibits
import io.nacular.measured.units.BinarySize.Companion.mebibytes
import io.nacular.measured.units.BinarySize.Companion.petabits
import io.nacular.measured.units.BinarySize.Companion.petabytes
import io.nacular.measured.units.BinarySize.Companion.pebibits
import io.nacular.measured.units.BinarySize.Companion.pebibytes
import io.nacular.measured.units.BinarySize.Companion.terabits
import io.nacular.measured.units.BinarySize.Companion.terabytes
import io.nacular.measured.units.BinarySize.Companion.tebibits
import io.nacular.measured.units.BinarySize.Companion.tebibytes
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [BinarySize].
 *
 * Verifies:
 *  - Unit suffixes (display names)
 *  - SI decimal conversion ratios (bytes -> kilobytes -> … -> petabytes)
 *  - IEC binary conversion ratios (bytes -> kibibytes -> … -> pebibytes)
 *  - Bit-based units (bits, kilobits … petabits; kibibits … pebibits)
 *  - The [BinarySize.div] operator for ratio comparisons
 *  - Round-trip conversions using the `in` infix function
 */
class BinarySizeTest {
    // ─────────────────────────────────────────────────────────────────────────
    // Suffixes
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("bytes_suffix_is_B"       ) fun `bytes suffix is B`       () = assertEquals("B",     bytes.suffix    )
    @Test @JsName("kilobytes_suffix_is_kB"  ) fun `kilobytes suffix is kB`  () = assertEquals("kB",    kilobytes.suffix)
    @Test @JsName("megabytes_suffix_is_MB"  ) fun `megabytes suffix is MB`  () = assertEquals("MB",    megabytes.suffix)
    @Test @JsName("gigabytes_suffix_is_GB"  ) fun `gigabytes suffix is GB`  () = assertEquals("GB",    gigabytes.suffix)
    @Test @JsName("terabytes_suffix_is_TB"  ) fun `terabytes suffix is TB`  () = assertEquals("TB",    terabytes.suffix)
    @Test @JsName("petabytes_suffix_is_PB"  ) fun `petabytes suffix is PB`  () = assertEquals("PB",    petabytes.suffix)
    @Test @JsName("kibibytes_suffix_is_KiB" ) fun `kibibytes suffix is KiB` () = assertEquals("KiB",   kibibytes.suffix)
    @Test @JsName("mebibytes_suffix_is_MiB" ) fun `mebibytes suffix is MiB` () = assertEquals("MiB",   mebibytes.suffix)
    @Test @JsName("gibibytes_suffix_is_GiB" ) fun `gibibytes suffix is GiB` () = assertEquals("GiB",   gibibytes.suffix)
    @Test @JsName("tebibytes_suffix_is_TiB" ) fun `tebibytes suffix is TiB` () = assertEquals("TiB",   tebibytes.suffix)
    @Test @JsName("pebibytes_suffix_is_PiB" ) fun `pebibytes suffix is PiB` () = assertEquals("PiB",   pebibytes.suffix)
    @Test @JsName("bits_suffix_is_bits"     ) fun `bits suffix is bits`     () = assertEquals("bits",  bits.suffix     )
    @Test @JsName("kilobits_suffix_is_kbit" ) fun `kilobits suffix is kbit` () = assertEquals("kbit",  kilobits.suffix )
    @Test @JsName("megabits_suffix_is_Mbit" ) fun `megabits suffix is Mbit` () = assertEquals("Mbit",  megabits.suffix )
    @Test @JsName("gigabits_suffix_is_Gbit" ) fun `gigabits suffix is Gbit` () = assertEquals("Gbit",  gigabits.suffix )
    @Test @JsName("terabits_suffix_is_Tbit" ) fun `terabits suffix is Tbit` () = assertEquals("Tbit",  terabits.suffix )
    @Test @JsName("petabits_suffix_is_Pbit" ) fun `petabits suffix is Pbit` () = assertEquals("Pbit",  petabits.suffix )
    @Test @JsName("kibibits_suffix_is_Kibit") fun `kibibits suffix is Kibit`() = assertEquals("Kibit", kibibits.suffix )
    @Test @JsName("mebibits_suffix_is_Mibit") fun `mebibits suffix is Mibit`() = assertEquals("Mibit", mebibits.suffix )
    @Test @JsName("gibibits_suffix_is_Gibit") fun `gibibits suffix is Gibit`() = assertEquals("Gibit", gibibits.suffix )
    @Test @JsName("tebibits_suffix_is_Tibit") fun `tebibits suffix is Tibit`() = assertEquals("Tibit", tebibits.suffix )
    @Test @JsName("pebibits_suffix_is_Pibit") fun `pebibits suffix is Pibit`() = assertEquals("Pibit", pebibits.suffix )

    // ─────────────────────────────────────────────────────────────────────────
    // SI decimal byte ratios  (all expressed in bytes)
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("bytes_ratio_is_1"                  ) fun `bytes ratio is 1`                  () = assertApprox(1.0,                     bytes.ratio    )
    @Test @JsName("kilobytes_ratio_is_1000_bytes"     ) fun `kilobytes ratio is 1000 bytes`     () = assertApprox(1_000.0,                 kilobytes.ratio)
    @Test @JsName("megabytes_ratio_is_1_000_000_bytes") fun `megabytes ratio is 1 000 000 bytes`() = assertApprox(1_000_000.0,             megabytes.ratio)
    @Test @JsName("gigabytes_ratio_is_1e9_bytes"      ) fun `gigabytes ratio is 1e9 bytes`      () = assertApprox(1_000_000_000.0,         gigabytes.ratio)
    @Test @JsName("terabytes_ratio_is_1e12_bytes"     ) fun `terabytes ratio is 1e12 bytes`     () = assertApprox(1_000_000_000_000.0,     terabytes.ratio)
    @Test @JsName("petabytes_ratio_is_1e15_bytes"     ) fun `petabytes ratio is 1e15 bytes`     () = assertApprox(1_000_000_000_000_000.0, petabytes.ratio)

    // ─────────────────────────────────────────────────────────────────────────
    // IEC binary byte ratios  (all expressed in bytes)
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("kibibytes_ratio_is_1024_bytes"           ) fun `kibibytes ratio is 1024 bytes`           () = assertApprox(1_024.0,                 kibibytes.ratio)
    @Test @JsName("mebibytes_ratio_is_1024_squared_bytes"   ) fun `mebibytes ratio is 1024 squared bytes`   () = assertApprox(1_048_576.0,             mebibytes.ratio)
    @Test @JsName("gibibytes_ratio_is_1024_cubed_bytes"     ) fun `gibibytes ratio is 1024 cubed bytes`     () = assertApprox(1_073_741_824.0,         gibibytes.ratio)
    @Test @JsName("tebibytes_ratio_is_1024_to_the_4th_bytes") fun `tebibytes ratio is 1024 to the 4th bytes`() = assertApprox(1_099_511_627_776.0,     tebibytes.ratio)
    @Test @JsName("pebibytes_ratio_is_1024_to_the_5th_bytes") fun `pebibytes ratio is 1024 to the 5th bytes`() = assertApprox(1_125_899_906_842_624.0, pebibytes.ratio)

    // ─────────────────────────────────────────────────────────────────────────
    // Bit-based ratios (base unit: bytes, so 1 bit = 1/8 byte)
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("bits_ratio_is_one_eighth_of_a_byte"             ) fun `bits ratio is one eighth of a byte`             () = assertApprox(1.0                     / 8, bits.ratio    )
    @Test @JsName("kilobits_ratio_is_1000_bits_in_bytes"           ) fun `kilobits ratio is 1000 bits in bytes`           () = assertApprox(1_000.0                 / 8, kilobits.ratio)
    @Test @JsName("gigabits_ratio_is_1e9_bits_in_bytes"            ) fun `gigabits ratio is 1e9 bits in bytes`            () = assertApprox(1_000_000_000.0         / 8, gigabits.ratio)
    @Test @JsName("megabits_ratio_is_1e6_bits_in_bytes"            ) fun `megabits ratio is 1e6 bits in bytes`            () = assertApprox(1_000_000.0             / 8, megabits.ratio)
    @Test @JsName("terabits_ratio_is_1e12_bits_in_bytes"           ) fun `terabits ratio is 1e12 bits in bytes`           () = assertApprox(1_000_000_000_000.0     / 8, terabits.ratio)
    @Test @JsName("petabits_ratio_is_1e15_bits_in_bytes"           ) fun `petabits ratio is 1e15 bits in bytes`           () = assertApprox(1_000_000_000_000_000.0 / 8, petabits.ratio)
    @Test @JsName("kibibits_ratio_is_1024_bits_in_bytes"           ) fun `kibibits ratio is 1024 bits in bytes`           () = assertApprox(1_024.0                 / 8, kibibits.ratio)
    @Test @JsName("mebibits_ratio_is_1024_squared_bits_in_bytes"   ) fun `mebibits ratio is 1024 squared bits in bytes`   () = assertApprox(1_048_576.0             / 8, mebibits.ratio)
    @Test @JsName("gibibits_ratio_is_1024_cubed_bits_in_bytes"     ) fun `gibibits ratio is 1024 cubed bits in bytes`     () = assertApprox(1_073_741_824.0         / 8, gibibits.ratio)
    @Test @JsName("tebibits_ratio_is_1024_to_the_4th_bits_in_bytes") fun `tebibits ratio is 1024 to the 4th bits in bytes`() = assertApprox(1_099_511_627_776.0     / 8, tebibits.ratio)

    // ─────────────────────────────────────────────────────────────────────────
    // div operator – unit-to-unit ratio comparisons
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("kilobytes_div_bytes_equals_1000"              ) fun `kilobytes div bytes equals 1000`              () = assertApprox(1_000.0,       kilobytes / bytes    )
    @Test @JsName("megabytes_div_kilobytes_equals_1000"          ) fun `megabytes div kilobytes equals 1000`          () = assertApprox(1_000.0,       megabytes / kilobytes)
    @Test @JsName("kibibytes_div_bytes_equals_1024"              ) fun `kibibytes div bytes equals 1024`              () = assertApprox(1_024.0,       kibibytes / bytes    )
    @Test @JsName("mebibytes_div_kibibytes_equals_1024"          ) fun `mebibytes div kibibytes equals 1024`          () = assertApprox(1_024.0,       mebibytes / kibibytes)
    @Test @JsName("bytes_div_kilobytes_is_the_reciprocal_of_1000") fun `bytes div kilobytes is the reciprocal of 1000`() = assertApprox(1.0 / 1_000.0, bytes     / kilobytes)
    @Test @JsName("bits_div_bytes_equals_1_eighth"               ) fun `bits div bytes equals 1 eighth`               () = assertApprox(1.0 / 8.0,     bits      / bytes    )
    @Test @JsName("bytes_div_bits_equals_8"                      ) fun `bytes div bits equals 8`                      () = assertApprox(8.0,           bytes     / bits     )
    @Test @JsName("kilobytes_div_kilobits_equals_8"              ) fun `kilobytes div kilobits equals 8`              () = assertApprox(8.0,           kilobytes / kilobits, "1 kilobyte should equal 8 kilobits")

    // ─────────────────────────────────────────────────────────────────────────
    // Measure conversion – using `in` infix
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("one_kilobyte_in_bytes_is_1000"     ) fun `1 kilobyte in bytes is 1000`    () = assertApprox(1_000.0, 1 * kilobytes `in` bytes    )
    @Test @JsName("one_megabyte_in_kilobytes_is_1000" ) fun `1 megabyte in kilobytes is 1000`() = assertApprox(1_000.0, 1 * megabytes `in` kilobytes)
    @Test @JsName("one_kibibyte_in_bytes_is_1024"     ) fun `1 kibibyte in bytes is 1024`    () = assertApprox(1_024.0, 1 * kibibytes `in` bytes    )
    @Test @JsName("one_mebibyte_in_kibibytes_is_1024" ) fun `1 mebibyte in kibibytes is 1024`() = assertApprox(1_024.0, 1 * mebibytes `in` kibibytes)
    @Test @JsName("eight_bits_in_bytes_is_1"          ) fun `8 bits in bytes is 1`           () = assertApprox(1.0,     8 * bits      `in` bytes    )
    @Test @JsName("one_byte_in_bits_is_8"             ) fun `1 byte in bits is 8`            () = assertApprox(8.0,     1 * bytes     `in` bits     )
    @Test @JsName("one_gigabyte_in_megabytes_is_1000" ) fun `1 gigabyte in megabytes is 1000`() = assertApprox(1_000.0, 1 * gigabytes `in` megabytes)
    @Test @JsName("one_gibibyte_in_mebibytes_is_1024" ) fun `1 gibibyte in mebibytes is 1024`() = assertApprox(1_024.0, 1 * gibibytes `in` mebibytes)
    @Test @JsName("one_megabit_in_kilobits_is_1000"   ) fun `1 megabit in kilobits is 1000`  () = assertApprox(1_000.0, 1 * megabits  `in` kilobits )
    @Test @JsName("one_mebibit_in_kibibits_is_1024"   ) fun `1 mebibit in kibibits is 1024`  () = assertApprox(1_024.0, 1 * mebibits  `in` kibibits )

    // ─────────────────────────────────────────────────────────────────────────
    // Cross-unit conversions (bits ↔ bytes across SI and IEC)
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("one_kilobyte_in_kilobits_is_8"                   ) fun `1 kilobyte in kilobits is 8`            () = assertApprox(8.0,     1   * kilobytes `in` kilobits )
    @Test @JsName("one_megabyte_in_megabits_is_8"                   ) fun `1 megabyte in megabits is 8`            () = assertApprox(8.0,     1   * megabytes `in` megabits )
    @Test @JsName("five_hundred_kilobytes_in_megabytes_is_0_point_5") fun `500 kilobytes in megabytes is 0 point 5`() = assertApprox(0.5,     500 * kilobytes `in` megabytes)
    @Test @JsName("one_terabyte_in_gigabytes_is_1000"               ) fun `1 terabyte in gigabytes is 1000`        () = assertApprox(1_000.0, 1   * terabytes `in` gigabytes)
    @Test @JsName("one_petabyte_in_terabytes_is_1000"               ) fun `1 petabyte in terabytes is 1000`        () = assertApprox(1_000.0, 1   * petabytes `in` terabytes)
    @Test @JsName("one_pebibyte_in_tebibytes_is_1024"               ) fun `1 pebibyte in tebibytes is 1024`        () = assertApprox(1_024.0, 1   * pebibytes `in` tebibytes)

    // ─────────────────────────────────────────────────────────────────────────
    // Arithmetic on Measure values
    // ─────────────────────────────────────────────────────────────────────────

    @Test @JsName("adding_two_byte_measures_produces_correct_sum"           ) fun `adding two byte measures produces correct sum`           () = assertApprox(500.0,   200 * bytes     + 300 * bytes `in` bytes    )
    @Test @JsName("adding_kilobytes_and_bytes_produces_correct_sum_in_bytes") fun `adding kilobytes and bytes produces correct sum in bytes`() = assertApprox(1_500.0, 1   * kilobytes + 500 * bytes `in` bytes    )
    @Test @JsName("subtracting_byte_measures_produces_correct_result"       ) fun `subtracting byte measures produces correct result`       () = assertApprox(800.0,   1   * kilobytes - 200 * bytes `in` bytes    )
    @Test @JsName("scaling_a_measure_by_a_scalar"                           ) fun `scaling a measure by a scalar`                           () = assertApprox(10.0,    2   * (5 * megabytes)         `in` megabytes)
}