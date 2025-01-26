package ru.clevertec.newssystem.news.adapter.output.persistence.jpa.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.repository.NewsRepository;

import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteNewsJpaAdapterTest {

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private DeleteNewsJpaAdapter deleteNewsJpaAdapter;

    @Test
    void shouldDeleteNews() {
        //given
        UUID newsId = UUID.randomUUID();

        doNothing()
                .when(newsRepository).deleteById(newsId);

        //when
        deleteNewsJpaAdapter.deleteNewsById(newsId);

        //then
        verifyNoMoreInteractions(newsRepository);
    }

}