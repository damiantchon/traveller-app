package tim.project.travellerapp.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tim.project.travellerapp.R;
import tim.project.travellerapp.models.Place;

public class VisitAdapter extends RecyclerView.Adapter<VisitAdapter.VisitViewHolder> {

    List<Place> placeItems;

    public VisitAdapter(List<Place> placeItems) {
        this.placeItems = placeItems;

    }

    public static class VisitViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_view_place_item)
        CardView cardViewPlaceItem;

        @BindView(R.id.place_item_name)
        TextView titleTextView;

        @BindView(R.id.place_item_description)
        TextView descriptionTextView;

        public VisitViewHolder(View viewItem) {
            super(viewItem);

            ButterKnife.bind(this, viewItem);

//            cardViewPlaceItem = (CardView) viewItem.findViewById(R.id.card_view_place_item);
//            titleTextView = (TextView) viewItem.findViewById(R.id.place_item_name);
//            descriptionTextView = (TextView) viewItem.findViewById(R.id.place_item_description);

        }
    }

    @Override
    public VisitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
        VisitViewHolder visitViewHolder = new VisitViewHolder(viewItem);
        return visitViewHolder;
    }

    @Override
    public void onBindViewHolder(VisitViewHolder holder, int position) {
        holder.titleTextView.setText(placeItems.get(position).getName());
        holder.descriptionTextView.setText(placeItems.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return this.placeItems.size();
    }



}
