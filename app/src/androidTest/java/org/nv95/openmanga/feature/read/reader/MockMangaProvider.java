package org.nv95.openmanga.feature.read.reader;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import org.nv95.openmanga.feature.manga.domain.MangaInfo;
import org.nv95.openmanga.items.MangaPage;
import org.nv95.openmanga.items.MangaSummary;
import org.nv95.openmanga.lists.MangaList;
import org.nv95.openmanga.providers.MangaProvider;

import java.util.List;

class MockMangaProvider implements MangaProvider {

    static public MockMangaProvider getInstance(Context context) {
        return new MockMangaProvider(context);
    }


    public MockMangaProvider(Context context) {
        Log.d("Ketai", "provider created " + context.getPackageName());

    }

    @Override
    public MangaList getList(int page, int sort, int genre) throws Exception {
        return null;
    }

    @Override
    public MangaSummary getDetailedInfo(MangaInfo mangaInfo) {
        return null;
    }

    @Override
    public List<MangaPage> getPages(String readLink) {
        return null;
    }

    @Override
    public String getPageImage(MangaPage mangaPage) {
        return mangaPage.path;
    }

    @Nullable
    @Override
    public MangaList search(String query, int page) throws Exception {
        return null;
    }

    @Override
    public boolean remove(long[] ids) {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Nullable
    @Override
    public String[] getSortTitles(Context context) {
        return new String[0];
    }

    @Nullable
    @Override
    public String[] getGenresTitles(Context context) {
        return new String[0];
    }

    @Override
    public boolean hasGenres() {
        return false;
    }

    @Override
    public boolean hasSort() {
        return false;
    }

    @Override
    public boolean isItemsRemovable() {
        return false;
    }

    @Override
    public boolean isSearchAvailable() {
        return false;
    }

    @Override
    public boolean isMultiPage() {
        return false;
    }
}
