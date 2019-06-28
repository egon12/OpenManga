package org.nv95.openmanga.providers;
  
import androidx.annotation.Nullable;
import org.nv95.openmanga.items.MangaPage;
import org.nv95.openmanga.items.MangaSummary;
import org.nv95.openmanga.lists.MangaList;
import org.nv95.openmanga.utils.AppHelper;
import org.nv95.openmanga.feature.manga.domain.MangaInfo;
import java.util.List;


interface IMangaProvider {
    public MangaList getList(int page, int sort, int genre) throws Exception;

    public MangaSummary getDetailedInfo(MangaInfo mangaInfo);

    public List<MangaPage> getPages(String readLink);

    public String getPageImage(MangaPage mangaPage);

    public MangaList search(String query, int page);

    public boolean remove(long[] ids);

    public String getName();

    public boolean hasGenres();

    public boolean hasSort();

    public boolean isItemsRemovable();

    public boolean isSearchAvailable();

    public boolean isMultiPage();
}                                            
