package prg1.lx03.throws

import prg1.lx03.exceptions.test => exception_test

def f(n: Int): Int = {
  if (n < 0) { throw new IllegalArgumentException("負の数は嫌いです") }
  if (n == 0) { 1 }
  else {
    val v = n * f(n - 1)
    if (v < 0) {
      throw new IllegalArgumentException("大きな数は嫌いです")
    }
    v
  }
}

def test(n: Int): Unit = {
  def factorial(): Unit = {
    println(s"factorial($n) = ${f(n)}")
  }
  exception_test(factorial _)
}

@main
def fact_normal() = {
  for (n <- 0 to 10) {
    test(n)
  }
}

@main
def fact_negative() = {
  test(-1)
}

@main
def fact_too_large() = {
  test(17)
}