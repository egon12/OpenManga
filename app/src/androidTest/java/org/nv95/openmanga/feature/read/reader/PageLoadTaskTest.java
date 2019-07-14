package org.nv95.openmanga.feature.read.reader;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.nv95.openmanga.debug.test.R;
import org.nv95.openmanga.items.MangaPage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;

public class PageLoadTaskTest {

    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    MockWebServer server = new MockWebServer();

    @Test
    public void testSomething() throws IOException {
        try {

            server.start();

            MockResponse response = new MockResponse()
                    .setBody("Hello")
                    .throttleBody(1, 1, TimeUnit.SECONDS);

            server.enqueue(response);

            String url = server.url("/").toString();

            MangaPage page = new MangaPage(url);
            page.provider = MockMangaProvider.class;

            PageWrapper pageWrapper = new PageWrapper(page, 0);

            PageLoadTask task = new PageLoadTask(context, pageWrapper, null);

            Object result = task.doInBackground(0);

            Assert.assertNotNull("result shouldn't be null", result);

            if (result instanceof Exception) {
                throw new RuntimeException("Result is error", (Exception) result);
            }

            File file = new File((String) result);

            InputStream is = new FileInputStream(file);

            byte[] buffer = new byte[100];
            int byteRead = is.read(buffer);
            byte[] newBuffer = new byte[byteRead];

            System.arraycopy(buffer, 0, newBuffer, 0, byteRead);

            Assert.assertEquals("Hello", new String(newBuffer));


        } catch (Exception e) {

        } finally {
            server.shutdown();
        }

    }


    @Test
    public void test_read_mri() throws IOException {
        try {

            Context testContext = InstrumentationRegistry.getInstrumentation().getContext();

            server.start();

            Buffer buffer = new Buffer();

            InputStream is = testContext.getResources().openRawResource(R.raw.sample_image_input);
            int fileLength = 96381;

            buffer.readFrom(is);

            MockResponse response = new MockResponse()
                    .setBody(buffer);

            server.enqueue(response);

            String url = server.url("/").toString();

            MangaPage page = new MangaPage(url);
            page.provider = MockMangaProvider.class;

            PageWrapper pageWrapper = new PageWrapper(page, 0);

            PageLoadTask task = new PageLoadTask(context, pageWrapper, null);

            Object result = task.doInBackground(0);

            Assert.assertNotNull("result shouldn't be null", result);

            if (result instanceof Exception) {
                throw new RuntimeException("Result is error", (Exception) result);
            }


            File file = new File((String) result);

            InputStream outputIs = new FileInputStream(file);
            Buffer output = new Buffer().readFrom(outputIs);

            Assert.assertEquals("Hello", output.readString(Charset.defaultCharset()));


        } catch (Exception e) {

        } finally {
            server.shutdown();
        }

    }
}
