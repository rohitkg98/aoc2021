sealed class Packet(val version: Long) {
    abstract fun versionSum(): Long
    abstract fun value(): Long

    companion object {
        fun parse(binary: String): List<Packet> {
            if (binary.isEmpty() || binary.length < 7) {
                return emptyList()
            }

            val version = binary.slice(0..2).toLong(2)
            val typeID = binary.slice(3..5).toLong(2)
            val packetData = binary.drop(6)

            return when (typeID) {
                4L -> parseLiteral(packetData, version)
                else -> parseOperator(packetData, version, typeID)
            }
        }

        private fun parseLiteral(packetData: String, version: Long): List<Packet> {
            val literalParts = packetData.chunked(5).takeWhileInclusive { it[0] != '0' }
            return listOf(
                Literal(
                    version, literalParts.fold("") { acc, num -> acc + num.drop(1) }.toLong(2)
                )
            ) + parse(packetData.drop(literalParts.size * 5))
        }

        private fun parseOperator(
            packetData: String,
            version: Long,
            typeID: Long
        ) = when (packetData.first()) {
            '0' -> { // next 15 bits are the length of the sub-packets
                val subPacketsLen = packetData.drop(1).take(15).toInt(2)

                listOf(Operator(version, typeID, parse(packetData.drop(16).take(subPacketsLen)))) + parse(
                    packetData.drop(16 + subPacketsLen)
                )
            }
            '1' -> { // next 11 bits are number of sub-packets in the packet
                val nSubPackets = packetData.drop(1).take(11).toInt(2)

                val rest = parse(packetData.drop(12))

                listOf(Operator(version, typeID, rest.take(nSubPackets))) + rest.drop(nSubPackets)
            }
            else -> throw Exception("Malformed Packet")
        }
    }

    class Literal(version: Long, val value: Long) : Packet(version) {
        override fun versionSum() = version
        override fun value() = value
    }

    class Operator(version: Long, val typeID: Long, val subPackets: List<Packet>) : Packet(version) {
        override fun versionSum() = version + subPackets.sumOf { it.versionSum() }
        override fun value(): Long {
            return when (typeID) {
                0L -> subPackets.sumOf { it.value() }
                1L -> subPackets.fold(1L) { acc, p -> acc * p.value() }
                2L -> subPackets.minOf { it.value() }
                3L -> subPackets.maxOf { it.value() }
                5L -> if (subPackets.first().value() > subPackets.last().value()) 1L else 0L
                6L -> if (subPackets.first().value() < subPackets.last().value()) 1L else 0L
                7L -> if (subPackets.first().value() == subPackets.last().value()) 1L else 0L
                else -> throw Exception("Malformed Packet")
            }
        }
    }

}

object Day16 : Day() {
    override fun main() {
        val binaryPackets = readInput("day16.txt").map {
            it.toString().toLong(16).toString(2).padStart(4, '0')
        }.joinToString("")

        val packets = Packet.parse(binaryPackets)

        part1(packets)

        part2(packets)
    }

    private fun part2(packets: List<Packet>) {
        println("the evaluation of the all packets is ${packets[0].value()}")
    }

    private fun part1(packets: List<Packet>) {
        val versionSum = packets.sumOf { it.versionSum() }

        println("version sum for the hierarchy of packets is $versionSum")
    }
}
