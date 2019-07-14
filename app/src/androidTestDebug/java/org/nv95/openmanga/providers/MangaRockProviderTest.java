package org.nv95.openmanga.providers;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nv95.openmanga.items.MangaChapter;
import org.nv95.openmanga.items.MangaPage;
import org.nv95.openmanga.items.MangaSummary;
import org.nv95.openmanga.lists.MangaList;

import java.util.List;

import static org.junit.Assert.*;

public class MangaRockProviderTest {

    MangaRockProvider provider;

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        provider = new MangaRockProvider(context);
    }

    @Test
    public void getList() {

        try {
            MangaList list = provider.getList(0, 0, 0);
            assertTrue("Size of MangaList must greater than zero", 0 < list.size());

        } catch (Exception e) {
            Assert.fail("Should not thrown exception but got" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void getDetailedInfo() {
        try {
            MangaList list = provider.getList(0, 0, 0);
            MangaSummary summary = provider.getDetailedInfo(list.get(0));
            assertTrue("Should have description", summary.description.length() > 100);

            Log.d("ketai", summary.description);
            for (MangaChapter chapter: summary.chapters) {
                Log.d("ketai", chapter.name);
                Log.d("ketai", chapter.readLink);
            }


        } catch (Exception e) {
            Assert.fail("Should not thrown exception but got" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void getPages() {
        try {
            MangaList list = provider.getList(0, 0, 0);
            MangaSummary summary = provider.getDetailedInfo(list.get(0));
            List<MangaPage> pages = provider.getPages(summary.chapters.first().readLink);
            assertTrue("First chapter should have more than one page", pages.size() > 1);
        } catch (Exception e) {
            Assert.fail("Should not thrown exception but got" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void getPageImage() {
        try {
            MangaList list = provider.getList(0, 0, 0);
            MangaSummary summary = provider.getDetailedInfo(list.get(0));
            List<MangaPage> pages = provider.getPages(summary.chapters.first().readLink);
            MangaPage page = pages.get(0);
            String imageUrl = provider.getPageImage(page);
            assertTrue("URL of Image should not be empty", imageUrl.length() > 0);
        } catch (Exception e) {
            Assert.fail("Should not thrown exception but got" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void search() {
        try {
            MangaList list = provider.search("Solo", 0);
            assertTrue("Size of MangaList must greater than zero", 0 < list.size());
//            for (MangaInfo info : list) {
//                assertTrue("name should contains solo got:" + info.name, info.name.contains("solo"));
//            }
        } catch (Exception e) {
            Assert.fail("Should not thrown exception but got" + e.getMessage());
            e.printStackTrace();
        }
    }
}
