package prg1.lx03.trycatch

import scala.io.Source

import java.lang.ArithmeticException
import java.lang.NumberFormatException
import java.io.FileNotFoundException


@main
def try1_div() = {
  // 除算をし、0による算術例外が発生した場合は結果を Int.MaxValue とする（お薦めしない）
  def div(a: Int, b: Int): Int = {
    try { a / b
    } catch {
      case e: ArithmeticException => Int.MaxValue
    }
  }

  for (i <- -5 to 5) { println(s"120 / $i = ${div(120, i)}") }
}

@main
def try2_format() = {
  var number_format_errors = 0

  // 数字を読み取り、失敗した場合には失敗数を計上する例
  def print_number(num: String): Unit = {
    try { println(s"$num => ${Integer.parseInt(num)}")
    } catch {
      case e: NumberFormatException => {
        number_format_errors = number_format_errors + 1
      }
    }
  }

  for (num <- List("1", "Zero", "零", "壱")) { print_number(num) }
  println(s"入力エラー数: $number_format_errors")
}

@main
def try3_first_line() = {
  // ファイルの先頭1行を表示し、ファイルが存在しない場合はエラーメッセージを表示する
  def first_line(path: String): String = {
    try { Source.fromFile(path).getLines().take(1).mkString
    } catch {
      case e: FileNotFoundException => "<<<ファイルは見つかりませんでした>>>"
    }
  }
  val filenames = List("src/trycatch.scala", "こんなファイルは存在しない", "throws.scala")
  for (filename <- filenames) {
    println(s"$filename => ${first_line(filename)}")
  }
}

@main
def try4_factorial() = {
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

  for (n <- -3 to 20) {
    val result = {
      try { s"${f(n)}"
      } catch {
        case e: IllegalArgumentException => {
          e.getMessage() match {
            case "負の数は嫌いです"   => { "-" * 10 }
            case "大きな数は嫌いです" => { "+" * 10 }
          }
        }
      }
    }
    println(s"factorial($n) = $result")
  }
}