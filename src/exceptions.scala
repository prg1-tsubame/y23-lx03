package prg1.lx03.exceptions

import sys.process._
import scala.io.Source

import java.lang.ArithmeticException
import java.io.{ FileNotFoundException, IOException }

/**
 *
 * さまざまな例外を発生させて様子を観察する例題。
 *
 * スライドで説明されたさまざまな状況下をプログラムで再現しています。
 * 個々の場合ごとに try { ... } catch { ... } を書くのが面倒だったので、
 * 例外処理は test 関数にまかせています。個別のケースは関数定義し、
 * その関数を test 関数の引数に渡すことで実現しています。
 * はい、そうなんです、関数の引数に関数を渡すことができるんです。（高階関数といいます。知ってますよね？）
 *
 * test のなかの h という関数は単に g を呼びます。
 * そして g は f を呼びます。
 * g, h にあまり意味はないように見えますし、実際、あまり意味はありません。
 * ただ、例外が発生したときに表示される例外の構造のなかに g と h が出現します。
 * それを観察して欲しいので加えました。
 *
 * 例外の構造は時としてに数十行にもなります。目が潰れて嫌になります。
 * 今回の例題ではその長い構造の先頭の10行だけを表示しています。
 **/

/** 例外を発生させるかもしれない関数 f を呼び出すためのテスト
  *  fから発生した例外を try ... catch で捉えています。
  *  発生した例外について、例外が発生した箇所までの関数の呼出順序（スタックトレース）を表示します。
  **/
def test(f:() => Unit): Unit = {
  def g(): Unit = { f() }
  def h(): Unit = { g() }

  try {
    h()
  } catch {
    case e: Exception => {
      println("-" * 70)
      println(s"例外が発生しました。 -- ${e}")
      println(e.getStackTrace.take(10).mkString("\n"))
      println("-" * 70 + "\n.\n")
    }
  }
}

@main
def 困った状況1_零で除算() = {
  def 普通の除算() = { 4 / 2; () }
  def 零で除算() = { 1 / 0; () }  // ArithmeticException: / by zero

  test(普通の除算)
  test(零で除算)
}

@main
def 困った状況2_数字じゃない() = {
  def parse1():   Unit = { Integer.parseInt("1");   () }
  def parseone(): Unit = { Integer.parseInt("one"); () }  // NumberFormatException
  def parseOne(): Unit = { Integer.parseInt("One"); () }  // NumberFormatException
  def parse壱(): Unit  = { Integer.parseInt("壱");   () }  // NumberFormatException

  test(parseone)
  test(parseOne)
  test(parse壱)
}

@main
def 困った状況3_そんなファイルパスはない() = {
  def source存在するファイル():   Unit = { Source.fromFile("src/exceptions.scala") }
  def source親の親():           Unit = { Source.fromFile("/../file.txt"); () }
  def source存在しないファイル(): Unit = { Source.fromFile("f" * 255);      () }
  def source長すぎるファイル名(): Unit = { Source.fromFile("f" * 256);      () }

  test(source存在するファイル)
  test(source親の親)            // No such file or direcotry
  test(source存在しないファイル)  // No such file or directory
  test(source長すぎるファイル名)  // File name too long
}

@main
def 困った状況4a_データが空() = {
  def 空リストに先頭の要素はない(): Unit = {
    (Nil: List[String]) match { case x :: _ => x }
    ()
  }

  def 空リストに残りのリストはない(): Unit = {
    (Nil: List[String]) match { case _ :: l => l }
    ()
  }

  test(空リストに先頭の要素はない)    // MatchError: List()
  test(空リストに残りのリストはない)  // MatchError: List()

  import scala.util.Random
  import scala.collection.Set
  import scala.math.max

  test(() => { Random.between(3, 5); () })
  test(() => { Random.between(5, 3); () })  // IllegalArgumentException:  requirement failed:  Invalid bounds
}

@main
def 困った状況4b_そんなファイルはない() = {
  def ファイルが存在する場合はその先頭から5行を表示(): Unit = {
    println(Source.fromFile("src/exceptions.scala").getLines().take(5).mkString("\n"))
  }

  def ファイル中のファイル(): Unit = { Source.fromFile("src/exceptions.scala/ファイルのなかにファイルがあるわけない"); () }

  test(ファイルが存在する場合はその先頭から5行を表示)
  test(ファイル中のファイル)  // FileNotFoundException
}

@main
def 困った状況5_長さがあわない() = {

  def zip[S, T](l1: List[S], l2: List[T]): List[(S, T)] = {
    (l1, l2) match {
      case (Nil, Nil) => { Nil }
      case (x1 :: l1, x2 :: l2) => { (x1, x2) :: zip(l1, l2) }
    }
  }

  def 異なる長さのリストをzipしてはいけない(): Unit = {
    zip(List(1, 2), List("A", "B", "C")); ()
  }

  test(異なる長さのリストをzipしてはいけない)  // MatchError: (List(), List(C))
}

@main
def 困った状況6_しずかちゃんはいないよ() = {
  val 学校の仲間たち = List("のび太", "ジャイアン", "スネ夫")

  def find(子供: String): (String, Int) = {
    def find_i(仲間たち: List[String], i: Int): Int = {
      仲間たち match {
        case 子供 :: _ => { i }
        case _ :: l => { find_i(l, i + 1) }
      }
    }
    (子供, find_i(学校の仲間たち, 0))
  }

  def しずかちゃんはもう卒業しちゃったよ(): Unit = {
    println(find("のび太"))
    println(find("ジャイアン"))
    println(find("スネ夫"))
    println(find("しずか"))  // MatchError: List()
  }

  test(しずかちゃんはもう卒業しちゃったよ)
}

/* 注意
 *
 * 以下のふたつの過酷な例外が発生すると Scala は「即死」するので例外を補足できません。
 * メモリーモンスターはメモリを食い潰す関数なので実行時間が長いです。一分以上かかるかもしれません。
 * 以下のコメントを外して実行してもパソコンが壊れることはありません。
 * 電源の繋がっていないノートパソコンの場合メモリーモンスターを実行すると急速に電池残量が失われるかもしれません。
 * 怪しげなこと、先生は危なくないと言っていたけど危なそうなことは「ヤル！」
 */
@main
def 困った状況7a_計算資源を食い潰す例_無限再帰地獄() = {
  def 無限再帰地獄(): Int = { 0 + 無限再帰地獄() }
  
  無限再帰地獄()
}

/* 注意
 * メモリーモンスターはメモリを食い潰す関数なので実行時間が長いです。一分以上かかるかもしれません。
 * 以下のコメントを外して実行してもパソコンが壊れることはありません。
 * 電源の繋がっていないノートパソコンの場合メモリーモンスターを実行すると急速に電池残量が失われるかもしれません。
 * 怪しげなこと、先生は危なくないと言っていたけど危なそうなことは「ヤル！」
 */

@main
def 困った状況7b_計算資源を食い潰す例_メモリーモンスター() = {
  def メモリーモンスター(): List[Int] = { Range(0, Int.MaxValue).toList }  // 長さ2^31のリスト
  
  メモリーモンスター()
}