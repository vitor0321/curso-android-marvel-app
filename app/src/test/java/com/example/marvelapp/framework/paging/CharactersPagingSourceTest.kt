package com.example.marvelapp.framework.paging

import androidx.paging.PagingSource
import com.example.core.data.repository.CharactersRemoteDataSource
import com.example.core.domain.model.Character
import com.example.marvelapp.factory.response.DataWrapperResponseFactory
import com.example.marvelapp.framework.network.response.DataWrapperResponse
import com.example.testing.MainCoroutinesRule
import com.example.testing.model.CharactersFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CharactersPagingSourceTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutinesRule()

    @Mock
    lateinit var remoteDataSource: CharactersRemoteDataSource<DataWrapperResponse>

    private val dataWrapperResponseFactory = DataWrapperResponseFactory()

    private val charactersFactory = CharactersFactory()

    private lateinit var charactersPagingSource: CharactersPagingSource

    @Before
    fun setUp() {
        charactersPagingSource = CharactersPagingSource(remoteDataSource, "")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return a success load result when load is called`() = runBlockingTest {
        //Arrange
        whenever(remoteDataSource.fetchCharacter(any()))
            .thenReturn(dataWrapperResponseFactory.create())

        //Act
        val result = charactersPagingSource.load(
            PagingSource.LoadParams.Refresh(null, 2, false)
        )

        //Assert
        val expected = listOf(
            charactersFactory.create(CharactersFactory.Hero.ThreeDMan),
            charactersFactory.create(CharactersFactory.Hero.ABomb)
        )

        assertEquals(
            PagingSource.LoadResult.Page(
                data = expected,
                prevKey = null,
                nextKey = 20
            ),
            result
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return a error load when load is called`() = runBlockingTest {
        //Arrange
        val exception = RuntimeException()
        whenever(remoteDataSource.fetchCharacter(any()))
            .thenThrow(exception)

        //Act
        val result = charactersPagingSource.load(
            PagingSource.LoadParams.Refresh(null, 2, false)
        )

        //Assert
        assertEquals(
            PagingSource.LoadResult.Error<Int, Character>(exception),
            result
        )
    }
}