import Action.*

sealed class Action {
    data class Explode(val n: SnailfishNumber.BothInteger) : Action()
    data class AddToRight(val n: Int) : Action()
    data class AddToLeft(val n: Int) : Action()
    object None : Action()
    object Complete : Action()
}

sealed class SnailfishNumber {
    abstract fun magnitude(): Int
    abstract fun reduce(n: Int, split: Boolean): Pair<SnailfishNumber, Action>
    abstract fun addToLeftMost(n: Int): SnailfishNumber
    abstract fun addToRightMost(n: Int): SnailfishNumber

    fun split(n: Int): BothInteger = (n / 2) snail ((n / 2) + (n % 2))

    data class BothInteger(val left: Int, val right: Int) : SnailfishNumber() {
        override fun magnitude() = 3 * left + 2 * right
        override fun addToLeftMost(n: Int) = if (n == 0) this else copy(left = left + n)
        override fun addToRightMost(n: Int) = if (n == 0) this else copy(right = right + n)
        override fun reduce(n: Int, split: Boolean): Pair<SnailfishNumber, Action> {
            return when {
                n >= 4 -> this to Explode(this)
                split && left >= 10 -> RightInteger(split(left), right) to Complete
                split && right >= 10 -> LeftInteger(left, split(right)) to Complete
                else -> this to None
            }
        }
    }

    data class LeftInteger(val left: Int, val right: SnailfishNumber) : SnailfishNumber() {
        override fun magnitude() = 3 * left + 2 * right.magnitude()
        override fun reduce(n: Int, split: Boolean): Pair<SnailfishNumber, Action> {
            if (split && left >= 10) return BothSnailfish(split(left), right) to Complete

            val (newNum, a) = this.right.reduce(n + 1, split)
            return when (a) {
                None, Complete, is AddToRight -> copy(right = newNum) to a
                is Explode -> left + a.n.left snail 0 to AddToRight(a.n.right)
                is AddToLeft -> left + a.n snail newNum to Complete
            }
        }

        override fun addToLeftMost(n: Int) = if (n == 0) this else copy(left = left + n)
        override fun addToRightMost(n: Int) = if (n == 0) this else copy(right = right.addToRightMost(n))
    }

    data class RightInteger(val left: SnailfishNumber, val right: Int) : SnailfishNumber() {
        override fun magnitude() = 3 * left.magnitude() + 2 * right
        override fun reduce(n: Int, split: Boolean): Pair<SnailfishNumber, Action> {
            val (newNum, a) = this.left.reduce(n + 1, split)
            return when (a) {
                None -> if (split && right >= 10) BothSnailfish(left, split(right)) to Complete
                else this to a
                Complete, is AddToLeft -> copy(left = newNum) to a
                is Explode -> 0 snail right + a.n.right to AddToLeft(a.n.left)
                is AddToRight -> newNum snail right + a.n to Complete
            }
        }

        override fun addToLeftMost(n: Int) = if (n == 0) this else copy(left = left.addToLeftMost(n))
        override fun addToRightMost(n: Int) = if (n == 0) this else copy(right = right + n)
    }

    data class BothSnailfish(val left: SnailfishNumber, val right: SnailfishNumber) : SnailfishNumber() {
        override fun magnitude() = 3 * left.magnitude() + 2 * right.magnitude()
        override fun reduce(n: Int, split: Boolean): Pair<SnailfishNumber, Action> {
            val (newNum, a) = left.reduce(n + 1, split)
            return when (a) {
                None -> { // impossible to get a right explode in both snailfish
                    val (rightNewNum, rightA) = right.reduce(n + 1, split)
                    if (rightA is AddToLeft) copy(left = left.addToRightMost(rightA.n), right = rightNewNum) to Complete
                    else copy(right = rightNewNum) to rightA
                }
                Complete, is AddToLeft -> copy(left = newNum) to a
                is Explode -> (0 snail right.addToLeftMost(a.n.right)) to if (a.n.left != 0) AddToLeft(a.n.left) else Complete
                is AddToRight -> newNum snail right.addToLeftMost(a.n) to Complete
            }
        }

