package yuwakisa.servel.adapters

import munit.FunSuite
import org.scalamock.MockFactoryBase

// Simplified adapter to make ScalaMock work with MUnit
trait MunitMockFactory extends MockFactoryBase { self: FunSuite =>
    override type ExpectationException = munit.FailException

    override protected def newExpectationException(message: String, methodName: Option[Symbol]): ExpectationException =
        new munit.FailException(
            message = message + methodName.fold("")(m => s" when calling $m"),
            location = munit.Location.empty
        )
}