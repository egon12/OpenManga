package org.nv95.openmanga.providers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import org.nv95.openmanga.core.network.NetworkUtils;
import org.nv95.openmanga.feature.manga.domain.MangaInfo;
import org.nv95.openmanga.items.MangaChapter;
import org.nv95.openmanga.items.MangaPage;
import org.nv95.openmanga.items.MangaSummary;
import org.nv95.openmanga.lists.ChaptersList;
import org.nv95.openmanga.lists.MangaList;

import java.util.ArrayList;
import java.util.List;

public class MangaRockProvider extends AbstractMangaProvider {

    public MangaRockProvider(Context context) {
        super(context);
    }

    @Override
    public MangaList getList(int page, int sort, int genre) throws Exception {
        String result = NetworkUtils.postRaw(
                "https://api.mangarockhd.com/query/web401/mrs_filter?country=Indonesia",
                null,
                "{\"status\":\"all\",\"genres\":{},\"order\":\"rank\"}"
        );

        JSONObject root = new JSONObject(result);
        JSONArray ids = root.getJSONArray("data");
        JSONArray newIds = new JSONArray();

        for (int i = 0; i < 16; i++) {
            newIds.put(ids.get(i));
        }

        String result2 = NetworkUtils.postRaw(
                "https://api.mangarockhd.com/meta",
                null,
                newIds.toString()
        );

        root = new JSONObject(result2);
        root = root.getJSONObject("data");

        MangaList endResult = new MangaList();

        for (int i = 0; i < 16; i++) {
            String mangaId = newIds.getString(i);
            JSONObject manga = root.getJSONObject(mangaId);
            MangaInfo info = new MangaInfo();
            info.name = manga.getString("name");
            info.status = manga.getBoolean("completed") ? MangaInfo.STATUS_COMPLETED : MangaInfo.STATUS_ONGOING;
            info.preview = manga.getString("thumbnail");
            info.provider = MangaRockProvider.class;
            info.rating = 0;
            info.path = mangaId;
            endResult.add(info);
        }

        return endResult;
    }

    @Override
    public MangaSummary getDetailedInfo(MangaInfo mangaInfo) {
        try {
            String mangaId = mangaInfo.path;


            String response = NetworkUtils.postRaw(
                    "https://api.mangarockhd.com/query/web401/manga_detail?country=Indonesia",
                    null,
                    "{\"oids\":{\"" + mangaId + "\":0},\"sections\":[\"basic_info\",\"chapters\"]}"

            );

            JSONObject rootJsonResponse = new JSONObject(response);
            JSONObject jsonResponse = rootJsonResponse.getJSONObject("data").getJSONObject(mangaId);
            MangaSummary summary = new MangaSummary(mangaInfo);
            summary.description = jsonResponse.getJSONObject("basic_info").getString("description");

            JSONArray jsonChapters = jsonResponse.getJSONObject("chapters").getJSONArray("chapters");
            ChaptersList chapters = new ChaptersList();

            for (int i=0; i<jsonChapters.length(); i++) {
                JSONObject jsonChapter = jsonChapters.getJSONObject(i);
                MangaChapter chapter = new MangaChapter();

                chapter.name = jsonChapter.getString("name");
                chapter.readLink = jsonChapter.getString("oid");
                chapter.number = jsonChapter.getInt("order");
                chapter.provider = MangaRockProvider.class;

                chapters.add(chapter);
            }

            summary.chapters = chapters;

            return summary;

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<MangaPage> getPages(String readLink) {
        String chapterId = readLink;

        String url = "https://api.mangarockhd.com/query/web401/pagesv2?oid=" + chapterId +"&country=Indonesia";
        try {
            JSONArray imagesList = NetworkUtils.getJsonObject(url).getJSONArray("data");
            ArrayList<MangaPage> result = new ArrayList<>(imagesList.length());
            for (int i=0; i<imagesList.length(); i++) {
                String imageUrl = imagesList.getJSONObject(i).getString("url");
                MangaPage page = new MangaPage();
                //page.path = imageUrl;
                page.path = "https://f01.mrcdn.info/file/mrfiles/i/4/o/1/o.k2eogSxp.mri";
                page.provider = MangaRockProvider.class;
                result.add(i, page);
            }
            return result;

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getPageImage(MangaPage mangaPage) {
        return mangaPage.path;
    }

    @Override
    public String getName() {
        return "MangaRock";
    }

}