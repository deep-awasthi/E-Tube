package com.etube.service;

import com.etube.model.postgres.Video;
import com.etube.repository.postgres.VideoRepository;
import com.etube.messaging.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private VideoService videoService;

    private Video mockVideo;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(videoService, "uploadDir", "./test-videos");
        mockVideo = Video.builder()
                .id(1L)
                .title("Test Video")
                .description("Test Description")
                .filePath("./test-videos/test.mp4")
                .viewCount(0L)
                .likeCount(0L)
                .status("PENDING")
                .createdAt(new Date())
                .build();
    }

    @Test
    public void testGetVideoById_Success() {
        when(videoRepository.findById(1L)).thenReturn(Optional.of(mockVideo));

        Video foundVideo = videoService.getVideoById(1L);

        assertNotNull(foundVideo);
        assertEquals("Test Video", foundVideo.getTitle());
        verify(videoRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetVideoById_NotFound() {
        when(videoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            videoService.getVideoById(2L);
        });
    }

    @Test
    public void testRecordView_Success() {
        videoService.recordView(1L, "127.0.0.1");
        verify(eventPublisher, times(1)).publishVideoViewed(1L, "127.0.0.1");
    }
}