        override fun addToLeftMost(n: Int) = if (n == 0) this else copy(left = left.addToLeftMost(n))
        override fun addToRightMost(n: Int) = if (n == 0) this else copy(right = right.addToRightMost(n))
    }

    fun reduce(): Pair<SnailfishNumber, Boolean> {
        val (snailfishNumber, a) = this.reduce(0, false)

        if (a is None) {
            val reduce = this.reduce(0, true)
            return reduce.first to (reduce.second !is None)
        }

        return snailfishNumber to true
    }

    fun reduceToSmallest(): SnailfishNumber {
        var (reduced, wasReduced) = reduce()
        while (wasReduced) {
            val new = reduced.reduce()
            reduced = new.first
            wasReduced = new.second
        }

        return reduced
    }

    fun debug(): String {
        return when (this) {
            is BothInteger -> "[$left, $right]"
            is BothSnailfish -> "[${left.debug()}, ${right.debug()}]"
            is LeftInteger -> "[$left, ${right.debug()}]"
            is RightInteger -> "[${left.debug()}, $right]"
        }
    }
}

infix fun SnailfishNumber.snail(right: Int) = SnailfishNumber.RightInteger(this, right)
infix fun SnailfishNumber.snail(right: SnailfishNumber) = SnailfishNumber.BothSnailfish(this, right)
infix fun Int.snail(right: Int): SnailfishNumber.BothInteger = SnailfishNumber.BothInteger(this, right)
infix fun Int.snail(right: SnailfishNumber): SnailfishNumber = SnailfishNumber.LeftInteger(this, right)

object Day18 : Day() {
    override fun main() {
        val final = day18Input.drop(1).fold(day18Input.first()) { acc, n ->
            (acc snail n).reduceToSmallest()
        }.reduceToSmallest()

        println("the total magnitude of the final sum is ${final.magnitude()}")

        val allPairs = day18Input.flatMap { i1 -> day18Input.map { i2 -> i1 to i2 } }
            .filter { it.first != it.second }

        val maxMagnitude = allPairs.maxOf { (n1, n2) -> (n1 snail n2).reduceToSmallest().magnitude() }

        println("the total magnitude of any two sum is $maxMagnitude")
    }
}

private val day18DummyInput = listOf(
    ((0 snail (5 snail 8)) snail ((1 snail 7) snail (9 snail 6))) snail ((4 snail (1 snail 2)) snail ((1 snail 4) snail 2)),
    (((5 snail (2 snail 8)) snail 4) snail (5 snail ((9 snail 9) snail 0))),
    (6 snail (((6 snail 2) snail (5 snail 6)) snail ((7 snail 6) snail (4 snail 7)))),
    (((6 snail (0 snail 7)) snail (0 snail 9)) snail (4 snail (9 snail (9 snail 0)))),
    (((7 snail (6 snail 4)) snail (3 snail (1 snail 3))) snail (((5 snail 5) snail 1) snail 9)),
    ((6 snail ((7 snail 3) snail (3 snail 2))) snail (((3 snail 8) snail (5 snail 7)) snail 4)),
    ((((5 snail 4) snail (7 snail 7)) snail 8) snail ((8 snail 3) snail 8)),
    ((9 snail 3) snail ((9 snail 9) snail (6 snail (4 snail 9)))),
    ((2 snail ((7 snail 7) snail 7)) snail ((5 snail 8) snail ((9 snail 3) snail (0 snail 2)))),
    ((((5 snail 2) snail 5) snail (8 snail (3 snail 7))) snail ((5 snail (7 snail 5)) snail (4 snail 4))),
)

private val day18DummyInput1 = listOf(
    (((0 snail (4 snail 5)) snail (0 snail 0)) snail (((4 snail 5) snail (2 snail 6)) snail (9 snail 5))),
    (7 snail (((3 snail 7) snail (4 snail 3)) snail ((6 snail 3) snail (8 snail 8)))),
    ((2 snail ((0 snail 8) snail (3 snail 4))) snail (((6 snail 7) snail 1) snail (7 snail (1 snail 6)))),
    ((((2 snail 4) snail 7) snail (6 snail (0 snail 5))) snail (((6 snail 8) snail (2 snail 8)) snail ((2 snail 1) snail (4 snail 5)))),
    (7 snail (5 snail ((3 snail 8) snail (1 snail 4)))),
    ((2 snail (2 snail 2)) snail (8 snail (8 snail 1))),
    (2 snail 9),
    (1 snail (((9 snail 3) snail 9) snail ((9 snail 0) snail (0 snail 7)))),
    (((5 snail (7 snail 4)) snail 7) snail 1),
    ((((4 snail 2) snail 2) snail 6) snail (8 snail 7)),
)

