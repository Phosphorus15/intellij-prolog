package tech.phosphorus.intellij.prolog.toolchain

import com.intellij.icons.AllIcons.Welcome.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs._
import tech.phosphorus.intellij.prolog.PrologLanguage
import tech.phosphorus.intellij.prolog.psi.PrologPredicateId
import tech.phosphorus.intellij.prolog.psi.impl.PrologPredicateIdImpl

abstract class PrologStubBase[T <: PsiElement](parent: StubElement[_], ty: PrologStubElementType[_ <: com.intellij.psi.stubs.StubElement[_ <: com.intellij.psi.PsiElement], _ <: com.intellij.psi.PsiElement])
  extends StubBase[T](parent, ty)

abstract class PrologStubElementType[StubT <: StubElement[_], PsiT <: PsiElement](debugName: String)
  extends IStubElementType[StubT, PsiT](debugName, PrologLanguage.INSTANCE) {
  override def getExternalId: String = f"prolog.${super.toString}"
}

class PrologPredicateStubIndex extends StringStubIndexExtension[PrologPredicateId] {
  override def getKey: StubIndexKey[String, PrologPredicateId] = PrologPredicateStubIndex.KEY
}

object PrologPredicateStubIndex {

  def getAllKeys(project: com.intellij.openapi.project.Project): Array[String] = StubIndex.getInstance().getAllKeys(PrologPredicateStubIndex.KEY, project).toArray(Array[String]())

  val KEY: StubIndexKey[String, PrologPredicateId] = StubIndexKey.createIndexKey("prolog.index.predicate-id")
}

trait PrologPredicateStub extends StubElement[PrologPredicateId] {
  val name: String
}

class PrologPredicateStubImpl(stub: StubElement[_],override val name: String)
  extends PrologStubBase[PrologPredicateId](stub, PrologStubElementType.PREDICATE_ID) with PrologPredicateStub

class PrologPredicateType(debugName: String) extends PrologStubElementType[PrologPredicateStub, PrologPredicateId](debugName) {

  override def createPsi(stubT: PrologPredicateStub): PrologPredicateId = new PrologPredicateIdImpl(stubT, this)

  override def createStub(psiT: PrologPredicateId, stubElement: StubElement[_ <: PsiElement]): PrologPredicateStub = new PrologPredicateStubImpl(stubElement, psiT.getText)

  override def serialize(t: PrologPredicateStub, stubOutputStream: StubOutputStream): Unit = stubOutputStream.writeName(t.name)

  override def deserialize(stubInputStream: StubInputStream, p: StubElement[_ <: PsiElement]): PrologPredicateStub = new PrologPredicateStubImpl(p, stubInputStream.readName().toString)

  override def indexStub(t: PrologPredicateStub, indexSink: IndexSink): Unit = if (t.name != null) {
    indexSink.occurrence(PrologPredicateStubIndex.KEY, t.name)
  }
}

object PrologStubElementType {
  val PREDICATE_ID = new PrologPredicateType("PREDICATE_ID")
}
