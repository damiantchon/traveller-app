package tim.project.travellerapp.adapters;

import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import tim.project.travellerapp.R;
import tim.project.travellerapp.models.VisitedPlace;

public class VisitedAdapter extends RecyclerView.Adapter<VisitedAdapter.VisitedViewHolder> {

    List<VisitedPlace> visitedPlaceList;

    public VisitedAdapter(List<VisitedPlace> visitedPlaces) {
        this.visitedPlaceList = visitedPlaces;
    }

    public static class VisitedViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        @BindView(R.id.card_view_place_visited_item)
        CardView cardViewPlaceVisitedItem;

        @BindView(R.id.place_item_visited_name)
        TextView titleVisitedTextView;

        @BindView(R.id.place_item_visited_description)
        TextView descriptionVisitedTextView;

        @BindView(R.id.place_item_visited_time)
        TextView timeVisitedTextView;

        public VisitedViewHolder(View viewItem) {
            super(viewItem);
            ButterKnife.bind(this, viewItem);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Resources resources = v.getResources();

            menu.add(this.getAdapterPosition(), resources.getInteger(R.integer.action_add_photo_visited_id), 0, resources.getString(R.string.action_add_photo_visited));
        }
    }

    @Override
    public VisitedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_visited_item, parent, false);
        return new VisitedViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(VisitedViewHolder holder, int position) {
        holder.titleVisitedTextView.setText(visitedPlaceList.get(position).getName());
        holder.descriptionVisitedTextView.setText(visitedPlaceList.get(position).getDescription());
        holder.timeVisitedTextView.setText(getDate(visitedPlaceList.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return this.visitedPlaceList.size();
    }

    private String getDate(long timeStamp){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

}