private val day18Input = listOf(
    (6 snail ((5 snail (7 snail 7)) snail ((8 snail 2) snail 2))),
    ((8 snail (0 snail 0)) snail (((1 snail 4) snail (2 snail 0)) snail ((2 snail 3) snail (8 snail 2)))),
    (((7 snail (6 snail 1)) snail (9 snail (7 snail 9))) snail ((6 snail 6) snail 2)),
    ((5 snail (2 snail 0)) snail (((9 snail 4) snail (6 snail 8)) snail (3 snail 9))),
    (((3 snail (0 snail 3)) snail (5 snail (9 snail 8))) snail ((5 snail (8 snail 1)) snail (1 snail 2))),
    (((5 snail (8 snail 5)) snail ((6 snail 3) snail 3)) snail ((1 snail (0 snail 9)) snail ((3 snail 0) snail (7 snail 3)))),
    ((((1 snail 2) snail 0) snail (8 snail (6 snail 6))) snail (6 snail (7 snail 5))),
    ((((0 snail 9) snail (5 snail 3)) snail ((9 snail 7) snail 8)) snail (9 snail ((1 snail 9) snail 1))),
    ((4 snail (6 snail (0 snail 8))) snail ((2 snail 9) snail 1)),
    (((5 snail 1) snail (6 snail (9 snail 5))) snail 8),
    (((4 snail (7 snail 0)) snail 1) snail (3 snail 3)),
    (2 snail ((3 snail 4) snail 6)),
    ((((0 snail 5) snail (7 snail 1)) snail ((7 snail 0) snail (1 snail 7))) snail 2),
    ((3 snail (7 snail 8)) snail ((1 snail 0) snail ((1 snail 7) snail 6))),
    (((7 snail (6 snail 3)) snail 1) snail (4 snail (1 snail (6 snail 8)))),
    ((((5 snail 5) snail (3 snail 5)) snail (5 snail (2 snail 6))) snail (3 snail (1 snail (3 snail 2)))),
    ((8 snail 2) snail 9),
    ((8 snail ((1 snail 9) snail 2)) snail ((8 snail (8 snail 4)) snail 3)),
    ((((8 snail 7) snail 9) snail 5) snail (((0 snail 6) snail 5) snail (6 snail 5))),
    ((2 snail 6) snail (0 snail ((9 snail 8) snail 6))),
    ((((7 snail 9) snail 0) snail ((1 snail 5) snail 9)) snail (((2 snail 2) snail 1) snail (3 snail 1))),
    ((8 snail (1 snail (9 snail 1))) snail (0 snail (0 snail (2 snail 4)))),
    ((((0 snail 2) snail 3) snail ((9 snail 4) snail 9)) snail ((3 snail 2) snail ((5 snail 7) snail (4 snail 8)))),
    ((((8 snail 0) snail 3) snail (9 snail (5 snail 9))) snail (4 snail 3)),
    (2 snail (7 snail ((3 snail 3) snail (7 snail 9)))),
    (3 snail (((6 snail 4) snail 0) snail 5)),
    ((1 snail ((9 snail 9) snail 9)) snail (((0 snail 3) snail (0 snail 6)) snail 0)),
    ((7 snail ((2 snail 3) snail 4)) snail (6 snail (9 snail 9))),
    (3 snail 5),
    ((((2 snail 3) snail 2) snail (1 snail (9 snail 9))) snail ((8 snail 2) snail (1 snail (2 snail 0)))),
    ((((7 snail 2) snail (3 snail 6)) snail ((9 snail 7) snail (8 snail 9))) snail (((5 snail 3) snail (5 snail 1)) snail 6)),
    (8 snail ((7 snail 0) snail ((8 snail 2) snail 5))),
    (((1 snail (1 snail 0)) snail ((3 snail 7) snail (4 snail 5))) snail (((6 snail 5) snail 2) snail 1)),
    (8 snail ((5 snail 0) snail 3)),
    (((4 snail 1) snail 9) snail (1 snail 5)),
    ((9 snail (5 snail (3 snail 7))) snail ((0 snail 4) snail 4)),
    ((((9 snail 6) snail 4) snail (8 snail (7 snail 5))) snail ((6 snail 9) snail 5)),
    (((7 snail (2 snail 5)) snail 0) snail (8 snail (0 snail 7))),
    (8 snail ((4 snail (1 snail 8)) snail ((8 snail 9) snail 3))),
    ((((3 snail 7) snail (7 snail 3)) snail ((5 snail 3) snail 1)) snail (((6 snail 7) snail (2 snail 7)) snail ((2 snail 6) snail (9 snail 0)))),
    (((9 snail (5 snail 4)) snail ((1 snail 7) snail 2)) snail ((1 snail 0) snail ((2 snail 6) snail 0))),
    ((((3 snail 2) snail (5 snail 3)) snail (9 snail (2 snail 0))) snail ((7 snail 6) snail ((8 snail 2) snail (0 snail 7)))),
    (((4 snail 0) snail 0) snail (9 snail 0)),
    ((((9 snail 4) snail (0 snail 9)) snail (2 snail 3)) snail 8),
    ((2 snail (8 snail (6 snail 7))) snail (((6 snail 1) snail (5 snail 1)) snail (3 snail (1 snail 1)))),
    ((6 snail ((8 snail 2) snail 7)) snail 5),
    ((((7 snail 3) snail (9 snail 7)) snail 5) snail (((0 snail 9) snail 4) snail (3 snail (0 snail 3)))),
    ((2 snail (6 snail 7)) snail (((9 snail 0) snail (6 snail 7)) snail 4)),
    ((((3 snail 1) snail 5) snail (7 snail (5 snail 1))) snail (((8 snail 4) snail 9) snail ((2 snail 7) snail (4 snail 6)))),
    ((8 snail 8) snail (((1 snail 4) snail (7 snail 3)) snail ((9 snail 6) snail 5))),
    ((((3 snail 0) snail 8) snail ((5 snail 1) snail (7 snail 8))) snail (((5 snail 0) snail (2 snail 2)) snail ((9 snail 0) snail (0 snail 7)))),
    ((1 snail 1) snail 7),
    ((4 snail 3) snail ((9 snail (7 snail 3)) snail (2 snail 3))),
    (((5 snail (3 snail 5)) snail 3) snail 7),
    (((5 snail (3 snail 2)) snail 1) snail (((3 snail 2) snail 8) snail (8 snail 5))),
    ((7 snail (6 snail 5)) snail (((9 snail 8) snail 7) snail 6)),
    (((7 snail 9) snail 0) snail (3 snail 4)),
    ((((5 snail 6) snail (7 snail 4)) snail (4 snail (1 snail 7))) snail (((8 snail 2) snail 7) snail (6 snail (4 snail 5)))),
    ((((8 snail 5) snail 1) snail ((7 snail 0) snail (9 snail 7))) snail (((5 snail 2) snail 3) snail 2)),
    ((((7 snail 3) snail 9) snail 7) snail (((8 snail 1) snail 9) snail 3)),
    (((6 snail 5) snail (4 snail (6 snail 1))) snail 7),
    ((((4 snail 0) snail 1) snail (4 snail 4)) snail ((1 snail 7) snail (3 snail 0))),
    (9 snail ((2 snail (9 snail 0)) snail 6)),
    ((((0 snail 0) snail 9) snail 2) snail (9 snail 3)),
    (5 snail (5 snail ((6 snail 6) snail (2 snail 6)))),
    ((((8 snail 2) snail (8 snail 9)) snail ((8 snail 4) snail (8 snail 0))) snail (((9 snail 5) snail 6) snail 4)),
    (((3 snail (6 snail 1)) snail (3 snail (6 snail 9))) snail (3 snail 4)),
    (((7 snail (6 snail 3)) snail ((6 snail 7) snail 1)) snail ((1 snail 1) snail 2)),
    ((((1 snail 2) snail (7 snail 1)) snail ((7 snail 7) snail 4)) snail 3),
    ((((7 snail 2) snail 4) snail ((4 snail 7) snail (2 snail 4))) snail (1 snail ((6 snail 2) snail 4))),
    (4 snail ((0 snail 4) snail 5)),
    (((4 snail 6) snail (0 snail (8 snail 2))) snail ((4 snail (8 snail 7)) snail ((7 snail 9) snail 0))),
    (((9 snail 2) snail (5 snail 2)) snail 7),
    ((9 snail (2 snail 2)) snail 4),
    (((6 snail 2) snail ((4 snail 3) snail (3 snail 9))) snail ((4 snail 1) snail ((9 snail 4) snail 4))),
    (((7 snail (4 snail 2)) snail 4) snail ((8 snail (9 snail 3)) snail ((9 snail 0) snail (5 snail 4)))),
    ((((4 snail 1) snail (4 snail 6)) snail (2 snail (4 snail 5))) snail ((1 snail (1 snail 5)) snail 9)),
    ((((5 snail 2) snail 8) snail 6) snail (1 snail ((8 snail 9) snail 4))),
    (((6 snail (4 snail 2)) snail (6 snail (5 snail 5))) snail (0 snail 3)),
    ((7 snail (5 snail (7 snail 0))) snail (((7 snail 2) snail 0) snail ((7 snail 3) snail 8))),
    ((3 snail ((2 snail 3) snail (2 snail 8))) snail (5 snail 7)),
    ((((8 snail 0) snail (6 snail 4)) snail ((3 snail 7) snail 3)) snail (((7 snail 3) snail (9 snail 8)) snail ((8 snail 0) snail 8))),
    ((2 snail (5 snail 2)) snail (((0 snail 9) snail (4 snail 1)) snail ((8 snail 8) snail 4))),
    ((((4 snail 1) snail (6 snail 5)) snail ((2 snail 7) snail (5 snail 8))) snail (((7 snail 1) snail (2 snail 3)) snail 2)),
    ((3 snail 5) snail 2),
    ((9 snail 2) snail ((6 snail (1 snail 9)) snail (9 snail 5))),
    ((4 snail (3 snail 8)) snail ((4 snail (0 snail 3)) snail (1 snail 1))),
    (((6 snail (0 snail 7)) snail ((0 snail 4) snail (6 snail 1))) snail ((8 snail 5) snail ((5 snail 2) snail (7 snail 2)))),
    ((((8 snail 8) snail (6 snail 3)) snail ((0 snail 2) snail (6 snail 5))) snail (((7 snail 6) snail (5 snail 4)) snail (4 snail (7 snail 1)))),
    ((((7 snail 6) snail (5 snail 8)) snail (6 snail 1)) snail (0 snail ((0 snail 8) snail 9))),
    (((6 snail (2 snail 2)) snail (5 snail 2)) snail 6),
    (((7 snail 9) snail ((1 snail 5) snail 8)) snail 2),
    (((3 snail 3) snail (5 snail 6)) snail ((3 snail (3 snail 6)) snail (2 snail (5 snail 9)))),
    ((((0 snail 8) snail (0 snail 1)) snail ((8 snail 6) snail 4)) snail ((0 snail (1 snail 3)) snail 4)),
    ((6 snail (4 snail 4)) snail (4 snail (0 snail 4))),
    (((3 snail 4) snail (4 snail 1)) snail 8),
    ((0 snail ((4 snail 6) snail (5 snail 1))) snail ((0 snail (4 snail 3)) snail (6 snail 9))),
    (((0 snail 7) snail 2) snail ((4 snail 9) snail (1 snail 6))),
    ((1 snail 0) snail (((4 snail 9) snail 9) snail ((5 snail 4) snail 9))),
    (8 snail (((1 snail 4) snail (5 snail 5)) snail (1 snail (0 snail 9)))),
)



