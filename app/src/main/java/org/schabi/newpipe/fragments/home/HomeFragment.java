package org.schabi.newpipe.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.schabi.newpipe.R;
import org.schabi.newpipe.error.ErrorInfo;
import org.schabi.newpipe.error.UserAction;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.database.stream.StreamStatisticsEntry;
import org.schabi.newpipe.fragments.list.BaseListFragment;
import org.schabi.newpipe.info_list.ItemViewMode;
import org.schabi.newpipe.local.history.HistoryRecordManager;
import org.schabi.newpipe.util.ExtractorHelper;
import org.schabi.newpipe.util.ServiceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Lin Pipe home feed.
 * <p>
 * The first chip ("For you") builds recommendations from the user's watch history:
 * the related videos of the most recently watched streams are merged, de-duplicated and
 * shuffled. When there is no history yet, the trending page of the selected service is used.
 * The other chips run a search for a topic, like the category chips of a video site.
 * </p>
 */
public class HomeFragment extends BaseListFragment<List<InfoItem>, Void> {

    private static final int HISTORY_SEEDS = 4;
    private static final int MAX_ITEMS = 60;

    private static final int CHIP_FOR_YOU = 0;
    private static final int CHIP_TRENDING = 1;
    private static final int RECENT_TOPICS = 4;

    private static final class Chip {
        @StringRes
        final int labelRes;
        @Nullable
        final String label;
        @Nullable
        final String query;

        Chip(@StringRes final int labelRes, @Nullable final String label,
             @Nullable final String query) {
            this.labelRes = labelRes;
            this.label = label;
            this.query = query;
        }
    }

    private static final int[] TOPIC_LABELS = {
            R.string.home_chip_music, R.string.home_chip_gaming, R.string.home_chip_news,
            R.string.home_chip_movies, R.string.home_chip_sports, R.string.home_chip_tech,
            R.string.home_chip_learning, R.string.home_chip_cooking,
    };
    private static final String[] TOPIC_QUERIES = {
            "music", "gaming", "news", "movie trailer", "sports highlights", "technology",
            "tutorial", "cooking recipe",
    };

    private final List<Chip> chips = new ArrayList<>();
    private final List<String> recentTopics = new ArrayList<>();
    private int selectedChip = CHIP_FOR_YOU;
    private Disposable worker;
    private Disposable recentWorker;
    private LinearLayout chipContainer;
    private SwipeRefreshLayout swipeRefresh;
    private final List<TextView> chipViews = new ArrayList<>();

    public HomeFragment() {
        super();
        setUseDefaultStateSaving(false);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Lifecycle
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    protected void initViews(final View rootView, final Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);
        chipContainer = rootView.findViewById(R.id.home_chip_container);
        swipeRefresh = rootView.findViewById(R.id.home_swipe_refresh);
        swipeRefresh.setOnRefreshListener(() -> startLoading(true));
        rebuildChips();
        loadRecentTopics();
    }

    @Override
    public void onDestroyView() {
        disposeWorker();
        if (recentWorker != null) {
            recentWorker.dispose();
            recentWorker = null;
        }
        chipViews.clear();
        chipContainer = null;
        swipeRefresh = null;
        super.onDestroyView();
    }

