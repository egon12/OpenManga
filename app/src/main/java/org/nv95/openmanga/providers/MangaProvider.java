package org.nv95.openmanga.providers;

import android.content.Context;

import androidx.annotation.Nullable;

import org.nv95.openmanga.feature.manga.domain.MangaInfo;
import org.nv95.openmanga.items.MangaPage;
import org.nv95.openmanga.items.MangaSummary;
import org.nv95.openmanga.lists.MangaList;

import java.util.List;

public interface MangaProvider {

    MangaList getList(int page, int sort, int genre) throws Exception;

    MangaSummary getDetailedInfo(MangaInfo mangaInfo);

    List<MangaPage> getPages(String readLink);

    String getPageImage(MangaPage mangaPage);

    @Nullable
    MangaList search(String query, int page) throws Exception;

    boolean remove(long[] ids);

    String getName();

    @Nullable
    String[] getSortTitles(Context context);

    @Nullable
    String[] getGenresTitles(Context context);

    boolean hasGenres();

    boolean hasSort();

    boolean isItemsRemovable();

    boolean isSearchAvailable();

    boolean isMultiPage();
}
