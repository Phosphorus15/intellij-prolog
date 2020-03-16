package tech.phosphorus.intellij.prolog

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.{CapturingProcessHandler, ProcessOutput}

import scala.reflect.ClassTag

/**
 * All the black magics here
 */
object FunctionalImplicits {
  implicit class ExecutedGeneralCommandLine(cmd: GeneralCommandLine) {
    def captureOutput(timeout: Int = 1000): ProcessOutput = {
      try {
        new CapturingProcessHandler(cmd).runProcess(timeout)
      } catch {
        case _: ExecutionException => null
      }
    }
  }
}

object RunnableImplicits {

  implicit def func2Runnable(f: () => Unit): Runnable =
    new Runnable {
      override def run(): Unit = f()
    }

}

object SingletonObject {

  implicit class SingletonObject[T](val value: T)(implicit clz: ClassTag[T]) {
    def singleton[R >: T](implicit clz: ClassTag[R]): Array[R] = Array[R](value)

    def singletonList[R >: T](implicit clz: ClassTag[R]): List[R] = List[R](value)
  }

}
