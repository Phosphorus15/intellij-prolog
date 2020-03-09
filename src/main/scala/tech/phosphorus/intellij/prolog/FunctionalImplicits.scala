package tech.phosphorus.intellij.prolog

object RunnableImplicits {

  implicit def func2Runnable(f: () => Unit): Runnable =
    new Runnable {
      override def run(): Unit = f()
    }

}