import Programming1._

lazy val lx03 = (project in file(".")).settings(Scala3)
// もっと大きなメモリ領域を利用してプログラムを走らせたい場合は、上の行に以下のような内容を設定すればよい。
//lazy val lx03 = (project in file(".")).settings(Scala3 ++ Seq(run / javaOptions += "-Xms256M -Xmx2G"))
// この設定はスタック領域を 256MB、メモリ領域を 2GB に設定するもの。