    private void disposeWorker() {
        if (worker != null) {
            worker.dispose();
            worker = null;
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Chips
    //////////////////////////////////////////////////////////////////////////*/

    /**
     * Chips: For you, Trending, then topics taken from the user's recent searches,
     * then the fixed categories (recent searches that duplicate a category are skipped).
     */
    private void rebuildChips() {
        if (chipContainer == null) {
            return;
        }
        chips.clear();
        chips.add(new Chip(R.string.home_chip_for_you, null, null));
        chips.add(new Chip(R.string.home_chip_trending, null, null));
        for (final String recent : recentTopics) {
            chips.add(new Chip(0, recent, recent));
        }
        for (int i = 0; i < TOPIC_LABELS.length; i++) {
            chips.add(new Chip(TOPIC_LABELS[i], null, TOPIC_QUERIES[i]));
        }

        final LayoutInflater inflater = LayoutInflater.from(requireContext());
        chipContainer.removeAllViews();
        chipViews.clear();
        for (int i = 0; i < chips.size(); i++) {
            final int index = i;
            final Chip chip = chips.get(i);
            final TextView view = (TextView) inflater.inflate(
                    R.layout.item_home_chip, chipContainer, false);
            if (chip.label != null) {
                view.setText(chip.label);
            } else {
                view.setText(chip.labelRes);
            }
            view.setSelected(i == selectedChip);
            view.setOnClickListener(v -> selectChip(index));
            chipContainer.addView(view);
            chipViews.add(view);
        }
    }

    private void loadRecentTopics() {
        recentWorker = new HistoryRecordManager(requireContext())
                .getRelatedSearches("", 0, RECENT_TOPICS)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    recentTopics.clear();
                    for (final String search : list) {
                        if (search != null && !search.trim().isEmpty()) {
                            recentTopics.add(search.trim());
                        }
                    }
                    // Only rebuild while a fixed chip is selected, otherwise indexes would shift
                    if (selectedChip <= CHIP_TRENDING) {
                        rebuildChips();
                    }
                }, throwable -> { /* recent topics are optional */ });
    }

