package tech.phosphorus.intellij.prolog

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.{CapturingProcessHandler, ProcessOutput}
import tech.phosphorus.intellij.prolog.FunctionalImplicits.OptionT

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

  implicit class Nullable[T](val t: T) extends AnyVal {
    def asOption(): Option[T] = Option(t)
    def isDefined: Boolean = t != null
    def isNull: Boolean = !isDefined
  }

  type Result[T] = Either[T, Exception]

  implicit class ExceptionMonad[T](val t: Result[T]) extends Monad[T, Result] {

    def this(t : T) {
      this(Left(t))
    }

    override def >>=[B](f: T => Result[B])(implicit classTag: ClassTag[B]): Result[B] = if(t.isRight) Right(t.right.get) else f(t.left.get)

    override def <*>[B](f: Result[T => B])(implicit classTag: ClassTag[B]): Result[B] = t match {
      case Left(a) => f match {
        case Left(f) => Left(f(a))
        case Right(r) => Right(r)
      }
      case Right(r) => Right(r)
    }

    override def <@>[B](f: T => B)(implicit classTag: ClassTag[B]): Result[B] = if(t.isRight) Right(t.right.get) else Left(f(t.left.get))
  }

  implicit class OptionT[T](val t: Option[T]) extends Monad[T, Option]{
    override def <@>[B](f: T => B)(implicit classTag: ClassTag[B]): Option[B] = t.map(f)

    override def >>=[B](f: T => Option[B])(implicit classTag: ClassTag[B]): Option[B] = if (t.isEmpty) None else f(t.get)

    override def <*>[B](f: Option[T => B])(implicit classTag: ClassTag[B]): Option[B] = if (t.isEmpty) None else f.map(_(t.get))
  }

  implicit class ArrayT[T](val t: Array[T]) extends Monad[T, Array] {
    override def >>=[B](f: T => Array[B])(implicit classTag: ClassTag[B]): Array[B] = t.flatMap(v => f(v).t).toArray

    override def <*>[B](f: Array[T => B])(implicit classTag: ClassTag[B]): Array[B] = t.flatMap(v => f.map(_(v))).toArray

    override def <@>[B](f: T => B)(implicit classTag: ClassTag[B]): Array[B] = t.map(f)
  }

}

object OptionLetIn {
  implicit class LetIn[T](val v: Option[T]) extends OptionT[T](v){
    def let[B](f: T => B)(implicit classTag: ClassTag[B]): Option[B] = super.<@>(f)
  }
}

trait Functor[A, Impl[T]] {
  def <@> [B](f: A => B)(implicit classTag: ClassTag[B]): Impl[B]
}

trait Applicative[A, Impl[T]] extends Functor[A, Impl] {
  def <*> [B](f: Impl[A => B])(implicit classTag: ClassTag[B]): Impl[B]
}

trait Monad[A, Impl[T]] extends Applicative[A, Impl] {
  def >>= [B](f: A => Impl[B])(implicit classTag: ClassTag[B]): Impl[B]
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
