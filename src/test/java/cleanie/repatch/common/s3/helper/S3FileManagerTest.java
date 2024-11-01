package cleanie.repatch.common.s3.helper;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = "cloud.aws.s3.bucket=${AWS_S3_BUCKET_NAME}")
class S3FileManagerTest {

    @Mock
    private AmazonS3 amazonS3;
    @InjectMocks
    private S3FileManager s3FileManager;
    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET_NAME;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile() throws IOException, URISyntaxException {
        MultipartFile mockFile = mock(MultipartFile.class);
        String fileName = "uploads/test-file.jpg";
        String fileUrl = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + fileName;

        when(mockFile.getOriginalFilename()).thenReturn("test-file.jpg");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));
        when(amazonS3.getUrl(BUCKET_NAME, fileName)).thenReturn(new URI(fileUrl).toURL());
        String resultUrl = s3FileManager.uploadFile(mockFile, fileName);

        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        assertEquals(fileUrl, resultUrl);
    }

    @Test
    void testDeleteFile() {
        String fileName = "uploads/test-file.jpg";

        s3FileManager.deleteFile(fileName);

        verify(amazonS3, times(1)).deleteObject(BUCKET_NAME, fileName);
    }
}