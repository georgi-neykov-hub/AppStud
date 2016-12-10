package appstud.neykov.com.appstudassigment.base.adapter;

import android.support.annotation.IntRange;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class SectionedRecyclerAdapter<HeaderVH extends RecyclerView.ViewHolder, ItemHV extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected final static int VIEW_TYPE_HEADER = -2;
    protected final static int VIEW_TYPE_ITEM = -1;

    private final ArrayMap<Integer, Integer> sectionStartToSectionIndexMap;

    public SectionedRecyclerAdapter() {
        sectionStartToSectionIndexMap = new ArrayMap<>();
    }

    public abstract int getSectionCount();

    public abstract int getItemCount(int section);

    public abstract void onBindHeaderViewHolder(HeaderVH holder, int section);

    public abstract void onBindItemViewHolder(ItemHV holder, int section, int relativePosition, int absolutePosition);

    public final boolean isHeader(int position) {
        return sectionStartToSectionIndexMap.get(position) != null;
    }

    private int[] intCache = new int[2];

    protected int getSectionIndex(int adapterPosition){
        Integer lastSectionIndex = -1;
        for (final Integer sectionIndex : sectionStartToSectionIndexMap.keySet()) {
            if (adapterPosition >= sectionIndex) {
                lastSectionIndex = sectionIndex;
            } else {
                break;
            }
        }

        return sectionStartToSectionIndexMap.get(lastSectionIndex);
    }

    // returns section along with offsetted position
    protected int[] getSectionIndexAndRelativePosition(int adapterPosition) {
        Integer lastSectionStart = -1;
        for (final Integer sectionStart : sectionStartToSectionIndexMap.keySet()) {
            if (adapterPosition >= sectionStart) {
                lastSectionStart = sectionStart;
            } else {
                break;
            }
        }

        int sectionIndex = sectionStartToSectionIndexMap.get(lastSectionStart);
        intCache[0] = sectionIndex;
        intCache[1] = adapterPosition - lastSectionStart - 1;
        return intCache;
    }

    protected int getRelativePosition(int section, int adapterPosition){
        return adapterPosition - section - 1;
    }

    protected int getAdapterPosition(int section, int relativePosition) {
        return sectionStartToSectionIndexMap.get(section) + relativePosition;
    }

    @Override
    public final int getItemCount() {
        int totalCount = 0;
        sectionStartToSectionIndexMap.clear();
        final int sectionCount = getSectionCount();
        for (int s = 0; s < sectionCount; s++) {
            int itemCount = getItemCount(s);
            sectionStartToSectionIndexMap.put(totalCount, s);
            totalCount += itemCount + 1;
        }
        return totalCount;
    }

    @Override
    public final int getItemViewType(int position) {
        if (isHeader(position)) {
            return getHeaderViewType(sectionStartToSectionIndexMap.get(position));
        } else {
            final int[] sectionAndPos = getSectionIndexAndRelativePosition(position);
            return getSectionItemViewType(sectionAndPos[0],
                    // offset section view positions
                    sectionAndPos[1],
                    position - (sectionAndPos[0] + 1));
        }
    }

    @SuppressWarnings("UnusedParameters")
    @IntRange(from = 0, to = Integer.MAX_VALUE)
    public int getHeaderViewType(int section) {
        //noinspection ResourceType
        return VIEW_TYPE_HEADER;
    }

    @SuppressWarnings("UnusedParameters")
    @IntRange(from = 0, to = Integer.MAX_VALUE)
    public int getSectionItemViewType(int section, int relativePosition, int absolutePosition) {
        //noinspection ResourceType
        return VIEW_TYPE_ITEM;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeader(position)) {
            onBindHeaderViewHolder((HeaderVH) holder, sectionStartToSectionIndexMap.get(position));
        } else {
            final int[] sectionAndPos = getSectionIndexAndRelativePosition(position);
            final int absPos = position - (sectionAndPos[0] + 1);
            onBindItemViewHolder((ItemHV) holder, sectionAndPos[0], sectionAndPos[1], absPos);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        onBindViewHolder(holder, position);
    }
}