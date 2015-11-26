package com.jay.montior.test

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.mockito.Mockito

object TestUtil : TestUtilClass()

open class TestUtilClass {

    inline fun <reified T : Any> lazyMock() = lazy { Mockito.mock(T::class.java) }

    infix fun Any?.returns(returnValue: Any?) =
            Mockito.`when`(this).thenReturn(returnValue)

    infix fun <T> T.equalTo(other: T) =
            MatcherAssert.assertThat(this, Matchers.equalTo(other))

}