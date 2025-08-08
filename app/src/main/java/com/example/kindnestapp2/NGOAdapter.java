package com.example.kindnestapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class NGOAdapter extends RecyclerView.Adapter<NGOAdapter.NGOViewHolder> {

    public interface OnNGOClickListener {
        void onNGOClick(NGO ngo);
    }

    private final Context context;
    private final List<NGO> ngoList;
    private final OnNGOClickListener listener;

    public NGOAdapter(Context context, List<NGO> ngoList, OnNGOClickListener listener) {
        this.context = context;
        this.ngoList = ngoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NGOViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ngo_item, parent, false);
        return new NGOViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NGOViewHolder holder, int position) {
        NGO ngo = ngoList.get(position);
        holder.bind(ngo, listener);
    }

    @Override
    public int getItemCount() {
        return ngoList.size();
    }

    class NGOViewHolder extends RecyclerView.ViewHolder {
        ImageView ngoLogo;
        TextView ngoName, ngoFocus;

        public NGOViewHolder(@NonNull View itemView) {
            super(itemView);
            ngoLogo = itemView.findViewById(R.id.ngo_logo);
            ngoName = itemView.findViewById(R.id.ngo_name);
            ngoFocus = itemView.findViewById(R.id.ngo_focus);
        }

        public void bind(final NGO ngo, final OnNGOClickListener listener) {
            ngoName.setText(ngo.getName());
            ngoFocus.setText(ngo.getFocusArea());

            Glide.with(context)
                    .load(ngo.getLogoUrl())
                    .into(ngoLogo);

            itemView.setOnClickListener(v -> listener.onNGOClick(ngo));
        }
    }
}