    private void selectChip(final int index) {
        if (index == selectedChip && !infoListAdapter.getItemsList().isEmpty()) {
            return;
        }
        if (index < 0 || index >= chips.size()) {
            return;
        }
        selectedChip = index;
        for (int i = 0; i < chipViews.size(); i++) {
            chipViews.get(i).setSelected(i == index);
        }
        startLoading(true);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Loading
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    protected void doInitialLoadLogic() {
        startLoading(false);
    }

    @Override
    public void startLoading(final boolean forceLoad) {
        super.startLoading(forceLoad);
        showListFooter(false);
        infoListAdapter.clearStreamItemList();
        disposeWorker();

        final Single<List<InfoItem>> source = selectedChip == CHIP_FOR_YOU
                ? loadRecommendations(forceLoad)
                : selectedChip == CHIP_TRENDING
                ? loadTrending(forceLoad)
                : loadTopic(chips.get(selectedChip).query);

        worker = source
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    isLoading.set(false);
                    stopRefreshing();
                    handleResult(items);
                }, throwable -> {
                    stopRefreshing();
                    showError(new ErrorInfo(throwable,
                            UserAction.REQUESTED_KIOSK, "Loading home feed",
                            ServiceHelper.getSelectedServiceId(requireContext()), null));
                });
    }

    private void stopRefreshing() {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }
    }

    private Single<List<InfoItem>> loadTrending(final boolean forceLoad) {
        return Single.defer(() -> {
            final int serviceId = ServiceHelper.getSelectedServiceId(requireContext());
            final org.schabi.newpipe.extractor.kiosk.KioskList kioskList =
                    NewPipe.getService(serviceId).getKioskList();
            final String kioskId = kioskList.getDefaultKioskId();
            final String kioskUrl = kioskList.getListLinkHandlerFactoryByType(kioskId)
                    .fromId(kioskId).getUrl();
            return ExtractorHelper.getKioskInfo(serviceId, kioskUrl, forceLoad)
                    .<List<InfoItem>>map(info -> new ArrayList<InfoItem>(info.getRelatedItems()));
        });
    }

    private Single<List<InfoItem>> loadTopic(@Nullable final String query) {
        return Single.defer(() -> {
            final int serviceId = ServiceHelper.getSelectedServiceId(requireContext());
            return ExtractorHelper.searchFor(serviceId, query, Collections.emptyList(), "")
                    .map(info -> {
                        final List<InfoItem> out = new ArrayList<>();
                        for (final InfoItem item : info.getRelatedItems()) {
                            if (item instanceof StreamInfoItem) {
                                out.add(item);
                            }
                        }
                        return out;
                    });
        });
    }

    private Single<List<InfoItem>> loadRecommendations(final boolean forceLoad) {
        final HistoryRecordManager history = new HistoryRecordManager(requireContext());
        return history.getStreamStatistics()
                .firstOrError()
                .flatMap(entries -> {
                    if (entries.isEmpty()) {
                        return loadTrending(forceLoad);
                    }
                    return recommendFrom(entries, forceLoad);
                });
    }

    private Single<List<InfoItem>> recommendFrom(final List<StreamStatisticsEntry> entries,
                                                 final boolean forceLoad) {
        final List<StreamStatisticsEntry> sorted = new ArrayList<>(entries);
        Collections.sort(sorted, new Comparator<StreamStatisticsEntry>() {
            @Override
            public int compare(final StreamStatisticsEntry a, final StreamStatisticsEntry b) {
                return b.getLatestAccessDate().compareTo(a.getLatestAccessDate());
            }
        });

        final Set<String> watchedUrls = new HashSet<>();
        for (final StreamStatisticsEntry e : sorted) {
            watchedUrls.add(e.getStreamEntity().getUrl());
        }

        final List<Single<List<InfoItem>>> loaders = new ArrayList<>();
        final int seeds = Math.min(HISTORY_SEEDS, sorted.size());
        for (int i = 0; i < seeds; i++) {
            final StreamStatisticsEntry seed = sorted.get(i);
            // A failing seed must not break the whole feed
            loaders.add(ExtractorHelper.getStreamInfo(seed.getStreamEntity().getServiceId(),
                            seed.getStreamEntity().getUrl(), forceLoad)
                    .<List<InfoItem>>map((StreamInfo info) ->
                            new ArrayList<InfoItem>(info.getRelatedItems()))
                    .onErrorReturnItem(Collections.<InfoItem>emptyList()));
        }

        return Single.<List<InfoItem>, List<InfoItem>>zip(loaders, results -> {
            // Interleave the results of every seed so the feed is varied
            final List<List<InfoItem>> lists = new ArrayList<>();
            for (final Object result : results) {
                @SuppressWarnings("unchecked")
                final List<InfoItem> list = (List<InfoItem>) result;
                lists.add(list);
            }

            final List<InfoItem> merged = new ArrayList<>();
            final Set<String> seen = new HashSet<>(watchedUrls);
            boolean added = true;
            for (int round = 0; added && merged.size() < MAX_ITEMS; round++) {
                added = false;
                for (final List<InfoItem> list : lists) {
                    if (round < list.size()) {
                        final InfoItem item = list.get(round);
                        added = true;
                        if (item instanceof StreamInfoItem && seen.add(item.getUrl())) {
                            merged.add(item);
                        }
                    }
                }
            }
            Collections.shuffle(merged);
            return merged;
        }).flatMap(items -> items.isEmpty() ? loadTrending(forceLoad) : Single.just(items));
    }

    /**
     * Big YouTube-like cards on phones, a multi-column grid on tablets and wide screens.
     */
    @Override
    protected ItemViewMode getItemViewMode() {
        return getResources().getConfiguration().screenWidthDp >= 600
                ? ItemViewMode.GRID : ItemViewMode.CARD;
    }

    @Override
    protected void loadMoreItems() {
        // The home feed is a single page
    }

    @Override
    protected boolean hasMoreItems() {
        return false;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Contract
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void handleResult(@NonNull final List<InfoItem> result) {
        super.handleResult(result);
        infoListAdapter.clearStreamItemList();
        if (result.isEmpty()) {
            showEmptyState();
            return;
        }
        infoListAdapter.addInfoItemList(result);
        hideLoading();
        itemsList.scheduleLayoutAnimation();
    }
}
