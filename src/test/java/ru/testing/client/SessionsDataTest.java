package ru.testing.client;

import org.junit.Test;
import ru.testing.client.common.FilesOperations;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test sessions data from file
 */
public class SessionsDataTest {

    @Test
    public void fileWillBeCreate() {
        FilesOperations filesOperations = new FilesOperations();
        filesOperations.readSessionsData();
        assertThat("File exist", new File(FilesOperations.getSessionsFile()).exists(), equalTo(true));
    }
}
