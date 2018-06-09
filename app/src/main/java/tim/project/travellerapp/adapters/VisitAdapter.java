package tim.project.travellerapp.adapters;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tim.project.travellerapp.R;
import tim.project.travellerapp.activities.ToVisitMapsActivity;
import tim.project.travellerapp.models.Place;

public class VisitAdapter extends RecyclerView.Adapter<VisitAdapter.VisitViewHolder> {

    List<Place> placeItems;

    public VisitAdapter(List<Place> placeItems) {
        this.placeItems = placeItems;
    }

    public static class VisitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        @BindView(R.id.card_view_place_item)
        CardView cardViewPlaceItem;

        @BindView(R.id.place_item_name)
        TextView titleTextView;

        @BindView(R.id.place_item_description)
        TextView descriptionTextView;

        String gps;

        public VisitViewHolder(View viewItem) {
            super(viewItem);

            ButterKnife.bind(this, viewItem);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), ToVisitMapsActivity.class);

            intent.putExtra(v.getResources().getString(R.string.PLACE_TO_VISIT_LOCALISATION), gps);
            intent.putExtra(v.getResources().getString(R.string.PLACE_TO_VISIT_TITLE), titleTextView.getText());

            v.getContext().startActivity(intent);

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Resources resources = v.getResources();
            menu.add(this.getAdapterPosition(), resources.getInteger(R.integer.action_visited_id), 0, resources.getString(R.string.action_visited));
            menu.add(this.getAdapterPosition(), resources.getInteger(R.integer.action_delete_id), 1, resources.getString(R.string.action_delete));

        }
    }

    @Override
    public VisitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
        return new VisitViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(VisitViewHolder holder, int position) {
        holder.titleTextView.setText(placeItems.get(position).getName());
        holder.descriptionTextView.setText(placeItems.get(position).getDescription());
        holder.gps = placeItems.get(position).getGps();
    }

    @Override
    public int getItemCount() {
        return placeItems.size();
    }
}
