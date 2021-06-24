package com.wezik.app.validator;

import com.wezik.app.wrapper.FilesWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class FileValidatorTest {

    @Mock
    private FilesWrapper filesWrapper;
    @InjectMocks
    private FileValidator fileValidator;

    @Test
    void shouldReturnTrueForCorrectFile() {
        // Given
        String[] args = new String[]{"C:\\sample.txt"};
        Mockito.when(filesWrapper.exists(Mockito.any(Path.class))).thenReturn(true);

        // When
        boolean result = fileValidator.isArgValid(args);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForNonExistentFile() {
        // Given
        String[] args = new String[]{"C:\\sample.txt"};
        Mockito.when(filesWrapper.exists(Mockito.any(Path.class))).thenReturn(false);

        // When
        boolean result = fileValidator.isArgValid(args);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForBadFileFormat() {
        // Given
        String[] args = new String[]{"C:\\sample.bat"};

        // When
        boolean result = fileValidator.isArgValid(args);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForNoArgs() {
        // Given
        String[] args = new String[0];

        // When
        boolean result = fileValidator.isArgValid(args);

        // Then
        assertFalse(result);
    }

}
